package com.charliesbrainpipe.seasurf;

import android.view.View;
import android.widget.Button;

public class ButtonHandler {
    private Button btn;
    private String action;

    public ButtonHandler(Button btn, String action) {
        this.btn = btn;
        this.action = action;

        this.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ButtonClickedEventObject(this, action);
            }
        });
    }
}
