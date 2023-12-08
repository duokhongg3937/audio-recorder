package com.duokhongg.audiorecorder.ui.categories;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.duokhongg.audiorecorder.R;

import java.util.List;

public class ColorButtonAdapter extends RecyclerView.Adapter<ColorButtonAdapter.ColorButtonViewHolder> {

    public static class ColorButtonViewHolder extends RecyclerView.ViewHolder {
        private ImageButton imageButton;
        public ColorButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton = (ImageButton) itemView;
        }
    }

    public interface OnColorButtonClickListener {
        void onColorButtonClick(int color);
    }

    private Context context;
    private List<Integer> colors;
    private OnColorButtonClickListener listener;
    private ImageButton selected;

    public ColorButtonAdapter(Context context, List<Integer> colors, OnColorButtonClickListener listener) {
        this.context = context;
        this.colors = colors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ColorButtonAdapter.ColorButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageButton imageButton = new ImageButton(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                160,
                160
        );
        params.setMargins(8, 0, 8, 0);
        imageButton.setLayoutParams(params);
        return new ColorButtonViewHolder(imageButton);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorButtonAdapter.ColorButtonViewHolder holder, int position) {
        int color = colors.get(position);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(color);
        shape.setStroke(2, ContextCompat.getColor(context, android.R.color.black));
        holder.imageButton.setBackground(shape);

        if (holder.imageButton == selected) {
            holder.imageButton.setImageResource(R.drawable.ic_check);
        } else {
            holder.imageButton.setImageResource(0);
        }

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onColorButtonClick(color);

                if (selected != null) {
                    selected.setImageResource(0);
                }

                holder.imageButton.setImageResource(R.drawable.ic_check);
                selected = holder.imageButton;
            }
        });
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }
}
