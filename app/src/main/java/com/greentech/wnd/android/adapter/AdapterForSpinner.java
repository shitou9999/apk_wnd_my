package com.greentech.wnd.android.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.greentech.wnd.android.R;

public class AdapterForSpinner extends BaseAdapter implements SpinnerAdapter{

	
	private Context context;
    private LayoutInflater inflater;
    private List<String> strings;
     
    public AdapterForSpinner(Context context,List<String> strings) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.strings = new ArrayList<String>();
        this.strings = strings;
    }
	@Override
	public int getCount() {
		return strings.size();
	}

	@Override
	public Object getItem(int position) {
		return strings.get(position);
	}

	@Override
	public long getItemId(int position) {
		 return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.spinner_show, null);
            holder.textView = (TextView) convertView.findViewById(R.id.tv);
            convertView.setTag(holder);
        } else {
           holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(strings.get(position).toString());
        return convertView;
	}
	/*
	   * 加载下拉布局
	   */
	   @Override
	    public View getDropDownView(int position, View convertView, ViewGroup parent) {
	        ViewHolder holder;
	        if (convertView == null) {
	            holder = new ViewHolder();
	            convertView = inflater.inflate(R.layout.spinner_down, null);
	            holder.textView = (TextView) convertView.findViewById(R.id.tv);
	            convertView.setTag(holder);
	        } else {
	          holder = (ViewHolder) convertView.getTag();
	        }
	        holder.textView.setText(strings.get(position).toString());
	        return convertView;
	    }
	 
	public static class ViewHolder{
	       TextView textView;
	   }

}
