package com.greentech.wnd.android;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.greentech.wnd.android.util.HeaderImageUtils;
import com.greentech.wnd.android.util.UserInfo;

public class MainFragment extends Fragment implements OnClickListener{
	private ViewPager viewPager;
	private List<ImageView> imageViews;// viewPager装载的图片
	private int[] imageResId; // 图片ID
	private List<View> dots; // 图片标题正文下面的那些点
	private int currentItem = 0; // 当前图片的索引号
	private ScheduledExecutorService scheduledExecutorService;// 启动一个线程执行定时任务

	ScaleAnimation animation = new ScaleAnimation(1.0f, 0.9f, 1.0f, 0.9f,
			Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

	private ImageButton btn_ask;
	private ImageButton btn_disease_info;
	private ImageButton tech;
	private Context mContext;
	private ImageView headerImage;

	// 构造方法
	public MainFragment() {

	}

	// 切换图片
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// 这个方法就是 设置当前的页面 参数 为 int 类型
			viewPager.setCurrentItem(currentItem);// 切换当前显示的图片
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化图片资源
		imageResId = new int[] { R.drawable.avd1, R.drawable.avd2 };
		imageViews = new ArrayList<ImageView>();
		int i = 0;
		for (int id : imageResId) {
			ImageView iv = new ImageView(getActivity());
			iv.setImageResource(id);
			iv.setScaleType(ScaleType.CENTER);
			imageViews.add(iv);
			if(i == 0) {
				iv.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Uri uri = Uri.parse("http://www.3w3n.com");
						Intent intent = new Intent(Intent.ACTION_VIEW,uri);
						startActivity(intent);

						
					}
				});
			}
			i++;
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = (Context) container.getContext();
		View view = inflater.inflate(R.layout.main_fragment, null);
		btn_ask = (ImageButton) view.findViewById(R.id.ask);
		btn_disease_info = (ImageButton) view.findViewById(R.id.disease_info);
		tech = (ImageButton) view.findViewById(R.id.tech);
		headerImage = (ImageView) view.findViewById(R.id.headerImage);

		btn_ask.setOnClickListener(this);
		btn_disease_info.setOnClickListener(this);
		tech.setOnClickListener(this);
		headerImage.setOnClickListener(this);
		// 初始化dots
		dots = new ArrayList<View>();
		dots.add(view.findViewById(R.id.v_dot0));
		dots.add(view.findViewById(R.id.v_dot1));
		// 初始化viewPager
		viewPager = (ViewPager) view.findViewById(R.id.vp);
		viewPager.setAdapter(new MyPagerAdapter());
		viewPager.setOnPageChangeListener(new MyPagerChangerListener());
		return view;
	}


	@Override
	public void onResume() {
		super.onResume();
		/**
		 * 在内存卡里面读取头像
		 */
		if (HeaderImageUtils.getHeaderImageFromSD(getActivity()) != null) {
			headerImage.setImageBitmap(HeaderImageUtils
					.getHeaderImageFromSD(getActivity()));
		}
	}

	/**
	 * 生命周期中 当 当前 activity 开始运行时 执行的方法 scheduledExecutorService可以以固定时间来发送一个任务
	 * 1表示启动延时1秒。3表示切换间隔3秒
	 */
	@Override
	public void onStart() {

		super.onStart();
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 5,
				TimeUnit.SECONDS);

	}

	// 执行定时更新图片任务
	class ScrollTask implements Runnable {

		@Override
		public void run() {
			synchronized (viewPager) {
				currentItem = (currentItem + 1) % imageViews.size();
				handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
			}
		}

	}

	@Override
	public void onStop() {
		// 当Activity不可见的时候停止切换
		scheduledExecutorService.shutdown();
		super.onStop();
	}

	// ViewPager适配器
	class MyPagerAdapter extends PagerAdapter {
		// 获得适配数据的 数量
		@Override
		public int getCount() {
			return imageResId.length;
		}

		// 初始化 页面时 所调用的方法
		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(imageViews.get(arg1));
			return imageViews.get(arg1);
		}

		// 页面销毁时 所调用的方法
		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		// 判断 view 是否与对象 相等
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 * 
	 * @author Administrator
	 * 
	 */
	class MyPagerChangerListener implements OnPageChangeListener {
		private int oldPosition = 0;

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {

		}

		@Override
		public void onPageSelected(int position) {
			currentItem = position;
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;
		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 免费提问
		case R.id.ask:
			animation.setDuration(100);
			btn_ask.startAnimation(animation);
			animation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					if (UserInfo.isLogin(getActivity())) {
						Intent intent = new Intent(getActivity(),
								AddTopicActivity.class);
						startActivity(intent);
					} else {
						Intent intent = new Intent(getActivity(),
								LoginActivity.class);
						startActivity(intent);
					}
				}
			});
			break;
		// 病虫害信息
		case R.id.disease_info:
			animation.setDuration(100);
			btn_disease_info.startAnimation(animation);
			animation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {

					startActivity(new Intent(mContext,
							DiseaseInformationActivity.class));

				}
			});

			break;
		// 自诊
		case R.id.tech:
			animation.setDuration(100);
			tech.startAnimation(animation);
			animation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					// startActivity(new Intent(mContext,
					// MainTechListActivity.class));
					startActivity(new Intent(mContext, AutognosisActivity.class));

				}
			});

			break;
		// 点击头像
		case R.id.headerImage:
           MainActivity mainActivity=(MainActivity) getActivity();
           
           mainActivity.getmDrawerLayout().openDrawer(Gravity.LEFT);;
           
           
		default:
			break;
		}

	}

	
}
