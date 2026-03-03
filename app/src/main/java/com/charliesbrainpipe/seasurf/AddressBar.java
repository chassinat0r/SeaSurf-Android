package com.charliesbrainpipe.seasurf;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/* Address Bar class
* Make an EditText an address bar and handle submission
*/

public class AddressBar implements URLChangedEventListener {
    private EditText editText;

    /* Constructor
    Params:
    - editText: The address bar component
    */
    public AddressBar(EditText editText) {
        this.editText = editText; // Store EditText
        // Handle editor actions for the address bar
        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) { // If the action is done (enter pressed)
                    new AddressBarDoneEventObject(this, editText.getText().toString()); // Fire an AddressBarDoneEvent
                    return true;
                }
                return false;
            }
        });
        URLChangedEventObject.addListener(this); // Listen for URL changes in webview
    }

    /* onURLChange
    Handle when the URL of the webview changes.
    Params:
    - event: Event that triggered this handler
    - url: The new URL
    */
    public void onURLChange(URLChangedEventObject event, String url) {
        this.editText.setText(url); // Display new URL in address bar
    }
}
