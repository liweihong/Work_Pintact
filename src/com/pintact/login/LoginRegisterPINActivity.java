package com.pintact.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pintact.R;
import com.pintact.utility.DataLoginData;
import com.pintact.utility.HttpConnection;
import com.pintact.utility.MyActivity;
import com.pintact.utility.SingletonLoginData;
import com.pintact.utility.SingletonNetworkStatus;

public class LoginRegisterPINActivity extends MyActivity {
		TextView pinPad;
		EditText init;
		int sendCmd = 0;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.login_register_pin);
			setupUI(findViewById(R.id.login_pin_view));
			hideLeft();
			
			TextView next = (TextView)findViewById(R.id.lrBack);
			next.setText(Html.fromHtml(getString(R.string.lrp_text_back)));		
			
			String fn = SingletonLoginData.getInstance().getSignupRequest().getFirstName().substring(0,1).toLowerCase();
			String ln = SingletonLoginData.getInstance().getSignupRequest().getLastName().substring(0,1).toLowerCase();
			String initStr;
			if ( SingletonLoginData.getInstance().getSignupRequest().getMiddleName().length() > 0 ) {
				String mn = SingletonLoginData.getInstance().getSignupRequest().getMiddleName().substring(0,1).toLowerCase();
				initStr = fn+mn+ln;
			} else
				initStr = fn + ln;
			
			init = (EditText)findViewById(R.id.lrPinInitial);
			init.setText(initStr);
			
			
			View.OnClickListener clickLn = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onBack(v);
				}
			};

			View.OnClickListener finClkLn = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onSuccess(v);
				}
			};

			Button btnPin = (Button) findViewById(R.id.lrPinSend);
			btnPin.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onPinGen();
				}
			});
			
			next.setClickable(true);
			next.setOnClickListener(clickLn);
			next.setFocusableInTouchMode(true);
			next.requestFocus();
			
			TextView tv = (TextView)findViewById(R.id.actionBar);
			tv.setText(getResources().getString(R.string.ab_pin));

			TextView finish = (TextView)findViewById(R.id.lrFinish);
			finish.setText(Html.fromHtml(getString(R.string.lrp_text_finish)));
			finish.setOnClickListener(finClkLn);
			
			Button btn = (Button)findViewById(R.id.actionBarBtn);
			btn.setOnClickListener(finClkLn);
			
		}
		
		public void onBack(View v) {
			finish();
			overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_right );
		}

	    @Override
	    public void onBackPressed() {
	    	System.out.println("OnBackPressed - Activity.");
	    	onBack(null);
	    }
		
		public void onPinGen() {
			String params = "{\"firstName\":\"" + SingletonLoginData.getInstance().getSignupRequest().getFirstName() + 
						 "\",\"middleName\":\"" + SingletonLoginData.getInstance().getSignupRequest().getMiddleName() +
						 "\",\"lastName\":\"" + SingletonLoginData.getInstance().getSignupRequest().getLastName() +
						 "\"}";
			SingletonNetworkStatus.getInstance().setActivity(this);
			String path = "/api/pins/suggestPin.json";
			new HttpConnection().access(this, path, params, "POST");
			
			sendCmd = 0;
		}
		
		
		public void onSuccess(View view) {
			String pinStr = ((EditText)findViewById(R.id.lrPinInitial)).getText().toString();
			SingletonLoginData.getInstance().getSignupRequest().setPin(pinStr);
				
			Gson gson = new GsonBuilder().create();
			String params = gson.toJson(SingletonLoginData.getInstance().getSignupRequest());
			
			SingletonNetworkStatus.getInstance().setActivity(this);
			String path = "/api/users.json";
			new HttpConnection().access(this, path, params, "POST");

			sendCmd = 1;
		}
		
		public void onPostNetwork() {
			
			if ( SingletonNetworkStatus.getInstance().getCode() != 200 ) {
				myDialog(SingletonNetworkStatus.getInstance().getMsg(), 
						SingletonNetworkStatus.getInstance().getErrMsg());
				return;
			}
			
			if ( sendCmd == 0 ) {
				Gson gson = new GsonBuilder().create();
				String[] pinStr = gson.fromJson(SingletonNetworkStatus.getInstance().getJson(), String[].class);
				
				init.setText(pinStr[0]);
				return;
			}
			
			Gson gson = new GsonBuilder().create();
			DataLoginData obj = gson.fromJson(SingletonNetworkStatus.getInstance().getJson(), DataLoginData.class);
			SingletonLoginData.getInstance().setAccessToken(obj.accessToken);
			SingletonLoginData.getInstance().setUserDTO(obj.userDTO);
			
			Intent it = new Intent(this, LoginRegisterSuccessActivity.class);
			startActivity(it);
			overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left );
			
		}
		
		public void onDummy(View view) {
		}
		
}
