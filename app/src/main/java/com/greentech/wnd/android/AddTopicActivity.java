package com.greentech.wnd.android;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.greentech.wnd.android.bean.User;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.CustomDialog;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.ImageUtil;
import com.greentech.wnd.android.util.NetUtil;
import com.greentech.wnd.android.util.PhotoUtil;
import com.greentech.wnd.android.util.UserInfo;

/**
 * 发布提问
 * 
 * @author WP
 * 
 */
public class AddTopicActivity extends BaseActivity {

	private Button mBtn_back;
	private Button mBtn_submit;
	private Button mBtn_takeAphoto;
	private Button mBtn_pickPicture;
	// private ImageView mIv_img;
	private Dialog dialog;
	private EditText mEt_content;
	private GridView grid_view;// 显示提问上传的照片
	private GridAdapter adapter;
	private ArrayList<Bitmap> bitmap_list = new ArrayList<Bitmap>();
	private ArrayList<String> imgPath_list = new ArrayList<String>();
	private PhotoUtil pu;
	private String imgPath;
	private User selectedUser;
	private TextView mTv_selectedUser;
//	private RelativeLayout mBtn_specifyUser;
	private RelativeLayout ask_type;
	private TextView expertProduct;
	private String type="";
	public static final String BUNDLE_KEY_SELECTED_USER = "selectedUser";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置布局
		setContentView(R.layout.add_topic);
		adapter = new GridAdapter();
		// 获得调用者传入的bundle
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			selectedUser = (User) bundle.get(BUNDLE_KEY_SELECTED_USER);
		}
		grid_view = (GridView) findViewById(R.id.grid_view);
		grid_view.setAdapter(adapter);
		mBtn_submit = (Button) findViewById(R.id.btn_submit);
		mBtn_takeAphoto = (Button) findViewById(R.id.btn_takeAphoto);
		mBtn_pickPicture = (Button) findViewById(R.id.btn_pickPicture);
		ask_type = (RelativeLayout) findViewById(R.id.ask_type);
		expertProduct = (TextView) findViewById(R.id.expert_product);
		// mIv_img = (ImageView) findViewById(R.id.iv_img);
		dialog = CustomDialog.createLoadingDialog(AddTopicActivity.this,
				"数据提交中...");
		mEt_content = (EditText) findViewById(R.id.et_content);
		OnFocusChangeListener ocl = new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					((EditText) v).setBackgroundResource(R.drawable.bg_input);//
				}

			}
		};
		
		ask_type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent=new Intent(AddTopicActivity.this,
						ExpertProduct.class);
				intent.putExtra("from", "指定作物");
				startActivityForResult(intent, 4);

			}
		});
		mEt_content.setOnFocusChangeListener(ocl);
		mBtn_takeAphoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					pu = new PhotoUtil();
					pu.startTakePhotoFromCameraOrAlbum(AddTopicActivity.this,
							PhotoUtil.FROM_CAMERA, 0, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		mBtn_pickPicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					
					pu = new PhotoUtil();
					pu.startTakePhotoFromCameraOrAlbum(AddTopicActivity.this,
							PhotoUtil.FROM_ALBUM, 0, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		mBtn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String content = mEt_content.getText().toString().trim();
				final String type = expertProduct.getText().toString();
				boolean flag = true;
				if (StringUtils.isBlank(type)) {
					Toast.makeText(AddTopicActivity.this, "请输入指定类型", 1).show();
					flag = false;
				} else if (StringUtils.isBlank(content)) {
					Toast.makeText(AddTopicActivity.this, "请输入问题", 1).show();
					flag = false;
					
				}
				if (flag) {
					dialog.show();
					new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("topic.title", "");// title已经不要了
								map.put("topic.content", content);
								map.put("topic.type", type);
								map.put("topic.publisherId", UserInfo
										.getUserId(AddTopicActivity.this));
								if (selectedUser != null) {
									map.put("topic.receiverId",
											selectedUser.getId());
								}
								if (StringUtils.isNotBlank(imgPath)) {
									// Bitmap bitmap = ImageUtil
									// .compressImage(ImageUtil
									// .getAjustedBitmap(imgPath,
									// 600, 0));
									// map.put("img", ImageUtil
									// .Bitmap2InputStream(bitmap, 100));
									map.put("imgFileName", "temp.jpg");
									//
									for (int i = 0; i < bitmap_list.size(); i++) {
										map.put("img" + i,
												ImageUtil.Bitmap2InputStream(
														bitmap_list.get(i), 100));
									}
									map.put("size", bitmap_list.size());// 共有几张图片

								}
								InputStream input = NetUtil.post(
										Constant.SERVIER_PATH
												+ "/json/addTopic.action", map);
								String json = NetUtil
										.getStringFromInputStream(input);
								JsonObject jsonObj = (JsonObject) GsonUtil
										.parse(json);
								String status = jsonObj.get("status")
										.getAsString();
								if (status.equals("1")) {
									handler.post(new Runnable() {

										@Override
										public void run() {
											toastShow("信息发布成功!");
											setResult(Activity.RESULT_OK);
											finish();
										}
									});
								} else {
									handler.post(new Runnable() {

										@Override
										public void run() {
											toastShow("信息发布失败,请稍后再试!");
										}
									});
								}
							} catch (Exception ex) {
								handler.post(new Runnable() {

									@Override
									public void run() {
										toastShow("信息发布失败,请稍后再试...");
									}
								});
							} finally {
								dialog.dismiss();
							}
						}
					}).start();
				}
			}
		});

		mBtn_back = (Button) findViewById(R.id.btn_back);
		mBtn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

//		mBtn_specifyUser = (RelativeLayout) findViewById(R.id.specifyUser);
//		mBtn_specifyUser.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(AddTopicActivity.this,
//						ExpertListActivity.class);
//				intent.putExtra(ExpertListActivity.REQUEST_TYPE,
//						ExpertListActivity.Enum_RequestType.RETURN_USER_INFO);
//				intent.putExtra("type", expertProduct.getText().toString());//根据选择的类型搜索专家
//				startActivityForResult(intent, 3);
//			}
//		});
//		mTv_selectedUser = (TextView) findViewById(R.id.tv_selectedUser);
//		if (selectedUser != null) {
//			mTv_selectedUser.setText(selectedUser.getName());
//		}
	}

	/**
	 * 按键点击事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AddTopicActivity.this.finish();
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PhotoUtil.REQUEST_CODE_FROM_CAMERA
				&& resultCode == RESULT_OK) {
			imgPath = pu.getPicturePath();
			// mIv_img.setBackgroundDrawable(new BitmapDrawable(getResources(),
			// ImageUtil.compressImage(ImageUtil.getAjustedBitmap(imgPath,
			// mIv_img.getWidth(), 0))));
			// Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
			bitmap_list
					.add(new BitmapDrawable(getResources(), ImageUtil
							.compressImage(ImageUtil.getAjustedBitmap(imgPath,
									200, 0))).getBitmap());
			adapter.notifyDataSetChanged();
		} else if (requestCode == PhotoUtil.REQUEST_CODE_FROM_ALBUM
				&& resultCode == RESULT_OK) {
			Uri uri = data.getData();
			imgPath = pu.getPicturePathFromUri(uri);
			Log.e(Constant.TAG,
					new BitmapDrawable(getResources(), ImageUtil
							.compressImage(ImageUtil.getAjustedBitmap(imgPath,
									200, 0))).getBitmap().getByteCount()
							+ "");
			bitmap_list
					.add(new BitmapDrawable(getResources(), ImageUtil
							.compressImage(ImageUtil.getAjustedBitmap(imgPath,
									800, 0))).getBitmap());
			// 限制在6张图片
			if (bitmap_list.size() == 6) {
				mBtn_takeAphoto.setClickable(false);
				mBtn_pickPicture.setClickable(false);
			}
			adapter.notifyDataSetChanged();
		} else if (requestCode == 3 && resultCode == RESULT_OK) {
			// 选择专家
			Bundle bundle = data.getExtras();
			if (bundle != null) {
				selectedUser = (User) bundle
						.get(ExpertListActivity.RETURN_VALUE_SELECTED_USER);
				if (selectedUser != null) {
					mTv_selectedUser.setText(selectedUser.getName());
				}
			}

		} else if (requestCode == 4 && resultCode == RESULT_OK) {
			ArrayList<String> nameList = data.getStringArrayListExtra("name");
			StringBuilder sb = new StringBuilder();
			for (String str : nameList) {
				sb.append(str + "|");

			}
			expertProduct.setText(sb.deleteCharAt(sb.length() - 1));

		}
	}

	class GridAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return bitmap_list.size();
		}

		@Override
		public Object getItem(int position) {
			return bitmap_list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView iv = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.grid_choiced_bitmap_item, null);
				iv = (ImageView) convertView.findViewById(R.id.iv);
			} else {
				iv = (ImageView) convertView.findViewById(R.id.iv);
			}
			iv.setImageBitmap(bitmap_list.get(position));
			return convertView;
		}

	}

}
