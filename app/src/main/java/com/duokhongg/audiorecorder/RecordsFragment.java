package com.duokhongg.audiorecorder;

import android.content.ContextWrapper;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RecordsFragment extends Fragment implements FragmentCallbacks {

    RecyclerView listRecords;
    List<File> listRecordsData;
    MediaPlayer mediaPlayer;
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

        mediaPlayer = new MediaPlayer();

        listRecordsData = getAllRecordsInDirectory();
        List<String> listRecordsName = listRecordsData.stream()
                .map(File::getName).collect(Collectors.toList());

        List<AudioRecord> recordList = new ArrayList<>();

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        for (File file : listRecordsData) {
            retriever.setDataSource(file.getPath());
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            duration = Helper.formatDuration(duration);
            String name = file.getName();
            String lastModified = Helper.formatLastModified(file.lastModified());
            AudioRecord record = new AudioRecord(name, file.getPath(), lastModified, duration);
            recordList.add(record);
        }

        RecordsAdapter adapter = new RecordsAdapter(requireActivity(), recordList);
        listRecords.setLayoutManager(new LinearLayoutManager(getActivity()));
        listRecords.setAdapter(adapter);

        adapter.setOnItemClickListener(new RecordsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                Intent intent = new Intent(requireContext(), DetailActivity.class);
                intent.putExtra("file_path", listRecordsData.get(pos).getPath());
                startActivity(intent);
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