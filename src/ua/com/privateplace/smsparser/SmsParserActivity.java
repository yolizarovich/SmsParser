package ua.com.privateplace.smsparser;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.text.Spannable;


public class SmsParserActivity extends ActivityEx implements OnClickListener {	
	static final String LEFT = "left";
	static final String RIGHT = "right";
	static final int    SMS_DELETE_CONFIRM = 1;
	
	private TextView messageTV;
	//private TextView statusbar;
	private TextView positioninfo;
	private Handler handler = new Handler(){ @Override public void  handleMessage(Message msg) {updateView();}};	
	private SmsParserMessageManager smsMngr = null;
	private SmsParserMessage smsMsg = null;
	
	private boolean justcreated = true;
    
	private ViewFlipper flipper; 
	private float oldTouchValue;	
	private RadioGroup groupRB;
		
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	msg("SmsParserActivity: ----------------------------------->onCreate");
    	
        setContentView(R.layout.main_r);
        smsMngr = getAppContext().getSmsManager();
        initViews();
        
        
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
       //statusbar = (TextView) findViewById(R.id.statusbar_text);     
       positioninfo = (TextView) findViewById(R.id.btnheader_text);
       
      /* findViewById(R.id.next_button).setOnClickListener(this);
       findViewById(R.id.prev_button).setOnClickListener(this);    */
      // findViewById(R.id.systembutton).setOnClickListener(this); 
       
       flipper=(ViewFlipper)findViewById(R.id.details);        
       groupRB=(RadioGroup)findViewById(R.id.SmsRadioGroup);  
    }
    
    public void updateView(){
    	if (smsMsg != null && smsMsg.messageBody.length() != 0){
    		msg("SmsParserActivity:---------------------------------->updateView()");
    		smsMngr.parseRequestedMessage();    		
    		messageTV.setText(smsMsg.messageBody, TextView.BufferType.SPANNABLE);
    		//statusbar.setText("------------>readed == "+smsMsg.isRead);
    		ArrayList<String> items = smsMsg.parsedMessageBody;    		
    		smsMngr.parse((Spannable)messageTV.getText());
    		smsMngr.setMessageRead(this);    		
    		//msg("----------------------------------------------->"+smsMsg.messageBody);
    		
    		int b_id = 10000;
    		flipper.removeAllViews();
    		if (items != null)
	        for (String item : items) { 	        	
	    	  TextView btn=new TextView(this);
	    	  btn.setTextSize(30);
	    	  btn.setTextColor(android.graphics.Color.GREEN);
	          b_id ++;
	          btn.setId(b_id);           
	          btn.setText(item); 
	          flipper.addView(btn, 
	          new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 
	                                     ViewGroup.LayoutParams.FILL_PARENT)); 
	        } 
    		
    		positioninfo.setText( getString(R.string.btnheader_label) + "   " + smsMngr.getPositionInfo());
    		
    		/*flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
        	flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
        	flipper.setFlipInterval(3000); 
        	flipper.startFlipping();*/
    	}
    	else if (smsMngr.getCurrentMessage() == null)
    	{
    		messageTV.setText(R.string.default_message_text, TextView.BufferType.SPANNABLE);
    		flipper.removeAllViews();
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
    	switch (groupRB.getCheckedRadioButtonId()){
    	case R.id.sms_button:
        	flipper.clearAnimation();
    		if (dir == LEFT){
            	flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in)); 
                flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
            	flipper.showPrevious();	
    		}
    		if(dir == RIGHT){
            	flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
            	flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
            	flipper.showNext();    			
    		}    		
    		break;
    	case R.id.smslist_button:
    		if (dir == LEFT) smsMsg = smsMngr.getNextMessage();
    		if(dir == RIGHT) smsMsg = smsMngr.getPrevMessage();
        	updateView();        	
    		break;
    	}    	
    }
    
  /*  
    @Override
    protected Dialog onCreateDialog(int id) {
        super.onCreateDialog(id);
        switch (id) {
            case SMS_DELETE_CONFIRM:
                // новый лайаут для диалога
                LinearLayout confirmView = new LinearLayout(this);

                // создаем диалог с параметрами и двумя кнопками
                return new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(confirmView)
                        .setTitle(R.string.conf_del_sms_label)
                        .setMessage(R.string.conf_del_sms_text)
                        .setPositiveButton(R.string.conf_del_sms_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {                            	
                            	delete_current_message();                                
                            }
                        })
                        .setNegativeButton(R.string.conf_del_sms_cancel, null)
                        .create();
            default:
                return null;
        }
    }    
    */
    
    
	/*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
	public void setUpdate(){
		getAppContext().getSmsManager().getUnreadMessages(getAppContext());
		smsMsg = smsMngr.getLastMessage();		
		handler.sendEmptyMessage(0);		 
	}   
    
}