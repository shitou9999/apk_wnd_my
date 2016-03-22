package com.greentech.wnd.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserInfo {
	/**
	 * 使用SharedPreferences保存用户登录信息
	 * 
	 * @param context
	 * @param username
	 * @param password
	 */
	public static void saveLoginInfo(Context context, String username,
			String password, String userType, int userId, String tel,
			String yqm, int bounds, String yqmOther) {
		// 获取SharedPreferences对象
		SharedPreferences sharedPre = context.getSharedPreferences("config", 1);
		// 获取Editor对象
		Editor editor = sharedPre.edit();

		// 设置参数
		editor.putString("username", username);
		editor.putString("password", password);
		editor.putString("userType", userType);
		editor.putString("tel", tel);
		editor.putString("yqm", yqm);// 自己邀请码
		editor.putString("yqmOther", yqmOther);// 别人的邀请码
		editor.putInt("userId", userId);
		editor.putInt("bounds", bounds);

		// 提交
		editor.commit();
	}

	// 删除所有信息
	public static boolean delete(Context context) {
		// 获取SharedPreferences对象
		SharedPreferences sharedPre = context.getSharedPreferences("config", 1);
		// 获取Editor对象
		Editor editor = sharedPre.edit();
		editor.clear();
		return editor.commit();
	}

	/**
	 * 判断是否登录
	 * 
	 * @param context
	 * @return
	 */

	public static boolean isLogin(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences("config", 1);
		String uesename = sharedPre.getString("username", null);
		if (uesename == null) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 查询用户类型
	 * 
	 * @param context
	 * @return
	 */
	public static String getUserType(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences("config", 1);
		return sharedPre.getString("userType", null);
	}

	// 更改用户积分
	public static void setUserBonus(Context context, int bonus) {
		SharedPreferences sharedPre = context.getSharedPreferences("config", 1);
		Editor editor = sharedPre.edit();
		editor.putInt("bounds", bonus);
		editor.commit();
	}

	// 设置朋友验证码
	public static void setFriendYQM(Context context, String yqm) {
		SharedPreferences sharedPre = context.getSharedPreferences("config", 1);
		Editor editor = sharedPre.edit();
		editor.putString("yqmOther", yqm);
		editor.commit();
	}

	public static String getFriendYQM(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences("config", 1);
		return sharedPre.getString("yqmOther", null);
	}
	public static String getYQM(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences("config", 1);
		return sharedPre.getString("yqm", null);
	}
	public static int getBounds(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences("config", 1);
		return sharedPre.getInt("bounds", 0);
	}

	// 设置用户名
	public static void setName(Context context, String name) {
		SharedPreferences sharedPre = context.getSharedPreferences("config", 1);
		Editor editor = sharedPre.edit();
		editor.putString("username", name);
		editor.commit();
	}

	/**
	 * 查询用户Id
	 * 
	 * @param context
	 * @return
	 */
	public static int getUserId(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences("config", 1);
		return sharedPre.getInt("userId", 1);

	}

	/**
	 * 查询用户电话
	 * 
	 * @param context
	 * @return
	 */
	public static String getTel(Context context) {
		SharedPreferences sharedPre = context.getSharedPreferences("config", 1);
		return sharedPre.getString("tel", "1");

	}

}
