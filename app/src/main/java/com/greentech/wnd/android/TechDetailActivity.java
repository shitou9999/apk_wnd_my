package com.greentech.wnd.android;

import org.apache.http.Header;

import com.google.gson.JsonObject;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.UserInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 实用技术信息详情
 * 
 * @author WP
 * 
 */
public class TechDetailActivity extends BaseActivity {

	private WebView content;
	private TextView type;
	private TextView title;
	private TextView releaseTime;
	private ImageView iv;
//	private Button btn_collect;
	private int id;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置布局
		setContentView(R.layout.tech_detail);
		init();

	}

	private void init() {
		content = (WebView) this.findViewById(R.id.content);
		type = (TextView) this.findViewById(R.id.type);
		title = (TextView) this.findViewById(R.id.title);
//		btn_collect = (Button) this.findViewById(R.id.collect);
		iv = (ImageView) findViewById(R.id.back);
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TechDetailActivity.this.finish();

			}
		});

		releaseTime = (TextView) this.findViewById(R.id.releaseTime);
		title.setText(Html.fromHtml(this.getIntent().getExtras().getString("title")));
		id = Integer.valueOf(this.getIntent().getExtras().getString("id")).intValue();
		
		releaseTime.setText(this.getIntent().getExtras()
				.getString("releaseTime"));
		type.setText(this.getIntent().getExtras().getString("type"));
		showReportDetail(this.getIntent().getExtras().getString("content"));
		// 判断此条记录有没有被收藏
		isCollected(UserInfo.getUserId(this), id, (byte) 3);
		// 收藏按钮
//		btn_collect.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (UserInfo.isLogin(TechDetailActivity.this)) {
//					// 需要传入userId,topic表里面的id，和自定义的收藏类型1.2.3
//					RequestParams params = new RequestParams();
//					params.put("userId",
//							UserInfo.getUserId(TechDetailActivity.this));
//					params.put("refId", id);
//					params.put("type", (byte) 3);// 3代表：实用信息
//					AsyncHttpClient client = new AsyncHttpClient();
//					String url = Constant.SERVIER_PATH
//							+ "/json/modifyCollection.action";
//					client.get(url, params, new AsyncHttpResponseHandler() {
//
//						@Override
//						public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
//							String str = new String(arg2);
//							JsonObject json = (JsonObject) GsonUtil.parse(str);
//							int i = json.get("collectionNum").getAsInt();
//							// i!=0,说明收藏被取消，现在是未收藏状态
//							if (i != 0) {
//								Drawable d_no = getResources().getDrawable(
//										R.drawable.ic_favo_no);
//								btn_collect
//										.setCompoundDrawablesWithIntrinsicBounds(
//												d_no, null, null, null);
//								btn_collect.setText("未收藏");
//							} else {
//								Drawable d = getResources().getDrawable(
//										R.drawable.ic_favo_yes);
//								btn_collect
//										.setCompoundDrawablesWithIntrinsicBounds(d,
//												null, null, null);
//								btn_collect.setText("已收藏");
//							}
//						}
//
//						@Override
//						public void onFailure(int arg0, Header[] arg1, byte[] arg2,
//								Throwable arg3) {
//							// TODO Auto-generated method stub
//
//						}
//					});
//				} else {
//					Intent intent = new Intent(TechDetailActivity.this,
//							LoginActivity.class);
//					startActivity(intent);
//				}
//				
//				
//				
//
//			}
//		});
	}

	private void showReportDetail(String c) {
		// webView.setInitialScale(100);
		WebSettings webSetting = content.getSettings();
		webSetting.setJavaScriptEnabled(true);
		// 设置可以支持缩放
		// webSetting.setSupportZoom(true);
		// webSetting.setUseWideViewPort(true);
		// 设置默认缩放方式尺寸是far
		// webSetting.setDefaultZoom(WebSettings.ZoomDensity.FAR);
		// 设置出现缩放工具
		webSetting.setBuiltInZoomControls(true);
		// 让网页自适应屏幕宽度
		// webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		content.loadDataWithBaseURL(null, c, "text/html", "utf-8", null);
	}

	public void isCollected(int userId, int refId, byte type) {

		RequestParams params = new RequestParams();
		params.put("userId", UserInfo.getUserId(this));
		params.put("refId", id);
		params.put("type", type);// 1代表：我的问题收藏
		AsyncHttpClient client = new AsyncHttpClient();
		String url = Constant.SERVIER_PATH + "/json/isCollected.action";
		client.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				String str = new String(arg2);
				JsonObject json = (JsonObject) GsonUtil.parse(str);
				boolean isCollected = json.get("state").getAsBoolean();
				if (isCollected) {
					Drawable d = getResources().getDrawable(
							R.drawable.ic_favo_yes);
//					btn_collect.setCompoundDrawablesWithIntrinsicBounds(d,
//							null, null, null);
//					btn_collect.setText("已收藏");
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
	 * 按键点击事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			TechDetailActivity.this.finish();
			return true;
		} else {
			return false;
		}
	}

}
