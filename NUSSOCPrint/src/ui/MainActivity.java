package ui;

import com.crashlytics.android.Crashlytics;
import com.google.analytics.tracking.android.EasyTracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import network.SSHManager;
import network.SSH_Clear_Cache;
import network.SSH_Delete_All_Jobs;
import network.SSH_Printer_Status;
import network.SSH_Upload_Print_Method_1;
import network.SSH_Upload_Print_Method_2;
import network.SSH_Upload_Print_Method_3;
import network.WebViewSettings;
import ui.PreferenceListFragment.OnPreferenceAttachedListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.lamerman.FileDialog;
import com.lamerman.SelectionMode;
import com.yeokm1.nussocprintandroid.R;






public class MainActivity extends SherlockFragmentActivity implements TabListener, OnPreferenceAttachedListener {

	final String tab1Text = "Print";
	final String tab2Text = "Status";
	final String tab3Text = "Quota";
	final String tab4Text = "Settings";
	final String tab5Text = "Help";

	final String fileNotSelected = "Please select a file to print";
	final String invalidPageRange = "Invalid page range given";
	final String invalidPageLayout = "Invalid page layout given";

	final int METHOD_1 = 1;
	final int METHOD_2 = 2;
	final int METHOD_3 = 3;

	final int REQUEST_OPEN = 63;

	RelativeLayout rl;

	Spinner printerSpinner;

	Fragment currentFragment;

	String fileName = null;

	int currentMethod = METHOD_1;
	boolean showPageRangeEntries = false;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Crashlytics.start(this);
		Bundle intentBundle = getIntent().getExtras();


		if(intentBundle != null) {

			Uri fileNameUri = (Uri) intentBundle.get(Intent.EXTRA_STREAM);
			fileName = fileNameUri.getPath();
		}






		setContentView(R.layout.activity_action_bar_main);
		try {
			rl = (RelativeLayout) findViewById(R.id.mainLayout);
			fragMentTra = getSupportFragmentManager().beginTransaction();
			ActionBar bar = getSupportActionBar();
			bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			//			bar.setDisplayShowHomeEnabled(true);
			//			bar.setDisplayShowTitleEnabled(true);
			bar.addTab(bar.newTab().setText(tab1Text).setTabListener(this));
			bar.addTab(bar.newTab().setText(tab2Text).setTabListener(this));
			bar.addTab(bar.newTab().setText(tab3Text).setTabListener(this));
			bar.addTab(bar.newTab().setText(tab4Text).setTabListener(this));
			bar.addTab(bar.newTab().setText(tab5Text).setTabListener(this));

			//			bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
			//					| ActionBar.DISPLAY_USE_LOGO);

			bar.show();


		} catch (Exception e) {
			e.getMessage();
		}

		setSSHManager();

		String[] credentials = obtainCredentials();

		if((credentials[0].length() == 0) || (credentials[1].length() == 0)){
			showCredNotSetDialog();
		}

	}

	@Override
	public void onStart() {
		super.onStart();
		if(fileName != null) {

			if(!(
					(fileName.endsWith("pdf")) 
					|| (fileName.endsWith("doc"))
					|| (fileName.endsWith("docx"))
					|| (fileName.endsWith("ppt"))
					|| (fileName.endsWith("pptx"))
					|| (fileName.endsWith("odt")))
					){
				showToast(fileName + " is not of a supported format");
			}
			else {
				setFilePathView(fileName);
			}

			fileName = null;
		}

		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); 
	}




	public void adjustColsxRowsVisibility(int position){
		View colsXRowsTitle = findViewById(R.id.tv_cols_x_rows); colsXRowsTitle.setVisibility(View.INVISIBLE);
		View colsXRows_cols = findViewById(R.id.numCols); colsXRows_cols.setVisibility(View.INVISIBLE);
		View colsXRows_x = findViewById(R.id.tv_x); colsXRows_x.setVisibility(View.INVISIBLE);
		View colsXRows_rows = findViewById(R.id.numRows); colsXRows_rows.setVisibility(View.INVISIBLE);


		if(position == 0 && currentMethod == METHOD_3){
			colsXRowsTitle.setVisibility(View.VISIBLE);
			colsXRows_cols.setVisibility(View.VISIBLE);
			colsXRows_x.setVisibility(View.VISIBLE);
			colsXRows_rows.setVisibility(View.VISIBLE);
		} else {
			colsXRowsTitle.setVisibility(View.INVISIBLE);
			colsXRows_cols.setVisibility(View.INVISIBLE);
			colsXRows_x.setVisibility(View.INVISIBLE);
			colsXRows_rows.setVisibility(View.INVISIBLE);
		}
	}

	public void adjustPageRangeVisibility(boolean value){

		View pageRangeStart = findViewById(R.id.num_start_range); pageRangeStart.setVisibility(View.INVISIBLE);
		View pageRangeTo = findViewById(R.id.tv_to); pageRangeTo.setVisibility(View.INVISIBLE);
		View pageRangeEnd = findViewById(R.id.num_end_range); pageRangeEnd.setVisibility(View.INVISIBLE);

		if(value && (currentMethod != METHOD_2)){
			pageRangeStart.setVisibility(View.VISIBLE);
			pageRangeTo.setVisibility(View.VISIBLE);
			pageRangeEnd.setVisibility(View.VISIBLE);
		} else {

			pageRangeStart.setVisibility(View.INVISIBLE);
			pageRangeTo.setVisibility(View.INVISIBLE);
			pageRangeEnd.setVisibility(View.INVISIBLE);
		}
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

		Spinner numPagesSpinner = (Spinner) pagesPerSheetSpinner;

		switch(method){
		case METHOD_1 : {

			pagesPerSheetTitle.setVisibility(View.VISIBLE);
			pagesPerSheetSpinner.setVisibility(View.VISIBLE);

			pageRangeTitle.setVisibility(View.VISIBLE);
			pageRangeRadios.setVisibility(View.VISIBLE);
			pageRangeStart.setVisibility(View.VISIBLE);
			pageRangeTo.setVisibility(View.VISIBLE);
			pageRangeEnd.setVisibility(View.VISIBLE);



			String[] pagesArray = getResources().getStringArray(R.array.pagesForM1);
			ArrayAdapter<String> pagesAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, pagesArray);
			numPagesSpinner.setAdapter(pagesAdapter);



		} break;
		case METHOD_2 :	{
			pagesPerSheetTitle.setVisibility(View.VISIBLE);
			pagesPerSheetSpinner.setVisibility(View.VISIBLE);

			String[] pagesArray = getResources().getStringArray(R.array.pagesForM2);
			ArrayAdapter<String> pagesAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, pagesArray);
			numPagesSpinner.setAdapter(pagesAdapter);

		} break;
		case METHOD_3 : {
			pagesPerSheetTitle.setVisibility(View.VISIBLE);
			pagesPerSheetSpinner.setVisibility(View.VISIBLE);


			String[] pagesArray = getResources().getStringArray(R.array.pagesForM3);
			ArrayAdapter<String> pagesAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, pagesArray);
			numPagesSpinner.setAdapter(pagesAdapter);

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
		if(printListAdapter != null){
			printListSpinner.setAdapter(printListAdapter);
		}
	}

	FragmentTransaction fragMentTra = null;
	PrintingFragment fram1 = new PrintingFragment();
	Fragment fram2 = new StatusFragment();
	QuotaFragment fram3 = new QuotaFragment();
	SettingsFragment fram4 = new SettingsFragment();
	HelpFragment fram5 = new HelpFragment();


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.action_bar, menu);
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
			fragMentTra = getSupportFragmentManager().beginTransaction();
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
			fragMentTra = getSupportFragmentManager().beginTransaction();
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
			fragMentTra = getSupportFragmentManager().beginTransaction();
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
			fragMentTra = getSupportFragmentManager().beginTransaction();
			fragMentTra.replace(rl.getId(), fram4);
			fragMentTra.commit();

			currentFragment = fram4;

		}else if (tab.getText().equals(tab5Text)) {
			try {
				rl.removeAllViews();
			} catch (Exception e) {
			}
			fragMentTra.remove(currentFragment);
			fram5.setCallingActivity(this);
			fragMentTra.addToBackStack(null);
			fragMentTra = getSupportFragmentManager().beginTransaction();
			fragMentTra.replace(rl.getId(), fram5);
			fragMentTra.commit();

			currentFragment = fram5;

		}



	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	public void refreshPrintQueue(View view){
		if(obtainCredentials() == null){
			showToast(getString(R.string.credentials_not_set_dialog_text));
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
			showToast(getString(R.string.credentials_not_set_dialog_text));
			return;
		}

		TextView filePathviewer = (TextView) findViewById(R.id.file_path_view);
		String filePath = filePathviewer.getText().toString();
		String printerName;
		String pagesPerSheetText = null;
		String startRangeText = null;
		String endRangeText = null;
		String lineBorder = null;

		if(filePath.equals(getString(R.string.select_file_to_print))){
			showToast(fileNotSelected);
			return;
		}

		Spinner printerList = (Spinner) findViewById(R.id.printer_list);

		printerName = printerList.getSelectedItem().toString();


		CheckBox lineBorderBox = (CheckBox) findViewById(R.id.checkbox_page_line_border);
		if(lineBorderBox.isChecked()){
			lineBorder = "lineBorder";
		}


		Spinner pagesPerSheetSpinner = (Spinner) findViewById(R.id.num_pages_per_sheet);

		EditText colsTextField = (EditText) findViewById(R.id.numCols);
		EditText rowsTextField = (EditText) findViewById(R.id.numRows);

		RadioButton radioRange = (RadioButton) findViewById(R.id.radio_range_page);
		EditText startRangeField = (EditText) findViewById(R.id.num_start_range);
		EditText endRangeField = (EditText) findViewById(R.id.num_end_range);


		if((currentMethod == METHOD_1) || (currentMethod == METHOD_3)){
			if(radioRange.isChecked()){

				startRangeText = startRangeField.getText().toString();
				endRangeText = endRangeField.getText().toString();

				if((startRangeText.length() == 0) && (endRangeText.length() == 0)){
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

			if(currentMethod == METHOD_1){
				pagesPerSheetText =  pagesPerSheetSpinner.getSelectedItem().toString();
				SSH_Upload_Print_Method_1 action = new SSH_Upload_Print_Method_1(this);
				action.execute(filePath, printerName,pagesPerSheetText, startRangeText, endRangeText, lineBorder);
				return;
			}

			if(currentMethod == METHOD_3){

				String numColsText = null;
				String numRowsText = null;

				if(pagesPerSheetSpinner.getSelectedItemPosition() == 0){
					numColsText = colsTextField.getText().toString();
					numRowsText = rowsTextField.getText().toString();

					try{
						int numCols = Integer.parseInt(numColsText);
						int numRows = Integer.parseInt(numRowsText);

						if((numCols == 0) || (numRows == 0)){
							showToast(invalidPageLayout);
							return;
						}

					} catch (NumberFormatException e){
						showToast(invalidPageLayout);
						return;
					}

				} else {
					pagesPerSheetText =  pagesPerSheetSpinner.getSelectedItem().toString();
				}

				SSH_Upload_Print_Method_3 printing = new SSH_Upload_Print_Method_3(this);
				printing.execute(filePath, printerName, pagesPerSheetText, numColsText, numRowsText, startRangeText, endRangeText, lineBorder);

				return;
			}


		}


		if(currentMethod == METHOD_2){
			pagesPerSheetText =  pagesPerSheetSpinner.getSelectedItem().toString();
			SSH_Upload_Print_Method_2 action = new SSH_Upload_Print_Method_2(this);
			action.execute(filePath, printerName, pagesPerSheetText, lineBorder);
			return;
		}



	}

	public void deleteAllJobs(View view){
		SSH_Delete_All_Jobs deleteAll = new SSH_Delete_All_Jobs(this);

		List<String> printerList = getPrinterList();
		String[] printerArray = new String[printerList.size()];
		printerList.toArray(printerArray);

		deleteAll.execute(printerArray);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	public void getPrintQuota(View view){

		showToast("Loading Quota Check page");

		WebView webView = (WebView) findViewById(R.id.webView_qouta);
		webView.getSettings().setJavaScriptEnabled(true);

		webView.setWebViewClient(new WebViewSettings(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(this)));

		webView.getSettings().setBuiltInZoomControls(true);
		//		webView.getSettings().setSupportZoom(true);
		webView.loadUrl(getString(R.string.quota_url));


		if(android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
			webView.getSettings().setSavePassword(false);
		}
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
		intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "pdf", "doc", "docx", "ppt", "pptx", "odt"});
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
		viewer.setMovementMethod(new ScrollingMovementMethod());
	}

	private List<String> getPrinterList(){
		Resources res = getResources();
		String[] printers = res.getStringArray(R.array.printer_name);
		List<String> printerList = new ArrayList<String>(Arrays.asList(printers));

		String customPrinterName;


		customPrinterName = getPreference(getString(R.string.custom_printer_preference));

		if(!(customPrinterName.length() == 0)){
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


		if((credentials[0].length() == 0) || (credentials[1].length() == 0)){
			showToast(getString(R.string.credentials_not_set_dialog_text));
			return false;
		}

		if(credentials[2].length() == 0){
			serverIP = getString(R.string.server_IP);
		} else {
			serverIP = credentials[2];
		}

		SSHManager.setSettings(credentials[0], credentials[1], serverIP);

		return status;
	}

	public void showCredNotSetDialog(){
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which){
				case DialogInterface.BUTTON_NEGATIVE:
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.credentials_not_set_dialog_text));
		builder.setNegativeButton(R.string.credentials_not_set_dialog_close_button, dialogClickListener);
		builder.show();
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

	public void setIndeterminateProgress(boolean value){
		ProgressBar bar1 = (ProgressBar) findViewById(R.id.separator1);
		ProgressBar bar2 = (ProgressBar) findViewById(R.id.separator2);

		if(bar1 == null || bar2 == null){
			return;
		}

		bar1.setIndeterminate(value);
		bar2.setIndeterminate(value);

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

	@Override
	public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
		//Dummy function used by PreferenceListFragment
	}
}
