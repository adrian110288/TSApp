package com.tracking.mobile;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tracking.fragments.*;
*/
import android.support.v4.*;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.R.bool;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.TableLayout.LayoutParams;

public class JobDetails extends FragmentActivity {

	private JSONObject extra;
	private TextView jobTitle, jobAddress, label_jobDesc, jobDesc, label_dateAdded, label_jobPriority, dateAdded, label_dateDue, dateDue, distance, jobPriority;
	private Button directionsButton, selectJobButton, markCompletedButton;
	private ToggleButton toggleView;
	public String title, add, desc, added, due, dist, employeeId, priority;
	private double lat, lng, myLat, myLng;
	//private LatLng target;
	//private GoogleMap map;
	private static DateFormat df;
	private Typeface fontNormal, fontBold;
	private ProgressDialog progressDialog;
	private boolean isSelected = false;
	private Intent i;
	private int tabPosition = 0;
	/*
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.job_details);
		
		tabPosition = getIntent().getIntExtra("tabPosition", 0);
		
		i = new Intent(this, JobSearchActivity.class);
		i.putExtra("tabPosition", tabPosition);
			
		prepareViews();
		adjustTypeface();
		prepareMap();
	}*/
	/*
	private class SelectJobTask extends AsyncTask<Object, Void, Boolean> {

		boolean jobdismiss;

		@Override
		protected void onPostExecute(Boolean result) {
			
			if (jobdismiss && result) Toast.makeText(getApplicationContext(), "Job dropped", Toast.LENGTH_SHORT).show();
			if (jobdismiss && !result) Toast.makeText(getApplicationContext(), "Job could not be dropped", Toast.LENGTH_SHORT).show();
			if (!jobdismiss && result) Toast.makeText(getApplicationContext(), "Job has been selected", Toast.LENGTH_SHORT).show();
			if (!jobdismiss && !result) Toast.makeText(getApplicationContext(), "Job could not be selected", Toast.LENGTH_SHORT).show();
		
			JobDetails.this.startActivity(i);
			
			progressDialog.dismiss();
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			
			Boolean isSelected = (Boolean)params[0];
			String title = (String) params[1];
			jobdismiss = isSelected;
			String user = getSharedPreferences(getResources().
					getString(R.string.LOGIN_PREF), MODE_PRIVATE).getString("usrnm", "error"); 
						
			return ApplicationUtilities.toggleSelection(isSelected, user, title,  getResources().
					getString(R.string.URL_PARAMS_FORMAT), getResources().
					getString(R.string.SERVICE_URL_TOGGLE_JOB_SELECTION), getResources().
					getString(R.string.REQUEST_METHOD));
		}
		
	}
	
	private class MarkCompletedTask extends AsyncTask<Object, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Object... params) {
			
			String title = (String) params[0];
			return ApplicationUtilities.markCompleted(title, getResources().getString(R.string.URL_PARAMS_FORMAT), getResources().getString(R.string.SERVICE_URL_MARK_JOB_COMPLETED), getResources().getString(R.string.REQUEST_METHOD));
		}

		@Override
		protected void onPostExecute(Boolean result) {
		
			if (result) Toast.makeText(getApplicationContext(), "Job marked completed", Toast.LENGTH_SHORT).show();
			else Toast.makeText(getApplicationContext(), "Job couldn't be marked completed", Toast.LENGTH_SHORT).show();
			
			JobDetails.this.startActivity(i);
		}
		
	}
	
	private void centerMap(CameraUpdate loc, GoogleMap mapIn){
		mapIn.moveCamera(loc);
	}
	
	private void buildAlertDialog(boolean isSelected, String title, final boolean setCompleted){
		
		final Boolean isSel = isSelected;
		final String jobTitle = title;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(JobDetails.this).setTitle("Confirm your choice");
		AlertDialog dialog;
		
		builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {

				progressDialog = new ProgressDialog(JobDetails.this);
				progressDialog.setMessage("Loading jobs...");
				progressDialog.show();
				
				if(setCompleted) new MarkCompletedTask().execute(jobTitle);
				else new SelectJobTask().execute(isSel, jobTitle);
				
			}
		});
			
		builder.setNegativeButton("No", null);
		
		if (setCompleted) {
			builder.setMessage("Mark this job as compeleted?");
		}
		
		else if(!isSelected) {
			builder.setMessage("Do you want to select this job?");
		}
		
		else if(isSelected) {
			builder.setMessage("Do you want to abandon this job?");
		}
		
		dialog = builder.create();
		dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
		dialog.show();
	}

	private void prepareViews(){
		
		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);

		SpannableString s = new SpannableString(getResources().getString(R.string.app_name));
	    s.setSpan(new TypefaceSpan(this, "weblysleekuil.ttf"), 0, s.length(),
	            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	 
	    bar.setTitle(s);
		
		
		
		try {
			extra = new JSONObject(getIntent().getStringExtra("jobJson"));
			title = extra.getString("jobtitle");
			add = extra.getString("address");
			if(extra.has("distance")) dist= extra.getString("distance");
			desc = extra.getString("jobdescription");
			priority = extra.getString("priority");
			added = extra.getString("dateadded");
			due = extra.getString("dateDue");
			lat = Double.parseDouble(extra.getString("latitude"));
			lng = Double.parseDouble(extra.getString("longitude"));
			if (getIntent().hasExtra("myLocation_lat")) myLat = getIntent().getDoubleExtra("myLocation_lat", 0);
			if (getIntent().hasExtra("myLocation_long")) myLng = getIntent().getDoubleExtra("myLocation_long", 0);
			
			Log.d("lat", myLat+"");
			Log.d("lng", myLng+"");

			
			if (extra.has("employeeId")) employeeId = extra.getString("employeeId"); 
			
		} catch (JSONException e) {
			Log.i("Error occured when getting Intent extras", e.getMessage());
		}
		
		df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
		
		String[] sDue = due.split("/");
		String[] sAdded = added.split("/");
		
		jobTitle = (TextView) findViewById(R.id.details_title);
		jobTitle.setText(title);
		jobAddress = (TextView) findViewById(R.id.details_address_text);
		jobAddress.setText(add);
		
		if(tabPosition == 1){
			distance = (TextView) findViewById(R.id.details_distance);
			distance.setText(new DecimalFormat("#0.00").format(Double.parseDouble(dist)/1000*0.621371)+" miles");
		}
		
		label_jobDesc = (TextView) findViewById(R.id.details_desc_label);
		jobDesc = (TextView) findViewById(R.id.details_desc_text);
		jobDesc.setText(desc);
		label_dateAdded = (TextView) findViewById(R.id.details_dateadded_label);
		dateAdded = (TextView) findViewById(R.id.details_dateadded_text);
		dateAdded.setText(df.format(new Date(Integer.parseInt(sAdded[2]), Integer.parseInt(sAdded[1]), Integer.parseInt(sAdded[0]))));
		label_dateDue = (TextView) findViewById(R.id.details_datedue_label);
		dateDue = (TextView) findViewById(R.id.details_datedue_text);
		dateDue.setText(df.format(new Date(Integer.parseInt(sDue[2]), Integer.parseInt(sDue[1]), Integer.parseInt(sDue[0]))));
		label_jobPriority = (TextView) findViewById(R.id.details_priority_label);
		jobPriority = (TextView) findViewById(R.id.details_piority_text);
		
		if(priority.equals("-1")) jobPriority.setText("Low");
		else if(priority.equals("0")) jobPriority.setText("Normal");
		else if(priority.equals("1")) jobPriority.setText("High");
		
		selectJobButton = (Button) findViewById(R.id.details_selectButton);
		markCompletedButton = (Button) findViewById(R.id.details_mark_as_completed_button);
		
		if (tabPosition == 1) {
			selectJobButton.setVisibility(Button.VISIBLE);
			selectJobButton.setTextColor(getResources().getColor(R.color.COLOR_WHITE));
			isSelected = false;
		}
		
		if (tabPosition ==2){
			selectJobButton.setVisibility(Button.VISIBLE);
			markCompletedButton.setVisibility(Button.VISIBLE);
					
			selectJobButton.setTextColor(getResources().getColor(R.color.COLOR_WHITE));
			markCompletedButton.setTextColor(getResources().getColor(R.color.COLOR_WHITE));

			selectJobButton.setText("DROP");
			isSelected = true;
		}
		
		selectJobButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				buildAlertDialog((Boolean)isSelected, title, false);
			}
		});
		
		markCompletedButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				buildAlertDialog(false, title, true);
			}
		});
	}
	
	private void adjustTypeface(){
		
		fontNormal = Typeface.createFromAsset(getAssets(), "weblysleekuil.ttf");
		fontBold = Typeface.createFromAsset(getAssets(), "weblysleekuisb.ttf");
		
		jobTitle.setTypeface(fontNormal);
		jobAddress.setTypeface(fontNormal);		
		if (dist != null) distance.setTypeface(fontBold);
		label_jobDesc.setTypeface(fontNormal);
		jobDesc.setTypeface(fontNormal);
		label_dateAdded.setTypeface(fontNormal);
		dateAdded.setTypeface(fontNormal);
		label_dateDue.setTypeface(fontNormal);
		dateDue.setTypeface(fontNormal);
		label_jobPriority.setTypeface(fontNormal);
		jobPriority.setTypeface(fontNormal);
		
	}
	
	private void prepareMap(){
		
		SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        map = mapFrag.getMap();
        UiSettings uisettings = map.getUiSettings();
        uisettings.setScrollGesturesEnabled(false);
        map.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));

		target = new LatLng(lat, lng);
		final CameraUpdate location = CameraUpdateFactory.newLatLngZoom(target, 15);
		centerMap(location, map);
		
		if (tabPosition == 1) {
		
			directionsButton = (Button) findViewById(R.id.directionsButton);
			directionsButton.setVisibility(Button.VISIBLE);
			directionsButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//centerMap(location, map);
					
					try {
						String uri = new String("http://maps.google.com/maps?saddr="+myLat+","+myLng+"&daddr="+lat+","+lng);
						Log.d("uri", uri);
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
						startActivity(intent);
					}
					catch(Exception e) {
						e.printStackTrace();
					}			
				}
			});
		}
		
		toggleView = (ToggleButton) findViewById(R.id.toggleMapType);
		toggleView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if(isChecked) map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				else map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			}
		});
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
		
		case android.R.id.home:{
			startActivity(i);
			return true;
		}
		
		default :return false;
		}
	}
*/
	@Override
	public void onBackPressed() {}

}
