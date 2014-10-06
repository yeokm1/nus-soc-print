package com.yeokm1.nussocprint.core;

import android.app.Application;

/**
 * Created by yeokm1 on 6/10/2014.
 */
public class MyApplication extends Application{


    public void onCreate(){
        super.onCreate();
        new Storage(getApplicationContext());
    }



}
