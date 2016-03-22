package com.greentech.wnd.android;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.greentech.wnd.android.bean.Topic;
import com.greentech.wnd.android.bean.TopicReply;
import com.greentech.wnd.android.cache.ImageLoader;
import com.greentech.wnd.android.common.MyViewHolder;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.NetUtil;
import com.greentech.wnd.android.util.UserInfo;
import com.greentech.wnd.android.view.AutoListView;
import com.greentech.wnd.android.view.AutoListView.OnLoadListener;
import com.greentech.wnd.android.view.AutoListView.OnRefreshListener;
/**
 * 提问者和某个人的对话。
 * 
 * 暂时不用
 * @author WP
 *
 */
public class TopicReplyDetailActivity extends BaseActivity implements
OnRefreshListener, OnLoadListener {

	private Button mBtn_back;
	private Button mBtn_reply;
	private TextView mTv_title;
	private EditText mEt_reply;
	
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			List<TopicReply> result=(List<TopicReply>) msg.obj;
			switch (msg.what) {
			case AutoListView.REFRESH:
				mListView.onRefreshComplete();
				mListViewItems.clear();
				mListViewItems.addAll(result);
				break;
			case AutoListView.LOAD:
				mListView.onLoadComplete();
				mListViewItems.addAll(result);
				break;
			}
			mListView.setResultSize(result.size());
			mAdapter.notifyDataSetChanged();
		};
	};
	private LayoutInflater mInflater;
	private AutoListView mListView;
	private BaseAdapter mAdapter;
	
	private List<TopicReply> mListViewItems = new ArrayList<TopicReply>();
	private Integer pageNo = 1;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
	private DisplayMetrics mMetrics;
	ImageLoader imageLoader;
	
	private int topicId;
	private Topic topic;
	private int topicReplyId;
	private TopicReply topicReply;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	setContentView(R.layout.topic_reply_detail_list);
    	
    	//获得调用者传入的bundle
    	Bundle bundle = getIntent().getExtras();
    	if(bundle != null) {
    		topic = (Topic)bundle.get("topic");
    		if(topic!=null) {
    			topicId = topic.getId();
    		}
    		topicReply = (TopicReply)bundle.get("topicReply");
    		if(topicReply!=null) {
    			topicReplyId = topicReply.getId();
    		}
    	}
		imageLoader = new ImageLoader(getApplicationContext());

    	mTv_title = (TextView) findViewById(R.id.tv_title);
    	mTv_title.setText(topic.getPublisher().getName() + "的提问");
    	
		mInflater = LayoutInflater.from(TopicReplyDetailActivity.this);
		
    	
    	mListView = (AutoListView)findViewById(R.id.autolistview);
    	mAdapter = new BaseAdapter() {
    		
			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				//第一个显示问题，后面的显示回答
				if(position == 0){
					MyViewHolder viewHolder = null;

					TextView mTv_content_main;
					ImageView mIv_headerImage_main;
					TextView mTv_username_main;
					TextView mTv_time_main;
					LinearLayout mLl_imgs;
					if (convertView == null || ((MyViewHolder) convertView.getTag()).getView("imgs") == null) {
						viewHolder = new MyViewHolder();
						convertView = mInflater.inflate(
								R.layout.listview_item_topic_reply_lz, null);

						mTv_content_main = (TextView) convertView.findViewById(R.id.text_content_main);
						mIv_headerImage_main = (ImageView) convertView.findViewById(R.id.iv_headerImage_main);
				    	mTv_username_main = (TextView) convertView.findViewById(R.id.text_username_main);
				    	mTv_time_main = (TextView) convertView.findViewById(R.id.text_time_main);
				    	mLl_imgs = (LinearLayout)convertView.findViewById(R.id.ll_imgs);
				    	
						viewHolder.addView("content", mTv_content_main);
						viewHolder.addView("time", mTv_time_main);
						viewHolder.addView("username", mTv_username_main);
						viewHolder.addView("headImage", mIv_headerImage_main);
						viewHolder.addView("imgs", mLl_imgs);
						convertView.setTag(viewHolder);
					} else {
						viewHolder = (MyViewHolder) convertView.getTag();
					}
					mTv_content_main = (TextView)viewHolder.getView("content");
					mTv_content_main.setText(topic.getContent());
					mTv_username_main = (TextView)viewHolder.getView("username");
			    	mTv_username_main.setText(topic.getPublisher().getName());
					mTv_time_main = (TextView)viewHolder.getView("time");
					mIv_headerImage_main = (ImageView)viewHolder.getView("headImage");
			    	mTv_time_main.setText(sdf.format(topic.getReleaseTime()));
					if(StringUtils.isNotBlank(topic.getPublisher().getImg())) {
						String imgUrl = topic.getPublisher().getImg();//topic表里的imgs以;来分割各个图片路径
						mIv_headerImage_main.setTag(Constant.HOST + imgUrl);
						imageLoader.DisplayImage(Constant.HOST + imgUrl, mIv_headerImage_main, false);
					} else {
						mIv_headerImage_main.setBackground(getResources().getDrawable(R.drawable.header_gray));
					}
					mLl_imgs = (LinearLayout)viewHolder.getView("imgs");
					if(StringUtils.isNotBlank(topic.getImgs())) {
						String[] imgUrls = topic.getImgs().split(";");
						final int width = (int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 120, mMetrics);
						final int height = (int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 90, mMetrics);
						mLl_imgs.removeAllViews();
						int i = 0;
						LinearLayout ll = null;
						for(String imgUrl : imgUrls) {
							if(i%3 == 0) {
								ll = new LinearLayout(getApplicationContext());
								LinearLayout.LayoutParams lp_ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
								ll.setWeightSum(3);//多行3列
								ll.setOrientation(LinearLayout.HORIZONTAL);
								mLl_imgs.addView(ll);
							}
							final ImageView iv = new ImageView(mInflater.getContext());
							LinearLayout.LayoutParams lp_iv = new LinearLayout.LayoutParams(0, height, 1);//layoutweight均为1
							int margin = (int) TypedValue.applyDimension(
									TypedValue.COMPLEX_UNIT_DIP, 5, mMetrics);
							lp_iv.setMargins(margin, margin, margin, margin);
							iv.setLayoutParams(lp_iv);
							iv.setScaleType(ScaleType.CENTER_CROP);
//							final ImageView iv = (ImageView)mInflater.inflate(R.layout.gridlayout_item_img, null);
							imageLoader.DisplayImage(Constant.HOST + imgUrl, iv, false);
							ll.addView(iv);
							i++;
						}
					}
					return convertView;
				} else {
					MyViewHolder viewHolder = null;
					if (convertView == null || ((MyViewHolder) convertView.getTag()).getView("imgs") != null) {
						viewHolder = new MyViewHolder();
						convertView = mInflater.inflate(
								R.layout.listview_item_topic_reply, null);

						TextView tv1 = (TextView) convertView
								.findViewById(R.id.text);// 标题
						TextView tv2 = (TextView) convertView
								.findViewById(R.id.text_agree);// 同意
						tv2.setVisibility(View.GONE);
						TextView tv3 = (TextView) convertView
								.findViewById(R.id.text_time);// 时间
						TextView tv4 = (TextView) convertView
								.findViewById(R.id.text_username);// 用户名称（地址）
						TextView tv5 = (TextView) convertView
								.findViewById(R.id.text_disagree);// 反对
						tv5.setVisibility(View.GONE);
						TextView tv6 = (TextView) convertView
								.findViewById(R.id.text_taked);// 采纳
						tv6.setVisibility(View.GONE);
						ImageView iv_headImage = (ImageView)convertView.findViewById(R.id.iv_headerImage);//头像
						
						viewHolder.addView("content", tv1);
//						viewHolder.addView("agree", tv2);
						viewHolder.addView("time", tv3);
						viewHolder.addView("username", tv4);
						viewHolder.addView("headImage", iv_headImage);
//						viewHolder.addView("disagree", tv5);
//						viewHolder.addView("taked", tv6);
						convertView.setTag(viewHolder);
					} else {
						viewHolder = (MyViewHolder) convertView.getTag();
					}

					TopicReply t = mListViewItems.get(position - 1);

					TextView t1 = (TextView) viewHolder.getView("content");
					t1.setText(t.getContent().toString());
					TextView t3 = (TextView) viewHolder.getView("time");
					t3.setText(sdf.format(t.getReleaseTime()));
					TextView t4 = (TextView) viewHolder.getView("username");
					t4.setText(t.getUser().getName() + "(" + t.getUser().getProvince() + t.getUser().getCity() + t.getUser().getDistrict() + ")");
					ImageView iv_headImage = (ImageView) viewHolder.getView("headImage");
					if(StringUtils.isNotBlank(t.getUser().getImg())) {
						final String imgUrl = t.getUser().getImg();//topic表里的imgs以;来分割各个图片路径
						iv_headImage.setTag(Constant.HOST + imgUrl);
						imageLoader.DisplayImage(Constant.HOST + imgUrl, iv_headImage, false);
					} else {
						iv_headImage.setBackground(getResources().getDrawable(R.drawable.header_gray));
					}
					return convertView;
				}
			}
			
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				if(position == 0) {
					return topic;
				} else {
					return mListViewItems.get(position-1);
				}
			}
			
			@Override
			public int getCount() {
				return mListViewItems != null ? mListViewItems.size() + 1 : 1;
			}
		};
		mListView.setOnRefreshListener(this);
		mListView.setOnLoadListener(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
			}
			
		});
		mMetrics= getResources().getDisplayMetrics();
		
		loadData(AutoListView.LOAD);

		mBtn_back = (Button)findViewById(R.id.btn_back);
		mBtn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TopicReplyDetailActivity.this.finish();
			}
		});
		mEt_reply = (EditText)findViewById(R.id.et_reply);
		mBtn_reply = (Button)findViewById(R.id.btn_reply);
		mBtn_reply.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!UserInfo.isLogin(getApplicationContext())) {
					Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
					startActivity(intent);
				} else {
					new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("topicReply.content", mEt_reply.getText().toString().trim());
								map.put("topicReply.topicId", topicId);
								map.put("topicReply.userId", UserInfo
										.getUserId(getApplicationContext()));
								map.put("topicReply.parentId", topicReplyId);
								InputStream input = NetUtil.post(
										Constant.SERVIER_PATH
												+ "/json/addTopicReply.action", map);
								String json = NetUtil
										.getStringFromInputStream(input);
								JsonObject jsonObj = (JsonObject) GsonUtil
										.parse(json);
								String status = jsonObj.get("status")
										.getAsString();
								if (status.equals("1")) {
									TopicReply topicReply = GsonUtil.fromJson(jsonObj.get("topicReply"), new TypeToken<TopicReply>(){}.getType());
									if(topicReply != null) {
										mListViewItems.add(topicReply);
									}
									handler.post(new Runnable() {

										@Override
										public void run() {
											toastShow("信息发布成功!");
											mAdapter.notifyDataSetChanged();
										}
									});
								} else {
									handler.post(new Runnable() {

										@Override
										public void run() {
											toastShow("信息发布失败,请稍后再试!");
										}
									});
								}
							} catch (Exception ex) {
								handler.post(new Runnable() {

									@Override
									public void run() {
										toastShow("信息发布失败,请稍后再试...");
									}
								});
							} finally {
							}
						}
					}).start();
				}
			}
		});
		if(!UserInfo.isLogin(getApplicationContext())) {
			mEt_reply.setVisibility(View.GONE);
			mBtn_reply.setVisibility(View.GONE);
		}
    }


	/**
	 *  AutoListView使用的监听器
	 */
	@Override
	public void onLoad() {
		++pageNo;
		loadData(AutoListView.LOAD);
		
	}

	/**
	 *  AutoListView使用的监听器
	 */
	@Override
	public void onRefresh() {
		pageNo=1;
		loadData(AutoListView.REFRESH);
	}
	
	/**
	 * 加载列表数据
	 * @param what
	 * @param agriProductId
	 * @param pageNo
	 */
	private void loadData(final int what) {
		final int pageNo = this.pageNo;
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<TopicReply> list = null;
				try {
					InputStream is = NetUtil.post(Constant.SERVIER_PATH + "/json/showTopicReplyList.action","pageNo="+ pageNo+"&topicId="+topicId+"&topicReplyId="+topicReplyId+"&queryAll=1");
					final String str = NetUtil.getStringFromInputStream(is);
					JsonObject json = (JsonObject) GsonUtil.parse(str);
					list = GsonUtil.fromJson(json.get("topicReplies"), new TypeToken<List<TopicReply>>(){}.getType());
					topic = GsonUtil.fromJson(json.get("topic"), new TypeToken<Topic>(){}.getType());
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					Message msg = mHandler.obtainMessage();
					msg.what = what;
					msg.obj = list;
					mHandler.sendMessage(msg);
				}
				
			}
		}).start();
		
	}
	
	/**
	 * 按键点击事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			TopicReplyDetailActivity.this.finish();
			return true;
		} else {
			return false;
		}
	}

}
