package ua.com.privateplace.smsparser;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.text.Spannable;


public class SmsParserActivity extends ActivityEx implements OnClickListener {	
	static final String LEFT = "left";
	static final String RIGHT = "right";
	static final int    SMS_DELETE_CONFIRM = 1;
	
	private TextView messageTV;
	private TextView positioninfo;
	private Handler handler = new Handler(){ @Override public void  handleMessage(Message msg) {updateView();}};	
	private SmsParserMessageManager smsMngr = null;
	private SmsParserMessage smsMsg = null;
	
	private boolean justcreated = true;
	private float oldTouchValue;	
		
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	msg("SmsParserActivity: ----------------------------------->onCreate");
    	
        setContentView(R.layout.main_r);
        smsMngr = getAppContext().getSmsManager();
        msg("SmsParserActivity: ----------------------------------->initv start");
        initViews();
        msg("SmsParserActivity: ----------------------------------->initv stop");
        
        
        if (smsMngr != null) {
        	smsMngr.getUnreadMessages(this);
        	smsMsg = smsMngr.getLastMessage();
        }       
        updateView();                
    }
       
    @Override
    public void onResume() {
        super.onResume();      
        msg("SmsParserActivity: ----------------------------------->onResume");
		getAppContext().setCurrentActivity(this);
		if (!justcreated) {
	        if (smsMngr != null) {
	        	smsMngr.getUnreadMessages(this);
	        	smsMsg = smsMngr.getLastMessage();
	        }       
	        updateView();	
	        msg("SmsParserActivity: ----------------------------------->onResumeUPDATE");
		}
    }      
    
    @Override
    public void onPause() { 
        super.onPause();
        justcreated = false;
        msg("SmsParserActivity: ----------------------------------->onPause");
        getAppContext().setCurrentActivity(null);
    }
    
    /*@Override
    public void onDestroy() {
        super.onDestroy(); 
        msg("SmsParserActivity: ----------------------------------->onDestroy");        
    }*/   
    
   
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       super.onCreateOptionsMenu(menu);
       MenuInflater inflater = getMenuInflater();
       inflater.inflate(R.menu.menu, menu);
       return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {
       case R.id.delete_sms:
    	  delete_current_message();
    	  return true;    	   
       case R.id.settings:
          startActivity(new Intent(this, Settings.class));
          return true;       
       case R.id.about:
           startActivity(new Intent(this, About.class));
           return true;          
       case R.id.exit_button: 	
           finish();
           return true;
       }
       return false;
    }    
    
    
    private void initViews() {
       messageTV = (TextView) findViewById(R.id.sms_text);
       positioninfo = (TextView) findViewById(R.id.btnheader_text);
    }
    
    public void updateView(){
    	if (smsMsg != null && smsMsg.messageBody.length() != 0){
    		msg("SmsParserActivity:---------------------------------->updateView()");    		    		
    		messageTV.setText(smsMsg.messageBody, TextView.BufferType.SPANNABLE);
    		smsMngr.parse((Spannable)messageTV.getText());
    		smsMngr.setMessageRead(this);    		
    		positioninfo.setText( getString(R.string.btnheader_label) + "   " + smsMngr.getPositionInfo());
    	}
    	else if (smsMngr.getCurrentMessage() == null)
    	{
    		messageTV.setText(R.string.default_message_text, TextView.BufferType.SPANNABLE);    		
    	}
	}

   public void onClick(View v) {
        switch (v.getId()) {
        /*case R.id.next_button:
        	flip(LEFT);
        	break;
        case R.id.prev_button:
        	flip(RIGHT);
        	break;*/
        /*case R.id.systembutton:
        	//smsMngr.setMessageRead(this);
        	showDialog(SMS_DELETE_CONFIRM);
        	break;*/
        }
    }         
    
   
    @Override
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction())
        {
            case MotionEvent.ACTION_DOWN:            
                oldTouchValue = touchevent.getX();                
                break;            
            case MotionEvent.ACTION_UP:                        	
	            float currentX = touchevent.getX();
	            if (oldTouchValue < currentX) flip(RIGHT);
	            if (oldTouchValue > currentX) flip(LEFT);                
	            break;            
        }
        return false;
    }
    
    private void delete_current_message(){    	
    	smsMngr.deleteCurrentMessage(this);
    	smsMsg = smsMngr.getCurrentMessage();
    	updateView();    	
    }
    
    
    private void flip(String dir){    	
		if (dir == LEFT ) smsMsg = smsMngr.getNextMessage();
		if (dir == RIGHT) smsMsg = smsMngr.getPrevMessage();
    	updateView();	
    }
    
  
	/*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
	public void setUpdate(){
		getAppContext().getSmsManager().getUnreadMessages(getAppContext());
		smsMsg = smsMngr.getLastMessage();		
		handler.sendEmptyMessage(0);		 
	}   
    
}