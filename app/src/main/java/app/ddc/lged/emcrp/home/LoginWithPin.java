package app.ddc.lged.emcrp.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import app.ddc.lged.emcrp.R;
import app.ddc.lged.emcrp.connectivity.Config;
import app.ddc.lged.emcrp.connectivity.DBHelper;

public class LoginWithPin extends AppCompatActivity {

    SharedPreferences sharedPref;
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText uPin;
    private View mProgressView;
    private View mLoginFormView;
    Config conf=new Config();
    HashMap<String, String> queryValues;
    private DBHelper mydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_pin);

        uPin = (EditText) findViewById(R.id.uspin);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


    }
    private void attemptLogin() {
        String pinno = uPin.getText().toString();
       // Toast.makeText(getApplicationContext(), pinno, Toast.LENGTH_LONG).show();
        if (mAuthTask != null) {
            return;
        }

        uPin.setError(null);

        View focusView = null;
        if(pinno.isEmpty()){
            //Toast.makeText(getApplicationContext(),"Need a valid MDSP User id", Toast.LENGTH_LONG).show();
            uPin.setError("Please Enter a Valid Pin");
            focusView = uPin;
            focusView.requestFocus();
        }
        else{
            showProgress(true);
            mAuthTask = new UserLoginTask(pinno);
            mAuthTask.execute((Void) null);
        }
    }


    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        Context context;
        private final String userPin;

        UserLoginTask(String uspin) {
            userPin = uspin;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            boolean status;

            mydb = new DBHelper(LoginWithPin.this);
            Cursor rs = mydb.loginWithPin(userPin);
            rs.moveToFirst();
            if(rs.getCount() > 0) {
                int userid = rs.getInt(rs.getColumnIndex("userid"));
                String username = rs.getString(rs.getColumnIndex("username"));
                String password = rs.getString(rs.getColumnIndex("password"));
                String name = rs.getString(rs.getColumnIndex("name"));
                String org = rs.getString(rs.getColumnIndex("organization"));
                String desig = rs.getString(rs.getColumnIndex("designation"));
                String contact = rs.getString(rs.getColumnIndex("contact"));
                Boolean loggedsts = true;
                save_session_info(userid,username,password,name,loggedsts,org,desig,contact);
                start_admin();
                status = true;
            }
            else{
                status = false;
            }

            return status;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                mAuthTask = null;
                showProgress(false);
            } else {
                mAuthTask = null;
                showProgress(false);

                uPin.setError(getString(R.string.error_incorrect_pin));
                uPin.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);

        }
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
    public void save_session_info(int uid, String uname, String pass, String name, Boolean logstatus,String org,String desig,String contact) {
        sharedPref = this.getSharedPreferences("LOGININFO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", uname);
        editor.putString("name", name);
        editor.putString("password", pass);
        editor.putString("organization", org);
        editor.putString("designation", desig);
        editor.putString("contact", contact);
        editor.putInt("userid", uid);
        editor.putBoolean("loggedin", logstatus);
        editor.commit();


        queryValues = new HashMap<String, String>();
        queryValues.put("name", name);
        queryValues.put("username", uname);
        queryValues.put("userid", Integer.toString(uid));
        queryValues.put("pass", pass);
        queryValues.put("org", org);
        queryValues.put("desig", desig);
        queryValues.put("contact", contact);
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
}
