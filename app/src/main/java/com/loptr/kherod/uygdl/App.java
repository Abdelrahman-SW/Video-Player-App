package com.loptr.kherod.uygdl;

import android.app.Application;
import android.util.Log;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("ab_do" , "init ADS");
//        MobileAds.initialize(this);
    }
}
