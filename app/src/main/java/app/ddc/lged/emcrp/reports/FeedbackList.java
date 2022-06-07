package app.ddc.lged.emcrp.reports;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.ddc.lged.emcrp.NavigationActivity;
import app.ddc.lged.emcrp.R;
import app.ddc.lged.emcrp.connectivity.Config;
import app.ddc.lged.emcrp.connectivity.DBHelper;
import app.ddc.lged.emcrp.home.GridViewActivity;
import app.ddc.lged.emcrp.home.LoginActivity;

public class FeedbackList extends NavigationActivity {

    Config conf=new Config();
    private final String URL = conf.getURL()+"get_feedback.php";
    ProgressDialog progressDialog;
    SharedPreferences sharedPref; private DBHelper mydb;
    ArrayList<FeedbackModel> arrayList = new ArrayList<FeedbackModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isLogin() && read_memory() != 0) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.activity_feedback_list, null, false);
            drawer.addView(view,0);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("Feedback");

            sharedPref = getSharedPreferences("LOGININFO", Context.MODE_PRIVATE);
            int userid = sharedPref.getInt("userid", 0);
            if(userid > 0) {
                autocomplete();
            }
            else{
               // Toast.makeText(getApplicationContext(), "Invalid Request", Toast.LENGTH_LONG).show();
                customToast("Invalid Request");
            }
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
        final RecyclerView recfeed = (RecyclerView) findViewById(R.id.recyfeedback);
        recfeed.setLayoutManager(new LinearLayoutManager(this));
        mydb = new DBHelper(this);

        sharedPref = getSharedPreferences("LOGININFO", MODE_PRIVATE);
        int userid = sharedPref.getInt("userid", 0);

        Cursor response = mydb.getDataFeedback(userid);
       /// ArrayList array_list = mydb.getAllLikeFeedback(userid);
        int totalitem = response.getCount();
       // Log.v("TotalFeedData", array_list.toString());
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
            recfeed.setAdapter(new RecyleFeedbackAdapter(this, arrayList));
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
        Intent intentfeed = new Intent(getApplicationContext(), GridViewActivity.class);
        startActivity(intentfeed);
    }
}
