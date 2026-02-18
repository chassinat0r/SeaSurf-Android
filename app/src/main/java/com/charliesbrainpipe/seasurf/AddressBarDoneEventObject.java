package com.charliesbrainpipe.seasurf;

import java.util.ArrayList;
import java.util.EventObject;

public class AddressBarDoneEventObject extends EventObject {
    private static ArrayList<AddressBarDoneEventListener> listeners = new ArrayList<>();

    private String text;

    public AddressBarDoneEventObject(Object source, String text) {
        super(source);
        this.text = text;
        for (AddressBarDoneEventListener listener : listeners) {
            listener.onAddressBarDone(this);
        }
    }

    public String getText() {
        return text;
    }

    public static void addListener(AddressBarDoneEventListener listener) {
        listeners.add(listener);
    }
}
