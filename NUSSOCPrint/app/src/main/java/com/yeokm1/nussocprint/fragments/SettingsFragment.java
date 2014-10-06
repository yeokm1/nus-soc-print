package com.yeokm1.nussocprint.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

import com.yeokm1.nussocprint.R;
/**
 * Created by yeokm1 on 6/10/2014.
 */
public class SettingsFragment extends PreferenceListFragment{


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.fragment_settings);

        Preference resetLink = findPreference(getString(R.string.settings_reset_key));
        resetLink.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                String url = getString(R.string.settings_reset_link);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
                return true;
            }
        });

    }




}
