package com.duokhongg.audiorecorder.data.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.duokhongg.audiorecorder.data.AppDatabase;
import com.duokhongg.audiorecorder.data.dao.CategoryDao;
import com.duokhongg.audiorecorder.data.model.Category;

import java.util.List;

public class CategoryRepository {
    private CategoryDao categoryDao;
    private LiveData<List<Category>> allCategories;

    public CategoryRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        categoryDao = db.getCategoryDao();
        allCategories = categoryDao.getAllCategories();
    }

    public void insert(Category category) {
        new InsertCategoryAsyncTask(categoryDao).execute(category);
    }

    public void delete(Category category) {
        new DeleteCategoryAsyncTask(categoryDao).execute(category);
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public LiveData<Category> getCategoryById(int id) {
        return categoryDao.getCategoryById(id);
    }

    private static class InsertCategoryAsyncTask extends AsyncTask<Category, Void, Void> {
        private CategoryDao categoryDao;

        private InsertCategoryAsyncTask(CategoryDao categoryDao) {
            this.categoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... model) {
            categoryDao.addCategory(model[0]);
            return null;
        }
    }

    private static class DeleteCategoryAsyncTask extends AsyncTask<Category, Void, Void> {
        private CategoryDao categoryDao;

        private DeleteCategoryAsyncTask(CategoryDao categoryDao) {
            this.categoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... model) {
            categoryDao.deleteCategory(model[0]);
            return null;
        }
    }

}
