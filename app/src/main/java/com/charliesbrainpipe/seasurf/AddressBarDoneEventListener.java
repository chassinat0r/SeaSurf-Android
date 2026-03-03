package com.charliesbrainpipe.seasurf;

import java.util.EventListener;

/* Address Bar Done Listener interface
 Listen for and handle address bar submission
 */
public interface AddressBarDoneEventListener extends EventListener {
    void onAddressBarDone(AddressBarDoneEventObject event);
}
