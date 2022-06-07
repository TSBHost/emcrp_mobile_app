package app.ddc.lged.emcrp.reports;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
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
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import app.ddc.lged.emcrp.NavigationActivity;
import app.ddc.lged.emcrp.R;
import app.ddc.lged.emcrp.connectivity.Config;
import app.ddc.lged.emcrp.connectivity.ConnectivityReceiver;
import app.ddc.lged.emcrp.connectivity.DBHelper;
import app.ddc.lged.emcrp.home.GridViewActivity;
import app.ddc.lged.emcrp.home.LoginActivity;


public class FeedBackInsert extends NavigationActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener{

    TextView title,mtasks,replyt,date,replyt2,date2,replyt3,date3,sbdate;
    Button dialogButton;
    ImageButton TakeP1,imgG1;
    ImageButton bt_voiceinput;
    String shelter,userfedbacks;
    EditText uf;
    Config conf=new Config();
    private final String URLFEED = conf.getURL()+"feedback_insert.php";
    ProgressDialog progressDialog;
    FileOutputStream fileoutputstream;
    SharedPreferences sharedPref;
    private DBHelper mydb;
    Context context;
    private static final String REQUIRED_MSG = "Please write your comment";

    ImageView imgv1;
    public  static final int RequestPermissionCode  = 1 ;
    Bitmap bitmap1;
    int feedid,submissionid;
    Intent intent;
    private static final int REQUEST_CODE = 1234;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2381;
    HashMap<String, String> queryValues;
    ArrayList<FeedbackModel> arrayList = new ArrayList<FeedbackModel>();

    OutputStream fOut;
    String filename1;
    public final static String APP_PATH_SD_CARD = "/EMCRPbd/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "images/feedback";
    boolean feedbackFlag;
    String CaptureTime1,Rand1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isLogin() && read_memory() != 0) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.activity_feed_back_insert, null, false);
            drawer.addView(view,0);

            intent = getIntent();
            feedid = intent.getIntExtra("feedid",0);
            shelter = intent.getStringExtra("shelter");
            userfedbacks = intent.getStringExtra("userfeed");
            submissionid = intent.getIntExtra("submissionid",0);

            //Toast.makeText(getApplicationContext(), "fID "+feedid, Toast.LENGTH_LONG).show();

            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("Reply for "+shelter);

            mydb = new DBHelper(this);
            EnableRuntimePermissionToAccessCamera();

            TakeP1 = (ImageButton) findViewById(R.id.takephoto1);
            imgG1 = (ImageButton) findViewById(R.id.img1gallery1);
            imgv1 = (ImageView) findViewById(R.id.imageView);
            //fdbtn = (Button) findViewById(R.id.feedbackbtn);
            uf = (EditText) findViewById(R.id.userfeedback);

            if(userfedbacks!=null){
                uf.setText(userfedbacks);
            }
            dialogButton = (Button) findViewById(R.id.dialogButtonOK);
            bt_voiceinput = (ImageButton) findViewById(R.id.ib_speak);

            bt_voiceinput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startVoiceRecognitionActivity();
                }
            });
            TakeP1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    captureImage(2);
                }
            });
            imgG1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFileChooser(1);
                }
            });
        }
        else {
            Intent i = new Intent(this.getBaseContext().getApplicationContext(), LoginActivity.class);
            this.startActivity(i);
        }
    }

    @SuppressLint("LongLogTag")
    public String imeiid() {
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice = "", tmSerial, androidId = "";
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        } else {
            tmDevice = "" + tm.getDeviceId();
            Log.v("DeviceIMEI", "" + tmDevice);
            tmSerial = "" + tm.getSimSerialNumber();
            Log.v("GSM devices Serial Number[simcard] ", "" + tmSerial);
            androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            Log.v("androidId CDMA devices", "" + androidId);
            UUID deviceUuid = new UUID(androidId.hashCode(),
                    ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            String deviceId = deviceUuid.toString();
            Log.v("deviceIdUUID universally unique identifier", "" + deviceId);
            String deviceModelName = android.os.Build.MODEL;
            Log.v("Model Name", "" + deviceModelName);
            String deviceUSER = android.os.Build.USER;
            Log.v("Name USER", "" + deviceUSER);
            String devicePRODUCT = android.os.Build.PRODUCT;
            Log.v("PRODUCT", "" + devicePRODUCT);
            String deviceHARDWARE = android.os.Build.HARDWARE;
            Log.v("HARDWARE", "" + deviceHARDWARE);
            String deviceBRAND = android.os.Build.BRAND;
            Log.v("BRAND", "" + deviceBRAND);
            String myVersion = android.os.Build.VERSION.RELEASE;
            Log.v("VERSION.RELEASE", "" + myVersion);
            int sdkVersion = android.os.Build.VERSION.SDK_INT;
            Log.v("VERSION.SDK_INT", "" + sdkVersion);
            //Toast.makeText(getApplicationContext(),deviceId,Toast.LENGTH_LONG).show();
        }
        String uniquekey = tmDevice;
        // Toast.makeText(getApplicationContext(),uniquekey,Toast.LENGTH_LONG).show();
        return uniquekey;
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

    private void showFileChooser(int REQ_CODE) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_CODE);
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }
    private void captureImage(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = null;
        try {
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getApplicationContext().getPackageName() + ".provider", f);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }


        startActivityForResult(takePictureIntent, actionCode);
    }
    private void setCemeraPic1() {
       /*int targetW = imgv1.getWidth();
       int targetH = imgv1.getHeight();*/
        int targetW = imgv1.getWidth();
        int targetH = imgv1.getHeight();
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = 7;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        bitmap1 = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imgv1.setImageBitmap(bitmap1);
        imgv1.setVisibility(View.VISIBLE);
    }


    public void formLocalSubmit(final int rid, final int submid){

        imgv1.buildDrawingCache();
        bitmap1 = imgv1.getDrawingCache();
        String fullPath = getFilesDir().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;

        try {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if(bitmap1 !=null) {

                filename1 = shelter+"_"+CaptureTime1+imeiid()+Rand1+"_feedback.webp";
                File file1 = new File(fullPath, filename1);
                file1.createNewFile();
                fOut = new FileOutputStream(file1);
                bitmap1.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
                MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), file1.getAbsolutePath(), file1.getName(), file1.getName());
            }
            else{
                filename1 = "";
            }
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            progressDialog = new ProgressDialog(FeedBackInsert.this);
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

            final String userfeedback = uf.getText().toString();
            queryValues = new HashMap<String, String>();
            queryValues.put("subid", Integer.toString(submid));
            queryValues.put("userfeed", userfeedback);
            queryValues.put("userstatus", "10");
            queryValues.put("userfeeddate", date);
            queryValues.put("userimg", filename1);

            Toast.makeText(getApplicationContext(), "fID "+rid, Toast.LENGTH_LONG).show();
            feedbackFlag = mydb.updateUserFeedback(queryValues, rid);
            if(feedbackFlag) {
                Toast.makeText(getApplicationContext(), "Successfully Saved Data", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Failed to Save Data", Toast.LENGTH_LONG).show();
            }

            progressDialog.dismiss();
            Intent inthome = new Intent(getApplicationContext(), FeedbackList.class);
            startActivity(inthome);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void formOnlineSubmit(final int rid, final int submid){
        //Toast.makeText(getApplicationContext(),"Test" + rid + " " + submid, Toast.LENGTH_LONG).show();
        imgv1.buildDrawingCache();
        bitmap1 = imgv1.getDrawingCache();

            if(bitmap1 !=null) {
                //filename1 = shelter+"_"+CaptureTime1+"_feedback.webp";
                filename1 = shelter+"_"+CaptureTime1+imeiid()+Rand1+"_feedback.webp";
            }
            else{
                filename1 = "";
            }
            final String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            final String imgpath1 = getStringImage(bitmap1);

            final String userfeedback = uf.getText().toString();
            // loading = ProgressDialog.show(SavedSubmissionDetails.this,"Sending data","Please wait...",false,false);
            progressDialog = new ProgressDialog(FeedBackInsert.this);
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

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URLFEED,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                //try {
                                    //JSONObject json = new JSONObject(s);
                                    //Toast.makeText(getApplicationContext(), "Successfully sent to server", Toast.LENGTH_LONG).show();

                                    mydb.deleteFeedbackBySubmission(submid);
                                    //progressDialog.dismiss();
                                    customToast("Successfully sent to server");
                                    Intent inthome = new Intent(getApplicationContext(), GridViewActivity.class);
                                    startActivity(inthome);

                                /*} catch (JSONException e) {
                                    e.printStackTrace();
                                }*/
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                // UploadImageToServer.setBackgroundColor(getResources().getColor(R.color.colorAsh));
                                //UploadImageToServer.setEnabled(false);
                                if (error instanceof NetworkError) {
                                } else if (error instanceof ServerError) {
                                } else if (error instanceof AuthFailureError) {
                                } else if (error instanceof ParseError) {
                                } else if (error instanceof NoConnectionError) {
                                    customToast("No Internet Connection");
                                } else if (error instanceof TimeoutError) {
                                    //progressDialog.dismiss();
                                    mydb.deleteFeedbackBySubmission(submid);
                                    customToast("Successfully sent to server");
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
                        Map<String,String> params = new Hashtable<String, String>();
                        params.put("feedback", userfeedback);
                        params.put("feeddate", date);
                        params.put("id", Integer.toString(rid));
                        params.put("userid", Integer.toString(read_memory()));
                        params.put("submid", Integer.toString(submid));
                        params.put("image1", imgpath1);
                        params.put("imagename", filename1);
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);

            //Intent intent = new Intent(getApplicationContext(), GridViewActivity.class);
            //startActivity(intent);

    }




    public void EnableRuntimePermissionToAccessCamera(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA))
        {

            // Printing toast message after enabling runtime permission.
            //Toast.makeText(getApplicationContext(),"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();
            customToast("CAMERA permission allows us to Access CAMERA app");

        } else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);
        }
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


    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {
        switch (RC) {
            case RequestPermissionCode:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(getActivity(),"Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(getActivity(),"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak the word");
        startActivityForResult(intent, REQUEST_CODE);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Random random = new Random();
        Date c = Calendar.getInstance().getTime();

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            /*String realPath = getRealPathFromURI(uri);

            File selectedFile = new File(realPath);
            Date date = new Date(selectedFile.lastModified());*/
            CaptureTime1 = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS").format(c);
            Rand1 = String.format("%04d", random.nextInt(10000));
            //cap1.setText(CaptureTime1);

            Glide.with(imgv1.getContext()).load(data.getData().toString()).asBitmap().into(imgv1);
        }


        if (requestCode == 2 && resultCode == RESULT_OK) {
            CaptureTime1 = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            // cap1.setText(CaptureTime1);
            setCemeraPic1();
        }



/*
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            Glide.with(imgv1.getContext()).load(data.getData().toString()).asBitmap().into(imgv1);
        }

        if (requestCode == 2 && resultCode == RESULT_OK) {
            setCemeraPic1();
        }
*/
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (!textMatchList.isEmpty()) {
                    String Query = textMatchList.get(0);
                    // uf.setText(Query);
                    if(uf.getText().length() > 0)
                    {
                        uf.setText( uf.getText().toString() + Query);
                    }
                    else
                    {
                        uf.setText(Query);
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
        //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        customToast(msg);
    }
    private void checkConnection(int subid, int submid) {
        boolean isConnected = ConnectivityReceiver.isConnected(this);
        String message;
        if (isConnected) {
            formOnlineSubmit(subid,submid);
        }
        else {
            //formLocalSubmit(subid,submid);
            message= "No Internet Connection";
            //Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
            customToast(message);
        }
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        //showSnack(isConnected);
        String message;
        if (isConnected) {
            message= "Connected. Please try again";
           // Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
        } else {
            message= "No Internet Connection";
           // Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
        }
        customToast(message);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feedbackmenu, menu);
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

        if (id == R.id.home) {
            homeActivity();
        }

        if (id == R.id.saved) {
            if(hasText(uf)){
                formLocalSubmit(feedid,submissionid);
            }
        }

        if (id == R.id.sendtoserver) {
            if(hasText(uf)){
                checkConnection(feedid,submissionid);
            }
        }
        /*if (id == R.id.syncdata) {
            syncdata();
        }*/

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        Intent intentfeed = new Intent(getApplicationContext(), FeedbackList.class);
        startActivity(intentfeed);
    }

}
