package com.duokhongg.audiorecorder.ui.records;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.duokhongg.audiorecorder.RecordDetailActivity;
import com.duokhongg.audiorecorder.R;
import com.duokhongg.audiorecorder.data.model.RecordWithCategory;
import com.duokhongg.audiorecorder.utils.FragmentCallbacks;
import com.duokhongg.audiorecorder.utils.Helper;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RecordsFragment extends Fragment implements FragmentCallbacks {

    RecyclerView listRecords;
    private AudioRecordViewModel recordViewModel;
    RecordWithCategoryRVAdapter adapter;
    SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = requireView().findViewById(R.id.searchViewRecord);
        adapter = new RecordWithCategoryRVAdapter();

        listRecords = requireView().findViewById(R.id.listRecords);
        listRecords.setLayoutManager(new LinearLayoutManager(getActivity()));
        listRecords.setAdapter(adapter);

        recordViewModel = new ViewModelProvider(requireActivity()).get(AudioRecordViewModel.class);

        // recordViewModel.deleteAllExpiredRecord();

        recordViewModel.getAllRecordsWithCategory().observe(getViewLifecycleOwner(), new Observer<List<RecordWithCategory>>() {
            @Override
            public void onChanged(List<RecordWithCategory> audioRecords) {
                adapter.submitList(audioRecords);
            }
        });


        adapter.setOnItemClickListener(new RecordWithCategoryRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecordWithCategory record) {

                if (record.getCategoryId() == 1)
                {
                    record.setCategoryId(record.getOldCategory());
                    record.setTimeDelete(null);
                    recordViewModel.update(Helper.RecordWithCategory2Record(record));
                }
                else
                {
                    Intent intent = new Intent(requireContext(), RecordDetailActivity.class);
                    intent.putExtra("file_path", record.getFilePath());
                    intent.putExtra("file_name", record.getFileName());
                    intent.putExtra("record", record);
                    startActivity(intent);
                }
            }

        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchRecords(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchRecords(newText);
                return false;
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

    private void searchRecords(String query) {
        if (query.isEmpty()) {
            recordViewModel.getAllRecordsWithCategory().observe(getViewLifecycleOwner(),
                    new Observer<List<RecordWithCategory>>() {
                @Override
                public void onChanged(List<RecordWithCategory> recordWithCategories) {
                    adapter.submitList(recordWithCategories);
                }
            });
        } else {
            List<RecordWithCategory> filteredRecords = new ArrayList<>();
            for (RecordWithCategory record : recordViewModel.getAllRecordsWithCategory().getValue()) {
                if (record.getFileName().toLowerCase().contains(query.toLowerCase()) ||
                        (record.getCategoryName() != null && record.getCategoryName().toLowerCase().contains(query.toLowerCase()))) {
                    filteredRecords.add(record);
                }
            }
            adapter.submitList(filteredRecords);
        }
    }

}