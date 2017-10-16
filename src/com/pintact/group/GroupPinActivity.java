package com.pintact.group;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pintact.R;
import com.pintact.data.ContactShareRequest;
import com.pintact.data.GroupDTO;
import com.pintact.data.NotificationDTO;
import com.pintact.data.PageDTO;
import com.pintact.utility.HttpConnection;
import com.pintact.utility.MyActivity;
import com.pintact.utility.SingletonLoginData;
import com.pintact.utility.SingletonNetworkStatus;

public class GroupPinActivity extends MyActivity {
	
		boolean isInitInquire = false;
		boolean isUpdateGroup = false;
		GroupDTO mGroup;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.group_pin_name);
			setupUI(findViewById(R.id.group_pin_view));
			
			TextView btn = (TextView) findViewById(R.id.gpn_pin_tv);
			btn.setFocusableInTouchMode(true);
			btn.requestFocus();
			
			TextView tv = (TextView)findViewById(R.id.actionBar);
			tv.setText(getResources().getString(R.string.ab_group_info));
			
			showRightText(getResources().getString(R.string.action_bar_option));
			showLeftImage(R.drawable.left_arrow);
			
			View.OnClickListener backLn = new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			};
			addLeftLn(backLn);

			View.OnClickListener nextLn = new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onProfileShare();
				}
			};
			addRightLn(nextLn);
			
			if ( SingletonLoginData.getInstance().getCurGroup().getGroupPin() != null ) {
				// set information
	        	TextView gp = (TextView ) findViewById(R.id.gm_pin);
	        	EditText gn = (EditText) findViewById(R.id.gm_name);
	        	mGroup = SingletonLoginData.getInstance().getCurGroup();
	        	gp.setText(mGroup.getGroupPin());
	        	gn.setText(mGroup.getGroupName());
	        	
	        	String expStr = mGroup.getExpiredTime();
	        	if ( expStr != null && expStr.length() > 1 ) {
	        		long expTime = Long.parseLong(expStr);
	        		Calendar cl = Calendar.getInstance();
	        		cl.setTimeInMillis(expTime);  //here your time in mili-seconds	        		
		        	DatePicker dp = (DatePicker) findViewById(R.id.gpn_date);
		        	dp.updateDate(cl.get(Calendar.YEAR), cl.get(Calendar.MONTH), cl.get(Calendar.DAY_OF_MONTH));
	        	}
				
				return;
			}
			
			// inquire group pin
			isInitInquire = true;
			SingletonNetworkStatus.getInstance().setActivity(this);
    		String path = "/api/groupPins.json?" + SingletonLoginData.getInstance().getPostParam();
    		new HttpConnection().access(this, path, "", "POST");
			
			
		}
		
		public class GroupUpdate{
			public String expiryTimeInUTC;
			public String name;
			public String groupPin;
		}
		
		public void onProfileShare() {
			
			// do validation on exp date
        	long seconds = System.currentTimeMillis();
        	Calendar cal = Calendar.getInstance();
        	DatePicker datePicker = (DatePicker) findViewById(R.id.gpn_date);
        	cal.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        	cal.set(Calendar.MONTH, datePicker.getMonth());
        	cal.set(Calendar.YEAR, datePicker.getYear());
        	long expTime = cal.getTimeInMillis();
        	System.out.println("cur : " + seconds + " == exp : " + expTime);
        	if ( expTime < seconds ) {
        		myDialog("Error", "You cannot set an expiration date earlier than today!");
        		return;
        	}
        	TextView gpTV = (TextView ) findViewById(R.id.gm_pin);
        	EditText gnTV = (EditText) findViewById(R.id.gm_name);
        	
        	String gp = gpTV.getText().toString();
        	String gn = gnTV.getText().toString();
        	if ( gn == null || gn.length() < 1 ) {
        		myDialog("Error", "You must specify a group name!");
        		return;
        	}

        	isUpdateGroup = true;
        	
        	GroupUpdate gu = new GroupUpdate();
        	gu.name = gn;
        	gu.groupPin = gp;
        	gu.expiryTimeInUTC = Long.toString(expTime);
        	
        	mGroup.setGroupName(gn);
        	mGroup.setExpiredTime(gu.expiryTimeInUTC);
        	
			SingletonNetworkStatus.getInstance().setActivity(this);
    		String path = "/api/groupPins/update.json?" + SingletonLoginData.getInstance().getPostParam();
			Gson gson = new GsonBuilder().create();
    		String params = gson.toJson(gu); 
    		new HttpConnection().access(this, path, params, "POST");
		}
		
		public void onDummy(View view) {
		}
		
	    @SuppressLint("NewApi")
		public void onPostNetwork() {
	    	
			if ( SingletonNetworkStatus.getInstance().getCode() != 200 ) {
				myDialog(SingletonNetworkStatus.getInstance().getMsg(), 
						SingletonNetworkStatus.getInstance().getErrMsg());
				return;
			}
			
			// if this is initial inquiring.
			if ( isInitInquire ) {
				isInitInquire = false;

				Gson gson = new GsonBuilder().create();
	        	mGroup = gson.fromJson(SingletonNetworkStatus.getInstance().getJson(), GroupDTO.class);
	        	TextView gp = (TextView ) findViewById(R.id.gm_pin);
	        	gp.setText(mGroup.getGroupPin());
	        	SingletonLoginData.getInstance().setCurGroup(mGroup);
	        	SingletonLoginData.getInstance().getGroups().add(mGroup);
	        	return;
			}

			if ( isUpdateGroup ) {
				isUpdateGroup = false;
				
		    	ContactShareRequest req = new ContactShareRequest();
				req.setSourceUserId(Long.parseLong(SingletonLoginData.getInstance().getUserData().id));
				req.setDestinationPin(mGroup.getGroupPin());
				SingletonLoginData.getInstance().setContactShareRequest(req);
				
				Intent it = new Intent (this, GroupProfileShareActivity.class);
		    	it.putExtra(GroupProfileShareActivity.ARG_PROFILE_SHARE, -1);
				startActivity(it);
				return;
			}
	    	
	    }
}
