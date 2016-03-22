package com.greentech.wnd.android;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.greentech.wnd.android.bean.Disease;
import com.greentech.wnd.android.bean.VegetableTech;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.NetUtil;
import com.greentech.wnd.android.util.UserInfo;

/**
 * 显示收藏的病虫害信息(没有涮新和分页功能)
 * 
 * @author zhoufazhan
 * 
 */
public class TechCollectionFragment extends Fragment {
	private ListView listView;
	private List<VegetableTech> mItemList = new ArrayList<VegetableTech>();
	private Handler mHandler = new Handler();
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	private String recommend;// 是否查询推荐技术
	private String content;// 是否查询推荐技术

	public TechCollectionFragment(String recommend,String content) {
		this.recommend = recommend;
		this.content=content;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.collection_techlist, container,
				false);
		listView = (ListView) view.findViewById(R.id.tech_collection);

		new Thread(new Runnable() {
			@Override
			public void run() {
				mItemList = getTechListData();
				// 此处的listView.setAdapter要放在itemList =
				// getDiseaseListData()之后，必须在run方法里面执行
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						listView.setAdapter(new ListNameAdapter(getActivity(),
								mItemList));
					}
				});
			}
		}).start();

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				VegetableTech vegetableTech = mItemList.get(position);
				Intent intent = new Intent();
				intent.setClass(getActivity(), TechDetailActivity.class);
				intent.putExtra("id", vegetableTech.getId().toString());
				intent.putExtra("title", vegetableTech.getTitle().toString());
				intent.putExtra("content", vegetableTech.getContent()
						.toString());
				intent.putExtra("type", vegetableTech.getType().toString());
				intent.putExtra("source", vegetableTech.getSource()==null?"":vegetableTech.getSource());
				if(vegetableTech.getReleaseTime()!=null){
					intent.putExtra("releaseTime",format.format(vegetableTech.getReleaseTime()));
				}
				else{
					intent.putExtra("releaseTime","");
				}
						
				startActivity(intent);

			}
		});
		return view;
	}

	// listView的适配器
	class ListNameAdapter extends BaseAdapter {
		Context context;
		List<VegetableTech> data;

		public ListNameAdapter(Context context, List<VegetableTech> data) {
			this.context = context;
			this.data = data;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
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
						R.layout.collection_tech_list_item, null);
			}
			TextView diseaseName = (TextView) convertView
					.findViewById(R.id.tech_list_item);

			diseaseName.setText(Html.fromHtml(data.get(position).getTitle()));
			return convertView;
		}

	}

	public List<VegetableTech> getTechListData() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(recommend)) {
			map.put("type_tech", recommend);
			map.put("content_tech", content);
		} else {
			map.put("userId", UserInfo.getUserId(getActivity()));
			map.put("type", 3);
		}

		InputStream is = NetUtil.post(Constant.SERVIER_PATH
				+ "/json/showCollectionTech.action", map);
		if(is!=null){
			final String str = NetUtil.getStringFromInputStream(is);
			JsonObject json = (JsonObject) GsonUtil.parse(str);
			List<VegetableTech> itemslist = GsonUtil.fromJson(json.get("techList"),
					new TypeToken<List<VegetableTech>>() {
					}.getType());
			return itemslist;
		}
		
		return new ArrayList<VegetableTech>();
	}
}
