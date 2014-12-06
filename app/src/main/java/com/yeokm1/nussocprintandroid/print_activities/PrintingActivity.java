package com.yeokm1.nussocprintandroid.print_activities;

import android.content.Intent;
import android.os.Bundle;

import com.yeokm1.nussocprintandroid.R;

public class PrintingActivity extends FatDialogActivity {

    public static final String INTENT_FILE_PATH = "filePath";
    public static final String INTENT_PRINTER_NAME = "printerName";
    public static final String INTENT_PAGES_PER_SHEET = "pagesPerSheet";
    public static final String INTENT_PAGE_START_RANGE = "startRange";
    public static final String INTENT_PAGE_END_RANGE = "endRange";

    private static final int INVALID_INTENT_INT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printing);
        resizeDialogWindow();

        Intent intent = getIntent();

        String filePath = intent.getStringExtra(INTENT_FILE_PATH);
        String printerName = intent.getStringExtra(INTENT_PRINTER_NAME);
        int pagesPerSheet = intent.getIntExtra(INTENT_PAGES_PER_SHEET, INVALID_INTENT_INT);
        int startRange = intent.getIntExtra(INTENT_PAGE_START_RANGE, INVALID_INTENT_INT);
        int endRange = intent.getIntExtra(INTENT_PAGE_END_RANGE, INVALID_INTENT_INT);


    }

}
