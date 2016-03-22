package com.greentech.wnd.android.cache;

import java.io.File;

import android.content.Context;
import android.util.Log;

import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.FileHelper;



public abstract class AbstractFileCache {

	private String dirString;
	
	public AbstractFileCache(Context context) {
		
		dirString = getCacheDir();
		boolean ret = FileHelper.createDirectory(dirString);
	}
	
	public File getFile(String url) {
		File f = new File(getSavePath(url));
		return f;
	}
	
	public abstract String  getSavePath(String url);
	public abstract String  getCacheDir();

	public void clear() {
		FileHelper.deleteDirectory(dirString);
	}

}
