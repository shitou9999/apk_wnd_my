package com.greentech.wnd.android.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.greentech.wnd.android.R;

public class DrawableUtil {
	private static List<Drawable> list_drawable = new ArrayList<Drawable>();
	private static Drawable drawable;
	private HttpClient client = new DefaultHttpClient();

	public static List<Drawable> getListDrawableFromUrls(
			final List<String> list_string, final Context context) {
		for (int i = 0; i < list_string.size(); i++) {
			if (getDrawableFromUrl(list_string.get(i)) != null) {
				list_drawable.add(getDrawableFromUrl(list_string.get(i)));
			} else {
				list_drawable.add(context.getResources().getDrawable(
						R.drawable.about));
			}

		}

		return list_drawable;
	}

	public static Drawable getDrawableFromUrl(String path) {
		InputStream is=null;
		try {
			URL url=new URL(path);
			
			is = url.openStream();
			
			drawable = Drawable.createFromStream(is, "");
			
			
			return drawable;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

}
