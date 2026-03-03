package com.charliesbrainpipe.seasurf;

import java.util.EventListener;

/* URL Changed Listener interface
 Listen for and handle changes in URL from the current tab
 */
public interface URLChangedEventListener extends EventListener {
    void onURLChange(URLChangedEventObject event, String url);
}
