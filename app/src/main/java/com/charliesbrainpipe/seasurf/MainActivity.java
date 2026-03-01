package com.charliesbrainpipe.seasurf;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity  {
    MyWebView myWebView;

    AddressBar addressBar;

    ButtonHandler backBtn;
    ButtonHandler reloadBtn;
    ButtonHandler homeBtn;

    HistoryDbHelper dbHelper;

    ActivityResultLauncher<Intent> resultLauncher;

    Toolbar topToolbar;

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

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == RESULT_OK) {
                            Intent data = o.getData();
                            String url = data.getStringExtra("url");
                            if (url != null) {
                                myWebView.goTo(url);
                            }
                        }
                    }
                }
        );
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (myWebView.canGoBack()) {
                    myWebView.goBack();
                }
            }
        });

        topToolbar = findViewById(R.id.topToolbar);
        setSupportActionBar(topToolbar);
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
            resultLauncher.launch(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
