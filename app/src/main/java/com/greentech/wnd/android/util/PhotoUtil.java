package com.greentech.wnd.android.util;

import java.io.File;
import java.io.IOException;

import com.greentech.wnd.android.constant.Constant;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

/**
 * 拍照或者选择相册图片的工具类
 * 
 * @author wlj TODO:可以提供一个从相册中选择多张图片的方法，思路：便利查询所有相册图片得到其路径，然后将图片显示在自己定义的layout里面
 */
public final class PhotoUtil {

	public static int REQUEST_CODE_FROM_CAMERA = 1;// 表示从相机中获取
	public static int REQUEST_CODE_FROM_ALBUM = 2;// 表示从相册中获取
	public static int FROM_CAMERA = 0;// 表示从相机中获取
	public static int FROM_ALBUM = 1;// 表示从相册中获取

	private File file = null;
	private Activity activity = null;
	private Fragment fragment = null;
	private Context context = null;

	public PhotoUtil() {
	}

	/**
	 * 从相册或者相机中获取图片
	 * 
	 * @param from
	 *            传入调用者的实例，调用者只能是Activity及其子类的实例或者Fragment及其子类的实例
	 * @param type
	 *            0表示从相机中获取，1表示从相册中获取
	 * @throws Exception
	 */
	public void startTakePhotoFromCameraOrAlbum(Object from, int type,
			int width, int height) throws Exception {
		if (from instanceof Activity) {
			activity = (Activity) from;
			context = activity;
		} else if (from instanceof Fragment) {
			fragment = (Fragment) from;
			context = fragment.getActivity();
		} else {
			throw new Exception("from参数只能为Activity或Fragment及其子类的实例");
		}
		if (type == FROM_CAMERA) {
			String state = Environment.getExternalStorageState();
			if (state.equals(Environment.MEDIA_MOUNTED)) {
				String saveDir = Environment.getExternalStorageDirectory()
						.getPath() + "/temp_image";
				File savePath = new File(saveDir);
				if (!savePath.exists()) {
					savePath.mkdirs();
				}
				file = new File(saveDir, "temp.jpg");
				file.delete();
				if (!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
						Toast.makeText(context, "照片创建失败!", Toast.LENGTH_SHORT)
								.show();
						return;
					}
				}
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
				if (activity != null) {
					activity.startActivityForResult(intent,
							REQUEST_CODE_FROM_CAMERA);
					// activity.finish();
				} else {
					fragment.startActivityForResult(intent,
							REQUEST_CODE_FROM_CAMERA);
				}
			} else {
				Toast.makeText(context, "sdcard无效或没有插入!", Toast.LENGTH_SHORT)
						.show();
			}
		} else if (type == FROM_ALBUM) {
			Intent intent = new Intent(Intent.ACTION_PICK);

			if (activity != null) {
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				activity.startActivityForResult(intent, REQUEST_CODE_FROM_ALBUM);
			} else {
				fragment.startActivityForResult(intent, REQUEST_CODE_FROM_ALBUM);
			}
		}
	}

	public void tackPhotoFromCamera(Activity from, int width, int height) {
		activity = from;
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			// 证件照的正面和背面的图片分别存储
			if (Constant.isFront) {
				file = new File(Constant.frontImagePath);
			}
			if (Constant.isBack) {
				file = new File(Constant.backImagePath);
			}else{
				file = new File(Constant.headImagePath);
			}
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			if (activity != null) {
				activity.startActivityForResult(intent,
						REQUEST_CODE_FROM_CAMERA);
			}
		} else {
			Toast.makeText(context, "sdcard无效或没有插入!", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * 获得图片路径(拍照选图)
	 * 
	 * @return
	 */
	public String getPicturePath() {
		return file.getPath();
	}

	/**
	 * 获得图片路径(相册选图)
	 * 
	 * @return
	 */
	public String getPicturePathFromUri(Uri uri) {
		Cursor cursor = context.getContentResolver().query(uri, null, null,
				null, null);
		cursor.moveToFirst();
		String imgPath = cursor.getString(1); // 图片文件路径
		cursor.close();
		return imgPath;
	}

}
