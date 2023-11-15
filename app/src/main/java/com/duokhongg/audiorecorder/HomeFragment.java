package com.duokhongg.audiorecorder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
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
import java.util.Objects;

public class HomeFragment extends Fragment implements FragmentCallbacks, Timer.OnTimerClickListener {
    AppCompatButton btnRecord;
    Button btnPlay, btnSave;
    MediaRecorder mediaRecorder;
    EditText edtTitle;
    TextView txtFilePath, txtRecordingTime;
    boolean isRecording = false;
    boolean isPaused = true;
    Timer timer;
    String filePath = "";
    String recordingTime = "";


    private String getRecordingFilePath(String fileTitle) {
        String title = fileTitle != null ? fileTitle : "testRecordingFile";
        ContextWrapper contextWrapper = new ContextWrapper(requireActivity().getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, title + ".mp3");
        return file.getPath();
    }

    private void startRecording() throws IOException {
        try {
            isRecording = true;
            isPaused = false;
            mediaRecorder = new MediaRecorder();

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(getRecordingFilePath(edtTitle.getText().toString()));
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();

            mediaRecorder.start();

            btnRecord.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(requireContext(), R.drawable.square), null, null);
            txtFilePath.setText(getRecordingFilePath(edtTitle.getText().toString()));

            // start timer
            timer.start();

            Toast.makeText(requireActivity(), "Recording is started", Toast.LENGTH_SHORT).show();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        btnRecord.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(requireContext(), R.drawable.mic), null, null);

        timer.stop();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnRecord = (AppCompatButton) requireView().findViewById(R.id.btnRecord);
        btnPlay = (Button) requireView().findViewById(R.id.btnPlay);
        btnSave = (Button) requireView().findViewById(R.id.btnSave);
        edtTitle = (EditText) requireView().findViewById(R.id.edtTitle);
        txtFilePath = (TextView) requireView().findViewById(R.id.txtFilePath);
        txtRecordingTime = (TextView) requireView().findViewById(R.id.txtRecordingTime);

        btnSave.setBackgroundResource(R.drawable.button);
        btnPlay.setEnabled(false);

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording) {
                    mediaRecorder.pause();
                    isRecording = false;
                    isPaused = true;
                    timer.pause();
                    btnRecord.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(requireContext(), R.drawable.mic), null, null);
                    Toast.makeText(requireActivity(), "Recording is stopped", Toast.LENGTH_SHORT).show();
                } else if (mediaRecorder != null) {
                    mediaRecorder.resume();
                    timer.start();
                    txtFilePath.setText(getRecordingFilePath(edtTitle.getText().toString()));
                    btnRecord.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(requireContext(), R.drawable.square), null, null);
                    isPaused = false;
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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaRecorder != null) {
                    stopRecording();
                    txtFilePath.setText("");
                    txtRecordingTime.setText("");
                    edtTitle.setText("");
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
        timer = new Timer();
        timer.setOnTimerCreateListener(this);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onMessageFromMainToFragment(String message) {
    }

    @Override
    public void onTimerTick(String value) {
        txtRecordingTime.setText(value);
    }

    @Override
    public void onPause() {
        super.onPause();
        filePath = txtFilePath.getText().toString();
        recordingTime = txtRecordingTime.getText().toString();
    }

    @Override
    public void onResume() {
        super.onResume();

        txtFilePath.setText(filePath);
        txtRecordingTime.setText(recordingTime);
    }
}