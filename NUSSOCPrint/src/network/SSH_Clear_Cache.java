package network;

import java.io.IOException;

import ui.MainActivity;

import com.jcraft.jsch.JSchException;

public class SSH_Clear_Cache extends SSHManager {

	public SSH_Clear_Cache(MainActivity caller) {
		super(caller);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			String message = super.sendCommand("rm -rf socPrint");
			
			if(message.length() == 0){
				message = "Delete socPrint folder success";
			}
			super.close();
			return message;
		} catch (JSchException e) {
			return String.format(JSCH_EXCEPTION_FORMAT, e.getMessage());
		} catch (IOException e) {
			return String.format(IO_EXCEPTION_FORMAT, e.getMessage());
		} finally {
			super.close();
		}
	}
	
	protected void onPostExecute(String output){
		callingActivity.showToast(output);
		super.onPostExecute(output);
	}

}
