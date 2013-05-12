package network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.yeokm1.nussocprint.R;

import ui.MainActivity;

public class SSH_Upload_Print_Method_2 extends SSHManager {

	final Float progressIncrement = (float) 100 / 8;
	Float currentProgress = (float) 0;

	InputStream nup_pdf_stream = null;
	String nup_pdf_Filename;



	public SSH_Upload_Print_Method_2(MainActivity caller) {
		super(caller);
	}


	@Override
	protected void onPreExecute(){

		nup_pdf_Filename = SSHManager.callingActivity.getString(R.string.nup_pdf_filename);

		AssetManager assetMgr = SSHManager.callingActivity.getAssets();
		try {
			nup_pdf_stream = assetMgr.open(nup_pdf_Filename);
		} catch (IOException e) {
			//Nothing
		}

	}

	@Override
	protected String doInBackground(String... params) {
		String filePath = params[0];
		String printerName = params[1];
		String pagesPerSheet = params[2];
		String lineBorder = params[3];


		try {
			publishProgress("Uploading " + nup_pdf_Filename);
			super.uploadFile(nup_pdf_stream, nup_pdf_Filename);

			File toBePrinted = new File(filePath);
			InputStream fileStream = new FileInputStream(toBePrinted);


			String tempDir = callingActivity.getString(R.string.server_temp_dir) + "/";


			String onServerFileName = tempDir + "\"" + toBePrinted.getName() + "\"";
			String pdfUpFilename = onServerFileName.substring(0, onServerFileName.length() - 5) + "-up.pdf\"";  //-5 to remove .pdf";
			String psFilename = onServerFileName.substring(0, onServerFileName.length() - 4) + "ps\"";

			publishProgress("Uploading Document...");
			super.uploadFile(fileStream, toBePrinted.getName());
			
			
			String nupCommand = generateNupCommand(onServerFileName, pdfUpFilename, pagesPerSheet, lineBorder);

			publishProgress("Formatting PDF using: " + nupCommand);
			super.sendCommand(nupCommand);

			String convertToPSCommand = "pdftops";

			convertToPSCommand += " " + pdfUpFilename + " "  + psFilename;	

			publishProgress("Converting to PostScript using: " + convertToPSCommand);
			
			super.sendCommand(convertToPSCommand);

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





	public String generateNupCommand(String inputFilePath, String outputFilePath, String pagesPerSheet, String lineBorder ){
		
		String command = "java -jar nup_pdf.jar";
		command += " " + inputFilePath;
		command += " " + outputFilePath;
		
		command += " " + pagesPerSheet;
		
		if(lineBorder != null){
			command += " -b";
		}
		
		return command;

	}

}
