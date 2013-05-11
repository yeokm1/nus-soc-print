package network;

/* 
 * SSHManager originally from stackoverflow
 * 
 * @author cabbott
 * @version 1.0
 */




import android.os.AsyncTask;


import com.jcraft.jsch.*;
import com.yeokm1.nussocprint.R;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


import ui.MainActivity;


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

	public SSHManager(MainActivity caller){
		SSHManager.callingActivity = caller;
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


	protected static synchronized String sendCommand(String command)throws IOException, JSchException
	{
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



		return outputBuffer.toString();
	}


	protected static synchronized void uploadFile(File toBePrinted) throws FileNotFoundException, SftpException, JSchException{
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
		channelSftp.put(new FileInputStream(toBePrinted), toBePrinted.getName());


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


}