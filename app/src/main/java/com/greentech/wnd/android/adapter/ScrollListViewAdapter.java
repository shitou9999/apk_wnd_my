package com.greentech.wnd.android.adapter;

import java.util.List;

import com.greentech.wnd.android.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ScrollListViewAdapter extends BaseAdapter {
	private ViewHolder holder;
	private List<String> listString;
	private List<Drawable> listDrawable;
	private Context context;

	public ScrollListViewAdapter(Context context,List<String> listString,
			List<Drawable> listDrawable) {
		this.context=context;
		this.listString = listString;
		this.listDrawable = listDrawable;
	}

	@Override
	public int getCount() {
		return listString.size();
	}

	@Override
	public Object getItem(int position) {
		return listString.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			holder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.scrolllistview, null);
			holder.text=(TextView)convertView.findViewById(R.id.titletext);
			holder.iv=(ImageView)convertView.findViewById(R.id.image);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		holder.text.setText(listString.get(position));
		holder.iv.setImageDrawable(listDrawable.get(position));
		return convertView;
	}

	class ViewHolder {
		TextView text;
		ImageView iv;
	}

}
