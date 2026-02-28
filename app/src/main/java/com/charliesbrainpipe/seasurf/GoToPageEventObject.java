package com.charliesbrainpipe.seasurf;

import java.util.ArrayList;
import java.util.EventObject;

public class GoToPageEventObject extends EventObject {
    private String url;
    private static ArrayList<GoToPageEventListener> listeners = new ArrayList<>();

    public GoToPageEventObject(Object source, String url) {
        super(source);
        this.url = url;
        for (GoToPageEventListener listener : listeners) {
            listener.onGoToPage(this);
        }
    }

    public String getUrl() {
        return url;
    }

    public static void addListener(GoToPageEventListener listener) {
        listeners.add(listener);
    }
}
