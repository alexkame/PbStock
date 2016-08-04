package com.pengbo.mhdcx.ui.main_activity;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.net.CMessageObject;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.MIniFile;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.trade.data.TradeNetConnect;
import com.pengbo.mhdzq.trade.data.Trade_Define;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.zq_activity.HdActivity;
import com.pengbo.mhdcx.widget.TimeButton;

/**
 * 账户登录界面 
 * @author pobo
 *
 */
public class AccountRegisterActivity extends HdActivity implements OnClickListener {

	private static final String TAG = "AccountRegisterActivity";
	
	private TextView mMiddle;// 抬头标题
	private TextView mSearch;// 搜索
	private EditText mEditPhone, mEditYZM;// 电话号码 验证码
	private TimeButton mTimeButton;
	private Button mBtn_OK;
	private MyApp mMyApp;
	private String mStrPhone = "", mStrVerifyCode = "";
	private ArrayList<String> mIPAdd;
	private String mSavedIPAdd = "";
	public Dialog mProgress;
	
	// 消息 处理
		private Handler mHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {

				int nFrameType = msg.arg1;
				int nRequestCode = msg.arg2;

				switch (msg.what) {
				case TradeNetConnect.MSG_UPDATE_DATA: // 交易
				{
					CMessageObject aMsgObject = null;
					if (msg.obj != null && (msg.obj instanceof CMessageObject)) {
						aMsgObject = (CMessageObject) msg.obj;
					} else {
						return;
					}

					if (nFrameType == 1)// 交易连接，交换密钥
					{
						if (aMsgObject.nErrorCode < 0) {
							
							//出错，弹出messagebox提醒用户
							new com.pengbo.mhdcx.widget.AlertDialog(getParent()).builder().setTitle("提示")
									.setMsg(aMsgObject.errorMsg)
									.setCancelable(false)
									.setCanceledOnTouchOutside(false)
									.setPositiveButton("确认", new OnClickListener() {
										@Override
										public void onClick(View v) {
										}
									}).show();
						}else
						{
							mMyApp.mTradeNet.setMainHandler(mHandler);
							mMyApp.mTradeNet.Request_GetSMSVerifyCode(mStrPhone, 6);
						}
						
					} else if (nFrameType == Trade_Define.Func_ZCSJHM) {
						closeProgress();
						if (aMsgObject.nErrorCode < 0) {
							
							//出错，弹出messagebox提醒用户
							new com.pengbo.mhdcx.widget.AlertDialog(getParent()).builder().setTitle("提示")
									.setMsg(aMsgObject.errorMsg)
									.setCancelable(false)
									.setCanceledOnTouchOutside(false)
									.setPositiveButton("确认", new OnClickListener() {
										@Override
										public void onClick(View v) {
										}
									}).show();
						} else {
							// login success
							L.i(TAG, "get sms verify code Successfully");
							Toast.makeText(getParent(), "请求验证码已发送！", Toast.LENGTH_SHORT).show();
						}
					}
					else if (nFrameType == Trade_Define.Func_YZSJZCM) {

						if (aMsgObject.nErrorCode < 0) {
							closeProgress();
							// 登陆出错，弹出messagebox提醒用户
							new com.pengbo.mhdcx.widget.AlertDialog(getParent()).builder().setTitle("提示")
									.setMsg(aMsgObject.errorMsg)
									.setCancelable(false)
									.setCanceledOnTouchOutside(false)
									.setPositiveButton("确认", new OnClickListener() {
										@Override
										public void onClick(View v) {
										}
									}).show();
						} else {
							L.i(TAG, "sms verify Successfully");
							
							PreferenceEngine.getInstance().savePhoneForVerify(mStrPhone);
							Intent intent = new Intent();
							intent.setClass(AccountRegisterActivity.this,
									TradeLoginActivity.class).addFlags(
									Intent.FLAG_ACTIVITY_CLEAR_TOP);
							
							// 把第一个Activity 转换成一个View
							View view = TradeGroupTabActivity.group
									.getLocalActivityManager()
									.startActivity("TradeLoginActivity", intent)
									.getDecorView();
							// 把View 添加到 大 ActivityGroup中
							TradeGroupTabActivity.group.setContentView(view);
							
							closeProgress();
						}
					}
				}
					break;
				case TradeNetConnect.MSG_DISCONNECT:
				{
					proc_MSG_DISCONNECT(msg);
				}
					break;
				case TradeNetConnect.MSG_TIMEOUT:
				{
					proc_MSG_TIMEOUT(msg);
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
		setContentView(R.layout.account_register_activity);
		mMyApp = (MyApp) this.getApplication();
		mIPAdd = new ArrayList<String>();
		initQSList();
		initView();
	}

	private void initView() {
		mMiddle = (TextView) this.findViewById(R.id.header_middle_textview);
		mMiddle.setText("交易注册");
		mSearch = (TextView) this.findViewById(R.id.header_right_search);
		mSearch.setVisibility(View.GONE);

		mEditPhone = (EditText) this.findViewById(R.id.edit_register_phonenum);

		mEditYZM = (EditText) this.findViewById(R.id.edit_register_yzm);
		mTimeButton = (TimeButton) this.findViewById(R.id.btn_register_hqyzm);
		mTimeButton.setTextAfter("秒后重新获取").setTextBefore("点击获取验证码").setLenght(60 * 1000);
		mTimeButton.setOnClickListener(this);
		
		mBtn_OK = (Button) this.findViewById(R.id.btn_sms_verify);
		mBtn_OK.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_register_hqyzm:
		{
			mStrPhone = this.mEditPhone.getText().toString();
			if (mStrPhone.isEmpty())
			{
				this.mTimeButton.setWorkFlag(false);
				new com.pengbo.mhdcx.widget.AlertDialog(getParent()).builder().setTitle("提示")
						.setMsg("手机号码不能为空，请重新输入！")
						.setCancelable(false)
						.setCanceledOnTouchOutside(false)
						.setPositiveButton("确认", new OnClickListener() {
							@Override
							public void onClick(View v) {
							}
						}).show();
				return;
			}
			if(this.mIPAdd.size() <= 0)
			{
				this.mTimeButton.setWorkFlag(false);
				new com.pengbo.mhdcx.widget.AlertDialog(getParent()).builder().setTitle("提示")
						.setMsg("验证服务器不存在！")
						.setCancelable(false)
						.setCanceledOnTouchOutside(false)
						.setPositiveButton("确认", new OnClickListener() {
							@Override
							public void onClick(View v) {
							}
						}).show();
				return;
			}
			mTimeButton.setWorkFlag(true);
			showProgress();
			mMyApp.mTradeNet.setMainHandler(mHandler);
			mMyApp.mTradeNet.initSocketThread();
			
		}
			break;
		case R.id.btn_sms_verify:
		{
			mStrPhone = this.mEditPhone.getText().toString();
			mStrVerifyCode = this.mEditYZM.getText().toString();
			if (mStrPhone.isEmpty() || mStrVerifyCode.isEmpty())
			{
				new com.pengbo.mhdcx.widget.AlertDialog(getParent()).builder().setTitle("提示")
						.setMsg("手机号码或验证码不能为空，请重新输入！")
						.setCancelable(false)
						.setCanceledOnTouchOutside(false)
						.setPositiveButton("确认", new OnClickListener() {
							@Override
							public void onClick(View v) {
							}
						}).show();
				return;
			}

			if(mMyApp.mTradeNet.IsConnected())
			{
				showProgress();
				mMyApp.mTradeNet.setMainHandler(mHandler);
				mMyApp.mTradeNet.Request_CheckSMSVerifyCode(mStrPhone, mStrVerifyCode);
			}else
			{
				new com.pengbo.mhdcx.widget.AlertDialog(getParent()).builder().setTitle("提示")
				.setMsg("未获取验证码，请重新点击获取验证码！")
				.setCancelable(false)
				.setCanceledOnTouchOutside(false)
				.setPositiveButton("确认", new OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				}).show();
			}
			
		}
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onPause() {
		mMyApp.mTradeNet.setMainHandler(null);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		mTimeButton.onDestroy();
		mMyApp.mTradeNet.setMainHandler(null);
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
	
	private void initQSList()
	{		
		MIniFile iniFile = new MIniFile();
		iniFile.setFilePath(getApplicationContext(), MyApp.TRADE_ADDRPATH);
		mIPAdd.clear();
		for (int i = 1; i < 50; i++)
		{
			String key = String.format("券商%d", i);
			
			String strIP = iniFile.ReadString(key, "ip", "");
			
			if (!strIP.isEmpty())
			{
			    String ipTemp = "";
			    for (int k = 0; k < 50; k++)
			    {
			    	ipTemp = STD.GetValue(strIP, k + 1, '|');
			        
			        if (ipTemp.isEmpty())
			        {
			            break;
			        }
			        mIPAdd.add(ipTemp);
			    }
			}else
			{
				break;
			}
		}
		
		setTradeNetAddress();
	}
	
	private void setTradeNetAddress()
	{
		int tnum = this.mIPAdd.size();
		int start = (int) (System.currentTimeMillis() & 0xff);
		String tradeAddr[] = new String[tnum];
		
		for (int i = 0; i < tnum; i++) {

			String addr = this.mIPAdd.get(i);
			if (addr.length() > 0) {
				tradeAddr[i] = addr;
			} else {
				break;
			}
		}
		mMyApp.resetAddrNum_Trade();
		for (int i = 0; i < tnum; i++, start++) {
			start %= tnum;
			mMyApp.addAddress_Trade(tradeAddr[start]);
		}
	}
	
	private void proc_MSG_TIMEOUT(Message msg)
    {
    	L.d(TAG, "proc_MSG_TIMEOUT");
    	closeProgress();
    	new com.pengbo.mhdcx.widget.AlertDialog(mMyApp.mMainTabActivity)
		.builder()
		.setTitle("提示")
		.setMsg("网络请求超时！")
		.setCancelable(false)
		.setCanceledOnTouchOutside(false)
		.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		}).show();
    }
	
	private void proc_MSG_DISCONNECT(Message msg)
    {
    	L.d(TAG, "proc_MSG_DISCONNECT");
    	closeProgress();
    	new com.pengbo.mhdcx.widget.AlertDialog(mMyApp.mMainTabActivity)
		.builder()
		.setTitle("提示")
		.setMsg("网络连接断开，请重试！")
		.setCancelable(false)
		.setCanceledOnTouchOutside(false)
		.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		}).show();
    }
	
	protected void showProgress() {
		closeProgress();

		if (mProgress == null) {
			mProgress = new Dialog(this.getParent(), R.style.AlertDialogStyle);
			mProgress.setContentView(R.layout.list_loading);
			TextView tv = (TextView) mProgress.findViewById(R.id.loading_text);
			tv.setText("请求发送中，请稍后......");
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
