package com.duokhongg.audiorecorder.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "category_name")
    String categoryName;

    @ColumnInfo(name = "category_color")
    int color;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Category(String categoryName, int color) {
        this.categoryName = categoryName;
        this.color = color;
    }

    @Override
    public String toString() {
        return categoryName;
    }
}
