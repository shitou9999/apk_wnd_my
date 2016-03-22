package com.greentech.wnd.android;

import java.util.ArrayList;
import java.util.List;

import com.greentech.wnd.android.ExpertProduct.ViewHolder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ExpertTypeActivity extends Activity {
	private ListView lv_type;
	private ListView lv_level;
	private ExpertTypeAdapter adapter_type;
	private ExpertLevelAdapter adapter_level;
	private List<String> list_type = new ArrayList<String>();//专家类型
	private List<String> list_level = new ArrayList<String>();//专家级别
    public String expert_type;
    private TextView cancel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.expert_type);
		
		list_type.add("农业技术推广员");
		list_type.add("农业技术协会专家");
		list_type.add("农业合作社专家");
		list_type.add("农资店专家");
		list_type.add("企业技术专家");
		list_type.add("种养殖大户");
		list_type.add("科研专家");
		list_type.add("教学专家");
		list_type.add("植保部门专家");
		
		list_level.add("研究员");
		list_level.add("副研究员");
		list_level.add("高级农艺师");
		list_level.add("农艺师");
		list_level.add("初级农艺师");
		list_level.add("经验专家");
		cancel=(TextView) findViewById(R.id.cancel);
		lv_type = (ListView) findViewById(R.id.expert_type);
		lv_level = (ListView) findViewById(R.id.expert_level);
		adapter_type = new ExpertTypeAdapter();
		adapter_level = new ExpertLevelAdapter();
		lv_type.setAdapter(adapter_type);
		lv_level.setAdapter(adapter_level);
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ExpertTypeActivity.this.finish();
				
			}
		});
		lv_type.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				expert_type=list_type.get(position);
				lv_level.setVisibility(View.VISIBLE);
			}
		});
		lv_level.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("data", expert_type+"-"+list_level.get(position));
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
	
	class ExpertTypeAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return list_type.size();
		}

		@Override
		public Object getItem(int position) {
			return list_type.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView==null){
				holder=new ViewHolder();
				convertView = LayoutInflater.from(ExpertTypeActivity.this).inflate(
						R.layout.expert_product_type_item, null);
				holder.textView=(TextView) convertView.findViewById(R.id.text);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}
			holder.textView.setText(list_type.get(position));
			return convertView;
		}
		
	}
	class ExpertLevelAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			return list_level.size();
		}
		
		@Override
		public Object getItem(int position) {
			return list_level.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolderName holderName;
			if(convertView==null){
				holderName=new ViewHolderName();
				convertView = LayoutInflater.from(ExpertTypeActivity.this).inflate(
						R.layout.expert_product_name_item, null);
				holderName.textViewName=(TextView) convertView.findViewById(R.id.text);
				convertView.setTag(holderName);
			}else{
				holderName=(ViewHolderName) convertView.getTag();
			}
			holderName.textViewName.setText(list_level.get(position));
			return convertView;
		}
		
	}
	public static class ViewHolder {
		public TextView textView;
	}
	public static class ViewHolderName {
		public TextView textViewName;
	}
}
