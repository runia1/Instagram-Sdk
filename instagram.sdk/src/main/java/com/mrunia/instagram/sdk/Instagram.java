package com.mrunia.instagram.sdk;

import android.content.Context;

/**
 * Created by mar on 2/17/15.
 */
public class Instagram {
    private static Context context;
    private static InstagramAuthConfig authConfig;

    private static Instagram ourInstance = new Instagram();

    public static Instagram getInstance() {
        return ourInstance;
    }

    private Instagram() { }

    public static void with(Context c, InstagramAuthConfig auth) {
        context = c;
        authConfig = auth;
    }

    public Context getContext() {
        return context;
    }

    public InstagramAuthConfig getAuthConfig() {
        return authConfig;
    }
}
