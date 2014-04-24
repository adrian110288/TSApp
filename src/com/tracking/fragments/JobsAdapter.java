package com.tracking.fragments;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.tracking.mobile.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class JobsAdapter extends ArrayAdapter<RowModel>{

	private Context context;
	private Typeface fontNormal, fontBold;
	
	public JobsAdapter(Context context, int resourceId, List<RowModel> items) {
		super(context, resourceId, items);
        this.context = context;
        fontNormal = Typeface.createFromAsset(context.getAssets(), "weblysleekuil.ttf");
		fontBold = Typeface.createFromAsset(context.getAssets(), "weblysleekuisb.ttf");
	}
	
	private class ViewHolder {
        ImageView imageView;
        TextView title;
        TextView address;
        TextView dueDate;
        TextView distance;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RowModel rowItem = getItem(position);
 
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.jobs_list_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.jobTitle);
            holder.title.setTypeface(fontNormal, Typeface.BOLD);
            holder.address = (TextView) convertView.findViewById(R.id.jobAddress);
            holder.address.setTypeface(fontNormal);
            holder.imageView = (ImageView) convertView.findViewById(R.id.colorSquare);
            holder.dueDate = (TextView) convertView.findViewById(R.id.dueDate);
            holder.dueDate.setTypeface(fontNormal);
            holder.distance = (TextView) convertView.findViewById(R.id.distance);
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
                
        if(days<3) holder.imageView.setImageResource(R.drawable.square_red);
        else if(days>=3 && days<5) holder.imageView.setImageResource(R.drawable.square_orange);
        else holder.imageView.setImageResource(R.drawable.square_green);
        return convertView;
    }
    
    private int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }
}
