package com.pintact;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pintact.contact.ContactAddActivity;
import com.pintact.contact.ContactFindActivity;
import com.pintact.contact.ContactInviteActivity;
import com.pintact.contact.ContactMainLVAdapter;
import com.pintact.data.AttributeType;
import com.pintact.data.ContactDTO;
import com.pintact.data.ContactShareRequest;
import com.pintact.data.EventType;
import com.pintact.data.GroupDTO;
import com.pintact.data.NotificationDTO;
import com.pintact.data.PageDTO;
import com.pintact.data.ProfileDTO;
import com.pintact.data.UserProfile;
import com.pintact.data.UserProfileAddress;
import com.pintact.data.UserProfileAttribute;
import com.pintact.group.GroupContactsActivity;
import com.pintact.group.GroupPinActivity;
import com.pintact.group.GroupProfileShareActivity;
import com.pintact.label.LabelContactsActivity;
import com.pintact.leftdeck.searchResult;
import com.pintact.leftdeck.searchResultAdapter;
import com.pintact.notification.NotifyInviteLVAdapter;
import com.pintact.profile.ProfileContentData;
import com.pintact.profile.ProfileExpLVAdapter;
import com.pintact.profile.ProfileViewNewActivity;
import com.pintact.profile.ProfileGridAdapter;
import com.pintact.setting.SettingSortActivity;
import com.pintact.utility.HttpConnection;
import com.pintact.utility.MyActivity;
import com.pintact.utility.SingletonLoginData;
import com.pintact.utility.SingletonNetworkStatus;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

/**
 * This example illustrates a common usage of the DrawerLayout widget
 * in the Android support library.
 * <p/>
 * <p>When a navigation (left) drawer is present, the host activity should detect presses of
 * the action bar's Up affordance as a signal to open and close the navigation drawer. The
 * ActionBarDrawerToggle facilitates this behavior.
 * Items within the drawer should fall into one of two categories:</p>
 * <p/>
 * <ul>
 * <li><strong>View switches</strong>. A view switch follows the same basic policies as
 * list or tab navigation in that a view switch does not create navigation history.
 * This pattern should only be used at the root activity of a task, leaving some form
 * of Up navigation active for activities further down the navigation hierarchy.</li>
 * <li><strong>Selective Up</strong>. The drawer allows the user to choose an alternate
 * parent for Up navigation. This allows a user to jump across an app's navigation
 * hierarchy at will. The application should treat this as it treats Up navigation from
 * a different task, replacing the current task stack using TaskStackBuilder or similar.
 * This is the only form of navigation drawer that should be used outside of the root
 * activity of a task.</li>
 * </ul>
 * <p/>
 * <p>Right side drawers should be used for actions, not navigation. This follows the pattern
 * established by the Action Bar that navigation should be to the left and actions to the right.
 * An action should be an operation performed on the current contents of the window,
 * for example enabling or disabling a data overlay on top of the current content.</p>
 */
public class LeftDeckActivity extends MyActivity {
	static final int OPTION_PROFILE = 0;
	static final int OPTION_SETTING = 1;
	static final int OPTION_CONTACT = 2;
	static final int OPTION_GROUP   = 3;
	static final int OPTION_LABEL   = 4;
	static final int OPTION_NOTIFY  = 5;
	public static String SELECTED_OPTIONS = "leftdeck_selected_option";
	
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;
    
	View tableView, searchView;
	int  tableIndex;
	boolean  isFirstTime = true;
	boolean  isSearchPage = false;
	int  labelOp = 0;
    ArrayList<View> mSubLabelItems;
	View mSubLabelView;
	View mAddView; // for label
	View leftDeckView;
	
	// for loading
	int mSelectedItem = OPTION_CONTACT;
	boolean isLoggingOut = false;
	boolean isLoadingLabel = false;
	boolean isLoadingGroup = false;
	boolean isQueryJoinedGroup = false;
	
	// for reject 
	int mRejectStep = 0; 
	int mNotifyIndex = 0;

    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leftdeck_navigation_main);
        setupUI(findViewById(R.id.drawer_layout));
        hideRight();
        
        loadPreferences();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        leftDeckView = (View) findViewById(R.id.left_drawer);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
            	hideRight();
            	((MyActivity)drawerView.getContext()).hideSoftKeyboard((MyActivity)drawerView.getContext());
                getActionBar().setTitle("Menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    	// go back to login page if not register yet
    	String accessToken = SingletonLoginData.getInstance().getAccessToken();
    	if ( accessToken == null || accessToken.isEmpty() || accessToken.length() < 1) {
    		Intent it = new Intent(this, MainActivity.class);
    		startActivity(it);
    	}
        
        Bundle bundle = getIntent().getExtras();
        if ( bundle != null ) 
        {
        	selectItem(bundle.getInt(SELECTED_OPTIONS));
        
        	// cancel all notifications if any
        	if ( SingletonLoginData.getInstance().mNotificationManager != null ) {
        		SingletonLoginData.getInstance().mNotificationManager.cancelAll();
        		SingletonLoginData.getInstance().mNotificationManager = null;
        	}
        } else if (savedInstanceState == null) {
            selectItem(OPTION_CONTACT);
        }
    	
		ImageView abIV = (ImageView)findViewById(R.id.actionBarMenu);
		abIV.setClickable(true);
		abIV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if ( mDrawerLayout.isDrawerOpen(leftDeckView) ) {
            		mDrawerLayout.closeDrawer(leftDeckView);
            	}
            	else {
            		mDrawerLayout.openDrawer(leftDeckView);
            	}
            }
        });

		TextView tvAdd = (TextView)findViewById(R.id.ldm_add_pintact);
		tvAdd.setClickable(true);
		tvAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onAddContact();
			}
		});
    }

    @Override
    public void onResume() {
    	super.onResume();
    	System.out.println("Onresume - Activity.");
    }
    
    @Override
    public void onBackPressed() {
    	moveTaskToBack(true);
    	System.out.println("OnBackPressed - Activity.");
    	return;
    }
    
    public void onAddContact() {
    	Intent myIntent = new Intent(this, ContactAddActivity.class);
        startActivity(myIntent); 
    	
    }
    
    public void setLabelOpPost(int op, View v, View subV, ArrayList<View> list) {
    	labelOp = op;
    	mSubLabelView = v;
    	mAddView = subV;
    	mSubLabelItems = list;
    }
    
    public void onAcceptInvite(View v) {
    	
    	String value = ((TextView)((View)v.getParent()).findViewById(R.id.for_data)).getText().toString();
    	System.out.println("Activity Accept clicked - value " + value);

    	int index = Integer.valueOf(value);
    	ContactShareRequest req = new ContactShareRequest();
    	String destId = SingletonLoginData.getInstance().getNotifications().getData().get(index).getData().sender.id;
		req.setDestinationUserId(Long.parseLong(destId));
		req.setSourceUserId(Long.parseLong(SingletonLoginData.getInstance().getUserData().id));
		SingletonLoginData.getInstance().setContactShareRequest(req);
    	
    	Intent myIntent = new Intent(this, GroupProfileShareActivity.class);
    	myIntent.putExtra(GroupProfileShareActivity.ARG_PROFILE_SHARE, index);
        startActivity(myIntent); 
    	
    	
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
                        rejectInvite();
                    }
                });
        builder.setNegativeButton("NO", 
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,
                    int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
	}		
	
    
    public void onRejectInvite(View v) {
    	
    	String value = ((TextView)((View)v.getParent()).findViewById(R.id.for_data1)).getText().toString();
    	System.out.println("Activity Rejected clicked - value " + value);
    	mNotifyIndex = Integer.valueOf(value);
    	
    	alertDialog("Confirm Rejection", "Are you sure you want to reject this invitation?");
    }
    
    public void rejectInvite() {
    	
    	int index = mNotifyIndex;
    	mRejectStep = 1;
    	String destId = SingletonLoginData.getInstance().getNotifications().getData().get(index).getData().sender.id;
		String path = "/api/contacts/" + destId + "/reject.json?" + SingletonLoginData.getInstance().getPostParam();

		SingletonNetworkStatus.getInstance().clean();
		SingletonNetworkStatus.getInstance().setDoNotDismissDialog(true);
		SingletonNetworkStatus.getInstance().setActivity(this);
		new HttpConnection().access(this, path, "", "POST");
    	
    }
    
    public void sendNotification(String msg) {
    	NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

    	Intent it = new Intent(this, LeftDeckActivity.class);
    	it.putExtra(LeftDeckActivity.SELECTED_OPTIONS, OPTION_NOTIFY);
    	// add the following line would show Pintact to the preview page.
    	// it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("Pintact Update")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(1, mBuilder.build());
    }
    
	public void logout() {
		isLoggingOut = true;
		String path = "/api/users/logout.json?" + SingletonLoginData.getInstance().getPostParam();
		SingletonNetworkStatus.getInstance().setActivity(this);
		new HttpConnection().access(this, path, "", "POST");
	}
    
	public void loadingLabel(String label) {
		isLoadingLabel = true;
		String path = "/api/contacts/labels/" + Uri.encode(label, "utf-8") + ".json?" + SingletonLoginData.getInstance().getPostParam();
		SingletonNetworkStatus.getInstance().setActivity(this);
		new HttpConnection().access(this, path, "", "GET");
	}
    
	public void loadingGroup(String group) {
		isLoadingGroup = true;
		String path = "/api/group/" + Uri.encode(group, "utf-8") + "/members.json?" + SingletonLoginData.getInstance().getPostParam();
		SingletonNetworkStatus.getInstance().setActivity(this);
		new HttpConnection().access(this, path, "", "GET");
	}
    
    private List<searchResult> getData() {
        List<searchResult> list = new ArrayList<searchResult>();
        list.add(new searchResult("Maria Stenson", "Brand Evangelist, Center"));
        list.add(new searchResult("Morgan Stanley", "Investment Banking, NYC"));
        list.add(new searchResult("Maria Stenson", "Brand Evangelist, Center"));
        list.add(new searchResult("Morgan Stanley", "Investment Banking, NYC"));
        return list;    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
    	/// COMMENT  boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
    	//boolean drawerOpen = mDrawerLayout.isDrawerOpen(leftDeckView);
        
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("NewApi")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        /*
        case R.id.action_websearch:
            // create intent to perform web search for this planet
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
            // catch event that there's no activity to handle intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
            }
            return true;
            */
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    /*
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    */

    @SuppressLint("NewApi")
	private void selectItem(int position) {
    	
    	mSelectedItem = position;

    	// do network access
    	// construct URL
    	String path = "";
		SingletonNetworkStatus.getInstance().setActivity(this);
    	switch (position) {
    		case OPTION_CONTACT:
    			if ( SingletonLoginData.getInstance().getIsContactLoaded() ) {
    				SingletonLoginData.getInstance().setIsContactLoaded(false);
    				onPostNetwork();
    			} else if ( SingletonLoginData.getInstance().getCloudContactList().size() < 10 ) {
		    		path = "/api/contactsDetails.json?" + SingletonLoginData.getInstance().getPostParam();
		    		new HttpConnection().access(this, path, "", "GET");
    			} else 
    				onPostNetwork();
	    		break;
    	
	    	case OPTION_PROFILE:
	    		path = "/api/profiles.json?" + SingletonLoginData.getInstance().getPostParam();
	    		new HttpConnection().access(this, path, "", "GET");
	    		break;
	    		
	    	case OPTION_NOTIFY:
	    		path = "/api/sortedNotifications.json?" + SingletonLoginData.getInstance().getPostParam();
	    		new HttpConnection().access(this, path, "", "GET");
	    		break;
	    		
	    	case OPTION_LABEL:
	    		path = "/api/labels.json?" + SingletonLoginData.getInstance().getPostParam();
	    		new HttpConnection().access(this, path, "", "GET");
	    		break;
	    		
	    	case OPTION_GROUP:
	    		path = "/api/groupMemberships.json?" + SingletonLoginData.getInstance().getPostParam();
	    		isQueryJoinedGroup = true;
	    		SingletonNetworkStatus.getInstance().setDoNotDismissDialog(true);
	    		new HttpConnection().access(this, path, "", "GET");
	    		break;
	    		
	    	default:
	    		onPostNetwork();
	    		break;
    	}
    	
    }
    
    @SuppressLint("NewApi")
	public void onPostNetwork() {
    	
    	int position = mSelectedItem;
    	
		if ( SingletonNetworkStatus.getInstance().getCode() != 200 ) {
			myDialog(SingletonNetworkStatus.getInstance().getMsg(), 
					SingletonNetworkStatus.getInstance().getErrMsg());
			
			if ( labelOp > 0 )
				labelOp = 0;
			
			if ( mRejectStep > 0 )
				mRejectStep = 0;
			
			if ( isLoadingLabel ) {
				isLoadingLabel = false;
			}
			
			if ( isLoadingGroup ) {
				isLoadingGroup = false;
			}
			
			return;
		}
		
		if ( isLoadingLabel ) {
			isLoadingLabel = false;
			
			// get the data
        	Type collectionType = new TypeToken<Collection<ContactDTO>>(){}.getType();
        	Gson gson = new GsonBuilder().create();
        	Collection<ContactDTO> contacts = gson.fromJson(SingletonNetworkStatus.getInstance().getJson(), collectionType);
        	SingletonLoginData.getInstance().setLabelContactList(new ArrayList<ContactDTO>(contacts));
			
	    	Intent myIntent = new Intent(this, LabelContactsActivity.class);
	        startActivity(myIntent); 
        	
        	return;
		}
    	
		if ( isLoadingGroup ) {
			isLoadingGroup = false;
			
			// get the data
        	Type collectionType = new TypeToken<Collection<ContactDTO>>(){}.getType();
        	Gson gson = new GsonBuilder().create();
        	Collection<ContactDTO> contacts = gson.fromJson(SingletonNetworkStatus.getInstance().getJson(), collectionType);
        	SingletonLoginData.getInstance().setLabelContactList(new ArrayList<ContactDTO>(contacts));
			
	    	Intent myIntent = new Intent(this, GroupContactsActivity.class);
	        startActivity(myIntent); 
        	
        	return;
		}
		
		if ( isQueryJoinedGroup ) {
			isQueryJoinedGroup = false;
			
        	Type collectionType = new TypeToken<Collection<GroupDTO>>(){}.getType();
        	Gson gson = new GsonBuilder().create();
        	Collection<GroupDTO> groups = gson.fromJson(SingletonNetworkStatus.getInstance().getJson(), collectionType);
        	SingletonLoginData.getInstance().setJoinedGroups(new ArrayList<GroupDTO>(groups));

			// query joined groups
    		SingletonNetworkStatus.getInstance().setDoNotDismissDialog(false);
    		String path = "/api/groupPins.json?" + SingletonLoginData.getInstance().getPostParam();
    		new HttpConnection().access(this, path, "", "GET");
    		
    		return;
		}
    	
    	if ( position == OPTION_SETTING && isLoggingOut ) {
    		// clear data
    		SingletonLoginData.getInstance().clean();
    		SingletonNetworkStatus.getInstance().clean();
    		
    		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    		SharedPreferences.Editor editor = sharedPref.edit();
    		editor.putString(getString(R.string.login_username), null);
    		editor.putString(getString(R.string.login_password), null);
    		editor.commit();    		
    		// return to login page
    		Intent intent = new Intent(this, MainActivity.class);
    		startActivity(intent); 
    	}
    	
    	// for Add/delete labels
    	if ( labelOp > 0 ) {
    		
    		if ( labelOp ==  1 ) // Add label
    		{
				EditText inputET = (EditText)mAddView.findViewById(R.id.label_input);
				TextView labelTV = (TextView)mAddView.findViewById(R.id.label_view);
				TextView addTV = (TextView)mAddView.findViewById(R.id.view_add);
				String label = inputET.getText().toString();
				labelTV.setText(label);
				inputET.setVisibility(View.GONE);
				labelTV.setVisibility(View.VISIBLE);
				addTV.setVisibility(View.INVISIBLE);
				
				RelativeLayout rlo = (RelativeLayout) mSubLabelView.findViewById(R.id.lm_clkLO);
				rlo.setClickable(true);
	    	    mSubLabelItems.add(mAddView);
	    	    SingletonLoginData.getInstance().getLabels().add(label);
    		}
    		
    		if ( labelOp == 2 ) // remove label
    		{
				LinearLayout container = (LinearLayout)mSubLabelView.findViewById(R.id.lm_llo);
				TextView labelTV = (TextView)mAddView.findViewById(R.id.label_view);
				String label = labelTV.getText().toString();
	    	    SingletonLoginData.getInstance().getLabels().remove(label);
				
				container.removeView(mAddView);
				mSubLabelItems.remove(mAddView);
    		}
    		
    		labelOp = 0;
    		return;
    	}

		// for reject INVITES/INTRODUCES
    	if ( mRejectStep > 0 ) 
    	{
			if ( mRejectStep == 1 ) {
				String path = "/api/notifications/" +
						SingletonLoginData.getInstance().getNotifications().getData().get(mNotifyIndex).getNotificationId() +
						"/seen.json?" + SingletonLoginData.getInstance().getPostParam();
				new HttpConnection().access(this, path, "", "POST");
				mRejectStep ++;
				return;
			}
			
			if ( mRejectStep == 2 ) {
				
				// this should be the last one
	    		SingletonNetworkStatus.getInstance().setDoNotDismissDialog(false);
				
	    		String path = "/api/sortedNotifications.json?" + SingletonLoginData.getInstance().getPostParam();
	    		new HttpConnection().access(this, path, "", "GET");
	    		mRejectStep ++;
	    		return;
			}
			
			if ( mRejectStep == 3 ) {
				// update notification
	        	Type collectionType = new TypeToken<PageDTO<NotificationDTO>>(){}.getType();
	        	Gson gson = new GsonBuilder().create();
	        	PageDTO<NotificationDTO> notifications = gson.fromJson(SingletonNetworkStatus.getInstance().getJson(), collectionType);
	        	SingletonLoginData.getInstance().setNotifications(notifications);
			}
			
			mRejectStep = 0;
    	}
    	
    	
        // update the main content by replacing fragments
        Fragment fragment = new PlanetFragment();
        Bundle args = new Bundle();
        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
      /// COMMENT mDrawerList.setItemChecked(position, true);
        TextView tv = (TextView)findViewById(R.id.actionBar);
        switch ( position ) {
        	case OPTION_PROFILE:
        		tv.setText(getResources().getString(R.string.ab_profile));
        		break;
        	case OPTION_SETTING:
        		tv.setText(getResources().getString(R.string.ab_setting));
        		break;
        	case OPTION_CONTACT:
        		tv.setText(getResources().getString(R.string.ab_contact_all));
        		break;
        	case OPTION_GROUP:
        		tv.setText(getResources().getString(R.string.ab_group_pin));
        		break;
        	case OPTION_LABEL:
        		tv.setText(getResources().getString(R.string.ab_label));
        		break;
        	case OPTION_NOTIFY:
        		tv.setText(getResources().getString(R.string.ab_notify));
        		break;
        		
        }
        
      /// COMMENT mDrawerLayout.closeDrawer(mDrawerList);
        mDrawerLayout.closeDrawer(leftDeckView);
    }

    @SuppressLint("NewApi")
	@Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
	public void onIconClicked(View v) {
		return;
	}
	
	public void onNoteClicked(View v) {

		return;
	}
	
	public void onPhoneClicked(View v) {
		return;
	}

	public void onLabelClicked(View v) {
		return;
	}

	public void onFeedBackClicked(View v) {
		TextView tv = (TextView)findViewById(R.id.set_feedback);
		tv.setText(getResources().getString(R.string.android_res_qualifier));
		return;
	}

	public void onMenuClicked(View v) {
        
        TextView tv = (TextView) v;
        if ( tv.getText() == getResources().getString(R.string.ldm_profile))
        	selectItem(OPTION_PROFILE);
        else if ( tv.getText() == getResources().getString(R.string.ldm_setting))
        	selectItem(OPTION_SETTING);
        else if ( tv.getText() == getResources().getString(R.string.ldm_contact))
        	selectItem(OPTION_CONTACT);
        else if ( tv.getText() == getResources().getString(R.string.ldm_group))
        	selectItem(OPTION_GROUP);
        else if ( tv.getText() == getResources().getString(R.string.ldm_label))
        	selectItem(OPTION_LABEL);
        else if ( tv.getText() == getResources().getString(R.string.ldm_notify))
        	selectItem(OPTION_NOTIFY);
        
		return;
	}
	
	/*
    private List<LeftDeckData> getLeftDeckGroup() {
        List<LeftDeckData> list = new ArrayList<LeftDeckData>();
        list.add(new LeftDeckData(R.drawable.left_row1_1, getResources().getString(R.string.left_deck_notify), R.drawable.left_row1_2));
        list.add(new LeftDeckData(R.drawable.left_row2_1, getResources().getString(R.string.left_deck_group), -1));
        list.add(new LeftDeckData(R.drawable.left_row3_1, getResources().getString(R.string.left_deck_label), R.drawable.left_row3_2_up));
        list.add(new LeftDeckData(R.drawable.left_row4_1, getResources().getString(R.string.left_deck_profile), -1));
        list.add(new LeftDeckData(R.drawable.left_row5_1, getResources().getString(R.string.left_deck_setting), -1));
        return list;    	
    }

    private List<LeftDeckData> getLeftDeckChild() {
        List<LeftDeckData> list = new ArrayList<LeftDeckData>();
        list.add(new LeftDeckData(R.drawable.left_row3_s1, getResources().getString(R.string.left_deck_label1), R.drawable.left_row3_s2));
        list.add(new LeftDeckData(R.drawable.left_row3_s1, getResources().getString(R.string.left_deck_label2), R.drawable.left_row3_s2));
        list.add(new LeftDeckData(R.drawable.left_row3_s3, getResources().getString(R.string.left_deck_label3), -1));
        
        return list;    	
    }
    */
	
	public void searchButtonClicked(View v) {
		EditText et = (EditText)findViewById(R.id.TopsearchEdit);
		
		//String testStr = "Quick Test";
		boolean isLeft = isSearchPage;
		
		ViewGroup parent;
		if ( isFirstTime ) {
			isFirstTime = false;
		    tableView = findViewById(R.id.leftTableLayout);
		    parent = (ViewGroup) tableView.getParent();
		    tableIndex = parent.indexOfChild(tableView);
		    
		    //searchView = getLayoutInflater().inflate(R.layout.search_left_deck, parent, false);
		    searchView = getLayoutInflater().inflate(R.layout.search_left_deck, parent, false);
		    
	        // init search result panel
	        ListView lv = (ListView) searchView.findViewById(R.id.searchListView);
	        searchResultAdapter adapter = new searchResultAdapter(this, getData());
	        lv.setAdapter(adapter);
		    
		}

		if ( isLeft ) {
		    parent = (ViewGroup) searchView.getParent();
			parent.removeView(searchView);
			parent.addView(tableView, tableIndex);
			et.setText("");
			et.setHint("search your Pintacts");
		} else {
		    parent = (ViewGroup) tableView.getParent();
			parent.removeView(tableView);
			parent.addView(searchView, tableIndex);
			//et.setText(testStr);
		}
		
		isSearchPage = !isSearchPage;

	}
	
    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    @SuppressLint("NewApi")
	public static class PlanetFragment extends Fragment {
        public static final String ARG_PLANET_NUMBER = "planet_number";
        ExpandableListView expListView;
        View mLabelView;
        ArrayList<View> mLabelItems = new ArrayList<View> ();
        MyActivity mActivity;
        View mRootView;

        public PlanetFragment() {
            // Empty constructor required for fragment subclasses
        }

        private List<Integer> getProfileGroup() {
            List<Integer> list = new ArrayList<Integer>();
            list.add(Integer.valueOf(R.drawable.prof_row1));
            list.add(Integer.valueOf(R.drawable.prof_row1));
            list.add(Integer.valueOf(R.drawable.prof_row1));
            list.add(Integer.valueOf(R.drawable.prof_row2));
            list.add(Integer.valueOf(R.drawable.prof_row1));
            list.add(Integer.valueOf(R.drawable.prof_row3));
            list.add(Integer.valueOf(R.drawable.prof_row1));
            list.add(Integer.valueOf(R.drawable.prof_row1));
            list.add(Integer.valueOf(R.drawable.prof_row1));
            list.add(Integer.valueOf(R.drawable.prof_row2));
            return list;    	
        }

        private List<ProfileContentData> getProfileData() {
            List<ProfileContentData> list = new ArrayList<ProfileContentData>();
            list.add(new ProfileContentData("", "", -1, -1));
            list.add(new ProfileContentData("", "", -1, -1));
            list.add(new ProfileContentData("", "", -1, -1));
            list.add(new ProfileContentData("notes", "Any note can go in this location", -1, -1));
            list.add(new ProfileContentData("", "", -1, -1));
            list.add(new ProfileContentData("mobile", "+1 917.582.8229", R.drawable.phone, R.drawable.text));
            list.add(new ProfileContentData("", "", -1, -1));
            list.add(new ProfileContentData("Gmail", "liweihong@gmail.com", -1, R.drawable.text));
            list.add(new ProfileContentData("", "", -1, -1));
            list.add(new ProfileContentData("work", "1 Wall St \nNew Yor, NY 10001", -1, R.drawable.text));
            return list;    	
        }
        
        public View gen_profile_old(LayoutInflater inflater, ViewGroup container) {
        	View rootView = inflater.inflate(R.layout.profile_content, container, false);
        	
        	expListView = (ExpandableListView) rootView.findViewById(R.id.profExListView);
        	final ProfileExpLVAdapter expListAdapter = new ProfileExpLVAdapter(
                    this.getActivity(), getProfileGroup(), getProfileData());
            expListView.setAdapter(expListAdapter);
            //setGroupIndicatorToRight();
        	
            return rootView;
        }
        
        public void onProfileNew() {
        	Intent myIntent = new Intent(this.getActivity(), ProfileViewNewActivity.class);
        	myIntent.putExtra(ProfileViewNewActivity.ARG_PROFILE_VIEW, -1);
            startActivity(myIntent); 
        	
        }
        
        public void onProfileView(int i) {
        	Intent myIntent = new Intent(this.getActivity(), ProfileViewNewActivity.class);
        	myIntent.putExtra(ProfileViewNewActivity.ARG_PROFILE_VIEW, i);
            startActivity(myIntent);
   			this.getActivity().overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left );
        }
        
        public View gen_profile(LayoutInflater inflater, ViewGroup container) {
        	
        	Type collectionType = new TypeToken<Collection<ProfileDTO>>(){}.getType();
        	Gson gson = new GsonBuilder().create();
        	Collection<ProfileDTO> profiles = gson.fromJson(SingletonNetworkStatus.getInstance().getJson(), collectionType);
        	SingletonLoginData.getInstance().setUserProfiles(new ArrayList<ProfileDTO>(profiles));
        	
        	View rootView = inflater.inflate(R.layout.profile_main, container, false);
        	((MyActivity)this.getActivity()).setupUI(rootView.findViewById(R.id.grid_profiles));

        	
        	// set up the callback
            GridView gridview = (GridView) rootView.findViewById(R.id.grid_profiles);
  			ProfileGridAdapter customGridAdapter = new ProfileGridAdapter(this.getActivity(), 
  					R.layout.profile_main_old, 
  					SingletonLoginData.getInstance().getUserProfiles());
  			gridview.setAdapter(customGridAdapter);
            
            gridview.setOnItemClickListener(new GridView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                	onProfileView(position);
                }
            });        	
        	
            
            /// show right
        	View.OnClickListener newLn = new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onProfileNew();
				}
			};

            //((MyActivity)this.getActivity()).showRightText(getResources().getString(R.string.ab_new));
            //((MyActivity)this.getActivity()).addRightLn(newLn);
			((MyActivity)this.getActivity()).showRightImage(R.drawable.white_plus);
            ((MyActivity)this.getActivity()).addRightImageLn(newLn);
            
            return rootView;
        }
        
        public View gen_setting(LayoutInflater inflater, ViewGroup container) {
        	View rootView = inflater.inflate(R.layout.setting_main, container, false);
    		TextView logout = (TextView) rootView.findViewById(R.id.set_logout);
    		logout.setOnClickListener(new View.OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				// TODO Auto-generated method stub
    				System.out.println("Logging out...");
    				((LeftDeckActivity)getActivity()).logout();
    			}
    		});        	
    		
    		// set default value for some settings;
    		Switch stLocal = (Switch) rootView.findViewById(R.id.set_broadcast_switch);
    		Switch stPush  = (Switch) rootView.findViewById(R.id.set_push_switch);
    		stLocal.setChecked(SingletonLoginData.getInstance().getUserSettings().local == 1);
    		stPush.setChecked(SingletonLoginData.getInstance().getUserSettings().push == 1);
    		
    		stLocal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    			
    			@Override
    			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    				// TODO Auto-generated method stub
    		    	((MyActivity)buttonView.getContext()).updatePreferencesLocal( isChecked ? 1 : 0);
    				
    			}
    		});
    		
    		stPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    			
    			@Override
    			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    				// TODO Auto-generated method stub
    				((MyActivity)buttonView.getContext()).updatePreferencesPush( isChecked ? 1 : 0);
    				
    			}
    		});
    		
    		// invite a friend
    		View.OnClickListener lnInvite = new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent it = new Intent (v.getContext(), ContactInviteActivity.class);
					it.putExtra(ContactInviteActivity.ARG_INVITE_ACTIVITY, 1);
					startActivity(it);
				}
			};
			TextView tvInvite = (TextView) rootView.findViewById(R.id.set_reset_pwd);
			TextView tvInvite1 = (TextView) rootView.findViewById(R.id.set_reset_pwd1);
			tvInvite.setOnClickListener(lnInvite);
			tvInvite1.setOnClickListener(lnInvite);
			
			// sort settings
    		View.OnClickListener lnSort = new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent it = new Intent (v.getContext(), SettingSortActivity.class);
					startActivity(it);
				}
			};
			TextView tvSort = (TextView) rootView.findViewById(R.id.set_sort_set1);
			TextView tvSort1 = (TextView) rootView.findViewById(R.id.set_sync_with);
			tvSort.setOnClickListener(lnSort);
			tvSort1.setOnClickListener(lnSort);
			
    		
            return rootView;
        }
        
        public void onNewGroup() {
        	Intent myIntent = new Intent(this.getActivity(), GroupPinActivity.class);
            startActivity(myIntent); 
        	
        }
        
        public void loadGroups() {
        	
        	final LinearLayout list = (LinearLayout)mRootView.findViewById(R.id.gmn_list);
        	final ArrayList<GroupDTO> allGroups = SingletonLoginData.getInstance().getGroups();
        	long seconds = System.currentTimeMillis();
        	list.removeAllViews();
        	for (int i = 0; i < allGroups.size(); i ++) {
        		GroupDTO group = allGroups.get(i);
        		View addView = this.getActivity().getLayoutInflater().inflate(R.layout.group_item_list, null);
        		addView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int num = list.getChildCount();
						for ( int i = 0; i < num; i ++ ) {
							if ( list.getChildAt(i) == v ) {
								System.out.println("Found child at index " + i);
								SingletonLoginData.getInstance().setCurGroup(allGroups.get(i));
								((LeftDeckActivity)v.getContext()).loadingGroup(allGroups.get(i).getGroupPin());
								return;
							}
						}
						System.out.println("=== ERROR: could not find this child view");
					}
				});
        		TextView groupName = (TextView) addView.findViewById(R.id.gil_name);
        		TextView groupPin = (TextView) addView.findViewById(R.id.gil_title);
        		TextView groupExp = (TextView) addView.findViewById(R.id.gil_expdate);
        		TextView groupCount = (TextView) addView.findViewById(R.id.gil_memcount);
        		groupName.setText(group.getGroupName());
        		groupPin.setText(group.getGroupPin());
        		String exp = "Exp";
        		if ( group.getExpiredTime() != null ) {
        			long diffMS = ( Long.parseLong(group.getExpiredTime()) - seconds );
        			long days = diffMS / (1000 * 60 * 60 * 24 );
        			exp = days + "d";
        		}
        		groupExp.setText(exp);
        		groupCount.setText(group.getMemberCount());
        		list.addView(addView);
        	}
        	
        	
        }
        
        public View gen_group(LayoutInflater inflater, ViewGroup container) {
        	Type collectionType = new TypeToken<Collection<GroupDTO>>(){}.getType();
        	Gson gson = new GsonBuilder().create();
        	Collection<GroupDTO> groups = gson.fromJson(SingletonNetworkStatus.getInstance().getJson(), collectionType);
        	SingletonLoginData.getInstance().setGroups(new ArrayList<GroupDTO>(groups));
        	SingletonLoginData.getInstance().setCreatedGroups(new ArrayList<GroupDTO>(groups));
        	System.out.println("Total " + groups.size() + " Created Groups.");
        	
        	// UI part
        	View rootView = inflater.inflate(R.layout.group_main_new, container, false);
        	((MyActivity)this.getActivity()).setupUI(rootView.findViewById(R.id.group_main_view));
        	mRootView = rootView;
        	
        	// set up call back for menus
        	final TextView createdTV = (TextView) rootView.findViewById(R.id.gmn_tab1);
        	final TextView joinedTV = (TextView) rootView.findViewById(R.id.gmn_tab2);
        	View.OnClickListener ln = new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					LinearLayout list = (LinearLayout)mRootView.findViewById(R.id.gmn_list);
					TextView selV, notV;
					if ( v == createdTV ) {
						selV = createdTV;
						notV = joinedTV;
						list.removeAllViews();
			        	SingletonLoginData.getInstance().setGroups(SingletonLoginData.getInstance().getCreatedGroups());
						loadGroups();
					} else {
						selV = joinedTV;
						notV = createdTV;
						list.removeAllViews();
			        	SingletonLoginData.getInstance().setGroups(SingletonLoginData.getInstance().getJoinedGroups());
						loadGroups();
					}
					
					// set background and color
					int fg = ((MyActivity)(v.getContext())).getResources().getColor(R.color.white);
					int bg = ((MyActivity)(v.getContext())).getResources().getColor(R.color.lf_send);
					selV.setTextColor(fg);
					selV.setBackgroundColor(bg);
					notV.setTextColor(bg);
					notV.setBackgroundColor(fg);
				}
			};
			createdTV.setOnClickListener(ln);
			joinedTV.setOnClickListener(ln);
        	
        	// load groups created
			loadGroups();
			
            /// show right
        	View.OnClickListener newLn = new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
		        	SingletonLoginData.getInstance().setCurGroup(null);
					onNewGroup();
				}
			};

			((MyActivity)this.getActivity()).showRightImage(R.drawable.white_plus);
            ((MyActivity)this.getActivity()).addRightImageLn(newLn);
			
            return rootView;
        }
        
        public void count_notify() {

        	// do not count twice.
        	if ( SingletonLoginData.getInstance().getNotifications().getData().size() == 0 ||
        		 SingletonLoginData.getInstance().getNotifications().getData().get(0).getNotificationId() < 0 )
        		return;
        	
            // return the number of records in cursor
        	int num = SingletonLoginData.getInstance().getNotifications().getData().size();
        	HashSet<Integer> set = new HashSet<Integer> ();
        	String topic;

        	for ( int i = 0; i < num + set.size(); i ++) {
        		EventType eType = SingletonLoginData.getInstance().getNotifications().getData().get(i).getEventType();
        		Integer intType;
    		    if ( eType == EventType.CONTACT_INVITE ) {
    		    	topic = "INVITES";
    		    	intType = 0;
    		    } else if ( eType == EventType.CONTACT_INTRODUCE ) {
    		    	topic = "INTRODUCTIONS";
    		    	intType = 1;
    		    } else { 
    		    	topic = "UPDATES";
    		    	intType = 2;
    		    }
        		
        		if ( !set.contains(intType) ) {
        			set.add(intType);
        			NotificationDTO topicNotify = new NotificationDTO();
        			topicNotify.topic = topic;
        			topicNotify.setNotificationId(-1L);
        			SingletonLoginData.getInstance().getNotifications().getData().add(i, topicNotify);
        			i++;
        		}
        		
        	}
        	
        	int count = num + set.size();
        	SingletonLoginData.getInstance().getNotifications().setTotalCount(count);
        
        }
        
        public View gen_notifcation(LayoutInflater inflater, ViewGroup container) {
        	View rootView = inflater.inflate(R.layout.notification_main, container, false);
        	((MyActivity)this.getActivity()).setupUI(rootView.findViewById(R.id.nm_invite));
        	
        	Type collectionType = new TypeToken<PageDTO<NotificationDTO>>(){}.getType();
        	Gson gson = new GsonBuilder().create();
        	PageDTO<NotificationDTO> notifications = gson.fromJson(SingletonNetworkStatus.getInstance().getJson(), collectionType);
        	SingletonLoginData.getInstance().setNotifications(notifications);
        
        	count_notify();
        	
        	// list view operation
			ListView lvInvite = (ListView) rootView.findViewById(R.id.nm_invite_list);

			// Create the Adapter and set
		    NotifyInviteLVAdapter adapter = new NotifyInviteLVAdapter(this.getActivity(), false);	
			lvInvite.setAdapter(adapter);
        	
			mRootView = rootView;
            return rootView;
        }
        
        public void deleteLabelItem() {
        	View.OnClickListener btnClk = new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					View addView = (View) v.getParent();
					TextView labelTV = (TextView)addView.findViewById(R.id.label_view);
					sendLabel2Server(labelTV.getText().toString(), 2, addView);
					
				}
			};
        	
        	for (int i = 0; i < mLabelItems.size(); i++) {
        		   View iv = mLabelItems.get(i);
        		   TextView tvDel =(TextView) iv.findViewById(R.id.view_delete);
        		   tvDel.setVisibility(View.VISIBLE);
        		   tvDel.setOnClickListener(btnClk);
        	}

        }
        
        public void showLabelContacts(String label) {
        	
        	try {
        		System.out.println("Label is " + label + " url:" + Uri.encode(label, "utf-8"));

        		((LeftDeckActivity)(this.getActivity())).loadingLabel(label);
        		
        	} catch (Exception e) {
        		
        	}
        	
        }
        
        public void showAllLabels () {
        	
        	ArrayList<String> labels = SingletonLoginData.getInstance().getLabels();
        	
			LinearLayout container = (LinearLayout)mLabelView.findViewById(R.id.lm_llo);
			
			for (int i =0 ; i < labels.size(); i ++ ) {
				
			    final View addView = this.getActivity().getLayoutInflater().inflate(R.layout.label_list_item, null);
	    	    container.addView(addView);
	    	    mLabelItems.add(addView);
	    	    addView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						TextView tv = (TextView ) v.findViewById(R.id.label_view);
						showLabelContacts(tv.getText().toString());
					}
				});
			    
				TextView labelTV = (TextView)addView.findViewById(R.id.label_view);
				labelTV.setText(labels.get(i));
				labelTV.setVisibility(View.VISIBLE);
				
			    TextView addTV = (TextView) addView.findViewById(R.id.view_add);
				EditText inputET = (EditText)addView.findViewById(R.id.label_input);
				inputET.setVisibility(View.GONE);
				addTV.setVisibility(View.INVISIBLE);
		    
			}
		    
        }
        
		public void addLabelItem() {

			LinearLayout container = (LinearLayout)mLabelView.findViewById(R.id.lm_llo);
		    final View addView = this.getActivity().getLayoutInflater().inflate(R.layout.label_list_item, null);
    	    container.addView(addView);
			RelativeLayout rlo = (RelativeLayout) mLabelView.findViewById(R.id.lm_clkLO);
        	ImageView rIV = (ImageView) mLabelView.findViewById(R.id.lm_icon);
        	TextView  rTV = (TextView) mLabelView.findViewById(R.id.lm_btn);
			rlo.setClickable(false);
			rIV.setClickable(false);
			rTV.setClickable(false);
		    
		    TextView addTV = (TextView) addView.findViewById(R.id.view_add);
		    addTV.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					View addView = (View) v.getParent();
					EditText inputET = (EditText)addView.findViewById(R.id.label_input);
					
					sendLabel2Server(inputET.getText().toString(), 1, addView);
				}
			});
		    
		    
		}
		
		public void sendLabel2Server(String value, int op, View v) {
			String path, params;
			if ( op == 1) {
				path = "/api/labels.json?" + SingletonLoginData.getInstance().getPostParam();
				params = "{\"label\":\"" + value + "\"}";
				((LeftDeckActivity)this.getActivity()).setLabelOpPost(1, mLabelView, v, mLabelItems);
			} else {
				value = value.replaceAll(" ", "%20");
				path = "/api/labels/" + value + "/delete.json?" + SingletonLoginData.getInstance().getPostParam();
				params = "";
				((LeftDeckActivity)this.getActivity()).setLabelOpPost(2, mLabelView, v, mLabelItems);
			}
			
			SingletonNetworkStatus.getInstance().setActivity(this.getActivity());
			new HttpConnection().access(this.getActivity(), path, params, "POST");
		}
		
        public View gen_label(LayoutInflater inflater, ViewGroup container) {

        	Type collectionType = new TypeToken<Collection<String>>(){}.getType();
        	Gson gson = new GsonBuilder().create();
        	Collection<String> labels = gson.fromJson(SingletonNetworkStatus.getInstance().getJson(), collectionType);
        	SingletonLoginData.getInstance().setLabels(new ArrayList<String>(labels));
        	
        	View rootView = inflater.inflate(R.layout.label_main, container, false);
        	((MyActivity)this.getActivity()).setupUI(rootView.findViewById(R.id.label_main_view));
        	mLabelView = rootView;
        	View.OnClickListener btnClk = new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					addLabelItem();
					
				}
			};
        	RelativeLayout rlo = (RelativeLayout) rootView.findViewById(R.id.lm_clkLO);
        	ImageView rIV = (ImageView) rootView.findViewById(R.id.lm_icon);
        	TextView  rTV = (TextView) rootView.findViewById(R.id.lm_btn);
        	rIV.setOnClickListener(btnClk);
        	rlo.setOnClickListener(btnClk);
        	rTV.setOnClickListener(btnClk);
        	
        	showAllLabels();
        	
        	View.OnClickListener editClk = new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if ( mActivity.getRightText() == getResources().getString(R.string.ab_edit) ) {
						mActivity.showRightText(getResources().getString(R.string.ab_done));
						deleteLabelItem();
					} else {
						mActivity.showRightText(getResources().getString(R.string.ab_edit));
			        	for (int i = 0; i < mLabelItems.size(); i++) {
			        		   View iv = mLabelItems.get(i);
			        		   TextView tvDel = (TextView) iv.findViewById(R.id.view_delete);
			        		   tvDel.setVisibility(View.INVISIBLE);
			        	}

					}
				}
			};
        	mActivity.showRightText(getResources().getString(R.string.ab_edit));
        	mActivity.addRightLn(editClk);
        	
            return rootView;
        }
        
        class MyContactComp implements Comparator<ContactDTO>{
        	
        	private int mode = 0;
        	
        	MyContactComp(int m) {
        		mode = m;
        	}
          	 
            @Override
            public int compare(ContactDTO c1, ContactDTO c2) {
            	String s2FN = c2.getSharedProfiles().size() == 0 ? "" : c2.getSharedProfiles().get(0).getUserProfile().getFirstName();
            	String s1FN = c1.getSharedProfiles().size() == 0 ? "" : c1.getSharedProfiles().get(0).getUserProfile().getFirstName();
            	String s2LN = c2.getSharedProfiles().size() == 0 ? "" : c2.getSharedProfiles().get(0).getUserProfile().getLastName();
            	String s1LN = c1.getSharedProfiles().size() == 0 ? "" : c1.getSharedProfiles().get(0).getUserProfile().getLastName();

            	int value;
            	
            	s1FN = s1FN == null ? "" : s1FN;
            	s1LN = s1LN == null ? "" : s1LN;
            	s2FN = s2FN == null ? "" : s2FN;
            	s2LN = s2LN == null ? "" : s2LN;
            	if ( mode == 0 ) {
            		value = s1FN.compareToIgnoreCase(s2FN); 
	            	if ( value == 0 ) {
	            		return s1LN.compareToIgnoreCase(s2LN);
	            	}
            	} else {
            		value = s1LN.compareToIgnoreCase(s2LN);
            		if ( value == 0 ) {
            			return s1FN.compareToIgnoreCase(s2FN);
            		}
            	}
                return value;
            }
        }
        
        public View gen_contact(LayoutInflater inflater, ViewGroup container) {

        	// for now
        	if ( SingletonLoginData.getInstance().getCloudContactList().size() < 10 ) {
	        	Type collectionType = new TypeToken<Collection<ContactDTO>>(){}.getType();
	        	Gson gson = new GsonBuilder().create();
	        	Collection<ContactDTO> contacts = gson.fromJson(SingletonNetworkStatus.getInstance().getJson(), collectionType);
	        	SingletonLoginData.getInstance().setContactList(new ArrayList<ContactDTO>(contacts));
	        	SingletonLoginData.getInstance().setCloudContactList(new ArrayList<ContactDTO>(contacts));
        	}
        	
        	View rootView = inflater.inflate(R.layout.contact_search_view, container, false);
        	((MyActivity)this.getActivity()).setupUI(rootView.findViewById(R.id.contact_main_view));
        	mRootView = rootView;
        	
			// add local contacts to contact list if user select show local contacts. 
			// sort the contacts based on the setting
			ArrayList<ContactDTO> list = SingletonLoginData.getInstance().getContactList();
			list.clear();
			list.addAll(SingletonLoginData.getInstance().getCloudContactList());
			list.addAll(SingletonLoginData.getInstance().getLocalContactList());
			Collections.sort(list, new MyContactComp(SingletonLoginData.getInstance().getUserSettings().sort));
			System.out.println("Cloud: " + SingletonLoginData.getInstance().getCloudContactList().size() +
					           "Local: " + SingletonLoginData.getInstance().getLocalContactList().size() +
					           "Total: " + list.size());
        	
        	// list view operation
			ListView lvContact = (ListView) rootView.findViewById(R.id.csv_list);
			// Create the Adapter and set
		    ContactMainLVAdapter adapter = new ContactMainLVAdapter(this.getActivity(), list);	
			lvContact.setAdapter(adapter);
			
			// to handle click event on listView item
			lvContact.setOnItemClickListener(new ListView.OnItemClickListener() {
				@Override
	            public void onItemClick(AdapterView<?> arg0, View v,int position, long arg3) {
		        	ProfileDTO merged = new ProfileDTO();
    				merged.setUserProfileAttributes(new ArrayList<UserProfileAttribute>());
    				merged.setUserProfileAddresses(new ArrayList<UserProfileAddress>());
    				
    				ContactDTO selectedItem = (ContactDTO)((ContactMainLVAdapter)((ListView)mRootView.findViewById(R.id.csv_list)).getAdapter()).getItem(position);
    				UserProfile profile;
    				if ( selectedItem.getSharedProfiles().size() > 0 )
    					profile = selectedItem.getSharedProfiles().get(0).getUserProfile();
    				else {
    					profile = new UserProfile();
    					profile.setFirstName(selectedItem.getContactUser().getFirstName());
    					profile.setLastName(selectedItem.getContactUser().getLastName());
    				}
    				merged.setUserProfile(profile);
		        	merged.setUserId(selectedItem.getUserId());
		        	
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
		        	if ( selectedItem.getLabels() == null ) {
		        		selectedItem.setLabels(new ArrayList<String> ());
		        	}
		        	SingletonLoginData.getInstance().setContactLabels((ArrayList<String>)selectedItem.getLabels());
					
					onProfileView(100000 + position);

	            }

	        });			
			
        	// end of list view
			
			// Add contact button
        	View.OnClickListener newLn = new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
			    	Intent myIntent = new Intent(getActivity(), ContactFindActivity.class);
			        startActivity(myIntent); 
					
				}
			};
			
			//((MyActivity)this.getActivity()).showRightText(getResources().getString(R.string.lli_add));
            //((MyActivity)this.getActivity()).addRightLn(newLn);
			((MyActivity)this.getActivity()).showRightImage(R.drawable.white_plus);
            ((MyActivity)this.getActivity()).addRightImageLn(newLn);
			
            // add filter for search view
            SearchView sv = (SearchView) rootView.findViewById(R.id.csv_search);
			sv.setFocusableInTouchMode(true);
			sv.requestFocus();
			sv.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					System.out.println("Search view clicked");
		            SearchView sv = (SearchView) mRootView.findViewById(R.id.csv_search);
					sv.setFocusableInTouchMode(true);
					sv.setIconified(false);
					sv.requestFocus();
					
				}
			});

			final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() { 
			    @Override 
			    public boolean onQueryTextChange(String cs) { 
			        // Do something
			    	((ContactMainLVAdapter)((ListView)mRootView.findViewById(R.id.csv_list)).getAdapter()).getFilter().filter(cs);
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
        	
            return rootView;
        }
        
       @SuppressLint("NewApi")
       @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
    	   
           int i = getArguments().getInt(ARG_PLANET_NUMBER);
           View result;
           mActivity = (MyActivity)this.getActivity();
           
           switch (i) {
	           case OPTION_PROFILE:
	        	   result = gen_profile(inflater, container);
	        	   break;
	           case OPTION_SETTING:
	        	   result = gen_setting(inflater, container);
	        	   break;
	           case OPTION_CONTACT:
	        	   result = gen_contact(inflater, container);
	        	   break;
	           case OPTION_GROUP:
	        	   result = gen_group(inflater, container);
	        	   break;
	           case OPTION_LABEL:
	        	   result = gen_label(inflater, container);
	        	   break;
	           case OPTION_NOTIFY:
	        	   result = gen_notifcation(inflater, container);
	        	   break;
	           default:
	        	   result = gen_contact(inflater, container);
	        	   break;
        		   
           }
           
           mRootView = result;
           return result;
        }
       
       @Override
       public void onResume() 
       {
    	   super.onResume();
    	   System.out.println("Onresume - Fragment.");
    	   
    	   int i = getArguments().getInt(ARG_PLANET_NUMBER);

    	   // set up the callback
    	   if ( i == OPTION_PROFILE ) {
	    	   GridView gridview = (GridView) mRootView.findViewById(R.id.grid_profiles);
	    	   ProfileGridAdapter customGridAdapter = new ProfileGridAdapter(this.getActivity(), 
					R.layout.profile_main_old, 
					SingletonLoginData.getInstance().getUserProfiles());
	    	   gridview.setAdapter(customGridAdapter);
    	   }
    	   
    	   if ( i == OPTION_CONTACT && SingletonLoginData.getInstance().getIsStatusChanged() ) {
    		   	SingletonLoginData.getInstance().setIsStatusChanged( false );
      			ArrayList<ContactDTO> list = SingletonLoginData.getInstance().getContactList();
    			list.clear();
    			list.addAll(SingletonLoginData.getInstance().getCloudContactList());
       			list.addAll(SingletonLoginData.getInstance().getLocalContactList());
       			Collections.sort(list, new MyContactComp(SingletonLoginData.getInstance().getUserSettings().sort));
    		   
       			ListView lvContact = (ListView) mRootView.findViewById(R.id.csv_list);
   			
	   			// Create the Adapter and set
	   		    ContactMainLVAdapter adapter = new ContactMainLVAdapter(this.getActivity(), list);	
	   			lvContact.setAdapter(adapter);
    	   }
    	   
    	   if ( i == OPTION_NOTIFY ) {
	           	count_notify();
	
	           	ListView lvInvite = (ListView) mRootView.findViewById(R.id.nm_invite_list);
	   		    NotifyInviteLVAdapter adapter = new NotifyInviteLVAdapter(this.getActivity(), false);	
	   			lvInvite.setAdapter(adapter);
    	   }

    	   if ( i == OPTION_GROUP ) {
    		   loadGroups();
    	   }
    	   
	   }
       
       // Convert pixel to dip
       @SuppressLint("NewApi")
       public int getDipsFromPixel(float pixels) {
           // Get the screen's density scale
           final float scale = getResources().getDisplayMetrics().density;
           // Convert the dps to pixels, based on density scale
           return (int) (pixels * scale + 0.5f);
       }
       
       @SuppressLint("NewApi")
       private void setGroupIndicatorToRight() {
           /* Get the screen width */
           DisplayMetrics dm = new DisplayMetrics();
           this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
           
           int width = dm.widthPixels;
           
           int p1 = getDipsFromPixel(45);
           int p2 = getDipsFromPixel(5);
    
           if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
        	   expListView.setIndicatorBounds(width - p1, width - p2);
        	} else {
        		expListView.setIndicatorBoundsRelative(width - p1, width - p2);
        	}           
       }
       
       
    }
}