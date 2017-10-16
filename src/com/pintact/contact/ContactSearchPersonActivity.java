package com.pintact.contact;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.pintact.R;
import com.pintact.utility.MyActivity;

public class ContactSearchPersonActivity extends MyActivity {

		ListView lvContact;
		Cursor people;
		ContactInviteLVAdapter adapter;
		boolean isSelectAll = false;
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.contact_search_view);
			
			TextView tv = (TextView)findViewById(R.id.actionBar);
			tv.setText(getResources().getString(R.string.ab_search_person));
			
			hideBoth();
			//showRightText(getResources().getString(R.string.ab_cancel));
			
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
			
		}
		
		public void searchPin(View v) 
		{
			Intent it = new Intent(this, ContactInviteMsgActivity.class);

			startActivity(it);
			
		}
		
		public void searchPerson(View v) 
		{
			Intent it = new Intent(this, ContactInviteMsgActivity.class);

			startActivity(it);
			
		}
		
		
		
}
