package com.charliesbrainpipe.seasurf;

import java.util.EventListener;

public interface URLChangedEventListener extends EventListener {
    void onURLChange(URLChangedEventObject event, String url);
}
