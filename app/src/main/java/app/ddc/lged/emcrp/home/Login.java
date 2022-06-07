package app.ddc.lged.emcrp.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Random;

import app.ddc.lged.emcrp.R;
import app.ddc.lged.emcrp.reports.SharedPrefManager;
import app.ddc.lged.emcrp.connectivity.Config;
import app.ddc.lged.emcrp.connectivity.ConnectivityReceiver;
import app.ddc.lged.emcrp.connectivity.DBHelper;


public class Login extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener,View.OnClickListener{
    EditText username,pass;
    TextView showPass;
    Button subBtn;
    CheckBox chk;
    Intent intent;
    Activity activity;
    HashMap<String, String> queryValues;
    private DBHelper mydb;
    Context appCtx;
    Config conf = new Config();
    public final String URL = "http://mdspbd.com/mdspbd_app/logins.php";
    SharedPreferences sharedPref;
    String uname,email,contact,loginid;
    int useridid;
    boolean loginstatus;
    String token;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(isLogin() && read_memory()!= 0){
            Intent i = new Intent(this.getBaseContext().getApplicationContext(), GridViewActivity.class);
            this.startActivity(i);
        }
        else {
            token = SharedPrefManager.getInstance(Login.this).getDeviceToken();
            setContentView(R.layout.activity_login);
            activity = Login.this;
            appCtx = getApplicationContext();
            initView();
        }
    }

    /////////////////// initView method define from Contract interface and call by Presenter///////////////////////////////
    public void initView(){
        username = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.password);
        showPass = (TextView) findViewById(R.id.text_view);
        chk = (CheckBox) findViewById(R.id.showpassword);
        subBtn = (Button) findViewById(R.id.email_sign_in_button);

        subBtn.setOnClickListener(this);
        showPassword();
    }

    /////////////////// Hide/Show Password by using CheckBox///////////////////////////////
    public void showPassword(){
        chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showPass.setText(R.string.hide);
                } else {
                    pass.setInputType(129);
                    showPass.setText(R.string.show);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        sendBtn();
    }

    public void sendBtn(){
        String usname = username.getText().toString().trim();
        String pword = pass.getText().toString().trim();


        if(TextUtils.isEmpty(usname) || TextUtils.isEmpty(pword)){
            loginValidation();
        }
        else {
            loginAction(usname,pword,token);
        }


    }
    public void loginAction(final String usrname, final String pword, final String tok) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Proccessing");
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        progressDialog.dismiss();

                        try {
                            Log.v("detailscont",s.toString());
                            JSONObject jsonobject = new JSONObject(s);
                            JSONArray jsonarray = jsonobject.getJSONArray("product_details");
                            JSONObject obj = jsonarray.getJSONObject(0);

                            // uname = obj.getString("name");
                            uname = obj.getString("useridid");
                            loginid = obj.getString("username");
                            email = obj.getString("email");
                            contact = obj.getString("contact");
                            loginstatus = obj.getBoolean("loggedin");
                            // useridid = obj.getInt("useridid");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        storeuserdata(uname,loginid,email,contact,loginstatus);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        //Log.e("Error", volleyError.getMessage());
                        Toast.makeText(getApplicationContext(),
                                "Image Uploading Problem. Please Select all image and try again", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new Hashtable<String, String>();
                params.put("usrname", usrname);
                params.put("password", pword);
                params.put("token", tok);

                return params;
            }
        };

        //VolleyAppController.getInstance().addToRequestQueue(stringRequest);

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

   /* public void loginAction(String uname, String pword, String tok){
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(ROOT_URL) //Setting the Root URL
                .build(); //Finally building the adapter

        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(Login.this);
        progressDoalog.setMax(100);
        progressDoalog.setMessage("Its loading....");
        progressDoalog.setTitle("ProgressDialog bar example");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // show it
        progressDoalog.show();

        //Creating object for our interface
        Logins api = adapter.create(Logins.class);

        //Defining the method insertuser of our interface
        api.insertUser(uname, pword,tok,
                //Creating an anonymous callback
                new Callback<Response>() {
                    @Override
                    public void success(Response result, Response response) {
                        BufferedReader reader = null;
                        String output = "";

                        try {
                            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                            output = reader.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        progressDoalog.dismiss();
                        loginSuccess(output);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressDoalog.dismiss();
                        //message(error.toString());
                        loginError();
                    }
                }
        );
    }*/


    /////////////////// Show Toast Messages for this Class by passing Stirng Type Parameter as message///////////////////////////////
    public void message(String msg) {
        Toast.makeText(appCtx,msg,Toast.LENGTH_LONG).show();
    }


    public void loginValidation() {
        message("Please Enter Valid Username and Passowrd");
    }

   /* @Override
    public void loginSuccess(String output) {
        //message("Output: "+ output);
        try {
            Log.e("userLoginDetailsInfo", output);
            JSONObject json = new JSONObject(output);
            JSONArray data = json.getJSONArray("logindetails");
            JSONObject obj = data.getJSONObject(0);

           // uname = obj.getString("name");
            uname = obj.getString("useridid");
            loginid = obj.getString("username");
            email = obj.getString("email");
            contact = obj.getString("contact");
            loginstatus = obj.getBoolean("loggedin");
           // useridid = obj.getInt("useridid");
            storeuserdata(uname,loginid,email,contact,loginstatus);

        }
        catch (JSONException e){
            e.printStackTrace();
        }
        //Toast.makeText(getApplicationContext(),""+read_memory()+isLogin(),Toast.LENGTH_LONG).show();
        //intent = new Intent(getApplicationContext(), GridViewActivity.class);
        //startActivity(intent);
    }

*/
    public void storeuserdata(String name,String loginid,String email,String contact,boolean loginstatus){
        message("Output: "+ name+email+contact+loginstatus);

        sharedPref = this.getSharedPreferences("LOGININFO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", email);
        editor.putString("name", uname);
        editor.putString("contact", contact);
        editor.putInt("userid", useridid);
        editor.putBoolean("loggedin", loginstatus);
        editor.commit();

        Random rand = new Random();
        int pin = rand.nextInt(10000);
        //Toast.makeText(getApplicationContext(),""+pin, Toast.LENGTH_LONG).show();

        queryValues = new HashMap<String, String>();
        queryValues.put("name", uname);
        queryValues.put("username", email);
        queryValues.put("userid", Integer.toString(useridid));
        queryValues.put("contact", contact);
        queryValues.put("pin", Integer.toString(pin));
        // queryValues.put("pin", "1234");
        mydb = new DBHelper(this);
        Cursor rs = mydb.checkExistingUser(Integer.toString(useridid));
        rs.moveToFirst();
        if(rs.getCount() > 0) {
            int sid = rs.getColumnIndex("id");
            mydb.updateUsers(queryValues,sid);
        }
        else{
            mydb.insertUser(queryValues);
        }
    }


    public void loginError() {
        message("Login Failed. Please try again with Valid Credentials");
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

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
