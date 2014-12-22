package com.yeokm1.nussocprintandroid.core;

import android.app.Activity;
import android.app.Application;

import com.flurry.android.FlurryAgent;

import java.util.Map;

/**
 * Created by yeokm1 on 21/12/2014.
 */
public class FlurryFunctions {
    public static String FLURRY_APIKEY = "N23T9M69R2JYGDV7Q5GP";

    public static void activityOnStart(Activity activity){
        if(HelperFunctions.isOS10AndAbove()) {
            FlurryAgent.onStartSession(activity, FlurryFunctions.FLURRY_APIKEY);
        }
    }

    public static void activityOnStop(Activity activity){
        if(HelperFunctions.isOS10AndAbove()) {
            FlurryAgent.onEndSession(activity);
        }
    }

    public static void initFlurry(Application app){

        if(HelperFunctions.isOS10AndAbove()) {
            // configure Flurry
            FlurryAgent.setLogEnabled(true);

            // init Flurry
            FlurryAgent.init(app, FLURRY_APIKEY);
        }

    }

    public static void logTimedEvent(String eventName,  Map<String, String> eventParams){
        if(HelperFunctions.isOS10AndAbove()) {
            FlurryAgent.logEvent(eventName, eventParams, true);
        }
    }

    public static void endTimedEvent(String eventName){
        if(HelperFunctions.isOS10AndAbove()) {
            FlurryAgent.endTimedEvent(eventName);
        }

    }

}
