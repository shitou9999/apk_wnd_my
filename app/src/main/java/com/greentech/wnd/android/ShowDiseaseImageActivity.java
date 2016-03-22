package com.greentech.wnd.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.greentech.wnd.android.bean.Topic;
import com.greentech.wnd.android.constant.Constant;
import com.loopj.android.http.AsyncHttpClient;

public class ShowDiseaseImageActivity extends BaseActivity implements
		ViewFactory {
	private ImageSwitcher is;
	private TextView text;// 显示当前图片位置
	private int downX;
	private int upX;
	private int curIndex;
	private int imgLength;
	private Drawable[] d;
	Handler handler = new Handler();
	private CharSequence[] urls;

	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_image);
		is = (ImageSwitcher) findViewById(R.id.is);
		text = (TextView) findViewById(R.id.imgNum);
		urls = getIntent().getCharSequenceArrayExtra("urls");
		position = getIntent().getIntExtra("position", 0);
		curIndex = position + 1;
		d = new Drawable[urls.length];
		imgLength = urls.length;
		text.setText(curIndex + "/" + imgLength);
		is.setFactory(this);
		DownImageAsyncTask task=new DownImageAsyncTask();
		task.execute();
		// 这里设置的动画没有效果（原因未知）
		is.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.slide_in_right));
		is.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.slide_out_right));

		is.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 记录按下的x坐标

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					downX = (int) event.getX(); // 取得按下时的坐标

					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// 记录抬起的x坐标
					upX = (int) event.getX(); // 取得松开时的坐标

					// 从左拖到右，即看前一张
					if (upX - downX > 100) {
						curIndex--;

						if (curIndex < 0 || curIndex == 0) {
							// curIndex = urls.length - 1;
							ShowDiseaseImageActivity.this.finish();
						} else {
							is.setBackground(d[curIndex - 1]);
							text.setText(curIndex + "/" + imgLength);
						}

					} else if (downX - upX > 100) { // 从右拖到左，即看后一张
						curIndex++;
						if (curIndex > urls.length) {
							// curIndex = 0;
							ShowDiseaseImageActivity.this.finish();
						} else {
							is.setBackground(d[curIndex - 1]);
							text.setText(curIndex + "/" + imgLength);
						}

					}
					
					return true;
				}
				return false;
			}
		});

	}

	@Override
	public View makeView() {
		return new ImageView(this);
	}

	public class DownImageAsyncTask extends
			AsyncTask<Void, Void, Void> {
		InputStream stream;

		@Override
		protected Void doInBackground(Void... params) {
			
				for (int i = 0; i < urls.length; i++) {
					try {
					stream = new URL(urls[i].toString()).openStream();
					d[i] = Drawable.createFromStream(stream, "image.png");	
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (stream != null) {
							try {
								stream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					}
				}
			

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(d[0]!=null){
				is.setBackground(d[position]);
			}
			
		}

	}

}
