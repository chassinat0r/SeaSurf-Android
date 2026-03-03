package com.charliesbrainpipe.seasurf;

import java.util.EventListener;

/* History Item Clicked Listener interface
Listen for and handle an item in the History RecyclerView being clicked
 */
public interface HistoryItemClickedEventListener extends EventListener {
    void onHistoryItemClicked(HistoryItemClickedEventObject event, int position);
}
