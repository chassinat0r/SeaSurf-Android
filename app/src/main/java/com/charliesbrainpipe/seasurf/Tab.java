package com.charliesbrainpipe.seasurf;

import android.content.Context;

import org.mozilla.geckoview.AllowOrDeny;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoRuntimeSettings;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.Stack;

public class Tab implements AddressBarDoneEventListener {
    private static GeckoRuntime sRuntime;
    private GeckoSession session;
    private static GeckoView geckoView;

    private String url = null;
    private String title = null;

    private Stack<String[]> history = new Stack<>();

    private static ArrayList<Tab> tabs = new ArrayList<>();
    private static Tab currentTab;

    public class MyNavigationDelegate implements GeckoSession.NavigationDelegate {
        @Override
        public GeckoResult<AllowOrDeny> onLoadRequest(GeckoSession session, GeckoSession.NavigationDelegate.LoadRequest request) {
            GeckoResult<AllowOrDeny> result = new GeckoResult<>();

            if (!Objects.equals(url, request.uri) && getTab() == currentTab) {
                new URLChangedEventObject(this, request.uri);
            } // If URL has changed, fire a URLChanged event with the new URL

            if (url != null) {
                String titleToPush = (title != null && !title.isEmpty()) ? title : url;
                history.push(new String[]{titleToPush, url});
                HistoryDbHelper dbHelper = HistoryDbHelper.getInstance();
                if (dbHelper != null) {
                    dbHelper.addEntry(titleToPush, url, Calendar.getInstance());
                    dbHelper.getHistory();
                }
            }

            url = request.uri;

            result.complete(AllowOrDeny.ALLOW); // Allow GeckoView to load the request

            return result;
        }
    }

    public class MyContentDelegate implements GeckoSession.ContentDelegate {
        @Override
        public void onTitleChange(GeckoSession session, String t) {
            title = t;
        }
    }

    private Tab(String url) {
        session = new GeckoSession();

        if (url == null || url.isEmpty()) {
            url = "https://google.com";
        }

        session.open(sRuntime);
        session.loadUri(url); // Load homepage
        session.setNavigationDelegate(new MyNavigationDelegate()); // Set navigation delegate to handle loading pages
        session.setContentDelegate(new MyContentDelegate());

        AddressBarDoneEventObject.addListener(this);
    }

    public static void init(Context context, GeckoView geckoView) {
        if (sRuntime == null) {
            GeckoRuntimeSettings settings = new GeckoRuntimeSettings.Builder().build();
            settings.setPreferredColorScheme(GeckoRuntimeSettings.COLOR_SCHEME_LIGHT);
            sRuntime = GeckoRuntime.create(context, settings); // Create a Gecko runtime
        }

        Tab.geckoView = geckoView;
    }

    public void goTo(String url) {
        session.loadUri(url);
    }

    public static int newTab(String url) {
        tabs.add(new Tab(url));
        return tabs.size()-1;
    }

    public static void changeTab(int index) throws IndexOutOfBoundsException {
        if (index >= tabs.size()) {
            throw new IndexOutOfBoundsException("No tab with that index");
        }

        currentTab = tabs.get(index);

        geckoView.setSession(currentTab.session);

        new URLChangedEventObject(currentTab, currentTab.getUrl());
    }

    public static Tab getCurrentTab() { return currentTab; }

    public static void closeTab(int index) throws IndexOutOfBoundsException {
        if (index >= tabs.size()) {
            throw new IndexOutOfBoundsException("No tab with that index");
        }

        tabs.remove(index);

        if (tabs.isEmpty()) {
            int tab = newTab(null);
            changeTab(tab);
        } else {
            if (index == 0) {
                changeTab(0);
            } else {
                changeTab(index-1);
            }
        }
    }

    public void onAddressBarDone(AddressBarDoneEventObject event) {
        if (this == currentTab) {
            String url = event.getText(); // Get text submitted

            if (!(url.startsWith("http://") || url.startsWith("https://"))) { // If URL doesn't start with http(s)
                if ((!url.contains(".") && !url.equals("localhost")) || url.contains(" ")) { // If URL doesn't contain a dot for domain extension and is not localhost
                    url = "http://www.google.com/search?q=" + url; // Assume it to be a search query
                } else { // If URL contains a dot or is localhost, assume it to be a website just without http prefix
                    url = "http://" + url; // Prepend http
                }
            }

            session.loadUri(url); // Load URL
        }
    }

    public boolean canGoBack() {
        return !history.isEmpty();
    }

    public void goBack() {
        String[] entry = history.pop();
        session.goBack();

        title = entry[0];
        url = entry[1];

        new URLChangedEventObject(this, url);
    }

    public void reload() { session.reload(); }

    public void goHome() { goTo("https://google.com"); }

    public static ArrayList<Tab> getTabs() { return tabs; }

    private Tab getTab() { return this; }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}
