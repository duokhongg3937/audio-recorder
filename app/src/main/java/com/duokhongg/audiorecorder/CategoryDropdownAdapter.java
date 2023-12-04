package com.duokhongg.audiorecorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class CategoryDropdownAdapter extends BaseAdapter implements Filterable {
    List<Category> categoryList;
    List<Category> filteredList;
    Context context;

    public CategoryDropdownAdapter(List<Category> categoryList, Context context) {
        this.categoryList = categoryList;
        this.filteredList = categoryList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Category getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.category_dropdown_item, null);

            viewHolder = new ViewHolder();
            viewHolder.txtCategoryItemName = view.findViewById(R.id.txtCategoryItemName);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.txtCategoryItemName.setText(filteredList.get(position).getCategoryName());

        return view;
    }

    @Override
    public Filter getFilter() {
        return new CategoryFilter();
    }

    private class CategoryFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                List<Category> filteredList = new ArrayList<>();
                for (Category category : categoryList) {
                    if (category.getCategoryName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(category);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            } else {
                results.count = categoryList.size();
                results.values = categoryList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (List<Category>) results.values;
            notifyDataSetChanged();
        }
    }

    private static class ViewHolder {
        TextView txtCategoryItemName;
    }

}
