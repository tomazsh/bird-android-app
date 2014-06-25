package com.nedeljko.bird.app;

import android.app.Application;
import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.nedeljko.bird.app.helpers.TwitterClient;

public class BirdApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        BirdApplication.context = this;
        ActiveAndroid.initialize(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }

    public static TwitterClient getTwitterClient() {
        return (TwitterClient)TwitterClient.getInstance(TwitterClient.class, BirdApplication.context);
    }
}
