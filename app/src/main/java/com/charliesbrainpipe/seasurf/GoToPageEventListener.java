package com.charliesbrainpipe.seasurf;

import java.util.EventListener;

public interface GoToPageEventListener extends EventListener {
    void onGoToPage(GoToPageEventObject event);
}
