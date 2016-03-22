package com.greentech.wnd.android.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

public class CrashHandler implements UncaughtExceptionHandler {

	private static CrashHandler instance = new CrashHandler();

	private Context mContext;
	private UncaughtExceptionHandler mDefaultHandler;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	/**
	 * 将构造函数私有化，使获得本类实例对象只能通过getInstance()方法
	 */
	private CrashHandler() {

	}

	/**
	 * 保证当前类在运行环境中仅有一个实例对象，即单例模式。
	 * 
	 * @return
	 */
	public static CrashHandler getInstance() {
		return instance;
	}

	/**
	 * 初始化，获得CrashHandler的单例后，必须调用init方法初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 实现UncaughtExceptionHandler接口的uncaughtException方法
	 * android系统默认的UncaughtExceptionHandler对于UI主线程
	 * (线程name=main)或者子线程（线程name=Thread-线程id号）出现未捕获异常，都是弹出应用程序出错提示，然后退出应用程序。
	 * 此处逻辑： 1、主线程抛出未捕获异常的话，则我们这边处理完异常后必须退出应用程序，不然应用程序会导致系统卡死等问题。
	 * 2、如果是子线程抛出为捕获异常的话，则我们这边可以不用退出应用程序，只是抛出出错提示。
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {

		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理异常，则交给系统默认处理器来处理。
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			// 可以区别对待主线程和子线程的未捕获异常
			if (thread.getName().equals("main")) {
				new Thread() {
					public void run() {
						Looper looper = Looper.myLooper();
						if (looper == null) {
							Looper.prepare();
						}
						Toast toast = Toast.makeText(mContext,
								"很抱歉，应用程序出错了，即将退出，您可以重新打开应用程序。",
								Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						Looper.loop();
						// loop是一个检查消息队列直至其消息队列为空的操作。在子线程中操作主线程，一种方式是通过主线程中定义的Handler来操作，每个Handler都会开启一个线程唯一的Looper。Looper.myLooper可以获得当前的looper，UI主线程由系统创建了一个Looper，并且执行了loop()方法。如果要再子线程中更新UI，但是又不用UI主线程中定义的Handler来更新的话，需要在子线程中自己创建一个Looper，通过Looper的prepare、loop方法来更新UI；或者在子线程中创建一个Handler，并且给Handler赋予一个新创建的Looper(必须给Handler赋予Looper对象)，通过handler更新主线程UI。
						// 实际debug过程中发现，loop()方法应该是个一直循环下去的方法，直到某个handler中获得到当前线程的looper对象，调用looper.quit()或者looper.quitSafely()方法才停止。
					}
				}.start();
				try {
					Thread.sleep(2000);// 一定要等下再关闭，不然消息框显示不了。
				} catch (InterruptedException e) {
				}
				
				//这一段关闭后能解决在Activity未创建成功前出错时不能退出的bug
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
				
				// 主线程出现未捕获异常，必须退出程序
				android.os.Process.killProcess(android.os.Process.myPid());//在Activity未创建成功前出错时不能实现退出，但是在Activity创建成功后点击界面按钮后出错时可以退出。
				System.exit(1);
			} else {
				new Thread() {
					public void run() {
						Looper looper = Looper.myLooper();
						if (looper == null) {
							Looper.prepare();// 如果当前线程未启用looper，则启用。
						}
						Toast toast = Toast.makeText(mContext,
								"很抱歉，应用程序出错了，但是您可以继续使用。", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						Looper.loop();// 这里会导致出错的那个线程死在那边，直到该线程的某个handler获得Looper对象并且quit之前，该线程一直不会被释放
					}
				}.start();
			}
		}
	}

	/**
	 * 手机设备信息，包括应用版本号和版本名称、设备Builder信息。
	 * 
	 * @param context
	 * @return map，不为null。
	 */
	private Map<String, String> collectDeviceInfo(Context context) {
		Map<String, String> deviceInfo = new HashMap<String, String>();
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				deviceInfo.put("VersionName", pi.versionName == null ? "null"
						: pi.versionName);
				deviceInfo.put("VersionCode", "" + pi.versionCode);
			}

			Field[] fields = Build.class.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				deviceInfo.put(field.getName(), field.get(null).toString());
			}
		} catch (Exception e) {

		}
		return deviceInfo;
	}

	/**
	 * 将出错信息和设备信息保存进扩展卡文件
	 * 
	 * @param ex
	 * @param deviceInfo
	 * @return 保存的路径 null或者/sdcard/crash/crash-yyyy-MM-dd-HH-mm-ss.log
	 */
	private String saveCrashInfo2File(Throwable ex,
			Map<String, String> deviceInfo) {
		try {
			if (PackageManager.PERMISSION_DENIED == mContext
					.getPackageManager().checkPermission(
							"android.permission.WRITE_EXTERNAL_STORAGE",
							mContext.getPackageName())) {
				// 没有写扩展卡权限
				return null;
			}
			StringBuffer sb = new StringBuffer();
			if (deviceInfo != null && !deviceInfo.isEmpty()) {
				sb.append("/** 设备信息 **/").append("\r\n");
				for (Map.Entry<String, String> entry : deviceInfo.entrySet()) {
					sb.append(entry.getKey()).append("=")
							.append(entry.getValue()).append("\r\n");
				}
			}
			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			// 这里会把Throwable的所有出错信息都打印出来，如果ex包含cause，则会循环打印出cause信息
			ex.printStackTrace(printWriter);
			printWriter.close();
			
			sb.append("/** 出错信息 **/").append("\r\n");
			sb.append(writer.toString());
			// writer.close();printWriter.close()里已经将传入的writer关闭了。
			String fileName = "crash-" + sdf.format(new Date()) + ".log";
			// 扩展卡是否可用，可写入
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				String path = "/sdcard/crash/";
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path + fileName);
				fos.write(sb.toString().getBytes());
				fos.close();
				return path + fileName;
			}
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * 处理异常信息
	 * 
	 * @param ex
	 * @return
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}

		// 保存日志
		saveCrashInfo2File(ex, collectDeviceInfo(mContext));
		return true;
	}
}
