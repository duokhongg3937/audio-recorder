package com.duokhongg.audiorecorder.ui.records;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import com.duokhongg.audiorecorder.data.model.AudioRecord;
import com.duokhongg.audiorecorder.data.model.RecordWithCategory;
import com.duokhongg.audiorecorder.data.repository.CategoryRepository;
import com.duokhongg.audiorecorder.ui.categories.CategoryViewModel;
import com.duokhongg.audiorecorder.utils.Helper;

public class RecordWithCategoryRVAdapter extends ListAdapter<RecordWithCategory, RecordWithCategoryRVAdapter.ViewHolder> {

    private OnItemClickListener listener;

    RecordWithCategoryRVAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<RecordWithCategory> DIFF_CALLBACK =  new DiffUtil.ItemCallback<RecordWithCategory>() {
        @Override
        public boolean areItemsTheSame(@NonNull RecordWithCategory oldItem, @NonNull RecordWithCategory newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull RecordWithCategory oldItem, @NonNull RecordWithCategory newItem) {
            return oldItem.getCategoryColor() == newItem.getCategoryColor() &&
                    oldItem.getFileName().equals(newItem.getFileName()) &&
                    oldItem.getFilePath().equals(newItem.getFilePath()) &&
                    oldItem.getDuration().equals(newItem.getDuration()) &&
                    oldItem.getCategoryName().equals(newItem.getCategoryName()) &&
                    oldItem.getTimeStamp().equals(newItem.getTimeStamp());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_item, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordWithCategoryRVAdapter.ViewHolder holder, int position) {
        RecordWithCategory record = getItem(position);
        holder.txtName.setText(record.getFileName());
        holder.txtDuration.setText(Helper.formatDuration(record.getDuration()));
        holder.txtLastModified.setText(Helper.formatLastModified(Long.parseLong(record.getTimeStamp())));
        holder.txtCategory.setText(record.getCategoryName());
        holder.txtCategory.setTextColor(Color.WHITE);
        holder.txtCategory.setPadding(8,4,8,4);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(record.getCategoryColor());
        gd.setCornerRadius(5);
        gd.setStroke(1, record.getCategoryColor()); // Sets border color and stroke
        holder.txtCategory.setBackground(gd);

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
                                AudioRecordViewModel recordViewModel = new ViewModelProvider((ViewModelStoreOwner) holder.itemView.getContext()).get(AudioRecordViewModel.class);
                                recordViewModel.delete(Helper.RecordWithCategory2Record(getItem(position)));
                            }
                            return true;
                        } else if (item.getItemId() == R.id.itemEdit) {
                            int position = holder.getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                AudioRecordViewModel recordViewModel = new ViewModelProvider((ViewModelStoreOwner) holder.itemView.getContext()).get(AudioRecordViewModel.class);
                                Helper.openEditRecordDialog(holder.itemView.getContext(), getItem(position), recordViewModel, Gravity.CENTER);
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
        TextView txtName, txtDuration, txtLastModified, txtCategory;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            txtLastModified = itemView.findViewById(R.id.txtLastModified);
            txtCategory = itemView.findViewById(R.id.txtCategory);

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
        void onItemClick(RecordWithCategory record);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
