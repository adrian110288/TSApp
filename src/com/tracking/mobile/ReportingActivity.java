package com.tracking.mobile;

import com.tracking.fragments.ApplicationUtilities;
import com.tracking.fragments.JobListFragment;
import com.tracking.fragments.TypefaceSpan;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ReportingActivity extends Activity {

	private String jobId;
	private String jobtitle;
	private TextView staticText, title;
	private Typeface fontNormal, fontBold;
	private Button submit;
	private ProgressDialog progressDialog;
	private EditText reportText; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reporting_layout);
		
		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);

		SpannableString s = new SpannableString(getResources().getString(R.string.app_name));
	    s.setSpan(new TypefaceSpan(this, "weblysleekuil.ttf"), 0, s.length(),
	            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	 
	    bar.setTitle(s);
		
		fontNormal = Typeface.createFromAsset(getAssets(), "weblysleekuil.ttf");
		fontBold = Typeface.createFromAsset(getAssets(), "weblysleekuisb.ttf");
		
		jobId = getIntent().getStringExtra("jobId");
		jobtitle = getIntent().getStringExtra("jobtitle");
		
		staticText = (TextView) findViewById(R.id.text1);
		staticText.setTypeface(fontNormal);
		title = (TextView) findViewById(R.id.report_job_title);
		title.setTypeface(fontNormal);
		title.setText(jobtitle);
		
		reportText = (EditText) findViewById(R.id.report_edittext);
		
		submit = (Button) findViewById(R.id.submitReport);
		submit.setTypeface(fontNormal);
		submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if ((reportText.getText().toString()).equals("") || (reportText.getText().toString()) == null) {
					Toast.makeText(getApplicationContext(), "Cannot submit empty report", Toast.LENGTH_SHORT).show();
				}
				else {
					progressDialog = new ProgressDialog(ReportingActivity.this);
					progressDialog.setMessage("Submitting report ... ");
					progressDialog.show();
					
					new SubmitReportTask().execute(getIntent().getStringExtra("jobId"));
				}
					
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
		
		case android.R.id.home:{
			
			Intent i = new Intent(this, JobSearchActivity.class);
			i.putExtra("tabPosition", 2);
			startActivity(i);
			return true;
		}
		
		default :return false;
		}
	}
	
	private class SubmitReportTask extends AsyncTask<Object, Void, Boolean> {

		@Override
		protected void onPostExecute(Boolean result) {
			
			Intent i = new Intent(ReportingActivity.this, JobSearchActivity.class);
			i.putExtra("tabPosition", 2);
			ReportingActivity.this.startActivity(i);
			
			progressDialog.dismiss();
			
			if (result) Toast.makeText(getApplicationContext(), "Report submitted", Toast.LENGTH_SHORT).show();
			else Toast.makeText(getApplicationContext(), "Report could not be submitted...", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			
			String id = (String) params[0];
			String username = getSharedPreferences(getResources().getString(R.string.LOGIN_PREF), MODE_PRIVATE).getString("usrnm", "error");
			String report = reportText.getText().toString();
			String resultFormat = getResources().getString(R.string.URL_PARAMS_FORMAT);
			String url = getResources().getString(R.string.SERVICE_URL_SUBMIT_REPORT);
			String requestMethod = getResources().getString(R.string.REQUEST_METHOD);
			
			return ApplicationUtilities.submitReport(id, username, report, resultFormat, url, requestMethod);
		}
		
	}
}
