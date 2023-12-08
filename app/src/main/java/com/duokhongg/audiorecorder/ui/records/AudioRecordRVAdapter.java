package com.duokhongg.audiorecorder.ui.records;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.duokhongg.audiorecorder.R;
import com.duokhongg.audiorecorder.data.model.AudioRecord;
import com.duokhongg.audiorecorder.data.repository.CategoryRepository;
import com.duokhongg.audiorecorder.ui.categories.CategoryViewModel;
import com.duokhongg.audiorecorder.utils.Helper;

public class AudioRecordRVAdapter extends ListAdapter<AudioRecord, AudioRecordRVAdapter.ViewHolder> {

    private OnItemClickListener listener;

    AudioRecordRVAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<AudioRecord> DIFF_CALLBACK =  new DiffUtil.ItemCallback<AudioRecord>() {
        @Override
        public boolean areItemsTheSame(@NonNull AudioRecord oldItem, @NonNull AudioRecord newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull AudioRecord oldItem, @NonNull AudioRecord newItem) {
            return oldItem.getCategoryId() == newItem.getCategoryId() &&
                    oldItem.getFileName().equals(newItem.getFileName()) &&
                    oldItem.getFilePath().equals(newItem.getFilePath()) &&
                    oldItem.getDuration().equals(newItem.getDuration()) &&
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
    public void onBindViewHolder(@NonNull AudioRecordRVAdapter.ViewHolder holder, int position) {
        AudioRecord record = getItem(position);
        holder.txtName.setText(record.getFileName());
        holder.txtDuration.setText(Helper.formatDuration(record.getDuration()));
        holder.txtLastModified.setText(Helper.formatLastModified(Long.parseLong(record.getTimeStamp())));
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
        void onItemClick(AudioRecord record);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
