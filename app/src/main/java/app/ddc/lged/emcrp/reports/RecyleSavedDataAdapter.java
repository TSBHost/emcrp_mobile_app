package app.ddc.lged.emcrp.reports;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.ddc.lged.emcrp.R;
import app.ddc.lged.emcrp.connectivity.DBHelper;


public class RecyleSavedDataAdapter extends RecyclerView.Adapter<RecyleSavedDataAdapter.CustomVHolder> {

    //CustomVHolder cvh;
    private Context context;

    Activity activity;
    List<SaveSubmissionModel> data = Collections.emptyList();
    //private final String IUrl = conf.getImgURL()+"news/";
    private List<String> currentSelectedItems = new ArrayList<>();
    SaveSubmissionActivity saveactivity;

    public RecyleSavedDataAdapter(Context context, List<SaveSubmissionModel> data) {
        this.context = context;
        this.data = data;

    }

    @Override
    public CustomVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_sendtoserver, parent, false);
        //cvh = new CustomVHolder(view);
        return new CustomVHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomVHolder holder, final int position) {

        final SaveSubmissionModel objIncome = data.get(position);

            if(!data.get(position).getMtask().equals("0")) {
                DBHelper mydb = new DBHelper(context);
                Cursor prs = mydb.getSelectedMajortask(Integer.parseInt(data.get(position).getMtask()));
                prs.moveToFirst();
                String mtaskname = prs.getString(prs.getColumnIndex("name"));
                prs.close();

                holder.mtask.setText(mtaskname);
            }
            else{
                holder.mtask.setText("");
            }
            holder.titem.setText(data.get(position).getShelter());
            holder.dated.setText(data.get(position).getDate());
        //holder.mtask.setText(data.get(position).getMtask());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(context, "ID: "+news.getId(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, SavedSubmissionDetails.class);
                    intent.putExtra("id",data.get(position).getId());
                    context.startActivity(intent);
                }
            });


       /* holder.cbSelect.setTag(position);
        holder.cbSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer pos = (Integer) holder.cbSelect.getTag();
                if (data.get(pos).getSelected()) {
                    data.get(pos).setSelected(false);
                } else {
                    data.get(pos).setSelected(true);
                    String strval = Integer.toString(data.get(pos).getId());

                    Toast.makeText(context, strval + " clicked!", Toast.LENGTH_SHORT).show();
                    currentSelectedItems.add(strval);

                    Intent intent = new Intent(context, SavedSubmissionDetails.class);
                    intent.putExtra("id",data.get(position).getId());
                    context.startActivity(intent);
                }
            }
        });*/

        /*holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                String strval = Integer.toString(data.get(position).getId());

                if(compoundButton.isChecked())
                {
                    compoundButton.setChecked(true);
                    currentSelectedItems.add(strval);
                    //holder.userdata.setBackgroundColor(context.getResources().getColor(R.color.black_overlay));
                    holder.userdata.setBackgroundResource(R.drawable.checked_box);
                    //Toast.makeText(context, strval + " clicked!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    compoundButton.setChecked(false);
                    currentSelectedItems.remove(strval);
                    holder.userdata.setBackgroundResource(R.drawable.box_shadow);
                   // Toast.makeText(context, strval + " removed!", Toast.LENGTH_SHORT).show();
                }
                saveactivity = new SaveSubmissionActivity();
                saveactivity.getCheckedItems(currentSelectedItems);
                //Toast.makeText(context, currentSelectedItems.toString() + " clicked!", Toast.LENGTH_SHORT).show();
            }
        });*/

    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    public class CustomVHolder extends RecyclerView.ViewHolder{

        TextView titem,mtask,dated;
        LinearLayout userdata;
        //public CheckBox cbSelect;
        public CustomVHolder(View itemView) {
            super(itemView);
            titem = (TextView) itemView.findViewById(R.id.titles);
            mtask = (TextView) itemView.findViewById(R.id.mtask);
            dated = (TextView) itemView.findViewById(R.id.datetime);
            //cbSelect = (CheckBox) itemView.findViewById(R.id.cbSelect);
            userdata = (LinearLayout) itemView.findViewById(R.id.userdata);
        }
    }
}
