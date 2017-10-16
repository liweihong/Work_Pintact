package com.pintact.label;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pintact.MainActivity;
import com.pintact.R;
import com.pintact.contact.ContactMainLVAdapter;
import com.pintact.data.ContactDTO;
import com.pintact.data.ProfileDTO;
import com.pintact.data.UserProfile;
import com.pintact.data.UserProfileAddress;
import com.pintact.data.UserProfileAttribute;
import com.pintact.profile.ProfileViewNewActivity;
import com.pintact.utility.HttpConnection;
import com.pintact.utility.MyActivity;
import com.pintact.utility.SingletonLoginData;
import com.pintact.utility.SingletonNetworkStatus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

public class LabelContactsActivity extends MyActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.label_list_view);
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

    	// list view operation
		ListView lvContact = (ListView) findViewById(R.id.llv_list);
		
		// Create the Adapter and set
	    ContactMainLVAdapter adapter = new ContactMainLVAdapter(this, SingletonLoginData.getInstance().getLabelContactList(), true);	
		lvContact.setAdapter(adapter);
		
		// set callback
		// to handle click event on listView item
		lvContact.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
            public void onItemClick(AdapterView<?> arg0, View v,int position, long arg3) {
	        	ProfileDTO merged = new ProfileDTO();
				merged.setUserProfileAttributes(new ArrayList<UserProfileAttribute>());
				merged.setUserProfileAddresses(new ArrayList<UserProfileAddress>());
				
				ContactDTO selectedItem = (ContactDTO)((ContactMainLVAdapter)((ListView)findViewById(R.id.llv_list)).getAdapter()).getItem(position);
				UserProfile profile;
				if ( selectedItem.getSharedProfiles().size() > 0 )
					profile = selectedItem.getSharedProfiles().get(0).getUserProfile();
				else {
					profile = new UserProfile();
					profile.setFirstName(selectedItem.getContactUser().getFirstName());
					profile.setLastName(selectedItem.getContactUser().getLastName());
				}
				merged.setUserProfile(profile);
	        	merged.setUserId(selectedItem.getContactUser().getId());
	        	System.out.println("id: " + selectedItem.getUserId());
	        	
	        	
				int num = selectedItem.getSharedProfiles().size();
				for ( int k = 0; k < num; k ++) {
					List<UserProfileAttribute> attrs = selectedItem.getSharedProfiles().get(k).getUserProfileAttributes();
					List<UserProfileAddress> addrs = selectedItem.getSharedProfiles().get(k).getUserProfileAddresses();
					for ( int item = 0; item < attrs.size(); item++) {
						if ( !merged.getUserProfileAttributes().contains(attrs.get(item)))
								merged.getUserProfileAttributes().add(attrs.get(item));
					}
					for ( int item = 0; item < addrs.size(); item++) {
						if ( !merged.getUserProfileAddresses().contains(addrs.get(item)))
								merged.getUserProfileAddresses().add(addrs.get(item));
					}
				}
				
	        	SingletonLoginData.getInstance().setMergedProfile(merged);
				System.out.println("getting labels: " + (selectedItem.getLabels() == null ? "null" : "not null"));
	        	SingletonLoginData.getInstance().setContactLabels((ArrayList<String>)selectedItem.getLabels());
				
				onProfileView(100000 + position);

            }

        });			
		
    	// end of list view
		
		
	}

    public void onProfileView(int i) {
    	Intent myIntent = new Intent(this, ProfileViewNewActivity.class);
    	myIntent.putExtra(ProfileViewNewActivity.ARG_PROFILE_VIEW, i);
        startActivity(myIntent);
    }
    
	
	public void onDummy(View view) {
	}


}
