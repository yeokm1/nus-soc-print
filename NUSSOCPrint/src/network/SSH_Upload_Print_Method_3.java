package network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ui.MainActivity;
import android.content.res.AssetManager;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.yeokm1.nussocprintandroid.R;

public class SSH_Upload_Print_Method_3 extends SSHManager {
	final Float progressIncrement = (float) 100 / 8;
	private Float currentProgress = (float) 0;
	private InputStream multiValStream = null;
	private String multivalentFilename;
	private String mvMD5;

	public SSH_Upload_Print_Method_3(MainActivity caller) {
		super(caller);
	}

	@Override
	protected void onPreExecute(){
		super.onPreExecute();
		multivalentFilename = SSHManager.callingActivity.getString(R.string.multivalent_filename);

		AssetManager assetMgr = SSHManager.callingActivity.getAssets();
		try {
			multiValStream = assetMgr.open(multivalentFilename);
		} catch (IOException e) {
			//Nothing
		}
		
		SSHManager.callingActivity.setIndeterminateProgress(true);
		
		mvMD5 = callingActivity.getString(R.string.multivalent_md5);

	}

	@Override
	protected String doInBackground(String... params) {

		if(multiValStream == null){
			return "Cannot open multival jar";
		}

		String filePath = params[0];
		String printerName = params[1];
		String numPagesPerSheet = params[2];
		String numCols = params[3];
		String numRows = params[4];
		String startRange = params[5];
		String endRange = params[6];
		String lineBorder = params[7];


		try {
			super.publishProgress(String.format(callingActivity.getString(R.string.server_checking_if_need_to_upload), multivalentFilename));

			if(!doesMD5MatchServerFile(multivalentFilename, mvMD5)){
				
				String wgetCommand = String.format(callingActivity.getString(R.string.server_wget_command_with_url), multivalentFilename, tempDir);
				
				super.publishProgress(wgetCommand);
				super.sendCommand(wgetCommand);
				
				if(!doesMD5MatchServerFile(multivalentFilename, mvMD5)){
					
					String downloadErrorMessage = String.format(callingActivity.getString(R.string.server_download_error_now_uploading_from_app), multivalentFilename);
					
					super.publishProgress(downloadErrorMessage);
					super.uploadFile(multiValStream, multivalentFilename);
				}
				
			}
			
			multiValStream.close();
			
			File toBePrinted = new File(filePath);
			InputStream fileStream = new FileInputStream(toBePrinted);

			publishProgress(callingActivity.getString(R.string.server_uploading_file));
			super.uploadFile(fileStream, toBePrinted.getName());

			String onServerFileName = convertDocsToPDFAndReturnFileName(toBePrinted.getName());
			String pdfUpFilename = onServerFileName.substring(0, onServerFileName.length() - 5) + "-up.pdf\"";  //-5 to remove .pdf";
			String psFilename = pdfUpFilename.substring(0, pdfUpFilename.length() - 4) + "ps\"";



			String imposeCommand = generateMultivalentCommand(onServerFileName, numPagesPerSheet, numRows, numCols, startRange, endRange, lineBorder);

			publishProgress(String.format(callingActivity.getString(R.string.server_formatting_pdf), imposeCommand));
			super.sendCommand(imposeCommand);

			convertToPS(pdfUpFilename, psFilename);
			
			return super.printThisPSFile(psFilename, printerName);

		} catch (FileNotFoundException e) {
			return String.format(FILE_NOT_FOUND_EXCEPTION_FORMAT, e.getMessage());
		} catch (SftpException e) {
			return String.format(SFTP_EXCEPTION_FORMAT, e.getMessage());
		} catch (JSchException e) {
			return String.format(JSCH_EXCEPTION_FORMAT, e.getMessage());
		} catch (IOException e) {
			return String.format(IO_EXCEPTION_FORMAT, e.getMessage());
		} finally {
			super.close();
		}
	}


	public String generateMultivalentCommand(String filePath, String numPagesPerSheet, String numRows, String numCols, 
			String startRange, String endRange, String lineBorder ){

		String command = "java -classpath " + tempDir + multivalentFilename + " tool.pdf.Impose -paper a4";
		
		
		if(numPagesPerSheet == null){
			command += " -dim " + numCols + "x" + numRows;
		} else {
			command += " -nup " + numPagesPerSheet;
		}

		if(!(startRange == null && endRange == null)){

			command += " -page ";
			if(startRange != null){
				command += startRange;
			}

			command += "-";


			if(endRange != null){
				command += endRange;
			}
		}

		command += " -sep ";

		if(lineBorder == null){
			command += "0";
		} else {
			command += "1";
		}

		command += " " + filePath;



		return command;
	}

	@Override
	protected void onProgressUpdate(String... progress){
		//If more than one items means progress is for file uploading
		if(progress.length == 1){
			currentProgress += progressIncrement;
		}
		String soFar = progress[0];
		callingActivity.updatePrintingStatusProgressBar(soFar, currentProgress.intValue());
	}


	@Override
	protected void onPostExecute(String output){
		callingActivity.updatePrintingStatusProgressBar(output, 100);
		SSHManager.callingActivity.setIndeterminateProgress(false);
		super.onPostExecute(output);
	}

}
