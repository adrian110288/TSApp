package com.tracking.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tracking.mobile.R;

import android.app.*;
import android.content.Context;
import android.graphics.Typeface;
import android.util.JsonReader;
import android.util.Log;

public class ApplicationUtilities {
	
	private static HttpURLConnection conn;
	private static JSONObject js;
	
	public static JSONObject processLogin(Context c, String username, String password, String serviceUrl, String requestMethod){
	
		try {
			String URL_PARAMS = "username="+username+"&password="+password;
			URL url = new URL(serviceUrl+URL_PARAMS);

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(requestMethod);
		    conn.setChunkedStreamingMode(0);
		    			    		    
		    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
		    	
		    	BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    	String msg = in.readLine();
		    	js = new JSONObject(msg);
		    }
		    else {
		    	js = new JSONObject();
		    	js.put("status", c.getResources().getString(R.string.LOGIN_CONNECTION_ERROR));
		    	js.put("message", conn.getResponseMessage());	    	
		    }
					
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return js;
	}
	
	public static JSONArray fetchJobs(String resultFormat, String serviceUrl, String requestMethod){
				
		try {
			
			URL url = new URL(serviceUrl+resultFormat);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(requestMethod);
		    conn.setChunkedStreamingMode(0);
		    
		    Log.d("URL", url.toString());
		    
		    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
		    	
		    	BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    	String output = in.readLine();
		    	JSONArray jsonOutput = new JSONArray(output);
		    	Log.d("JSON", jsonOutput.toString());
		    	return jsonOutput;
		    }
		    else {
		    	JSONObject js = new JSONObject();
		    	js.put("message", conn.getResponseMessage());
		    	return new JSONArray().put(js);
		    }
				
		} catch (MalformedURLException e) {
			e.printStackTrace();
		
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		finally{
		    conn.disconnect();
		}
		return null;
	}
	
	public static boolean toggleSelection(boolean selection, String employeeId, String job, String resultFormat, String serviceUrl, String requestMethod){
		
		try{
			String encodedJob = URLEncoder.encode(job, "UTF-8");
			
			String stringUrl = serviceUrl+resultFormat+"&select="+!selection+"&job="+encodedJob;
			if (selection == false) stringUrl+="&empId="+employeeId;
			
			URL url = new URL(stringUrl);
			
			Log.d("URL", url.toString());
			
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(requestMethod);
		    conn.setChunkedStreamingMode(0);
		    
		    Log.d("URL", url.toString());
		    
		    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
		    	
		    	BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    	String output = in.readLine();
		    	return Boolean.parseBoolean(output);
		    }
		    else {
		    	JSONObject js = new JSONObject();
		    	js.put("message", conn.getResponseMessage());
		    	return false;
		    }
				
		} catch (MalformedURLException e) {
			e.printStackTrace();
		
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		finally{
		    conn.disconnect();
		}
		return false;
	}
	
	public static boolean markCompleted(String job, String resultFormat, String serviceUrl, String requestMethod){
		
		try{
			String encodedJob = URLEncoder.encode(job, "UTF-8");
			String stringUrl = serviceUrl+resultFormat+"&job="+encodedJob;
			
			URL url = new URL(stringUrl);
			
			Log.d("URL", url.toString());
			
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(requestMethod);
		    conn.setChunkedStreamingMode(0);
		    
		    Log.d("URL", url.toString());
		    
		    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
		    	
		    	BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    	String output = in.readLine();
		    	return Boolean.parseBoolean(output);
		    }
		    else {
		    	JSONObject js = new JSONObject();
		    	js.put("message", conn.getResponseMessage());
		    	return false;
		    }
				
		} catch (MalformedURLException e) {
			e.printStackTrace();
		
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		finally{
		    conn.disconnect();
		}
		return false;
	}
	
	public static boolean submitReport(String job, String employeeId, String reportText, String resultFormat, String serviceUrl, String requestMethod){
		
		try{
			String encodedJob = URLEncoder.encode(job, "UTF-8");
			
			JSONObject data = new JSONObject();
			data.put("job", encodedJob);
			data.put("emp", employeeId);
			data.put("report", reportText);
			
			String stringUrl = serviceUrl+resultFormat+"&data="+data.toString();
			
			URL url = new URL(stringUrl);
			
			Log.d("URL", url.toString());
			
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(requestMethod);
		    conn.setChunkedStreamingMode(0);
		    
		    Log.d("URL", url.toString());
		    
		    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
		    	
		    	BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    	String output = in.readLine();
		    	return Boolean.parseBoolean(output);
		    }
		    else {
		    	JSONObject js = new JSONObject();
		    	js.put("message", conn.getResponseMessage());
		    	return false;
		    }
				
		} catch (MalformedURLException e) {
			e.printStackTrace();
		
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		finally{
		    conn.disconnect();
		}
		return false;
	}
	
	public static String submitEmployeeStatus(String url, String method, String resultFormat, String paramsString){
		
		try {
			String encodedParams = URLEncoder.encode(paramsString, "UTF-8");
			String stringUrl = url+"params="+encodedParams+"&"+resultFormat;
			URL urlConn = new URL(stringUrl);
			
			conn = (HttpURLConnection) urlConn.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(method);
		    conn.setChunkedStreamingMode(0);
		    
		    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
		    	
		    	BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    	String output = in.readLine();
		    	return output;
		    }
		    else {
		    	JSONObject js = new JSONObject();
		    	js.put("submit", false);
		    	js.put("message", conn.getResponseMessage());
		    	return js.toString();
		    }
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		finally{
		    conn.disconnect();
		}
		
		return null;
	}

}
