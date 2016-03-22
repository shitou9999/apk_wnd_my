package com.greentech.wnd.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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

public class ShowImageActivity extends BaseActivity implements ViewFactory {
	private ImageSwitcher is;
	private TextView text;// 显示当前图片位置
	private Topic topic;
	private int downX;
	private int upX;
	private int curIndex;
	private int imgLength;
	private Drawable[] d;
	Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_image);
		is = (ImageSwitcher) findViewById(R.id.is);
		text = (TextView) findViewById(R.id.imgNum);
		topic = (Topic) getIntent().getExtras().get("topic");
		final String[] imgUrls = topic.getImgs().split(";");
		d = new Drawable[imgUrls.length];
		imgLength = imgUrls.length;
		text.setText("1/" + imgLength);
		is.setFactory(this);
		new Thread(new Runnable() {
			InputStream stream;

			@Override
			public void run() {
				for (int i = 0; i < imgUrls.length; i++) {
					try {

						stream = new URL(Constant.HOST + imgUrls[i])
								.openStream();
						d[i] = Drawable.createFromStream(stream, "image.jpg");

						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								is.setBackground(d[0]);

							}
						});
						//

					} catch (Exception e) {
						try {
							if (stream != null) {
								stream.close();
							}

						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				}

			}
		}).start();
		// 这里设置的动画没有效果（原因未知）
		is.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.slide_in_right));
		is.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.slide_out_right));

		is.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					downX = (int) event.getX(); // 取得按下时的坐标

					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {

					upX = (int) event.getX(); // 取得松开时的坐标

					// 从左拖到右，即看前一张
					if (upX - downX > 100) {
						curIndex--;

						if (curIndex < 0) {
							curIndex = imgUrls.length - 1;
							ShowImageActivity.this.finish();
						}

						is.setBackground(d[curIndex]);

					} else if (downX - upX > 100) { // 从右拖到左，即看后一张
						curIndex++;
						if (curIndex > imgUrls.length - 1) {
							curIndex = 0;
							ShowImageActivity.this.finish();
						}

						is.setBackground(d[curIndex]);
					}
					text.setText(curIndex + 1 + "/" + imgLength);
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

}
