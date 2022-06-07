package app.ddc.lged.emcrp.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class ConnectivityReceiver extends BroadcastReceiver {

    public static ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityReceiver() {
        super();
    }
    @Override
    public void onReceive(Context context, Intent arg1) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
        }
        String status = NetworkUtil.getConnectivityStatusString(context);
        Toast.makeText(context, status, Toast.LENGTH_LONG).show();


       /* ComponentName comp = new ComponentName(context.getPackageName(), NetworkService.class.getName());
        arg1.putExtra("isNetworkConnected",isConnected());
        context.startService(arg1.setComponent(comp));

        gv = new GridViewActivity();
        if(isConnected() == true){
            Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
            gv.syncAllData();
        }
        else{
            Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
        }*/
    }

    public static boolean isConnected(Context context) {
        /*ConnectivityManager cm = (ConnectivityManager) MyApplication.getInstance().getAppContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();*/



       /* ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfoMob = cm.getNetworkInfo(cm.TYPE_MOBILE);
        NetworkInfo netInfoWifi = cm.getNetworkInfo(cm.TYPE_WIFI);
        if ((netInfoMob != null || netInfoWifi != null) && (netInfoMob.isConnectedOrConnecting() || netInfoWifi.isConnectedOrConnecting())) {
            return true;
        }
        return false;*/


        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected())
                || (connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnected())) {
            return true;
        } else {
            return false;
        }

    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }

}