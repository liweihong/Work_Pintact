package com.pintact.profile;

import java.util.List;
import com.pintact.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class ProfileExpLVAdapter extends BaseExpandableListAdapter {
 
    private Activity context;
    private List<ProfileContentData> laptopCollections;
    private List<Integer> laptops;
 
    public ProfileExpLVAdapter(Activity context, List<Integer> laptops,
    		List<ProfileContentData> laptopCollections) {
        this.context = context;
        this.laptopCollections = laptopCollections;
        this.laptops = laptops;
    }
 
    public Object getChild(int groupPosition, int childPosition) {
        return laptopCollections.get(childPosition);
    }
 
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    public View getChildView(final int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
        final ProfileContentData cont = laptopCollections.get(groupPosition);
        LayoutInflater inflater = context.getLayoutInflater();
 
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.profile_content_child, null);
        }
 
        TextView item1 = (TextView) convertView.findViewById(R.id.profNoteTV);
        TextView item2 = (TextView) convertView.findViewById(R.id.profCommentTV);
 
        ImageView iv1 = (ImageView) convertView.findViewById(R.id.firstIV);
        ImageView iv2 = (ImageView) convertView.findViewById(R.id.secondIV);
        
        /*
        iv1.setOnClickListener(new OnClickListener() {
 
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you want to remove?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                List<String> child =
                                    laptopCollections.get(laptops.get(groupPosition));
                                child.remove(childPosition);
                                notifyDataSetChanged();
                            }
                        });
                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        */
 
        item1.setText(cont.getType());
        item2.setText(cont.getContent());
        
        iv1.setVisibility(View.VISIBLE);
        iv2.setVisibility(View.VISIBLE);
        
        if ( cont.getFirstIcon() != -1 )
        	iv1.setImageDrawable(convertView.getResources().getDrawable(cont.getFirstIcon()));
        else 
        	iv1.setVisibility(View.INVISIBLE);
        
        if ( cont.getSecondIcon() != -1 )
        	iv2.setImageDrawable(convertView.getResources().getDrawable(cont.getSecondIcon()));
        else
        	iv2.setVisibility(View.INVISIBLE);
        
        return convertView;
    }
 
    public int getChildrenCount(int groupPosition) {
        // return laptopCollections.size();
    	return laptopCollections.get(groupPosition).getType().equals("") ? 0 : 1;
    }
 
    public Object getGroup(int groupPosition) {
        return laptops.get(groupPosition);
    }
 
    public int getGroupCount() {
    	int num = laptops.size();
        return num;
    }
 
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
    	
        if( groupPosition % 2 == 0) {
            View divider = convertView;
            if(divider == null || divider.getId() != R.id.profile_divider ) {
                LayoutInflater infalInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                divider = infalInflater.inflate(R.layout.profile_content_divider, null);
            }

            return divider;
        }    	
    	
        int laptopIndex = (Integer)getGroup(groupPosition);
        if (convertView == null || convertView.getId() != R.id.profile_content ) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.profile_content_parent,
                    null);
        }
        ImageView item = (ImageView) convertView.findViewById(R.id.parentProfileImage);
        item.setImageDrawable(convertView.getResources().getDrawable(laptopIndex));

        ImageView indicator = (ImageView) convertView.findViewById(R.id.profGroupIndicator);
        
        if ( getChildrenCount(groupPosition) == 0 ) {
        	indicator.setVisibility(View.INVISIBLE);
        } else {
        	indicator.setVisibility(View.VISIBLE);
        	if ( isExpanded )
            	indicator.setImageDrawable(convertView.getResources().getDrawable(R.drawable.up_arrows));
        	else 
            	indicator.setImageDrawable(convertView.getResources().getDrawable(R.drawable.down_arrows));
        }
        
        return convertView;
    }
 
    public boolean hasStableIds() {
        return true;
    }
 
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}