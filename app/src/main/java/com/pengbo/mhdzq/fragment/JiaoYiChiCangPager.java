package com.pengbo.mhdzq.fragment;

import java.util.ArrayList;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.ZqCCAdapter;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.base.BasePager;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.net.CMessageObject;
import com.pengbo.mhdzq.net.GlobalNetConnect;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.TradeData;
import com.pengbo.mhdzq.trade.data.TradeNetConnect;
import com.pengbo.mhdzq.trade.data.Trade_Define;
import com.pengbo.mhdzq.view.AutoScaleTextView;
import com.pengbo.mhdzq.widget.AlertDialog;
import com.pengbo.mhdzq.zq_activity.ZQMarketDetailActivity;
import com.pengbo.mhdzq.zq_activity.ZhengQuanActivity;
import com.pengbo.mhdzq.zq_trade_activity.ZqTradeDetailActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 证券-交易持仓首页
 * 
 * @author pobo
 */
public class JiaoYiChiCangPager extends BasePager implements OnClickListener{

	private final static String TAG = "JiaoYiChiCangPager";
	public final static String INTENT_TRADE_PADE_INDEX = "trade_page_index";
	
	private View view;
	// Content View Elements
	private Button btn_buy, btn_sell, btn_chedan, btn_weituo, btn_more;
	private TextView tv_zongzichan;
	private AutoScaleTextView tv_zongyinkui, tv_zongshizhi, tv_kequ, tv_keyong;
	
	private ListView mCCListView;
	private ZqCCAdapter mChiCangAdapterCC;
	
	private com.pengbo.mhdzq.widget.AlertDialog mAlertDlg;
	// Content View Elements
	
	private int mRequestCode[]; // 0:资金 1：持仓 2：行情推送
	private PBSTEP mMoney;//资金
	private PBSTEP mHoldList;//持仓列表
	
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
				
				if (aMsgObject.nErrorCode < 0 && nFrameType != Trade_Define.Func_QRJZD) {
					//出错，弹出messagebox提醒用户
					if (mAlertDlg == null)
					{
						mAlertDlg = new com.pengbo.mhdzq.widget.AlertDialog(mActivity).builder();
					}
					mAlertDlg.dismiss();
					mAlertDlg.setTitle("委托")			
					.setMsg(aMsgObject.errorMsg)
					.setCancelable(false)
					.setCanceledOnTouchOutside(false)
					.setPositiveButton("确认", new OnClickListener() {
						@Override
						public void onClick(View v) {
						}
					}).show(); 
					return;
				}
				
				if (nFrameType == 1)// 交易连接，交换密钥
				{
					L.i(TAG,
							"Trade connect success, change secret key and request login");
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
						Toast.makeText(mActivity, "登录失败", 5000)
								.show();
					} else {
						// login success
						L.i(TAG, "Trade Login Successfully");
//						Toast.makeText(ZqTradeDetailActivity.this, "登录成功", 5000)
//								.show();
						mMyApp.mTradeData.mTradeLoginFlag = true;
						requestZJ();
						requestHoldStock();
						requestHQPushData(null, null);
						closeProgress();

					}
					
				}
				else if (nFrameType == Trade_Define.Func_Money)// 资金查询
				{
					updateZJView();
				}
				else if (nFrameType == Trade_Define.Func_HOLDSTOCK) // 持仓查询
				{
					updateTradeData(false);
					requestHQPushData(null, null);
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
				
				if (nFrameType == Global_Define.MFT_MOBILE_PUSH_DATA) {
					updateTradeData(true);
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
				
			case TradeNetConnect.MSG_ADAPTER_CC_BUY_BUTTON_CLICK: {
				proc_MSG_ADAPTER_BUY_BUTTON_CLICK(msg);
			}
				break;
			case TradeNetConnect.MSG_ADAPTER_CC_SELL_BUTTON_CLICK: {
				proc_MSG_ADAPTER_SELL_SELECT_CLICK(msg);
			}
				break;
			case TradeNetConnect.MSG_ADAPTER_CC_HANGQING_BUTTON_CLICK: {
				proc_MSG_ADAPTER_HANGQING_BUTTON_CLICK(msg);
			}
				break;
			default:
				break;
			}
		};
	};
	
	public JiaoYiChiCangPager(Activity activity) {
		super(activity);
	}


	/**
	 * 持仓点开中的买入跳转 
	 * @param msg
	 */
	protected void proc_MSG_ADAPTER_BUY_BUTTON_CLICK(Message msg) {

		final int pos = msg.arg1;
		L.i(TAG, "pos: " + pos);
		if (mHoldList != null) {
			int num = mHoldList.GetRecNum();
			if (pos < num) {
				mHoldList.GotoRecNo(pos);
				Intent intent = new Intent(mActivity, ZqTradeDetailActivity.class);
				intent.putExtra(INTENT_TRADE_PADE_INDEX, 0);
				String strMarket = mHoldList.GetFieldValueString(STEP_Define.STEP_SCDM);
			    String strCode = mHoldList.GetFieldValueString(STEP_Define.STEP_HYDM);
			        
			    int nMarket = TradeData.GetHQMarketFromTradeMarket(strMarket);
				intent.putExtra("STOCK_MARKET", nMarket);
				intent.putExtra("STOCK_CODE", strCode);
				mActivity.startActivity(intent);
			}
		}
	}

	/**
	 * 持仓点开中的卖出跳转 
	 * @param msg
	 */
	
	protected void proc_MSG_ADAPTER_SELL_SELECT_CLICK(Message msg) {


		final int pos = msg.arg1;
		L.i(TAG, "pos: " + pos);
		if (mHoldList != null) {
			int num = mHoldList.GetRecNum();
			if (pos < num) {
				mHoldList.GotoRecNo(pos);
				Intent intent = new Intent(mActivity, ZqTradeDetailActivity.class);
				intent.putExtra(INTENT_TRADE_PADE_INDEX, 1);				
				String strMarket = mHoldList.GetFieldValueString(STEP_Define.STEP_SCDM);
			    String strCode = mHoldList.GetFieldValueString(STEP_Define.STEP_HYDM);
			        
			    int nMarket = TradeData.GetHQMarketFromTradeMarket(strMarket);
				intent.putExtra("STOCK_MARKET", nMarket);
				intent.putExtra("STOCK_CODE", strCode);
				mActivity.startActivity(intent);
			}
		}
	
	}

	/**
	 * 持仓点开中的行情跳转 
	 * @param msg
	 */
	protected void proc_MSG_ADAPTER_HANGQING_BUTTON_CLICK(Message msg) {
		
		final int pos = msg.arg1;
		L.i(TAG, "pos: " + pos);
		if (mHoldList != null) {
			int num = mHoldList.GetRecNum();
			if (pos < num) {
				mHoldList.GotoRecNo(pos);
				String strMarket = mHoldList.GetFieldValueString(STEP_Define.STEP_SCDM);
			    String strCode = mHoldList.GetFieldValueString(STEP_Define.STEP_HYDM);   
			    int nMarket = TradeData.GetHQMarketFromTradeMarket(strMarket);
				String strName = mHoldList.GetFieldValueString(STEP_Define.STEP_HYDMMC);
			    Intent intent = new Intent(mActivity, ZQMarketDetailActivity.class);			   
			    TagCodeInfo optionCodeInfo = new TagCodeInfo((short)nMarket, strCode);
				ZQMarketDetailActivity.mOptionCodeInfo = optionCodeInfo;
				mActivity.startActivity(intent);
			}
		}
	
	}

	@Override
	public void initDetailView() {
		
		mRequestCode = new int[3];
		mMoney = new PBSTEP();
		mHoldList = new PBSTEP();
		
//		tvTitle.setText("证券-交易");
//		tvTitle.setVisibility(View.VISIBLE);
		
		mLlayUpDown.setVisibility(View.VISIBLE);
		tvTitleUp.setText("证券-交易");
		tvTitleDown.setText(mMyApp.mTradeData.mTradeAccount);
		
		imgRightRefresh.setVisibility(View.VISIBLE);
		imgRightRefresh.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				requestZJ();
				requestHoldStock();
			}});

		view = View.inflate(mActivity, R.layout.zq_trade_chicang, null);
		
		bindViews();
		initPagerView();
		
		flContent.addView(view);
		bPagerReady = true;
		
		ZhengQuanFragment frag = (ZhengQuanFragment) ((FragmentActivity)mActivity).getSupportFragmentManager().findFragmentByTag(ZhengQuanActivity.TAG);
		if (frag != null && frag.mCurrentView == ZhengQuanFragment.ZQ_VIEW_JIAOYI)
		{
			visibleOnScreen();
		}
	}
	
	private void initPagerView() {
	}
	

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_buy:
		{
			Intent intent = new Intent(mActivity, ZqTradeDetailActivity.class);
			intent.putExtra(INTENT_TRADE_PADE_INDEX, 0);
			mActivity.startActivity(intent);
		}
			break;
		case R.id.btn_sell:
		{
			Intent intent = new Intent(mActivity, ZqTradeDetailActivity.class);
			intent.putExtra(INTENT_TRADE_PADE_INDEX, 1);
			mActivity.startActivity(intent);
		}
			break;
		case R.id.btn_chedan:
		{
			Intent intent = new Intent(mActivity, ZqTradeDetailActivity.class);
			intent.putExtra(INTENT_TRADE_PADE_INDEX, 2);
			mActivity.startActivity(intent);
		}	
			break;
		case R.id.btn_weituo:
		{
			Intent intent = new Intent(mActivity, ZqTradeDetailActivity.class);
			intent.putExtra(INTENT_TRADE_PADE_INDEX, 3);
			mActivity.startActivity(intent);
		}	
			break;
		case R.id.btn_more:
		{
			Intent intent = new Intent(mActivity, ZqTradeDetailActivity.class);
			intent.putExtra(INTENT_TRADE_PADE_INDEX, 4);
			mActivity.startActivity(intent);
		}	
			break;
		
		default:
			break;
		}
	}

	private void bindViews() {
		btn_buy = (Button) view.findViewById(R.id.btn_buy);
		btn_buy.setOnClickListener(this);
		
		btn_sell = (Button) view.findViewById(R.id.btn_sell);
		btn_sell.setOnClickListener(this);
		
		btn_chedan = (Button) view.findViewById(R.id.btn_chedan);
		btn_chedan.setOnClickListener(this);
		
		btn_weituo = (Button) view.findViewById(R.id.btn_weituo);
		btn_weituo.setOnClickListener(this);
		
		btn_more = (Button) view.findViewById(R.id.btn_more);
        btn_more.setOnClickListener(this);
        
        tv_zongzichan = (TextView) view.findViewById(R.id.tv_zongzichan);
        tv_zongyinkui = (AutoScaleTextView) view.findViewById(R.id.tv_zongyinkui_value);
        tv_zongshizhi = (AutoScaleTextView) view.findViewById(R.id.tv_zongshizhi_value);
        tv_kequ = (AutoScaleTextView) view.findViewById(R.id.tv_kequ_value);
        tv_keyong = (AutoScaleTextView) view.findViewById(R.id.tv_keyong_value);
        
        
        mCCListView = (ListView) view.findViewById(R.id.zq_trade_cc_listview);

		mChiCangAdapterCC = new ZqCCAdapter(mHoldList, mActivity, mHandler);
		mChiCangAdapterCC.setNeedMenu(true);
		mCCListView.setAdapter(mChiCangAdapterCC);
    }
	
	private void updateZJView()
	{
		mMyApp.mTradeData.GetMoney(mMoney);
		mMyApp.mTradeData.GetHoldStock(mHoldList);
		if (mMoney == null || mMoney.GetRecNum() <= 0)
		{
			tv_zongzichan.setText(Global_Define.STRING_VALUE_EMPTY);
			tv_zongyinkui.setText(Global_Define.STRING_VALUE_EMPTY);
			tv_zongshizhi.setText(Global_Define.STRING_VALUE_EMPTY);
			tv_kequ.setText(Global_Define.STRING_VALUE_EMPTY);
			tv_keyong.setText(Global_Define.STRING_VALUE_EMPTY);
		}else
		{
			String temp = mMoney.GetFieldValueString(STEP_Define.STEP_ZZC);
			tv_zongzichan.setText(temp);
			
			double FDYK = 0;//浮动盈亏
		    for (int j = 0; j < mHoldList.GetRecNum(); j++) {
		    	mHoldList.GotoRecNo(j);

		        String strMarket = mHoldList.GetFieldValueString(STEP_Define.STEP_SCDM);
		        String strCode = mHoldList.GetFieldValueString(STEP_Define.STEP_HYDM);
		        
		        int nMarket = TradeData.GetHQMarketFromTradeMarket(strMarket);
		        
		        TagLocalStockData stockData = new TagLocalStockData();
		        mMyApp.mHQData_ZQ.getData(stockData, (short)nMarket, strCode, false);

		        float fPrice = ViewTools.getPriceByFieldNo(Global_Define.FIELD_HQ_NOW, stockData);
		        
		        float fCBJ = 0.0f;
		        fCBJ = STD.StringToValue(mHoldList.GetFieldValueString(STEP_Define.STEP_CBJ));
		        		
		        float fDQSL = 0f;
		        fDQSL = STD.StringToValue(mHoldList.GetFieldValueString(STEP_Define.STEP_DQSL));
		        
		        float fMMBZ = 0f;
		        fMMBZ = STD.StringToValue(mHoldList.GetFieldValueString(STEP_Define.STEP_MMLB));
		        
		        boolean bBuy = (fMMBZ == 0f ? true : false);
		        
		        if (bBuy) {
		            FDYK += (fPrice - fCBJ) * fDQSL;
		        }
		        else
		        {
		            FDYK += (fCBJ - fPrice) * fDQSL;
		        }
		    }
		    
		    //浮动盈亏
		    tv_zongyinkui.setText(String.format("%.2f", FDYK));
		    tv_zongyinkui.setTextColor(ViewTools.getColor((float)FDYK));
			
			temp = mMoney.GetFieldValueString(STEP_Define.STEP_ZSZ);
			tv_zongshizhi.setText(temp);
			
			temp = mMoney.GetFieldValueString(STEP_Define.STEP_KQZJ);
			tv_kequ.setText(temp);
			
			temp = mMoney.GetFieldValueString(STEP_Define.STEP_KYZJ);
			tv_keyong.setText(temp);
		}
	}
	
	private void updateTradeData(boolean isPushRefresh) {
		updateZJView();
		mMyApp.mTradeData.GetHoldStock(mHoldList);
		mChiCangAdapterCC.notifyDataSetChanged();
	}
	
	public void requestZJ()
	{
		mMyApp.mTradeNet.setMainHandler(mHandler);
		StringBuffer str = new StringBuffer();
		str = str.append(STEP_Define.STEP_HBDM).append(":").append(Trade_Define.MType_RMB);
		mRequestCode[0] = mMyApp.mTradeNet.Request_ListQuery(Trade_Define.Func_Money,str.toString());	
	}
	
	public void requestHoldStock(){
		mMyApp.mTradeNet.setMainHandler(mHandler);
		//市场代码null代表查询所有市场
		mRequestCode[1] = mMyApp.mTradeNet.Request_HoldStock(null, null);
	}
	
	public void requestHQPushData(TagCodeInfo optionInfo, ArrayList<TagCodeInfo> codeList) {
		
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
            
            String code_hq = mMyApp.mTradeData.GetHQCodeFromTradeCode(strCode);
            
            int nMarket = TradeData.GetHQMarketFromTradeMarket(strMarket);
            
            boolean bAdd = true;
            for (int m = 0; m < nCodeCount; m++)
            {
                if (nMarket == codeList.get(m).market && code_hq.equals(codeList.get(m).code))
                {
                    bAdd = false;
                    break;
                }
            }
            if (bAdd == true) {
            	TagCodeInfo aInfoItem = new TagCodeInfo((short) nMarket, code_hq);
            	codeList.add(aInfoItem);
            }
        }
	    
	    mMyApp.setHQPushNetHandler(mHandler);
	    mRequestCode[2] = GlobalNetProgress.HQRequest_MultiCodeInfoPush(
	    			mMyApp.mHQPushNet, codeList, 0, codeList.size());
	}
	
	private void proc_MSG_LOCK(Message msg)
    {
    	L.d(TAG, "proc_MSG_LOCK");
    	
    	closeProgress();
    	new AlertDialog(mActivity)
		.builder()
		.setTitle("交易登录超时")
		.setMsg("交易登录在线时间过长，需要重新登录！")
		.setCancelable(false)
		.setCanceledOnTouchOutside(false)
		.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mAlertDlg != null)
				{
					mAlertDlg.dismiss();
				}
				mMyApp.setHQPushNetHandler(null);
				GlobalNetProgress.HQRequest_MultiCodeInfoPush(mMyApp.mHQPushNet, null, 0, 0);
				mMyApp.mTradeNet.setMainHandler(null);
				mMyApp.mTradeData.quitTrade();

				ZhengQuanFragment frag = (ZhengQuanFragment) ((FragmentActivity)mActivity).getSupportFragmentManager().findFragmentByTag(ZhengQuanActivity.TAG);
				if (frag != null)
				{
					frag.updateViewPagerItem(new JiaoYiPager(mActivity), 2);
				}
			}
		}).show();
    }

	private void proc_MSG_TIMEOUT(Message msg)
    {
		L.d(TAG, "proc_MSG_DISCONNECT");
    	closeProgress();

		if(mMyApp.mTradeData.mReconnected)
		{
		    new com.pengbo.mhdzq.widget.AlertDialog(mActivity).builder().setTitle("提示")
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
					if (mAlertDlg != null)
					{
						mAlertDlg.dismiss();
					}
					processNetLose();
			   }})
		    .show();
		}
		else
		{
		    new com.pengbo.mhdzq.widget.AlertDialog(mActivity).builder().setTitle("提示")
		    .setMsg("网络请求超时").setCancelable(false)
		    .setCanceledOnTouchOutside(false)
		    .setPositiveButton("重新连接", new OnClickListener() {

			   @Override
			   public void onClick(View v) {
				// TODO Auto-generated method stub
					if (mAlertDlg != null)
					{
						mAlertDlg.dismiss();
					}
				   processReconnect();
			   }
		    }).show();
		}
    }

	private void processNetLose()
	{
		mMyApp.mTradeData.mReconnected = false;
		mMyApp.setHQPushNetHandler(null);
		GlobalNetProgress.HQRequest_MultiCodeInfoPush(mMyApp.mHQPushNet, null, 0, 0);
		mMyApp.mTradeNet.setMainHandler(null);
		mMyApp.mTradeData.quitTrade();

		ZhengQuanFragment frag = (ZhengQuanFragment) ((FragmentActivity)mActivity).getSupportFragmentManager().findFragmentByTag(ZhengQuanActivity.TAG);
		if (frag != null)
		{
			frag.updateViewPagerItem(new JiaoYiPager(mActivity), 2);
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

		    showProgress("");

		    mMyApp.resetAddrNum_Trade(mMyApp.mTrade_reconnectAddrNum);
		    mMyApp.mTradeNet.setMainHandler(mHandler);
		    mMyApp.mTradeData.mTradeLockTimeout = PreferenceEngine.getInstance().getTradeOnlineTime();
		    mMyApp.mTradeNet.initSocketThread();
		}
	}
	
	protected void proc_MSG_DISCONNECT(Message msg) {
    	
		L.d(TAG, "proc_MSG_DISCONNECT");
    	closeProgress();

		if(mMyApp.mTradeData.mReconnected)
		{
		    new com.pengbo.mhdzq.widget.AlertDialog(mActivity).builder().setTitle("提示")
		    .setMsg(mActivity.getString(R.string.reconnect_warn)).setCancelable(false)
		    .setCanceledOnTouchOutside(false)
		    .setPositiveButton(mActivity.getString(R.string.connect_warn_btn), new OnClickListener() {

			   @Override
			   public void onClick(View v) {
				// TODO Auto-generated method stub
				   processReconnect();
			   }
		    })
		    .setNegativeButton(mActivity.getString(R.string.connect_warn_login),new OnClickListener(){

			   @Override
			   public void onClick(View v) {
					if (mAlertDlg != null)
					{
						mAlertDlg.dismiss();
					}
					processNetLose();
			   }})
		    .show();
		}
		else
		{
		    new com.pengbo.mhdzq.widget.AlertDialog(mActivity).builder().setTitle("提示")
		    .setMsg(mActivity.getString(R.string.connect_warn)).setCancelable(false)
		    .setCanceledOnTouchOutside(false)
		    .setPositiveButton(mActivity.getString(R.string.connect_warn_btn), new OnClickListener() {

			   @Override
			   public void onClick(View v) {
				// TODO Auto-generated method stub
					if (mAlertDlg != null)
					{
						mAlertDlg.dismiss();
					}
				   processReconnect();
			   }
		    }).show();
		}
	}

	@Override
	public void visibleOnScreen() {
		if (bPagerReady)
		{
			requestZJ();
			requestHoldStock();
			requestHQPushData(null, null);
		}
	}

	@Override
	public void invisibleOnScreen() {
		//mMyApp.mTradeNet.setMainHandler(null);
		//mMyApp.mHQPushNet.setMainHandler(null);
	}


}
