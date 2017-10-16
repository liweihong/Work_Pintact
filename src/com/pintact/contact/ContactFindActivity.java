package com.pintact.contact;

import java.lang.reflect.Type;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pintact.R;
import com.pintact.data.ContactShareRequest;
import com.pintact.data.PageDTO;
import com.pintact.data.SearchDTO;
import com.pintact.group.GroupProfileShareActivity;
import com.pintact.utility.HttpConnection;
import com.pintact.utility.MyActivity;
import com.pintact.utility.SingletonLoginData;
import com.pintact.utility.SingletonNetworkStatus;

public class ContactFindActivity extends MyActivity {

		ListView lvContact;
		Cursor people;
		PageDTO<SearchDTO> suggests;
		ContactInviteLVAdapter adapter;
		boolean isSelectAll = false;
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.contact_search_view);
			setupUI(findViewById(R.id.contact_main_view));
			
			TextView tv = (TextView)findViewById(R.id.actionBar);
			tv.setText(getResources().getString(R.string.ab_invite_add));
			
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

			// search button
			SearchView sv = (SearchView) findViewById(R.id.csv_search);
			//EditText sv = (EditText) findViewById(R.id.csv_search);
			//sv.setQueryHint(getResources().getString(R.string.ss_contact));
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
			    public boolean onQueryTextChange(String newText) { 
			        // Do something
			    	onKeyInput(newText);
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
		
		public void onKeyInput(String str) 
		{
			SingletonNetworkStatus.getInstance().setActivity(this);
			
    		String path = "/api/profiles/suggest.json?" + SingletonLoginData.getInstance().getPostParam() +
    				"&query=" + str;
    		SingletonNetworkStatus.getInstance().setActivity(this);
    		SingletonNetworkStatus.getInstance().setDoNotShowStatus(true);
    		new HttpConnection().access(this, path, "", "GET");
			
		}
		
		public void onPostNetwork() {
			
    		SingletonNetworkStatus.getInstance().setDoNotShowStatus(false);
			if ( SingletonNetworkStatus.getInstance().getCode() != 200 ) {
				myDialog(SingletonNetworkStatus.getInstance().getMsg(), 
						SingletonNetworkStatus.getInstance().getErrMsg());
				return;
			}
			
			Type collectionType = new TypeToken<PageDTO<SearchDTO>>(){}.getType();			
        	Gson gson = new GsonBuilder().create();
        	suggests = gson.fromJson(SingletonNetworkStatus.getInstance().getJson(), collectionType);

			// search result
        	// list view operation
			ListView lvContact = (ListView) findViewById(R.id.csv_list);
		    ContactFindLVAdapter adapter = new ContactFindLVAdapter(this, suggests);	
			lvContact.setAdapter(adapter);
        	
			// to handle click event on listView item
			lvContact.setOnItemClickListener(new ListView.OnItemClickListener() {
				@Override
	            public void onItemClick(AdapterView<?> arg0, View v,int position, long arg3) {
					searchPerson(position);
	            }

	        });			
			
		}
		
		public void searchPerson(int p) 
		{
			ContactShareRequest req = new ContactShareRequest();
			Intent it = new Intent (this, GroupProfileShareActivity.class);
			
			// if this is group pin, do sth else.
			if ( suggests.getData().get(p).getGroupPin() == null ) {
				req.setDestinationUserId(Long.parseLong(suggests.getData().get(p).getUserId()));
				req.setSourceUserId(Long.parseLong(SingletonLoginData.getInstance().getUserData().id));
		    	it.putExtra(GroupProfileShareActivity.ARG_PROFILE_SHARE, 0);
			} else {
				req.setDestinationPin(suggests.getData().get(p).getGroupPin());
		    	it.putExtra(GroupProfileShareActivity.ARG_PROFILE_SHARE, -1);
			}
			
			SingletonLoginData.getInstance().setContactShareRequest(req);
			startActivity(it);
		}
		
		
		
}
