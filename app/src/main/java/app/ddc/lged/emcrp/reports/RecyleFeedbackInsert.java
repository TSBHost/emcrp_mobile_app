package app.ddc.lged.emcrp.reports;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.ddc.lged.emcrp.R;


public class RecyleFeedbackInsert extends RecyclerView.Adapter<RecyleFeedbackInsert.CustomVHolder> {
    //CustomVHolder cvh;
    private Context context;

    Activity activity;
    List<FeedbackModel> data = Collections.emptyList();
    //private final String IUrl = conf.getImgURL()+"news/";


    public RecyleFeedbackInsert(Context context, ArrayList<FeedbackModel> data) {
        this.context = context;
        this.data = data;

    }

    @Override
    public RecyleFeedbackInsert.CustomVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.feedbackinsert, parent, false);
        //cvh = new CustomVHolder(view);
        return new RecyleFeedbackInsert.CustomVHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomVHolder holder, final int position) {
        holder.hqfeed.setText(data.get(position).getHqFeed()+data.get(position).getId());
        holder.hqfeeddate.setText(data.get(position).getHqFeedDate());
        holder.userfeed.setText(data.get(position).getUserfeed());
        holder.userfeeddate.setText(data.get(position).getUserFeedDate());

        String ufeed1 = data.get(position).getUserfeed();
        /*if(ufeed1.equals("null") || ufeed1.equals(null)) {
            //holder.replytn.setImageResource(R.drawable.reply);
            holder.replytn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.reply, 0, 0, 0);
            holder.replytn.setText("Reply");
        }
        else{
            //holder.replytn.setImageResource(R.drawable.reply_dis);
            holder.replytn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.reply_dis, 0, 0, 0);
            holder.replytn.setText("Replied");
            holder.replytn.setEnabled(false);
        }*/

        //Toast.makeText(RecyleFeedbackAdapter.this, "Total"+Integer.toString(news.getTotalform()),Toast.LENGTH_LONG).show();

            // Glide.with(holder.imgv.getContext()).load(imgurl.toString()).into(holder.imgv);
            holder.replytn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // Toast.makeText(context, "ID: "+data.get(position).getId(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, FeedBackInsert.class);
                    intent.putExtra("feedid",data.get(position).getId());
                    intent.putExtra("shelter",data.get(position).getShelter());
                    intent.putExtra("submissionid",data.get(position).getSubid());
                    intent.putExtra("userfeed",data.get(position).getUserfeed());
                    //intent.putExtra("shelter",data.get(position).getShelter());
                    /*intent.putExtra("mtask",data.get(position).getMtask());
                    intent.putExtra("subdate",data.get(position).getSdate());
                    intent.putExtra("hqfeed",data.get(position).getHqFeed());
                    intent.putExtra("hqfeeddate",data.get(position).getHqFeedDate());
                    intent.putExtra("hqstatus",data.get(position).getHqStatus());
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

        TextView hqfeed,hqfeeddate,userfeed,userfeeddate;
        Button replytn;
        public CustomVHolder(View itemView) {
            super(itemView);
            hqfeed = (TextView) itemView.findViewById(R.id.hqfeed);
            hqfeeddate = (TextView) itemView.findViewById(R.id.hqfeeddate);
            userfeed = (TextView) itemView.findViewById(R.id.userfeedback);
            userfeeddate = (TextView) itemView.findViewById(R.id.userfeeddate);
            replytn = (Button) itemView.findViewById(R.id.replytn);
        }
    }
}
