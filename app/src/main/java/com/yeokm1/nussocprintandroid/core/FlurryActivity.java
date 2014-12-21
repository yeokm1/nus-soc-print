package com.yeokm1.nussocprintandroid.core;

import android.app.Activity;

import com.flurry.android.FlurryAgent;

/**
 * Created by yeokm1 on 21/12/2014.
 */
public abstract class FlurryActivity extends Activity{
    @Override
    protected void onStart(){
        super.onStart();
        if(HelperFunctions.isOS10AndAbove()) {
            FlurryAgent.onStartSession(this, FlurryFunctions.FLURRY_APIKEY);
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(HelperFunctions.isOS10AndAbove()) {
            FlurryAgent.onEndSession(this);
        }
    }
}
