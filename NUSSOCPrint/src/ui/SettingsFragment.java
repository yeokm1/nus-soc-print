package ui;





import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;

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

    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		caller.onPreferenceChange();
		
	}


    
}
