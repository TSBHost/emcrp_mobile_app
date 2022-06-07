package app.ddc.lged.emcrp.connectivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import app.ddc.lged.emcrp.R;
import app.ddc.lged.emcrp.reports.SubmittedForm;


public class DisconnectAdapter extends RecyclerView.Adapter<DisconnectAdapter.CustomVHolder> {

    private Context context;
    public DisconnectAdapter(Context context) {
        this.context = context;

    }

    @Override
    public CustomVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.retry_layout, parent, false);
        //cvh = new CustomVHolder(view);
        return new CustomVHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomVHolder holder, int position) {

        holder.titem.setText("Retry");
        // Glide.with(holder.imgv.getContext()).load(imgurl.toString()).into(holder.imgv);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Retry", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, SubmittedForm.class);
                context.startActivity(intent);

               // Intent intent = new Intent(context, NewsDetails.class);
               // intent.putExtra("id",news.getId());
               // context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class CustomVHolder extends RecyclerView.ViewHolder{

        TextView titem;
        public CustomVHolder(View itemView) {
            super(itemView);
            titem = (TextView) itemView.findViewById(R.id.retry);
        }
    }
}
