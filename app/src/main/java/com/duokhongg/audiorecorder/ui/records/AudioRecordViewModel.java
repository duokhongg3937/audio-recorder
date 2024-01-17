package com.duokhongg.audiorecorder.ui.records;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.duokhongg.audiorecorder.data.AppDatabase;
import com.duokhongg.audiorecorder.data.dao.AudioRecordDao;
import com.duokhongg.audiorecorder.data.dao.AudioRecordDao_Impl;
import com.duokhongg.audiorecorder.data.model.AudioRecord;
import com.duokhongg.audiorecorder.data.model.RecordWithCategory;
import com.duokhongg.audiorecorder.data.repository.AudioRecordRepository;

import java.util.List;


public class AudioRecordViewModel extends AndroidViewModel {
    private AudioRecordRepository repository;
    private LiveData<List<AudioRecord>> allRecords;
    private LiveData<List<RecordWithCategory>> allRecordsWithCategory;

    public AudioRecordViewModel(@NonNull Application application) {
        super(application);
        repository = new AudioRecordRepository(application);
        allRecords = repository.getAllRecords();
        allRecordsWithCategory = repository.getAllRecordsWithCategory();
    }

    public void insert(AudioRecord record) {
        repository.insert(record);
    }
    public void delete(AudioRecord record) {
        repository.delete(record);
    }
    public void update(AudioRecord record) {
        repository.update(record);
    }
    public LiveData<List<AudioRecord>> getAllRecords() {
        return allRecords;
    }
    public LiveData<List<RecordWithCategory>> getAllRecordsWithCategory() {
        return allRecordsWithCategory;
    }

    public void deleteAllExpiredRecord() {
        repository.deleteAllExpiredRecord();
    }
}
