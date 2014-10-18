package com.yeokm1.nussocprintandroid.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import com.yeokm1.nussocprintandroid.R;
import com.yeokm1.nussocprintandroid.print_activities.StatusActivity;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PrintFragment extends Fragment {


    private Spinner printerSpinner;
    private Spinner pagesPerSheetSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_print, container, false);

        Button statusButton = (Button) view.findViewById(R.id.print_status_button);
        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startStatusActivity();
            }
        });

        pagesPerSheetSpinner = (Spinner) view.findViewById(R.id.printer_page_sheet_spinner);
        printerSpinner = (Spinner) view.findViewById(R.id.print_printer_names);


        String[] printerArray = getResources().getStringArray(R.array.printer_names);

        ArrayAdapter<String> printerAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.item_simple , printerArray);
        printerSpinner.setAdapter(printerAdapter);

        String[] pagesArray = getResources().getStringArray(R.array.printer_page_sheet_options);
        ArrayAdapter<String> pagesAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.item_simple , pagesArray);

        pagesPerSheetSpinner.setAdapter(pagesAdapter);
        return view;
    }


    public void startStatusActivity(){
        Intent intent = new Intent(getActivity(), StatusActivity.class);
        startActivity(intent);
    }





}
