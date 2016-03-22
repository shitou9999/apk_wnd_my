package com.greentech.wnd.android;

import com.greentech.wnd.android.constant.Constant;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class CollectActivity extends FragmentActivity {
	private ImageView back;
	private ViewPager mViewPager;
	private PagerAdapter mPagerAdapter;
	private Button[] mBtnTabs = new Button[3];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collection);
		back = (ImageView) findViewById(R.id.back);
		mBtnTabs[0] = (Button) findViewById(R.id.question);
		mBtnTabs[0].setFocusable(true);
		mBtnTabs[0].setOnClickListener(mTabClickListener);

		mBtnTabs[1] = (Button) findViewById(R.id.disease);
		mBtnTabs[1].setFocusable(true);
		mBtnTabs[1].setOnClickListener(mTabClickListener);

		mBtnTabs[2] = (Button) findViewById(R.id.tech);
		mBtnTabs[2].setFocusable(true);
		mBtnTabs[2].setOnClickListener(mTabClickListener);
		// 默认选择第一个
		mBtnTabs[0].setBackgroundColor(getResources().getColor(R.color.white));
		mBtnTabs[1].setBackgroundColor(getResources().getColor(
				R.color.white_gray));
		mBtnTabs[2].setBackgroundColor(getResources().getColor(
				R.color.white_gray));

		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(mPageChangeListener);
		mViewPager.setCurrentItem(0);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CollectActivity.this.finish();
				
			}
		});
	}

	private OnClickListener mTabClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == mBtnTabs[0]) {
				 mViewPager.setCurrentItem(0);
				mBtnTabs[0].setBackgroundColor(getResources().getColor(
						R.color.white));
				mBtnTabs[1].setBackgroundColor(getResources().getColor(
						R.color.white_gray));
				mBtnTabs[2].setBackgroundColor(getResources().getColor(
						R.color.white_gray));
			} else if (v == mBtnTabs[1]) {
				 mViewPager.setCurrentItem(1);
				mBtnTabs[0].setBackgroundColor(getResources().getColor(
						R.color.white_gray));
				mBtnTabs[1].setBackgroundColor(getResources().getColor(
						R.color.white));
				mBtnTabs[2].setBackgroundColor(getResources().getColor(
						R.color.white_gray));

			} else {
				 mViewPager.setCurrentItem(2);
				mBtnTabs[0].setBackgroundColor(getResources().getColor(
						R.color.white_gray));
				mBtnTabs[1].setBackgroundColor(getResources().getColor(
						R.color.white_gray));
				mBtnTabs[2].setBackgroundColor(getResources().getColor(
						R.color.white));
			}
		}
	};
	private OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			if (arg0 == 0) {
				mBtnTabs[0].setBackgroundColor(getResources().getColor(
						R.color.white));
				mBtnTabs[1].setBackgroundColor(getResources().getColor(
						R.color.white_gray));
				mBtnTabs[2].setBackgroundColor(getResources().getColor(
						R.color.white_gray));
			} else if (arg0 == 1) {
				mBtnTabs[0].setBackgroundColor(getResources().getColor(
						R.color.white_gray));
				mBtnTabs[1].setBackgroundColor(getResources().getColor(
						R.color.white));
				mBtnTabs[2].setBackgroundColor(getResources().getColor(
						R.color.white_gray));
			} else {
				mBtnTabs[0].setBackgroundColor(getResources().getColor(
						R.color.white_gray));
				mBtnTabs[1].setBackgroundColor(getResources().getColor(
						R.color.white_gray));
				mBtnTabs[2].setBackgroundColor(getResources().getColor(
						R.color.white));
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	private class MyPagerAdapter extends FragmentStatePagerAdapter {

		public MyPagerAdapter(android.support.v4.app.FragmentManager fm) {
			super(fm);
		}

		@Override
		public android.support.v4.app.Fragment getItem(int position) {
			// position == 0,显示我收藏的问题
			if (position == 0) {
				return new QAFragment(false,false, 3,null);
			}
			// position == 1,显示我收藏的病害
			if (position == 1) {
				return new DiseaseCollectionFragment(null,null);
			}
			// position == 1,显示我收藏的病害
			if (position == 2) {
				return new TechCollectionFragment(null,null);
			}
			return new QAFragment(false,false, 3,null);

		}

		@Override
		public int getCount() {
			return 3;
		}

	}
}
