package com.yeokm1.nussocprintandroid.core;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

/**
 * Created by yeokm1 on 6/10/2014.
 */
public class MyApplication extends Application{

    private String currentDocumentPath;
    private static final String TAG = "MyApplication";

    public String getCurrentDocumentPath() {
        return currentDocumentPath;
    }

    public void setCurrentDocumentUri(Uri newDocumentUri) {
        String path = newDocumentUri.getPath();
        Log.i(TAG, "incoming path " + path);
        this.currentDocumentPath = newDocumentUri.getPath();
    }

    public void onCreate(){
        super.onCreate();
        new Storage(getApplicationContext());
    }



}
