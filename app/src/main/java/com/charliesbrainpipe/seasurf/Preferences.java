package com.charliesbrainpipe.seasurf;

import android.app.Activity;
import android.content.SharedPreferences;

public class Preferences {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static int searchEngine;
    private static String homePage;

    public static void load(Activity activity) {
        sharedPreferences = activity.getPreferences( 0);
        searchEngine = sharedPreferences.getInt("searchEngine", 0);
        homePage = sharedPreferences.getString("homePage", "http://google.com");
        editor = sharedPreferences.edit();
    }

    public static int getSearchEngine() { return searchEngine; }
    public static String getHomePage() { return homePage; }

    public static void setSearchEngine(int searchEngine) {
        Preferences.searchEngine = (searchEngine > 3) ? 0 : searchEngine;
        editor.putInt("searchEngine", Preferences.searchEngine);
    }
    public static void setHomePage(String homePage) {
        if (!homePage.contains(".") && !homePage.equals("localhost")) {
            homePage = "http://google.com";
        }
        if (!homePage.startsWith("http://") && !homePage.startsWith("https://")) {
            homePage = "http://" + homePage;
        }
        Preferences.homePage = homePage;
        editor.putString("homePage", Preferences.homePage);
    }

    public static void commitChanges() {
        editor.commit();
    }
}
