package com.tracking.mobile;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tracking.fragments.ApplicationUtilities;
import com.tracking.mobile.R.string;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class StatusReport extends Activity implements LocationListener {

	private Spinner mainSpinner, jobsSpinner;
	private ArrayAdapter<String> mainAdapter, jobsAdapter;
	private String[] ongoingJobs;
	private JSONArray jobs;
	private ProgressDialog progressDialog;
	private TextView maintitle, addCommentLabel, dataLabel, timeLabel, activityLabel, jobLabel, locLabel;
	private EditText comment;
	private double lat, lng;
	private SpannableString wordtoSpan;
	private Button submit;
	private DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
	private DateFormat tf = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.UK);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status_report);
		
		prepareProgressDialog();
		new GetSelectedJobsTask().execute();
		
		acquireLocation();
		findAllViews();
		adjustTypeface();
		prepareAllViews();		
	}
	
	private void prepareAllViews(){
		
		wordtoSpan = new SpannableString(locLabel.getText().toString());        
		wordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), 9, wordtoSpan.length()-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		locLabel.setText(wordtoSpan);

		Date currDate = new Date();
		
		dataLabel.setText("Date: "+df.format(currDate));
		timeLabel.setText("Time: "+tf.format(currDate));
		
		mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				
				if (position == 2 || position ==3 || position ==5) {
					
					jobLabel.setVisibility(View.VISIBLE);
					jobsSpinner.setVisibility(View.VISIBLE);
				}
				else {
					jobLabel.setVisibility(View.INVISIBLE);
					jobsSpinner.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		jobsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		jobsSpinner.setVisibility(View.INVISIBLE);
		
		String[] statusList = getResources().getStringArray(R.array.statusReport);
		ArrayAdapter<String> mainAdapter =new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, statusList);
		mainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mainSpinner.setAdapter(mainAdapter);
		
		submit.setEnabled(false);
		submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {	
				
				JSONObject reportJson = new JSONObject();
				try {
					reportJson.put("employee", StatusReport.this.getSharedPreferences(getResources()
							.getString(R.string.LOGIN_PREF), StatusReport.this.MODE_PRIVATE)
							.getString("usrnm", "error"));
					reportJson.put("date", new Date());
					reportJson.put("lat", lat);
					reportJson.put("long", lng);
					reportJson.put("activity", mainSpinner.getSelectedItem().toString());
					
					if (jobsSpinner.getVisibility() == View.VISIBLE) {
						
						for (int i=0;i<jobs.length(); i++) {
							
							try {
								JSONObject o = jobs.getJSONObject(i);
								String title = o.getString("jobtitle");
								
								if(((String)jobsSpinner.getSelectedItem()).toString().equals(title)) {
									reportJson.put("job", o.getString("id"));
									break;
								}
							} catch (JSONException e) {
								e.printStackTrace(); }
						}
					}
					
					if (comment.getText().toString() != null) reportJson.put("comment", comment.getText().toString());
								
				} catch (JSONException e) {
					e.printStackTrace();
				}
	
				new SubmitEmployeeReport().execute(reportJson.toString());
			}
		});
		
		submit.setTypeface(Typeface.createFromAsset(getAssets(), "weblysleekuil.ttf"));
		
	}
	
	private void findAllViews(){
		
		submit = (Button) findViewById(R.id.submitEmployeeReport);
		maintitle = (TextView) findViewById(R.id.reportTitle);
		addCommentLabel = (TextView) findViewById(R.id.addComentLabel);
		dataLabel = (TextView) findViewById(R.id.dateLabel);
		timeLabel = (TextView) findViewById(R.id.timeLabel);
		activityLabel = (TextView) findViewById(R.id.mainSpinnerLabel);
		jobLabel = (TextView) findViewById(R.id.jobLabel);
		locLabel = (TextView) findViewById(R.id.locLabel);
		mainSpinner = (Spinner) findViewById(R.id.statusSpinner);
		jobsSpinner = (Spinner) findViewById(R.id.jobsSpinner);
		comment = (EditText) findViewById(R.id.addComment);
	}

	private void acquireLocation(){
		
		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        manager.requestSingleUpdate(criteria, this, null);	
	}
	
	private class SubmitEmployeeReport extends AsyncTask<String, Void, String>{

		@Override
		protected void onPostExecute(String result) {
			
			try {
				JSONObject o = new JSONObject(result);
				Toast.makeText(StatusReport.this, o.getString("message"), Toast.LENGTH_SHORT).show();
				
			} catch (JSONException e) {
				e.printStackTrace();
			}	
		}

		@Override
		protected String doInBackground(String... params) {
			
			String url = getResources().getString(R.string.SERVICE_URL_SUBMIT_EMPLOYEE_STATUS);
			String method = getResources().getString(R.string.REQUEST_METHOD);
			String resultFormat = getResources().getString(R.string.URL_PARAMS_FORMAT);
			
			return ApplicationUtilities.submitEmployeeStatus(url, method, resultFormat, params[0]);
		}
		
	}
	
	
	
	private class GetSelectedJobsTask extends AsyncTask<Integer, Void, JSONArray>{

		@Override
		protected JSONArray doInBackground(Integer... params) {

			String url = getResources().getString(R.string.SERVICE_URL_SELECTED)+"emp="+getApplicationContext().getSharedPreferences(getResources().getString(R.string.LOGIN_PREF), getApplicationContext().MODE_PRIVATE).getString("usrnm", "error")+"&";
			
			return ApplicationUtilities.fetchJobs(getResources().getString(R.string.URL_PARAMS_FORMAT), url, "POST");
	
		}

		@Override
		protected void onPostExecute(JSONArray result) {

			jobs = result;
			ongoingJobs = new String[result.length()];
			
			for (int i=0;i<result.length(); i++) {
				
				try {
					JSONObject o = result.getJSONObject(i);
					String title = o.getString("jobtitle");
					ongoingJobs[i] = title;
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			jobsAdapter = new ArrayAdapter<String>(StatusReport.this, android.R.layout.simple_spinner_item, ongoingJobs);
			jobsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			jobsSpinner.setAdapter(jobsAdapter);	
						
			progressDialog.dismiss();
		}
		
	}
	
	private void prepareProgressDialog(){
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Loading jobs");
		progressDialog.setMessage("Please wait...");
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	
	private void adjustTypeface(){
		
		Typeface fontNormal = Typeface.createFromAsset(getAssets(), "weblysleekuil.ttf");
		
		maintitle.setTypeface(fontNormal);
		addCommentLabel.setTypeface(fontNormal);
		dataLabel.setTypeface(fontNormal);
		timeLabel.setTypeface(fontNormal);
		activityLabel.setTypeface(fontNormal);
		jobLabel.setTypeface(fontNormal);
		locLabel.setTypeface(fontNormal);
	}

	@Override
	public void onLocationChanged(Location location) {

		lat = location.getLatitude();
        lng = location.getLongitude();
        
        wordtoSpan = new SpannableString("Location: Obtained"); 
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(102, 204, 0)), 9, wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		locLabel.setText(wordtoSpan);
		submit.setEnabled(true);
		submit.setText("Submit your status");
		Toast.makeText(getApplicationContext(), "You can submit your status now", Toast.LENGTH_SHORT).show();
        
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
