package ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yeokm1.nussocprintandroid.R;





public class StatusFragment extends Fragment {


	String storedText = "No status to show yet.";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.print_status_layout, null);


		return view;
	}
}
