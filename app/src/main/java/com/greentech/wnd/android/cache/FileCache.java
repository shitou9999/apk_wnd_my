package com.greentech.wnd.android.cache;

import android.content.Context;
import android.util.Log;

import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.FileManager;

public class FileCache extends AbstractFileCache{

	public FileCache(Context context) {
		super(context);
	
	}


	@Override
	public String getSavePath(String url) {
		String filename = String.valueOf(url.hashCode());
		return getCacheDir() + filename;
	}

	@Override
	public String getCacheDir() {
		return FileManager.getSaveFilePath();
		
		
	}

}
