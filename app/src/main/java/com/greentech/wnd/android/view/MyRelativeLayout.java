package com.greentech.wnd.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * 将MyRelativeLayout作为左右侧滑的根View，监听onInterceptTouchEvent和onTouchEvent事件，返回true来拦截事件，不让事件往父层或者底层走，不然的话，在侧滑菜单点击后中间主界面也会相应点击事件，导致侧滑界面盖不住主界面。
 * @author wlj
 *
 */
public class MyRelativeLayout extends RelativeLayout {

	public MyRelativeLayout(Context context) {
		super(context);
	}

	public MyRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onInterceptTouchEvent(event);
//		return true;//触摸事件到当前view为止，不会再传递给父视图处理onTouchEvent事件。
	}
	
	
}
