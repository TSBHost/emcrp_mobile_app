package app.ddc.lged.emcrp.reports;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import androidx.appcompat.app.ActionBar;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;

import app.ddc.lged.emcrp.NavigationActivity;
import app.ddc.lged.emcrp.R;
import app.ddc.lged.emcrp.connectivity.DBHelper;

public class ShelterDetails extends NavigationActivity {

    ProgressDialog loading;

    TextView title,date,latlong,mtask,task,comm,c1,c2,c3,c4,c5;
    ImageView imgv1,imgv2,imgv3,imgv4,imgv5;
    String ntitle,mtaskv,taskv,commv,latv,ndate,lanv,area,
            Image1,Image2,Image3,Image4,Image5,imgfile1,imgfile2,imgfile3,imgfile4,imgfile5,cap1,cap2,cap3,cap4,cap5;
    int submittedid;
    Bitmap bmp1,bmp2,bmp3,bmp4,bmp5;
    ActionBar actionBar;
    private DBHelper mydb;
    public final static String APP_PATH_SD_CARD = "/EMCRPbd/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "submission_images";
    Context context;
    Cursor rs;
    Dialog dialog;
    Button dialogButton;
    ImageView dImg;
    LinearLayout frame1,frame2,frame3,frame4,frame5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_shelter_details);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_shelter_details, null, false);
        drawer.addView(view,0);
        mydb = new DBHelper(this);
        actionBar = getSupportActionBar();
        actionBar.setTitle("MDSPBD : Submitted Forms");

        UploadDataSoSerter uploadServer = new UploadDataSoSerter();
        uploadServer.execute();
        frame1 = (LinearLayout) findViewById(R.id.frm1);
        frame2 = (LinearLayout) findViewById(R.id.frm2);
        frame3 = (LinearLayout) findViewById(R.id.frm3);
        frame4 = (LinearLayout) findViewById(R.id.frm4);
        frame5 = (LinearLayout) findViewById(R.id.frm5);

    }


    public String getStringImage(Bitmap bmp){

        if(bmp!=null) {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, bao);

            byte[] ba = bao.toByteArray();
            String imgstr = Base64.encodeToString(ba, Base64.DEFAULT);
            return imgstr;
        }
        else {
            return "";
        }

    }


    public class UploadDataSoSerter extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            //loading = ProgressDialog.show(SavedSubmissionDetails.this,"Loading Shelter data","Please wait...",false,false);
            loading = new ProgressDialog(ShelterDetails.this);
            loading.setTitle("Loading Shelter data");
            loading.setMessage("Please Wait...");
            loading.setIcon(R.drawable.loader);
            loading.show();
        }


        @Override
        protected Void doInBackground(Void...params) {
            mydb = new DBHelper(ShelterDetails.this);
            Bundle extras = getIntent().getExtras();
            int subid = extras.getInt("id");
            //Log.v("subid", Integer.toString(subid));
            try {
                //means this is the view part not the add contact part.
                rs = mydb.getDataSUbmittedForm(subid);
                rs.moveToFirst();
                submittedid = subid;
                ntitle = rs.getString(rs.getColumnIndex("ShelterCode"));
                mtaskv = rs.getString(rs.getColumnIndex("MajorTasks"));
                taskv = rs.getString(rs.getColumnIndex("Tasks"));
                commv = rs.getString(rs.getColumnIndex("Comments"));
                latv = rs.getString(rs.getColumnIndex("Latitude"));
                lanv = rs.getString(rs.getColumnIndex("Longitude"));
                area = rs.getString(rs.getColumnIndex("Area"));
                ndate = rs.getString(rs.getColumnIndex("SubmisionDate"));

                rs.close();

                Cursor res1 = mydb.getDataGallery(subid);
                res1.moveToFirst();

                if(res1!=null && res1.getCount() > 0)
                {
                    if (res1.moveToFirst())
                    {
                        cap1 = res1.getString(res1.getColumnIndex("Caption1"));
                        Image1 = res1.getString(res1.getColumnIndex("Image1"));
                        cap2 = res1.getString(res1.getColumnIndex("Caption2"));
                        Image2 = res1.getString(res1.getColumnIndex("Image2"));
                        cap3 = res1.getString(res1.getColumnIndex("Caption3"));
                        Image3 = res1.getString(res1.getColumnIndex("Image3"));
                        cap4 = res1.getString(res1.getColumnIndex("Caption4"));
                        Image4 = res1.getString(res1.getColumnIndex("Image4"));
                        cap5 = res1.getString(res1.getColumnIndex("Caption5"));
                        Image5 = res1.getString(res1.getColumnIndex("Image5"));
                    }
                }
                res1.close();
                String fullPath = getFilesDir().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;

                String imgpath1 = fullPath + "/" + Image1;
                BitmapFactory.Options options1 = new BitmapFactory.Options();
                options1.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bmp1 = BitmapFactory.decodeFile(imgpath1, options1);

                String imgpath2 = fullPath + "/" + Image2;
                BitmapFactory.Options options2 = new BitmapFactory.Options();
                options2.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bmp2 = BitmapFactory.decodeFile(imgpath2, options2);

                String imgpath3 = fullPath + "/" + Image3;
                BitmapFactory.Options options3 = new BitmapFactory.Options();
                options3.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bmp3 = BitmapFactory.decodeFile(imgpath3, options3);

                String imgpath4 = fullPath + "/" + Image4;
                BitmapFactory.Options options4 = new BitmapFactory.Options();
                options4.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bmp4 = BitmapFactory.decodeFile(imgpath4, options4);

                String imgpath5 = fullPath + "/" + Image5;
                BitmapFactory.Options options5 = new BitmapFactory.Options();
                options5.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bmp5 = BitmapFactory.decodeFile(imgpath5, options5);
            }
            catch (Exception e){
                Log.v("CatchVal", e.toString());
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            title = (TextView) findViewById(R.id.titles);
            mtask = (TextView) findViewById(R.id.mtask);
            task = (TextView) findViewById(R.id.task);
            comm = (TextView) findViewById(R.id.comments);
            date = (TextView) findViewById(R.id.datetime);
            latlong = (TextView) findViewById(R.id.latlong);
            imgv1 = (ImageView) findViewById(R.id.img1);
            imgv2 = (ImageView) findViewById(R.id.img2);
            imgv3 = (ImageView) findViewById(R.id.img3);
            imgv4 = (ImageView) findViewById(R.id.img4);
            imgv5 = (ImageView) findViewById(R.id.img5);

            c1 = (TextView) findViewById(R.id.caption1);
            c2 = (TextView) findViewById(R.id.caption2);
            c3 = (TextView) findViewById(R.id.caption3);
            c4 = (TextView) findViewById(R.id.caption4);
            c5 = (TextView) findViewById(R.id.caption5);

            title.setText("Shelter Code: " + ntitle);
            date.setText(ndate);
            comm.setText("Comments: " + commv);


            if(!mtaskv.equals("0") || !mtaskv.equals("")) {
                Cursor prs = mydb.getSelectedMajortask(Integer.parseInt(mtaskv));
                prs.moveToFirst();
                String mtaskname = prs.getString(prs.getColumnIndex("name"));
                prs.close();
                mtask.setText(mtaskname);
            }
            else{
                mtask.setText("");
            }

            /*if(!taskv.equals("0") || !taskv.equals("") || taskv!=null) {
                Cursor prs = mydb.getSelectedSubtask(Integer.parseInt(taskv));
                prs.moveToFirst();
                String staskname = prs.getString(prs.getColumnIndex("name"));
                prs.close();
                task.setText(staskname);
            }
            else{
                task.setText("");
            }*/


            task.setText(taskv);
           // mtask.setText("Major Task: \t" + mtaskv);



            if(lanv !=null && !lanv.isEmpty() && latv !=null && !latv.isEmpty()) {
                double lanCon = Double.parseDouble(lanv);
                double latCOn = Double.parseDouble(latv);
                String latLong = new DecimalFormat("##.######").format(latCOn) + "," + new DecimalFormat("##.######").format(lanCon);
                latlong.setText(latLong);
            }
            else{
                latlong.setText("");
            }


            if(Image1!=null) {
                frame1.setVisibility(View.VISIBLE);
                imgv1.setVisibility(View.VISIBLE);
                imgv1.setImageBitmap(bmp1);
                imgfile1 = getStringImage(bmp1);
                c1.setText(cap1);
                c1.setVisibility(View.VISIBLE);
                imgv1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showBigImage(bmp1);
                    }
                });
            }
            else {
                frame1.setVisibility(View.GONE);
                imgfile1 = "";
                imgv1.setVisibility(View.GONE);
                c1.setVisibility(View.GONE);
            }

            if(Image2!=null) {
                frame2.setVisibility(View.VISIBLE);
                imgv2.setVisibility(View.VISIBLE);
                imgv2.setImageBitmap(bmp2);
                imgfile2 = getStringImage(bmp2);
                c2.setVisibility(View.VISIBLE);
                c2.setText(cap2);
                imgv2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showBigImage(bmp2);
                    }
                });
            }
            else {
                frame2.setVisibility(View.GONE);
                imgfile2 = "";
                imgv2.setVisibility(View.GONE);
                c2.setVisibility(View.GONE);
            }


            if(Image3!=null) {
                frame3.setVisibility(View.VISIBLE);
                imgv3.setVisibility(View.VISIBLE);
                imgv3.setImageBitmap(bmp3);
                imgfile3 = getStringImage(bmp3);
                c3.setText(cap3);
                c3.setVisibility(View.VISIBLE);
                imgv3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showBigImage(bmp3);
                    }
                });
            }
            else {
                frame3.setVisibility(View.GONE);
                imgfile3 = "";
                imgv3.setVisibility(View.GONE);
                c3.setVisibility(View.GONE);
            }


            if(Image4!=null) {
                frame4.setVisibility(View.VISIBLE);
                imgv4.setVisibility(View.VISIBLE);
                imgv4.setImageBitmap(bmp4);
                imgfile4 = getStringImage(bmp4);
                c4.setVisibility(View.VISIBLE);
                c4.setText(cap4);
                imgv4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showBigImage(bmp4);
                    }
                });
            }
            else {
                frame4.setVisibility(View.GONE);
                imgfile4 = "";
                imgv4.setVisibility(View.GONE);
                c4.setVisibility(View.GONE);
            }

            if(Image5!=null) {
                frame5.setVisibility(View.VISIBLE);
                imgv4.setVisibility(View.VISIBLE);
                imgv5.setImageBitmap(bmp5);
                imgfile5 = getStringImage(bmp5);
                c5.setVisibility(View.VISIBLE);
                c5.setText(cap5);
                imgv5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showBigImage(bmp5);
                    }
                });
            }
            else {
                frame5.setVisibility(View.GONE);
                imgfile5 = "";
                c5.setVisibility(View.GONE);
                imgv5.setVisibility(View.GONE);
            }

            loading.dismiss();
        }

    }
    public void showBigImage(Bitmap bmp){
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();

        int DeviceTotalWidth = metrics.widthPixels;
        int DeviceTotalHeight = metrics.heightPixels;

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.photo_dialog);
        dialog.getWindow().setLayout(DeviceTotalWidth ,DeviceTotalHeight);
        dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        dImg = (ImageView) dialog.findViewById(R.id.galleryImg);
        dImg.setImageBitmap(bmp);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
}
