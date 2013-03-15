package network;

/* 
 * SSHManager
 * 
 * @author cabbott
 * @version 1.0
 */




import android.os.AsyncTask;


import com.jcraft.jsch.*;
import com.yeokm1.nussocprint.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.PublicKey;
import java.util.LinkedList;
import java.util.List;

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



	protected static String connect()
	{
		if(connectionStatus){
			close();
		}

		String returnMessage = "Login Success";

		try
		{
			if((strUserName == null) || (strPassword == null) || (strConnectionIP == null)){
				throw new JSchException("Username/password not set");
			}
			sesConnection = jschSSHChannel.getSession(strUserName, 
					strConnectionIP, intConnectionPort);
			sesConnection.setPassword(strPassword);
			// UNCOMMENT THIS FOR TESTING PURPOSES, BUT DO NOT USE IN PRODUCTION
			sesConnection.setConfig("StrictHostKeyChecking", "no");
			sesConnection.connect(intTimeOut);
			connectionStatus = true;
		}
		catch(JSchException jschX)
		{
			returnMessage = jschX.getMessage();
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
			throw new JSchException("Connection not set up yet");
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
	
	protected static synchronized List<String> sendShellCommand(String command)throws IOException, JSchException
	{
		if(connectionStatus == false){
			connect();
		}



		if(sesConnection == null){
			throw new JSchException("Connection not set up yet");
		}
		Channel channel = sesConnection.openChannel("shell");
		
		
        InputStream inStream = channel.getInputStream();
        BufferedReader fromChannel = new BufferedReader(new InputStreamReader(inStream));
        OutputStream outStream = channel.getOutputStream();
        PrintWriter toChannel = new PrintWriter(outStream);
        channel.connect();


        

 //       fromChannel.skip(1000);

        

        byte[] buffer = new byte[15000];
        inStream.read(buffer);
        toChannel.write(command);
        
  //      String string = new String(buffer);

//        String input;
        List<String> outputStrings = new LinkedList<String>();
//        while((input = fromChannel.readLine()) != null){
//        	outputStrings.add(input);
//        }
//  
  //      fromChannel.read(buf);
    //    String tmp = new String(buf);
        
        buffer = new byte[15000];
        inStream.read(buffer);
        outputStrings.add(new String(buffer));

		channel.disconnect();



		return outputStrings;
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