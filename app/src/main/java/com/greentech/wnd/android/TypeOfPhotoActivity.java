package com.greentech.wnd.android;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.ImageUtil;
import com.greentech.wnd.android.util.PhotoUtil;

public class TypeOfPhotoActivity extends Activity {
	private Button camera_btn;// 相机按钮
	private Button alum_btn;// 相册按钮
	private Button cancel_btn;
	private PhotoUtil pu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_type);
		camera_btn = (Button) findViewById(R.id.camera_btn);
		alum_btn = (Button) findViewById(R.id.alum_btn);
		cancel_btn = (Button) findViewById(R.id.cancel_btn);
		pu = new PhotoUtil();
		camera_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					pu.tackPhotoFromCamera(TypeOfPhotoActivity.this, 0, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		alum_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					pu.startTakePhotoFromCameraOrAlbum(
							TypeOfPhotoActivity.this, PhotoUtil.FROM_ALBUM, 0,
							0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		cancel_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TypeOfPhotoActivity.this.finish();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		int idPhotoWidth = getWindowManager().getDefaultDisplay().getWidth() / 2 - 10;
		if (requestCode == PhotoUtil.REQUEST_CODE_FROM_CAMERA
				&& resultCode == RESULT_OK) {
			if (Constant.isFront) {
				Constant.front_bitmap = ImageUtil.compressImage(ImageUtil
						.getAjustedBitmap(Constant.frontImagePath,
								idPhotoWidth, 0));
				TypeOfPhotoActivity.this.finish();
			}
			if (Constant.isBack) {
				Constant.back_bitmap = ImageUtil.compressImage(ImageUtil
						.getAjustedBitmap(Constant.backImagePath, 200, 0));
				TypeOfPhotoActivity.this.finish();
			} else {
				Constant.head_bitmap = ImageUtil.compressImage(ImageUtil
						.getAjustedBitmap(Constant.headImagePath, 200, 0));
				TypeOfPhotoActivity.this.finish();
			}
		}
		if (requestCode == PhotoUtil.REQUEST_CODE_FROM_ALBUM
				&& resultCode == RESULT_OK) {
			Uri uri = data.getData();
			String imagePath = getPicturePathFromUri(uri);
			Bitmap bt = ImageUtil.compressImage(ImageUtil.getAjustedBitmap(
					imagePath, idPhotoWidth, 0));
			if (Constant.isFront) {
				Constant.front_bitmap = bt;
				TypeOfPhotoActivity.this.finish();
			} if (Constant.isBack) {
				Constant.back_bitmap = bt;
				TypeOfPhotoActivity.this.finish();
			}else{
				Constant.head_bitmap = bt;
				TypeOfPhotoActivity.this.finish();
			}

		}
	}

	/**
	 * 获得图片路径(相册选图)
	 * 
	 * @return
	 */
	public String getPicturePathFromUri(Uri uri) {
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		cursor.moveToFirst();
		String imgPath = cursor.getString(1); // 图片文件路径
		cursor.close();
		return imgPath;
	}
}
