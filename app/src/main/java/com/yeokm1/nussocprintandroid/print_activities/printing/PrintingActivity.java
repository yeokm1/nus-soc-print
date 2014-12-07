package com.yeokm1.nussocprintandroid.print_activities.printing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.yeokm1.nussocprintandroid.R;
import com.yeokm1.nussocprintandroid.network.ConnectionTask;
import com.yeokm1.nussocprintandroid.print_activities.FatDialogActivity;

import java.util.ArrayList;

public class PrintingActivity extends FatDialogActivity {

    public static final String INTENT_FILE_PATH = "filePath";
    public static final String INTENT_PRINTER_NAME = "printerName";
    public static final String INTENT_PAGES_PER_SHEET = "pagesPerSheet";
    public static final String INTENT_PAGE_START_RANGE = "startRange";
    public static final String INTENT_PAGE_END_RANGE = "endRange";

    private static final int INVALID_INTENT_INT = 0;

    private ListView printProgress;
    private Button finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printing);
        resizeDialogWindow();

        printProgress = (ListView) findViewById(R.id.printing_list);
        finishButton = (Button) findViewById(R.id.printing_button_finish);



        Intent intent = getIntent();

        String filePath = intent.getStringExtra(INTENT_FILE_PATH);
        String printerName = intent.getStringExtra(INTENT_PRINTER_NAME);
        int pagesPerSheet = intent.getIntExtra(INTENT_PAGES_PER_SHEET, INVALID_INTENT_INT);
        int startRange = intent.getIntExtra(INTENT_PAGE_START_RANGE, INVALID_INTENT_INT);
        int endRange = intent.getIntExtra(INTENT_PAGE_END_RANGE, INVALID_INTENT_INT);



        ArrayList<PrintingProgressItem> items = new ArrayList<PrintingProgressItem>();
        for (int i = 0; i < 5; i++) {
            String title = "Uploading DOC converter";
            String description = "This could take a while...";
            PrintingProgressItem item = new PrintingProgressItem(title, description, true, false);
            items.add(item);
        }

        for (int i = 0; i < 5; i++) {
            String title = "Uploading PDF converter";
            String description = "This could take a while...";
            PrintingProgressItem item = new PrintingProgressItem(title, description, 50, false, true);
            items.add(item);
        }

        printProgress.setAdapter(new PrintingProgressItemAdapter(this, items));

    }


    class PrintingTask extends ConnectionTask {

        public PrintingTask(Activity activity){
            super(activity);
        }

        @Override
        protected String doInBackground(String... params) {
            return null;
        }
    }

}
