package ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yeokm1.nussocprintandroid.R;



public class QuotaFragment extends Fragment {

	private TextView outputTextView;
	private Button refreshButton;
	private MainActivity caller;
	private final String TAG = "Quota";
	
	public void setCallingActivity(MainActivity activity){
		caller = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View result = inflater.inflate(R.layout.quota_layout, container, false);

		outputTextView = (TextView) result.findViewById(R.id.print_quota_text);
		refreshButton = (Button) result.findViewById(R.id.button_print_quota);

		refreshButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refreshPrintQuota();

			}
		});

		return result;
	}

	@Override
	public void onStart(){
		super.onStart();
		refreshPrintQuota();
	}

	public void refreshPrintQuota(){
		outputTextView.setText(R.string.quota_refresh_text);


		String loginURL = getString(R.string.quota_login_url);
		String quotaPageRelURL = getString(R.string.quota_page_relative_url);
		String[] credentials = caller.obtainCredentials();
		
		String username = credentials[0];
		String password = credentials[1];
		
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
		        Log.i(TAG, output);
			}
			
			

		}.execute(new String[]{loginURL, quotaPageRelURL, username, password});
		
		 

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
