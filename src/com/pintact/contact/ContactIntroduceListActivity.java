package com.pintact.contact;


import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.pintact.R;
import com.pintact.data.ContactDTO;
import com.pintact.data.ProfileDTO;
import com.pintact.data.UserProfileAddress;
import com.pintact.data.UserProfileAttribute;
import com.pintact.utility.MyActivity;
import com.pintact.utility.SingletonLoginData;


public class ContactIntroduceListActivity extends MyActivity {

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.contact_search_view);
			setupUI(findViewById(R.id.contact_main_view));

			hideRight();

			TextView tv = (TextView)findViewById(R.id.actionBar);
			tv.setText(getResources().getString(R.string.ab_pintroduce));
			
			showLeftImage(R.drawable.left_arrow);
			View.OnClickListener backLn = new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			};
			addLeftLn(backLn);
			
			
			// add adapter
        	// list view operation
			ListView lvContact = (ListView) findViewById(R.id.csv_list);
			
			// Create the Adapter and set
		    ContactMainLVAdapter adapter = new ContactMainLVAdapter(this, SingletonLoginData.getInstance().getCloudContactList());	
			lvContact.setAdapter(adapter);
        	
			// to handle click event on listView item
			lvContact.setOnItemClickListener(new ListView.OnItemClickListener() {
				@Override
	            public void onItemClick(AdapterView<?> arg0, View v,int position, long arg3) {
					onIntroduct(position);
	            }

	        });			
			
        	// end of list view
			
            // add filter for search view
            SearchView sv = (SearchView) findViewById(R.id.csv_search);
			sv.setFocusableInTouchMode(true);
			sv.requestFocus();
			
			sv.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					System.out.println("Search view clicked");
		            SearchView sv = (SearchView) findViewById(R.id.csv_search);
					sv.setFocusableInTouchMode(true);
					sv.setIconified(false);
					sv.requestFocus();
					
				}
			});

			
			final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() { 
			    @Override 
			    public boolean onQueryTextChange(String cs) { 
			        // Do something
			    	((ContactMainLVAdapter)((ListView)findViewById(R.id.csv_list)).getAdapter()).getFilter().filter(cs);
			        return true; 
			    } 

			    @Override 
			    public boolean onQueryTextSubmit(String query) { 
			        // Do something 
			    	System.out.println("Query Text is " + query);
			        return true; 
			    } 
			}; 

			sv.setOnQueryTextListener(queryTextListener);             
			

		}
		
		public void onIntroduct(int pos) {
        	ProfileDTO introduced = new ProfileDTO();
			introduced.setUserProfileAttributes(new ArrayList<UserProfileAttribute>());
			introduced.setUserProfileAddresses(new ArrayList<UserProfileAddress>());
			
			ContactDTO selectedItem = (ContactDTO)((ContactMainLVAdapter)((ListView)findViewById(R.id.csv_list)).getAdapter()).getItem(pos);
			introduced.setUserProfile(selectedItem.getSharedProfiles().get(0).getUserProfile());
			introduced.setUserId(selectedItem.getUserId());

			SingletonLoginData.getInstance().setIntroducedProfile(introduced);
			
			Intent it = new Intent(this, ContactIntroduceActivity.class);
			startActivity(it);
			
		}
		
}
