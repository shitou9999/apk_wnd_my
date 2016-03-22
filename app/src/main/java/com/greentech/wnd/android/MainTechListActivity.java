package com.greentech.wnd.android;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.greentech.wnd.android.bean.AgriProduct;
import com.greentech.wnd.android.bean.VegetableTech;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.CustomDialog;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.NetUtil;
import com.greentech.wnd.android.view.AutoListView;
import com.greentech.wnd.android.view.popMenu.PopMenu;
import com.greentech.wnd.android.view.scrollview.OnPullDownListener;
import com.greentech.wnd.android.view.scrollview.ScrollListView;

public class MainTechListActivity extends BaseActivity implements
		OnPullDownListener, OnClickListener {
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	private ListView listView;
	private SimpleAdapter simpleAdapter;
	private ScrollListView scrollListView;
	private MyHandler myHandler;
	private Integer pageNum = 1;
	private static final int WHAT_DID_LOAD_DATA = 0;
	private static final int WHAT_DID_REFRESH = 1;
	private static final int WHAT_DID_MORE = 2;
	private Integer position;
	private List<VegetableTech> vegetableTechs;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	private PopMenu mPopMenu;
	private TextView choice;
	private String type;
	private Dialog mDialog;
	private EditText find;
	private Button mSubmit;
	private boolean isSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_list_release);
		mSubmit = (Button) findViewById(R.id.submit);
		find = (EditText) findViewById(R.id.tech_find);
		mSubmit.setOnClickListener(this);
		init();
	}

	private void init() {
		scrollListView = (ScrollListView) this.findViewById(R.id.gsyw_list);
		choice = (TextView) findViewById(R.id.choice);
		simpleAdapter = new SimpleAdapter(MainTechListActivity.this, list,
				R.layout.main_list_item, new String[] { "title" },
				new int[] { R.id.item_title });
		scrollListView.setOnPullDownListener(this);
		listView = scrollListView.getListView();
		listView.setAdapter(simpleAdapter);
		myHandler = new MyHandler(MainTechListActivity.this);
		scrollListView.enableAutoFetchMore(true, 1);
		listView.setSmoothScrollbarEnabled(true);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if ((position+1) == list.size()) {
				} else {
					Map<?, ?> m = list.get(position);
					Intent intent = new Intent();
					intent.setClass(MainTechListActivity.this,
							TechDetailActivity.class);
					intent.putExtra("title", m.get("title").toString());
					intent.putExtra("content", m.get("content").toString());
					intent.putExtra("type", m.get("type").toString());
					intent.putExtra("source", m.get("source").toString());
					intent.putExtra("releaseTime",
							format.format(m.get("releaseTime")));
					intent.putExtra("id", m.get("id").toString());
					startActivity(intent);
				}

			}
		});

		mPopMenu = new PopMenu(MainTechListActivity.this);
		// 菜单项点击监听器
		mPopMenu.setOnItemClickListener(popClickListener);

		// 点击choice的时候显示popmenu也就是PopupWindow
		choice.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				List<AgriProduct> agriProductList = SelectedDiseaseFragment
						.getmAgriProductList();
				String[] productName = new String[agriProductList.size()];
				for (int i = 0; i < agriProductList.size(); i++) {
					productName[i] = agriProductList.get(i).getName();
				}
				// 为popmenu加载数据
				mPopMenu.addItems(productName);
				mPopMenu.showAsDropDown(v);
			}
		});
		loadGsywList(WHAT_DID_LOAD_DATA, 1, null);
	}

	public void loadGsywList(final int what, final int pageNum,
			final String search) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// Map<String, Object> map = new HashMap<String, Object>();
					// map.put("pageNum", pageNum);
					// map.put("search", search);
					// 查询指定id的数据
					// map.put("vegetableTech.userId",UserInfo.getUserId(MainTechListActivity.this));
					InputStream is = NetUtil.post(Constant.SERVIER_PATH
							+ "/json/showVegetableTechInfo.action", "&pageNum="
							+ pageNum + "&search=" + search);
					String json = NetUtil.getStringFromInputStream(is);
					JsonObject jsonObj = (JsonObject) GsonUtil.parse(json);
					vegetableTechs = GsonUtil.fromJson(jsonObj.get("pageData"),
							new TypeToken<List<VegetableTech>>() {
							}.getType());
					Message msg = myHandler.obtainMessage(what);
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
		loadGsywList(WHAT_DID_REFRESH, pageNum, null);

	}

	@Override
	public void onMore() {
		pageNum++;
		if (isSearch) {
			loadGsywList(WHAT_DID_MORE, pageNum, find.getText().toString()
					.trim());
		} else {
			loadGsywList(WHAT_DID_MORE, pageNum, null);
		}

	}

	@Override
	public void onClick(View v) {
		if (find.getText().toString().trim().length() > 0) {
			loadGsywList(WHAT_DID_REFRESH, 1, find.getText().toString());
			isSearch = true;
		}

	}

	class MyHandler extends Handler {
		WeakReference<MainTechListActivity> aActivity;

		MyHandler(MainTechListActivity activity) {
			aActivity = new WeakReference<MainTechListActivity>(activity);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			MainTechListActivity theActivity = aActivity.get();
			switch (msg.what) {
			case WHAT_DID_REFRESH:
				if (null != msg.obj) {
					list.clear();
					vegetableTechs = (List<VegetableTech>) msg.obj;
					for (VegetableTech vegetableTech : vegetableTechs) {
						Map<String, Object> m = new HashMap<String, Object>();
						String title = vegetableTech.getTitle();
						m.put("title", title);
						m.put("source", vegetableTech.getSource());
						m.put("type", vegetableTech.getType());
						m.put("content", vegetableTech.getContent());
						m.put("releaseTime", vegetableTech.getReleaseTime());
						m.put("userId", vegetableTech.getUserId());
						m.put("id", vegetableTech.getId());
						list.add(m);
					}
					simpleAdapter.notifyDataSetChanged();
				}
				theActivity.scrollListView.notifyDidRefresh();
				break;
			case WHAT_DID_MORE:
				if (null != msg.obj) {
					vegetableTechs = (List<VegetableTech>) msg.obj;
					for (VegetableTech vegetableTech : vegetableTechs) {
						Map<String, Object> m = new HashMap<String, Object>();
						String title = vegetableTech.getTitle();
						m.put("title", title);
						m.put("source", vegetableTech.getSource());
						m.put("type", vegetableTech.getType());
						m.put("content", vegetableTech.getContent());
						m.put("releaseTime", vegetableTech.getReleaseTime());
						m.put("userId", vegetableTech.getUserId());
						m.put("id", vegetableTech.getId());
						list.add(m);
					}
					simpleAdapter.notifyDataSetChanged();
				}
				scrollListView.notifyDidMore();
				break;
			case WHAT_DID_LOAD_DATA:
				if (null != msg.obj) {
					vegetableTechs = (List<VegetableTech>) msg.obj;
					for (VegetableTech vegetableTech : vegetableTechs) {
						Map<String, Object> m = new HashMap<String, Object>();
						String title = vegetableTech.getTitle();
						m.put("title", title);
						m.put("source", vegetableTech.getSource());
						m.put("type", vegetableTech.getType());
						m.put("content", vegetableTech.getContent());
						m.put("releaseTime", vegetableTech.getReleaseTime());
						m.put("userId", vegetableTech.getUserId());
						m.put("id", vegetableTech.getId());
						list.add(m);
					}
					simpleAdapter.notifyDataSetChanged();
				}
				theActivity.scrollListView.notifyDidLoad();
				break;
			}
			if (null != position) {
				listView.smoothScrollToPosition(position);
				position = null;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
			return true;
		} else {
			return false;
		}

	}

	// popmenu单机事件
	OnItemClickListener popClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			mDialog = CustomDialog.createLoadingDialog(
					MainTechListActivity.this, "数据获取中...");
			mDialog.show();
			type = parent.getItemAtPosition(position).toString();
			mPopMenu.dismiss();
			// 更新ui
			pageNum = 1;
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("pageNum", pageNum);
						map.put("vegetableTech.type", type);
						InputStream is = NetUtil.post(Constant.SERVIER_PATH
								+ "/json/showVegetableTechInfo.action", map);
						String json = NetUtil.getStringFromInputStream(is);
						JsonObject jsonObj = (JsonObject) GsonUtil.parse(json);
						vegetableTechs = GsonUtil.fromJson(
								jsonObj.get("pageData"),
								new TypeToken<List<VegetableTech>>() {
								}.getType());
						Message msg = myHandler.obtainMessage(WHAT_DID_REFRESH);
						msg.obj = vegetableTechs;
						mDialog.dismiss();
						msg.sendToTarget();
					} catch (Exception e) {
						e.printStackTrace();

					}

				}
			}).start();

		}
	};

}
