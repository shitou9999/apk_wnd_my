package com.greentech.wnd.android;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.greentech.wnd.android.bean.Topic;
import com.greentech.wnd.android.bean.TopicReply;
import com.greentech.wnd.android.cache.ImageLoader;
import com.greentech.wnd.android.common.MyViewHolder;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.BoundUtil;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.NetUtil;
import com.greentech.wnd.android.util.TimeUtil;
import com.greentech.wnd.android.util.UserInfo;
import com.greentech.wnd.android.view.AutoListView;
import com.greentech.wnd.android.view.AutoListView.OnLoadListener;
import com.greentech.wnd.android.view.AutoListView.OnRefreshListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 提问+回答列表
 * 
 * @author WP
 * 
 */
public class TopicDetailActivity extends BaseActivity implements
		OnRefreshListener, OnLoadListener {

	private Button mBtn_back;
	private Button mBtn_reply;
	private Button mBtn_collect;
	private Button Recommend;
	private TextView mTv_title;

	private LayoutInflater mInflater;
	private AutoListView mListView;
	private BaseAdapter mAdapter;
	private boolean needReply;// 是否需要显示底部回答区，点击底部回答按钮时需要，点击listview里面的item时不需要
	private List<TopicReply> mListViewItems = new ArrayList<TopicReply>();
	private Integer pageNo = 1;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
	private DisplayMetrics mMetrics;
	ImageLoader imageLoader;

	private int topicId;
	private Topic topic;
	private TopicReply t;
	boolean isCollected = false;// 判断是否被收藏
	boolean isNeedRecommend;// 是否需要显示推荐按钮

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.topic_reply_list);
		mBtn_collect = (Button) findViewById(R.id.btn_collect);
		mBtn_reply = (Button) findViewById(R.id.btn_reply);
		Recommend = (Button) findViewById(R.id.Recommend);
		imageLoader = new ImageLoader(getApplicationContext());
		// 获得调用者传入的bundle
		Bundle bundle = getIntent().getExtras();
		isNeedRecommend = bundle.getBoolean("isNeedRecommend");
		if (!isNeedRecommend) {
			Recommend.setVisibility(View.GONE);
		}
		if (bundle != null) {
			topic = (Topic) bundle.get("topic");
			if (topic != null) {
				topicId = topic.getId();
			}
		}
		// 判断此条记录有没有被收藏
		isCollected(UserInfo.getUserId(TopicDetailActivity.this), topicId,
				(byte) 1);

		// 回答按钮
		mBtn_reply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				needReply = true;
				if (!UserInfo.isLogin(getApplicationContext())) {
					Intent intent = new Intent(getApplicationContext(),
							LoginActivity.class);
					startActivity(intent);
				} else {
					// 如果这条提问是自己的，就不能回答这个问题
					if (topic.getPublisherId() == UserInfo
							.getUserId(getApplicationContext())) {
						mBtn_reply.setClickable(false);
					} else {
						Intent intent = new Intent(getApplicationContext(),
								AddTopicReplyActivity.class);
						intent.putExtra("topic", topic);
						intent.putExtra("needReply", needReply);
						// 是点击我来回答按钮
						intent.putExtra("myReply", true);
						intent.putExtra("topicReply", t);
						startActivity(intent);
					}
				}

			}
		});
		// 收藏问题按钮
		mBtn_collect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (UserInfo.isLogin(TopicDetailActivity.this)) {

					// 需要传入userId,topic表里面的id，和自定义的收藏类型1.2.3
					RequestParams params = new RequestParams();
					params.put("userId",
							UserInfo.getUserId(TopicDetailActivity.this));
					params.put("refId", topicId);
					params.put("type", (byte) 1);// 1代表：我的问题收藏
					AsyncHttpClient client = new AsyncHttpClient();
					String url = Constant.SERVIER_PATH
							+ "/json/modifyCollection.action";
					client.get(url, params, new AsyncHttpResponseHandler() {

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							String str = new String(arg2);
							JsonObject json = (JsonObject) GsonUtil.parse(str);
							int i = json.get("collectionNum").getAsInt();
							// i!=0,说明收藏被取消，现在是未收藏状态
							if (i != 0) {
								Drawable d_no = getResources().getDrawable(
										R.drawable.ic_favo_no);
								mBtn_collect
										.setCompoundDrawablesWithIntrinsicBounds(
												d_no, null, null, null);
								mBtn_collect.setText("未收藏");
							} else {
								Drawable d = getResources().getDrawable(
										R.drawable.ic_favo_yes);
								mBtn_collect
										.setCompoundDrawablesWithIntrinsicBounds(
												d, null, null, null);
								mBtn_collect.setText("已收藏");
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							// TODO Auto-generated method stub

						}
					});
				} else {
					Intent intent = new Intent(TopicDetailActivity.this,
							LoginActivity.class);
					startActivity(intent);
				}

			}
		});
		// 推荐答案按钮
		Recommend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TopicDetailActivity.this,
						RecommendActivity.class);
				intent.putExtras(getIntent().getExtras());
				startActivity(intent);
			}
		});
		mAdapter = new BaseAdapter() {

			@Override
			public View getView(final int position, View convertView,
					ViewGroup parent) {
				// 第一个显示问题，后面的显示回答
				if (position == 0) {
					MyViewHolder viewHolder = null;
					TextView mTv_content_main;
					ImageView mIv_headerImage_main;
					ImageView mTv_expert_tag;
					TextView mTv_username_main;
					TextView mTv_time_main;
					LinearLayout mLl_imgs;
					if (convertView == null
							|| ((MyViewHolder) convertView.getTag())
									.getView("imgs") == null) {
						viewHolder = new MyViewHolder();
						convertView = mInflater.inflate(
								R.layout.listview_item_topic_reply_lz, null);

						mTv_content_main = (TextView) convertView
								.findViewById(R.id.text_content_main);
						mIv_headerImage_main = (ImageView) convertView
								.findViewById(R.id.iv_headerImage_main);
						mTv_expert_tag = (ImageView) convertView
								.findViewById(R.id.expert_tag);
						mTv_username_main = (TextView) convertView
								.findViewById(R.id.text_username_main);
						mTv_time_main = (TextView) convertView
								.findViewById(R.id.text_time_main);
						mLl_imgs = (LinearLayout) convertView
								.findViewById(R.id.ll_imgs);

						viewHolder.addView("content", mTv_content_main);
						viewHolder.addView("time", mTv_time_main);
						viewHolder.addView("username", mTv_username_main);
						viewHolder.addView("headImage", mIv_headerImage_main);
						viewHolder.addView("imgs", mLl_imgs);
						viewHolder.addView("expert_tag", mTv_expert_tag);
						convertView.setTag(viewHolder);
					} else {
						viewHolder = (MyViewHolder) convertView.getTag();
					}
					mTv_content_main = (TextView) viewHolder.getView("content");
					mTv_content_main.setText(topic.getContent());
					mTv_username_main = (TextView) viewHolder
							.getView("username");
					mTv_username_main
							.setText(topic.getPublisher().getName() == "" ? "问农答网友"
									: topic.getPublisher().getName());
					mTv_time_main = (TextView) viewHolder.getView("time");
					mIv_headerImage_main = (ImageView) viewHolder
							.getView("headImage");
					mTv_time_main.setText(TimeUtil.setTime(topic
							.getReleaseTime()));
					mTv_expert_tag = (ImageView) viewHolder
							.getView("expert_tag");
					// 如果是专家用户
					if (topic.getPublisher().getIsExpert() == 0) {
						mTv_expert_tag.setVisibility(View.GONE);
					} else {
						mTv_expert_tag.setVisibility(View.VISIBLE);
					}
					if (StringUtils.isNotBlank(topic.getPublisher().getImg())) {
						String imgUrl = topic.getPublisher().getImg();
						mIv_headerImage_main.setTag(Constant.HOST + imgUrl);
						imageLoader.DisplayImage(Constant.HOST + imgUrl,
								mIv_headerImage_main, false);
					} else {
						mIv_headerImage_main.setBackground(getResources()
								.getDrawable(R.drawable.header_gray));
					}
					mLl_imgs = (LinearLayout) viewHolder.getView("imgs");
					if (StringUtils.isNotBlank(topic.getImgs())) {
						String[] imgUrls = topic.getImgs().split(";");
						final int width = (int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 120, mMetrics);
						final int height = (int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 90, mMetrics);
						mLl_imgs.removeAllViews();
						int i = 0;
						LinearLayout ll = null;
						for (String imgUrl : imgUrls) {
							if (i % 3 == 0) {
								ll = new LinearLayout(getApplicationContext());
								LinearLayout.LayoutParams lp_ll = new LinearLayout.LayoutParams(
										LinearLayout.LayoutParams.MATCH_PARENT,
										LinearLayout.LayoutParams.WRAP_CONTENT);
								ll.setWeightSum(3);// 多行3列
								ll.setOrientation(LinearLayout.HORIZONTAL);
								mLl_imgs.addView(ll);
							}
							final ImageView iv = new ImageView(
									mInflater.getContext());
							LinearLayout.LayoutParams lp_iv = new LinearLayout.LayoutParams(
									0, height, 1);// layoutweight均为1
							int margin = (int) TypedValue.applyDimension(
									TypedValue.COMPLEX_UNIT_DIP, 5, mMetrics);
							lp_iv.setMargins(margin, margin, margin, margin);
							iv.setLayoutParams(lp_iv);
							iv.setScaleType(ScaleType.CENTER_CROP);
							// final ImageView iv =
							// (ImageView)mInflater.inflate(R.layout.gridlayout_item_img,
							// null);
							imageLoader.DisplayImage(Constant.HOST + imgUrl,
									iv, false);
							ll.addView(iv);
							i++;
						}
					}
					return convertView;
				} else {
					// 回复使用的View

					MyViewHolder viewHolder = null;
					if (convertView == null
							|| ((MyViewHolder) convertView.getTag())
									.getView("agree") == null) {
						viewHolder = new MyViewHolder();
						convertView = mInflater.inflate(
								R.layout.listview_item_topic_reply, null);

						TextView tv1 = (TextView) convertView
								.findViewById(R.id.text);// 标题
						TextView tv2 = (TextView) convertView
								.findViewById(R.id.text_agree);// 同意
						TextView tv3 = (TextView) convertView
								.findViewById(R.id.text_time);// 时间
						TextView tv4 = (TextView) convertView
								.findViewById(R.id.text_username);// 用户名称（地址）
						TextView tv5 = (TextView) convertView
								.findViewById(R.id.text_disagree);// 反对
						TextView tv6 = (TextView) convertView
								.findViewById(R.id.text_taked);// 采纳
						ImageView iv_headImage = (ImageView) convertView
								.findViewById(R.id.iv_headerImage);// 头像
						ImageView expert_tag_re = (ImageView) convertView
								.findViewById(R.id.expert_tag_re);// 专家标记

						viewHolder.addView("content", tv1);
						viewHolder.addView("agree", tv2);
						viewHolder.addView("time", tv3);
						viewHolder.addView("username", tv4);
						viewHolder.addView("headImage", iv_headImage);
						viewHolder.addView("disagree", tv5);
						viewHolder.addView("taked", tv6);
						viewHolder.addView("expert_tag_re", expert_tag_re);
						convertView.setTag(viewHolder);
					} else {
						viewHolder = (MyViewHolder) convertView.getTag();
					}

					t = mListViewItems.get(position - 1);

					TextView t1 = (TextView) viewHolder.getView("content");
					t1.setText(t.getContent().toString());
					TextView t2 = (TextView) viewHolder.getView("agree");
					try {
						// --------------------------------------------------------------------------------------
						int text = t.getAgreed();
						t2.setText("同意(" + text + ")");

					} catch (Exception ex) {
						ex.printStackTrace();
						Log.e("wudc", "位置：" + position);
					}
					t2.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(final View v) {
							if (!UserInfo.isLogin(getApplicationContext())) {
								Intent intent = new Intent(
										getApplicationContext(),
										LoginActivity.class);
								startActivity(intent);
							} else {

								RequestParams params = new RequestParams();
								params.put("id",
										mListViewItems.get(position - 1)
												.getId());
								params.put("loginId", UserInfo
										.getUserId(getApplicationContext()));
								params.put("userId",
										mListViewItems.get(position - 1)
												.getUserId());
								AsyncHttpClient client = new AsyncHttpClient();
								client.get(Constant.SERVIER_PATH
										+ "/json/agreeTopicReply.action",
										params, new AsyncHttpResponseHandler() {

											@Override
											public void onSuccess(int arg0,
													Header[] arg1, byte[] arg2) {
												String str = new String(arg2);
												JsonObject json = (JsonObject) GsonUtil
														.parse(str);
												((TextView) v).setText("同意("
														+ json.get("agreed")
														+ ")");
											}

											@Override
											public void onFailure(int arg0,
													Header[] arg1, byte[] arg2,
													Throwable arg3) {
												// TODO Auto-generated method
												// stub

											}
										});
							}
						}
					});
					TextView t5 = (TextView) viewHolder.getView("disagree");
					t5.setText("反对(" + t.getDisagreed() + ")");
					t5.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(final View v) {
							if (!UserInfo.isLogin(getApplicationContext())) {
								Intent intent = new Intent(
										getApplicationContext(),
										LoginActivity.class);
								startActivity(intent);
							} else {
								RequestParams params = new RequestParams();
								params.put("id",
										mListViewItems.get(position - 1)
												.getId());
								params.put("loginId", UserInfo
										.getUserId(getApplicationContext()));
								params.put("userId",
										mListViewItems.get(position - 1)
												.getUserId());
								AsyncHttpClient client = new AsyncHttpClient();
								client.get(Constant.SERVIER_PATH
										+ "/json/disagreeTopicReply.action",
										params, new AsyncHttpResponseHandler() {

											@Override
											public void onSuccess(int arg0,
													Header[] arg1, byte[] arg2) {
												String str = new String(arg2);
												JsonObject json = (JsonObject) GsonUtil
														.parse(str);
												((TextView) v).setText("反对("
														+ json.get("disagreed")
														+ ")");
											}

											@Override
											public void onFailure(int arg0,
													Header[] arg1, byte[] arg2,
													Throwable arg3) {
												// TODO Auto-generated method
												// stub

											}
										});
							}
						}
					});
					final TextView t6 = (TextView) viewHolder.getView("taked");
					// t6.setTag(mListViewItems.get(position - 1).getIstaked());
					// 未登录、登录但是提问者不是本人、登录且提问者是本人但是已经采纳过答案并且采纳的答案不是当前回复，这三种情况下t6不显示。
					if (!UserInfo.isLogin(getApplicationContext())
							|| topic.getPublisherId() != UserInfo
									.getUserId(getApplicationContext())
							|| topic.getTakedReplyId() > 0
							&& mListViewItems.get(position - 1).getId() != topic
									.getTakedReplyId()) {
						t6.setVisibility(View.GONE);
						t6.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub

							}
						});
					}
					if (t.getIstaked() > 0) {
						// 回复是提问者采纳过的，则显示已被采纳，并且点击事件置空。
						t6.setText("已被采纳");
						t6.setVisibility(View.VISIBLE);
						t6.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub

							}
						});
					}
					// 如果没有被采纳过 并且提问者就是登陆者
					if (topic.getTakedReplyId() <= 0
							&& topic.getPublisherId() == UserInfo
									.getUserId(TopicDetailActivity.this)) {
						t6.setVisibility(View.VISIBLE);
						// 如果为没有采纳过回复，则提问者可以选择采纳回复，并且给被采纳的人加分
						t6.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(final View v) {
								if (!UserInfo.isLogin(getApplicationContext())) {
									Intent intent = new Intent(
											getApplicationContext(),
											LoginActivity.class);
									startActivity(intent);
								} else {
									if (TimeUtil.getMinTime(t.getReleaseTime()) <= 10) {
										// 被采纳的回答者增加300积分
										BoundUtil.addOtherBonus(300,
												mListViewItems
														.get(position - 1)
														.getUserId(),
												TopicDetailActivity.this);
									} else {
										// 被采纳的回答者增加200积分
										BoundUtil.addOtherBonus(200,
												mListViewItems
														.get(position - 1)
														.getUserId(),
												TopicDetailActivity.this);
									}

									new Thread(new Runnable() {

										@Override
										public void run() {
											try {
												InputStream input = NetUtil
														.post(Constant.SERVIER_PATH
																+ "/json/takeTopicReply.action",
																"topic.id="
																		+ topic.getId()
																		+ "&topicReply.id="
																		+ mListViewItems
																				.get(position - 1)
																				.getId());
												String json = NetUtil
														.getStringFromInputStream(input);
												JsonObject jsonObj = (JsonObject) GsonUtil
														.parse(json);
												// topic =
												// GsonUtil.fromJson(jsonObj.get("topic"),
												// new
												// TypeToken<Topic>(){}.getType());
												mHandler.post(new Runnable() {

													@Override
													public void run() {
														t6.setText("已被采纳");
														t6.setOnClickListener(null);
														ArrayList<View> outViews = new ArrayList<View>();
														mListView
																.findViewsWithText(
																		outViews,
																		"我要采纳",
																		View.FIND_VIEWS_WITH_TEXT);
														if (outViews != null
																&& !outViews
																		.isEmpty()) {
															for (View t6 : outViews) {
																if (t6 instanceof TextView) {
																	t6.setVisibility(View.GONE);
																	t6.setOnClickListener(null);
																}
															}
														}
													}
												});
											} catch (Exception ex) {
											} finally {
											}
										}
									}).start();
								}
							}
						});
					}
					TextView t3 = (TextView) viewHolder.getView("time");
					t3.setText(TimeUtil.setTime(t.getReleaseTime()));
					TextView t4 = (TextView) viewHolder.getView("username");
					t4.setText(t.getUser().getName() == "" ? "问农答网友" : t
							.getUser().getName()
							+ "("
							+ t.getUser().getProvince() + ")");
					ImageView iv_headImage = (ImageView) viewHolder
							.getView("headImage");
					ImageView mTv_expert_tag = (ImageView) viewHolder
							.getView("expert_tag_re");

					// 如果是专家用户
					if (t.getUser().getIsExpert() == 0) {
						mTv_expert_tag.setVisibility(View.GONE);
					} else {
						mTv_expert_tag.setVisibility(View.VISIBLE);
						t4.setTextColor(R.color.gold);
					}

					if (StringUtils.isNotBlank(t.getUser().getImg())) {
						final String imgUrl = t.getUser().getImg();// topic表里的imgs以;来分割各个图片路径
						iv_headImage.setTag(Constant.HOST + imgUrl);
						imageLoader.DisplayImage(Constant.HOST + imgUrl,
								iv_headImage, false);
					} else {
						iv_headImage.setBackground(getResources().getDrawable(
								R.drawable.header_gray));
					}
					// 设置点击头像，进入个人信息详情
					iv_headImage.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(
									TopicDetailActivity.this,
									MyCardActivity.class);
							intent.putExtra("userId", t.getUserId());
							startActivity(intent);

						}
					});
					return convertView;
				}
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				if (position == 0) {
					return topic;
				} else {
					return mListViewItems.get(position - 1);
				}
			}

			@Override
			public int getCount() {
				return mListViewItems != null ? mListViewItems.size() + 1 : 1;
			}
		};

		mMetrics = getResources().getDisplayMetrics();

		loadData(AutoListView.LOAD);

		mBtn_back = (Button) findViewById(R.id.btn_back);
		mBtn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TopicDetailActivity.this.finish();
			}
		});
		mTv_title = (TextView) findViewById(R.id.tv_title);
		mTv_title.setText(StringUtils
				.isNotBlank(topic.getPublisher().getName()) ? topic
				.getPublisher().getName() + "的提问" : "网友的提问");
		mInflater = LayoutInflater.from(TopicDetailActivity.this);
		mListView = (AutoListView) findViewById(R.id.autolistview);
		mListView.setOnRefreshListener(this);
		mListView.setOnLoadListener(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			boolean needReply;

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				/**
				 * arg2从1开始；arg3从0开始
				 */
				// 如果点击楼主的提问就显示楼主发布的图片
				if (arg2 == 1 && StringUtils.isNotBlank(topic.getImgs())) {

					Intent intent = new Intent(getApplicationContext(),
							ShowImageActivity.class);
					intent.putExtra("topic", topic);
					startActivity(intent);
				}

				// 使一头一尾点击无效
				if (arg2 > 1 && (arg2 - 1) != mAdapter.getCount()) {

					Intent intent = new Intent(getApplicationContext(),
							AddTopicReplyActivity.class);
					intent.putExtra("topic", topic);
					if (topic.getPublisherId() == UserInfo
							.getUserId(TopicDetailActivity.this)) {
						needReply = true;
						intent.putExtra("needReply", needReply);
						// 不是点击我来回答按钮
						intent.putExtra("myReply", false);
					} else {
						intent.putExtra("needReply", needReply);
					}

					intent.putExtra("topicReply", mListViewItems.get(arg2 - 2));
					startActivity(intent);

				}
			}

		});
	}

	public void isCollected(int userId, int refId, byte type) {

		RequestParams params = new RequestParams();
		params.put("userId", UserInfo.getUserId(TopicDetailActivity.this));
		params.put("refId", topicId);
		params.put("type", (byte) 1);// 1代表：我的问题收藏
		AsyncHttpClient client = new AsyncHttpClient();
		String url = Constant.SERVIER_PATH + "/json/isCollected.action";
		client.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				String str = new String(arg2);
				JsonObject json = (JsonObject) GsonUtil.parse(str);
				isCollected = json.get("state").getAsBoolean();
				if (isCollected) {
					Drawable d = getResources().getDrawable(
							R.drawable.ic_favo_yes);
					mBtn_collect.setCompoundDrawablesWithIntrinsicBounds(d,
							null, null, null);
					mBtn_collect.setText("已收藏");
				}

			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub

			}
		});

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
				List<TopicReply> list = null;
				try {
					InputStream is = NetUtil.post(Constant.SERVIER_PATH
							+ "/json/showTopicReplyList.action", "pageNo="
							+ pageNo + "&topicId=" + topicId);
					final String str = NetUtil.getStringFromInputStream(is);
					JsonObject json = (JsonObject) GsonUtil.parse(str);
					list = GsonUtil.fromJson(json.get("topicReplies"),
							new TypeToken<List<TopicReply>>() {
							}.getType());
					topic = GsonUtil.fromJson(json.get("topic"),
							new TypeToken<Topic>() {
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

	/**
	 * 按键点击事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			TopicDetailActivity.this.finish();
			return true;
		} else {
			return false;
		}
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			List<TopicReply> result = (List<TopicReply>) msg.obj;
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

}
