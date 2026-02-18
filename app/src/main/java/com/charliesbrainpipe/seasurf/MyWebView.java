package com.charliesbrainpipe.seasurf;

import android.app.Activity;
import android.content.Context;

import org.mozilla.geckoview.AllowOrDeny;
import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

import java.util.Objects;
import java.util.Stack;

public class MyWebView {
    private static GeckoRuntime sRuntime;
    private GeckoSession session;

    private String url;
    private Stack<String> history = new Stack<>();

    public class MyNavigationDelegate implements GeckoSession.NavigationDelegate {
        @Override
        public GeckoResult<AllowOrDeny> onLoadRequest(GeckoSession session, GeckoSession.NavigationDelegate.LoadRequest request) {
            GeckoResult<AllowOrDeny> result = new GeckoResult<>();

            if (!Objects.equals(url, request.uri)) { new URLChangedEventObject(this, request.uri); } // If URL has changed, fire a URLChanged event with the new URL

            // If the request is not just a redirect to another page, also save it in history so we can go back
            if (!request.isRedirect) {
                if (url != null) { history.push(url); } // If current URL is not null, push to history stack
                url = request.uri; // Set current URL to new URL
            }

            result.complete(AllowOrDeny.ALLOW); // Allow GeckoView to load the request

            return result;
        }
    }

    public MyWebView(Context context, GeckoView geckoView) {
        session = new GeckoSession(); // Start new Gecko session
        if (sRuntime == null) { // We can only have one Gecko Runtime at one point, so only create one if null
            sRuntime = GeckoRuntime.create(context);
        }

        session.open(sRuntime);
        geckoView.setSession(session); // Set session in GeckoView
        session.loadUri("https://google.com"); // Load homepage
        session.setNavigationDelegate(new MyNavigationDelegate()); // Set navigation delegate to handle loading pages
    }

    /* getUrl()
    Return: The current URL (excluding redirects).
    */
    public String getUrl() {
        return url;
    }
}
