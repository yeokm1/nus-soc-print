package network;

import java.io.IOException;

import ui.MainActivity;

import com.jcraft.jsch.JSchException;


public class SSH_Printer_Status extends SSHManager {

	Float progressIncrement;
	Float currentProgress = (float) 0;

	public SSH_Printer_Status(MainActivity caller) {
		super(caller);
	}


	@Override
	protected String doInBackground(String... params) {
		StringBuffer output = new StringBuffer();
		String[] printerArray = params;
		progressIncrement = (float) 100 / printerArray.length;

		String[] status = {"", "Refresh Command Started, \"no entries\" means printer is free."};
		
		super.publishProgress(status);
		try{
			for(int i = 0; i < printerArray.length; i++){
				String printerStatus = super.sendCommand("lpq -P " + printerArray[i]);
				
				output.append(printerArray[i] + ": " + printerStatus);
				output.append("\n");
				currentProgress += progressIncrement;
				super.publishProgress(output.toString());
			}
		} catch (IOException e){
			return String.format(IO_EXCEPTION_FORMAT, e.getMessage());
		} catch (JSchException e) {
			return String.format(JSCH_EXCEPTION_FORMAT, e.getMessage());
		} finally {
			super.close();
		}

		return output.toString();
	}

	@Override
	protected void onProgressUpdate(String... progress){
		if(progress.length == 2){
			callingActivity.showToast(progress[1]);
		}
		
		String soFar = progress[0];
		callingActivity.updateRefreshStatusProgressBar(soFar, currentProgress.intValue());
	}

	@Override
	protected void onPostExecute(String output){
		callingActivity.showToPrinterQueueStatus(output);

	}


}
