package app.ddc.lged.emcrp.reports;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Toast;

import java.util.ArrayList;

import app.ddc.lged.emcrp.NavigationActivity;
import app.ddc.lged.emcrp.R;
import app.ddc.lged.emcrp.connectivity.DBHelper;

public class Packages extends NavigationActivity {


    private DBHelper mydb;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecylePackageAdapter rcvAdapter;
    ArrayList<String> itemsArrayList1 = new ArrayList<>();
    Cursor rs,prs;
    LinearLayout linearLayout;
    Button packageid;
    SharedPreferences sharedPref;
    int packid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.common_layout, null, false);
        drawer.addView(view, 0);
        mydb = new DBHelper(this);
        Intent intv = getIntent();
        final String statusval = intv.getStringExtra("action");
        final int sid = intv.getIntExtra("subid", 0);

        sharedPref = this.getSharedPreferences("EditValues", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("subid", sid);
        editor.putString("statusval",  statusval);
        editor.commit();

        if(statusval.equals("edit")) {
            rs = mydb.getDataSUbmittedForm(sid);
            rs.moveToFirst();
            packid = rs.getInt(rs.getColumnIndex("package_id"));
            rs.close();
        }
        else{
            packid = 0;
        }
        getAllPackagesData(packid);
    }

    public void getAllPackagesData(Integer pid){
        if(pid!=0){
            prs = mydb.getSelectedPackage(pid);
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
                    Intent intent = new Intent(getApplicationContext(), SubPackages.class);
                    intent.putExtra("pids",pid);
                    startActivity(intent);
                }
            });
        }



        recyclerView = (RecyclerView) findViewById(R.id.recylv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mydb = new DBHelper(this);

        Cursor response = mydb.getPackageData();
        int totalitem = response.getCount();

        Log.v("totalPackages", Integer.toString(totalitem));
        if(totalitem > 0) {
            rcvAdapter = new RecylePackageAdapter(this, mydb.getPackageData());
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
        mydb.deletePackage(id);
        rcvAdapter.swapCursor(mydb.getPackageData());
    }
}