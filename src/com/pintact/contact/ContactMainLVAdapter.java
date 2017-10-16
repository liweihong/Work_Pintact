package com.pintact.contact;

import java.util.ArrayList;
import java.util.Locale;

import com.pintact.R;
import com.pintact.data.ContactDTO;
import com.pintact.utility.SingletonLoginData;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.Filter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ContactMainLVAdapter extends BaseAdapter implements Filterable
{
    
    private Context mContext;
    ArrayList<ContactDTO> contacts;
    boolean useLocal = false;
    boolean isMembers = false;
    public ContactMainLVAdapter(Context context, ArrayList<ContactDTO> conts) 
    {
	    super();
	    mContext=context;
	    contacts = conts;
	    //contacts = SingletonLoginData.getInstance().getContactList();
    }

    public ContactMainLVAdapter(Context context, ArrayList<ContactDTO> conts, boolean isMember) 
    {
	    super();
	    mContext=context;
	    contacts = conts;
	    isMembers = isMember;
	    //contacts = SingletonLoginData.getInstance().getContactList();
    }
    
    public int getCount() 
    {
        // return the number of records in cursor
    	return contacts.size();
    }

    // getView method is called for each item of ListView
    public View getView(int position,  View view, ViewGroup parent) 
    {
	    // inflate the layout for each item of listView
	    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    view = inflater.inflate(R.layout.contact_search_list, null);
	
	    // get the reference of textViews
	    TextView name=(TextView)view.findViewById(R.id.csl_name);
	    TextView numb=(TextView)view.findViewById(R.id.csl_phone);
	    TextView initIV=(TextView)view.findViewById(R.id.csl_init);
	    RelativeLayout invite=(RelativeLayout)view.findViewById(R.id.csl_invite);
	    
	    {
	    	
	    	String fn; 
	    	String ln; 
	    	String cn;
	    	
	    	if ( contacts.get(position).isLocalContact ) {
		    	fn = contacts.get(position).getSharedProfiles().get(0).getUserProfile().getFirstName();
		    	ln = contacts.get(position).getSharedProfiles().get(0).getUserProfile().getLastName();
		    	cn = contacts.get(position).getSharedProfiles().get(0).getUserProfileAttributes().get(0).getValue();
		    	view.setBackgroundDrawable(mContext.getResources().getDrawable(R.anim.background_listview_local));
	    	} else {
		    	fn = contacts.get(position).getContactUser().getFirstName(); 
		    	ln = contacts.get(position).getContactUser().getLastName(); 
		    	if ( isMembers ) {
		    		cn = contacts.get(position).getContactUser().getPin();
		    	} else
		    		cn = contacts.get(position).getSharedProfiles() == null || contacts.get(position).getSharedProfiles().size() == 0 ? "" : contacts.get(position).getSharedProfiles().get(0).getUserProfile().getCompanyName();
	    	}
	    	
	    	
		    char ab[] = "ab".toCharArray();
		    
		    if ( fn == null || fn.length() < 1) {
		    	ab[0] = ab[1] = ln.charAt(0);
		    	fn = "";
		    }
		    else if (ln == null || ln.length() < 1) {
		    	ab[0] = ab[1] = fn.charAt(0);
		    	ln = "";
		    }
		    else {
		    	ab[0] = fn.charAt(0);
		    	ab[1] = ln.charAt(0);
		    }
		    
		    String init = new String(ab);
		    init = init.toUpperCase(Locale.US);
		    initIV.setText(init);
		    name.setText(fn + " " + ln);
		    numb.setText(cn);
	    	
		    // hide invite
		    if (! contacts.get(position).isLocalContact)
		    	invite.setVisibility(View.GONE);
	    }
	    
	    // set short name

	    return view;
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return contacts.get(position);
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                contacts = (ArrayList<ContactDTO> ) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<ContactDTO> FilteredArrayNames = new ArrayList<ContactDTO>();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                ArrayList<ContactDTO> conts = SingletonLoginData.getInstance().getContactList();
                for (int i = 0; i < conts.size(); i++) {
        	    	String fn = conts.get(i).getContactUser().getFirstName(); 
        	    	String ln = conts.get(i).getContactUser().getLastName();
        	    	String name = fn + " " + ln;
                    if (name.toLowerCase().contains(constraint.toString()))  {
                        FilteredArrayNames.add(conts.get(i));
                        System.out.println("Found " + name);
                    }
                }

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;

                return results;
            }
        };

        return filter;
    }
}
