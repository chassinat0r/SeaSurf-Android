package com.charliesbrainpipe.seasurf;

import java.util.ArrayList;
import java.util.EventObject;

/* URL Changed Event
Fire when the URL in the current tab changes, or the user switches tabs
*/
public class URLChangedEventObject extends EventObject {
    private String url;
    private static ArrayList<URLChangedEventListener> listeners = new ArrayList<>();

    /* Constructor
    Params:
    - source: The object firing the event
    - url: New URL
    */
    public URLChangedEventObject(Object source, String url) {
        super(source);
        this.url = url;
        for (URLChangedEventListener listener : listeners) {
            listener.onURLChange(this, url);
        }
    }

    public String getUrl() {
        return url;
    }

    public static void addListener(URLChangedEventListener listener) {
        listeners.add(listener);
    }
}
