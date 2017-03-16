package com.codefather.gitify.utils;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;

public class ChromeCustomTabs {

    private static final String PRIMARY_COLOR = "#3F51B5";
    private static CustomTabsIntent sCustomTabsIntent;

    private ChromeCustomTabs() {
    }

    @SuppressWarnings("deprecation")
    public static void initialize() {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        sCustomTabsIntent = builder.build();
        builder.setToolbarColor(Color.parseColor(PRIMARY_COLOR));
    }

    public static void start(Context context, String url) {
        start(context, Uri.parse(url));
    }

    public static void start(Context context, Uri url) {
        sCustomTabsIntent.launchUrl(context, url);
    }
}
