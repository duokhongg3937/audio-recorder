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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duokhongg.audiorecorder.databinding.ActivityMainBinding;
import com.duokhongg.audiorecorder.databinding.BottomSheetBinding;
import com.duokhongg.audiorecorder.databinding.FragmentHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class HomeFragment extends Fragment implements FragmentCallbacks, Timer.OnTimerClickListener {
    private FragmentHomeBinding homeBinding;
    Button btnCancel, btnOk;
    MediaRecorder mediaRecorder;
    EditText fileNameInput;
    boolean isRecording = false;
    boolean isPaused = true;
    Timer timer;
    String filePath = "";
    String recordingTime = "";
    BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    String currentRecordingFilePath;
    String currentFileName;
    BottomNavigationView bottomNavigationView;

    public HomeFragment() {
    }


    private String generateOutputFilePath() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(new Date());
        ContextWrapper contextWrapper = new ContextWrapper(requireActivity().getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, "record-" + timeStamp + ".mp3");
        return file.getPath();
    }

    private String generateOutputFilePath(String fileName) {
        ContextWrapper contextWrapper = new ContextWrapper(requireActivity().getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, fileName + ".mp3");
        return file.getPath();
    }

    private String getFileName(String filePath) {
        String[] temp = filePath.split("/");
        String[] fullFileName = temp[temp.length - 1].split("\\.");
        return fullFileName[0];
    }

    private void startRecording() throws IOException {
        try {
            isRecording = true;
            isPaused = false;
            mediaRecorder = new MediaRecorder();
            currentRecordingFilePath = generateOutputFilePath();

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(currentRecordingFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();

            homeBinding.btnRecord.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(requireContext(), R.drawable.square), null, null);

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

        homeBinding.btnRecord.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(requireContext(), R.drawable.mic), null, null);

        timer.stop();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnCancel = requireView().findViewById(R.id.btnCancel);
        btnOk = requireView().findViewById(R.id.btnOk);
        fileNameInput = (EditText) requireView().findViewById(R.id.fileNameInput);
        bottomNavigationView = requireActivity().findViewById(R.id.navigationView);

        bottomSheetBehavior = BottomSheetBehavior.from(requireView().findViewById(R.id.bottomSheet));
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        homeBinding.btnSave.setBackgroundResource(R.drawable.button);
        homeBinding.btnPlay.setEnabled(false);

        homeBinding.btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording) {
                    mediaRecorder.pause();
                    isRecording = false;
                    isPaused = true;
                    timer.pause();
                    homeBinding.btnRecord.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(requireContext(), R.drawable.mic), null, null);
                    Toast.makeText(requireActivity(), "Recording is stopped", Toast.LENGTH_SHORT).show();
                } else if (mediaRecorder != null) {
                    mediaRecorder.resume();
                    timer.start();
                    homeBinding.btnRecord.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(requireContext(), R.drawable.square), null, null);
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

        homeBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaRecorder != null) {
                    stopRecording();
                    homeBinding.txtFilePath.setText("");
                    homeBinding.txtRecordingTime.setText("");
                    currentFileName = getFileName(currentRecordingFilePath);

                    bottomNavigationView.setVisibility(View.GONE);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                    homeBinding.bottomSheetBG.setVisibility(View.VISIBLE);
                    fileNameInput.setText(currentFileName);

                    Toast.makeText(requireActivity(), "Recording is saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireActivity(), "Nothing to save", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File currentFile = new File(currentRecordingFilePath);
                currentFile.delete();

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                homeBinding.bottomSheetBG.setVisibility(View.GONE);
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFileName = fileNameInput.getText().toString();
                if (!newFileName.equals(currentFileName)) {
                    File currentFile = new File(currentRecordingFilePath);
                    File newFile = new File(generateOutputFilePath(newFileName));
                    if (currentFile.renameTo(newFile)) {
                        Toast.makeText(requireContext(), "Rename successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Rename failed!", Toast.LENGTH_SHORT).show();
                    }
                }

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                homeBinding.bottomSheetBG.setVisibility(View.GONE);
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        timer = new Timer();
        timer.setOnTimerCreateListener(this);
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return homeBinding.getRoot();
    }

    @Override
    public void onMessageFromMainToFragment(String message) {
    }

    @Override
    public void onTimerTick(String value) {
        homeBinding.txtRecordingTime.setText(value);
    }

    @Override
    public void onPause() {
        super.onPause();
        filePath = homeBinding.txtFilePath.getText().toString();
        recordingTime = homeBinding.txtRecordingTime.getText().toString();
    }

    @Override
    public void onResume() {
        super.onResume();

        homeBinding.txtFilePath.setText(filePath);
        homeBinding.txtRecordingTime.setText(recordingTime);
    }
}