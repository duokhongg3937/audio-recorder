package com.duokhongg.audiorecorder;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
    private Context context;
    public DatabaseHandler db;
    private List<Category> categoryList;
    private OnItemClickListener onItemClickListener;

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        db = new DatabaseHandler(context);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_category_item, parent, false);
        return new CategoryViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category data = categoryList.get(position);
        holder.txtName.setText(data.getCategoryName());
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
                                db.deleteCategory(categoryList.get(position));
                                categoryList.remove(position);

                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, categoryList.size());
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
        return categoryList.size();
    }
}
