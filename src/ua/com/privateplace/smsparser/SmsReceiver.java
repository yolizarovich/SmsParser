package ua.com.privateplace.smsparser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SmsReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
	AppEx.v("SMSReceiver: onReceive(AHTUNG)");
    
    intent.setClass(context, SmsReceiverService.class);
    intent.putExtra("result", getResultCode());
    
    SmsReceiverService.beginStartingService(context, intent);
  }
}