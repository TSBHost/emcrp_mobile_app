package app.ddc.lged.emcrp.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import app.ddc.lged.emcrp.R;
import app.ddc.lged.emcrp.connectivity.DBHelper;

public class GridCustomeAdapter extends BaseAdapter {
    private Context context;
    private final String[] mobileValues;
    private DBHelper mydb;
    SharedPreferences sharedPref;

    public GridCustomeAdapter(Context context, String[] mobileValues) {
        this.context = context;
        this.mobileValues = mobileValues;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            sharedPref = context.getSharedPreferences("LOGININFO", Context.MODE_PRIVATE);
            int uid = sharedPref.getInt("userid", 0);

            mydb = new DBHelper(context);
            int totalpend = mydb.totalPendingForm(uid,0);
            int totalsent = mydb.totalPendingForm(uid,1);
            int totalFeed = mydb.totalUserFeedback(uid);
            convertView = inflater.inflate(R.layout.grid_content, null);
            TextView textView = (TextView) convertView.findViewById(R.id.icontitle);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.iconimg);

            String mobile = mobileValues[position];

             if (mobile.equals("Refresh Data")) {
                textView.setText(mobileValues[position]);
                //textView.setTextColor(Color.parseColor("#0A3887"));
                imageView.setImageResource(R.drawable.refresh);
            }
            else if (mobile.equals("Send Reports")) {
                textView.setText(mobileValues[position]);
                imageView.setImageResource(R.drawable.mobilesetting);
            }
            else if (mobile.equals("Submitted Forms")) {
                if(totalsent > 0){
                    String totalnum = " ( "+ totalsent +" )";
                    textView.setText(mobileValues[position]+totalnum);
                }
                else{
                    textView.setText(mobileValues[position]);
                }

                imageView.setImageResource(R.drawable.reviewdata);
            }
            else if (mobile.equals("Send Reports to Server")) {
                if(totalpend > 0){
                    String totalnum = " ( "+ totalpend +" )";
                    textView.setText(mobileValues[position]+totalnum);
                }
                else{
                    textView.setText(mobileValues[position]);
                }
                imageView.setImageResource(R.drawable.sendserver);
            }
            else if (mobile.equals("Comments from HQ")) {
                if(totalFeed > 0){
                    String totalnum = " ( "+ totalFeed +" )";
                    textView.setText(mobileValues[position]+totalnum);
                }
                else{
                    textView.setText(mobileValues[position]);
                }

                //textView.setTextColor(Color.parseColor("#EFB505"));
                imageView.setImageResource(R.drawable.feedback);
            }

            else if (mobile.equals("Clear All Data")) {
                textView.setText(mobileValues[position]);
                imageView.setImageResource(R.drawable.clear);
                // textView.setTextColor(Color.parseColor("#FB1300"));
            }
            /*else if (mobile.equals("Logout")) {
                textView.setText(mobileValues[position]);
                imageView.setImageResource(R.drawable.logouticon);
                //textView.setTextColor(Color.parseColor("#FB1300"));
            }*/
        /*} else {
            gridView = (View) convertView;
        }*/

        return convertView;
    }

    @Override
    public int getCount() {
        return mobileValues.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}