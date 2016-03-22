package com.greentech.wnd.android;

import java.io.File;

import org.apache.http.Header;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.CustomDialog;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.HeaderImageUtils;
import com.greentech.wnd.android.util.ImageUtil;
import com.greentech.wnd.android.util.UserInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SettingActivity extends BaseActivity {
	private Bitmap bitmap;
	private ImageView mFace;
	/* 头像名称 */
	private static final String PHOTO_FILE_NAME = "header.png";
	private File tempFile;
	private static final int PHOTO_REQUEST_CAMERA = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		mFace = (ImageView) findViewById(R.id.header);
		// 在内存卡里面读取头像
		if (HeaderImageUtils.getHeaderImageFromSD(this) != null) {
			mFace.setImageBitmap(HeaderImageUtils.getHeaderImageFromSD(this));
		}
		dialog = CustomDialog.createLoadingDialog(SettingActivity.this,
				"正在上传头像");
	}

	/**
	 * 更换头像按钮
	 * @param v
	 */
	public void gallery(View v) {
		// 激活系统图库，选择一张图片
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
	}

	/*
	 * 从相机获取
	 */
	public void camera(View view) {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		// 判断存储卡是否可以用，可用进行存储
		if (hasSdcard()) {
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
		}
		startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PHOTO_REQUEST_GALLERY) {
			if (data != null) {
				// 得到图片的全路径
				Uri uri = data.getData();
				crop(uri);
			}

		} else if (requestCode == PHOTO_REQUEST_CUT) {
			try {
				bitmap = data.getParcelableExtra("data");
				int l = bitmap.getByteCount();
				this.mFace.setImageBitmap(bitmap);
				// 删除保存的

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (requestCode == PHOTO_REQUEST_CAMERA) {
			if (hasSdcard()) {
				tempFile = new File(Environment.getExternalStorageDirectory(),
						PHOTO_FILE_NAME);
				crop(Uri.fromFile(tempFile));
			} else {
				Toast.makeText(this, "未找到存储卡，无法存储照片！", 0).show();
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 剪切图片
	 * 
	 * @function:
	 * @author:Jerry
	 * @date:2013-12-30
	 * @param uri
	 */
	private void crop(Uri uri) {
		// 裁剪图片意图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		// 图片格式
		intent.putExtra("outputFormat", "PNG");
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	private boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * 上传图片
	 */
	public void upload(View view) {
		try {
			if(bitmap!=null){
				dialog.show();
				RequestParams params = new RequestParams();
				params.put("imgFile", ImageUtil.Bitmap2InputStream(bitmap, 100));
				params.put("imgFileName", "temp.jpg");
				params.put("user.id", UserInfo.getUserId(this));
				final String url = Constant.SERVIER_PATH
						+ "/json/updateAndroidUser.action";
				AsyncHttpClient client = new AsyncHttpClient();
				client.post(url, params, new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						String jsonString = new String(responseBody);
						JsonObject jsonObject = (JsonObject) GsonUtil
								.parse(jsonString);
						if (jsonObject.get("status").getAsString().equals("success")
								&& HeaderImageUtils.savedIntoSD(bitmap,SettingActivity.this)) {
							dialog.dismiss();
							Toast.makeText(SettingActivity.this, "头像上传成功", 1).show();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						
						Toast.makeText(SettingActivity.this, "头像上传失败", 1).show();

					}
				});
			}else{
				Toast.makeText(SettingActivity.this, "请选择要上传的图片", 1).show();
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
