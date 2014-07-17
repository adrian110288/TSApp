package com.tracking.mobile;

import java.util.zip.Inflater;

import com.tracking.fragments.*;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.*;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class JobSearchActivity extends FragmentActivity {

    private FragmentTabHost mTabHost;
	private Bundle bundle;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jobsearch);

		Typeface fontNormal = Typeface.createFromAsset(getAssets(), "weblysleekuil.ttf");
		
		ActionBar bar = getActionBar();

		SpannableString s = new SpannableString(getResources().getString(R.string.app_name));
	    s.setSpan(new TypefaceSpan(this, "Minecrafter_3.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    bar.setTitle(s);
		
		ConnectivityManager conn = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conn.getActiveNetworkInfo();
		
		mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
	    mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
	    
	    Bundle b1 = new Bundle();
	    b1.putInt("p", 0);
	    
	    Bundle b2 = new Bundle();
	    b2.putInt("p", 1);
	    
	    Bundle b3 = new Bundle();
	    b3.putInt("p", 2);
	    
	    bundle = new Bundle();
	    bundle.putInt("p", 0);
	    mTabHost.addTab(mTabHost.newTabSpec("allJobs").setIndicator("ALL JOBS"), JobListFragment.class, bundle);
	    	    
	    bundle = new Bundle();
	    bundle.putInt("p", 1);
	    mTabHost.addTab(mTabHost.newTabSpec("recomm").setIndicator("NEARBY"), JobListFragment.class, bundle);
	    
	    bundle = new Bundle();
	    bundle.putInt("p", 2);
	    mTabHost.addTab(mTabHost.newTabSpec("selected").setIndicator("SELECTED"), JobListFragment.class, bundle);
	    
	    if(getIntent().hasExtra("tabPosition")) {
			mTabHost.setCurrentTab(getIntent().getIntExtra("tabPosition", 0));
		}
	    
	    mTabHost.getTabWidget().setDividerDrawable(null);
	    mTabHost.getTabWidget().setLeftStripDrawable(null);
	    
	    for(int i=0;i<mTabHost.getTabWidget().getChildCount();i++) {
	    	
	    	TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
	    	
	    	if (tv == null) continue;
	    	else tv.setTypeface(fontNormal);
	    }
	}

	@Override
	public void onBackPressed() {}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inf = getMenuInflater();
		inf.inflate(R.menu.menu, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	
		switch(item.getItemId()){
			case R.id.logout: {
				SharedPreferences pref = getSharedPreferences(getResources().getString(R.string.LOGIN_PREF), MODE_PRIVATE);
				SharedPreferences.Editor e = pref.edit();
				e.remove("usrnm");
				e.remove("pass");
				e.commit();
				startActivity(new Intent(getApplicationContext(), LoginActivity.class));
				return true;
			}
			
			case R.id.statusreport:{
				startActivity(new Intent(getApplicationContext(), StatusReport.class));
			}
			
			default: return false;
				
		}
	
	}
	
}


