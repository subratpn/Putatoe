package com.putatoe.app.application;

import android.app.Application;


import com.androidnetworking.AndroidNetworking;

import io.paperdb.Paper;

public class CustomApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
        Paper.init(getApplicationContext());
    }


}
