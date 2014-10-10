package com.yeokm1.nussocprint.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.yeokm1.nussocprint.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        String key = context.getString(R.string.settings_username_key);
        String output = sharedPreferences.getString(key, "");
        return output;
    }

    public String getPassword(){
        String key = context.getString(R.string.settings_password_key);
        String output = sharedPreferences.getString(key, "");
        return output;
    }

    public String getPrinter(){
        String key = context.getString(R.string.settings_printer_key);
        String output = sharedPreferences.getString(key, "");
        return output;
    }

    public String getServer(){
        String key = context.getString(R.string.settings_server_key);
        String output = sharedPreferences.getString(key, "");
        if(output.length() == 0){
            return context.getString(R.string.misc_server);
        } else {
            return output;
        }
    }

    public List<String> getPrinterList(){
        String customPrinter = getPrinter();
        String[] printerArray =  context.getResources().getStringArray(R.array.printer_names);
        List<String> printerList = new ArrayList<String>(Arrays.asList(printerArray));

        if(customPrinter.length() != 0){
            List<String> newList = new ArrayList<String>();
            newList.add(customPrinter);
            newList.addAll(printerList);
            return newList;
        } else {
            return printerList;
        }
    }


}
