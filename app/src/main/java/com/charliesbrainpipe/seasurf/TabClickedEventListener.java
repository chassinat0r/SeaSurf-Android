package com.charliesbrainpipe.seasurf;

import java.util.EventListener;

/* Tab Clicked Listener interface
 Listen for and handle a tab view being clicked in the RecyclerView in TabsActivity
 */
public interface TabClickedEventListener extends EventListener {
    void onTabClicked(TabClickedEventObject event, int position);
}
