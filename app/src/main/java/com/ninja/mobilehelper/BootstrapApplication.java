package com.ninja.mobilehelper;

import android.app.Application;

/**
 * Created by ninja on 4/14/16.
 */
public class BootstrapApplication extends Application{
    private static BootstrapApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
    public static BootstrapApplication getInstance() {
        return instance;
    }

}
