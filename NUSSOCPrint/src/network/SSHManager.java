package network;

/* 
 * SSHManager originally from stackoverflow
 * 
 * @author cabbott
 * @version 1.0
 */




import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import ui.MainActivity;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.util.Log;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import com.yeokm1.nussocprintandroid.R;



public abstract class SSHManager extends AsyncTask<String, String, String>
{

	private static JSch jschSSHChannel;
	private static String strUserName;
	private static String strConnectionIP;
	private static int intConnectionPort = 22;
	private static String strPassword;
	private static Session sesConnection;
	private static int intTimeOut = 60000;
	private static boolean connectionStatus = false;

	protected static MainActivity callingActivity = null;

	protected final String FILE_NOT_FOUND_EXCEPTION_FORMAT = "File not found exception: " + "%1$s";
	protected final String SFTP_EXCEPTION_FORMAT = "Sftp exception: " + "%1$s";
	protected final String JSCH_EXCEPTION_FORMAT = "Jsch exception: " + "%1$s";
	protected final String IO_EXCEPTION_FORMAT = "IO exception: " + "%1$s";
	
	protected String tempDir;
	
	protected String docsToPDFFileName;
	protected String docsToPDFMD5;

	public SSHManager(MainActivity caller){
		SSHManager.callingActivity = caller;
		tempDir = callingActivity.getString(R.string.server_temp_dir) + "/";
		docsToPDFFileName = callingActivity.getString(R.string.docs_to_pdf_filename);
		docsToPDFMD5 = callingActivity.getString(R.string.docs_to_pdf_md5);
	}


	public static void setSettings(String userName, String password,  String connectionIP)
	{ 

		if(connectionStatus){
			close();
		}

		jschSSHChannel = new JSch();
		strUserName = userName;
		strPassword = password;
		strConnectionIP = connectionIP;
	}



	protected static String connect() throws JSchException
	{
		if(connectionStatus){
			close();
		}

		String returnMessage = "Login Success";
		try{

			if((strUserName == null) || (strPassword == null) || (strConnectionIP == null)){
				throw new JSchException("Username/password not set");
			}
			sesConnection = jschSSHChannel.getSession(strUserName, 
					strConnectionIP, intConnectionPort);
			sesConnection.setPassword(strPassword);
			// To make things easier for user, I skip key check
			sesConnection.setConfig("StrictHostKeyChecking", "no");
			sesConnection.connect(intTimeOut);
			connectionStatus = true;
		} catch(JSchException e){
			if(e.getMessage().equals("Auth fail")){
				throw new JSchException("Username/Password incorrect");
			} else {
				throw e;
			}
		}
		return returnMessage;
	}


	protected static synchronized String sendCommand(String command) throws IOException, JSchException
	{
		Log.i("SSHmgr command", command);

		if(connectionStatus == false){
			connect();
		}

		StringBuilder outputBuffer = new StringBuilder();

		if(sesConnection == null){
			throw new JSchException("Connection not set up yet, check username/password");
		}
		Channel channel = sesConnection.openChannel("exec");
		((ChannelExec)channel).setCommand(command);
		channel.connect();
		InputStream commandOutput = channel.getInputStream();
		int readByte = commandOutput.read();

		while(readByte != 0xffffffff)
		{
			outputBuffer.append((char)readByte);
			readByte = commandOutput.read();
		}

		channel.disconnect();


		String output = outputBuffer.toString();
		Log.i("SSHmgr command output", output);
		return output;
	}


	protected synchronized void uploadFile(File toBePrinted) throws SftpException, JSchException, IOException{
		uploadFile(new FileInputStream(toBePrinted), toBePrinted.getName());
	}

	protected synchronized void uploadFile(InputStream toBePrinted, String fileName) throws SftpException, JSchException, IOException{
		if(connectionStatus == false){
			connect();
		}

		if(sesConnection == null){
			throw new JSchException("Connection not set up yet");
		}

		Channel channel = sesConnection.openChannel("sftp");

		channel.connect();
		ChannelSftp channelSftp = (ChannelSftp)channel;

		String tempDir = callingActivity.getString(R.string.server_temp_dir);

		try{
			channelSftp.mkdir(tempDir);
		} catch (SftpException e){
			//If cannot make directory, means directory already created
		}
		channelSftp.cd(tempDir);
		channelSftp.put(toBePrinted, fileName, new FileUploadMonitor(fileName, toBePrinted.available(), this));

	}

	public static void close()
	{
		if(sesConnection != null){
			sesConnection.disconnect();
		}
		connectionStatus = false;
	}

	protected static boolean getConnectionStatus(){
		return connectionStatus;
	}


	protected String printThisPSFile(String psFilename, String printerName) throws IOException, JSchException{
		String printCommand = "lpr -P ";

		printCommand += printerName + " ";

		printCommand += psFilename;

		publishProgress("Sending print command : \n" + printCommand);

		String printReply = sendCommand(printCommand);

		if(printReply.length() == 0){
			return "Print command sent successfully";
		} else {
			return printReply;
		}
	}

	@Override
	protected void onPreExecute(){
		// Stop the screen orientation changing during an event
		switch (callingActivity.getResources().getConfiguration().orientation)
		{
		case Configuration.ORIENTATION_PORTRAIT:
			callingActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			callingActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		}
		
		keepScreenOn();
	}

	@Override
	protected void onPostExecute(String output){
		callingActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		stopKeepScreenOn();
	}
	
	protected boolean doesMD5MatchServerFile(String filename, String md5) throws IOException, JSchException{
		String md5reply = sendCommand("md5 " + tempDir + filename);
		return md5reply.startsWith(md5);
	}
	

	protected void convertToPS(String pdfUpFilename, String psFilename)
			throws IOException, JSchException {
		String convertToPSCommand = "pdftops";

		convertToPSCommand += " " + pdfUpFilename + " "  + psFilename;	

		publishProgress(String.format(callingActivity.getString(R.string.server_converting_to_ps), convertToPSCommand));
		
		sendCommand(convertToPSCommand);
	}
	
	//From http://stackoverflow.com/questions/941272/how-do-i-trim-a-file-extension-from-a-string-in-java
	public String changeExtensionToPDF(String originalPath) {

//		String separator = System.getProperty("file.separator");
		String filename = originalPath;

//		// Remove the path upto the filename.
//		int lastSeparatorIndex = originalPath.lastIndexOf(separator);
//		if (lastSeparatorIndex == -1) {
//			filename = originalPath;
//		} else {
//			filename = originalPath.substring(lastSeparatorIndex + 1);
//		}

		// Remove the extension.
		int extensionIndex = filename.lastIndexOf(".");

		String removedExtension;
		if (extensionIndex == -1){
			removedExtension =  filename;
		} else {
			removedExtension =  filename.substring(0, extensionIndex);
		}
		String addPDFExtension = removedExtension + ".pdf";

		return addPDFExtension;
	}
	
	
	protected String convertDocsToPDFAndReturnFileName(String originalfileName) throws IOException, JSchException{
		String fileName = tempDir + "\"" + originalfileName + "\"";

		if(!originalfileName.endsWith("pdf")){

			//Attempt to get docs-to-pdf-converter.jar to server then convert
			if(!doesMD5MatchServerFile(docsToPDFFileName, docsToPDFMD5)){

				String wgetCommand = String.format(callingActivity.getString(R.string.server_wget_command_with_url), docsToPDFFileName, tempDir);

				super.publishProgress(wgetCommand);
				sendCommand(wgetCommand);

				if(!doesMD5MatchServerFile(docsToPDFFileName, docsToPDFMD5)){
					throw new JSchException(callingActivity.getString(R.string.server_download_fail_docs_to_pdf_converter));
				}

			}


			String converterPath = tempDir + "\"" + docsToPDFFileName + "\"";
			String outputFilePath = changeExtensionToPDF(fileName) + "\"";

			String convertCommand = "java -jar " + converterPath + " -i " + fileName + " -o " + outputFilePath;
			super.publishProgress(String.format(callingActivity.getString(R.string.server_converting_to_pdf), convertCommand));
			String reply = sendCommand(convertCommand);

			if(reply.length() != 0){
				throw new JSchException(callingActivity.getString(R.string.server_converting_to_pdf_error));
			}

			fileName = outputFilePath;

		}
		
		
		return fileName;
		
		
	}
	
	
	public static class FileUploadMonitor implements SftpProgressMonitor {

		
		private long bytesTransferredSoFar = 0;
		private String actualFileSize;
		private String fileName;
		private SSHManager task;
		
		public FileUploadMonitor(String fileName, long fileSize, SSHManager task){
			actualFileSize = humanReadableByteCount(fileSize, false);
			this.fileName = fileName;
			this.task = task;
		}
		@Override
		public boolean count(long bytesTransferred) {
			bytesTransferredSoFar += bytesTransferred;
			
			String bytesTransferredSoFarStr = humanReadableByteCount(bytesTransferredSoFar, false);
			
			Log.d("transfer",  " transferred "  + bytesTransferredSoFarStr + " of " + actualFileSize);
			
			String progressMessage = String.format(callingActivity.getString(R.string.server_uploading_file_progress), fileName, bytesTransferredSoFarStr, actualFileSize);
			String[] arrayStr = {progressMessage, "dummy"};
			
			task.publishProgress(arrayStr);
			return true;
		}

		@Override
		public void end() {
			Log.d("transfer", "end");
		}

		@Override
		public void init(int opCodeOfTransfer, String sourceFileName, String destFileName, long fileSize) {
//			Log.d("transfer", opCodeOfTransfer + " " + sourceFileName + " " + destFileName + " " + Long.toString(fileSize));
		}

	}
	
	//http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
	@SuppressLint("DefaultLocale")
	public static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	
	protected void keepScreenOn(){
		callingActivity.getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	protected void stopKeepScreenOn(){
		callingActivity.getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
}