package com.greentech.wnd.android;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.greentech.wnd.android.bean.AgriProduct;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.HeaderImageUtils;
import com.greentech.wnd.android.util.LocationUtil;
import com.greentech.wnd.android.util.LocationUtil.OnLocationListener;
import com.greentech.wnd.android.util.NetUtil;
import com.greentech.wnd.android.util.UserInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 注册页面 获取短信验证码，并且自动填写
 * 
 * @author test
 * 
 */
public class RegisterActivity extends BaseActivity {
	public List<AgriProduct> agriProducts;
	private TextView password;// 密码
	private TextView tel;// 电话
	String tel_str = "dsf";
	String passWord = "dsf";
	private Button registerBtn;
	private Button registerCancleBtn;
	public int productId;
	public int isNormalUserValue;
	public int isExpertValue;
	public int isGovDeptValue;
	private Toast toast;
	public String userType_Str;
	public BroadcastReceiver receiver;
	private Handler handler;
	private IntentFilter filter;
	private String patternCoder = "(?<!\\d)\\d{4}(?!\\d)";//短信过滤
	private String strContent;
	String userName;
	private EditText text_yzm;// 验证码内容
	private Button yzm_btn;// 验证码按钮
	public int yzm_int;
	private static String Url = "http://106.ihuyi.cn/webservice/sms.php?method=Submit";
	// 多选
	PopupWindow window;
	Button button;
	public String[] agriTypes = new String[50];
	private BaseAdapter mAdapter;
	private List<Map<String, String>> items = new ArrayList<Map<String, String>>();
	int i = 60;//倒计时的整个时间数
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.release);
		init();// 绑定控件
		handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				text_yzm.setText(strContent);
			}
		};
		registerBtn.setOnClickListener(new SubButtonOnclickListener());
		registerCancleBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});
		yzm_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tel.getText().toString().trim().equals("")) {
					toastShow("请输入电话号码!");
					return;
				}
				yzm_int = (int) (Math.random() * 9000 + 1000);
				AsyncHttpClient client = new AsyncHttpClient();
				RequestParams params = new RequestParams();
				params.put("method", "Submit");
				params.put("account", "cf_wnd");
				params.put("password", "wnd88888888");
				params.put("mobile", tel.getText().toString().trim());
				params.put("content", "您的验证码是：" + yzm_int + "。请不要把验证码泄露给其他人。");
				client.post(Url, params, new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						String str = new String(arg2);
						try {
							Document doc = DocumentHelper.parseText(str);
							Element root = doc.getRootElement();

							String code = root.elementText("code");
							String msg = root.elementText("msg");
							String smsid = root.elementText("smsid");

						} catch (DocumentException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						String str = new String(arg2);
						Log.i("zhoufazhan", str);

					}
				});
				
				//开始倒计时60秒
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						while(i>0){
							i--;
							//将按钮设置为不可点击，防止一直点击，这样耗钱啊，哈哈哈
							yzm_btn.setClickable(false);
							//设置倒计时时间
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									yzm_btn.setText(""+i);
									
								}
							});
							try {
								Thread.sleep(1000);
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
						//倒计时结束后
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								yzm_btn.setText("获取验证码");
								yzm_btn.setClickable(true);
							}
						});
						i=60;//修改倒计时剩余时间变量为60秒
						
					}
				}).start();
				
				
				
				
			}
		});
		filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
		receiver=new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				  Object[] objs = (Object[]) intent.getExtras().get("pdus");
	                for (Object obj : objs) {
	                    byte[] pdu = (byte[]) obj;
	                    SmsMessage sms = SmsMessage.createFromPdu(pdu);
	                    // 短信的内容
	                    String message = sms.getMessageBody();
	                    // 短息的手机号
	                    String from = sms.getOriginatingAddress();
	                    if (!TextUtils.isEmpty(from)) {
	                        String code = patternCode(message);
	                        if (!TextUtils.isEmpty(code)) {
	                            strContent = code;
	                            handler.sendEmptyMessage(1);
	                        }
	                    }
	                }
				
			}
			
		};
		registerReceiver(receiver, filter);
		
		
		
	}
	 /**
     * 匹配短信中间的4个数字（验证码等）
     *
     * @param patternContent
     * @return
     */
    private String patternCode(String patternContent) {
        if (TextUtils.isEmpty(patternContent)) {
            return null;
        }
        Pattern p = Pattern.compile(patternCoder);
        Matcher matcher = p.matcher(patternContent);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

	private void init() {
		password = (TextView) this.findViewById(R.id.release_pwd);
		tel = (TextView) this.findViewById(R.id.release_tel);
		registerBtn = (Button) this.findViewById(R.id.register_button);
		registerCancleBtn = (Button) this.findViewById(R.id.register_cancel);
		text_yzm =(EditText)findViewById(R.id.text_yzm);
		yzm_btn = (Button) findViewById(R.id.yzm);

	}

	// 注册提交按钮
	class SubButtonOnclickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (!text_yzm.getText().toString().equals(yzm_int + "")) {
				Toast.makeText(getApplicationContext(), "验证码错误，请重新填写", 1)
						.show();
				return;
			}

			if (tel.getText().toString().trim().equals("")) {
				toastShow("请输入电话号码!");
				return;
			}
			if (password.getText().toString().trim().equals("")) {
				toastShow("请输入密码!");
				return;
			}
			
			
			subRegisterInfo();
		}

	}

	public void subRegisterInfo() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		final String time = sdf.format(new Date());
		final StringBuffer sb = new StringBuffer();
		// 获取用户当前gps定位的地址
		LocationUtil location = new LocationUtil(RegisterActivity.this);
		location.setOnLocationListener(new OnLocationListener() {

			@Override
			public void setLocation(String province, String city,
					String district) {
				sb.append("&province=" + province);
				sb.append("&city1=" + city);
				sb.append("&district=" + district);
				new Thread(new Runnable() {
					@Override
					public void run() {
						sb.append("&password="
								+ password.getText().toString().trim());
						sb.append("&tel=" + tel.getText().toString().trim());
						sb.append("&agriProductIds=" + productId);
						sb.append("&provinceId=" + 1);
						sb.append("&cityId=" + 1);
						sb.append("&districtId=" + 1);
						sb.append("&registerTime=" + time);
						sb.append("&bounds=" + 100);// 手机注册成功加100积分
						sb.append("&yqm=" + getRandomString(5));
						// userType_Str =
						// userTypeStr.getText().toString().trim();
						// 默认全都拥有普通用户的权限
						isNormalUserValue = 1;
						sb.append("&isNormalUserValue=" + isNormalUserValue);
						// sb.append("&isExpertValue=" + isExpertValue);
						// sb.append("&isGovDeptValue=" + isGovDeptValue);
						InputStream input = NetUtil.post(Constant.SERVIER_PATH
								+ "/json/registerUser.action", sb.toString());
						String str = NetUtil.getStringFromInputStream(input);
						try {
							JsonObject jsonObj = (JsonObject) GsonUtil
									.parse(str);
							String state = jsonObj.get("state").getAsString();
							if (state.equals("-1")) {
								handler.post(new Runnable() {
									@Override
									public void run() {
										toastShow("用户已存在,请重新输入!");
									}
								});
							} else {
								handler.post(new Runnable() {
									@Override
									public void run() {
										toastShow("用户注册成功!");
										tel_str=tel.getText().toString().trim();
										passWord=password.getText().toString().trim();
										toLogin(tel_str,passWord);
									}
								});
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();

			}
		});

	}
	public void toLogin(final String tel_str,final String passWord) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					InputStream is = NetUtil.post(Constant.SERVIER_PATH
							+ "/json/userLogin.action", "tel=" + tel_str
							+ "&password=" + passWord);
					String str = NetUtil.getStringFromInputStream(is);
					JSONObject json = new JSONObject(str);
					if (json.getString("state").equals("-1")) {
						handler.post(new Runnable() {
							@Override
							public void run() {
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
								+ json.getString("img"), RegisterActivity.this);
						handler.post(new Runnable() {
							@Override
							public void run() {
							}
						});
						int expert = json.getInt("expert");
						int normaluser = json.getInt("normaluser");
						int govdept = json.getInt("govdept");
						
						String yqm = json.getString("yqm");
						String yqmOther = json.getString("yqmOther");
						int bounds = json.getInt("bounds");
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
						UserInfo.saveLoginInfo(RegisterActivity.this, userName,
								passWord, userType, userId, tel_str, yqm, bounds,yqmOther);
//						HeaderImageUtils.deleteHeader(LoginActivity.this.getFilesDir().getAbsolutePath() + "/header");
						// 结束当前activity,回到之前的界面
						Intent intent = new Intent();
						intent.setClass(RegisterActivity.this,
								DataModificationActivity.class);
						startActivity(intent);
						RegisterActivity.this.finish();
					}

				} catch (Exception e) {
					Toast.makeText(RegisterActivity.this, "登录出现异常", 1);
				}
			}

		}).start();

	}
	public void toastShow(String info) {
		toast = Toast.makeText(getApplicationContext(), info,
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	/*
	 * 获得产品类别
	 */

	public void getAgriProducts() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					List<Map<String, String>> infolist = new ArrayList<Map<String, String>>();
					InputStream input = NetUtil.post(Constant.SERVIER_PATH
							+ "/json/loadProductType.action");
					String json = NetUtil.getStringFromInputStream(input);
					JsonArray jsonObj = (JsonArray) GsonUtil.parse(json);
					agriProducts = GsonUtil.fromJson(jsonObj.toString(),
							new TypeToken<List<AgriProduct>>() {
							}.getType());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	// 生成随机数
	public static String getRandomString(int length) { // length表示生成字符串的长度
		String base = "ABCDEFGHIJKLMNPQRSTUVWXYZ123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}
	@Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
