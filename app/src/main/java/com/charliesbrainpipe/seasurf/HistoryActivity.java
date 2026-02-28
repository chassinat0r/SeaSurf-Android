package com.charliesbrainpipe.seasurf;

import android.database.Cursor;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.widget.Button;
import android.window.SurfaceSyncGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryActivity extends AppCompatActivity implements HistoryItemClickedEventListener {
    RecyclerView historyView;
    HistoryDbHelper dbHelper;

    ArrayList<String[]> history = new ArrayList<>();

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

        clearButton = findViewById(R.id.clearBtn);

        dbHelper = HistoryDbHelper.getInstance();
        historyView = findViewById(R.id.historyView);

        Cursor cursor = dbHelper.getHistory();

        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow("pageTitle"));
            String url = cursor.getString(cursor.getColumnIndexOrThrow("pageURL"));
            history.add(new String[]{title, url});
        }

        historyView.setLayoutManager(new LinearLayoutManager(this));
        HistoryAdapter adapter = new HistoryAdapter(history);
        historyView.setAdapter(adapter);

        HistoryItemClickedEventObject.addListener(this);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.clearHistory();
                recreate();
            }
        });
    }

    public void onHistoryItemClicked(HistoryItemClickedEventObject event, int position) {
        if (position < history.size()) {
            new GoToPageEventObject(this, history.get(position)[1]);
            finish();
        }
    }
}
