package com.duokhongg.audiorecorder;

public class Category {
    int id;
    String categoryName;
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
