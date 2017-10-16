package com.pintact.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pintact.R;
import com.pintact.utility.MyActivity;

public class LoginForgotActivity extends MyActivity {

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.login_forgot);
			setupUI(findViewById(R.id.login_forgot_view));
			
			Button btn = (Button) findViewById(R.id.lfSend);
			btn.setFocusableInTouchMode(true);
			btn.requestFocus();
			
			TextView tv = (TextView)findViewById(R.id.actionBar);
			tv.setText(getResources().getString(R.string.ab_forgot));
			
			hideBoth();
		}
		
		public void onDummy(View view) {
		}
		
	    @Override
	    public void onBackPressed() {
	    	System.out.println("OnBackPressed - Activity.");
	    	finish();
			overridePendingTransition( R.anim.slide_in_down, R.anim.slide_out_down );
	    	return;
	    }
		
		
}
