package com.yeokm1.nussocprintandroid.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yeokm1.nussocprintandroid.R;
import com.yeokm1.nussocprintandroid.core.Storage;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class QuotaFragment extends Fragment {


    private TextView outputTextView;
    private Button refreshButton;
    private final String TAG = "Quota";
    private final String QUOTA_REGEX = "<tr><td bgcolor=.*?>(.*?)</td><td bgcolor=.*?>(.*?)</td></tr>";
    private Pattern patternQuotaPage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quota, container, false);

        outputTextView = (TextView) view.findViewById(R.id.fragment_quota_output);
        outputTextView.setMovementMethod(new ScrollingMovementMethod());
        refreshButton = (Button) view.findViewById(R.id.fragment_quota_refresh_button);
        patternQuotaPage = Pattern.compile(QUOTA_REGEX);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshPrintQuota();
            }
        });

        refreshPrintQuota();

        return view;
    }


    public void refreshPrintQuota(){
        Storage storage = Storage.getInstance();

        String username = storage.getUsername();
        String password = storage.getPassword();

        if(username.length() == 0 || password.length() == 0){
            Toast.makeText(getActivity(), R.string.misc_missing_credential, Toast.LENGTH_SHORT).show();
            return;
        }

        String loginURL = getString(R.string.quota_login_url);
        String quotaPageRelURL = getString(R.string.quota_page_relative_url);

        outputTextView.setText(R.string.quota_refresh_text);

        new AsyncTask<String, String, String>() {

            @Override
            protected String doInBackground(String... params) {



                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(params[0]);

                try {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("destination", params[1]));
                    nameValuePairs.add(new BasicNameValuePair("credential_0", params[2]));
                    nameValuePairs.add(new BasicNameValuePair("credential_1", params[3]));
                    nameValuePairs.add(new BasicNameValuePair("AuthType", "AuthDBICookieHandler"));
                    nameValuePairs.add(new BasicNameValuePair("AuthName", "mysoc"));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpclient.execute(httppost);
                    String responseOutput = inputStreamToString(response.getEntity().getContent());



                    return responseOutput;

                } catch (Exception e) {
                    return "";
                }


            }

            @Override
            public void onPostExecute(String output){
                if(isAdded() && outputTextView != null){
                    if(output.length() == 0){
                        outputTextView.setText(R.string.misc_no_network);
                    } else {
                        String processed = processHTMLOutputToQuota(output);
                        outputTextView.setText(processed);
                    }

                }


            }



        }.execute(new String[]{loginURL, quotaPageRelURL, username, password});



    }

    private String processHTMLOutputToQuota(String input){
        Matcher match = patternQuotaPage.matcher(input);
        StringBuffer finalString = new StringBuffer();
        //Find quota strings
        while (match.find()) {

            String matched = match.group();


            String processed = android.text.Html.fromHtml(matched).toString();

            int indexToInsertTo = processed.length();

            while(Character.isDigit(processed.charAt(indexToInsertTo - 1))){
                indexToInsertTo--;
            }




            StringBuffer temp = new StringBuffer(processed);
            temp.insert(indexToInsertTo, ": ");

            String quotaString = temp.toString();
            finalString.append(quotaString + System.getProperty("line.separator") + System.getProperty("line.separator"));
        }

        String finalText = finalString.toString();

        if(finalText.length() == 0){
            finalText = getString(R.string.misc_wrong_credentials);
        }

        Log.i(TAG, finalText);

        return finalText;
    }


    private String inputStreamToString(InputStream is) throws IOException {
        String line = "";
        StringBuilder total = new StringBuilder();

        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        // Read response until the end
        while ((line = rd.readLine()) != null) {
            total.append(line);
        }

        String result = total.toString();

        // Return full string
        return result;
    }


}
