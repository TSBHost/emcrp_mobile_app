package app.ddc.lged.emcrp.common;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import app.ddc.lged.emcrp.connectivity.ConnectivityReceiver;

/**
 * Created by Ravi Tamada on 15/06/16.
 * www.androidhive.info
 */
public class MyApplication extends Application {

    private static MyApplication mInstance;
    protected RequestQueue queue;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        queue = Volley.newRequestQueue(this);
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    public Context getAppContext() {
        return mInstance.getApplicationContext();
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
