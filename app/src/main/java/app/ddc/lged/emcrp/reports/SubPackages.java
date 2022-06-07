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

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.ddc.lged.emcrp.NavigationActivity;
import app.ddc.lged.emcrp.R;
import app.ddc.lged.emcrp.connectivity.DBHelper;

public class SubPackages extends NavigationActivity {


    private DBHelper mydb;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyleSubPackageAdapter rcvAdapter;
    ArrayList<String> itemsArrayList1 = new ArrayList<>();
    TextView instance;
    int pid;
    SharedPreferences sharedPref;
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

        instance = (TextView) findViewById(R.id.instance);
        instance.setText("Sub Packages");
        mydb = new DBHelper(this);
        Intent pintent = getIntent();
        pid = pintent.getIntExtra("pids",0);
        //Toast.makeText(getApplicationContext(), "IDs"+pid, Toast.LENGTH_LONG).show();

        sharedPref = this.getSharedPreferences("EditValues", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        int sid = sharedPref.getInt("subid", 0);
        String statusval = sharedPref.getString("statusval",null);

        if(statusval.equals("edit")) {
            rs = mydb.getDataSUbmittedForm(sid);
            rs.moveToFirst();
            packid = rs.getInt(rs.getColumnIndex("subpackage_id"));
            rs.close();
        }
        else{
            packid = 0;
        }

        getALlPackagesData(packid);
    }

    public void getALlPackagesData(int spid){
        if(spid!=0){
            prs = mydb.getSelectedSubPackage(spid);
            prs.moveToFirst();
            String packagename = prs.getString(prs.getColumnIndex("internal_code"));
            linearLayout = (LinearLayout) findViewById(R.id.selecteddatas);
            linearLayout.setVisibility(View.VISIBLE);
            packageid = (Button) findViewById(R.id.packageid);
            packageid.setText(packagename);
            prs.close();

            packageid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), Majortask.class);
                    intent.putExtra("subpackage_id",spid);
                    intent.putExtra("package_id",pid);
                    intent.putExtra("sheltercode",packagename);
                    startActivity(intent);
                }
            });
        }

        recyclerView = (RecyclerView) findViewById(R.id.recylv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mydb = new DBHelper(this);

        Cursor response = mydb.getSubPackageData(pid);
        int totalitem = response.getCount();

        Log.v("totalSubPackages", Integer.toString(totalitem));
        if(totalitem > 0) {
            rcvAdapter = new RecyleSubPackageAdapter(this, mydb.getSubPackageData(pid));
            recyclerView.setAdapter(rcvAdapter);
            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    removeItem((int) viewHolder.itemView.getTag());
                }
            }).attachToRecyclerView(recyclerView);
            response.close();
        }
        else{
            Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.zerodata);
            linearLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void removeItem(int id){
        mydb.deleteSubpackage(id);
        rcvAdapter.swapCursor(mydb.getSubPackageData(pid));

    }
}