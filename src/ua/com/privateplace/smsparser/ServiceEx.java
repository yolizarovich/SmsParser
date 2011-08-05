package ua.com.privateplace.smsparser;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ServiceEx extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public AppEx getAppContext(){ 
		 return (AppEx) getApplicationContext(); 
	}
	
	public void msg(String info){
		AppEx.v(info);
	}
	
}
