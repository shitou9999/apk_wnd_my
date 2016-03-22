package com.greentech.wnd.android.observer;

import java.util.Observable;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 观察者模式 
 * 此类是一个被观察者，当调用deleteShared方法时就会通知所有的观察者来实现数据的更改，这里只有一个观察者LoginFragment
 * 作用：当SharedPreferences的数据被清空时通知LoginFragment将头像下面的用户名改为     “请点击头像登录”
 */
public class SharedPreferenceObserveable extends Observable {

	private SharedPreferences shared;
	private SharedPreferences.Editor editor;

	public void deleteShared(Context context) {
		shared = context.getSharedPreferences("config", 1);
		editor = shared.edit();
		editor.clear();
		editor.commit();
		setChanged();
		
	}
}
