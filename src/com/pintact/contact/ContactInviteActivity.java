package com.pintact.contact;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pintact.LeftDeckActivity;
import com.pintact.R;
import com.pintact.utility.MyActivity;

public class ContactInviteActivity extends MyActivity {
	
		public static final String ARG_INVITE_ACTIVITY = "invite_activity_view";
		
		int mArgInt = 0;

		ListView lvContact;
		Cursor people;
		ContactInviteLVAdapter adapter;
		boolean isSelectAll = false;
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.contact_invite_main);
			
			TextView tv = (TextView)findViewById(R.id.actionBar);
			tv.setText(getResources().getString(R.string.ab_invite_all));
			
			TextView goodJob = (TextView)findViewById(R.id.im_invite);
			goodJob.setText(Html.fromHtml(getString(R.string.im_invite)));		

			TextView skip = (TextView)findViewById(R.id.im_skip);

			if ( getIntent().getExtras() != null )
				mArgInt = getIntent().getExtras().getInt(ARG_INVITE_ACTIVITY);
			
			if ( mArgInt == 0)
				hideLeft();
			else {
				showLeftImage(R.drawable.left_arrow);
				View.OnClickListener backLn = new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						finish();
					}
				};
				addLeftLn(backLn);
				hideRight();
				skip.setVisibility(View.INVISIBLE);
			}
			
			View.OnClickListener finClkLn = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					inviteContactList(v);
				}
			};

			View.OnClickListener addClkLn = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showContactList(v);
				}
			};
			
			skip.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onLogin(v);
				}
			});
			

			Button btn = (Button)findViewById(R.id.actionBarBtn);
			btn.setOnClickListener(finClkLn);
			goodJob.setOnClickListener(finClkLn);
			
			// set invite all button
			ImageView ivAll = (ImageView) findViewById(R.id.im_title);
			ivAll.setOnClickListener(addClkLn);
			
			// get list view
			lvContact = (ListView) findViewById(R.id.im_list);
			Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
			String[] projection    = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
		    String sortOrder =  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
			people = getContentResolver().query(uri, projection, null, null, sortOrder);
			
			// Create the Adapter and set
			adapter = new ContactInviteLVAdapter(this, people);	
			lvContact.setAdapter(adapter);
		
			// to handle click event on listView item
			lvContact.setOnItemClickListener(new ListView.OnItemClickListener() {
				@Override
	            public void onItemClick(AdapterView<?> arg0, View v,int position, long arg3) {
	                // when user clicks on ListView Item , onItemClick is called 
	                // with position and View of the item which is clicked
	                // we can use the position parameter to get index of clicked item

					ImageView ivSelect = (ImageView) v.findViewById(R.id.im_check);
					if (lvContact.isItemChecked(position)) {
						ivSelect.setImageResource(R.drawable.circle_check);
					} else {
						ivSelect.setImageResource(R.drawable.circle);
					}
					
	            }

	        });			
		}
		
		public void onLogin(View view) {
			Intent it = new Intent(this, LeftDeckActivity.class);
			
			startActivity(it);
		}		

		public void showContactList(View v) 
		{
			isSelectAll = !isSelectAll;
			ImageView tmp = (ImageView) v;
			tmp.setImageResource(isSelectAll ? R.drawable.circle_check : R.drawable.circle);
			for ( int i=0; i< adapter.getCount(); i++ ) {
			        lvContact.setItemChecked(i, isSelectAll);
			}
			
		}
		
		public void inviteContactList(View v) 
		{
			Intent it = new Intent(this, ContactInviteMsgActivity.class);

			startActivity(it);
			
		}
		
		
		
}
