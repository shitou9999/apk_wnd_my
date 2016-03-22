package com.greentech.wnd.android;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.greentech.wnd.android.bean.AgriProd;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.BoundUtil;
import com.greentech.wnd.android.util.CommonUtil;
import com.greentech.wnd.android.util.FileHelper;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.ImageUtil;
import com.greentech.wnd.android.util.LocationUtil;
import com.greentech.wnd.android.util.UserInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 申请为专家
 * 
 * @author zhoufazhan
 * 
 */
public class ApplyExpertActivity extends BaseActivity {
	private RelativeLayout expert_type;// 选择专家类型
	private RelativeLayout agriproduct;// 擅长的作物
	private TextView back;
	private EditText name;
	private TextView front;
	private TextView submit;
	private Context context;
	private TextView expert_text_type;// 显示选择的专家类型
	private TextView expertProduct;// 专家选择的擅长农作物
	private EditText company;// 专家单位
	private EditText job;// 专家职位
	private EditText address;// 专家注册地址
	private EditText remark;// 简介
	private ImageButton front_button;
	private ImageButton back_button;
	private Button mBtn_back;// 返回按钮
	private List<AgriProd> list;
	// 记录选择产品的名称
	final StringBuilder sb = new StringBuilder();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apply_expert);
		context = this;
		expert_type = (RelativeLayout) findViewById(R.id.expert_type);
		agriproduct = (RelativeLayout) findViewById(R.id.agriproduct);
		name = (EditText) findViewById(R.id.name);
		company = (EditText) findViewById(R.id.company);
		job = (EditText) findViewById(R.id.job);
		address = (EditText) findViewById(R.id.address);
		remark = (EditText) findViewById(R.id.remark);
		expertProduct = (TextView) findViewById(R.id.expert_product);
		submit = (TextView) findViewById(R.id.submit);// 提交按钮
		expert_text_type = (TextView) findViewById(R.id.expert_text_type);
		back = (TextView) findViewById(R.id.back_text);
		front = (TextView) findViewById(R.id.front_text);
		front_button = (ImageButton) findViewById(R.id.front_pic);
		back_button = (ImageButton) findViewById(R.id.back_pic);
		mBtn_back = (Button) findViewById(R.id.back);
		front.setText("正面");
		back.setText("背面");
		// 删除内存卡里面存储的病虫害图片 测试
		// FileHelper.deleteDirectory(CommonUtil.getRootFilePath() +
		// "com.greentech.wnd/files/");
		File f = getFilesDir();
		f.delete();
		// 提交
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 判断各个必填项是否为空
				if (name.getText().length() == 0) {
					Toast.makeText(context, "请填写姓名", 1).show();
					return;

				}
				if (expert_text_type.getText().equals("必填")) {
					Toast.makeText(context, "请填写专家类型", 1).show();
					return;
				}
				if (expertProduct.getText().equals("必填")) {
					Toast.makeText(context, "请填写擅长作物", 1).show();
					return;
				}
				if (Constant.front_bitmap == null) {
					Toast.makeText(context, "请选择正面照", 1).show();
					return;
				}
				if (Constant.back_bitmap == null) {
					Toast.makeText(context, "请选择背面照", 1).show();
					return;
				}

				RequestParams params = new RequestParams();
				params.put("frontImg", ImageUtil.Bitmap2InputStream(
						Constant.front_bitmap, 100));// 证件照正面
				params.put("backImg",
						ImageUtil.Bitmap2InputStream(Constant.back_bitmap, 100));// 证件照背面
				params.put("frontImgName", "front.jpg");// 证件照正面名字
				params.put("backImgName", "back.jpg");// 证件照背面名字
				params.put("user.realName", name.getText());// 用戶名
				params.put("user.expertType", expert_text_type.getText());// 专家类型
				params.put("user.agriSc", expertProduct.getText());// 专家擅长的作物
				params.put("user.address", address.getText());
				params.put("user.company", company.getText());
				params.put("user.job", job.getText());
				params.put("user.id", UserInfo.getUserId(context));// 用户id
				params.put("user.remark", remark.getText());// 用户id

				final String url = Constant.SERVIER_PATH
						+ "/json/applyExpertUser.action";
				AsyncHttpClient client = new AsyncHttpClient();
				client.post(url, params, new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						String string = new String(arg2);
						JsonObject json = (JsonObject) GsonUtil.parse(string);
						if (json.get("status").getAsString().equals("success")) {
							ApplyExpertActivity.this.finish();
							Toast.makeText(ApplyExpertActivity.this, "提交成功,请等待审核", 1)
									.show();
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
//						String string = new String(arg2);
						Toast.makeText(ApplyExpertActivity.this, "提交失败", 1)
								.show();

					}
				});

			}
		});
		// 返回按钮
		mBtn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ApplyExpertActivity.this.finish();

			}
		});

		// 证件照正面
		front_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Constant.isFront = true;
				startActivityForResult(new Intent(ApplyExpertActivity.this,
						TypeOfPhotoActivity.class), 2);

			}
		});
		// 证件照背面
		back_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 拍摄背面
				Constant.isFront = false;
				Constant.isBack = true;
				startActivity(new Intent(ApplyExpertActivity.this,
						TypeOfPhotoActivity.class));

			}
		});
		// 专家类型
		expert_type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(ApplyExpertActivity.this,
						ExpertTypeActivity.class), 1);
			}
		});
		// 选择擅长的作物
		agriproduct.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(ApplyExpertActivity.this,
						ExpertProduct.class), 2);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		front_button.setImageBitmap(Constant.front_bitmap);
		back_button.setImageBitmap(Constant.back_bitmap);
	}

	@Override
	protected void onPause() {
		super.onStop();
		back.setText("");
		front.setText("");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 专家类型的选择
		if (requestCode == 1 && resultCode == RESULT_OK) {
			String expert_type = data.getStringExtra("data");
			expert_text_type.setText(expert_type);
		}
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
