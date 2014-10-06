package com.yeokm1.nussocprint.fragments;

import android.os.Bundle;

import com.yeokm1.nussocprint.R;
/**
 * Created by yeokm1 on 6/10/2014.
 */
public class SettingsFragment extends PreferenceListFragment{


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.preference_layout);


    }




}
