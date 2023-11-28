package com.duokhongg.audiorecorder;

import static android.graphics.Color.parseColor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.duokhongg.audiorecorder.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MainCallbacks {

    BottomNavigationView bottomNavigationView;
    static int MICROPHONE_PERMISSION_CODE = 200;
    FragmentManager fragmentManager;
    RecordsFragment recordsFragment;
    CategoryFragment categoryFragment;
    HomeFragment homeFragment;
    private ActivityMainBinding binding;
    private RecordViewModel recordViewModel;
    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        bottomNavigationView = binding.navigationView;
        recordViewModel = new ViewModelProvider(this).get(RecordViewModel.class);

        List<AudioRecord> recordList = db.getAllRecords();
        recordViewModel.setRecordList(recordList);

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
            window.setStatusBarColor(parseColor("#bc8953"));
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

    private List<File> getAllRecordsInDirectory() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);

        if (musicDirectory != null && musicDirectory.exists() && musicDirectory.isDirectory()) {
            File[] files = musicDirectory.listFiles();
            if (files != null) {
                return Arrays.asList(files);
            }
        }

        return new ArrayList<>();
    }
}