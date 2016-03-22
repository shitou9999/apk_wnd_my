package com.greentech.wnd.android.view.scrollview;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.greentech.wnd.android.R;

/**
 * 自定义滑动列表视图
 * [向下滑动自动加载，向上滑动自动刷新]
 * @author O-J-S
 * @version 1.0
 * @since 2012-9-1
 */
public class ScrollListView extends LinearLayout implements OnScrollOverListener {

	// 移动误差
	private static final int START_PULL_DEVIATION = 50;

	// 自增量，用于回弹
	private static final int AUTO_INCREMENTAL = 10;

	// Handler what 数据加载完毕
	private static final int WHAT_DID_LOAD_DATA = 1;

	// Handler what 刷新中
	private static final int WHAT_ON_REFRESH = 2;

	@Override
	public boolean isInEditMode() {
		// TODO Auto-generated method stub
		return super.isInEditMode();
	}

	// Handler what 已经刷新完
	private static final int WHAT_DID_REFRESH = 3;

	// Handler what 设置高度
	private static final int WHAT_SET_HEADER_HEIGHT = 4;

	// Handler what 已经获取完更多
	private static final int WHAT_DID_MORE = 5;

	// 头部视图原本的高度
	private static final int DEFAULT_HEADER_VIEW_HEIGHT = 105;

	// 定义日期显示格式
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private View mHeaderView;
	private LayoutParams mHeaderViewParams;
	private TextView mHeaderViewDateView;
	private TextView mHeaderTextView;
	private ImageView mHeaderArrowView;
	private View mHeaderLoadingView;
	private View mFooterView;
	private TextView mFooterTextView;
	private View mFooterLoadingView;
	private ScrollOverListView mListView;

	private Handler mUIHandler;
	
	private OnPullDownListener mOnPullDownListener;
	
	private RotateAnimation mRotateOTo180Animation;
	private RotateAnimation mRotate180To0Animation;

	// 增量
	private int mHeaderIncremental;

	// 按下时候的Y轴坐标
	private float mMotionDownLastY;

	// 是否按下
	private boolean mIsDown;

	// 是否下拉刷新中
	private boolean mIsRefreshing;

	// 是否获取更多中
	private boolean mIsFetchMoreing;

	// 是否回推完成
	private boolean mIsPullUpDone;

	// 是否允许自动获取更多
	private boolean mEnableAutoFetchMore;

	// 头部视图的状态[空闲]
	private static final int HEADER_VIEW_STATE_IDLE = 0;

	// 头部视图的状态[未超过默认高度]
	private static final int HEADER_VIEW_STATE_NOT_OVER_HEIGHT = 1;

	// 头部视图的状态[超过默认高度]
	private static final int HEADER_VIEW_STATE_OVER_HEIGHT = 2;

	// 头部视图的状态
	private int mHeaderViewState = HEADER_VIEW_STATE_IDLE;

	/**
	 * 构造函数
	 * @param context 上下文环境
	 * @param attrs 属性集合
	 */
	public ScrollListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initHeaderViewAndFooterViewAndListView(context);
	}

	/**
	 * 构造函数
	 * @param context 上下文环境
	 */
	public ScrollListView(Context context)
	{
		super(context);
		initHeaderViewAndFooterViewAndListView(context);
	}

	/**
	 * 通知加载完数据完毕，要放在Adapter.notifyDataSetChanged后面
	 * [当你加载完数据的时候，调用该方法才会执行隐藏头部和初始化数据等]
	 */
	public void notifyDidLoad()
	{
		mUIHandler.sendEmptyMessage(WHAT_DID_LOAD_DATA);
	}

	/**
	 * 通知已经刷新完毕，要放在Adapter.notifyDataSetChanged后面
	 * [当你执行完刷新任务之后，调用该方法才会执行隐藏掉头部文件等操作]
	 */
	public void notifyDidRefresh()
	{
		mUIHandler.sendEmptyMessage(WHAT_DID_REFRESH);
	}

	/**
	 * 通知已经获取更多完毕，要放在Adapter.notifyDataSetChanged后面
	 * [当你执行完获取更多任务之后，调用该方法才会执行隐藏加载圈等操作]
	 */
	public void notifyDidMore()
	{
		mUIHandler.sendEmptyMessage(WHAT_DID_MORE);
	}

	/**
	 * 设置下拉刷新监听器
	 * @param listener 下拉刷新监听器
	 */
	public void setOnPullDownListener(OnPullDownListener listener)
	{
		mOnPullDownListener = listener;
	}

	/**
	 * 获取内嵌的ListView
	 * @return ScrollOverListView
	 */
	public ListView getListView()
	{
		return mListView;
	}

	/**
	 * 是否开启自动获取更多 
	 * [自动获取更多，将会隐藏底部视图，并在到达底部的时候自动刷新]
	 * @param index 倒数第几个触发
	 */
	public void enableAutoFetchMore(boolean enable, int index)
	{
		if(enable)
		{
			mListView.setBottomPosition(index);
			mFooterLoadingView.setVisibility(View.VISIBLE);
		}else
		{
			mFooterTextView.setText("更多");
			mFooterTextView.setTextColor(mFooterTextView.getResources().getColor(R.color.custom_grey));
			mFooterLoadingView.setVisibility(View.GONE);
		}
		mEnableAutoFetchMore = enable;
	}

	/**
	 * 初始化头部和脚部视图
	 * @param context 上下文
	 */
	private void initHeaderViewAndFooterViewAndListView(Context context)
	{
		// 设置布局方向
		setOrientation(LinearLayout.VERTICAL);
		
		// 自定义头部视图[放在这里是因为考虑到很多界面都需要使用， 如果要修改，和它相关的设置都要更改]
		mHeaderView = LayoutInflater.from(context).inflate(R.layout.pulldown_header, null);
		mHeaderViewParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		addView(mHeaderView, 0, mHeaderViewParams);

		// 获取头部视图中的子视图
		mHeaderTextView = (TextView) mHeaderView.findViewById(R.id.pulldown_header_text);
		mHeaderArrowView = (ImageView) mHeaderView.findViewById(R.id.pulldown_header_arrow);
		mHeaderLoadingView = mHeaderView.findViewById(R.id.pulldown_header_loading);

		// 旋转动画[注意，图片旋转之后，再执行旋转，坐标会重新开始计算]
		mRotateOTo180Animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateOTo180Animation.setDuration(250);
		mRotateOTo180Animation.setFillAfter(true);

		mRotate180To0Animation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mRotate180To0Animation.setDuration(250);
		mRotate180To0Animation.setFillAfter(true);

		// 自定义底部视图
		mFooterView = LayoutInflater.from(context).inflate(R.layout.pulldown_footer, null);
		if(!isInEditMode()){
			mFooterTextView = (TextView) mFooterView.findViewById(R.id.pulldown_footer_text);
		}
		
		
		mFooterLoadingView = mFooterView.findViewById(R.id.pulldown_footer_loading);
		mFooterView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				if(!mIsFetchMoreing)
				{
					mIsFetchMoreing = true;
					mFooterLoadingView.setVisibility(View.VISIBLE);
					mOnPullDownListener.onMore();
				}
			}
		});
		
		// 自定义Handler
		mUIHandler = new UIHandler(ScrollListView.this);

		// 滑动列表视图
		mListView = new ScrollOverListView(context);
		mListView.setOnScrollOverListener(this);
		mListView.setCacheColorHint(0);
		addView(mListView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		// 定义空的下拉刷新监听器
		mOnPullDownListener = new OnPullDownListener() {
			@Override
			public void onRefresh()
			{}

			@Override
			public void onMore()
			{}
		};
	}

	/**
	 * 在下拉和回推的时候检查头部文件的状态
	 * [如果超过了默认高度，就显示松开可以刷新， 否则显示下拉可以刷新]
	 */
	private void checkHeaderViewState()
	{
		if(mHeaderViewParams.height >= DEFAULT_HEADER_VIEW_HEIGHT)
		{
			if(mHeaderViewState == HEADER_VIEW_STATE_OVER_HEIGHT)
			{
				return;
			}
			mHeaderViewState = HEADER_VIEW_STATE_OVER_HEIGHT;
			mHeaderTextView.setText("松开可以刷新");
			mHeaderTextView.setTextColor(mHeaderTextView.getResources().getColor(R.color.custom_grey));
			mHeaderArrowView.startAnimation(mRotateOTo180Animation);
		}else
		{
			if(mHeaderViewState == HEADER_VIEW_STATE_NOT_OVER_HEIGHT || mHeaderViewState == HEADER_VIEW_STATE_IDLE)
			{
				return;
			}
			mHeaderViewState = HEADER_VIEW_STATE_NOT_OVER_HEIGHT;
			mHeaderTextView.setText("下拉可以刷新");
			mHeaderTextView.setTextColor(mHeaderTextView.getResources().getColor(R.color.custom_grey));
			mHeaderArrowView.startAnimation(mRotate180To0Animation);
		}
	}

	/**
	 * 设置头部视图的高度
	 * @param height 头部视图的高度
	 */
	private void setHeaderHeight(final int height)
	{
		mHeaderIncremental = height;
		mHeaderViewParams.height = height;
		mHeaderView.setLayoutParams(mHeaderViewParams);
	}

	/**
	 * 自动隐藏动画
	 */
	class HideHeaderViewTask extends TimerTask {
		@Override
		public void run()
		{
			if(mIsDown)
			{
				cancel();
				return;
			}
			mHeaderIncremental -= AUTO_INCREMENTAL;
			if(mHeaderIncremental > 0)
			{
				mUIHandler.sendEmptyMessage(WHAT_SET_HEADER_HEIGHT);
			}else
			{
				mHeaderIncremental = 0;
				mUIHandler.sendEmptyMessage(WHAT_SET_HEADER_HEIGHT);
				cancel();
			}
		}
	}

	/**
	 * 自动显示动画
	 */
	class ShowHeaderViewTask extends TimerTask {

		@Override
		public void run()
		{
			if(mIsDown)
			{
				cancel();
				return;
			}
			mHeaderIncremental -= AUTO_INCREMENTAL;
			if(mHeaderIncremental > DEFAULT_HEADER_VIEW_HEIGHT)
			{
				mUIHandler.sendEmptyMessage(WHAT_SET_HEADER_HEIGHT);
			}else
			{
				mHeaderIncremental = DEFAULT_HEADER_VIEW_HEIGHT;
				mUIHandler.sendEmptyMessage(WHAT_SET_HEADER_HEIGHT);
				if(!mIsRefreshing)
				{
					mIsRefreshing = true;
					mUIHandler.sendEmptyMessage(WHAT_ON_REFRESH);
				}
				cancel();
			}
		}
	}

	/**
	 * 自定义Handler
	 * @author O-J-S
	 * @version 1.0
	 * @since 2012-9-1
	 */
	private static class UIHandler extends Handler{
		
		// 定义弱应用
		WeakReference<ScrollListView> myListView;
		
		/**
		 * 构造函数
		 */
		UIHandler(ScrollListView listView)
		{
			myListView = new WeakReference<ScrollListView>(listView);
		}
		
		@Override
		public void handleMessage(Message msg)
		{
			// 定义父类的方法
			super.handleMessage(msg);
			
			// 获取外部对象的弱引用
			ScrollListView listView = myListView.get();
			
			switch(msg.what)
			{
				case WHAT_DID_LOAD_DATA:
				{
					listView.mHeaderViewParams.height = 0;
					listView.mHeaderLoadingView.setVisibility(View.GONE);
					listView.mHeaderTextView.setText("下拉可以刷新");
					listView.mHeaderTextView.setTextColor(listView.mListView.getResources().getColor(R.color.custom_grey));
					listView.mHeaderViewDateView = (TextView) listView.mHeaderView.findViewById(R.id.pulldown_header_date);
					listView.mHeaderViewDateView.setVisibility(View.VISIBLE);
					listView.mHeaderViewDateView.setText("更新于：" + dateFormat.format(new Date(System.currentTimeMillis())));
					listView.mHeaderViewDateView.setTextColor(listView.mListView.getResources().getColor(R.color.custom_grey));
					listView.mHeaderArrowView.setVisibility(View.VISIBLE);
					listView.showFooterView();
					return;
				}
				case WHAT_ON_REFRESH:
				{
					// 要清除掉动画，否则无法隐藏
					listView.mHeaderArrowView.clearAnimation();
					listView.mHeaderArrowView.setVisibility(View.INVISIBLE);
					listView.mHeaderLoadingView.setVisibility(View.VISIBLE);
					listView.mOnPullDownListener.onRefresh();
					return;
				}
				case WHAT_DID_REFRESH:
				{
					listView.mIsRefreshing = false;
					listView.mHeaderViewState = HEADER_VIEW_STATE_IDLE;
					listView.mHeaderArrowView.setVisibility(View.VISIBLE);
					listView.mHeaderLoadingView.setVisibility(View.GONE);
					listView.mHeaderViewDateView.setText("更新于：" + dateFormat.format(new Date(System.currentTimeMillis())));
					listView.mHeaderViewDateView.setTextColor(listView.mListView.getResources().getColor(R.color.custom_grey));
					listView.setHeaderHeight(0);
					listView.showFooterView();
					return;
				}
				case WHAT_SET_HEADER_HEIGHT:
				{
					listView.setHeaderHeight(listView.mHeaderIncremental);
					return;
				}
				case WHAT_DID_MORE:
				{
					listView.mIsFetchMoreing = false;
					listView.mFooterTextView.setText("更多");
					listView.mFooterTextView.setTextColor(listView.mListView.getResources().getColor(R.color.custom_grey));
					listView.mFooterLoadingView.setVisibility(View.GONE);
				}
			}		
		}	
		
	}

	/**
	 * 显示底部视图
	 */
	private void showFooterView()
	{
		if(mListView.getFooterViewsCount() == 0 && isFillScreenItem())
		{
			mListView.addFooterView(mFooterView);
			mListView.setAdapter(mListView.getAdapter());
		}
	}

	/**
	 * 判断条目是否填满整个屏幕
	 */
	private boolean isFillScreenItem()
	{
		final int firstVisiblePosition = mListView.getFirstVisiblePosition();
		final int lastVisiblePostion = mListView.getLastVisiblePosition() - mListView.getFooterViewsCount();
		final int visibleItemCount = lastVisiblePostion - firstVisiblePosition + 1;
		final int totalItemCount = mListView.getCount() - mListView.getFooterViewsCount();

		if(visibleItemCount < totalItemCount)
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean onListViewTopAndPullDown(int delta)
	{
		if(mIsRefreshing || mListView.getCount() - mListView.getFooterViewsCount() == 0)
		{
			return false;
		}

		int absDelta = Math.abs(delta);
		final int i = (int) Math.ceil((double) absDelta / 2);

		mHeaderIncremental += i;
		if(mHeaderIncremental >= 0)
		{
			setHeaderHeight(mHeaderIncremental);
			checkHeaderViewState();
		}
		return true;
	}

	@Override
	public boolean onListViewBottomAndPullUp(int delta)
	{
		if(!mEnableAutoFetchMore || mIsFetchMoreing)
		{
			return false;
		}

		// 数量充满屏幕才触发
		if(isFillScreenItem())
		{
			mIsFetchMoreing = true;
			mFooterTextView.setText("加载更多中...");
			mFooterTextView.setTextColor(this.getResources().getColor(R.color.custom_grey));
			mFooterLoadingView.setVisibility(View.VISIBLE);
			mOnPullDownListener.onMore();
			return true;
		}
		return false;
	}

	@Override
	public boolean onMotionDown(MotionEvent ev)
	{
		mIsDown = true;
		mIsPullUpDone = false;
		mMotionDownLastY = ev.getRawY();
		return false;
	}
	@Override
	public boolean onMotionMove(MotionEvent ev, int delta)
	{
		// 当头部文件回推消失的时候，不允许滚动
		if(mIsPullUpDone)
		{
			return true;
		}

		// 如果开始按下到滑动距离不超过误差值，则不滑动
		final int absMotionY = (int) Math.abs(ev.getRawY() - mMotionDownLastY);
		if(absMotionY < START_PULL_DEVIATION)
		{
			return true;
		}

		final int absDelta = Math.abs(delta);
		final int i = (int) Math.ceil((double) absDelta / 2);

		// onTopDown在顶部，并上回推和onTopUp相对
		if(mHeaderViewParams.height > 0 && delta < 0)
		{
			mHeaderIncremental -= i;
			if(mHeaderIncremental > 0)
			{
				setHeaderHeight(mHeaderIncremental);
				checkHeaderViewState();
			}else
			{
				mHeaderViewState = HEADER_VIEW_STATE_IDLE;
				mHeaderIncremental = 0;
				setHeaderHeight(mHeaderIncremental);
				mIsPullUpDone = true;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean onMotionUp(MotionEvent ev)
	{
		mIsDown = false;
		
		// 避免和点击事件冲突
		if(mHeaderViewParams.height > 0)
		{
			// 判断头文件拉动的距离与设定的高度，小了就隐藏，多了就固定高度
			int x = mHeaderIncremental - DEFAULT_HEADER_VIEW_HEIGHT;
			Timer timer = new Timer(true);
			if(x < 0)
			{
				timer.scheduleAtFixedRate(new HideHeaderViewTask(), 0, 10);
			}else
			{
				timer.scheduleAtFixedRate(new ShowHeaderViewTask(), 0, 10);
			}
			return true;
		}
		
		return false;
	}

}
