package com.greentech.wnd.android.view.scrollview;

/**
 * 下拉刷新事件监听器
 * @author O-J-S
 * @version 1.0
 * @since 2012-9-2
 */
public interface OnPullDownListener {
	
	// 下拉刷新
	void onRefresh();
	
	// 加载更多
	void onMore();
	
}
