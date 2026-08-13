package xyz.segfaulty.seasurf;

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
    // RecyclerView for listing tabs
    RecyclerView tabView;

    // Data set
    ArrayList<Tab> tabs = new ArrayList<>();

    // Buttons
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

        // Define views
        tabView = findViewById(R.id.tabView);
        backButton = findViewById(R.id.backBtn);
        newTabBtn = findViewById(R.id.newTabBtn);

        display(); // Display tabs in RecyclerView

        // If back button clicked, exit activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitActivity();
            }
        });

        // If new tab button clicked, create a new tab, switch to it, and exit activity
        newTabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = Tab.newTab(null);
                Tab.changeTab(index);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        // Listen for refresh activity events
        RefreshTabsActivityEventObject.addListener(this);

        // Listen for tab items being clicked
        TabClickedEventObject.addListener(this);
    }

    /* void display
    Display open tabs in the RecyclerView
     */
    private void display() {
        tabs = Tab.getTabs(); // Get open tabs

        tabView.setLayoutManager(new LinearLayoutManager(this)); // Use LinearLayout
        TabsAdapter adapter = new TabsAdapter(tabs); // Create specialised adapter passing in open tabs
        tabView.setAdapter(adapter); // Use adapter for tabView
    }

    /* void exitActivity
    Exit the TabsActivity
     */
    private void exitActivity() {
        setResult(RESULT_CANCELED);
        finish();
    }

    /* void onRefreshTabsActivity
    Handle an event to refresh the RecyclerView
    Params:
    - RefreshTabsActivityEventObject event
     */
    public void onRefreshTabsActivity(RefreshTabsActivityEventObject event) {
        display(); // Re-display tabs
    }

    /* void onTabClicked
    Handle a tab item being clicked
    Params:
    - TabClickedEventObject event
    - int position: Tab index that was clicked on
     */
    public void onTabClicked(TabClickedEventObject event, int position) {
        if (position < Tab.getTabs().size()) {
            // Tell the MainActivity to change the tab, and exit TabsActivity
            Intent changeTabIntent = new Intent();
            changeTabIntent.putExtra("index", position);
            setResult(RESULT_OK, changeTabIntent);
            finish();
        }
    }
}
