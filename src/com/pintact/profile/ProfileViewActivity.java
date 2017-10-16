package com.pintact.profile;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.pintact.R;
import com.pintact.utility.MyActivity;

public class ProfileViewActivity extends MyActivity {

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.profile_content);
			
			ExpandableListView expListView = (ExpandableListView) findViewById(R.id.profExListView);
        	final ProfileExpLVAdapter expListAdapter = new ProfileExpLVAdapter(
                    this, getProfileGroup(), getProfileData());
            expListView.setAdapter(expListAdapter);
            //setGroupIndicatorToRight();
        	
			
			TextView tv = (TextView)findViewById(R.id.actionBar);
			tv.setText(getResources().getString(R.string.ab_new_profile));
			
			View.OnClickListener finClkLn = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			};

			Button btn = (Button)findViewById(R.id.actionBarBtn);
			btn.setOnClickListener(finClkLn);
			hideLeft();
			showRightText(getResources().getString(R.string.ab_cancel));
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
        
		
		
}
