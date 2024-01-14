package com.duokhongg.audiorecorder;

import static android.graphics.Color.parseColor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.duokhongg.audiorecorder.data.model.RecordWithCategory;
import com.duokhongg.audiorecorder.databinding.ActivityRecordDetailBinding;
import com.duokhongg.audiorecorder.ui.categories.CategoryViewModel;
import com.duokhongg.audiorecorder.ui.records.AudioRecordViewModel;
import com.duokhongg.audiorecorder.utils.Helper;
import com.google.android.material.slider.Slider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class RecordDetailActivity extends AppCompatActivity {
    ActivityRecordDetailBinding binding;
    ExoPlayer exoPlayer;
    String filePath;
    AudioManager audioManager;
    Handler handler = new Handler();
    final int delayMillis = 10;
    AudioRecordViewModel recordViewModel;
    RecordWithCategory record;
    boolean isPlaying = false, isStarting = false, isBound = false;

    ServiceConnection playerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioPlayerService.ServiceBinder binder = (AudioPlayerService.ServiceBinder) service;
            exoPlayer = binder.getPlayerService().player;
            isBound = true;
            exoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    if (playbackState == Player.STATE_ENDED) {
                        binding.btnToggle.setImageResource(R.drawable.ic_play_big);
                        isPlaying = false;
                        isStarting = false;
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


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
        binding = ActivityRecordDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        startService(new Intent(getApplicationContext(), AudioPlayerService.class));
        doBindService();

        filePath = getIntent().getStringExtra("file_path");
        String fileName = getIntent().getStringExtra("file_name");
        record = (RecordWithCategory) getIntent().getSerializableExtra("record");

        recordViewModel = new ViewModelProvider(this).get(AudioRecordViewModel.class);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        binding.sliderVolume.setValueTo(maxVolume);
        binding.sliderVolume.setValue(currentVolume);

        if (currentVolume == 0) {
            binding.btnVolume.setImageResource(R.drawable.ic_volume_off);
        } else {
            binding.btnVolume.setImageResource(R.drawable.ic_volume_on);
        }


        binding.sliderVolume.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) value, 0);
                if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
                    binding.btnVolume.setImageResource(R.drawable.ic_volume_off);
                } else {
                    binding.btnVolume.setImageResource(R.drawable.ic_volume_on);
                }
            }
        });


        setSupportActionBar(binding.toolbar);

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

        binding.seekbar.setMax((int) Integer.parseInt(record.getDuration()));
        binding.txtEndTime.setText(Helper.formatDuration(record.getDuration()));


        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    exoPlayer.seekTo(progress);
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

        binding.btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isPlaying) {
                    exoPlayer.pause();
                    isPlaying = false;
                    binding.btnToggle.setImageResource(R.drawable.ic_play_big);
                } else if (!isStarting) {
                    isStarting = true;
                    try {
                        exoPlayer.setMediaItem(new MediaItem.Builder()
                                .setUri(filePath)
                                .setMediaMetadata(new MediaMetadata.Builder().setTitle(fileName).build())
                                .build());
                        exoPlayer.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    exoPlayer.setPlayWhenReady(true);
                    binding.btnToggle.setImageResource(R.drawable.ic_pause_big);
                    isPlaying = true;
                    updateSeekBar();
                } else {
                    exoPlayer.setPlayWhenReady(true);
                    isPlaying = true;
                    binding.btnToggle.setImageResource(R.drawable.ic_pause_big);
                }

            }
        });

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record.setTimeDelete(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(new Date()));
                record.setOldCategory(record.getCategoryId());
                record.setCategoryId(0);
                recordViewModel.update(Helper.RecordWithCategory2Record(record));
                //recordViewModel.delete(Helper.RecordWithCategory2Record(record));
            }
        });

        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
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
                binding.seekbar.setProgress(position);
                binding.txtCurrentTime.setText(Helper.formatDuration(String.valueOf(position)));
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
        doUnbindService();
    }

    private void doUnbindService() {
        if (isBound) {
            unbindService(playerServiceConnection);
            isBound = false;
        }
    }

    private void doBindService() {
        Intent playerServiceIntent = new Intent(this, AudioPlayerService.class);
        bindService(playerServiceIntent, playerServiceConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

}