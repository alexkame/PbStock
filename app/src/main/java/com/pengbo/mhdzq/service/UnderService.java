package com.pengbo.mhdzq.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.pengbo.mhdzq.data.SSLEncrypt;
import com.pengbo.mhdzq.receiver.HomeWatcherReceiver;
import com.pengbo.mhdzq.tools.FileService;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.ViewTools;

import com.pengbo.mhdzq.R;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class UnderService extends Service {

	private static final String TAG = "UnderService";
	private Thread underThread;
	private boolean start = true;
	private TimerTask task;
	private Timer timer;
	private View btn_floatView;
	TextView message;
	// Button btnAddConfirm;
	Button btnIgnore;

	private boolean isAdded = false;
	private static WindowManager wm;
	private static WindowManager.LayoutParams params;
	private Handler mHandler = null;

	public static String ACTION = "com.pengbo.mhdcx.service.UnderService";
	private final int HANDLE_CHECK_ACTIVITY = 201;
	private HashSet<String> mActivitySet = new HashSet<String>();
	private HomeWatcherReceiver mHomeKeyReceiver = null;
	private HashSet<String> pbblist = new HashSet<String>();

	private Cursor mCursor;
	private static final String PBB_CONFIGPATH = "hq_pbb.ini";
	private String currentActivity = "";

	@Override
	public IBinder onBind(Intent intent) {
		L.d(TAG, "ServiceDemo onStart");

		createFloatView();

		// getPBblist();
		// initPBBFile();
		initPBBArray();

		if (mHandler == null) {
			mHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case HANDLE_CHECK_ACTIVITY:
						if (!isAdded) {
							isAdded = true;
							wm.addView(btn_floatView, params);
							// Altet();
						}
						break;
					}
				}
			};
		}

		mActivitySet = ViewTools.getActivities(this);

		timer = new Timer(true);

		TimerTask task = new TimerTask() {
			public void run() {

				String top = getTopActivity();
				String[] tops = top.split("/");
				String topActivity = tops[1].split("[}]")[0];

				if (topActivity == null)
					return;

				if (ViewTools.isShouldForegraund && !isAdded) {// 没有被home键按下
					if (!topActivity.endsWith(".Launcher")) {// 不在launcher桌面
						if (!mActivitySet.contains(topActivity)) {// 不是本app的activity
							if (!pbblist.contains(topActivity)) {// 不在信任列表
								if (!currentActivity.equals(topActivity)) {// 还没有提示过
									message.setText("您的期权程序不在最前端运行");
									mHandler.sendEmptyMessage(HANDLE_CHECK_ACTIVITY);
								}
							}
						}
						else
						{
							//currentActivity = topActivity;
						}
					}
				} else if (!currentActivity.equals(topActivity)) {
					currentActivity = topActivity;
				}

				L.d(TAG, top);
			}
		};

		timer.schedule(task, 1000, 1000);

		return null;
	}

	@Override
	public void onCreate() {
		L.d(TAG, "ServiceDemo onCreate");
		registerHomeKeyReceiver(this);
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);
	}

	/*
	 * private void getPBblist() { if (mpbDB == null) { mpbDB = new
	 * HolddbHelper(this); } mCursor = mpbDB.select();
	 * 
	 * while (mCursor.moveToNext()) { String pbb =
	 * mCursor.getString(mCursor.getColumnIndex("name")); if
	 * (!pbblist.contains(pbb)) { pbblist.add(pbb); } } mCursor.close(); }
	 */

	public void initPBBFile() {
		FileService file = new FileService(this.getApplicationContext());
		int size = file.getFileSize(PBB_CONFIGPATH);
		byte[] data = new byte[size + 1];

		if (size > 0)// 文件不存在
		{
			int ret = file.readFile(PBB_CONFIGPATH, data);
			String pbbarray = new String(data);
			String[] pbbarrays = pbbarray.split(",");

			for (int i = 0; i < pbbarrays.length; i++) {
				if (!pbblist.contains(pbbarrays[i])) {
					pbblist.add(pbbarrays[i]);
				}
			}
		}
	}

	public void initPBBArray() {
		// pbblist.add("com.android.mms.ui.NewMessagePopupActivity");
		// pbblist.add("com.android.packageinstaller.PackageInstallerActivity");

		String pbarray = SSLEncrypt.GetPbBarray();

		if (pbarray != null) {
			String[] pbstr = pbarray.split(",");

			for (int i = 0; i < pbstr.length; i++) {
				if (!pbblist.contains(pbstr[i])) {
					pbblist.add(pbstr[i]);
				}
			}

		}
		// pbblist.add("com.taobao.taobao");
		// pbblist.add("com.wandoujia");
	}

	@Override
	public void onDestroy() {
		unregisterHomeKeyReceiver(this);
		timer.cancel();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		L.d(TAG, "ServiceDemo onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	String getTopActivity() {

		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

		if (runningTaskInfos != null)
			return (runningTaskInfos.get(0).topActivity).toString();
		else
			return null;

	}

	private void createFloatView() {
		// btn_floatView = new Button(getApplicationContext());

		LayoutInflater inflater = (LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		btn_floatView = inflater.inflate(R.layout.hold_alert, null);
		message = (TextView) btn_floatView.findViewById(R.id.AlertText);

		/*
		 * btnAddConfirm = (Button) btn_floatView
		 * .findViewById(R.id.buttonAddConfirm);
		 */
		btnIgnore = (Button) btn_floatView.findViewById(R.id.buttonCancel);

		/*
		 * if (btnAddConfirm != null) { btnAddConfirm.setOnTouchListener(new
		 * OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) { // TODO
		 * Auto-generated method stub wm.removeView(btn_floatView); if (mpbDB !=
		 * null) { String top = getTopActivity(); String[] tops =
		 * top.split("/"); String topActivity = tops[1].split("[}]")[0];
		 * mpbDB.insert(topActivity); getPBblist(); } isAdded = false; return
		 * false; } }); }
		 */

		if (btnIgnore != null) {
			btnIgnore.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub

					String top = getTopActivity();
					String[] tops = top.split("/");
					String topActivity = tops[1].split("[}]")[0];

					currentActivity = topActivity;

					try {
						wm.removeView(btn_floatView);
					} catch (IllegalArgumentException exception) {
						L.d(TAG, "remove exception");
					}
					isAdded = false;
					return false;
				}
			});
		}

		// btn_floatView.setText("你的程序被劫持");

		wm = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
		params = new WindowManager.LayoutParams();

		// 设置window type
		//params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		params.type = WindowManager.LayoutParams.TYPE_TOAST;
		/*
		 * 如果设置为params.type = WindowManager.LayoutParams.TYPE_PHONE; 那么优先级会降低一些,
		 * 即拉下通知栏不可见
		 */

		params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

		// 设置Window flag
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		// 设置悬浮窗的长得宽
		params.width = params.WRAP_CONTENT;
		params.height = params.WRAP_CONTENT;

		params.gravity = Gravity.CENTER_VERTICAL;

		// 设置悬浮窗的Touch监听
		/*
		 * btn_floatView.setOnTouchListener(new OnTouchListener() { int lastX,
		 * lastY; int paramX, paramY;
		 * 
		 * public boolean onTouch(View v, MotionEvent event) {
		 * wm.removeView(btn_floatView); isAdded = false; return true; } });
		 */

		// wm.addView(btn_floatView, params);
	}

	private void registerHomeKeyReceiver(Context context) {
		mHomeKeyReceiver = new HomeWatcherReceiver();
		final IntentFilter homeFilter = new IntentFilter(
				Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

		context.registerReceiver(mHomeKeyReceiver, homeFilter);
	}

	private void unregisterHomeKeyReceiver(Context context) {
		if (null != mHomeKeyReceiver) {
			context.unregisterReceiver(mHomeKeyReceiver);
		}
	}

}
