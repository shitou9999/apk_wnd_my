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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.greentech.wnd.android.bean.AgriProduct;
import com.greentech.wnd.android.bean.VegetableTech;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.NetUtil;
import com.greentech.wnd.android.util.UserInfo;
import com.greentech.wnd.android.view.popMenu.PopMenu;
import com.greentech.wnd.android.view.scrollview.OnPullDownListener;
import com.greentech.wnd.android.view.scrollview.ScrollListView;
/**
 * 实用技术
 * @author WP
 *
 */
public class TechListActivity extends BaseActivity implements OnPullDownListener,OnClickListener{
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
	private List<VegetableTech> vegetableTechs;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	private Button btnBack;
	private TextView choise;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		// 设置布局
		setContentView(R.layout.list_release);
		btnBack=(Button)findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		choise=(TextView)findViewById(R.id.product_chiose);
		choise.setOnClickListener(this);
		iniGsyw();  
    }

    private void iniGsyw() {
    	
    	
    	scrollListView = (ScrollListView) this.findViewById(R.id.gsyw_list);
    	simpleAdapter = new SimpleAdapter(TechListActivity.this,list,R.layout.main_list_item,new String[]{"title"},new int[]{R.id.item_title});
    	scrollListView.setOnPullDownListener(this);
    	listView = scrollListView.getListView();
    	listView.setAdapter(simpleAdapter);
    	myHandler = new MyHandler(TechListActivity.this);
    	scrollListView.enableAutoFetchMore(true,1);
		listView.setSmoothScrollbarEnabled(true);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Map<?, ?> m = list.get(position);
				Intent intent = new Intent();
				intent.setClass(TechListActivity.this,TechDetailActivity.class);
				intent.putExtra("id",m.get("id").toString());
				intent.putExtra("title",m.get("title").toString());
				intent.putExtra("content",m.get("content").toString());
				intent.putExtra("type",m.get("type").toString());
				intent.putExtra("source",m.get("source").toString());
				intent.putExtra("releaseTime",format.format(m.get("releaseTime")));
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
					//查询指定id的数据
					map.put("vegetableTech.userId",UserInfo.getUserId(TechListActivity.this));
					InputStream is = NetUtil.post(Constant.SERVIER_PATH+"/json/showVegetableTechInfo.action",map);
					String json = NetUtil.getStringFromInputStream(is);
					JsonObject jsonObj = (JsonObject)GsonUtil.parse(json);
					vegetableTechs = GsonUtil.fromJson(jsonObj.get("pageData"), new TypeToken<List<VegetableTech>>(){}.getType());
					
					Message msg = myHandler.obtainMessage(WHAT_DID_LOAD_DATA);
					msg.obj = vegetableTechs;
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
					Map<String, Object> map = new HashMap<String,Object>();
					map.put("pageNum",pageNum);
					map.put("vegetableTech.userId",UserInfo.getUserId(TechListActivity.this));
					InputStream is = NetUtil.post(Constant.SERVIER_PATH+"/json/showVegetableTechInfo.action",map);
					String json = NetUtil.getStringFromInputStream(is);
					JsonObject jsonObj = (JsonObject)GsonUtil.parse(json);
					vegetableTechs = GsonUtil.fromJson(jsonObj.get("pageData"), new TypeToken<List<VegetableTech>>(){}.getType());
					Message msg = myHandler.obtainMessage(WHAT_DID_REFRESH);
					msg.obj = vegetableTechs;
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
					Map<String, Object> map = new HashMap<String,Object>();
					map.put("pageNum",pageNum);
					map.put("vegetableTech.userId",UserInfo.getUserId(TechListActivity.this));
					InputStream is = NetUtil.post(Constant.SERVIER_PATH+"/json/showVegetableTechInfo.action",map);
					String json = NetUtil.getStringFromInputStream(is);
					JsonObject jsonObj = (JsonObject)GsonUtil.parse(json);
					vegetableTechs = GsonUtil.fromJson(jsonObj.get("pageData"), new TypeToken<List<VegetableTech>>(){}.getType());
					Message msg = myHandler.obtainMessage(WHAT_DID_MORE);
					msg.obj = vegetableTechs;
					msg.sendToTarget();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}).start();
		
	}
	
	class MyHandler extends Handler{
		WeakReference<TechListActivity> aActivity;
		
		MyHandler(TechListActivity activity){
			aActivity = new WeakReference<TechListActivity>(activity);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			TechListActivity theActivity = aActivity.get();
			switch(msg.what){
				case WHAT_DID_REFRESH:
					if(null != msg.obj){
						list.clear();
						vegetableTechs = (List<VegetableTech>) msg.obj;
						for(VegetableTech vegetableTech : vegetableTechs){
							Map<String, Object> m = new HashMap<String,Object>();
							String title = vegetableTech.getTitle();
							m.put("title",title);
							m.put("source",vegetableTech.getSource());
							m.put("type",vegetableTech.getType());
							m.put("content",vegetableTech.getContent());
							m.put("releaseTime",vegetableTech.getReleaseTime());
							m.put("userId",vegetableTech.getUserId());
							m.put("id", vegetableTech.getId());
							list.add(m);
						}
						simpleAdapter.notifyDataSetChanged();
					}
					theActivity.scrollListView.notifyDidRefresh();
					break;
				case WHAT_DID_MORE:
					if(null != msg.obj){
						vegetableTechs = (List<VegetableTech>) msg.obj;
						for(VegetableTech vegetableTech : vegetableTechs){
							Map<String, Object> m = new HashMap<String,Object>();
							String title = vegetableTech.getTitle();
							m.put("title",title);
							m.put("source",vegetableTech.getSource());
							m.put("type",vegetableTech.getType());
							m.put("content",vegetableTech.getContent());
							m.put("releaseTime",vegetableTech.getReleaseTime());
							m.put("userId",vegetableTech.getUserId());
							m.put("id", vegetableTech.getId());
							
							list.add(m);
						}
						simpleAdapter.notifyDataSetChanged();
					}
					scrollListView.notifyDidMore();
					break;
				case WHAT_DID_LOAD_DATA:
					if(null != msg.obj){
						vegetableTechs = (List<VegetableTech>) msg.obj;
						for(VegetableTech vegetableTech : vegetableTechs){
							Map<String, Object> m = new HashMap<String,Object>();
							String title = vegetableTech.getTitle();
							m.put("title",title);
							m.put("source",vegetableTech.getSource());
							m.put("type",vegetableTech.getType());
							m.put("content",vegetableTech.getContent());
							m.put("releaseTime",vegetableTech.getReleaseTime());
							m.put("userId",vegetableTech.getUserId());
							m.put("id", vegetableTech.getId());
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
	 * 按键点击事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			TechListActivity.this.finish();
			return true;
		} else {
			return false;
		}
	}
/**
 * 返回按钮点击事件
 * @param v
 */
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.back:
			TechListActivity.this.finish();
			break;
		
		default:
			break;
		}
		
		
	}

}
