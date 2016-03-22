package com.greentech.wnd.android.util;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.greentech.wnd.android.constant.Constant;

/**
 * 获取位置信息，使用回调接口的方式来得到位置 省市县
 * 
 * 使用方法:
 * LocationUtil util=new LocationUtil(context);
 * util.setOnLocationListener(new OnLocationListener)
 * @author zhoufazhan 
 */
public class LocationUtil implements BDLocationListener {
	private OnLocationListener onLocationListener;
	public LocationClient mLocationClient = null;
	public static String province = "";// 省
	public static String city = "";// 市
	public static String district = "";// 县

	public LocationUtil(Context context) {
		mLocationClient = new LocationClient(context);
		mLocationClient.registerLocationListener(this);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps
		option.setAddrType("all");
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}

	public  void setOnLocationListener(OnLocationListener onLocationListener) {
		this.onLocationListener = onLocationListener;
	}

	public interface OnLocationListener {
		public void setLocation(String province, String city, String district);
	}

	@Override
	public void onReceiveLocation(BDLocation arg0) {
		province = arg0.getProvince();
		city = arg0.getCity();
		district = arg0.getDistrict();
		onLocationListener.setLocation(province, city, district);

	}
}
