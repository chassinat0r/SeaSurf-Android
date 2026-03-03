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
    // Address Bar wrapper
    AddressBar addressBar;

    // Buttons
    ImageButton backBtn;
    ImageButton reloadBtn;
    ImageButton homeBtn;
    ImageButton tabsBtn;

    // Database helper
    HistoryDbHelper dbHelper;

    // Handlers for launching activities
    ActivityResultLauncher<Intent> historyResultLauncher;

    ActivityResultLauncher<Intent> tabResultLauncher;

    ActivityResultLauncher<Intent> settingsResultLauncher;

    // Top toolbar
    Toolbar topToolbar;

    // DownloadManager service instance
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

        HistoryDbHelper.createInstance(getApplicationContext()); // Create instance of db helper
        dbHelper = HistoryDbHelper.getInstance(); // Get and store the reference to the instance

        addressBar = new AddressBar(findViewById(R.id.addressBar)); // Create an AddressBar wrapper around the addressBar EditText

        Preferences.load(this); // Load preferences

        Tab.init(this, findViewById(R.id.geckoview)); // Initialise Tab system
        // Create new tab opening the set homepage, and change to it
        int newTab = Tab.newTab(Preferences.getHomePage());
        Tab.changeTab(newTab);

        // Define buttons
        backBtn = findViewById(R.id.backBtn);
        reloadBtn = findViewById(R.id.reloadBtn);
        homeBtn = findViewById(R.id.homeBtn);
        tabsBtn = findViewById(R.id.tabsBtn);

        // When back button is clicked, attempt to go back
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tab currentTab = Tab.getCurrentTab(); // Get tab currently in use
                if (currentTab.canGoBack()) { // If there are previous pages
                    currentTab.goBack(); // Go back
                }
            }
        });

        // When reload button is clicked, reload the page
        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tab currentTab = Tab.getCurrentTab();
                currentTab.reload();
            }
        });

        // When home button is clicked, go to set homepage
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tab currentTab = Tab.getCurrentTab();
                currentTab.goHome();
            }
        });

        // When tabs button is clicked, switch to tabs activity
        tabsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TabsActivity.class);
                tabResultLauncher.launch(intent);
            }
        });

        // Handle the end of HistoryActivity
        historyResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == RESULT_OK) { // If the user clicked an entry in history
                            Intent data = o.getData();
                            String url = data.getStringExtra("url"); // Get URL returned
                            if (url != null) { // Make sure URL is not null
                                // Go to selected URL in current tab
                                Tab currentTab = Tab.getCurrentTab();
                                currentTab.goTo(url);
                            }
                        }
                    }
                }
        );

        // Handle the end of TabsActivity
        tabResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == RESULT_OK) { // A tab was selected
                            Intent data = o.getData();
                            // Get index of tab selected
                            int index = data.getIntExtra("index", -1);
                            if (index >= 0) { // If index is valid
                                Tab.changeTab(index); // Change to that tab
                            }
                        }
                    }
                }
        );

        // Handle the end of SettingsActivity
        settingsResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        // Commit changes to SharedPreferences after exiting settings screen
                        if (o.getResultCode() == RESULT_OK) {
                            Preferences.commitChanges();
                        }
                    }
                }
        );

        // If the back key in the Android navigation bar is pressed, do the same as the back button
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Tab currentTab = Tab.getCurrentTab();
                if (currentTab.canGoBack()) {
                    currentTab.goBack();
                }
            }
        });

        // Set top toolbar to be an action bar, making available the options menu
        topToolbar = findViewById(R.id.topToolbar);
        setSupportActionBar(topToolbar);

        // Store Android DownloadManager
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
    }

    /* boolean onCreateOptionsMenu
    Handle an attempt to load an options menu by pressing the three dots
    on the action bar
    Params:
    - Menu menu: Menu to be created
    Returns: true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /* boolean onOptionsItemSelected
    Handle when an item in the options menu is clicked on
    Params:
    - MenuItem item: Item that was selected
    Returns: true
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.history) { // If history was clicked
            // Launch history activity
            Intent intent = new Intent(this, HistoryActivity.class);
            historyResultLauncher.launch(intent);
            return true;
        } else if (item.getItemId() == R.id.settings) { // If settings was selected
            // Launch settings activity
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            settingsResultLauncher.launch(intent);
            return true;
        }

        // Otherwise, allow parent method to process
        return super.onOptionsItemSelected(item);
    }

    public MainActivity getActivity() { return this; }
}
