package com.yeokm1.nussocprint.network;

import android.app.Activity;
import android.os.AsyncTask;

import com.yeokm1.nussocprint.R;
import com.yeokm1.nussocprint.core.Storage;

/**
 * Created by yeokm1 on 6/10/2014.
 */
public abstract class ConnectionTask extends AsyncTask<String, String, String>{

    protected Activity activity;
    protected SSHConnectivity connection;

    public ConnectionTask(Activity activity){
        this.activity = activity;
    }

    protected void startConnection() throws Exception{
        Storage storage = Storage.getInstance();
        String username = storage.getUsername();
        String password = storage.getPassword();
        String server = storage.getServer();

        if(username.length() == 0 || password.length() == 0 || server.length() == 0){
            String missingCredentials = activity.getString(R.string.misc_missing_credentials);
            throw new Exception(missingCredentials);
        }

        connection = new SSHConnectivity(server,username, password, activity);
        connection.connect();
    }

    protected void disconnect(){
        if(connection != null){
            connection.disconnect();
        }
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        keepScreenOn();
    }

    @Override
    protected void onPostExecute(String output){
        super.onPostExecute(output);
        stopKeepScreenOn();
    }
    @Override
    protected void onCancelled(){
        super.onCancelled();
        stopKeepScreenOn();
    }

    protected void keepScreenOn(){
        activity.getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    protected void stopKeepScreenOn(){
        activity.getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


}
