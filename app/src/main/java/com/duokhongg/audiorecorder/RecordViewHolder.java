package com.duokhongg.audiorecorder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecordViewHolder extends RecyclerView.ViewHolder {
    public TextView txtName;
    public TextView txtDuration;
    public TextView txtLastModified;
    public ImageButton imgBtn;
    public TextView txtCategory;
    private RecordsAdapter.OnItemClickListener onItemClickListener;

    public RecordViewHolder(@NonNull View itemView, RecordsAdapter.OnItemClickListener onItemClickListener) {
        super(itemView);


        txtName = itemView.findViewById(R.id.txtName);
        txtDuration = itemView.findViewById(R.id.txtDuration);
        txtLastModified = itemView.findViewById(R.id.txtLastModified);
        // imgBtn = itemView.findViewById(R.id.imgBtn);
        txtCategory = itemView.findViewById(R.id.txtCategory);

        this.onItemClickListener = onItemClickListener;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(pos);
                    }
                }
            }
        });
    }
}
