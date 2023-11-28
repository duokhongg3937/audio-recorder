package com.duokhongg.audiorecorder;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class RecordsAdapter extends RecyclerView.Adapter<RecordViewHolder> {
    private Context context;
    public DatabaseHandler db;
    public void setRecordList(List<AudioRecord> recordList) {
        this.recordList = recordList;
        notifyDataSetChanged();
    }
    private List<AudioRecord> recordList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    public RecordsAdapter(Context context, List<AudioRecord> recordList) {
        this.context = context;
        this.recordList = recordList;
        db = new DatabaseHandler(context);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_item, parent, false);
        return new RecordViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        AudioRecord data = recordList.get(position);
        holder.txtName.setText(data.fileName);
        holder.txtDuration.setText(Helper.formatDuration(data.duration));
        holder.txtLastModified.setText(Helper.formatLastModified(Long.parseLong(data.timeStamp)));

        ImageButton btnMore = holder.itemView.findViewById(R.id.btnMore);

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(holder.itemView.getContext(), btnMore);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.itemDelete) {
                            int position = holder.getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                deleteRecord(recordList.get(position).fileName);
                                db.deleteRecord(recordList.get(position));
                                recordList.remove(position);

                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, recordList.size());
                            }
                            return true;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }


    private void deleteRecord(String fileName) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, fileName);
        file.delete();
    }
}
