package com.greentech.wnd.android;







import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.greentech.wnd.android.bean.AgriProduct;
import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.GsonUtil;
import com.greentech.wnd.android.util.NetUtil;
import com.greentech.wnd.android.util.UserInfo;
import com.greentech.wnd.android.view.popMenu.PopMenu;
/**
 * 发布使用技术信息
 * @author WP
 *
 */
public class AddTechActivity extends BaseActivity{
	
	final String mimeType = "text/html";  
	final String encoding = "utf-8"; 
	private LinearLayout menu;
	private ImageView down_img;
	private TextView menu_text;
	private PopMenu popMenu;
	private Context context;
	private ImageView back;
	private EditText title;
	private TextView content;
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
		setContentView(R.layout.add_tech);
		initView();
    }
    
    public void initView(){
    	title = (EditText) findViewById(R.id.title);
    	content = (TextView) findViewById(R.id.content);
    	btn_publish = (Button) findViewById(R.id.btn_publish);
    	menu = (LinearLayout) findViewById(R.id.menu);
    	menu_text = (TextView) findViewById(R.id.menu_text);
    	down_img = (ImageView) findViewById(R.id.down_img);
		
		//加载下拉菜单
		context = AddTechActivity.this;
		popMenu = new PopMenu(context);
		loadAgriPrudcts();
		// 菜单项点击监听器
		popMenu.setOnItemClickListener(popmenuItemClickListener);
		back = (ImageView) findViewById(R.id.back);
		menu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popMenu.showAsDropDown(v);
			}
		});
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AddTechActivity.this.finish();
			}
		});
		btn_publish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String title = AddTechActivity.this.title.getText().toString();
				final String type = menu_text.getText().toString();
				final String content = AddTechActivity.this.content.getText().toString();
				if(title.equals("")){
					AddTechActivity.this.toastShow("请输入标题!");
					return;
				}
				if(type.equals("选择类型")){
					AddTechActivity.this.toastShow("请选择技术类型!");
					return;
				}
				if(content.equals("")){
					AddTechActivity.this.toastShow("请输入内容!");
					return;
				}
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							HashMap<String,Object> map = new HashMap<String,Object>();
							map.put("vegetableTech.title",title);
							map.put("vegetableTech.type",type);
							map.put("vegetableTech.content",content);
							map.put("vegetableTech.source","admin");//后期修改
							map.put("vegetableTech.publisherId",1);//后期修改
							map.put("vegetableTech.userName","admin");//后期修改
							map.put("vegetableTech.userId",UserInfo.getUserId(AddTechActivity.this));
							map.put("vegetableTech.status",0);//后期修改
							InputStream input = NetUtil.post(Constant.SERVIER_PATH+"/json/addVegetableTech.action", map);
							String json = NetUtil.getStringFromInputStream(input);
							JSONObject jsonObj = new JSONObject(json);
							String status = jsonObj.getString("status");
							if(status.equals("success")){
								handler.post(new Runnable() {
									
									@Override
									public void run() {
										AddTechActivity.this.toastShow("信息发布成功!");
										Intent intent = new Intent();
										intent.setClass(AddTechActivity.this,TechListActivity.class);
										startActivity(intent);
										AddTechActivity.this.finish();
									}
								});
							}else if(status.equals("error")){
								handler.post(new Runnable() {
									
									@Override
									public void run() {
										AddTechActivity.this.toastShow("信息发布失败,请稍后再试!");
									}
								});
							}
						} catch (JSONException e) {
							e.printStackTrace();
							final String message = e.getMessage();
							handler.post(new Runnable() {
								@Override
								public void run() {
									AddTechActivity.this.toastShow(message);
								}
							});
						}
					}
				}){}.start();
			}
		});
    }
    
    
    
    private void loadAgriPrudcts() {
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
							productItemds = new String[products.size()];
							for(int i = 0;i<products.size();i++){
								productItemds[i] = products.get(i).getName();
							}
							popMenu.addItems(productItemds);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

    
 // 弹出菜单监听器
  	OnItemClickListener popmenuItemClickListener = new OnItemClickListener() {
  		@Override
  		public void onItemClick(AdapterView<?> parent, View view, int position,
  				long id) {
  			String type = parent.getItemAtPosition(position).toString();
  			popMenu.dismiss();
  			menu_text.setText(type);
  		}
  	};
    

	
	/**
	 * 按键点击事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AddTechActivity.this.finish();
			return true;
		} else {
			return false;
		}
	}

}
