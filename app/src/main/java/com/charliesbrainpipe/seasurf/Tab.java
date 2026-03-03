package com.charliesbrainpipe.seasurf;

import static android.os.Environment.getExternalStoragePublicDirectory;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import org.mozilla.geckoview.AllowOrDeny;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoRuntimeSettings;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;
import org.mozilla.geckoview.WebExtension;
import org.mozilla.geckoview.WebResponse;

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

    private static Toast downloadNotification;

    private boolean wasRedirect = false;

    private static Context context;

    public class MyNavigationDelegate implements GeckoSession.NavigationDelegate {
        @Override
        public GeckoResult<AllowOrDeny> onLoadRequest(GeckoSession session, GeckoSession.NavigationDelegate.LoadRequest request) {
            GeckoResult<AllowOrDeny> result = new GeckoResult<>();

            if (!Objects.equals(url, request.uri) && getTab() == currentTab) {
                new URLChangedEventObject(this, request.uri);
            } // If URL has changed, fire a URLChanged event with the new URL

            if (url != null) {
                String titleToPush = (title != null && !title.isEmpty()) ? title : url;
                if (request.isRedirect && !history.isEmpty()) {
                    history.pop();
                }

                history.push(new String[]{titleToPush, url});

                HistoryDbHelper dbHelper = HistoryDbHelper.getInstance();
                if (dbHelper != null) {
                    dbHelper.addEntry(titleToPush, url, Calendar.getInstance());
                    dbHelper.getHistory();
                }
            }

            url = request.uri;
            wasRedirect = request.isRedirect;

            result.complete(AllowOrDeny.ALLOW); // Allow GeckoView to load the request

            return result;
        }
    }

    public class MyContentDelegate implements GeckoSession.ContentDelegate {
        @Override
        public void onTitleChange(GeckoSession session, String t) {
            title = t;
        }

        @Override
        public void onExternalResponse(GeckoSession session, WebResponse response) {
            String downloadUri = response.uri;
            String[] uriSplit = downloadUri.split("/");
            String fileName = uriSplit[uriSplit.length-1];
            System.out.println(fileName);

            if (MainActivity.downloadManager != null) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUri));
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                MainActivity.downloadManager.enqueue(request);

                goBack();

                downloadNotification.show();
            }
        }
    }

    private Tab(String url) {
        session = new GeckoSession();

        if (url == null || url.isEmpty()) {
            url = Preferences.getHomePage();
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

        Tab.context = context;

        downloadNotification = Toast.makeText(context, "Downloading file...", Toast.LENGTH_SHORT);
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
                    // Assume it to be a search query
                    int searchEngine = Preferences.getSearchEngine();
                    switch (searchEngine) {
                        case 0: {
                            url = "http://www.google.com/search?q=" + url;
                            break;
                        }
                        case 1: {
                            url = "http://www.bing.com/search?q=" + url;
                            break;
                        }
                        case 2: {
                            url = "http://search.yahoo.com/search?p=" + url;
                            break;
                        }
                        case 3: {
                            url = "http://duckduckgo.com/?ia=web&q=" + url;
                            break;
                        }
                    }
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

    public void goHome() { goTo(Preferences.getHomePage()); }

    public static ArrayList<Tab> getTabs() { return tabs; }

    private Tab getTab() { return this; }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}
