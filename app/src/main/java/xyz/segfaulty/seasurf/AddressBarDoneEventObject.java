package xyz.segfaulty.seasurf;

import java.util.ArrayList;
import java.util.EventObject;

/* Address Bar Done Event
Fire when the address bar is submitted
Store URL entered in address bar
*/
public class AddressBarDoneEventObject extends EventObject {
    private static ArrayList<AddressBarDoneEventListener> listeners = new ArrayList<>(); // Store listeners in an ArrayList

    private String text; // Text entered in address bar

    /* Constructor
    Params:
    - source: The object firing the event
    - text: Text entered in address bar
    */
    public AddressBarDoneEventObject(Object source, String text) {
        super(source);
        this.text = text; // Store text
        for (AddressBarDoneEventListener listener : listeners) {
            // Trigger address bar handler
            listener.onAddressBarDone(this);
        }
    }

    // GETTERS

    /* String getText
    Returns the text entered in the address bar
    */
    public String getText() {
        return text;
    }

    // STATIC

    /* void addListener
    Add an object extending the AddressBarDoneEventListener to be notified when the event is fired
    Params:
    - AddressBarDoneEventListener listener: The object to be notified
     */
    public static void addListener(AddressBarDoneEventListener listener) {
        listeners.add(listener);
    }
}
