package com.charliesbrainpipe.seasurf;

import java.util.ArrayList;
import java.util.EventObject;

public class URLChangedEventObject extends EventObject {
    private String url;
    private static ArrayList<URLChangedEventListener> listeners = new ArrayList<>();

    public URLChangedEventObject(Object source, String url) {
        super(source);
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
