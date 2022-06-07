package app.ddc.lged.emcrp.reports;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import app.ddc.lged.emcrp.NavigationActivity;
import app.ddc.lged.emcrp.R;
import app.ddc.lged.emcrp.connectivity.Config;
import app.ddc.lged.emcrp.connectivity.DBHelper;
import app.ddc.lged.emcrp.home.GridViewActivity;
import app.ddc.lged.emcrp.home.LoginActivity;

public class PicturesActivity extends NavigationActivity implements View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    View v;
    Button nextbtn, prevbtn;
    Context context;
    Bitmap bitmap1, bitmap2, bitmap3, bitmap4, bitmap5;
    ImageButton TakeP1, TakeP2, TakeP3, TakeP4, TakeP5, imgG1, imgG2, imgG3, imgG4, imgG5;
    TextView cap1, cap2, cap3, cap4, cap5;
    Button UploadImageToServer;
    ImageView imgv1, imgv2, imgv3, imgv4, imgv5;
    public static final int RequestPermissionCode = 1;
    ToggleButton plusbtn, pbtn1, pbtn2, pbtn3, pbtn4;
    LinearLayout img1area, img2area, img3area, img4area, img5area;
    Config conf = new Config();
    SharedPreferences sharedPref;
    String str;
    private DBHelper mydb;
    HashMap<String, String> submissionValues;
    HashMap<String, String> galleryValues;
    File file;
    OutputStream fOut, fOut1, fOut2, fOut3, fOut4, fOut5;
    FileOutputStream fileoutputstream;
    String filename1, filename2, filename3, filename4, filename5;
    public final static String APP_PATH_SD_CARD = "/EMCRPbd/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "submission_images";
    ProgressDialog loading;
    Cursor rs;
    String capt1, capt2, capt3, capt4, capt5, imge1, imge2, imge3, imge4, imge5;

    String CaptureTime1, CaptureTime2, CaptureTime3, CaptureTime4, CaptureTime5,Rand1,Rand2,Rand3,Rand4,Rand5;
    String selectedImgPath = null;
    boolean imgflag1 = false;
    boolean imgflag2 = false;
    boolean imgflag3 = false;
    boolean imgflag4 = false;
    boolean imgflag5 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Intent intv1 = getIntent();
        final String statusval1 = intv1.getStringExtra("action");
        final int packageid = intv1.getIntExtra("packageid",0);
        final int subpackageid = intv1.getIntExtra("subpackageid",0);
        final int mtaskid = intv1.getIntExtra("mtaskid",0);
        final int staskid = intv1.getIntExtra("staskid",0);
        final String ucom = intv1.getStringExtra("ucomment");
        final String sheltercode = intv1.getStringExtra("sheltercode");

        Toast.makeText(getApplicationContext(), "IDs"+packageid+", SIDs"+subpackageid+", Shelter"+sheltercode+", mTask"+mtaskid+", sTask"+staskid+", comment"+ucom+", action"+statusval1, Toast.LENGTH_LONG).show();*/


        if (isLogin() && read_memory() != 0) {

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.activity_form_step10, null, false);
            drawer.addView(v, 0);

            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("Upload Photos");

            img1area = (LinearLayout) findViewById(R.id.image1area);
            img2area = (LinearLayout) findViewById(R.id.image2area);
            img3area = (LinearLayout) findViewById(R.id.image3area);
            img4area = (LinearLayout) findViewById(R.id.image4area);
            img5area = (LinearLayout) findViewById(R.id.image5area);

            plusbtn = (ToggleButton) findViewById(R.id.addimg);
            pbtn1 = (ToggleButton) findViewById(R.id.addimg1);
            pbtn2 = (ToggleButton) findViewById(R.id.addimg2);
            pbtn3 = (ToggleButton) findViewById(R.id.addimg3);
            pbtn4 = (ToggleButton) findViewById(R.id.addimg4);

            cap1 = (TextView) findViewById(R.id.caption1);
            cap2 = (TextView) findViewById(R.id.caption2);
            cap3 = (TextView) findViewById(R.id.caption3);
            cap4 = (TextView) findViewById(R.id.caption4);
            cap5 = (TextView) findViewById(R.id.caption5);

            TakeP1 = (ImageButton) findViewById(R.id.takephoto1);
            TakeP2 = (ImageButton) findViewById(R.id.takephoto2);
            TakeP3 = (ImageButton) findViewById(R.id.takephoto3);
            TakeP4 = (ImageButton) findViewById(R.id.takephoto4);
            TakeP5 = (ImageButton) findViewById(R.id.takephoto5);
            imgG1 = (ImageButton) findViewById(R.id.img1gallery1);
            imgG2 = (ImageButton) findViewById(R.id.img1gallery2);
            imgG3 = (ImageButton) findViewById(R.id.img1gallery3);
            imgG4 = (ImageButton) findViewById(R.id.img1gallery4);
            imgG5 = (ImageButton) findViewById(R.id.img1gallery5);

            TakeP1.setOnClickListener(this);
            TakeP2.setOnClickListener(this);
            TakeP3.setOnClickListener(this);
            TakeP4.setOnClickListener(this);
            TakeP5.setOnClickListener(this);
            imgG1.setOnClickListener(this);
            imgG2.setOnClickListener(this);
            imgG3.setOnClickListener(this);
            imgG4.setOnClickListener(this);
            imgG5.setOnClickListener(this);
            plusbtn.setOnClickListener(this);
            pbtn1.setOnClickListener(this);
            pbtn2.setOnClickListener(this);
            pbtn3.setOnClickListener(this);
            pbtn4.setOnClickListener(this);

            imgv1 = (ImageView) findViewById(R.id.imageView1);
            imgv2 = (ImageView) findViewById(R.id.imageView2);
            imgv3 = (ImageView) findViewById(R.id.imageView3);
            imgv4 = (ImageView) findViewById(R.id.imageView4);
            imgv5 = (ImageView) findViewById(R.id.imageView5);
            //UploadImageToServer = (Button)    findViewById(R.id.sendbtn);

            EnableRuntimePermissionToAccessCamera();
            Intent intv = getIntent();
            final String statusval = intv.getStringExtra("action");
            if (statusval.equals("edit")) {
                UploadDataSoSerter uploadServer = new UploadDataSoSerter();
                uploadServer.execute();
                img1area.setVisibility(View.VISIBLE);
                img2area.setVisibility(View.VISIBLE);
                img3area.setVisibility(View.VISIBLE);
                img4area.setVisibility(View.VISIBLE);
                img5area.setVisibility(View.VISIBLE);
            }

            if (imgv1.getDrawable() != null) {
                pbtn1.setVisibility(View.VISIBLE);
            } else {
                pbtn1.setVisibility(View.GONE);
            }

            //Toast.makeText(getApplicationContext(),imeiid(),Toast.LENGTH_LONG).show();
        } else {
            Intent i = new Intent(this.getBaseContext().getApplicationContext(), LoginActivity.class);
            this.startActivity(i);
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.takephoto1:
                captureImage(11);
                break;
            case R.id.img1gallery1:
                showFileChooser(1);
                break;
            case R.id.takephoto2:
                captureImage(12);
                break;
            case R.id.img1gallery2:
                showFileChooser(2);
                break;
            case R.id.takephoto3:
                captureImage(13);
                break;
            case R.id.img1gallery3:
                showFileChooser(3);
                break;
            case R.id.takephoto4:
                captureImage(14);
                break;
            case R.id.img1gallery4:
                showFileChooser(4);
                break;
            case R.id.takephoto5:
                captureImage(15);
                break;
            case R.id.img1gallery5:
                showFileChooser(5);
                break;
            case R.id.addimg:
                showHideFunc(1);
                break;
            case R.id.addimg1:
                showHideFunc(2);
                break;
            case R.id.addimg2:
                showHideFunc(3);
                break;
            case R.id.addimg3:
                showHideFunc(4);
                break;
            case R.id.addimg4:
                showHideFunc(5);
                break;

        }
    }

    private void showHideFunc(int order) {
        if (order == 1) {
            if (img1area.getVisibility() == View.GONE) {
                img1area.setVisibility(View.VISIBLE);
                Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                img1area.startAnimation(animSlideDown);
            } else {
                Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                img1area.startAnimation(animSlideDown);
                img1area.setVisibility(View.GONE);
            }
        } else if (order == 2) {
            if (img2area.getVisibility() == View.GONE) {
                img2area.setVisibility(View.VISIBLE);
                Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                img2area.startAnimation(animSlideDown);
            } else {
                Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                img2area.startAnimation(animSlideDown);
                img2area.setVisibility(View.GONE);
            }
        } else if (order == 3) {
            if (img3area.getVisibility() == View.GONE) {
                img3area.setVisibility(View.VISIBLE);
                Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                img3area.startAnimation(animSlideDown);
            } else {
                Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                img3area.startAnimation(animSlideDown);
                img3area.setVisibility(View.GONE);
            }
        } else if (order == 4) {
            if (img4area.getVisibility() == View.GONE) {
                img4area.setVisibility(View.VISIBLE);
                Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                img4area.startAnimation(animSlideDown);
            } else {
                Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                img4area.startAnimation(animSlideDown);
                img4area.setVisibility(View.GONE);
            }
        } else if (order == 5) {
            if (img5area.getVisibility() == View.GONE) {
                img5area.setVisibility(View.VISIBLE);
                Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                img5area.startAnimation(animSlideDown);
            } else {
                Animation animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                img5area.startAnimation(animSlideDown);
                img5area.setVisibility(View.GONE);
            }
        }
    }

    ////// get all images for edit submission
    public class UploadDataSoSerter extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            //loading = ProgressDialog.show(SavedSubmissionDetails.this,"Loading Shelter data","Please wait...",false,false);
            loading = new ProgressDialog(PicturesActivity.this);
            loading.setTitle("Data is Loading");
            loading.setMessage("Please Wait...");
            loading.setIcon(R.drawable.loader);
            loading.show();
        }


        @Override
        protected Void doInBackground(Void... params) {
            mydb = new DBHelper(PicturesActivity.this);
            sharedPref = getSharedPreferences("EditValues", Context.MODE_PRIVATE);
            final int subid = sharedPref.getInt("subid", 0);

            Bundle extras = getIntent().getExtras();
            //final int subid = extras.getInt("subid");
            // final int sid = intv.getIntExtra("subid",0);
            try {
                //means this is the view part not the add contact part.
                rs = mydb.getDataSUbmittedForm(subid);
                rs.moveToFirst();
                rs.close();

                Cursor res1 = mydb.getDataGallery(subid);
                res1.moveToFirst();

                if (res1 != null && res1.getCount() > 0) {
                    if (res1.moveToFirst()) {
                        capt1 = res1.getString(res1.getColumnIndex("Caption1"));
                        imge1 = res1.getString(res1.getColumnIndex("Image1"));
                        capt2 = res1.getString(res1.getColumnIndex("Caption2"));
                        imge2 = res1.getString(res1.getColumnIndex("Image2"));
                        capt3 = res1.getString(res1.getColumnIndex("Caption3"));
                        imge3 = res1.getString(res1.getColumnIndex("Image3"));
                        capt4 = res1.getString(res1.getColumnIndex("Caption4"));
                        imge4 = res1.getString(res1.getColumnIndex("Image4"));
                        capt5 = res1.getString(res1.getColumnIndex("Caption5"));
                        imge5 = res1.getString(res1.getColumnIndex("Image5"));
                    }
                }

                //Log.v("ImageCaptions", cap1+" "+cap2+" "+cap3+" "+cap4+" "+cap5);
                res1.close();
                String fullPath = getFilesDir().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;

                if (imge1 != null) {
                    String imgpath1 = fullPath + "/" + imge1;
                    BitmapFactory.Options options1 = new BitmapFactory.Options();
                    options1.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bitmap1 = BitmapFactory.decodeFile(imgpath1, options1);
                }
                if (imge2 != null) {
                    String imgpath2 = fullPath + "/" + imge2;
                    BitmapFactory.Options options2 = new BitmapFactory.Options();
                    options2.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bitmap2 = BitmapFactory.decodeFile(imgpath2, options2);
                }
                if (imge3 != null) {
                    String imgpath3 = fullPath + "/" + imge3;
                    BitmapFactory.Options options3 = new BitmapFactory.Options();
                    options3.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bitmap3 = BitmapFactory.decodeFile(imgpath3, options3);
                }
                if (imge4 != null) {
                    String imgpath4 = fullPath + "/" + imge4;
                    BitmapFactory.Options options4 = new BitmapFactory.Options();
                    options4.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bitmap4 = BitmapFactory.decodeFile(imgpath4, options4);
                }
                if (imge5 != null) {
                    String imgpath5 = fullPath + "/" + imge5;
                    BitmapFactory.Options options5 = new BitmapFactory.Options();
                    options5.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bitmap5 = BitmapFactory.decodeFile(imgpath5, options5);
                }
            } catch (Exception e) {
                Log.v("CatchVal", e.toString());
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            Log.v("ImagesExisting", imge1 + " " + imge2 + " " + imge3 + " " + imge4 + " " + imge5);
            if (bitmap1 != null) {
                imgv1.setImageBitmap(bitmap1);
                cap1.setText(capt1);
            }
            if (bitmap2 != null) {
                imgv2.setImageBitmap(bitmap2);
                cap2.setText(capt2);
            }
            if (bitmap3 != null) {
                imgv3.setImageBitmap(bitmap3);
                cap3.setText(capt3);
            }
            if (bitmap4 != null) {
                imgv4.setImageBitmap(bitmap4);
                cap4.setText(capt4);

            }
            if (bitmap5 != null) {
                imgv5.setImageBitmap(bitmap5);
                cap5.setText(capt5);
            }
            //rs.close();
            loading.dismiss();
        }
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
//        int targetW = imgv1.getWidth();
//        int targetH = imgv1.getHeight();
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//        int scaleFactor = 5;
//        if ((targetW > 0) || (targetH > 0)) {
//            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
//        }
//        bmOptions.inJustDecodeBounds = false;
////        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
        try {
            bitmap1=handleSamplingAndRotationBitmap(getApplicationContext(),Uri.fromFile(new File(mCurrentPhotoPath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        bitmap1 = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imgv1.setImageBitmap(bitmap1);
        imgv1.setVisibility(View.VISIBLE);
    }

    private void setCemeraPic2() {
//        int targetW = imgv2.getWidth();
//        int targetH = imgv2.getHeight();
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//        int scaleFactor = 7;
//        if ((targetW > 0) || (targetH > 0)) {
//            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
//        }
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//        bitmap2 = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        try {
            bitmap2=handleSamplingAndRotationBitmap(getApplicationContext(),Uri.fromFile(new File(mCurrentPhotoPath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        imgv2.setImageBitmap(bitmap2);
        imgv2.setVisibility(View.VISIBLE);
    }

    private void setCemeraPic3() {
//        int targetW = imgv3.getWidth();
//        int targetH = imgv3.getHeight();
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//        int scaleFactor = 7;
//        if ((targetW > 0) || (targetH > 0)) {
//            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
//        }
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//        bitmap3 = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        try {
            bitmap3=handleSamplingAndRotationBitmap(getApplicationContext(),Uri.fromFile(new File(mCurrentPhotoPath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        imgv3.setImageBitmap(bitmap3);
        imgv3.setVisibility(View.VISIBLE);
    }

    private void setCemeraPic4() {
//        int targetW = imgv4.getWidth();
//        int targetH = imgv4.getHeight();
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//        int scaleFactor = 7;
//        if ((targetW > 0) || (targetH > 0)) {
//            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
//        }
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//        bitmap4 = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        try {
            bitmap4=handleSamplingAndRotationBitmap(getApplicationContext(),Uri.fromFile(new File(mCurrentPhotoPath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        imgv4.setImageBitmap(bitmap4);
        imgv4.setVisibility(View.VISIBLE);
    }

    private void setCemeraPic5() {
//        int targetW = imgv5.getWidth();
//        int targetH = imgv5.getHeight();
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//        int scaleFactor = 7;
//        if ((targetW > 0) || (targetH > 0)) {
//            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
//        }
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//        bitmap5 = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        try {
            bitmap5=handleSamplingAndRotationBitmap(getApplicationContext(),Uri.fromFile(new File(mCurrentPhotoPath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        imgv5.setImageBitmap(bitmap5);
        imgv5.setVisibility(View.VISIBLE);
    }



    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 768;
        int MAX_WIDTH = 768;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(img, selectedImage);
        return img;
    }
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }
    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {
try {
    ExifInterface ei = new ExifInterface(selectedImage.getPath());
    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

    switch (orientation) {
        case ExifInterface.ORIENTATION_ROTATE_90:
            return rotateImage(img, 90);
        case ExifInterface.ORIENTATION_ROTATE_180:
            return rotateImage(img, 180);
        case ExifInterface.ORIENTATION_ROTATE_270:
            return rotateImage(img, 270);
        default:
            return img;
    }
}
catch (Exception e)
{
    e.printStackTrace();
}

        return img;
    }
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }









    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Random random = new Random();
        Date c = Calendar.getInstance().getTime();

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            CaptureTime1 = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(c);
            Rand1 = String.format("%04d", random.nextInt(10000));

//            Glide.with(imgv1.getContext()).load(data.getData().toString()).asBitmap().into(imgv1);
            try {
                bitmap1=handleSamplingAndRotationBitmap(getApplicationContext(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgv1.setImageBitmap(bitmap1);
            imgv1.setVisibility(View.VISIBLE);
            pbtn1.setVisibility(View.VISIBLE);
            imgflag1 = true;
        }


        if (requestCode == 11 && resultCode == RESULT_OK) {
            CaptureTime1 = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(c);
            Rand1 = String.format("%04d", random.nextInt(10000));
            // cap1.setText(CaptureTime1);
            setCemeraPic1();
            pbtn1.setVisibility(View.VISIBLE);
            imgflag1 = true;
        }


        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
           /* String realPath = getRealPathFromURI(uri);

            File selectedFile = new File(realPath);
            Date date = new Date(selectedFile.lastModified());*/
            CaptureTime2 = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(c);
            Rand2 = String.format("%04d", random.nextInt(10000));
            // cap2.setText(CaptureTime2);
//            Glide.with(imgv2.getContext()).load(data.getData().toString()).asBitmap().into(imgv2);
            pbtn2.setVisibility(View.VISIBLE);
            try {
                bitmap2=handleSamplingAndRotationBitmap(getApplicationContext(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgv2.setImageBitmap(bitmap2);
            imgv2.setVisibility(View.VISIBLE);
            imgflag2 = true;
        }

        if (requestCode == 12 && resultCode == RESULT_OK) {
            CaptureTime2 = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(c);
            Rand2 = String.format("%04d", random.nextInt(10000));
            // cap2.setText(CaptureTime2);
            setCemeraPic2();
            pbtn2.setVisibility(View.VISIBLE);
            imgflag2 = true;
        }


        if (requestCode == 3 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            /*String realPath = getRealPathFromURI(uri);

            File selectedFile = new File(realPath);
            Date date = new Date(selectedFile.lastModified());*/
            CaptureTime3 = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(c);
            Rand3 = String.format("%04d", random.nextInt(10000));

//            Glide.with(imgv3.getContext()).load(data.getData().toString()).asBitmap().into(imgv3);
            try {
                bitmap3=handleSamplingAndRotationBitmap(getApplicationContext(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgv3.setImageBitmap(bitmap3);
            imgv3.setVisibility(View.VISIBLE);
            pbtn3.setVisibility(View.VISIBLE);
            imgflag3 = true;
        }


        if (requestCode == 13 && resultCode == RESULT_OK) {
            CaptureTime3 = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(c);
            Rand3 = String.format("%04d", random.nextInt(10000));
            setCemeraPic3();
            pbtn3.setVisibility(View.VISIBLE);
            imgflag3 = true;
        }


        if (requestCode == 4 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            CaptureTime4 = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(c);
            Rand4 = String.format("%04d", random.nextInt(10000));

//            Glide.with(imgv4.getContext()).load(data.getData().toString()).asBitmap().into(imgv4);
            try {
                bitmap4=handleSamplingAndRotationBitmap(getApplicationContext(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgv4.setImageBitmap(bitmap4);
            imgv4.setVisibility(View.VISIBLE);
            pbtn4.setVisibility(View.VISIBLE);
            imgflag4 = true;
        }


        if (requestCode == 14 && resultCode == RESULT_OK) {
            CaptureTime4 = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(c);
            Rand4 = String.format("%04d", random.nextInt(10000));

            setCemeraPic4();
            pbtn4.setVisibility(View.VISIBLE);
            imgflag4 = true;
        }


        if (requestCode == 5 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            CaptureTime5 = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(c);
            Rand5 = String.format("%04d", random.nextInt(10000));
//            Glide.with(imgv5.getContext()).load(data.getData().toString()).asBitmap().into(imgv5);

            try {
                bitmap5=handleSamplingAndRotationBitmap(getApplicationContext(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgv5.setImageBitmap(bitmap5);
            imgv5.setVisibility(View.VISIBLE);
            imgflag5 = true;
        }


        if (requestCode == 15 && resultCode == RESULT_OK) {
            CaptureTime5 = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(c);
            Rand5 = String.format("%04d", random.nextInt(10000));
            setCemeraPic5();
            imgflag5 = true;
        }

    }
    public void EnableRuntimePermissionToAccessCamera() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

            customToast("CAMERA permission allows us to Access CAMERA app");

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);

        }
    }


    @SuppressLint({"LongLogTag", "HardwareIds"})
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
    private void uploadImage(){
        Intent intv = getIntent();
        String sheltid = intv.getStringExtra("sheltercode");
        imgv1.buildDrawingCache();
        imgv2.buildDrawingCache();
        imgv3.buildDrawingCache();
        imgv4.buildDrawingCache();
        imgv5.buildDrawingCache();

        bitmap1 = imgv1.getDrawingCache();
        bitmap2 = imgv2.getDrawingCache();
        bitmap3 = imgv3.getDrawingCache();
        bitmap4 = imgv4.getDrawingCache();
        bitmap5 = imgv5.getDrawingCache();
        String fullPath = getFilesDir().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;

        try {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            if(imgflag1) {
                filename1 = sheltid + "-" + CaptureTime1 + getDeviceId(getApplicationContext()).toString() + Rand1 + "_submission1.webp";
                //filename1 = sheltid+"_"+Rand1+"_submission1.webp";
                File file1 = new File(fullPath, filename1);
                file1.createNewFile();
                fOut1 = new FileOutputStream(file1);
                //bitmap1.compress(Bitmap.CompressFormat.WEBP, 100, fOut1);
                bitmap1.compress(Bitmap.CompressFormat.WEBP, 100, fOut1);
                fOut1.flush();
                fOut1.close();
                MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), file1.getAbsolutePath(), file1.getName(), file1.getName());
            }
            if(imgflag2) {
                //filename2 = sheltid+"_"+Rand2+"_submission2.webp";
                filename2 = sheltid + "-" + CaptureTime2 + getDeviceId(getApplicationContext()).toString() + Rand2 + "_submission2.webp";
                File file2 = new File(fullPath, filename2);
                file2.createNewFile();
                fOut2 = new FileOutputStream(file2);
                bitmap2.compress(Bitmap.CompressFormat.WEBP, 100, fOut2);
                fOut2.flush();
                fOut2.close();
                MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), file2.getAbsolutePath(), file2.getName(), file2.getName());
            }
            if(imgflag3) {
                //filename3 = sheltid+"_"+Rand3+"_submission3.webp";
                filename3 = sheltid + "-" + CaptureTime3 + getDeviceId(getApplicationContext()).toString() + Rand3 + "_submission3.webp";
                File file3 = new File(fullPath, filename3);
                file3.createNewFile();
                fOut3 = new FileOutputStream(file3);
                bitmap3.compress(Bitmap.CompressFormat.WEBP, 100, fOut3);
                fOut3.flush();
                fOut3.close();
                MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), file3.getAbsolutePath(), file3.getName(), file3.getName());
            }
            if(imgflag4) {
                //filename4 = sheltid+"_"+Rand4+"_submission4.webp";
                filename4 = sheltid + "-" + CaptureTime4 + getDeviceId(getApplicationContext()).toString() + Rand4 + "_submission4.webp";
                File file4 = new File(fullPath, filename4);
                file4.createNewFile();
                fOut4 = new FileOutputStream(file4);
                bitmap4.compress(Bitmap.CompressFormat.WEBP, 100, fOut4);
                fOut4.flush();
                fOut4.close();
                MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), file4.getAbsolutePath(), file4.getName(), file4.getName());
            }
            if(imgflag5) {
                //filename5 = sheltid+"_"+Rand5+"_submission5.webp";
                filename5 = sheltid + "-" + CaptureTime5 + getDeviceId(getApplicationContext()).toString() + Rand5 + "_submission5.webp";
                File file5 = new File(fullPath, filename5);
                file5.createNewFile();
                fOut5 = new FileOutputStream(file5);
                bitmap5.compress(Bitmap.CompressFormat.WEBP, 100, fOut5);
                fOut5.flush();
                fOut5.close();
                MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), file5.getAbsolutePath(), file5.getName(), file5.getName());
            }
        } catch (Exception e) {
            Log.e("saveToExternalStorage()", e.getMessage());
        }

        sharedPref = getSharedPreferences("LOGININFO", MODE_PRIVATE);
        int userid = sharedPref.getInt("userid", 0);

        sharedPref = getSharedPreferences("GPSLOCATION", MODE_PRIVATE);
        String latv = sharedPref.getString("lat", "");
        String longv = sharedPref.getString("long", "");
        String area = sharedPref.getString("area", "");

        sharedPref = this.getSharedPreferences("EditValues", Context.MODE_PRIVATE);
        int subid = sharedPref.getInt("subid", 0);

        final String statusval = intv.getStringExtra("action");
        final int packageid = intv.getIntExtra("packageid",0);
        final int subpackageid = intv.getIntExtra("subpackageid",0);
        final int mtaskid = intv.getIntExtra("mtaskid",0);
        final int staskid = intv.getIntExtra("staskid",0);
        final String ucom = intv.getStringExtra("ucomment");
        final String sheltercode = intv.getStringExtra("sheltercode");

        String caption1 = cap1.getText().toString();
        String caption2 = cap2.getText().toString();
        String caption3 = cap3.getText().toString();
        String caption4 = cap4.getText().toString();
        String caption5 = cap5.getText().toString();

        mydb = new DBHelper(this);

        if(statusval.equals("new")) {
            submissionValues = new HashMap<String, String>();
            submissionValues.put("userid", Integer.toString(userid));
            submissionValues.put("package_id", Integer.toString(packageid));
            submissionValues.put("subpackage_id",  Integer.toString(subpackageid));
            submissionValues.put("mtaskid",  Integer.toString(mtaskid));
            submissionValues.put("staskid",  Integer.toString(staskid));
            submissionValues.put("sheltercode", sheltercode);
            submissionValues.put("ucom", ucom);
            submissionValues.put("latv", latv);
            submissionValues.put("longv", longv);
            submissionValues.put("area", area);

            if (mydb.insertSaveForm(submissionValues)) {
                int lastsubid = mydb.getLastSubmissionId();
                mydb.insertGallery(userid, lastsubid, packageid,subpackageid,sheltercode,mtaskid, caption1, caption2, caption3, caption4, caption5,
                        filename1, filename2, filename3, filename4, filename5);
                customToast("Successfully Saved Data");

                loading.dismiss();
                Intent inthome = new Intent(getApplicationContext(), GridViewActivity.class);
                startActivity(inthome);
            } else {
                Toast.makeText(getApplicationContext(), "Failed !", Toast.LENGTH_LONG).show();
                loading.dismiss();
                Intent inthome = new Intent(getApplicationContext(), GridViewActivity.class);
                startActivity(inthome);
            }
       }
        else if(statusval.equals("edit")) {
            submissionValues = new HashMap<String, String>();
            submissionValues.put("userid", Integer.toString(userid));
            submissionValues.put("package_id", Integer.toString(packageid));
            submissionValues.put("subpackage_id", Integer.toString(subpackageid));
            submissionValues.put("sheltercode", sheltercode);
            submissionValues.put("mtask", Integer.toString(mtaskid));
            submissionValues.put("stask", Integer.toString(staskid));
            submissionValues.put("ucom", ucom);

            if (mydb.updateSaveSub(submissionValues,subid)) {
                mydb.updateGallery(userid, subid, sheltercode, caption1, caption2, caption3, caption4, caption5,
                        filename1, filename2, filename3, filename4, filename5);

                customToast("Successfully Updated Data");
                loading.dismiss();
                Intent inthome = new Intent(getApplicationContext(), GridViewActivity.class);
                startActivity(inthome);
            } else {
                customToast("Failed to update data");
                loading.dismiss();
                Intent inthome = new Intent(getApplicationContext(), GridViewActivity.class);
                startActivity(inthome);
            }
        }


        int totaldata = mydb.numberOfRowsSUbmittedForm();
        int totalgallery = mydb.numberOfRowsGallery();
        Log.v("TotalSaveShelter", Integer.toString(totaldata)+". \n TotalGallery"+Integer.toString(totalgallery));
        mydb.close();
    }
    public static String getDeviceId(Context context) {

        String deviceId;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {
                deviceId = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }

        return deviceId;
    }
    public void requestRead() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            uploadImage();
            //uploadQuestion();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(getActivity(),"Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(getActivity(),"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
                }
            break;
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    uploadImage();
                } else {
                    // Permission Denied
                    customToast("Permisson Denied");
                }
             break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


     @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.savetostorage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Bundle extras = getIntent().getExtras();
        ///int subid = extras.getInt("id");
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            exit_app();
        }
       /* if (id == R.id.prevbtn) {
            onBackPressed();
        }*/
        if (id == R.id.savetostorage) {
            loading = new ProgressDialog(PicturesActivity.this);
            loading.setMessage("Loading Please Wait...");
            loading.setIcon(R.drawable.loader);
            loading.show();
            Timer buttonTimer = new Timer();
            buttonTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            requestRead();
                        }
                    });
                }
            }, 1000);
        }
        return super.onOptionsItemSelected(item);
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

    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to drop all selected images?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

}
