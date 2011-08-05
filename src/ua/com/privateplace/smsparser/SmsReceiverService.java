package ua.com.privateplace.smsparser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.telephony.SmsMessage.MessageClass;
import android.widget.Toast;

public class SmsReceiverService extends ServiceEx  {
  //private SharedPreferences mPrefs;
  private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
  private static final String ACTION_MESSAGE_RECEIVED = "net.everythingandroid.smspopup.MESSAGE_RECEIVED";

  // http://android.git.kernel.org/?p=platform/packages/apps/Mms.git;a=blob;f=src/com/android/mms/transaction/SmsReceiverService.java
  public static final String MESSAGE_SENT_ACTION = "com.android.mms.transaction.MESSAGE_SENT";

  /*
   * This is the number of retries and pause between retries that we will keep
   * checking the system message database for the latest incoming message
   */
 // private static final int MESSAGE_RETRY = 8;
//  private static final int MESSAGE_RETRY_PAUSE = 1000;

  private Context context;
  private ServiceHandler mServiceHandler;
  private Looper mServiceLooper;
  //private int mResultCode;

  private static final Object mStartingServiceSync = new Object();
  private static PowerManager.WakeLock mStartingService;

  @Override
  public void onCreate() {
    msg("SMSReceiverService: onCreate()");
    HandlerThread thread = new HandlerThread(AppEx.TAG, Process.THREAD_PRIORITY_BACKGROUND);
    thread.start();
    context = getAppContext();
    mServiceLooper = thread.getLooper();
    mServiceHandler = new ServiceHandler(mServiceLooper);
  }

  @Override
  public void onStart(Intent intent, int startId) {
    msg("SMSReceiverService: onStart()");

  //  mResultCode = intent != null ? intent.getIntExtra("result", 0) : 0;

    Message msg = mServiceHandler.obtainMessage();
    msg.arg1 = startId;
    msg.obj = intent;
    mServiceHandler.sendMessage(msg);
  }

  @Override
  public void onDestroy() {
    msg("SMSReceiverService: onDestroy()");
    mServiceLooper.quit();
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  private final class ServiceHandler extends Handler {
    public ServiceHandler(Looper looper) {
      super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
      msg("SMSReceiverService: handleMessage()");

      int serviceId = msg.arg1;
      Intent intent = (Intent) msg.obj;
      String action = intent.getAction();

      if (ACTION_SMS_RECEIVED.equals(action)) {
    	handleSmsReceived(intent); 
      } else if (MESSAGE_SENT_ACTION.equals(action)) {
       // handleSmsSent(intent);
      } else if (ACTION_MESSAGE_RECEIVED.equals(action)) {
       // handleMessageReceived(intent);
      }

      finishStartingService(SmsReceiverService.this, serviceId);
    }
  } 
 
  private void handleSmsReceived(Intent intent) {
    msg("SMSReceiver.handleSmsReceived(): Intercept SMS");

    Bundle bundle = intent.getExtras();
    if (bundle != null) {
    	
    	/*Context context = getApplicationContext();
    	mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    	msg((mPrefs.getBoolean( context.getString(R.string.pref_music_key),false)) ? "music true" : "music false");
    	msg((mPrefs.getBoolean( context.getString(R.string.pref_hints_key),false)) ? "hints true" : "hints false");*/
      
    
    	
      SmsMessage[] messages = getMessagesFromIntent(intent);
      if (messages != null) {
    	  //msg("Uraaaaaaa woto poluchili"); 
          notifyMessageReceived(messages);    	
      }
    }
  }
  
  
  public static final SmsMessage[] getMessagesFromIntent(Intent intent) {
	    Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
	    if (messages == null) {
	      return null;
	    }
	    if (messages.length == 0) {
	      return null;
	    }

	    byte[] pduObj;	
	    int msgCount = messages.length;	       
	    SmsMessage[] msgs = new SmsMessage[msgCount];
	    
	    for (int i = 0; i < msgCount; i++) {
	      pduObj = (byte[]) messages[i];	     
	      msgs[i] = SmsMessage.createFromPdu(pduObj);
	    }
	    return msgs;
  }
  

  private void notifyMessageReceived(SmsMessage[] messages) {
    Boolean showPopup = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.pref_popup_key), true);  
	SmsMessage message = messages[0];
	msg(message.getDisplayMessageBody());
	
	//Toast.makeText(context,	message.getDisplayMessageBody(), Toast.LENGTH_LONG ).show();
	
	//for (int i = 0; i < messages.length; i++) {
	//	message = messages[i];
    // Class 0 SMS, let the system handle this
    if (message.getMessageClass() == MessageClass.CLASS_0) {    
    	Toast.makeText(context,	message.getDisplayMessageBody(), Toast.LENGTH_SHORT).show();
      return;
    }
    if (message.getDisplayMessageBody().trim().startsWith("//ANDROID:")) {
        // Checks if user is on carrier Sprint and message is a special system message    	
    	Toast.makeText(context,	message.getDisplayMessageBody(), Toast.LENGTH_SHORT).show();
      return;
    }

    // Fetch call state, if the user is in a call or the phone is ringing we don't want to show the popup
    TelephonyManager mTM = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    boolean callStateIdle = mTM.getCallState() == TelephonyManager.CALL_STATE_IDLE;

   // Init keyguard manager
   // ManageKeyguard.initialize(context);

   /* if (showPopup && callStateIdle && !docked
        && (ManageKeyguard.inKeyguardRestrictedInputMode() ||
            (!onlyShowOnKeyguard && !SmsPopupUtils.inMessagingApp(context))))*/
   
    String fromAddr = message.getDisplayOriginatingAddress();
    boolean otKogoNada =  fromAddr != null ? fromAddr.equalsIgnoreCase("10060") : false;    
    
    if (showPopup && callStateIdle && otKogoNada ) {
    	msg("SmsReceiverService: Popup Message");
     	//ManageWakeLock.acquireFull(context);
      
	    final Activity lastactivity;		
		lastactivity = getAppContext().getCurrentActivity();
	      
		if (lastactivity == null){
			//bugfix: we need to read database twice to get last message when starting activity from service
			getAppContext().getSmsManager().getUnreadMessages(context); 
			
	      Intent l_intent = getPopupIntent();
	      //l_intent.putExtra("messagetext", message.getDisplayMessageBody());      
	      context.startActivity(l_intent);
	    }
		else{	
		 // getAppContext().getSmsManager().getUnreadMessages(context);
		  ((SmsParserActivity)lastactivity).setUpdate();		
	    };
      
/*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/      
      
	   /* } else if (notifEnabled) {
	      msg("^^^^^^Not showing SMS Popup, using notifications");
	      ManageNotification.show(context, message);
	      ReminderReceiver.scheduleReminder(context, message);*/
    }	
  }
  
  public Intent getPopupIntent() {
    Intent popup = new Intent(context, SmsParserActivity.class);
    popup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    return popup;
  }

  

 /*
  public Handler mToastHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {

      if (msg != null) {
        switch (msg.what) {
          case TOAST_HANDLER_MESSAGE_SENT:
            Toast.makeText(SmsReceiverService.this,
                SmsReceiverService.this.getString(R.string.quickreply_sent_toast),
                Toast.LENGTH_SHORT).show();
            break;
          case TOAST_HANDLER_MESSAGE_SEND_LATER:
            Toast.makeText(SmsReceiverService.this,
                SmsReceiverService.this.getString(R.string.quickreply_failed_send_later),
                Toast.LENGTH_SHORT).show();
            break;
          case TOAST_HANDLER_MESSAGE_FAILED:
            Toast.makeText(SmsReceiverService.this,
                SmsReceiverService.this.getString(R.string.quickreply_failed),
                Toast.LENGTH_SHORT).show();
            break;
        }
      }
    }
  };

*/
  
  public static void beginStartingService(Context context, Intent intent) {
    synchronized (mStartingServiceSync) {
    	AppEx.v("SMSReceiverService: beginStartingService()");
      if (mStartingService == null) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mStartingService = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
        		AppEx.TAG+".SmsReceiverService");
        mStartingService.setReferenceCounted(false);
      }
      mStartingService.acquire();
      context.startService(intent);
    }
  }
  public static void finishStartingService(ServiceEx service, int startId) {
    synchronized (mStartingServiceSync) {
    	AppEx.v("SMSReceiverService: finishStartingService()");
      if (mStartingService != null) {
        if (service.stopSelfResult(startId)) {
          mStartingService.release();
        }
      }
    }
  } 
  

}
