package ua.com.privateplace.smsparser;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

public class AppEx extends Application {
	private Activity currentActivity;	
	private SmsParserMessageManager smsManager;
	
	private static AppEx singleton;
	
	public final static String TAG = "SmsParser";
	
	@Override
	public final void onCreate() {		
	  super.onCreate();
	  v("AppEx: onCreate()");
	  singleton = this;
	  smsManager = new SmsParserMessageManager();
	  smsManager.getUnreadMessages(this);
	}		
		
	public Activity getCurrentActivity() {
		v("AppEx: getCurrentActivity()");
		return currentActivity;
	}
	public void setCurrentActivity(Activity _activity) {
		v("AppEx: setCurrentActivity()");		
		currentActivity = _activity;
	}	
	
	public SmsParserMessageManager getSmsManager() {
		v("AppEx: getSmsManager()");		
		return smsManager;
	}	
	
	public static AppEx getInstance() {
		v("AppEx: getInstance()");		
		return singleton;
	}	
	
	public static void v(String msg) {
		if (write_log())  Log.v(TAG, msg);
	}

 	public static void e(String msg) {
    	if (write_log())  Log.e(TAG, msg);
  	}
  
  	private static boolean write_log()
  	{
		  return true;
  	}
}
