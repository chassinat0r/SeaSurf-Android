package com.charliesbrainpipe.seasurf;

import java.util.EventListener;

public interface ButtonClickedEventListener extends EventListener {
    void onButtonClick(ButtonClickedEventObject event);
}
