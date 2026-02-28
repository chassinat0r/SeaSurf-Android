package com.charliesbrainpipe.seasurf;

import android.app.Activity;
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

public class MyWebView implements AddressBarDoneEventListener, ButtonClickedEventListener {
    private static GeckoRuntime sRuntime;
    private GeckoSession session;

    private String url; // Store current URL (excluding redirects)

    private Stack<String[]> history = new Stack<>();

    public String title;

    private Boolean wasRedirect = false;
    public class MyNavigationDelegate implements GeckoSession.NavigationDelegate {
        @Override
        public GeckoResult<AllowOrDeny> onLoadRequest(GeckoSession session, GeckoSession.NavigationDelegate.LoadRequest request) {
            GeckoResult<AllowOrDeny> result = new GeckoResult<>();

            if (!Objects.equals(url, request.uri)) { new URLChangedEventObject(this, request.uri); } // If URL has changed, fire a URLChanged event with the new URL

            if (url != null) {
                String titleToPush = (title != null && !title.isEmpty()) ? title : url;
                history.push(new String[]{titleToPush, url});
                HistoryDbHelper dbHelper = HistoryDbHelper.getInstance();
                if (dbHelper != null) {
                    dbHelper.addEntry(titleToPush, url, Calendar.getInstance());
                    dbHelper.getHistory();
                }
            }

            wasRedirect = request.isRedirect;

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

    /* Constructor
    Params:
    - Context: Used to provide access to the application.
    - GeckoView: The Mozilla GeckoView component
    */
    public MyWebView(Context context, GeckoView geckoView) {
        session = new GeckoSession(); // Start new Gecko session
        if (sRuntime == null) { // We can only have one Gecko Runtime at one point, so only create one if null
            GeckoRuntimeSettings settings = new GeckoRuntimeSettings.Builder().build();
            settings.setPreferredColorScheme(GeckoRuntimeSettings.COLOR_SCHEME_LIGHT);
            sRuntime = GeckoRuntime.create(context, settings); // Create a Gecko runtime on the given context
        }

        session.open(sRuntime);
        geckoView.setSession(session); // Set session in GeckoView
        session.loadUri("https://google.com"); // Load homepage
        session.setNavigationDelegate(new MyNavigationDelegate()); // Set navigation delegate to handle loading pages
        session.setContentDelegate(new MyContentDelegate());

        AddressBarDoneEventObject.addListener(this); // Listen for address bar submissions
        ButtonClickedEventObject.addListener(this); // Listen for buttons being clicked
    }

    /* getUrl()
    Return: The current URL (excluding redirects).
    */
    public String getUrl() {
        return url;
    }

    /* onAddressBarDone
    Format the text submitted in the address bar to be a URL and go to it
    Params:
    - AddressBarDoneEventObject: Event signalling the address has been entered.
    */
    public void onAddressBarDone(AddressBarDoneEventObject event) {
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

    public void goTo(String url) {
        session.loadUri(url);
    }

    public void onButtonClick(ButtonClickedEventObject event) {
        String action = event.getAction();
        switch (action) {
            case "back": {
                if (canGoBack()) {
                    goBack();
                }
                break;
            }
            case "reload": {
                session.reload();
                break;
            }
            case "home": {
                session.loadUri("https://google.com");
                break;
            }
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
}
