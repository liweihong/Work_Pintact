package com.pintact.profile;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pintact.R;
import com.pintact.utility.MyActivity;

public class ProfileShareActivity extends MyActivity {

		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.profile_share);
			
			TextView tv = (TextView)findViewById(R.id.actionBar);
			tv.setText(getResources().getString(R.string.ab_new_profile));
			
			View.OnClickListener finClkLn = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onSuccess(v);
				}
			};

			Button btn = (Button)findViewById(R.id.actionBarBtn);
			btn.setOnClickListener(finClkLn);
			
			addProfiles("WORK1");
			addProfiles("LIFE1");
			addProfiles("WORK2");
			addProfiles("LIFE2");
			addProfiles("WORK3");
			addProfiles("LIFE3");
			addProfiles("WORK4");
			addProfiles("LIFE4");
			
		}
		
		public void onSuccess(View view) {
			Intent it = new Intent(this, ProfileNewSuccessActivity.class);
			startActivity(it);
		}
		
		public void addProfiles(String title) {
			
			hm.put(title, 0);
			
			LinearLayout container = (LinearLayout)findViewById(R.id.ps_share_lo);
		    final View addView = getLayoutInflater().inflate(R.layout.profile_thumb, null);
    	    container.addView(addView);
		    
		    // change the name of the text
		    TextView tv = (TextView)addView.findViewById(R.id.pt_name);
		    tv.setText(title);
		    
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

		
		
}
