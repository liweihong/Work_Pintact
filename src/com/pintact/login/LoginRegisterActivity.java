package com.pintact.login;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pintact.R;
import com.pintact.data.SignupRequest;
import com.pintact.utility.HttpConnection;
import com.pintact.utility.MyActivity;
import com.pintact.utility.SingletonLoginData;
import com.pintact.utility.SingletonNetworkStatus;

public class LoginRegisterActivity extends MyActivity {
		TextView errMsg;
		EditText middleName;
	

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.login_register);
			setupUI(findViewById(R.id.login_register_view));
			hideLeft();
			
			/*
			Button foo = (Button)findViewById(R.id.lrFacebook);
			foo.setText(Html.fromHtml(getString(R.string.lr_facebook)));
			foo.setFocusableInTouchMode(true);
			foo.requestFocus();

			Button twtr = (Button)findViewById(R.id.lrTwitter);
			twtr.setText(Html.fromHtml(getString(R.string.lr_twitter)));		
			*/
			
			TextView next = (TextView)findViewById(R.id.lrNext);
			next.setText(Html.fromHtml(getString(R.string.lr_text_next)));		
			
			middleName = (EditText)findViewById(R.id.lrFirstName);
			middleName.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_WORDS);
			
			middleName = (EditText)findViewById(R.id.lrLastName);
			middleName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
			
			errMsg = (TextView)findViewById(R.id.lrErrorMsg);
			errMsg.setText(Html.fromHtml(getString(R.string.lr_error_msg)));
			errMsg.setVisibility(View.GONE);
			
			View.OnClickListener clickLn = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// go to next page
					onNext(v);
					// new pin
				}
			};

			next.setClickable(true);
			next.setOnClickListener(clickLn);
			
			TextView tv = (TextView)findViewById(R.id.actionBar);
			tv.setText(getResources().getString(R.string.ab_register));

			Button btn = (Button)findViewById(R.id.actionBarBtn);
			btn.setOnClickListener(clickLn);
		}
		
		public void onDummy(View view) {
		}
		
		boolean validField(String str, String info) {
			if ( str == null || str == "" || str.length() == 0) {
				myDialog("Field Empty", "Please enter your " + info);
				return false;
			}
			
			return true;
		}
		
		public static boolean isEmailValid(String email) {
		    boolean isValid = false;

		    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		    CharSequence inputStr = email;

		    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		    Matcher matcher = pattern.matcher(inputStr);
		    if (matcher.matches()) {
		        isValid = true;
		    }
		    return isValid;
		}
		
		public void onNext(View view) {
			
			// Do validation
			String fn = ((EditText) findViewById(R.id.lrFirstName)).getText().toString();
			String mn = ((EditText) findViewById(R.id.lrMiddleName)).getText().toString();
			String ln = ((EditText) findViewById(R.id.lrLastName)).getText().toString();
			String email = ((EditText) findViewById(R.id.lrEmail)).getText().toString();
			String phone = ((EditText) findViewById(R.id.lrMobile)).getText().toString();
			if ( !validField(fn, "first name") ||
				 !validField(ln, "last name") ||
				 !validField(email, "email") ||
				 !validField(phone, "mobile"))
				return;
			
			
			// check if valid email address
			if ( !isEmailValid(email)) {
				myDialog("Invalid Input", "Please enter a valid email address ");
				return;
			}
			
			SignupRequest req = new SignupRequest();
			req.setFirstName(fn);
			req.setMiddleName(mn);
			req.setLastName(ln);
			req.setEmailId(email);
			req.setPassword(phone);
			SingletonLoginData.getInstance().setSignupRequest(req);

			// check if email is still available
			String params = "{\"email\":\"" + email + "\"}";
			
			SingletonNetworkStatus.getInstance().setActivity(this);
			String path = "/api/users/emailAvailability.json";
			new HttpConnection().access(this, path, params, "POST");
			
		}
		
		public void onPostNetwork() {
			if ( SingletonNetworkStatus.getInstance().getCode() != 200 ) {
				myDialog(SingletonNetworkStatus.getInstance().getMsg(), 
						SingletonNetworkStatus.getInstance().getErrMsg());
				return;
			}
			
			if ( SingletonNetworkStatus.getInstance().getJson().contains("false")) {
				myDialog("Email Already In Use", "A Pintact user already exists with the specified email, please choose a different e-mail or go to Forgot Password.");
				return;
			}
			
			Intent it = new Intent(this, LoginRegisterPINActivity.class);
			startActivity(it);
			overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left );
		}

	    @Override
	    public void onBackPressed() {
	    	System.out.println("OnBackPressed - Activity.");
	    	finish();
			overridePendingTransition( R.anim.slide_in_down, R.anim.slide_out_down );
	    	return;
	    }
		

		
}
