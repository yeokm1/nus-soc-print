package com.yeokm1.nussocprintandroid.print_activities;

import android.os.Bundle;

import com.yeokm1.nussocprintandroid.R;

public class PrintingActivity extends FatDialogActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printing);
        resizeDialogWindow();
    }

}
