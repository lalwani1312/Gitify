package com.codefather.gitify;

import android.app.Application;

import com.codefather.gitify.analytics.FirebaseUtils;
import com.codefather.gitify.network.RetrofitApiClient;
import com.codefather.gitify.utils.ChromeCustomTabs;
import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * This class initializes all singleton classes.
 * <p>
 * Created by hitesh-lalwani on 2/3/17.
 */

public class GitifyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitApiClient.initialize();
        ChromeCustomTabs.initialize();
        // This instantiates DBFlow
        FlowManager.init(new FlowConfig.Builder(this).build());
        // add for verbose logging
        FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);
        FirebaseUtils.initialize(this);
        Stetho.initializeWithDefaults(this);
    }
}
