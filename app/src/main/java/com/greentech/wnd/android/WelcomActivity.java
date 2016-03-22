package com.greentech.wnd.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.greentech.wnd.android.constant.Constant;
import com.greentech.wnd.android.util.CheckedNet;
import com.greentech.wnd.android.util.NetUtil;

public class WelcomActivity extends Activity {
	private static String savePath = "";
	private static String saveFileName = "";
	private boolean isFirstIn = false;// 判断是否为第一次加载
	Handler handler = new Handler();

	String version = "";
	String currentVersion = "";
	String currentVersionName = "";
	String currentPackageName = "";
	String mustUpdate = "";//等于1必须更新
	String downloadUrl = "";
	private boolean stopDownloadFlag = false;
	private boolean permitFlag = true;
	private ProgressBar progressBar;
	private int progress;
	private Dialog noticeDialog;
	private Dialog downloadDialog;

	ImageView iv = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logo_activity);

		// 读取SharedPreferences中需要的数据
		// 使用SharedPreferences来记录程序的使用次数
		SharedPreferences preferences = getSharedPreferences("firstLoad",
				MODE_PRIVATE);

		// 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
		isFirstIn = preferences.getBoolean("isFirstIn", true);

		// 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
//		if (!isFirstIn) {
//			goHome();
//		} else {
//			goGuider();
//		}
		goHome();

	}

	// 如果是第一次加载就进入引导界面
	private void goGuider() {
		Intent intent = new Intent(this, GuideActivity.class);
		startActivity(intent);
		finish();

	}

	// 如果不是第一次加载就进入引导界面
	private void goHome() {

		savePath = Environment.getExternalStorageDirectory().toString() + "/";
		saveFileName = savePath + "wnd.apk";
		final AlphaAnimation anima = new AlphaAnimation(0.1f, 1.0f);
		iv = (ImageView) findViewById(R.id.logo);
		anima.setDuration(2000);
		iv.startAnimation(anima);
		anima.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 检查软件是否需要更新
//				if (CheckedNet.isNetworkAviaible(WelcomActivity.this)) {
//
//					// 检查版本更新
//					try {
//						Thread t = new Thread(new Runnable() {
//
//							@Override
//							public void run() {
//								try {
//									InputStream is = NetUtil
//											.post(Constant.SERVIER_PATH
//													+ "/json/getNewestVersion.action");
//									String str = NetUtil
//											.getStringFromInputStream(is);
//									Log.i(Constant.TAG, str);
//									if (str == null) {
//										WelcomActivity.this.finish();
//									}
//									if (str != null) {
//										JSONObject json = new JSONObject(str);
//										if (json != null) {
//											version = json.getString("version");
//											mustUpdate = json
//													.getString("mustUpdate");
//											downloadUrl = json
//													.getString("downloadUrl");
//										}
//									}
//
//									currentPackageName = getApplicationContext()
//											.getPackageName();
//									currentVersion = String
//											.valueOf(getApplicationContext()
//													.getPackageManager()
//													.getPackageInfo(
//															currentPackageName,
//															0).versionCode);
//									currentVersionName = String
//											.valueOf(getApplicationContext()
//													.getPackageManager()
//													.getPackageInfo(
//															currentPackageName,
//															0).versionName);
//
//									// 如果当前版本号不是最新的，则弹出更新对话框
//									if (version != null
//											&& !version.equals(currentVersion)) {
//										handler.post(new Runnable() {
//
//											@Override
//											public void run() {
////												Toast.makeText(
////														getApplicationContext(),
////														"最新版本Code："
////																+ version
////																+ " 当前版本Code："
////																+ currentVersion
////																+ "当前版本号："
////																+ currentVersionName,
////														Toast.LENGTH_LONG)
////														.show();
//												try {
//													showNoticeDialog();
//												} catch (Exception e) {
//													Log.d("111", "222", e);
//												}
//											}
//										});
//									} else {
//										loadOnSystem();
//
//									}
//								} catch (Exception e) {
//									Log.d("111", "222", e);
//								}
//							}
//						});
//						t.start();
//					} catch (Exception e) {
//						Log.d("111", "222", e);
//					}
//				} else {
//					Toast.makeText(getApplicationContext(),
//							"未开启网络，请先退出，连接网络后重新进入...", Toast.LENGTH_LONG)
//							.show();
//				}
				loadOnSystem();
			}
		});

	}
	
	private void loadOnSystem() {
		// 以startActivityForResult方式启动
		// 以便在下一个activity finish掉的时候通知本activity来finish
		WelcomActivity.this.startActivityForResult(new Intent(
				WelcomActivity.this, MainActivity.class), 1);
		WelcomActivity.this.finish();
	}

	/**
	 * 显示通知更新对话框
	 */
	private void showNoticeDialog() {
		String msg = "";
		if (mustUpdate != null && mustUpdate.equals("1")) {
			// 必须更新
			msg = "有新版本，必须更新后才能继续使用。";
			permitFlag = false;
		} else {
			msg = "有新版本，是否要更新？";
		}
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("软件版本更新");
		builder.setMessage(msg);
		builder.setPositiveButton("下载", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();
			}
		}).setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (permitFlag) {
					 loadOnSystem();
				}
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
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("软件版本更新-下载");
		LayoutInflater inflater = LayoutInflater.from(this);
		View v = inflater.inflate(R.layout.progress_bar, null);
		progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
		builder.setView(v);
		builder.setNegativeButton("终止下载", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				stopDownloadFlag = true;
				dialog.dismiss();
				if(permitFlag) {
					 loadOnSystem();
				}
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
		this.startActivity(i);
	}

}
