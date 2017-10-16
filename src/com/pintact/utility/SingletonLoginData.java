package com.pintact.utility;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.graphics.Bitmap;

import com.pintact.data.ContactDTO;
import com.pintact.data.ContactShareRequest;
import com.pintact.data.GroupDTO;
import com.pintact.data.NotificationDTO;
import com.pintact.data.PageDTO;
import com.pintact.data.ProfileDTO;
import com.pintact.data.SignupRequest;
import com.pintact.data.UserSettings;

public class SingletonLoginData {

	String accessToken;
	String registrationID;
	DataUserDTO userData;
	GroupDTO curGroup;
	ProfileDTO mergedProfile;
	ProfileDTO introducedProfile;
	PageDTO<NotificationDTO> notifications;
	ArrayList<ProfileDTO> userProfiles;
	ArrayList<ContactDTO> contactList;
	ArrayList<ContactDTO> cloudContactList;
	ArrayList<ContactDTO> localContactList;
	ArrayList<ContactDTO> labelContactList;
	ArrayList<GroupDTO> userGroups;
	ArrayList<GroupDTO> createdGroups;
	ArrayList<GroupDTO> joinedGroups;
	ArrayList<String> labels;
	ArrayList<String> contactLabels;
	SignupRequest signupRequest;
	UserSettings userSettings;
	ContactShareRequest contactShareRequest;
	boolean isContactLoaded = false;
	boolean isStatusChanged = false;
	
	@SuppressLint("UseSparseArrays")
	HashMap<Integer, Bitmap> profImages = new HashMap<Integer, Bitmap> ();
	
	static SingletonLoginData instance = null;
	private SingletonLoginData () {
		// initialize to avoid null point access
		contactList = new ArrayList<ContactDTO>();
		labelContactList = new ArrayList<ContactDTO>();
		localContactList = new ArrayList<ContactDTO>();
		cloudContactList = new ArrayList<ContactDTO>();
		userProfiles = new ArrayList<ProfileDTO>();
		notifications = new PageDTO<NotificationDTO>();
		labels = new ArrayList<String>();
		contactLabels = new ArrayList<String>();
		userGroups = new ArrayList<GroupDTO>();
		createdGroups = new ArrayList<GroupDTO>();
		joinedGroups = new ArrayList<GroupDTO>();
		userSettings = new UserSettings();
	}
	
	public boolean getIsContactLoaded () { return isContactLoaded; }
	public void setIsContactLoaded( boolean b) { isContactLoaded = b; }
	
	public boolean getIsStatusChanged () { return isStatusChanged; }
	public void setIsStatusChanged( boolean b) { isStatusChanged = b; }
	
	public Bitmap getBitmap(int index) { return profImages.get(index); }
	public void setBitmap( int index, Bitmap bm ) { profImages.put(index, bm); }
	
	public String getAccessToken() { return accessToken; }
	public void setAccessToken( String s ) { accessToken = s; }
	
	public String getRegistrationID() { return registrationID; }
	public void setRegistrationID( String s ) { registrationID = s; }

	public UserSettings getUserSettings() { return userSettings; }
	public void setUserSettigns( UserSettings s ) { if ( s != null ) userSettings = s; else userSettings = new UserSettings(); }
	
	public ProfileDTO getMergedProfile() { return mergedProfile; }
	public void setMergedProfile( ProfileDTO d ) { if ( d != null ) mergedProfile = d; else mergedProfile = new ProfileDTO(); }

	public GroupDTO getCurGroup() { return curGroup; }
	public void setCurGroup( GroupDTO d ) { if ( d != null ) curGroup = d; else curGroup = new GroupDTO();  }

	public ProfileDTO getIntroducedProfile() { return introducedProfile; }
	public void setIntroducedProfile( ProfileDTO d ) { if ( d != null ) introducedProfile = d; else introducedProfile = new ProfileDTO (); }

	public DataUserDTO getUserData() { return userData; }
	public void setUserDTO( DataUserDTO d ) { if ( d != null ) userData = d; else userData = new DataUserDTO(); }

	public SignupRequest getSignupRequest() { return signupRequest; }
	public void setSignupRequest( SignupRequest d ) { if ( d != null ) signupRequest = d; else signupRequest = new SignupRequest(); }

	public ContactShareRequest getContactShareRequest() { return contactShareRequest; }
	public void setContactShareRequest( ContactShareRequest d ) { if ( d != null ) contactShareRequest = d; else contactShareRequest = new ContactShareRequest(); }

	public PageDTO<NotificationDTO> getNotifications() { return notifications; }
	public void setNotifications( PageDTO<NotificationDTO> d ) { if ( d != null ) notifications = d; else notifications = new PageDTO<NotificationDTO>(); }
	
	public ArrayList<ProfileDTO> getUserProfiles() { return userProfiles; }
	public void setUserProfiles( ArrayList<ProfileDTO> d ) { if ( d != null ) userProfiles = d; else userProfiles = new ArrayList<ProfileDTO>(); }
	
	public ArrayList<ContactDTO> getContactList() { return contactList; }
	public void setContactList( ArrayList<ContactDTO> d ) { if ( d != null ) contactList = d; else contactList = new ArrayList<ContactDTO>(); }
	
	public ArrayList<ContactDTO> getLocalContactList() { return localContactList; }
	public void setLocalContactList( ArrayList<ContactDTO> d ) { if ( d != null ) localContactList = d; else localContactList = new ArrayList<ContactDTO>(); }
	
	public ArrayList<ContactDTO> getLabelContactList() { return labelContactList; }
	public void setLabelContactList( ArrayList<ContactDTO> d ) { if ( d != null ) labelContactList = d; else labelContactList = new ArrayList<ContactDTO>(); }
	
	public ArrayList<ContactDTO> getCloudContactList() { return cloudContactList; }
	public void setCloudContactList( ArrayList<ContactDTO> d ) { if ( d != null ) cloudContactList = d; else cloudContactList = new ArrayList<ContactDTO>(); }
	
	public ArrayList<String> getLabels() { return labels; }
	public void setLabels( ArrayList<String> d ) { if ( d != null ) labels = d; else labels = new ArrayList<String>(); }
	
	public ArrayList<String> getContactLabels() { return contactLabels; }
	public void setContactLabels( ArrayList<String> d ) { if ( d != null ) contactLabels = d; else contactLabels = new ArrayList<String>(); }
	
	public ArrayList<GroupDTO> getGroups() { return userGroups; }
	public void setGroups( ArrayList<GroupDTO> d ) { if ( d != null ) userGroups = d; else userGroups = new ArrayList<GroupDTO>(); }
	
	public ArrayList<GroupDTO> getCreatedGroups() { return createdGroups; }
	public void setCreatedGroups( ArrayList<GroupDTO> d ) { if ( d != null ) createdGroups = d; else createdGroups = new ArrayList<GroupDTO>(); }
	
	public ArrayList<GroupDTO> getJoinedGroups() { return joinedGroups; }
	public void setJoinedGroups( ArrayList<GroupDTO> d ) { if ( d != null ) joinedGroups = d; else joinedGroups = new ArrayList<GroupDTO>(); }
	
	public static SingletonLoginData getInstance() {
		if ( instance == null )
			instance = new SingletonLoginData();
		
		return instance;
	}
	
	public String getPostParam() {
		
    	String stringUrl = "";
    	try {
    		stringUrl = "userId=" + URLEncoder.encode(userData.id, "UTF-8") +
    				    "&accessToken=" + URLEncoder.encode(accessToken, "UTF-8"); 
		} catch (Exception e) {
			System.out.println("wrong");
		}

    	return stringUrl;
	}
	
	public void clean() {
		if ( profImages != null)
			profImages.clear();
		if ( userProfiles != null)
			userProfiles.clear();
		if ( contactList != null )
			contactList.clear();
		if ( labelContactList != null )
			labelContactList.clear();
		if ( localContactList != null )
			localContactList.clear();
		if ( cloudContactList != null )
			cloudContactList.clear();
		if ( labels != null )
			labels.clear();
		if ( contactLabels != null )
			contactLabels.clear();
		if ( userGroups != null )
			userGroups.clear();
		if ( createdGroups != null )
			createdGroups.clear();
		if ( joinedGroups != null )
			joinedGroups.clear();
		
		accessToken = "";
	}
	
	public NotificationManager mNotificationManager;
}
