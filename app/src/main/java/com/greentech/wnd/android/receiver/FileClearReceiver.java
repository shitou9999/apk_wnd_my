package com.greentech.wnd.android.receiver;

import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.FileHelper;
import com.greentech.wnd.android.util.FileManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 当应用卸载时，会发送这个广播，但是onreceive不会执行，因为程序卸载后，进程在onreceive之前停止了
 * 
 * @author zhoufazhan
 * 
 */
public class FileClearReceiver extends BroadcastReceiver {
	String filePath = "com.greentech.wnd/files/";
	FileHelper helper = new FileHelper();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(Constant.TAG, "程序卸载了");
		if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
			Log.d(Constant.TAG, "程序卸载了");
			helper.deleteDirectory(filePath);
		}

	}

}
