package com.greentech.wnd.android;


import android.R.color;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.greentech.wnd.android.bean.AgriProduct;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.NetUtil;
import com.greentech.wnd.android.util.Utils;
import com.greentech.wnd.android.view.popMenu.PopMenu;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 发布病害预警，通知公告，惠农政策详情
 * @author WP
 *
 */
public class AddNoticeActivity extends BaseActivity{
	
	final String mimeType = "text/html";  
	final String encoding = "utf-8"; 
	private LinearLayout menu;
	private ImageView down_img;
	private TextView menu_text;
	private PopMenu popMenu;
	private Context context;
	private ImageView back;
	@Bind(R.id.addr)
	private TextView title;
	private TextView content;
	private TextView target;
	private Button btn_publish;
	private final int MUTI_CHOICE_DIALOG = 1;
	private List<AgriProduct> products;
	
	boolean[] selected = null;
    String[] productItemds = null;
    Integer[] productIds = null;
    String selectedIds = "";
	private Integer typeId;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		// 设置布局
		setContentView(R.layout.add_notice);
		initView();
    }
    
    public void initView(){
    	title = (TextView) findViewById(R.id.title);
    	target = (TextView) findViewById(R.id.target);
    	content = (TextView) findViewById(R.id.content);
    	btn_publish = (Button) findViewById(R.id.btn_publish);
    	menu = (LinearLayout) findViewById(R.id.menu);
    	menu_text = (TextView) findViewById(R.id.menu_text);
    	down_img = (ImageView) findViewById(R.id.down_img);
		
		//加载下拉菜单
		context = AddNoticeActivity.this;
		popMenu = new PopMenu(context);
		popMenu.addItems(this.getResources().getStringArray(R.array.menutions_down_menu));
		// 菜单项点击监听器
		popMenu.setOnItemClickListener(popmenuItemClickListener);
		back = (ImageView) findViewById(R.id.back);
		menu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popMenu.showAsDropDown(v);
			}
		});
		target.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							if(null!=Constant.DATA_APP.get("products")){
								products = (List<AgriProduct>) Constant.DATA_APP.get("products");
							}else{
								HashMap<String,Object> map = new HashMap<String,Object>();
								InputStream input = NetUtil.post(Constant.SERVIER_PATH+"/json/showAgriProducts.action", map);
								String str = NetUtil.getStringFromInputStream(input);
								JsonArray jsonArray = (JsonArray)GsonUtil.parse(str);
				   				products = GsonUtil.fromJson(jsonArray, new TypeToken<List<AgriProduct>>(){}.getType());
								Constant.DATA_APP.put("products",products);
							}
							handler.post(new Runnable() {
								
								@Override
								public void run() {
									AddNoticeActivity.this.showDialog(MUTI_CHOICE_DIALOG);
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AddNoticeActivity.this.finish();
			}
		});
		btn_publish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String title = AddNoticeActivity.this.title.getText().toString();
				final String type = menu_text.getText().toString();
				final String content = AddNoticeActivity.this.content.getText().toString();
				if(title.equals("")){
					AddNoticeActivity.this.toastShow("请输入标题!");
					return;
				}
				if(type.equals("选择类型")){
					AddNoticeActivity.this.toastShow("请选择信息类型!");
					return;
				}
				if(content.equals("")){
					AddNoticeActivity.this.toastShow("请输入内容!");
					return;
				}
				ButterKnife.bind(AddNoticeActivity.this);
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							HashMap<String,Object> map = new HashMap<String,Object>();
							map.put("notice.title",title);
							if(type.equals("病害预警")){
								map.put("notice.type",1);
							}else if(type.equals("惠农政策")){
								map.put("notice.type",2);
							}else{
								map.put("notice.type",3);
							}
							map.put("notice.target",selectedIds);
							map.put("notice.content",content);
							map.put("notice.publisher","admin");//后期修改
							map.put("notice.publisherId",1);//后期修改
							InputStream input = NetUtil.post(Constant.SERVIER_PATH+"/json/addNotice.action", map);
							String json = NetUtil.getStringFromInputStream(input);
							JsonObject jsonObj = (JsonObject)GsonUtil.parse(json);
							String status = jsonObj.get("status").getAsString();
							if(status.equals("success")){
								handler.post(new Runnable() {
									
									@Override
									public void run() {
										AddNoticeActivity.this.toastShow("信息发布成功!");
										Intent intent = new Intent();
										intent.setClass(AddNoticeActivity.this,NoticeListActivity.class);
										startActivity(intent);
										AddNoticeActivity.this.finish();
									}
								});
							}else if(status.equals("error")){
								handler.post(new Runnable() {
									
									@Override
									public void run() {
										AddNoticeActivity.this.toastShow("信息发布失败,请稍后再试!");
									}
								});
							}
						} catch (Exception e) {
							e.printStackTrace();
							final String message = e.getMessage();
							handler.post(new Runnable() {
								@Override
								public void run() {
									AddNoticeActivity.this.toastShow(message);
								}
							});
						}
					}
				}){}.start();
			}
		});
    }
    
    
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	Dialog dialog = null;
    	switch(id) {
    		case MUTI_CHOICE_DIALOG:
    			selected = new boolean[products.size()];
    			productItemds = new String[products.size()];
    			productIds = new Integer[products.size()];
    			for(int i=0;i<products.size();i++){
    				productItemds[i] = products.get(i).getName();
    				productIds[i] = products.get(i).getId();
    				selected[i] = false;
    			}
    			Builder builder = new AlertDialog.Builder(this);
    			builder.setTitle("选择发布对象");
    			DialogInterface.OnMultiChoiceClickListener mutiListener = 
    				new DialogInterface.OnMultiChoiceClickListener() {
						
						@Override
						public void onClick(DialogInterface dialogInterface, 
								int which, boolean isChecked) {
							selected[which] = isChecked;
						}
					};
    			builder.setMultiChoiceItems(productItemds, selected, mutiListener);
    			DialogInterface.OnClickListener btnListener = 
    				new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int which) {
							String selectedStr = "";
							selectedIds = "";
							for(int i=0; i<selected.length; i++) {
								if(selected[i] == true) {
									if(i==0){
										selectedStr = productItemds[i];
										selectedIds = productIds[i].toString();
									}else{
										selectedStr = selectedStr + " " +
										productItemds[i];
										selectedIds = selectedIds + Constant.BREAK +productIds[i];
									}
								}
							}
							
							target.setText(selectedStr);
						}
					};
    			builder.setPositiveButton("确定", btnListener);
    			dialog = builder.create();
    			break;
    	}
    	return dialog;
    }
    
 // 弹出菜单监听器
  	OnItemClickListener popmenuItemClickListener = new OnItemClickListener() {
  		@Override
  		public void onItemClick(AdapterView<?> parent, View view, int position,
  				long id) {
  			String type = parent.getItemAtPosition(position).toString();
  			popMenu.dismiss();
  			int count = popMenu.getListView().getCount();
  			for(int i =0;i<count;i++){
  				View v = (View) popMenu.getListView().getChildAt(i);
  				v.setBackgroundColor(Color.GRAY);
  				TextView tv = (TextView) v.findViewById(R.id.textView);
  				tv.setTextColor(getResources().getColor(color.white));
  			}
  			TextView tv = (TextView) view.findViewById(R.id.textView);
  			menu_text.setText(type);
  		}
  	};
    
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
			AddNoticeActivity.this.finish();
			return true;
		} else {
			return false;
		}
	}

}
