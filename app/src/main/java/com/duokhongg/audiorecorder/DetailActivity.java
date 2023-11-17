package com.duokhongg.audiorecorder;

import static android.graphics.Color.parseColor;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    String filePath;
    Button btnPlay;
    Button btnPause;
    SeekBar seekBar;
    TextView txtCurrentTime, txtEndTime;
    Handler handler = new Handler();
    final int delayMillis = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        filePath = getIntent().getStringExtra("file_path");
        mediaPlayer = new MediaPlayer();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(parseColor("#bc8953"));
        }

        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnPlay = findViewById(R.id.btnPlayDetail);
        btnPause = findViewById(R.id.btnPause);
        seekBar = findViewById(R.id.seekbar);
        txtCurrentTime = findViewById(R.id.txtCurrentTime);
        txtEndTime = findViewById(R.id.txtEndTime);

        seekBar.setMax(mediaPlayer.getDuration());
        txtEndTime.setText(Helper.formatDuration(String.valueOf(mediaPlayer.getDuration())));

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
                updateSeekBar();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Stop updating the SeekBar progress when the user starts dragging
                handler.removeCallbacks(updateSeekBarTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Resume updating the SeekBar progress when the user stops dragging
                updateSeekBar();
            }
        });


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
        });
    }

    private Runnable updateSeekBarTask = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {
                int position = mediaPlayer.getCurrentPosition();
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
        if (mediaPlayer != null) {
            mediaPlayer.release();
            handler.removeCallbacks(updateSeekBarTask);
        }
    }
}