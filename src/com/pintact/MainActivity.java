package com.pintact;


import com.crashlytics.android.Crashlytics;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pintact.data.AttributeType;
import com.pintact.data.ContactDTO;
import com.pintact.data.DeviceRegistrationRequest;
import com.pintact.data.ProfileDTO;
import com.pintact.data.DeviceRegistrationRequest.DeviceType;
import com.pintact.data.UserProfile;
import com.pintact.data.UserProfileAttribute;
import com.pintact.login.*;
import com.pintact.utility.DataLogin;
import com.pintact.utility.DataLoginData;
import com.pintact.utility.HttpConnection;
import com.pintact.utility.MyActivity;
import com.pintact.utility.SingletonLoginData;
import com.pintact.utility.SingletonNetworkStatus;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings.Secure;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends MyActivity {
	
	String mUserName;
	String mPassword;
	
	int mloginStep = 0;
	boolean isGCMRegister = true;
	
	// from sample code
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "159895103372";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCM Demo";

    GoogleCloudMessaging gcm;
    Context context;

    String regid;	
	// end of sample code

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		setContentView(R.layout.activity_main);
		setupUI(findViewById(R.id.main_view));
		
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		TextView btn = (TextView) findViewById(R.id.loginBtn);
		btn.setFocusableInTouchMode(true);
		btn.requestFocus();
		
		ActionBar actionBar = getActionBar();
		actionBar.hide();		
		
		loadLoginData();
		
		if ( mUserName != null && mPassword != null ) {
			loginSent();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onDummy(View view) {
		
	}

    @Override
    public void onBackPressed() {
    	moveTaskToBack(true);
    	System.out.println("OnBackPressed - MainActivity.");
    	return;
    }

	public void onTest(View view) {

		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection    = new String[] {
				ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.Contacts.HAS_PHONE_NUMBER};
	    String sortOrder =  ContactsContract.Contacts.DISPLAY_NAME + " ASC";
	    String filter =  ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1 " ;
	    Cursor people = this.getContentResolver().query(uri, projection, filter, null, sortOrder);
	    
	    int indexID = people.getColumnIndex(ContactsContract.Contacts._ID);
	    int indexDN = people.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
	    int indexPN = people.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
	    for ( int i = 0; i < people.getCount(); i ++) {
	    	people.moveToPosition(i);
	    	String dn = people.getString(indexDN);
	    	String pn = people.getString(indexPN);
	    	String id = people.getString(indexID);
	    	System.out.println("Row " + i + " dn:" + dn + " pn:" + pn + " id:" + id);
	    	
	    	// loading all phone for this entry
	    }
	    
	    people.close();
	    
	    
    	Uri uri1 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection1    = new String[] {
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.LABEL};
		String conds = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = 625";
		Cursor phones = this.getContentResolver().query(uri1, projection1, conds, null, sortOrder);
		while ( phones.moveToNext()) {
			String pid = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
			String label = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
			String dName = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			System.out.println(dName +" == " + label + ":" + number + " id:" + pid);
		}
	    
	    
		/*
		Intent it = new Intent(this, ProfileShareActivity.class);

		startActivity(it);
		*/
	}

	
	public void onLogin(View view) {
		
		EditText userName = (EditText) findViewById(R.id.userText);
		EditText password = (EditText) findViewById(R.id.passText);
		
		mUserName = userName.getText().toString();
		mPassword = password.getText().toString(); 
		// Validation
		
		loginSent();
	}
	
	public void loginSent() {
		
		if ( mUserName.isEmpty() || mPassword.isEmpty() ) {
			// need a layout for info
			// maybe a existing one
			System.out.println("Field Empty!");
			
			// test dialog
			myDialog("Field Empty", "Please enter your email and password");
			
			return;
		}
		
		// Authentication
		DataLogin data = new DataLogin(mUserName, mPassword);
		Gson gson = new GsonBuilder().create();
		String params = gson.toJson(data);
		
		SingletonNetworkStatus.getInstance().clean();
		SingletonNetworkStatus.getInstance().setActivity(this);
		SingletonNetworkStatus.getInstance().setDoNotDismissDialog(true);
		String path = "/api/users/login.json";
		new HttpConnection().access(this, path, params, "POST");
		
	}

	public void postLoginGetLabels() {
		String path = "/api/labels.json?" + SingletonLoginData.getInstance().getPostParam();
		new HttpConnection().access(this, path, "", "GET");
	}

	public void postLoginGetProfiles() {
		String path = "/api/profiles.json?" + SingletonLoginData.getInstance().getPostParam();
		new HttpConnection().access(this, path, "", "GET");
	}
	
	public void postLoginGetContacts() {
		String path = "/api/contactsDetails.json?" + SingletonLoginData.getInstance().getPostParam();
		new HttpConnection().access(this, path, "", "GET");
	}
	
	public void postGetRegistrationID() {
		
		// sample code testing
        context = getApplicationContext();

        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);
            
            if (regid.isEmpty()) {
                registerInBackground();
            } else {
            	SingletonLoginData.getInstance().setRegistrationID(regid);
            }
        } else {
            System.out.println("No valid Google Play Services APK found.");
            return;
        }
		
		// end of testing
		
	}
	
	public void postLoginGetRegister() {
		
		if ( regid == null || regid.isEmpty() ) {
			// wait for regid;
			System.out.println("Something is wrong with regid!!!!");
		}
		
		// Device information
		String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		String info = "device_id:" + deviceId + ",made:" +
		Build.MANUFACTURER + ",model:" + Build.MODEL + ",sdk:" +
		Build.VERSION.SDK_INT + ",release:" + Build.VERSION.RELEASE +  ",density:" + 
		getResources().getDisplayMetrics().density + ",dpi:" +
		getResources().getDisplayMetrics().densityDpi + ",width:" +
		getResources().getDisplayMetrics().widthPixels + ",height:" +
		getResources().getDisplayMetrics().heightPixels;

		DeviceRegistrationRequest data = new DeviceRegistrationRequest();
		data.setDeviceId(regid);
		data.setClientInfo(info);
		data.setStaticId(deviceId);
		data.setDeviceType(DeviceType.ANDROID);
		data.setUserPreference(true);
		Gson gson = new GsonBuilder().create();
		String params = gson.toJson(data);

		String path = "/api/device/register.json?" + SingletonLoginData.getInstance().getPostParam();
		new HttpConnection().access(this, path, params, "POST");
	}
	
	// copy from sample code

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                System.out.println("This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        System.out.println("Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            System.out.println("Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            System.out.println("App version changed.");
            return "";
        }
        System.out.println("Registration ID is [" + registrationId + "]");
        return registrationId;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
      // Your implementation here.
    }    
    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                	SingletonLoginData.getInstance().setRegistrationID(regid);

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                System.out.println("My Registration ID: [" + msg + "]");
            }
        }.execute(null, null, null);
    }
	
	// end of sample code
	
    public void loadLoginData() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		mUserName = sharedPref.getString(getString(R.string.login_username), null);
		mPassword = sharedPref.getString(getString(R.string.login_password), null);
		
    }
    
    public void saveLoginData() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.login_username), mUserName);
		editor.putString(getString(R.string.login_password), mPassword);
		editor.commit();
		
    }
    
	public void onPostNetwork () {
		
		if ( mloginStep == 0 && SingletonNetworkStatus.getInstance().getCode() != 200 ) {
    		SingletonNetworkStatus.getInstance().setDoNotDismissDialog(false);
        	SingletonNetworkStatus.getInstance().getWaitDialog().dismiss();
        	SingletonNetworkStatus.getInstance().setWaitDialog(null);
    		
			myDialog(SingletonNetworkStatus.getInstance().getMsg(), 
					SingletonNetworkStatus.getInstance().getErrMsg());
			
			
			return;
		}
		
		saveLoginData();
		
		if ( mloginStep == 0 && isGCMRegister ) {
			isGCMRegister = false;
			postGetRegistrationID();
		}
		
		if ( mloginStep == 0 ) {
			Gson gson = new GsonBuilder().create();
			DataLoginData obj = gson.fromJson(SingletonNetworkStatus.getInstance().getJson(), DataLoginData.class);
			SingletonLoginData.getInstance().setAccessToken(obj.accessToken);
			SingletonLoginData.getInstance().setUserDTO(obj.userDTO);
			mloginStep ++;
			postLoginGetProfiles();
			return;
		}
		
		if ( mloginStep == 1 ) { // getting profiles
        	Type collectionType = new TypeToken<Collection<ProfileDTO>>(){}.getType();
        	Gson gson = new GsonBuilder().create();
        	Collection<ProfileDTO> profiles = gson.fromJson(SingletonNetworkStatus.getInstance().getJson(), collectionType);
        	SingletonLoginData.getInstance().setUserProfiles(new ArrayList<ProfileDTO>(profiles));
        	loadProfileImages();
        	mloginStep ++;

        	postLoginGetRegister();
        	return;
		}
		
		if ( mloginStep == 2 ) { // getting label list
			postLoginGetLabels();
			mloginStep ++;
			return;
		}
		
		if ( mloginStep == 3 ) { // getting contact list
			// load labels
        	Type collectionType = new TypeToken<Collection<String>>(){}.getType();
        	Gson gson = new GsonBuilder().create();
        	Collection<String> labels = gson.fromJson(SingletonNetworkStatus.getInstance().getJson(), collectionType);
        	SingletonLoginData.getInstance().setLabels(new ArrayList<String>(labels));
			
			// do the fromJson call in gen_contact;
			SingletonLoginData.getInstance().setIsContactLoaded(true);
			mloginStep++;

        	// this should be the last one.
        	// should call this before 
    		SingletonNetworkStatus.getInstance().setDoNotDismissDialog(false);
        	
    		postLoginGetContacts();
			return;
		}
		
		// Bring up the LeftDeck
		Intent it = new Intent(this, LeftDeckActivity.class);
		startActivity(it);
		
	}
	
	public void onForgot(View view) {
		Intent it = new Intent(this, LoginForgotActivity.class);
		startActivity(it);
		overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
	}

	public void onRegister(View view) {
		Intent it = new Intent(this, LoginRegisterActivity.class);
		startActivity(it);
		overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
		
	}
	
	public void loadProfileImages() {
		ArrayList<ProfileDTO> data = SingletonLoginData.getInstance().getUserProfiles();
		for ( int position = 0; position < data.size(); position ++ ) 
		{
		   ProfileDTO item = data.get(position);
		   if ( item.getUserProfile().getPathToImage() != null &&
				item.getUserProfile().getPathToImage() != ""  &&
				SingletonLoginData.getInstance().getBitmap(position) == null)  
		   {
			   loadImage(position, item.getUserProfile().getPathToImage());
		   }		
		}
	}
	
	// test loading profile images
	public void loadImage(int index, String photo_url_str) {
		 System.out.println("Loading image from " + photo_url_str);
		 new DownloadImageTask().execute(photo_url_str, Integer.toString(index));		 
	 }
	 
	 private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> 
	 {
		    int position;

		    public DownloadImageTask() {
		    }

		    protected Bitmap doInBackground(String... urls) {
		        String urldisplay = urls[0];
		        position = Integer.parseInt(urls[1]);
		        Bitmap mIcon11 = null;
		        try {
		            InputStream in = new java.net.URL(urldisplay).openStream();
		            mIcon11 = BitmapFactory.decodeStream(in);
		        } catch (Exception e) {
		            System.out.println("Error" + e.getMessage());
		        }
		        return mIcon11;
		    }

		    protected void onPostExecute(Bitmap result) {
		    	SingletonLoginData.getInstance().setBitmap(position, result);
		    }
	}
	 

}
