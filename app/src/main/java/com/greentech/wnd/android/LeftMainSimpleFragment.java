package com.greentech.wnd.android;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.Header;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.BoundUtil;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.HeaderImageUtils;
import com.greentech.wnd.android.util.UserInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 根据构造函数中传入的需要填充的Layout资源Id，将相应的Layout填充到指定的容器中。
 * 
 * @author wlj
 * 
 */
public class LeftMainSimpleFragment extends Fragment {
	private ImageView mHeaderImage;
	private Button exitBtn;
	private Button dataBtn;
	private Button sign;// 签到
	private TextView tv;
	private SharedPreferences preference;
	private Editor editor;
	private ImageView money;

	public LeftMainSimpleFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_left, null);
		exitBtn = (Button) view.findViewById(R.id.exitBtn);
		dataBtn = (Button) view.findViewById(R.id.completeData);
		money = (ImageView) view.findViewById(R.id.money);
		sign = (Button) view.findViewById(R.id.sign);
		mHeaderImage = (ImageView) view.findViewById(R.id.headerImage);
		tv = (TextView) view.findViewById(R.id.textView1);
		preference = getActivity().getSharedPreferences("config", 1);
		exitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HeaderImageUtils.deleteHeader(getActivity().getFilesDir()
						+ "/header");
				preference = getActivity().getSharedPreferences("config", 1);
				editor = preference.edit();
				editor.clear();
				editor.commit();
				getActivity().finish();
				System.exit(0);
			}
		});
		dataBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UserInfo.isLogin(getActivity())) {
					startActivity(new Intent(getActivity(),
							DataModificationActivity.class));
				} else {
					startActivity(new Intent(getActivity(), LoginActivity.class));
				}

			}
		});
		sign.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UserInfo.isLogin(getActivity())) {
					getSignInfo();
				} else {
					startActivity(new Intent(getActivity(), LoginActivity.class));
				}

			}
		});
		// 为头像图片设置监听事件,如果已经登录就设置头像，没有登录则跳转到登录界面
		// mHeaderImage.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// if (UserInfo.isLogin(getActivity())) {
		// startActivity(new Intent(getActivity(),
		// SettingActivity.class));
		// } else {
		// startActivity(new Intent(getActivity(), LoginActivity.class));
		// }
		//
		// }
		// });

		return view;
	}

	// 格式化时间
	SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
	final String time = format.format(new Date());
	// 连续签到次数
	int i = 0;
	// 积分
	int bounds = 0;
	// 获取用户的签到信息
	public String getSignInfo() {

		RequestParams params = new RequestParams();
		params.put("user.id", UserInfo.getUserId(getActivity()));
		String url = Constant.SERVIER_PATH + "/json/getSignInfo.action";
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				String str = new String(arg2);
				JsonObject json = (JsonObject) GsonUtil.parse(str);
				if (json.get("sign").getAsString().equals("")) {
					Log.i(Constant.TAG, "没有签过到");
					// 没有签过到,添加签到信息，并且增加积分
					i = 1;bounds=2;
					addSign(time+"."+i, bounds);
				} else {
					//有签到过，计算这次签到与上次是否连续，如果连续增加相应的积分与连续的次数---一天只能签到一次
					//获取上次签到的信息
					String lastSign=json.get("sign").getAsString();
					//上次签到的时间
					String lastTime=lastSign.substring(0, 8);
					//连续签到的次数
					i=Integer.valueOf(lastSign.substring(9));
					//计算是否连续签到
					int t=Integer.valueOf(time).intValue()-Integer.valueOf(lastTime).intValue();
					//t==0表示当天连续签到，不容许，t==1表示连续签到，否则不连续
					if(t==1){
						if(i==1){
							bounds=4;
							addSign(time+"."+(i+1), bounds);
						}
						if(i==2){
							bounds=6;
							addSign(time+"."+(i+1), bounds);
						}
						if(i==3){
							bounds=8;
							addSign(time+"."+(i+1), bounds);
						}
						if(i==4){
							bounds=10;
							addSign(time+"."+(i+1), bounds);
						}if(i>=5){
							bounds=10;
							
							addSign(time+"."+(i+1), bounds);
						}
					}if(t==0){
						Toast.makeText(getActivity(), "您今天已经签过到", 1).show();
					}if(t>1){
						//不连续就重头开始
						bounds=2;
						i=1;
						addSign(time+"."+i, bounds);
					}
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				Log.i(Constant.TAG, "错误");

			}
		});
		return null;

	}

	// 添加签到信息，并且增加积分
	public void addSign(String time, final int bounds) {
		RequestParams params = new RequestParams();
		params.put("user.id", UserInfo.getUserId(getActivity()));
		params.put("sign", time);

		String url = Constant.SERVIER_PATH + "/json/addSignInfo.action";
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
               BoundUtil.addBonus(bounds, getActivity());
              Animation animation= AnimationUtils.loadAnimation(getActivity(), R.anim.money);
              money.startAnimation(animation);
              animation.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					money.setVisibility(View.VISIBLE);
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					money.setVisibility(View.GONE);
					
				}
			});
              
               sign.setText("签到成功");
               Toast.makeText(getActivity(), "签到成功，增加"+bounds+"积分", 1).show();
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onResume() {
		// 将保存的用户名显示出来
		super.onResume();

		/**
		 * 在内存卡里面读取头像
		 */
		if (HeaderImageUtils.getHeaderImageFromSD(getActivity()) != null) {
			mHeaderImage.setImageBitmap(HeaderImageUtils
					.getHeaderImageFromSD(getActivity()));
		}
		String username = preference.getString("username", "");
		if (username.length() > 0) {
			tv.setText(username);
		} else {
			tv.setText("欢迎您");
		}

	}

}
