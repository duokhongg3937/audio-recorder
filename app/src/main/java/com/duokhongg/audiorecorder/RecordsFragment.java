package com.duokhongg.audiorecorder;

import android.content.ContextWrapper;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RecordsFragment extends Fragment implements FragmentCallbacks {

    ListView listRecords;
    List<File> listRecordsData;
    MediaPlayer mediaPlayer;
    MainActivity mainActivity;
    Button btnDelete;
    Boolean is_delete_mode = false;


    private String getRecordingFilePath(String fileTitle) {
        ContextWrapper contextWrapper = new ContextWrapper(requireActivity().getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, fileTitle);
        return file.getPath();
    }

    private List<File> getAllRecordsInDirectory() {
        ContextWrapper contextWrapper = new ContextWrapper(requireActivity().getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);

        if (musicDirectory != null && musicDirectory.exists() && musicDirectory.isDirectory()) {
            File[] files = musicDirectory.listFiles();
            if (files != null) {
                return Arrays.asList(files);
            }
        }

        return new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listRecords = requireView().findViewById(R.id.listRecords);
        btnDelete = requireView().findViewById(R.id.btnDelete);
        mediaPlayer = new MediaPlayer();

        listRecordsData = getAllRecordsInDirectory();
        List<String> listRecordsName = listRecordsData.stream()
                .map(File::getName).collect(Collectors.toList());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, listRecordsName);
        listRecords.setAdapter(adapter);


        listRecords.setOnItemClickListener((parent, view1, position, id) -> {

            String selectedRecord  = (String) parent.getItemAtPosition(position);
            if (is_delete_mode) {
               //listRecordsName.remove(position);
               //listRecords.deferNotifyDataSetChanged();
               Toast.makeText(requireActivity(), selectedRecord + " is deleted.", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(requireActivity(), selectedRecord + " is clicked.", Toast.LENGTH_SHORT).show();
                try {
                    mediaPlayer.setDataSource(getRecordingFilePath(selectedRecord));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_delete_mode = !is_delete_mode;
                if(is_delete_mode){
                    Toast.makeText(requireActivity(), " Delete mode: Enabled", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(requireActivity(), " Delete mode: Disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_records, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer = null;
    }

    @Override
    public void onMessageFromMainToFragment(String message) {
    }
}