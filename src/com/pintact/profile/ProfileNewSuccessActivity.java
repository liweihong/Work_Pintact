package com.pintact.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pintact.R;
import com.pintact.contact.ContactInviteActivity;
import com.pintact.utility.MyActivity;

public class ProfileNewSuccessActivity extends MyActivity {

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.profile_create_success);
			
			TextView tv = (TextView)findViewById(R.id.actionBar);
			tv.setText(getResources().getString(R.string.ab_new_profile_success));
			
			TextView goodJob = (TextView)findViewById(R.id.pcs_good);
			goodJob.setText(Html.fromHtml(getString(R.string.pcs_good)));		

			TextView next = (TextView)findViewById(R.id.pcs_next);
			next.setText(Html.fromHtml(getString(R.string.lr_text_next)));		
			
			View.OnClickListener finClkLn = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onInvite(v);
				}
			};

			Button btn = (Button)findViewById(R.id.actionBarBtn);
			btn.setOnClickListener(finClkLn);
			next.setOnClickListener(finClkLn);
			hideLeft();
			
		}
		
		public void onInvite(View view) {
			Intent it = new Intent(this, ContactInviteActivity.class);
			startActivity(it);
		}
		
		
}
