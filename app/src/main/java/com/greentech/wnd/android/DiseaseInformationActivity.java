package com.greentech.wnd.android;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.greentech.wnd.android.bean.AgriProduct;
import com.greentech.wnd.android.constant.Constant;

public class DiseaseInformationActivity extends FragmentActivity implements OnClickListener{
	private ViewPager mViewPager;
	private PagerAdapter mPagerAdapter;
	private TabWidget mTabWidget;
	private GridView gridView;
	private Button back;
	private String[] addresses = { "蔬菜", "果品", "粮棉油" };
	private Button[] mBtnTabs = new Button[addresses.length];
	private static List<AgriProduct> mAgriProductList;// 保存所有的农作物产品
	private static List<String> data_vegeable = new ArrayList<String>();// 蔬菜类别的产品
	private static List<String> data_fruit = new ArrayList<String>();// 蔬菜类别的产品
	private static List<String> data_oil = new ArrayList<String>();// 蔬菜类别的产品
	private static List<Integer> id_vegeable = new ArrayList<Integer>();// 蔬菜类别的产品id
	private static List<Integer> id_fruit = new ArrayList<Integer>();// 蔬菜类别的产品id
	private static List<Integer> id_oil = new ArrayList<Integer>();// 蔬菜类别的产品id
	private static GridViewAdapter gridViewAdapter;
	private int[] drawables_vegeable = { R.drawable.baicai, R.drawable.bocai,
			R.drawable.caidou, R.drawable.cong, R.drawable.donggua,
			R.drawable.fanqie, R.drawable.ganlan, R.drawable.huluobo,
			R.drawable.huacai, R.drawable.huanggua, R.drawable.gangdou,
			R.drawable.jaicai, R.drawable.jiucai, R.drawable.kugua,
			R.drawable.lajiao, R.drawable.lusun, R.drawable.luobo,
			R.drawable.nangua, R.drawable.qiezi, R.drawable.qincai,
			R.drawable.sigua, R.drawable.suan, R.drawable.tianjiao,
			R.drawable.wandou, R.drawable.woju, R.drawable.xihulu,
			R.drawable.xiangcai, R.drawable.yangcong, R.drawable.tianyumi,
			R.drawable.jailan };
	private int[] drawables_fruit = { R.drawable.pingguo, R.drawable.putao,
			R.drawable.li, R.drawable.tao, R.drawable.caomei };
	private int[] drawables_oil = { R.drawable.sd, R.drawable.dm,
			R.drawable.xm, R.drawable.mh, R.drawable.mls, R.drawable.yc,
			R.drawable.dd, R.drawable.zm, R.drawable.hs, R.drawable.gl,
			R.drawable.ld, R.drawable.cd, R.drawable.qm };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diseaseinformationactivity);
		mAgriProductList = Constant.getmAgriProductList();
		setData();
		mTabWidget = (TabWidget) findViewById(R.id.tab);
		mViewPager = (ViewPager) findViewById(R.id.viewPager1);
		back=(Button) findViewById(R.id.back);
		mTabWidget.setStripEnabled(false);
		mBtnTabs[0] = new Button(this);
		mBtnTabs[0].setId(0);
		mBtnTabs[0].setFocusable(true);
		mBtnTabs[0].setText(addresses[0]);
		mBtnTabs[0].setTextColor(getResources().getColor(R.color.black));
		mBtnTabs[0].setTextSize(20);
		mBtnTabs[0].setBackgroundDrawable(getResources().getDrawable(
				R.drawable.tab_btn_hover));
		mTabWidget.addView(mBtnTabs[0]);
		/*
		 * Listener必须在mTabWidget.addView()之后再加入，用于覆盖默认的Listener，
		 * mTabWidget.addView()中默认的Listener没有NullPointer检测。
		 */
		mBtnTabs[0].setOnClickListener(mTabClickListener);
		mBtnTabs[1] = new Button(this);
		mBtnTabs[1].setId(1);
		mBtnTabs[1].setFocusable(true);
		mBtnTabs[1].setText(addresses[1]);
		mBtnTabs[1].setTextColor(getResources().getColor(R.color.black));
		mBtnTabs[1].setTextSize(20);
		mBtnTabs[1].setBackgroundDrawable(getResources().getDrawable(
				R.drawable.tab_btn_hover));
		mTabWidget.addView(mBtnTabs[1]);
		mBtnTabs[1].setOnClickListener(mTabClickListener);

		mBtnTabs[2] = new Button(this);
		mBtnTabs[2].setId(2);
		mBtnTabs[2].setFocusable(true);
		mBtnTabs[2].setText(addresses[2]);
		mBtnTabs[2].setTextSize(20);
		mBtnTabs[2].setBackgroundDrawable(getResources().getDrawable(
				R.drawable.tab_btn_hover));
		mBtnTabs[2].setTextColor(getResources().getColor(R.color.black));
		mTabWidget.addView(mBtnTabs[2]);
		mBtnTabs[2].setOnClickListener(mTabClickListener);
		mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(mPageChangeListener);

		mTabWidget.setCurrentTab(0);
		
		

	}

	private void setData() {

		for (AgriProduct pro : mAgriProductList) {
			if (pro.getType() == 1) {
				data_vegeable.add(pro.getName());
				id_vegeable.add(pro.getId());
			}
		}
		for (AgriProduct pro : mAgriProductList) {
			if (pro.getType() == 2) {
				data_fruit.add(pro.getName());
				id_fruit.add(pro.getId());
			}
		}
		for (AgriProduct pro : mAgriProductList) {
			if (pro.getType() == 3) {
				data_oil.add(pro.getName());
				id_oil.add(pro.getId());
			}
		}
	}

	private OnClickListener mTabClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == mBtnTabs[0]) {
				mViewPager.setCurrentItem(0);
			} else if (v == mBtnTabs[1]) {
				mViewPager.setCurrentItem(1);

			} else if (v == mBtnTabs[2]) {
				mViewPager.setCurrentItem(2);

			}
		}
	};
	private OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			mTabWidget.setCurrentTab(arg0);
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
			if (position == 0) {
				return new MyFragment(id_vegeable,data_vegeable, drawables_vegeable);
			}
			if (position == 1) {
				return new MyFragment(id_fruit,data_fruit, drawables_fruit);
			}
			if (position == 2) {
				return new MyFragment(id_oil,data_oil, drawables_oil);
			} else {
				return null;
			}

		}

		@Override
		public int getCount() {
			return addresses.length;
		}

	}

	private class MyFragment extends android.support.v4.app.Fragment {
		private List<Integer> ids;
		private List<String> data;
		private int[] drawables;

		public MyFragment(List<Integer> ids,List<String> data, int[] drawables) {
			this.ids=ids;
			this.data = data;
			this.drawables = drawables;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View v = inflater.inflate(R.layout.test, null, false);

			gridView = (GridView) v.findViewById(R.id.gridView);
			gridView.setSelector(getResources().getDrawable(R.drawable.grid_tab_btn_hover));
			gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent intent=new Intent(DiseaseInformationActivity.this,TitleAndImageGridActivity.class);
					intent.putExtra("id", ids.get(position));
					startActivity(intent);
				}
			});
			gridViewAdapter = new GridViewAdapter(data, drawables);
			gridView.setAdapter(gridViewAdapter);
			return v;
		}

	}

	public class GridViewAdapter extends BaseAdapter {
		private List<String> data;
		private int[] drawables;

		public GridViewAdapter() {
		}

		public GridViewAdapter(List<String> data, int[] drawables) {
			this.data = data;
			this.drawables = drawables;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView;
			ImageView imageView;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.text_test,
						null);

			}
			textView = (TextView) convertView.findViewById(R.id.TextView);
			imageView = (ImageView) convertView.findViewById(R.id.imageView);
			convertView.setTag(convertView);
			imageView.setImageDrawable(getResources().getDrawable(
					drawables[position]));
			textView.setText(data.get(position));
			return convertView;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		data_fruit.clear();
		data_oil.clear();
		data_vegeable.clear();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			DiseaseInformationActivity.this.finish();
			break;
		}
		
	}

}
