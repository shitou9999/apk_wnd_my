package com.greentech.wnd.android.constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.greentech.wnd.android.bean.AgriProduct;

import android.graphics.Bitmap;
import android.os.Environment;

/**
 * 常量
 * 
 * @author wudc
 * 
 */
public class Constant {

	// 是否debug
	public static boolean DEBUG = true;
	// public static final String HOST = "http://gs.agri114.cn";
	// public static final String HOST = "http://192.168.71.15:8080";
	public static final String HOST = "http://120.55.192.216";
	public static final String SERVIER_PATH = HOST + "/wndms";

	// 应用常驻内存的变量
	public static Map<String, Object> DATA_APP = new HashMap<String, Object>();
	public static final String pageSize = "15";
	public static Bitmap bitmap;
	public static Bitmap front_bitmap;// 保存正面证件照
	public static Bitmap back_bitmap;// 保存背面证件照
	public static Bitmap head_bitmap;// 保存背面证件照
	public static boolean isFront;// 判断是否是证件照的正面
	public static boolean isBack;// 判断是否是证件照的背面
	public static String frontImagePath = Environment
			.getExternalStorageDirectory().getPath() + "/temp_image/front.jpg";
	public static String backImagePath = Environment
			.getExternalStorageDirectory().getPath() + "/temp_image/back.jpg";
	public static String headImagePath = Environment
			.getExternalStorageDirectory().getPath() + "/temp_image/head.jpg";
	public static int idPhotoWidth;
	public static String province = "";
	// 存放头像的路径
	public static String filePath = "data/data/com.greentech.wnd.android/files/header";
	// 分隔符
	public static final String BREAK = ";";
	public static List<AgriProduct> mAgriProductList;//保存所有的农作物产品
	// TAG
	public static final String TAG = "zhoufazhan";

	public static List<AgriProduct> getmAgriProductList() {
		return mAgriProductList;
	}

	public static void setmAgriProductList(List<AgriProduct> mAgriProductList) {
		Constant.mAgriProductList = mAgriProductList;
	}

	public static Bitmap getBitmap() {
		return bitmap;
	}

	public static void setBitmap(Bitmap bitmap) {
		Constant.bitmap = bitmap;
	}
}
