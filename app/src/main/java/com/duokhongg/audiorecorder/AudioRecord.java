package com.duokhongg.audiorecorder;

public class AudioRecord {
    String fileName;
    String filePath;
    String timeStamp;
    String duration;

    public AudioRecord(String fileName, String filePath, String timeStamp, String duration) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.timeStamp = timeStamp;
        this.duration = duration;
    }
}
