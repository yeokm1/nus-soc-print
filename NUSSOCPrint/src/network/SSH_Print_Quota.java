package network;

import java.io.IOException;

import ui.MainActivity;

import android.widget.Toast;

import com.jcraft.jsch.JSchException;
import com.yeokm1.nussocprint.R;

public class SSH_Print_Quota extends SSHManager {

	public SSH_Print_Quota(MainActivity caller) {
		super(caller);
	}

	@Override
	protected String doInBackground(String... params) {
		
		String fileName = "quotaUsage.txt";
		
		try {
			publishProgress(super.sendCommand("pusage > " + fileName));
			publishProgress("pusage > " + fileName);
			String quota = super.sendCommand("cat " + fileName);
	//		super.sendCommand("rm -f " + fileName);
			
			return quota;
		} catch (JSchException e) {
			return "Jsch exception " + e.getMessage();
		} catch (IOException e) {
			return "IO exception " + e.getMessage();
		} finally {
			super.close();
		}
	}
	
	@Override
	protected void onProgressUpdate(String... progress){
		callingActivity.showToast(progress[0]);
	}
	
	@Override
	protected void onPostExecute(String output){
		callingActivity.showToPrinterStatus(output);

	}

}
