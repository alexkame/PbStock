package com.pengbo.mhdcx.ui.main_activity;

import java.util.ArrayList;

import com.pengbo.mhdcx.adapter.AccountDialogAdapter;
import com.pengbo.mhdcx.adapter.MyDialogAdapter;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.net.CMessageObject;
import com.pengbo.mhdzq.tools.Code;
import com.pengbo.mhdzq.tools.FileService;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.MIniFile;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.trade.data.TradeAccountInfo;
import com.pengbo.mhdzq.trade.data.TradeNetConnect;
import com.pengbo.mhdzq.trade.data.TradeQSInfo;
import com.pengbo.mhdzq.trade.data.TradeZJRecord;
import com.pengbo.mhdzq.trade.data.Trade_Define;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.ui.activity.CorrectHotOptionActivity;
import com.pengbo.mhdcx.ui.trade_activity.TradeAccountTypeActivity;
import com.pengbo.mhdcx.ui.trade_activity.TradeDetailActivity;
import com.pengbo.mhdcx.ui.trade_activity.TradeQSListActivity;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.zq_activity.HdActivity;
import com.pengbo.mhdcx.view.MyDigitKeyboardWindow;
import com.pengbo.mhdcx.widget.AlertDialog;
import com.pengbo.mhdcx.widget.MyListDialog;
import com.pengbo.mhdcx.widget.MyListDialog.Dialogcallback;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * 交易登录界面
 * author： pobo
 */
public class TradeLoginActivity extends HdActivity implements OnClickListener{

	private static final String TAG = "TradeLoginActivity";
	public static final String INTENT_QS_INDEX = "qs_index";
	
	private MyApp mMyApp;
	private Context mContext;
	public Dialog mProgress;
	
	public static final int REQUEST_CODE=1;
	public TextView mTV_head_option, mTV_head_searchlist; // 右边的搜索期权列表

	/**************券商  ***************/
	public LinearLayout mViewQS;//点击券商整个条目弹出券商选择的的对话框
	public TextView mTV_QS_Name;// 券商名称
	private ImageView mImg_QSSel;
	
	/**************帐户类型  ***************/
	public LinearLayout mViewAccount;//点击账户类型整个条目弹出帐户选择的的对话框
	public TextView mTV_account_type;// ----帐号类型（资金帐户   和  客户帐户 ）
	
	private LinearLayout mViewRenZhengKL;//认证口令
	private LinearLayout mViewYZM;//验证码
	private View mViewSepRZKL;//认证口令分割线
	private View mViewSepYZM;//验证码分割线
	public EditText mEdit_account,// ---交易帐号 数字 
			        mEdit_password,// --交易密码
			        mEdit_identify_password;// 认证密码
	private ImageView iv_showCode;
	private EditText et_phoneCode;
	//产生的验证码
	private String realCode;
	
	public MyDigitKeyboardWindow mKeyboard;
	
	public ImageView mImageAccountNum; // ---交易帐号
	
	public Button mBthLogin; //登陆验证按钮
	private ImageButton mBtnDelete; //清空密码按钮
	private ImageView mBtnAccountMore; //历史账号
	private MyListDialog mDlg_Account;
	private AccountDialogAdapter mDlg_AccountAdapter;
	
	private String mAccount; //交易账号
	private String mPassword; //交易密码
	private String mIdPassword; //认证密码
	private boolean mIsSave = false;
	private int mQSIndex;
	private String mQSAccountType;
	private String mSavedQSInfo; //保存的券商登录信息（券商id|账号类型|交易账号）
	
	private ArrayList<TradeQSInfo> mTradeQSList;
	private ArrayList<TradeAccountInfo> mAccountList;

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
					L.i(TAG,
							"Trade connect success, change secret key and request login");
					mMyApp.mTradeNet.setMainHandler(mHandler);

					String type = "0";
					type = STD.GetValue(mQSAccountType, 2, ',');
					if (type.isEmpty()) {
						type = "0";
					}
					
				    mMyApp.mQQTradeAccount = mMyApp.mTradeData.mTradeAccount;
			        mMyApp.mQQTradePassword = mMyApp.mTradeData.mTradePassword;
			        mMyApp.mQQHQType = mMyApp.mTradeData.mHQType;

			        mMyApp.mQQType = type;

					String sjhm = PreferenceEngine.getInstance().getPhoneForVerify();
					mMyApp.mTradeNet.Request_Login(
							mMyApp.mTradeData.mTradeAccount,
							mMyApp.mTradeData.mTradePassword, 0, 0, AppConstants.APP_VERSION_INFO, type, sjhm, 6);
				} else if (nFrameType == Trade_Define.Func_Login) {

					if (aMsgObject.nErrorCode < 0) {
						closeProgress();
						// 登陆出错，弹出messagebox提醒用户
						new com.pengbo.mhdcx.widget.AlertDialog(
								mMyApp.mTradeLoginActivity
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
						L.i(TAG, "Trade Login Successfully");
						if (mIsSave)
						{
							//交易成功，保存按钮选择，需要保存当前登录的券商信息
							//券商id|账号类型|登录账号
							saveQSLoginInfo(false);
							mMyApp.mTradeQSIndex = -1;
							mMyApp.mTradeQSZHType = "";
						}else
						{
							saveQSLoginInfo(true);
						}
						//登录成功，获取资金显示属性列表
						getZJDataList(mTradeQSList.get(mQSIndex).mID);
						
						mMyApp.mTradeData.mTradeLoginFlag = true;
						Intent intent = new Intent();
						intent.setClass(TradeLoginActivity.this,
								TradeDetailActivity.class).addFlags(
								Intent.FLAG_ACTIVITY_CLEAR_TOP);
						if (mMyApp.mbDirectInOrderPage) {
							Bundle mBundle = new Bundle();
							mBundle.putInt(
									TradeDetailActivity.INTENT_SERIALIZABLE_CURRENTPAGE,
									TradeDetailActivity.TRADE_ORDER);
							intent.putExtras(mBundle);
						}
						
						TradeGroupTabActivity.group.getLocalActivityManager().removeAllActivities();
						// 把第一个Activity 转换成一个View
						View view = TradeGroupTabActivity.group
								.getLocalActivityManager()
								.startActivity("TradeDetailActivity", intent)
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
		setContentView(R.layout.activity_trade);
		mContext = this;
		initData();
		initView();	
		initTradeAccount();//初始化交易帐号 
		initTradePassword();//初始化交易密码
		initTradeIdentifyPassword();//认证密码 
	}
	
	private void initData() {
		mMyApp = (MyApp)this.getApplication();
		mMyApp.mTradeLoginActivity = this;
		mTradeQSList = mMyApp.getTradeQSArray();
		mAccount = "";
		mPassword = "";
		mIsSave = PreferenceEngine.getInstance().getIsTradeAccountSaved();
		initQSList();
		mAccountList = new ArrayList<TradeAccountInfo>();
	}
	
	
	@Override
	protected void onResume() {
		initQSInfoView();
		if(mMyApp.isRenZhengKouLin)
		{
			this.mViewRenZhengKL.setVisibility(View.VISIBLE);
			this.mViewSepRZKL.setVisibility(View.VISIBLE);
		}else
		{
			this.mViewRenZhengKL.setVisibility(View.GONE);
			this.mViewSepRZKL.setVisibility(View.GONE);
		}
		if(mMyApp.isYanZhengMa)
		{
			this.mViewYZM.setVisibility(View.VISIBLE);
			this.mViewSepYZM.setVisibility(View.VISIBLE);
		}else
		{
			this.mViewYZM.setVisibility(View.GONE);
			this.mViewSepYZM.setVisibility(View.GONE);
		}
		super.onResume();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mTV_head_searchlist = (TextView) this
				.findViewById(R.id.header_right_search);
		mTV_head_option = (TextView) this
				.findViewById(R.id.header_middle_textview);

		mTV_head_option.setText("交易登录");
		mTV_head_searchlist.setVisibility(View.GONE);

		mBthLogin = (Button) this.findViewById(R.id.btn_login_trade_activity);
		mBthLogin.setOnClickListener(this);
		
		mBtnAccountMore =   (ImageView) this.findViewById(R.id.btn_trade_accout_more);
		mBtnAccountMore.setOnClickListener(this);
		
		mBtnDelete = (ImageButton) this.findViewById(R.id.trade_activity_image_trade_password);
		mBtnDelete.setOnClickListener(this);
		mBtnDelete.setVisibility(View.GONE);
		
		et_phoneCode = (EditText) findViewById(R.id.et_phoneCodes);
		iv_showCode = (ImageView) findViewById(R.id.iv_showCode);
		//将验证码用图片的形式显示出来
		iv_showCode.setImageBitmap(Code.getInstance().createBitmap());
		realCode = Code.getInstance().getCode();
		iv_showCode.setOnClickListener(this);
		
		mViewRenZhengKL = (LinearLayout) findViewById(R.id.trade_activity_linearlayout5);
		mViewSepRZKL = (View) findViewById(R.id.view_l5);
		mViewYZM = (LinearLayout) findViewById(R.id.trade_activity_linearlayout6);
		mViewSepYZM = (View) findViewById(R.id.view_l6);
	}
	
	// 隐藏系统键盘
	public void hideSoftInputMethod(EditText editText) {
		InputMethodManager imm = ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE));

		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(editText.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}

	}
		
	/**
	 * 初始化交易密码
	 */
	private void initTradePassword() {
		mEdit_password = (EditText) this.findViewById(R.id.trade_activity_business_trade_password);// --交易密码
		mEdit_password.addTextChangedListener(mTextWatcher);
		mEdit_password.setInputType(InputType.TYPE_NULL);
		mEdit_password.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_UP) {
					
					mEdit_password.setInputType(InputType.TYPE_NULL);
					hideSoftInputMethod(mEdit_password);
					if (mKeyboard == null) {
						mKeyboard = new MyDigitKeyboardWindow(mContext, itemsOnClick, mEdit_password);
						mKeyboard.setOutsideTouchable(true);
						mKeyboard.setFocusable(false);
						mKeyboard.showAtLocation(
								findViewById(R.id.trade_login),
								Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
								0);
					} else {
						mKeyboard.ResetKeyboard(mEdit_password);
						mKeyboard.setOutsideTouchable(true);
						mKeyboard.setFocusable(false);
						mKeyboard.showAtLocation(
								findViewById(R.id.trade_login),
								Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
								0);

					}
				}

				return false;
			}
		});
	}

	/**
	 * 初始化认证密码  
	 */
	private void initTradeIdentifyPassword() {
		mEdit_identify_password = (EditText) this.findViewById(R.id.trade_activity_business_identify_password);// 认证密码
	}

	/**
	 * 初始化交易帐号 
	 */
	private void initTradeAccount() {

		mEdit_account = (EditText) this
				.findViewById(R.id.trade_activity_business_trade_account);// ---交易帐号

		mEdit_account.setText(mAccount);
		mImageAccountNum =   (ImageView) this
				.findViewById(R.id.trade_activity_image_trade_account);// ---交易帐号
		if(mIsSave){
			mImageAccountNum.setBackgroundResource(R.drawable.save02);
		}else{
			mImageAccountNum.setBackgroundResource(R.drawable.save01);				
		}
		
		mEdit_account.setInputType(EditorInfo.TYPE_CLASS_PHONE);
			
		mImageAccountNum.setOnClickListener(this);
	}
	
	private void initQSInfoView() {
		mTV_QS_Name = (TextView) this
				.findViewById(R.id.trade_activity_business_hall);	
		
		mViewQS = (LinearLayout) this.findViewById(R.id.trade_activity_linearlayout1);		
		mImg_QSSel = (ImageView) this.findViewById(R.id.trade_activity_image_business_hall);
		
		mTV_account_type = (TextView) this
				.findViewById(R.id.trade_activity_business_fund_account);	
		
		mViewAccount = (LinearLayout) this.findViewById(R.id.trade_activity_linearlayout2);
		
		if (mTradeQSList.isEmpty())
		{
			mTV_QS_Name.setText("无");
			mTV_account_type.setText("");
			mQSIndex = -1;
			mQSAccountType = "";
			mAccount = "";
		}else
		{
			if(mMyApp.mTradeQSIndex != -1)
			{
				mQSIndex = mMyApp.mTradeQSIndex;
			}
			if (mQSIndex >= 0 && mQSIndex < mTradeQSList.size())
			{
				mTV_QS_Name.setText(mTradeQSList.get(mQSIndex).mName);
			}else
			{
				mTV_QS_Name.setText("无");
			}
			
			if (!mMyApp.mTradeQSZHType.isEmpty())
			{
				mQSAccountType = mMyApp.mTradeQSZHType;
			}
			
			String type = STD.GetValue(mQSAccountType, 1, ',');
			if (type.isEmpty()) {
				type = mQSAccountType;
			}

			mTV_account_type.setText(type);
		}
		if (mTradeQSList.size() <= 1)
		{
			mImg_QSSel.setVisibility(View.GONE);
		}else
		{
			mImg_QSSel.setVisibility(View.VISIBLE);
		}
		mViewAccount.setOnClickListener(this);
		mViewQS.setOnClickListener(this);
		
		mMyApp.mTradeQSZHType = mQSAccountType;
	}
	
	private void initQSList()
	{
		mQSAccountType = "";
		mQSIndex = -1;
		String strQSId = "";
		
		if (mMyApp.mTradeQSIndex >= 0)//说明不是第一次程序启动，用户有选择过券商
		{
			mQSIndex = mMyApp.mTradeQSIndex;
			mQSAccountType = mMyApp.mTradeQSZHType;
		}else
		{
			//mSavedQSInfo = PreferenceEngine.getInstance().getTradeQSInfo();
			if (mMyApp.getTradeAccountList().size() > 0)
			{
				TradeAccountInfo info = mMyApp.getTradeAccountList().get(0);
				strQSId = info.mID;
				mQSAccountType = info.mAccoutType;
				mMyApp.mTradeQSZHType = mQSAccountType;
				if (mIsSave)
				{
					mAccount = info.mAccout;
				}else
				{
					mAccount = "";
				}
//				if (mSavedQSInfo.length() > 0)
//				{
//					String tmp = mSavedQSInfo;
//					int index = tmp.indexOf("|");
//					
//					if (index != -1) {
//						strQSId = tmp.substring(0, index);
//						tmp = tmp.substring(index + 1);
//					}
//					index = tmp.indexOf("|");
//					if (index != -1) {
//						mQSAccountType = tmp.substring(0, index);
//					}
//					this.mAccount = tmp.substring(index + 1);
//				}
			}
			
		}
		
		MIniFile iniFile = new MIniFile();
		iniFile.setFilePath(getApplicationContext(), MyApp.TRADE_ADDRPATH);
		mMyApp.resetTradeQSArray();
		mTradeQSList.clear();
		int icount = 0;
		for (int i = 1; i <= 1000; i++)
		{
			String key = String.format("券商%d", i);
			
			String strName = iniFile.ReadString(key, "券商名称", "");
			String strId = iniFile.ReadString(key, "券商id", "");
			String strType = iniFile.ReadString(key, "帐号类型", "");
			String strIP = iniFile.ReadString(key, "ip", "");
			
			if (!strName.isEmpty() && !strId.isEmpty() 
					&& !strType.isEmpty() && !strIP.isEmpty())
			{
				TradeQSInfo qsInfo = new TradeQSInfo();				
				qsInfo.mName = strName;
				qsInfo.mID = strId;
				if (strQSId.equals(strId))
				{
					if (this.mQSIndex == -1)//说明是第一次启动，没有选择过券商，从保存的券商信息里获取
					{
						this.mQSIndex = icount;
					}
				}
				//账号类型以|分割
				String typeTemp = "";
			    for (int j = 0; j < 10; j++)
			    {
			    	typeTemp = STD.GetValue(strType, j + 1, '|');
			        
			        if (typeTemp.isEmpty())
			        {
			            break;
			        }
			        qsInfo.mAccoutType.add(typeTemp);
			    }
				
//				int index = strType.indexOf("|");
//				while (index != -1) {
//					String tmp = strType.substring(0, index);
//
//					qsInfo.mAccoutType.add(tmp);
//					strType = strType.substring(index + 1);
//					index = strType.indexOf("|");
//				}
//				qsInfo.mAccoutType.add(strType);
				
			    String ipTemp = "";
			    for (int k = 0; k < 50; k++)
			    {
			    	ipTemp = STD.GetValue(strIP, k + 1, '|');
			        
			        if (ipTemp.isEmpty())
			        {
			            break;
			        }
			        qsInfo.mIPAdd.add(ipTemp);
			    }
			    
//				index = strIP.indexOf("|");
//				while (index != -1) {
//					String tmp = strIP.substring(0, index);
//
//					qsInfo.mIPAdd.add(tmp);
//					strIP = strIP.substring(index + 1);
//					index = strIP.indexOf("|");
//				}
//				qsInfo.mIPAdd.add(strIP);

				mTradeQSList.add(qsInfo);
				icount++;
			}else
			{
				break;
			}
		}
		
		if (this.mQSIndex == -1)
		{
			this.mQSIndex = 0;
			mMyApp.mTradeQSIndex = 0;
		}
		if (mQSAccountType.equalsIgnoreCase("") && mTradeQSList.size() > 0)
		{
			mQSAccountType = mTradeQSList.get(mQSIndex).mAccoutType.get(0);
		}
		mMyApp.mTradeQSZHType = mQSAccountType;
	}
	
	private void setTradeNetAddress()
	{
		if (mQSIndex >= 0 && mQSIndex < mTradeQSList.size())
		{
			int start = (int) (System.currentTimeMillis() & 0xff);
	
			int tnum = mTradeQSList.get(mQSIndex).mIPAdd.size();
			String tradeAddr[] = new String[tnum];
			for (int i = 0; i < tnum; i++) {
	
				String addr = mTradeQSList.get(mQSIndex).mIPAdd.get(i);
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

			mMyApp.resetreconnectAddrNum_Trade();
			for (int i = 0; i < tnum; i++, start++) {
				start %= tnum;
				mMyApp.addreconnectAddress_Trade(tradeAddr[start]);
			}
		}
	}
	
	//保存券商登录信息，bClear-true:清空保存信息
	private void saveQSLoginInfo(boolean bClear)
	{
//		String qsInfo = "";
//		if (!bClear)
//		{
//			qsInfo = mTradeQSList.get(mQSIndex).mID + "|" + mQSAccountType + "|" + mAccount;
//		}
//		PreferenceEngine.getInstance().saveTradeQSInfo(qsInfo);
		if (!bClear)
		{
			TradeAccountInfo info = new TradeAccountInfo();
			info.mName = mTradeQSList.get(mQSIndex).mName;
			info.mID = mTradeQSList.get(mQSIndex).mID;
			info.mAccout = mAccount;
			info.mAccoutType = mQSAccountType;
			mMyApp.AddtoMyTradeAccount(info, 0);
		}
	}
	
	//根据券商ID读取对应的ini文件来获取要显示的资金属性段
	//如果券商ini不存在，默认读取trade_server.ini
	private void getZJDataList(String qsID)
	{
		String configFile = String.format("qsconfig_%s.ini", qsID);
		FileService file = new FileService(this.getApplicationContext());
		int size = file.getFileSize(configFile);
		boolean bQSConfig = true;//默认券商config文件存在qsconfig_id.ini
		if (size < 0)// 文件不存在
		{
			bQSConfig = false;
		}
		
		MIniFile iniFile = new MIniFile();
		if (bQSConfig)
		{
			iniFile.setFilePath(getApplicationContext(), configFile);
		}else
		{
			iniFile.setFilePath(getApplicationContext(), MyApp.TRADE_CONFIGPATH);
		}
		
		String key = "money";
		String subKey = "icount";
		String strValue = "";
		String strTemp = "";
		int nCount = iniFile.ReadInt(key, subKey, 0);
		mMyApp.mTradeData.m_ZJDataList.clear();
		for(int i = 0; i < nCount; i++)
		{
			subKey = String.format("item%d", i+1);
			strValue = iniFile.ReadString(key, subKey, "");
			
			//持仓市值,96,142
			if(strValue.isEmpty())
			{
				continue;
			}
			strTemp = STD.GetValue(strValue, 1, ',');
			if (strTemp.isEmpty())
			{
				continue;
			}
			TradeZJRecord record = new TradeZJRecord();
			record.mTitle = strTemp;
			
			strTemp = STD.GetValue(strValue, 2, ',');
			if (strTemp.isEmpty())
			{
				mMyApp.mTradeData.m_ZJDataList.add(record);
				continue;
			}
			record.mStepVaules[0] = STD.StringToInt(strTemp);
			
			strTemp = STD.GetValue(strValue, 3, ',');
			if (strTemp.isEmpty())
			{
				mMyApp.mTradeData.m_ZJDataList.add(record);
				continue;
			}
			record.mStepVaules[1] = STD.StringToInt(strTemp);
			mMyApp.mTradeData.m_ZJDataList.add(record);
		}
		
		if(mMyApp.mTradeData.m_ZJDataList.size() <= 0)
		{
			String title[] = {"浮动盈亏", "总资产", "持仓市值", "风险度", "可用资金", "可取资金"};
			int filed0[] = {141,97,96,345,93,95};
			int filed1[] = {-1,-1,142,107,-1,-1};
			for(int i = 0; i < 6; i++)
			{
				TradeZJRecord record = new TradeZJRecord();
				record.mTitle = title[i];
				record.mStepVaules[0] = filed0[i];
				record.mStepVaules[1] = filed1[i];
				mMyApp.mTradeData.m_ZJDataList.add(record);
			}
		}
		
	}

	TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable arg0) {
			String temp = arg0.toString();
			if (temp.isEmpty())
			{
				mBtnDelete.setVisibility(View.GONE);
			}else
			{
				mBtnDelete.setVisibility(View.VISIBLE);
			}
			
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2,
				int arg3) {
		}
		
	};
	
	private void getHistoryAccount()
	{
		mAccountList.clear();
		mAccountList.addAll(mMyApp.getTradeAccountList());
	}
	
	/**
	 * 历史账号选择的回掉函数
	 */
	Dialogcallback dlgAccountCallBack = new Dialogcallback() {
		@Override
		public void dialogdo(int index) {
			if(index >= 0 && index < mAccountList.size())
			{
				TradeAccountInfo info = mAccountList.get(index);
				String strQSId = info.mID;
				mQSAccountType = info.mAccoutType;
				mAccount = info.mAccout;
				for (int i = 0; i < mTradeQSList.size(); i++)
				{
					if(strQSId.equals(mTradeQSList.get(i).mID))
					{
						mQSIndex = i;
						break;
					}
				}
				
				if (mTradeQSList.isEmpty())
				{
					mTV_QS_Name.setText("无");
					mTV_account_type.setText("");
					mQSIndex = -1;
					mQSAccountType = "";
					mAccount = "";
				}else
				{
					mTV_QS_Name.setText(mTradeQSList.get(mQSIndex).mName);
					String type = STD.GetValue(mQSAccountType, 1, ',');
					if (type.isEmpty()) {
						type = mQSAccountType;
					}
					mTV_account_type.setText(type);
					mEdit_account.setText(mAccount);
				}
				mMyApp.mTradeQSZHType = mQSAccountType;
			}
			
		}
	};
	
	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_trade_accout_more: //弹出历史账号选择
		{
			getHistoryAccount();
			if (mDlg_AccountAdapter == null)
			{
				mDlg_AccountAdapter = new AccountDialogAdapter(this.getParent(), mAccountList);
			}else
			{
				mDlg_AccountAdapter.notifyDataSetChanged();
			}
			if (mDlg_Account == null)
			{
				mDlg_Account  = new MyListDialog(this.getParent()).builder();
				mDlg_Account.setTitle("历史账号选择或编辑");
				mDlg_Account.setContent(mDlg_AccountAdapter);
				mDlg_Account.setDialogCallback(dlgAccountCallBack);
				mDlg_Account.setCancelable(true);
				mDlg_Account.setCanceledOnTouchOutside(true);
			}
			mDlg_Account.show();
		}
		break;
		case R.id.btn_login_trade_activity: //login
		{
			if (mQSIndex < 0 && mQSIndex < mTradeQSList.size())
			{
				new com.pengbo.mhdcx.widget.AlertDialog(
						mMyApp.mTradeLoginActivity
								.getParent()).builder().setTitle("提示")
						.setMsg("券商不存在！")
						.setCancelable(false)
						.setCanceledOnTouchOutside(false)
						.setPositiveButton("确认", new OnClickListener() {
							@Override
							public void onClick(View v) {
							}
						}).show();
				return;
			}
			
			String phoneCode = et_phoneCode.getText().toString();
			realCode = Code.getInstance().getCode();
			if(!phoneCode.equalsIgnoreCase(realCode)){
				new com.pengbo.mhdcx.widget.AlertDialog(
						mMyApp.mTradeLoginActivity
								.getParent()).builder().setTitle("提示")
						.setMsg("验证码不正确！")
						.setCancelable(false)
						.setCanceledOnTouchOutside(false)
						.setPositiveButton("确认", new OnClickListener() {
							@Override
							public void onClick(View v) {
								et_phoneCode.setText("");
							}
						}).show();
				return;
			}
			
			if (mMyApp.mTradeData.mHQType != mMyApp.getCurrentHQType())
			{
				mMyApp.mTradeData.quitTrade();
			}
			
			showProgress();
			setTradeNetAddress();
			mAccount = mEdit_account.getText().toString();
			mMyApp.mTradeData.mTradeAccount = mAccount;
			mMyApp.mTradeData.mTradePassword = mPassword;
			mMyApp.mTradeData.mHQType = mMyApp.getCurrentHQType();
			mIdPassword = mEdit_identify_password.getText().toString();
			mMyApp.mTradeNet.setMainHandler(mHandler);
			mMyApp.mTradeData.mTradeLockTimeout = PreferenceEngine.getInstance().getTradeOnlineTime();
			mMyApp.mTradeNet.initSocketThread();
		}
			break;
			
		case R.id.trade_activity_linearlayout1: //券商选择
		{
			if (mTradeQSList.size() <= 1)
			{
				return;
			}
			Intent intent = new Intent();
			intent.putExtra(INTENT_QS_INDEX, mQSIndex);
			intent.setClass(
					TradeLoginActivity.this, TradeQSListActivity.class);
			startActivityForResult(intent, REQUEST_CODE);
		
		}
			break;
			
			
		case R.id.trade_activity_linearlayout2:
		{
			if (mQSIndex < 0)
			{
				return;
			}
			Intent intent = new Intent();
			intent.putExtra(INTENT_QS_INDEX, mQSIndex);
			
			intent.setClass(
					TradeLoginActivity.this, TradeAccountTypeActivity.class);
			startActivityForResult(intent, REQUEST_CODE);
		
		}
			break;
			
		case R.id.trade_activity_image_trade_account :
			if(!mIsSave){
				if (AppConstants.IS_NEED_WARNING_SAVE_ACCOUNT)
				{
					new AlertDialog(this.getParent()).builder().setTitle("提示")

					.setMsg("保存账号可能会带来一定的安全风险，是否确定保存？").setCancelable(false).setCanceledOnTouchOutside(false).setPositiveButton("确认", new OnClickListener() {

						@Override
						public void onClick(View v) {
							mImageAccountNum.setBackgroundResource(R.drawable.save02);
							mIsSave = true;
							PreferenceEngine.getInstance().saveIsTradeAccountSaved(mIsSave);
						}
					}).setNegativeButton("取消", new OnClickListener() {

						@Override
						public void onClick(View v) {
							
						}
					}).show();
					return;
				}else
				{
					mImageAccountNum.setBackgroundResource(R.drawable.save02);
					mIsSave = true;
				}
			}else{
				mImageAccountNum.setBackgroundResource(R.drawable.save01);
				mIsSave = false;				
			}
			PreferenceEngine.getInstance().saveIsTradeAccountSaved(mIsSave);
		
			break;
			
		case R.id.trade_activity_image_trade_password: //clear password
		{
			mEdit_password.setText("");
			mPassword = "";
		}
			break;
		case R.id.iv_showCode:
		{
			iv_showCode.setImageBitmap(Code.getInstance().createBitmap());
		}
			break;	
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE:
			if (resultCode == TradeAccountTypeActivity.RESULT_CODE) 
			{

			}
			break;

		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		mMyApp.mTradeNet.setMainHandler(null);
		closeProgress();
		super.onPause();
	}
	
	private OnClickListener itemsOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.btn_digit_0:
			case R.id.btn_digit_1:
			case R.id.btn_digit_2:
			case R.id.btn_digit_3:
			case R.id.btn_digit_4:
			case R.id.btn_digit_5:
			case R.id.btn_digit_6:
			case R.id.btn_digit_7:
			case R.id.btn_digit_8:
			case R.id.btn_digit_9: {
				String input = ((Button) v).getText().toString();
				if (mEdit_password.getText().length() == 0) {
					mPassword = input;
					mEdit_password.setText("*");
				} else if (input != null) {
					String strTmp = mEdit_password.getText().toString();
					strTmp += "*";
					mPassword += input;
					mEdit_password.setText(strTmp);
				}
			}
				break;
			case R.id.btn_digit_wancheng: {
				mKeyboard.dismiss();
			}
				break;

			case R.id.btn_digit_delete: {
				if (mEdit_password.getText().length() > 0) {
					String strTmp = mEdit_password.getText().toString();
					strTmp = strTmp.substring(0, strTmp.length() - 1);
					mPassword = mPassword.substring(0, mPassword.length() - 1);
					mEdit_password.setText(strTmp);
				}
			}
				break;
			default:
				break;
			}
		}

	};
	
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
		.setMsg("网络连接断开，请重新登录！")
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

				if(mKeyboard != null)
				{
					mKeyboard.dismiss();
				}
			}
		}).show();
    }
	
	protected void showProgress() {
		closeProgress();

		if (mProgress == null) {
			mProgress = new Dialog(this.getParent(), R.style.AlertDialogStyle);
			mProgress.setContentView(R.layout.list_loading);
			TextView tv = (TextView) mProgress.findViewById(R.id.loading_text);
			tv.setText("登录中，请稍后......");
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
}
