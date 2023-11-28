package com.duokhongg.audiorecorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "RecordsManager";
    private static final String TABLE_RECORDS = "records";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createRecordsTableQuery = "CREATE TABLE " + TABLE_RECORDS + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "file_name TEXT, " +
                "file_path TEXT, " +
                "time_stamp TEXT, " +
                "duration TEXT);";

        db.execSQL(createRecordsTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        onCreate(db);
    }

    int addRecord(AudioRecord record) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("file_name", record.fileName);
        values.put("file_path", record.filePath);
        values.put("time_stamp", record.timeStamp);
        values.put("duration", record.duration);

        int id = (int) db.insert(TABLE_RECORDS, null, values);
        db.close();

        return id;
    }

    AudioRecord getRecordById(int id) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.query(TABLE_RECORDS, new String[] {"id", "file_name", "file_path", "time_stamp", "duration"},
                "id = ?", new String[] {String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        AudioRecord record = new AudioRecord(cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getString(4));

        return record;
    }

    public List<AudioRecord> getAllRecords() {
        List<AudioRecord> result = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECORDS, null);

        if (cursor.moveToFirst()) {
            do {
                AudioRecord record = new AudioRecord();
                record.id = cursor.getInt(0);
                record.fileName = cursor.getString(1);
                record.filePath = cursor.getString(2);
                record.timeStamp = cursor.getString(3);
                record.duration = cursor.getString(4);

                result.add(record);
            } while (cursor.moveToNext());
        }

        return result;
    }


    public void deleteRecord(AudioRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECORDS, "id = ?", new String[]{String.valueOf(record.id)});
        db.close();
    }
}
