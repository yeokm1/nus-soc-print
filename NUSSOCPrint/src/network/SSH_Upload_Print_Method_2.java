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

public class SSH_Upload_Print_Method_2 extends SSHManager {

	final Float progressIncrement = (float) 100 / 8;
	private Float currentProgress = (float) 0;

	private InputStream nup_pdf_stream = null;
	private String nup_pdf_Filename;
	
	private String nupMD5;



	public SSH_Upload_Print_Method_2(MainActivity caller) {
		super(caller);
	}


	@Override
	protected void onPreExecute(){
		super.onPreExecute();

		nup_pdf_Filename = SSHManager.callingActivity.getString(R.string.nup_pdf_filename);

		AssetManager assetMgr = SSHManager.callingActivity.getAssets();
		try {
			nup_pdf_stream = assetMgr.open(nup_pdf_Filename);
		} catch (IOException e) {
			//Nothing
		}
		SSHManager.callingActivity.setIndeterminateProgress(true);
		
		nupMD5 = callingActivity.getString(R.string.nup_pdf_md5);

	}

	@Override
	protected String doInBackground(String... params) {
		if(nup_pdf_stream == null){
			return "Cannot open multival jar";
		}
		
		String filePath = params[0];
		String printerName = params[1];
		String pagesPerSheet = params[2];
		String lineBorder = params[3];


		try {
			super.publishProgress(String.format(callingActivity.getString(R.string.server_checking_if_need_to_upload), nup_pdf_Filename));

			if(!doesMD5MatchServerFile(nup_pdf_Filename, nupMD5)){
				
				String wgetCommand = String.format(callingActivity.getString(R.string.server_wget_command_with_url), nup_pdf_Filename, tempDir);
				
				super.publishProgress(wgetCommand);
				super.sendCommand(wgetCommand);
				
				if(!doesMD5MatchServerFile(nup_pdf_Filename, nupMD5)){
					
					String downloadErrorMessage = String.format(callingActivity.getString(R.string.server_download_error_now_uploading_from_app), nup_pdf_Filename);
					
					super.publishProgress(downloadErrorMessage);
					super.uploadFile(nup_pdf_stream, nup_pdf_Filename);
				}
				
			}
			
			nup_pdf_stream.close();
			

			
			File toBePrinted = new File(filePath);
			InputStream fileStream = new FileInputStream(toBePrinted);
			
			publishProgress(callingActivity.getString(R.string.server_uploading_file));
			super.uploadFile(fileStream, toBePrinted.getName());

			String onServerFileName = convertDocsToPDFAndReturnFileName(toBePrinted.getName());
			String pdfUpFilename = onServerFileName.substring(0, onServerFileName.length() - 5) + "-up.pdf\"";  //-5 to remove .pdf";
			String psFilename = pdfUpFilename.substring(0, pdfUpFilename.length() - 4) + "ps\"";


			
			
			String nupCommand = generateNupCommand(onServerFileName, pdfUpFilename, pagesPerSheet, lineBorder);

			publishProgress(String.format(callingActivity.getString(R.string.server_formatting_pdf), nupCommand));
			super.sendCommand(nupCommand);

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


	public String generateNupCommand(String inputFilePath, String outputFilePath, String pagesPerSheet, String lineBorder ){
		
		String command = "java -jar " + tempDir + nup_pdf_Filename;
		command += " " + inputFilePath;
		command += " " + outputFilePath;
		
		command += " " + pagesPerSheet;
		
		if(lineBorder != null){
			command += " -b";
		}
		
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
