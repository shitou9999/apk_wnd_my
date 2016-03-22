package com.greentech.wnd.android;


import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.greentech.wnd.android.bean.Notice;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.NetUtil;
import com.greentech.wnd.android.view.scrollview.OnPullDownListener;
import com.greentech.wnd.android.view.scrollview.ScrollListView;

public class NoticeListActivity extends BaseActivity implements OnPullDownListener,OnClickListener{
	private List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
	private ListView listView;
	private SimpleAdapter simpleAdapter;
	private ScrollListView scrollListView;
	private MyHandler myHandler;
	private Integer pageNum=1;
	private static final int WHAT_DID_LOAD_DATA = 0;
	private static final int WHAT_DID_REFRESH = 1;
	private static final int WHAT_DID_MORE = 2;
	private Integer position;
	private Button back;
	private List<Notice> notices;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	private Integer type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		// 设置布局
		setContentView(R.layout.list_release);
		type = 3;
		iniGsyw();
    }

    private void iniGsyw() {
    	back=(Button)findViewById(R.id.back);
    	back.setOnClickListener(this);
    	
    	scrollListView = (ScrollListView) this.findViewById(R.id.gsyw_list);
    	simpleAdapter = new SimpleAdapter(NoticeListActivity.this,list,R.layout.main_list_item,new String[]{"title"},new int[]{R.id.item_title});
    	scrollListView.setOnPullDownListener(this);
    	listView = scrollListView.getListView();
    	listView.setAdapter(simpleAdapter);
    	myHandler = new MyHandler(NoticeListActivity.this);
    	scrollListView.enableAutoFetchMore(true,1);
		listView.setSmoothScrollbarEnabled(true);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Map m = list.get(position);
				Intent intent = new Intent();
				intent.setClass(NoticeListActivity.this,NoticeDetailActivity.class);
				intent.putExtra("title",m.get("title").toString());
				intent.putExtra("publisher",m.get("publisher").toString());
				intent.putExtra("content",m.get("content").toString());
				intent.putExtra("type",m.get("type").toString());
				startActivity(intent);
			}
		});
		loadGsywList();
	}
    
    public void loadGsywList(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Map<String, Object> map = new HashMap<String,Object>();
					map.put("pageNum",pageNum);
					map.put("notice.type",type);
					InputStream is = NetUtil.post(Constant.SERVIER_PATH+"/json/showNoticeInfo.action",map);
					String json = NetUtil.getStringFromInputStream(is);
					JsonObject jsonObj = (JsonObject)GsonUtil.parse(json);
					notices = GsonUtil.fromJson(jsonObj.get("pageData"), new TypeToken<List<Notice>>(){}.getType());
					Message msg = myHandler.obtainMessage(WHAT_DID_LOAD_DATA);
					msg.obj = notices;
					msg.sendToTarget();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	@Override
	public void onRefresh() {
		pageNum = 1;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("pageNum",pageNum);
					map.put("notice.type",type);
					InputStream is = NetUtil.post(Constant.SERVIER_PATH+"/json/showNoticeInfo.action",map);
					String json = NetUtil.getStringFromInputStream(is);
					JsonObject jsonObj = (JsonObject)GsonUtil.parse(json);
					notices = GsonUtil.fromJson(jsonObj.get("pageData"), new TypeToken<List<Notice>>(){}.getType());
					Message msg = myHandler.obtainMessage(WHAT_DID_REFRESH);
					msg.obj = notices;
					msg.sendToTarget();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}).start();		
	}

	
	
	@Override
	public void onMore() {
		pageNum++;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Map map = new HashMap<String,Object>();
					map.put("pageNum",pageNum);
					map.put("notice.type",type);
					InputStream is = NetUtil.post(Constant.SERVIER_PATH+"/json/showNoticeInfo.action",map);
					String json = NetUtil.getStringFromInputStream(is);
					JsonObject jsonObj = (JsonObject)GsonUtil.parse(json);
					notices = GsonUtil.fromJson(jsonObj.get("pageData"), new TypeToken<List<Notice>>(){}.getType());
					Message msg = myHandler.obtainMessage(WHAT_DID_MORE);
					msg.obj = notices;
					msg.sendToTarget();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}).start();
		
	}
	
	class MyHandler extends Handler{
		WeakReference<NoticeListActivity> aActivity;
		
		MyHandler(NoticeListActivity activity){
			aActivity = new WeakReference<NoticeListActivity>(activity);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			NoticeListActivity theActivity = aActivity.get();
			switch(msg.what){
				case WHAT_DID_REFRESH:
					if(null != msg.obj){
						list.clear();
						notices = (List<Notice>) msg.obj;
						for(Notice notice : notices){
							Map m = new HashMap<String,Object>();
							String title = notice.getTitle();
							m.put("title",title);
							m.put("publisher",notice.getPublisher());
							m.put("type",notice.getType());
							m.put("content",notice.getContent());
							list.add(m);
						}
						simpleAdapter.notifyDataSetChanged();
					}
					theActivity.scrollListView.notifyDidRefresh();
					break;
				case WHAT_DID_MORE:
					if(null != msg.obj){
						notices = (List<Notice>) msg.obj;
						for(Notice notice : notices){
							Map m = new HashMap<String,Object>();
							String title = notice.getTitle();
							m.put("title",title);
							m.put("publisher",notice.getPublisher());
							m.put("type",notice.getType());
							m.put("content",notice.getContent());
							list.add(m);
						}
						simpleAdapter.notifyDataSetChanged();
					}
					scrollListView.notifyDidMore();
					break;
				case WHAT_DID_LOAD_DATA:
					if(null != msg.obj){
						notices = (List<Notice>) msg.obj;
						for(Notice notice : notices){
							Map m = new HashMap<String,Object>();
							String title = notice.getTitle();
							m.put("title",title);
							m.put("publisher",notice.getPublisher());
							m.put("type",notice.getType());
							m.put("content",notice.getContent());
							list.add(m);
						}
						simpleAdapter.notifyDataSetChanged();
					}
					theActivity.scrollListView.notifyDidLoad();
					break;
			}
			if(null != position){
				listView.smoothScrollToPosition(position);
				position = null;
			}
		}
	}
	
	/**
	 * 自定义菜单选项
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
//		menu.add(0, 0, 0, this.getString(R.string.str_logout));
//		menu.add(0, 1, 1, this.getString(R.string.str_exit));
//		menu.add(0, 0, 0, this.getString(R.string.str_exit));
		
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * 定义菜单选项选中事件
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
//			case 0:
//				controller.logout();
//				break;
//			case 1:
//				controller.exit();
			case 0:
//				controller.exit();
		}

		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * 按键点击事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			NoticeListActivity.this.finish();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onClick(View v) {
		this.finish();
		
	}

}
