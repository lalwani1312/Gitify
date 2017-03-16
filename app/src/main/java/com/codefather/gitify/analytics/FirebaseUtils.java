package com.codefather.gitify.analytics;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Firebase analytics
 * <p>
 * Created by hitesh-lalwani on 14/3/17.
 */

public class FirebaseUtils {

    private static FirebaseAnalytics sFirebaseAnalytics;

    public static void initialize(Context appContext) {
        sFirebaseAnalytics = FirebaseAnalytics.getInstance(appContext);
    }

    public static void log(String event, Bundle bundle) {
        sFirebaseAnalytics.logEvent(event, bundle);
    }
}
