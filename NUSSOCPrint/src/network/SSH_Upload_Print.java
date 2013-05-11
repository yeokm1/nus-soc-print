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
import com.yeokm1.nussocprint.R;

public class SSH_Upload_Print extends SSHManager {

	//f.pdf for shortfileName as multivalent seems to have issue with long file names
	final String dummyServerFileName = "f.pdf";
	Float progressIncrement;
	Float currentProgress = (float) 0;
	InputStream multiVal = null;
	String multivalentFilename;

	public SSH_Upload_Print(MainActivity caller) {
		super(caller);
	}

	@Override
	protected void onPreExecute(){

		multivalentFilename = SSHManager.callingActivity.getString(R.string.multivalent_filename);

		AssetManager assetMgr = SSHManager.callingActivity.getAssets();
		try {
			multiVal = assetMgr.open(multivalentFilename);
		} catch (IOException e) {
			//Nothing
		}

	}

	@Override
	protected String doInBackground(String... params) {

		if(multiVal == null){
			return "Cannot open multival jar";
		}

		String filePath = params[0];
		String printerName = params[1];
		String numCols = params[2];
		String numRows = params[3];
		String startRange = params[4];
		String endRange = params[5];
		String lineBorder = params[6];


		progressIncrement = (float) 100 / 8;

		try {
			publishProgress("Uploading MultiValent.jar");
			super.uploadFile(multiVal, multivalentFilename);


			File toBePrinted = new File(filePath);
			InputStream fileStream = new FileInputStream(toBePrinted);


			String tempDir = callingActivity.getString(R.string.server_temp_dir) + "/";


			String onServerFileName = tempDir + "\"" + dummyServerFileName + "\"";
			String pdfUpFilename = onServerFileName.substring(0, onServerFileName.length() - 5) + "-up.pdf\"";  //-5 to remove .pdf";
			String psFilename = onServerFileName.substring(0, onServerFileName.length() - 4) + "ps\"";

			publishProgress("Uploading Document...");
			super.uploadFile(fileStream, dummyServerFileName);

			String imposeCommand = generateMultivalentCommand(onServerFileName, numRows, numCols, startRange, endRange, lineBorder);

			publishProgress("Formatting PDF using: " + imposeCommand);
			super.sendCommand(imposeCommand);

			String convertToPSCommand = "pdftops";

			convertToPSCommand += " " + pdfUpFilename + " "  + psFilename;	

			publishProgress("Converting to PostScript using: " + convertToPSCommand);
			
			super.sendCommand(convertToPSCommand);

			String printCommand = "lpr -P ";

			printCommand += printerName + " ";

			printCommand += psFilename;

			publishProgress("Sending print command : \n" + printCommand);

			String printReply = super.sendCommand(printCommand);

			if(printReply.isEmpty()){
				return "Print command sent successfully";
			} else {
				return printReply;
			}

		} catch (FileNotFoundException e) {
			return "file not found exception " + e.getMessage();
		} catch (SftpException e) {
			return "sftp exception " + e.getMessage();
		} catch (JSchException e) {
			return "Jsch exception " + e.getMessage();
		} catch (IOException e) {
			return "IO exception " + e.getMessage();
		} finally {
			super.close();
		}
	}


	public String generateMultivalentCommand(String filePath, String numRows, String numCols, 
			String startRange, String endRange, String lineBorder ){

		String command = "java -classpath socPrint/Multivalent.jar tool.pdf.Impose -paper a4";
		
		command += " -dim " + numCols + "x" + numRows;

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
		currentProgress += progressIncrement;
		String soFar = progress[0];
		callingActivity.updatePrintingStatusProgressBar(soFar, currentProgress.intValue());
	}


	@Override
	protected void onPostExecute(String output){
		callingActivity.updatePrintingStatusProgressBar(output, 100);
	}

}
