package xyz.segfaulty.seasurf;

import java.util.ArrayList;
import java.util.EventObject;

/* History Item Clicked Event
Fire when a view in the HistoryActivity RecyclerView is clicked
Store position in dataset of the view
 */
public class HistoryItemClickedEventObject extends EventObject {
    private static ArrayList<HistoryItemClickedEventListener> listeners = new ArrayList<>();

    public HistoryItemClickedEventObject(Object source, int position) {
        super(source);
        for (HistoryItemClickedEventListener listener : listeners) { // Iterate through listeners
            // Trigger history item clicked handler
            listener.onHistoryItemClicked(this, position);
        }
    }

    // STATIC

    /* static void addListener
    Add an object extending the AddressBarDoneEventListener to be notified when
    a history view is clicked
    Params:
    - HistoryItemClickedEventListener listener: The object to be notified
     */
    public static void addListener(HistoryItemClickedEventListener listener) {
        listeners.add(listener);
    }
}
