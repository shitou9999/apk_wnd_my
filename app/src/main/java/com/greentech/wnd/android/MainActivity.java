package com.greentech.wnd.android;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.greentech.wnd.android.adapter.SimpleFragmentPagerAdapter;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.view.MyViewPager;
import com.greentech.wnd.android.view.MyViewPager.OnSlideListener;

public class MainActivity extends FragmentActivity {
	// 构建一个handler传递给其他fragment来使用。
	private Handler mHandler = new Handler();
	// 管理Fragment，包括左右抽出的侧滑界面，不包括添加进ViewPager的Fragment（因为没有添加进FragmentManager）
	private FragmentManager mFragmentManager;
	private MyViewPager mViewPager;
	// 装载viewpager的四个fragment
	private List<Fragment> tabFragmentList;// 除了左右
	private DrawerLayout mDrawerLayout;
	private RelativeLayout mLeftLayout;
	private RelativeLayout mCenterLayout;
	private LinearLayout mCenterContentLayout;
	private LinearLayout mTabContanerLayout;
	private int oldTabSelectedIndex;
	private int[][] tabBtnDrawables = {
			{ R.drawable.btn_home, R.drawable.btn_home_hover },
			{ R.drawable.btn_zjdy, R.drawable.btn_zjdy_hover },
			{ R.drawable.btn_zizhen, R.drawable.btn_zizhen_hover },
			{ R.drawable.btn_mine, R.drawable.btn_mine_hover } };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// 160dpi的screen-density值为1（160/160），120dpi的screen-density值为0.75（120/160）
		// 获取所有下面后面用到的view等变量
		mFragmentManager = getSupportFragmentManager();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);// 抽屉根view

		mViewPager = (MyViewPager) findViewById(R.id.viewPager);
		mLeftLayout = (RelativeLayout) findViewById(R.id.main_left);// 左侧抽屉
		mCenterLayout = (RelativeLayout) findViewById(R.id.main_center);// 中间根view
		mCenterContentLayout = (LinearLayout) findViewById(R.id.main_center_content);// 中间根view的内容。
		mCenterLayout = (RelativeLayout) mDrawerLayout.getChildAt(0);// 中间主View;
		mTabContanerLayout = (LinearLayout) findViewById(R.id.tab_bottom);// 底部tab容器
		// 给底部按钮设置监听器
		for (int i = 0; i < mTabContanerLayout.getChildCount(); i++) {
			final int current = i;
			mTabContanerLayout.getChildAt(i).setOnClickListener(
					new OnClickListener() {
						private int currentIndex = current;

						@Override
						public void onClick(View v) {
							mViewPager.setCurrentItem(currentIndex);
							changeTabState(currentIndex);
						}
					});
		}
		// 添加左右fragment
		FragmentTransaction fragmentTransaction = mFragmentManager
				.beginTransaction();

		fragmentTransaction.add(R.id.main_left, new LeftMainSimpleFragment(),
				"main_left");
		fragmentTransaction.commit();

		// 填充几个Fragment进ViewPager,Fragment必须是v4版本的
		tabFragmentList = new ArrayList<Fragment>();

		tabFragmentList.add(new MainFragment());
		tabFragmentList.add(new QAFragment(true, false, 2, null));
		tabFragmentList.add(new SelectedDiseaseFragment(false));
		tabFragmentList.add(new LoginFragment(mHandler));
		// 注意：因为这里虽然把mFragmentManager传了进去，但是因为SimpleFragmentPagerAdapter里面显示的是tabFragmentList，
		// 所以mFragmentManager里的两个侧滑界面不会被显示出来。
		// 但是mViewPager.getChildCount()却是计算的mFragmentManager里面的Fragment个数
		/**
		 * mViewPager.setAdapter(PagerAdapter adapter)
		 * PagerAdapter是FragmentAdapter的父类，如果ViewPager配合fragment使用就会用到
		 * FragmentPagerAdapter适配器
		 * ，这里的SimpleFragmentPagerAdapter继承了FragmentPagerAdapter
		 * 并且重载了他的构造，传入一个list，用来重写FragmentPagerAdapter里面的getItem(int arg0)和
		 * getCount()方法
		 */
		// 设置缓存页面个数，3表示当前页面左右两边各三个界面。一共缓存六个界面，默认是1，即缓存当前页面的相邻两个，其他的destoryView
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setAdapter(new SimpleFragmentPagerAdapter(mFragmentManager,
				tabFragmentList));
		mViewPager.setOnSlideListener(new OnSlideListener() {

			@Override
			public void onSlide(float dx) {
				// 滑动距离大于宽度的五分之一，就滑动
				if (dx > 0 && dx > (mViewPager.getWidth() / 6)) {
					// 是向右滑动
					if (mViewPager.getCurrentItem() == 0) {
						// Log.wtf("注意：", "是在第一个tab向右滑动，那么抽出DrawLayout左边的滑动界面");
						mDrawerLayout.openDrawer(Gravity.LEFT);
					}
				}
				// else if (dx < 0 && dx < (mViewPager.getWidth() / 6)) {
				// // 是向左滑动
				// if (mViewPager.getCurrentItem() == (tabFragmentList.size() -
				// 1)) {
				// // Log.wtf("注意：", "是在第一个tab向右滑动，那么抽出DrawLayout左边的滑动界面");
				// mDrawerLayout.openDrawer(Gravity.RIGHT);
				// }
				// }
			}
		});
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				changeTabState(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		mDrawerLayout.setScrimColor(Color.TRANSPARENT);// Scrim设置的是这个DrawerLayout抽屉和主页面的背景颜色

		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		// （DrawerLayout的LOCK_MODE_LOCKED_CLOSED和LOCK_MODE_LOCKED_OPEN意思跟实现应该弄反了吧）设置为仅代码打开，禁止DrawerLayout默认的手指从左右贴边处拨动抽出侧滑抽屉行为，不然的话左抽屉抽出的时候，点击右边贴边处往轻点的时候，左边抽屉收进去的动作会有问题（就是因为默认的会将右边抽屉打开而导致的。又因为轻点的时候手指移动不够，抽屉不会完全抽出，而是抽出一点之后又收回去，所以表现出来的现象并不是左边抽屉收进去右边抽屉完全抽出）
		mDrawerLayout.setDrawerListener(new DrawerListener() {
			float drawDistanceX, minScale = 0.87f, toX, scale, d_scaled_total;
			private Method setScaleX, setScaleY;

			private void resetPrivate() {
				drawDistanceX = 0;
				toX = 0;
				scale = 0;
			}

			@Override
			public void onDrawerStateChanged(int arg0) {
				// 状态
				// Log.wtf("状态：", ""+arg0);
				if (arg0 == DrawerLayout.STATE_SETTLING) {

				} else if (arg0 == DrawerLayout.STATE_SETTLING) {

				} else if (arg0 == DrawerLayout.STATE_IDLE) {

				}
			}

			/**
			 * arg1应该是侧滑的比例，如果在变大，表示滑出来，如果在变小，表示收进去。
			 */
			@Override
			public void onDrawerSlide(View arg0, float arg1) {
				DisplayMetrics outMetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
				// 将主界面内容侧滑"+arg1比例的+"距离，同时比例缩放
				// 先缩放mCenterLayout中的第一个子view，并计算缩放后宽度的差值
				// Log.wtf("状态：", ""+arg1);

				scale = (1 - arg1) * (1 - minScale) + minScale;
				if (Build.VERSION.SDK_INT >= 11) {
					// 由于编译版本是2.2，在2.2里面没有setScaleX方法，所以用反射机制调用
					try {
						if (setScaleX == null) {
							setScaleX = LinearLayout.class.getMethod(
									"setScaleX", new Class[] { float.class });
						}
						if (setScaleY == null) {
							setScaleY = LinearLayout.class.getMethod(
									"setScaleY", new Class[] { float.class });
						}
						if (setScaleX != null) {
							setScaleX.invoke(mCenterContentLayout, scale);
						}
						if (setScaleY != null) {
							setScaleY.invoke(mCenterContentLayout, scale);
						}
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
					// mCenterContentLayout.setScaleX(scale);
					// mCenterContentLayout.setScaleY(scale);
					d_scaled_total = mCenterContentLayout.getWidth()
							* (1 - scale) / 2;// 现在的缩放级别和不缩放时的宽度差值
				}
				drawDistanceX = arg1 * arg0.getWidth();// 抽屉抽出来的距离

				// Log.wtf("滑动中：", ""+arg1);
				if (arg0.equals(mDrawerLayout.getChildAt(1))) {
					// 如果是左边的抽屉，则arg0在变大的话，就把centerView向右比例移动，变小的话，向左比例移动
					// 如果不缩放的话，temp就表示移动位置，加上缩放之后，移动位置需要考虑缩放后的X距离
					toX = -drawDistanceX + d_scaled_total;
				} else if (arg0.equals(mDrawerLayout.getChildAt(2))) {
					// 如果是右边的抽屉，则arg0在变大的话，就把centerView向左比例移动，变小的话，向右比例移动
					toX = drawDistanceX - d_scaled_total;
				}
				// 设置mCenterLayout的scroll位置，效果是把mCenterLayout中的第一个子view滚动。
				mCenterLayout.scrollTo((int) toX, 0);
			}

			@Override
			public void onDrawerOpened(View arg0) {
				resetPrivate();
				// 打开之后，需要把当前抽屉设置为可以滑动回去，解锁
				if (arg0.equals(mDrawerLayout.getChildAt(1))) {
					// 如果是左边的抽屉
					mDrawerLayout.setDrawerLockMode(
							DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT);
				} else {
					// 如果是右边边的抽屉
					mDrawerLayout.setDrawerLockMode(
							DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.RIGHT);
				}
			}

			@Override
			public void onDrawerClosed(View arg0) {
				resetPrivate();
				// 抽屉关闭后，再次加锁
				if (arg0.equals(mDrawerLayout.getChildAt(1))) {
					// 如果是左边的抽屉
					mDrawerLayout.setDrawerLockMode(
							DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.LEFT);
				} else {
					// 如果是右边边的抽屉
					mDrawerLayout
							.setDrawerLockMode(
									DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
									Gravity.RIGHT);
				}
			}
		});

	}



	public DrawerLayout getmDrawerLayout() {
		return mDrawerLayout;
	}


	/**
	 * 底部tab选中时，改变tab状态
	 */
	private void changeTabState(int newSel) {
		TextView oldTextView = ((TextView) ((LinearLayout) mTabContanerLayout
				.getChildAt(oldTabSelectedIndex)).getChildAt(1));
		TextView currentTextView = ((TextView) ((LinearLayout) mTabContanerLayout
				.getChildAt(newSel)).getChildAt(1));
		oldTextView.setTextColor(getResources().getColor(
				R.color.color_black));
		currentTextView.setTextColor(getResources().getColor(
				R.color.color_black));

		ImageView oldImageView = ((ImageView) ((LinearLayout) mTabContanerLayout
				.getChildAt(oldTabSelectedIndex)).getChildAt(0));
		ImageView currentImageView = ((ImageView) ((LinearLayout) mTabContanerLayout
				.getChildAt(newSel)).getChildAt(0));
		oldImageView
				.setBackgroundResource(tabBtnDrawables[oldTabSelectedIndex][0]);
		currentImageView.setBackgroundResource(tabBtnDrawables[newSel][1]);

		oldTabSelectedIndex = newSel;

	}

	/**
	 * 菜单、返回键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click(); // 调用双击退出函数
		}
		return false;
	}

	/**
	 * 双击退出函数
	 */
	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {
			setResult(RESULT_OK);
			finish();
			System.exit(0);
		}
	}


}
