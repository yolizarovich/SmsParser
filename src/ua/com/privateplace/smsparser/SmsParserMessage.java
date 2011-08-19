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

}
