
package app.ddc.lged.emcrp.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;

import app.ddc.lged.emcrp.R;
import app.ddc.lged.emcrp.reports.SharedPrefManager;
import app.ddc.lged.emcrp.connectivity.Config;
import app.ddc.lged.emcrp.connectivity.ConnectivityReceiver;
import app.ddc.lged.emcrp.connectivity.DBHelper;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener {

    SharedPreferences sharedPref;
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    TextView showtext;
    private View mProgressView;
    private View mLoginFormView;
    Config conf=new Config();
    URL url = null;
    HashMap<String, String> queryValues;
    private DBHelper mydb;
    Button exspn;
    Context context;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(isLogin() && read_memory()!= 0){
            Intent i = new Intent(this.getBaseContext().getApplicationContext(), GridViewActivity.class);
            this.startActivity(i);
        }
        else {
            token = SharedPrefManager.getInstance(LoginActivity.this).getDeviceToken();
            setContentView(R.layout.activity_login);
            mEmailView = (EditText) findViewById(R.id.email);

            mPasswordView = (EditText) findViewById(R.id.password);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        checkConnection();
                        return true;
                    }
                    return false;
                }
            });

            exspn = (Button) findViewById(R.id.existingPin);
            exspn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intpin = new Intent(LoginActivity.this, LoginWithPin.class);
                    startActivity(intpin);
                }
            });

            Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //attemptLogin();
                    checkConnection();
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
            showtext = (TextView) this.findViewById(R.id.text_view);

            CheckBox checkbox = (CheckBox) findViewById(R.id.showpassword);
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mPasswordView.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        showtext.setText(R.string.hide);
                    } else {
                        mPasswordView.setInputType(129);
                        showtext.setText(R.string.show);
                    }
                }
            });
        }
       // Toast.makeText(getApplicationContext(), conf.getURL()+"login.php", Toast.LENGTH_LONG).show();
    }





    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }


        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        View focusView = null;
        if(email.isEmpty()){
            //Toast.makeText(getApplicationContext(),"Need a valid MDSP User id", Toast.LENGTH_LONG).show();
            mEmailView.setError("Please Enter a Valid EMCRP User ID");
            focusView = mEmailView;
            focusView.requestFocus();
        }
        else if(password.isEmpty()){
            //Toast.makeText(getApplicationContext(),"Need a valid MDSP User password", Toast.LENGTH_LONG).show();
            mPasswordView.setError("Please Enter a Valid EMCRP Password");
            focusView = mPasswordView;
            focusView.requestFocus();
        }
        else{
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 2;
    }



    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }





    /**
     * login query
     * send request to server and get response
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        String server_response;
        String json_data;
        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            HttpURLConnection urlConnection = null;
            boolean status = false;

            try {

                url = new URL(conf.getURL()+"login.php");

                urlConnection = (HttpURLConnection) url.openConnection();


                String str="username="+mEmail;
                str+="&password="+mPassword;
                str+="&token="+token;

                urlConnection.setRequestMethod("POST");

                urlConnection.setDoOutput(true);

                DataOutputStream dStream = new DataOutputStream(urlConnection.getOutputStream());
                dStream.writeBytes(str);
                dStream.flush();
                dStream.close();

                int responseCode = urlConnection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    server_response = readStream(urlConnection.getInputStream());
                    json_data=server_response;
                    Log.v("LoginInformation", server_response);
                    final JSONObject reader = new JSONObject(server_response);
                    if(reader.getString("login").compareTo("success")==0) {
                        int userid = reader.getInt("id");
                        String username = reader.getString("username");
                        String password = reader.getString("password");
                        String name = reader.getString("name");
                        String org = reader.getString("org");
                        String desig = reader.getString("desig");
                        int district = reader.getInt("district");
                        String contact = reader.getString("contact");
                        Boolean loggedsts = reader.getBoolean("loggedin");
                        save_session_info(userid,username,password,name,loggedsts,org,desig,contact,district);
                        start_admin();
                        status = true;
                    }
                    else {
                        status = false;
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Faild to login", Toast.LENGTH_LONG).show();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }   finally {

                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
            }


            return status;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                mAuthTask = null;
                showProgress(false);
                //Toast.makeText(getApplicationContext(), "Token "+token, Toast.LENGTH_LONG).show();
            } else {
                mAuthTask = null;
                showProgress(false);
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();

                mEmailView.setError(getString(R.string.error_incorrect_password));
                mEmailView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);

        }
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    public void start_admin() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //dialog.hide();

            }
        });
        Intent i = new Intent(this.getBaseContext().getApplicationContext(), GridViewActivity.class);
        this.startActivity(i);
    }

    ////// get login information into session //////
    public void save_session_info(int uid, String uname, String pass, String name, Boolean logstatus,String org,String desig,String contact,int district) {
        sharedPref = this.getSharedPreferences("LOGININFO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", uname);
        editor.putString("name", name);
        editor.putString("password", pass);
        editor.putString("organization", org);
        editor.putString("designation", desig);
        editor.putInt("district", district);
        editor.putString("contact", contact);
        editor.putInt("userid", uid);
        editor.putBoolean("loggedin", logstatus);
        editor.commit();

        Random rand = new Random();
        int pin = rand.nextInt(10000);
        //Toast.makeText(getApplicationContext(),""+pin+". Dist: "+district, Toast.LENGTH_LONG).show();

        queryValues = new HashMap<String, String>();
        queryValues.put("name", name);
        queryValues.put("username", uname);
        queryValues.put("userid", Integer.toString(uid));
        queryValues.put("pass", pass);
        queryValues.put("org", org);
        queryValues.put("desig", desig);
        queryValues.put("district", Integer.toString(district));
        queryValues.put("contact", contact);
        queryValues.put("pin", Integer.toString(pin));
        // queryValues.put("pin", "1234");
        mydb = new DBHelper(this);
        Cursor rs = mydb.checkExistingUser(Integer.toString(uid));
        rs.moveToFirst();
        if(rs.getCount() > 0) {
            int sid = rs.getColumnIndex("id");
            mydb.updateUsers(queryValues,sid);
        }
        else{
            mydb.insertUser(queryValues);
        }

        //mydb.insertUser(queryValues);
    }


    ////// Login check true or false//////
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

    ////// back key pressed closed app with 3 times pressed//////
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finishAffinity();
            System.exit(0);
        }
        else {
            customToast("Press again to exit");
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }


    public void customToast(String tstMsg){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custtoastid));
        ImageView imgv = (ImageView) layout.findViewById(R.id.customToastImage);
        TextView text = (TextView) layout.findViewById(R.id.customToastText);
        imgv.setImageDrawable(getResources().getDrawable(R.drawable.sync));
        text.setText(tstMsg);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    ////// cehck connectivity//////
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected(this);
        //showSnack(isConnected);
        String message;
        if (isConnected) {
            message = "Connected";

            attemptLogin();
            //color = Color.WHITE;
        } else {
            message = "No Internet Connection";
            customToast(message);
            //color = Color.RED;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
       // MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        //showSnack(isConnected);
        String message;
        if (isConnected) {
            message = "Connected";
            //Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            attemptLogin();
            //color = Color.WHITE;
        } else {
            message = "No Internet Connection";
            customToast(message);
            //color = Color.RED;
        }
    }

}

