package com.yeokm1.nussocprintandroid.fragments;


import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yeokhengmeng.craftsupportemailintent.CraftIntentEmail;
import com.yeokhengmeng.craftsupportemailintent.CraftSupportEmail;
import com.yeokhengmeng.craftsupportemailintent.GetInfoSummary;
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
        helpTextView.setMovementMethod(new ScrollingMovementMethod());
        helpTextView.setText(helpText);

        Button sourceCodeButton = (Button) view.findViewById(R.id.help_source_code_button);
        sourceCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/yeokm1/nus-soc-print"));
                startActivity(browserIntent);
            }
        });


        final Button emailButton = (Button) view.findViewById(R.id.help_problems_button);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CraftSupportEmail emailGen = new CraftSupportEmail(getActivity(), "yeokm1@gmail.com", "NUS SOC Print Android");
                CraftIntentEmail emailIntent = new CraftIntentEmail();
                emailGen.appendMinimumDetailsToContent();
                emailGen.appendAppDetailsToContent();

                String emailEndText = getString(R.string.help_support_email_end_text);
                emailGen.appendContent(emailEndText);

                Intent intent = emailGen.generateIntentWithNewTaskFlag();
                boolean status = emailGen.sendIntent(getActivity(), intent);
            }
        });
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
        GetInfoSummary infoSummary = new GetInfoSummary(getActivity().getApplicationContext());
        return infoSummary.getPackageVersionAndName();
    }


}
