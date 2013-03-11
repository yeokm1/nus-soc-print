package network;

import java.io.IOException;

import ui.MainActivity;

import com.jcraft.jsch.JSchException;

public class SSH_Delete_All_Jobs extends SSHManager {

	public SSH_Delete_All_Jobs(MainActivity caller) {
		super(caller);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			publishProgress("Deletion Command Started");
			for(String printer : params){
				super.sendCommand("lprm -P " + printer + " -");
				publishProgress("Deletion command sent to " + printer);
			}
			
			return "Mass deletion command completed";
		} catch (JSchException e) {
			return "Jsch exception " + e.getMessage();
		} catch (IOException e) {
			return "IO exception " + e.getMessage();
		} finally {
			super.close();
		}
	}
	@Override
	protected void onProgressUpdate(String... update){
		callingActivity.showToast(update[0]);
	}
	
	
	@Override
	protected void onPostExecute(String output){
		callingActivity.showToast(output);
	}

}
