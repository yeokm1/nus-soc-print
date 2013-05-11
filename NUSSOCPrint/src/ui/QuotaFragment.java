package ui;

import com.yeokm1.nussocprint.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class QuotaFragment extends Fragment {


	
    private MainActivity caller;
    
    public void setCallingActivity(MainActivity caller){
		this.caller = caller;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View result = inflater.inflate(R.layout.quota_layout, container, false);
		return result;
	}
	
	@Override
	public void onStart(){
		super.onStart();
		caller.getPrintQuota(getView());
	}
}
