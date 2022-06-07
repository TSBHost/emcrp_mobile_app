package app.ddc.lged.emcrp.reports;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import app.ddc.lged.emcrp.ContentDetails;
import app.ddc.lged.emcrp.MenuModel;
import app.ddc.lged.emcrp.R;


public class RecyleMenuAdapter extends RecyclerView.Adapter<RecyleMenuAdapter.CustomVHolder> {
    private MenuModel[] data;
    //CustomVHolder cvh;
    private Context context;

    //private final String IUrl = conf.getImgURL()+"news/";


    public RecyleMenuAdapter(Context context, MenuModel[] data) {
        this.context = context;
        this.data = data;

    }

    @Override
    public CustomVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.menu_list, parent, false);
        //cvh = new CustomVHolder(view);
        return new CustomVHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomVHolder holder, int position) {
        final MenuModel menu = data[position];

        if(menu.getTotalform() > 0){
            holder.linearLayout.setVisibility(View.GONE);
            holder.udata.setVisibility(View.VISIBLE);

            holder.titem.setText(menu.getTitle());
            // Glide.with(holder.imgv.getContext()).load(imgurl.toString()).into(holder.imgv);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(context, "ID: "+menu.getId(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, ContentDetails.class);
                    intent.putExtra("id",menu.getId());
                    context.startActivity(intent);

                }
            });
        }
        else{
            holder.linearLayout.setVisibility(View.VISIBLE);
            holder.udata.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class CustomVHolder extends RecyclerView.ViewHolder{

        TextView titem;
        LinearLayout linearLayout,udata;
        public CustomVHolder(View itemView) {
            super(itemView);
            titem = (TextView) itemView.findViewById(R.id.titles);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.zerodata);
            udata = (LinearLayout) itemView.findViewById(R.id.userdata);
        }
    }
}
