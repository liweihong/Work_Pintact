package com.pintact.contact;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.pintact.LeftDeckActivity;
import com.pintact.R;
import com.pintact.utility.MyActivity;

public class ContactInviteMsgActivity extends MyActivity {
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.contact_invite_message);
			
			TextView tv = (TextView)findViewById(R.id.actionBar);
			tv.setText(getResources().getString(R.string.ab_invite_all));
			
			hideBoth();
			
			TextView back = (TextView)findViewById(R.id.lrBack);
			back.setText(Html.fromHtml(getString(R.string.lrp_text_back)));
			back.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});

			TextView send = (TextView)findViewById(R.id.im_send);
			send.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onLogin(v);
				}
			});
			
			
		}
		
		public void onLogin(View view) {
			Intent it = new Intent(this, LeftDeckActivity.class);
			
			startActivity(it);
		}		
		
}
