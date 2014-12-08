package com.yeokm1.nussocprintandroid.print_activities.printing;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.yeokm1.nussocprintandroid.R;
import com.yeokm1.nussocprintandroid.core.HelperFunctions;
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

    private final int POSITION_CONNECTING = 0;
    private final int POSITION_HOUSEKEEPING = 1;
    private final int POSITION_DOWNLOADING_DOC_CONVERTER = 2;
    private final int POSITION_UPLOADING_PDF_CONVERTER = 3;
    private final int POSITION_UPLOADING_USER_DOC = 4;
    private final int POSITION_CONVERTING_TO_PDF = 5;
    private final int POSITION_TRIM_PDF_TO_PAGE_RANGE = 6;
    private final int POSITION_FORMATTING_PDF = 7;
    private final int POSITION_CONVERTING_TO_POSTSCRIPT = 8;
    private final int POSITION_SENDING_TO_PRINTER = 9;
    private final int POSITION_COMPLETED = 10;

    private ListView printProgress;
    private Button finishButton;

    private String filePath;
    private String filename;
    private String printer;
    private int pagesPerSheet;
    private int startPageRange;
    private int endPageRange;


    private String[] HEADER_TEXT;
    private String SUBTITLE_PROGRESS_TEXT;
    private String SUBTITLE_INDETERMINATE_TEXT;
    private String SUBTITLE_DOWNLOAD_DOC_CONVERTER_SEC_SITE;


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


    private boolean needToConvertDocToPDF = true;
    private boolean needToFormatPDF = true;
    private boolean needToTrimPDFToPageRange = false;

    private boolean nowDownloadingDocConverterFromSecondarySite = false;

    private int pdfConvSize = 0;
    private int pdfConvUploaded = 0;

    private int docToPrintSize = 0;
    private int docToPrintUploaded = 0;

    private int currentProgress = 0;
    private PrintingTask printingTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printing);
        resizeDialogWindow();

        printProgress = (ListView) findViewById(R.id.printing_list);
        finishButton = (Button) findViewById(R.id.printing_button_finish);

        HEADER_TEXT = getResources().getStringArray(R.array.printing_progress_title_text);
        SUBTITLE_PROGRESS_TEXT = getString(R.string.printing_progress_subtitle_progress);
        SUBTITLE_INDETERMINATE_TEXT = getString(R.string.printing_progress_subtitle_progress_indeterminate);
        SUBTITLE_DOWNLOAD_DOC_CONVERTER_SEC_SITE = getString(R.string.printing_progress_doc_converter_secondary_site);

        Intent intent = getIntent();

        filePath = intent.getStringExtra(INTENT_FILE_PATH);

        Uri fileUri =  Uri.parse(filePath);
        filename = fileUri.getLastPathSegment();


        printer = intent.getStringExtra(INTENT_PRINTER_NAME);
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



        printingTask = new PrintingTask(this);
        printingTask.execute();

        refreshList();
    }



    public void refreshList(){
        ArrayList<PrintingProgressItem> items = new ArrayList<PrintingProgressItem>();

        for(int row = 0; row < HEADER_TEXT.length; row++){

            if(row == POSITION_DOWNLOADING_DOC_CONVERTER && !needToConvertDocToPDF
                    || row == POSITION_CONVERTING_TO_PDF && !needToConvertDocToPDF
                    || row == POSITION_UPLOADING_PDF_CONVERTER && !needToFormatPDF
                    || row == POSITION_FORMATTING_PDF && !needToFormatPDF
                    || row == POSITION_TRIM_PDF_TO_PAGE_RANGE && !needToTrimPDFToPageRange){
                //Don't have to do
                continue;
            }

            String title;
            String subtitle;


            String headerText = HEADER_TEXT[row];
            if(row == POSITION_UPLOADING_USER_DOC){
                title = String.format(headerText, filename);
            } else if(row == POSITION_FORMATTING_PDF){
                title = String.format(headerText, pagesPerSheet);
            } else if(row == POSITION_SENDING_TO_PRINTER){
                title = String.format(headerText, printer);
            } else if(row == POSITION_TRIM_PDF_TO_PAGE_RANGE){
                title = String.format(headerText, startPageRange, endPageRange);
            } else {
                title = headerText;
            }

            boolean isThisDone;
            boolean isError;
            boolean isInProgress;

            if(currentProgress > row){
                isThisDone = true;
                isError = false;
                isInProgress = false;
            } else if(row == currentProgress){
                isThisDone = false;
                if(printingTask == null){
                    //Means the operation has ended on the current progress, show a cross to mean an error
                    isError = true;
                    isInProgress = false;
                } else {
                    isError = false;
                    isInProgress = true;
                }
            } else {
                isThisDone = false;
                isError = false;
                isInProgress = false;
            }

            float progressFraction = 0;
            boolean progressIndeterminate = PROGRESS_INDETERMINATE[row];

            if(row == POSITION_UPLOADING_PDF_CONVERTER){
                progressFraction = generateProgressFraction(pdfConvUploaded, pdfConvSize);
                subtitle = generateProgressString(pdfConvUploaded, pdfConvSize, progressFraction);
            } else if(row == POSITION_UPLOADING_USER_DOC) {
                progressFraction = generateProgressFraction(docToPrintUploaded, docToPrintSize);
                subtitle = generateProgressString(docToPrintUploaded, docToPrintSize, progressFraction);
            } else if(row == POSITION_DOWNLOADING_DOC_CONVERTER && nowDownloadingDocConverterFromSecondarySite){
                subtitle = SUBTITLE_DOWNLOAD_DOC_CONVERTER_SEC_SITE;
            } else if(row == POSITION_CONVERTING_TO_PDF || row == POSITION_TRIM_PDF_TO_PAGE_RANGE || row == POSITION_CONVERTING_TO_POSTSCRIPT){
                subtitle = SUBTITLE_INDETERMINATE_TEXT;
            } else {
                subtitle = "";
            }

            PrintingProgressItem item;
            if(progressIndeterminate){
                item = new PrintingProgressItem(title, subtitle, isThisDone, isError, isInProgress);
            } else {
                item = new PrintingProgressItem(title, subtitle, progressFraction, isThisDone, isError, isInProgress);
            }



            items.add(item);
        }

        printProgress.setAdapter(new PrintingProgressItemAdapter(this, items));
    }

    private String generateProgressString(long currentSize, long totalSize, float progressFraction){
        String currentSizeStr = humanReadableByteCount(currentSize, true);
        String totalSizeStr = humanReadableByteCount(totalSize, true);

        String progressStr = String.format(SUBTITLE_PROGRESS_TEXT, currentSizeStr, totalSizeStr, progressFraction * 100);
        return progressStr;
    }

    private float generateProgressFraction(long currentSize, long totalSize){
        double currentSizeDbl = currentSize;
        double totalSizeDbl = totalSize;

        float fraction = 0;
        if(totalSize != 0){
            fraction = currentSize / totalSize;
        }

        return fraction;
    }




    private boolean isFileAPdf(String path){

        String extension = path.substring(path.lastIndexOf("."));
        if(extension.equalsIgnoreCase(".pdf")){
            return true;
        } else {
            return false;
        }
    }

    //http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    class PrintingTask extends ConnectionTask {

        String PDF_CONVERTER_NAME = "nup_pdf";
        String PDF_CONVERTER_FILENAME = "nup_pdf.jar";
        String PDF_CONVERTER_FILEPATH = "socPrint/nup_pdf.jar";

        //This converter is for 6 pages/sheet as nup_pdf cannot generate such a file
        String PDF_CONVERTER_6PAGE_NAME = "Multivalent";
        String PDF_CONVERTER_6PAGE_FILENAME = "Multivalent.jar";
        String PDF_CONVERTER_6PAGE_FILEPATH = "socPrint/Multivalent.jar";

        String DOC_CONVERTER_NAME = "docs-to-pdf-converter-1.7";
        String DOC_CONVERTER_FILENAME = "docs-to-pdf-converter-1.7.jar";
        String DOC_CONVERTER_FILEPATH = "socPrint/docs-to-pdf-converter-1.7.jar";

        String PDF_CONVERTER_MD5 = "C1F8FF3F9DE7B2D2A2B41FBC0085888B";
        String PDF_CONVERTER_6PAGE_MD5 = "813BB651A1CC6EA230F28AAC47F78051";
        String DOC_CONVERTER_MD5 = "1FC140AD8074E333F9082300F4EA38DC";

        String TEMP_DIRECTORY_NO_SLASH = "socPrint";
        String TEMP_DIRECTORY = "socPrint/";

        String[] ERROR_TITLE_TEXT;

        public PrintingTask(Activity activity){
            super(activity);
            ERROR_TITLE_TEXT = getResources().getStringArray(R.array.printing_progress_error_title);
        }

        @Override
        protected String doInBackground(String... params) {

            //Step 0: Connecting to server
            currentProgress = POSITION_CONNECTING;
            publishProgress();
            try {
                startConnection();

                //Step 1: Housekeeping, creating socPrint folder if not yet, delete all files except converters
                if(!isCancelled()){
                    currentProgress = POSITION_HOUSEKEEPING;
                    publishProgress();

                    createDirectory(TEMP_DIRECTORY);

                    String houseKeepingCommand = "find " + TEMP_DIRECTORY + " -type f \\( \\! -name '" + PDF_CONVERTER_FILENAME + "' \\) \\( \\! -name '" + DOC_CONVERTER_FILENAME + "' \\) \\( \\! -name '" + PDF_CONVERTER_6PAGE_FILENAME + "' \\) -exec rm '{}' \\;";

                    connection.runCommand(houseKeepingCommand);
                }

                //Step 2 : Uploading DOC converter
                if(needToConvertDocToPDF && !isCancelled()){
                    currentProgress = POSITION_DOWNLOADING_DOC_CONVERTER;
                    publishProgress();


                    boolean needToUpload = doesThisFileNeedToBeUploaded(DOC_CONVERTER_FILEPATH, DOC_CONVERTER_MD5);

                    if(needToUpload){

                        deleteFile(DOC_CONVERTER_FILEPATH); //If don't do this, wget will download to a another filename.
                        String primaryDownloadCommand = "wget -N http://www.comp.nus.edu.sg/~yeokm1/nus-soc-print-tools/docs-to-pdf-converter-1.7.jar -P " + TEMP_DIRECTORY_NO_SLASH;
                        connection.runCommand(primaryDownloadCommand);

                        if(doesThisFileNeedToBeUploaded(DOC_CONVERTER_FILEPATH, DOC_CONVERTER_MD5)){
                            nowDownloadingDocConverterFromSecondarySite = true;
                            publishProgress();
                            deleteFile(DOC_CONVERTER_FILEPATH);
                            String secondaryDownloadCommand = "wget --no-check-certificate https://github.com/yeokm1/docs-to-pdf-converter/releases/download/v1.7/docs-to-pdf-converter-1.7.jar -P " + TEMP_DIRECTORY_NO_SLASH;
                            connection.runCommand(secondaryDownloadCommand);
                            boolean stillNeedToBeUploaded = doesThisFileNeedToBeUploaded(DOC_CONVERTER_FILEPATH, DOC_CONVERTER_MD5);

                            if(stillNeedToBeUploaded == true){
                                String message = getString(R.string.printing_progress_error_message);
                                throw new Exception(message);
                            }
                        }
                    }

                }

                if(needToFormatPDF && !isCancelled()) {
                    currentProgress = POSITION_UPLOADING_PDF_CONVERTER;
                    publishProgress();
                }




            } catch (Exception e) {
                publishProgress(e.getMessage());
            }

            disconnect();

            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress){
            if(progress.length > 0){
                String errorTitle = ERROR_TITLE_TEXT[currentProgress];

                if(currentProgress == POSITION_TRIM_PDF_TO_PAGE_RANGE){
                    errorTitle = String.format(errorTitle, startPageRange, endPageRange);
                } else if(currentProgress == POSITION_FORMATTING_PDF){
                    errorTitle = String.format(errorTitle, pagesPerSheet);
                }

                HelperFunctions.showAlert(activity, errorTitle, progress[0]);
            }

            refreshList();
        }

        @Override
        protected void onPostExecute(String output){
            super.onPostExecute(output);
            printingTask = null;
            refreshList();
        }
    }

}
