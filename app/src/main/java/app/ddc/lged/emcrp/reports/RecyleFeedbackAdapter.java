package app.ddc.lged.emcrp.reports;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.ddc.lged.emcrp.R;


public class RecyleFeedbackAdapter extends RecyclerView.Adapter<RecyleFeedbackAdapter.CustomVHolder> {
    //CustomVHolder cvh;
    private Context context;

    Activity activity;
    List<FeedbackModel> data = Collections.emptyList();
    //private final String IUrl = conf.getImgURL()+"news/";


    public RecyleFeedbackAdapter(Context context, ArrayList<FeedbackModel> data) {
        this.context = context;
        this.data = data;

    }

    @Override
    public RecyleFeedbackAdapter.CustomVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.feedback_list, parent, false);
        //cvh = new CustomVHolder(view);
        return new RecyleFeedbackAdapter.CustomVHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomVHolder holder, final int position) {
        holder.titem.setText(data.get(position).getShelter());
        holder.mtask.setText(data.get(position).getMtask());
        holder.dated.setText(data.get(position).getSdate());
        //Toast.makeText(RecyleFeedbackAdapter.this, "Total"+Integer.toString(news.getTotalform()),Toast.LENGTH_LONG).show();

            // Glide.with(holder.imgv.getContext()).load(imgurl.toString()).into(holder.imgv);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   //Toast.makeText(context, "ID: "+data.get(position).getSubid(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, FeedbackForm.class);
                    intent.putExtra("subid",data.get(position).getSubid());
                    intent.putExtra("shelter",data.get(position).getShelter());
                    intent.putExtra("mtask",data.get(position).getMtask());
                    intent.putExtra("subdate",data.get(position).getSdate());
                    /*intent.putExtra("hqfeed",data.get(position).getHqFeed());
                    intent.putExtra("hqfeeddate",data.get(position).getHqFeedDate());
                    intent.putExtra("status",data.get(position).getStatus());
                    intent.putExtra("userfeed",data.get(position).getUserfeed());
                    intent.putExtra("userimg",data.get(position).getUserImg());
                    intent.putExtra("userfeeddate",data.get(position).getUserFeedDate());*/

                    context.startActivity(intent);

                }
            });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class CustomVHolder extends RecyclerView.ViewHolder{

        TextView titem,mtask,dated;
        public CustomVHolder(View itemView) {
            super(itemView);
            titem = (TextView) itemView.findViewById(R.id.titles);
            mtask = (TextView) itemView.findViewById(R.id.mtask);
            dated = (TextView) itemView.findViewById(R.id.datetime);
        }
    }
}
