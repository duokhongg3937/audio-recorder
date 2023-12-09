package com.duokhongg.audiorecorder.ui.categories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.duokhongg.audiorecorder.data.model.Category;
import com.duokhongg.audiorecorder.data.repository.CategoryRepository;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private CategoryRepository repository;
    private LiveData<List<Category>> allCategories;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        repository = new CategoryRepository(application);
        allCategories = repository.getAllCategories();
    }

    public void insert(Category category) {
        repository.insert(category);
    }

    public void delete(Category category) {
        repository.delete(category);
    }

    public void update(Category category) {
        repository.update(category);
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }
}
