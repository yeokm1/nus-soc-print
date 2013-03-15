package ui;





import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import network.SSHManager;
import network.SSH_Clear_Cache;
import network.SSH_Delete_All_Jobs;
import network.SSH_Print_Quota;
import network.SSH_Printer_Status;
import network.SSH_Upload_Print;

import com.lamerman.FileDialog;
import com.lamerman.SelectionMode;
import com.yeokm1.nussocprint.R;



import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements TabListener {

	final String tab1Text = "Print";
	final String tab2Text = "Status";
	final String tab3Text = "Quota";
	final String tab4Text = "Settings";

	final String fileNotSelected = "Please select a file to print";
	final String invalidPageRange = "Invalid page range given";

	final int REQUEST_OPEN = 123456;

	RelativeLayout rl;


	Spinner printerSpinner;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_action_bar_main);
		try {
			rl = (RelativeLayout) findViewById(R.id.mainLayout);
			fragMentTra = getFragmentManager().beginTransaction();
			ActionBar bar = getActionBar();
			bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			//			bar.setDisplayShowHomeEnabled(true);
			//			bar.setDisplayShowTitleEnabled(true);
			bar.addTab(bar.newTab().setText(tab1Text).setTabListener(this));
			bar.addTab(bar.newTab().setText(tab2Text).setTabListener(this));
			bar.addTab(bar.newTab().setText(tab3Text).setTabListener(this));
			bar.addTab(bar.newTab().setText(tab4Text).setTabListener(this));

			//			bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
			//					| ActionBar.DISPLAY_USE_LOGO);

			bar.show();


		} catch (Exception e) {
			e.getMessage();
		}

		setSSHManager();

	}

	FragmentTransaction fragMentTra = null;
	PrintingFragment fram1;	
	StatusFragment fram2;
	QuotaFragment fram3;
	SettingsFragment fram4;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.action_bar, menu);
		return true;
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {

		if (tab.getText().equals(tab1Text)) {
			try {
				rl.removeAllViews();
			} catch (Exception e) {
			}
			fram1 = new PrintingFragment();
			fragMentTra.addToBackStack(null);
			fragMentTra = getFragmentManager().beginTransaction();
			fragMentTra.add(rl.getId(), fram1);
			fragMentTra.commit();
		} else if (tab.getText().equals(tab2Text)) {
			try {
				rl.removeAllViews();
			} catch (Exception e) {
			}
			fram2 = new StatusFragment();
			fragMentTra.addToBackStack(null);
			fragMentTra = getFragmentManager().beginTransaction();
			fragMentTra.add(rl.getId(), fram2);
			fragMentTra.commit();
		}else if (tab.getText().equals(tab3Text)) {
			try {
				rl.removeAllViews();
			} catch (Exception e) {
			}
			fram3 = new QuotaFragment();
			fragMentTra.addToBackStack(null);
			fragMentTra = getFragmentManager().beginTransaction();
			fragMentTra.add(rl.getId(), fram3);
			fragMentTra.commit();
		}  else if (tab.getText().equals(tab3Text)) {
			try {
				rl.removeAllViews();
			} catch (Exception e) {
			}
			fram4 = new SettingsFragment();
			fram4.setCallingActivity(this);
			fragMentTra.addToBackStack(null);
			fragMentTra = getFragmentManager().beginTransaction();
			fragMentTra.add(rl.getId(), fram4);
			fragMentTra.commit();
		}

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	public void refreshPrintQueue(View view){
		if(obtainCredentials() == null){
			showToast(getString(R.string.credentials_not_set));
			return;
		}

		List<String> printerList = getPrinterList();
		String[] printerArray = new String[printerList.size()];
		printerList.toArray(printerArray);

		SSH_Printer_Status status = new SSH_Printer_Status(this);


		status.execute(printerArray);

	}

	public void printButtonPress(View view){
		if(obtainCredentials() == null){
			showToast(getString(R.string.credentials_not_set));
			return;
		}

		TextView filePathviewer = (TextView) findViewById(R.id.file_path_view);
		String filePath = (String) filePathviewer.getText();
		String printerName;
		String startRangeText = null;
		String endRangeText = null;
		String lineBorder = null;

		if(filePath.equals(getString(R.string.select_file_to_print))){
			showToast(fileNotSelected);
			return;
		}




		CheckBox customPrinter = (CheckBox) findViewById(R.id.checkbox_custom_printer);
		Spinner printerList = (Spinner) findViewById(R.id.printer_list);





		if(customPrinter.isChecked()){
			printerName = getPreference(getString(R.string.custom_printer_preference));
			if(printerName.isEmpty()){
				showToast("Custom printer not set");
				return;
			}
		} else {
			printerName = printerList.getSelectedItem().toString();
		}

		Spinner pagesList = (Spinner) findViewById(R.id.num_pages_per_sheet);
		String numPagesPerSheet = pagesList.getSelectedItem().toString();


		RadioButton radioRange = (RadioButton) findViewById(R.id.radio_range_page);



		if(radioRange.isChecked()){


			EditText startRangeField = (EditText) findViewById(R.id.num_page_start);
			EditText endRangeField = (EditText) findViewById(R.id.num_page_end);


			startRangeText = startRangeField.getText().toString();
			endRangeText = endRangeField.getText().toString();

			if(startRangeText.isEmpty() && endRangeText.isEmpty()){
				showToast(invalidPageRange);
				return;
			}

			Integer startRange = null;
			Integer endRange = null;

			try{
				startRange = Integer.parseInt(startRangeText);
			} catch (NumberFormatException e){
				startRangeText = null;
			}

			try{
				endRange = Integer.parseInt(endRangeText);
			} catch (NumberFormatException e){
				endRangeText = null;
			}


			if(((startRange != null) && (endRange != null)) 
					&& ((startRange <= 0) || (startRange > endRange))){
				showToast(invalidPageRange);
				return;
			}


		}

		CheckBox lineBorderBox = (CheckBox) findViewById(R.id.checkbox_page_line_border);
		if(lineBorderBox.isChecked()){
			lineBorder = "lineBorder";
		}



		String[] settings = {filePath, printerName, numPagesPerSheet, startRangeText, endRangeText, lineBorder};

		SSH_Upload_Print printing = new SSH_Upload_Print(this);
		printing.execute(settings);

	}

	public void deleteAllJobs(View view){
		SSH_Delete_All_Jobs deleteAll = new SSH_Delete_All_Jobs(this);

		List<String> printerList = getPrinterList();
		String[] printerArray = new String[printerList.size()];
		printerList.toArray(printerArray);

		deleteAll.execute(printerArray);
	}

	public void getPrintQuota(View view){
		//		showToast("Feature not implemented yet");
		//		return;
		SSH_Print_Quota getQuota = new SSH_Print_Quota(this);
		getQuota.execute("");
	}

	public void forceDisconnectAndReinit(View view){
		SSHManager.close();
		setSSHManager();
		showToast("Disconnected and reset credentials");
	}

	public void clearServerCache(View view){
		SSH_Clear_Cache clear = new SSH_Clear_Cache(this);
		clear.execute("");
	}

	public void browseFile(View view){


		Intent intent = new Intent(getBaseContext(), FileDialog.class);
		intent.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory().getPath());

		//can user select directories or not
		intent.putExtra(FileDialog.CAN_SELECT_DIR, false);

		//alternatively you can set file filter
		intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "pdf" });
		intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);
		startActivityForResult(intent, REQUEST_OPEN);
		showToastSetLength("Press the back button to return if no file is selected", Toast.LENGTH_LONG);
	}

	public synchronized void onActivityResult(final int requestCode,
			int resultCode, final Intent data) {

		if (resultCode == Activity.RESULT_OK) {

			if (requestCode == REQUEST_OPEN) {
				String filePath = data.getStringExtra(FileDialog.RESULT_PATH);
				setFilePathView(filePath);
			}

			//		} else if (resultCode == Activity.RESULT_CANCELED) {
			//			showToast("no file selected");
		}

	}


	public void setFilePathView(String filePath){
		TextView viewer = (TextView) findViewById(R.id.file_path_view);
		viewer.setText(filePath);
	}

	private List<String> getPrinterList(){
		Resources res = getResources();
		String[] printers = res.getStringArray(R.array.printer_name);
		List<String> printerList = new ArrayList<String>(Arrays.asList(printers));

		String customPrinterName;


		customPrinterName = getPreference(getString(R.string.custom_printer_preference));

		if(!customPrinterName.isEmpty()){
			printerList.add(customPrinterName);
		}


		return printerList;
	}



	public String[] obtainCredentials(){
		String username = getPreference(getString(R.string.username_preference));
		String password = getPreference(getString(R.string.password_preference));
		String serverIP = getPreference(getString(R.string.custom_server_preference));

		return new String[]{username, password, serverIP};
	}

	public void onPreferenceChange(){		
		forceDisconnectAndReinit(null);
	}

	public boolean setSSHManager(){

		boolean status = true;

		String[] credentials = obtainCredentials();
		String serverIP;


		if(credentials[0].isEmpty()|| credentials[1].isEmpty()){
			showToast(getString(R.string.credentials_not_set));
			return false;
		}

		if(credentials[2].isEmpty()){
			serverIP = getString(R.string.server_IP);
		} else {
			serverIP = credentials[2];
		}

		SSHManager.setSettings(credentials[0], credentials[1], serverIP);

		return status;
	}

	public void showToast(String message){
		showToastSetLength(message, Toast.LENGTH_SHORT);
	}

	public void showToastSetLength(String message, int length){
		Toast.makeText(getApplicationContext(), message, length).show();
	}

	public void updateProgressBar(String text, int progress){
		ProgressBar progressBar = (ProgressBar) findViewById(R.id.refreshProgressBar);

		//If the user switches screens while updating halfway, the progress bar will be destroyed
		if(progressBar == null){
			return;
		}

		progressBar.setProgress(progress);
		showToPrinterStatus(text);
	}
	public void showToPrinterStatus(String data){
		TextView statusView = (TextView) findViewById(R.id.printer_status_output);

		if(statusView == null){
			return;
		}
		statusView.setText(data);
		statusView.setMovementMethod(new ScrollingMovementMethod());
	}

	public String getPreference(String key){
		SharedPreferences userDetails = PreferenceManager.getDefaultSharedPreferences(this);
		String value = userDetails.getString(key, "");
		return value;
	}
}