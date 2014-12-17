package com.yeokm1.nussocprintandroid.fragments;


import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yeokm1.nussocprintandroid.R;

import java.text.SimpleDateFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class HelpFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_help, container, false);
        String helpText = generateHelpText();
        TextView helpTextView = (TextView) view.findViewById(R.id.help_textview);
        helpTextView.setText(helpText);
        return view;
    }


    String generateHelpText(){
        String textFormat = getString(R.string.help_text);
        String compileTime = getCompileDateTime();
        String packageDetails = getPackageVersion();
        String helpText = String.format(textFormat, packageDetails, compileTime);
        return helpText;
    }

    String getCompileDateTime(){
        try{
            ApplicationInfo ai = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), 0);
            ZipFile zf = new ZipFile(ai.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            long time = ze.getTime();
            String s = new SimpleDateFormat("dd MMM yyyy").format(new java.util.Date(time));
            zf.close();
            return s;
        }catch(Exception e){
        }

        return "";
    }


    String getPackageVersion(){
        try {
            String packageName = getActivity().getApplicationContext().getPackageName();
            PackageInfo pi = getActivity().getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
            int versionCode = pi.versionCode;
            String versionName = pi.versionName;
            return  versionName + "," + versionCode;
        } catch (Exception e) {
            return "";
        }
    }


}
