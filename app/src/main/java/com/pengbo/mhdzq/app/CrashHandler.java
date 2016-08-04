/*
 * (C) Copyright 2015 by Pobo.
 *
 *
 */

package com.pengbo.mhdzq.app;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Calendar;

import org.apache.http.impl.cookie.DateUtils;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.tools.FileService;
import com.pengbo.mhdzq.tools.L;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;


public class CrashHandler implements UncaughtExceptionHandler {
	public static final String TAG = CrashHandler.class.toString();
	private static CrashHandler instance = new CrashHandler();
	private Context mContext;
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	private String mDeviceCrashInfo = "";
	private static final String VERSION_NAME = "Version: ";
	public static final String CRASH_LOG_EXTENSION = ".txt";
	private static String SD_CARD = String.format(
			"%s", Environment.getExternalStorageDirectory());
	
	public static final String CRASH_LOG_DIR = SD_CARD + "/Pobo/CrashLog/";
	public static final String CRASH_LOG_DATE_PATTERN = "yyyy-MM-dd-HH-mm-ss";

	private CrashHandler() {
	}

	public static CrashHandler getInstance() {
		if (instance == null)
			instance = new CrashHandler();
		return instance;
	}

	public void init(Context ctx) {
		mContext = ctx;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		handleException(ex);

		try {
			// sleep for showing toast.
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			L.e(TAG, "Error : " + e);
		}
		if (mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		}
	}

	private boolean handleException(final Throwable ex) {
		if (ex == null) {
			return false;
		}
		
		FileService.makeDir(CRASH_LOG_DIR);
		String timestamp = DateUtils.formatDate(Calendar.getInstance().getTime(), CRASH_LOG_DATE_PATTERN);
		final String fileName = timestamp + CRASH_LOG_EXTENSION;
		
		
		
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, "Crash log saved to: " + CRASH_LOG_DIR + fileName, Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}.start();
		collectCrashDeviceInfo(mContext);
		saveCrashInfoToFile(ex , fileName, timestamp);
		AppActivityManager.getAppManager().AppExit(false);
		//sendAppCrashReport(mContext, mDeviceCrashInfo);
		return true;
	}
	
	/**
	 * 发送App异常崩溃报告
	 * @param cont
	 * @param crashReport
	 */
	public static void sendAppCrashReport(final Context cont, final String crashReport)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_error);
		builder.setMessage(R.string.app_error_message);
		builder.setPositiveButton(R.string.submit_report, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//退出
				AppActivityManager.getAppManager().AppExit(false);
			}
		});
		builder.setNegativeButton(R.string.sure, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//退出
				AppActivityManager.getAppManager().AppExit(false);
			}
		});
		builder.show();
	}

	public void collectCrashDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				mDeviceCrashInfo += VERSION_NAME + AppConstants.APP_VERSION_INFO + FileService.ENTER;
			}
		} catch (NameNotFoundException e) {
			L.e(TAG, "Error while collect package info" + e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				mDeviceCrashInfo = mDeviceCrashInfo + field.getName() + ": " + field.get(null) + FileService.ENTER;
			} catch (Exception e) {
				L.e(TAG, "Error while collect crash info" + e);
			}
		}
	}

	private String saveCrashInfoToFile(Throwable ex, String fileName, String timestamp) {
		PrintWriter printWriter = null;
		try {
			Writer stackTrace = new StringWriter();
			printWriter = new PrintWriter(stackTrace);
			ex.printStackTrace(printWriter);

			Throwable cause = ex.getCause();
			while (cause != null) {
				cause.printStackTrace(printWriter);
				cause = cause.getCause();
			}
			String result = stackTrace.toString();
			
			FileService.saveDataToFile(new File(CRASH_LOG_DIR + fileName), timestamp + FileService.ENTER + mDeviceCrashInfo
					+ result);
			return fileName;
		} catch (Exception e) {
			L.e(TAG, "an error occured while writing report file..." + e);
		} finally {
			FileService.close(printWriter);
		}
		return null;
	}

}