package com.greentech.wnd.android;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
/**
 * 基Activity类，
 * 用于添加所有Activity公共模块，如菜单按键监听
 * @author O-J-S
 * @since 2012-08-26
 */
public class BaseActivity extends Activity {
	
	protected Handler handler = new Handler();
//	Gso	nBuilder gsonb = new GsonBuilder();
	private Toast toast;
	
	public BaseActivity() {
		super();
//		gsonb.setDateFormat("yyyy-MM-dd HH:mm:ss").registerTypeAdapter(Date.class, new DateTypeAdapter());
	}

	/**
	 * 创建菜单
	 */
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(-1, 0, 0, "查看内存");
		menu.add(-1, 1, 1,"后台运行");
		menu.add(-1, 2, 2, "退出");
		
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * 菜单点击事件
	 */
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getGroupId() == -1) {
			Intent intent = null;
			switch(item.getItemId())
			{
				case 0:
					showMem();
					break;
				case 1:
					moveTaskToBack(true);//将程序后台运行
					break;
				case 2:
					intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_HOME);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					this.startActivity(intent);
//					System.exit(0);//效果和finish()一样
					finish();
					break;
			}
		}
		return super.onOptionsItemSelected(item);//继续调用父类的此方法。
	}
	
	protected void showMem() {
		//获取android当前可用内存大小  
        ActivityManager am=(ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);  
        MemoryInfo mi=new MemoryInfo();  
        am.getMemoryInfo(mi);
        String mem_free = Formatter.formatFileSize(getBaseContext(), mi.availMem);
        
        String max = Formatter.formatFileSize(getBaseContext(), Runtime.getRuntime().maxMemory());
        String free = Formatter.formatFileSize(getBaseContext(), Runtime.getRuntime().freeMemory());
        String total = Formatter.formatFileSize(getBaseContext(), Runtime.getRuntime().totalMemory());
        
        StringBuffer sb = new StringBuffer();
        String br = "\r\n";
        sb.append("手机总内存：").append(getTotalMemory()).append(br)
			.append("手机可用内存：").append(mem_free).append(br)
			.append("max:").append(max).append(br)
			.append("free:").append(free).append(br)
			.append("total:").append(total).append(br);
		Dialog d = new AlertDialog.Builder(this)
				.setTitle("查看内存")
				.setMessage(sb.toString())
				.create();
		d.show();
	}
	private String getTotalMemory(){  
        String str1="/proc/meminfo";//系统内存信息文件  
        String str2;  
        String[] arrayOfString;  
        long initial_memory=0;  
          
        try{  
            FileReader localFileReader=new FileReader(str1);  
            BufferedReader localBufferedReader=new BufferedReader(localFileReader,8192);  
            str2=localBufferedReader.readLine();//读取meminfo第一行，系统内存大小  
            arrayOfString=str2.split("\\s+");  
            for(String num:arrayOfString){  
            }  
            initial_memory=Integer.valueOf(arrayOfString[1]).intValue()*1024;//获得系统总内存，单位KB  
            localBufferedReader.close();  
        }catch(IOException e){  
              
        }  
        return Formatter.formatFileSize(getBaseContext(), initial_memory);  
        //Byte转位KB或MB  
          
    }

	/**
	 * 按键点击事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			finish();
			return true;
		}
		
		return false;
	}
	
	public void toastShow(String info){
		toast = Toast.makeText(getApplicationContext(),
				info, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

}
