package com.pintact.setting;

import com.pintact.MainActivity;
import com.pintact.R;
import com.pintact.utility.HttpConnection;
import com.pintact.utility.MyActivity;
import com.pintact.utility.SingletonLoginData;
import com.pintact.utility.SingletonNetworkStatus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class SettingMainActivity extends MyActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		//////////////////////////////////////////
		/// NOT USED ANYMORE //////////////////// 
		//////////////////////////////////////////
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_main);
		hideRight();
		
		loadPreferences();

		TextView tv = (TextView)findViewById(R.id.actionBar);
		tv.setText(getResources().getString(R.string.ab_setting));

		Button btn = (Button)findViewById(R.id.actionBarBtn);
		btn.setVisibility(View.INVISIBLE);

		TextView tv2 = (TextView)findViewById(R.id.actionBarRight);
		tv2.setVisibility(View.INVISIBLE);
		
		TextView logout = (TextView) findViewById(R.id.set_logout);
		logout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("Logging out...");
				logout();
			}
		});
		
		// set default value for some settings;
		Switch stLocal = (Switch) findViewById(R.id.set_broadcast_switch);
		Switch stPush  = (Switch) findViewById(R.id.set_push_switch);
		stLocal.setChecked(SingletonLoginData.getInstance().getUserSettings().local == 1);
		stPush.setChecked(SingletonLoginData.getInstance().getUserSettings().push == 1);
		
		stLocal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
		    	updatePreferencesLocal( isChecked ? 1 : 0);
				
			}
		});
		
		stPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
		    	updatePreferencesPush( isChecked ? 1 : 0);
				
			}
		});
		
		System.out.println("Finished setting....");
		
	}

	public void logout() {
		String path = "/api/users/logout.json?" + SingletonLoginData.getInstance().getPostParam();
		SingletonNetworkStatus.getInstance().setActivity(this);
		new HttpConnection().access(this, path, "", "POST");
	}

	public void onPostNetwork() {
		// clear data
		SingletonLoginData.getInstance().clean();
		
		// return to login page
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	public void onDummy(View view) {
	}


}
