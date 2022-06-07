package app.ddc.lged.emcrp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;

import app.ddc.lged.emcrp.connectivity.Config;
import app.ddc.lged.emcrp.connectivity.ConnectivityReceiver;
import app.ddc.lged.emcrp.connectivity.DBHelper;
import app.ddc.lged.emcrp.connectivity.NotificationDemo;
import app.ddc.lged.emcrp.home.GridViewActivity;
import app.ddc.lged.emcrp.home.LoginActivity;
import app.ddc.lged.emcrp.reports.RecyleMenuAdapter;

public class NavigationActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener {

    // implements NavigationView.OnNavigationItemSelectedListener
    protected DrawerLayout drawer;
    private DBHelper mydb;
    RecyclerView recfeed;
    Config conf = new Config();
    private final String URL = conf.getURL() + "get_menu.php";
    RecyleMenuAdapter radapter;
    SharedPreferences sharedPref;
    TextView uname, userid, pin,cver;
    String newVersion,currentVersion;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);
        //checkConnection();
        //navigationView.bringToFront();
       // currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        uname = (TextView) findViewById(R.id.username);
        userid = (TextView) findViewById(R.id.userid);
        pin = (TextView) findViewById(R.id.pinno);/// User status
        cver = (TextView) findViewById(R.id.version);/// User status
        userInformation();
    }

    public void userInformation() {
        sharedPref = this.getSharedPreferences("LOGININFO", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        String name = sharedPref.getString("name", null);
        int uid = sharedPref.getInt("userid", 0);
        Boolean loggedin = sharedPref.getBoolean("loggedin", true);
        mydb = new DBHelper(this);
        Cursor response = mydb.getDataUsers(uid);
        if (response.moveToFirst()) {
            pin.setText("Your Pin is  : " + response.getString(response.getColumnIndex("pin")));
        }
        uname.setText("Name: " + name);
        userid.setText("ID : " + username);

        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            cver.setText("App Current Version  : " + currentVersion);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            exit_app();
        }
        /*if (id == R.id.syncdata) {
            syncdata();
        }*/
        if (id == R.id.home) {
            homeActivity();
        }
        if (id == R.id.logout) {
            logout();
        }
        /*if (id == R.id.update) {
            getAndroidVersion();
        }*/
        /*if (id == R.id.contact) {
            contactActivity();
        }*/
        /*if (id == R.id.newShelter) {
            insertshelter();
        }*/

        return super.onOptionsItemSelected(item);
    }

    public void logout() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.PauseDialog);
        builder.setMessage(R.string.logutcont)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sharedPref = getSharedPreferences("LOGININFO", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("username", "");
                        editor.putString("name", "");
                        editor.putString("password", "");
                        editor.putInt("userid", 0);
                        editor.commit();
                        finish();
                        Intent goin = new Intent(NavigationActivity.this, LoginActivity.class);
                        startActivity(goin);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });

        android.app.AlertDialog d = builder.create();
        d.setTitle("Confirmation !");
        d.show();
    }

    private long value(String string) {
        string = string.trim();
        if( string.contains( "." )){
            final int index = string.lastIndexOf( "." );
            return value( string.substring( 0, index ))* 100 + value( string.substring( index + 1 ));
        }
        else {
            return Long.valueOf( string );
        }
    }


    public void getAndroidVersion() {
        GetVersionCode vClass = new GetVersionCode();
        vClass.execute();
        }

    private class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = null;
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + NavigationActivity.this.getPackageName() + "&hl=it")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
                return newVersion;
            } catch (Exception e) {
                return newVersion;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
                    //show dialog
                }
            }
            Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);
        }
    }


    public void homeActivity(){
        Intent intent = new Intent(getApplicationContext(), GridViewActivity.class);
        startActivity(intent);
    }
    public void notifictionActivity(){
        Intent intent = new Intent(getApplicationContext(), NotificationDemo.class);
        startActivity(intent);
    }

    public void exit_app() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure ? you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {
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


    /*
    public void cutommenu(){
        recfeed = (RecyclerView) findViewById(R.id.recyfeedback);
        recfeed.setLayoutManager(new LinearLayoutManager(this));


        StringRequest request = new StringRequest(URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("Menu List: ",s);
               try {
                   GsonBuilder gsonBuilder = new GsonBuilder();
                   Gson gson = gsonBuilder.create();
                   MenuModel[] menu = gson.fromJson(s, MenuModel[].class);
                   radapter = new RecyleMenuAdapter(NavigationActivity.this, menu);
                   if (radapter.getItemCount() > 0) {
                       //Toast.makeText(getApplicationContext(), "Item: "+radapter.getItemCount(), Toast.LENGTH_LONG).show();
                       recfeed.setAdapter(radapter);
                   } else {
                       //Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
                   }
               }
               catch (IllegalStateException | JsonSyntaxException exception)
               {
                    Log.v("ErrorMenu", exception.toString());
               }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                Log.e("Error", "Failed");
                *//*Toast.makeText(getApplicationContext(),
                        "Data Loading failed. Please try again", Toast.LENGTH_LONG).show();*//*
            }
        });


        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        //showSnack(isConnected);
        String message;
        if (isConnected) {
            message = "Connected";
            // Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
            cutommenu();

            //color = Color.WHITE;
        } else {
            message = "Not Connected !";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            *//*final RecyclerView rcv = (RecyclerView) findViewById(R.id.recylv);
            rcv.setLayoutManager(new LinearLayoutManager(this));
            rcv.setAdapter(new DisconnectAdapter(this));*//*
        }
    }
*/
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        //showSnack(isConnected);
        String message;
        /*if (isConnected) {
            cutommenu();
            //color = Color.WHITE;
        } else {
            message = "Not Connected !";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            final RecyclerView rcv = (RecyclerView) findViewById(R.id.recylv);
            rcv.setLayoutManager(new LinearLayoutManager(this));
            rcv.setAdapter(new DisconnectAdapter(this));
        }*/
    }


    public void customToast(String tstMsg){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custtoastid));
        ImageView imgv = (ImageView) layout.findViewById(R.id.customToastImage);
        TextView text = (TextView) layout.findViewById(R.id.customToastText);
        imgv.setImageDrawable(getResources().getDrawable(R.drawable.sync));
        text.setText(tstMsg);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
