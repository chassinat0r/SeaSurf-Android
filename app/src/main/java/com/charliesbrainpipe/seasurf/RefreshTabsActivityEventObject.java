package com.charliesbrainpipe.seasurf;

import java.util.ArrayList;
import java.util.EventObject;

public class RefreshTabsActivityEventObject extends EventObject {
    private static ArrayList<RefreshTabsActivityEventListener> listeners = new ArrayList<>();

    public RefreshTabsActivityEventObject(Object source) {
        super(source);
        for (RefreshTabsActivityEventListener listener : listeners) {
            listener.onRefreshTabsActivity(this);
        }
    }

    public static void addListener(RefreshTabsActivityEventListener listener) {
        listeners.add(listener);
    }
}
