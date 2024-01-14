package com.duokhongg.audiorecorder.data.model;

import androidx.room.ColumnInfo;

import java.io.Serializable;

public class RecordWithCategory implements Serializable {
    public int id;

    @ColumnInfo(name = "category_id")
    public int categoryId;

    @ColumnInfo(name = "file_name")
    public String fileName;

    @ColumnInfo(name = "file_path")
    public String filePath;

    @ColumnInfo(name = "time_stamp")
    public String timeStamp;
    @ColumnInfo(name = "time_delete")
    public String timeDelete;
    public String duration;
    @ColumnInfo(name = "category_name")
    public String categoryName;
    @ColumnInfo(name = "category_color")
    public int categoryColor;
    @ColumnInfo(name = "old_category")
    public int oldCategory;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTimeDelete() {
        return timeDelete;
    }

    public void setTimeDelete(String timeDelete) {
        this.timeStamp = timeDelete;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(int categoryColor) {
        this.categoryColor = categoryColor;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getOldCategory() { return oldCategory; }
    public void setOldCategory(int oldCategory) { this.oldCategory = oldCategory; }
}
