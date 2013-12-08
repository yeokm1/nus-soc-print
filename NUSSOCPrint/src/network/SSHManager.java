package network;

/* 
 * SSHManager originally from stackoverflow
 * 
 * @author cabbott
 * @version 1.0
 */




import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ui.MainActivity;
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


	public SSHManager(MainActivity caller){
		SSHManager.callingActivity = caller;
		tempDir = callingActivity.getString(R.string.server_temp_dir) + "/";
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


	protected static synchronized void uploadFile(File toBePrinted) throws FileNotFoundException, SftpException, JSchException{
		uploadFile(new FileInputStream(toBePrinted), toBePrinted.getName());
	}

	protected static synchronized void uploadFile(InputStream toBePrinted, String fileName) throws FileNotFoundException, SftpException, JSchException{
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
		channelSftp.put(toBePrinted, fileName);


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
	}

	@Override
	protected void onPostExecute(String output){
		callingActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
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
	
}