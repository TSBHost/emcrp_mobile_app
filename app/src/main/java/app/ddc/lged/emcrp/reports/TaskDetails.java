package app.ddc.lged.emcrp.reports;

import androidx.appcompat.app.ActionBar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import app.ddc.lged.emcrp.NavigationActivity;
import app.ddc.lged.emcrp.R;
import app.ddc.lged.emcrp.connectivity.DBHelper;
import app.ddc.lged.emcrp.home.LoginActivity;

public class TaskDetails  extends NavigationActivity {

    View v;
    Button nextbtn,prevbtn;
    Context context;
    EditText edt;
    SharedPreferences sharedPref;
    private static final int REQUEST_CODE = 1234;
    ImageButton bt_voiceinput;
    private static final String REQUIRED_MSG = "Sorry! Need to add some comments";
    public DBHelper mydb;
    String ucomments;
    Cursor rs;
    String exDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isLogin() && read_memory() != 0) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.activity_task_details, null,false);
            drawer.addView(v,0);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("Report Details");
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

            mydb = new DBHelper(this);
            edt = (EditText) findViewById(R.id.usertaskcomment);


            Intent intv = getIntent();
            final int sid = intv.getIntExtra("subtaskid",0);

            sharedPref = this.getSharedPreferences("ReportsSession", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("subtask_id", sid);
            editor.commit();

            sharedPref = this.getSharedPreferences("EditValues", Context.MODE_PRIVATE);
            int subid = sharedPref.getInt("subid", 0);
            String statusval = sharedPref.getString("statusval",null);

            if(statusval.equals("edit")) {
                rs = mydb.getDataSUbmittedForm(subid);
                rs.moveToFirst();
                exDetails = rs.getString(rs.getColumnIndex("Comments"));
                rs.close();
                edt.setText(exDetails);
            }


            nextbtn = (Button) findViewById(R.id.nextbtn);
            nextbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(hasText(edt)){
                        sharedPref = getSharedPreferences("ReportsSession", Context.MODE_PRIVATE);
                        int packageid = sharedPref.getInt("package_id", 0);
                        int subpackageid = sharedPref.getInt("subpackage_id", 0);
                        int mtaskid = sharedPref.getInt("majortask_id", 0);
                        int staskid = sharedPref.getInt("subtask_id", 0);
                        String sheltercode = sharedPref.getString("sheltercode",null);

                        Intent intentnext;
                        intentnext = new Intent(getApplicationContext(), PicturesActivity.class);
                        intentnext.putExtra("action", statusval);
                        intentnext.putExtra("packageid", packageid);
                        intentnext.putExtra("subpackageid", subpackageid);
                        intentnext.putExtra("mtaskid", mtaskid);
                        intentnext.putExtra("staskid", staskid);
                        intentnext.putExtra("sheltercode", sheltercode);
                        intentnext.putExtra("ucomment", edt.getText().toString());
                        startActivity(intentnext);
                    }
                }
            });



            edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                }
            });
            bt_voiceinput = (ImageButton) findViewById(R.id.ib_speak);
            bt_voiceinput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startVoiceRecognitionActivity();
                }
            });
        } else {
            Intent i = new Intent(this.getBaseContext().getApplicationContext(), LoginActivity.class);
            this.startActivity(i);
        }



    }
    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");
        startActivityForResult(intent, REQUEST_CODE);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (!textMatchList.isEmpty()) {
                    String Query = textMatchList.get(0);
                    // uf.setText(Query);
                    if(edt.getText().length() > 0)
                    {
                        edt.setText( edt.getText().toString() + Query);
                    }
                    else
                    {
                        edt.setText(Query);
                    }
                }

                //Result code for various error.
            } else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
                showToastMessage("Network Error");
            } else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
                showToastMessage("No Match");
            } else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
                showToastMessage("Server Error");
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showToastMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(REQUIRED_MSG);
            return false;
        }

        return true;
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
}