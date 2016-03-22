package com.greentech.wnd.android;

import org.apache.http.Header;

import com.google.gson.JsonObject;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.BoundUtil;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.UserInfo;
import com.greentech.wnd.android.util.LocationUtil.OnLocationListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 填写朋友的邀请码直接给自己加200积分，没有检验邀请码是否正确，也没有检查自己的邀请码是否被别人填写
 * @author zhoufazhan
 *
 */
public class YzmActivity extends Activity implements OnClickListener {
	private EditText editText;
	private Button back;
	private TextView submit;
	private String yqm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yzm);
		init();

	}

	private void init() {
		editText = (EditText) findViewById(R.id.yzm);
		back = (Button) findViewById(R.id.back);
		submit = (TextView) findViewById(R.id.submit);
		back.setOnClickListener(this);
		submit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			YzmActivity.this.finish();
			break;
		case R.id.submit:
			yqm = editText.getText().toString();
			if (yqm.trim().length() == 0 || yqm.equals(null)) {
				Toast.makeText(YzmActivity.this, "请输入验证码", 1).show();
				return;
			}
			RequestParams params = new RequestParams();
			params.put("yqmOthers", yqm);
			params.put("user.id", UserInfo.getUserId(YzmActivity.this));
			AsyncHttpClient client = new AsyncHttpClient();
			String url = Constant.SERVIER_PATH + "/json/saveYQM.action";
			client.get(url, params, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					String string = new String(arg2);
					JsonObject json = (JsonObject) GsonUtil.parse(string);
					String s = json.get("status").getAsString();
					if (s.equals("success")) {
						UserInfo.setFriendYQM(YzmActivity.this, yqm);
						Toast.makeText(YzmActivity.this, "提交成功", 1).show();
						// 200积分
						BoundUtil.addBonus(200, YzmActivity.this);
						YzmActivity.this.finish();
					}if(s.equals("failed")){
						Toast.makeText(YzmActivity.this, "您填写的邀请码不存在", 1).show();
					}
				}

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2,
						Throwable arg3) {

				}
			});
			break;
		}

	}

}
