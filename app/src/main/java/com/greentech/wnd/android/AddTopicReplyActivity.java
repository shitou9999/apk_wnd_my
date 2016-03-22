package com.greentech.wnd.android;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.greentech.wnd.android.bean.Topic;
import com.greentech.wnd.android.bean.TopicReply;
import com.greentech.wnd.android.cache.ImageLoader;
import com.greentech.wnd.android.common.MyViewHolder;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.CustomDialog;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.NetUtil;
import com.greentech.wnd.android.util.UserInfo;
import com.greentech.wnd.android.view.CircleImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 发布回答，或者显示某个提问某个人的回答
 * 
 * 
 */
public class AddTopicReplyActivity extends BaseActivity {

	private Button mBtn_back;
	private Button mBtn_send;
	private TextView mTv_title;
	private TextView tv_question;// 楼主的问题
	private EditText mEt_step;// 回复内容
	private Topic topic;
	private TopicReply topicReply;
	private Dialog dialog;
	private CircleImageView head;// 提问者的头像
	private CircleImageView myhead;// 我的头像
	private ImageLoader imageLoader;
	private RelativeLayout reply_reLative;
	private RelativeLayout bottom_reply;
	private boolean isNeedReply;// 是否需要底部回答区
	private boolean isMyReply;// 判断是否点击我来回答按钮到这个界面的
	private ListView listView;
	List<TopicReply> list = null;
	private LayoutInflater mInflater;
	private BaseAdapter mAdapter;
	Integer parentId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置布局
		setContentView(R.layout.add_topic_reply);
		bottom_reply = (RelativeLayout) findViewById(R.id.needReply);// 显示回复的信息
		// head=(CircleImageView) findViewById(R.id.head);
		mInflater = LayoutInflater.from(getApplicationContext());
		mAdapter = new MyAdapter();
		imageLoader = new ImageLoader(getApplicationContext());
		// 获得调用者传入的bundle
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			topic = (Topic) bundle.get("topic");
			isNeedReply = bundle.getBoolean("needReply");
			isMyReply = bundle.getBoolean("myReply");
			topicReply = (TopicReply) bundle.get("topicReply");
		}
		if (!isNeedReply) {
			bottom_reply.setVisibility(View.GONE);
		}

		mTv_title = (TextView) findViewById(R.id.tv_title);
		mTv_title.setText(topic.getPublisher().getName() + "的提问");
		mEt_step = (EditText) findViewById(R.id.et_step);

		mBtn_back = (Button) findViewById(R.id.btn_back);
		mBtn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		dialog = CustomDialog.createLoadingDialog(AddTopicReplyActivity.this,
				"数据提交中...");
		mBtn_send = (Button) findViewById(R.id.btn_send);
		// ------------------------------------------------------------------------------------
		listView = (ListView) findViewById(R.id.dialog_list);
		int topicId = topic.getId();
		int userId;
		// 判断加载谁的回复，点击"我来回答"就加载我的回复，点击listView就指定加载某个人的回复
		if (isNeedReply && isMyReply) {
			userId = UserInfo.getUserId(getApplicationContext());
			parentId = 0;
		} else {
			userId = topicReply.getUserId();
			// parentId表示这条回复是 --- 追问
			parentId = topicReply.getId();
			mEt_step.setHint("回复");
		}

		String url = Constant.SERVIER_PATH + "/json/showTopicDetail.action";
		RequestParams params = new RequestParams();
		params.put("id", topicId);
		params.put("userId", userId);
		params.put("parentId", parentId);

		AsyncHttpClient client = new AsyncHttpClient();
		// 获取某一条提问的所有回答
		client.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

				String json = new String(arg2);
				JsonObject jsonObj = (JsonObject) GsonUtil.parse(json);
				list = GsonUtil.fromJson(jsonObj.get("topicReplies"),
						new TypeToken<List<TopicReply>>() {
						}.getType());
				listView.setAdapter(mAdapter);
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub

			}
		});
		mBtn_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String str1 = mEt_step.getText().toString().trim();
				StringBuffer sb = new StringBuffer();
				if (StringUtils.isNotBlank(str1)) {
					sb.append(str1);
				}
				final String content = sb.toString();
				boolean flag = true;
				if (StringUtils.isBlank(content)) {
					toastShow("请输入内容");
					flag = false;
				}

				if (flag) {
					dialog.show();
					new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("topicReply.content", content);
								map.put("topicReply.topicId", topic.getId());
								map.put("topicReply.type", topic.getType());
								map.put("topicReply.type", topic.getType());
								map.put("topicReply.parentId", parentId);
								map.put("topicReply.userId", UserInfo
										.getUserId(AddTopicReplyActivity.this));
								InputStream input = NetUtil.post(
										Constant.SERVIER_PATH
												+ "/json/addTopicReply.action",
										map);
								String json = NetUtil
										.getStringFromInputStream(input);
								JsonObject jsonObj = (JsonObject) GsonUtil
										.parse(json);
								String status = jsonObj.get("status")
										.getAsString();
								if (status.equals("1")) {
									handler.post(new Runnable() {

										@Override
										public void run() {
											CustomDialog.createLoadingDialog(
													AddTopicReplyActivity.this,
													"回答发布成功").show();
											setResult(Activity.RESULT_OK);
											finish();
										}
									});
								} else {
									handler.post(new Runnable() {

										@Override
										public void run() {
											CustomDialog.createLoadingDialog(
													AddTopicReplyActivity.this,
													"信息发布失败,请稍后再试!").show();
										}
									});
								}
							} catch (Exception ex) {
								handler.post(new Runnable() {

									@Override
									public void run() {
										CustomDialog.createLoadingDialog(
												AddTopicReplyActivity.this,
												"信息发布失败,请稍后再试!").show();
									}
								});
							} finally {
								dialog.dismiss();
							}
						}
					}).start();
				}
			}
		});
	}

	/**
	 * 按键点击事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AddTopicReplyActivity.this.finish();
			return true;
		} else {
			return false;
		}
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return list.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 第一个显示问题，后面的显示回答和回复
			if (position == 0
					|| (list.get(position - 1).getUserId().intValue() == topic.getPublisherId().intValue()
							&& !isMyReply)) {
				MyViewHolder viewHolder = null;
				TextView mTv_content_main;// 楼主的问题
				ImageView mIv_headerImage_main;// 楼主的头像
				if (convertView == null) {
					viewHolder = new MyViewHolder();
					convertView = mInflater.inflate(R.layout.a, null);

					mTv_content_main = (TextView) convertView
							.findViewById(R.id.tv_question);
					mIv_headerImage_main = (ImageView) convertView
							.findViewById(R.id.head);
					viewHolder.addView("content", mTv_content_main);
					viewHolder.addView("headImage", mIv_headerImage_main);
					convertView.setTag(viewHolder);
				} else {
					viewHolder = (MyViewHolder) convertView.getTag();
				}
				mTv_content_main = (TextView) viewHolder.getView("content");
				mIv_headerImage_main = (ImageView) viewHolder
						.getView("headImage");
				// 设置楼主问题
				if (position == 0) {
					mTv_content_main.setText(topic.getContent());
				}
				// 楼主的回复
				else {
					mTv_content_main.setText(list.get(position - 1)
							.getContent());
				}
				// 设置楼主头像
				if (StringUtils.isNotBlank(topic.getPublisher().getImg())) {
					String imgUrl = topic.getPublisher().getImg();// topic表里的imgs以;来分割各个图片路径
					mIv_headerImage_main.setTag(Constant.HOST + imgUrl);
					imageLoader.DisplayImage(Constant.HOST + imgUrl,
							mIv_headerImage_main, false);
				} else {
					mIv_headerImage_main.setBackground(getResources()
							.getDrawable(R.drawable.header_gray));
				}
			} else {
				MyViewHolder viewHolder = null;
				if (convertView == null) {
					viewHolder = new MyViewHolder();
					convertView = mInflater.inflate(R.layout.aa, null);
					viewHolder.addView("content",
							convertView.findViewById(R.id.topic_reply));
					viewHolder.addView("headImage",
							convertView.findViewById(R.id.myhead));
					convertView.setTag(viewHolder);
				} else {
					viewHolder = (MyViewHolder) convertView.getTag();
				}
				// 设置回复内容
				TextView t1 = (TextView) viewHolder.getView("content");
				t1.setText(list.get(position - 1).getContent().toString());
				ImageView iv_headImage = (ImageView) viewHolder
						.getView("headImage");
				if (StringUtils.isNotBlank(list.get(position - 1).getUser()
						.getImg())) {
					String imgUrl = list.get(position - 1).getUser().getImg();
					iv_headImage.setTag(Constant.HOST + imgUrl);
					imageLoader.DisplayImage(Constant.HOST + imgUrl,
							iv_headImage, false);
				} else {
					iv_headImage
							.setImageBitmap(((BitmapDrawable) getResources()
									.getDrawable(R.drawable.username))
									.getBitmap());
				}
			}
			return convertView;
		}

	}

}
