package com.pintact.leftdeck;

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
 
public class LeftDeckExpLVAdapter extends BaseExpandableListAdapter {
 
    private Activity context;
    private List<LeftDeckData> laptopCollections;
    private List<LeftDeckData> laptops;
 
    public LeftDeckExpLVAdapter(Activity context, List<LeftDeckData> laptops,
    		List<LeftDeckData> laptopCollections) {
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
        final LeftDeckData cont = laptopCollections.get(childPosition);
        LayoutInflater inflater = context.getLayoutInflater();
 
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.leftdeck_explist_child, null);
        }
 
        TextView item1 = (TextView) convertView.findViewById(R.id.Label1TV);
        ImageView iv1 = (ImageView) convertView.findViewById(R.id.imageLabel31);
        ImageView iv2 = (ImageView) convertView.findViewById(R.id.imagelabel32);
        
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
 
        item1.setText(cont.getOption());
        
        iv1.setVisibility(View.VISIBLE);
        iv2.setVisibility(View.VISIBLE);
        
        if ( cont.getFirstIcon() != -1 )
        	iv1.setImageDrawable(convertView.getResources().getDrawable(cont.getFirstIcon()));
        else 
        	iv1.setVisibility(View.INVISIBLE);
        
        if ( cont.getSecondIcon() != -1 )
        	iv2.setImageDrawable(convertView.getResources().getDrawable(cont.getSecondIcon()));
        else
        	iv2.setVisibility(View.GONE);
        
        return convertView;
    }
 
    public int getChildrenCount(int groupPosition) {
        // return laptopCollections.size();
    	return groupPosition == 2 ? 3 : 0;
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
    	
        int laptopIndex = groupPosition;
        if (convertView == null ) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.leftdeck_explist_parent,
                    null);
        }
        ImageView item = (ImageView) convertView.findViewById(R.id.imageIcon);
        item.setImageDrawable(convertView.getResources().getDrawable(laptops.get(laptopIndex).getFirstIcon()));

        TextView tv = (TextView) convertView.findViewById(R.id.OptionTxt);
        tv.setText(laptops.get(laptopIndex).getOption());
        
        /*
        tv.setOnClickListener(new View.OnClickListener() {
        	 
            public void onClick(View v) {
            	
            	
            }
        });
        */

        
        ImageView indicator = (ImageView) convertView.findViewById(R.id.imageFunc);

        int imIndex = laptops.get(laptopIndex).getSecondIcon() ;
        if ( imIndex == -1 )
        	indicator.setVisibility(View.INVISIBLE);
        else {
        	indicator.setImageDrawable(convertView.getResources().getDrawable(imIndex));
        	indicator.setVisibility(View.VISIBLE);
        }

        if ( getChildrenCount(groupPosition) > 0 ) {
        	indicator.setVisibility(View.VISIBLE);
        	if ( isExpanded )
            	indicator.setImageDrawable(convertView.getResources().getDrawable(R.drawable.left_row3_2_up));
        	else 
            	indicator.setImageDrawable(convertView.getResources().getDrawable(R.drawable.left_row3_2_down));
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