package com.greentech.wnd.android;

import org.apache.http.Header;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.greentech.wnd.android.cache.ImageLoader;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.HeaderImageUtils;
import com.greentech.wnd.android.util.LocationUtil;
import com.greentech.wnd.android.util.LocationUtil.OnLocationListener;
import com.greentech.wnd.android.util.UserInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 显示我的个人信息
 * 
 * @author zhoufazhan
 * 
 */
public class MyCardActivity extends BaseActivity {
	
	
	private TextView title_name;// 名子(头)
	private TextView username;// 名子
	private TextView area;// 所在地区
	private TextView agri_gz;// 关注作物
	private TextView expert_type;// 专家类型
	private TextView agri_sc;// 擅长作物
	private TextView company;// 单位
	private TextView addr;// 地址
	private TextView job;// 职位
	private TextView score;// 积分
	private TextView code;// 邀请码
	private TextView remark;// 简介
	private ImageView head;// 头像
	private ImageView back;// 返回
	private TextView adopted;// 被采纳数
	private TextView agreed;// 被同意
	private TextView question_num;// 问题个数
	private TextView answer_num;// 回答个数
	private int userId;
	private int isExpert;
	private String img = "";
	private LinearLayout expert_type_l;
	private LinearLayout agri_sc_l;
	private LinearLayout company_l;
	private LinearLayout addr_l;
	private LinearLayout job_l;
	private LinearLayout remark_l;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_card);
		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", 0);
		initView();
		initData();
		// initArea();

	}
	
	private void initView() {
		head = (ImageView) findViewById(R.id.img);
		back = (ImageView) findViewById(R.id.back);
		expert_type_l = (LinearLayout) findViewById(R.id.expert_type_l);
		agri_sc_l = (LinearLayout) findViewById(R.id.agri_sc_l);
		company_l = (LinearLayout) findViewById(R.id.company_l);
		addr_l = (LinearLayout) findViewById(R.id.addr_l);
		job_l = (LinearLayout) findViewById(R.id.job_l);
		remark_l = (LinearLayout) findViewById(R.id.remark_l);
		area = (TextView) findViewById(R.id.area);
		title_name = (TextView) findViewById(R.id.title_card);
		username = (TextView) findViewById(R.id.username);
		agri_gz = (TextView) findViewById(R.id.agri_gz);
		expert_type = (TextView) findViewById(R.id.expert_type);
		agri_sc = (TextView) findViewById(R.id.agri_sc);
		company = (TextView) findViewById(R.id.company);
		addr = (TextView) findViewById(R.id.addr);
		job = (TextView) findViewById(R.id.job);
		remark = (TextView) findViewById(R.id.remark);
		score = (TextView) findViewById(R.id.score);
		code = (TextView) findViewById(R.id.code);
		adopted = (TextView) findViewById(R.id.adopted);
		agreed = (TextView) findViewById(R.id.agreed);
		question_num = (TextView) findViewById(R.id.question_num);
		answer_num = (TextView) findViewById(R.id.answer_num);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MyCardActivity.this.finish();

			}
		});
	}

	// 向服务器请发送请求
	private void initData() {
		RequestParams params = new RequestParams();
		if (userId == 0) {
			params.put("user.id", UserInfo.getUserId(MyCardActivity.this));
		} else {
			params.put("user.id", userId);
		}

		final String url = Constant.SERVIER_PATH + "/json/findUserInfo.action";
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] responseBody) {
				String jsonString = new String(responseBody);
				JsonObject jsonObject = (JsonObject) GsonUtil.parse(jsonString);
				Log.i(Constant.TAG, jsonString);
				if (jsonObject.get("state").getAsString().equals("success")) {
					if (jsonObject.get("agriGz").getAsString() == "") {
						agri_gz.setVisibility(View.GONE);
					} else {
						agri_gz.setText(jsonObject.get("agriGz").getAsString());
					}
					isExpert = jsonObject.get("isExpert").getAsInt();
					//如果是专家用户，就显示专家类型等等信息，否则只显示所在区域和关注的作物
					//isExpert==0,不是专家
					if(isExpert==0){
						expert_type.setText(jsonObject.get("expertType")
								.getAsString());
						area.setText(jsonObject.get("addr").getAsString());
						
						
						expert_type_l.setVisibility(View.GONE);
						agri_sc_l.setVisibility(View.GONE);
						company_l.setVisibility(View.GONE);
						addr_l.setVisibility(View.GONE);
						job_l.setVisibility(View.GONE);
						remark_l.setVisibility(View.GONE);
					}else{
					expert_type.setText(jsonObject.get("expertType")
							.getAsString());
					agri_sc.setText(jsonObject.get("agriSc").getAsString());
					company.setText(jsonObject.get("company").getAsString());
					addr.setText(jsonObject.get("addr").getAsString());
					area.setText(jsonObject.get("addr").getAsString());
					job.setText(jsonObject.get("job").getAsString());
					remark.setText(jsonObject.get("remark").getAsString());
					}
					title_name
							.setText(jsonObject.get("username").getAsString() == "" ? "问农答网友的名片"
									: (jsonObject.get("username").getAsString() + "的名片"));
					username.setText(jsonObject.get("username").getAsString() == "" ? "问农答网友"
							: jsonObject.get("username").getAsString());
					adopted.setText(jsonObject.get("adopted").getAsString());
					agreed.setText(jsonObject.get("agreed").getAsString());
					question_num.setText(jsonObject.get("questions")
							.getAsString());
					answer_num.setText(jsonObject.get("answers").getAsString());
					// 填写邀请码
					code.setText("邀请码:" + jsonObject.get("yqm").getAsString());
					// 填写积分
					score.setText("积分:" + jsonObject.get("bound").getAsString());
					// 给头像路径赋值
					img = jsonObject.get("img").getAsString();
					if (userId != 0) {
						if (img != "") {
							ImageLoader loader = new ImageLoader(
									MyCardActivity.this);
							loader.DisplayImage(Constant.HOST + img, head,
									false);
						} else {
							head.setImageBitmap(((BitmapDrawable) getResources()
									.getDrawable(R.drawable.header_gray))
									.getBitmap());
						}
					}

				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub

			}
		});
		// 设置头像
		if (userId == 0) {
			if (HeaderImageUtils.getHeaderImageFromSD(this) != null) {
				head.setImageBitmap(HeaderImageUtils.getHeaderImageFromSD(this));
			}
		}

	}

	public void initArea() {
		LocationUtil locationUtil = new LocationUtil(this);
		locationUtil.setOnLocationListener(new OnLocationListener() {

			@Override
			public void setLocation(String province, String city,
					String district) {
				area.setText(province + city + district);

			}

		});
	}
}
