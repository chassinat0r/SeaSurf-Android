package com.charliesbrainpipe.seasurf;

import java.util.EventListener;

/* Refresh TabsActivity Listener interface
 Listen for and handle requests to refresh TabActivity (e.g.
 if a tab is closed)
 */
public interface RefreshTabsActivityEventListener extends EventListener {
    void onRefreshTabsActivity(RefreshTabsActivityEventObject event);
}
