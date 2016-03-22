package com.greentech.wnd.android.util;

import android.app.Activity;


public class FileManager{

	public static String getSaveFilePath() {
		if (CommonUtil.hasSDCard()) {
			//这个路径是系统内存路径，当程序卸载时，此文件夹里面的内容也全部被删除
			return "data/data/com.greentech.wnd.android/files/";
		} else {
			return CommonUtil.getRootFilePath() + "com.greentech.wnd/files/";
		}
	}
}
