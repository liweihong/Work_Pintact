package com.pintact.group;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pintact.R;
import com.pintact.data.NotificationDTO;
import com.pintact.data.PageDTO;
import com.pintact.data.ProfileDTO;
import com.pintact.data.UserProfile;
import com.pintact.data.UserProfileAddress;
import com.pintact.data.UserProfileAttribute;
import com.pintact.profile.ProfileViewActivity;
import com.pintact.profile.ProfileViewNewActivity;
import com.pintact.utility.HttpConnection;
import com.pintact.utility.MyActivity;
import com.pintact.utility.SingletonLoginData;
import com.pintact.utility.SingletonNetworkStatus;

public class GroupProfileShareActivity extends MyActivity {

		public static String ARG_PROFILE_SHARE = "SHARE_PROFILES";
		int mArgInt = 0; // -1 : from group pin; 0 : from add contact >0: from notification accept
		int mShareStep = 0; // 0 : send see; 1 : reload notification;
		
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.group_profile_share);
			setupUI(findViewById(R.id.group_profile_view));

			if ( getIntent().getExtras() != null )
				mArgInt = getIntent().getExtras().getInt(ARG_PROFILE_SHARE);
			
			TextView btn = (TextView) findViewById(R.id.gps_pin_tv);
			btn.setFocusableInTouchMode(true);
			btn.requestFocus();
			
			TextView tv = (TextView)findViewById(R.id.actionBar);
			tv.setText(getResources().getString(R.string.ab_group_add));
			showLeftImage(R.drawable.left_arrow);
			
			hideRight();
			
			View.OnClickListener backLn = new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			};
			addLeftLn(backLn);

			TextView share = (TextView) findViewById(R.id.gm_share);
			share.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onShareProfile();
				}
			});

			TextView preview = (TextView) findViewById(R.id.gm_preview);
			preview.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onProfileView();
				}
			});

			for (int i = 0; i < SingletonLoginData.getInstance().getUserProfiles().size() ; i ++ ) {
				addProfiles(i);
			}
			
		}
		
        public void onProfileView() {

        	// set shared profile
        	Long profId[] = new Long[hm.size()];
        	ProfileDTO merged = null;
        	int numProfs = 0;
        	for (int i = 0; i < hm.size(); i ++ ) {
        		UserProfile prof = SingletonLoginData.getInstance().getUserProfiles().get(i).getUserProfile();
        		Integer value = hm.get(prof.getName());
        		if ( value == 1) {
        			profId[i] = prof.getId();
        			numProfs ++;
        			if ( merged == null ) {
        				merged = new ProfileDTO();
        				merged.setUserProfileAttributes(new ArrayList<UserProfileAttribute>());
        				merged.setUserProfileAddresses(new ArrayList<UserProfileAddress>());
        				merged.setUserProfile(SingletonLoginData.getInstance().getUserProfiles().get(i).getUserProfile());
        			}
    				merged.getUserProfileAttributes().addAll(SingletonLoginData.getInstance().getUserProfiles().get(i).getUserProfileAttributes());
    				merged.getUserProfileAddresses().addAll(SingletonLoginData.getInstance().getUserProfiles().get(i).getUserProfileAddresses());
        		} else {
        			profId[i] = 0L;
        		}
        	}
        	
        	
        	if ( numProfs == 0 ) {
        		myDialog("No Profile Selected", "Please select at least one profile to share");
        		return;
        	}

        	SingletonLoginData.getInstance().setMergedProfile(merged);
        	
        	Intent myIntent = new Intent(this, ProfileViewNewActivity.class);
        	myIntent.putExtra(ProfileViewNewActivity.ARG_PROFILE_VIEW, 10000);
            startActivity(myIntent); 
        	
        }
        
        public void onShareProfile() {
        	// set shared profile
        	String notes = ((EditText)findViewById(R.id.gps_msg_cont)).getText().toString();
        	Long profId[] = new Long[hm.size()];
        	int numProfs = 0;
        	for (int i = 0; i < hm.size(); i ++ ) {
        		UserProfile prof = SingletonLoginData.getInstance().getUserProfiles().get(i).getUserProfile();
        		Integer value = hm.get(prof.getName());
        		if ( value == 1) {
        			profId[i] = prof.getId();
        			numProfs ++;
        		} else {
        			profId[i] = 0L;
        		}
        	}
        	
        	
        	if ( numProfs == 0 ) {
        		myDialog("No Profile Selected", "Please select at least one profile to share");
        		return;
        	}
        	
        	Long ids[] = new Long[numProfs];
        	int idIdx = 0;
        	for ( int i = 0; i < profId.length; i ++)
        		if ( profId[i] != 0L)
        			ids[idIdx ++] = profId[i];
        	
        	SingletonLoginData.getInstance().getContactShareRequest().setNote(notes);
        	SingletonLoginData.getInstance().getContactShareRequest().setUserProfileIdsShared(ids);
        	
    		Gson gson = new GsonBuilder().create();
    		String params = gson.toJson(SingletonLoginData.getInstance().getContactShareRequest());
    		
    		if ( mArgInt > 0 ) {
    			SingletonNetworkStatus.getInstance().clean();
    			SingletonNetworkStatus.getInstance().setDoNotDismissDialog(true);
    		}
    		
    		SingletonNetworkStatus.getInstance().setActivity(this);
    		String path = "/api/contacts.json?" + SingletonLoginData.getInstance().getPostParam();
        	if ( mArgInt < 0 ) // from group pin
        	{
        		path = "/api/contacts/addByPin.json?" + SingletonLoginData.getInstance().getPostParam();
        	}
        	
    		new HttpConnection().access(this, path, params, "POST");
        	
        }
        
    	public void onPostNetwork() {

    		if ( SingletonNetworkStatus.getInstance().getCode() != 200 ) {
				myDialog(SingletonNetworkStatus.getInstance().getMsg(), 
						SingletonNetworkStatus.getInstance().getErrMsg());
				return;
			}
			

    		if ( mArgInt <= 0 ) {
        		finish();
        		return;
    		}
    		
    		if ( mArgInt > 0 && mShareStep == 0 ) {
    			String path = "/api/notifications/" +
    					SingletonLoginData.getInstance().getNotifications().getData().get(mArgInt).getNotificationId() +
    					"/seen.json?" + SingletonLoginData.getInstance().getPostParam();
    			new HttpConnection().access(this, path, "", "POST");
    			mShareStep ++;
    			return;
    		}
    		
    		if ( mArgInt > 0 && mShareStep == 1 ) {
    			
    			// this should be the last one
        		SingletonNetworkStatus.getInstance().setDoNotDismissDialog(false);
    			
	    		String path = "/api/sortedNotifications.json?" + SingletonLoginData.getInstance().getPostParam();
	    		new HttpConnection().access(this, path, "", "GET");
	    		mShareStep ++;
	    		return;
    		}
    		
    		if ( mShareStep == 2 ) {
    			// update notification
            	Type collectionType = new TypeToken<PageDTO<NotificationDTO>>(){}.getType();
            	Gson gson = new GsonBuilder().create();
            	PageDTO<NotificationDTO> notifications = gson.fromJson(SingletonNetworkStatus.getInstance().getJson(), collectionType);
            	SingletonLoginData.getInstance().setNotifications(notifications);
    			finish();
    		}
    	}
		
		public void addProfiles(int i) {
			
			String title = SingletonLoginData.getInstance().getUserProfiles().get(i).getUserProfile().getName();
			
			hm.put(title, 0);
			
			LinearLayout container = (LinearLayout)findViewById(R.id.ps_share_lo);
		    final View addView = getLayoutInflater().inflate(R.layout.profile_thumb, null);
    	    container.addView(addView);
		    
		    // change the name of the text
		    TextView tv = (TextView)addView.findViewById(R.id.pt_name);
		    tv.setText(title);

		    ImageView ivPhoto = (ImageView) addView.findViewById(R.id.pt_profile_image);
			Bitmap bm = SingletonLoginData.getInstance().getBitmap(i);
			if ( bm != null ) {
				ivPhoto.setImageBitmap(bm);
				ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
			} else {
				ivPhoto.setImageResource(R.drawable.silhouette);
			}
		    
		    
    	    final RelativeLayout lo = (RelativeLayout)addView.findViewById(R.id.pt_all);
    	    lo.setOnClickListener(new View.OnClickListener(){

    	     @SuppressLint("NewApi")
			@Override
    	     public void onClick(View v) {
    	    	 // ((LinearLayout)addView.getParent()).removeView(addView);
    	    	 TextView tv = (TextView) v.findViewById(R.id.pt_name);
    	    	 String key = tv.getText().toString();
    	    	 Integer value = hm.get(key);
    	    	 lo.setBackgroundDrawable(v.getResources().getDrawable(
    	    			 	value == 0 ? 
    	    			 			R.drawable.border_profile_thumb_sel :
    	    			 			R.drawable.border_profile_thumb_nosel
    	    					 ));
    	    	 if ( value == 0 ) {
    	    		 hm.put(key, 1);
    	    	 } else 
    	    		 hm.put(key, 0);
    	    	 
    	     }});
    	    
		}
		
		public void onDummy(View view) {
		}
		
}
