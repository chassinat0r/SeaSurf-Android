package com.charliesbrainpipe.seasurf;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;
import java.util.Date;

/* History database helper class
Handle opening, reading, and writing of the History database
 */
public class HistoryDbHelper extends SQLiteOpenHelper {
    // CONSTANTS

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "history.db";

    // Create table storing each entry's title, URL, and date/time accessed in milliseconds
    public static final String CREATE_STATEMENT =
            "CREATE TABLE IF NOT EXISTS History (" +
                    "pageTitle TEXT," +
                    "pageURL TEXT," +
                    "dateTimeAccessed INTEGER" +
            ");";

    // Delete all entries in History table
    public static final String CLEAR_STATEMENT = "DELETE FROM History;";

    // Delete the entire History table
    public static final String RESET_STATEMENT = "DROP TABLE IF EXISTS History;";

    private static HistoryDbHelper instance;

    /* Constructor
    Private as this uses the Singleton pattern to ensure only one instance of the helper
    exists at a time.
    */
    private HistoryDbHelper(Context context) {
        // Run parent constructor
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /* void onCreate
    Called if the database does not exist, so create the table
    Params:
    - SQLiteDatabase db: Database object
    */
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STATEMENT); // Execute create statement
    }


    /* static void createInstance
    If an instance does not already exist, create and store it
     */
    public static void createInstance(Context context) {
        if (instance == null) {
            instance = new HistoryDbHelper(context); // Run private constructor
        }
    }

    /* static HistoryDbHelper getInstance
    Returns: the single instance of the helper, or null if
    it has not been created
     */
    public static HistoryDbHelper getInstance() {
        return instance;
    }

    /* void onUpgrade
    When the database version is increased, reset the database and create it again
    Params:
    - SQLiteDatabase db: Database object
    - int oldVersion: Previous version of the database
    - int newVersion: Current version of the database
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(RESET_STATEMENT); // Execute reset statement
        onCreate(db); // Create again
    }

    /* void onDowngrade
    If the database version is decreased, then handle it the same way as if it
    were increased: reset and create again.
    Params:
    - SQLiteDatabase db: Database object
    - int oldVersion: Previous version of the database
    - int newVersion: Current version of the database
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion); // Just use onUpgrade function
    }

    /* void clearHistory
    Clear all entries from the History table
     */
    public void clearHistory() {
        SQLiteDatabase db = getWritableDatabase(); // Get database object that can be used to write
        db.execSQL(CLEAR_STATEMENT); // Delete all records in History table
    }

    /* void addEntry
    Add a new entry to the History table
    Params:
    - String pageTitle: Title of the page
    - String pageURL: Address of the page
    - Calendar c: Date/time the page was visited
     */
    public void addEntry(String pageTitle, String pageURL, Calendar c) {
        // Store values in a ContentValues
        ContentValues values = new ContentValues();
        values.put("pageTitle", pageTitle);
        values.put("pageURL", pageURL);
        values.put("dateTimeAccessed", c.getTimeInMillis()); // Convert datetime to milliseconds to work with INTEGER type

        SQLiteDatabase db = getWritableDatabase();

        db.insert("History", null, values); // Insert values as a new row in History table
    }

    /* Cursor getHistory
    Return: cursor for reading all entries in the History table
     */
    public Cursor getHistory() {
        SQLiteDatabase db = getReadableDatabase(); // Get readable database object

        Cursor cursor = db.query(
                "History",
                null,
                null,
                null,
                null,
                null,
                "dateTimeAccessed DESC"
        ); // Get all entries in descending order of datetime accessed (most recent first)

        return cursor;
    }
}
