package com.duokhongg.audiorecorder.data.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.duokhongg.audiorecorder.data.AppDatabase;
import com.duokhongg.audiorecorder.data.dao.AudioRecordDao;
import com.duokhongg.audiorecorder.data.model.AudioRecord;
import com.duokhongg.audiorecorder.data.model.RecordWithCategory;

import java.io.File;
import java.util.List;

public class AudioRecordRepository {
    private AudioRecordDao audioRecordDao;
    private LiveData<List<AudioRecord>> allRecords;
    private LiveData<List<RecordWithCategory>> allRecordsWithCategory;

    public AudioRecordRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        audioRecordDao = db.getAudioRecordDao();
        allRecords = audioRecordDao.getAllRecords();
        allRecordsWithCategory = audioRecordDao.getAllRecordsWithCategory();
    }

    public void insert(AudioRecord record) {
        new InsertRecordAsyncTask(audioRecordDao).execute(record);
    }

    public void delete(AudioRecord record) {
        new DeleteRecordAsyncTask(audioRecordDao).execute(record);
    }

    public void update(AudioRecord record) {
        new UpdateRecordAsyncTask(audioRecordDao).execute(record);
    }

    public LiveData<List<AudioRecord>> getAllRecords() {
        return allRecords;
    }

    public LiveData<List<RecordWithCategory>> getAllRecordsWithCategory() {
        return allRecordsWithCategory;
    }

    public void deleteAllExpiredRecord() {
        audioRecordDao.deleteAllExpiredRecord();
    }

    private static class InsertRecordAsyncTask extends AsyncTask<AudioRecord, Void, Void> {
        private AudioRecordDao recordDao;

        private InsertRecordAsyncTask(AudioRecordDao recordDao) {
            this.recordDao = recordDao;
        }

        @Override
        protected Void doInBackground(AudioRecord... model) {
            recordDao.addRecord(model[0]);
            return null;
        }
    }

    private static class UpdateRecordAsyncTask extends AsyncTask<AudioRecord, Void, Void> {
        private AudioRecordDao recordDao;
        private UpdateRecordAsyncTask(AudioRecordDao recordDao) {
            this.recordDao = recordDao;
        }

        @Override
        protected Void doInBackground(AudioRecord... model) {
            File file = new File(model[0].getFilePath());
            File newFile = new File(file.getParent(), model[0].getFileName());
            file.renameTo(newFile);
            model[0].setFilePath(newFile.getPath());
            recordDao.updateRecord(model[0]);
            return null;
        }
    }

    private static class DeleteRecordAsyncTask extends AsyncTask<AudioRecord, Void, Void> {
        private AudioRecordDao recordDao;
        private DeleteRecordAsyncTask(AudioRecordDao recordDao) {
            this.recordDao = recordDao;
        }

        @Override
        protected Void doInBackground(AudioRecord... model) {
            recordDao.deleteRecord(model[0]);
            File file = new File(model[0].getFilePath());
            file.delete();
            return null;
        }
    }
}
