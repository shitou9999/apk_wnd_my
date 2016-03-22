package com.greentech.wnd.android.util;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

public class CheckedNet {
	//弹出对话框设置网络
	//因为这里面有一个匿名内部类需要用到context，所以这里用LoadActivity
	public static void checkNetwork(final Activity context){
		//如果没有可用的网络就弹出对话框
		if(!isNetworkAviaible(context)){
			//创建一个对话框，用来提示用户是否需要打开网络
			
			
			TextView textView = new TextView(context);
			textView.setText("当前没有可用网络，请设置网络");
			
			new AlertDialog.Builder(context).setIcon(R.drawable.btn_default)
			.setTitle("设置网络").setView(textView).setPositiveButton("确定",
					        new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//跳转到网络设置
					context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
					//结束当前的activity
					context.finish();
				}
			}).create().show();
			
		}
		
		
	}
	
	//检查是否有可用的网络
	 public static  boolean isNetworkAviaible(Context context){
	    	//获得网络管理器，检查网络连接状态
		 boolean flag=false;
			ConnectivityManager manager = 
					(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if(manager !=null){
				NetworkInfo[]  info=   manager.getAllNetworkInfo();
				if(info!=null){
					for(NetworkInfo network:info){
						if(network.getState()==NetworkInfo.State.CONNECTED){
							flag= true;
						}
					}
					
				};
			};
			return flag;
	    }
	
	
}
