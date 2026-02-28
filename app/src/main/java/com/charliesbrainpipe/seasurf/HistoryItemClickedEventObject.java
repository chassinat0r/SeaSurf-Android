package com.charliesbrainpipe.seasurf;

import java.util.ArrayList;
import java.util.EventObject;

public class HistoryItemClickedEventObject extends EventObject {
    private static ArrayList<HistoryItemClickedEventListener> listeners = new ArrayList<>();

    public HistoryItemClickedEventObject(Object source, int position) {
        super(source);
        for (HistoryItemClickedEventListener listener : listeners) {
            listener.onHistoryItemClicked(this, position);
        }
    }
    public static void addListener(HistoryItemClickedEventListener listener) {
        listeners.add(listener);
    }
}
