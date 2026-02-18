package com.charliesbrainpipe.seasurf;

import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements URLChangedEventListener {
    MyWebView myWebView;

    EditText addressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addressBar = findViewById(R.id.addressBar);
        myWebView = new MyWebView(this, findViewById(R.id.geckoview));
        URLChangedEventObject.addListener(this);
    }

    public void onURLChange(URLChangedEventObject source, String url) {
        addressBar.setText(url);
    }
}