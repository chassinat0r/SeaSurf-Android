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

    // URL and title of the page open in the tab
    private String url = null;
    private String title = null;

    // Stack of previously visited page titles and URLs
    private Stack<String[]> history = new Stack<>();

    // Store all tabs in an ArrayList
    private static ArrayList<Tab> tabs = new ArrayList<>();
    private static Tab currentTab; // Store the currently open tab

    private static Toast downloadNotification; // Toast notification for displaying when a file starts downloading

    /* MyNavigationDelegate class
    Intercept and handle load requests (requests to load a page)
     */
    public class MyNavigationDelegate implements GeckoSession.NavigationDelegate {
        /* GeckoResult<AllowOrDeny> onLoadRequest
        Handle a request to load a URL
        Params:
        - GeckoSession session: Session that is making the request
        - LoadRequest request: Information about the request
        */
        @Override
        public GeckoResult<AllowOrDeny> onLoadRequest(GeckoSession session, GeckoSession.NavigationDelegate.LoadRequest request) {
            GeckoResult<AllowOrDeny> result = new GeckoResult<>(); // Create an asynchronous result of type AllowOrDeny

            // If the new URL is different and the tab is currently in use, fire a URLChanged event to update the address bar
            if (!Objects.equals(url, request.uri) && getTab() == currentTab) {
                new URLChangedEventObject(this, request.uri);
            }

            // Commit current page and title to history
            if (url != null) { // Make sure URL is not null
                String titleToPush = (title != null && !title.isEmpty()) ? title : url; // If title is null, set to URL, otherwise use title
                if (request.isRedirect && !history.isEmpty()) { // If the request is a result of a redirect from a past one
                    history.pop(); // Remove the past request from history stack
                }

                history.push(new String[]{titleToPush, url}); // Push current title and URL to history stack

                HistoryDbHelper dbHelper = HistoryDbHelper.getInstance(); // Get DB helper
                if (dbHelper != null) { // Make sure DB helper isn't null
                    // Push current title, URL, and date to History table
                    dbHelper.addEntry(titleToPush, url, Calendar.getInstance());
                    dbHelper.getHistory();
                }
            }

            url = request.uri; // Set current URL to that of the LoadRequest

            result.complete(AllowOrDeny.ALLOW); // Allow GeckoView to load the request

            return result;
        }
    }

    /* MyContentDelegate class
    Handle changes in the page's content
    */
    public class MyContentDelegate implements GeckoSession.ContentDelegate {
        /* void onTitleChange
        Set the title of the Tab to be the page's title
        Params:
        - GeckoSession session: Session in which the change occurred
        - String t: The new title
         */
        @Override
        public void onTitleChange(GeckoSession session, String t) {
            title = t;
        }

        /* void onExternalResponse
        Handle an attempt to download a file
        Params:
        - GeckoSession session: Session that tried to download a file
        - WebResponse response: Response including file information
         */
        @Override
        public void onExternalResponse(GeckoSession session, WebResponse response) {
            String downloadUri = response.uri; // Get URI of download response
            // Get the actual filename separate from the URI
            String[] uriSplit = downloadUri.split("/");
            String fileName = uriSplit[uriSplit.length-1];

            if (MainActivity.downloadManager != null) { // If DownloadManager instance in MainActivity is defined
                // Create a new Download Request
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUri));
                // Save in Downloads directory as its filename
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // Notify on download completion
                MainActivity.downloadManager.enqueue(request); // Ask DownloadManager to download

                goBack(); // Go back to previous page

                downloadNotification.show(); // Show Toast notification for a brief period of time so user knows something is downloading
            }
        }
    }

    /* Constructor
    Params:
    - String url: URL to be opened in the tab
     */
    private Tab(String url) {
        session = new GeckoSession(); // Create a GeckoSession

        // If URL is unspecified, use homepage set in preferences
        if (url == null || url.isEmpty()) {
            url = Preferences.getHomePage();
        }

        session.open(sRuntime);
        session.loadUri(url); // Load homepage
        session.setNavigationDelegate(new MyNavigationDelegate()); // Set navigation delegate to handle loading pages
        session.setContentDelegate(new MyContentDelegate()); // Set content delegate to handle changes in page content

        AddressBarDoneEventObject.addListener(this); // Listen for address bar submission
    }

    /* static void init
    Initialise tab manager for the first time by creating GeckoRuntime and storing GeckoView element
     */
    public static void init(Context context, GeckoView geckoView) {
        if (sRuntime == null) { // If Runtime does not exist
            // Set the page colour scheme to always be light
            GeckoRuntimeSettings settings = new GeckoRuntimeSettings.Builder().build();
            settings.setPreferredColorScheme(GeckoRuntimeSettings.COLOR_SCHEME_LIGHT);
            sRuntime = GeckoRuntime.create(context, settings); // Create a Gecko runtime with that setting
        }

        Tab.geckoView = geckoView; // Store GeckoView

        // Create re-usable download toast notification
        downloadNotification = Toast.makeText(context, "Downloading file...", Toast.LENGTH_SHORT);
    }

    /* void goTo
    Load a specified URL
    Params:
    - String url: URL to go to
     */
    public void goTo(String url) {
        session.loadUri(url);
    }

    /* static int newTab
    Create a new tab with a given URL
    Params:
    - String url: URL to load in the tab
    Returns: Index of new tab
     */
    public static int newTab(String url) {
        tabs.add(new Tab(url)); // Add a new tab to ArrayList
        return tabs.size()-1;
    }

    /* static void changeTab
    Change to a tab at a given index
    Params:
    - int index: Index of the tab to change to
    Throws:
    - IndexOutOfBoundsException: If the tab index is out of range
     */
    public static void changeTab(int index) throws IndexOutOfBoundsException {
        if (index >= tabs.size()) { // If index is not within limits of tab ArrayList
            throw new IndexOutOfBoundsException("No tab with that index"); // Throw error
        }

        currentTab = tabs.get(index); // Set currentTab to the other tab

        geckoView.setSession(currentTab.session); // Change the session shown in GeckoView to that of the other tab

        new URLChangedEventObject(currentTab, currentTab.getUrl()); // Let the address bar know the URL has changed
    }

    /* static Tab getCurrentTab
    Returns: Tab currently in use by GeckoView
     */
    public static Tab getCurrentTab() { return currentTab; }

    /* static void closeTab
    Close a tab of a given index
    Params:
    - int index: Index of the tab to close
    Throws:
    - IndexOutOfBoundsException: If there is no tab with that index
     */
    public static void closeTab(int index) throws IndexOutOfBoundsException {
        if (index >= tabs.size()) {
            throw new IndexOutOfBoundsException("No tab with that index");
        }

        tabs.remove(index); // Remove tab with that index

        if (tabs.isEmpty()) { // If there are no tabs
            // Create a new one and change to it
            int tab = newTab(null);
            changeTab(tab);
        } else {
            if (index == 0) { // If the closed tab index was 0
                // Change to the new tab 0 (former tab 1)
                changeTab(0);
            } else { // Otherwise
                changeTab(index-1); // Change to the tab to the left of the closed one
            }
        }
    }

    /* void onAddressBarDone
    Handle a submission of the address bar
    Params:
    - AddressBarDoneEventObject event: Event including the address bar's contents
     */
    public void onAddressBarDone(AddressBarDoneEventObject event) {
        if (this == currentTab) { // If the tab is currently in use
            String url = event.getText(); // Get text submitted

            if (!(url.startsWith("http://") || url.startsWith("https://"))) { // If URL doesn't start with http(s)
                if ((!url.contains(".") && !url.equals("localhost")) || url.contains(" ")) { // If URL doesn't contain a dot for domain extension and is not localhost
                    // Assume it to be a search query
                    int searchEngine = Preferences.getSearchEngine(); // Get set search engine
                    switch (searchEngine) {
                        case 0: { // Google
                            url = "http://www.google.com/search?q=" + url;
                            break;
                        }
                        case 1: { // Bing
                            url = "http://www.bing.com/search?q=" + url;
                            break;
                        }
                        case 2: { // Yahoo
                            url = "http://search.yahoo.com/search?p=" + url;
                            break;
                        }
                        case 3: { // DuckDuckGo
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

    /* boolean canGoBack
    Returns: Whether there are previous pages recorded in history
     */
    public boolean canGoBack() {
        return !history.isEmpty();
    }

    /* void goBack
    Go to the previously visited page
     */
    public void goBack() {
        String[] entry = history.pop(); // Get last-visited title and URL
        session.goBack(); // Go back in GeckoSession

        // Set title and URL to be that of previous page
        title = entry[0];
        url = entry[1];

        // Let the address bar know the current URL has changed so it can update
        new URLChangedEventObject(this, url);
    }

    /* void reload
    Reload the page
     */
    public void reload() { session.reload(); }

    /* void goHome
    Go to the set homepage
     */
    public void goHome() { goTo(Preferences.getHomePage()); }

    /* static ArrayList<Tab> getTabs
    Returns: all open tabs
     */
    public static ArrayList<Tab> getTabs() { return tabs; }

    /* Tab getTab
    Returns: This Tab object, used if we need to get parent in a delegate class
     */
    private Tab getTab() { return this; }

    /* String getTitle
    Returns: Title of current page
     */
    public String getTitle() {
        return title;
    }

    /* String getUrl
    Returns: URL of current page
     */
    public String getUrl() {
        return url;
    }
}
