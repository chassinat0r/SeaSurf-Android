package com.charliesbrainpipe.seasurf;

import java.util.EventListener;

public interface RefreshTabsActivityEventListener extends EventListener {
    void onRefreshTabsActivity(RefreshTabsActivityEventObject event);
}
