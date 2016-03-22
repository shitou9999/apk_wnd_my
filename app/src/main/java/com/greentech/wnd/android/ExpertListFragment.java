package com.greentech.wnd.android;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.greentech.wnd.android.bean.User;
import com.greentech.wnd.android.cache.ImageLoader;
import com.greentech.wnd.android.common.MyViewHolder;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.NetUtil;
import com.greentech.wnd.android.view.AutoListView;
import com.greentech.wnd.android.view.AutoListView.OnLoadListener;
import com.greentech.wnd.android.view.AutoListView.OnRefreshListener;

/**
 * 专家列表，选择专家
 * 
 * @author wlj
 * 
 */
public class ExpertListFragment extends Fragment implements OnRefreshListener,
		OnLoadListener {

	private Button mBtn_back;
	private String type;

	public ExpertListFragment(String type) {
		this.type = type;
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			List<User> result = (List<User>) msg.obj;
			switch (msg.what) {
			case AutoListView.REFRESH:
				mListView.onRefreshComplete();
				mListViewItems.clear();
				if (result != null) {
					mListViewItems.addAll(result);
				}

				break;
			case AutoListView.LOAD:
				mListView.onLoadComplete();
				if (result != null) {
					mListViewItems.addAll(result);
				}
				break;
			}
			mListView.setResultSize(result.size());
			mAdapter.notifyDataSetChanged();
		};
	};
	private LayoutInflater mInflater;
	private AutoListView mListView;
	private BaseAdapter mAdapter;

	private List<User> mListViewItems = new ArrayList<User>();
	private Integer pageNo = 1;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
	private DisplayMetrics mMetrics;
	ImageLoader imageLoader;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		View view = inflater.inflate(R.layout.expert_list, container, false);
		mListView = (AutoListView) view.findViewById(R.id.autolistview);
		loadData(AutoListView.LOAD);
		mAdapter = new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				MyViewHolder viewHolder = null;
				if (convertView == null) {
					viewHolder = new MyViewHolder();
					convertView = mInflater.inflate(
							R.layout.listview_item_expert, null);

					TextView tv1 = (TextView) convertView
							.findViewById(R.id.text_name);// 专家姓名
					TextView tv2 = (TextView) convertView
							.findViewById(R.id.text_tel);// 电话
					TextView tv3 = (TextView) convertView
							.findViewById(R.id.text_remark);// 时间
					viewHolder.addView("name", tv1);
					viewHolder.addView("tel", tv2);
					viewHolder.addView("remark", tv3);
					convertView.setTag(viewHolder);
				} else {
					viewHolder = (MyViewHolder) convertView.getTag();
				}

				User t = getItem(position);

				TextView t1 = (TextView) viewHolder.getView("name");
				t1.setText(t.getName().toString());
				TextView t2 = (TextView) viewHolder.getView("tel");
				t2.setText(t.getTel());
				TextView t3 = (TextView) viewHolder.getView("remark");
				t3.setText(t.getRemark());
				// 如果有图片，则查找中memoryCache是否有该Bitmap，如果没有则去服务器上获得Bitmap并添加到memoryCache。
				if (StringUtils.isNotBlank(getItem(position).getImg())) {
					final int width = (int) TypedValue.applyDimension(
							TypedValue.COMPLEX_UNIT_DIP, 80, mMetrics);
					final int height = (int) TypedValue.applyDimension(
							TypedValue.COMPLEX_UNIT_DIP, 60, mMetrics);
					final ImageView iv;
					if (viewHolder.getView("img") != null) {
						iv = (ImageView) viewHolder.getView("img");
						iv.setVisibility(View.VISIBLE);
					} else {
						iv = new ImageView(mInflater.getContext());
						LinearLayout.LayoutParams lp_iv = new LinearLayout.LayoutParams(
								width, height);
						int margin = (int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 5, mMetrics);
						lp_iv.setMargins(margin, margin, margin, margin);
						iv.setLayoutParams(lp_iv);
						iv.setScaleType(ScaleType.CENTER_CROP);
						((ViewGroup) convertView).addView(iv, 0);
						viewHolder.addView("img", iv);
						iv.setTag(Constant.HOST + getItem(position).getImg());
					}

					imageLoader.DisplayImage(Constant.HOST
							+ getItem(position).getImg(), iv, false);
					;
				} else if (viewHolder.getView("img") != null) {// 如果没有图片，并且这一行被添加过img了，则把该img不显示
					((ImageView) viewHolder.getView("img"))
							.setVisibility(View.GONE);
				}
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public User getItem(int position) {
				return mListViewItems.get(position);
			}

			@Override
			public int getCount() {
				return mListViewItems != null ? mListViewItems.size() : 0;
			}
		};
		mListView.setOnRefreshListener(this);
		mListView.setOnLoadListener(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (mListener != null && (arg2 - 1) != mAdapter.getCount()) {
					mListener.onItemSelected(mListViewItems.get(arg2 - 1));
				}
			}

		});
		mMetrics = getResources().getDisplayMetrics();

		mBtn_back = (Button) view.findViewById(R.id.btn_back);
		mBtn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onBackBtn_click();
				}
			}
		});
		imageLoader = new ImageLoader(getActivity().getApplicationContext());
		return view;
	}

	/**
	 * AutoListView使用的监听器
	 */
	@Override
	public void onLoad() {
		++pageNo;
		loadData(AutoListView.LOAD);

	}

	/**
	 * AutoListView使用的监听器
	 */
	@Override
	public void onRefresh() {
		pageNo = 1;
		loadData(AutoListView.REFRESH);
	}

	/**
	 * 加载列表数据
	 * 
	 * @param what
	 * @param agriProductId
	 * @param pageNo
	 */
	private void loadData(final int what) {
		final int pageNo = this.pageNo;
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<User> list = null;
				try {
					//type_search根据选择的类型搜索专家
					InputStream is = NetUtil.post(Constant.SERVIER_PATH
							+ "/json/showExpertList.action", "pageNo=" + pageNo
							+ "&type_search=" + type);
					final String str = NetUtil.getStringFromInputStream(is);
					JsonObject json = (JsonObject) GsonUtil.parse(str);
					list = GsonUtil.fromJson(json.get("experts"),
							new TypeToken<List<User>>() {
							}.getType());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					Message msg = mHandler.obtainMessage();
					msg.what = what;
					msg.obj = list;
					mHandler.sendMessage(msg);
				}

			}
		}).start();

	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	public void setOnExpertListFragmentListener(
			OnExpertListFragmentListener listener) {
		this.mListener = listener;
	}

	private OnExpertListFragmentListener mListener;

	/**
	 * 
	 * @author wlj
	 * 
	 */
	public interface OnExpertListFragmentListener {

		void onBackBtn_click();

		void onItemSelected(User selectedUser);
	}
}
