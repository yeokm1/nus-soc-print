package com.yeokm1.nussocprintandroid.print_activities;

import android.app.Activity;
import android.view.WindowManager;

/**
 * Created by yeokm1 on 4/12/2014.
 */
public abstract class FatDialogActivity extends Activity {

    protected void resizeDialogWindow(){
        //To maximise screen width as ICS Holo Dialog is rather "skinny"
        final int screen_width = getResources().getDisplayMetrics().widthPixels;
        final int new_window_width = screen_width * 98 / 100;
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.width = Math.max(layout.width, new_window_width);
        getWindow().setAttributes(layout);
    }
}
