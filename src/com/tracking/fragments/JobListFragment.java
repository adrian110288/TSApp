package com.tracking.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.prefs.Preferences;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.test.AssertionFailedError;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.tracking.mobile.JobDetails;
import com.tracking.mobile.R;


public class JobListFragment extends Fragment implements LocationListener, OnItemClickListener, OnChildClickListener{

	private int position;
	private LocationManager manager;
    private double lat, lng;
	private ProgressDialog progressDIalog;
	private JSONArray mainlist, completedJobs, ongoingJobs;
	private ListView listview;
	private ExpandableListView expListview;
	private NetworkInfo networkInfo;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		lat = location.getLatitude();
        lng = location.getLongitude();
        
        new GetJobsTask().execute(position);
	}
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
		View list;
		position = getArguments().getInt("p");
				
		if(position == 2){
			
			list = inflater.inflate(R.layout.jobs_listview_alternative, container, false);
			expListview = (ExpandableListView) list.findViewById(R.id.expandableListView1);
			expListview.setOnChildClickListener(this);
			expListview.setGroupIndicator(null);
		}
		
		else {
			
			list = inflater.inflate(R.layout.jobs_listview, container, false);
			listview = (ListView) list.findViewById(android.R.id.list);	
			listview.setOnItemClickListener(this);
		}
		
		return list;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
			
		if(position == 0) {
			prepareProgressDialog();
			new GetJobsTask().execute(position);
		}
		
		else if(position==1) {

			prepareProgressDialog();
			
			manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
	        Criteria criteria = new Criteria();
	        criteria.setAccuracy(Criteria.ACCURACY_FINE);
	        manager.requestSingleUpdate(criteria, this, null);			
		}
		
		else if(position == 2) {
			
			prepareProgressDialog();
			new GetJobsTask().execute(position);
		}
	}

	private class GetJobsTask extends AsyncTask<Integer, Void, JSONArray> {
	
		private int position;
		
		@Override
		protected JSONArray doInBackground(Integer... params) {
			
			position = params[0];
			String url = "";
			
			if(position==0) {
				url = getResources().getString(R.string.SERVICE_URL_ALLJOBS);
				Log.i("ALLJOBS URL", url);
			}
			else if(position == 1) {
				url = getResources().getString(R.string.SERVICE_URL_SUGGESTED)+"lat="+lat+"&lng="+lng+"&";
				Log.i("NEARBY URL", url);
			}
			else if(position == 2) url = getResources().getString(R.string.SERVICE_URL_SELECTED)+"emp="+getActivity().getSharedPreferences(getResources().getString(R.string.LOGIN_PREF), getActivity().MODE_PRIVATE).getString("usrnm", "error")+"&";
			
			return ApplicationUtilities.fetchJobs(getResources().getString(R.string.URL_PARAMS_FORMAT), url, "POST");
		}

		protected void onPostExecute(final JSONArray output) {
		
			getActivity().runOnUiThread(new Runnable() {	
					
				@Override
				public void run() {	
					
					Log.d("onPostExecute", "onCreateView");
					
					//Toast.makeText(getActivity(), output.length()+" jobs available", Toast.LENGTH_SHORT).show();
					mainlist = output;
					RowModel row;
					
					try {
						
						if(position==2) {
							
							String[] headersList = JobListFragment.this.getResources().getStringArray(R.array.listHeaders);
							HashMap<String, List<RowModel>> childrenList = new HashMap<String, List<RowModel>>();
							
							List<RowModel> completedList = new ArrayList<RowModel>();
							List<RowModel> ongoingList = new ArrayList<RowModel>();
							
							completedJobs = new JSONArray();
							ongoingJobs = new JSONArray();
							
							for(int i=0;i<output.length(); i++) {
								
								JSONObject o = output.getJSONObject(i);
								boolean completed = o.getBoolean("completed");
								
								if(completed) {
									row = new RowModel(o.getString("jobtitle"), o.getString("address"), o.getString("id"));
									completedList.add(row);
									completedJobs.put(o);
								}
								else {
									String[] date = o.getString("dateDue").split("/");
									Date dateDue = new Date(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]));
									
									row = new RowModel(0, o.getString("jobtitle"), o.getString("address"), dateDue);
									ongoingList.add(row);
									ongoingJobs.put(o);
								}
							}
							
							childrenList.put(headersList[0], completedList);
							childrenList.put(headersList[1], ongoingList);
							
							CustomExpandableListAdapter expListAdapter = new CustomExpandableListAdapter(getActivity(), headersList, childrenList);
							
							expListview.setAdapter(expListAdapter);
							
							for (int i=0;i<expListAdapter.getGroupCount();i++) {
								expListview.expandGroup(i);
							}
							
						}
						
						if(position!=2) {
							
							List<RowModel> list = new ArrayList<RowModel>();
							
							for(int i=0;i<output.length(); i++) {
								JSONObject o = output.getJSONObject(i);
								String[] date = o.getString("dateDue").split("/");
								Date dateDue = new Date(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]));
							
								if(o.isNull("distance")) row = new RowModel(0, o.getString("jobtitle"), o.getString("address"), dateDue);
								else {
									String dis = o.getString("distance");
									row = new RowModel(0, o.getString("jobtitle"), o.getString("address"), dateDue, dis);
								}
								
								list.add(row);
							}
					
							JobListFragment.this.listview.setAdapter(new JobsAdapter(getActivity(), R.layout.jobs_list_item, list));
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					progressDIalog.dismiss();
				}
			});
		}
	}
	
	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	private void prepareProgressDialog(){
		progressDIalog = new ProgressDialog(getActivity());
		progressDIalog.setTitle("Loading jobs");
		progressDIalog.setMessage("Please wait...");
		progressDIalog.setCancelable(false);
		progressDIalog.show();
	}

	@Override
	public void onItemClick(AdapterView<?> l, View v, int itemPosition, long id) {
		
		try {
			String jobClicked = mainlist.getJSONObject(itemPosition).toString();
					
			Intent i = new Intent(getActivity().getApplicationContext(), JobDetails.class);
			i.putExtra("jobJson", jobClicked);
			i.putExtra("tabPosition", position);
			
			if (position == 1) {
				i.putExtra("myLocation_lat", lat);
				i.putExtra("myLocation_long", lng);
			}
			
			startActivity(i);
			
		} catch (JSONException e) {
			Log.i("Error occured when parsing JSON on list clicked", e.getMessage());
		}
		catch(Exception e) {
			Log.i("Error occured when list clicked", e.getMessage());
		}
		
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

		String jobClicked = "";
		try {
			if (groupPosition == 0) jobClicked = completedJobs.getJSONObject(childPosition).toString();
			else jobClicked = ongoingJobs.getJSONObject(childPosition).toString();
			
			Intent i = new Intent(getActivity().getApplicationContext(), JobDetails.class);
			i.putExtra("jobJson", jobClicked);
			i.putExtra("tabPosition", position);
			Log.d("TAB POSITION", position+"");
			startActivity(i);
		} 
		
		catch (JSONException e) {
			e.getMessage();
		}
		
		return true;
	}
}

