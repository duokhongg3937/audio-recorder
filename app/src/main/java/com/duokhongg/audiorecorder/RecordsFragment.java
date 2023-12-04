package com.duokhongg.audiorecorder;

import android.content.ContextWrapper;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecordsFragment extends Fragment implements FragmentCallbacks {

    RecyclerView listRecords;
    private RecordViewModel recordViewModel;
    RecordsAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listRecords = requireView().findViewById(R.id.listRecords);

        adapter = new RecordsAdapter(requireContext(), new ArrayList<>());
        listRecords.setLayoutManager(new LinearLayoutManager(getActivity()));
        listRecords.setAdapter(adapter);

        recordViewModel = new ViewModelProvider(requireActivity()).get(RecordViewModel.class);
        recordViewModel.getRecordList().observe(getViewLifecycleOwner(), new Observer<List<AudioRecord>>() {
            @Override
            public void onChanged(List<AudioRecord> audioRecords) {
                adapter.setRecordList(audioRecords);
            }
        });

        adapter.setOnItemClickListener(new RecordsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                Intent intent = new Intent(requireContext(), DetailActivity.class);
                intent.putExtra("file_path", recordViewModel.getRecordList().getValue().get(pos).getFilePath());
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
    }

    @Override
    public void onMessageFromMainToFragment(Object object) {

    }
}