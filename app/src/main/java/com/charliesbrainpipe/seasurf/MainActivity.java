package com.charliesbrainpipe.seasurf;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {
    AddressBar addressBar;

    ImageButton backBtn;
    ImageButton reloadBtn;
    ImageButton homeBtn;
    ImageButton tabsBtn;

    HistoryDbHelper dbHelper;

    ActivityResultLauncher<Intent> historyResultLauncher;

    ActivityResultLauncher<Intent> tabResultLauncher;

    ActivityResultLauncher<Intent> settingsResultLauncher;

    Toolbar topToolbar;

    static DownloadManager downloadManager;

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

        Preferences.load(this);

        Tab.init(this, findViewById(R.id.geckoview));
        int newTab = Tab.newTab(Preferences.getHomePage());
        Tab.changeTab(newTab);

        backBtn = findViewById(R.id.backBtn);
        reloadBtn = findViewById(R.id.reloadBtn);
        homeBtn = findViewById(R.id.homeBtn);
        tabsBtn = findViewById(R.id.tabsBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tab currentTab = Tab.getCurrentTab();
                if (currentTab.canGoBack()) {
                    currentTab.goBack();
                }
            }
        });

        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tab currentTab = Tab.getCurrentTab();
                currentTab.reload();
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tab currentTab = Tab.getCurrentTab();
                currentTab.goHome();
            }
        });

        tabsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TabsActivity.class);
                tabResultLauncher.launch(intent);
            }
        });

        historyResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == RESULT_OK) {
                            Intent data = o.getData();
                            String url = data.getStringExtra("url");
                            if (url != null) {
                                Tab currentTab = Tab.getCurrentTab();
                                currentTab.goTo(url);
                            }
                        }
                    }
                }
        );

        tabResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == RESULT_OK) {
                            Intent data = o.getData();
                            int index = data.getIntExtra("index", -1);
                            if (index >= 0) {
                                Tab.changeTab(index);
                            }
                        }
                    }
                }
        );

        settingsResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == RESULT_OK) {
                            Preferences.commitChanges();
                        }
                    }
                }
        );

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Tab currentTab = Tab.getCurrentTab();
                if (currentTab.canGoBack()) {
                    currentTab.goBack();
                }
            }
        });

        topToolbar = findViewById(R.id.topToolbar);
        setSupportActionBar(topToolbar);

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.history) {
            Intent intent = new Intent(this, HistoryActivity.class);
            historyResultLauncher.launch(intent);
            return true;
        } else if (item.getItemId() == R.id.settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            settingsResultLauncher.launch(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public MainActivity getActivity() { return this; }
}
