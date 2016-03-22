package com.greentech.wnd.android;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.greentech.wnd.android.ExpertListFragment.OnExpertListFragmentListener;
import com.greentech.wnd.android.bean.User;
import com.greentech.wnd.android.util.UserInfo;
/**
 * 专家列表，选择专家
 * @author WP
 *
 */
public class ExpertListActivity extends BaseActivity{
	
	private ExpertListFragment mFragment;
	private Enum_RequestType requestType = null;
	public static final String RETURN_VALUE_SELECTED_USER = "selectedUser";
	public static final String REQUEST_TYPE = "requestType";
	public String type="";
	public static enum Enum_RequestType {
		RETURN_USER_INFO,
		GOTO_ASK
	}
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	setContentView(R.layout.root);
    	
    	//获得调用者传入的bundle
    	Bundle bundle = getIntent().getExtras();
    	if(bundle != null) {
    		requestType = (Enum_RequestType)bundle.get(REQUEST_TYPE);
    		type=bundle.getString("type");
    	}
    	
    	if(getFragmentManager().findFragmentByTag("experts") == null) {
    		
			FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
			mFragment = new ExpertListFragment(type);
			mFragment.setOnExpertListFragmentListener(new OnExpertListFragmentListener() {
				
				@Override
				public void onItemSelected(User selectedUser) {
					if (UserInfo.isLogin(ExpertListActivity.this)) {
						//获得选中item，传给调用者或者进入提问页面
						if(requestType != null && requestType == Enum_RequestType.RETURN_USER_INFO) {
							Intent intent = new Intent();
							intent.putExtra(RETURN_VALUE_SELECTED_USER, selectedUser);
							setResult(Activity.RESULT_OK, intent);
							finish();
						} else if(requestType != null && requestType == Enum_RequestType.GOTO_ASK) {
							Intent intent = new Intent(ExpertListActivity.this, AddTopicActivity.class);
							intent.putExtra(RETURN_VALUE_SELECTED_USER, selectedUser);
							startActivity(intent);
						}
					} else {
						toastShow("需要先登录才能向专家提问");
						Intent intent = new Intent(ExpertListActivity.this,
								LoginActivity.class);
						startActivity(intent);
					}
				}
				
				@Override
				public void onBackBtn_click() {
					finish();
				}
			});
			fragmentTransaction.add(R.id.root, mFragment, "experts");
			fragmentTransaction.commit();
    	}
    }
    
    
	
	/**
	 * 按键点击事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ExpertListActivity.this.finish();
			return true;
		} else {
			return false;
		}
	}

}
