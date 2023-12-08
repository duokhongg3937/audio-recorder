package com.duokhongg.audiorecorder;

import static android.graphics.Color.parseColor;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.duokhongg.audiorecorder.data.model.Category;
import com.duokhongg.audiorecorder.data.model.RecordWithCategory;
import com.duokhongg.audiorecorder.ui.categories.ColorButtonAdapter;
import com.duokhongg.audiorecorder.ui.records.AudioRecordViewModel;
import com.duokhongg.audiorecorder.utils.Helper;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.List;

public class RecordDetailActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    ExoPlayer exoPlayer;
    String filePath;
    ImageButton btnToggle, btnVolume;
    SeekBar seekBar;
    Slider volumeSlider;
    AudioManager audioManager;
    ImageButton btnEdit, btnDelete;
    TextView txtCurrentTime, txtEndTime;
    Handler handler = new Handler();
    final int delayMillis = 10;
    AudioRecordViewModel recordViewModel;
    RecordWithCategory record;
    Toolbar toolbar;
    boolean isPlaying = false;
    boolean isBound = false;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        startService(new Intent(getApplicationContext(), MusicPlayerService.class));
        doBindService();

        btnToggle = findViewById(R.id.btnToggle);
        btnVolume = findViewById(R.id.btnVolume);
        seekBar = findViewById(R.id.seekbar);
        volumeSlider = findViewById(R.id.sliderVolume);
        txtCurrentTime = findViewById(R.id.txtCurrentTime);
        txtEndTime = findViewById(R.id.txtEndTime);
        toolbar = findViewById(R.id.toolbar);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        filePath = getIntent().getStringExtra("file_path");
        String fileName = getIntent().getStringExtra("file_name");
        record = (RecordWithCategory) getIntent().getSerializableExtra("record");

        recordViewModel = new ViewModelProvider(this).get(AudioRecordViewModel.class);
        mediaPlayer = new MediaPlayer();

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeSlider.setValueTo(maxVolume);
        volumeSlider.setValue(currentVolume);

        if (currentVolume == 0) {
            btnVolume.setImageResource(R.drawable.ic_volume_off);
        } else {
            btnVolume.setImageResource(R.drawable.ic_volume_on);
        }



        volumeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) value, 0);
                if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
                    btnVolume.setImageResource(R.drawable.ic_volume_off);
                } else {
                    btnVolume.setImageResource(R.drawable.ic_volume_on);
                }
            }
        });


        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(fileName);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(parseColor("#C67C4E"));
        }

        seekBar.setMax(mediaPlayer.getDuration());
        txtEndTime.setText(Helper.formatDuration(String.valueOf(mediaPlayer.getDuration())));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateSeekBarTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateSeekBar();
            }
        });

        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mediaPlayer.isPlaying()) {
//                    mediaPlayer.pause();
//                    btnToggle.setImageResource(R.drawable.ic_play_big);
//                } else {
//                    mediaPlayer.start();
//                    btnToggle.setImageResource(R.drawable.ic_pause_big);
//                    updateSeekBar();
//                }

                if (isPlaying) {
                    exoPlayer.pause();
                    isPlaying = false;
                    btnToggle.setImageResource(R.drawable.ic_play_big);
                } else {
                    try {
                        exoPlayer.setMediaItem(new MediaItem.Builder()
                                .setUri(filePath)
                                .setMediaMetadata(new MediaMetadata.Builder().setTitle(fileName).build())
                                .build());
                        exoPlayer.prepare();
//            mediaPlayer.setDataSource(filePath);
//            mediaPlayer.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    exoPlayer.play();
                    isPlaying = true;
                    updateSeekBar();
                }

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnToggle.setImageResource(R.drawable.ic_play_big);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordViewModel.delete(Helper.RecordWithCategory2Record(record));
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.openEditRecordDialog(RecordDetailActivity.this, record, recordViewModel, Gravity.CENTER);
            }
        });

    }

    private Runnable updateSeekBarTask = new Runnable() {
        @Override
        public void run() {
            if (exoPlayer != null) {
                int position = (int) exoPlayer.getCurrentPosition();
                seekBar.setProgress(position);
                txtCurrentTime.setText(Helper.formatDuration(String.valueOf(position)));
                handler.postDelayed(this, delayMillis);
            }
        }
    };

    private void updateSeekBar() {
        handler.postDelayed(updateSeekBarTask, delayMillis);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mediaPlayer != null) {
//            mediaPlayer.release();
//            handler.removeCallbacks(updateSeekBarTask);
//        }
        doUnbindService();
    }

    private void doUnbindService() {
        if (isBound) {
            unbindService(playerServiceConnection);
            isBound = false;
        }
    }

    private void doBindService() {
        Intent playerServiceIntent = new Intent(this, MusicPlayerService.class);
        bindService(playerServiceIntent, playerServiceConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    ServiceConnection playerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayerService.ServiceBinder binder = (MusicPlayerService.ServiceBinder) service;
            exoPlayer = binder.getPlayerService().player;
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}