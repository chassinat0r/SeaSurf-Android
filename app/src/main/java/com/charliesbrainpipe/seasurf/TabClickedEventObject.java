package com.charliesbrainpipe.seasurf;

import java.util.ArrayList;
import java.util.EventObject;

public class TabClickedEventObject extends EventObject {
    private static ArrayList<TabClickedEventListener> listeners = new ArrayList<>();

    public TabClickedEventObject(Object source, int position) {
        super(source);
        for (TabClickedEventListener listener : listeners) {
            listener.onTabClicked(this, position);
        }
    }
    public static void addListener(TabClickedEventListener listener) {
        listeners.add(listener);
    }
}
