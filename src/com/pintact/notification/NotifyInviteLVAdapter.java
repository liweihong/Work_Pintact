package com.pintact.notification;

import java.util.HashSet;
import java.util.Locale;

import com.pintact.R;
import com.pintact.data.EventType;
import com.pintact.data.NotificationDTO;
import com.pintact.data.PageDTO;
import com.pintact.utility.SingletonLoginData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NotifyInviteLVAdapter extends BaseAdapter
{
    
    private Context mContext;
    boolean  mIsUpdate;
    PageDTO<NotificationDTO> mData;
    HashSet<Integer> mSet;
    int mOffset = 0;
    
    public NotifyInviteLVAdapter(Context context, boolean isUpdate) 
    {
	    super();
	    mContext=context;
	    mIsUpdate = isUpdate;
	    mData = SingletonLoginData.getInstance().getNotifications();
	    mSet = new HashSet<Integer>();
    }
       
    public int getCount() 
    {
    	return mData.getTotalCount();
    }

    // getView method is called for each item of ListView
    public View getView(int position,  View view, ViewGroup parent) 
    {
    	// inflate the layout for each item of listView
	    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	    boolean isTopic = mData.getData().get(position).getNotificationId() == -1L ? true : false;
	    if ( ! isTopic) {
	    	view = inflater.inflate(R.layout.notification_list_invite, null);
	    } else {
	    	view = inflater.inflate(R.layout.notification_list_topic, null);
		    TextView topicTV=(TextView)view.findViewById(R.id.notify_topic);
		    topicTV.setText(mData.getData().get(position).topic);
	    	return view;
	    }
	    
	    String fName = SingletonLoginData.getInstance().getNotifications().getData().get(position).getData().sender.firstName;
	    String lName = SingletonLoginData.getInstance().getNotifications().getData().get(position).getData().sender.lastName;
	    
	    // get the reference of textViews
	    TextView initTV=(TextView)view.findViewById(R.id.nli_initial);
	    TextView nameTV=(TextView)view.findViewById(R.id.nli_name);
	    RelativeLayout accept=(RelativeLayout)view.findViewById(R.id.nli_accept);
	    RelativeLayout cancel=(RelativeLayout)view.findViewById(R.id.nli_cxl);

	    EventType eType = SingletonLoginData.getInstance().getNotifications().getData().get(position).getEventType(); 
	    if ( EventType.CONTACT_INVITE != eType &&
	    	 EventType.CONTACT_INTRODUCE != eType )
	    	accept.setVisibility(View.GONE);
	    else {
	    	TextView data = (TextView)view.findViewById(R.id.for_data);
	    	TextView data1 = (TextView)view.findViewById(R.id.for_data1);
	    	data.setText(Integer.valueOf(position).toString());
	    	data1.setText(Integer.valueOf(position).toString());
	    	cancel.setVisibility(View.VISIBLE);
	    }

	    if ( EventType.CONTACT_INTRODUCE == eType ) {
	    	fName = SingletonLoginData.getInstance().getNotifications().getData().get(position).getData().introducingUser.firstName;
	    	lName = SingletonLoginData.getInstance().getNotifications().getData().get(position).getData().introducingUser.lastName;
	    }
	    
	    // Set the Sender number and smsBody to respective TextViews
		String senderName =  fName + " " + lName;
	    char ab[] = "ab".toCharArray();
	    ab[0] = senderName.charAt(0);
	    if ( lName == null || lName.length() == 0 ) {
	    	ab[1] = ab[0];
	    } else {
	    	ab[1] = lName.charAt(0);
	    }
	    String init = new String(ab);
	    init = init.toUpperCase(Locale.US);
	    
	    nameTV.setText(senderName);
	    initTV.setText(init);

	    return view;
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
}
