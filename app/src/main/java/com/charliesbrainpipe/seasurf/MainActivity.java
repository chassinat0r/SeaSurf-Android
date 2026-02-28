package com.charliesbrainpipe.seasurf;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements ButtonClickedEventListener, GoToPageEventListener  {
    MyWebView myWebView;

    AddressBar addressBar;

    ButtonHandler backBtn;
    ButtonHandler reloadBtn;
    ButtonHandler homeBtn;
    ButtonHandler historyBtn;

    HistoryDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        HistoryDbHelper.createInstance(getApplicationContext());
        dbHelper = HistoryDbHelper.getInstance();

        addressBar = new AddressBar(findViewById(R.id.addressBar));
        myWebView = new MyWebView(this, findViewById(R.id.geckoview));

        backBtn = new ButtonHandler(findViewById(R.id.backBtn), "back");
        reloadBtn = new ButtonHandler(findViewById(R.id.reloadBtn), "reload");
        homeBtn = new ButtonHandler(findViewById(R.id.homeBtn), "home");
        historyBtn = new ButtonHandler(findViewById(R.id.historyBtn), "history");

        ButtonClickedEventObject.addListener(this);
        GoToPageEventObject.addListener(this);
    }

    public void onButtonClick(ButtonClickedEventObject event) {
        if (event.getAction().equals("history")) {
            Context context = MainActivity.this;
            Class destinationActivity = HistoryActivity.class;
            Intent intent = new Intent(context, destinationActivity);
            startActivity(intent);
        }
    }

    public void onGoToPage(GoToPageEventObject event) {
        myWebView.goTo(event.getUrl());
    }
}
