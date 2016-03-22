package com.greentech.wnd.android.util;

import java.util.Date;

/**
 * 计算问题发布距离现在多久
 * 
 * @author zhoufazhan
 * 
 */
public class TimeUtil {
	public static String setTime(Date date) {

		Date d = new Date();// 现在的时间
		long temp = d.getTime() - date.getTime();
		long mins = temp / 1000 / 60;// 相差分钟数
		if(mins == 0){
			return "刚刚";
		}
		// 如果小于一个小时，就显示分钟
		if (mins < 60 && mins > 0) {
			return mins + "分钟前";
		}
		// 如果大于一个小时并且小于一天，就显示小时
		if (mins >= 60 && mins < (60 * 24)) {
			return mins / 60 + "小时前";
		}
		if (mins >= (60 * 24)) {
			return mins / (60 * 24) + "天前";
		}
		return null;
	}
	//获取相差的分钟数
	public static int getMinTime(Date date){
		Date d = new Date();// 现在的时间
		long temp = d.getTime() - date.getTime();
		long mins = temp / 1000 / 60;// 相差分钟数
		
		return (int)mins;
		
	}
}
