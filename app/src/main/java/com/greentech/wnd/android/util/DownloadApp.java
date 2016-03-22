package com.greentech.wnd.android.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.greentech.wnd.android.R;

/**
 * 下载安装app
 * 
 * @author wlj
 * 
 */
public class DownloadApp {

	private static String savePath = "";
	private static String saveFileName = "";
	Context context = null;

	Handler handler = null;
	String downloadUrl = "";
	private boolean stopDownloadFlag = false;
	private ProgressBar progressBar;
	private int progress;
	private Dialog noticeDialog;
	private Dialog downloadDialog;

	public DownloadApp(Context context, String downloadUrl, Handler handler) {
		this.context = context;
		this.downloadUrl = downloadUrl;
		this.handler = handler;
	}

	public void show() {
		Looper.prepare();
		savePath = Environment.getExternalStorageDirectory().toString() + "/";
		saveFileName = savePath + "temp.apk";

		if (isNetworkConnected()) {

			try {
				showNoticeDialog();
			} catch (Exception e) {
				Log.d("111", "222", e);
			}
		} else {
			Toast.makeText(context, "未开启网络，请先退出，连接网络后重新进入...",
					Toast.LENGTH_LONG).show();
		}
		Looper.loop();
	}

	/**
	 * 检测是否开启了网络
	 * 
	 * @return
	 */
	private boolean isNetworkConnected() {
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo network = cm.getActiveNetworkInfo();
			if (network != null) {
				return network.isAvailable();
			}
		} catch (Exception e) {
			Log.d("111", "222", e);
		}
		return false;
	}

	/**
	 * 显示通知更新对话框
	 */
	private void showNoticeDialog() {
		String msg = "是否下载";
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("软件更新下载");
		builder.setMessage(msg);
		builder.setPositiveButton("下载", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();
			}
		}).setNegativeButton("暂时不下载更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		noticeDialog = builder.create();
		noticeDialog.setCancelable(false);
		noticeDialog.show();
	}

	/**
	 * 显示下载进度对话框
	 */
	private void showDownloadDialog() {
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("软件版本更新-下载");
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.progress_bar, null);
		progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
		builder.setView(v);
		builder.setNegativeButton("终止下载", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				stopDownloadFlag = true;
				dialog.dismiss();
			}
		});
		downloadDialog = builder.create();
		downloadDialog.setCancelable(false);
		downloadDialog.show();
		downloadApk();
	}

	/**
	 * 下载线程，现在结束后关闭下载对话框，下载过程中可以取消下载
	 */
	private void downloadApk() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					URL url = new URL(downloadUrl);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.connect();
					int length = conn.getContentLength();
					InputStream is = conn.getInputStream();
					File file = new File(savePath);
					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(saveFileName);
					FileOutputStream fos = new FileOutputStream(apkFile);

					int count = 0;
					byte buf[] = new byte[1024];
					do {
						int numread = is.read(buf);
						count += numread;
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						handler.post(new Runnable() {

							@Override
							public void run() {
								progressBar.setProgress(progress);
							}
						});

						if (numread <= 0) {
							// 下载完成通知安装
							handler.post(new Runnable() {

								@Override
								public void run() {
									downloadDialog.dismiss();
									installApk();
								}
							});
							break;
						}

						fos.write(buf, 0, numread);
					} while (!stopDownloadFlag);// 点击取消则终止下载。

					fos.close();
					is.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.d("111", "222", e);
				}
			}
		}).start();
		;
	}

	/**
	 * 安装
	 */
	private void installApk() {
		File apkFile = new File(saveFileName);
		if (!apkFile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkFile.toString()),
				"application/vnd.android.package-archive");
		context.startActivity(i);
	}
}
