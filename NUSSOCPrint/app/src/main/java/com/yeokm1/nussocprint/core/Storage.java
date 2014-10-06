package com.yeokm1.nussocprint.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.yeokm1.nussocprint.R;

/**
 * Created by yeokm1 on 6/10/2014.
 */
public class Storage {

    private static Storage theOne = null;
    private Context context;
    private SharedPreferences sharedPreferences;

    //Initialised in MyApplication for the first time
    protected Storage(Context context) {
        theOne = this;
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static Storage getInstance() {
        return theOne;
    }

    public String getUsername(){
        String key = context.getString(R.string.preference_username_key);
        String output = sharedPreferences.getString(key, "");
        return output;
    }

    public String getPassword(){
        String key = context.getString(R.string.preference_password_key);
        String output = sharedPreferences.getString(key, "");
        return output;
    }

    public String getPrinter(){
        String key = context.getString(R.string.preference_printer_key);
        String output = sharedPreferences.getString(key, "");
        return output;
    }

    public String getServer(){
        String key = context.getString(R.string.preference_server_key);
        String output = sharedPreferences.getString(key, "");
        return output;
    }


}
