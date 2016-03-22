package com.greentech.wnd.android;

import java.io.File;
import java.io.InputStream;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.CustomDialog;
import com.greentech.wnd.android.util.HeaderImageUtils;
import com.greentech.wnd.android.util.NetUtil;
import com.greentech.wnd.android.util.UserInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class LoginActivity extends Activity {
	protected Handler handler = new Handler();
	// 用户名--使用电话号码登录
	private EditText user_edit;
	// 用户密码
	private EditText userPwd;
	private String password;
	// 登陆按钮
	private Button loginButton;
	private Button registerButton;
	private Button cancel;
	private Toast toast;
	private ProgressDialog progressDialog;
	public String state;// 登录是否成功状态
	public String realName;// 真实姓名
	public String userName;//昵称
	public String tel;// 用来登录，注册
	public int expert;
	public int normaluser;
	public int govdept;
	public int bounds;
	public String yqm;
	public String yqmOther;//别人的邀请码
	public String imagePath;//头像图片的路径
	public Bitmap bitmap;
	private static String Url = "http://106.ihuyi.cn/webservice/sms.php?method=Submit";
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		user_edit = (EditText) findViewById(R.id.text_username);
		userPwd = (EditText) findViewById(R.id.text_password);
		loginButton = (Button) findViewById(R.id.login_button);
		registerButton = (Button) findViewById(R.id.register_btn);
		cancel = (Button) findViewById(R.id.cancel);

		// 将SharedPreferences中保存的用户信息显示在用户登录的输入框里
		showSavedUserName();
		// 取消按钮
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LoginActivity.this.finish();
			}
		});
		
		// 注册按钮
		registerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
				finish();
			}
		});
		// 登陆按钮
		loginButton.setOnClickListener(new LoginBtnSubOnClickListener());
	}

	/**
	 * 将已经登录的用户名显示在edittext当中
	 */
	public void showSavedUserName() {
		SharedPreferences sharedPre = this.getSharedPreferences("config", 1);
		String savedUserName = sharedPre.getString("username", "");
		String savedPassword = sharedPre.getString("password", "");
		if (savedUserName.length() > 0 && savedPassword.length() > 0) {
			user_edit.setText(savedUserName);
			userPwd.setText(savedPassword);

		}
	}

	public void toastShow(String info) {
		toast = Toast.makeText(getApplicationContext(), info,
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	class LoginBtnSubOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (user_edit.getText().toString().equals("")) {
				toastShow("用户名不能为空!");
				return;
			}
			if (userPwd.getText().toString().equals("")) {
				toastShow("登录密码不能为空！");
				return;
			}
			toLogin();
		}

	}

	public void toLogin() {
		final Dialog dialog=CustomDialog.createLoadingDialog(LoginActivity.this,"正在登录...");
		dialog.show();
		tel = user_edit.getText().toString();
		password = userPwd.getText().toString();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					InputStream is = NetUtil.post(Constant.SERVIER_PATH
							+ "/json/userLogin.action", "tel=" + tel
							+ "&password=" + password);
					String str = NetUtil.getStringFromInputStream(is);
					JSONObject json = new JSONObject(str);
					if (json.getString("state").equals("-1")) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								dialog.dismiss();
								toastShow("登录失败,用户名或密码错误!");
							}
						});
					} else if (json.getString("state").equals("1")) {
						// 下载头像图片,判断一下内存卡里面有没有图片
						File file = new File(getFilesDir() + "/header");
						if (file.list().length > 0) {
							HeaderImageUtils.deleteHeader(getFilesDir().getAbsolutePath() + "/header");
							
						}
						HeaderImageUtils.downLoadHeaderImage(Constant.HOST
								+ json.getString("img"), LoginActivity.this);
						handler.post(new Runnable() {
							@Override
							public void run() {
								dialog.dismiss();
							}
						});
						expert = json.getInt("expert");
						normaluser = json.getInt("normaluser");
						govdept = json.getInt("govdept");
						tel = json.getString("tel");
						yqm = json.getString("yqm");
						yqmOther = json.getString("yqmOther");
						bounds = json.getInt("bounds");
						String name = json.getString("name");//昵称
						int userId = json.getInt("userId");
						// 判断用户类型
						String userType = "";
						if (expert == 1) {
							userType = "expert";
						}if (normaluser == 1) {
							userType = "normaluser";
							userName = "问农答网友";
							if(name.trim().length()!=0){
								userName=name;	
							}
						}if (govdept == 1) {
							userType = "govdept";
						}
						// 将登陆的用户信息保存到sharedprefence中
						UserInfo.saveLoginInfo(LoginActivity.this, userName,
								password, userType, userId, tel, yqm, bounds,yqmOther);
//						HeaderImageUtils.deleteHeader(LoginActivity.this.getFilesDir().getAbsolutePath() + "/header");
						// 结束当前activity,回到之前的界面
						LoginActivity.this.finish();
					}

				} catch (Exception e) {
					Toast.makeText(LoginActivity.this, "登录出现异常", 1);
				}
			}

		}).start();

	}

}
