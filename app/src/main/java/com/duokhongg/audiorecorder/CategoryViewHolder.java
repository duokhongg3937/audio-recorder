package com.duokhongg.audiorecorder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    public TextView txtName;
    private CategoryAdapter.OnItemClickListener onItemClickListener;

    public CategoryViewHolder(@NonNull View itemView, CategoryAdapter.OnItemClickListener onItemClickListener) {
        super(itemView);

        txtName = itemView.findViewById(R.id.txtName);
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
