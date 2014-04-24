package com.tracking.fragments;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.tracking.mobile.ReportingActivity;


import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter{

	private Context c;
	private String[] listHeaders;
	private HashMap<String, List<RowModel>> listChildren;
	private Typeface fontNormal, fontBold;
	
	public CustomExpandableListAdapter(Context c, String[] listHeaders, HashMap<String, List<RowModel>> listChildren) {
		
		this.c = c;
		this.listHeaders = listHeaders;
		this.listChildren = listChildren;
		
		fontNormal = Typeface.createFromAsset(c.getAssets(), "weblysleekuil.ttf");
		fontBold = Typeface.createFromAsset(c.getAssets(), "weblysleekuisb.ttf");
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return listChildren.get(listHeaders[groupPosition]).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}
	
	private class ViewHolder {
        ImageView imageView;
        TextView title;
        TextView address;
        TextView dueDate;
        TextView distance;
    }
	
	private class ViewHolderCompleted {
        TextView title;
        TextView address;
    }

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		
		final RowModel rowItem = (RowModel) getChild(groupPosition, childPosition);
		LayoutInflater mInflater = (LayoutInflater) c.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View view;
		
		if(groupPosition == 0) {
			
			view = mInflater.inflate(com.tracking.mobile.R.layout.jobs_completed_list_item,null);
			TextView title = (TextView) view.findViewById(com.tracking.mobile.R.id.completed_job_title);
			title.setTypeface(fontNormal, Typeface.BOLD);
			TextView address = (TextView) view.findViewById(com.tracking.mobile.R.id.completed_job_address);
			address.setTypeface(fontNormal);
			title.setText(rowItem.getJobTitle());
			address.setText(rowItem.getJobAddress());
			
			ImageButton reportImage = (ImageButton) view.findViewById(com.tracking.mobile.R.id.imageButton1);
			
			reportImage.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					//Log.d("imageButton", rowItem.getJobId());
					Intent intent = new Intent(c, ReportingActivity.class);
					intent.putExtra("jobtitle", rowItem.getJobTitle());
					intent.putExtra("jobId", rowItem.getJobId());
					c.startActivity(intent);
				}
			});
			
			return view;
		}
		
		if(groupPosition == 1) {
			
			ViewHolder holder = null;
	 
	        if (convertView == null) {
	            convertView = mInflater.inflate(com.tracking.mobile.R.layout.jobs_list_item, null);
	            holder = new ViewHolder();
	            holder.title = (TextView) convertView.findViewById(com.tracking.mobile.R.id.jobTitle);
	            holder.title.setTypeface(fontNormal, Typeface.BOLD);
	            holder.address = (TextView) convertView.findViewById(com.tracking.mobile.R.id.jobAddress);
	            holder.address.setTypeface(fontNormal);
	            holder.imageView = (ImageView) convertView.findViewById(com.tracking.mobile.R.id.colorSquare);
	            holder.dueDate = (TextView) convertView.findViewById(com.tracking.mobile.R.id.dueDate);
	            holder.dueDate.setTypeface(fontNormal);
	            holder.distance = (TextView) convertView.findViewById(com.tracking.mobile.R.id.distance);
	            holder.distance.setTypeface(fontNormal);
	            
	            convertView.setTag(holder);
	        } else
	            holder = (ViewHolder) convertView.getTag();
	 
	        holder.title.setText(rowItem.getJobTitle());
	        holder.address.setText(rowItem.getJobAddress());
	        holder.imageView.setImageResource(rowItem.getImageId());
	        holder.dueDate.setText(rowItem.getDateString());
	        holder.distance.setText(rowItem.getDistanceAsString());
	        if(rowItem.getDistance() != -1){
	        	
	        }
	        
	        
	        
	        int days = Math.abs(daysBetween(new Date(), rowItem.getDate())+1);
	                
	        if(days<3) holder.imageView.setImageResource(com.tracking.mobile.R.drawable.square_red);
	        else if(days>=3 && days<5) holder.imageView.setImageResource(com.tracking.mobile.R.drawable.square_orange);
	        else holder.imageView.setImageResource(com.tracking.mobile.R.drawable.square_green);
	        return convertView;
		}
		return null;
		
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		
		return listChildren.get(listHeaders[groupPosition]).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return listHeaders[groupPosition];
	}

	@Override
	public int getGroupCount() {
		return listHeaders.length;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		
		String headerTitle = (String) getGroup(groupPosition);
		LayoutInflater infalInflater = (LayoutInflater) this.c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if (convertView == null) {
            convertView = infalInflater.inflate(com.tracking.mobile.R.layout.jobs_list_group, null);
        }
 
        TextView lblListHeader = (TextView) convertView.findViewById(com.tracking.mobile.R.id.group_item);
        lblListHeader.setText(headerTitle);
        lblListHeader.setTypeface(fontNormal);
        
        TextView count = (TextView) convertView.findViewById(com.tracking.mobile.R.id.group_item_count);
        count.setTypeface(fontNormal);
       
        count.setText(listChildren.get(listHeaders[groupPosition]).size()+"");
        
 
        return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	private int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

	
}
