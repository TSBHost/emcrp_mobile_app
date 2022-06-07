package app.ddc.lged.emcrp.reports;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.appcompat.app.ActionBar;
import android.os.Bundle;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import app.ddc.lged.emcrp.NavigationActivity;
import app.ddc.lged.emcrp.R;
import app.ddc.lged.emcrp.connectivity.DBHelper;
import app.ddc.lged.emcrp.home.LoginActivity;

public class SubmittedForm extends NavigationActivity {

    ProgressDialog progressDialog;
    SharedPreferences sharedPref;
    private DBHelper mydb;
    private Context context;
    ProgressDialog loading;
    public final static String APP_PATH_SD_CARD = "/EMCRPbd/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "images";
    //private final String IUrl = conf.getImgURL()+"news/";
    String Image1,Image2,Image3,Image4,Image5;
    int Gid;
    RecyclerView rcv;
    RecyleAdapter rcvAdapter;
    int userid;
    ArrayList<SaveSubmissionModel> arrayList = new ArrayList<SaveSubmissionModel>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isLogin() && read_memory() != 0) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.activity_submitted_form, null, false);
            drawer.addView(view,0);

            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("Review Forms");
            sharedPref = getSharedPreferences("LOGININFO", Context.MODE_PRIVATE);
            int userid = sharedPref.getInt("userid", 0);
            if(userid > 0) {
                autocomplete();
            }
            else{
                Toast.makeText(getApplicationContext(), "Invalid Request", Toast.LENGTH_LONG).show();
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
        rcv = (RecyclerView) findViewById(R.id.recylv);
        rcv.setLayoutManager(new LinearLayoutManager(this));
        mydb = new DBHelper(this);

        sharedPref = getSharedPreferences("LOGININFO", MODE_PRIVATE);
        userid = sharedPref.getInt("userid", 0);
        Cursor rs = mydb.getDataSUbmittedForm(userid);
        //rs.moveToFirst();
        //Log.v("TotalDataSubmission", Integer.toString(rs.getCount()));

        Cursor response = mydb.getAllForms(1,userid);
        int totalitem = response.getCount();
        // Log.v("totalitem", Integer.toString(totalitem));

        //ArrayList array_list = mydb.getAllLikeGallery(userid);
        //Log.v("usergallist", array_list.toString());

        // ArrayList array_list = mydb.getAllLikeForms(userid);
        //Log.v("userformlist", array_list.toString());

       /* if(response!= null){
            if(response.moveToFirst()){
                do{
                    SaveSubmissionModel saveitem = new SaveSubmissionModel();
                    saveitem.setId(response.getInt(response.getColumnIndex("id")));
                    saveitem.setShelter(response.getString(response.getColumnIndex("ShelterCode")));
                    saveitem.setMtask(response.getString(response.getColumnIndex("MajorTasks")));
                    saveitem.setTasks(response.getString(response.getColumnIndex("Tasks")));
                    saveitem.setDate(response.getString(response.getColumnIndex("SubmisionDate")));
                    arrayList.add(saveitem);

                }
                while(response.moveToNext());
            }
        }*/

        if(totalitem > 0) {
            rcvAdapter = new RecyleAdapter(this, mydb.getAllForms(1,userid));
            rcv.setAdapter(rcvAdapter);
            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    removeItem((int) viewHolder.itemView.getTag());
                }
            }).attachToRecyclerView(rcv);
            response.close();
        }
        else{
            Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.zerodata);
            linearLayout.setVisibility(View.VISIBLE);
            rcv.setVisibility(View.GONE);
        }
    }

    private void removeItem(int id){
        mydb.deleteSUbmittedForm(id);
        rcvAdapter.swapCursor(mydb.getAllForms(1,userid));

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.savesubmission, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            exit_app();
        }
        if (id == R.id.deletesub) {

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setMessage(R.string.deleteContact)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new deleteClass().execute();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            return;
                        }
                    });

            android.app.AlertDialog d = builder.create();
            d.setTitle("Are you sure");
            d.show();
        }

        return super.onOptionsItemSelected(item);
    }


    public class deleteClass extends AsyncTask<Integer, Integer, Void> {
        @Override
        protected void onPreExecute() {
            //loading = ProgressDialog.show(SavedSubmissionDetails.this,"Loading Shelter data","Please wait...",false,false);
            loading = new ProgressDialog(SubmittedForm.this);
            loading.setTitle("Deleting");
            loading.setMessage("Please Wait...");
            loading.setIcon(R.drawable.loader);
            loading.show();
        }


        @Override
        protected Void doInBackground(Integer... position) {

            mydb.deleteAllSUbmittedForm();

            boolean deleted = false;

            String fullPath = getFilesDir().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;
            File dir = new File(fullPath);
            if(dir.exists()) {
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

            if(mydb.deleteAllSUbmittedForm()) {
                loading.dismiss();
                displayError("Successfully Deleted All Data");

            }
            else{
                loading.dismiss();
                displayError("Failed to Delete");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            loading.dismiss();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    Handler messageHandler = new Handler();
    public void displayError(final String errorText) {
        Runnable doDisplayError = new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), errorText, Toast.LENGTH_LONG).show();
            }
        };
        messageHandler.post(doDisplayError);
    }
}
