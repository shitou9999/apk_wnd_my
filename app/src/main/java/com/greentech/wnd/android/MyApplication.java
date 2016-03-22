/**
 * 
 */
package com.greentech.wnd.android;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.greentech.wnd.android.handler.CrashHandler;

/**
 * 自定义Application类取代默认的Application
 * 处理以下事情：
 * 1、程序崩溃时，记录崩溃日志，写入文件中（或者有网络时，发送到服务器，以便我们分析哪种手机出错以及出错信息）；
 * 
 * @author wlj
 *
 */
public class MyApplication extends Application {

	/**
	 * 
	 */
	public MyApplication() {
		super();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		SDKInitializer.initialize(getApplicationContext());//百度地图初始化
	}

	
}
