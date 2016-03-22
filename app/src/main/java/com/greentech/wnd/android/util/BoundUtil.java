package com.greentech.wnd.android.util;

import org.apache.http.Header;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.greentech.wnd.android.ApplyExpertActivity;
import com.greentech.wnd.android.constant.Constant;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 积分操作
 * 
 * @author zhoufazhan
 * 
 */
public class BoundUtil {
	// 增加当前用户的积分,增加以后返回bonus的值,保存在UserInfo当中 用来显示
	public static boolean addBonus(final int bonus, final Context context) {

		RequestParams params = new RequestParams();
		params.put("user.id", UserInfo.getUserId(context));// 用户id
		params.put("user.bonus", bonus);
		final String url = Constant.SERVIER_PATH + "/json/addBonus.action";
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				String string = new String(arg2);
				JsonObject json = (JsonObject) GsonUtil.parse(string);
				int i = json.get("bonus").getAsInt();
				UserInfo.setUserBonus(context, i);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
//				String string = new String(arg2);
				Toast.makeText(context, "提交失败", 1).show();

			}
		});

		return false;
	}
	// 增加当前用户的积分,增加以后返回bonus的值,保存在UserInfo当中 用来显示
	public static boolean addOtherBonus(final int bonus,int userId, final Context context) {
		
		RequestParams params = new RequestParams();
		params.put("user.id",userId);// 用户id
		params.put("user.bonus", bonus);
		final String url = Constant.SERVIER_PATH + "/json/addBonus.action";
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(url, params, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
//				String string = new String(arg2);
//				JsonObject json = (JsonObject) GsonUtil.parse(string);
//				int i = json.get("bonus").getAsInt();
//				UserInfo.setUserBonus(context, i);
				Toast.makeText(context, "提交成功", 1).show();
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
//				String string = new String(arg2);
				Toast.makeText(context, "提交失败", 1).show();
				
			}
		});
		
		return false;
	}

	// 获得当前用户的积分
	public static int getBonus(final Context context) {
		int bonus = 0;
		RequestParams params = new RequestParams();
		params.put("user.id", UserInfo.getUserId(context));// 用户id
		final String url = Constant.SERVIER_PATH + "/json/getBonus.action";
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				String string = new String(arg2);
				JsonObject json = (JsonObject) GsonUtil.parse(string);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				Toast.makeText(context, "提交失败", 1).show();

			}
		});

		return bonus;
	}
}
