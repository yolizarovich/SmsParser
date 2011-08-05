package ua.com.privateplace.smsparser;

import android.app.Activity;

public class ActivityEx extends Activity {	

	public AppEx getAppContext(){ 
		 return (AppEx) getApplicationContext(); 
	} 
	
	public void msg(String info){
		AppEx.v(info);
	}
	
}


