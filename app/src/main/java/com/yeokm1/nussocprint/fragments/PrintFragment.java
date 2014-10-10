package com.yeokm1.nussocprint.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yeokm1.nussocprint.R;
import com.yeokm1.nussocprint.print_activities.StatusActivity;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PrintFragment extends Fragment {


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


        return view;
    }


    public void startStatusActivity(){
        Intent intent = new Intent(getActivity(), StatusActivity.class);
        startActivity(intent);
    }





}
