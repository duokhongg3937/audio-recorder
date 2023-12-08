package com.duokhongg.audiorecorder;

import static android.graphics.Color.parseColor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.duokhongg.audiorecorder.databinding.ActivityMainBinding;
import com.duokhongg.audiorecorder.ui.categories.CategoryFragment;
import com.duokhongg.audiorecorder.ui.categories.CategoryViewModel;
import com.duokhongg.audiorecorder.ui.home.HomeFragment;
import com.duokhongg.audiorecorder.ui.records.AudioRecordViewModel;
import com.duokhongg.audiorecorder.ui.records.RecordsFragment;
import com.duokhongg.audiorecorder.utils.MainCallbacks;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity implements MainCallbacks {

    BottomNavigationView bottomNavigationView;
    static int MICROPHONE_PERMISSION_CODE = 200;
    FragmentManager fragmentManager;
    RecordsFragment recordsFragment;
    CategoryFragment categoryFragment;
    HomeFragment homeFragment;
    private ActivityMainBinding binding;
    private AudioRecordViewModel recordViewModel;
    private CategoryViewModel categoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        bottomNavigationView = binding.navigationView;
        recordViewModel = new ViewModelProvider(this).get(AudioRecordViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        fragmentManager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        recordsFragment = new RecordsFragment();
        categoryFragment = new CategoryFragment();
        replaceFragment(homeFragment);

        if (isMicPresent()) {
            getMicPermission();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(parseColor("#C67C4E"));
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(homeFragment);
            } else if (item.getItemId() == R.id.record) {
                replaceFragment(recordsFragment);
            } else {
                replaceFragment(categoryFragment);
            }
            return true;
        });


    }


    private boolean isMicPresent() {
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    private void getMicPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE);
        }
    }


    private void replaceFragment(Fragment fragment) {
        fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commitNow();
    }


    @Override
    public void onMessageFromFragmentToMain(String sender, String message) {
    }
}