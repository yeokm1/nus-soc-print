package com.yeokm1.nussocprint.print_activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yeokm1.nussocprint.R;
import com.yeokm1.nussocprint.network.ConnectionTask;

public class StatusActivity extends Activity {

    private RefreshStatusTask refreshTask;
    private DeleteTask deleteTask;
    private TextView outputView;

    private Button refreshStatusButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        outputView = (TextView) findViewById(R.id.status_output);

        refreshStatusButton = (Button) findViewById(R.id.status_refresh_button);
        refreshStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRefreshTask();
            }
        });

    }

    public void startRefreshTask(){
        if(deleteTask != null){
            deleteTask.cancel(true);
        }

        if(refreshTask == null){
            refreshTask = new RefreshStatusTask(this);
            refreshTask.execute("");
        }

    }

    public void startDeleteTask(){
        if(refreshTask != null){
            refreshTask.cancel(true);
        }

        if(deleteTask == null){
            deleteTask = new DeleteTask(this);
            deleteTask.execute("");
        }

    }

    
    class RefreshStatusTask extends ConnectionTask {

        public RefreshStatusTask(Activity activity){
            super(activity);
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder builder = new StringBuilder();

            String connecting = activity.getString(R.string.misc_connecting_to_server);
            publishProgress(connecting);
            try {
                startConnection();
            } catch (Exception e){
                publishProgress(e.getMessage());
            }

            disconnect();
            return null;
        }


        @Override
        protected void onProgressUpdate(String... progress){
            outputView.setText(progress[0]);
        }
        
        @Override
        protected void onPostExecute(String output){
            super.onPostExecute(output);
            refreshTask = null;
        }


    }



    class DeleteTask extends ConnectionTask {

        public DeleteTask(Activity activity){
            super(activity);
        }

        @Override
        protected String doInBackground(String... params) {
            return null;
        }


    }
}
