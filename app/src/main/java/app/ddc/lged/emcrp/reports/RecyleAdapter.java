package app.ddc.lged.emcrp.reports;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.MultiSelector;

import java.io.File;
import java.util.Collections;
import java.util.List;

import app.ddc.lged.emcrp.R;
import app.ddc.lged.emcrp.connectivity.DBHelper;


public class RecyleAdapter extends RecyclerView.Adapter<RecyleAdapter.CustomVHolder> {

    //CustomVHolder cvh;
    private Context context;
    private DBHelper mydb;
    Activity activity;
    List<SaveSubmissionModel> data = Collections.emptyList();
    public final static String APP_PATH_SD_CARD = "/MDSP/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "images";
    //private final String IUrl = conf.getImgURL()+"news/";
    String Image1,Image2,Image3,Image4,Image5;
    int Gid;
    ProgressDialog loading;
    Cursor mCursor;

   /* public RecyleAdapter(Context context, List<SaveSubmissionModel> data) {
        this.context = context;
        this.data = data;
    }*/

    public RecyleAdapter(Context context, Cursor data) {
        this.context = context;
        this.mCursor = data;

       // sharedPref = getSharedPreferences("LOGININFO", MODE_PRIVATE);
       // int userid = sharedPref.getInt("userid", 0);
       // mCursor = mydb.getAllForms(1,userid);


    }

    @Override
    public CustomVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item, parent, false);
        //cvh = new CustomVHolder(view);
        return new CustomVHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomVHolder holder, final int position) {

        if(!mCursor.moveToPosition(position)){
            return;
        }

       final int id =  mCursor.getInt(mCursor.getColumnIndex(ReviewSubmissionModel.ReviewEntry.id));
        String shelter = mCursor.getString(mCursor.getColumnIndex(ReviewSubmissionModel.ReviewEntry.ShelterCode));
        String mTask = mCursor.getString(mCursor.getColumnIndex(ReviewSubmissionModel.ReviewEntry.MajorTasks));
        String sDate = mCursor.getString(mCursor.getColumnIndex(ReviewSubmissionModel.ReviewEntry.sdate));

        if(!mTask.equals("0")) {
            DBHelper mydb = new DBHelper(context);
            Cursor prs = mydb.getSelectedMajortask(Integer.parseInt(mTask));
            prs.moveToFirst();
            String mtaskname = prs.getString(prs.getColumnIndex("name"));
            prs.close();

            holder.mtask.setText(mtaskname);
        }
        else{
            holder.mtask.setText("");
        }

        holder.titem.setText(shelter);
       // holder.mtask.setText(mTask);
        holder.dated.setText(sDate);
        holder.itemView.setTag(id);

          /*  mydb = new DBHelper(context);
            long id = data.get(position).getId();
            holder.titem.setText(data.get(position).getShelter());
            holder.mtask.setText(data.get(position).getMtask());
            holder.dated.setText(data.get(position).getDate());
            holder.itemView.setTag(id);*/


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MultiSelector mMultiSelector = new MultiSelector();
                mMultiSelector.setSelectable(true); // enter selection mode
                //Toast.makeText(context, "LongPress" , Toast.LENGTH_LONG).show();
                if (!mMultiSelector.isSelectable()) { // (3)
                    mMultiSelector.setSelectable(true); // (4)
                    //mMultiSelector.setSelected(holder, true); // (5)
                    return true;
                }
                return false;
            }
        });
          holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(context, "ID: "+news.getId(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, ShelterDetails.class);
                    intent.putExtra("id",id);
                    context.startActivity(intent);

                }
            });

           /*  final boolean deleted = false;

            holder.crsbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                    builder.setMessage(R.string.deleteContact)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    new deleteClass().execute(data.get(position).getId());
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
            });*/

    }

    public class deleteClass extends AsyncTask<Integer, Integer, Void> {
        @Override
        protected void onPreExecute() {
            //loading = ProgressDialog.show(SavedSubmissionDetails.this,"Loading Shelter data","Please wait...",false,false);
            loading = new ProgressDialog(context);
            loading.setTitle("Deleting");
            loading.setMessage("Please Wait...");
            loading.setIcon(R.drawable.loader);
            loading.show();
        }


        @Override
        protected Void doInBackground(Integer... position) {

            final int subid = position[0].intValue();
            mydb.deleteSUbmittedForm(subid);

            Log.v("submid",Integer.toString(subid));
            Cursor res1 = mydb.getDataGallery(subid);

            res1.moveToFirst();

            if(res1!=null && res1.getCount() > 0)
            {
                if (res1.moveToFirst())
                {

                    Gid    = res1.getInt(res1.getColumnIndex("id"));
                    Image1 = res1.getString(res1.getColumnIndex("Image1"));
                    Image2 = res1.getString(res1.getColumnIndex("Image2"));
                    Image3 = res1.getString(res1.getColumnIndex("Image3"));
                    Image4 = res1.getString(res1.getColumnIndex("Image4"));
                    Image5 = res1.getString(res1.getColumnIndex("Image5"));
                }
            }

            ///// Image 1 Deleted from folder/////
            String fullPath1 = context.getFilesDir().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD+"/"+Image1;
            File dir1 = new File(fullPath1);
            if (dir1.exists()) {
                if (dir1.delete()) {
                    Log.e("-->", "file Deleted :" + fullPath1);
                } else {
                    Log.e("-->", "file not Deleted :" + fullPath1);
                }
            }
            ///// Image 2 Deleted from folder/////
            String fullPath2 = context.getFilesDir().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD+"/"+Image2;
            File dir2 = new File(fullPath2);
            if (dir2.exists()) {
                if (dir2.delete()) {
                    Log.e("-->", "file Deleted :" + fullPath2);
                } else {
                    Log.e("-->", "file not Deleted :" + fullPath2);
                }
            }
            ///// Image 3 Deleted from folder/////
            String fullPath3 = context.getFilesDir().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD+"/"+Image3;
            File dir3 = new File(fullPath3);
            if (dir3.exists()) {
                if (dir3.delete()) {
                    Log.e("-->", "file Deleted :" + fullPath3);
                } else {
                    Log.e("-->", "file not Deleted :" + fullPath3);
                }
            }
            ///// Image 4 Deleted from folder/////
            String fullPath4 = context.getFilesDir().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD+"/"+Image4;
            File dir4 = new File(fullPath4);
            if (dir4.exists()) {
                if (dir4.delete()) {
                    Log.e("-->", "file Deleted :" + fullPath4);
                } else {
                    Log.e("-->", "file not Deleted :" + fullPath4);
                }
            }
            ///// Image 5 Deleted from folder/////
            String fullPath5 = context.getFilesDir().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD+"/"+Image5;
            File dir5 = new File(fullPath5);
            if (dir5.exists()) {
                if (dir5.delete()) {
                    Log.e("-->", "file Deleted :" + fullPath5);
                } else {
                    Log.e("-->", "file not Deleted :" + fullPath5);
                }
            }
            mydb.deleteGallery(Gid);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            loading.dismiss();
            Intent intent = new Intent(context, SubmittedForm.class);
            context.startActivity(intent);

        }
    }
    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor){
        if(mCursor != null){
            mCursor.close();
        }
        mCursor = newCursor;
        if(newCursor != null){
            notifyDataSetChanged();
        }
    }


    public class CustomVHolder extends RecyclerView.ViewHolder{

        TextView titem,mtask,dated;
        //Button crsbtn;
        LinearLayout linearLayout,udata;
        public CustomVHolder(View itemView) {
            super(itemView);
            titem = (TextView) itemView.findViewById(R.id.titles);
            mtask = (TextView) itemView.findViewById(R.id.mtask);
            dated = (TextView) itemView.findViewById(R.id.datetime);
            //crsbtn = (Button) itemView.findViewById(R.id.crossBtn);
        }
    }
}
