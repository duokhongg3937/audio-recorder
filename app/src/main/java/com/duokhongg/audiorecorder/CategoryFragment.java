package com.duokhongg.audiorecorder;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.duokhongg.audiorecorder.databinding.FragmentCategoryBinding;
import com.google.android.material.dialog.MaterialDialogs;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding categoryBinding;
    private CategoryViewModel categoryViewModel;
    CategoryAdapter adapter;
    DatabaseHandler db;
    int pickedColor = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = new DatabaseHandler(requireContext());

        adapter = new CategoryAdapter(requireContext(), new ArrayList<>());
        categoryBinding.listCategory.setLayoutManager(new LinearLayoutManager(getActivity()));
        categoryBinding.listCategory.setAdapter(adapter);

        categoryViewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);
        categoryViewModel.getCategoryList().observe(getViewLifecycleOwner(), new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categoryList) {
                adapter.setCategoryList(categoryList);
            }
        });

        adapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                Intent intent = new Intent(requireContext(), DetailActivity.class);
                intent.putExtra("file_path", categoryViewModel.getCategoryList().getValue().get(pos).getCategoryName());
                startActivity(intent);
            }
        });

        categoryBinding.imgBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategoryDialog(Gravity.CENTER);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        categoryBinding = FragmentCategoryBinding.inflate(inflater, container, false);
        View view = categoryBinding.getRoot();
        return view;
    }

    private void openCategoryDialog(int gravity) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_category);

        List<Integer> colors = new ArrayList<Integer>();
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);
        colors.add(Color.GRAY);

        RecyclerView colorRecyclerView = dialog.findViewById(R.id.btnColorLayout);
        colorRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        ColorButtonAdapter colorButtonAdapter = new ColorButtonAdapter(requireContext(), colors, new ColorButtonAdapter.OnColorButtonClickListener() {
            @Override
            public void onColorButtonClick(int color) {
                pickedColor = color;
            }
        });
        colorRecyclerView.setAdapter(colorButtonAdapter);

        Window window = dialog.getWindow();
        if (window == null) return;

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttr = window.getAttributes();
        windowAttr.gravity = gravity;
        window.setAttributes(windowAttr);

        dialog.setCancelable(true);

        Button btnClose = dialog.findViewById(R.id.btnCloseDialog);
        Button btnSave = dialog.findViewById(R.id.btnSaveDialog);
        EditText edtCategoryName = dialog.findViewById(R.id.edtCategoryName);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtCategoryName.getText().toString();
                Category category = new Category(name, pickedColor);
                int id = db.addCategory(category);
                category.setId(id);

                categoryViewModel.getCategoryList().getValue().add(category);
                adapter.notifyDataSetChanged();

                dialog.dismiss();
            }
        });



        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        categoryBinding = null;
    }
}