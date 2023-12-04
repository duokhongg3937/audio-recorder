package com.duokhongg.audiorecorder;

public class AudioRecord {
    private int id;
    private Category category;
    private String fileName;
    private String filePath;
    private String timeStamp;
    private String duration;

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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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
