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


    private String filePath;
    private String printerName;
    private int pagesPerSheet;
    private int startPageRange;
    private int endPageRange;

    private final int POSITION_CONNECTING = 0;
    private final int POSITION_HOUSEKEEPING = 1;
    private final int POSITION_DOWNLOADING_DOC_CONVERTER = 2;
    private int POSITION_UPLOADING_PDF_CONVERTER = 3;
    private int POSITION_UPLOADING_USER_DOC = 4;
    private int POSITION_CONVERTING_TO_PDF = 5;
    private int POSITION_TRIM_PDF_TO_PAGE_RANGE = 6;
    private int POSITION_FORMATTING_PDF = 7;
    private int POSITION_CONVERTING_TO_POSTSCRIPT = 8;
    private int POSITION_SENDING_TO_PRINTER = 9;
    private int POSITION_COMPLETED = 10;

    private String[] PROGRESS_TEXT;

    private boolean[] PROGRESS_INDETERMINATE =
            {true
                    ,true
                    ,true
                    ,false
                    ,false
                    ,true
                    ,true
                    ,true
                    ,true
                    ,true};


    boolean needToConvertDocToPDF = true;
    boolean needToFormatPDF = true;
    boolean needToTrimPDFToPageRange = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printing);
        resizeDialogWindow();

        printProgress = (ListView) findViewById(R.id.printing_list);
        finishButton = (Button) findViewById(R.id.printing_button_finish);

        PROGRESS_TEXT = getResources().getStringArray(R.array.status_title_text);

        Intent intent = getIntent();

        filePath = intent.getStringExtra(INTENT_FILE_PATH);
        printerName = intent.getStringExtra(INTENT_PRINTER_NAME);
        pagesPerSheet = intent.getIntExtra(INTENT_PAGES_PER_SHEET, INVALID_INTENT_INT);
        startPageRange = intent.getIntExtra(INTENT_PAGE_START_RANGE, INVALID_INTENT_INT);
        endPageRange = intent.getIntExtra(INTENT_PAGE_END_RANGE, INVALID_INTENT_INT);


        if(pagesPerSheet == 1){
            needToFormatPDF = false;
        }


        if(isFileAPdf(filePath)){
            needToConvertDocToPDF = false;
        }

        if(startPageRange > 0){
            needToTrimPDFToPageRange = true;
        }

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
            PrintingProgressItem item = new PrintingProgressItem(title, description, 0.5f, false, true);
            items.add(item);
        }

    }



    public void refreshList(){
        ArrayList<PrintingProgressItem> items = new ArrayList<PrintingProgressItem>();






        printProgress.setAdapter(new PrintingProgressItemAdapter(this, items));
    }


    private boolean isFileAPdf(String path){

        String extension = path.substring(path.lastIndexOf("."));
        if(extension.equalsIgnoreCase(".pdf")){
            return true;
        } else {
            return false;
        }
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
