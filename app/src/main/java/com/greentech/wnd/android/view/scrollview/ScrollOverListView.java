package com.greentech.wnd.android.view.scrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * 自定义滑动列表监听视图
 * [可以监听滑动列表是否滚动到最顶部或最底部]
 * @author O-J-S
 * @version 1.0
 * @since 2012-9-1
 */
public class ScrollOverListView extends ListView {

	// 触摸点的竖列位置
	private int mLastY;
	
	// 列表的顶部位置
	private int mTopPosition;
	
	// 列表的底部位置
	private int mBottomPosition;
	
	// 定义一个空的滑动监听器
	private OnScrollOverListener mOnScrollOverListener = new OnScrollOverListener() {

		@Override
		public boolean onListViewTopAndPullDown(int delta)
		{
			return false;
		}

		@Override
		public boolean onListViewBottomAndPullUp(int delta)
		{
			return false;
		}

		@Override
		public boolean onMotionDown(MotionEvent ev)
		{
			return false;
		}

		@Override
		public boolean onMotionMove(MotionEvent ev, int delta)
		{
			return false;
		}

		@Override
		public boolean onMotionUp(MotionEvent ev)
		{
			return false;
		}

	};

	/**
	 * 构造函数
	 * @param context 上下文环境
	 * @param attrs 属性集合
	 * @param defStyle 
	 */
	public ScrollOverListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	/**
	 * 构造函数
	 * @param context 上下文环境
	 * @param attrs 属性集合
	 */
	public ScrollOverListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	/**
	 * 构造函数
	 * @param context 上下文环境
	 */
	public ScrollOverListView(Context context)
	{
		super(context);
		init();
	}

	/**
	 * 初始化
	 */
	private void init()
	{
		mTopPosition = 0;
		mBottomPosition = 0;
	}

	/**
	 * 定义触摸事件响应
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		final int action = ev.getAction();
		final int y = (int) ev.getRawY();

		switch(action)
		{
			case MotionEvent.ACTION_DOWN:
			{
				mLastY = y;
				final boolean isHandled = mOnScrollOverListener.onMotionDown(ev);
				if(isHandled)
				{
					mLastY = y;
					return isHandled;
				}
				break;
			}
			case MotionEvent.ACTION_MOVE:
			{
				final int childCount = getChildCount();
				if(childCount == 0)
				{	
					return super.onTouchEvent(ev);
				}

				final int itemCount = getAdapter().getCount() - mBottomPosition;
				final int deltaY = y - mLastY;
				final int firstTop = getChildAt(0).getTop();
				final int listPadding = getListPaddingTop();
				final int lastBottom = getChildAt(childCount - 1).getBottom();
				final int end = getHeight() - getPaddingBottom();
				final int firstVisiblePosition = getFirstVisiblePosition();
				final boolean isHandleMotionMove = mOnScrollOverListener.onMotionMove(ev, deltaY);
				if(isHandleMotionMove)
				{
					mLastY = y;
					return true;
				}

				if(firstVisiblePosition <= mTopPosition && firstTop >= listPadding && deltaY > 0)
				{
					final boolean isHandleOnListViewTopAndPullDown;
					isHandleOnListViewTopAndPullDown = mOnScrollOverListener.onListViewTopAndPullDown(deltaY);
					if(isHandleOnListViewTopAndPullDown)
					{
						mLastY = y;
						return true;
					}
				}

				if(firstVisiblePosition + childCount >= itemCount && lastBottom <= end && deltaY < 0)
				{
					final boolean isHandleOnListViewBottomAndPullDown;
					isHandleOnListViewBottomAndPullDown = mOnScrollOverListener.onListViewBottomAndPullUp(deltaY);
					if(isHandleOnListViewBottomAndPullDown)
					{
						mLastY = y;
						return true;
					}
				}
				break;
			}
			case MotionEvent.ACTION_UP:
			{
				final boolean isHandlerMotionUp = mOnScrollOverListener.onMotionUp(ev);
				if(isHandlerMotionUp)
				{
					mLastY = y;
					return true;
				}
				break;
			}
		}

		mLastY = y;
		return super.onTouchEvent(ev);
	}

	/**
	 * 自定义其中一个条目为头部，头部触发的事件将以这个为准，默认为第一个
	 * @param index 正数第几个[必须在条目数范围之内]
	 */
	public void setTopPosition(int index)
	{
		if(getAdapter() == null)
		{
			throw new NullPointerException("You must set adapter before setTopPosition!");
		}
		if(index < 0)
		{
			throw new IllegalArgumentException("Top position must > 0");
		}

		mTopPosition = index;
	}

	/**
	 * 自定义其中一个条目为尾部，尾部触发的事件将以这个为准，默认为最后一个
	 * @param index 倒数第几个[必须在条目数范围之内]
	 */
	public void setBottomPosition(int index)
	{
		if(getAdapter() == null)
		{
			throw new NullPointerException("You must set adapter before setBottonPosition!");
		}
		if(index < 0)
		{
			throw new IllegalArgumentException("Bottom position must > 0");
		}

		mBottomPosition = index;
	}

	/**
	 * 设置监听器监听是否到达顶端或者是否到达低端等事件
	 * @see OnScrollOverListener
	 */
	public void setOnScrollOverListener(OnScrollOverListener onScrollOverListener)
	{
		mOnScrollOverListener = onScrollOverListener;
	}

}
