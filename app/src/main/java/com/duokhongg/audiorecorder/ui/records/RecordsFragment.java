package com.duokhongg.audiorecorder.ui.records;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duokhongg.audiorecorder.RecordDetailActivity;
import com.duokhongg.audiorecorder.R;
import com.duokhongg.audiorecorder.data.model.RecordWithCategory;
import com.duokhongg.audiorecorder.utils.FragmentCallbacks;

import java.util.List;

public class RecordsFragment extends Fragment implements FragmentCallbacks {

    RecyclerView listRecords;
    private AudioRecordViewModel recordViewModel;
    RecordWithCategoryRVAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listRecords = requireView().findViewById(R.id.listRecords);
        adapter = new RecordWithCategoryRVAdapter();

        listRecords.setLayoutManager(new LinearLayoutManager(getActivity()));
        listRecords.setAdapter(adapter);

        recordViewModel = new ViewModelProvider(requireActivity()).get(AudioRecordViewModel.class);
        recordViewModel.getAllRecordsWithCategory().observe(getViewLifecycleOwner(), new Observer<List<RecordWithCategory>>() {
            @Override
            public void onChanged(List<RecordWithCategory> audioRecords) {
                adapter.submitList(audioRecords);
            }
        });

        adapter.setOnItemClickListener(new RecordWithCategoryRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecordWithCategory record) {
                Intent intent = new Intent(requireContext(), RecordDetailActivity.class);
                intent.putExtra("file_path", record.getFilePath());
                intent.putExtra("file_name", record.getFileName());
                intent.putExtra("record", record);
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