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
    private static final String TABLE_CATEGORY = "category";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createCategoryTable(db);
        createRecordTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        onCreate(db);
    }

    /**
     *
     * @param record record needs to be added
     * @return record id if succeed, -1 if an error occurred
     */
    public int addRecord(AudioRecord record) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("file_name", record.getFileName());
        values.put("file_path", record.getFilePath());
        values.put("time_stamp", record.getTimeStamp());
        values.put("duration", record.getDuration());
        values.put("category_id", record.getCategory().getId());

        int id = (int) db.insert(TABLE_RECORDS, null, values);
        db.close();

        return id;
    }

    /**
     *
     * @param category category needs to be added
     * @return category id if succeed, -1 if an error occurred
     */
    public int addCategory(Category category) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("category_name", category.getCategoryName());
        values.put("color", category.getColor());

        int id = (int) db.insert(TABLE_CATEGORY, null, values);
        db.close();

        return id;
    }


    public AudioRecord getRecordById(int id) {
        AudioRecord result = new AudioRecord();
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_RECORDS + " JOIN " + TABLE_CATEGORY
                + " ON " + TABLE_CATEGORY + ".id = " + TABLE_RECORDS  + ".category_id WHERE " + TABLE_RECORDS + ".id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            int recId = cursor.getInt(0);
            String fileName = cursor.getString(2);
            String filePath = cursor.getString(3);
            String timeStamp = cursor.getString(4);
            String duration = cursor.getString(5);
            String categoryName = cursor.getString(7);
            int categoryColor = cursor.getInt(8);

            AudioRecord record = new AudioRecord(fileName, filePath, timeStamp, duration);
            record.setId(id);
            record.setCategory(new Category(categoryName, categoryColor));

            result = record;
        }

        cursor.close();
        db.close();
        return result;
    }

    public Category getCategoryById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORY, new String[] {"id", "category_name", "color"},
                "id = ?", new String[] {String.valueOf(id)}, null, null, null, null);

        if (cursor == null) {
            return null;
        } else {
            cursor.moveToFirst();
        }

        String name = cursor.getString(1);
        int color = cursor.getInt(2);

        cursor.close();
        db.close();

        Category category = new Category(name, color);
        category.setId(id);

        return category;
    }

    public List<AudioRecord> getAllRecords() {
        List<AudioRecord> result = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECORDS + " JOIN " + TABLE_CATEGORY
                + " ON " + TABLE_CATEGORY + ".id = " + TABLE_RECORDS  + ".category_id", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String fileName = cursor.getString(2);
                String filePath = cursor.getString(3);
                String timeStamp = cursor.getString(4);
                String duration = cursor.getString(5);
                String categoryName = cursor.getString(7);
                int categoryColor = cursor.getInt(8);

                AudioRecord record = new AudioRecord(fileName, filePath, timeStamp, duration);
                record.setId(id);
                record.setCategory(new Category(categoryName, categoryColor));

                result.add(record);
            } while (cursor.moveToNext());
        }

        db.close();
        return result;
    }

    public List<Category> getAllCategory() {
        List<Category> result = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CATEGORY , null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int color = cursor.getInt(2);

                Category category = new Category(name, color);
                category.setId(id);

                result.add(category);
            } while (cursor.moveToNext());
        }

        db.close();
        return result;
    }

    public void deleteRecord(AudioRecord record) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_RECORDS, "id = ?", new String[]{String.valueOf(record.getId())});
        db.close();
    }

    public void deleteCategory(Category category) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CATEGORY, "id = ?", new String[]{String.valueOf(category.getId())});
        db.close();
    }

    public int deleteRecordsByCategoryId(int categoryId) {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(TABLE_RECORDS, "category_id = ?", new String[]{String.valueOf(categoryId)});

        db.close();

        return result;
    }

    private void createRecordTable(SQLiteDatabase db) {
        String createRecordsTableQuery = "CREATE TABLE " + TABLE_RECORDS + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "category_id INTEGER, "  +
                "file_name TEXT, " +
                "file_path TEXT, " +
                "time_stamp TEXT, " +
                "duration TEXT," +
                "FOREIGN KEY (category_id) REFERENCES category(id))";
        db.execSQL(createRecordsTableQuery);
    }

    private void createCategoryTable(SQLiteDatabase db) {
        String createCategoryTableQuery = "CREATE TABLE " + TABLE_CATEGORY + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "category_name TEXT, " +
                "color INTEGER)";

        db.execSQL(createCategoryTableQuery);
    }
}
