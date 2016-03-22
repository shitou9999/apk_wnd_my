package com.greentech.wnd.android;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.JsonObject;
import com.greentech.wnd.android.DiseaseInfoMainActivity.MyGridViewAdapter;
import com.greentech.wnd.android.cache.ImageLoader;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.UserInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
/**
 *病因详情
 *第一版使用textview的mTextContent.setMovementMethod(new ScrollingMovementMethod());
 *方法来实现滑动
 *
 *第二版使用scrollView来实现滑动效果（这个更快，顺滑）
 *
 */
public class DiseaseInfoActivity extends Activity {
	
	TextView mTextTitle,mTextContent;
	private Button btn_collection;
	private LinearLayout ll;
	int  diseaseId;
	WebView wv;
	private GridView gridView;
	private MyGridViewAdapter gridViewAdapter;
	private int width;
	private int height;
	String[] urls;
	ImageLoader loader = new ImageLoader(DiseaseInfoActivity.this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diseaseinfo);
		ll=(LinearLayout) findViewById(R.id.ll);
        mTextTitle=(TextView)findViewById(R.id.textTitle);
        gridView=(GridView)findViewById(R.id.gridView);
        btn_collection=(Button)findViewById(R.id.btn_collection);
        wv=(WebView)findViewById(R.id.textContent);
        DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels; // 屏幕宽度（像素）
		height = metric.heightPixels; // 屏幕高度（像素）
        init();
	}
	private void init() {
		Intent intent=getIntent();
		String title=intent.getStringExtra("title");
		String content=intent.getStringExtra("content");
		String url_img=intent.getStringExtra("url_img");
		if(StringUtils.isNotBlank(url_img)){
			urls=url_img.split(";");
			gridViewAdapter = new MyGridViewAdapter(urls);
			gridView.setAdapter(gridViewAdapter);
			// 设置GridView的宽度
			// 一张或两张图片的时候，高度为一半的宽度-10pix
			if (urls.length <= 2) {
				LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT,
						width / 2 + 20);
				gridView.setLayoutParams(param);
			}
			if (2 < urls.length && urls.length <= 4) {
				LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT, width + 20);
				gridView.setLayoutParams(param);
			}
			if (4 < urls.length && urls.length <= 6) {
				LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT, (int) (width*1.5));
				gridView.setLayoutParams(param);
			}
			
			
			
			
//			for(int i=0;i<urls.length;i++){
//				ImageView imageView = new ImageView(
//						DiseaseInfoActivity.this);
//				imageView.setLayoutParams(new LayoutParams(800, 600));
//				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//				loader.DisplayImage(urls[i], imageView, false);
//				
//				ll.addView(imageView);
//		}
		}
		else{
			gridView.setVisibility(View.GONE);
		}
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(StringUtils.isNotBlank(urls[0])){
					Intent intent = new Intent(DiseaseInfoActivity.this,ShowDiseaseImageActivity.class);
					intent.putExtra("urls", urls);
					intent.putExtra("position", position);
//					intent.putExtra("urls", BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
					startActivity(intent);
				}
				
				
			}
		});
		diseaseId=intent.getIntExtra("diseaseId",0);
		//设置是否收藏的初始值
		isCollected(UserInfo.getUserId(this),diseaseId,(byte)2);
		WebSettings setting=wv.getSettings();
		setting.setJavaScriptEnabled(true);
		wv.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
		
		mTextTitle.setText(title);
		btn_collection.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (UserInfo.isLogin(DiseaseInfoActivity.this)) {
					// 需要传入userId,topic表里面的id，和自定义的收藏类型1.2.3
					RequestParams params = new RequestParams();
					params.put("userId",
							UserInfo.getUserId(DiseaseInfoActivity.this));
					params.put("refId", diseaseId);
					params.put("type", (byte) 2);// 2代表：病害信息
					AsyncHttpClient client = new AsyncHttpClient();
					String url = Constant.SERVIER_PATH
							+ "/json/modifyCollection.action";
					client.get(url, params, new AsyncHttpResponseHandler() {

						@Override
						public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
							String str = new String(arg2);
							JsonObject json = (JsonObject) GsonUtil.parse(str);
							int i=json.get("collectionNum").getAsInt();
							//i!=0,说明收藏被取消，现在是未收藏状态
							if(i!=0){
								Drawable d_no = getResources().getDrawable(
										R.drawable.ic_favo_no);
								btn_collection.setCompoundDrawablesWithIntrinsicBounds(d_no,
										null, null, null);
								btn_collection.setText("未收藏");
							}else{
								Drawable d = getResources().getDrawable(
										R.drawable.ic_favo_yes);
								btn_collection.setCompoundDrawablesWithIntrinsicBounds(d,
										null, null, null);
								btn_collection.setText("已收藏");
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1, byte[] arg2,
								Throwable arg3) {
							// TODO Auto-generated method stub

						}
					});
					
				} else {
					Intent intent = new Intent(DiseaseInfoActivity.this,
							LoginActivity.class);
					startActivity(intent);
				}
				
				
				
				
			}
		});
	}
	public void isCollected(int userId, int refId, byte type) {

		RequestParams params = new RequestParams();
		params.put("userId", UserInfo.getUserId(this));
		params.put("refId", diseaseId);
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
					btn_collection.setCompoundDrawablesWithIntrinsicBounds(d,
							null, null, null);
					btn_collection.setText("已收藏");
				}

			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub

			}
		});

	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		loader.clearCache();
	}
	public class MyGridViewAdapter extends BaseAdapter {
		String[] urls;

		public MyGridViewAdapter(String[] urls) {
			this.urls = urls;
		}

		@Override
		public int getCount() {
			return urls.length;
		}

		@Override
		public Object getItem(int position) {
			return urls[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView iv;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.grid_disease, null);
			}
			iv = (ImageView) convertView.findViewById(R.id.iv);
			RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT, height / 4);
			iv.setLayoutParams(param);
			loader.DisplayImage(urls[position], iv, false);
			if(iv.getBackground()!=null){
				Drawable d=iv.getBackground();
				BitmapDrawable bd = (BitmapDrawable) d;
				Bitmap b=bd.getBitmap();
			}
			
			return convertView;
		}

	}
}
