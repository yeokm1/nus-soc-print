package network;

import ui.MainActivity;





public class SSH_Login extends SSHManager{

	
	public SSH_Login(MainActivity caller) {
		super(caller);
	}

	@Override
	protected String doInBackground(String... params) {
		String output =  super.connect();
		return output;
	}
	
	@Override
	protected void onPostExecute(String output){
		callingActivity.showToast(output);
		
	}






}
