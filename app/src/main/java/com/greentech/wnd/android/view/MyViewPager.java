package com.greentech.wnd.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends android.support.v4.view.ViewPager {
	
	OnSlideListener onSlideListener;
	float downX = 0,downY = 0,moveX = 0,moveY = 0,upX = 0, upY = 0;

	public MyViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * ViewPager里面包含了ScrollView时(滑动ScrollView区域跟非ScrollView区域不一样的)，ACTION_UP在onTouchEvent中才监听得到
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		//先执行super.onInterceptTouchEvent(ev)
		boolean flag =super.onInterceptTouchEvent(ev);
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downX = ev.getX();
				downY = ev.getY();
				printXY("onInterceptTouchEvent按下：");
				break;
	
			default:
				break;
		}
		return flag;
	}
	
	/**
	 * ViewPager里面包含了ScrollView时(滑动ScrollView区域跟非ScrollView区域不一样的)，ACTION_DOWN在onTouchEvent中才监听得到
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_UP:
				upX = ev.getX();
				upY = ev.getY();
				//在根据当前移动距离，执行左右滑动的调用
				if(onSlideListener != null){
					onSlideListener.onSlide(upX - downX);
				}
				printXY("onTouchEvent抬起：");
				resetXY();
				printXY("onTouchEvent结束：");
				break;
	
			default:
				break;
		}
		return super.onTouchEvent(ev);
	}

	private void printXY(String tag){
//		Log.wtf(tag, "downX:"+downX+"  " + "downY:"+downY+"  " + "moveX:"+moveX+"  " + "moveY:"+moveY+"  " + "upX:"+upX+"  " + "upY:"+upY+"");
	}
	
	private void resetXY(){
	downX = 0;
	downY = 0;
	moveX = 0;
	moveY = 0;
	upX = 0;
	upY = 0;
	}
	

	public void setOnSlideListener(OnSlideListener onSlideListener) {
		this.onSlideListener = onSlideListener;
	}


	public interface OnSlideListener{
		void onSlide(float dx);
	}
}
