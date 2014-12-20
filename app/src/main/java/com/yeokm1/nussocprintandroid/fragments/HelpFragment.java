package com.yeokm1.nussocprintandroid.fragments;


import android.content.Intent;
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

public class HelpFragment extends Fragment {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

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
                emailGen.appendAppDetailsToContent(dateFormat);

                String emailEndText = getString(R.string.help_support_email_end_text);
                emailGen.appendContent(emailEndText);

                Intent intent = emailGen.generateIntentWithNewTaskFlag();
                boolean status = emailGen.sendIntent(getActivity(), intent);
            }
        });
        return view;
    }


    private String generateHelpText(){
        String textFormat = getString(R.string.help_text);
        GetInfoSummary info = new GetInfoSummary(getActivity());
        String helpText = String.format(textFormat, info.getPackageVersionAndName(dateFormat));
        return helpText;
    }


    private String getPackageVersion(){
        GetInfoSummary infoSummary = new GetInfoSummary(getActivity().getApplicationContext());
        return infoSummary.getPackageVersionAndName(dateFormat);
    }


}
