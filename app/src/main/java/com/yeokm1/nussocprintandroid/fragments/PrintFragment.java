package com.yeokm1.nussocprintandroid.fragments;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.yeokm1.nussocprintandroid.R;
import com.yeokm1.nussocprintandroid.core.MyApplication;
import com.yeokm1.nussocprintandroid.core.Storage;
import com.yeokm1.nussocprintandroid.print_activities.PrintingActivity;
import com.yeokm1.nussocprintandroid.print_activities.StatusActivity;

import java.util.List;

public class PrintFragment extends Fragment {


    private Spinner printerSpinner;
    private Spinner pagesPerSheetSpinner;

    private EditText pageRangeStart;
    private EditText pageRangeEnd;

    private boolean pageRange = false;

    private TextView filePathView;

    private static final int REQUEST_CHOOSER = 1234;
    private static final String TAG = "PrintFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_print, container, false);

        filePathView = (TextView) view.findViewById(R.id.print_filename_textview);
        pagesPerSheetSpinner = (Spinner) view.findViewById(R.id.printer_page_sheet_spinner);
        printerSpinner = (Spinner) view.findViewById(R.id.print_printer_names);

        pageRangeStart = (EditText) view.findViewById(R.id.print_page_range_start);
        pageRangeEnd = (EditText) view.findViewById(R.id.print_page_range_end);

        List<String> printerList = Storage.getInstance().getPrinterList();
        ArrayAdapter<String> printerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_simple , printerList);
        printerSpinner.setAdapter(printerAdapter);


        String[] pagesArray = getResources().getStringArray(R.array.printer_page_sheet_options);
        ArrayAdapter<String> pagesAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.item_simple , pagesArray);

        pagesPerSheetSpinner.setAdapter(pagesAdapter);


        Button statusButton = (Button) view.findViewById(R.id.print_status_button);
        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startStatusActivity();
            }
        });

        Button printButton = (Button) view.findViewById(R.id.print_print_button);
        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPrinting();
            }
        });

        Button browseButton = (Button) view.findViewById(R.id.print_browse_button);
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFileChooser();
            }
        });


        RadioGroup pageRangeRadioGroup = (RadioGroup) view.findViewById(R.id.print_page_range_radiogroup);
        pageRangeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.print_page_range_all_text: showRangeFields(false);
                        break;
                    case R.id.print_page_range_custom_text:
                        //Fall through
                    default : showRangeFields(true);
                }

            }
        });

        showRangeFields(false);

        refreshDocumentPathIntoTextView();

        return view;
    }

    public void showRangeFields(boolean showFields){
        pageRange = showFields;
        pageRangeStart.setEnabled(showFields);
        pageRangeEnd.setEnabled(showFields);
    }

    public void startFileChooser(){
        Intent getContentIntent = FileUtils.createGetContentIntent();
        Intent intent = Intent.createChooser(getContentIntent, getString(R.string.print_select_a_document));
        startActivityForResult(intent, REQUEST_CHOOSER);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHOOSER:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    ((MyApplication) getActivity().getApplication()).setCurrentDocumentUri(uri);
                    refreshDocumentPathIntoTextView();
                }
                break;
        }
    }


    private void refreshDocumentPathIntoTextView(){
        Uri filePath =  ((MyApplication) getActivity().getApplication()).getCurrentDocumentPath();
        if(filePath != null) {

            String filename = filePath.getLastPathSegment();
            filePathView.setText(filename);
        }
    }




    public void startStatusActivity(){
        Intent intent = new Intent(getActivity(), StatusActivity.class);
        startActivity(intent);
    }

    public void startPrinting(){
        Storage storage = Storage.getInstance();
        String username = storage.getUsername();
        String password = storage.getPassword();
        String server = storage.getServer();

        if(username.length() == 0 || password.length() == 0){
            showToast(R.string.misc_missing_credential);
            return;
        }

        if(server.length() == 0){
            showToast(R.string.misc_missing_server);
            return;
        }

        Uri filePathUri =  ((MyApplication) getActivity().getApplication()).getCurrentDocumentPath();

        if(filePathUri == null){
            showToast(R.string.print_no_file_selected_yet);
            return;
        }

        String filePath = filePathUri.getPath();

        String printerName = printerSpinner.getSelectedItem().toString();
        String pagesPerSheetStr = pagesPerSheetSpinner.getSelectedItem().toString();

        int pagesPerSheet = Integer.parseInt(pagesPerSheetStr);

        String startRangeStr = pageRangeStart.getText().toString();
        String endRangeStr = pageRangeEnd.getText().toString();

        Intent intent = new Intent(getActivity(), PrintingActivity.class);

        intent.putExtra(PrintingActivity.INTENT_FILE_PATH, filePath);
        intent.putExtra(PrintingActivity.INTENT_PRINTER_NAME, printerName);
        intent.putExtra(PrintingActivity.INTENT_PAGES_PER_SHEET, pagesPerSheet);


        if(pageRange){

            try {

                int startNumber = Integer.parseInt(startRangeStr);
                int endNumber = Integer.parseInt(endRangeStr);

                if(startNumber == 0 || endNumber == 0
                        || startNumber > endNumber) {
                    throw new NumberFormatException();
                }

                intent.putExtra(PrintingActivity.INTENT_PAGE_START_RANGE, startNumber);
                intent.putExtra(PrintingActivity.INTENT_PAGE_END_RANGE, endNumber);

            } catch(NumberFormatException e){
                showToast(R.string.print_invalid_page_range);
                return;
            }
        }


        startActivity(intent);

    }


    public void showToast(int stringId){
        Toast.makeText(getActivity(), stringId, Toast.LENGTH_SHORT).show();
    }

}
