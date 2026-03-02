package com.charliesbrainpipe.seasurf;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;
import java.util.Date;

public class HistoryDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "history.db";

    public static final String CREATE_STATEMENT =
            "CREATE TABLE IF NOT EXISTS History (" +
                    "pageTitle TEXT," +
                    "pageURL TEXT," +
                    "dateTimeAccessed INTEGER" +
            ");";

    public static final String CLEAR_STATEMENT = "DELETE FROM History;";

    public static final String RESET_STATEMENT = "DROP TABLE IF EXISTS History;";

    private static HistoryDbHelper instance;

    public HistoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STATEMENT);
    }

    public static void createInstance(Context context) {
        if (instance == null) {
            instance = new HistoryDbHelper(context);
        }
    }

    public static HistoryDbHelper getInstance() {
        return instance;
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(RESET_STATEMENT);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void clearHistory() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(CLEAR_STATEMENT);
    }

    public void addEntry(String pageTitle, String pageURL, Calendar c) {
        ContentValues values = new ContentValues();
        values.put("pageTitle", pageTitle);
        values.put("pageURL", pageURL);
        values.put("dateTimeAccessed", c.getTimeInMillis());

        SQLiteDatabase db = getWritableDatabase();

        db.insert("History", null, values);
    }

    public Cursor getHistory() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                "History",
                null,
                null,
                null,
                null,
                null,
                "dateTimeAccessed DESC"
        );

        return cursor;

//        while (cursor.moveToNext()) {
//            String title = cursor.getString(cursor.getColumnIndex("pageTitle"));
//            String url = cursor.getString(cursor.getColumnIndex("pageURL"));
//            System.out.println("Title: " + title + " URL: " + url);
//        }
    }
}
