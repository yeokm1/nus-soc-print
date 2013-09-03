package network;

import java.io.IOException;

import ui.MainActivity;

import com.jcraft.jsch.JSchException;

public class SSH_Delete_All_Jobs extends SSHManager {

	Float progressIncrement;
	Float currentProgress = (float) 0;
	
	public SSH_Delete_All_Jobs(MainActivity caller) {
		super(caller);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			StringBuffer outputBuffer = new StringBuffer();
			progressIncrement = (float) 100 / params.length;
			
			outputBuffer.append("Deletion Command Started");
			outputBuffer.append("\n");
			
			publishProgress(outputBuffer.toString());
			for(String printer : params){
				super.sendCommand("lprm -P " + printer + " -");
				outputBuffer.append("Deletion command sent to " + printer);
				outputBuffer.append("\n");
				publishProgress(outputBuffer.toString());
				
			}
			outputBuffer.append("Mass deletion command completed");
			return outputBuffer.toString();
		} catch (JSchException e) {
			return String.format(JSCH_EXCEPTION_FORMAT, e.getMessage());
		} catch (IOException e) {
			return String.format(IO_EXCEPTION_FORMAT, e.getMessage());
		} finally {
			super.close();
		}
	}
	@Override
	protected void onProgressUpdate(String... progress){
		currentProgress += progressIncrement;
		String soFar = progress[0];
		callingActivity.updateRefreshStatusProgressBar(soFar, currentProgress.intValue());
	}
	
	
	@Override
	protected void onPostExecute(String output){
		callingActivity.updateRefreshStatusProgressBar(output, currentProgress.intValue());
		super.onPostExecute(output);
	}

}
