package app.ddc.lged.emcrp.reports;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import app.ddc.lged.emcrp.R;


public class RecyleSubPackageAdapter extends RecyclerView.Adapter<RecyleSubPackageAdapter.CustomVHolder> {

    private Context context;
    List<SubPackageModel> data = Collections.emptyList();
    Cursor mCursor;


    public RecyleSubPackageAdapter(Context context, Cursor data) {
        this.context = context;
        this.mCursor = data;
    }

    @Override
    public CustomVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.subpackage_list, parent, false);
        return new CustomVHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomVHolder holder, final int position) {
        if(!mCursor.moveToPosition(position)){
            return;
        }

        final int id =  mCursor.getInt(mCursor.getColumnIndex("id"));
        final int tid =  mCursor.getInt(mCursor.getColumnIndex("tid"));
        final int package_id =  mCursor.getInt(mCursor.getColumnIndex("package_id"));
        String pcode = mCursor.getString(mCursor.getColumnIndex("internal_code"));
        String pname = mCursor.getString(mCursor.getColumnIndex("name"));
        String palias = mCursor.getString(mCursor.getColumnIndex("alias"));

        holder.packageid.setText(pcode);
        holder.packagename.setText(" = "+palias);
        holder.itemView.setTag(id);

        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle(pcode);
                alertDialog.setMessage(pname);

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // Toast.makeText(context, "ID: "+package_id, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, Majortask.class);
                    intent.putExtra("subpackage_id",tid);
                    intent.putExtra("package_id",package_id);
                    intent.putExtra("sheltercode",pcode);
                    context.startActivity(intent);
                }
            });
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }


    public void swapCursor(Cursor newCursor){
        if(mCursor != null){
            mCursor.close();
        }
        mCursor = newCursor;
        if(newCursor != null){
            notifyDataSetChanged();
        }
    }


    public class CustomVHolder extends RecyclerView.ViewHolder{


        Button packageid;
        ImageView details;
        TextView packagename;
        public CustomVHolder(View itemView) {
            super(itemView);
            packageid = (Button) itemView.findViewById(R.id.packageid);
            details = (ImageView) itemView.findViewById(R.id.details);
            packagename = (TextView) itemView.findViewById(R.id.packagename);
        }
    }
}
