package network;

import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yeokm1.nussocprint.R;



public class WebViewSettings extends WebViewClient{

	private SharedPreferences userDetails;
	private Context context;

	public WebViewSettings(Context context, SharedPreferences prefs){
		userDetails = prefs;
		this.context = context;
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		view.loadUrl(url);
		return true;
	}

	@Override
	public void onPageFinished(WebView view, String url)
	{

		String nusUsername = userDetails.getString(context.getString(R.string.username_preference), "");

		if(!(nusUsername.length() == 0)){
			view.loadUrl("javascript: {" + 
					"document.mysoc_login.credential_0.value = '" + nusUsername + "'; };");
		}

		String nusPassword = userDetails.getString(context.getString(R.string.password_preference), "");

		if(!(nusPassword.length() == 0)){
			view.loadUrl("javascript: {" + 
					"document.mysoc_login.credential_1.value = '" + nusPassword + "'; };");
		}

		if(!(nusUsername.length() == 0) && !(nusPassword.length() == 0)){
			view.loadUrl("javascript: {" +
					"var frms = document.getElementsByName('mysoc_login');" +
					"frms[0].submit(); };");
		}
		
	//	view.getSettings().setJavaScriptEnabled(false);


	}

}
