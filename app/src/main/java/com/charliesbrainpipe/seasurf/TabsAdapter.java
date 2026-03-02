package com.charliesbrainpipe.seasurf;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TabsAdapter extends RecyclerView.Adapter<TabsAdapter.ViewHolder> {
    private ArrayList<Tab> dataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView titleView;
        private final ImageButton closeBtn;

        private String title;

        public ViewHolder(View view) {
            super(view);

            view.setOnClickListener(this);

            titleView = view.findViewById(R.id.title);
            closeBtn = view.findViewById(R.id.closeBtn);
        }

        @Override
        public void onClick(View view) {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                new TabClickedEventObject(this, position);
            }
        }

        public TextView getTitleView() { return titleView; }
        public ImageButton getCloseBtn() { return closeBtn; }

        public void setTitle(String title) { this.title = title; }

        public String getTitle() { return title; }
    }

    public TabsAdapter(ArrayList<Tab> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tab, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        String title = dataSet.get(position).getTitle();
        if (title == null || title.isEmpty()) { title = "New Tab"; }
        viewHolder.getTitleView().setText(title);
        viewHolder.setTitle(title);
        viewHolder.getCloseBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tab.closeTab(position);
                new RefreshTabsActivityEventObject(this);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
