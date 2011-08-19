package ua.com.privateplace.smsparser;

//ловить сообщение об удалении смс через стандартный бравзер 
//добавить признак старта активити по событию нового смс а не через юзера


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
 
public class SmsParserMessageManager {
	public static final Uri SMS_CONTENT_URI = Uri.parse("content://sms");
	public static final Uri SMS_INBOX_CONTENT_URI = Uri.withAppendedPath(SMS_CONTENT_URI, "inbox");
	
	Pattern pBank = Pattern.compile(".*(OPLATA\\D*|BANKOMAT\\D*|KASSA\\D*).*(?:[\\D&&[^,.]])(\\d+[.,]?\\d*UAH).*(Bal:\\d+[.,]?\\d*)(?:.*|$)");
	Pattern pPass = Pattern.compile("(?:^|.*Vhod|.*Parol).*(?:\\D)(\\d{8})(?:\\D|$)");	
	//Pattern pBank = Pattern.compile(".*(OPLATA\\D*|BANKOMAT\\D*|KASSA\\D*).*(?:[\\D&&[^,.]])(\\d+[.,]?\\d*UAH).*(Bal:\\d+[.,]?\\d*)(?:.*|$)");
	//pass = Pattern.compile("(?:^|\\D*)(OPLATA\\D*|KASSA\\D*)(\\d+[.,]?\\d*UAH)(?:\\D*)(Bal:\\d+[.,]?\\d*)(?:\\D*|$)");
	
	private ArrayList<SmsParserMessage> smsMessages = null;
	private Integer smsCount = 0;
	private Integer lasstRequestedMessage = -1;	
	
	public void getUnreadMessages(Context context) {
		AppEx.v("getUnreadMessages(): ");
		SmsParserMessage message;		
		final String[] projection = new String[] { "_id", "thread_id", "address", "date", "body", "read" };
		String selection = "read in (0,1) and address = '10060'";
		String[] selectionArgs = null;
		final String sortOrder = "date";
	
		if (smsCount > 0) {
			String unn = "";
			//selectionArgs = new String[smsCount];
			selection += " and _id not in (";
			for (int i = 0; i < smsCount; i++){				
				selection += unn + smsMessages.get(i).messageId;
				unn = ", ";
			}
			selection += ")";
			AppEx.v(selection);
		}
		
		
		// Create cursor
		Cursor cursor = context.getContentResolver().query(
				SMS_INBOX_CONTENT_URI, projection, selection,
				selectionArgs, sortOrder);
		
		if (cursor != null) {
			try {
				int count = cursor.getCount();				
				if (count > 0) {
					if (smsMessages == null) smsMessages = new ArrayList<SmsParserMessage>(count);
					while (cursor.moveToNext()) {	
						message = new SmsParserMessage(); 
						message.messageId = cursor.getLong(0); 
						message.threadId = cursor.getLong(1); 
						message.fromAddress = cursor.getString(2);
						message.timestamp = cursor.getLong(3); 
						message.messageBody = cursor.getString(4);
						message.isRead = cursor.getString(5).equalsIgnoreCase("1");
						smsMessages.add(message);										
					}
				} else AppEx.v("!!!!!!!!!!!!!!!!!!!!!!!----------------------------------------------> nothing new was found");;
			} finally {
				cursor.close();
			}
			if (smsMessages != null) smsCount = smsMessages.size(); 
		}		
	}
	
	synchronized public void setMessageRead(Context context) {
		SmsParserMessage  message;
		if (smsCount > 0 && lasstRequestedMessage >= 0 && lasstRequestedMessage < smsCount){
			message =  smsMessages.get(lasstRequestedMessage);
			if (!message.isRead ){
				ContentValues values = new ContentValues(1);
				values.put("read", 1);
				Uri messageUri = Uri.withAppendedPath(SMS_CONTENT_URI, String.valueOf(message.messageId));
				ContentResolver cr = context.getContentResolver();
				int result;
				try {
					result = cr.update(messageUri, values, null, null);
					message.isRead = true;
				} catch (Exception e) {
					result = 0;
				}
				AppEx.v(String.format("message id = %s marked as read, result = %s", message.messageId, result ));
			}else AppEx.v(String.format("message WAS marked as read"));
				
        }
    }	
	
	public void deleteCurrentMessage(Context context) {
		SmsParserMessage  message;
		if (smsCount > 0 && lasstRequestedMessage >= 0 && lasstRequestedMessage < smsCount){
			message =  smsMessages.get(lasstRequestedMessage);						
	    	AppEx.v("id of message to delete is " + message.messageId);

	      // We need to mark this message read first to ensure the entire thread is marked as read
	      setMessageRead(context);

	      // Construct delete message uri
	      Uri deleteUri;
	      deleteUri = Uri.withAppendedPath(SMS_CONTENT_URI, String.valueOf(message.messageId));
	      
	      int count = 0;
	      try {
	        count = context.getContentResolver().delete(deleteUri, null, null);	        
	      } catch (Exception e) {
	    	  AppEx.v("deleteMessage(): Problem deleting message - " + e.toString());
	      }
	      
	      AppEx.v("Messages deleted: " + count);
	      if (count == 1) {
	    	 deleteRequestedMessage();
	        //TODO: should only set the thread read if there are no more unread messages
	        //setThreadRead(context, threadId);
	      }
	    }
	}

	
	
	/**
	 * 
	 * @param i 
	 * number of sms message from 1 to SmsCount
	 * 
	 * @return 
	 * sms message from manager list
	 */
	public SmsParserMessage getMessage(int i){
		if (smsCount <= 0 && i >= smsCount && i <= 0 ) return null;		
		lasstRequestedMessage = i - 1;
		return smsMessages.get(lasstRequestedMessage);
	}
	
	public SmsParserMessage getLastMessage(){
		if (smsCount <= 0) return null; 
		lasstRequestedMessage = smsCount - 1;
		return smsMessages.get(lasstRequestedMessage);
	}
	
	public SmsParserMessage getNextMessage(){
		if (smsCount <= 0) return null;
		if (lasstRequestedMessage + 1 < smsCount){
			lasstRequestedMessage ++;
			return smsMessages.get(lasstRequestedMessage);
		}else return null;			
	}
	
	public SmsParserMessage getPrevMessage(){
		if (smsCount <= 0) return null;
		if (lasstRequestedMessage - 1 >= 0 && lasstRequestedMessage < smsCount ){
			lasstRequestedMessage --;
			return smsMessages.get(lasstRequestedMessage);
		}else return null;			
	}	

	public SmsParserMessage getCurrentMessage(){		
		if (smsCount <= 0 || lasstRequestedMessage >= smsCount || lasstRequestedMessage < 0 ) return null;
		return smsMessages.get(lasstRequestedMessage);			
	}	
	
	private void deleteRequestedMessage(){
		SmsParserMessage  message;	
		AppEx.v("---- lasstRequestedMessage------" + lasstRequestedMessage);
		AppEx.v("---- smsCount------" + smsCount);
		if (smsCount > 0 && lasstRequestedMessage >= 0 && lasstRequestedMessage < smsCount){
			message =  smsMessages.get(lasstRequestedMessage);			
			smsMessages.remove(message);			
			if (smsMessages != null){ smsCount = smsMessages.size();}else {smsCount = 0;} 			
			if (lasstRequestedMessage >= smsCount) lasstRequestedMessage --;
			
			AppEx.v("---- lasstRequestedMessage2------" + lasstRequestedMessage);
			AppEx.v("---- smsCount2------" + smsCount);
		}
	}
	
	
	public void parse(Spannable str){
		AppEx.v("===sp===PARSING===");		
			
		Matcher mt = pPass.matcher(str);	
		while (mt.find()) {
			str.setSpan(new StyleSpan(android.graphics.Typeface.BOLD ), mt.start(1), mt.end(1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			str.setSpan(new ForegroundColorSpan(Color.GREEN), mt.start(1), mt.end(1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		mt = pBank.matcher(str);	
		while (mt.find()) {
		for (int i = 1;i<4;i++){	
			if (mt.group(i) != null){
			 str.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), mt.start(i), mt.end(i), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			 str.setSpan(new ForegroundColorSpan(Color.GREEN), mt.start(i), mt.end(i), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			 			 
			}
		}}
	}
	
	public String getPositionInfo(){		
		if (smsCount <= 0 || lasstRequestedMessage >= smsCount || lasstRequestedMessage < 0 ) return "0/0";
		return (lasstRequestedMessage +1) + "/" + smsCount;			
	}	
	
	
}

