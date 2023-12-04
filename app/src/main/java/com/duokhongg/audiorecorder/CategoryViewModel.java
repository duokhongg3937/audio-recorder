package com.duokhongg.audiorecorder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class CategoryViewModel extends ViewModel {
    private MutableLiveData<List<Category>> categoryList = new MutableLiveData<>();

    public LiveData<List<Category>> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList.setValue(categoryList);
    }
}
