package com.pintact.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pintact.R;
import com.pintact.utility.MyActivity;

public class ProfileNewActivity extends MyActivity {
	
		public static final String ARG_PROFILE_NEW = "profile_new";
		public int mArgInt;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.profile_create_new);
			
			TextView tv = (TextView)findViewById(R.id.actionBar);
			tv.setText(getResources().getString(R.string.ab_new_profile));
			
			TextView ph = (TextView)findViewById(R.id.pcn_add_phone);
			ph.setFocusableInTouchMode(true);
			ph.requestFocus();
			
			addNoteField();
			
			if ( getIntent().getExtras() != null )
				mArgInt = getIntent().getExtras().getInt(ARG_PROFILE_NEW);
			
			if ( mArgInt > 0 ) {
				showRightText(getResources().getString(R.string.ab_cancel));
			}
			
			View.OnClickListener finClkLn = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					if ( mArgInt > 0 ) {
						finish();
					} else {
						onSuccess(v);
					}
				}
			};

			//Button btn = (Button)findViewById(R.id.actionBarBtn);
			//btn.setOnClickListener(finClkLn);
			hideBoth();
			showRightImage(R.drawable.white_cross);
			addRightImageLn(finClkLn);			
		}
		
		public void onSuccess(View view) {
			Intent it = new Intent(this, ProfileNewSuccessActivity.class);
			startActivity(it);
		}
		
		
		public void addNoteField() {
			
			LinearLayout container = (LinearLayout)findViewById(R.id.pcn_note_lo);
		    final View addView = getLayoutInflater().inflate(R.layout.profile_create_add_phone, null);
		    
		    // change the name of the text
		    EditText tv = (EditText)addView.findViewById(R.id.input_label);
		    tv.setText(getResources().getString(R.string.pcn_label_note));
		    
		    EditText et = (EditText)addView.findViewById(R.id.input_id1);
		    et.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		    
    	    TextView buttonRemove = (TextView)addView.findViewById(R.id.view_id1);
    	    buttonRemove.setVisibility(View.GONE);
    	    
    	    container.addView(addView);
		}
		
		
		public void onDummy(View view) {
		}
		
		public void onPhoneAddNew(View view) {
			
			// add new view
			System.out.println("onPhoneAddNew clicked");
			

			LinearLayout container = (LinearLayout)findViewById(R.id.pcn_phone_lo);
		    final View addView = getLayoutInflater().inflate(R.layout.profile_create_add_phone, null);
    	    TextView buttonRemove = (TextView)addView.findViewById(R.id.view_id1);
    	    buttonRemove.setOnClickListener(new View.OnClickListener(){

    	     @Override
    	     public void onClick(View v) {
    	    	 ((LinearLayout)addView.getParent()).removeView(addView);
    	     }});
    	    
    	    container.addView(addView);
		}

		public void onEmailAddNew(View view) {
			
			// add new view
			System.out.println("onEmailAddNew clicked");
			

			LinearLayout container = (LinearLayout)findViewById(R.id.pcn_email_lo);
		    final View addView = getLayoutInflater().inflate(R.layout.profile_create_add_phone, null);
		    
		    // change the name of the text
		    EditText tv = (EditText)addView.findViewById(R.id.input_label);
		    tv.setText(getResources().getString(R.string.pcn_label_email));
		    
		    EditText et = (EditText)addView.findViewById(R.id.input_id1);
		    et.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		    
    	    TextView buttonRemove = (TextView)addView.findViewById(R.id.view_id1);
    	    buttonRemove.setOnClickListener(new View.OnClickListener(){

    	     @Override
    	     public void onClick(View v) {
    	    	 ((LinearLayout)addView.getParent()).removeView(addView);
    	     }});
    	    
    	    container.addView(addView);
		}

		public void onAddrAddNew(View view) {
			
			LinearLayout container = (LinearLayout)findViewById(R.id.pcn_addr_lo);
		    final View addView = getLayoutInflater().inflate(R.layout.profile_create_add_address, null);
		    
		    // change the name of the text
		    EditText tv = (EditText)addView.findViewById(R.id.input_label);
		    tv.setText(getResources().getString(R.string.pcn_label_addr));
		    
		    EditText et = (EditText)addView.findViewById(R.id.input_id1);
		    et.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		    
    	    TextView buttonRemove = (TextView)addView.findViewById(R.id.view_id1);
    	    buttonRemove.setOnClickListener(new View.OnClickListener(){

    	     @Override
    	     public void onClick(View v) {
    	    	 ((LinearLayout)addView.getParent()).removeView(addView);
    	     }});
    	    
    	    container.addView(addView);
		}
		
		public void onUrlAddNew(View view) {
			
			LinearLayout container = (LinearLayout)findViewById(R.id.pcn_url_lo);
		    final View addView = getLayoutInflater().inflate(R.layout.profile_create_add_phone, null);
		    
		    // change the name of the text
		    EditText tv = (EditText)addView.findViewById(R.id.input_label);
		    tv.setText(getResources().getString(R.string.pcn_label_url));
		    
		    EditText et = (EditText)addView.findViewById(R.id.input_id1);
		    et.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		    
    	    TextView buttonRemove = (TextView)addView.findViewById(R.id.view_id1);
    	    buttonRemove.setOnClickListener(new View.OnClickListener(){

    	     @Override
    	     public void onClick(View v) {
    	    	 ((LinearLayout)addView.getParent()).removeView(addView);
    	     }});
    	    
    	    container.addView(addView);
		}
		
		
}
