package com.pintact.contact;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pintact.R;
import com.pintact.utility.MyActivity;

public class ContactAddActivity extends MyActivity {

		ListView lvContact;
		Cursor people;
		ContactInviteLVAdapter adapter;
		boolean isSelectAll = false;
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.contact_add_main);
			
			TextView tv = (TextView)findViewById(R.id.actionBar);
			tv.setText(getResources().getString(R.string.ab_invite_add));
			
			hideBoth();
			//showRightText(getResources().getString(R.string.ab_cancel));
			
			View.OnClickListener pinClkLn = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					searchPerson(v);
				}
			};

			View.OnClickListener personClkLn = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					searchPerson(v);
				}
			};


			View.OnClickListener finClkLn = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			};

			
			//Button btn = (Button)findViewById(R.id.actionBarBtn);
			//btn.setOnClickListener(finClkLn);
			showRightImage(R.drawable.white_cross);
			addRightImageLn(finClkLn);			
			
			// pin image view clicked
			ImageView ivAll = (ImageView) findViewById(R.id.cam_pin);
			ivAll.setOnClickListener(pinClkLn);
			
			// person image view clicked
			ImageView ivSearch = (ImageView) findViewById(R.id.cam_search);
			ivSearch.setOnClickListener(personClkLn);
			
		}
		
		public void searchPin(View v) 
		{
			Intent it = new Intent(this, ContactInviteMsgActivity.class);

			startActivity(it);
			
		}
		
		public void searchPerson(View v) 
		{
			Intent it = new Intent(this, ContactSearchPersonActivity.class);

			startActivity(it);
			
		}
		
		
		
}
