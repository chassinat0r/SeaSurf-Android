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

    /* ViewHolder class */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Views
        private final TextView titleView;
        private final ImageButton closeBtn;

        private String title; // Tab title

        /* Constructor */
        public ViewHolder(View view) {
            super(view);

            view.setOnClickListener(this);

            // Define views
            titleView = view.findViewById(R.id.title);
            closeBtn = view.findViewById(R.id.closeBtn);
        }

        /* void onClick
        Let the TabsActivity know which tab was clicked
        Params:
        - View: The view that was clicked
         */
        @Override
        public void onClick(View view) {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                new TabClickedEventObject(this, position);
            }
        }

        // GETTERS

        public TextView getTitleView() { return titleView; }
        public ImageButton getCloseBtn() { return closeBtn; }
        public String getTitle() { return title; }

        // SETTERS

        public void setTitle(String title) { this.title = title; }

    }

    /* Constructor
    Params:
    - ArrayList<Tab> dataSet: Set of open tabs
     */
    public TabsAdapter(ArrayList<Tab> dataSet) {
        this.dataSet = dataSet;
    }

    /* ViewHolder onCreateViewHolder
    Facilitate the creation of a ViewHolder for each tab
    Params:
    - ViewGroup viewGroup: ViewGroup in which new ViewHolder will be inserted
    - int viewType: View type
    */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Use the tab layout for displaying the entry
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tab, viewGroup, false);
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
        String title = dataSet.get(position).getTitle();
        if (title == null || title.isEmpty()) { title = "New Tab"; } // If there is no title, just show the tab as "New  Tab"
        viewHolder.getTitleView().setText(title);
        viewHolder.setTitle(title);
        // When close button is clicked, close the associated tab
        viewHolder.getCloseBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tab.closeTab(position);
                new RefreshTabsActivityEventObject(this); // Tell the TabsActivity to refresh to show the updated list of tabs
            }
        });
    }

    /* int getItemCount
    Returns: The number of entries in the dataSet
    */
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
