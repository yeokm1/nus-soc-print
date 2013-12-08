package ui;





import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

import com.yeokm1.nussocprintandroid.R;


public class SettingsFragment extends PreferenceListFragment implements OnSharedPreferenceChangeListener{

    private MainActivity caller;
    
    
    
    //Set to static as any listeners associated with the shared Pref will be GCed
    //when this fragment is destroyed
    private static SharedPreferences sharedPref;
	
    public void setCallingActivity(MainActivity caller){
		this.caller = caller;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.layout.preference_layout);
        
        sharedPref = getPreferenceScreen().getSharedPreferences();
        sharedPref.registerOnSharedPreferenceChangeListener(this);  
        
        Preference resetLink = findPreference(getString(R.string.preference_reset_unix_password_link_key));
        resetLink.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				String url = getString(R.string.reset_unix_password_link);
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(browserIntent);
				return true;
			}
		});

    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if(caller != null) {
			caller.onPreferenceChange();
		}
		
	}


    
}
