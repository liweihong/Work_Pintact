package com.pintact.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.pintact.R;
import com.pintact.profile.ProfileNewActivity;
import com.pintact.profile.ProfileViewNewActivity;
import com.pintact.utility.MyActivity;
import com.pintact.utility.SingletonLoginData;

public class LoginRegisterSuccessActivity extends MyActivity {
		TextView errMsg;
		EditText middleName;
	

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.login_register_success);
			hideBoth();
			
			TextView next = (TextView)findViewById(R.id.lrsProfile);
			next.setText(Html.fromHtml(getString(R.string.lrs_create_profile)));		
			
			TextView tv = (TextView)findViewById(R.id.actionBar);
			tv.setText(getResources().getString(R.string.ab_register_success));

			TextView pin = (TextView) findViewById(R.id.lrsPin);
			pin.setText(SingletonLoginData.getInstance().getUserData().pin);
			
			saveLoginData();
		}
		
	    public void saveLoginData() {
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(getString(R.string.login_username), SingletonLoginData.getInstance().getSignupRequest().getEmailId());
			editor.putString(getString(R.string.login_password), SingletonLoginData.getInstance().getSignupRequest().getPassword());
			editor.commit();
			
	    }		
		
		public void onDummy(View view) {
		}
		
		public void onNext(View view) {
			finish();
			overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_right );
		}

		public void onNewProfile(View view) {
			Intent it = new Intent(this, ProfileViewNewActivity.class);
        	it.putExtra(ProfileViewNewActivity.ARG_PROFILE_VIEW, -2);
			startActivity(it);
			overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left );

		}

	    @Override
	    public void onBackPressed() {
	    	System.out.println("OnBackPressed - Activity.");
			finish();
			overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_right );
	    }
		
		
}
