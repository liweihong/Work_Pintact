package com.pintact.contact;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pintact.R;
import com.pintact.utility.HttpConnection;
import com.pintact.utility.MyActivity;
import com.pintact.utility.SingletonLoginData;
import com.pintact.utility.SingletonNetworkStatus;


public class ContactIntroduceActivity extends MyActivity {
	
		String mFirstName1, mFirstName2, mFirstName3;
		EditText mETMsg;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.contact_introduce);
			setupUI(findViewById(R.id.contact_intro_view));

			mFirstName1 = SingletonLoginData.getInstance().getIntroducedProfile().getUserProfile().getFirstName();
			mFirstName2 = SingletonLoginData.getInstance().getMergedProfile().getUserProfile().getFirstName();
			mFirstName3 = SingletonLoginData.getInstance().getUserData().firstName;
			
			mETMsg = (EditText)findViewById(R.id.gps_msg_cont);
			String msg = "Hi " + mFirstName1 + ", I would like to inroduce you to " +
			mFirstName2 + ". I think you should connect on Pintact! - " + mFirstName3;
			mETMsg.setText(msg);
			
			hideRight();
			
			showLeftImage(R.drawable.left_arrow);
			View.OnClickListener backLn = new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			};
			addLeftLn(backLn);
			
			View.OnClickListener sendLn = new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onSend(v);
				}
			};
			
			TextView tvSend = (TextView)findViewById(R.id.gm_preview);
			tvSend.setOnClickListener(sendLn);
		}
		
		public void onSend(View view) {
			
			// Json: userId1: mFirstName2, userId2: mFirstName1, message:
			// /api/users/introduce.json + params
			introRequest ir = new introRequest();
			ir.userId1 = SingletonLoginData.getInstance().getMergedProfile().getUserId();
			ir.userId2 = SingletonLoginData.getInstance().getIntroducedProfile().getUserId();
			ir.message = mETMsg.getText().toString();
			
			Gson gson = new GsonBuilder().create();
			String params = gson.toJson(ir);

			SingletonNetworkStatus.getInstance().setActivity(this);
			String path = "/api/users/introduce.json?" + SingletonLoginData.getInstance().getPostParam();
			new HttpConnection().access(this, path, params, "POST");
			
		}
		
		public void onPostNetwork () {
			
			if ( SingletonNetworkStatus.getInstance().getCode() != 200 ) {
				myDialog(SingletonNetworkStatus.getInstance().getMsg(), 
						SingletonNetworkStatus.getInstance().getErrMsg());
				
				return;
			}
			
			finish();
		}
		
		public class introRequest {
			public Long userId1;
			public Long userId2;
			public String message;
		}
		
}
