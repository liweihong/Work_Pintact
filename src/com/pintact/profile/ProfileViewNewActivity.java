package com.pintact.profile;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContacts.Data;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.MediaColumns;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pintact.R;
import com.pintact.contact.ContactIntroduceListActivity;
import com.pintact.data.AttributeType;
import com.pintact.data.ContactShareRequest;
import com.pintact.data.ProfileDTO;
import com.pintact.data.UserProfile;
import com.pintact.data.UserProfileAddress;
import com.pintact.data.UserProfileAttribute;
import com.pintact.label.LabelMainActivity;
import com.pintact.utility.HttpConnection;
import com.pintact.utility.HttpConnectionForImage;
import com.pintact.utility.MyActivity;
import com.pintact.utility.SingletonLoginData;
import com.pintact.utility.SingletonNetworkStatus;

public class ProfileViewNewActivity extends MyActivity {

	public static final String ARG_PROFILE_VIEW = "profile_view";
	public static final String MYTOKEN = "  ~~||~~  ";
	
	public int mArgInt;  // -2: signup; -1: profile new; 0-1000:profile view; >10K: share view; >100K: contact view
	public ProfileDTO mProfile;
	ArrayList<String>  mAllLabels = new ArrayList<String>();
	boolean mIsContactView = false;
	boolean mIsSharedView = false;
	boolean isUploadingImage = false;
	boolean isDeleteContact = false;
	boolean isDeleteProfile = false;
	boolean isUpdatedPNotes = false;
	boolean isQuerySharedProfile = false;
	boolean isQueryContactLabels = false;
	boolean isUpdateSharedProfile = false;
	String  mImagePath;
	
	Typeface myFont;

	Map<AttributeType, ArrayList<View>> mViewMap = new HashMap<AttributeType, ArrayList<View>> ();
	ArrayList<View> mViewAddress = new ArrayList<View> ();
	HashMap<String, Integer> hm = new HashMap<String, Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_create_new);
		setupUI(findViewById(R.id.profile_edit_view));
		
		// try customized font
		myFont = Typeface.createFromAsset(getAssets(), "Aller_Bd.ttf");
		if ( myFont != null ) {
			myDialog("Font Error!", "Could not find the font");
		} else {
			myDialog("Font Ok!", "Found the font");
		}

		if ( getIntent().getExtras() != null )
			mArgInt = getIntent().getExtras().getInt(ARG_PROFILE_VIEW);

		if (mArgInt > -1 )
			fromProfileView();
		else if ( mArgInt == -1 )
			fromProfileNew();
		else if ( mArgInt == -2 )
			fromLoginNew();

	}

	public void initForNew() {
		TextView tv = (TextView)findViewById(R.id.actionBar);
		tv.setText(getResources().getString(R.string.ab_new_profile));

		mProfile = new ProfileDTO();
		mProfile.setUserProfile(new UserProfile());
		mProfile.setUserProfileAttributes(new ArrayList<UserProfileAttribute>());
		mProfile.setUserProfileAddresses(new ArrayList<UserProfileAddress>());
		onEdit();
	}

	public void fromProfileNew() {
		showLeftImage(R.drawable.left_arrow);
		View.OnClickListener backLn = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goBack();
			}
		};
		addLeftLn(backLn);
		initForNew();

	}

	public void fromLoginNew() {
		initForNew();
		hideLeft();
	}

	public void fromProfileView() 
	{

		if ( mArgInt >= 10000 ) 
		{
			mProfile = SingletonLoginData.getInstance().getMergedProfile();
			if ( mArgInt >= 100000 )
			{
				mArgInt -= 100000;
				if ( mProfile.getUserId() != null )
					mIsContactView = true;
			} else {
				mArgInt -= 10000;
				mIsSharedView = true;
			}
		}else 
			mProfile = SingletonLoginData.getInstance().getUserProfiles().get(mArgInt);


		TextView tv = (TextView)findViewById(R.id.actionBar);
		tv.setFocusableInTouchMode(true);
		tv.requestFocus();
		
		if ( mIsContactView ) {
			tv.setText(mProfile.getUserProfile().getFirstName() + " " + mProfile.getUserProfile().getLastName());
		} else {
			tv.setText(mProfile.getUserProfile().getName());
		}


		onView();

		View.OnClickListener finClkLn = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onEdit();
			}
		};
		Button btn = (Button)findViewById(R.id.actionBarBtn);
		btn.setOnClickListener(finClkLn);

		// show back on LEFT and edit on RIGHT
		showRightText(getResources().getString(R.string.ab_edit));
		showLeftImage(R.drawable.left_arrow);
		View.OnClickListener backLn = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goBack();
			}
		};
		addLeftLn(backLn);

		if ( mIsSharedView ) {
			hideRight();
			//showRightText(getResources().getString(R.string.ab_cancel));
			//addRightLn(backLn);
			showRightImage(R.drawable.white_cross);
			addRightImageLn(backLn);			
		}

		View.OnClickListener exportLn = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onIntroduce();
			}
		};

		if ( mIsContactView ) {
			querySharedProfiles();
			// queryContactLabels();
			// we first send out request, and then update the shared profiles.
			hideRight();
			showRightImage(0);
			addRightImageLn(exportLn);
			showContactView();
		}
	}

	public void queryContactLabels() {
		// send request to get shared profiles
		isQueryContactLabels = true;
		String path = "/api/contacts/profiles/" + mProfile.getUserId() + "/shared.json?" + SingletonLoginData.getInstance().getPostParam();
		SingletonNetworkStatus.getInstance().setActivity(this);
		SingletonNetworkStatus.getInstance().setDoNotShowStatus(true);
		new HttpConnection().access(this, path, "", "GET");

		
	}
	
	public void querySharedProfiles() {
		// send request to get shared profiles
		isQuerySharedProfile = true;
		String path = "/api/contacts/profiles/" + mProfile.getUserId() + "/shared.json?" + SingletonLoginData.getInstance().getPostParam();
		SingletonNetworkStatus.getInstance().setActivity(this);
		SingletonNetworkStatus.getInstance().setDoNotShowStatus(true);
		new HttpConnection().access(this, path, "", "GET");

	}

	public void updateSharedProfileIcons() {
		// highlighting those profiles shared with this user
		Type collectionType = new TypeToken<Collection<ProfileDTO>>(){}.getType();
		Gson gson = new GsonBuilder().create();
		Collection<ProfileDTO> profiles00 = gson.fromJson(SingletonNetworkStatus.getInstance().getJson(), collectionType);
		String listProfiles = "";
		ArrayList<ProfileDTO> profiles = new ArrayList<ProfileDTO>(profiles00);
		for (int i = 0; i < profiles.size(); i ++ ) {
			listProfiles += profiles.get(i).getUserProfile().getName() + ",";
		}
		if ( listProfiles.length() > 1 )
			listProfiles = listProfiles.substring(0, listProfiles.length() - 1);

		LinearLayout container = (LinearLayout)findViewById(R.id.share_layout);
		int totalProfiles = SingletonLoginData.getInstance().getUserProfiles().size();
		for ( int i = 0; i < totalProfiles; i ++ ) {
			String title = SingletonLoginData.getInstance().getUserProfiles().get(i).getUserProfile().getName();
			if ( listProfiles.contains(title) ) {
				hm.put(title, 1);
				View v = container.getChildAt(i);
				RelativeLayout lo = (RelativeLayout)v.findViewById(R.id.pt_all);
				// for API 15 - Jeff's crab
				lo.setBackgroundDrawable(v.getResources().getDrawable(R.drawable.border_profile_thumb_sel_half));
			}
		}
		TextView tvInfo = (TextView) findViewById(R.id.tv_share);
		tvInfo.setText("PROFILES SHARED: " + listProfiles);

	}

	public void sendUpdatedSharedProfile() {

		// set shared profile
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

		ContactShareRequest req = new ContactShareRequest();
		req.setDestinationUserId(mProfile.getUserId());
		req.setSourceUserId(Long.parseLong(SingletonLoginData.getInstance().getUserData().id));
		req.setUserProfileIdsShared(ids);

		isUpdateSharedProfile = true;
		
		Gson gson = new GsonBuilder().create();
		String params = gson.toJson(req);
		SingletonNetworkStatus.getInstance().setActivity(this);
		String path = "/api/contacts/updateSharedProfiles.json?" + SingletonLoginData.getInstance().getPostParam();
		new HttpConnection().access(this, path, params, "POST");


	}


	public void showContactView() {

		// show all the elements
		RelativeLayout lo = (RelativeLayout) findViewById(R.id.label_rlo);
		TextView tv = (TextView) findViewById(R.id.sep_below_label_rlo);
		ImageView ivLabel = (ImageView) findViewById(R.id.pcn_label_icon);
		TextView tvLabel = (TextView) findViewById(R.id.pcn_add_label);
		lo.setVisibility(View.VISIBLE);
		tv.setVisibility(View.VISIBLE);
		ivLabel.setVisibility(View.GONE);
		tvLabel.setVisibility(View.GONE);
		lo = (RelativeLayout) findViewById(R.id.pnotes_rlo);
		tv = (TextView) findViewById(R.id.sep_below_pnotes_rlo);
		lo.setVisibility(View.VISIBLE);
		tv.setVisibility(View.VISIBLE);
		lo = (RelativeLayout) findViewById(R.id.share_rlo);
		tv = (TextView) findViewById(R.id.sep_below_share_rlo);
		lo.setVisibility(View.VISIBLE);
		tv.setVisibility(View.VISIBLE);
		lo = (RelativeLayout) findViewById(R.id.button_rlo);
		tv = (TextView) findViewById(R.id.sep_below_button_rlo);
		lo.setVisibility(View.VISIBLE);
		tv.setVisibility(View.VISIBLE);

		// add delete view
		TextView delete = (TextView) findViewById(R.id.tv_button_delete);
		delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isDeleteContact = true;
				String msg = "You are about to delete this contact. This operation cannot be undone. Do you still wish to continue?";
				alertDialog("ARE YOU SURE", msg);
			}
		});

		// add insert view
		TextView insert = (TextView) findViewById(R.id.tv_button_add);
		insert.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				insertContact();
			}
		});

		// add share view
		TextView share = (TextView) findViewById(R.id.tv_share_button);
		share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendUpdatedSharedProfile();
			}
		});

		// add pnotes edit button
		EditText et = (EditText) findViewById(R.id.et_pnotes);
		String str = SingletonLoginData.getInstance().getContactList().get(mArgInt).getContactNote();
		String strNote = ( str != null && str.length() > 1 ) ? str.replace(MYTOKEN, "\n") : str;
		System.out.println("Original String:" + str + "||||||| updated string:" + strNote);
		et.setText(strNote);
		et.setFocusable(false);
		et.setClickable(false);
		et.setFocusableInTouchMode(false);
		
		TextView pnoteTV = (TextView) findViewById(R.id.tv_pnotes_edit);
		pnoteTV.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String str = ((TextView)v).getText().toString();
				EditText et = (EditText) findViewById(R.id.et_pnotes);
				if (str.equals("EDIT")) {
					((TextView)v).setText("SAVE");
					et.setFocusable(true);
					et.setClickable(true);
					et.setFocusableInTouchMode(true);
					et.requestFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
				} else {
					((TextView)v).setText("EDIT");
					et.setFocusable(false);
					et.setClickable(false);
					et.setFocusableInTouchMode(false);
					sendUpdatedPNotes();
				}
			}
		});

		// add label edit button
		
		TextView labelTV = (TextView) findViewById(R.id.tv_label_edit);
		labelTV.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent (v.getContext(), LabelMainActivity.class);
				startActivity(it);
				/*
				ImageView ivLabel = (ImageView) findViewById(R.id.pcn_label_icon);
				TextView tvLabel = (TextView) findViewById(R.id.pcn_add_label);
				String str = ((TextView)v).getText().toString();
				LinearLayout container = (LinearLayout)findViewById(R.id.pcn_label_lo);
				boolean showDelete = false;
				if (str.equals("EDIT")) {
					((TextView)v).setText("SAVE");
					ivLabel.setVisibility(View.VISIBLE);
					tvLabel.setVisibility(View.VISIBLE);
					showDelete = true;
				} else {
					((TextView)v).setText("EDIT");
					ivLabel.setVisibility(View.GONE);
					tvLabel.setVisibility(View.GONE);				
					sendUpdatedLabels();
				}
				
				for ( int i = 0; i < container.getChildCount(); i ++ ) {
					View addView = container.getChildAt(i);
					TextView deleteTV = (TextView) addView.findViewById(R.id.view_delete);
					deleteTV.setVisibility(showDelete?View.VISIBLE:View.INVISIBLE);
				}
				*/
			}
		});
		
		View.OnClickListener lnLabel = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent (v.getContext(), LabelMainActivity.class);
				startActivity(it);
			}
		};
		ivLabel.setOnClickListener(lnLabel);
		tvLabel.setOnClickListener(lnLabel);
		
		updateLabel();
	}
	
	public void insertContact() {
		
		// check whether this contact is existed in the local address book
		// get all the phone number (string)
		List<UserProfileAttribute> attr = mProfile.getUserProfileAttributes();
		String conds = "";
		String num = ContactsContract.CommonDataKinds.Phone.NUMBER;
		for ( int i = 0; i < attr.size(); i ++ ) {
			if ( attr.get(i).getType() == AttributeType.PHONE_NUMBER ) {
				String pad = conds.length() > 1 ? " OR " : ""; 
				conds += pad + num + " = \"" + attr.get(i).getValue() + "\""; 
			}
		}
		
    	Uri uri1 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection1    = new String[] {ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
				ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
		Cursor phones = this.getContentResolver().query(uri1, projection1, conds, null, null);
		int indexID = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
		int indexRI = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID);
		int indexDN = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
		boolean found = false;
		String raw_contact_id = "", contact_id;
		if ( phones.getCount() > 0 ) {
			found = true;
			phones.moveToNext();
			contact_id = phones.getString(indexID);
			raw_contact_id = phones.getString(indexRI);
			String name = phones.getString(indexDN);
			System.out.println("Found " + name + " in the local contact, id: " + contact_id );
		} else {
			System.out.println("Not found - to be inserted.");
		}
		
		// if found, check whether we need to update
		// if not found, add all the records.
		if ( ! found ) {
			insertNewContact();
			myDialog("Contact Added", "The contact has been added into your address book.");
		} else	{
			updateContact(Integer.parseInt(raw_contact_id));
			myDialog("Contact Updated", "The contact in your address book has been updated.");
		}

	}

	public ArrayList<String> queryAllPhones(int id) {
    	Uri uri1 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection1    = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER };
		String conds = ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + " = " + id;
		Cursor phones = this.getContentResolver().query(uri1, projection1, conds, null, null);
		int indexNM = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
		ArrayList<String> result = new ArrayList<String> ();
		while ( phones != null && phones.moveToNext()) {
			result.add(phones.getString(indexNM));
			System.out.println("Found Phone:" + phones.getString(indexNM));
		}
		return result;
	}
	
	public ArrayList<String> queryAllEmails(int id) {
    	Uri uri1 = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
		String[] projection1    = new String[] { ContactsContract.CommonDataKinds.Email.ADDRESS };
		String conds = ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID + " = " + id;
		Cursor phones = this.getContentResolver().query(uri1, projection1, conds, null, null);
		int indexNM = phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
		ArrayList<String> result = new ArrayList<String> ();
		while ( phones != null && phones.moveToNext()) {
			result.add(phones.getString(indexNM));
			System.out.println("Found Email:" + phones.getString(indexNM));
		}
		return result;
	}
	
	public ArrayList<String> queryAllUrls(int id) {
    	Uri uri1 = ContactsContract.Data.CONTENT_URI;
		String[] projection1    = new String[] { ContactsContract.CommonDataKinds.Im.DATA };
		String conds = ContactsContract.Data.RAW_CONTACT_ID + " = " + id;
		Cursor phones = this.getContentResolver().query(uri1, projection1, conds, null, null);
		int indexNM = phones.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA);
		ArrayList<String> result = new ArrayList<String> ();
		while ( phones != null && phones.moveToNext()) {
			result.add(phones.getString(indexNM));
			System.out.println("Found URL:" + phones.getString(indexNM));
		}
		return result;
	}
	
	public ArrayList<String> queryAllNotes(int id) {
    	Uri uri1 = ContactsContract.Data.CONTENT_URI;
		String[] projection1    = new String[] { ContactsContract.CommonDataKinds.Note.NOTE };
		String conds = ContactsContract.Data.RAW_CONTACT_ID + " = " + id;
		Cursor phones = this.getContentResolver().query(uri1, projection1, conds, null, null);
		int indexNM = phones.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE);
		ArrayList<String> result = new ArrayList<String> ();
		while ( phones != null && phones.moveToNext()) {
			result.add(phones.getString(indexNM));
			System.out.println("Found Note:" + phones.getString(indexNM));
		}
		return result;
	}
	
	public ArrayList<String> queryAllAddrs(int id) {
    	Uri uri1 = ContactsContract.Data.CONTENT_URI;
		String[] projection1    = new String[] { ContactsContract.CommonDataKinds.StructuredPostal.STREET };
		String conds = ContactsContract.CommonDataKinds.StructuredPostal.RAW_CONTACT_ID + " = " + id;
		Cursor phones = this.getContentResolver().query(uri1, projection1, conds, null, null);
		int indexNM = phones.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET);
		ArrayList<String> result = new ArrayList<String> ();
		while ( phones.moveToNext()) {
			result.add(phones.getString(indexNM));
			System.out.println("Found Address:" + phones.getString(indexNM));
		}
		return result;
	}
	
	public void updateContact(int contactId) {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		List<UserProfileAttribute> attr = mProfile.getUserProfileAttributes();
		List<UserProfileAddress> addr = mProfile.getUserProfileAddresses();
		ArrayList<String> allPhones = queryAllPhones( contactId );
		ArrayList<String> allEmails = queryAllEmails( contactId );
		ArrayList<String> allNotes  = queryAllNotes( contactId );
		ArrayList<String> allUrls   = queryAllUrls( contactId );
		ArrayList<String> allAddrs  = queryAllAddrs( contactId );
		for ( int i = 0; i < attr.size(); i ++ ) 
		{
			String value = attr.get(i).getValue();
			String label = attr.get(i).getLabel();
			if ( attr.get(i).getType() == AttributeType.PHONE_NUMBER  && !allPhones.contains(value)) {
				//Phone Number
				ops.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValue(Data.RAW_CONTACT_ID, contactId)
						.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
						.withValue(Phone.NUMBER, value)
						.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
						.withValue(Phone.LABEL, label)
						.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
						.withValue(Phone.TYPE, Phone.TYPE_CUSTOM).build());				
				System.out.println("Adding Phone - label: " + label + " value: " + value);
			}
			
			else if ( attr.get(i).getType() == AttributeType.EMAIL && !allEmails.contains(value)) {
				//Email details
				ops.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
						.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
						.withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, value)
						.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)	
						.withValue(ContactsContract.CommonDataKinds.Email.LABEL, label)
						.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
						.withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM).build());
				System.out.println("Adding Email - label: " + label + " value: " + value);
			}
			
			else if ( attr.get(i).getType() == AttributeType.SERVICE_ID && !allUrls.contains(value)) {
				//IM details
				ops.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
						.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
						.withValue(ContactsContract.CommonDataKinds.Im.DATA, value)
						.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
						.withValue(ContactsContract.CommonDataKinds.Im.LABEL, label)
						.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE )
						.withValue(ContactsContract.CommonDataKinds.Im.TYPE, ContactsContract.CommonDataKinds.Im.TYPE_CUSTOM)
						.build());
				System.out.println("Adding URLs - label: " + label + " value: " + value);
			}
			
			else if ( attr.get(i).getType() == AttributeType.PRIVATE_NOTE && !allNotes.contains(value)) {
				//Notes details
				ops.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
						.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
						.withValue(ContactsContract.CommonDataKinds.Note.NOTE, value)
						.build());
				System.out.println("Adding Notes - label: " + label + " value: " + value);

			}

		} // end of for
		
		for ( int i = 0; i < addr.size(); i ++ ) 
		{
			if ( allAddrs.contains(addr.get(i).getAddressLine1()))
				continue;
			
			//Postal Address
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
					.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
					.withValue(ContactsContract.CommonDataKinds.StructuredPostal.LABEL, addr.get(i).getLabel())
					.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
					.withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, addr.get(i).getAddressLine1())
					.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
					.withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, addr.get(i).getCity())
					.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
					.withValue(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, addr.get(i).getPostalCode())
					.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
					.withValue(ContactsContract.CommonDataKinds.StructuredPostal.REGION, addr.get(i).getState())
					.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
					.withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM)
					.build());
			System.out.println("Adding Address - " + addr.get(i).getAddressLine1());
			
		}

		try {
			ContentProviderResult[] res = getContentResolver().applyBatch(
					ContactsContract.AUTHORITY, ops);
			System.out.println("Insert contact successfully.");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			System.out.println("RemoteException");
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			// TODO Auto-generated catch block
			System.out.println("OperationApplicationException");
			e.printStackTrace();
		}		

		
		
	}
	
	public void insertNewContact() {

		List<UserProfileAttribute> attr = mProfile.getUserProfileAttributes();
		List<UserProfileAddress> addr = mProfile.getUserProfileAddresses();
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		int rawContactInsertIndex = ops.size();

		ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
				.withValue(RawContacts.ACCOUNT_TYPE, null)
				.withValue(RawContacts.ACCOUNT_NAME, null).build());

		String name = mProfile.getUserProfile().getFirstName() + " " + mProfile.getUserProfile().getLastName();
		//Display name/Contact name
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID,
						rawContactInsertIndex)
						.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
						.withValue(StructuredName.DISPLAY_NAME, name)
						.build());

		for ( int i = 0; i < attr.size(); i ++ ) 
		{
			String value = attr.get(i).getValue();
			String label = attr.get(i).getLabel();
			if ( attr.get(i).getType() == AttributeType.PHONE_NUMBER ) {

				//Phone Number
				ops.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
						.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)	
						.withValue(Phone.NUMBER, value)
						.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
						.withValue(Phone.LABEL, label)
						.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
						.withValue(Phone.TYPE, Phone.TYPE_CUSTOM).build());
			
			}

			else if ( attr.get(i).getType() == AttributeType.EMAIL ) {
				//Email details
				ops.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
						.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
						.withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, value)
						.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)	
						.withValue(ContactsContract.CommonDataKinds.Email.LABEL, label)
						.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
						.withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM).build());
	
			}
			
			else if ( attr.get(i).getType() == AttributeType.SERVICE_ID) {
				//IM details
				ops.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID,rawContactInsertIndex)
						.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
						.withValue(ContactsContract.CommonDataKinds.Im.DATA, value)
						.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
						.withValue(ContactsContract.CommonDataKinds.Im.LABEL, label)
						.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE )
						.withValue(ContactsContract.CommonDataKinds.Im.TYPE, ContactsContract.CommonDataKinds.Im.TYPE_CUSTOM)
						.build());
			}
			
			else if ( attr.get(i).getType() == AttributeType.PRIVATE_NOTE) {
				//Notes details
				ops.add(ContentProviderOperation
						.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValueBackReference(Data.RAW_CONTACT_ID,rawContactInsertIndex)
						.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
						.withValue(ContactsContract.CommonDataKinds.Note.NOTE, value)
						.build());

			}

		}
		

		for ( int i = 0; i < addr.size(); i ++ ) 
		{
			//Postal Address
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,rawContactInsertIndex)
					.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
					.withValue(ContactsContract.CommonDataKinds.StructuredPostal.LABEL, addr.get(i).getLabel())
					.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
					.withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, addr.get(i).getAddressLine1())
					.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
					.withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, addr.get(i).getCity())
					.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
					.withValue(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, addr.get(i).getPostalCode())
					.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
					.withValue(ContactsContract.CommonDataKinds.StructuredPostal.REGION, addr.get(i).getState())
					.withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
					.withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM)
					.build());
		}

		try {
			ContentProviderResult[] res = getContentResolver().applyBatch(
					ContactsContract.AUTHORITY, ops);
			System.out.println("Insert contact successfully.");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			System.out.println("RemoteException");
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			// TODO Auto-generated catch block
			System.out.println("OperationApplicationException");
			e.printStackTrace();
		}		

	}
	
	public void sendUpdatedLabels() {
		isUpdatedPNotes = true;
		
		String path = "/api/contacts/" + mProfile.getUserId() + "/labels.json?" + SingletonLoginData.getInstance().getPostParam();
		SingletonNetworkStatus.getInstance().setActivity(this);
		
		SingletonLoginData.getInstance().getContactList().get(mArgInt).setLabels(mAllLabels);
		
		Gson gson = new GsonBuilder().create();
		String params = gson.toJson(mAllLabels);
		
		params = "{\"label\":" + params + "}";
		new HttpConnection().access(this, path, params, "POST");
		
	}

	public void updateLabel() {

		List<String> labels = SingletonLoginData.getInstance().getContactLabels();
		LinearLayout container = (LinearLayout)findViewById(R.id.pcn_label_lo);
		container.removeAllViews();
		mAllLabels.clear();
		for ( int i = 0; labels!=null && i < labels.size(); i ++ ) {
			final View addView = getLayoutInflater().inflate(R.layout.label_list_item, null);
		    String label = labels.get(i);
			TextView tvView = (TextView)addView.findViewById(R.id.label_view);
			tvView.setText(label);
			tvView.setVisibility(View.VISIBLE);
			
		    TextView addTV = (TextView) addView.findViewById(R.id.view_add);
			EditText inputET = (EditText)addView.findViewById(R.id.label_input);
			inputET.setVisibility(View.GONE);
			addTV.setVisibility(View.INVISIBLE);
			
		    TextView deleteTV = (TextView) addView.findViewById(R.id.view_delete);
			deleteTV.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					LinearLayout container = (LinearLayout)findViewById(R.id.pcn_label_lo);
					View addView = (View)v.getParent();
					String label = ((TextView)addView.findViewById(R.id.label_view)).getText().toString();
					container.removeView(addView);
					mAllLabels.remove(label);
				}
			});
			
			container.addView(addView);
			
			mAllLabels.add(label);
			
		}
		
	}
	
	public void updateLabelOld() {
		String label = SingletonLoginData.getInstance().getUserSettings().selectedLabel;
		if ( label != null && label.length() > 0 ) {
			SingletonLoginData.getInstance().getUserSettings().selectedLabel = "";
			
			if ( !mAllLabels.contains(label) ) {
				// add this label
				LinearLayout container = (LinearLayout)findViewById(R.id.pcn_label_lo);
				final View addView = getLayoutInflater().inflate(R.layout.label_list_item, null);
				
			    
				TextView labelTV = (TextView)addView.findViewById(R.id.label_view);
				labelTV.setText(label);
				labelTV.setVisibility(View.VISIBLE);
				
			    TextView addTV = (TextView) addView.findViewById(R.id.view_add);
				EditText inputET = (EditText)addView.findViewById(R.id.label_input);
				inputET.setVisibility(View.GONE);
				addTV.setVisibility(View.INVISIBLE);

			    TextView deleteTV = (TextView) addView.findViewById(R.id.view_delete);
				deleteTV.setVisibility(View.VISIBLE);
				deleteTV.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						LinearLayout container = (LinearLayout)findViewById(R.id.pcn_label_lo);
						View addView = (View)v.getParent();
						String label = ((TextView)addView.findViewById(R.id.label_view)).getText().toString();
						container.removeView(addView);
						mAllLabels.remove(label);
					}
				});
				
				container.addView(addView);
				
				mAllLabels.add(label);
			}
		}
	}
	

	public void sendUpdatedPNotes() {
		// send request to get shared profiles
		EditText et = (EditText) findViewById(R.id.et_pnotes);
		String sstr = et.getText().toString();
		SingletonLoginData.getInstance().getContactList().get(mArgInt).setContactNote(sstr);
		
		isUpdatedPNotes = true;
		String path = "/api/contacts/" + mProfile.getUserId() + "/note.json?" + SingletonLoginData.getInstance().getPostParam();
		SingletonNetworkStatus.getInstance().setActivity(this);
		
		String str = sstr.replaceAll("\\n", MYTOKEN);
		String params = "{\"note\":\"" + str + "\"}";
		new HttpConnection().access(this, path, params, "POST");

	}

	
	public void deleteContact() {
		isDeleteContact = true;
		String path = "/api/contacts/" + mProfile.getUserId() + "/delete.json?" + SingletonLoginData.getInstance().getPostParam();
		SingletonNetworkStatus.getInstance().setActivity(this);
		new HttpConnection().access(this, path, "", "POST");

	}

	public void onIntroduce() {
		Intent it = new Intent(this, ContactIntroduceListActivity.class);
		startActivity(it);
	}

	public void onView() {
		viewProfile();
		viewPhone();
		viewEmail();
		viewURL();
		viewNote();
		viewAddress();			
	}

	public void onSave() {

		// collect data
		String profileName = ((EditText)findViewById(R.id.pcn_profile_name)).getText().toString();
		String firstName = ((EditText)findViewById(R.id.pcn_first_name)).getText().toString();
		String middleName = ((EditText)findViewById(R.id.pcn_middle_name)).getText().toString();
		String lastName = ((EditText)findViewById(R.id.pcn_last_name)).getText().toString();
		String title = ((EditText)findViewById(R.id.pcn_title)).getText().toString();
		String company = ((EditText)findViewById(R.id.pcn_company)).getText().toString();

		ProfileDTO profile = new ProfileDTO();
		profile.setUserProfile(new UserProfile());
		profile.setUserProfileAttributes(new ArrayList<UserProfileAttribute>());
		profile.setUserProfileAddresses(new ArrayList<UserProfileAddress>());

		profile.setUserId(mProfile.getUserId());
		profile.setProfileId(mProfile.getUserProfile().getId());
		profile.getUserProfile().setId(mProfile.getUserProfile().getId());
		profile.getUserProfile().setName(profileName);
		profile.getUserProfile().setFirstName(firstName);
		profile.getUserProfile().setMiddleName(middleName);
		profile.getUserProfile().setLastName(lastName);
		profile.getUserProfile().setTitle(title);
		profile.getUserProfile().setCompanyName(company);
		profile.getUserProfile().setPathToImage(mProfile.getUserProfile().getPathToImage());

		addAttribute(profile);
		addAddress(profile);

		Gson gson = new GsonBuilder().create();
		String params = gson.toJson(profile);
		String path;

		if ( mArgInt > -1 ) {
			path = "/api/profiles/" + profile.getUserProfile().getId() 
					+ "/update.json?" + SingletonLoginData.getInstance().getPostParam();
		} else {
			path = "/api/profiles.json?" + SingletonLoginData.getInstance().getPostParam();
		}

		SingletonNetworkStatus.getInstance().setActivity(this);
		new HttpConnection().access(this, path, params, "POST");

		mProfile = profile;
	}

	public class ImageDTO {

		public Long sourceId;
		public String thumbnailPath;

	}

	public void onPostNetwork() {

		SingletonNetworkStatus.getInstance().setDoNotShowStatus(false);
		if ( SingletonNetworkStatus.getInstance().getCode() != 200 ) {
			myDialog(SingletonNetworkStatus.getInstance().getMsg(), 
					SingletonNetworkStatus.getInstance().getErrMsg());
			return;
		}

		if ( isUpdateSharedProfile ) {
			isUpdateSharedProfile = false;
			return;
		}
		
		if ( isUpdatedPNotes ) {
			isUpdatedPNotes = false;
			return;
		}
		
		if ( isQuerySharedProfile ) {
			isQuerySharedProfile = false;
			updateSharedProfileIcons();
			return;
		}

		if ( isQueryContactLabels ) {
			isQueryContactLabels = false;
			updateSharedProfileIcons();
			return;
		}

		if ( isDeleteContact ) {
			SingletonLoginData.getInstance().getContactList().remove(mArgInt);
			goBack();
			return;
		}

		if ( isDeleteProfile ) {
			ArrayList<ProfileDTO> profs = SingletonLoginData.getInstance().getUserProfiles();
			profs.remove(mArgInt);
			goBack();
			return;
		}

		if ( isUploadingImage ) {
			isUploadingImage = false;

			Gson gson = new GsonBuilder().create();
			ImageDTO imInfo = gson.fromJson(SingletonNetworkStatus.getInstance().getJson(), ImageDTO.class);
			mProfile.getUserProfile().setPathToImage(imInfo.thumbnailPath);
			
			/*
			if ( mImagePath != null ) {
				System.out.println("delete file path : " + mImagePath);
	            File f = new File(mImagePath);
	            if (f.exists()) {
	                  f.delete();
	                  System.out.println("Delete....");
	            } else 
	            	System.out.println("Not exist..");
			}
			*/
            
			return;
		}

		Gson gson = new GsonBuilder().create();

		// 200 is ok
		if ( mArgInt >= -1 )
		{
			Intent intent = getIntent();

			ArrayList<ProfileDTO> profs = SingletonLoginData.getInstance().getUserProfiles();
			if ( mArgInt > -1 ) {
				profs.remove(mArgInt);
				profs.add(mArgInt, mProfile);
			} else {
				mProfile = gson.fromJson(SingletonNetworkStatus.getInstance().getJson(), ProfileDTO.class);
				profs.add(mProfile);
				intent.putExtra(ProfileViewNewActivity.ARG_PROFILE_VIEW, profs.size()-1);
			}

			goBack();
			startActivity(intent);
		} else {
			// for login new
			Intent it = new Intent(this, ProfileNewSuccessActivity.class);
			startActivity(it);
		}

	}

	public void addAddress(ProfileDTO profile) {
		if ( mViewAddress.size() > 0) {
			for (int i = 0; i < mViewAddress.size(); i ++) {
				UserProfileAddress attr = new UserProfileAddress();
				attr.setLabel(((EditText)mViewAddress.get(i).findViewById(R.id.input_label)).getText().toString());
				attr.setAddressLine1(((EditText)mViewAddress.get(i).findViewById(R.id.input_id1)).getText().toString());
				attr.setCity(((EditText)mViewAddress.get(i).findViewById(R.id.input_city)).getText().toString());
				attr.setState(((EditText)mViewAddress.get(i).findViewById(R.id.input_state)).getText().toString());
				attr.setPostalCode(((EditText)mViewAddress.get(i).findViewById(R.id.input_zip)).getText().toString());
				profile.getUserProfileAddresses().add(attr);
			}
		}
	}

	public void addAttribute(ProfileDTO profile) {
		addAttributeType(profile, AttributeType.PHONE_NUMBER);
		addAttributeType(profile, AttributeType.EMAIL);
		addAttributeType(profile, AttributeType.PRIVATE_NOTE);
		addAttributeType(profile, AttributeType.SERVICE_ID);
	}

	public void addAttributeType(ProfileDTO profile, AttributeType type) {
		ArrayList<View> vl = mViewMap.get(type);
		if ( vl != null && vl.size() > 0) {
			for (int i = 0; i < vl.size(); i ++) {
				UserProfileAttribute attr = new UserProfileAttribute();
				attr.setType(type);
				attr.setLabel(((EditText)vl.get(i).findViewById(R.id.input_label)).getText().toString());
				attr.setValue(((EditText)vl.get(i).findViewById(R.id.input_id1)).getText().toString());
				profile.getUserProfileAttributes().add(attr);
			}
		}
	}

	public void onEdit() {
		showRightText(getResources().getString(R.string.ab_done));
		View.OnClickListener finClkLn = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onSave();
			}
		};
		addRightLn(finClkLn);

		// make all field visible
		hidePhone(View.VISIBLE);
		hideEmail(View.VISIBLE);
		hideUrl(View.VISIBLE);
		hideAddr(View.VISIBLE);
		hideNote(View.VISIBLE);

		hideAddPhone(View.VISIBLE);
		hideAddEmail(View.VISIBLE);
		hideAddUrl(View.VISIBLE);
		hideAddAddr(View.VISIBLE);

		// enable each section
		editProfile();
		editAttributes();
		editAddress();

	}

	public void addNoteField() {

		LinearLayout container = (LinearLayout)findViewById(R.id.pcn_note_lo);
		final View addView = getLayoutInflater().inflate(R.layout.profile_create_add_phone, null);

		// change the name of the text
		EditText tv = (EditText)addView.findViewById(R.id.input_label);
		tv.setText(getResources().getString(R.string.pcn_label_note));

		EditText et = (EditText)addView.findViewById(R.id.input_id1);
		et.setInputType(InputType.TYPE_CLASS_TEXT);

		TextView buttonRemove = (TextView)addView.findViewById(R.id.view_id1);
		buttonRemove.setVisibility(View.GONE);

		container.addView(addView);
		addViewIntoMap(addView, AttributeType.PRIVATE_NOTE);
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
		addViewIntoMap(addView, AttributeType.PHONE_NUMBER);
	}

	public void addViewIntoMap(View v, AttributeType type) {
		ArrayList<View> vl = mViewMap.get(type); 
		if ( vl == null ) {
			vl = new ArrayList<View> ();
			mViewMap.put( type,  vl);
		}

		vl.add(v);
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
		et.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

		TextView buttonRemove = (TextView)addView.findViewById(R.id.view_id1);
		buttonRemove.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				((LinearLayout)addView.getParent()).removeView(addView);
			}});

		container.addView(addView);
		addViewIntoMap(addView, AttributeType.EMAIL);

	}

	public void onAddrAddNew(View view) {

		LinearLayout container = (LinearLayout)findViewById(R.id.pcn_addr_lo);
		final View addView = getLayoutInflater().inflate(R.layout.profile_create_add_address, null);

		// change the name of the text
		EditText tv = (EditText)addView.findViewById(R.id.input_label);
		tv.setText(getResources().getString(R.string.pcn_label_addr));

		EditText et = (EditText)addView.findViewById(R.id.input_id1);
		et.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

		TextView buttonRemove = (TextView)addView.findViewById(R.id.view_id1);
		buttonRemove.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				((LinearLayout)addView.getParent()).removeView(addView);
			}});

		container.addView(addView);
		mViewAddress.add(addView);

	}

	public void onUrlAddNew(View view) {

		LinearLayout container = (LinearLayout)findViewById(R.id.pcn_url_lo);
		final View addView = getLayoutInflater().inflate(R.layout.profile_create_add_phone, null);

		// change the name of the text
		EditText tv = (EditText)addView.findViewById(R.id.input_label);
		tv.setText(getResources().getString(R.string.pcn_label_url));

		EditText et = (EditText)addView.findViewById(R.id.input_id1);
		et.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

		TextView buttonRemove = (TextView)addView.findViewById(R.id.view_id1);
		buttonRemove.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				((LinearLayout)addView.getParent()).removeView(addView);
			}});

		container.addView(addView);
		addViewIntoMap(addView, AttributeType.SERVICE_ID);

	}

	public void setSubViewEditable(View parent, int id) 
	{
		EditText v = (EditText) parent.findViewById(id);
		v.setFocusableInTouchMode(true);
	}


	public void setSubViewValue(View parent, int id, String value, boolean b) 
	{
		EditText v = (EditText) parent.findViewById(id);
		v.setText(value);
		v.setFocusable(b);
		v.setClickable(b);

		if ( b )
			v.setFocusableInTouchMode(b);
	}

	public void setViewValue(int id, String value, boolean b) 
	{
		EditText v = (EditText) findViewById(id);
		if ( myFont != null )
			v.setTypeface(myFont);
		v.setText(value);
		v.setFocusable(b);
		v.setClickable(b);

		if ( b )
			v.setFocusableInTouchMode(b);
	}

	public void setViewOnly(int id, String value) 
	{
		setViewValue(id, value == null || value.isEmpty() ? " " : value, false);
	}

	public void setSubViewOnly(View parent, int id, String value) 
	{
		setSubViewValue(parent, id, value == null || value.isEmpty() ? " " : value, false);
	}

	public void setEditable(int id, String value) 
	{
		setViewValue(id, value, true);
	}

	public void setSubEditable(View parent, int id, String value) 
	{
		setSubViewValue(parent, id, value, true);
	}

	protected void viewProfile() {
		EditText profName = (EditText) findViewById(R.id.pcn_profile_name);
		profName.setVisibility(View.GONE);

		TextView tvSep = (TextView) findViewById(R.id.pcn_sep_name);
		tvSep.setVisibility(View.GONE);

		ImageView ivPhoto = (ImageView) findViewById(R.id.pcn_add_image);
		Bitmap bm = SingletonLoginData.getInstance().getBitmap(mArgInt);
		if ( bm != null && !mIsContactView ) {  // do not show my profile image to contact.
			ivPhoto.setImageBitmap(bm);
			ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
		} else {
			ivPhoto.setImageResource(R.drawable.silhouette);
		}
		ivPhoto.setFocusable(false);
		ivPhoto.setClickable(false);

		String middleName = mProfile.getUserProfile().getMiddleName();
		String middleName1 = " ";
		if ( middleName != null && middleName.length() > 0 ) {
			middleName1 = " " + middleName + " ";
		}
		setViewOnly(R.id.pcn_first_name, mProfile.getUserProfile().getFirstName() + middleName1 + mProfile.getUserProfile().getLastName());
		setViewOnly(R.id.pcn_last_name, mProfile.getUserProfile().getTitle());
		setViewOnly(R.id.pcn_middle_name, mProfile.getUserProfile().getMiddleName());
		setViewOnly(R.id.pcn_title, mProfile.getUserProfile().getCompanyName());
		if ( !mIsContactView && !mIsSharedView )
			setViewOnly(R.id.pcn_company, SingletonLoginData.getInstance().getUserData().pin);
		else 
			setViewOnly(R.id.pcn_company, " ");

		// hide middel name
		EditText hide = (EditText)findViewById(R.id.pcn_middle_name);
		hide.setVisibility(View.GONE);

		// show shared profiles
		for (int i = 0; mIsContactView && i < SingletonLoginData.getInstance().getUserProfiles().size() ; i ++ ) {
			addProfiles(i);
		}

	}

	protected void editAttributes() {
		editView(AttributeType.PHONE_NUMBER);
		editView(AttributeType.EMAIL);
		editView(AttributeType.SERVICE_ID);
		editView(AttributeType.PRIVATE_NOTE);
	}

	protected void editView(AttributeType type) {
		ArrayList<View> vl = mViewMap.get(type);

		if ( vl != null ) {
			for (int i = 0 ; i < vl.size(); i ++) {
				View v = vl.get(i);

				if ( type != AttributeType.PRIVATE_NOTE) {
					TextView buttonRemove = (TextView)v.findViewById(R.id.view_id1);
					buttonRemove.setVisibility(View.VISIBLE);
				}

				setSubViewEditable(v, R.id.input_label);
				setSubViewEditable(v, R.id.input_id1);

			}

		} else if ( type == AttributeType.PRIVATE_NOTE ) {
			// new note item
			addNoteField();
		}
	}

	protected void editAddress() {
		for (int i = 0 ; i < mViewAddress.size(); i ++) {
			View v = mViewAddress.get(i);
			TextView buttonRemove = (TextView)v.findViewById(R.id.view_id1);
			buttonRemove.setVisibility(View.VISIBLE);

			setSubViewEditable(v, R.id.input_label);
			setSubViewEditable(v, R.id.input_id1);
			setSubViewEditable(v, R.id.input_city);
			setSubViewEditable(v, R.id.input_state);
			setSubViewEditable(v, R.id.input_zip);

		}
	}


	protected void editProfile() {
		EditText profName = (EditText) findViewById(R.id.pcn_profile_name);
		profName.setVisibility(View.VISIBLE);
		profName.setText(mProfile.getUserProfile().getName());

		TextView tvSep = (TextView) findViewById(R.id.pcn_sep_name);
		tvSep.setVisibility(View.VISIBLE);

		ImageView ivPhoto = (ImageView) findViewById(R.id.pcn_add_image);
		Bitmap bm = SingletonLoginData.getInstance().getBitmap(mArgInt);
		if ( bm != null ) {
			ivPhoto.setImageBitmap(bm);
			ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
		} else {
			ivPhoto.setImageResource(R.drawable.add_an_image);
		}
		ivPhoto.setFocusable(true);
		ivPhoto.setClickable(true);


		setEditable(R.id.pcn_first_name, mProfile.getUserProfile().getFirstName());
		setEditable(R.id.pcn_last_name, mProfile.getUserProfile().getLastName());
		setEditable(R.id.pcn_middle_name, mProfile.getUserProfile().getMiddleName());
		setEditable(R.id.pcn_title, mProfile.getUserProfile().getTitle());
		setEditable(R.id.pcn_company, mProfile.getUserProfile().getCompanyName());

		// show middel name
		EditText hide = (EditText)findViewById(R.id.pcn_middle_name);
		hide.setVisibility(View.VISIBLE);


		if ( mArgInt < 0 ) {  // singup or new
			setEditable(R.id.pcn_first_name, SingletonLoginData.getInstance().getUserData().firstName);
			setEditable(R.id.pcn_last_name, SingletonLoginData.getInstance().getUserData().lastName);
			setEditable(R.id.pcn_middle_name, SingletonLoginData.getInstance().getUserData().middleName);

			onEmailAddNew(tvSep);
			ArrayList<View> vl = mViewMap.get(AttributeType.EMAIL);
			View parent = vl.get(0);
			EditText et = (EditText) parent.findViewById(R.id.input_id1);
			et.setText(SingletonLoginData.getInstance().getUserData().emailId);
		} else if ( mArgInt < 1000 ){ // profile edit
			RelativeLayout lo = (RelativeLayout) findViewById(R.id.button_rlo);
			TextView tv = (TextView) findViewById(R.id.sep_below_button_rlo);
			lo.setVisibility(View.VISIBLE);
			tv.setVisibility(View.VISIBLE);
			tv = (TextView) findViewById(R.id.tv_button_add);
			tv.setVisibility(View.GONE);
			tv = (TextView) findViewById(R.id.tv_button_delete);
			tv.setText("DELETE PROFILE");
			tv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String msg = "You are about to delete this profile. This operation cannot be undone. Do you still wish to continue?";
					alertDialog("ARE YOU SURE", msg);
				}
			});
		}
	}

	public void alertDialog(String title, String info) {
		AlertDialog.Builder builder = new AlertDialog.Builder( new ContextThemeWrapper(this, R.style.AlertDialogCustom));
		//AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(true);
		builder.setTitle(title);
		builder.setMessage(info);
		builder.setInverseBackgroundForced(true);
		builder.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,
					int which) {
				dialog.dismiss();
				
				if ( isDeleteContact ) {
					deleteContact();
				} else {
					deleteProfile();
				}
			}
		});
		builder.setNegativeButton("NO", 
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,
					int which) {
				isDeleteContact = false;
				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}		

	public void deleteProfile() {
		isDeleteProfile = true;
		String path = "/api/profiles/" + mProfile.getUserProfile().getId() + "/delete.json?" + SingletonLoginData.getInstance().getPostParam();
		SingletonNetworkStatus.getInstance().setActivity(this);
		new HttpConnection().access(this, path, "", "POST");

	}

	protected void viewPhone() {

		if (! viewAttr(AttributeType.PHONE_NUMBER, R.id.pcn_phone_lo))
			hidePhone(View.GONE);
		else
			hideAddPhone(View.GONE);
	}

	protected void viewEmail() {

		if (! viewAttr(AttributeType.EMAIL, R.id.pcn_email_lo))
			hideEmail(View.GONE);
		else
			hideAddEmail(View.GONE);
	}

	protected void viewURL() {

		if (! viewAttr(AttributeType.SERVICE_ID, R.id.pcn_url_lo))
			hideUrl(View.GONE);
		else
			hideAddUrl(View.GONE);
	}

	protected void viewNote() {

		if (! viewAttr(AttributeType.PRIVATE_NOTE, R.id.pcn_note_lo))
			hideNote(View.GONE);
	}

	protected boolean viewAttr(AttributeType type, int id) {
		boolean found = false;

		for (int i = 0; i < mProfile.getUserProfileAttributes().size(); i ++)
		{
			UserProfileAttribute attr = mProfile.getUserProfileAttributes().get(i);
			if ( attr.getType() == type ) {
				found = true;
				addView(attr, id, type);
			}
		}

		return found;
	}

	protected void viewAddress() {
		boolean found = false;

		for (int i = 0; i < mProfile.getUserProfileAddresses().size(); i ++)
		{
			found = true;
			UserProfileAddress attr = mProfile.getUserProfileAddresses().get(i);
			addAddressView(attr);
		}

		if (!found)
			hideAddr(View.GONE);
		else {
			hideAddAddr(View.GONE);
		}
	}


	public void addAddressView(UserProfileAddress attr) {
		LinearLayout container = (LinearLayout)findViewById(R.id.pcn_addr_lo);
		final View addView = getLayoutInflater().inflate(R.layout.profile_create_add_address, null);
		TextView buttonRemove = (TextView)addView.findViewById(R.id.view_id1);
		buttonRemove.setVisibility(View.GONE);
		buttonRemove.setOnClickListener( new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mViewAddress.remove(addView);
				((LinearLayout)addView.getParent()).removeView(addView);
			}
		});

		setSubViewOnly(addView, R.id.input_label, attr.getLabel());
		setSubViewOnly(addView, R.id.input_id1, attr.getAddressLine1());
		setSubViewOnly(addView, R.id.input_city, attr.getCity());
		setSubViewOnly(addView, R.id.input_state, attr.getState());
		setSubViewOnly(addView, R.id.input_zip, attr.getPostalCode());
		container.addView(addView);

		TextView sep = (TextView)addView.findViewById(R.id.addr_sep);
		sep.setVisibility(View.GONE);
		if ( mViewAddress.size() > 0 ) {
			sep =  (TextView)mViewAddress.get(mViewAddress.size() - 1).findViewById(R.id.addr_sep);
			sep.setVisibility(View.VISIBLE);
		}

		mViewAddress.add(addView);

	}


	public void addView(UserProfileAttribute attr, int id, AttributeType type) {
		LinearLayout container = (LinearLayout)findViewById(id);
		final View addView = getLayoutInflater().inflate(R.layout.profile_create_add_phone, null);
		final AttributeType myType = type;
		TextView buttonRemove = (TextView)addView.findViewById(R.id.view_id1);
		buttonRemove.setVisibility(View.GONE);
		buttonRemove.setOnClickListener( new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mViewMap.get(myType).remove(addView);
				((LinearLayout)addView.getParent()).removeView(addView);
			}
		});


		setSubViewOnly(addView, R.id.input_label, attr.getLabel());
		setSubViewOnly(addView, R.id.input_id1, attr.getValue());
		EditText et = (EditText)addView.findViewById(R.id.input_id1);
		if ( type == AttributeType.EMAIL )
			et.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		else if ( type == AttributeType.PRIVATE_NOTE)
			et.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_NORMAL);
		else if ( type == AttributeType.SERVICE_ID)
			et.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_URI);
		else if ( type == AttributeType.PHONE_NUMBER)
			et.setInputType(InputType.TYPE_CLASS_PHONE);

		container.addView(addView);

		TextView sep = (TextView)addView.findViewById(R.id.phone_sep);
		sep.setVisibility(View.GONE);

		ArrayList<View> vl = mViewMap.get(type); 
		if ( vl == null ) {
			vl = new ArrayList<View> ();
			mViewMap.put( type,  vl);
		}

		if ( vl.size() > 0 ) {
			sep =  (TextView)vl.get(vl.size() - 1).findViewById(R.id.phone_sep);
			sep.setVisibility(View.VISIBLE);
		}

		vl.add(addView);
	}

	protected void hideAddPhone(int v) {
		ImageView iv = (ImageView) findViewById(R.id.pcn_phone_icon);
		iv.setVisibility(v);
		TextView sepRlo = (TextView) findViewById(R.id.pcn_add_phone);
		sepRlo.setVisibility(v);
	}

	protected void hideAddEmail(int v) {
		ImageView iv = (ImageView) findViewById(R.id.pcn_email_icon);
		iv.setVisibility(v);
		TextView sepRlo = (TextView) findViewById(R.id.pcn_add_email);
		sepRlo.setVisibility(v);
	}

	protected void hideAddUrl(int v) {
		ImageView iv = (ImageView) findViewById(R.id.pcn_url_icon);
		iv.setVisibility(v);
		TextView sepRlo = (TextView) findViewById(R.id.pcn_add_url);
		sepRlo.setVisibility(v);
	}

	protected void hideAddAddr(int v) {
		ImageView iv = (ImageView) findViewById(R.id.pcn_addr_icon);
		iv.setVisibility(v);
		TextView sepRlo = (TextView) findViewById(R.id.pcn_add_addr);
		sepRlo.setVisibility(v);
	}

	protected void hidePhone(int v) {
		RelativeLayout rlo = (RelativeLayout) findViewById(R.id.first_rlo);
		rlo.setVisibility(v);
		TextView sepRlo = (TextView) findViewById(R.id.sep_below_first_rlo);
		sepRlo.setVisibility(v);
	}

	protected void hideEmail(int v) {
		RelativeLayout rlo = (RelativeLayout) findViewById(R.id.email_rlo);
		rlo.setVisibility(v);
		TextView sepRlo = (TextView) findViewById(R.id.sep_below_email_rlo);
		sepRlo.setVisibility(v);
	}

	protected void hideAddr(int v) {
		RelativeLayout rlo = (RelativeLayout) findViewById(R.id.addr_rlo);
		rlo.setVisibility(v);
		TextView sepRlo = (TextView) findViewById(R.id.sep_below_addr_rlo);
		sepRlo.setVisibility(v);
	}

	protected void hideUrl(int v) {
		RelativeLayout rlo = (RelativeLayout) findViewById(R.id.url_rlo);
		rlo.setVisibility(v);
		TextView sepRlo = (TextView) findViewById(R.id.sep_below_url_rlo);
		sepRlo.setVisibility(v);
	}

	protected void hideNote(int v) {
		RelativeLayout rlo = (RelativeLayout) findViewById(R.id.note_rlo);
		rlo.setVisibility(v);
		TextView sepRlo = (TextView) findViewById(R.id.sep_below_note_rlo);
		sepRlo.setVisibility(v);
	}

	public void addProfiles(int i) {

		String title = SingletonLoginData.getInstance().getUserProfiles().get(i).getUserProfile().getName();

		hm.put(title, 0);

		LinearLayout container = (LinearLayout)findViewById(R.id.share_layout);
		final View addView = getLayoutInflater().inflate(R.layout.profile_thumb_half, null);
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
								R.drawable.border_profile_thumb_sel_half :
									R.drawable.border_profile_thumb_nosel_half
						));
				if ( value == 0 ) {
					hm.put(key, 1);
				} else 
					hm.put(key, 0);

				// we need to update tv_share
				String listProfiles = "";
				for ( int i = 0; i < hm.size(); i ++ ) {
					String name = SingletonLoginData.getInstance().getUserProfiles().get(i).getUserProfile().getName();
					if ( hm.get(name) == 1 ) {
						listProfiles += name + ",";
					}
				}
				TextView tvInfo = (TextView) findViewById(R.id.tv_share);
				if ( listProfiles.length() > 1 )
					listProfiles = listProfiles.substring(0, listProfiles.length() - 1);

				tvInfo.setText("PROFILES SHARED: " + listProfiles);                 

			}});

	}		

	private Uri getTempUri() {
	    return Uri.fromFile(getTempFile());
	}

	private static final String TEMP_PHOTO_FILE = "temporary_holder.png";  
	private File getTempFile() {

	    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

	        File file = new File(Environment.getExternalStorageDirectory(),TEMP_PHOTO_FILE);
	        try {
	            file.createNewFile();
	        } catch (IOException e) {}

	        return file;
	    } else {

	        return null;
	    }
	}	
	
	public String saveBitmap2File(Bitmap bmp) {
		FileOutputStream out;
		String filename;
		
	    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

	        filename = Environment.getExternalStorageDirectory() + "/" + TEMP_PHOTO_FILE;
	    } else 
	    	return null;
		
		try {
		       out = new FileOutputStream(filename);
		       bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
		       out.close();
		       return filename;
		} catch (Exception e) {
		    e.printStackTrace();
		    return filename;
		}		
	}

	public void onUploadProfImage(View view) {
		System.out.println("Select an image from gallery.");

		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK); //, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		photoPickerIntent.setType("image/*");
		//photoPickerIntent.putExtra("crop", "true");
		//photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
		//photoPickerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		startActivityForResult(photoPickerIntent, 1);		

	}
	
	//create helping method cropCapturedImage(Uri picUri)
	public void cropCapturedImage(Uri picUri)
	{
		//call the standard crop action intent 
		Intent cropIntent = new Intent("com.android.camera.action.CROP");
		//indicate image type and Uri of image
		cropIntent.setDataAndType(picUri, "image/*");
		//set crop properties
		cropIntent.putExtra("crop", "true");
		//indicate aspect of desired crop
		cropIntent.putExtra("aspectX", 1);
		cropIntent.putExtra("aspectY", 1);
		//indicate output X and Y
		cropIntent.putExtra("outputX", 256);
		cropIntent.putExtra("outputY", 256);
		//retrieve data on return
		cropIntent.putExtra("return-data", true);
		//start the activity - we handle returning in onActivityResult
		startActivityForResult(cropIntent, 2);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  

		if (requestCode == 1 && resultCode == RESULT_OK && data != null) 
		{
			Uri uri = data.getData();
			if (uri == null)
				return;
			
			try {
				/*the user's device may not support cropping*/
				String path = getPath(uri);
				if ( path != null && path.length() > 1 )
					System.out.println("Getting image from : " + path);
				cropCapturedImage(uri);
			}
			catch(ActivityNotFoundException aNFE){
				//display an error message if user device doesn't support
				String errorMessage = "Sorry - your device doesn't support the crop action!";
				Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
				toast.show();
			}    
		}
		
		if ( requestCode == 2 && resultCode == RESULT_OK && data != null ) {

			//Create an instance of bundle and get the returned data
			Bundle extras = data.getExtras();
			//get the cropped bitmap from extras
			Bitmap bm = extras.getParcelable("data");				

			//Bitmap bm = getBitmapFromUri(uri);
			ImageView ivPhoto = (ImageView) findViewById(R.id.pcn_add_image);
			if ( bm != null ) {
				SingletonLoginData.getInstance().setBitmap(mArgInt, bm);
				ivPhoto.setImageBitmap(bm);
				ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
			}

			// WRITE Bitmap TO FILE
			mImagePath = saveBitmap2File(bm);
			
			// CALL THIS METHOD TO GET THE URI FROM THE BITMAP
			//Uri tempUri = getImageUri(getApplicationContext(), bm);

			// CALL THIS METHOD TO GET THE ACTUAL PATH
			//mImagePath = getTempUri();
			//mImagePath = getRealPathFromURI(tempUri);

			System.out.println("file path : " + mImagePath);

			if ( mImagePath == null || mImagePath.length() < 1)
				return;

			// send file to server?
			isUploadingImage = true;
			String path = "/api/image.json?" + 
					SingletonLoginData.getInstance().getPostParam();

			SingletonNetworkStatus.getInstance().setActivity(this);
			new HttpConnectionForImage().access(this, path, "", "POST", mImagePath);

			//File finalFile = new File(filePath);

		}  
	}
	
	@SuppressWarnings("deprecation")
	public String getPath(Uri uri) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		String imagePath = cursor.getString(column_index);
		System.out.println("getPath: " + imagePath);

		return imagePath;		
	}

	public Uri getImageUri(Context inContext, Bitmap inImage) {
	    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	    inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
	    String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
	    System.out.println("Inserting path " + path);
	    return Uri.parse(path);
	}

	public String getRealPathFromURI(Uri uri) {
	    Cursor cursor = getContentResolver().query(uri, null, null, null, null); 
	    cursor.moveToFirst(); 
	    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
	    return cursor.getString(idx); 
	}

    @Override
    public void onResume() {
    	super.onResume();
    	System.out.println("Onresume - ProfileViewNewActiviyt.");
    	
    	updateLabel();
    }
    
    public void goBack() {
		finish();
		overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_right );
    }
    
    @Override
    public void onBackPressed() {
    	System.out.println("OnBackPressed - Activity.");
    	goBack();
    }


}
