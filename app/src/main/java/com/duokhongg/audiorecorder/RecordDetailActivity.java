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


public class RecordDetailActivity extends AppCompatActivity implements AudioManager.OnAudioFocusChangeListener{
    private ActivityRecordDetailBinding binding;
    private ExoPlayer exoPlayer;
    private AudioManager audioManager;
    private Handler handler = new Handler();
    private final int delayMillis = 10;
    private AudioRecordViewModel recordViewModel;
    private RecordWithCategory record;
    private boolean isPlaying = false, isStarting = false, isBound = false;

    private ServiceConnection playerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioPlayerService.ServiceBinder binder = (AudioPlayerService.ServiceBinder) service;
            exoPlayer = binder.getPlayerService().player;
            isBound = true;
            exoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    if (playbackState == Player.STATE_ENDED) {
                        updateState(R.drawable.ic_play_big, false, false);
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                updateState(R.drawable.ic_pause_big, true, true);
                break;
        }
    }


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

        // view binding
        binding = ActivityRecordDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // bind notification service
        startService(new Intent(getApplicationContext(), AudioPlayerService.class));
        doBindService();

        // get record and recordViewModel
        record = (RecordWithCategory) getIntent().getSerializableExtra("record");
        recordViewModel = new ViewModelProvider(this).get(AudioRecordViewModel.class);

        // create ui
        setupUi();
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

    private void updateState(int image, boolean isPlaying, boolean isStarting) {
        binding.btnToggle.setImageResource(image);
        this.isPlaying = isPlaying;
        this.isStarting = isStarting;
    }

    private MediaItem buildMediaItem() {
        return new MediaItem.Builder()
                .setUri(record.getFilePath())
                .setMediaMetadata(new MediaMetadata.Builder().setTitle(record.getFileName()).build())
                .build();
    }

    private void setupUi() {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        // set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(parseColor("#C67C4E"));
        }

        // set actionbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(record.getFileName());

        // set volume
        binding.sliderVolume.setValueTo(maxVolume);
        binding.sliderVolume.setValue(currentVolume);
        binding.btnVolume.setImageResource(currentVolume == 0 ? R.drawable.ic_volume_off : R.drawable.ic_volume_on);

        // setup record seekbar
        binding.seekbar.setMax((int) Integer.parseInt(record.getDuration()));
        binding.txtEndTime.setText(Helper.formatDuration(record.getDuration()));



        // add listener to sliderVolume
        binding.sliderVolume.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) value, 0);
                binding.btnVolume.setImageResource(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0 ?
                        R.drawable.ic_volume_off : R.drawable.ic_volume_on);
            }
        });

        // add listener to seekbar
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
                } else
                {
                    if (!isStarting) {
                        isStarting = true;
                        try {
                            exoPlayer.setMediaItem(buildMediaItem());
                            exoPlayer.prepare();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    exoPlayer.setPlayWhenReady(true);
                    updateSeekBar();
                }

                if (!isPlaying) {
                    updateState(R.drawable.ic_pause_big, true, isStarting);
                } else {
                    updateState(R.drawable.ic_play_big, false, isStarting);
                }
            }
        });

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record.setTimeDelete(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
                record.setOldCategory(record.getCategoryId());
                record.setCategoryId(1);
                recordViewModel.update(Helper.RecordWithCategory2Record(record));
                onBackPressed();
                //recordViewModel.delete(Helper.RecordWithCategory2Record(record));
            }
        });

        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.openEditRecordDialog(RecordDetailActivity.this, record, recordViewModel, Gravity.CENTER);
                onBackPressed();
            }
        });
    }



}