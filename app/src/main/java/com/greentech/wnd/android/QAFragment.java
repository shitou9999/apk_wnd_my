package com.greentech.wnd.android;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.greentech.wnd.android.bean.Topic;
import com.greentech.wnd.android.cache.ImageLoader;
import com.greentech.wnd.android.common.MyViewHolder;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.NetUtil;
import com.greentech.wnd.android.util.TimeUtil;
import com.greentech.wnd.android.util.UserInfo;
import com.greentech.wnd.android.view.AutoListView;
import com.greentech.wnd.android.view.AutoListView.OnLoadListener;
import com.greentech.wnd.android.view.AutoListView.OnRefreshListener;

/**
 * 专家答疑
 * 
 * @author wlj
 * 
 */
public class QAFragment extends Fragment implements OnRefreshListener,
		OnLoadListener {
	private boolean isNeedTitle = true;// 是否需要布局文件的标题（最上面的部分）1：需要，0：不需要，默认需要
	private int mAskOrAnswer = 2;// 判断加载问还是答 0:问,1:答， 2:默认
	private boolean isNeedRecommend;// 是否在我的提问当中显示推荐答案按钮（在我的问答里面需要此按钮）
	private String recommend;// 查询推荐答案的条件

	public QAFragment(boolean isTitle, boolean isNeedRecommend, int position,
			String recommend) {
		isNeedTitle = isTitle;
		mAskOrAnswer = position;
		this.isNeedRecommend = isNeedRecommend;
		this.recommend = recommend;
	}

	private LayoutInflater mInflater;
	private AutoListView mListView_topics;
	private BaseAdapter mAdapter;
	private Button mBtn_ask;
	private RelativeLayout title;
	private List<Topic> mTopicList = new ArrayList<Topic>();
	private Integer pageNo = 1;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private DisplayMetrics mMetrics;
	ImageLoader imageLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		View view = inflater.inflate(R.layout.fragment_main_zjdy, container,
				false);
		mListView_topics = (AutoListView) view.findViewById(R.id.autolistview);
		mBtn_ask = (Button) view.findViewById(R.id.btn_topic_add);
		title = (RelativeLayout) view.findViewById(R.id.title);

		if (isNeedTitle == false) {
			title.setVisibility(View.GONE);
		}
		mBtn_ask.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UserInfo.isLogin(getActivity())) {
					Intent intent = new Intent(getActivity(),
							AddTopicActivity.class);
					startActivityForResult(intent, 10);// 注意这里一定要用startActivityForResult而不能用getActivity().startActivityForResult，并且requestCode>=0才行。
				} else {
					Intent intent = new Intent(getActivity(),
							LoginActivity.class);
					startActivity(intent);
				}
			}
		});
		mAdapter = new BaseAdapter() {
			@Override
			public View getView(final int position, View convertView,
					ViewGroup parent) {

				MyViewHolder viewHolder = null;
				if (convertView == null) {
					viewHolder = new MyViewHolder();
					convertView = mInflater.inflate(
							R.layout.listview_item_topic, null);

					TextView tv1 = (TextView) convertView
							.findViewById(R.id.text);// 内容
					TextView tv2 = (TextView) convertView
							.findViewById(R.id.text_hf);// 回复数
					TextView tv3 = (TextView) convertView
							.findViewById(R.id.text_time);// 时间
					TextView tv4 = (TextView) convertView
							.findViewById(R.id.text_username);// 用户名称（地址）
					ImageView iv_headImage = (ImageView) convertView
							.findViewById(R.id.iv_headerImage);// 头像
					ImageView expert_tag = (ImageView)
					 convertView.findViewById(R.id.expert_tag);//标记为专家图片
					viewHolder.addView("title", tv1);
					viewHolder.addView("replyNum", tv2);
					viewHolder.addView("time", tv3);
					viewHolder.addView("username", tv4);
					viewHolder.addView("headImage", iv_headImage);
					 viewHolder.addView("expert_tag", expert_tag);
					convertView.setTag(viewHolder);
				} else {
					viewHolder = (MyViewHolder) convertView.getTag();
				}

				final Topic t = getItem(position);

				TextView t1 = (TextView) viewHolder.getView("title");
				t1.setText(t.getContent().toString());
				TextView t2 = (TextView) viewHolder.getView("replyNum");
				t2.setText("回答(" + t.getReplyCnt() + ")");
				TextView t3 = (TextView) viewHolder.getView("time");
				t3.setText(TimeUtil.setTime(t.getReleaseTime()));
				TextView t4 = (TextView) viewHolder.getView("username");
				t4.setText((t.getPublisher().getName() == "" ? "网友" : t
						.getPublisher().getName())
						+ "("
						+ t.getPublisher().getProvince()
						+ t.getPublisher().getCity() + ")");

				ImageView iv_headImage = (ImageView) viewHolder
						.getView("headImage");
				ImageView
				 expert_tag1=(ImageView)viewHolder.getView("expert_tag");
				// 如果不是专家用户
				if (t.getPublisher().getIsExpert()==0) {
					expert_tag1.setVisibility(View.GONE);
				}else{
					expert_tag1.setVisibility(View.VISIBLE);
					t4.setTextColor(getResources().getColor(R.color.gold));
				}
				// 加载头像
				if (StringUtils.isNotBlank(t.getPublisher().getImg())) {
					final String imgUrl = t.getPublisher().getImg();
					imageLoader.DisplayImage(Constant.HOST + imgUrl,
							iv_headImage, false);
				} else {
					iv_headImage
							.setImageBitmap(((BitmapDrawable) getResources()
									.getDrawable(R.drawable.username))
									.getBitmap());
				}
				iv_headImage.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								MyCardActivity.class);
						intent.putExtra("userId", t.getPublisherId());
						startActivity(intent);
					}

				});

				// 如果有图片，则查找中memoryCache是否有该Bitmap，如果没有则去服务器上获得Bitmap并添加到memoryCache。
				if (StringUtils.isNotBlank(t.getImgs())) {
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
						((ViewGroup) convertView).addView(iv);
						viewHolder.addView("img", iv);
					}

					final String imgUrl = t.getImgs().split(";")[0];// topic表里的imgs以;来分割各个图片路径
					imageLoader.DisplayImage(Constant.HOST + imgUrl, iv, false);
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
			public Topic getItem(int position) {
				return mTopicList.get(position);
			}

			@Override
			public int getCount() {
				return mTopicList != null ? mTopicList.size() : 0;
			}
		};

		mListView_topics.setOnRefreshListener(this);
		mListView_topics.setOnLoadListener(this);
		mListView_topics.setAdapter(mAdapter);
		mListView_topics.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if ((arg2 - 1) != mAdapter.getCount()) {
					Intent intent = new Intent(getActivity(),
							TopicDetailActivity.class);
					intent.putExtra("topic", mTopicList.get(arg2 - 1));
					intent.putExtra("isNeedRecommend", isNeedRecommend);
					startActivity(intent);
				}

			}
		});
		mMetrics = getResources().getDisplayMetrics();

		loadData(AutoListView.LOAD);
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
	private InputStream is;

	private void loadData(final int what) {
		final int pageNo = this.pageNo;

		new Thread(new Runnable() {

			@Override
			public void run() {
				List<Topic> list = null;

				try {
					// 如果mAskOrAnswer==0，显示我的提问
					if (mAskOrAnswer == 0) {
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("publisherId",
								UserInfo.getUserId(getActivity()));
						map.put("pageNo", pageNo);
						is = NetUtil.post(Constant.SERVIER_PATH
								+ "/json/showTopicList.action", map);
					}
					// 如果mAskOrAnswer==1，显示我的回答
					if (mAskOrAnswer == 1) {
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("replierId", UserInfo.getUserId(getActivity()));
						map.put("pageNo", pageNo);
						is = NetUtil.post(Constant.SERVIER_PATH
								+ "/json/showTopicList.action", map);
					}
					if (mAskOrAnswer == 2) {
						is = NetUtil.post(Constant.SERVIER_PATH
								+ "/json/showTopicList.action", "pageNo="
								+ pageNo);
					}
					// 如果mAskOrAnswer==3，显示我收藏的问题
					if (mAskOrAnswer == 3) {
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("userId", UserInfo.getUserId(getActivity()));
						map.put("type", 1);
						map.put("pageNo", pageNo);
						is = NetUtil.post(Constant.SERVIER_PATH
								+ "/json/showCollectionQuestion.action", map);
					}
					if (StringUtils.isNotBlank(recommend)) {
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("type_recommend", recommend);
						map.put("pageNo", pageNo);
						is = NetUtil.post(Constant.SERVIER_PATH
								+ "/json/showTopicList.action", map);
					}
					final String str = NetUtil.getStringFromInputStream(is);
					JsonObject json = (JsonObject) GsonUtil.parse(str);
					list = GsonUtil.fromJson(json.get("topics"),
							new TypeToken<List<Topic>>() {
							}.getType());
					// 根据list查询每一个条目是不是专家，如果是专家就特殊标记

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					Message msg = mHandler.obtainMessage();
					msg.what = what;
					msg.obj = list;
					mHandler.sendMessage(msg);
					if (is != null) {
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			}
		}).start();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 10) {
			if (resultCode == Activity.RESULT_OK) {
				onRefresh();
			}
		}
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			List<Topic> result = (List<Topic>) msg.obj;
			switch (msg.what) {
			case AutoListView.REFRESH:
				mListView_topics.onRefreshComplete();
				mTopicList.clear();
				if (result != null) {
					mTopicList.addAll(result);
				}

				break;
			case AutoListView.LOAD:
				mListView_topics.onLoadComplete();
				if (result != null) {
					mTopicList.addAll(result);
				}

				break;
			}
			if (result != null) {
				mListView_topics.setResultSize(result.size());
			}

			mAdapter.notifyDataSetChanged();
		};
	};

}
