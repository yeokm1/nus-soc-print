package ui;



import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import network.SSHManager;
import network.SSH_Clear_Cache;
import network.SSH_Delete_All_Jobs;
import network.SSH_Printer_Status;
import network.WebViewSettings;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerTitleStrip;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lamerman.FileDialog;
import com.lamerman.SelectionMode;
import com.yeokm1.nussocprint.R;


public class MainActivity extends Activity implements TabListener {

	final String tab1Text = "Print";
	final String tab2Text = "Status";
	final String tab3Text = "Quota";
	final String tab4Text = "Settings";

	final String fileNotSelected = "Please select a file to print";
	final String invalidPageRange = "Invalid page range given";
	final String invalidPageLayout = "Invalid page layout given";

	final int METHOD_1 = 1;
	final int METHOD_2 = 2;
	final int METHOD_3 = 3;

	final int REQUEST_OPEN = 123456;

	RelativeLayout rl;

	Spinner printerSpinner;

	Fragment currentFragment;

	String fileName = null;

	private int currentMethod = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle intentBundle = getIntent().getExtras();


		if(intentBundle != null) {

			Uri fileNameUri = (Uri) intentBundle.get(Intent.EXTRA_STREAM);
			fileName = fileNameUri.getPath();
		}






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

	@Override
	public void onStart() {
		super.onStart();
		if(fileName != null) {

			if(!(fileName.endsWith("pdf"))){
				showToast(fileName + " is not a pdf file");
			}
			else {
				setFilePathView(fileName);
			}

			fileName = null;
		}

		disableAndAdjustSomeUiOptionsBasedOnMethods(METHOD_1);

		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group_method);        
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				int method;
				switch(checkedId){
				case R.id.radio_method1: method = METHOD_1;
				break;
				case R.id.radio_method2: method = METHOD_2;
				break;
				case R.id.radio_method3: method = METHOD_3;
				break;
				default : method = METHOD_1;
				}

				currentMethod = method;

				disableAndAdjustSomeUiOptionsBasedOnMethods(method);
			}
		});

		updatePrinterSpinner();

	}

	public void disableAndAdjustSomeUiOptionsBasedOnMethods(int method){

		View pagesPerSheetTitle = findViewById(R.id.tv_pages_per_sheet); pagesPerSheetTitle.setVisibility(View.INVISIBLE);
		View pagesPerSheetSpinner = findViewById(R.id.num_pages_per_sheet);	pagesPerSheetSpinner.setVisibility(View.INVISIBLE);

		View colsXRowsTitle = findViewById(R.id.tv_cols_x_rows); colsXRowsTitle.setVisibility(View.INVISIBLE);
		View colsXRows_cols = findViewById(R.id.numCols); colsXRows_cols.setVisibility(View.INVISIBLE);
		View colsXRows_x = findViewById(R.id.tv_x); colsXRows_x.setVisibility(View.INVISIBLE);
		View colsXRows_rows = findViewById(R.id.numRows); colsXRows_rows.setVisibility(View.INVISIBLE);

		View pageRangeTitle = findViewById(R.id.tv_page_range); pageRangeTitle.setVisibility(View.INVISIBLE);
		View pageRangeRadios = findViewById(R.id.radioGroup_page_range); pageRangeRadios.setVisibility(View.INVISIBLE);
		View pageRangeStart = findViewById(R.id.num_start_range); pageRangeStart.setVisibility(View.INVISIBLE);
		View pageRangeTo = findViewById(R.id.tv_to); pageRangeTo.setVisibility(View.INVISIBLE);
		View pageRangeEnd = findViewById(R.id.num_end_range); pageRangeEnd.setVisibility(View.INVISIBLE);






		switch(method){
		case METHOD_1 : {
			pagesPerSheetTitle.setVisibility(View.VISIBLE);
			pagesPerSheetSpinner.setVisibility(View.VISIBLE);

			pageRangeTitle.setVisibility(View.VISIBLE);
			pageRangeRadios.setVisibility(View.VISIBLE);
			pageRangeStart.setVisibility(View.VISIBLE);
			pageRangeTo.setVisibility(View.VISIBLE);
			pageRangeEnd.setVisibility(View.VISIBLE);


			Spinner numPagesSpinner = (Spinner) pagesPerSheetSpinner;
			String[] pagesArray = getResources().getStringArray(R.array.pagesForM1);
			ArrayAdapter<String> pagesAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, pagesArray);
			numPagesSpinner.setAdapter(pagesAdapter);

		} break;
		case METHOD_2 :	{
			pagesPerSheetTitle.setVisibility(View.VISIBLE);
			pagesPerSheetSpinner.setVisibility(View.VISIBLE);

			Spinner numPagesSpinner = (Spinner) pagesPerSheetSpinner;
			String[] pagesArray = getResources().getStringArray(R.array.pagesForM2);
			ArrayAdapter<String> pagesAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, pagesArray);
			numPagesSpinner.setAdapter(pagesAdapter);

		} break;
		case METHOD_3 : {
			colsXRowsTitle.setVisibility(View.VISIBLE);
			colsXRows_cols.setVisibility(View.VISIBLE);
			colsXRows_x.setVisibility(View.VISIBLE);
			colsXRows_rows.setVisibility(View.VISIBLE);

			pageRangeTitle.setVisibility(View.VISIBLE);
			pageRangeRadios.setVisibility(View.VISIBLE);
			pageRangeStart.setVisibility(View.VISIBLE);
			pageRangeTo.setVisibility(View.VISIBLE);
			pageRangeEnd.setVisibility(View.VISIBLE);
			
		} break;

		}



	}

	public void updatePrinterSpinner() {
		List<String> printerList = getPrinterList();
		Spinner printListSpinner = (Spinner) findViewById(R.id.printer_list);
		ArrayAdapter<String> printListAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, printerList);
		printListSpinner.setAdapter(printListAdapter);
	}

	FragmentTransaction fragMentTra = null;
	PrintingFragment fram1 = new PrintingFragment();
	Fragment fram2 = new StatusFragment();
	QuotaFragment fram3 = new QuotaFragment();
	SettingsFragment fram4 = new SettingsFragment();


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



			fragMentTra.addToBackStack(null);
			fragMentTra = getFragmentManager().beginTransaction();
			fram1.setCallingActivity(this);

			fragMentTra.replace(rl.getId(), fram1);

			fragMentTra.commit();

			currentFragment = fram1;
		} else if (tab.getText().equals(tab2Text)) {
			try {
				rl.removeAllViews();
			} catch (Exception e) {
			}
			fragMentTra.remove(currentFragment);
			fragMentTra.addToBackStack(null);
			fragMentTra = getFragmentManager().beginTransaction();
			fragMentTra.replace(rl.getId(), fram2);
			fragMentTra.commit();

			currentFragment = fram2;


		}else if (tab.getText().equals(tab3Text)) {
			try {
				rl.removeAllViews();
			} catch (Exception e) {
			}
			fragMentTra.remove(currentFragment);
			fram3.setCallingActivity(this);
			fragMentTra.addToBackStack(null);
			fragMentTra = getFragmentManager().beginTransaction();
			fragMentTra.replace(rl.getId(), fram3);
			fragMentTra.commit();

			currentFragment = fram3;
		}  else if (tab.getText().equals(tab4Text)) {
			try {
				rl.removeAllViews();
			} catch (Exception e) {
			}
			fragMentTra.remove(currentFragment);
			fram4.setCallingActivity(this);
			fragMentTra.addToBackStack(null);
			fragMentTra = getFragmentManager().beginTransaction();
			fragMentTra.replace(rl.getId(), fram4);
			fragMentTra.commit();

			currentFragment = fram4;

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
		//		if(obtainCredentials() == null){
		//			showToast(getString(R.string.credentials_not_set));
		//			return;
		//		}
		//
		//		TextView filePathviewer = (TextView) findViewById(R.id.file_path_view);
		//		String filePath = (String) filePathviewer.getText();
		//		String printerName;
		//		String startRangeText = null;
		//		String endRangeText = null;
		//		String lineBorder = null;
		//
		//		if(filePath.equals(getString(R.string.select_file_to_print))){
		//			showToast(fileNotSelected);
		//			return;
		//		}
		//
		//
		//		CheckBox customPrinter = (CheckBox) findViewById(R.id.checkbox_custom_printer);
		//		Spinner printerList = (Spinner) findViewById(R.id.printer_list);
		//
		//
		//
		//
		//
		//		if(customPrinter.isChecked()){
		//			printerName = getPreference(getString(R.string.custom_printer_preference));
		//			if(printerName.isEmpty()){
		//				showToast("Custom printer not set");
		//				return;
		//			}
		//		} else {
		//			printerName = printerList.getSelectedItem().toString();
		//		}
		//
		//	
		//		EditText colsTextField = (EditText) findViewById(R.id.num_cols);
		//		EditText rowsTextField = (EditText) findViewById(R.id.num_rows);
		//		
		//		String numColsText = colsTextField.getText().toString();
		//		String numRowsText = rowsTextField.getText().toString();
		//		
		//		
		//		try{
		//			int numCols = Integer.parseInt(numColsText);
		//			int numRows = Integer.parseInt(numRowsText);
		//			
		//			if((numCols == 0) || (numRows == 0)){
		//				showToast(invalidPageLayout);
		//				return;
		//			}
		//			
		//		} catch (NumberFormatException e){
		//			showToast(invalidPageLayout);
		//			return;
		//		}
		//
		//		RadioButton radioRange = (RadioButton) findViewById(R.id.radio_range_page);
		//
		//
		//		if(radioRange.isChecked()){
		//
		//
		//			EditText startRangeField = (EditText) findViewById(R.id.num_page_start);
		//			EditText endRangeField = (EditText) findViewById(R.id.num_page_end);
		//
		//
		//			startRangeText = startRangeField.getText().toString();
		//			endRangeText = endRangeField.getText().toString();
		//
		//			if(startRangeText.isEmpty() && endRangeText.isEmpty()){
		//				showToast(invalidPageRange);
		//				return;
		//			}
		//
		//			Integer startRange = null;
		//			Integer endRange = null;
		//
		//			try{
		//				startRange = Integer.parseInt(startRangeText);
		//			} catch (NumberFormatException e){
		//				startRangeText = null;
		//			}
		//
		//			try{
		//				endRange = Integer.parseInt(endRangeText);
		//			} catch (NumberFormatException e){
		//				endRangeText = null;
		//			}
		//
		//
		//			if(((startRange != null) && (endRange != null)) 
		//					&& ((startRange <= 0) || (startRange > endRange))){
		//				showToast(invalidPageRange);
		//				return;
		//			}
		//
		//
		//		}
		//
		//		CheckBox lineBorderBox = (CheckBox) findViewById(R.id.checkbox_page_line_border);
		//		if(lineBorderBox.isChecked()){
		//			lineBorder = "lineBorder";
		//		}
		//
		//
		//
		//		String[] settings = {filePath, printerName, numColsText, numRowsText, startRangeText, endRangeText, lineBorder};
		//
		//		SSH_Upload_Print printing = new SSH_Upload_Print(this);
		//		printing.execute(settings);

	}

	public void deleteAllJobs(View view){
		SSH_Delete_All_Jobs deleteAll = new SSH_Delete_All_Jobs(this);

		List<String> printerList = getPrinterList();
		String[] printerArray = new String[printerList.size()];
		printerList.toArray(printerArray);

		deleteAll.execute(printerArray);
	}

	@SuppressLint("SetJavaScriptEnabled")
	public void getPrintQuota(View view){

		showToast("Loading Quota Check page");

		WebView webView = (WebView) findViewById(R.id.webView_qouta);
		webView.getSettings().setJavaScriptEnabled(true);

		webView.setWebViewClient(new WebViewSettings(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(this)));

		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setSavePassword(false);
		webView.loadUrl(getString(R.string.quota_url));
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

		//File Filter
		intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "pdf"});
		intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);
		startActivityForResult(intent, REQUEST_OPEN);
		showToastSetLength("Press the back button to return if no file is selected", Toast.LENGTH_LONG);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(item.getItemId() == R.id.menu_about){

			final TextView message = new TextView(this);
			final SpannableString s = new SpannableString(getString(R.string.about_box_text));
			Linkify.addLinks(s, Linkify.WEB_URLS);
			message.setText(s);
			message.setMovementMethod(LinkMovementMethod.getInstance());

			AlertDialog dialog = new AlertDialog.Builder(this)
			.setTitle("About")
			.setCancelable(true)
			.setIcon(android.R.drawable.ic_dialog_info)
			.setPositiveButton("OK", null)
			.setView(message)
			.create();

			dialog.show();
		}

		return true;
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
			printerList.add(0, customPrinterName);
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

	public void updatePrintingStatusProgressBar(String text, int progress){
		ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar_print);

		//If the user switches screens while updating halfway, the progress bar will be destroyed
		if(progressBar == null){
			return;
		}

		progressBar.setProgress(progress);
		showToPrintingStatus(text);
	}


	public void showToPrintingStatus(String data){
		TextView statusView = (TextView) findViewById(R.id.print_status);
		if(statusView == null){
			return;
		}
		statusView.setText(data);
	}

	public void updateRefreshStatusProgressBar(String text, int progress){
		ProgressBar progressBar = (ProgressBar) findViewById(R.id.refreshProgressBar);

		//If the user switches screens while updating halfway, the progress bar will be destroyed
		if(progressBar == null){
			return;
		}

		progressBar.setProgress(progress);
		showToPrinterQueueStatus(text);
	}


	public void showToPrinterQueueStatus(String data){
		TextView statusView = (TextView) findViewById(R.id.printer_queue_status_output);

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
