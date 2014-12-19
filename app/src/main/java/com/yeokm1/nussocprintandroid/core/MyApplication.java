package com.yeokm1.nussocprintandroid.core;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.yeokm1.nussocprintandroid.R;

import java.util.HashMap;

/**
 * Created by yeokm1 on 6/10/2014.
 */
public class MyApplication extends Application{

    private Uri currentDocumentPath;
    private static final String TAG = "MyApplication";

    private static final String PROPERTY_ID = "UA-46031707-1";

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();


    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t;
            if(trackerId == TrackerName.APP_TRACKER){
                t = analytics.newTracker(R.xml.app_tracker);
            } else {
                t =  analytics.newTracker(PROPERTY_ID);
            }
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }

    public Uri getCurrentDocumentPath() {
        return currentDocumentPath;
    }

    public void setCurrentDocumentUri(Uri newDocumentUri) {
        String path = newDocumentUri.getPath();
        Log.i(TAG, "incoming path " + path);

        if(isFileFormatSupported(path)) {
            currentDocumentPath = newDocumentUri;
        } else {
            Toast.makeText(this, R.string.print_file_not_supported, Toast.LENGTH_SHORT).show();
        }


    }

    private boolean isFileFormatSupported(String path){
        if(path.contains(".")) {
            String extension = path.substring(path.lastIndexOf("."));

            if(extension == null || extension.length() == 0){
                return false;
            }

            if(extension.equalsIgnoreCase(".pdf")
                    || (extension.equalsIgnoreCase(".doc"))
                    || (extension.equalsIgnoreCase(".docx"))
                    || (extension.equalsIgnoreCase(".ppt"))
                    || (extension.equalsIgnoreCase(".pptx"))
                    || (extension.equalsIgnoreCase(".odt"))){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }




    }

    public void onCreate(){
        super.onCreate();
        new Storage(getApplicationContext());
    }



}
