package app.ddc.lged.emcrp.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by macbookpro on 9/26/17.
 */

public class GPSLocation extends AppCompatActivity {

    SharedPreferences sharedPref;
    GPSTracker gps;
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;

    public void fetchLocationData(){

        gps = new GPSTracker(GPSLocation.this);

        // Check if GPS enabled
        if(gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            String getLat = Double.toString(latitude);
            String getLon = Double.toString(longitude);

            getGpsLocation(getLat,getLon);
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + getLat + "\nLong: " + getLon, Toast.LENGTH_LONG).show();
        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    fetchLocationData();
                } else {
                    Toast.makeText(getApplicationContext(),"Permission Denied, You cannot access location data.",Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public void getGpsLocation(String lat, String lon) {
        sharedPref = getSharedPreferences("GPSLOCATION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("lat", lat);
        editor.putString("long", lon);
        editor.commit();
    }
}
