package com.greentech.wnd.android;

import java.io.InputStream;
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
public class DiseaseCollectionFragment extends Fragment {
	private ListView listView;
	private List<Disease> mItemList = new ArrayList<Disease>();
	private Handler mHandler = new Handler();
	private String recommend;// 是否查询推荐病害
	private String content;// 是否查询推荐病害

	public DiseaseCollectionFragment(String recommend,String content) {
		this.recommend = recommend;
		this.content=content;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.collection_diseaselist,
				container, false);
		listView = (ListView) view.findViewById(R.id.diseaseDetail_collection);
                    
		new Thread(new Runnable() {
			@Override
			public void run() {
				mItemList = getDiseaseListData();
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
				String content = mItemList.get(position).getContent();
				String title = Html.fromHtml(mItemList.get(position).getTitle()).toString();
				int diseaseId = mItemList.get(position).getId();
				Intent intent = new Intent(getActivity(),
						DiseaseInfoActivity.class);
				intent.putExtra("content", content);
				intent.putExtra("title", title);
				intent.putExtra("diseaseId", diseaseId);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.fade_in_right,
						android.R.anim.fade_out);
			}
		});
		return view;
	}

	// listView的适配器
	class ListNameAdapter extends BaseAdapter {
		Context context;
		List<Disease> data;

		public ListNameAdapter(Context context, List<Disease> data) {
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
						R.layout.collection_disease_list_item, null);
			}
			TextView diseaseName = (TextView) convertView
					.findViewById(R.id.disease_list_item);

			diseaseName.setText(Html.fromHtml(data.get(position).getTitle()));
			return convertView;
		}

	}

	public List<Disease> getDiseaseListData() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(recommend)){
			map.put("type_product", recommend);
			map.put("content_disease", content);
		}else{
			map.put("userId", UserInfo.getUserId(getActivity()));
			map.put("type", 2);
		}
		
		InputStream is = NetUtil.post(Constant.SERVIER_PATH
				+ "/json/showCollectionDiseases.action", map);
		final String str = NetUtil.getStringFromInputStream(is);
		JsonObject json = (JsonObject) GsonUtil.parse(str);
		List<Disease> itemslist = GsonUtil.fromJson(json.get("diseaseList"),
				new TypeToken<List<Disease>>() {
				}.getType());
		return itemslist;
	}
}
