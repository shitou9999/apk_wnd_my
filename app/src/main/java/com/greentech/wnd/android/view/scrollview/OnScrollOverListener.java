package com.greentech.wnd.android.view.scrollview;

import android.view.MotionEvent;
import android.view.View;

/**
 * 滑动监听器
 * @author O-J-S
 * @version 1.0
 * @since 2012-9-1
 */
public interface OnScrollOverListener {

	/**
	 * 到达最顶部触发
	 * @param delta 手指点击移动产生的偏移量
	 * @return 
	 */
	boolean onListViewTopAndPullDown(int delta);

	/**
	 * 到达最底部触发
	 * @param delta 手指点击移动产生的偏移量
	 * @return 
	 */
	boolean onListViewBottomAndPullUp(int delta);
	
	/**
	 * 手指触摸按下触发，相当于{@link MotionEvent#ACTION_DOWN}
	 * @return 返回true表示自己处理
	 * @see View#onTouchEvent(MotionEvent)
	 */
	boolean onMotionDown(MotionEvent ev);
	
	/**
	 * 手指触摸移动触发，相当于{@link MotionEvent#ACTION_MOVE}
	 * @return 返回true表示自己处理
	 * @see View#onTouchEvent(MotionEvent)
	 */
	boolean onMotionMove(MotionEvent ev, int delta);
	
	/**
	 * 手指触摸后提起触发，相当于{@link MotionEvent#ACTION_UP} 
	 * @return 返回true表示自己处理
	 * @see View#onTouchEvent(MotionEvent)
	 */
	boolean onMotionUp(MotionEvent ev);
	
}
