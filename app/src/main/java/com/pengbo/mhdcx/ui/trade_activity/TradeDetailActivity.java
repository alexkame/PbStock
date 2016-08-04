package com.pengbo.mhdcx.ui.trade_activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.CPbDataDecode;
import com.pengbo.mhdzq.data.CPbDataField;
import com.pengbo.mhdzq.data.CPbDataItem;
import com.pengbo.mhdzq.data.CPbDataPackage;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.TagLocalTrendData;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdcx.fragment.TradeMoreFragment;
import com.pengbo.mhdcx.fragment.TradeOrderFragment;
import com.pengbo.mhdcx.fragment.TradeXingQuanFragment;
import com.pengbo.mhdcx.fragment.TradePositionFragment;
import com.pengbo.mhdcx.fragment.TradeQueryFragment;
import com.pengbo.mhdzq.main_activity.SplashActivity;
import com.pengbo.mhdzq.net.CMessageObject;
import com.pengbo.mhdzq.net.GlobalNetConnect;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.net.MyByteBuffer;
import com.pengbo.mhdzq.tools.DataTools;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.PTK_Define;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.TradeData;
import com.pengbo.mhdzq.trade.data.TradeLocalRecord;
import com.pengbo.mhdzq.trade.data.TradeNetConnect;
import com.pengbo.mhdzq.trade.data.Trade_Define;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.ui.main_activity.MainTabActivity;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.zq_activity.ZQLSCJActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class TradeDetailActivity extends FragmentActivity implements OnCheckedChangeListener, OnTradeFragmentListener {
	private static final String TAG = "TradeDetailActivity";
	private MyApp mMyApp;
	
	public static final int TRADE_ORDER = 0;
	public static final int TRADE_QUERY = 1;
	public static final int TRADE_POSITION = 2;
	public static final int TRADE_XINGQUAN=3;
	public static final int TRADE_MORE = 4;
	public static final String INTENT_SERIALIZABLE_CURRENTPAGE = "page";
	
	private RadioGroup mRadioGroup;
	private FragmentManager mFragmentMgr = null;
	private TradeOrderFragment mOrderFrag = null;
	private TradeQueryFragment mQueryFrag = null;
	private TradePositionFragment mPositionFrag = null;
	private TradeXingQuanFragment mXingQuanFrag=null;
	private Fragment mCurrentFragment = null;
	private TradeMoreFragment mMoreFrag = null;
	private TradeLocalRecord mTradeRecord = null;
	private ArrayList<TagLocalTrendData> mTrendDataArray;
	private ArrayList<TagLocalTrendData> mStockTrendDataArray;
	private TagLocalStockData mCurrentStockData;
	
	private int mCurrentPage = TRADE_POSITION;
	
    //private TagCodeInfo mOptionCodeInfo = null;//合约
	//private TagLocalStockData mOptionData = null;//合约
	//private TagLocalStockData mStockData = null;//标的
	
	//private char mMMLB; //买卖方向: 买('0',PTK_D_Buy) 卖('1',PTK_D_Sell)
    //private char mKPBZ; //开平标志: 开仓('0',PTK_OF_Open) 平仓('1',PTK_OF_Close)
	
	//public PBSTEP mMoney;//资金
	public PBSTEP mHoldList;//持仓列表
    //public PBSTEP mDRWTList;//当日委托
    public PBSTEP mDRWTCDList;//当日委托中可以撤单
    //public PBSTEP mDRCJList;//当日成交
    public int mKMSL[]; //num 4 - 买开 卖平 卖开 买平
    
	public int mRequestCode[]; //0:持仓查询 1:查询证券公司资金信息 2-走势查询 3-标的走势查询 4-HQ推送数据 5-委托下单 6-9可买数量查询(6:买开 7:卖平 8:卖开 9:买平 10:全部平仓 11:快捷反手中的平仓)12-撤单
	private Timer 	mTimerRequestDRWT = null;
	private long mTimerForRequestDRWT = 0;
	private boolean mLocalRefreshWT = true;
	public ArrayList<Integer> mWTRequestCodeArray;
	public ArrayList<Integer> mKJFSRequestCodeArray;
	public Dialog mProgress;
	
	private boolean mbKJFSRuning = false;//是否正在进行快捷反手，如果之前的快捷反手未结束，是不能再进行快捷反手的
	// 消息 处理
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			int nFrameType = msg.arg1;
			int nRequestCode = msg.arg2;

			switch (msg.what) {
			case TradeNetConnect.MSG_UPDATE_DATA:{// 交易
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
						
						if(mOrderFrag != null && (mCurrentPage == TRADE_ORDER))
						{
							requestDRWT();
							requestHoldStock();

						}else if(mPositionFrag != null && (mCurrentPage == TRADE_POSITION))
						{
							requestZJ();
							requestHoldStock();
						}
						else if(mQueryFrag != null && (mCurrentPage == TRADE_QUERY))
						{

						}else if(mXingQuanFrag != null && (mCurrentPage == TRADE_XINGQUAN))
						{

						}
						
						closeProgress();

					}
					
				}
				else if (nFrameType == Trade_Define.Func_Money)// 资金查询
				{

					if(mOrderFrag != null && (mCurrentPage == TRADE_ORDER))
					{
						mOrderFrag.updateZJData();
						mOrderFrag.updateZJView();
					}else if(mPositionFrag != null && (mCurrentPage == TRADE_POSITION))
					{
						mPositionFrag.updateZJData();
						mPositionFrag.updateZJView();
					}
				} else if (nFrameType == Trade_Define.Func_DRCJ) // 当日成交
				{
					requestZJ();
					requestHoldStock();
					
					if(mOrderFrag != null && (mCurrentPage == TRADE_ORDER))
					{
						mOrderFrag.updateDRWT_CD(false);
					}
					else if(mQueryFrag != null && (mCurrentPage == TRADE_QUERY))
					{
						mQueryFrag.updateWTData();
					}


				} else if (nFrameType == Trade_Define.Func_HOLDSTOCK) // 持仓查询
				{
					mMyApp.mTradeData.GetHoldStock(mHoldList);
					
		    		if(mOrderFrag != null && (mCurrentPage == TRADE_ORDER))
					{
						mOrderFrag.updateChiCang(false);
					}
		    		else if(mPositionFrag != null && (mCurrentPage == TRADE_POSITION))
					{
						mPositionFrag.updateChiCangData();
						mPositionFrag.updateAllView();
					}
		    		else if (mXingQuanFrag != null && (mCurrentPage == TRADE_XINGQUAN))
					{
						mXingQuanFrag.updateXQList();
					}
				} else if (nFrameType == Trade_Define.Func_HYLB) // 查询合约列表
				{

				} else if (nFrameType == Trade_Define.Func_DRWT) // 当日委托查询
				{
					//mMyApp.mTradeData.GetDRWT(mDRWTList);
					mMyApp.mTradeData.GetDRWT_CD(mDRWTCDList);
					requestZJ();
					requestHoldStock();
					
					if (mOrderFrag != null && mOrderFrag.refreshPCRecordStatus())
					{
			            if ((mOrderFrag.mTradeRecordKJFS.mWTZT.isEmpty() || mOrderFrag.mTradeRecordKJFS.mWTZT.equals("0")) && !mOrderFrag.mTradeRecordKJFS.mStockCode.isEmpty()) {
			                requestWT(mOrderFrag.mTradeRecordKJFS);
			                mOrderFrag.mTradeRecordKJFS.mWTZT = "1";
			                mbKJFSRuning = false;
			            }
			        }
					
					if(mOrderFrag != null && (mCurrentPage == TRADE_ORDER))
					{
						mOrderFrag.updateDRWT_CD(false);
					}
					else if(mQueryFrag != null && (mCurrentPage == TRADE_QUERY))
					{
						mQueryFrag.updateWTData();
					}

				} else if (nFrameType == Trade_Define.Func_WT && (nRequestCode == mRequestCode[5] || nRequestCode == mRequestCode[10]|| nRequestCode == mRequestCode[11])) // 委托下单
				{
					if (aMsgObject.nErrorCode < 0) {
						if (nRequestCode == mRequestCode[11]) {//快捷反手
							mbKJFSRuning = false;
						}
						//委托下单出错，弹出messagebox提醒用户
						new com.pengbo.mhdcx.widget.AlertDialog(MyApp.getInstance().mTradeDetailActivity.getParent()).builder().setTitle("委托")			
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
						requestDRWT();
						PBSTEP aStep = new PBSTEP();
						int num = mMyApp.mTradeData.GetWT(aStep);
						String wtbh = aStep.GetFieldValueString(STEP_Define.STEP_WTBH);
						if (num > 0) {
							String strMsg = String.format("委托编号：%s", wtbh);
							Toast.makeText(MyApp.getInstance().mTradeDetailActivity.getParent(), strMsg, Toast.LENGTH_SHORT)
									.show();
							//下单成功，需要跳转到撤单页
							if (mOrderFrag != null && (mCurrentPage == TRADE_ORDER))
							{
								mOrderFrag.changeToChedanView();
							}
						}
						
						if (nRequestCode == mRequestCode[11]) {//快捷反手
							mbKJFSRuning = true;
							if (mOrderFrag != null)
							{
								mOrderFrag.mTradeWTBHArray.add(wtbh);
								//mOrderFrag.mTradeRecordQBPC.mWTBH = wtbh;
								mOrderFrag.startKJFS(mOrderFrag.mTradeRecordKJFS, mOrderFrag.mTradeRecordQBPC);
							}
				        }
					}
					
				}else if (nFrameType == Trade_Define.Func_WT) // 拆单后委托下单
				{
					boolean bKJFS = false;
			        if (mWTRequestCodeArray.size() > 0 && mWTRequestCodeArray.contains(Integer.valueOf(nRequestCode)))//手动下单和全部平仓 拆单后的requestCode
			        {
			        	mWTRequestCodeArray.remove(Integer.valueOf(nRequestCode));
			        }
			        else if (mKJFSRequestCodeArray.size() > 0 && mKJFSRequestCodeArray.contains(Integer.valueOf(nRequestCode)))//快捷反手平仓 拆单后的requestCode
			        {
			            bKJFS = true;
			            mKJFSRequestCodeArray.remove(Integer.valueOf(nRequestCode));
			        }
			        else
			        {
			            return;
			        }
			        
			        if (aMsgObject.nErrorCode < 0) {
			        	if(bKJFS)
			        		mbKJFSRuning = false;
						//委托下单出错，弹出messagebox提醒用户
						new com.pengbo.mhdcx.widget.AlertDialog(MyApp.getInstance().mTradeDetailActivity.getParent()).builder().setTitle("委托")			
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
						requestDRWT();
						PBSTEP aStep = new PBSTEP();
						int num = mMyApp.mTradeData.GetWT(aStep);
						String wtbh = aStep.GetFieldValueString(STEP_Define.STEP_WTBH);
						if (num > 0) {
							String strMsg = String.format("委托编号：%s", wtbh);
							Toast.makeText(MyApp.getInstance().mTradeDetailActivity.getParent(), strMsg, Toast.LENGTH_SHORT)
									.show();
							//下单成功，需要跳转到撤单页
							if (mOrderFrag != null && (mCurrentPage == TRADE_ORDER))
							{
								mOrderFrag.changeToChedanView();
							}
						}
						
						if (bKJFS == true)
						{
							mbKJFSRuning = true;
							if (mOrderFrag != null)
							{
								mOrderFrag.mTradeWTBHArray.add(wtbh);
								if(mKJFSRequestCodeArray.size() == 0)
								{
									mOrderFrag.startKJFS(mOrderFrag.mTradeRecordKJFS, mOrderFrag.mTradeRecordQBPC);
								}
							}
				        }
					}
					
				} else if (nFrameType == Trade_Define.Func_WTCD) // 委托撤单
				{
					if (aMsgObject.nErrorCode < 0) {
						//委托撤单出错
						Toast.makeText(MyApp.getInstance().mTradeDetailActivity.getParent(), aMsgObject.errorMsg, Toast.LENGTH_SHORT)
								.show();
					}else
					{
						Toast.makeText(MyApp.getInstance().mTradeDetailActivity.getParent(), "撤单请求已发送成功", Toast.LENGTH_SHORT)
						.show();
					}
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					requestDRWT();
		            //requestZJ();
				} else if (nFrameType == Trade_Define.Func_GDZH) // 查询股东账号
				{

				} else if (nFrameType == Trade_Define.Func_KMSL) // 查询可买卖数量
				{
					PBSTEP aStep = new PBSTEP();
					int num = mMyApp.mTradeData.GetKMSL(aStep);
					if (num > 0)
					{
						if (nRequestCode == mRequestCode[6]) {
				            mKMSL[0] = aStep.GetFieldValueInt(STEP_Define.STEP_KMSL);
				        }
				        else if (nRequestCode == mRequestCode[7]) {
				            mKMSL[1] = aStep.GetFieldValueInt(STEP_Define.STEP_KMSL);
				        }
				        else if (nRequestCode == mRequestCode[8]) {
				            mKMSL[2] = aStep.GetFieldValueInt(STEP_Define.STEP_KMSL);
				        }
				        else if (nRequestCode == mRequestCode[9]) {
				            mKMSL[3] = aStep.GetFieldValueInt(STEP_Define.STEP_KMSL);
				        }
						
						if(mOrderFrag != null && (mCurrentPage == TRADE_ORDER))
						{
							mOrderFrag.updateKMSLView(mKMSL);
						}
					}
				} else if (nFrameType == Trade_Define.Func_Push_DRCJ) // 当日成交推送
				{
					//mMyApp.mTradeData.GetDRCJ(mDRCJList);
					//requestHoldStock();
					//requestZJ();
					requestDRWT();

				} else if (nFrameType == Trade_Define.Func_KXQSL) //查询可行权数量
				{
					if (aMsgObject.nErrorCode >= 0)
					{
						if(mXingQuanFrag != null && (mCurrentPage == TRADE_XINGQUAN))
						{
							PBSTEP aStep = new PBSTEP();
							mMyApp.mTradeData.GetKXQSL(aStep);
							mXingQuanFrag.UpdateXQSL(aStep);
						}
					}
				}else if (nFrameType == Trade_Define.Func_XQ) //行权下单
				{
					if (aMsgObject.nErrorCode < 0) {
						//行权下单出错，弹出messagebox提醒用户
						new com.pengbo.mhdcx.widget.AlertDialog(MyApp.getInstance().mTradeDetailActivity.getParent()).builder().setTitle("委托")			
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
						requestXQWT();
						PBSTEP aStep = new PBSTEP();
						int num = mMyApp.mTradeData.GetXQWT(aStep);
						String wtbh = aStep.GetFieldValueString(STEP_Define.STEP_WTBH);
						if (num > 0) {
							String strMsg = String.format("委托编号：%s", wtbh);
							Toast.makeText(MyApp.getInstance().mTradeDetailActivity.getParent(), strMsg, Toast.LENGTH_SHORT)
									.show();
							//下单成功，需要跳转到撤单页
						}
					}
				} else if (nFrameType == Trade_Define.Func_FJYWTCX) // 行权委托查询
				{
					if(mXingQuanFrag != null && (mCurrentPage == TRADE_XINGQUAN))
					{
						mXingQuanFrag.updateXQCDList();
					}
				} else if (nFrameType == Trade_Define.Func_XQZP) // 行权指派查询
				{
					if(mXingQuanFrag != null && (mCurrentPage == TRADE_XINGQUAN))
					{
						mXingQuanFrag.updateXQZPList();
					}
				} else if (nFrameType == Trade_Define.Func_LSXQZP) // 历史行权指派查询
				{
					if(mXingQuanFrag != null && (mCurrentPage == TRADE_XINGQUAN))
					{
						mXingQuanFrag.updateXQLSZPList();
					}
				} else if (nFrameType == Trade_Define.Func_XQCD)// 行权撤销
				{
					if (aMsgObject.nErrorCode < 0) {
						//行权撤单出错
						Toast.makeText(MyApp.getInstance().mTradeDetailActivity.getParent(), aMsgObject.errorMsg, Toast.LENGTH_SHORT)
								.show();
					}else
					{
						Toast.makeText(MyApp.getInstance().mTradeDetailActivity.getParent(), "撤单请求已发送成功", Toast.LENGTH_SHORT)
						.show();
					}
					
					requestXQWT();
				}
				else if (nFrameType == Trade_Define.Func_Push_HOLDSTOCK) // 持仓推送
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

					if (mRequestCode[2] == nRequestCode)// 走势
					{
						parseTrendData(aMsgObject.getData(),
								aMsgObject.getDataLength(), false);
						requestHQStockTrendLine(mCurrentStockData);
						if(mOrderFrag != null && (mCurrentPage == TRADE_ORDER))
						{
							mOrderFrag.updateTrendLineData();
						}
					}else if (mRequestCode[3] == nRequestCode)// 标的走势
					{
						parseTrendData(aMsgObject.getData(),
								aMsgObject.getDataLength(), true);
						
						if(mOrderFrag != null && (mCurrentPage == TRADE_ORDER))
						{
							mOrderFrag.updateTrendLineData();
						}
					}
				}
				else if(nFrameType == Global_Define.MFT_MOBILE_PUSH_DATA)//行情推送
				{
					L.i(TAG, "Received push data");
					
					if (mOrderFrag != null && mOrderFrag.mViewSwitcherIndex == TradeOrderFragment.VIEW_WT)
					{
						processHQPushData(); //根据推送的行情数据判断是否启动定时刷新DRWT
					}

					if(mOrderFrag != null && (mCurrentPage == TRADE_ORDER))
					{
						mOrderFrag.updateOptionData(true);
						mOrderFrag.updateHQView("", false);
						//mOrderFrag.updateChiCang(false);
					}else if(mPositionFrag != null && (mCurrentPage == TRADE_POSITION))
					{
						mPositionFrag.updateZJView();
						mPositionFrag.updateChiCangData();
						mPositionFrag.updateChiCangView();
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
		mMyApp = (MyApp)this.getApplication();

		L.d(TAG,"TradeDetailActivity.OnCreate");
		
		boolean isRestart = false;
		if(mMyApp.mCodeTableMarketNum <= 0) {
			isRestart = true;
		}
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trade_detail_activity);
		
        initData();
		initView();
		
		if(isRestart) {
			onAppRestore();
		}
		
		requestZJ();
		requestHoldStock();
	}
	
	public void onAppRestore() {
		AppActivityManager.getAppManager().finishAllActivity();
		
		Intent i = new Intent(this, SplashActivity.class);
		this.startActivity(i);
		this.finish();
	}

	public void setCurrentPage(int page) {
		if (mCurrentPage != page)
		{
			mCurrentPage = page;
		}
		if(mMyApp.mbDirectInOrderPage)
		{
			if(mOrderFrag != null)
			{
				mOrderFrag.mbUpdateFromSelect = true;
			}
			mMyApp.mbDirectInOrderPage = false;
		}
	}
	
	private void initData() {

		mMyApp.mTradeDetailActivity = this;

		mRequestCode = new int[13]; //0:持仓查询 1:查询证券公司资金信息 2-走势查询 3-明细查询 4-HQ推送数据 5-委托下单 6-9可买数量查询(6:买开 7:卖平 8:卖开 9:买平 10:全部平仓 11:快捷反手中的平仓)12-撤单
		mWTRequestCodeArray = new ArrayList<Integer>();
		mKJFSRequestCodeArray = new ArrayList<Integer>();
		mKMSL = new int[4]; //买开 卖平 卖开 买平
		mCurrentStockData = new TagLocalStockData();
		mHoldList = new PBSTEP();
		//mMoney = new PBSTEP();
		//mDRWTList = new PBSTEP();
		mDRWTCDList = new PBSTEP();
		//mDRCJList = new PBSTEP();
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null)
		{
			mCurrentPage = bundle.getInt(INTENT_SERIALIZABLE_CURRENTPAGE);
		}else
		{
			mCurrentPage = TRADE_POSITION;
		}
		
		if (mTradeRecord == null)
		{
			mTradeRecord = new TradeLocalRecord();
		}
		
		mTrendDataArray = mMyApp.getTrendDataArray();
		mStockTrendDataArray = mMyApp.getTrendStockDataArray();
	}
	
	/**
	 * 初始化 控件
	 */
	private void initView() {
		mFragmentMgr = getSupportFragmentManager();
		//包含头部的 Radiogroup
		mRadioGroup=(RadioGroup) this.findViewById(R.id.head_navigation_tab);		
		mRadioGroup.setOnCheckedChangeListener(this);
		
		switch(mCurrentPage){
		case TRADE_ORDER:
			if (mOrderFrag == null)
			{
				mOrderFrag = new TradeOrderFragment(mMyApp);
			}
			addFragment(R.id.framelayout_trade_detail_activity, mOrderFrag);
			((RadioButton) mRadioGroup.findViewById(R.id.trade_detail_activity_order)).setChecked(true); 
			break;
			
		case TRADE_QUERY:
			if (mQueryFrag == null)
			{
				mQueryFrag = new TradeQueryFragment(mMyApp);
			}
		    addFragment(R.id.framelayout_trade_detail_activity, mQueryFrag);
		    ((RadioButton) mRadioGroup.findViewById(R.id.trade_detail_activity_query)).setChecked(true);
			break;
			
		case TRADE_POSITION:
			if (mPositionFrag == null)
			{
				mPositionFrag = new TradePositionFragment(mMyApp);
			}
			addFragment(R.id.framelayout_trade_detail_activity, mPositionFrag);
			((RadioButton) mRadioGroup.findViewById(R.id.trade_detail_activity_position)).setChecked(true);
			break;
		case TRADE_XINGQUAN:
			if(mXingQuanFrag==null){
				mXingQuanFrag=new TradeXingQuanFragment(mMyApp);
			}
			addFragment(R.id.framelayout_trade_detail_activity, mPositionFrag);
			((RadioButton) mRadioGroup.findViewById(R.id.trade_detail_activity_xingquan)).setChecked(true);
			break;
			
		case TRADE_MORE:
			if (mMoreFrag == null)
			{
				mMoreFrag = new TradeMoreFragment(mMyApp);
			}
			addFragment(R.id.framelayout_trade_detail_activity, mMoreFrag);
			((RadioButton) mRadioGroup.findViewById(R.id.trade_detail_activity_more)).setChecked(true);
			break;
		}
	}
	
	public void updateTradeOrderOptionData(String keyongNum) {
		if(mCurrentPage == TRADE_ORDER)
		{
			if (mOrderFrag != null)
			{
				mOrderFrag.updateOptionData(false);
				mOrderFrag.updateHQView(keyongNum, true);
			}
		}else
		{
			((RadioButton) mRadioGroup.findViewById(R.id.trade_detail_activity_order)).setChecked(true); 	
		}
	}
	
	
	public void requestWTCDFromCheDanView(TradeLocalRecord record)
	{
		if (record == null || !DataTools.isCDStatusEnabled(record.mWTZT))
		{
			Toast.makeText(mMyApp.mTradeDetailActivity.getParent(), "此委托无法撤单" , Toast.LENGTH_SHORT)
			.show();
		}
		
		mTradeRecord.copy(record);
		String strMsg = String.format("委托编号：%s\r\n委托日期：%s\r\n委托状态：%s\r\n期权标的：%s(%s)\r\n委托价格：%s\r\n委托数量：%s\r\n\r\n您确认撤单吗？", 
				record.mWTBH, record.mWTSHJ, record.mWTZTMC, record.mBiaodiMC, record.mBiaodiCode
				, record.mWTPrice, record.mWTSL);
		final String wtbh = record.mWTBH;
		new com.pengbo.mhdcx.widget.AlertDialog(MyApp.getInstance().mTradeDetailActivity.getParent()).builder().setTitle("撤单")			
		.setMsg(strMsg)
		.setCancelable(false)
		.setCanceledOnTouchOutside(false)
		.setPositiveButton("确认", new OnClickListener() {
			@Override
			public void onClick(View v) {
				requestWTCD(mTradeRecord, wtbh);
			}
		}).setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(View v) {	
			}
		}).show(); 
		
	}
	
	/*
	 * Fragment switch
	 * 
	 */
	private void turnToFragment(Fragment fromFragment,
			Fragment toFragment, Bundle args) {

		if (fromFragment == null)
		{
			addFragment(R.id.framelayout_trade_detail_activity, toFragment);
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
			transaction.hide(fromFragment).add(R.id.framelayout_trade_detail_activity, toFragment, toTag);
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
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId){
		case R.id.trade_detail_activity_order:
			L.d(TAG, "onCheckedChanged trade_detail_activity_order");
			if (mOrderFrag == null)
			{
				mOrderFrag = new TradeOrderFragment(mMyApp);
			}
			turnToFragment(mCurrentFragment, mOrderFrag, null);
			setCurrentPage(TRADE_ORDER);
			requestDRWT();//当日委托回报后会查询持仓和资金
			break;
			
		case R.id.trade_detail_activity_query:
			L.d(TAG, "onCheckedChanged trade_detail_activity_query");
			if (mQueryFrag == null)
			{
				mQueryFrag = new TradeQueryFragment(mMyApp);
			}
			turnToFragment(mCurrentFragment, mQueryFrag, null);
			setCurrentPage(TRADE_QUERY);
			requestDRWT();
			requestDRCJ();
			break;
			
		case R.id.trade_detail_activity_position:
			L.d(TAG, "onCheckedChanged trade_detail_activity_position");
			if (mPositionFrag == null)
			{
				mPositionFrag = new TradePositionFragment(mMyApp);
			}
			turnToFragment(mCurrentFragment, mPositionFrag, null);
			setCurrentPage(TRADE_POSITION);
			requestZJ();
			requestHoldStock();
			break;
			
		case R.id.trade_detail_activity_xingquan:
			L.d(TAG, "onCheckedChanged trade_detail_activity_xingquan");
			if (mXingQuanFrag== null)
			{
				mXingQuanFrag = new TradeXingQuanFragment(mMyApp);
			}
			turnToFragment(mCurrentFragment, mXingQuanFrag, null);
			setCurrentPage(TRADE_XINGQUAN);
			requestHoldStock();
			break;
			
		case R.id.trade_detail_activity_more:
			L.d(TAG, "onCheckedChanged trade_detail_activity_more");
			if (mMoreFrag == null)
			{
				mMoreFrag = new TradeMoreFragment(mMyApp);
			}
			turnToFragment(mCurrentFragment, mMoreFrag, null);
			setCurrentPage(TRADE_MORE);
			break;
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		Bundle bundle = intent.getExtras();
		int page = mCurrentPage;
		if (bundle != null)
		{
			page = bundle.getInt(INTENT_SERIALIZABLE_CURRENTPAGE);
		}
		
		switch(page){
		case TRADE_ORDER:
			L.d(TAG, "onNewIntent TRADE_ORDER");
			((RadioButton) mRadioGroup.findViewById(R.id.trade_detail_activity_order)).setChecked(true); 
			break;
			
		case TRADE_QUERY:
			L.d(TAG, "onNewIntent TRADE_QUERY");
		    ((RadioButton) mRadioGroup.findViewById(R.id.trade_detail_activity_query)).setChecked(true);
			break;
			
		case TRADE_POSITION:
			L.d(TAG, "onNewIntent TRADE_POSITION");
			((RadioButton) mRadioGroup.findViewById(R.id.trade_detail_activity_position)).setChecked(true);
			break;
			
		case TRADE_XINGQUAN:
			L.d(TAG, "onNewIntent TRADE_XINGQUAN");
			((RadioButton) mRadioGroup.findViewById(R.id.trade_detail_activity_xingquan)).setChecked(true);
			break;
		case TRADE_MORE:
			L.d(TAG, "onNewIntent TRADE_MORE");
			((RadioButton) mRadioGroup.findViewById(R.id.trade_detail_activity_more)).setChecked(true);
			break;
		}
		super.onNewIntent(intent);
	}

	@Override
	protected void onResume()
	{
		ViewTools.isShouldForegraund = true;
		super.onResume();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
	
	private void proc_MSG_LOCK(Message msg)
    {
    	L.d(TAG, "proc_MSG_LOCK");
    	
    	new com.pengbo.mhdcx.widget.AlertDialog(mMyApp.mMainTabActivity)
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
				
				if (mOrderFrag != null)
				{
					if (mOrderFrag.mWTConfirmDialog != null)
					{
						mOrderFrag.mWTConfirmDialog.dismiss();
					}
					if (mOrderFrag.mTrendDialog != null)
					{
						mOrderFrag.mTrendDialog.dismiss();
					}
					if (mOrderFrag.mKeyboard != null)
					{
						mOrderFrag.mKeyboard.dismiss();
					}
					if(mOrderFrag.mActionSheetDlg != null)
					{
						mOrderFrag.mActionSheetDlg.dismiss();
					}
				}
				
				if (mXingQuanFrag != null)
				{
					if (mXingQuanFrag.mXQConfirmDialog != null)
					{
						mXingQuanFrag.mXQConfirmDialog.dismiss();
					}
					if (mXingQuanFrag.mXQInputDialog != null)
					{
						mXingQuanFrag.mXQInputDialog.dismiss();
					}
					if (mXingQuanFrag.mDatePickerDialog != null)
					{
						mXingQuanFrag.mDatePickerDialog.dismiss();
					}
				}
				
				Intent intent = new Intent();
				MainTabActivity act = AppActivityManager.getAppManager().getMainTabActivity();
				
				if (act != null) {
					act.setChangePage(MainTabActivity.PAGE_TRADE);
				}
				intent.setClass(mMyApp.mTradeDetailActivity, MainTabActivity.class);
				startActivity(intent);
				//mMyApp.mTradeDetailActivity.finish();
			}
		}).show();
    }

	protected void proc_MSG_TIMEOUT(Message msg) {
		if(mMyApp.mTradeData.mReconnected)
		{
		     new com.pengbo.mhdzq.widget.AlertDialog(mMyApp.mMainTabActivity).builder().setTitle("提示")
				.setMsg("网络请求超时").setCancelable(false)
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
		    new com.pengbo.mhdzq.widget.AlertDialog(mMyApp.mMainTabActivity).builder().setTitle("提示")
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
	
	protected void proc_MSG_DISCONNECT(Message msg) {
		closeProgress();
		if(mMyApp.mTradeData.mReconnected)
		{
		    new com.pengbo.mhdzq.widget.AlertDialog(mMyApp.mMainTabActivity).builder().setTitle("提示")
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
		    new com.pengbo.mhdzq.widget.AlertDialog(mMyApp.mMainTabActivity).builder().setTitle("提示")
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
		if (mOrderFrag != null)
		{
			if (mOrderFrag.mWTConfirmDialog != null)
			{
				mOrderFrag.mWTConfirmDialog.dismiss();
			}
			if (mOrderFrag.mTrendDialog != null)
			{
				mOrderFrag.mTrendDialog.dismiss();
			}
			if (mOrderFrag.mKeyboard != null)
			{
				mOrderFrag.mKeyboard.dismiss();
			}
			if(mOrderFrag.mActionSheetDlg != null)
			{
				mOrderFrag.mActionSheetDlg.dismiss();
			}
		}
		if (mXingQuanFrag != null)
		{
			if (mXingQuanFrag.mXQConfirmDialog != null)
			{
				mXingQuanFrag.mXQConfirmDialog.dismiss();
			}
			if (mXingQuanFrag.mXQInputDialog != null)
			{
				mXingQuanFrag.mXQInputDialog.dismiss();
			}
			if (mXingQuanFrag.mDatePickerDialog != null)
			{
				mXingQuanFrag.mDatePickerDialog.dismiss();
			}
		}
		
		Intent intent = new Intent();
		MainTabActivity act = AppActivityManager.getAppManager().getMainTabActivity();
		
		if (act != null) {
			act.setChangePage(MainTabActivity.PAGE_TRADE);
		}
		intent.setClass(mMyApp.mTradeDetailActivity, MainTabActivity.class);
		startActivity(intent);
	}
	
	@Override
	public void requestHoldStock()
	{
		mMyApp.mTradeNet.setMainHandler(mHandler);
		//市场代码null代表查询所有市场
		mRequestCode[0] = mMyApp.mTradeNet.Request_HoldStock(null, null);
	}
	
	@Override
	public void requestZJ()
	{
		mMyApp.mTradeNet.setMainHandler(mHandler);
		StringBuffer str = new StringBuffer();
		str = str.append(STEP_Define.STEP_HBDM).append(":").append(Trade_Define.MType_RMB);
		mRequestCode[1] = mMyApp.mTradeNet.Request_ListQuery(Trade_Define.Func_Money,str.toString());	
	}
	
	@Override
	public void requestDRCJ()
	{
		mMyApp.mTradeNet.setMainHandler(mHandler);
		mMyApp.mTradeNet.Request_ListQuery(Trade_Define.Func_DRCJ, null);
	}
	
	@Override
	public void requestDRWT()
	{
		mMyApp.mTradeNet.setMainHandler(mHandler);
		mMyApp.mTradeNet.Request_ListQuery(Trade_Define.Func_DRWT, null);
	}
	
	@Override
	public void requestXQ(String marketCode, String stockCode, String xqsl, String gdzh, String xwh, String sqType) {
		mMyApp.mTradeNet.setMainHandler(mHandler);
		mMyApp.mTradeNet.Request_XQ(marketCode, stockCode, xqsl, gdzh, xwh, sqType);
	}

	@Override
	public void requestKXQSL(String stockCode, String marketCode, String gdzh, String xwh) {
		mMyApp.mTradeNet.setMainHandler(mHandler);
		mMyApp.mTradeNet.Request_KXQSL(stockCode, marketCode, gdzh, xwh);
		
		if(mXingQuanFrag != null && (mCurrentPage == TRADE_XINGQUAN))
		{
			PBSTEP aStep = new PBSTEP();
			mXingQuanFrag.ConfirmToXQ(aStep);
		}
	}

	@Override
	public void requestXQCD(String marketCode, String wtbh, String gdzh, String xwh, String xdxw) {
		mMyApp.mTradeNet.setMainHandler(mHandler);
		mMyApp.mTradeNet.Request_XQCD(marketCode, wtbh, gdzh, xwh, xdxw);
	}

	@Override
	public void requestXQWT() {
		mMyApp.mTradeNet.setMainHandler(mHandler);
		mMyApp.mTradeNet.Request_ListQuery(Trade_Define.Func_FJYWTCX, null);
	}
	
	@Override
	public void requestXQZP(String marketCode, String stockCode, String gdzh) {
		mMyApp.mTradeNet.setMainHandler(mHandler);
		mMyApp.mTradeNet.Request_ListQuery(Trade_Define.Func_XQZP, null);
	}
	
	@Override
	public void requestXQLSZP(String startDate, String endDate) {
		mMyApp.mTradeNet.setMainHandler(mHandler);
		String param = "";
		param = String.format("%d:%s|%d:%s", STEP_Define.STEP_QSRQ, startDate, STEP_Define.STEP_ZZRQ, endDate); 
		mMyApp.mTradeNet.Request_ListQuery(Trade_Define.Func_LSXQZP, param);
	}
	
	@Override
	public void requestWTCD(TradeLocalRecord record, String wtbh) {
		mMyApp.mTradeNet.setMainHandler(mHandler);
		
		PBSTEP drwtList = new PBSTEP();
		mMyApp.mTradeData.GetDRWT(drwtList);

	    for (int i = 0; i < drwtList.GetRecNum(); i++) {
	        
	        drwtList.GotoRecNo(i);
	        String tempWTBH		= drwtList.GetFieldValueString(STEP_Define.STEP_WTBH);
	        String code		= drwtList.GetFieldValueString(STEP_Define.STEP_HYDM);
	        if (tempWTBH.equalsIgnoreCase(wtbh))
	        {
	            String wtzt		= drwtList.GetFieldValueString(STEP_Define.STEP_WTZT);
	            if (DataTools.isCDStatusEnabled(wtzt) == false)
	            {
	                L.d(TAG, "requestWTCD: Can‘t CD");
	                return;
	            }
	            
	            record.mWTBH = drwtList.GetFieldValueString(STEP_Define.STEP_WTBH);
	            record.mWTSHJ = drwtList.GetFieldValueString(STEP_Define.STEP_WTRQ);
				record.mGDZH = drwtList.GetFieldValueString(STEP_Define.STEP_GDH);
				record.mMarketCode = drwtList.GetFieldValueString(STEP_Define.STEP_SCDM);
				record.mXWH = drwtList.GetFieldValueString(STEP_Define.STEP_XWH);
				record.mXDXW = drwtList.GetFieldValueString(STEP_Define.STEP_XDXW);
				record.mWTZT = drwtList.GetFieldValueString(STEP_Define.STEP_WTZT);
				record.mWTZTMC = drwtList.GetFieldValueString(STEP_Define.STEP_WTZTMC);
				record.mBiaodiCode = drwtList.GetFieldValueString(STEP_Define.STEP_BDDM);
				record.mBiaodiMC = drwtList.GetFieldValueString(STEP_Define.STEP_BDMC);
				record.mWTPrice = drwtList.GetFieldValueString(STEP_Define.STEP_WTJG);
				record.mWTSL = drwtList.GetFieldValueString(STEP_Define.STEP_WTSL);
				
	            //委托撤单
	            mRequestCode[12] = mMyApp.mTradeNet.Request_WTCD(record.mWTBH,
	    				record.mWTSHJ, record.mGDZH, record.mMarketCode, record.mXWH,
	    				record.mXDXW);
	            L.d(TAG, "requestWTCD: CD");
	            break;
	        }
	    }
		
		if(mOrderFrag != null && mOrderFrag.mTradeWTBHArray.contains(record.mWTBH))
		{
			this.mbKJFSRuning = false;
		}
	}
	
	@Override
	public void requestKMSL(String price, TagLocalStockData optionData, char sjType)
	{
	    if (!mMyApp.mTradeData.mTradeLoginFlag) {
	        return;
	    }
	    
	    String market = TradeData.GetTradeMarketFromHQMarket(optionData.HQData.market, optionData.group);

	    String gdzh = mMyApp.mTradeData.GetGDZHFromMarket(market);
	    String xwh = mMyApp.mTradeData.GetXWHFromMarket(market);
	    
	    //6-9可买数量查询(6:买开 7:卖平 8:卖开 9:买平)
	    mMyApp.mTradeNet.setMainHandler(mHandler);
	    mRequestCode[6] = mMyApp.mTradeNet.Request_KMSL(market, optionData.HQData.code, PTK_Define.PTK_D_Buy, PTK_Define.PTK_OF_Open, price, gdzh,xwh,0,sjType);
	    mRequestCode[7] = mMyApp.mTradeNet.Request_KMSL(market, optionData.HQData.code, PTK_Define.PTK_D_Sell, PTK_Define.PTK_OF_Close, price, gdzh,xwh,0,sjType);
	    mRequestCode[8] = mMyApp.mTradeNet.Request_KMSL(market, optionData.HQData.code, PTK_Define.PTK_D_Sell, PTK_Define.PTK_OF_Open, price, gdzh,xwh,0,sjType);
	    mRequestCode[9] = mMyApp.mTradeNet.Request_KMSL(market, optionData.HQData.code, PTK_Define.PTK_D_Buy, PTK_Define.PTK_OF_Close, price, gdzh,xwh,0,sjType);
	}

	@Override
	public void requestWT(TradeLocalRecord record) {
		mMyApp.mTradeNet.setMainHandler(mHandler);
		int nCDSL = PreferenceEngine.getInstance().getTradeMRCDNum();
		int nWTSL = STD.StringToInt(record.mWTSL);
		if (nCDSL > 0 && nCDSL < nWTSL)//拆单
		{
			if(this.mWTRequestCodeArray == null)
			{
				mWTRequestCodeArray = new ArrayList<Integer>();
			}
			mWTRequestCodeArray.clear();
			int nTotal = nWTSL;
			int nRequestCode = 0;
			while(nTotal > 0)
			{
				int currentSL = 0;
	            if (nTotal > nCDSL) {
	            	currentSL = nCDSL;
	            }
	            else
	            {
	            	currentSL = nTotal;
	            }
	            nTotal -= currentSL;
	            String wtsl = STD.IntToString(currentSL);
	            nRequestCode = mMyApp.mTradeNet.Request_WT(record.mMarketCode,
						record.mStockCode, record.mMMLB, record.mKPBZ, wtsl,
						record.mWTPrice, record.mGDZH, record.mXWH, record.mBDFlag, record.mSJType);
	            mWTRequestCodeArray.add(Integer.valueOf(nRequestCode));
	            try {
					Thread.sleep(mMyApp.mTradeData.m_wtIntervalTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}else
		{
			mRequestCode[5] = mMyApp.mTradeNet.Request_WT(record.mMarketCode,
					record.mStockCode, record.mMMLB, record.mKPBZ, record.mWTSL,
				record.mWTPrice, record.mGDZH, record.mXWH, record.mBDFlag, record.mSJType);
		}
	}
	
	//全部平仓和快捷反手
	@Override
	public void requestWTWithFlag(TradeLocalRecord record, boolean bOnlyPC) {
		if (!bOnlyPC && mbKJFSRuning)//快捷反手并且上一次快捷反手未结束
		{
			Toast.makeText(MyApp.getInstance().mTradeDetailActivity.getParent(), 
					"之前的快捷反手还未完成，请稍后再操作！", 
					Toast.LENGTH_SHORT).show();
			return;
		}
		mMyApp.mTradeNet.setMainHandler(mHandler);
		int nCDSL = PreferenceEngine.getInstance().getTradeMRCDNum();
		int nWTSL = STD.StringToInt(record.mWTSL);

		if (nCDSL > 0 && nCDSL < nWTSL)//拆单处理
		{
			if (bOnlyPC)//全部平仓
			{
				if(this.mWTRequestCodeArray == null)
				{
					mWTRequestCodeArray = new ArrayList<Integer>();
				}
				mWTRequestCodeArray.clear();
			}else
			{
				if(this.mKJFSRequestCodeArray == null)
				{
					mKJFSRequestCodeArray = new ArrayList<Integer>();
				}
				mKJFSRequestCodeArray.clear();
			}
			
			int nTotal = nWTSL;
			int nRequestCode = 0;
			while(nTotal > 0)
			{
				int currentSL = 0;
	            if (nTotal > nCDSL) {
	            	currentSL = nCDSL;
	            }
	            else
	            {
	            	currentSL = nTotal;
	            }
	            nTotal -= currentSL;
	            String wtsl = STD.IntToString(currentSL);
	            nRequestCode = mMyApp.mTradeNet.Request_WT(record.mMarketCode,
						record.mStockCode, record.mMMLB, record.mKPBZ, wtsl,
						record.mWTPrice, record.mGDZH, record.mXWH, record.mBDFlag, record.mSJType);
	            if (bOnlyPC)//全部平仓
	            {
	            	mWTRequestCodeArray.add(Integer.valueOf(nRequestCode));
	            }else
	            {
	            	mKJFSRequestCodeArray.add(Integer.valueOf(nRequestCode));
	            }
	            
	            try {
					Thread.sleep(mMyApp.mTradeData.m_wtIntervalTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}else
		{
			if (bOnlyPC)//全部平仓
			{
				mRequestCode[10] = mMyApp.mTradeNet.Request_WT(record.mMarketCode,
					record.mStockCode, record.mMMLB, record.mKPBZ, record.mWTSL,
					record.mWTPrice, record.mGDZH, record.mXWH, record.mBDFlag, record.mSJType);
			}else
			{
				mRequestCode[11] = mMyApp.mTradeNet.Request_WT(record.mMarketCode,
						record.mStockCode, record.mMMLB, record.mKPBZ, record.mWTSL,
						record.mWTPrice, record.mGDZH, record.mXWH, record.mBDFlag, record.mSJType);
			}
		}
	}

	@Override
	public void requestHQPushData(ArrayList<TagCodeInfo> codeList) {
		L.i(TAG, "requestHQPushData");
		
		int nCodeCount = 0;
	    if (codeList == null) {
	        //发送请求
	    	codeList = new ArrayList<TagCodeInfo>();
	    }
	    nCodeCount = codeList.size();
	    
	    String strMarket = "";
	    String strCode = "";
	    //添加持仓合约列表到推送
	    for (int i = 0; i < mHoldList.GetRecNum(); i++) {
	    	mHoldList.GotoRecNo(i);
            
	    	strMarket = mHoldList.GetFieldValueString(STEP_Define.STEP_SCDM);
            strCode = mHoldList.GetFieldValueString(STEP_Define.STEP_HYDM);
            
            int nMarket = TradeData.GetHQMarketFromTradeMarket(strMarket);
            
            boolean bAdd = true;
            for (int m = 0; m < nCodeCount; m++)
            {
                if (nMarket == codeList.get(m).market && strCode.equals(codeList.get(m).code))
                {
                    bAdd = false;
                    break;
                }
            }
            if (bAdd == true) {
            	TagCodeInfo aInfoItem = new TagCodeInfo((short) nMarket, strCode);
            	codeList.add(aInfoItem);
            }
        }
	    
	    if (mLocalRefreshWT) {

	        for (int i = 0; i < mDRWTCDList.GetRecNum(); i++) {
	            mDRWTCDList.GotoRecNo(i);
	            
	            strMarket = mDRWTCDList.GetFieldValueString(STEP_Define.STEP_SCDM);
	            strCode = mDRWTCDList.GetFieldValueString(STEP_Define.STEP_HYDM);
	            
	            int nMarket = TradeData.GetHQMarketFromTradeMarket(strMarket);
	            
	            boolean bAdd = true;
	            for (int m = 0; m < nCodeCount; m++)
	            {
	                if (nMarket == codeList.get(m).market && strCode.equals(codeList.get(m).code))
	                {
	                    bAdd = false;
	                    break;
	                }
	            }
	            if (bAdd == true) {
	            	TagCodeInfo aInfoItem = new TagCodeInfo((short) nMarket, strCode);
	            	codeList.add(aInfoItem);
	            }
	        }
	    }
	    
	    L.i(TAG, "requestHQPushData send");
	    mMyApp.setHQPushNetHandler(mHandler);
	    mRequestCode[4] = GlobalNetProgress.HQRequest_MultiCodeInfoPush(
	    			mMyApp.mHQPushNet, codeList, 0, codeList.size());
	    
	}

	@Override
	public void requestHQTrendLine(TagLocalStockData optionData, TagLocalStockData stockData) {
		L.i(TAG, "requestHQTrendLine");
		// send request to retrieve trend line
		mMyApp.setCertifyNetHandler(mHandler);
		mRequestCode[2] = GlobalNetProgress.HQRequest_TrendLine(
				mMyApp.mCertifyNet, optionData.HQData.code,
				optionData.HQData.market, 0, (short) 0, optionData.Start[0]);
		this.mCurrentStockData = stockData;
	}
	
	// send request to retrieve Stock trend line
	public void requestHQStockTrendLine(TagLocalStockData stockData) {
		mMyApp.setCertifyNetHandler(mHandler);
		mRequestCode[3] = GlobalNetProgress.HQRequest_TrendLine(
				mMyApp.mCertifyNet, stockData.HQData.code,
				stockData.HQData.market, 0, (short) 0, stockData.Start[0]);
	}
	
	// parse trend data and store it into ArrayList<TagLocalTrendData>
		private void parseTrendData(byte[] szData, int nSize, boolean isBiaoDi) {
			L.e("MyApp", "Start parseTrendData");

			int offset = 0;
			int wPackSize = MyByteBuffer.getInt(szData, offset);
			offset += 4;
			if (wPackSize > nSize - offset || nSize == 0) {
				L.e("MyApp", "ERROR parseTrendData data error");
				return;
			}

			CPbDataPackage pack = new CPbDataPackage();
			CPbDataDecode.DecodeOnePackage(szData, offset, wPackSize, pack);

			if (isBiaoDi)
			{
				mStockTrendDataArray.clear();
			}else
			{
				mTrendDataArray.clear();
			}
			int nTrendNum = 0;
			double lastVolume = 0;// 存储上一笔成交量
			for (int i = 0; i < pack.m_nItemSize; ++i) {
				CPbDataItem item = pack.m_DataItems.get(i);
				if (item.m_ItemType == CPbDataItem.DIT_NORMAL) {
					if (item.m_NormalField.IsValid()) {
						String strSection = item.m_NormalField.m_szFieldName;
						switch (item.m_NormalField.m_FieldID) {
						case 10:
							// nMarketId = item.m_NormalField.GetInt16();
							L.e("MyApp",
									strSection + "="
											+ item.m_NormalField.GetInt16());
							break;
						case 11:
							// int nMarketId = item.m_NormalField.GetInt16();
							L.e("MyApp",
									strSection + "="
											+ item.m_NormalField.GetInt16());
							break;
						case 23:
							// int nLastClose = item.m_NormalField.GetInt32();
							L.e("MyApp",
									strSection + "="
											+ item.m_NormalField.GetInt32());
							break;
						case 24:
							// int nLastClear = item.m_NormalField.GetInt32();
							L.e("MyApp",
									strSection + "="
											+ item.m_NormalField.GetInt32());
							break;
						default:
							L.e("MyApp", "App unused field:"
									+ item.m_NormalField.m_FieldID + "->"
									+ strSection);
							break;
						}
					}
				} else {
					for (int m = 0; m < item.nArraySize; ++m) {
						TagLocalTrendData aRecord = new TagLocalTrendData();
						for (int n = 0; n < item.nSubFields; ++n) {
							int nIndex = n + m * item.nSubFields;
							CPbDataField field = item.m_ArrayValue.get(nIndex);

							if (field.IsValid()) {
								// String strSection = field.m_szFieldName;
								switch (field.m_FieldID) {
								case 21:
									aRecord.date = field.GetInt32();
									// L.e("MyApp", strSection + "=" +
									// aRecord.date);
									break;
								case 22:
									aRecord.time = field.GetInt32();
									// L.e("MyApp", strSection + "=" +
									// aRecord.time);
									break;
								case 29:
									aRecord.now = field.GetInt32();
									// L.e("MyApp", strSection + "=" + aRecord.now);
									break;
								case 39:
									aRecord.ccl = field.GetDouble();
									// L.e("MyApp", strSection + "=" +
									// field.GetDouble());
									break;
								case 35:
									aRecord.volume = field.GetDouble();
									// L.e("MyApp", strSection + "=" +
									// field.GetDouble());
									break;
								case 36:
									aRecord.amount = field.GetDouble();
									// L.e("MyApp", strSection + "=" +
									// field.GetDouble());
									break;
								case 37:
									aRecord.volSell = field.GetDouble();
									// L.e("MyApp", strSection + "=" +
									// field.GetDouble());
									break;
								default:
									// L.e("MyApp", "App unused field:" +
									// field.m_FieldID + "->" + strSection);
									break;
								}
							}
						}
						nTrendNum++;
						if (nTrendNum > TagLocalTrendData.MAX_TREND_NUM) {
							L.e("nTrendNum > MAX_TREND_NUM");
							break;
						}

						// 计算当前成交量（转换为与上一笔的之差）
						double saveVolume = lastVolume;
						if (aRecord.volume > 0) {
							saveVolume = aRecord.volume;
						}
						if (aRecord.now == 0 && aRecord.volume == 0) {
							aRecord.volume = 0;
						} else {
							aRecord.volume = aRecord.volume - lastVolume;
						}
						lastVolume = saveVolume;

						if (isBiaoDi)
						{
							mStockTrendDataArray.add(aRecord);
							L.e("mStockTrendDataArray Add a record[" + nTrendNum + "]:"
									+ aRecord.time + ",Price=" + aRecord.now
									+ ", volume=" + aRecord.volume);
						}else
						{
							mTrendDataArray.add(aRecord);
							L.e("mTrendDataArray Add a record[" + nTrendNum + "]:"
									+ aRecord.time + ",Price=" + aRecord.now
									+ ", volume=" + aRecord.volume);
						}
						
						aRecord = null;
					}
				}
			}

			L.e("MyApp", "End parseTrendData,nRecordCount=" + nTrendNum);
		}
		
		private void processHQPushData()
		{
			if (mLocalRefreshWT && mMyApp.mTradeData.mTradeLoginFlag) {
		        
		        if (mTimerRequestDRWT != null) {
		            //如果有请求正在等待发送，则不用再判断是否有更新
		            L.i(TAG, "mTimerRequestDRWT isValid");
		            return;
		        }
		        
		        TagLocalStockData optionData = new TagLocalStockData();
		        for (int i = 0; i < mDRWTCDList.GetRecNum(); i++) {
		            mDRWTCDList.GotoRecNo(i);
		            
		            String strMarket = mDRWTCDList.GetFieldValueString(STEP_Define.STEP_SCDM);
		            String strCode = mDRWTCDList.GetFieldValueString(STEP_Define.STEP_HYDM);
		            
		            int nMarket = TradeData.GetHQMarketFromTradeMarket(strMarket);
		            
		            if (mMyApp.mHQData.getData(optionData, (short) nMarket, strCode, true))
		            {
		                if (optionData.HQData.bNewUpdated == true) {
		                   
		                    long lInterval = maybeCloseWT(mDRWTCDList, optionData);
		                    if (lInterval > 0) { //判断可能有成交
		                        this.mTimerForRequestDRWT = lInterval;
		                        L.i(TAG, "startRequestDRWTTimer mIntervalForRequestDRWT=%d" + mTimerForRequestDRWT);
		                        this.startRequestDRWTTimer();
		                        break;
		                    }
		                }
		            }
		        }
		    }
		}
		
		private void startRequestDRWTTimer() 
		{
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
		
		private void stopRequestDRWTTimer()
		{
			if(mTimerRequestDRWT != null) {
				mTimerRequestDRWT.cancel();
			}
			mTimerRequestDRWT = null;
		}
		
		private void onRequestDRWT()
		{
			stopRequestDRWTTimer();
			this.requestDRWT();
			//this.requestHoldStock();
		}
		
		//可能有交易完成或关闭,返回刷新价格时间，0表示不需要刷新
		private long maybeCloseWT(PBSTEP aStep, TagLocalStockData optionData)
		{
		    float fCmpMinPrice = 0.00009f;
		    long fRet = 0;
		    
		    char sjType = aStep.GetFieldValueCHAR(STEP_Define.STEP_SJWTLB);
		    if(PTK_Define.PTK_OPT_LimitPrice != sjType && '\0' != sjType) //如果是非限价委托
		    {
		        L.e(TAG, "maybeCloseWT: sjwt sjType=%c" + sjType);
		        return 2000;//2sec
		    }
		    else
		    {
		        float fKPBZ = STD.StringToValue(aStep.GetFieldValueString(STEP_Define.STEP_KPBZ));
		        boolean bBuy = (fKPBZ == 0f?true:false);
		        float fWTPrice = STD.StringToValue(aStep.GetFieldValueString(STEP_Define.STEP_WTJG));
		        float fNowPrice = ViewTools.getPriceByFieldNo(Global_Define.FIELD_HQ_NOW, optionData);
		        float fAveragePrice = (float) optionData.HQData.currentCJAveragePrice;
		        
		        
		        L.i(TAG, String.format("maybeCloseWT: fWTPrice = %f,fNowPrice = %f,fAveragePrice = %f",fWTPrice,fNowPrice,fAveragePrice));
		        
		        if (bBuy) {
		            
		            if( optionData.HQData.sellPrice[0] < 1  )
		            {
		                // 涨停了，就认为买不到了
		                return 0;
		            }
		            float fSub = fWTPrice - fNowPrice;
		            float fSub1 = fWTPrice - fAveragePrice;
		            if( fSub > fCmpMinPrice || (fSub1 > fCmpMinPrice && fAveragePrice != 0))
		            {
		                L.i(TAG, "maybeCloseWT: return 1.0");
		                return 1000;
		            }
		            
		            if( ( fSub <= fCmpMinPrice && fSub > -fCmpMinPrice ) ||
		               ( fSub1 <= fCmpMinPrice && fSub1 > -fCmpMinPrice && fAveragePrice != 0) )
		            {
		                // 认为价格相等，可能存在需要立即刷新的
		                if (optionData.HQData.currentCJ > 0) {//当前有成交
		                    L.i(TAG, "maybeCloseWT: currentCJ = %f" + optionData.HQData.currentCJ);
		                    return 1000;
		                }
		                else
		                {
		                    return 0;
		                }
		            }
		        }
		        else
		        {
		            if(optionData.HQData.buyPrice[0] < 1 )
		            {
		                // 跌停了，就认为卖不出去了
		                return 0;
		            }
		            float fSub =  fNowPrice - fWTPrice;
		            float fSub1 = fAveragePrice - fWTPrice;
		            if( fSub > fCmpMinPrice || (fSub1 > fCmpMinPrice && fAveragePrice != 0))
		            {
		            	L.i(TAG, "maybeCloseWT: return 1.0");
		                return 1000;
		            }
		            
		            if( ( fSub <= fCmpMinPrice && fSub > -fCmpMinPrice ) ||
		               ( fSub1 <= fCmpMinPrice && fSub1 > -fCmpMinPrice && fAveragePrice != 0) )
		            {
		                // 认为价格相等，可能存在需要立即刷新的
		                if (optionData.HQData.currentCJ > 0) {//当前有成交
		                    L.i(TAG, "maybeCloseWT: currentCJ = %f" + optionData.HQData.currentCJ);
		                    return 1000;
		                }
		                else
		                {
		                    return 0;
		                }
		            }
		        }
		    }
		    return fRet;
		}

		@Override
		protected void onDestroy() {
			mMyApp.setHQPushNetHandler(null);
			mMyApp.setCertifyNetHandler(null);
			mMyApp.mTradeNet.setMainHandler(null);
			super.onDestroy();
		}

		@Override
		protected void onPause() {
			mMyApp.setCertifyNetHandler(null);
			mMyApp.setHQPushNetHandler(null);
			super.onPause();
		}

		@Override
		public void setKJFSRuning(boolean bRuning) {
			this.mbKJFSRuning = bRuning;
		}
		
		
		protected void showProgress(String msg) {
			closeProgress();

			if (mProgress == null) {
				mProgress = new Dialog(MyApp.getInstance().mTradeDetailActivity.getParent(), R.style.ProgressDialogStyle);
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
