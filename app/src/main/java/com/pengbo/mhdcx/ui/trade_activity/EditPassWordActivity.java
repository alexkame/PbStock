package com.pengbo.mhdcx.ui.trade_activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.net.CMessageObject;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.tools.MIniFile;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.trade.data.TradeNetConnect;
import com.pengbo.mhdzq.trade.data.Trade_Define;
import com.pengbo.mhdzq.zq_activity.HdActivity;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.ui.main_activity.MainTabActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


public class EditPassWordActivity extends HdActivity implements OnClickListener{

	private static final String key_type = "pw_type";
	private static final String key_title1 = "pw_title1";
	private static final String key_title2 = "pw_title2";
	private static final String key_title3 = "pw_title3";
	private static final String key_hint1 = "pw_hint1";
	private static final String key_hint2 = "pw_hint2";
	private static final String key_hint3 = "pw_hint3";
	private final static int REQUEST_CODE_MMLX = 10001;
	
	private MyApp mMyApp;
	public LinearLayout mType;// password  type
	public TextView mBack;
	private Button mBtnSave;
	private TextView mTV_pwtype,
					 mTV_oldpw_name, mEdit_oldpw,
					 mTV_newpw_name, mEdit_newpw,
					 mTV_newpw_confirm, mEdit_newpw_confirm;
	private TextView[] mTVPWTitles;
	private EditText[] mEditPWs;
	private int mRequestCode;
	private int mSelectPWTypeIndex;

	private String[] mTitleKeys = {key_title1, key_title2, key_title3};
	private String[] mHintKeys = {key_hint1, key_hint2, key_hint3};
	private ArrayList< Map<String, String> > mArrayAttr;
	private ArrayList<String> mStrPWTypes; //密码类型
	private String mOldPW, mNewPW, mNewConfirmPW;
	private int mMMLB;
	private Context mContext;
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
						new com.pengbo.mhdcx.widget.AlertDialog(EditPassWordActivity.this).builder().setTitle("登录")
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
				else if (nFrameType == Trade_Define.Func_XGMM)//modify password
				{
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
					
					new com.pengbo.mhdcx.widget.AlertDialog(mContext).builder().setTitle(mContext.getString(R.string.IDS_TiShi))
					.setMsg(mContext.getString(R.string.IDS_XiuGaiChengGone))
					.setCancelable(false)
					.setCanceledOnTouchOutside(false)
					.setPositiveButton(mContext.getString(R.string.IDS_QueDing), new OnClickListener() {
						@Override
						public void onClick(View v) {
							mMyApp.setHQPushNetHandler(null);
							mMyApp.mTradeNet.closeConnect();
							mMyApp.mTradeData.mTradeLoginFlag = false;
							mMyApp.setCurrentOption(null);
							mMyApp.mTradeData.clearStepData();
							
							Intent intent = new Intent();
							MainTabActivity act = AppActivityManager.getAppManager().getMainTabActivity();
							
							if (act != null) {
								act.setChangePage(MainTabActivity.PAGE_TRADE);
							}
							intent.setClass(EditPassWordActivity.this, MainTabActivity.class);
							startActivity(intent);
							EditPassWordActivity.this.finish();
						}
					}).show();
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
		mContext = this;
		setContentView(R.layout.edit_password);
		mMyApp = (MyApp) this.getApplication();
		mArrayAttr = new ArrayList< Map<String, String> >();
		mStrPWTypes = new ArrayList<String>();
		mSelectPWTypeIndex = 0;
		initView();
		readSettingFromTradeConfig();
	}
	
	@Override
	protected void onResume()
	{
		mMyApp.mTradeNet.setMainHandler(mHandler);
		updateView();
		super.onResume();
	}
	
	@Override
	protected void onDestroy()
	{
		mMyApp.mTradeNet.setMainHandler(null);
		super.onDestroy();
	}
	
	private void initView() {
		mTV_pwtype = (TextView) this.findViewById(R.id.id_tv_pwtype);
		
		TextView[] textViews = {
				(TextView) findViewById(R.id.id_tv_oldpw_name), 
				(TextView) findViewById(R.id.id_tv_newpw_name),
				(TextView) findViewById(R.id.id_tv_newpw_confirm),
		};
		mTVPWTitles = textViews;
		textViews = null;
		
		EditText[] editTexts = {
				(EditText) findViewById(R.id.id_edit_oldpw), 
				(EditText) findViewById(R.id.id_edit_newpw),
				(EditText) findViewById(R.id.id_edit_newpw_confirm),
		};
		mEditPWs = editTexts;
		editTexts = null;
	
		mType=(LinearLayout) this.findViewById(R.id.password_type);
		mType.setOnClickListener(this);
		
		mBack=(TextView) this.findViewById(R.id.back);
		mBack.setOnClickListener(this);
		
		mBtnSave = (Button) this.findViewById(R.id.save_edit_password);
		mBtnSave.setOnClickListener(this);
	}
	
	private void updateView()
	{
		if (mSelectPWTypeIndex < mStrPWTypes.size())
		{
			mTV_pwtype.setText(mStrPWTypes.get(mSelectPWTypeIndex));
			int count = mTitleKeys.length;
			for (int i = 0; i < count; i++)
			{
				mTVPWTitles[i].setText(this.mArrayAttr.get(mSelectPWTypeIndex).get(mTitleKeys[i]));
				mEditPWs[i].setHint(this.mArrayAttr.get(mSelectPWTypeIndex).get(mHintKeys[i]));
			}
			
		}else
		{
			mTV_pwtype.setText("");
		}
	}
	
	private void readSettingFromTradeConfig()
	{
//		类别1=交易密码,0
//		输入框1_1=原交易密码,请输入原交易密码
//		输入框1_2=新交易密码,请输入新交易密码
//		输入框1_3=新密码确认,请再输入一次
		
		String key = String.format("f_%d", 6023);
		String strName = "";
		String strValue = "";
		String strTemp = "";
		mArrayAttr.clear();
		mStrPWTypes.clear();
		MIniFile iniFile = new MIniFile();
		iniFile.setFilePath(getApplicationContext(), MyApp.TRADE_CONFIGPATH);
		for(int i = 0; i < 10; i++)
		{
			
			strName = String.format("类别%d", i+1);
			strValue = iniFile.ReadString(key, strName, "");
			
			if(strValue.isEmpty())
			{
				break;
			}
			strTemp = STD.GetValue(strValue, 1, ',');
			if (strTemp.isEmpty())
			{
				continue;
			}
			mStrPWTypes.add(strTemp);
			
			String strType = STD.GetValue(strValue, 2, ',');
			Map<String, String> tempMap = new HashMap<String, String>();
			tempMap.put(key_type, strType);
			
			for (int j = 0; j < 3; j++)
			{
				strName = String.format("输入框%d_%d", i+1, j+1);
				strValue = iniFile.ReadString(key, strName, "");
				
				strTemp = STD.GetValue(strValue, 1, ',');
				if(strTemp.isEmpty())
				{
					continue;
				}
				tempMap.put(this.mTitleKeys[j], strTemp);
				
				strTemp = "";
				strTemp = STD.GetValue(strValue, 2, ',');
				if(strTemp.isEmpty())
				{
					continue;
				}
				tempMap.put(this.mHintKeys[j], strTemp);
			} //for (int j = 0; j < 3; j++)
			this.mArrayAttr.add(tempMap);
		} //for(int i = 0; i < 10; i++)
	}
	
	private void requestXGMM()
	{
		mMyApp.mTradeNet.setMainHandler(mHandler);
		mRequestCode = mMyApp.mTradeNet.Request_XGMM(mOldPW, mNewPW, mMMLB);	
	 
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_CODE_MMLX://交易模式 
			if (resultCode == TradeMoreChooseBankActivity.REQUEST_CODE_MMLX) {
				this.mSelectPWTypeIndex = data.getIntExtra("PWType", 0);
			}
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save_edit_password:
		{
			mOldPW = mEditPWs[0].getText().toString();
			mNewPW = mEditPWs[1].getText().toString();
			mNewConfirmPW = mEditPWs[2].getText().toString();
			
			if (mOldPW.isEmpty())
			{
				new com.pengbo.mhdcx.widget.AlertDialog(this)
				.builder()
				.setTitle("修改密码")
				.setMsg("旧密码未输入，请重新输入！")
				.setCancelable(false)
				.setCanceledOnTouchOutside(false)
				.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
						return;
					}
				}).show();
			}else if (mNewPW.isEmpty())
			{
				new com.pengbo.mhdcx.widget.AlertDialog(this)
				.builder()
				.setTitle("修改密码")
				.setMsg("新密码未输入，请重新输入！")
				.setCancelable(false)
				.setCanceledOnTouchOutside(false)
				.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
						return;
					}
				}).show();
			}else if (mNewConfirmPW.isEmpty())
			{
				new com.pengbo.mhdcx.widget.AlertDialog(this)
				.builder()
				.setTitle("修改密码")
				.setMsg("密码确认未输入，请重新输入！")
				.setCancelable(false)
				.setCanceledOnTouchOutside(false)
				.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
						return;
					}
				}).show();
			} else if (!mNewPW.equals(mNewConfirmPW))
			{
				new com.pengbo.mhdcx.widget.AlertDialog(this)
				.builder()
				.setTitle("修改密码")
				.setMsg("确认密码和新密码不匹配，请重新输入！")
				.setCancelable(false)
				.setCanceledOnTouchOutside(false)
				.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
						return;
					}
				}).show();
			}else
			{
				requestXGMM();
			}
		}
			break;
		case R.id.password_type:
			Intent intent = new Intent();
			intent.setClass(this, TradeMoreChooseBankActivity.class);

			intent.putExtra("mmlx", mStrPWTypes);
			intent.putExtra("pwtype", mSelectPWTypeIndex);

			startActivityForResult(intent, REQUEST_CODE_MMLX);

			break;

		case R.id.back:
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
				mMyApp.mTradeNet.closeConnect();
				mMyApp.mTradeData.mTradeLoginFlag = false;
				mMyApp.setCurrentOption(null);
				mMyApp.mTradeData.clearStepData();
				
				Intent intent = new Intent();
				MainTabActivity act = AppActivityManager.getAppManager().getMainTabActivity();
				
				if (act != null) {
					act.setChangePage(MainTabActivity.PAGE_TRADE);
				}
				intent.setClass(EditPassWordActivity.this, MainTabActivity.class);
				startActivity(intent);
				EditPassWordActivity.this.finish();
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
			showProgress();
		}
	}

	private void processNetLose()
	{

		mMyApp.setHQPushNetHandler(null);
		mMyApp.mTradeNet.closeConnect();
		mMyApp.mTradeData.mTradeLoginFlag = false;
		mMyApp.setCurrentOption(null);
		mMyApp.mTradeData.clearStepData();
		
		Intent intent = new Intent();
		MainTabActivity act = AppActivityManager.getAppManager().getMainTabActivity();
		
		if (act != null) {
			act.setChangePage(MainTabActivity.PAGE_TRADE);
		}
		intent.setClass(EditPassWordActivity.this, MainTabActivity.class);
		startActivity(intent);
		EditPassWordActivity.this.finish();

	}
	
	protected void showProgress() {
		closeProgress();

		if (mProgress == null) {
			mProgress = new Dialog(this, R.style.AlertDialogStyle);
			mProgress.setContentView(R.layout.list_loading);
			TextView tv = (TextView) mProgress.findViewById(R.id.loading_text);
			tv.setText("查询中，请稍后......");
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
