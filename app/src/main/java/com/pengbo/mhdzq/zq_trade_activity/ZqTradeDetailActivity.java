package com.pengbo.mhdzq.zq_trade_activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.pengbo.mhdzq.widget.AlertDialog;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.HQ_Define;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.fragment.TradeZqBuyFragment;
import com.pengbo.mhdzq.fragment.TradeZqCancellOrderFragment;
import com.pengbo.mhdzq.fragment.TradeZqEntrustFragment;
import com.pengbo.mhdzq.fragment.TradeZqMoreFragment;
import com.pengbo.mhdzq.fragment.TradeZqSellFragment;
import com.pengbo.mhdzq.fragment.ZhengQuanFragment;
import com.pengbo.mhdzq.net.CMessageObject;
import com.pengbo.mhdzq.net.GlobalNetConnect;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.PTK_Define;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.TradeData;
import com.pengbo.mhdzq.trade.data.TradeLocalRecord;
import com.pengbo.mhdzq.trade.data.TradeNetConnect;
import com.pengbo.mhdzq.trade.data.TradeQSInfo;
import com.pengbo.mhdzq.trade.data.Trade_Define;
import com.pengbo.mhdzq.zq_activity.HdActivity;
import com.pengbo.mhdzq.zq_activity.ZhengQuanActivity;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class ZqTradeDetailActivity extends HdActivity implements
		OnClickListener, OnTradeFragmentListener, OnCheckedChangeListener {

	private String TAG = ZqTradeDetailActivity.class.getSimpleName();

	private FragmentManager mFragmentMgr = null;
	private RadioGroup mRadioGroup = null;
	private MyApp mMyApp;
	public Dialog mProgress;

	public static final int TRADE_BUY = 0;
	public static final int TRADE_SELL = 1;
	public static final int TRADE_CANCELL = 2;
	public static final int TRADE_ENTRUST = 3;
	public static final int TRADE_MORE = 4;

	private int mCurrentPage = TRADE_BUY;

	private Fragment mCurrentFragment = null;

	TradeZqBuyFragment mBuyFragment;
	TradeZqCancellOrderFragment mCancellFragment;
	TradeZqEntrustFragment mEntrustFragment;
	TradeZqSellFragment mSellFragment;
	TradeZqMoreFragment mMoreFragment;

	View mActivityView;

	ImageView mBack;
	ImageView mRefresh;

	TextView mHeadText;
	TextView mHeadacctText;
	/**
	 * 当日委托中可以撤单
	 */
	public PBSTEP mDRWTCDList;
	/**
	 * 当日委托
	 */
	public PBSTEP mDRWTList;
	/**
	 * 成交回报
	 */
	public PBSTEP mPBCJHB;
	/**
	 * 持仓列表
	 */
	public PBSTEP mHoldList;

	private boolean mLocalRefreshWT = true;

	private long mTimerForRequestDRWT = 0;
	private Timer mTimerRequestDRWT = null;

	public int mRequestCode[];

	private TagCodeInfo mTradeCode;

	private AlertDialog mAlertDlg;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			int nFrameType = msg.arg1;
			int nRequestCode = msg.arg2;

			switch (msg.what) {
			// 交易
			case TradeNetConnect.MSG_UPDATE_DATA: {
				CMessageObject aMsgObject = null;
				if (msg.obj != null && (msg.obj instanceof CMessageObject)) {
					aMsgObject = (CMessageObject) msg.obj;
				} else {
					return;
				}
				// 交易连接，交换密钥
				if (nFrameType == 1) {
					L.i(TAG,
							"Trade connect success, change secret key and request login");
					mMyApp.mTradeNet.setMainHandler(mHandler);

					String type = "0";

					mMyApp.mTradeData.mTradeAccount = mMyApp.mTradeAccount;
					mMyApp.mTradeData.mTradePassword = mMyApp.mTradePassword;

					mMyApp.mTradeData.mTradeVersion = mMyApp.mTradeVersion;
					mMyApp.mTradeData.mTradeLockTimeout = mMyApp.mTradeLockTimeout;
					mMyApp.mTradeData.mHQType = mMyApp.mHQType;

					mMyApp.mTradeNet.Request_Login(
							mMyApp.mTradeData.mTradeAccount,
							mMyApp.mTradeData.mTradePassword, 0, 0,
							AppConstants.APP_VERSION_INFO, type, "", 0);
				} else if (nFrameType == Trade_Define.Func_Login) {

					if (aMsgObject.nErrorCode < 0) {
						// 登陆出错，弹出messagebox提醒用户
						Toast.makeText(ZqTradeDetailActivity.this, "登录失败", 5000)
								.show();
					} else {
						// login success
						L.i(TAG, "Trade Login Successfully");
						// Toast.makeText(ZqTradeDetailActivity.this, "登录成功",
						// 5000)
						// .show();
						mMyApp.mTradeData.mTradeLoginFlag = true;
						if ((mCurrentPage == TRADE_BUY || mCurrentPage == TRADE_SELL)
								&& mBuyFragment != null) {
							requestHoldStock();
						}

						if (mCurrentPage == TRADE_CANCELL
								&& mCancellFragment != null) {
							requestDRWT();
						}
						if (mCurrentPage == TRADE_ENTRUST
								&& mEntrustFragment != null) {
							requestDRWT();
						}
						closeProgress();

					}

				} else if (nFrameType == Trade_Define.Func_Money)// 资金查询
				{

				} else if (nFrameType == Trade_Define.Func_DRCJ) // 当日成交
				{
					if (mCurrentPage == TRADE_CANCELL
							&& mCancellFragment != null) {
						mCancellFragment.updateTradeData(false);
					} else if (mCurrentPage == TRADE_ENTRUST
							&& mEntrustFragment != null) {
						mEntrustFragment.updateTradeData(false);
					}

				} else if (nFrameType == Trade_Define.Func_HOLDSTOCK) // 持仓查询
				{

					mMyApp.mTradeData.GetHoldStock(mHoldList);
					requestHQPushData(null, null);

					if (mCurrentPage == TRADE_BUY && mBuyFragment != null) {
						mBuyFragment.updateTradeData(false);
					} else if (mCurrentPage == TRADE_SELL
							&& mSellFragment != null) {
						mSellFragment.updateTradeData(false);
					}

				} else if (nFrameType == Trade_Define.Func_HYLB) // 查询合约列表
				{

				} else if (nFrameType == Trade_Define.Func_DRWT) // 当日委托查询
				{
					mMyApp.mTradeData.GetDRWT(mDRWTList);
					mMyApp.mTradeData.GetDRWT_CD(mDRWTCDList);

					Handler handle = new Handler();
					handle.postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							requestHoldStock();
						}
					}, 2000);

					requestDRCJ();// 委托计算成交均价需要

				} else if (nFrameType == Trade_Define.Func_WT
						&& (nRequestCode == mRequestCode[5]
								|| nRequestCode == mRequestCode[10] || nRequestCode == mRequestCode[11])) // 委托下单
				{

					if (aMsgObject.nErrorCode < 0) {

						// 委托下单出错，弹出messagebox提醒用户
						if (mAlertDlg == null) {
							mAlertDlg = new com.pengbo.mhdzq.widget.AlertDialog(
									ZqTradeDetailActivity.this).builder();
						}
						mAlertDlg.dismiss();
						mAlertDlg.setTitle("委托").setMsg(aMsgObject.errorMsg)
								.setCancelable(false)
								.setCanceledOnTouchOutside(false)
								.setPositiveButton("确认", new OnClickListener() {
									@Override
									public void onClick(View v) {
									}
								}).show();
					} else {
						Toast.makeText(ZqTradeDetailActivity.this, "委托已发送",
								Toast.LENGTH_LONG).show();
						requestDRWT();
					}

				} else if (nFrameType == Trade_Define.Func_WT) // 拆单后委托下单
				{

				} else if (nFrameType == Trade_Define.Func_WTCD) // 委托撤单
				{

					if (aMsgObject.nErrorCode < 0) {
						// 委托撤单出错
						Toast.makeText(ZqTradeDetailActivity.this,
								aMsgObject.errorMsg, Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(ZqTradeDetailActivity.this, "撤单请求已发送成功",
								Toast.LENGTH_LONG).show();
					}
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					requestDRWT();
				} else if (nFrameType == Trade_Define.Func_GDZH) // 查询股东账号
				{

				} else if (nFrameType == Trade_Define.Func_KMSL) // 查询可买卖数量
				{

					PBSTEP aStep = new PBSTEP();
					int num = mMyApp.mTradeData.GetKMSL(aStep);

					String kmsl;
					if (num > 0) {
						if (nRequestCode == mRequestCode[6]) {
							kmsl = aStep
									.GetFieldValueString(STEP_Define.STEP_KMSL);
							if (mBuyFragment != null
									&& (mCurrentPage == TRADE_BUY)) {
								mBuyFragment.updateKMSLView(kmsl, false);
							}
						} else if (nRequestCode == mRequestCode[8]) {
							kmsl = aStep
									.GetFieldValueString(STEP_Define.STEP_KMSL);
							L.d(TAG, "KMSL=  " + kmsl);
							if (mSellFragment != null
									&& (mCurrentPage == TRADE_SELL)) {
								mSellFragment.updateKMSLView(kmsl, false);
							}
						}
					}

				} else if (nFrameType == Trade_Define.Func_Push_DRCJ) // 当日成交推送
				{
					requestDRWT();

				} else if (nFrameType == Trade_Define.Func_KXQSL) // 查询可行权数量
				{

				} else if (nFrameType == Trade_Define.Func_XQ) // 行权下单
				{

				} else if (nFrameType == Trade_Define.Func_FJYWTCX) // 行权委托查询
				{

				} else if (nFrameType == Trade_Define.Func_XQZP) // 行权指派查询
				{

				} else if (nFrameType == Trade_Define.Func_LSXQZP) // 历史行权指派查询
				{

				} else if (nFrameType == Trade_Define.Func_XQCD)// 行权撤销
				{

				} else if (nFrameType == Trade_Define.Func_Push_HOLDSTOCK) // 持仓推送
				{

				} else if (nFrameType == Trade_Define.Func_Push_DRWT) // 当日委托推送
				{

				} else if (nFrameType == Trade_Define.Func_Push_HBBZ) // 推送委托回报，成交回报，撤单回报
				{

				}
			}
				break;

			case GlobalNetConnect.MSG_UPDATE_DATA: {
				CMessageObject aMsgObject = null;
				if (msg.obj != null && (msg.obj instanceof CMessageObject)) {
					aMsgObject = (CMessageObject) msg.obj;
				} else {
					return;
				}

				if (nFrameType == Global_Define.MFT_MOBILE_DATA_APPLY) {
					if (aMsgObject.nErrorCode != 0) {
						L.e(TAG, "RequestCode:" + nRequestCode);
						return;
					}

				} else if (nFrameType == Global_Define.MFT_MOBILE_PUSH_DATA)// 行情推送
				{
					L.i(TAG, "Received push data");

					processHQPushData(); // 根据推送的行情数据判断是否启动定时刷新DRWT

					if (mBuyFragment != null && (mCurrentPage == TRADE_BUY)) {
						mBuyFragment.updateView();
					} else if (mSellFragment != null
							&& (mCurrentPage == TRADE_SELL)) {
						mSellFragment.updateView();
					}
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
		};
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
	}

	protected void proc_MSG_DISCONNECT(Message msg) {

		if (mMyApp.mTradeData.mReconnected) {
			new com.pengbo.mhdzq.widget.AlertDialog(this)
					.builder()
					.setTitle("提示")
					.setMsg(this.getString(R.string.reconnect_warn))
					.setCancelable(false)
					.setCanceledOnTouchOutside(false)
					.setPositiveButton(
							this.getString(R.string.connect_warn_btn),
							new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									processReconnect();
								}
							})
					.setNegativeButton(
							this.getString(R.string.connect_warn_login),
							new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									processNetLose();
								}
							}).show();
		} else {
			new com.pengbo.mhdzq.widget.AlertDialog(this)
					.builder()
					.setTitle("提示")
					.setMsg(this.getString(R.string.connect_warn))
					.setCancelable(false)
					.setCanceledOnTouchOutside(false)
					.setPositiveButton(
							this.getString(R.string.connect_warn_btn),
							new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									processReconnect();
								}
							}).show();
		}
	}

	protected void proc_MSG_TIMEOUT(Message msg) {
		if (mMyApp.mTradeData.mReconnected) {
			new com.pengbo.mhdzq.widget.AlertDialog(this).builder()
					.setTitle("提示").setMsg("网络请求超时").setCancelable(false)
					.setCanceledOnTouchOutside(false)
					.setPositiveButton("重新连接", new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							processReconnect();
						}
					}).setNegativeButton("重新登录", new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							processNetLose();
						}
					}).show();
		} else {
			new com.pengbo.mhdzq.widget.AlertDialog(this).builder()
					.setTitle("提示").setMsg("连接交易服务器失败，请重新登录！")
					.setCancelable(false).setCanceledOnTouchOutside(false)
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
		mMyApp.mTradeData.mReconnected = false;
		mMyApp.setHQPushNetHandler(null);
		GlobalNetProgress.HQRequest_MultiCodeInfoPush(mMyApp.mHQPushNet, null,
				0, 0);
		mMyApp.setTradeHandler(null);
		mMyApp.mTradeNet.closeConnect();

		mMyApp.mTradeData.clearStepData();

		mMyApp.mTradeData.mTradeLoginFlag = false;
		// Toast.makeText(this, "退出账户", Toast.LENGTH_LONG).show();

		if (mBuyFragment != null) {
			mBuyFragment.dissMissKeyboard();
		}

		Intent intent = new Intent();
		intent.setClass(this, ZhengQuanActivity.class);
		if (AppActivityManager.getAppManager().getZQActivity() != null) {
			AppActivityManager.getAppManager().finishActivity(
					AppActivityManager.getAppManager().getZQActivity());
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
		// Toast.makeText(this, "退出账户", Toast.LENGTH_LONG).show();

		if (mBuyFragment != null) {
			mBuyFragment.dissMissKeyboard();
		}

		if (MyApp.getInstance().mTradeData.mTradeLoginFlag == false) {
			String type = "0";
			type = mMyApp.mTradeQSZHType_ZQ;
			if (type.isEmpty()) {
				type = "0";
			}

			showProgress("");

			getCurrentTradeAddList();
			mMyApp.mTradeNet.setMainHandler(mHandler);
			mMyApp.mTradeData.mTradeLockTimeout = PreferenceEngine
					.getInstance().getTradeOnlineTime();
			mMyApp.mTradeNet.initSocketThread();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trade_zq_detail_activity);

		initData();
		initView();

		Intent intent = getIntent();

		int currentpage = intent.getIntExtra("trade_page_index", 0);
		int nMarket = intent.getIntExtra("STOCK_MARKET", 0);
		String hqcode = intent.getStringExtra("STOCK_CODE");

		if ((nMarket == HQ_Define.HQ_MARKET_SH_INT || nMarket == HQ_Define.HQ_MARKET_SZ_INT)
				&& (hqcode != null)) {
			mTradeCode = new TagCodeInfo((short) nMarket, hqcode);
		}

		switch (currentpage) {
		case 0:
			setCurrentPage(TRADE_BUY);
			break;
		case 1:
			setCurrentPage(TRADE_SELL);
			break;
		case 2:
			setCurrentPage(TRADE_CANCELL);
			break;
		case 3:
			setCurrentPage(TRADE_ENTRUST);
			break;
		case 4:
			setCurrentPage(TRADE_MORE);
			break;
		}

		initFragment();

		mMyApp.mTradeNet.setMainHandler(mHandler);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initData() {
		mMyApp = (MyApp) this.getApplication();
		mMyApp.mTradeZqDetailActivity = this;

		mDRWTCDList = new PBSTEP();
		mDRWTList = new PBSTEP();
		mPBCJHB = new PBSTEP();
		mHoldList = new PBSTEP();

		mRequestCode = new int[13]; // 0:持仓查询 1:查询
	}

	private void initView() {

		mActivityView = (View) this.findViewById(R.id.trade_detail_layout);

		mActivityView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				/*
				 * if(MyApp.getInstance().mTradeData.mTradeLoginFlag == false) {
				 * String type = "0"; type = mMyApp.mTradeQSZHType_ZQ; if
				 * (type.isEmpty()) { type = "0"; }
				 * 
				 * showProgress("");
				 * 
				 * getCurrentTradeAddList();
				 * mMyApp.mTradeNet.setMainHandler(mHandler);
				 * mMyApp.mTradeData.mTradeLockTimeout =
				 * PreferenceEngine.getInstance().getTradeOnlineTime();
				 * mMyApp.mTradeNet.initSocketThread(); }
				 */

				return false;
			}

		});

		mBack = (ImageView) this.findViewById(R.id.zq_header_back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setOnClickListener(this);
		mBack.setVisibility(View.VISIBLE);

		mRefresh = (ImageView) this.findViewById(R.id.zq_header_right_refresh);
		mRefresh.setVisibility(View.VISIBLE);
		mRefresh.setOnClickListener(this);

		mHeadText = (TextView) this
				.findViewById(R.id.zq_header_middle_textview);
		mHeadacctText = (TextView) this
				.findViewById(R.id.zq_header_middle_textview2);

		mHeadacctText.setText(mMyApp.mTradeData.mTradeAccount);

		mFragmentMgr = getFragmentManager();
		// 包含头部的 Radiogroup
		mRadioGroup = (RadioGroup) this.findViewById(R.id.head_navigation_tab);
		mRadioGroup.setOnCheckedChangeListener(this);
	}

	private void initFragment() {
		TagCodeInfo option;

		switch (mCurrentPage) {
		case TRADE_BUY:
			if (mBuyFragment == null) {
				mBuyFragment = TradeZqBuyFragment.newInstance();
			}
			if ((option = mTradeCode) != null) {
				Bundle bundle = new Bundle();
				bundle.putShort("STOCK_MARKET", option.market);
				bundle.putString("STOCK_CODE", option.code);
				mBuyFragment.setArguments(bundle);
				mTradeCode = null;
			}
			addFragment(R.id.trade_detail_xh_frame, mBuyFragment);
			((RadioButton) mRadioGroup.findViewById(R.id.trade_detail_xh_mairu))
					.setChecked(true);
			break;

		case TRADE_SELL:
			if (mSellFragment == null) {
				mSellFragment = TradeZqSellFragment.newInstance();
			}
			if ((option = mTradeCode) != null) {
				Bundle bundle = new Bundle();
				bundle.putShort("STOCK_MARKET", option.market);
				bundle.putString("STOCK_CODE", option.code);
				mSellFragment.setArguments(bundle);
				mTradeCode = null;
			}
			addFragment(R.id.trade_detail_xh_frame, mSellFragment);
			((RadioButton) mRadioGroup
					.findViewById(R.id.trade_detail_xh_maichu))
					.setChecked(true);
			break;

		case TRADE_CANCELL:
			if (mCancellFragment == null) {
				mCancellFragment = TradeZqCancellOrderFragment.newInstance();
			}
			addFragment(R.id.trade_detail_xh_frame, mCancellFragment);
			((RadioButton) mRadioGroup
					.findViewById(R.id.trade_detail_xh_chedan))
					.setChecked(true);
			break;
		case TRADE_ENTRUST:
			if (mEntrustFragment == null) {
				mEntrustFragment = TradeZqEntrustFragment.newInstance();
			}
			addFragment(R.id.trade_detail_xh_frame, mEntrustFragment);
			((RadioButton) mRadioGroup
					.findViewById(R.id.trade_detail_xh_weituo))
					.setChecked(true);
			break;

		case TRADE_MORE:
			if (mMoreFragment == null) {
				mMoreFragment = TradeZqMoreFragment.newInstance();
			}
			addFragment(R.id.trade_detail_xh_frame, mMoreFragment);
			((RadioButton) mRadioGroup.findViewById(R.id.trade_detail_xh_more))
					.setChecked(true);
			break;
		}
	}

	private void addFragment(int id, Fragment fragment) {
		if (mFragmentMgr == null) {
			mFragmentMgr = getFragmentManager();
		}
		String tag = fragment.getClass().getSimpleName();

		FragmentTransaction transaction = mFragmentMgr.beginTransaction();
		if (fragment.isAdded()) {
			transaction.show(fragment);
		} else {
			transaction.add(id, fragment, tag);
		}
		mCurrentFragment = fragment;
		transaction.commitAllowingStateLoss();
	}

	private void turnToFragment(Fragment fromFragment, Fragment toFragment,
			Bundle args) {

		if (fromFragment == null) {
			addFragment(R.id.trade_detail_xh_frame, toFragment);
			mCurrentFragment = toFragment;
			return;
		}

		if (mFragmentMgr == null) {
			mFragmentMgr = getFragmentManager();
		}
		String fromTag = fromFragment.getClass().getSimpleName();
		String toTag = toFragment.getClass().getSimpleName();
		if (fromTag.equalsIgnoreCase(toTag)) {
			return;
		}

		if (args != null && !args.isEmpty()) {
			toFragment.getArguments().putAll(args);
		}

		FragmentTransaction transaction = mFragmentMgr.beginTransaction();

		// transaction.setCustomAnimations(R.anim.slide_left_in,
		// R.anim.slide_left_out,
		// R.anim.slide_left_in, R.anim.slide_left_out);

		transaction.setTransition(FragmentTransaction.TRANSIT_NONE);

		if (!toFragment.isAdded()) {
			transaction.hide(fromFragment).add(R.id.trade_detail_xh_frame,
					toFragment, toTag);
		} else {
			transaction.hide(fromFragment).show(toFragment);
		}
		mCurrentFragment = toFragment;
		transaction.commitAllowingStateLoss();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.trade_detail_xh_mairu:
			L.d(TAG, "onCheckedChanged trade_detail_activity_order");
			if (mBuyFragment == null) {
				mBuyFragment = TradeZqBuyFragment.newInstance();
			}
			turnToFragment(mCurrentFragment, mBuyFragment, null);
			setCurrentPage(TRADE_BUY);
			requestDRWT();// 当日委托回报后会查询持仓和资金
			break;

		case R.id.trade_detail_xh_maichu:
			L.d(TAG, "onCheckedChanged trade_detail_activity_query");
			if (mSellFragment == null) {
				mSellFragment = TradeZqSellFragment.newInstance();
			}
			turnToFragment(mCurrentFragment, mSellFragment, null);
			setCurrentPage(TRADE_SELL);
			requestDRWT();
			break;

		case R.id.trade_detail_xh_chedan:
			L.d(TAG, "onCheckedChanged trade_detail_activity_position");
			if (mCancellFragment == null) {
				mCancellFragment = TradeZqCancellOrderFragment.newInstance();
			}
			turnToFragment(mCurrentFragment, mCancellFragment, null);
			setCurrentPage(TRADE_CANCELL);
			requestZJ();
			requestHoldStock();
			break;

		case R.id.trade_detail_xh_weituo:
			L.d(TAG, "onCheckedChanged trade_detail_activity_xingquan");
			if (mEntrustFragment == null) {
				mEntrustFragment = TradeZqEntrustFragment.newInstance();
			}
			turnToFragment(mCurrentFragment, mEntrustFragment, null);
			setCurrentPage(TRADE_ENTRUST);
			requestDRWT();
			break;

		case R.id.trade_detail_xh_more:
			L.d(TAG, "onCheckedChanged trade_detail_activity_xingquan");
			if (mMoreFragment == null) {
				mMoreFragment = TradeZqMoreFragment.newInstance();
			}
			turnToFragment(mCurrentFragment, mMoreFragment, null);
			setCurrentPage(TRADE_MORE);
			break;
		}
	}

	public void setCurrentPage(int page) {

		switch (page) {
		case TRADE_BUY:
			mHeadText.setText("证券-下单");
			mRefresh.setVisibility(View.VISIBLE);
			break;
		case TRADE_SELL:
			mHeadText.setText("证券-下单");
			mRefresh.setVisibility(View.VISIBLE);
			break;
		case TRADE_CANCELL:
			mHeadText.setText("证券-撤单");
			mRefresh.setVisibility(View.VISIBLE);
			break;
		case TRADE_ENTRUST:
			mHeadText.setText("证券-当日委托");
			mRefresh.setVisibility(View.VISIBLE);
			break;
		case TRADE_MORE:
			mHeadText.setText("证券-更多");
			mRefresh.setVisibility(View.INVISIBLE);
			break;

		}
		if (mCurrentPage != page) {
			mCurrentPage = page;
		}
		if (mMyApp.mbDirectInOrderPage) {
			if (mBuyFragment != null) {
				// mBuyFragment.mbUpdateFromSelect = true;
			}
			mMyApp.mbDirectInOrderPage = false;
		}
	}

	@Override
	public void requestHQPushData(TagCodeInfo optionInfo,
			ArrayList<TagCodeInfo> codeList) {

		int nCodeCount = 0;
		if (codeList == null) {
			// 发送请求
			codeList = new ArrayList<TagCodeInfo>();
		}

		if (optionInfo != null) {
			codeList.add(optionInfo);
		}
		nCodeCount = codeList.size();

		String strMarket = "";
		String strCode = "";
		// 添加持仓合约列表到推送
		for (int i = 0; i < mHoldList.GetRecNum(); i++) {
			mHoldList.GotoRecNo(i);

			strMarket = mHoldList.GetFieldValueString(STEP_Define.STEP_SCDM);
			strCode = mHoldList.GetFieldValueString(STEP_Define.STEP_HYDM);

			String code_hq = mMyApp.mTradeData.GetHQCodeFromTradeCode(strCode);

			int nMarket = TradeData.GetHQMarketFromTradeMarket(strMarket);

			boolean bAdd = true;
			for (int m = 0; m < nCodeCount; m++) {
				if (nMarket == codeList.get(m).market
						&& code_hq.equals(codeList.get(m).code)) {
					bAdd = false;
					break;
				}
			}
			if (bAdd == true) {
				TagCodeInfo aInfoItem = new TagCodeInfo((short) nMarket,
						code_hq);
				codeList.add(aInfoItem);
			}
		}

		if (mLocalRefreshWT) {

			strMarket = "";
			strCode = "";
			for (int i = 0; i < mDRWTCDList.GetRecNum(); i++) {
				mDRWTCDList.GotoRecNo(i);

				strMarket = mDRWTCDList
						.GetFieldValueString(STEP_Define.STEP_SCDM);
				strCode = mDRWTCDList
						.GetFieldValueString(STEP_Define.STEP_HYDM);
				String code_hq = mMyApp.mTradeData
						.GetHQCodeFromTradeCode(strCode);

				int nMarket = TradeData.GetHQMarketFromTradeMarket(strMarket);

				boolean bAdd = true;
				for (int m = 0; m < nCodeCount; m++) {
					if (nMarket == codeList.get(m).market
							&& code_hq.equals(codeList.get(m).code)) {
						bAdd = false;
						break;
					}
				}
				if (bAdd == true) {
					TagCodeInfo aInfoItem = new TagCodeInfo((short) nMarket,
							code_hq);
					codeList.add(aInfoItem);
				}
			}
		}

		mMyApp.setHQPushNetHandler(mHandler);
		mRequestCode[1] = GlobalNetProgress.HQRequest_MultiCodeInfoPush(
				mMyApp.mHQPushNet, codeList, 0, codeList.size());
	}

	private void processHQPushData() {
		if (mLocalRefreshWT && mMyApp.mTradeData.mTradeLoginFlag) {

			if (mTimerRequestDRWT != null) {
				// 如果有请求正在等待发送，则不用再判断是否有更新
				L.i(TAG, "mTimerRequestDRWT isValid");
				return;
			}

			TagLocalStockData optionData = new TagLocalStockData();
			for (int i = 0; i < mDRWTCDList.GetRecNum(); i++) {
				mDRWTCDList.GotoRecNo(i);

				String strMarket = mDRWTCDList
						.GetFieldValueString(STEP_Define.STEP_SCDM);
				String strCode = mDRWTCDList
						.GetFieldValueString(STEP_Define.STEP_HYDM);

				int nMarket = TradeData.GetHQMarketFromTradeMarket(strMarket);
				String code_hq = mMyApp.mTradeData
						.GetHQCodeFromTradeCode(strCode);

				if (mMyApp.mHQData_ZQ.getData(optionData, (short) nMarket,
						code_hq, true)) {
					if (optionData.HQData.bNewUpdated == true) {

						long lInterval = maybeCloseWT(mDRWTCDList, optionData);
						if (lInterval > 0) { // 判断可能有成交
							this.mTimerForRequestDRWT = lInterval;
							L.i(TAG,
									"startRequestDRWTTimer mIntervalForRequestDRWT=%d"
											+ mTimerForRequestDRWT);
							this.startRequestDRWTTimer();
							break;
						}
					}
				}
			}
		}
	}

	private void startRequestDRWTTimer() {
		if (mTimerForRequestDRWT <= 0)
			return;

		stopRequestDRWTTimer();

		mTimerRequestDRWT = new Timer();
		mTimerRequestDRWT.schedule(new TimerTask() {
			//
			public void run() {
				onRequestDRWT();

			}
		}, mTimerForRequestDRWT, mTimerForRequestDRWT);
	}

	private void stopRequestDRWTTimer() {
		if (mTimerRequestDRWT != null) {
			mTimerRequestDRWT.cancel();
		}
		mTimerRequestDRWT = null;
	}

	private void onRequestDRWT() {
		stopRequestDRWTTimer();
		this.requestDRWT();
		// this.requestHoldStock();
	}

	// 可能有交易完成或关闭,返回刷新价格时间，0表示不需要刷新
	private long maybeCloseWT(PBSTEP aStep, TagLocalStockData optionData) {
		float fCmpMinPrice = 0.00009f;
		long fRet = 0;

		char sjType = aStep.GetFieldValueCHAR(STEP_Define.STEP_SJWTLB);
		if (PTK_Define.PTK_OPT_LimitPrice != sjType && '\0' != sjType) // 如果是非限价委托
		{
			L.e(TAG, "maybeCloseWT: sjwt sjType=%c" + sjType);
			return 3000;// 2sec
		} else {
			float fKPBZ = STD.StringToValue(aStep
					.GetFieldValueString(STEP_Define.STEP_KPBZ));
			boolean bBuy = (fKPBZ == 0f ? true : false);
			float fWTPrice = STD.StringToValue(aStep
					.GetFieldValueString(STEP_Define.STEP_WTJG));
			float fNowPrice = ViewTools.getPriceByFieldNo(
					Global_Define.FIELD_HQ_NOW, optionData);
			float fAveragePrice = (float) optionData.HQData.currentCJAveragePrice;

			L.i(TAG,
					String.format(
							"maybeCloseWT: fWTPrice = %f,fNowPrice = %f,fAveragePrice = %f",
							fWTPrice, fNowPrice, fAveragePrice));

			if (bBuy) {

				if (optionData.HQData.sellPrice[0] < 1) {
					// 涨停了，就认为买不到了
					return 0;
				}
				float fSub = fWTPrice - fNowPrice;
				float fSub1 = fWTPrice - fAveragePrice;
				if (fSub > fCmpMinPrice
						|| (fSub1 > fCmpMinPrice && fAveragePrice != 0)) {
					L.i(TAG, "maybeCloseWT: return 1.0");
					return 3000;
				}

				if ((fSub <= fCmpMinPrice && fSub > -fCmpMinPrice)
						|| (fSub1 <= fCmpMinPrice && fSub1 > -fCmpMinPrice && fAveragePrice != 0)) {
					// 认为价格相等，可能存在需要立即刷新的
					if (optionData.HQData.currentCJ > 0) {// 当前有成交
						L.i(TAG, "maybeCloseWT: currentCJ = %f"
								+ optionData.HQData.currentCJ);
						return 3000;
					} else {
						return 0;
					}
				}
			} else {
				if (optionData.HQData.buyPrice[0] < 1) {
					// 跌停了，就认为卖不出去了
					return 0;
				}
				float fSub = fNowPrice - fWTPrice;
				float fSub1 = fAveragePrice - fWTPrice;
				if (fSub > fCmpMinPrice
						|| (fSub1 > fCmpMinPrice && fAveragePrice != 0)) {
					L.i(TAG, "maybeCloseWT: return 1.0");
					return 3000;
				}

				if ((fSub <= fCmpMinPrice && fSub > -fCmpMinPrice)
						|| (fSub1 <= fCmpMinPrice && fSub1 > -fCmpMinPrice && fAveragePrice != 0)) {
					// 认为价格相等，可能存在需要立即刷新的
					if (optionData.HQData.currentCJ > 0) {// 当前有成交
						L.i(TAG, "maybeCloseWT: currentCJ = %f"
								+ optionData.HQData.currentCJ);
						return 3000;
					} else {
						return 0;
					}
				}
			}
		}
		return fRet;
	}

	@Override
	public void requestWT(TradeLocalRecord record) {
		mMyApp.mTradeNet.setMainHandler(mHandler);

		mRequestCode[5] = mMyApp.mTradeNet.Request_WT(record.mMarketCode,
				record.mStockCode, record.mMMLB, record.mKPBZ, record.mWTSL,
				record.mWTPrice, record.mGDZH, record.mXWH, record.mBDFlag,
				record.mSJType);

	}

	@Override
	public void requestDRWT() {
		// TODO Auto-generated method stub
		mMyApp.mTradeNet.setMainHandler(mHandler);
		mMyApp.mTradeNet.Request_ListQuery(Trade_Define.Func_DRWT, null);
	}

	@Override
	public void requestDRCJ() {
		// TODO Auto-generated method stub
		mMyApp.mTradeNet.setMainHandler(mHandler);
		mMyApp.mTradeNet.Request_ListQuery(Trade_Define.Func_DRCJ, null);

	}

	@Override
	public void requestZJ() {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestHoldStock() {
		// TODO Auto-generated method stub
		mMyApp.mTradeNet.setMainHandler(mHandler);
		// 市场代码null代表查询所有市场
		mRequestCode[3] = mMyApp.mTradeNet.Request_HoldStock(null, null);

	}

	@Override
	public void requestWTWithFlag(TradeLocalRecord record, boolean bOnlyPC) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestWTCD(TradeLocalRecord record) {
		// TODO Auto-generated method stub
		for (int i = 0; i < mDRWTCDList.GetRecNum(); i++) {
			mDRWTCDList.GotoRecNo(i);

			String wtbh = mDRWTCDList
					.GetFieldValueString(STEP_Define.STEP_WTBH);
			if (record.mWTBH.equals(wtbh)) {

				record.mWTSHJ = mDRWTCDList
						.GetFieldValueString(STEP_Define.STEP_WTRQ);
				record.mGDZH = mDRWTCDList
						.GetFieldValueString(STEP_Define.STEP_GDH);
				record.mMarketCode = mDRWTCDList
						.GetFieldValueString(STEP_Define.STEP_SCDM);
				record.mXWH = mDRWTCDList
						.GetFieldValueString(STEP_Define.STEP_XWH);
				record.mXDXW = mDRWTCDList
						.GetFieldValueString(STEP_Define.STEP_XDXW);
				record.mWTZT = mDRWTCDList
						.GetFieldValueString(STEP_Define.STEP_WTZT);
				record.mWTZTMC = mDRWTCDList
						.GetFieldValueString(STEP_Define.STEP_WTZTMC);
				record.mBiaodiCode = mDRWTCDList
						.GetFieldValueString(STEP_Define.STEP_BDDM);
				record.mBiaodiMC = mDRWTCDList
						.GetFieldValueString(STEP_Define.STEP_BDMC);
				record.mWTPrice = mDRWTCDList
						.GetFieldValueString(STEP_Define.STEP_WTJG);
				record.mWTSL = mDRWTCDList
						.GetFieldValueString(STEP_Define.STEP_WTSL);
				break;
			}
		}

		mMyApp.mTradeNet.setMainHandler(mHandler);
		mRequestCode[12] = mMyApp.mTradeNet.Request_WTCD(record.mWTBH,
				record.mWTSHJ, record.mGDZH, record.mMarketCode, record.mXWH,
				record.mXDXW);

	}

	@Override
	public void requestKMSL(String price, TagLocalStockData optionData,
			char sjtype, boolean buy) {
		// TODO Auto-generated method stub

		if (!mMyApp.mTradeData.mTradeLoginFlag) {
			return;
		}

		String market = TradeData.GetTradeMarketFromHQMarket(
				optionData.HQData.market, optionData.group);

		String gdzh = mMyApp.mTradeData.GetGDZHFromMarket(market);
		String xwh = mMyApp.mTradeData.GetXWHFromMarket(market);

		// 6-9可买数量查询(6:买开 7:卖平 8:卖开 9:买平)
		mMyApp.mTradeNet.setMainHandler(mHandler);
		if (buy) {
			mRequestCode[6] = mMyApp.mTradeNet.Request_KMSL(market,
					optionData.HQData.code, PTK_Define.PTK_D_Buy,
					PTK_Define.PTK_OF_Open, price, gdzh, xwh, 0, sjtype);
		} else {
			mRequestCode[8] = mMyApp.mTradeNet.Request_KMSL(market,
					optionData.HQData.code, PTK_Define.PTK_D_Sell,
					PTK_Define.PTK_OF_Open, price, gdzh, xwh, 0, sjtype);
		}

	}

	@Override
	public void requestHQPushData(ArrayList<TagCodeInfo> codeList) {
		// TODO Auto-generated method stub

		L.i(TAG, "requestHQPushData");

		int nCodeCount = 0;
		if (codeList == null) {
			// 发送请求
			codeList = new ArrayList<TagCodeInfo>();
		}
		nCodeCount = codeList.size();

		L.i(TAG, "requestHQPushData send");
		mMyApp.setHQPushNetHandler(mHandler);
		mRequestCode[4] = GlobalNetProgress.HQRequest_MultiCodeInfoPush(
				mMyApp.mHQPushNet, codeList, 0, codeList.size());

	}

	@Override
	public void requestKXQSL(String stockCode, String marketCode, String gdzh,
			String xwh) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestXQCD(String marketCode, String wtbh, String gdzh,
			String xwh, String xdxw) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestXQWT() {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestXQZP(String marketCode, String stockCode, String gdzh) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestXQLSZP(String startDate, String endDate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setKJFSRuning(boolean bRuning) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestHQTrendLine(TagLocalStockData optionData,
			TagLocalStockData stockData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestXQ(String marketCode, String stockCode, String xqsl,
			String gdzh, String xwh, String sqType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.zq_header_back: {
			backToJiaoYiPager();
		}
			break;
		case R.id.zq_header_right_refresh: {

			if (mCurrentPage == TRADE_BUY && mBuyFragment != null) {
				requestHoldStock();

			} else if (mCurrentPage == TRADE_SELL && mSellFragment != null) {
				requestHoldStock();
			} else if (mCurrentPage == TRADE_CANCELL
					&& mCancellFragment != null) {
				requestDRWT();

			} else if (mCurrentPage == TRADE_ENTRUST
					&& mEntrustFragment != null) {
				requestDRWT();
			}
		}
		}

	}

	private void backToJiaoYiPager() {
		Intent intent = new Intent();
		intent.setClass(this, ZhengQuanActivity.class);
		if (AppActivityManager.getAppManager().getZQActivity() != null) {
			AppActivityManager.getAppManager().finishActivity(
					AppActivityManager.getAppManager().getZQActivity());
		}

		intent.putExtra("ZQ_VIEW_INDEX", ZhengQuanFragment.ZQ_VIEW_JIAOYI);
		startActivity(intent);
		this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backToJiaoYiPager();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void getCurrentTradeAddList() {
		// mMyApp.resetAddrNum_Trade();
		// for(int i = 0; i< mMyApp.mTrade_reconnectAddrNum; i++)
		// {
		// mMyApp.addAddress_Trade(mMyApp.mTrade_reconnectAddress[i]);
		// }
		mMyApp.resetAddrNum_Trade(mMyApp.mTrade_reconnectAddrNum);
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

}
