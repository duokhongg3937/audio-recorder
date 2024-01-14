package com.duokhongg.audiorecorder.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.duokhongg.audiorecorder.data.model.AudioRecord;
import com.duokhongg.audiorecorder.data.model.RecordWithCategory;

import java.util.List;

@Dao
public interface AudioRecordDao {
    @Insert
    long addRecord(AudioRecord record);

    @Query("SELECT * FROM records WHERE id = :id")
    LiveData<AudioRecord> getRecordById(int id);

    @Query("SELECT * FROM records")
    LiveData<List<AudioRecord>> getAllRecords();

    @Query("SELECT records.id, records.category_id, records.file_name, records.file_path, " +
            "records.time_stamp, records.duration, categories.category_name, " +
            "categories.category_color, records.old_category FROM records LEFT JOIN categories ON records.category_id = categories.id")
    LiveData<List<RecordWithCategory>> getAllRecordsWithCategory();

    @Query("DELETE FROM records WHERE strftime('%d', 'now') - strftime('%d', records.time_delete) = 7  AND records.category_id = 0")
    void deleteAllExpiredRecord();

    @Delete
    void deleteRecord(AudioRecord record);

    @Update
    void updateRecord(AudioRecord record);


}
