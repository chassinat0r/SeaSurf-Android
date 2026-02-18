package com.charliesbrainpipe.seasurf;

import java.util.EventListener;

public interface AddressBarDoneEventListener extends EventListener {
    void onAddressBarDone(AddressBarDoneEventObject event);
}
