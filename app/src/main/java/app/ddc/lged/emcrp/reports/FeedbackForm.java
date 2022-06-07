package app.ddc.lged.emcrp.reports;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import app.ddc.lged.emcrp.NavigationActivity;
import app.ddc.lged.emcrp.R;
import app.ddc.lged.emcrp.connectivity.Config;
import app.ddc.lged.emcrp.connectivity.ConnectivityReceiver;
import app.ddc.lged.emcrp.connectivity.DBHelper;
import app.ddc.lged.emcrp.home.LoginActivity;

public class FeedbackForm extends NavigationActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    TextView title,mtasks,replyt,date,replyt2,date2,replyt3,date3,sbdate;
    Button fdbtn,TakeP1,imgG1,dialogButton;
    ImageButton rbtn,rbtn1,bt_voiceinput;
    EditText uf;
    Config conf=new Config();
    private final String URL = conf.getURL()+"feedback_insert.php";
    URL url = null;
    ProgressDialog progressDialog;
    SharedPreferences sharedPref;
    private DBHelper mydb;
    Context context;

    ImageView imgv1;
    public  static final int RequestPermissionCode  = 1 ;
    Bitmap bitmap1;
    int subid,rid;
    Intent intent;
    Dialog dialog;
    private static final int REQUEST_CODE = 1234;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2381;
    HashMap<String, String> queryValues;
    ArrayList<FeedbackModel> arrayList = new ArrayList<FeedbackModel>();

    OutputStream fOut;
    String filename1;
    public final static String APP_PATH_SD_CARD = "/EMCRPbd/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "images/feedback";
    boolean feedbackFlag;
    ProgressDialog loading;
    String shelter,mtask,subdate;

    private final String URLFeedback = conf.getFeedbackSyncUrl();

    //@RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isLogin() && read_memory() != 0) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.activity_feedback_form, null, false);
            drawer.addView(view,0);

            mydb = new DBHelper(this);
            Bundle extras = getIntent().getExtras();
            String fid = extras.getString("customId");
            if(fid !=null) {
                shelter = extras.getString("shelter");
                mtask = extras.getString("mtask");
                subdate = extras.getString("subdate");
                subid = Integer.parseInt(fid);
                checkConnection();
            }
            else {
                intent = getIntent();
                subid = intent.getIntExtra("subid", 0);
                shelter = intent.getStringExtra("shelter");
                mtask = intent.getStringExtra("mtask");
                subdate = intent.getStringExtra("subdate");
            }

            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("Feedback for "+ shelter);

            sbdate = (TextView) findViewById(R.id.subdate);
            mtasks = (TextView) findViewById(R.id.mtask);
            mtasks.setText(mtask);
            sbdate.setText(subdate);
            autocomplete(subid);
        }
        else {
            Intent i = new Intent(this.getBaseContext().getApplicationContext(), LoginActivity.class);
            this.startActivity(i);
        }
    }

    public Boolean isLogin() {
        sharedPref = this.getSharedPreferences("LOGININFO", Context.MODE_PRIVATE);
        Boolean s = sharedPref.getBoolean("loggedin", true);
        return s;
    }


    public boolean feedbackSyncList() {
        StringRequest request = new StringRequest(Request.Method.POST, URLFeedback, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray JA = new JSONArray(response);
                    for (int i = 0; i < JA.length(); i++) {
                        JSONObject obj = JA.getJSONObject(i);
                        queryValues = new HashMap<String, String>();

                        int subid = obj.getInt("subid");
                        int refid = obj.getInt("id");
                        int hqstatus = obj.getInt("hqstatus");
                        Log.v("feedbackhqstatus", Integer.toString(hqstatus));
                        queryValues.put("userid", Integer.toString(read_memory()));
                        queryValues.put("refid", Integer.toString(refid));
                        queryValues.put("shelter", obj.getString("shelter"));
                        queryValues.put("mtask", obj.getString("mtask"));
                        queryValues.put("subid", obj.getString("subid"));
                        queryValues.put("subdate", obj.getString("subdate"));
                        queryValues.put("hqfeeddate", obj.getString("hqfeeddate"));
                        queryValues.put("hqfeed", obj.getString("hqfeed"));
                        queryValues.put("hqstatus", obj.getString("hqstatus"));
                        queryValues.put("userfeed", obj.getString("userfeed"));
                        queryValues.put("userimg", obj.getString("userimg"));
                        queryValues.put("userfeeddate", obj.getString("userfeeddate"));

                        mydb = new DBHelper(FeedbackForm.this);

                        Cursor rs = mydb.checkExistingFeed(refid);
                        rs.moveToFirst();

                        rs.close();
                        if (rs.getCount() > 0) {
                            int sid = rs.getColumnIndex("refid");
                            feedbackFlag = mydb.updateFeedback(queryValues, refid);
                        } else {
                            feedbackFlag = mydb.insertFeedback(queryValues);
                        }
                        if(hqstatus == 12) {
                            mydb.deleteFeedbackBySubmission(hqstatus);

                        }
                        //Log.v("allfeedback", mydb.getAllFeedback().toString());

                    }

                } catch (JSONException e) {
                    Log.d("JSON Exception", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //progressDialog.dismiss();
                Log.e("Error", "Failed");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                sharedPref = getSharedPreferences("LOGININFO", Context.MODE_PRIVATE);
                int userid = sharedPref.getInt("userid", 0);

                Map<String, String> params = new Hashtable<String, String>();
                params.put("userid", Integer.toString(userid));

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(FeedbackForm.this);
        queue.add(request);

        return true;
    }
    private void checkConnection() {
      boolean isConnected = ConnectivityReceiver.isConnected(this);
      String message;
      if (isConnected) {
          if (feedbackSyncList()) {
              finish();
              startActivity(getIntent());
          }
      } else {
          message = "No Internet Connection";
          //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
          customToast(message);
      }
  }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            customToast("Connected");
        } else {
            customToast("No Internet Connection");
        }
    }
    public int read_memory() {
        sharedPref = this.getSharedPreferences("LOGININFO", Context.MODE_PRIVATE);
        int s = sharedPref.getInt("userid", 0);
        return s;
    }
    public void autocomplete(int subid){
        final RecyclerView recfeed = (RecyclerView) findViewById(R.id.feedbackform);
        recfeed.setLayoutManager(new LinearLayoutManager(this));
        mydb = new DBHelper(this);

        sharedPref = getSharedPreferences("LOGININFO", MODE_PRIVATE);
        int userid = sharedPref.getInt("userid", 0);

        Cursor response = mydb.getSubmissionFeedbacks(userid,subid);
       ArrayList array_list1 = mydb.getAllLikeFeedback(userid,subid);
       Log.e("AllFedbackLIst", array_list1.toString());
        int totalitem = response.getCount();
      //  Log.v("TotalFeedData", Integer.toString(totalitem)+" SID:"+Integer.toString(subid)+" UID:"+Integer.toString(userid));
        if(response!= null){
            if(response.moveToFirst()){
                do{
                    FeedbackModel saveitem = new FeedbackModel();
                    saveitem.setId(response.getInt(response.getColumnIndex("refid")));
                    saveitem.setSubid(response.getInt(response.getColumnIndex("subid")));
                    saveitem.setShelter(response.getString(response.getColumnIndex("shelter")));
                    saveitem.setMtask(response.getString(response.getColumnIndex("mtask")));
                    saveitem.setSdate(response.getString(response.getColumnIndex("subdate")));
                    saveitem.setHqFeed(response.getString(response.getColumnIndex("hqfeed")));
                    saveitem.setHqFeedDate(response.getString(response.getColumnIndex("hqfeeddate")));
                    saveitem.setStatus(response.getString(response.getColumnIndex("status")));
                    saveitem.setUserfeed(response.getString(response.getColumnIndex("userfeed")));
                    saveitem.setUserImg(response.getString(response.getColumnIndex("userimg")));
                    saveitem.setUserFeedDate(response.getString(response.getColumnIndex("userfeeddate")));
                    arrayList.add(saveitem);
                }
                while(response.moveToNext());
            }
        }

        if(totalitem > 0) {
            recfeed.setAdapter(new RecyleFeedbackInsert(this, arrayList));
            response.close();
        }
        else{
            //Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.zerodata);
            linearLayout.setVisibility(View.VISIBLE);
            recfeed.setVisibility(View.GONE);
        }
    }

    public void onBackPressed() {
        Intent intentfeed = new Intent(getApplicationContext(), FeedbackList.class);
        startActivity(intentfeed);
    }

}
