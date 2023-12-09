package com.duokhongg.audiorecorder.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.duokhongg.audiorecorder.R;
import com.duokhongg.audiorecorder.data.model.AudioRecord;
import com.duokhongg.audiorecorder.data.model.Category;
import com.duokhongg.audiorecorder.data.model.RecordWithCategory;
import com.duokhongg.audiorecorder.ui.categories.CategoryViewModel;
import com.duokhongg.audiorecorder.ui.records.AudioRecordViewModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

public class Helper {
    public static String formatDuration(String durationInMillis) {
        long duration = Long.parseLong(durationInMillis);
        long millis = duration % 1000;
        long seconds = (duration / 1000) % 60;
        if (millis >= 500) {
            seconds++;
        }
        long minutes = (duration / (1000 * 60)) % 60;
        long hours = (duration / (1000 * 60 * 60));

        String formatted;
        if (hours > 0) {
            formatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            formatted = String.format("%02d:%02d", minutes, seconds);
        }

        return formatted;
    }

    public static String formatLastModified(long timeStamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        SimpleDateFormat dateToCompareFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date(timeStamp);

        String fullModifiedDate = dateFormat.format(date);
        String[] temp = fullModifiedDate.split(" ");


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate currentDate = LocalDate.now();
            try {
                LocalDate modifiedDate = LocalDate.parse(dateToCompareFormat.format(date));
                if (modifiedDate.equals(currentDate)) {
                    return "Hôm nay, " + temp[1];
                } else {
                    return fullModifiedDate;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "";
    }

    public static AudioRecord RecordWithCategory2Record(RecordWithCategory record) {
        AudioRecord rec = new AudioRecord();

        rec.setId(record.getId());
        rec.setCategoryId(record.getCategoryId());
        rec.setFileName(record.getFileName());
        rec.setFilePath(record.getFilePath());
        rec.setTimeStamp(record.getTimeStamp());
        rec.setDuration(record.getDuration());

        return rec;

    }

    public static AudioRecord File2AudioRecord(File file) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        String name = file.getName();
        long lastModified = file.lastModified();
        String path = file.getPath();

        retriever.setDataSource(path);
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        return new AudioRecord(name, path, String.valueOf(lastModified), duration);
    }


    public static void openEditRecordDialog(Context context, RecordWithCategory record, AudioRecordViewModel recordViewModel, int gravity) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_edit_record);

        Window window = dialog.getWindow();
        if (window == null) return;

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttr = window.getAttributes();
        windowAttr.gravity = gravity;
        window.setAttributes(windowAttr);

        dialog.setCancelable(true);

        Button btnClose = dialog.findViewById(R.id.btnCloseDialog);
        Button btnSave = dialog.findViewById(R.id.btnSaveDialog);
        EditText edtRecordName = dialog.findViewById(R.id.edtRecordName);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtRecordName.getText().toString();
                record.setFileName(name + ".mp3");
                recordViewModel.update(Helper.RecordWithCategory2Record(record));
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public static void openEditCategoryDialog(Context context, Category category, CategoryViewModel categoryViewModel, int gravity) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_category);

        Window window = dialog.getWindow();
        if (window == null) return;

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttr = window.getAttributes();
        windowAttr.gravity = gravity;
        window.setAttributes(windowAttr);

        dialog.setCancelable(true);

        Button btnClose = dialog.findViewById(R.id.btnCloseDialog);
        Button btnSave = dialog.findViewById(R.id.btnSaveDialog);
        EditText edtCategoryName = dialog.findViewById(R.id.edtCategoryName);
        TextView txtPopupCategoryTitle = dialog.findViewById(R.id.txtPopupCategoryTitle);

        edtCategoryName.setText(category.getCategoryName());
        txtPopupCategoryTitle.setText("EDIT CATEGORY");
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtCategoryName.getText().toString();
                category.setCategoryName(name);
                categoryViewModel.update(category);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}

