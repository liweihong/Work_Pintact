package com.pintact.utility;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pintact.R;
import com.pintact.data.AttributeType;
import com.pintact.data.ContactDTO;
import com.pintact.data.ProfileDTO;
import com.pintact.data.UserDTO;
import com.pintact.data.UserProfile;
import com.pintact.data.UserProfileAddress;
import com.pintact.data.UserProfileAttribute;

public class MyActivity extends Activity {
	
		TextView  barLeft, barRight, title;
		ImageView imgLeft, imgRight;
		Button   btnRight;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			ActionBar actionBar = getActionBar();
			actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.register_actionbar));
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
			actionBar.setCustomView(R.layout.action_bar_all);
			
			barLeft  = (TextView)findViewById(R.id.actionBarLeft);
			title    = (TextView)findViewById(R.id.actionBar);
			imgLeft  = (ImageView)findViewById(R.id.actionBarMenu);
			imgRight  = (ImageView)findViewById(R.id.actionBarRightImage);
			barRight = (TextView)findViewById(R.id.actionBarRight);
			btnRight = (Button)findViewById(R.id.actionBarBtn);

			barLeft.setVisibility(View.GONE);
			barRight.setVisibility(View.GONE);
		}
		
		public void hideLeft() {
			//barLeft.setVisibility(View.INVISIBLE);
			imgLeft.setVisibility(View.INVISIBLE);
		}

		public void showTitle(String str) {
			title.setText(str);
		}
		
		public void showLeft() {
			//barLeft.setVisibility(View.VISIBLE);
			imgLeft.setVisibility(View.VISIBLE);
		}

		public void hideRight() {
			//barRight.setVisibility(View.INVISIBLE);
			btnRight.setVisibility(View.INVISIBLE);
			imgRight.setVisibility(View.INVISIBLE);
		}
		
		public void showRight() {
			//barRight.setVisibility(View.VISIBLE);
			btnRight.setVisibility(View.VISIBLE);
		}
		
		public void showRightText(String txt) {
			//barRight.setVisibility(View.VISIBLE);
			btnRight.setText(txt);
			btnRight.setVisibility(View.VISIBLE);
		}
		
		public void showLeftImage(int resId) {
			imgLeft.setImageResource(resId);
			imgLeft.setVisibility(View.VISIBLE);
			//barLeft.setVisibility(View.VISIBLE);
		}

		public void showRightImage(int resId) {
			if ( resId != 0 )
				imgRight.setImageResource(resId);
			imgRight.setVisibility(View.VISIBLE);
			//barRight.setVisibility(View.VISIBLE);
		}

		public String getRightText() {
			return btnRight.getText().toString();
		}
		

		public void addRightLn(View.OnClickListener ln) {
			btnRight.setOnClickListener(ln);
		}
		
		public void addRightImageLn(View.OnClickListener ln) {
			imgRight.setOnClickListener(ln);
		}
		
		public void addLeftLn(View.OnClickListener ln) {
			imgLeft.setOnClickListener(ln);
		}
		
		public void hideBoth() {
			hideLeft();
			hideRight();
		}
		
		public void onPostNetwork() {
			
		}
		

		public void myDialog(String title, String info) {
			AlertDialog.Builder builder = new AlertDialog.Builder( new ContextThemeWrapper(this, R.style.AlertDialogCustom));
			//AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setCancelable(true);
	        builder.setTitle(title);
	        builder.setMessage(info);
	        builder.setInverseBackgroundForced(true);
	        builder.setPositiveButton("OK",
	                new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialog,
	                            int which) {
	                        dialog.dismiss();
	                    }
	                });
	        AlertDialog alert = builder.create();
	        alert.show();
	        //TextView ttt = (TextView)alert.findViewById(android.R.id.title);
	        //ttt.setGravity(Gravity.CENTER);
		}		
		
		
		public void updatePreferencesSort(int value) {
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			//SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putInt(getString(R.string.set_sort_setting), value);
			editor.commit();
			SingletonLoginData.getInstance().getUserSettings().sort = value;
		
		}
		
		public void updatePreferencesLocal(int value) {
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			//SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putInt(getString(R.string.set_show_native_contacts), value);
			editor.commit();
			SingletonLoginData.getInstance().getUserSettings().local = value;

			System.out.println("local:" + value);
			
			// clear local contact info
			if ( value == 0 ) {
				SingletonLoginData.getInstance().setLocalContactList(new ArrayList<ContactDTO> ());
			} else {
				loadLocalContacts();
			}
		
		}
		
		public void updatePreferencesPush(int value) {
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			//SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putInt(getString(R.string.set_push_notifications), value);
			editor.commit();
			SingletonLoginData.getInstance().getUserSettings().push = value;
		
		}
		
		public void loadPreferences() {
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			//SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
			int sort = sharedPref.getInt(getString(R.string.set_sort_setting), 0);
			int local = sharedPref.getInt(getString(R.string.set_show_native_contacts), 0);
			int push = sharedPref.getInt(getString(R.string.set_push_notifications), 0);
			SingletonLoginData.getInstance().getUserSettings().sort = sort;
			SingletonLoginData.getInstance().getUserSettings().local = local;
			SingletonLoginData.getInstance().getUserSettings().push = push;
			
			System.out.println("sort, local, push:" + sort + " " + local + " " + push);
			if ( local == 1 ) {
				loadLocalContacts();
			}
			
		}
		
	    public void loadLocalContacts() {
	    	
	    	Uri uri1 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
			String[] projection1    = new String[] {
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
					ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
					ContactsContract.CommonDataKinds.Phone.NUMBER,
					ContactsContract.CommonDataKinds.Phone.LABEL};
		    String sortOrder =  ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
			Cursor phones = this.getContentResolver().query(uri1, projection1, null, null, sortOrder);
			int indexID = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
			int indexLB = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL);
			int indexDN = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
			int indexNM = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
			String cid = "";
			ArrayList<ContactDTO> list = new ArrayList<ContactDTO> ();
			SingletonLoginData.getInstance().setLocalContactList(list);
			ProfileDTO profile = new ProfileDTO();
			UserProfileAttribute attr = new UserProfileAttribute();
			while ( phones.moveToNext()) {
				String pid = phones.getString(indexID);
				String label = phones.getString(indexLB);
				String dName = phones.getString(indexDN);
				String number = phones.getString(indexNM);
				
				if ( ! cid.equals(pid) ) {
					cid = pid;
					
					ContactDTO contact = new ContactDTO();
					UserDTO userInfo = new UserDTO();
					profile = new ProfileDTO();
					UserProfile user = new UserProfile();
					ArrayList<UserProfileAttribute> lAttr = new ArrayList<UserProfileAttribute>();
					profile.setUserProfile(user);
					profile.setUserProfileAttributes(lAttr);
					profile.setUserProfileAddresses(new ArrayList<UserProfileAddress>());
					ArrayList<ProfileDTO> pList = new ArrayList<ProfileDTO> ();
					contact.isLocalContact = true;
					pList.add(profile);
					contact.setSharedProfiles(pList);
					contact.setContactUser(userInfo);
					list.add(contact);
					
					// break dName to first name and last name  ONLY at first time
					// construct the attribute list
					int pos = dName.lastIndexOf(' ');
					if ( pos == -1) {
						user.setFirstName(dName);
						userInfo.setFirstName(dName);
					} else {
						String fn = dName.substring(0, pos);
						String ln = "";
						// in case the last name was followed by an extra ' ';
						if ( pos != dName.length() - 1)
							ln = dName.substring(pos+1, dName.length());
						user.setFirstName(fn);
						user.setLastName(ln);
						userInfo.setFirstName(fn);
						userInfo.setLastName(ln);
					}
					
					//System.out.println("New One: first name: " + user.getFirstName() + " ln:" + user.getLastName());
				}
				
				attr = new UserProfileAttribute();
				attr.setType(AttributeType.PHONE_NUMBER);
				if ( label == null  || label.length() < 1 ) {
					label = "Phone";
				}
				attr.setLabel(label);
				attr.setValue(number);
				profile.getUserProfileAttributes().add(attr);

				System.out.println(dName +" == " + label + ":" + number + " id:" + pid);
				
			}
			
			System.out.println("Total local contacts: " + list.size());
	    
	    }
	    
	    public void hideSoftKeyboard(Activity activity) {
	        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	    }
	    
	    public void setupUI(View view) {

	        //Set up touch listener for non-text box views to hide keyboard.
	        if(!(view instanceof EditText)) {

	            view.setOnTouchListener(new View.OnTouchListener() {
	            	
	            	@Override
	                public boolean onTouch(View v, MotionEvent event) {
	                    hideSoftKeyboard((Activity)v.getContext());
	                    return false;
	                }

	            });
	        }

	        //If a layout container, iterate over children and seed recursion.
	        if (view instanceof ViewGroup) {

	            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

	                View innerView = ((ViewGroup) view).getChildAt(i);

	                setupUI(innerView);
	            }
	        }
	    }	    
	    
		
}
