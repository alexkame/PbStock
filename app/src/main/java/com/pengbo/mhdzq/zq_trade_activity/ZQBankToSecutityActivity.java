package com.pengbo.mhdzq.zq_trade_activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import com.pengbo.mhdcx.ui.trade_activity.TradeMoreChooseBankActivity;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.fragment.ZQBankQueryFragment;
import com.pengbo.mhdzq.fragment.ZQBankToZQFragment;
import com.pengbo.mhdzq.fragment.ZQBankYHYEFragment;
import com.pengbo.mhdzq.fragment.ZQZQToBankFragment;
import com.pengbo.mhdzq.fragment.ZhengQuanFragment;
import com.pengbo.mhdzq.main_activity.SplashActivity;
import com.pengbo.mhdzq.net.CMessageObject;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.TradeNetConnect;
import com.pengbo.mhdzq.trade.data.Trade_Define;
import com.pengbo.mhdzq.zq_activity.ZQLSCJActivity;
import com.pengbo.mhdzq.zq_activity.ZhengQuanActivity;

/**
 * 银行转账 
 * 
 * @author pobo
 * @date   2015-11-27 上午9:54:42
 * @className ZQBankToSecutityActivity.java
 * @verson 1.0.0
 */
public class ZQBankToSecutityActivity extends FragmentActivity implements OnClickListener , OnCheckedChangeListener,ZQOnBankFragmentListener{

	private final static String TAG = "BankSecuritiesTransferActivity";	

	public static final String KEY_MONEYARRAY = "Bank_MoneyArray";
	public static final String KEY_MONEYNAMEARRAY = "Bank_MoneyNameArray";
	public static final String KEY_PWDFLAGARRAY = "Bank_PWDFlagArray";
	public static final String KEY_PWDFLAG = "Bank_PWDFlag";
	public static final int REQUEST_CODE_BANK = 1000;
	
	public static final int BANK_TOZQ = 0;
	public static final int BANK_ZQTO = 1;
	public static final int BANK_YHYE = 2;
	public static final int BANK_QUERY = 3;
	
	private FragmentManager mFragmentMgr = null;
	private ZQBankToZQFragment mBankToZQFrag = null;
	private ZQZQToBankFragment mZQToBankFrag = null;
	private ZQBankYHYEFragment mBankYHYEFrag = null;
	private ZQBankQueryFragment mBankQueryFrag = null;
	private Fragment mCurrentFragment = null;
	private int mCurrentPage = BANK_TOZQ;
	
	private ImageView mBack;//返回按钮 
	private TextView mMiddle; //中间的标题 
	private RadioGroup mRadioGroup;//  RadioButton
	private MyApp mMyApp;
	private PBSTEP mStepYZZZ;
	private PBSTEP mStepMoney;
	
	private ArrayList< Map<String, String> > mArrayBank;
	private ArrayList<String> mArrayBankName;
	private int m_nSelBankIndex = 0;
	private int m_nPassword1Flag; //第一个密码：>0表示需要输入密码(1为银行密码，2为资金密码)
	private int m_nPassword2Flag; //第二个密码（如果有的话）：>0表示需要输入密码(1为银行密码，2为资金密码)
	private Context mContext;
	
	private int	m_RequestCode[];//0.查询银行账号  1.转账  2.银行余额查询 3.查询证券公司资金信息 4.查询银证转账流水
	public Dialog mProgress;
	// 消息 处理
		private Handler mHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {

				int nFrameType = msg.arg1;
				int nRequestCode = msg.arg2;

				switch (msg.what) {
				case TradeNetConnect.MSG_UPDATE_DATA: // 交易
				{
					closeProgress();
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
							Toast.makeText(ZQBankToSecutityActivity.this, "登录失败", 5000)
									.show();
						} else {
							// login success
							mMyApp.mTradeData.mTradeLoginFlag = true;
							closeProgress();

						}
						
					}
					else if (nFrameType == Trade_Define.Func_YHZH)//
					{
						if (nRequestCode == m_RequestCode[0])
						{
							if (aMsgObject.nErrorCode < 0)
							{
								showDialog(aMsgObject.errorMsg);
							}else
							{
								mMyApp.mTradeData.GetYZZZ(mStepYZZZ);
								parseBankInfo(mStepYZZZ);
								if (mBankToZQFrag != null && mCurrentPage == BANK_TOZQ)
								{
									mBankToZQFrag.updateBankInfo(mArrayBankName, mArrayBank);
								}else if (mZQToBankFrag != null && mCurrentPage == BANK_ZQTO)
								{
									mZQToBankFrag.updateBankInfo(mArrayBankName, mArrayBank);
								}else if (mBankYHYEFrag != null && mCurrentPage == BANK_YHYE)
								{
									mBankYHYEFrag.updateBankInfo(mArrayBankName, mArrayBank);
								}
							}
							
						}
					}else if((nFrameType == Trade_Define.Func_ZQZYH || nFrameType == Trade_Define.Func_YHZZQ)&& nRequestCode == m_RequestCode[1]) //转账
				    {
						if (aMsgObject.nErrorCode < 0)
						{
							showDialog(aMsgObject.errorMsg);
						}else
						{
							mMyApp.mTradeData.GetYZZZ(mStepYZZZ);
							String strMsg = mStepYZZZ.GetFieldValueString(STEP_Define.STEP_FHXX);
							
							showDialog(strMsg);
						}
				    }
				    else if(nFrameType == Trade_Define.Func_YHYE && nRequestCode == m_RequestCode[2]) //银行余额查询
				    {
				    	if (aMsgObject.nErrorCode < 0)
						{
				    		showDialog(aMsgObject.errorMsg);
						}else
						{
							mMyApp.mTradeData.GetYZZZ(mStepYZZZ);
							String strMsg = mStepYZZZ.GetFieldValueString(STEP_Define.STEP_FHXX);
							
							showDialog(strMsg);
						}
				    }
				    else if(nFrameType == Trade_Define.Func_Money && nRequestCode == m_RequestCode[3]) //查询证券公司资金信息
				    {
				    	if (aMsgObject.nErrorCode < 0)
				    	{
				    		return;
				    	}
				        if (mZQToBankFrag != null && mCurrentPage == BANK_ZQTO)
				        {
				        	mMyApp.mTradeData.GetMoney(mStepMoney);
				        	String str = mStepMoney.GetFieldValueString(STEP_Define.STEP_KQZJ);
				        	mZQToBankFrag.updateZJInfo(str);
				        }
				    }
				    else if(nFrameType == Trade_Define.Func_ZZLS && nRequestCode == m_RequestCode[4]) //查询银证转账流水
				    {
				    	if (aMsgObject.nErrorCode < 0)
						{
				    		showDialog(aMsgObject.errorMsg);
						}else
						{
							mMyApp.mTradeData.GetYZZZ(mStepYZZZ);
							if (mBankQueryFrag != null && mCurrentPage == BANK_QUERY)
							{
								mBankQueryFrag.updateBankLS(mStepYZZZ);
							}
						}
				    }
				}
					break;
					
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
		
		mMyApp = (MyApp) this.getApplication();
		
		boolean isRestart = false;
		if(mMyApp.mCodeTableMarketNum <= 0) {
			isRestart = true;
		}
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zq_bank_security_transfer_activity);
		
		mContext = this;
		m_RequestCode = new int[5];
		mStepYZZZ = new PBSTEP();
		mStepMoney = new PBSTEP();
		mArrayBank = new ArrayList< Map<String,String> >();
		mArrayBankName = new ArrayList<String>();
		initView();
		
		if(isRestart) {
			onAppRestore();
		}
		
		requestBankInfo();
	}
	
	public void onAppRestore() {
		AppActivityManager.getAppManager().finishAllActivity();
		Intent i = new Intent(this, SplashActivity.class);
		this.startActivity(i);
		this.finish();
	}

	
	public void onResume() {
		ViewTools.isShouldForegraund = true;
		mMyApp.mTradeNet.setMainHandler(mHandler);
		super.onResume();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_CODE_BANK://
			if (resultCode == TradeMoreChooseBankActivity.REQUEST_CODE_MMLX) {
				int index = data.getIntExtra("PWType", 0);
				if (this.mBankToZQFrag != null && this.mCurrentPage == BANK_TOZQ)
				{
					mBankToZQFrag.updateCurrentBankIndex(index);
				}else if (this.mZQToBankFrag != null && this.mCurrentPage == BANK_ZQTO)
				{
					mZQToBankFrag.updateCurrentBankIndex(index);
				}else if (this.mBankYHYEFrag != null && this.mCurrentPage == BANK_YHYE)
				{
					mBankYHYEFrag.updateCurrentBankIndex(index);
				}
			}
			break;

		default:
			break;
		}
	}
	
	/**
	 * 初始化控件 
	 */
	private void initView() {
		//初始化抬头中的按钮及事件  
		mBack = (ImageView) this.findViewById(R.id.img_public_black_head_title_left_blue_back);
		mMiddle = (TextView) this.findViewById(R.id.tv_public_black_head_title_middle_name);
		mBack.setVisibility(View.VISIBLE);
		mMiddle.setVisibility(View.VISIBLE);
		mBack.setOnClickListener(this);
		mMiddle.setText("银证转账");
		
		
		mFragmentMgr = getSupportFragmentManager();
		//fm.beginTransaction().add(R.id.zq_framelayout_bank, new ZQToBankFragment()).commit();		
		//包含头部的 Radiogroup
		mRadioGroup=(RadioGroup) this.findViewById(R.id.zq_rg_bank_radiogroup);		
		mRadioGroup.setOnCheckedChangeListener(this);
		
		switch(mCurrentPage){
		case BANK_TOZQ:
			if (mBankToZQFrag == null)
			{
				mBankToZQFrag = new ZQBankToZQFragment();
			}
			addFragment(R.id.zq_framelayout_bank, mBankToZQFrag);
			((RadioButton) mRadioGroup.findViewById(R.id.zq_rb_bank_security1)).setChecked(true); 
			break;
			
		case BANK_ZQTO:
			if (mZQToBankFrag == null)
			{
				mZQToBankFrag = new ZQZQToBankFragment();
			}
		    addFragment(R.id.zq_framelayout_bank, mZQToBankFrag);
		    ((RadioButton) mRadioGroup.findViewById(R.id.zq_rb_bank_security2)).setChecked(true);
			break;
			
		case BANK_YHYE:
			if (mBankYHYEFrag == null)
			{
				mBankYHYEFrag = new ZQBankYHYEFragment();
			}
			addFragment(R.id.zq_framelayout_bank, mBankYHYEFrag);
			((RadioButton) mRadioGroup.findViewById(R.id.zq_rb_bank_security3)).setChecked(true);
			break;
			
		case BANK_QUERY:
			if (mBankQueryFrag == null)
			{
				mBankQueryFrag = new ZQBankQueryFragment();
			}
			addFragment(R.id.zq_framelayout_bank, mBankQueryFrag);
			((RadioButton) mRadioGroup.findViewById(R.id.zq_rb_bank_security5)).setChecked(true);
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_public_black_head_title_left_blue_back:
			ZQBankToSecutityActivity.this.finish();
			break;

		default:
			break;
		}
	}
	
	/*
	 * Fragment switch
	 * 
	 */
	private void turnToFragment(Fragment fromFragment,
			Fragment toFragment, Bundle args) {

		if (fromFragment == null)
		{
			addFragment(R.id.zq_framelayout_bank, toFragment);
			mCurrentFragment = toFragment;
			return;
		}
		
		if (mFragmentMgr == null)
        {
        	mFragmentMgr = getSupportFragmentManager();
        }
		String fromTag = fromFragment.getClass().getSimpleName();
		String toTag = toFragment.getClass().getSimpleName();
		if(fromTag.equalsIgnoreCase(toTag))
		{
			return;
		}
		
		if (args != null && !args.isEmpty()) {
			toFragment.getArguments().putAll(args);
		}
		
		FragmentTransaction transaction = mFragmentMgr.beginTransaction();

		transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
				android.R.anim.fade_in, android.R.anim.fade_out);

		if (!toFragment.isAdded()) {
			transaction.hide(fromFragment).add(R.id.zq_framelayout_bank, toFragment, toTag);
		} else {
			transaction.hide(fromFragment).show(toFragment);
		}
		mCurrentFragment = toFragment;
		transaction.commitAllowingStateLoss();
	}
	
	private void addFragment(int id, Fragment fragment)  
    {
        if (mFragmentMgr == null)
        {
        	mFragmentMgr = getSupportFragmentManager();
        }
        String tag = fragment.getClass().getSimpleName();
		
        FragmentTransaction transaction = mFragmentMgr.beginTransaction();
        if(fragment.isAdded())
        {
        	transaction.show(fragment);
        }else
        {
            transaction.add(id, fragment, tag);
        }
        mCurrentFragment = fragment;
        transaction.commitAllowingStateLoss();
    }
	
	public void setCurrentPage(int page) {
		if (mCurrentPage != page)
		{
			mCurrentPage = page;
			if (mCurrentPage == BANK_ZQTO)
			{
				requestZJYE();
			}else if (mCurrentPage == BANK_QUERY)
			{
				requestZJLS();
			}
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {

		switch (checkedId) {
		case R.id.zq_rb_bank_security1:
			if (mBankToZQFrag == null)
			{
				mBankToZQFrag = new ZQBankToZQFragment();
			}
			turnToFragment(mCurrentFragment, mBankToZQFrag, null);
			mBankToZQFrag.updateBankInfo(mArrayBankName, mArrayBank);
			setCurrentPage(BANK_TOZQ);
			//fm.beginTransaction().replace(R.id.zq_framelayout_bank, new BankToZQFragment()).commit();
			break;
			
		case R.id.zq_rb_bank_security2:
			if (mZQToBankFrag == null)
			{
				mZQToBankFrag = new ZQZQToBankFragment();
			}
			turnToFragment(mCurrentFragment, mZQToBankFrag, null);
			mZQToBankFrag.updateBankInfo(mArrayBankName, mArrayBank);
			setCurrentPage(BANK_ZQTO);
			//fm.beginTransaction().replace(R.id.zq_framelayout_bank, new ZQToBankFragment()).commit();
			break;
			
		case R.id.zq_rb_bank_security3:
			if (mBankYHYEFrag == null)
			{
				mBankYHYEFrag = new ZQBankYHYEFragment();
			}
			turnToFragment(mCurrentFragment, mBankYHYEFrag, null);
			mBankYHYEFrag.updateBankInfo(mArrayBankName, mArrayBank);
			setCurrentPage(BANK_YHYE);
			//fm.beginTransaction().replace(R.id.zq_framelayout_bank, new BankYHYEFragment()).commit();
			break;
			
		case R.id.zq_rb_bank_security5:
			if (mBankQueryFrag == null)
			{
				mBankQueryFrag = new ZQBankQueryFragment();
			}
			turnToFragment(mCurrentFragment, mBankQueryFrag, null);
			setCurrentPage(BANK_QUERY);
			//fm.beginTransaction().replace(R.id.zq_framelayout_bank, new BankQueryFragment()).commit();
			break;
		}
	}
	
	//查询银行信息
	private void requestBankInfo()
	{
		mMyApp.mTradeNet.setMainHandler(mHandler);
		m_RequestCode[0] = mMyApp.mTradeNet.Request_ListQuery(Trade_Define.Func_YHZH, null);
	}

	//查询证券公司资金信息
	private void requestZJYE()
	{
		showProgress();
		mMyApp.mTradeNet.setMainHandler(mHandler);
		String param = "";
		param = String.format("%d:%s", STEP_Define.STEP_HBDM, Trade_Define.MType_RMB);
	    m_RequestCode[3] = mMyApp.mTradeNet.Request_ListQuery(Trade_Define.Func_Money, param);
	}
	
	//查询银证转账流水
	private void requestZJLS()
	{
		showProgress();
		mMyApp.mTradeNet.setMainHandler(mHandler);
		m_RequestCode[4] = mMyApp.mTradeNet.Request_ListQuery(Trade_Define.Func_ZZLS, null);
	}
	
	private boolean parseBankInfo(PBSTEP aStep)
	{
		//目前只支持人民币，若有币种选择，用上面注释的parseBankInfo
		mArrayBank.clear();
		mArrayBankName.clear();
	    
		String account = "";
		int nCount = aStep.GetRecNum();
		for (int i = 0; i < nCount; i++) {
			aStep.GotoRecNo(i);
			account = aStep.GetFieldValueString(STEP_Define.STEP_YHMC);

			if (!account.isEmpty() && !mArrayBankName.contains(account))
			{
				mArrayBankName.add(account);
			}
		}

		for (int m = 0; m < mArrayBankName.size(); m++)
		{
			Map<String, String> tempMap = new HashMap<String, String>();
			
			account = mArrayBankName.get(m);
			tempMap.put(String.format("Bank_%d", STEP_Define.STEP_YHMC), account);
			
			int nPwdFlag = 0xFFFF;
			for (int i = 0; i < nCount; i++)
			{
				aStep.GotoRecNo(i);
				String tmpAccount = aStep.GetFieldValueString(STEP_Define.STEP_YHMC);
				
				if (!tmpAccount.isEmpty() && account.equals(tmpAccount)) {
					String value = aStep.GetFieldValueString(STEP_Define.STEP_HBDM);
	                //目前只支持人民币
					if (value.equals(Trade_Define.MType_RMB))
					{
						tempMap.put(String.format("Bank_%d", STEP_Define.STEP_HBDM), value);

	                    int nSubFlag = 0;
	                    nPwdFlag = 0;
	                    nSubFlag = aStep.GetFieldValueInt(STEP_Define.STEP_YECXMMBZ);
	                    nPwdFlag += (nSubFlag&0xF);
	                    nPwdFlag = nPwdFlag<<4;
	                    nSubFlag = aStep.GetFieldValueInt(STEP_Define.STEP_ZJMMBZ);
	                    nPwdFlag += (nSubFlag&0xF);
	                    nPwdFlag = nPwdFlag<<4;
	                    nSubFlag = aStep.GetFieldValueInt(STEP_Define.STEP_YHMMBZ);
	                    nPwdFlag += (nSubFlag&0xF);
	                    tempMap.put(KEY_PWDFLAG, String.format("%d", nPwdFlag));
	                    
	                    value = aStep.GetFieldValueString(STEP_Define.STEP_YHBM);
	                    tempMap.put(String.format("Bank_%d", STEP_Define.STEP_YHBM), value);
	                    
	                    value = aStep.GetFieldValueString(STEP_Define.STEP_YHZH);
	                    tempMap.put(String.format("Bank_%d", STEP_Define.STEP_YHZH), value);

	                    value = aStep.GetFieldValueString(STEP_Define.STEP_ZJZH);
	                    tempMap.put(String.format("Bank_%d", STEP_Define.STEP_ZJZH), value);

	                    break;
					}
				}
			}
			mArrayBank.add(tempMap);
		}
		m_nSelBankIndex = 0;
	    //更新list
		
		return true;
	}

	private void proc_MSG_LOCK(Message msg)
    {
    	L.d(TAG, "proc_MSG_LOCK");
    	
    	new com.pengbo.mhdzq.widget.AlertDialog(this)
		.builder()
		.setTitle("交易登录超时")
		.setMsg("交易登录在线时间过长，需要重新登录！")
		.setCancelable(false)
		.setCanceledOnTouchOutside(false)
		.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(View v) {
				processNetLose();
			}
		}).show();
    }
	
	private void proc_MSG_DISCONNECT(Message msg)
    {
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
		GlobalNetProgress.HQRequest_MultiCodeInfoPush(mMyApp.mHQPushNet, null, 0, 0);
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

		if (AppActivityManager.getAppManager().getZQTradeDetailActivity() != null)
		{
			AppActivityManager.getAppManager().finishActivity(
					AppActivityManager.getAppManager().getZQTradeDetailActivity());
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

	
	//登录成功直接进入主界面
	private void showDialog(String strMsg) {

		AlertDialog alertDialog = new AlertDialog.Builder(mContext)
			.setTitle("提示")
			.setMessage(strMsg)
			.setCancelable(true)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).create();
		alertDialog.show();
	}

	@Override
	public void requestYZZZ_YZQ(int bankIndex, String zzje, String yhmm,
			String zjmm) {
		if (bankIndex >= mArrayBank.size()) {
	        L.e(TAG, "ERROR:requestYZZZ_YZQ 银行信息错误");
	        showDialog("银行信息错误!");
	        return;
	    }
		showProgress();
		m_nSelBankIndex = bankIndex;
		
		Map<String,String> temp = new HashMap<String,String>();
		temp = mArrayBank.get(m_nSelBankIndex);

		//银行编码
	    String yhbm = temp.get(String.format("Bank_%d", STEP_Define.STEP_YHBM));
	    //银行账号
	    String yhzh = temp.get(String.format("Bank_%d", STEP_Define.STEP_YHZH));
	    //资金账号
	    String zjzh = temp.get(String.format("Bank_%d", STEP_Define.STEP_ZJZH));
	    //开通币种
	    String hbdm = temp.get(String.format("Bank_%d", STEP_Define.STEP_HBDM));
		
		mMyApp.mTradeNet.setMainHandler(mHandler);
		m_RequestCode[1] = mMyApp.mTradeNet.Request_YZZZ_YZQ(zjzh, yhbm, yhzh, yhmm, zjmm, hbdm, zzje);
		
	}

	@Override
	public void requestYZZZ_QZY(int bankIndex, String zzje, String yhmm,
			String zjmm) {
		
		if (bankIndex >= mArrayBank.size()) {
	        L.e(TAG, "ERROR:requestYZZZ_QZY 银行信息错误");
	        showDialog("银行信息错误!");
	        return;
	    }
		showProgress();
		m_nSelBankIndex = bankIndex;
		
		Map<String,String> temp = new HashMap<String,String>();
		temp = mArrayBank.get(m_nSelBankIndex);

		//银行编码
	    String yhbm = temp.get(String.format("Bank_%d", STEP_Define.STEP_YHBM));
	    //银行账号
	    String yhzh = temp.get(String.format("Bank_%d", STEP_Define.STEP_YHZH));
	    //资金账号
	    String zjzh = temp.get(String.format("Bank_%d", STEP_Define.STEP_ZJZH));
	    //开通币种
	    String hbdm = temp.get(String.format("Bank_%d", STEP_Define.STEP_HBDM));
		
		mMyApp.mTradeNet.setMainHandler(mHandler);
		m_RequestCode[1] = mMyApp.mTradeNet.Request_YZZZ_QZY(zjzh, yhbm, yhzh, yhmm, zjmm, hbdm, zzje);
		
		
	}

	@Override
	public void request_YHYE(int bankIndex, String yhmm, String zjmm) {
		
		if (bankIndex >= mArrayBank.size()) {
	        L.e(TAG, "ERROR:requestYHYE 银行信息错误");
	        showDialog("银行信息错误!");
	        return;
	    }
		showProgress();
		m_nSelBankIndex = bankIndex;
		
		Map<String,String> temp = new HashMap<String,String>();
		temp = mArrayBank.get(m_nSelBankIndex);

		//银行编码
	    String yhbm = temp.get(String.format("Bank_%d", STEP_Define.STEP_YHBM));
	    //银行账号
	    String yhzh = temp.get(String.format("Bank_%d", STEP_Define.STEP_YHZH));
	    //资金账号
	    String zjzh = temp.get(String.format("Bank_%d", STEP_Define.STEP_ZJZH));
	    //开通币种
	    String hbdm = temp.get(String.format("Bank_%d", STEP_Define.STEP_HBDM));
		
		mMyApp.mTradeNet.setMainHandler(mHandler);
		m_RequestCode[2] = mMyApp.mTradeNet.Request_YHYE(zjzh, yhbm, yhzh, yhmm, zjmm, hbdm);
		
	}
	
	protected void showProgress() {
		closeProgress();

		if (mProgress == null) {
			mProgress = new Dialog(this, R.style.ProgressDialogStyle);
			mProgress.setContentView(R.layout.list_loading);
			TextView tv = (TextView) mProgress.findViewById(R.id.loading_text);
			tv.setText("请求中，请稍后......");
			mProgress.setCancelable(true);
		}
		L.i(TAG, "showProgress--->" + this.toString());
		mProgress.show();
	}

	protected void closeProgress() {

		if (mProgress != null && mProgress.isShowing()) {
			L.i(TAG, "closeProgress--->" + this.toString());
			mProgress.cancel();
			mProgress.dismiss();
			mProgress = null;
		}
	}

	@Override
	protected void onPause() {
		mMyApp.mTradeNet.setMainHandler(null);
		closeProgress();
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		mMyApp.mTradeNet.setMainHandler(null);
		super.onDestroy();
	}

}
