package com.example.myphotoeditor;

import android.app.Application;
import android.content.Context;

public class PhotoApp extends Application {
    private static PhotoApp photoApp;

    @Override
    public void onCreate() {
        super.onCreate();
        photoApp = this;
    }

    public static PhotoApp getInstance() {
        return photoApp;
    }

    private static final String TAG = PhotoApp.class.getSimpleName();
}
