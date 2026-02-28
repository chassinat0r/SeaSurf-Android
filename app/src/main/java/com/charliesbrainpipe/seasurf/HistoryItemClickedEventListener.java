package com.charliesbrainpipe.seasurf;

import java.util.EventListener;

public interface HistoryItemClickedEventListener extends EventListener {
    void onHistoryItemClicked(HistoryItemClickedEventObject event, int position);
}
