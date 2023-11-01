package com.duokhongg.audiorecorder;

import android.content.ContextWrapper;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.IOException;

public class HomeFragment extends Fragment {

    Handler timeHandler = new Handler();
    Runnable timerRunnable;
    long recordingStartTime = 0;

    MaterialButton btnRecord;
    Button btnPlay, btnSave;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    EditText edtTitle;
    TextView txtFilePath, txtRecordingTime;
    boolean isRecording = false;
    boolean isTimerPaused = true;

    private String getRecordingFilePath(String fileTitle) {
        String title = fileTitle != null ? fileTitle : "testRecordingFile";
        ContextWrapper contextWrapper = new ContextWrapper(requireActivity().getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, title + ".mp3");
        return file.getPath();
    }

    private void startTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isTimerPaused) {
                    long elapsedMillis = SystemClock.elapsedRealtime() - recordingStartTime;
                    updateRecordingTime(elapsedMillis);
                }
                timeHandler.postDelayed(this, 1000);
            }
        };
        timeHandler.post(timerRunnable);
    }

    private void updateRecordingTime(long elapsedMillis) {
        long elapsedSeconds = elapsedMillis / 1000;
        long hours = elapsedSeconds / 3600;
        long minutes = (elapsedSeconds % 3600) / 60;
        long seconds = elapsedSeconds % 60;
        String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        txtRecordingTime.setText(formattedTime);
    }

    private void startRecording() throws IOException {
        try {
            isTimerPaused = false;
            mediaRecorder = new MediaRecorder();

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(getRecordingFilePath(edtTitle.getText().toString()));
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();

            mediaRecorder.start();

            btnRecord.setIconResource(R.drawable.square);
            isRecording = true;
            txtFilePath.setText(getRecordingFilePath(edtTitle.getText().toString()));
        } catch(Exception e) {
            e.printStackTrace();
        }

        // start timer
        recordingStartTime = SystemClock.elapsedRealtime();
        startTimer();

        Toast.makeText(requireActivity(), "Recording is started", Toast.LENGTH_SHORT).show();
    }

    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        btnRecord.setIconResource(R.drawable.mic);

        // stop timer
        timeHandler.removeCallbacks(timerRunnable);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnRecord = (MaterialButton) requireView().findViewById(R.id.btnRecord);
        btnPlay = (Button) requireView().findViewById(R.id.btnPlay);
        btnSave = (Button) requireView().findViewById(R.id.btnSave);
        edtTitle = (EditText) requireView().findViewById(R.id.edtTitle);
        txtFilePath = (TextView) requireView().findViewById(R.id.txtFilePath);
        txtRecordingTime = (TextView) requireView().findViewById(R.id.txtRecordingTime);

        btnPlay.setEnabled(false);


        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording) {
                    mediaRecorder.pause();

                    isRecording = false;
                    isTimerPaused = true;
                    btnRecord.setIconResource(R.drawable.mic);

                    Toast.makeText(requireActivity(), "Recording is stopped", Toast.LENGTH_SHORT).show();
                } else if (mediaRecorder != null) {
                    mediaRecorder.resume();

                    btnRecord.setIconResource(R.drawable.square);
                    isTimerPaused = false;
                    isRecording = true;
                } else {
                    try {
                        startRecording();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(getRecordingFilePath(edtTitle.getText().toString()));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Toast.makeText(requireActivity(), "Recording is playing", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaRecorder != null) {
                    stopRecording();
                    Toast.makeText(requireActivity(), "Recording is saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireActivity(), "Nothing to save", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mediaRecorder != null && isRecording) {
//            stopRecording();
//        }
//    }
}