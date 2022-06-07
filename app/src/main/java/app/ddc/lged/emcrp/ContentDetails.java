package app.ddc.lged.emcrp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

import app.ddc.lged.emcrp.connectivity.Config;

public class ContentDetails extends NavigationActivity {

    Config conf=new Config();
    TextView title,content,date;
    private final String URL = conf.getURL()+"get_article.php";

    String mtitle,mcontent,mdate;

    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_shelter_details);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_content_details, null, false);
        drawer.addView(view,0);

        actionBar = getSupportActionBar();


        title = (TextView) findViewById(R.id.title);
        content = (TextView) findViewById(R.id.content);
        date = (TextView) findViewById(R.id.datetime);

        final ProgressDialog loading = ProgressDialog.show(this,"Loading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        loading.dismiss();
                        try {
                            Log.e("RESPONSE_Image", s);
                            JSONObject json = new JSONObject(s);
                            JSONArray data = json.getJSONArray("content_details");
                            for(int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                mtitle = obj.getString("title");
                                mcontent = obj.getString("content");
                                mdate = obj.getString("sdate");
                            }

                            actionBar.setTitle(mtitle);
                            title.setText(mtitle);
                            date.setText(mdate);
                            content.setText(Html.fromHtml(mcontent));


                        } catch (JSONException e) {
                            Log.d("JSON Exception", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        loading.dismiss();
                        Log.e("Error", volleyError.getMessage().toString());
                        Toast.makeText(getApplicationContext(),
                                "Content Loading Failed", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Intent intent = getIntent();
                int nid = intent.getIntExtra("id",0);
                Map<String,String> params = new Hashtable<String, String>();
                params.put("id", Integer.toString(nid));

                return params;
            }
        };

        //VolleyAppController.getInstance().addToRequestQueue(stringRequest);

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestQueue.add(stringRequest);

    }


}
