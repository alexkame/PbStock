package com.pengbo.mhdzq.zq_activity;

import java.util.Calendar;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.ZqWTAdapter;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.fragment.ZhengQuanFragment;
import com.pengbo.mhdzq.net.CMessageObject;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.TradeNetConnect;
import com.pengbo.mhdzq.trade.data.Trade_Define;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ZQLSWTActivity extends HdActivity implements OnClickListener {

	private MyApp mMyApp;
	private PBSTEP mListData_wt;// 委托

	private boolean mbStartOrEnd = true;// true-选择起始日期
	private String mStartDate, mEndDate;// 历史查询起始时间和结束时间（20150511）
	private int mStartYear = 2015, mStartMonth = 1, mStartDay = 1;
	private int mEndYear = 2015, mEndMonth = 1, mEndDay = 1;

	ImageView mBack;
	ImageView mRefresh;

	private ListView mListView;
	private TextView mHeadView;
	private EditText mEdit_StartDate, mEdit_EndDate;
	private Button mQueryBtn;
	public ZqWTAdapter mWTAdapter;
	public DatePickerDialog mDatePickerDialog;
	public Dialog mProgress;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			int nFrameType = msg.arg1;
			int nRequestCode = msg.arg2;

			switch (msg.what) {
			case TradeNetConnect.MSG_UPDATE_DATA: {// 交易
				CMessageObject aMsgObject = null;
				if (msg.obj != null && (msg.obj instanceof CMessageObject)) {
					aMsgObject = (CMessageObject) msg.obj;
				} else {
					return;
				}

				if (nFrameType == 1)// 交易连接，交换密钥
				{
					mMyApp.mTradeNet.setMainHandler(mHandler);

					String type = "0";

					mMyApp.mTradeData.mTradeAccount = mMyApp.mTradeAccount;
					mMyApp.mTradeData.mTradePassword = mMyApp.mTradePassword;
					
					mMyApp.mTradeData.mTradeVersion= mMyApp.mTradeVersion;
					mMyApp.mTradeData.mTradeLockTimeout = mMyApp.mTradeLockTimeout;
					mMyApp.mTradeData.mHQType = mMyApp.mHQType;
					

					mMyApp.mTradeNet.Request_Login(
							mMyApp.mTradeData.mTradeAccount,
							mMyApp.mTradeData.mTradePassword, 0, 0,
							AppConstants.APP_VERSION_INFO, type, "", 0);
				} else if (nFrameType == Trade_Define.Func_Login) {

					if (aMsgObject.nErrorCode < 0) {
						// 登陆出错，弹出messagebox提醒用户
						Toast.makeText(ZQLSWTActivity.this, "登录失败", 5000)
								.show();
					} else {
						// login success
						mMyApp.mTradeData.mTradeLoginFlag = true;
						closeProgress();

					}
					
				}
				else if (nFrameType == Trade_Define.Func_LSWT) {
					updateTradeData();
				}
			}

				break;

			case TradeNetConnect.MSG_LOCK: {
				proc_MSG_LOCK(msg);
			}
				break;

			case TradeNetConnect.MSG_DISCONNECT: {
				proc_MSG_DISCONNECT(msg);
			}
				break;
			case TradeNetConnect.MSG_TIMEOUT: {
				proc_MSG_TIMEOUT(msg);
			}
				break;
			default:
				break;
			}
		}
	};

	protected void proc_MSG_LOCK(Message msg) {

		new com.pengbo.mhdzq.widget.AlertDialog(this).builder()
				.setTitle("交易登录超时").setMsg("交易登录在线时间过长，需要重新登录！")
				.setCancelable(false).setCanceledOnTouchOutside(false)
				.setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						processNetLose();
					}

				}).show();
		;

	}

	protected void proc_MSG_DISCONNECT(Message msg) {
		if(mMyApp.mTradeData.mReconnected)
		{
		    new com.pengbo.mhdzq.widget.AlertDialog(this).builder().setTitle("提示")
		    .setMsg(this.getString(R.string.reconnect_warn)).setCancelable(false)
		    .setCanceledOnTouchOutside(false)
		    .setPositiveButton(this.getString(R.string.connect_warn_btn), new OnClickListener() {

			   @Override
			   public void onClick(View v) {
				// TODO Auto-generated method stub
				   processReconnect();
			   }
		    })
		    .setNegativeButton(this.getString(R.string.connect_warn_login),new OnClickListener(){

			   @Override
			   public void onClick(View v) {
				// TODO Auto-generated method stub
				   processNetLose();
			   }})
		    .show();
		}
		else
		{
		    new com.pengbo.mhdzq.widget.AlertDialog(this).builder().setTitle("提示")
		    .setMsg(this.getString(R.string.connect_warn)).setCancelable(false)
		    .setCanceledOnTouchOutside(false)
		    .setPositiveButton(this.getString(R.string.connect_warn_btn), new OnClickListener() {

			   @Override
			   public void onClick(View v) {
				// TODO Auto-generated method stub
				   processReconnect();
			   }
		    }).show();
		}
	}

	protected void proc_MSG_TIMEOUT(Message msg) {
		if(mMyApp.mTradeData.mReconnected)
		{
		     new com.pengbo.mhdzq.widget.AlertDialog(this).builder().setTitle("提示")
				.setMsg("网络请求超时").setCancelable(false)
				.setCanceledOnTouchOutside(false)
				.setPositiveButton("重新连接", new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						processReconnect();
					}
				})
				.setNegativeButton("重新登录",new OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						processNetLose();
					}})
				.show();
		}
		else
		{
		    new com.pengbo.mhdzq.widget.AlertDialog(this).builder().setTitle("提示")
		    .setMsg("网络请求超时").setCancelable(false)
		    .setCanceledOnTouchOutside(false)
		    .setPositiveButton("重新连接", new OnClickListener() {

			   @Override
			   public void onClick(View v) {
				// TODO Auto-generated method stub
				   processReconnect();
			   }
		    }).show();
		}
	}

	private void processNetLose() {
		mMyApp.setHQPushNetHandler(null);
		mMyApp.setTradeHandler(null);
		mMyApp.mTradeNet.closeConnect();

		mMyApp.mTradeData.clearStepData();
		mMyApp.mTradeData.mTradeLoginFlag = false;
		Toast.makeText(this, "退出账户", Toast.LENGTH_SHORT).show();

		Intent intent = new Intent();
		intent.setClass(this, ZhengQuanActivity.class);
		if (AppActivityManager.getAppManager().getZQActivity() != null) {
			AppActivityManager.getAppManager().finishActivity(
					AppActivityManager.getAppManager().getZQActivity());
		}

		if (AppActivityManager.getAppManager().getZQTradeDetailActivity() != null) {
			AppActivityManager.getAppManager().finishActivity(
					AppActivityManager.getAppManager()
							.getZQTradeDetailActivity());
		}

		intent.putExtra("ZQ_VIEW_INDEX", ZhengQuanFragment.ZQ_VIEW_JIAOYI);
		startActivity(intent);
		this.finish();
	}
	
	private void processReconnect() {
		mMyApp.mTradeData.mReconnected = true;
		mMyApp.setHQPushNetHandler(null);
		GlobalNetProgress.HQRequest_MultiCodeInfoPush(mMyApp.mHQPushNet, null,
				0, 0);
		mMyApp.mTradeNet.closeConnect();

		mMyApp.mTradeData.clearJustStepData();
		
		mMyApp.mTradeData.mTradeLoginFlag = false;
		//Toast.makeText(this, "退出账户", Toast.LENGTH_LONG).show();

		
		if(MyApp.getInstance().mTradeData.mTradeLoginFlag == false)
		{
		    String type = "0";
		    type = mMyApp.mTradeQSZHType_ZQ;
		    if (type.isEmpty()) {
			   type = "0";
		    }

		    mMyApp.resetAddrNum_Trade(mMyApp.mTrade_reconnectAddrNum);
		    mMyApp.mTradeNet.setMainHandler(mHandler);
		    mMyApp.mTradeData.mTradeLockTimeout = PreferenceEngine.getInstance().getTradeOnlineTime();
		    mMyApp.mTradeNet.initSocketThread();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zq_trade_lswt_frame);
		initData();
		initView();
		initDate();
	}

	private void initData() {
		mMyApp = (MyApp) this.getApplication();
		mListData_wt = new PBSTEP();
	}

	private void initDate() {
		Calendar cal = Calendar.getInstance();
		mEndYear = cal.get(Calendar.YEAR);
		mEndMonth = cal.get(Calendar.MONTH) + 1;
		mEndDay = cal.get(Calendar.DATE);
		String strM = (mEndMonth < 10) ? ("0" + mEndMonth) : ("" + mEndMonth);
		String strD = (mEndDay < 10) ? ("0" + mEndDay) : ("" + mEndDay);
		mEndDate = String.format("%d%s%s", mEndYear, strM, strD);

		mStartYear = cal.get(Calendar.YEAR);
		mStartMonth = cal.get(Calendar.MONTH) + 1;
		mStartDay = cal.get(Calendar.DATE);
		strM = (mStartMonth < 10) ? ("0" + mStartMonth) : ("" + mStartMonth);
		strD = (mStartDay < 10) ? ("0" + mStartDay) : ("" + mStartDay);
		mStartDate = String.format("%d%s%s", mStartYear, strM, strD);

		mEdit_StartDate.setText(ViewTools.formatDate(mStartDate));
		mEdit_EndDate.setText(ViewTools.formatDate(mEndDate));

	}

	private void initView() {
		mEdit_StartDate = (EditText) findViewById(R.id.lscj_date1);
		mEdit_EndDate = (EditText) findViewById(R.id.lscj_date2);
		mEdit_StartDate.setOnClickListener(this);
		mEdit_EndDate.setOnClickListener(this);

		mQueryBtn = (Button) findViewById(R.id.lswt_query_btn);
		mQueryBtn.setOnClickListener(this);

		mListView = (ListView) findViewById(R.id.trade_order_listview_cj);

		mWTAdapter = new ZqWTAdapter(mListData_wt, this, mHandler, mMyApp, true);
		mListView.setAdapter(mWTAdapter);

		mBack = (ImageView) this.findViewById(R.id.zq_header_back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ZQLSWTActivity.this.finish();

			}
		});
		mBack.setVisibility(View.VISIBLE);

		// mRefresh = (ImageView)
		// this.findViewById(R.id.zq_header_right_refresh);
		// mRefresh.setVisibility(View.VISIBLE);
		// mRefresh.setOnClickListener(new OnClickListener(){
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// requestDRCJ();
		// }});

		mHeadView = (TextView) findViewById(R.id.zq_header_middle_textview);
		mHeadView.setText("历史委托 ");
	}

	@Override
	public void onResume() {
		mMyApp.mTradeNet.setMainHandler(mHandler);
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		mMyApp.mTradeNet.setMainHandler(null);
		closeProgress();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mMyApp.mTradeNet.setMainHandler(null);
		closeProgress();
		super.onPause();
	}

	private void requestLSWT() {
		String param = "";
		param = String.format("%d:%s|%d:%s", STEP_Define.STEP_QSRQ, mStartDate,
				STEP_Define.STEP_ZZRQ, mEndDate);
		mMyApp.mTradeNet.Request_ListQuery(Trade_Define.Func_LSWT, param);
	}

	private void updateTradeData() {
		mMyApp.mTradeData.GetLSWT(mListData_wt);

		mWTAdapter.notifyDataSetChanged();
	}
	
	
	protected void showProgress(String msg) {
		closeProgress();

		if (mProgress == null) {
			mProgress = new Dialog(this, R.style.ProgressDialogStyle);
			mProgress.setContentView(R.layout.list_loading);
			TextView tv = (TextView) mProgress.findViewById(R.id.loading_text);
			tv.setText(msg);
			mProgress.setCancelable(true);
		}
		mProgress.show();
	}

	protected void closeProgress() {

		if (mProgress != null && mProgress.isShowing()) {
			mProgress.cancel();
			mProgress.dismiss();
			mProgress = null;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.lscj_date1:// 点击起始日期
		{
			// 构建一个 DatePickerDialog 并显示
			mbStartOrEnd = true;
			mDatePickerDialog = new DatePickerDialog(this, listener,
					mStartYear, mStartMonth - 1, mStartDay);
			mDatePickerDialog.show();
		}

			break;
		case R.id.lscj_date2:// 点击终止日期
			// 构建一个 DatePickerDialog 并显示
			mbStartOrEnd = false;
			mDatePickerDialog = new DatePickerDialog(this, listener, mEndYear,
					mEndMonth - 1, mEndDay);
			mDatePickerDialog.show();
			break;
		case R.id.lswt_query_btn:
			boolean bNeeded = true;
			if (mStartYear > mEndYear) {
				bNeeded = false;
			} else if (mStartYear == mEndYear) {
				if (mStartMonth > mEndMonth) {
					bNeeded = false;
				} else if (mStartMonth == mEndMonth) {
					if (mStartDay > mEndDay) {
						bNeeded = false;
					}
				}
			}
			if (!bNeeded) {
				Toast.makeText(this, "起始日期应比截止日期早！", Toast.LENGTH_SHORT).show();
				return;
			}

			if (mListData_wt != null) {
				mListData_wt.Free();
			}
			if (mWTAdapter != null) {
				mWTAdapter.notifyDataSetChanged();
			}

			{
				requestLSWT();
			}

			break;
		case R.id.tv_lscx_header_left_back:
			this.finish();
			break;
		default:
			break;
		}
	}

	private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() { //

		@Override
		public void onDateSet(DatePicker arg0, int year, int month, int day) {

			if (mbStartOrEnd) {
				mStartYear = year;
				mStartMonth = month + 1;
				mStartDay = day;

				String strM = (mStartMonth < 10) ? ("0" + mStartMonth)
						: ("" + mStartMonth);
				String strD = (mStartDay < 10) ? ("0" + mStartDay)
						: ("" + mStartDay);
				mStartDate = String.format("%d%s%s", mStartYear, strM, strD);

				mEdit_StartDate.setText(ViewTools.formatDate(mStartDate));
			} else {
				mEndYear = year;
				mEndMonth = month + 1;
				mEndDay = day;

				String strM = (mEndMonth < 10) ? ("0" + mEndMonth)
						: ("" + mEndMonth);
				String strD = (mEndDay < 10) ? ("0" + mEndDay) : ("" + mEndDay);
				mEndDate = String.format("%d%s%s", mEndYear, strM, strD);

				mEdit_EndDate.setText(ViewTools.formatDate(mEndDate));
			}
		}
	};

}
