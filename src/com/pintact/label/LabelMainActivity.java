package com.pintact.label;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pintact.LeftDeckActivity;
import com.pintact.R;
import com.pintact.utility.HttpConnection;
import com.pintact.utility.MyActivity;
import com.pintact.utility.SingletonLoginData;
import com.pintact.utility.SingletonNetworkStatus;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LabelMainActivity extends MyActivity {
	
	boolean isAddingLabel = false;
	String newLabel;
	View mSubLabelView;
	ArrayList<String> mLabels;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.label_main);

		showLeftImage(R.drawable.left_arrow);
		View.OnClickListener backLn = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		};
		addLeftLn(backLn);
		
		mLabels = new ArrayList<String>(SingletonLoginData.getInstance().getContactLabels());
		
		// add Done on right
		showRightText(getResources().getString(R.string.ab_done));
		View.OnClickListener doneLn = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendUpdatedLabels();
			}
		};
		addRightLn(doneLn);		
		
    	View.OnClickListener btnClk = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addLabelItem();
				
			}
		};
    	RelativeLayout rlo = (RelativeLayout) findViewById(R.id.lm_clkLO);
    	ImageView rIV = (ImageView) findViewById(R.id.lm_icon);
    	TextView  rTV = (TextView) findViewById(R.id.lm_btn);
    	rIV.setOnClickListener(btnClk);
    	rlo.setOnClickListener(btnClk);
    	rTV.setOnClickListener(btnClk);
    	
    	showAllLabels();
    	
		
	}

    public void showAllLabels () {
    	
    	ArrayList<String> labels = SingletonLoginData.getInstance().getLabels();
    	
		LinearLayout container = (LinearLayout)findViewById(R.id.lm_llo);
		
		View.OnClickListener selectLn = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String label = ((TextView)v.findViewById(R.id.label_view)).getText().toString();
				ImageView check = (ImageView)v.findViewById(R.id.label_check);
				SingletonLoginData.getInstance().getUserSettings().selectedLabel = label;
				
				if ( mLabels.contains(label)) {
					mLabels.remove(label);
					check.setImageResource(R.drawable.circle);
				} else {
					mLabels.add(label);
					check.setImageResource(R.drawable.circle_check);
				}
				//finish();
			}
		};
		
		for (int i =0 ; i < labels.size(); i ++ ) {
			
		    final View addView = this.getLayoutInflater().inflate(R.layout.label_list_item, null);
    	    container.addView(addView);
		    
			TextView labelTV = (TextView)addView.findViewById(R.id.label_view);
			labelTV.setText(labels.get(i));
			labelTV.setVisibility(View.VISIBLE);
			//labelTV.setOnClickListener(selectLn);
			addView.setOnClickListener(selectLn);
			
		    TextView addTV = (TextView) addView.findViewById(R.id.view_add);
			EditText inputET = (EditText)addView.findViewById(R.id.label_input);
			inputET.setVisibility(View.GONE);
			addTV.setVisibility(View.INVISIBLE);

			ImageView check = (ImageView)addView.findViewById(R.id.label_check);
			check.setVisibility(View.VISIBLE);
			
			if ( SingletonLoginData.getInstance().getContactLabels() != null &&
				 SingletonLoginData.getInstance().getContactLabels().contains(labels.get(i))) {
				check.setImageResource(R.drawable.circle_check);
			}
			
		}
	    
    }
    
	public void addLabelItem() {

		LinearLayout container = (LinearLayout)findViewById(R.id.lm_llo);
	    final View addView = this.getLayoutInflater().inflate(R.layout.label_list_item, null);
	    container.addView(addView);
		RelativeLayout rlo = (RelativeLayout) findViewById(R.id.lm_clkLO);
    	ImageView rIV = (ImageView) findViewById(R.id.lm_icon);
    	TextView  rTV = (TextView) findViewById(R.id.lm_btn);
		rlo.setClickable(false);
		rIV.setClickable(false);
		rTV.setClickable(false);
		mSubLabelView = addView;
	    
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

		isAddingLabel = true;
		newLabel = value;
		String path = "/api/labels.json?" + SingletonLoginData.getInstance().getPostParam();
		String params = "{\"label\":\"" + value + "\"}";
		
		SingletonNetworkStatus.getInstance().setActivity(this);
		new HttpConnection().access(this, path, params, "POST");
	}
	
    
	public void sendUpdatedLabels() {
		String path = "/api/contacts/" + SingletonLoginData.getInstance().getMergedProfile().getUserId() + "/labels.json?" + SingletonLoginData.getInstance().getPostParam();
		SingletonNetworkStatus.getInstance().setActivity(this);
		
		Gson gson = new GsonBuilder().create();
		String params = gson.toJson(mLabels);
		
		params = "{\"label\":" + params + "}";
		new HttpConnection().access(this, path, params, "POST");
		
	}
	

	public void onPostNetwork() {
		
		if ( SingletonNetworkStatus.getInstance().getCode() != 200 ) {
			myDialog(SingletonNetworkStatus.getInstance().getMsg(), 
					SingletonNetworkStatus.getInstance().getErrMsg());
			return;
		}
		
		if ( isAddingLabel ) {
			isAddingLabel = false;
			
			// add it to global labels
			// add it to local too
			mLabels.add(newLabel);
			SingletonLoginData.getInstance().getLabels().add(newLabel);
			
			// update GUI
    		{
				EditText inputET = (EditText)mSubLabelView.findViewById(R.id.label_input);
				TextView labelTV = (TextView)mSubLabelView.findViewById(R.id.label_view);
				TextView addTV = (TextView)mSubLabelView.findViewById(R.id.view_add);
				labelTV.setText(inputET.getText().toString());
				inputET.setVisibility(View.GONE);
				labelTV.setVisibility(View.VISIBLE);
				addTV.setVisibility(View.INVISIBLE);
				
				// hide Add, show checked.
				RelativeLayout rlo = (RelativeLayout) findViewById(R.id.lm_clkLO);
				rlo.setClickable(true);
				
				ImageView check = (ImageView)mSubLabelView.findViewById(R.id.label_check);
				check.setVisibility(View.VISIBLE);
				check.setImageResource(R.drawable.circle_check);	
				
    		}			
			
			return;
		}
		
		// update labels
		SingletonLoginData.getInstance().getContactLabels().clear();
		SingletonLoginData.getInstance().getContactLabels().addAll(mLabels);
		finish();
	}
	
	
	
	public void onDummy(View view) {
	}


}
