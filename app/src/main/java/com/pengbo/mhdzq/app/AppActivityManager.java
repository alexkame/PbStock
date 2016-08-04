package com.pengbo.mhdzq.app;

import java.util.Stack;

import com.pengbo.mhdcx.ui.main_activity.MainTabActivity;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.zq_activity.ZhengQuanActivity;
import com.pengbo.mhdzq.zq_trade_activity.ZqTradeDetailActivity;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 * 
 * @author Administrator
 * 
 */
public class AppActivityManager {
	private static String TAG = AppActivityManager.class.getSimpleName();
	private static Stack<Activity> activityStack;
	private static AppActivityManager instance;

	private AppActivityManager() {
	}

	/**
	 * 单一实例，应用程序Activity管理类：用于Activity管理和应用程序退出
	 */
	public static AppActivityManager getAppManager() {
		if (instance == null) {
			instance = new AppActivityManager();
		}
		return instance;
	}

	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	/**
	 * 将activity从堆栈中删除
	 * @param activity
	 */
	public void removeActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		if (activity != null) {
			activityStack.remove(activity);
			activity = null;
		}
	}

	public ZqTradeDetailActivity getZQTradeDetailActivity() {
		if (activityStack == null)
			return null;

		for (Activity activity : activityStack) {
			if (activity instanceof ZqTradeDetailActivity) {
				return (ZqTradeDetailActivity) activity;
			}
		}
		return null;
	}

	public ZhengQuanActivity getZQActivity() {
		if (activityStack == null)
			return null;

		for (Activity activity : activityStack) {
			if (activity instanceof ZhengQuanActivity) {
				return (ZhengQuanActivity) activity;
			}
		}
		return null;
	}

	public MainTabActivity getMainTabActivity() {
		if (activityStack == null)
			return null;

		for (Activity activity : activityStack) {
			if (activity instanceof MainTabActivity) {
				return (MainTabActivity) activity;
			}
		}
		return null;
	}

	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 */
	public Activity currentActivity() {
		if (activityStack == null)
			return null;

		Activity activity = activityStack.lastElement();

		while (activity.isFinishing()) {
			activityStack.remove(activity);
			if (activityStack.size() <= 0) {
				break;
			}
			activity = activityStack.lastElement();
		}
		return activity;
	}

	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void finishActivity() {
		if (activityStack == null)
			return;

		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			if (!activity.isFinishing()) {
				activity.finish();
			}
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls) {
		if (activityStack == null)
			return;

		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		if (activityStack == null)
			return;

		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}

	public int getActivityCount() {
		if (activityStack == null)
			return 0;

		return activityStack.size();
	}

	/**
	 * 退出应用程序
	 */
	public void AppExit(boolean showConfirm) {
		if (showConfirm) {
			showExitConfirm();
		} else {
			killProcess();
		}
	}

	private void showExitConfirm() {
		if (currentActivity() == null) {
			killProcess();
			return;
		}
		new AlertDialog.Builder(currentActivity()).setTitle("退出程序")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						killProcess();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//
						dialog.cancel(); // 删除对话框
					}
				}).show();
	}

	private void killProcess() {
		try {
			L.d(TAG, "killProcess is calling");

			finishAllActivity();

			ActivityManager activityMgr = (ActivityManager) MyApp.getInstance()
					.getApplicationContext()
					.getSystemService(Context.ACTIVITY_SERVICE);
			L.d(TAG, "killing bg process");
			activityMgr.killBackgroundProcesses(MyApp.getInstance()
					.getApplicationContext().getPackageName());
			L.d(TAG, "os killing process");
			android.os.Process.killProcess(android.os.Process.myPid());
			L.d(TAG, "exit(0)");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
	}

}
