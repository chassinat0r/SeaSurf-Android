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
    ImageButton backButton;
    Spinner searchEngineSelector;
    EditText homePageText;
    Button resetBtn;

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

        backButton = findViewById(R.id.backBtn);
        resetBtn = findViewById(R.id.resetBtn);
        homePageText = findViewById(R.id.homePageText);

        searchEngineSelector = findViewById(R.id.searchEngineSelector);
        searchEngineSelector.setOnItemSelectedListener(this);
        homePageText.setText(Preferences.getHomePage());

        ArrayAdapter<String> searchEngineAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                searchEngines
        );

        searchEngineSelector.setAdapter(searchEngineAdapter);

        searchEngineSelector.setSelection(Preferences.getSearchEngine());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitActivity();
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homePageText.setText("http://google.com");
                searchEngineSelector.setSelection(0);
            }
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                exitActivity();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Preferences.setSearchEngine(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    public void exitActivity() {
        Preferences.setHomePage(homePageText.getText().toString());
        setResult(RESULT_OK);
        finish();
    }
}
