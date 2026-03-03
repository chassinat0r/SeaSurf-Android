package com.charliesbrainpipe.seasurf;

import java.util.EventListener;

/* Button Clicked listener interface
 Listen for and handle a button click
 */
public interface ButtonClickedEventListener extends EventListener {
    void onButtonClick(ButtonClickedEventObject event);
}
