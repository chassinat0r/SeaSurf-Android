package com.charliesbrainpipe.seasurf;

import java.util.EventListener;

public interface TabClickedEventListener extends EventListener {
    void onTabClicked(TabClickedEventObject event, int position);
}
