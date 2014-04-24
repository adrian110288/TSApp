package com.tracking.mobile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tracking.fragments.ApplicationUtilities;
import com.tracking.mobile.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private String SERVICE_URL="", REQUEST_METHOD, FORMAT;
	private Button login;
	private EditText usernameEditText, passwordEditText;
	private HttpURLConnection conn;
	private String URL_PARAMS;
	private SharedPreferences pref;
	private static String LOGIN_PREF = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SERVICE_URL = getResources().getString(R.string.SERVICE_URL_LOGIN);
		REQUEST_METHOD = getResources().getString(R.string.REQUEST_METHOD);
		FORMAT = getResources().getString(R.string.URL_PARAMS_FORMAT);
		LOGIN_PREF = getResources().getString(R.string.LOGIN_PREF);
		
		pref = getSharedPreferences(LOGIN_PREF, MODE_PRIVATE);
		String username = pref.getString("usrnm", "error");
		
		if(!username.equals("error")) {
			String psw = pref.getString("pass", "error");
			startActivity(new Intent(this, JobSearchActivity.class));
		}
		else{
			setContentView(R.layout.activity_login);
			
			login = (Button) findViewById(R.id.loginButton);
			usernameEditText = (EditText) findViewById(R.id.username);
			passwordEditText = (EditText) findViewById(R.id.password);
			
			login.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new LoginTask().execute();
				}
			});	
		}
	}
	
	private class LoginTask extends AsyncTask<Void, Void, JSONObject> {
		@Override
		protected JSONObject doInBackground(Void... params) {
			
			return ApplicationUtilities.processLogin(LoginActivity.this, usernameEditText.getText().toString(), passwordEditText.getText().toString(), SERVICE_URL, REQUEST_METHOD);
		}

		protected void onPostExecute(final JSONObject json) {
		
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try{
						int status = Integer.parseInt(json.getString("status"));						
						
						if(status == 1) {
							
							SharedPreferences.Editor e = pref.edit();
							e.remove("usrnm");
							e.putString("usrnm", usernameEditText.getText().toString());
							e.remove("pass");
							e.putString("pass", passwordEditText.getText().toString());
							e.commit();
							
							startActivity(new Intent(getApplicationContext(), JobSearchActivity.class));
						}
						else {
							String message = json.getString("message");
							Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
						}
				
					} catch (JSONException e) {
						Log.e("ERROR", e.getMessage());
					}			
				}
			});
		}
	}
}
