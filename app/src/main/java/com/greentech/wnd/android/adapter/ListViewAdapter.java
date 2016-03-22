package com.greentech.wnd.android.adapter;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.greentech.wnd.android.DiseaseInfoMainActivity;
import com.greentech.wnd.android.R;
import com.greentech.wnd.android.cache.ImageLoader;
import com.greentech.wnd.android.constant.Constant;

/**
 * @author SunnyCoffee
 * @date 2014-2-2
 * @version 1.0
 * @desc 适配器
 * 
 */
public class ListViewAdapter extends BaseAdapter {

	private ViewHolder holder;
	private List<String> list;
	private Context context;
	private List<String> list_url;
	private List<Integer> list_id;
	private List<String> list_content;
	private boolean mBusy = false;
	private ImageLoader mImageLoader;
	public ListViewAdapter(Context context, List<String> list_url,
			List<String> list_title,List<String> list_content,List<Integer> list_id) {
		this.list = list_title;
		this.context = context;
		this.list_url = list_url;
		this.list_content = list_content;
		this.list_id = list_id;
		mImageLoader = new ImageLoader(context);
	}

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}

	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

	@Override
	public int getCount() {
		return list.size()/2;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.listview_item, null);
			holder.text1 = (TextView) convertView.findViewById(R.id.text1);
			holder.text2 = (TextView) convertView.findViewById(R.id.text2);
			holder.iv1 = (ImageView) convertView.findViewById(R.id.iv1);
			holder.iv2 = (ImageView) convertView.findViewById(R.id.iv2);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.iv1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,
						DiseaseInfoMainActivity.class);
				intent.putExtra("title", list.get(position*2));
				intent.putExtra("content", list_content.get(position*2));
				intent.putExtra("imagePath", list_url.get(position*2));
				intent.putExtra("id", list_id.get(position*2));
				context.startActivity(intent);
				
			}
		});
		holder.iv2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,
						DiseaseInfoMainActivity.class);
				intent.putExtra("title", list.get((position*2)+1));
				intent.putExtra("content", list_content.get((position*2)+1));
				intent.putExtra("imagePath", list_url.get((position*2)+1));
				intent.putExtra("id", list_id.get((position*2)+1));
				context.startActivity(intent);
				
			}
		});
		String url1 = "";
		String url2 = "";
		holder.text1.setText(list.get(position*2));
		holder.text2.setText(list.get((position*2)+1));
		
		url1 = list_url.get(position*2);
		url2 = list_url.get((position*2)+1);
		Log.i(Constant.TAG, "url1="+url1);
		holder.iv1.setImageResource(R.drawable.defaultimage);
		holder.iv2.setImageResource(R.drawable.defaultimage);

		if (!mBusy) {
			if(StringUtils.isNotBlank(url1)){
				mImageLoader.DisplayImage(url1, holder.iv1, false);
			}
			if(StringUtils.isNotBlank(url2)){
				mImageLoader.DisplayImage(url2, holder.iv2, false);
			}
			
			holder.text1.setText(list.get(position*2));
			holder.text2.setText(list.get((position*2)+1));
		} else {
			mImageLoader.DisplayImage(url1, holder.iv1, false);
			holder.text1.setText(list.get(position*2));
			mImageLoader.DisplayImage(url2, holder.iv2, false);
			holder.text2.setText(list.get((position*2)+1));
		}
		return convertView;
	}

	private static class ViewHolder {
		TextView text1;
		TextView text2;
		ImageView iv1;
		ImageView iv2;
	}

}
