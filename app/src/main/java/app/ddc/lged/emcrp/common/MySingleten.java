package app.ddc.lged.emcrp.common;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MySingleten {
        private static MySingleten mInstance;
        private static Context mContx;
        private RequestQueue requestQueue;

        private MySingleten(Context context){
            this.mContx = context;
            this.requestQueue = getRequestQueue();
        }
        private RequestQueue getRequestQueue(){
            if(requestQueue== null){
                requestQueue = Volley.newRequestQueue(mContx.getApplicationContext());
            }
            return requestQueue;
        }

        public static synchronized MySingleten getmInstance(Context context){
            if(mInstance==null){
                mInstance = new MySingleten(context);
            }
            return mInstance;
        }

        public<T> void addToRequestQue(Request<T> request){
            getRequestQueue().add(request);
        }
}
