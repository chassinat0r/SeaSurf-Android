package com.charliesbrainpipe.seasurf;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    // Buttons
    ImageButton backButton;
    Button resetBtn;

    // Spinner for selecting search engine
    Spinner searchEngineSelector;

    // EditText for setting homepage
    EditText homePageText;

    // Search Engines to choose
    private String[] searchEngines = {"Google", "Bing", "Yahoo", "DuckDuckGo"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Define elements
        backButton = findViewById(R.id.backBtn);
        resetBtn = findViewById(R.id.resetBtn);
        homePageText = findViewById(R.id.homePageText);

        // Fill search engine spinner with options
        searchEngineSelector = findViewById(R.id.searchEngineSelector);
        searchEngineSelector.setOnItemSelectedListener(this);
        homePageText.setText(Preferences.getHomePage());

        // Create adapter containing search engines for the spinner
        ArrayAdapter<String> searchEngineAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                searchEngines
        );

        searchEngineSelector.setAdapter(searchEngineAdapter); // Set adapter

        searchEngineSelector.setSelection(Preferences.getSearchEngine()); // Select whatever search engine is currently set

        // If back button is clicked, exit activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitActivity();
            }
        });

        // If reset button is clicked, reset Preferences to default
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homePageText.setText("http://google.com"); // Set homepage to Google
                searchEngineSelector.setSelection(0); // Set search engine to Google
            }
        });

        // If back key in system navigation bar is pressed, exit activity
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                exitActivity();
            }
        });
    }

    /* void onItemSelected
    Handle a change in the selection in the search engine spinner
    */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Preferences.setSearchEngine(position); // Set search engine to be index of selected item
    }

    /* void onNothingSelected
    If nothing is selected in the Spinner. As something is always selected,
    this does nothing
    */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    /* void exitActivity
    Set the homepage to be the contents of the EditText, and exit the activity
     */
    public void exitActivity() {
        Preferences.setHomePage(homePageText.getText().toString());
        setResult(RESULT_OK);
        finish();
    }
}
