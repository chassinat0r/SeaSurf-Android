package com.charliesbrainpipe.seasurf;

import java.util.ArrayList;
import java.util.EventObject;

/* Refresh TabsActivity Event
Fire when there is a need to refresh the TabsActivity RecyclerView
*/
public class RefreshTabsActivityEventObject extends EventObject {
    private static ArrayList<RefreshTabsActivityEventListener> listeners = new ArrayList<>();

    /* Constructor
    Params:
    - source: The object firing the event
    */
    public RefreshTabsActivityEventObject(Object source) {
        super(source);
        for (RefreshTabsActivityEventListener listener : listeners) {
            listener.onRefreshTabsActivity(this);
        }
    }

    // STATIC

    /* void addListener
    Add an object extending the RefreshTabsActivityEventListener to be notified when the event is fired
    Params:
    - RefreshTabsActivityEventListener listener: The object to be notified
     */
    public static void addListener(RefreshTabsActivityEventListener listener) {
        listeners.add(listener);
    }
}
