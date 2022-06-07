package app.ddc.lged.emcrp.reports;

import android.app.ProgressDialog;
import android.content.Context;
import androidx.appcompat.app.ActionBar;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import app.ddc.lged.emcrp.NavigationActivity;
import app.ddc.lged.emcrp.R;
import app.ddc.lged.emcrp.connectivity.Config;
import app.ddc.lged.emcrp.connectivity.ConnectivityReceiver;
import app.ddc.lged.emcrp.connectivity.DBHelper;
import app.ddc.lged.emcrp.connectivity.DisconnectAdapter;

public class SyncData extends NavigationActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private DBHelper mydb;
    HashMap<String, String> queryValues;
    Context context;
    Config conf = new Config();
    Button sheltbtn,subformbtn,feedbtn;
    ProgressDialog loadingprog;
    Boolean shelterflag,feedbackFlag,submissionFlag,mastksflag;
    SharedPreferences sharedPref;
    private final String URLPack = conf.getPackage();
    private final String URLSubPack = conf.getSubPackage();
    private final String URLTask = conf.getTaskUrl();
    private final String URLSTask = conf.getSubTask();
    private final String URLSform = conf.getSubmissionSyncUrl();
    private final String URLFeedback = conf.getFeedbackSyncUrl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_sync);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.activity_sync_data, null);
        drawer.addView(v, 0);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Sync Data");

        sheltbtn = (Button) findViewById(R.id.shelterbtn);
        subformbtn = (Button) findViewById(R.id.submitform);
        feedbtn = (Button) findViewById(R.id.feedbackbtn);

        sheltbtn.setOnClickListener(this);
        subformbtn.setOnClickListener(this);
        feedbtn.setOnClickListener(this);
        sharedPref = getSharedPreferences("LOGININFO", Context.MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.shelterbtn:
                checkConnection("shelter");
                break;
            case R.id.submitform:
                checkConnection("submission");
                break;
            case R.id.feedbackbtn:
                checkConnection("feedback");
                break;
        }
    }

    //////// Syncining data for Shelter Code ///////////
    public boolean getPackages() {
        shelterflag = false;
        StringRequest request = new StringRequest(Request.Method.POST, URLPack, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("Packages: ",response);
                    JSONArray JA = new JSONArray(response);
                    for (int i = 0; i < JA.length(); i++) {
                        JSONObject obj = JA.getJSONObject(i);
                        int totalitem = obj.getInt("totalitems");
                        if (JA.length() == totalitem) {
                            try {
                                queryValues = new HashMap<String, String>();
                                Integer tid = obj.getInt("tid");
                                String name = obj.getString("name");
                                String alias = obj.getString("alias");
                                Integer groupid = obj.getInt("group_id");
                                Integer flag = obj.getInt("flag");
                                queryValues.put("name", name);
                                queryValues.put("alias", alias);
                                queryValues.put("group_id", Integer.toString(groupid));
                                queryValues.put("flag", Integer.toString(flag));
                                mydb = new DBHelper(SyncData.this);
                                Cursor rs = mydb.checkExisting(name);
                                rs.moveToFirst();
                                //Log.v("exisitngcode", Integer.toString(rs.getCount()));
                                rs.close();
                                if (rs.getCount() > 0) {
                                    int sid = rs.getColumnIndex("id");
                                    shelterflag = mydb.updateShelter(sid, tid, name,alias,flag,groupid);
                                } else {
                                    shelterflag = mydb.insertPackages(queryValues);
                                }
                                mydb.close();
                            } catch (SQLException s) {
                                new Exception("Error with DB Open");
                            }
                        }


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
                /*Toast.makeText(getApplicationContext(),
                        "Data Loading failed", Toast.LENGTH_SHORT).show();*/
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("allshelter", "allshelter");
                //params.put("terms", "FEP");

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
        return true;
    }
    public boolean getSubPackage() {
        shelterflag = false;
        StringRequest request = new StringRequest(Request.Method.POST, URLSubPack, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("ShelterListS: ",response);
                    JSONArray JA = new JSONArray(response);
                    for (int i = 0; i < JA.length(); i++) {
                        JSONObject obj = JA.getJSONObject(i);
                        int totalitem = obj.getInt("totalitems");
                        if (JA.length() == totalitem) {

                            try {

                                queryValues = new HashMap<String, String>();
                                String code = obj.getString("code");
                                String name = obj.getString("name");
                                String alias = obj.getString("alias");
                                Integer groupid = obj.getInt("package_id");
                                Integer tid = obj.getInt("id");
                                queryValues.put("internal_code", code);
                                queryValues.put("name", name);
                                queryValues.put("alias", alias);
                                queryValues.put("package_id", Integer.toString(groupid));
                                queryValues.put("tid", Integer.toString(tid));
                                mydb = new DBHelper(SyncData.this);
                                Cursor rs = mydb.checkExistingSub(code);
                                rs.moveToFirst();
                                //Log.v("exisitngcode", Integer.toString(rs.getCount()));
                                rs.close();
                                if (rs.getCount() > 0) {
                                    int sid = rs.getColumnIndex("id");
                                    shelterflag = mydb.updateSubpackage(sid,tid, code,name,alias,groupid);
                                } else {
                                    shelterflag = mydb.insertSubpackages(queryValues);
                                }
                                mydb.close();
                            } catch (SQLException s) {
                                new Exception("Error with DB Open");
                            }
                        }


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
                /*Toast.makeText(getApplicationContext(),
                        "Data Loading failed", Toast.LENGTH_SHORT).show();*/
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("allshelter", "allshelter");
                //params.put("terms", "FEP");

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
        return true;
    }
    public boolean majorTasks() {
        mastksflag = false;
        mydb = new DBHelper(SyncData.this);
        //mydb.deleteAllTasks();
        StringRequest request = new StringRequest(URLTask, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("ShelterTaskList: ",response);
                    JSONArray JA = new JSONArray(response);
                    for (int i = 0; i < JA.length(); i++) {
                        JSONObject obj = JA.getJSONObject(i);
                        int totalitem = obj.getInt("totalitems");
                        if (JA.length() == totalitem) {

                            try {
                                queryValues = new HashMap<String, String>();
                                int tid = obj.getInt("id");
                                String package_id = obj.getString("package_id");
                                String name = obj.getString("name");
                                String details = obj.getString("details");
                                int flag = obj.getInt("flag");
                                queryValues.put("id", Integer.toString(tid));
                                queryValues.put("name", name);
                                queryValues.put("details", details);
                                queryValues.put("package_id",  package_id);
                                queryValues.put("flag", Integer.toString(flag));

                                Cursor rs = mydb.checkExistingTask(tid);
                                rs.moveToFirst();
                                //Log.v("exisitngcode", Integer.toString(rs.getCount()));
                                rs.close();
                                if (rs.getCount() > 0) {
                                    int sid = rs.getColumnIndex("id");
                                    mastksflag = mydb.updateTask(sid, package_id, name, details ,flag);
                                } else {
                                    mastksflag = mydb.insertTask(queryValues);
                                }
                                //mastksflag = mydb.insertTask(queryValues);
                                mydb.close();
                            } catch (SQLException s) {
                                new Exception("Error with DB Open");
                            }

                        }


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
                /*Toast.makeText(getApplicationContext(),
                        "Data Loading failed", Toast.LENGTH_SHORT).show();*/
            }
        });

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
        return true;
    }
    public boolean subTasks() {
        mastksflag = false;
        mydb = new DBHelper(SyncData.this);
        //mydb.deleteAllTasks();
        StringRequest request = new StringRequest(URLSTask, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("SubTaskList: ",response);
                    JSONArray JA = new JSONArray(response);
                    for (int i = 0; i < JA.length(); i++) {
                        JSONObject obj = JA.getJSONObject(i);
                        int totalitem = obj.getInt("totalitems");
                        if (JA.length() == totalitem) {

                            try {
                                queryValues = new HashMap<String, String>();
                                int tid = obj.getInt("id");
                                int majortask_id = obj.getInt("majortask_id");
                                String name = obj.getString("name");
                                String details = obj.getString("details");
                                int flag = obj.getInt("flag");
                                queryValues.put("id", Integer.toString(tid));
                                queryValues.put("name", name);
                                queryValues.put("details", details);
                                queryValues.put("majortask_id",  Integer.toString(majortask_id));
                                queryValues.put("flag", Integer.toString(flag));

                                Cursor rs = mydb.checkExistingTask(tid);
                                rs.moveToFirst();
                                //Log.v("exisitngcode", Integer.toString(rs.getCount()));
                                rs.close();
                                if (rs.getCount() > 0) {
                                    int sid = rs.getColumnIndex("id");
                                    mastksflag = mydb.updateSubTask(sid, majortask_id, name, details ,flag);
                                } else {
                                    mastksflag = mydb.insertSubTask(queryValues);
                                }
                                //mastksflag = mydb.insertTask(queryValues);
                                mydb.close();
                            } catch (SQLException s) {
                                new Exception("Error with DB Open");
                            }

                        }


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
                /*Toast.makeText(getApplicationContext(),
                        "Data Loading failed", Toast.LENGTH_SHORT).show();*/
            }
        });

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
        return true;
    }
    //////// Syncining data for Submission ///////////
    public boolean submittedFormCode() {
        StringRequest request = new StringRequest(Request.Method.POST, URLSform, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray JA = new JSONArray(response);
                    for (int i = 0; i < JA.length(); i++) {
                        JSONObject obj = JA.getJSONObject(i);
                        queryValues = new HashMap<String, String>();
                        int refid = obj.getInt("id");

                        queryValues.put("refid", String.valueOf(refid));
                        queryValues.put("userid", obj.getString("userid"));
                        queryValues.put("shelid", obj.getString("shelter"));
                        queryValues.put("mtask", obj.getString("mtask"));
                        queryValues.put("stask", obj.getString("task"));
                        queryValues.put("ucom", obj.getString("comm"));
                        queryValues.put("latv", obj.getString("lat"));
                        queryValues.put("longv", obj.getString("lon"));
                        queryValues.put("SubmisionDate", obj.getString("sdate"));

                        mydb = new DBHelper(SyncData.this);
                        //submissionFlag = mydb.insertSubmittedForm(queryValues);
                        Cursor rs = mydb.checkExistingSub(refid);
                        rs.moveToFirst();
                        //Log.v("exisitngcode", Integer.toString(rs.getCount()));
                        rs.close();
                        if (rs.getCount() > 0) {
                            int sid = rs.getColumnIndex("id");
                            //Log.v("existid", Integer.toString(sid));
                            submissionFlag = mydb.updateSubmission(queryValues, sid);
                        } else {
                            submissionFlag = mydb.insertSubmittedForm(queryValues);
                        }
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
                /*Toast.makeText(getApplicationContext(),"Data Loading failed", Toast.LENGTH_LONG).show();*/
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Log.d("UserIdSession: ",Integer.toString(read_memory()));
                Map<String, String> params = new Hashtable<String, String>();
                params.put("userid", Integer.toString(read_memory()));

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
        return true;
    }
    public boolean feedbackSyncList() {
        String JssonUrl = URLFeedback+Integer.toString(read_memory())+".json";
        //Toast.makeText(getApplicationContext(), JssonUrl, Toast.LENGTH_LONG).show();
        StringRequest request = new StringRequest(Request.Method.GET, JssonUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray JA = new JSONArray(response);
                    Log.v("feedbackhqstatus", response.toString());

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = JA.getJSONObject(i);
                        queryValues = new HashMap<String, String>();

                        int subid = obj.getInt("sid");
                        int refid = obj.getInt("id");
                        int hqstatus = obj.getInt("status");
                        // Toast.makeText(getApplicationContext(), obj.getString("feedback"), Toast.LENGTH_LONG).show();

                        //Log.v("feedbackhqstatus", response.toString());
                        queryValues.put("userid", Integer.toString(read_memory()));
                        queryValues.put("refid", Integer.toString(refid));

                        queryValues.put("shelter", obj.getString("ShelterCode"));
                        queryValues.put("mtask", obj.getString("MajorTasks"));
                        queryValues.put("subid", obj.getString("sid"));
                        queryValues.put("subdate", obj.getString("SubmisionDate"));

                        queryValues.put("hqfeeddate", obj.getString("feedback_date"));
                        queryValues.put("hqfeed", obj.getString("feedback"));
                        queryValues.put("hqstatus", obj.getString("status"));
                        queryValues.put("userfeed", obj.getString("user_feedback"));
                        queryValues.put("userfeeddate", obj.getString("user_feed_date"));

                        mydb = new DBHelper(SyncData.this);

                        Cursor rs = mydb.checkExistingFeed(refid);
                        // rs.moveToFirst();
                        // rs.close();
                        // feedbackFlag = mydb.insertFeedback(queryValues);

                        if (rs.getCount() > 0) {
                            int sid = rs.getColumnIndex("refid");
                            feedbackFlag = mydb.updateFeedback(queryValues, refid);
                        } else {
                            feedbackFlag = mydb.insertFeedback(queryValues);
                        }
                        /*  if(hqstatus == 12) {
                            mydb.deleteFeedbackBySubmission(hqstatus);
                        }*/
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
                /*Toast.makeText(getApplicationContext(),
                        "Data Loading failed", Toast.LENGTH_SHORT).show();*/
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

        RequestQueue queue = Volley.newRequestQueue(SyncData.this);
        queue.add(request);

        return true;
    }





    private void checkConnection(String methodeType) {
        boolean isConnected = ConnectivityReceiver.isConnected(this);
        //showSnack(isConnected);
        String message;
        if (isConnected) {
            message = "Connected";
            if(methodeType.equals("shelter")) {
                getSubPackage();
            }
            else if(methodeType.equals("submission")) {
                submittedFormCode();
            }
            else if(methodeType.equals("feedback")) {
                feedbackSyncList();
            }
        }
        else {
            message = "Network Failed. Please Check connection";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            final RecyclerView rcv = (RecyclerView) findViewById(R.id.recylv);
            rcv.setLayoutManager(new LinearLayoutManager(this));
            rcv.setAdapter(new DisconnectAdapter(this));
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        //showSnack(isConnected);
        String message;
        if (isConnected) {
            Toast.makeText(getApplicationContext(), "Connected Plase Try again", Toast.LENGTH_LONG).show();
            //color = Color.WHITE;
        } else {
            message = "Network Failed. Please Check connection";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            final RecyclerView rcv = (RecyclerView) findViewById(R.id.recylv);
            rcv.setLayoutManager(new LinearLayoutManager(this));
            rcv.setAdapter(new DisconnectAdapter(this));
        }
    }

    public Boolean isLogin() {
        sharedPref = this.getSharedPreferences("LOGININFO", Context.MODE_PRIVATE);
        Boolean s = sharedPref.getBoolean("loggedin",true);
        return s;
    }

    ////// Login check with user id //////
    public int read_memory() {
        sharedPref = this.getSharedPreferences("LOGININFO", Context.MODE_PRIVATE);
        int s = sharedPref.getInt("userid", 0);
        return s;
    }


}
