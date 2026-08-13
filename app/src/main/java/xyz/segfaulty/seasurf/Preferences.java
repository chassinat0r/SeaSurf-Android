package xyz.segfaulty.seasurf;

import android.app.Activity;
import android.content.SharedPreferences;

/* Preferences class
For loading and setting preferences
 */
public class Preferences {
    // Pointer to settings file
    private static SharedPreferences sharedPreferences;

    // Object for changing preferences
    private static SharedPreferences.Editor editor;

    // Preference values
    private static int searchEngine; // Search engine from 0-3
    private static String homePage; // URL for homepage

    /* static void load
    Load preferences and define objects
    Params:
    - Activity activity: App activity to get preferences for
     */
    public static void load(Activity activity) {
        sharedPreferences = activity.getPreferences( 0);
        searchEngine = sharedPreferences.getInt("searchEngine", 0);
        homePage = sharedPreferences.getString("homePage", "http://google.com");
        editor = sharedPreferences.edit();
    }

    // GETTERS
    public static int getSearchEngine() { return searchEngine; }
    public static String getHomePage() { return homePage; }

    // SETTERS
    public static void setSearchEngine(int searchEngine) {
        Preferences.searchEngine = (searchEngine > 3) ? 0 : searchEngine; // If somehow search engine exceeds 3, default to 0 (Google)
        editor.putInt("searchEngine", Preferences.searchEngine); // Set value in SharedPreferences
    }
    public static void setHomePage(String homePage) {
        // If URL given is invalid, default to Google
        if (!homePage.contains(".") && !homePage.equals("localhost")) {
            homePage = "http://google.com";
        }

        // If URL missing HTTP at the start, prepend it
        if (!homePage.startsWith("http://") && !homePage.startsWith("https://")) {
            homePage = "http://" + homePage;
        }

        // Set homepage
        Preferences.homePage = homePage;
        editor.putString("homePage", Preferences.homePage);
    }

    /* static void commitChanges
    Permanently commit changes to SharedPreferences
    */
    public static void commitChanges() {
        editor.commit();
    }
}
