package com.pintact.profile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import com.pintact.R;
import com.pintact.data.ProfileDTO;
import com.pintact.utility.SingletonLoginData;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author manish.s
 *
 */
public class ProfileGridAdapter extends ArrayAdapter<ProfileDTO> {
 Context context;
 int layoutResourceId;
 ArrayList<ProfileDTO> data = new ArrayList<ProfileDTO>();

 public ProfileGridAdapter(Context context, int layoutResourceId,
   ArrayList<ProfileDTO> data) {
  super(context, layoutResourceId, data);
  this.layoutResourceId = layoutResourceId;
  this.context = context;
  this.data = data;
 }

 @Override
 public View getView(int position, View convertView, ViewGroup parent) {

	   LayoutInflater inflater = ((Activity) context).getLayoutInflater();
	   View row = inflater.inflate(layoutResourceId, parent, false);
	
	   TextView title = (TextView) row.findViewById(R.id.pm_name);
	   TextView name = (TextView) row.findViewById(R.id.pm_profile_id);
	   ImageView image = (ImageView) row.findViewById(R.id.pm_image);

	   ProfileDTO item = data.get(position);
	   title.setText(item.getUserProfile().getName());
	   name.setText(SingletonLoginData.getInstance().getUserData().pin);
	   
	   Bitmap bm = SingletonLoginData.getInstance().getBitmap(position);
	   if ( item.getUserProfile().getPathToImage() != null &&
			item.getUserProfile().getPathToImage() != ""  &&
			bm == null) 
	   {
		   loadImage(position, item.getUserProfile().getPathToImage(), image);
	   } else if ( bm != null ) {
		   image.setImageBitmap(bm);
		   image.setScaleType(ImageView.ScaleType.CENTER_CROP);
		   
	   }
	   
	   return row;
 }
 
 public void loadImage(int index, String photo_url_str, ImageView profile_photo) {
	 System.out.println("Loading image from " + photo_url_str);
	 new DownloadImageTask(profile_photo).execute(photo_url_str, Integer.toString(index));		 
 }
 
 private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;
	    int position;

	    public DownloadImageTask(ImageView bmImage) {
	        this.bmImage = bmImage;
	    }

	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        position = Integer.parseInt(urls[1]);
	        Bitmap mIcon11 = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            System.out.println("Error" + e.getMessage());
	        }
	        return mIcon11;
	    }

	    protected void onPostExecute(Bitmap result) {
	    	SingletonLoginData.getInstance().setBitmap(position, result);
	        bmImage.setImageBitmap(result);
	        bmImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
	    }
}
 

 }
