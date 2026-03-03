package com.charliesbrainpipe.seasurf;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/* History Adapter class
Bind the history data to the RecyclerView
*/

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private ArrayList<String[]> dataSet;

    /* ViewHolder class */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // TextViews
        private final TextView titleView;
        private final TextView urlView;

        // Associated information
        private String title;
        private String url;

        /* Constructor
        Configure click listener and define TextViews
        */
        public ViewHolder(View view) {
            super(view);

            view.setOnClickListener(this); // Use this class' onClick function

            // Define TextViews
            titleView = view.findViewById(R.id.pageTitle);
            urlView = view.findViewById(R.id.pageURL);
        }

        /* void onClick
        Let the HistoryActivity know which item was clicked
        Params:
        - View: The view that was clicked
        */
        @Override
        public void onClick(View view) {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                new HistoryItemClickedEventObject(this, position);
            }
        }

        // GETTERS

        public TextView getTitleView() { return titleView; }
        public TextView getURLView() { return urlView; }
        public String getTitle() { return title; }
        public String getURL() { return url; }

        // SETTERS

        public void setTitle(String title) { this.title = title; }
        public void setURL(String url) { this.url = url; }
    }

    /* Constructor
    Params:
    - ArrayList<String[]> dataSet: Set of titles and URLs to display
    */
    public HistoryAdapter(ArrayList<String[]> dataSet) {
        this.dataSet = dataSet;
    }

    /* ViewHolder onCreateViewHolder
    Facilitate the creation of a ViewHolder for each entry in history
    Params:
    - ViewGroup viewGroup: ViewGroup in which new ViewHolder will be inserted
    - int viewType: View type
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Use the history item layout for displaying the entry
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_history_item, viewGroup, false);
        return new ViewHolder(view);
    }

    /* void onBindViewHolder
    Display the data at the position in the data set
    Params:
    - ViewHolder viewHolder: ViewHolder to be updated
    - int position: Position in the data set
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Set TextViews to show title and URL respectively
        viewHolder.getTitleView().setText(dataSet.get(position)[0]);
        viewHolder.getURLView().setText(dataSet.get(position)[1]);

        // Update the title and URL String variables in viewHolder
        viewHolder.setTitle(dataSet.get(position)[0]);
        viewHolder.setURL(dataSet.get(position)[1]);

    }

    /* int getItemCount
    Returns: The number of entries in the dataSet
     */
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
