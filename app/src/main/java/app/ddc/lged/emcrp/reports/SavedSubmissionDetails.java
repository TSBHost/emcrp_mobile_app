package app.ddc.lged.emcrp.reports;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import app.ddc.lged.emcrp.NavigationActivity;
import app.ddc.lged.emcrp.R;
import app.ddc.lged.emcrp.common.MultipartUtility;
import app.ddc.lged.emcrp.connectivity.Config;
import app.ddc.lged.emcrp.connectivity.ConnectivityReceiver;
import app.ddc.lged.emcrp.connectivity.DBHelper;
import app.ddc.lged.emcrp.home.GridViewActivity;
import app.ddc.lged.emcrp.home.LoginActivity;

public class SavedSubmissionDetails extends NavigationActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener{

    ProgressDialog progressDialog;

    TextView title,date,lan,lat,mtask,task,comm,c1,c2,c3,c4,c5,latlong;
    ImageView imgv1,imgv2,imgv3,imgv4,imgv5;
    Config conf=new Config();
    // private final String UPLOAD_URL = conf.getURL()+"insert.php";
    private final String UPLOAD_URL = conf.getURL()+"insert.php";
    String ntitle,mtaskv,taskv,commv,latv,ndate,lanv,area;
    int submittedid;
    Bitmap bmp1,bmp2,bmp3,bmp4,bmp5;
    ActionBar actionBar;
    private DBHelper mydb;
    public final static String APP_PATH_SD_CARD = "/EMCRPbd/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "submission_images";
    Context context;
    String Image1,Image2,Image3,Image4,Image5;
    // Button UploadImageToServer;
    String imgfile1,imgfile2,imgfile3,imgfile4,imgfile5,cap1,cap2,cap3,cap4,cap5;
    Cursor rs;
    ProgressDialog loading;
    Dialog dialog;
    Button dialogButton;
    ImageView dImg;
    LinearLayout frame1,frame2,frame3,frame4,frame5;
    String imgname1,imgname2,imgname3,imgname4,imgname5;
    String selectedImgPath1 = null;
    String selectedImgPath2 = null;
    String selectedImgPath3 = null;
    String selectedImgPath4 = null;
    String selectedImgPath5 = null;
    Uri imgUri1,imgUri2,imgUri3,imgUri4,imgUri5;
    SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isLogin() && read_memory() != 0) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.activity_saved_submission_details, null, false);
            drawer.addView(view,0);

            mydb = new DBHelper(this);
            // UploadImageToServer = (Button)  view.findViewById(R.id.sendbtn);

            actionBar = getSupportActionBar();
            actionBar.setTitle("Submission Details");

            frame1 = (LinearLayout) findViewById(R.id.frm1);
            frame2 = (LinearLayout) findViewById(R.id.frm2);
            frame3 = (LinearLayout) findViewById(R.id.frm3);
            frame4 = (LinearLayout) findViewById(R.id.frm4);
            frame5 = (LinearLayout) findViewById(R.id.frm5);

            DisplayData disData = new DisplayData();
            disData.execute();
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


    public String getStringImage(Bitmap bmp){

        if(bmp!=null) {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.WEBP, 100, bao);

            byte[] ba = bao.toByteArray();
            String imgstr = Base64.encodeToString(ba, Base64.DEFAULT);
            return imgstr;
        }
        else {
            return "";
        }

    }

    public void uploadToServer(final String shelter,final String mtask,final String stask,
                               final String comm,final String lat,final String lan,final String area,final String date,
                               final String imgn1,final String imgn2,final String imgn3,final String imgn4,final String imgn5,
                               final String imgf1,final String imgf2,final String imgf3,final String imgf4,final String imgf5,
                               final String capt1,final String capt2,final String capt3,final String capt4,final String capt5){

        Bundle extras = getIntent().getExtras();
        final int submid = extras.getInt("id");

        // loading = ProgressDialog.show(SavedSubmissionDetails.this,"Sending data","Please wait...",false,false);
        progressDialog = new ProgressDialog(SavedSubmissionDetails.this);
        progressDialog.setTitle("Sending data");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onBackPressed();
            }
        });
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        progressDialog.dismiss();
                        try {
                            JSONObject json = new JSONObject(s);
                            int refid = json.getInt("refid");
                            String act = json.getString("action");
                            Log.v("ResponseInsert", "Submission ID: "+String.valueOf(refid));
                            //Toast.makeText(getApplicationContext(), "Successfully sent to server", Toast.LENGTH_LONG).show();
                            customToast("Successfully sent to server");
                            mydb.updateSUbmittedForm(submid,refid);

                            Intent inthome = new Intent(getApplicationContext(), GridViewActivity.class);
                            startActivity(inthome);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        if (error instanceof NetworkError) {
                        } else if (error instanceof ServerError) {
                        } else if (error instanceof AuthFailureError) {
                        } else if (error instanceof ParseError) {
                        } else if (error instanceof NoConnectionError) {
                            customToast("Oops! Disconnected");
                        } else if (error instanceof TimeoutError) {
                            customToast("Successfully sent to server");
                            mydb.updateSUbmittedForm(submid,0);
                            Intent inthome = new Intent(getApplicationContext(), GridViewActivity.class);
                            startActivity(inthome);
                        }
                    }
                }){

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                if (response.headers == null)
                {
                    // cant just set a new empty map because the member is final.
                    response = new NetworkResponse(
                            response.statusCode,
                            response.data,
                            Collections.<String, String>emptyMap(), // this is the important line, set an empty but non-null map.
                            response.notModified,
                            response.networkTimeMs);
                }

                return super.parseNetworkResponse(response);
            }
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                sharedPref = getSharedPreferences("LOGININFO", Context.MODE_PRIVATE);
                int userid = sharedPref.getInt("userid", 0);
                String subtasks = null;
                if(stask!=null){
                    subtasks = stask;
                }
                else{
                    subtasks = "  ";
                }
                Map<String,String> params = new Hashtable<String, String>();
                params.put("userid", Integer.toString(userid));
                params.put("image1", imgf1);
                params.put("image2", imgf2);
                params.put("image3", imgf3);
                params.put("image4", imgf4);
                params.put("image5", imgf5);
                params.put("caption1", capt1);
                params.put("caption2", capt2);
                params.put("caption3", capt3);
                params.put("caption4", capt4);
                params.put("caption5", capt5);

                params.put("imgname1", imgn1);
                params.put("imgname2", imgn2);
                params.put("imgname3", imgn3);
                params.put("imgname4", imgn4);
                params.put("imgname5", imgn5);

                params.put("shelid", shelter);
                params.put("mtask", mtask);
                params.put("stask", subtasks);
                params.put("ucom", comm);
                params.put("latv", lat);
                params.put("longv", lan);
                params.put("area", area);
                params.put("saveddate", date);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }




    private String getPaths(Uri selectedImaeUri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor = managedQuery(selectedImaeUri, projection, null, null,
                null);

        if (cursor != null)
        {
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            return cursor.getString(columnIndex);
        }

        return selectedImaeUri.getPath();
    }


    public class DisplayData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(SavedSubmissionDetails.this);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    onBackPressed();
                }
            });
            progressDialog.show();
        }


        @Override
        protected Void doInBackground(Void...params) {
            mydb = new DBHelper(SavedSubmissionDetails.this);
            Bundle extras = getIntent().getExtras();
            int subid = extras.getInt("id");
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

                //Log.v("ImageCaptions", cap1+" "+cap2+" "+cap3+" "+cap4+" "+cap5);
                res1.close();
                String fullPath = getFilesDir().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;

                String imgpath1 = fullPath + "/" + Image1;
                File f1 = new File(imgpath1);
                imgUri1 = Uri.fromFile(f1);

                BitmapFactory.Options options1 = new BitmapFactory.Options();
                //options1.inSampleSize = 1;
                options1.inDither=false;                     //Disable Dithering mode
                options1.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
                options1.inInputShareable=true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
                options1.inTempStorage=new byte[32 * 1024];
                options1.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bmp1 = BitmapFactory.decodeFile(imgpath1, options1);

                String imgpath2 = fullPath + "/" + Image2;
                File f2 = new File(imgpath2);
                imgUri2 = Uri.fromFile(f2);

                BitmapFactory.Options options2 = new BitmapFactory.Options();
                //options2.inSampleSize = 1;
                options2.inDither=false;                     //Disable Dithering mode
                options2.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
                options2.inInputShareable=true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
                options2.inTempStorage=new byte[32 * 1024];
                options2.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bmp2 = BitmapFactory.decodeFile(imgpath2, options2);

                String imgpath3 = fullPath + "/" + Image3;
                File f3 = new File(imgpath3);
                imgUri3 = Uri.fromFile(f3);

                BitmapFactory.Options options3 = new BitmapFactory.Options();
                // options3.inSampleSize = 1;
                options3.inDither=false;                     //Disable Dithering mode
                options3.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
                options3.inInputShareable=true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
                options3.inTempStorage=new byte[32 * 1024];
                options3.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bmp3 = BitmapFactory.decodeFile(imgpath3, options3);

                String imgpath4 = fullPath + "/" + Image4;
                File f4 = new File(imgpath4);
                imgUri4 = Uri.fromFile(f4);

                BitmapFactory.Options options4 = new BitmapFactory.Options();
                //options4.inSampleSize = 1;
                options4.inDither=false;                     //Disable Dithering mode
                options4.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
                options4.inInputShareable=true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
                options4.inTempStorage=new byte[32 * 1024];
                options4.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bmp4 = BitmapFactory.decodeFile(imgpath4, options4);

                String imgpath5 = fullPath + "/" + Image5;
                File f5 = new File(imgpath5);
                imgUri5 = Uri.fromFile(f5);
                BitmapFactory.Options options5 = new BitmapFactory.Options();
                //options5.inSampleSize = 1;
                options5.inDither=false;                     //Disable Dithering mode
                options5.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
                options5.inInputShareable=true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
                options5.inTempStorage=new byte[32 * 1024];
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
            ///lat = (TextView) findViewById(R.id.lat);
            //lan = (TextView) findViewById(R.id.lan);
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


            if(!mtaskv.equals("0")) {
                Cursor prs = mydb.getSelectedMajortask(Integer.parseInt(mtaskv));
                prs.moveToFirst();
                String mtaskname = prs.getString(prs.getColumnIndex("name"));
                prs.close();
                mtask.setText(mtaskname);
            }
            else{
                mtask.setText("");
            }

            if(!taskv.equals("0")) {
                Cursor prs = mydb.getSelectedSubtask(Integer.parseInt(taskv));
                prs.moveToFirst();
                String staskname = prs.getString(prs.getColumnIndex("name"));
                prs.close();
                task.setText(staskname);
            }
            else{
                task.setText("");
            }


            title.setText(ntitle);
            date.setText(ndate);
            comm.setText(commv);

            if(lanv !=null && !lanv.isEmpty() && latv !=null && !latv.isEmpty()) {
                double lanCon = Double.parseDouble(lanv);
                double latCOn = Double.parseDouble(latv);
                String latLong = new DecimalFormat("##.######").format(latCOn) + "," + new DecimalFormat("##.######").format(lanCon);
                latlong.setText(latLong);
            }
            else{
                latlong.setText("");
            }
            // lan.setText("Long: E " + new DecimalFormat("##.######").format(lanCon));
            //lat.setText("Lat: N " + new DecimalFormat("##.######").format(latCOn));



            if(Image1!=null) {
                frame1.setVisibility(View.VISIBLE);
                imgv1.setVisibility(View.VISIBLE);
                imgv1.setImageBitmap(bmp1);
                selectedImgPath1 = getPaths(imgUri1);
                imgfile1 = getStringImage(bmp1);
                c1.setText(cap1);
                c1.setVisibility(View.VISIBLE);
                imgv1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showBigImage(bmp1);
                    }
                });
                imgname1 = Image1;
            }
            else {
                frame1.setVisibility(View.GONE);
                 imgfile1 = "";
                selectedImgPath1 = null;
                imgv1.setVisibility(View.GONE);
                c1.setVisibility(View.GONE);
                imgname1 = "";
            }

            if(Image2!=null) {
                frame2.setVisibility(View.VISIBLE);
                imgv2.setVisibility(View.VISIBLE);
                imgv2.setImageBitmap(bmp2);
                 imgfile2 = getStringImage(bmp2);
                selectedImgPath2 = getPaths(imgUri2);
                c2.setVisibility(View.VISIBLE);
                c2.setText(cap2);
                imgv2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showBigImage(bmp2);
                    }
                });
                imgname2 = Image2;
            }
            else {
                frame2.setVisibility(View.GONE);
                imgfile2 = "";
                selectedImgPath2 = null;
                imgv2.setVisibility(View.GONE);
                c2.setVisibility(View.GONE);
                imgname2 = "";
            }


            if(Image3!=null) {
                frame3.setVisibility(View.VISIBLE);
                imgv3.setVisibility(View.VISIBLE);
                imgv3.setImageBitmap(bmp3);
                imgfile3 = getStringImage(bmp3);
                selectedImgPath3 = getPaths(imgUri3);

                c3.setText(cap3);
                c3.setVisibility(View.VISIBLE);
                imgv3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showBigImage(bmp3);
                    }
                });
                imgname3 = Image3;
            }
            else {
                frame3.setVisibility(View.GONE);
                imgfile3 = "";
                selectedImgPath3 = null;
                imgv3.setVisibility(View.GONE);
                c3.setVisibility(View.GONE);
                imgname3 = "";
            }


            if(Image4!=null) {
                frame4.setVisibility(View.VISIBLE);
                imgv4.setVisibility(View.VISIBLE);
                imgv4.setImageBitmap(bmp4);
                imgfile4 = getStringImage(bmp4);
                selectedImgPath4 = getPaths(imgUri4);

                c4.setVisibility(View.VISIBLE);
                c4.setText(cap4);
                imgv4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showBigImage(bmp4);
                    }
                });
                imgname4 = Image4;
            }
            else {
                frame4.setVisibility(View.GONE);
                imgfile4 = "";
                selectedImgPath4 = null;
                imgv4.setVisibility(View.GONE);
                c4.setVisibility(View.GONE);
                imgname4 = "";
            }


            if(Image5!=null) {
                frame5.setVisibility(View.VISIBLE);
                imgv4.setVisibility(View.VISIBLE);
                imgv5.setImageBitmap(bmp5);
                imgfile5 = getStringImage(bmp5);
                selectedImgPath5 = getPaths(imgUri5);

                c5.setVisibility(View.VISIBLE);
                c5.setText(cap5);
                imgv5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showBigImage(bmp5);
                    }
                });
                imgname5 = Image5;
            }
            else {
                frame5.setVisibility(View.GONE);
                imgfile5 = "";
                selectedImgPath5 = null;
                c5.setVisibility(View.GONE);
                imgv5.setVisibility(View.GONE);
                imgname5 = "";
            }
            //rs.close();
            //Log.e("checkimagespath",selectedImgPath1+"\n\n"+selectedImgPath2+"\n\n"+selectedImgPath3+"\n\n"+selectedImgPath4+"\n\n"+selectedImgPath5);
            progressDialog.dismiss();
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



    private void uploadQuestion(final String shelter,final String mtask,final String stask,
                                final String comm,final String lat,final String lan,final String area,final String date,
                                final String capt1,final String capt2,final String capt3,final String capt4,final String capt5) {

        /*Toast.makeText(getApplicationContext(), shelter+" IMGS:"+selectedImgPath1 + "" + selectedImgPath2 + "" +
                selectedImgPath3 + "" + selectedImgPath4 + "" + selectedImgPath5, Toast.LENGTH_LONG).show();*/

        class UploadVideo extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;
            Bundle extras = getIntent().getExtras();
            final int submid = extras.getInt("id");

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(SavedSubmissionDetails.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                //customToast(s);
                Log.e("errorchecks",s);
                JSONObject json = null;
                try {
                    json = new JSONObject(s);
                    int refid = json.getInt("refid");
                    String act = json.getString("action");
                    Log.v("ResponseInsert", "Submission ID: "+String.valueOf(refid));
                    customToast("Successfully sent to server");
                    mydb.updateSUbmittedForm(submid,refid);

                    Intent inthome = new Intent(getApplicationContext(), GridViewActivity.class);
                    startActivity(inthome);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(Void... params) {

                final String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mmss").format(new Date());
                //final String devicename = android.os.Build.MANUFACTURER + android.os.Build.MODEL;
                final String devicename = android.os.Build.MANUFACTURER
                        + " " + android.os.Build.MODEL + " " + android.os.Build.VERSION.RELEASE
                        + " " + android.os.Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();

                Random rand = new Random();
                final int tokenid = rand.nextInt(10000);
                final String method="",price="Free",transaction="",account="";

                String charset = "UTF-8";
                //String requestURL = "http://mdspbd.com/healthcare/master_uploaded.php";
                String response = "";
                MultipartUtility multipart = null;
                try {
                    sharedPref = getSharedPreferences("LOGININFO", Context.MODE_PRIVATE);
                    int userid = sharedPref.getInt("userid", 0);
                    String subtasks = null;
                    if(stask!=null){
                        subtasks = stask;
                    }
                    else{
                        subtasks = "  ";
                    }
                    multipart = new MultipartUtility(UPLOAD_URL, charset);

                    multipart.addFormField("userid", Integer.toString(userid));
                    multipart.addFormField("caption1", capt1);
                    multipart.addFormField("caption2", capt2);
                    multipart.addFormField("caption3", capt3);
                    multipart.addFormField("caption4", capt4);
                    multipart.addFormField("caption5", capt5);

                    multipart.addFormField("shelid", shelter);
                    multipart.addFormField("mtask", mtask);
                    multipart.addFormField("stask", subtasks);
                    multipart.addFormField("ucom", comm);
                    multipart.addFormField("latv", lat);
                    multipart.addFormField("longv", lan);
                    multipart.addFormField("area", area);
                    multipart.addFormField("saveddate", date);

                    multipart.addFormField("imgname1", imgname1);
                    multipart.addFormField("imgname2", imgname2);
                    multipart.addFormField("imgname3", imgname3);
                    multipart.addFormField("imgname4", imgname4);
                    multipart.addFormField("imgname5", imgname5);

                    if(selectedImgPath1!=null){
                        multipart.addFilePart("image1", new File(selectedImgPath1));
                    }
                    if(selectedImgPath2!=null){
                        multipart.addFilePart("image2", new File(selectedImgPath2));
                    }
                    if(selectedImgPath3!=null){
                        multipart.addFilePart("image3", new File(selectedImgPath3));
                    }
                    if(selectedImgPath4!=null){
                        multipart.addFilePart("image4", new File(selectedImgPath4));
                    }
                    if(selectedImgPath5!=null){
                        multipart.addFilePart("image5", new File(selectedImgPath5));
                    }
                    response = multipart.finish(); // response from server.
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }
        }


        UploadVideo uv = new UploadVideo();
        uv.execute();
    }


    public int read_memory() {
        sharedPref = this.getSharedPreferences("LOGININFO", Context.MODE_PRIVATE);
        int s = sharedPref.getInt("userid", 0);
        return s;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.subdetails, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Bundle extras = getIntent().getExtras();
        int subid = extras.getInt("id");
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            exit_app();
        }
        if (id == R.id.editsub) {
            editSub(subid);
        }
        if (id == R.id.sendtoserver) {
            checkConnection();
        }
        /*if (id == R.id.syncdata) {
            syncdata();
        }*/

        return super.onOptionsItemSelected(item);
    }

    public void syncdata(){
        Intent intent = new Intent(getApplicationContext(), SyncData.class);
        startActivity(intent);
    }
    public void editSub(int subid){

        Intent intent = new Intent(getApplicationContext(), Packages.class);
        intent.putExtra("uniqueid",ntitle);
        intent.putExtra("action","edit");
        intent.putExtra("subid",subid);
        startActivity(intent);
    }
    public void exit_app() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure ? you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        /*Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        int pid = android.os.Process.myPid();
                        android.os.Process.killProcess(pid);*/

                        finishAffinity();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }



    ////// cehck connectivity//////
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected(this);
        //showSnack(isConnected);
        String message;
        if (isConnected) {
            message = "Connected";
            //Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            //uploadQuestion(ntitle,mtaskv,taskv,commv,latv,lanv,area,ndate,cap1,cap2,cap3,cap4,cap5);
            uploadToServer(ntitle,mtaskv,taskv,commv,latv,lanv,area,ndate,imgname1,imgname2,imgname3,imgname4,imgname5,imgfile1,imgfile2,imgfile3,imgfile4,imgfile5,cap1,cap2,cap3,cap4,cap5);
            //color = Color.WHITE;
        } else {
            message = "No Internet Connection";
            Toast.makeText(SavedSubmissionDetails.this, message, Toast.LENGTH_LONG).show();
            //color = Color.RED;
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        //showSnack(isConnected);
        String message;
        if (isConnected) {
            message = "Connected";
            customToast(message);
        } else {
            message = "No Internet Connection";
            customToast(message);
        }
    }
}
