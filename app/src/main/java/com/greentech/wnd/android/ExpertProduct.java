package com.greentech.wnd.android;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.greentech.wnd.android.bean.AgriProd;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.NetUtil;

/**
 * 擅长的作物
 * 
 * @author zhoufazhan
 * 
 */
public class ExpertProduct extends BaseActivity implements OnClickListener {
	private List<AgriProd> productTypeList;
	private List<AgriProd> productNameList;
	private ArrayList<String> gridNameList;
	private ViewHolder holder;
	private ViewHolderName holderName;
	private TextView back;// 返回按钮
	private TextView submit;// 提交按钮
	private TextView text_title;// 提交按钮
	private GridView choiced;// 显示已选择的作物
	private ListView productTypeListView;// 产品类别
	private ListView productNameListView;// 产品名称
	private ProductTypeAdapter productTypeAdapter;
	private ProductNameAdapter productNameAdapter;
	private GridViewAdapter gridViewAdapter;
	private String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choice_product);
		
		
		init();
		laodData();
		if(getIntent().getStringExtra("from")!=null){
			title=getIntent().getStringExtra("from");
			text_title.setText(title);
		}
		productTypeAdapter = new ProductTypeAdapter();
		productNameAdapter = new ProductNameAdapter();
		gridViewAdapter = new GridViewAdapter();
		gridNameList = new ArrayList<String>();
		// 大的种类点击事件
		productTypeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 改变选中的item的颜色
				productTypeAdapter.setSelectedPosition(position);
				productTypeAdapter.notifyDataSetChanged();
				int i = productTypeList.get(position).getId();// 记录type的id
				loadDataByName(i);
			}

		});
		// 小的种类点击事件
		productNameListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				choiced.setVisibility(View.VISIBLE);
				String name = productNameList.get(position).getType();
				// 没有被选过则增加，只能选一种类型提问
				if(StringUtils.isNotBlank(title)){
					gridNameList.add(name);
					if(gridNameList.size()>1){
						gridNameList.remove(1);
					}
					
					choiced.setAdapter(gridViewAdapter);
				}
				//最多关注6个
				else if (!hasSelected(name)) {
					gridNameList.add(name);
					if(gridNameList.size()>6){
						gridNameList.remove(6);
					}
					choiced.setAdapter(gridViewAdapter);
				}

			}

		});

		// grodView点击事件，当点击的时候删除单击选项
		choiced.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				gridNameList.remove(position);
				gridViewAdapter.notifyDataSetChanged();

			}
		});
	}

	// 判断是否已经被选中,被选过返回true
	public boolean hasSelected(String name) {
		for (int i = 0; i < gridNameList.size(); i++) {
			if (name.equals(gridNameList.get(i))) {
				return true;
			}
		}

		return false;
	}

	// 查询大类下的农产品
	private void loadDataByName(final int i) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				InputStream is = NetUtil.get(Constant.SERVIER_PATH
						+ "/json/showAgriProdsName.action", "id=" + i);
				String str = NetUtil.getStringFromInputStream(is);
				JsonObject json = (JsonObject) GsonUtil.parse(str);
				productNameList = GsonUtil.fromJson(
						json.get("agriProdNameList"),
						new TypeToken<List<AgriProd>>() {
						}.getType());
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						productNameListView.setVisibility(View.VISIBLE);
						productNameListView.setAdapter(productNameAdapter);

					}
				});

			}
		}).start();

	}

	// 查询农产品大类
	private void laodData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				InputStream is = NetUtil.get(Constant.SERVIER_PATH
						+ "/json/showAgriProdsType.action");
				String str = NetUtil.getStringFromInputStream(is);
				JsonObject json = (JsonObject) GsonUtil.parse(str);
				productTypeList = GsonUtil.fromJson(json.get("agriProdList"),
						new TypeToken<List<AgriProd>>() {
						}.getType());
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						productTypeListView.setAdapter(productTypeAdapter);

					}
				});

			}
		}).start();

	}

	private void init() {
		back = (TextView) findViewById(R.id.cancel);
		submit = (TextView) findViewById(R.id.submit);
		text_title = (TextView) findViewById(R.id.title);
		choiced = (GridView) findViewById(R.id.gridView);
		productTypeListView = (ListView) findViewById(R.id.type_product);
		productNameListView = (ListView) findViewById(R.id.name_product);
		back.setOnClickListener(this);
		submit.setOnClickListener(this);
	}

	/**
	 * gridView使用的adapter
	 * 
	 * @author zhoufazhan
	 * 
	 */
	class GridViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return gridNameList.size();
		}

		@Override
		public Object getItem(int position) {
			return gridNameList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(ExpertProduct.this).inflate(
						R.layout.grid_choiced_name_item, null);
				textView = (TextView) convertView.findViewById(R.id.text);

			} else {
				textView = (TextView) convertView.findViewById(R.id.text);
			}
			textView.setText(gridNameList.get(position));
			return convertView;
		}

	}

	/*
	 * 产品类别的adapter
	 */
	class ProductTypeAdapter extends BaseAdapter {
		// 记录选中的位置
		private int selectedPosition = -1;

		public void setSelectedPosition(int selectedPosition) {
			this.selectedPosition = selectedPosition;
		}

		@Override
		public int getCount() {
			return productTypeList.size();
		}

		@Override
		public Object getItem(int position) {
			return productTypeList.get(position).getType();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(ExpertProduct.this).inflate(
						R.layout.expert_product_type_item, null);
				holder.textView = (TextView) convertView
						.findViewById(R.id.text);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.textView.setText(productTypeList.get(position).getType());

			// 判断item是否被选中，如果被选中改变颜色
			if (selectedPosition == position) {
				convertView
						.setBackgroundResource(R.drawable.select_listview_background);
			} else {
				convertView.setBackgroundColor(Color.TRANSPARENT);
			}
			return convertView;
		}

	}

	public static class ViewHolder {
		public TextView textView;
	}

	/*
	 * 产品类别的adapter
	 */
	class ProductNameAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return productNameList.size();
		}

		@Override
		public Object getItem(int position) {
			return productNameList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holderName = new ViewHolderName();
				convertView = LayoutInflater.from(ExpertProduct.this).inflate(
						R.layout.expert_product_name_item, null);
				holderName.textViewName = (TextView) convertView
						.findViewById(R.id.text);
				convertView.setTag(holderName);
			} else {
				holderName = (ViewHolderName) convertView.getTag();
			}
			holderName.textViewName.setText(productNameList.get(position)
					.getType());
			return convertView;
		}

	}

	public static class ViewHolderName {
		public TextView textViewName;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel:
			finish();
			break;

		case R.id.submit:
			Intent data1 = new Intent();
			data1.putStringArrayListExtra("name", gridNameList);
			setResult(RESULT_OK, data1);
			finish();
			break;
		}

	}

}
