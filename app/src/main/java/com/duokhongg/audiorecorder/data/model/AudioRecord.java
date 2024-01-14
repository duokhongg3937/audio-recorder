package com.duokhongg.audiorecorder.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "records")
public class AudioRecord implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ForeignKey(entity = Category.class, parentColumns = "category_id", childColumns = "id")
    @ColumnInfo(name = "category_id")
    private int categoryId;

    @ColumnInfo(name = "file_name")
    private String fileName;

    @ColumnInfo(name = "file_path")
    private String filePath;

    @ColumnInfo(name = "time_stamp")
    private String timeStamp;

    @ColumnInfo(name = "time_delete")
    public String timeDelete;

    @ColumnInfo(name = "old_category")
    public int oldCategory;
    @ColumnInfo(name = "duration")
    private String duration;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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
        this.timeDelete = timeDelete;
    }

    public int getOldCategory() {
        return oldCategory;
    }

    public void setOldCategory(int oldCategory) {
        this.oldCategory = oldCategory;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public AudioRecord(String fileName, String filePath, String timeStamp, String duration) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.timeStamp = timeStamp;
        this.duration = duration;
    }

    public AudioRecord() {

    }
}
