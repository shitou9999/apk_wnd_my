package com.greentech.wnd.android;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 病害预警，通知公告，惠农政策详情
 * @author WP
 *
 */
public class NoticeDetailActivity extends BaseActivity {
	
	private WebView content;
	private TextView type;
	private TextView title;
	private TextView publisher;
	private ImageView back;//返回按钮
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		// 设置布局
		setContentView(R.layout.notice_detail);
		back=(ImageView)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				NoticeDetailActivity.this.finish();
			}
		});
		init();
		
    }
    
    private void init() {
    	content = (WebView) this.findViewById(R.id.content);
    	type = (TextView) this.findViewById(R.id.type);
    	title = (TextView) this.findViewById(R.id.title);
    	publisher = (TextView) this.findViewById(R.id.publisher);
    	showReportDetail(this.getIntent().getExtras().getString("content"));
    	title.setText(this.getIntent().getExtras().getString("title"));
    	publisher.setText(this.getIntent().getExtras().getString("publisher"));
    	String t = this.getNoticeType(this.getIntent().getExtras().getString("type"));
    	type.setText(t);
    }
    
    private void showReportDetail(String c) {
		//webView.setInitialScale(100);
		WebSettings webSetting = content.getSettings();
		webSetting.setJavaScriptEnabled(true);
        //设置可以支持缩放   
        webSetting.setSupportZoom(true);   
        //webSetting.setUseWideViewPort(true);
        //设置默认缩放方式尺寸是far   
        //webSetting.setDefaultZoom(WebSettings.ZoomDensity.FAR);  
        //设置出现缩放工具   
        webSetting.setBuiltInZoomControls(true);
        // 让网页自适应屏幕宽度
        //webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        content.loadDataWithBaseURL(null,c, "text/html","utf-8", null);
	}
    
    public String getNoticeType(String typeId){
		if(typeId.equals("1")){
			return "病害预警";
		}else if(typeId.equals("2")){
			return "惠农政策";
		}else {
			return "通知公告";
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
			NoticeDetailActivity.this.finish();
			return true;
		} else {
			return false;
		}
	}

}
