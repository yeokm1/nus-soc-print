package com.yeokm1.nussocprintandroid;

import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.yeokm1.nussocprintandroid.fragments.HelpFragment;
import com.yeokm1.nussocprintandroid.fragments.PreferenceListFragment.OnPreferenceAttachedListener;
import com.yeokm1.nussocprintandroid.fragments.PrintFragment;
import com.yeokm1.nussocprintandroid.fragments.QuotaFragment;
import com.yeokm1.nussocprintandroid.fragments.SettingsFragment;

import java.util.Locale;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, OnPreferenceAttachedListener {



    private static final int FRAGMENT_PRINT_NUMBER = 0;
    private static final int FRAGMENT_QUOTA_NUMBER = 1;
    private static final int FRAGMENT_SETTINGS_NUMBER = 2;
    private static final int FRAGMENT_HELP_NUMBER = 3;

    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentStatePagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position){
                case FRAGMENT_PRINT_NUMBER :
                    return new PrintFragment();
                case FRAGMENT_QUOTA_NUMBER :
                    return new QuotaFragment();
                case FRAGMENT_SETTINGS_NUMBER :
                    return new SettingsFragment();
                case FRAGMENT_HELP_NUMBER :
                    return new HelpFragment();
                default :
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case FRAGMENT_PRINT_NUMBER:
                    return getString(R.string.tab_print).toUpperCase(l);
                case FRAGMENT_QUOTA_NUMBER:
                    return getString(R.string.tab_quota).toUpperCase(l);
                case FRAGMENT_SETTINGS_NUMBER:
                    return getString(R.string.tab_settings).toUpperCase(l);
                case FRAGMENT_HELP_NUMBER:
                    return getString(R.string.tab_help).toUpperCase(l);
            }
            return null;
        }
    }


    @Override
    public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
        //Dummy function used by PreferenceListFragment
    }

}
