package com.greentech.wnd.android.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

	private List<Fragment> list;
	
	public SimpleFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
		list = fm.getFragments();
	}
	
	public SimpleFragmentPagerAdapter(FragmentManager fm, List<?> list) {
		super(fm);
		this.list = (List<Fragment>)list;
	}

	@Override
	public Fragment getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public int getCount() {
		return list.size();
	}

}
