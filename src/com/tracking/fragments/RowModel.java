package com.tracking.fragments;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.graphics.YuvImage;
import android.util.Log;

public class RowModel {

	private int imageId;
	private String jobTitle, jobAddress, jobId;
	private Date date;
	private double distance;
	private static DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
	
	
	public RowModel(int imageId, String jobTitle, String jobAddress, Date date) {
		super();
		this.imageId = imageId;
		this.jobTitle = jobTitle;
		this.jobAddress = jobAddress;
		this.date = date;
		this.distance = -1;
		}
	
	public RowModel(int imageId, String jobTitle, String jobAddress, Date date, String distanceIn) {
		super();
		this.imageId = imageId;
		this.jobTitle = jobTitle;
		this.jobAddress = jobAddress;
		this.date = date;
		this.distance = setDistance(distanceIn);
		}
	
	public RowModel(String jobTitle, String jobAddress, String jobIdin) {
		this.jobTitle = jobTitle;
		this.jobAddress = jobAddress;
		this.jobId = jobIdin;
	}
	
	public String getJobId() {
		return jobId;
	}

	public double getDistance() {
		return this.distance;
	}
	
	public String getDistanceAsString(){
		
		DecimalFormat decf = new DecimalFormat("#0.00");
		
		if(distance!=-1) return decf.format(distance)+" miles";
		else return "";
		
		
	}

	public double setDistance(String distance) {
		return (Double.parseDouble(distance)/1000)*0.621371;
	}

	public Date getDate() {
		return date;
	}
	
	public String getDateString(){
		return df.format(date);
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getImageId() {
		return imageId;
	}
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
	public String getJobTitle() {
		return jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	public String getJobAddress() {
		return jobAddress;
	}
	public void setJobAddress(String jobAddress) {
		this.jobAddress = jobAddress;
	}
}
