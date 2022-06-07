package app.ddc.lged.emcrp.home;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import app.ddc.lged.emcrp.reports.FeedbackList;
import app.ddc.lged.emcrp.NavigationActivity;
import app.ddc.lged.emcrp.R;
import app.ddc.lged.emcrp.reports.Packages;
import app.ddc.lged.emcrp.reports.SaveSubmissionActivity;
import app.ddc.lged.emcrp.reports.SubmittedForm;
import app.ddc.lged.emcrp.connectivity.Config;
import app.ddc.lged.emcrp.connectivity.ConnectivityReceiver;
import app.ddc.lged.emcrp.connectivity.DBHelper;

//public class GridViewActivity extends NavigationActivity implements SwipeRefreshLayout.OnRefreshListener,LocationListener,ConnectivityReceiver.ConnectivityReceiverListener {
public class GridViewActivity extends NavigationActivity implements SwipeRefreshLayout.OnRefreshListener,LocationListener, ConnectivityReceiver.ConnectivityReceiverListener {

    ProgressDialog progressDialog;
    Context context;
    Intent intent;
    //GridView gv;
    GridView gv;
    Boolean shelterflag, feedbackFlag, submissionFlag, mastksflag;
    SharedPreferences sharedPref;
    private DBHelper mydb;
    HashMap<String, String> queryValues;
    HashMap<String, String> taskValues;
    HashMap<String, String> formValues;
    SwipeRefreshLayout refreshLayout;
    public final static String APP_PATH_SD_CARD = "/EMCRPbd/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "submission_images";
    Config conf = new Config();
    private final String URLPack = conf.getPackage();
    private final String URLSubPack = conf.getSubPackage();
    private final String URLTask = conf.getTaskUrl();
    private final String URLSTask = conf.getSubTask();
    private final String URLSform = conf.getSubmissionSyncUrl();
    private final String URLFeedback = conf.getFeedbackSyncUrl();
    private final String URLAll = conf.getAllSync();
    View view;
    String taskname,taskid;
    Integer flag;
    //"Comments Send to Server",
    static final String[] MOBILE_OS = new String[]{
            "Refresh Data","Send Reports", "Send Reports to Server", "Comments from HQ", "Submitted Forms", "Clear All Data"};
    TextView gText;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;

    private LocationManager locationManager;
    private Location location;
    private final int REQUEST_LOCATION = 200;
    private static final String TAG = "GridViewActivity";
    GridCustomeAdapter gAdapter;
    ProgressDialog loadingprog;
    boolean refreshToggle = true;
    Handler handler;
    private Handler mHandler;

    private GoogleApiClient googleApiClient;
    private ProgressDialog mProgressDialog;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        if (isLogin() && read_memory() != 0) {
            //feedbackSyncList();
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.activity_grid_view, null, false);
            drawer.addView(view, 0);

            mydb = new DBHelper(this);
            cleareallsharevalue();

            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("EMCRP");

            refreshMethod();
            eventClick();
            locationMethod();
        } else {
            Intent i = new Intent(this.getBaseContext().getApplicationContext(), LoginActivity.class);
            //Intent i = new Intent(this.getBaseContext().getApplicationContext(), Login.class);
            this.startActivity(i);
        }
    }

    public void refreshMethod(){
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiftrefreshl);
        refreshLayout.setColorSchemeResources(R.color.colorWhite, R.color.colorPrimary, R.color.colorDefault, R.color.colorOrange, R.color.colorDeepBlue);
        refreshLayout.setProgressBackgroundColor(android.R.color.background_dark);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setDistanceToTriggerSync(20);// in dips
        refreshLayout.setSize(SwipeRefreshLayout.DEFAULT);// LARGE also can be used

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {

                if (refreshToggle) {
                    checkConnection();
                } else {
                    refreshToggle = true;
                    //checkConnection();
                }
                // listView.setAdapter(adapter);

                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       // Toast.makeText(getApplicationContext(),"Successfully Loaded all Synced Data", Toast.LENGTH_SHORT).show();
                        customToast("Successfully Synced Data");
                        refreshLayout.setRefreshing(false);
                    }
                }, 10000);
            }
        };
    }

    public void refreshClickMethod(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                  Update the value background thread to UI thread
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        checkConnection();
                    }
                });


            }
        }).start();
    }

    public void eventClick(){
        gAdapter = new GridCustomeAdapter(this, MOBILE_OS);
        gv = (GridView) findViewById(R.id.gridView);
        gText = (TextView) findViewById(R.id.icontitle);
        gv.setAdapter(gAdapter);
        gAdapter.notifyDataSetChanged();

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                 if (position == 0) {
                    refreshClickMethod();
                }
                else if (position == 1) {
                    Intent intent = new Intent(getApplicationContext(), Packages.class);
                    intent.putExtra("action", "new");
                    intent.putExtra("subid", "");
                    startActivity(intent);
                }
                else if (position == 2) {
                    Intent intent = new Intent(GridViewActivity.this, SaveSubmissionActivity.class);
                    startActivity(intent);
                }
                else if (position == 3) {
                    Intent intent = new Intent(GridViewActivity.this, FeedbackList.class);
                    startActivity(intent);
                }
                else if (position == 4) {
                    Intent intent = new Intent(GridViewActivity.this, SubmittedForm.class);
                    startActivity(intent);
                }
               /* else if (position == 5) {
                    Intent intent = new Intent(GridViewActivity.this, ResourceActivity.class);
                    intent.putExtra("action", "new");
                    intent.putExtra("subid", "");
                    startActivity(intent);
                }
                else if (position == 6) {
                    Intent intent = new Intent(GridViewActivity.this, SaveResourcesActivity.class);
                    startActivity(intent);
                }*/
                else if (position == 5) {
                    clearData();
                }
                /*else if (position == 8) {
                    logout();
                }*/
                else {
                    //Toast.makeText(GridViewActivity.this, "Under Construction", Toast.LENGTH_SHORT).show();
                     customToast("Under Construction");
                }
            }
        });

    }


    public void locationMethod(){
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GridViewActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (location != null) {
                sharedPref = this.getSharedPreferences("GPSLOCATION", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("lat", Double.toString(location.getLatitude()));
                editor.putString("long", Double.toString(location.getLongitude()));
                editor.commit();
                // Toast.makeText(GridViewActivity.this,"Latitude: "+Double.toString(location.getLatitude())+"Longtitude: "+Double.toString(location.getLongitude()),Toast.LENGTH_SHORT).show();
            }
        } else {
            // showGPSDisabledAlertToUser();
            enableLoc();
        }
    }
    ///// Clear all data ///////////
    public void clearData() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.PauseDialog);
        builder.setMessage(R.string.deleteContact)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        clearAllData();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });

        android.app.AlertDialog d = builder.create();
        d.setTitle("Confirmation !");
        d.show();
    }

    public void clearAllData() {
        loadingprog = new ProgressDialog(this);
        loadingprog.setTitle("Deleting in Progress");
        loadingprog.setMessage("Please Wait...");
        loadingprog.setCancelable(false);
        loadingprog.show();

        mydb = new DBHelper(this);
        mydb.deleteAllShelter();
        mydb.deleteAllTasks();
        mydb.deleteAllUsers();
        mydb.deleteAllSUbmittedForm();
        mydb.deleteAllFeedback();
        boolean deleted = false;

        String fullPath = getFilesDir().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;
        File dir = new File(fullPath);
        if (dir.exists()) {
            ///////// View all images form folder///////////
            File[] files = dir.listFiles();
            if (files.length > 0) {
                // Log.v("listfile", "Size: "+ files.length);
                for (int i = 0; i < files.length; i++) {
                    Log.v("Files", "FileName:" + files[i].getName());
                    //deleted = this.deleteFile(files[i].getName());
                    ///////// Delete all images form folder (One by one)///////////
                    File file = new File(dir, files[i].getName());
                    deleted = file.delete();
                }
                if (deleted) {
                    mydb.deleteAllGallery();
                }
            }
        }

        if (mydb.deleteAllShelter()) {
            loadingprog.dismiss();
            //Toast.makeText(getApplicationContext(), "Successfully Deleted All Data", Toast.LENGTH_SHORT).show();
            customToast("Successfully Deleted All Data");

        } else {
            loadingprog.dismiss();
           // Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            customToast("Failed");

        }

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }


    public void onLocationChanged(Location location) {
        sharedPref = this.getSharedPreferences("GPSLOCATION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("lat", Double.toString(location.getLatitude()));
        editor.putString("long", Double.toString(location.getLongitude()));
        editor.commit();
        /*Toast.makeText(getApplicationContext(), "Latitude Value"+location.getLatitude()+
                " Lontitude Value"+location.getLongitude(), Toast.LENGTH_LONG).show();*/
       // getAddressFromLocation(location, getApplicationContext());
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onProviderDisabled(String provider) {
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            //showGPSDisabledAlertToUser();
            enableLoc();
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

    public void cleareallsharevalue() {
        sharedPref = this.getSharedPreferences("REGISTRATION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }




    ////// back key pressed closed app with 3 times pressed//////
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            turnGPSOff();
            finishAffinity();
            System.exit(0);

        }
        else {
            customToast("Press again to exit");
           // Toast.makeText(this, "Press again to exit",Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }

    //////// Syncining data for Shelter Code ///////////
    public boolean getPackages() {
        shelterflag = false;
        StringRequest request = new StringRequest(Request.Method.POST, URLPack, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("PackagesInfo: ",response);
                    JSONArray JA = new JSONArray(response);
                    for (int i = 0; i < JA.length(); i++) {
                        JSONObject obj = JA.getJSONObject(i);
                        int totalitem = obj.getInt("totalitems");
                        if (JA.length() == totalitem) {
                            try {
                                queryValues = new HashMap<String, String>();
                                Integer tid = obj.getInt("id");
                                String name = obj.getString("name");
                                String alias = obj.getString("alias");
                                Integer groupid = obj.getInt("group_id");
                                Integer flag = obj.getInt("flag");
                                queryValues.put("tid", Integer.toString(tid));
                                queryValues.put("name", name);
                                queryValues.put("alias", alias);
                                queryValues.put("group_id", Integer.toString(groupid));
                                queryValues.put("flag", Integer.toString(flag));
                                mydb = new DBHelper(GridViewActivity.this);
                                Cursor rs = mydb.checkExisting(name);
                                rs.moveToFirst();
                                //Log.v("exisitngpackages", Integer.toString(rs.getCount()));
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
                    Log.d("subpackagesdata: ",response);
                    JSONArray JA = new JSONArray(response);
                    for (int i = 0; i < JA.length(); i++) {
                        JSONObject obj = JA.getJSONObject(i);
                        int totalitem = obj.getInt("totalitems");
                        if (JA.length() == totalitem) {

                            try {

                                queryValues = new HashMap<String, String>();
                                Integer tid = obj.getInt("id");
                                String name = obj.getString("name");
                                String code = obj.getString("code");
                                String alias = obj.getString("alias");
                                Integer groupid = obj.getInt("package_id");
                                queryValues.put("tid", Integer.toString(tid));
                                queryValues.put("internal_code", code);
                                queryValues.put("name", name);
                                queryValues.put("alias", alias);
                                queryValues.put("package_id", Integer.toString(groupid));
                                mydb = new DBHelper(GridViewActivity.this);
                                Cursor rs = mydb.checkExistingSub(code);
                                rs.moveToFirst();
                                //Log.v("exisitngcode", Integer.toString(rs.getCount()));
                                rs.close();
                                if (rs.getCount() > 0) {
                                    int sid = rs.getColumnIndex("id");
                                    shelterflag = mydb.updateSubpackage(sid,tid,code,name,alias,groupid);
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
        mydb = new DBHelper(GridViewActivity.this);
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
                                int flag = obj.getInt("flag");
                                String name = obj.getString("name");
                                String details = obj.getString("details");

                                queryValues.put("tid", Integer.toString(tid));
                                queryValues.put("name", name);
                                queryValues.put("details", details);
                                queryValues.put("package_id",  package_id);
                                queryValues.put("flag", Integer.toString(flag));

                                Cursor rs = mydb.checkExistingTask(tid);
                                rs.moveToFirst();
                                Log.d("existingtasks", Integer.toString(rs.getCount()));
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
        mydb = new DBHelper(GridViewActivity.this);
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

                                Cursor rs = mydb.checkExistingSubTask(tid);
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

                        mydb = new DBHelper(GridViewActivity.this);
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

                        mydb = new DBHelper(GridViewActivity.this);

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

        RequestQueue queue = Volley.newRequestQueue(GridViewActivity.this);
        queue.add(request);

        return true;
    }

   /* public boolean getAllSyncData() {
        shelterflag = false;
        progressDialog = new ProgressDialog(GridViewActivity.this);
        progressDialog.setTitle("Syncing data");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onBackPressed();
            }
        });
        progressDialog.show();


        StringRequest request = new StringRequest(Request.Method.POST, URLAll, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.v("getAllSyncingDatas", response);

                    JSONArray JA = new JSONArray(response);
                    for (int i = 0; i < JA.length(); i++) {
                        JSONObject obj = JA.getJSONObject(i);
                       // int totalitem = obj.getInt("totalitems");

                            try {
                               mydb = new DBHelper(GridViewActivity.this);
                                ////////////////////// get and Store all Shelter Code from Server to SQLite///////////////////////////

                                queryValues = new HashMap<String, String>();
                                taskValues = new HashMap<String, String>();

                                String shCode = obj.getString("shelter");

                                String mTasks = obj.getString("name");
                                String seQue = obj.getString("sequence");
                                String stask = obj.getString("stask");

                                queryValues.put("code", shCode);

                                taskValues.put("name", mTasks);
                                taskValues.put("flag", seQue);
                                taskValues.put("stask", stask);

                                mydb.insertAllSyncData(queryValues,taskValues);
                                *//* queryValues = new HashMap<String, String>();
                                String shCode = obj.getString("shelter");
                                queryValues.put("code", shCode);
                                Cursor rs = mydb.checkExisting(shCode);
                                rs.moveToFirst();
                                rs.close();
                                if (rs.getCount() > 0) {
                                    int sid = rs.getColumnIndex("id");
                                    mydb.updateShelter(sid, shCode);
                                } else {
                                    mydb.insertShelter(queryValues);
                                }

                                ////////////////////// get and Store all Tasks from Server to SQLite///////////////////////////
                                taskValues = new HashMap<String, String>();
                                String mTasks = obj.getString("name");
                                String seQue = obj.getString("sequence");
                                String stask = obj.getString("stask");
                                taskValues.put("name", mTasks);
                                taskValues.put("flag", seQue);
                                taskValues.put("stask", stask);

                                Cursor rs1 = mydb.checkExistingTask(mTasks);
                                rs1.moveToFirst();
                                rs1.close();
                                if (rs1.getCount() > 0) {
                                    int sid1 = rs1.getColumnIndex("id");
                                    mydb.updateTask(sid1, mTasks, seQue, stask);
                                } else {
                                    mydb.insertTask(taskValues);
                                }

                                ////////////////////// get and Store all submisson forms from Server to SQLite///////////////////////////
                                formValues = new HashMap<String, String>();
                                int refid = obj.getInt("id");

                                formValues.put("refid", String.valueOf(refid));
                                formValues.put("userid", obj.getString("userid"));
                                formValues.put("shelid", obj.getString("scode"));
                                formValues.put("mtask", obj.getString("mtask"));
                                formValues.put("stask", obj.getString("task"));
                                formValues.put("ucom", obj.getString("comm"));
                                formValues.put("latv", obj.getString("lat"));
                                formValues.put("longv", obj.getString("lon"));
                                formValues.put("SubmisionDate", obj.getString("sdate"));

                                Cursor rs2 = mydb.checkExistingSub(refid);
                                rs2.moveToFirst();
                                rs2.close();
                                if (rs2.getCount() > 0) {
                                    int sid = rs2.getColumnIndex("id");
                                    mydb.updateSubmission(formValues, sid);
                                } else {
                                    mydb.insertSubmittedForm(formValues);
                                }
                        *//*

                                mydb.close();
                            } catch (SQLException s) {
                                new Exception("Error with DB Open");
                            }
                    }
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Successfully Synced all Data", Toast.LENGTH_SHORT).show();
                    Intent intents = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                    intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intents);

                } catch (JSONException e) {
                    Log.d("JSON Exception", e.toString());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //progressDialog.dismiss();
                Log.e("Error", "Failed");
                *//*Toast.makeText(getApplicationContext(),
                        "Data Loading failed", Toast.LENGTH_SHORT).show();*//*
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("allshelter", "allshelter");
                params.put("userid", Integer.toString(read_memory()));
                //params.put("terms", "FEP");

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
        return true;
    }*/


    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected(this);
        String message;
        if (isConnected) {
            if (getPackages() && getSubPackage() && majorTasks() && subTasks() && submittedFormCode() && feedbackSyncList()) {
                customToast("Synchronizing data \n Please Wait...");
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
            //getAllSyncData();
        } else {
            message = "No Internet Connection";
           // Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            customToast(message);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Not Connected !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefresh() {

        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                handler.sendEmptyMessage(0);
            }
        }, 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(GridViewActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            //getMyLocation();
        }
    }
    private void enableLoc() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(GridViewActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error","Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(GridViewActivity.this, REQUEST_LOCATION);
                               // finish();
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }

    private void turnGPSOff(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if(locationManager.isProviderEnabled("gps")){ //if gps is enabled
        //if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        turnGPSOff();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        turnGPSOff();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
