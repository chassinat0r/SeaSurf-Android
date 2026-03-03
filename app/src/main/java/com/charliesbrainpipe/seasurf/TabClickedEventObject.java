package com.charliesbrainpipe.seasurf;

import java.util.ArrayList;
import java.util.EventObject;

/* Tab Clicked Event
Fire when a tab view is clicked in the TabsActivity,
i.e. the user wants to switch tabs
*/
public class TabClickedEventObject extends EventObject {
    private static ArrayList<TabClickedEventListener> listeners = new ArrayList<>();

    /* Constructor
    Params:
    - source: The object firing the event
    - position: Tab user wants to change to
    */
    public TabClickedEventObject(Object source, int position) {
        super(source);
        for (TabClickedEventListener listener : listeners) {
            listener.onTabClicked(this, position);
        }
    }

    // STATIC

    /* void addListener
    Add an object extending the TabClickedEventListener to be notified when the event is fired
    Params:
    - TabClickedEventListener listener: The object to be notified
     */
    public static void addListener(TabClickedEventListener listener) {
        listeners.add(listener);
    }
}
