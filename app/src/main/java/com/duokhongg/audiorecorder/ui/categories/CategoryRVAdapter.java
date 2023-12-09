package com.duokhongg.audiorecorder.ui.categories;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.duokhongg.audiorecorder.R;
import com.duokhongg.audiorecorder.data.model.Category;
import com.duokhongg.audiorecorder.ui.records.AudioRecordViewModel;
import com.duokhongg.audiorecorder.utils.Helper;

public class CategoryRVAdapter extends ListAdapter<Category, CategoryRVAdapter.ViewHolder> {

    private OnItemClickListener listener;

    CategoryRVAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Category> DIFF_CALLBACK =  new DiffUtil.ItemCallback<Category>() {
        @Override
        public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.getCategoryName().equals(newItem.getCategoryName()) &&
                    oldItem.getColor() == newItem.getColor();
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_category_item, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryRVAdapter.ViewHolder holder, int position) {
        Category category = getItem(position);
        holder.txtName.setText(category.getCategoryName());

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
                                CategoryViewModel categoryViewModel = new ViewModelProvider((ViewModelStoreOwner) holder.itemView.getContext()).get(CategoryViewModel.class);
                                categoryViewModel.delete(getItem(position));
                            }
                            return true;
                        } else if (item.getItemId() == R.id.itemEdit) {
                            int position = holder.getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                CategoryViewModel categoryViewModel = new ViewModelProvider((ViewModelStoreOwner) holder.itemView.getContext()).get(CategoryViewModel.class);
                                Helper.openEditCategoryDialog(holder.itemView.getContext(), getItem(position), categoryViewModel, Gravity.CENTER);
                            }
                            return true;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Category category);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
