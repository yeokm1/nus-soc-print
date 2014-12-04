package com.yeokm1.nussocprintandroid.core;

import android.app.Application;

/**
 * Created by yeokm1 on 6/10/2014.
 */
public class MyApplication extends Application{

    private String currentDocumentPath;


    public String getCurrentDocumentPath() {
        return currentDocumentPath;
    }

    public void setCurrentDocumentPath(String currentDocumentPath) {
        this.currentDocumentPath = currentDocumentPath;
    }

    public void onCreate(){
        super.onCreate();
        new Storage(getApplicationContext());
    }



}
