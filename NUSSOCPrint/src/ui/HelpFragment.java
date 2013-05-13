package ui;

import android.app.Fragment;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yeokm1.nussocprint.R;

public class HelpFragment extends Fragment {
	
    private MainActivity caller;
    
    public void setCallingActivity(MainActivity caller){
		this.caller = caller;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View result = inflater.inflate(R.layout.help_layout, container, false);
		return result;
	}
	@Override
	public void onStart(){
		super.onStart();
		TextView helpView = (TextView) caller.findViewById(R.id.tv_help_text);
		helpView.setMovementMethod(new ScrollingMovementMethod());
	}
	

}
