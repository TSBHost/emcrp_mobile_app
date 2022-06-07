package app.ddc.lged.emcrp.reports;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import androidx.appcompat.app.ActionBar;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import app.ddc.lged.emcrp.NavigationActivity;
import app.ddc.lged.emcrp.R;
import app.ddc.lged.emcrp.connectivity.Config;
import app.ddc.lged.emcrp.connectivity.DBHelper;
import app.ddc.lged.emcrp.home.LoginActivity;

public class SaveSubmissionActivity extends NavigationActivity {

    Config conf=new Config();
    private final String URL = conf.getURL()+"get_form_list.php";
    ProgressDialog progressDialog;
    SharedPreferences sharedPref;
    private DBHelper mydb;
    ArrayList<SaveSubmissionModel> arrayList = new ArrayList<SaveSubmissionModel>();
    ProgressDialog loading;
    String Image1,Image2,Image3,Image4,Image5,ntitle,mtaskv,taskv,commv,latv,ndate,lanv,area,imgfile1,imgfile2,imgfile3,imgfile4,imgfile5,cap1,cap2,cap3,cap4,cap5;
    Cursor rs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isLogin() && read_memory() != 0) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.activity_save_submission, null, false);
            drawer.addView(view,0);
            //sharedPref = getSharedPreferences("LOGININFO", Context.MODE_PRIVATE);
            //int userid = sharedPref.getInt("userid", 0);
            //Toast.makeText(getApplicationContext(), ""+userid, Toast.LENGTH_LONG).show();

            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("Send to Server");
            autocomplete();
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

    public int read_memory() {
        sharedPref = this.getSharedPreferences("LOGININFO", Context.MODE_PRIVATE);
        int s = sharedPref.getInt("userid", 0);
        return s;
    }

    public void autocomplete(){
        final RecyclerView rcv = (RecyclerView) findViewById(R.id.recylv);
        rcv.setLayoutManager(new LinearLayoutManager(this));
        mydb = new DBHelper(this);
        sharedPref = getSharedPreferences("LOGININFO", MODE_PRIVATE);
        int userid = sharedPref.getInt("userid", 0);

        Cursor response = mydb.getAllForms(0,userid);
        int totalitem = response.getCount();

        if(response!= null){
            if(response.moveToFirst()){
                do{
                    SaveSubmissionModel saveitem = new SaveSubmissionModel();
                    saveitem.setId(response.getInt(response.getColumnIndex("id")));
                    saveitem.setShelter(response.getString(response.getColumnIndex("ShelterCode")));
                    saveitem.setMtask(response.getString(response.getColumnIndex("MajorTasks")));
                    saveitem.setTasks(response.getString(response.getColumnIndex("Tasks")));
                    saveitem.setDate(response.getString(response.getColumnIndex("SubmisionDate")));

                    //saveitem.setShelter(response.getString(2));
                    //saveitem.setUname(response.getString(3));
                    //saveitem.setMtask(response.getString(4));
                   //saveitem.setTasks(response.getString(5));
                    arrayList.add(saveitem);

                }while(response.moveToNext());
            }

        }
        if(totalitem > 0) {
            rcv.setAdapter(new RecyleSavedDataAdapter(this, arrayList));
            response.close();
        }
        else{
            //Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.zerodata);
            linearLayout.setVisibility(View.VISIBLE);
            rcv.setVisibility(View.GONE);
        }
    }




   // @RequiresApi(api = Build.VERSION_CODES.O)
    public void getCheckedItems(List<String> items){
        //Toast.makeText(SaveSubmissionActivity.this, items.toString() + " clicked!", Toast.LENGTH_SHORT).show();
        Log.v("checkeditems",items.toString());
        //String listString = String.join(",", items);
        String listString = TextUtils.join(", ", items);

        List<String> stringList = new ArrayList<>();
        int length = items.size();

        mydb = new DBHelper(SaveSubmissionActivity.this);
        rs = mydb.getDataMultiplrForm("1,2,3");

        int j = 0;
        for (int i = 0; i < length; i++) {
            while (rs.moveToNext()){
                String dateTemp = rs.getString(rs.getColumnIndex("ShelterCode"));
                if (dateTemp.equals(items.get(i))) {
                    String name = rs.getString(rs.getColumnIndex("MajorTasks"));
                    stringList.add(j, name);
                    j++;
                }
            }
        }
        //return stringList;
        Log.v("allcheckedquery", stringList.toString());


       /* mydb = new DBHelper(SaveSubmissionActivity.this);
       // Bundle extras = getIntent().getExtras();
        //int subid = extras.getInt("id");
        try {
            //means this is the view part not the add contact part.
            rs = mydb.getDataMultiplrForm(items);
            rs.moveToLast();

            ntitle = rs.getString(rs.getColumnIndex("ShelterCode"));
            mtaskv = rs.getString(rs.getColumnIndex("MajorTasks"));
            taskv = rs.getString(rs.getColumnIndex("Tasks"));
            commv = rs.getString(rs.getColumnIndex("Comments"));
            latv = rs.getString(rs.getColumnIndex("Latitude"));
            lanv = rs.getString(rs.getColumnIndex("Longitude"));
            area = rs.getString(rs.getColumnIndex("Area"));
            ndate = rs.getString(rs.getColumnIndex("SubmisionDate"));

            rs.close();

            Cursor res1 = mydb.getDataGallery(subid);
            res1.moveToFirst();

            if(res1!=null && res1.getCount() > 0)
            {
                if (res1.moveToFirst())
                {
                    cap1 = res1.getString(res1.getColumnIndex("Caption1"));
                    Image1 = res1.getString(res1.getColumnIndex("Image1"));
                    cap2 = res1.getString(res1.getColumnIndex("Caption2"));
                    Image2 = res1.getString(res1.getColumnIndex("Image2"));
                    cap3 = res1.getString(res1.getColumnIndex("Caption3"));
                    Image3 = res1.getString(res1.getColumnIndex("Image3"));
                    cap4 = res1.getString(res1.getColumnIndex("Caption4"));
                    Image4 = res1.getString(res1.getColumnIndex("Image4"));
                    cap5 = res1.getString(res1.getColumnIndex("Caption5"));
                    Image5 = res1.getString(res1.getColumnIndex("Image5"));
                }
            }

            Log.v("allcheckedquery", rs.toString());
            res1.close();
            String fullPath = getFilesDir().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;

            String imgpath1 = fullPath + "/" + Image1;
            BitmapFactory.Options options1 = new BitmapFactory.Options();
            options1.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bmp1 = BitmapFactory.decodeFile(imgpath1, options1);

            String imgpath2 = fullPath + "/" + Image2;
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bmp2 = BitmapFactory.decodeFile(imgpath2, options2);

            String imgpath3 = fullPath + "/" + Image3;
            BitmapFactory.Options options3 = new BitmapFactory.Options();
            options3.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bmp3 = BitmapFactory.decodeFile(imgpath3, options3);

            String imgpath4 = fullPath + "/" + Image4;
            BitmapFactory.Options options4 = new BitmapFactory.Options();
            options4.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bmp4 = BitmapFactory.decodeFile(imgpath4, options4);

            String imgpath5 = fullPath + "/" + Image5;
            BitmapFactory.Options options5 = new BitmapFactory.Options();
            options5.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bmp5 = BitmapFactory.decodeFile(imgpath5, options5);
        }
        catch (Exception e){
            Log.v("CatchVal", e.toString());
        }*/
    }



    /*public void uploadToServer(final String shelter,final String mtask,final String stask,
                               final String comm,final String lat,final String lan,final String area,final String date,
                               final String imgf1,final String imgf2,final String imgf3,final String imgf4,final String imgf5,
                               final String capt1,final String capt2,final String capt3,final String capt4,final String capt5){

        Bundle extras = getIntent().getExtras();
        final int submid = extras.getInt("id");

        loading = ProgressDialog.show(SavedSubmissionDetails.this,"Sending data","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        loading.dismiss();
                        try {
                            JSONObject json = new JSONObject(s);
                            int refid = json.getInt("refid");
                            String act = json.getString("action");
                            Log.v("ResponseInsert", "Submission ID: "+String.valueOf(refid));
                            Toast.makeText(getApplicationContext(), "Successfully sent to server", Toast.LENGTH_LONG).show();
                            mydb.updateSUbmittedForm(submid,refid);

                            Intent inthome = new Intent(getApplicationContext(), GridViewActivity.class);
                            startActivity(inthome);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        if (error instanceof NetworkError) {
                        } else if (error instanceof ServerError) {
                        } else if (error instanceof AuthFailureError) {
                        } else if (error instanceof ParseError) {
                        } else if (error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),
                                    "Oops! Disconnected",
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof TimeoutError) {
                            Toast.makeText(getApplicationContext(),
                                    "Successfully sent to server",
                                    Toast.LENGTH_LONG).show();
                            mydb.updateSUbmittedForm(submid,0);
                            Intent inthome = new Intent(getApplicationContext(), GridViewActivity.class);
                            startActivity(inthome);
                        }
                    }
                }){

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                if (response.headers == null)
                {
                    // cant just set a new empty map because the member is final.
                    response = new NetworkResponse(
                            response.statusCode,
                            response.data,
                            Collections.<String, String>emptyMap(), // this is the important line, set an empty but non-null map.
                            response.notModified,
                            response.networkTimeMs);
                }

                return super.parseNetworkResponse(response);
            }
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                sharedPref = getSharedPreferences("LOGININFO", Context.MODE_PRIVATE);
                int userid = sharedPref.getInt("userid", 0);

                Map<String,String> params = new Hashtable<String, String>();
                params.put("userid", Integer.toString(userid));
                params.put("image1", imgf1);
                params.put("image2", imgf2);
                params.put("image3", imgf3);
                params.put("image4", imgf4);
                params.put("image5", imgf5);
                params.put("caption1", capt1);
                params.put("caption2", capt2);
                params.put("caption3", capt3);
                params.put("caption4", capt4);
                params.put("caption5", capt5);

                params.put("imgname1", imgname1);
                params.put("imgname2", imgname2);
                params.put("imgname3", imgname3);
                params.put("imgname4", imgname4);
                params.put("imgname5", imgname5);

                params.put("shelid", shelter);
                params.put("mtask", mtask);
                params.put("stask", stask);
                params.put("ucom", comm);
                params.put("latv", lat);
                params.put("longv", lan);
                params.put("area", area);
                params.put("saveddate", date);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }*/

}
