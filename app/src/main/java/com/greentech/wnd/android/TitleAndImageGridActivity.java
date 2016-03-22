package com.greentech.wnd.android;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.greentech.wnd.android.adapter.ListViewAdapter;
import com.greentech.wnd.android.bean.AgriProduct;
import com.greentech.wnd.android.bean.Disease;
import com.greentech.wnd.android.cache.ImageLoader;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.NetUtil;
import com.greentech.wnd.android.view.AutoListView;
import com.greentech.wnd.android.view.AutoListView.OnLoadListener;
import com.greentech.wnd.android.view.AutoListView.OnRefreshListener;
import com.greentech.wnd.android.view.popMenu.PopMenu;

public class TitleAndImageGridActivity extends BaseActivity implements
		OnRefreshListener, OnLoadListener {
	private AutoListView lstv;
	private Button mSubmit;
	private EditText find;
	private boolean isSearch;// 是否手动搜索
	private ListViewAdapter adapter;
	private ImageLoader imageLoader;
	private List<String> list_url = new ArrayList<String>();
	private List<String> list_title = new ArrayList<String>();
	private List<String> list_content = new ArrayList<String>();
	private List<Integer> list_id = new ArrayList<Integer>();
	private List<Disease> list_disease;
	private Integer lastProductId = 1;// 记录最后一次点击的productid,用来传递给onRefresh()方法
	private Integer pageNum = 1;
	private int id;
	private String search;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_release_all);
		id = getIntent().getIntExtra("id", 0);
		search = getIntent().getStringExtra("search");
		mSubmit = (Button) findViewById(R.id.submit);
		find = (EditText) findViewById(R.id.disease_find);

		lstv = (AutoListView) findViewById(R.id.gsyw_list);
		adapter = new ListViewAdapter(this, list_url, list_title,list_content,list_id);
		imageLoader = adapter.getImageLoader();
		lstv.setAdapter(adapter);
		lstv.setOnRefreshListener(this);
		lstv.setOnLoadListener(this);
		mSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (find.getText().toString().trim().length() > 0) {
					loadData(AutoListView.REFRESH, id, 1, find
							.getText().toString());
					isSearch = true;
				}
			}
		});

		lstv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if ((position - 1) != adapter.getCount()) {
					Intent intent = new Intent(TitleAndImageGridActivity.this,
							DiseaseInfoMainActivity.class);
					intent.putExtra("title", list_title.get(position - 1));
					intent.putExtra("content", list_content.get(position - 1));
					intent.putExtra("imagePath", list_url.get(position - 1));
					intent.putExtra("id", list_id.get(position - 1));
					startActivity(intent);

				}

			}
		});

		initData();
	}

	private void initData() {
		if (StringUtils.isBlank(search)) {
			loadData(AutoListView.REFRESH, id, 1, null);
		} else {
			loadData(AutoListView.REFRESH, 0, 1, search);
		}

	}

	private void loadData(final int what, final int agriProductId,
			final int pageNum, final String find) {
		final List<String> list = new ArrayList<String>();
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					InputStream is = NetUtil
							.post(Constant.SERVIER_PATH
									+ "/json/findTitleAndContentByProductIdOnPage.action",
									"&agriProductId=" + agriProductId
											+ "&pageNum=" + pageNum
											+ "&search=" + find);
					final String str = NetUtil.getStringFromInputStream(is);
					JsonObject json = (JsonObject) GsonUtil.parse(str);
					list_disease = GsonUtil.fromJson(json.get("pageData"),
							new TypeToken<List<Disease>>() {
							}.getType());
					if (StringUtils.isNotBlank(find)) {
						list_url.clear();
						list_content.clear();
						list_id.clear();
						list.clear();
					}
					for (int i = 0; i < list_disease.size(); i++) {
						list.add(Html.fromHtml(list_disease.get(i).getTitle())
								.toString());
						list_content.add(list_disease.get(i).getContent()
								.toString());
						list_url.add(list_disease.get(i).getImg());
						list_id.add(list_disease.get(i).getId());
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					Message msg = handler.obtainMessage();
					msg.what = what;
					msg.obj = list;
					handler.sendMessage(msg);
				}

			}
		}).start();

	}

	@Override
	public void onLoad() {
		++pageNum;
		// 判断是否手动搜索的结果,如果是手动搜索就不需要ProductId
		if (StringUtils.isNotBlank(search)) {
			loadData(AutoListView.LOAD, 0, pageNum, null);
		} else {
			loadData(AutoListView.LOAD, id, pageNum, null);
		}

	}

	@Override
	public void onRefresh() {
		pageNum = 1;

		if (imageLoader != null) {
			imageLoader.clearCache();
			// list_url.clear();
		}
		loadData(AutoListView.REFRESH, id, pageNum, null);

	}

	@Override
	protected void onDestroy() {

		if (imageLoader != null) {
			list_url.clear();
			imageLoader.clearCache();
		}

		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onDestroy();
			TitleAndImageGridActivity.this.finish();
			return true;
		} else {
			return false;
		}

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			@SuppressWarnings("unchecked")
			List<String> result = (List<String>) msg.obj;
			switch (msg.what) {
			case AutoListView.REFRESH:
				lstv.onRefreshComplete();
				list_title.clear();
				list_title.addAll(result);
				break;
			case AutoListView.LOAD:
				lstv.onLoadComplete();
				list_title.addAll(result);
				break;
			}
			lstv.setResultSize(result.size());
			adapter.notifyDataSetChanged();
		};
	};

}
