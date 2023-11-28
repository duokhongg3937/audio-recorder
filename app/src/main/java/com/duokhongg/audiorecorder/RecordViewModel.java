package com.duokhongg.audiorecorder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class RecordViewModel extends ViewModel {
    private MutableLiveData<List<AudioRecord>> recordList = new MutableLiveData<>();

    public LiveData<List<AudioRecord>> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<AudioRecord> recordList) {
        this.recordList.setValue(recordList);
    }
}
