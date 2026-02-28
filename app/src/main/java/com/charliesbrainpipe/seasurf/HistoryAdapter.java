package com.charliesbrainpipe.seasurf;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private ArrayList<String[]> dataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView titleView;
        private final TextView urlView;

        private String title;
        private String url;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            view.setOnClickListener(this);

            titleView = (TextView) view.findViewById(R.id.pageTitle);
            urlView = (TextView) view.findViewById(R.id.pageURL);
        }

        @Override
        public void onClick(View view) {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                new HistoryItemClickedEventObject(this, position);
            }
        }

        public TextView getTitleView() { return titleView; }
        public TextView getURLView() { return urlView; }

        public void setTitle(String title) { this.title = title; }
        public void setURL(String url) { this.url = url; }

        public String getTitle() { return title; }
        public String getURL() { return url; }
    }

    public HistoryAdapter(ArrayList<String[]> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_history_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTitleView().setText(dataSet.get(position)[0]);
        viewHolder.getURLView().setText(dataSet.get(position)[1]);
        viewHolder.setTitle(dataSet.get(position)[0]);
        viewHolder.setURL(dataSet.get(position)[1]);

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
