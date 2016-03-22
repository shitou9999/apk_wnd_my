package com.greentech.wnd.android;

import java.util.ArrayList;

import org.apache.http.Header;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

public class DataModificationActivity extends BaseActivity {

	private Button back;
	private Button submit;
	private ImageButton head;
	private EditText username;
	private EditText password;
	private EditText tel;
	private Dialog dialog;
	private TextView expertProduct;// 专家选择的擅长农作物
	private RelativeLayout agriproduct;// 擅长的作物

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.datamodification);
		back = (Button) findViewById(R.id.back);
		submit = (Button) findViewById(R.id.submit);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		head = (ImageButton) findViewById(R.id.head);
		tel = (EditText) findViewById(R.id.tel);
		agriproduct = (RelativeLayout) findViewById(R.id.agriproduct);
		expertProduct = (TextView) findViewById(R.id.expert_product);

		dialog = CustomDialog.createLoadingDialog(this, "正在上传");
		showSavedUserName();
		// 在内存卡里面读取头像

		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DataModificationActivity.this.finish();
			}
		});
		// 选择关注的作物
		agriproduct.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(
						DataModificationActivity.this, ExpertProduct.class), 2);
			}
		});
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.show();
				RequestParams params = new RequestParams();
				if (Constant.head_bitmap != null) {
					params.put("imgFile", ImageUtil.Bitmap2InputStream(
							Constant.head_bitmap, 100));
					params.put("imgFileName", "head.jpg");
					HeaderImageUtils.savedIntoSD(Constant.head_bitmap,
							DataModificationActivity.this);
				}

				params.put("name", username.getText().toString());
				params.put("password", password.getText().toString());
				params.put("agriGz", expertProduct.getText().toString());
				params.put("user.id",
						UserInfo.getUserId(DataModificationActivity.this));
				final String url = Constant.SERVIER_PATH
						+ "/json/updateAndroidUser.action";
				AsyncHttpClient client = new AsyncHttpClient();
				client.post(url, params, new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						dialog.dismiss();
						String jsonString = new String(responseBody);
						JsonObject jsonObject = (JsonObject) GsonUtil
								.parse(jsonString);
						if (jsonObject.get("status").getAsString()
								.equals("success")) {

							UserInfo.setName(DataModificationActivity.this,
									username.getText().toString());
							Toast.makeText(DataModificationActivity.this,
									"修改成功", 1).show();
							DataModificationActivity.this.finish();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						dialog.dismiss();
						Toast.makeText(DataModificationActivity.this, "修改失败", 1)
								.show();

					}
				});
			}

		});
		head.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(
						DataModificationActivity.this,
						TypeOfPhotoActivity.class), 1);

			}
		});
	}

	/**
	 * 将已经登录的用户名显示在edittext当中
	 */
	public void showSavedUserName() {
		SharedPreferences sharedPre = this.getSharedPreferences("config", 1);
		String savedPassword = sharedPre.getString("password", "");
		String tel_text = sharedPre.getString("tel", "");
		if (savedPassword.length() > 0) {
			password.setText(savedPassword);
			tel.setText(tel_text);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(Constant.head_bitmap!=null){
			head.setImageBitmap(Constant.head_bitmap);
		}else{
			head.setImageDrawable(getResources().getDrawable(R.drawable.header_gray));
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 擅长的作物
		if (requestCode == 2 && resultCode == RESULT_OK) {
			ArrayList<String> nameList = data.getStringArrayListExtra("name");
			StringBuilder sb = new StringBuilder();
			for (String str : nameList) {
				sb.append(str + "|");

			}
			expertProduct.setText(sb.deleteCharAt(sb.length() - 1));
		}
	}
}
