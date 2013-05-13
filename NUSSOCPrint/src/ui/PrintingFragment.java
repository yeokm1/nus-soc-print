package ui;



import com.yeokm1.nussocprint.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;


public class PrintingFragment extends Fragment {



	private MainActivity caller;


	public void setCallingActivity(MainActivity caller){
		this.caller = caller;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View result = inflater.inflate(R.layout.print_layout, container, false);


		return result;
	}

	@Override
	public void onStart(){
		super.onStart();
		caller.updatePrinterSpinner();
		

		caller.disableAndAdjustSomeUiOptionsBasedOnMethods(caller.currentMethod);
		
		//The statement below must come after visibility is adjusted
		caller.adjustPageRangeVisibility(caller.showPageRangeEntries);
		//Put this listener here to renew it across tab changes
		RadioGroup methodsRadioGroup = (RadioGroup) caller.findViewById(R.id.radio_group_method);        
		methodsRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				int method;
				switch(checkedId){
				case R.id.radio_method1: method = caller.METHOD_1;
				break;
				case R.id.radio_method2: method = caller.METHOD_2;
				break;
				case R.id.radio_method3: method = caller.METHOD_3;
				break;
				default : method = caller.METHOD_1;
				}

				caller.currentMethod = method;

				
				caller.disableAndAdjustSomeUiOptionsBasedOnMethods(method);
				caller.adjustPageRangeVisibility(caller.showPageRangeEntries);
				
			}
		});
		
		
		RadioGroup pageRangeRadioGroup = (RadioGroup) caller.findViewById(R.id.radioGroup_page_range);        
		pageRangeRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				boolean showRangeOptions;
				switch(checkedId){
				case R.id.radio_all_page: showRangeOptions = false;
				break;
				case R.id.radio_range_page: showRangeOptions = true;
				break;
				default : showRangeOptions = true;
				}

				caller.showPageRangeEntries = showRangeOptions;

				caller.adjustPageRangeVisibility(showRangeOptions);
			}
		});
		
		
		
		
		

	}
}
