package com.greentech.wnd.android;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.greentech.wnd.android.bean.Disease;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.customcontrols.CornerListView;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.NetUtil;

/**
 * 显示查询出来的病因列表
 */
public class DiseaseListActivity extends Activity {

	// 显示列表
	private CornerListView mCornerListView = null;
	public int mStart = 0;
	public int mPageSize = Integer.parseInt(Constant.pageSize);
	private int mAgriProductId;
	private String diseaseFeatures;
	private Handler mHandler = new Handler();
	private List<Disease> mItemList = new ArrayList<Disease>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diseaselist);
		mCornerListView = (CornerListView) findViewById(R.id.diseaseDetail);
		mCornerListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String content = mItemList.get(position).getContent();
				String title = mItemList.get(position).getTitle();
				String url_img=mItemList.get(position).getImg();
                int diseaseId=mItemList.get(position).getId();
				Intent intent = new Intent(DiseaseListActivity.this,
						DiseaseInfoActivity.class);
				intent.putExtra("content", content);
				intent.putExtra("title", title);
				intent.putExtra("url_img", url_img);
				intent.putExtra("diseaseId", diseaseId);
				startActivity(intent);
//				overridePendingTransition(R.anim.fade_in_right,
//						android.R.anim.fade_out);

			}
		});
		// 获取前一个界面传入的参数
		mAgriProductId = getIntent().getIntExtra("agriProductId", 0);
		diseaseFeatures = getIntent().getStringExtra("diseaseFeatures");
		new Thread(new Runnable() {
			@Override
			public void run() {
				mItemList = getDiseaseListData();
				// 此处的listView.setAdapter要放在itemList =
				// getDiseaseListData()之后，必须在run方法里面执行
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mCornerListView.setAdapter(new ListNameAdapter(
								DiseaseListActivity.this));
					}
				});
			}
		}).start();
	}

	/**
	 * 根据参数查数据
	 */
	public List<Disease> getDiseaseListData() {

		/**
		 * 1.流 2.String 3.Json 4.List 加载数据（新建线程）
		 */

		InputStream is = NetUtil.post(Constant.SERVIER_PATH
				+ "/json/findDiseases.action", "start=" + mStart + "&pageSize="
				+ mPageSize + "&agriProductId=" + mAgriProductId
				+ "&diseaseFeatures=" + diseaseFeatures);
		final String str = NetUtil.getStringFromInputStream(is);
		JsonObject json = (JsonObject) GsonUtil.parse(str);
		List<Disease> itemslist = GsonUtil.fromJson(json.get("diseaseList"),
				new TypeToken<List<Disease>>() {
				}.getType());
		return itemslist;
	}

	
	
	
	
	
	// listView的适配器
	class ListNameAdapter extends BaseAdapter {
		Context context;

		public ListNameAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return mItemList.size();
		}

		@Override
		public Object getItem(int position) {
			return mItemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 显示listView的内容

			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.main_tab_setting_list_item, null);
			}                                                      
			TextView diseaseName = (TextView) convertView
					.findViewById(R.id.disease_list_item_text);
			TextView score=(TextView)convertView.findViewById(R.id.score);
			int i=getAverage(mItemList,position);
			
			score.setText("匹配度:"+i+"%");
			diseaseName.setText(mItemList.get(position).getTitle());
			return convertView;
		}

	}
	//得到比例数字
	public static int getAverage(List<Disease> listDisease,int position){
		
		List<Double> list  = new ArrayList<Double>();
		for(Disease d:listDisease){
			list.add(Double.valueOf(d.getScore()));
		}
		double max=Collections.max(list)+0.2;
		double min=0;
		double m=listDisease.get(position).getScore();
		double averageDouble=(m-min)/(max-min)*100;
		int average=(int) Math.round(averageDouble);
		return average;
	}

}
