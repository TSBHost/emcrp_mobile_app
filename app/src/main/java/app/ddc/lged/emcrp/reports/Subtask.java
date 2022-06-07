package app.ddc.lged.emcrp.reports;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import app.ddc.lged.emcrp.NavigationActivity;
import app.ddc.lged.emcrp.R;
import app.ddc.lged.emcrp.connectivity.DBHelper;

public class Subtask extends NavigationActivity {


    private DBHelper mydb;
    RecyclerView recyclerView;
    RecyleSubtaskAdapter rcvAdapter;
    SharedPreferences sharedPref;
    TextView instance;
    int pid;
    LinearLayout linearLayout;
    Button packageid;
    Cursor rs,prs;
    int packid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.common_layout, null, false);
        drawer.addView(view, 0);
        mydb = new DBHelper(this);
        instance = (TextView) findViewById(R.id.instance);
        instance.setText("Sub tasks");

        Intent pintent = getIntent();
        pid = pintent.getIntExtra("majortask",0);

        sharedPref = this.getSharedPreferences("ReportsSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("majortask_id", pid);
        editor.commit();

        sharedPref = this.getSharedPreferences("EditValues", Context.MODE_PRIVATE);
        int sid = sharedPref.getInt("subid", 0);
        String statusval = sharedPref.getString("statusval",null);

        if(statusval.equals("edit")) {
            rs = mydb.getDataSUbmittedForm(sid);
            rs.moveToFirst();
            packid = rs.getInt(rs.getColumnIndex("Tasks"));
            rs.close();
        }
        else{
            packid = 0;
        }

        getALlPackagesData(packid);
    }

    public void getALlPackagesData(int stid){
        if(stid!=0){
            prs = mydb.getSelectedSubtask(stid);
            prs.moveToFirst();
            String packagename = prs.getString(prs.getColumnIndex("name"));
            linearLayout = (LinearLayout) findViewById(R.id.selecteddatas);
            linearLayout.setVisibility(View.VISIBLE);
            packageid = (Button) findViewById(R.id.packageid);
            packageid.setText(packagename);
            prs.close();

            packageid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), TaskDetails.class);
                    intent.putExtra("subtaskid",stid);
                    startActivity(intent);
                }
            });
        }


        recyclerView = (RecyclerView) findViewById(R.id.recylv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mydb = new DBHelper(this);

        Cursor response = mydb.getAllSubTasks(pid);
        int totalitem = response.getCount();

        Log.v("totalMajortask", Integer.toString(totalitem));
        if(totalitem > 0) {
            rcvAdapter = new RecyleSubtaskAdapter(this, mydb.getAllSubTasks(pid));
            recyclerView.setAdapter(rcvAdapter);
            response.close();
        }
        else{
            Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.zerodata);
            linearLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}