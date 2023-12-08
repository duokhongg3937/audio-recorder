package com.duokhongg.audiorecorder.data;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;
import android.os.AsyncTask;

import com.duokhongg.audiorecorder.data.dao.AudioRecordDao;
import com.duokhongg.audiorecorder.data.dao.CategoryDao;
import com.duokhongg.audiorecorder.data.model.AudioRecord;
import com.duokhongg.audiorecorder.data.model.Category;

@Database(entities = {AudioRecord.class, Category.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AudioRecordDao getAudioRecordDao();
    public abstract CategoryDao getCategoryDao();

    private static volatile AppDatabase INSTANCE;
    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "records_manager")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return INSTANCE;
    }

    private static Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(INSTANCE).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void ,Void> {

        PopulateDbAsyncTask(AppDatabase instance) {
            AudioRecordDao audioRecordDao = instance.getAudioRecordDao();
            CategoryDao categoryDao = instance.getCategoryDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

    }
}
