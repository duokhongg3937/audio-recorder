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

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class HomeFragment extends Fragment implements FragmentCallbacks, Timer.OnTimerClickListener {
    AppCompatButton btnRecord;
    Button btnPlay, btnSave, btnCancel, btnOk;
    MediaRecorder mediaRecorder;
    EditText edtTitle, fileNameInput;
    TextView txtFilePath, txtRecordingTime;
    boolean isRecording = false;
    boolean isPaused = true;
    Timer timer;
    String filePath = "";
    String recordingTime = "";
    BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    View bottomSheetBg;
    String currentRecordingFilePath;
    String currentFileName;


    private String generateOutputFilePath() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault()).format(new Date());
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

            btnRecord.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(requireContext(), R.drawable.square), null, null);

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
        btnCancel = requireView().findViewById(R.id.btnCancel);
        btnOk = requireView().findViewById(R.id.btnOk);
        txtFilePath = (TextView) requireView().findViewById(R.id.txtFilePath);
        txtRecordingTime = (TextView) requireView().findViewById(R.id.txtRecordingTime);
        fileNameInput = (EditText) requireView().findViewById(R.id.fileNameInput);

        bottomSheetBehavior = BottomSheetBehavior.from(requireView().findViewById(R.id.bottomSheet));
        bottomSheetBg = requireView().findViewById(R.id.bottomSheetBG);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

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
                    currentFileName = getFileName(currentRecordingFilePath);

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    bottomSheetBg.setVisibility(View.VISIBLE);
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
                bottomSheetBg.setVisibility(View.GONE);
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
                bottomSheetBg.setVisibility(View.GONE);
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