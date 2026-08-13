package xyz.segfaulty.seasurf;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity implements HistoryItemClickedEventListener {
    // Recycler View
    RecyclerView historyView;

    // Database Helper
    HistoryDbHelper dbHelper;

    // Previously visited URLs and titles
    ArrayList<String[]> history = new ArrayList<>();

    // Buttons
    ImageButton backButton;
    Button clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = HistoryDbHelper.getInstance(); // Get DB helper

        // Define elements
        backButton = findViewById(R.id.backBtn);
        clearButton = findViewById(R.id.clearBtn);
        historyView = findViewById(R.id.historyView);

        Cursor cursor = dbHelper.getHistory(); // Get all entries in history as a cursor to navigate through

        while (cursor.moveToNext()) { // Go through history until there are no entries left
            // Get title and URL
            String title = cursor.getString(cursor.getColumnIndexOrThrow("pageTitle"));
            String url = cursor.getString(cursor.getColumnIndexOrThrow("pageURL"));
            // Add to history ArrayList
            history.add(new String[]{title, url});
        }

        historyView.setLayoutManager(new LinearLayoutManager(this)); // Use LinearLayout for the history recycler view

        // Use a specialised adapter to bind the recycler view to the history data
        HistoryAdapter adapter = new HistoryAdapter(history);
        historyView.setAdapter(adapter);

        HistoryItemClickedEventObject.addListener(this); // Listen for any item in the recycler view being clicked

        // When the clear button is clicked, clear history and reload the activity
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.clearHistory();
                recreate();
            }
        });

        // When the back button is clicked, exit the activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitActivity();
            }
        });

        // When the back button in the Android navigation bar is clicked, exit the activity
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                exitActivity();
            }
        });
    }

    /* void onHistoryItemClicked
    Handle an item in the RecyclerView being clicked
    Params:
    - HistoryItemClickedEventObject event: the event fired
    - int position: The index in the History ArrayList that was clicked
     */
    public void onHistoryItemClicked(HistoryItemClickedEventObject event, int position) {
        if (position < history.size()) { // If position is in range
            // Inform the calling activity (MainActivity) that this activity is finished and what URL to go to
            Intent goToUrlIntent = new Intent(); // Create a new intent
            goToUrlIntent.putExtra("url", history.get(position)[1]); // Tell the main activity the URL of the clicked item
            setResult(RESULT_OK, goToUrlIntent);
            finish(); // Exit this activity
        }
    }

    /* void exitActivity
    Exit the activity
     */
    private void exitActivity() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
