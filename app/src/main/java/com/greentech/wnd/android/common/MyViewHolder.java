/**
 * 
 */
package com.greentech.wnd.android.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 公共类，是供ListView等保存列表每一行中各元素用，
 * @author wlj
 *
 */
public class MyViewHolder {
	
	private Map<String, Object> viewMap = new HashMap<String, Object>();

	/**
	 * 
	 */
	public MyViewHolder() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 设置View对象
	 * @param key
	 * @param value
	 */
	public void addView(String key, Object value) {
		viewMap.put(key, value);
	}

	/**
	 * 根据key获得保存住的View对象
	 * @param key
	 * @return
	 */
	public Object getView(String key) {
		return viewMap.get(key);
	}
	
}
