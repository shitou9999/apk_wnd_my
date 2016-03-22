package com.greentech.wnd.android;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.greentech.wnd.android.util.BoundUtil;
import com.greentech.wnd.android.util.HeaderImageUtils;
import com.greentech.wnd.android.util.UserInfo;

/**
 * 根据构造函数中传入的需要填充的Layout资源Id，将相应的Layout填充到指定的容器中。 此类实现了Observer是一个观察者，
 */
public class LoginFragment extends Fragment implements Observer {

	private Handler mHandler;
	private Context mContext;
	private View mView;
	private ImageView mImageView;
	private RelativeLayout friend_yqm;
	private RelativeLayout myCard;
	private RelativeLayout invitefriends;// 邀请好友
	private RelativeLayout apply_expert;// 申请为专家按钮
	private RelativeLayout score;// 积分规则
	private RelativeLayout ask_answer;// 我的问答
	private RelativeLayout collect;// 我的收藏
	private RelativeLayout code;// 二维码
	private TextView username;
	private TextView yqm_text;
	private TextView friend_yqm_text;// 朋友的邀请码
	private TextView bounds_text;
	SharedPreferences sharedPre;
	SharedPreferences isAdd;// 增加邀请积分
	SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
	private LoginActivity loginActivity;

	public LoginFragment() {
		super();
		mHandler = new Handler();
	}

	public LoginFragment(Handler handler) {
		super();
		mHandler = handler;
		if (mHandler == null) {
			mHandler = new Handler();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = container.getContext();

		loginActivity = new LoginActivity();
		mView = inflater.inflate(R.layout.fragment_login, container, false);
		username = (TextView) mView.findViewById(R.id.username);
		yqm_text = (TextView) mView.findViewById(R.id.code);
		bounds_text = (TextView) mView.findViewById(R.id.score);
		friend_yqm_text = (TextView) mView.findViewById(R.id.friend_yqm_text);
		friend_yqm = (RelativeLayout) mView.findViewById(R.id.friend_yqm);
		apply_expert = (RelativeLayout) mView.findViewById(R.id.apply_expert);
		invitefriends = (RelativeLayout) mView.findViewById(R.id.invitefriends);
		myCard = (RelativeLayout) mView.findViewById(R.id.myCard);
		score = (RelativeLayout) mView.findViewById(R.id.score_relative);
		ask_answer = (RelativeLayout) mView.findViewById(R.id.ask_answer);
		collect = (RelativeLayout) mView.findViewById(R.id.collect);
		code = (RelativeLayout) mView.findViewById(R.id.code_er);
		mImageView = (ImageView) mView.findViewById(R.id.img);
		sharedPre = getActivity().getSharedPreferences("config", 1);
		
		mImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, LoginActivity.class);
				startActivity(intent);
			}
		});
		ask_answer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UserInfo.isLogin(getActivity())) {
					Intent intent = new Intent(mContext,
							AskAndAnswerActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(mContext, LoginActivity.class);
					startActivity(intent);
				}
			}
		});
		collect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UserInfo.isLogin(getActivity())) {
					Intent intent = new Intent(mContext, CollectActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(mContext, LoginActivity.class);
					startActivity(intent);
				}

			}
		});
		code.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					Intent intent = new Intent(mContext, ErWeiMaActivity.class);
					startActivity(intent);
				
			}
		});
		// notice.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(mContext, NoticeListActivity.class);
		// startActivity(intent);
		// }
		// });
		// tech.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(mContext, TechListActivity.class);
		// startActivity(intent);
		// }
		// });
		// notice_add.setOnClickListener(new OnClickListener() {
		// /**
		// * 判断用户是否登录，并且是否是政府部门的用户 如果没有登录，就跳转到登录界面 如果已登录但不是政府部门,跳出提示框
		// */
		// @Override
		// public void onClick(View v) {
		//
		// if (UserInfo.isLogin(getActivity())
		// && UserInfo.getUserType(getActivity())
		// .equals("govdept")) {
		// Intent intent = new Intent(mContext,
		// AddNoticeActivity.class);
		// startActivity(intent);
		// } else {
		// Intent intent = new Intent(mContext, LoginActivity.class);
		// startActivity(intent);
		// }
		// }
		// });
		// tech_add.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// if (UserInfo.isLogin(getActivity())
		// && UserInfo.getUserType(getActivity()).equals("expert")) {
		// Intent intent = new Intent(mContext, AddTechActivity.class);
		// startActivity(intent);
		// } else {
		// Intent intent = new Intent(mContext, LoginActivity.class);
		// startActivity(intent);
		// }
		//
		// }
		// });
		// 专家申请按钮点击事件，申请前必须先注册普通用户，并且已登录
		apply_expert.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UserInfo.isLogin(getActivity())) {
					Intent intent = new Intent(mContext,
							ApplyExpertActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(mContext, LoginActivity.class);
					startActivity(intent);
				}

			}
		});
		// 邀请好友
		invitefriends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UserInfo.isLogin(getActivity())) {

					Intent sendIntent = new Intent();
					sendIntent.setAction(Intent.ACTION_SEND);
					sendIntent.putExtra(Intent.EXTRA_TEXT, "我在用问农答，我的验证码"
							+ UserInfo.getYQM(mContext)
							+ "http://www.greentech-nanjing.com");
					sendIntent.setType("text/plain");
					startActivity(sendIntent);
					// 邀请好友增加10积分，没有做判断
					SharedPreferences pre = mContext.getSharedPreferences(
							"bound", 2);
					// 获取Editor对象
					Editor editor = pre.edit();
					// 格式化时间
					String time = format.format(new Date());
					// 用现在的时间减去保存到的是时间，如果大于等于1表示，不是同一天邀请好友，增加10积分
					if (Integer.valueOf(time).intValue()
							- pre.getInt("time", 0) >= 1) {
						BoundUtil.addBonus(10, getActivity());

						// 更新时间
						editor.putInt("time", Integer.valueOf(time).intValue());
						editor.commit();
					}

				} else {
					Intent intent = new Intent(getActivity(),
							LoginActivity.class);
					startActivity(intent);
				}

			}
		});
		// 我的名片
		myCard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UserInfo.isLogin(getActivity())) {
					Intent intent = new Intent(mContext, MyCardActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(mContext, LoginActivity.class);
					startActivity(intent);
				}
			}
		});
		// 填写朋友的验证码
		friend_yqm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UserInfo.isLogin(getActivity())) {
					Intent intent = new Intent(mContext, YzmActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(mContext, LoginActivity.class);
					startActivity(intent);
				}

			}
		});
		// 积分规则
		score.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UserInfo.isLogin(getActivity())) {
					Intent intent = new Intent(mContext, RulesOfScore.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(mContext, LoginActivity.class);
					startActivity(intent);
				}
			}
		});

		return mView;
	}

	/**
	 * 将SharedPreferences中保存的用户信息显示出来
	 */
	@Override
	public void onResume() {
		super.onResume();
		/**
		 * 将保存的bitmap头像放入imageview中 在内存卡里面读取头像
		 */
		if (HeaderImageUtils.getHeaderImageFromSD(getActivity()) != null) {
			mImageView.setImageBitmap(HeaderImageUtils
					.getHeaderImageFromSD(getActivity()));
		} else {
			mImageView.setImageBitmap(((BitmapDrawable) getResources()
					.getDrawable(R.drawable.header_gray)).getBitmap());
		}

		/**
		 * 首先判断是否已经有保存的用户名，如果有则把它显示在textView上 在有用户名的基础上在判断是哪一种用户，然后显示其应该有的功能
		 * 如果用户没有登录则显示 “请点击头像登录”
		 */
		String savedUserName = sharedPre.getString("username", "");// 这是修改提交的用户名
		int bounds = sharedPre.getInt("bounds", 0);
		String yqm = sharedPre.getString("yqm", "");
		if (UserInfo.isLogin(getActivity())
				&& StringUtils.isBlank(savedUserName)) {
			username.setText("问农答网友");
		}
		if (UserInfo.isLogin(getActivity())
				&& StringUtils.isNotBlank(savedUserName)) {
			username.setText(savedUserName);
		}

		if(!UserInfo.isLogin(getActivity())){
			username.setText("点击头像登陆");
		}
		bounds_text.setText("积分:" + bounds);
		if (StringUtils.isNotBlank(yqm)) {
			yqm_text.setText("邀请码:" + yqm);
		}
		if (UserInfo.getFriendYQM(getActivity()) != null
				&& UserInfo.getFriendYQM(getActivity()).trim().length() > 0) {
			friend_yqm_text.setText("您已输入了朋友的邀请码:"
					+ UserInfo.getFriendYQM(getActivity()));
			friend_yqm.setClickable(false);
		}
	}

	// 当被观察者调用notifyObservers方法时，LoginFragment调用update方法
	// 这里不能直接写 username.setText("请点击头像登录");
	// 会报username为null，具体原因不知
	@Override
	public void update(Observable observable, Object data) {

	}

}
