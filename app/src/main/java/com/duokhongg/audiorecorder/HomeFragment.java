package com.duokhongg.audiorecorder;

import android.content.ContextWrapper;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.duokhongg.audiorecorder.databinding.FragmentHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements FragmentCallbacks, Timer.OnTimerClickListener {
    private FragmentHomeBinding homeBinding;
    Button btnCancel, btnOk;
    MediaRecorder mediaRecorder;
    EditText fileNameInput;
    boolean isRecording = false;
    Timer timer;
    String recordingTime = "00:00:00";
    BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    String currentRecordingFilePath;
    String currentFileName;
    BottomNavigationView bottomNavigationView;
    RecordViewModel recordViewModel;
    DatabaseHandler db;
    TextInputEditText edtFileName;
    int currentCategoryId = 0;

    AutoCompleteTextView txtCategory;
    public HomeFragment() {}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnCancel = requireView().findViewById(R.id.btnCancel);
        btnOk = requireView().findViewById(R.id.btnOk);
        fileNameInput = requireView().findViewById(R.id.fileNameInput);
        bottomNavigationView = requireActivity().findViewById(R.id.navigationView);
        edtFileName = requireView().findViewById(R.id.edtFileName);

        bottomSheetBehavior = BottomSheetBehavior.from(requireView().findViewById(R.id.bottomSheet));
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        homeBinding.btnSave.setBackgroundResource(R.drawable.bg_button_primary);
        recordViewModel = new ViewModelProvider(requireActivity()).get(RecordViewModel.class);

        db = new DatabaseHandler(requireContext());
        homeBinding.btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording) {
                    isRecording = false;
                    mediaRecorder.pause();
                    timer.pause();
                    homeBinding.btnRecord.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(requireContext(), R.drawable.mic), null, null);
                    Toast.makeText(requireActivity(), "Recording is stopped", Toast.LENGTH_SHORT).show();
                } else if (mediaRecorder != null) {
                    isRecording = true;
                    mediaRecorder.resume();
                    timer.start();
                    homeBinding.btnRecord.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(requireContext(), R.drawable.square), null, null);
                    Toast.makeText(requireActivity(), "Recording is resumed", Toast.LENGTH_SHORT).show();
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

                    // update UI
                    homeBinding.txtRecordingTime.setText("00:00:00");
                    bottomNavigationView.setVisibility(View.GONE);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    homeBinding.bottomSheetBG.setVisibility(View.VISIBLE);

                    if (edtFileName.getText().toString().length() > 0) {
                        currentFileName = edtFileName.getText().toString();
                    } else {
                        currentFileName = getFileName(currentRecordingFilePath);
                    }

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
                File currentFile = new File(currentRecordingFilePath);
                String newFileName = fileNameInput.getText().toString();

                if (!newFileName.equals(currentFile.getName())) {
                    File newFile = new File(generateOutputFilePath(newFileName));
                    if (currentFile.renameTo(newFile)) {
                        Toast.makeText(requireContext(), "Rename successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Rename failed!", Toast.LENGTH_SHORT).show();
                    }
                    currentRecordingFilePath = newFile.getPath();
                }

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                homeBinding.bottomSheetBG.setVisibility(View.GONE);
                bottomNavigationView.setVisibility(View.VISIBLE);

                File file = new File(currentRecordingFilePath);
                AudioRecord record = File2AudioRecord(file);
                record.getCategory().setId(currentCategoryId);
                int id = db.addRecord(record);
                record.setId(id);

                recordViewModel.getRecordList().getValue().add(record);
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
    public void onMessageFromMainToFragment(Object object) {
    }

    @Override
    public void onTimerTick(String value) {
        homeBinding.txtRecordingTime.setText(value);
    }

    @Override
    public void onPause() {
        super.onPause();
        recordingTime = homeBinding.txtRecordingTime.getText().toString();
    }

    @Override
    public void onResume() {
        super.onResume();
        homeBinding.txtRecordingTime.setText(recordingTime);

        List<Category> categoryList = db.getAllCategory();
        String[] categories = categoryList.stream()
                .map(Category::getCategoryName)
                .toArray(String[]::new);

        CategoryDropdownAdapter categoryAdapter = new CategoryDropdownAdapter(categoryList, requireContext());
        txtCategory = requireView().findViewById(R.id.txtCategory);
        txtCategory.setAdapter(categoryAdapter);

        homeBinding.txtCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentCategoryId = categoryAdapter.getItem(position).getId();
            }
        });
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

    private AudioRecord File2AudioRecord(File file) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        String name = file.getName();
        long lastModified = file.lastModified();
        String path = file.getPath();

        retriever.setDataSource(path);
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        return new AudioRecord(name, path, String.valueOf(lastModified), duration);
    }
}