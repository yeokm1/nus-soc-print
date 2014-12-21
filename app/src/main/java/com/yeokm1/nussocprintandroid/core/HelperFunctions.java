package com.yeokm1.nussocprintandroid.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;

import com.yeokm1.nussocprintandroid.R;

/**
 * Created by yeokm1 on 8/12/2014.
 */
public class HelperFunctions {

    public static boolean isOS10AndAbove(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1){
            return true;
        } else {
            return false;
        }
    }

    public static void showAlert(Activity activity, String title, String message){
        if(activity != null && !activity.isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(activity.getString(R.string.misc_ok), null);
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public static void showYesNoAlert(Activity activity, String title, String message, DialogInterface.OnClickListener listener) {
        if (activity != null && !activity.isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(R.string.misc_yes, listener)
                    .setNegativeButton(R.string.misc_no, listener);
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
