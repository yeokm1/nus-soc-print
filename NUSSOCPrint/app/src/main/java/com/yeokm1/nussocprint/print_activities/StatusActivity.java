package com.yeokm1.nussocprint.print_activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yeokm1.nussocprint.R;
import com.yeokm1.nussocprint.core.Storage;
import com.yeokm1.nussocprint.network.ConnectionTask;

import java.util.List;

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


        final String FORMAT_PRINTER_COMMAND = "lpq -P %s";
        final String FORMAT_PRINTER_OUTPUT = "%s : %s\n";
        final String FORMAT_PRINTER_NO_OUTPUT = "%s : No Output\n";
        final String TEXT_NO_JOB = "Print Queue Empty\n";

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
                List<String> printerList =  Storage.getInstance().getPrinterList();

                for(String printer : printerList){
                    if(isCancelled()){
                        break;
                    }

                    String command = String.format(FORMAT_PRINTER_COMMAND, printer);
                    String commandOutput = connection.runCommand(command);
                    String lineToShowToUI;

                    if("no entries\n".equals(commandOutput)){
                        lineToShowToUI = String.format(FORMAT_PRINTER_OUTPUT, printer, TEXT_NO_JOB);
                    } else {
                        lineToShowToUI = String.format(FORMAT_PRINTER_OUTPUT, printer, commandOutput);
                    }

                    builder.append(lineToShowToUI);
                    publishProgress(builder.toString());

                }

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
