package com.charliesbrainpipe.seasurf;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TabsActivity extends AppCompatActivity implements RefreshTabsActivityEventListener, TabClickedEventListener {

    RecyclerView tabView;

    ArrayList<Tab> tabs = new ArrayList<>();

    ImageButton backButton;
    ImageButton newTabBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tabs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tabView = findViewById(R.id.tabView);
        backButton = findViewById(R.id.backBtn);
        newTabBtn = findViewById(R.id.newTabBtn);

        display();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitActivity();
            }
        });

        newTabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = Tab.newTab("https://google.com");
                Tab.changeTab(index);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        RefreshTabsActivityEventObject.addListener(this);
        TabClickedEventObject.addListener(this);
    }

    private void display() {
        tabs = Tab.getTabs();

        tabView.setLayoutManager(new LinearLayoutManager(this));
        TabsAdapter adapter = new TabsAdapter(tabs);
        tabView.setAdapter(adapter);
    }

    private void exitActivity() {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onRefreshTabsActivity(RefreshTabsActivityEventObject event) {
        display();
    }

    public void onTabClicked(TabClickedEventObject event, int position) {
        if (position < Tab.getTabs().size()) {
            Intent changeTabIntent = new Intent();
            changeTabIntent.putExtra("index", position);
            setResult(RESULT_OK, changeTabIntent);
            finish();
        }
    }
}
