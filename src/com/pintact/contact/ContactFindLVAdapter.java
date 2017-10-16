package com.pintact.contact;


import com.pintact.R;
import com.pintact.data.PageDTO;
import com.pintact.data.SearchDTO;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ContactFindLVAdapter extends BaseAdapter
{
    
    private Context mContext;
    PageDTO<SearchDTO> cursor;
    public ContactFindLVAdapter(Context context, PageDTO<SearchDTO> cur) 
    {
	    super();
	    mContext=context;
	    cursor=cur;
    }
       
    public int getCount() 
    {
        // return the number of records in cursor
        return cursor.getData().size();
    }

    // getView method is called for each item of ListView
    public View getView(int position,  View view, ViewGroup parent) 
    {
	    // inflate the layout for each item of listView
	    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    view = inflater.inflate(R.layout.contact_suggest_list, null);
	
		String senderName   = cursor.getData().get(position).getFirstName() + " " +
								cursor.getData().get(position).getLastName();
		String senderNumber   = cursor.getData().get(position).getTitle();
	    
	    // get the reference of textViews
	    TextView name=(TextView)view.findViewById(R.id.csl_name);
	    TextView numb=(TextView)view.findViewById(R.id.csl_title);
	    
	    // Set the Sender number and smsBody to respective TextViews
	    name.setText(senderName);
	    
	    if ( senderNumber.length() < 1) 
	    	numb.setVisibility(View.GONE);
	    else 
	    	numb.setText(senderNumber);
	    
	    // set short name

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
