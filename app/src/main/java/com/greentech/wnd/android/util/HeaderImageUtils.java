package com.greentech.wnd.android.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.greentech.wnd.android.constant.Constant;

/**
 * 下载头像图片
 * 
 * @author zhoufazhan
 * 
 */
public class HeaderImageUtils {
	static Bitmap bitmap;

	/**
	 * 下载头像
	 * 
	 * @param url
	 */
	public static void downLoadHeaderImage(String url,Context context) {
		try {
			if (url.length() > 0 && url != null) {
				URL imageUrl = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) imageUrl
						.openConnection();
				conn.setConnectTimeout(30000);
				conn.setReadTimeout(30000);
				conn.setInstanceFollowRedirects(true);
				InputStream is = conn.getInputStream();
				bitmap = BitmapFactory.decodeStream(is);
				Constant.setBitmap(bitmap);
				savedIntoSD(bitmap,context);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Constant.TAG, "下载头像出现异常");
		}
	}

	/**
	 * 将bitmap保存到sd卡
	 * 
	 * @param bitmap
	 * @return
	 */
	public static boolean savedIntoSD(Bitmap bitmap,Context context) {
		File file1 = new File(context.getFilesDir().getAbsolutePath() + "/header");
		if (!file1.exists()) {
			file1.mkdirs();
		}
		File file = new File(file1, "header.png");
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 在sd卡中取bitmap
	 * 
	 * @return
	 */
	public static Bitmap getHeaderImageFromSD(Context context) {
		// 判断一下内存卡里面有没有图片
		File file = new File(context.getFilesDir().getAbsolutePath() + "/header");
		if (!file.exists()) {
			file.mkdirs();
		}
		if (file.list().length != 0) {
			bitmap = BitmapFactory.decodeFile(context.getFilesDir().getAbsoluteFile()
					+ "/header/header.png");

			return bitmap;

		}
		return null;
	}

	public static boolean deleteHeader(String filePath) {

		File dir = new File(filePath);
		if (dir.exists()) {
			File[] tmp = dir.listFiles();
			for (int i = 0; i < tmp.length; i++) {
				if (tmp[i].isDirectory()) {
					deleteHeader(filePath + "/" + tmp[i].getName());
				} else {
					tmp[i].delete();
				}
			}
			// dir.delete();
		}

		return false;
	}

}
