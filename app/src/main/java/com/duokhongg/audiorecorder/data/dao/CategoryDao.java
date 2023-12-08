package com.duokhongg.audiorecorder.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.duokhongg.audiorecorder.data.model.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    long addCategory(Category category);

    @Query("SELECT * FROM categories WHERE id = :id")
    LiveData<Category> getCategoryById(int id);

    @Query("SELECT * FROM categories")
    LiveData<List<Category>> getAllCategories();

    @Delete
    void deleteCategory(Category category);
}
