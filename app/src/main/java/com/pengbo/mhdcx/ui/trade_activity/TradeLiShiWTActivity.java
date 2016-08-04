package com.pengbo.mhdcx.ui.trade_activity;

import java.util.ArrayList;
import java.util.Calendar;

import com.pengbo.mhdcx.adapter.CustomOptionListAdapter;
import com.pengbo.mhdcx.adapter.TradeLSWTListAdapter;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.net.CMessageObject;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.TradeNetConnect;
import com.pengbo.mhdzq.trade.data.Trade_Define;
import com.pengbo.mhdzq.zq_activity.HdActivity;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.ui.main_activity.HotOptionActivity;
import com.pengbo.mhdcx.ui.main_activity.MainTabActivity;
import com.pengbo.mhdcx.view.HVListView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class TradeLiShiWTActivity extends HdActivity implements OnClickListener{
	
	private TextView mTV_Header, mTV_Back;
	private EditText mEdit_StartDate, mEdit_EndDate;
	private Button mBtn_ChaXun;
	private LinearLayout mlLayout_lscxHeader;

	private MyApp mMyApp;
	private Context mContext;
	
	private boolean mbStartOrEnd = true;//true-选择起始日期
	private String mStartDate, mEndDate;//历史查询起始时间和结束时间（20150511）
	private int mStartYear = 2000, 
			mStartMonth = 1, 
			mStartDay = 1;
	private int mEndYear = 2000, 
			mEndMonth = 1, 
			mEndDay = 1;
	
	private PBSTEP mListData;
	private HVListView mListView_LSCX;
	private TradeLSWTListAdapter mListAdapter;
	
	public DatePickerDialog mDatePickerDialog;
	public Dialog mProgress;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			int nFrameType = msg.arg1;
			int nRequestCode = msg.arg2;

			switch (msg.what) {
			case TradeNetConnect.MSG_UPDATE_DATA: //
			{
				CMessageObject aMsgObject = null;
				if (msg.obj != null && (msg.obj instanceof CMessageObject)) {
					aMsgObject = (CMessageObject) msg.obj;
				} else {
					return;
				}
				
				if (nFrameType == 1)// 交易连接，交换密钥
				{

					String type = mMyApp.mQQType;

					String sjhm = PreferenceEngine.getInstance().getPhoneForVerify();
					
					mMyApp.mTradeNet.Request_Login(
							mMyApp.mTradeData.mTradeAccount,
							mMyApp.mTradeData.mTradePassword, 0, 0, AppConstants.APP_VERSION_INFO, type, sjhm, 6);
					
				} else if (nFrameType == Trade_Define.Func_Login) {

					closeProgress();
					if (aMsgObject.nErrorCode < 0) {
						// 登陆出错，弹出messagebox提醒用户
						new com.pengbo.mhdcx.widget.AlertDialog(
								MyApp.getInstance().mTradeDetailActivity
										.getParent()).builder().setTitle("登录")
								.setMsg(aMsgObject.errorMsg)
								.setCancelable(false)
								.setCanceledOnTouchOutside(false)
								.setPositiveButton("确认", new OnClickListener() {
									@Override
									public void onClick(View v) {
										// requestZJ();
									}
								}).show();
					} else {
						// login success
						mMyApp.mTradeData.mTradeLoginFlag = true;
						closeProgress();

					}
					
				}
				else if (nFrameType == Trade_Define.Func_LSWT)//历史委托
				{
					closeProgress();
					if (aMsgObject.nErrorCode < 0) {
						// 修改失败，弹出messagebox提醒用户
						new com.pengbo.mhdcx.widget.AlertDialog(mContext).builder().setTitle(mContext.getString(R.string.IDS_Warning))
								.setMsg(aMsgObject.errorMsg)
								.setCancelable(false)
								.setCanceledOnTouchOutside(false)
								.setPositiveButton(mContext.getString(R.string.IDS_QueDing), new OnClickListener() {
									@Override
									public void onClick(View v) {
										
									}
								}).show();
						return;
					}
					
					loadData();
					
				}
				break;
			}
				
			case TradeNetConnect.MSG_LOCK:
            {
            	proc_MSG_LOCK(msg);
            }
            break;
            
			case TradeNetConnect.MSG_DISCONNECT:
			{
				proc_MSG_DISCONNECT(msg);
			}
			break;

			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trade_lishi_chaxun);
		
		mContext = this;
		mMyApp = (MyApp) this.getApplication();
		initView();
	}
	
	@Override
	protected void onResume()
	{
		mMyApp.mTradeNet.setMainHandler(mHandler);
		closeProgress();
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
	
	private void initView() {
		mTV_Back = (TextView) findViewById(R.id.tv_lscx_header_left_back);
		mTV_Back.setOnClickListener(this);
		mTV_Header = (TextView) findViewById(R.id.tv_lscx);
		mTV_Header.setText("历史委托");
		
		mEdit_StartDate = (EditText) findViewById(R.id.edit_lscx_qsrq);
		mEdit_EndDate = (EditText) findViewById(R.id.edit_lscx_zzrq);
		mEdit_StartDate.setOnClickListener(this);
		mEdit_EndDate.setOnClickListener(this);
		
		mBtn_ChaXun = (Button) findViewById(R.id.btn_lscx_chaxun);
		mBtn_ChaXun.setOnClickListener(this);
		
		Calendar cal = Calendar.getInstance(); 
		mEndYear = cal.get(Calendar.YEAR);    
		mEndMonth = cal.get(Calendar.MONTH) + 1;  
		mEndDay = cal.get(Calendar.DATE);
		String strM = (mEndMonth < 10) ? ("0"+mEndMonth) : (""+mEndMonth);
		String strD = (mEndDay < 10) ? ("0"+mEndDay) : (""+mEndDay);
		mEndDate = String.format("%d%s%s", mEndYear, strM, strD);
		
		cal.add(Calendar.MONTH, -1);
		mStartYear = cal.get(Calendar.YEAR);    
		mStartMonth = cal.get(Calendar.MONTH)+1;    
		mStartDay = cal.get(Calendar.DATE);
		strM = (mStartMonth < 10) ? ("0"+mStartMonth) : (""+mStartMonth);
		strD = (mStartDay < 10) ? ("0"+mStartDay) : (""+mStartDay);
		mStartDate = String.format("%d%s%s", mStartYear, strM, strD);
		
		mEdit_StartDate.setText(ViewTools.formatDate(mStartDate));
		mEdit_EndDate.setText(ViewTools.formatDate(mEndDate));
		
		mlLayout_lscxHeader = (LinearLayout) findViewById(R.id.lLayout_lscxheader);
		
		int iScreenWidth = ViewTools.getScreenSize(this.mContext).widthPixels;
		ViewGroup.LayoutParams params = mlLayout_lscxHeader.getLayoutParams();

		params.width = (iScreenWidth/4) * 15;
		mlLayout_lscxHeader.setLayoutParams(params);
		
		LinearLayout lLayout_cjHeader = (LinearLayout) findViewById(R.id.lLayout_lscjheader);
		lLayout_cjHeader.setVisibility(View.GONE);
		
		mListView_LSCX = (HVListView) findViewById(R.id.lv_lscx);
		mListView_LSCX.mListHead = mlLayout_lscxHeader;
		mListView_LSCX.setWidth(params.width);
		mListView_LSCX.setItemId(R.id.hv_lscx_item);
		mListView_LSCX.setScreenItemNum(4);

		if (mListData == null)
		{
			mListData = new PBSTEP();
		}
		mListAdapter = new TradeLSWTListAdapter(mContext, mListData);
		mListView_LSCX.setAdapter(mListAdapter);
		
	}
	
	private void loadData()
	{
		if(mListData == null)
		{
			mListData = new PBSTEP();
		}
		mMyApp.mTradeData.GetLSWT(mListData);
		if (mListAdapter != null)
		{
			mListAdapter.notifyDataSetChanged();
		}
	}
	
	private void requestLSWT()
	{
		mMyApp.mTradeNet.setMainHandler(mHandler);
		String param = "";
		param = String.format("%d:%s|%d:%s", STEP_Define.STEP_QSRQ, mStartDate, STEP_Define.STEP_ZZRQ, mEndDate); 
		mMyApp.mTradeNet.Request_ListQuery(Trade_Define.Func_LSWT, param);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.edit_lscx_qsrq:// 点击起始日期
		{
			// 构建一个 DatePickerDialog 并显示
			mbStartOrEnd = true;
			mDatePickerDialog = new DatePickerDialog(this, listener, mStartYear,
						mStartMonth - 1,
						mStartDay);
			mDatePickerDialog.show();
		}
			
			break;
		case R.id.edit_lscx_zzrq:// 点击终止日期
			// 构建一个 DatePickerDialog 并显示
			mbStartOrEnd = false;
			mDatePickerDialog = new DatePickerDialog(this, listener, mEndYear,
						mEndMonth - 1,
						mEndDay);
			mDatePickerDialog.show();
			break;
		case R.id.btn_lscx_chaxun:
			boolean bNeeded = true;
			if (mStartYear > mEndYear)
			{
				bNeeded = false;
			}else if(mStartYear == mEndYear)
			{
				if (mStartMonth > mEndMonth)
				{
					bNeeded = false;
				}else if(mStartMonth == mEndMonth)
				{
					if (mStartDay > mEndDay)
					{
						bNeeded = false;
					}
				}
			}
			if (!bNeeded)
			{
				Toast.makeText(this, "起始日期应比截止日期早！", Toast.LENGTH_SHORT)
				.show();
				return;
			}
			showProgress("查询中，请稍后......");
			if (mListData != null)
			{
				mListData.Free();
			}
			if (mListAdapter != null)
			{
				mListAdapter.notifyDataSetChanged();
			}
			requestLSWT();

			break;
		case R.id.tv_lscx_header_left_back:
			this.finish();
			break;
		default:
			break;
		}
	}
	
	private void proc_MSG_LOCK(Message msg)
    {    	
    	new com.pengbo.mhdcx.widget.AlertDialog(this)
		.builder()
		.setTitle("交易登录超时")
		.setMsg("交易登录在线时间过长，需要重新登录！")
		.setCancelable(false)
		.setCanceledOnTouchOutside(false)
		.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(View v) {
				mMyApp.setHQPushNetHandler(null);
				mMyApp.mTradeNet.setMainHandler(null);
				mMyApp.mTradeNet.closeConnect();
				mMyApp.mTradeData.mTradeLoginFlag = false;
				mMyApp.setCurrentOption(null);
				mMyApp.mTradeData.clearStepData();
				
				if (mDatePickerDialog != null)
				{
					mDatePickerDialog.dismiss();
				}
				
				Intent intent = new Intent();
				MainTabActivity act = AppActivityManager.getAppManager().getMainTabActivity();
				
				if (act != null) {
					act.setChangePage(MainTabActivity.PAGE_TRADE);
				}
				intent.setClass(TradeLiShiWTActivity.this, MainTabActivity.class);
				startActivity(intent);
				TradeLiShiWTActivity.this.finish();
			}
		}).show();
    }
	
	protected void proc_MSG_DISCONNECT(Message msg) {
		closeProgress();
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
	
	private void processReconnect()
	{
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

			mMyApp.mTradeNet.setMainHandler(mHandler);

			mMyApp.mTradeData.mTradeAccount = mMyApp.mQQTradeAccount;
			mMyApp.mTradeData.mTradePassword = mMyApp.mQQTradePassword;
			mMyApp.mTradeData.mHQType = mMyApp.mQQHQType;
			showProgress("");
		}
	}

	private void processNetLose()
	{
		mMyApp.setHQPushNetHandler(null);
		mMyApp.mTradeNet.setMainHandler(null);
		mMyApp.mTradeNet.closeConnect();
		mMyApp.mTradeData.mTradeLoginFlag = false;
		mMyApp.setCurrentOption(null);
		mMyApp.mTradeData.clearStepData();
		
		if (mDatePickerDialog != null)
		{
			mDatePickerDialog.dismiss();
		}
		
		Intent intent = new Intent();
		MainTabActivity act = AppActivityManager.getAppManager().getMainTabActivity();
		
		if (act != null) {
			act.setChangePage(MainTabActivity.PAGE_TRADE);
		}
		intent.setClass(TradeLiShiWTActivity.this, MainTabActivity.class);
		startActivity(intent);
		TradeLiShiWTActivity.this.finish();
	}
	
	// 日期选择对话框的 DateSet 事件监听器
		private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() { //

			@Override
			public void onDateSet(DatePicker arg0, int year, int month, int day) {
				
				if (mbStartOrEnd)
				{
					mStartYear = year;
					mStartMonth = month + 1;
					mStartDay = day;
					
					String strM = (mStartMonth < 10) ? ("0"+mStartMonth) : (""+mStartMonth);
					String strD = (mStartDay < 10) ? ("0"+mStartDay) : (""+mStartDay);
					mStartDate = String.format("%d%s%s", mStartYear, strM, strD);
					
					mEdit_StartDate.setText(ViewTools.formatDate(mStartDate));
				}else
				{
					mEndYear = year;
					mEndMonth = month + 1;
					mEndDay = day;
					
					String strM = (mEndMonth < 10) ? ("0"+mEndMonth) : (""+mEndMonth);
					String strD = (mEndDay < 10) ? ("0"+mEndDay) : (""+mEndDay);
					mEndDate = String.format("%d%s%s", mEndYear, strM, strD);
					
					mEdit_EndDate.setText(ViewTools.formatDate(mEndDate));
				}
			}
		};
		
		protected void showProgress(String msg) {
			closeProgress();

			if (mProgress == null) {
				mProgress = new Dialog(this, R.style.AlertDialogStyle);
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
}
