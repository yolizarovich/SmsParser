package ua.com.privateplace.smsparser;

import java.util.ArrayList;

public class SmsParserMessage {
	public String fromAddress = null;
	public String messageBody = null;	
	public ArrayList<String> parsedMessageBody = null;
	public long threadId = 0;	
	public long messageId = 0;
	public long timestamp = 0;	
	public boolean isRead = false;	
	//public String contactId = null;
	//public String contactLookupKey = null;
	//public String contactName = null;
	//public boolean fromEmailGateway = false;
}










/*	private static final String PREFIX = "ua.com.privateplace.smsparser.";
	private static final String EXTRAS_FROM_ADDRESS   = PREFIX + "EXTRAS_FROM_ADDRESS";
	private static final String EXTRAS_MESSAGE_BODY   = PREFIX + "EXTRAS_MESSAGE_BODY";
	private static final String EXTRAS_THREAD_ID      = PREFIX + "EXTRAS_THREAD_ID";
	private static final String EXTRAS_CONTACT_ID     = PREFIX + "EXTRAS_CONTACT_ID";
	private static final String EXTRAS_CONTACT_LOOKUP = PREFIX + "EXTRAS_CONTACT_LOOKUP";
	private static final String EXTRAS_CONTACT_NAME   = PREFIX + "EXTRAS_CONTACT_NAME";	  
	private static final String EXTRAS_MESSAGE_ID     = PREFIX + "EXTRAS_MESSAGE_ID";
	private static final String EXTRAS_EMAIL_GATEWAY  = PREFIX + "EXTRAS_EMAIL_GATEWAY";
	
	public Bundle toBundle() {
	  Bundle b = new Bundle();
	  b.putString(EXTRAS_FROM_ADDRESS, fromAddress);
	  b.putString(EXTRAS_MESSAGE_BODY, messageBody);  
	  b.putString(EXTRAS_CONTACT_ID, contactId);
	  b.putString(EXTRAS_CONTACT_LOOKUP, contactLookupKey);
	  b.putString(EXTRAS_CONTACT_NAME, contactName);
	  b.putLong(EXTRAS_THREAD_ID, threadId);
	  b.putLong(EXTRAS_MESSAGE_ID, messageId);
	  b.putBoolean(EXTRAS_EMAIL_GATEWAY, fromEmailGateway);
	  return b;
	} */

