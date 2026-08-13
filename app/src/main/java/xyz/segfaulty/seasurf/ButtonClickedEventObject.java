package xyz.segfaulty.seasurf;

import java.util.ArrayList;
import java.util.EventObject;

public class ButtonClickedEventObject extends EventObject {
    private String action;
    private static ArrayList<ButtonClickedEventListener> listeners = new ArrayList<>();

    public ButtonClickedEventObject(Object source, String action) {
        super(source);
        this.action = action;
        for (ButtonClickedEventListener listener : listeners) {
            listener.onButtonClick(this);
        }
    }

    public String getAction() {
        return action;
    }

    public static void addListener(ButtonClickedEventListener listener) {
        listeners.add(listener);
    }
}
