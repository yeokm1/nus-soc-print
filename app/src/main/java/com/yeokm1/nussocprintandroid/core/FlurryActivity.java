package com.yeokm1.nussocprintandroid.core;

import android.app.Activity;

/**
 * Created by yeokm1 on 21/12/2014.
 */
public abstract class FlurryActivity extends Activity{
    @Override
    protected void onStart(){
        super.onStart();
        FlurryFunctions.activityOnStart(this);
    }

    @Override
    protected void onStop(){
        super.onStop();
        FlurryFunctions.activityOnStop(this);
    }
}
