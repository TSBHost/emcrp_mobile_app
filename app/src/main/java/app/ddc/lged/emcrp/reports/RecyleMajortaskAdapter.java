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

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import app.ddc.lged.emcrp.R;


public class RecyleMajortaskAdapter extends RecyclerView.Adapter<RecyleMajortaskAdapter.CustomVHolder> {

    private Context context;
    List<MajortaskModel> data = Collections.emptyList();
    Cursor mCursor;


    public RecyleMajortaskAdapter(Context context, Cursor data) {
        this.context = context;
        this.mCursor = data;
    }

    @Override
    public CustomVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.mtask_list, parent, false);
        return new CustomVHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomVHolder holder, final int position) {
        if(!mCursor.moveToPosition(position)){
            return;
        }

        final int id =  mCursor.getInt(mCursor.getColumnIndex("id"));
        final int tid =  mCursor.getInt(mCursor.getColumnIndex("tid"));
        String pname = mCursor.getString(mCursor.getColumnIndex("name"));
        String details = mCursor.getString(mCursor.getColumnIndex("details"));

        holder.packageid.setText(pname);
        holder.itemView.setTag(id);

        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle(pname);
                alertDialog.setMessage(details);

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });
        holder.packageid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(context, "ID: "+news.getId(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, Subtask.class);
                    intent.putExtra("majortask",tid);
                    context.startActivity(intent);
                }
            });
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public class CustomVHolder extends RecyclerView.ViewHolder{

        Button packageid;
        ImageView details;
        public CustomVHolder(View itemView) {
            super(itemView);
            packageid = (Button) itemView.findViewById(R.id.packageid);
            details = (ImageView) itemView.findViewById(R.id.details);
        }
    }
}
