package com.yeokm1.nussocprintandroid.print_activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yeokm1.nussocprintandroid.R;
import com.yeokm1.nussocprintandroid.core.Storage;
import com.yeokm1.nussocprintandroid.network.ConnectionTask;

import java.util.List;

public class StatusActivity extends FatDialogActivity {

    private RefreshStatusTask refreshTask;
    private DeleteTask deleteTask;
    private TextView outputView;

    private Button refreshStatusButton;
    private Button deleteButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        resizeDialogWindow();

        outputView = (TextView) findViewById(R.id.status_output);
        outputView.setMovementMethod(new ScrollingMovementMethod());
        outputView.setClickable(false);     // Both Clickable and LongClickable
        outputView.setLongClickable(false); // after setting the MovementMeathod

        refreshStatusButton = (Button) findViewById(R.id.status_refresh_button);
        refreshStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRefreshTask();
            }
        });


        deleteButton = (Button) findViewById(R.id.status_delete_jobs_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDeleteTask();
            }
        });


    }

    public void startRefreshTask(){
        stopDeleteTask();

        if(refreshTask == null){
            refreshTask = new RefreshStatusTask(this);
            refreshTask.execute("");
        }

    }

    public void startDeleteTask(){
        stopRefreshTask();

        if(deleteTask == null){
            deleteTask = new DeleteTask(this);
            deleteTask.execute("");
        }

    }

    public void stopDeleteTask(){
        if(deleteTask != null){
            deleteTask.cancel(false);
            deleteTask = null;

        }
    }

    public void stopRefreshTask(){
        if(refreshTask != null){
            refreshTask.cancel(false);
            refreshTask = null;
        }
    }

    @Override
    public void onBackPressed(){
        stopRefreshTask();
        stopDeleteTask();
        finish();
    }
    
    class RefreshStatusTask extends ConnectionTask {


        final String FORMAT_PRINTER_COMMAND = "lpq -P %s";
        final String FORMAT_PRINTER_OUTPUT = getString(R.string.status_refresh_format_printer_output);
        final String TEXT_NO_JOB = getString(R.string.status_refresh_printer_queue_empty);

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
            if(outputView != null){
                outputView.setText(progress[0]);
            }
        }

        @Override
        protected void onPostExecute(String output){
            super.onPostExecute(output);
            refreshTask = null;
        }


    }



    class DeleteTask extends ConnectionTask {

        final String FORMAT_PRINTER_COMMAND = "lprm -P %s -";
        final String FORMAT_DELETION_OUTPUT = getString(R.string.status_delete_format_status);
        final String DELETION_COMMAND_SENT_TO_ALL = getString(R.string.status_delete_finished);

        public DeleteTask(Activity activity){
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
                    connection.runCommand(command);

                    String lineToShowToUI = String.format(FORMAT_DELETION_OUTPUT, printer);

                    builder.append(lineToShowToUI);
                    publishProgress(builder.toString());

                }

                if(!isCancelled()){
                    builder.append(DELETION_COMMAND_SENT_TO_ALL);
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
            if(outputView != null){
                outputView.setText(progress[0]);
            }
        }

        @Override
        protected void onPostExecute(String output){
            super.onPostExecute(output);
            deleteTask = null;
        }


    }
}
