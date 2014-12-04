package com.yeokm1.nussocprintandroid.fragments;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.yeokm1.nussocprintandroid.R;
import com.yeokm1.nussocprintandroid.core.MyApplication;
import com.yeokm1.nussocprintandroid.core.Storage;
import com.yeokm1.nussocprintandroid.print_activities.StatusActivity;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PrintFragment extends Fragment {


    private Spinner printerSpinner;
    private Spinner pagesPerSheetSpinner;
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


        Button browseButton = (Button) view.findViewById(R.id.print_browse_button);
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFileChooser();
            }
        });

        return view;
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
                    String path = FileUtils.getPath(getActivity(), uri);
                    obtainedDocumentPath(path);
                }
                break;
        }
    }

    public void obtainedDocumentPath(String path){
        Log.i(TAG, "incoming path " + path);
        ((MyApplication) getActivity().getApplication()).setCurrentDocumentPath(path);
        refreshDocumentPathIntoTextView();
    }


    private void refreshDocumentPathIntoTextView(){
        String filePath =  ((MyApplication) getActivity().getApplication()).getCurrentDocumentPath();
        filePathView.setText(filePath);
    }




    public void startStatusActivity(){
        Intent intent = new Intent(getActivity(), StatusActivity.class);
        startActivity(intent);
    }





}
