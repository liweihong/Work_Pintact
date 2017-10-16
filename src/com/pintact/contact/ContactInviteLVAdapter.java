package com.pintact.contact;

import com.pintact.R;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ContactInviteLVAdapter extends BaseAdapter
{
    
    private Context mContext;
    Cursor cursor;
    public ContactInviteLVAdapter(Context context,Cursor cur) 
    {
	    super();
	    mContext=context;
	    cursor=cur;
    }
       
    public int getCount() 
    {
        // return the number of records in cursor
        return cursor.getCount();
    }

    // getView method is called for each item of ListView
    public View getView(int position,  View view, ViewGroup parent) 
    {
	    // inflate the layout for each item of listView
	    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    view = inflater.inflate(R.layout.contact_invite_list, null);
	
	    // move the cursor to required position 
	    cursor.moveToPosition(position);
	    
	    // fetch the sender number and sms body from cursor
		int indexName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
		String senderNumber   = cursor.getString(indexName);
	    
	    // get the reference of textViews
	    TextView name=(TextView)view.findViewById(R.id.im_name);
	    
	    // Set the Sender number and smsBody to respective TextViews 
	    name.setText(senderNumber);
	    
	    // set image
	    ListView lvContact = (ListView) parent;
		ImageView ivSelect = (ImageView) view.findViewById(R.id.im_check);
		if (lvContact.isItemChecked(position)) {
			ivSelect.setImageResource(R.drawable.circle_check);
		} else {
			ivSelect.setImageResource(R.drawable.circle);
		}	    

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
