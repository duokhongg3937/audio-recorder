package com.duokhongg.audiorecorder;

import android.content.ContextWrapper;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.File;

public class HomeFragment extends Fragment {
    MaterialButton btnRecord;
    Button btnPlay, btnSave;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    EditText edtTitle;
    TextView txtFilePath;
    boolean isRecording = false;

    private String getRecordingFilePath(String fileTitle) {
        String title = fileTitle != null ? fileTitle : "testRecordingFile";
        ContextWrapper contextWrapper = new ContextWrapper(requireActivity().getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, title + ".mp3");
        return file.getPath();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnRecord = (MaterialButton) requireView().findViewById(R.id.btnRecord);
        btnPlay = (Button) requireView().findViewById(R.id.btnPlay);
        btnSave = (Button) requireView().findViewById(R.id.btnSave);
        edtTitle = (EditText) requireView().findViewById(R.id.edtTitle);
        txtFilePath = (TextView) requireView().findViewById(R.id.txtFilePath);

        btnPlay.setEnabled(false);


        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording) {
                    btnRecord.setIconResource(R.drawable.mic);
                    mediaRecorder.stop();
                    Toast.makeText(requireActivity(), "Recording is stopped", Toast.LENGTH_SHORT).show();
                    isRecording = false;
                } else {
                    try {
                        btnRecord.setIconResource(R.drawable.square);
                        mediaRecorder = new MediaRecorder();
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        mediaRecorder.setOutputFile(getRecordingFilePath(edtTitle.getText().toString()));
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        isRecording = true;
                        txtFilePath.setText(edtTitle.getText().toString());

                        Toast.makeText(requireActivity(), "Recording is started", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
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
                    if (isRecording) {
                        btnRecord.setIconResource(R.drawable.mic);
                        mediaRecorder.stop();
                    }
                    mediaRecorder.release();
                    mediaRecorder = null;

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
}