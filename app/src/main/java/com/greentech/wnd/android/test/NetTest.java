package com.greentech.wnd.android.test;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.test.AndroidTestCase;

import com.google.gson.JsonObject;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.NetUtil;

public class NetTest extends AndroidTestCase {
	public void testNet() {
		{
			InputStream is = NetUtil.post(Constant.SERVIER_PATH
					+ "/json/findDiseaseFeatures.action", "agriProductId=" + 0);
			String str = NetUtil.getStringFromInputStream(is);
			JsonObject json = (JsonObject) GsonUtil.parse(str);

		}
	}
	HttpClient client=new DefaultHttpClient();
	
	public void myTest(){
		HttpPost post=new HttpPost(Constant.SERVIER_PATH+"index.jsp");
		try {
			HttpResponse response=client.execute(post);
		int code=response.getStatusLine().getStatusCode();
		System.out.println(code);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
