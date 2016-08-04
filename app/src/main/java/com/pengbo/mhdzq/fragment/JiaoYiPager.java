package com.pengbo.mhdzq.fragment;

import java.util.ArrayList;

import com.pengbo.mhdcx.ui.activity.SetOnLineTimeActivity;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.base.BasePager;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.net.CMessageObject;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.tools.DataTools;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.MIniFile;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.trade.data.TradeAccountInfo;
import com.pengbo.mhdzq.trade.data.TradeNetConnect;
import com.pengbo.mhdzq.trade.data.TradeQSInfo;
import com.pengbo.mhdzq.trade.data.Trade_Define;
import com.pengbo.mhdzq.view.MyDigitKeyboardWindow;
import com.pengbo.mhdzq.widget.AlertDialog;
import com.pengbo.mhdzq.zq_activity.ZhengQuanActivity;
import com.pengbo.mhdzq.zq_trade_activity.ZQTradeAccountTypeActivity;
import com.pengbo.mhdzq.zq_trade_activity.ZQTradeQSListActivity;
import com.pengbo.mhdzq.zq_trade_activity.ZQTradeTimeOutSetActivity;
import com.pengbo.mhdzq.tools.Code;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 证券-交易页面
 * 
 * @author pobo
 */
public class JiaoYiPager extends BasePager implements OnActivityResultListener,
		OnClickListener {

	private final static String TAG = "JiaoYiPager";
	public static final String INTENT_QS_INDEX = "qs_index";

	public static final int REQUEST_CODE_QS = 0;
	public static final int REQUEST_CODE = 1;

	private View view;
	// Content View Elements
	/** 账号类型 **/
	private TextView tv_zhanghaoleixing;
	/** 账号 **/
	private EditText editAccount;
	/** 登录密码 **/
	private EditText editPwd;
	/** 通讯密码 **/
	private EditText editCommPwd;
	/** 验证码 **/
	private EditText editYZM;
	/** 保存账号 **/
	private CheckBox cb_save_zhanghao;
	/** 保存通讯密码 **/
	private CheckBox cb_save_comm_psw;
	/** 登录 **/
	private Button btn_timeout_set;
	/** 登录 **/
	private Button btn_login;
	/** 开户 **/
	private Button btn_register;
	/** 清空密码按钮 **/
	private ImageButton mBtnDelete;
	/** 选择账号类型 **/
	private RelativeLayout rl_xuanzheleixing;
	/** 验证码 **/
	private ImageView iv_giveCode;

	private MyDigitKeyboardWindow mKeyboard;
	// End Of Content View Elements
	/** 交易站点列表 **/
	private ArrayList<TradeQSInfo> mTradeQSList;
	/** 交易站点选择索引 **/
	private int mQSIndex;
	/** 交易账号类别 **/
	private String mQSAccountType;
	/** 交易账号 **/
	private String mAccount;
	/** 交易密码 **/
	private String mPassword;
	/** 通讯密码 **/
	private String mCommPwd;
	/** 是否保存交易账号 **/
	private boolean mIsSaveAccount = false;
	/** 产生的验证码 **/
	private String realCode;

	/** 消息 处理 **/
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

					String sjhm = PreferenceEngine.getInstance()
							.getPhoneForVerify();
					mMyApp.mTradeNet.Request_Login(
							mMyApp.mTradeData.mTradeAccount,
							mMyApp.mTradeData.mTradePassword, 0, 0,
							AppConstants.APP_VERSION_INFO, type, sjhm, 0);
				} else if (nFrameType == Trade_Define.Func_Login) {

					if (aMsgObject.nErrorCode < 0) {
						closeProgress();
						// 登陆出错，弹出messagebox提醒用户
						new AlertDialog(mActivity).builder().setTitle("登录")
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
						if (mIsSaveAccount) {
							// 交易成功，保存按钮选择，需要保存当前登录的券商信息
							// 券商id|账号类型|登录账号
							saveQSLoginInfo(false);
							mMyApp.mTradeQSIndex_ZQ = -1;
							mMyApp.mTradeQSZHType_ZQ = "";
						} else {
							saveQSLoginInfo(true);
						}

						mMyApp.mTradeData.mTradeLoginFlag = true;
					}
				} else if (nFrameType == Trade_Define.Func_GDZH) {
					if (aMsgObject.nErrorCode < 0) {
						closeProgress();
						mMyApp.mTradeData.mTradeLoginFlag = false;
						// 股东账号查询出错，弹出messagebox提醒用户
						new AlertDialog(mActivity).builder().setTitle("提示")
								.setMsg(aMsgObject.errorMsg)
								.setCancelable(false)
								.setCanceledOnTouchOutside(false)
								.setPositiveButton("确认", new OnClickListener() {
									@Override
									public void onClick(View v) {
									}
								}).show();
					} else {
						gotoChiCangView();
						closeProgress();
					}
				}
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

	public JiaoYiPager(Activity activity) {
		super(activity);
	}

	@Override
	public void initDetailView() {

		tvTitle.setText("证券-交易登录");
		tvTitle.setVisibility(View.VISIBLE);

		view = View.inflate(mActivity, R.layout.zq_trade_login, null);

		mTradeQSList = mMyApp.getTradeQSArray_ZQ();
		mAccount = "";
		mPassword = "";
		mIsSaveAccount = PreferenceEngine.getInstance()
				.getIsTradeAccountSaved_ZQ();

		initQSList();

		bindViews();
		initPagerView();

		flContent.addView(view);
		bPagerReady = true;
	}

	private void initPagerView() {
		initTradeAccount();
		initTradePassword();
		initQSInfoView();
		initTradeKeepLiveTime();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.trade_activity_linearlayout0: {
			if (mTradeQSList.size() <= 1) {
				return;
			}
			Intent intent = new Intent();
			intent.putExtra(INTENT_QS_INDEX, mQSIndex);
			intent.setClass(mActivity, ZQTradeQSListActivity.class);
			// saveZhangDian();
			mActivity.startActivityForResult(intent, REQUEST_CODE_QS);
		}
			break;
		case R.id.trade_activity_linearlayout1: {
			if (mQSIndex < 0) {
				return;
			}
			Intent intent = new Intent();
			intent.putExtra(INTENT_QS_INDEX, mQSIndex);

			intent.setClass(mActivity, ZQTradeAccountTypeActivity.class);
			// saveZhangDian();
			mActivity.startActivityForResult(intent, REQUEST_CODE);
		}
			break;

		case R.id.btn_register_trade:// 注册
		{
			try {
				if (DataTools.isAPKAvalible(mActivity,
						AppConstants.THIRD_APP_PACKAGE)) {
					mMyApp.doStartApplicationWithPackageName(mActivity,
							AppConstants.THIRD_APP_PACKAGE);
				} else {
					new AlertDialog(mActivity).builder().setTitle("提示")
							.setMsg("开户APP未安装，是否前往下载！").setCancelable(false)
							.setCanceledOnTouchOutside(false)
							.setNegativeButton("取消", new OnClickListener() {
								@Override
								public void onClick(View v) {
								}
							}).setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent intent = new Intent();
									intent.setAction("android.intent.action.VIEW");
									Uri content_uri_browsers = Uri
											.parse(AppConstants.THIRD_APP_DOWNLOAD_PATH);
									intent.setData(content_uri_browsers);

									try {
										mActivity.startActivity(intent);
									} catch (ActivityNotFoundException e) {
										e.printStackTrace();
									}
								}
							}).show();

				}
			} catch (Exception e) {
				L.e(TAG, "exception" + e);
			}
		}
			break;

		case R.id.btn_login_trade: // login
		{
			if (mQSIndex < 0 && mQSIndex < mTradeQSList.size()) {
				new AlertDialog(mActivity).builder().setTitle("提示")
						.setMsg("站点不存在！").setCancelable(false)
						.setCanceledOnTouchOutside(false)
						.setPositiveButton("确认", new OnClickListener() {
							@Override
							public void onClick(View v) {
							}
						}).show();
				return;
			}

			String phoneCode = editYZM.getText().toString();
			realCode = Code.getInstance().getCode();
			if (!phoneCode.equalsIgnoreCase(realCode)) {
				new AlertDialog(mActivity).builder().setTitle("提示")
						.setMsg("验证码不正确！").setCancelable(false)
						.setCanceledOnTouchOutside(false)
						.setPositiveButton("确认", new OnClickListener() {
							@Override
							public void onClick(View v) {
								editYZM.setText("");
							}
						}).show();
				return;
			}

			if (mMyApp.mTradeData.mHQType != mMyApp.getCurrentHQType()) {
				mMyApp.mTradeData.quitTrade();
			}

			showProgress("登录中，请稍后...");
			setTradeNetAddress();
			mAccount = editAccount.getText().toString();
			mMyApp.mTradeData.mTradeAccount = mAccount;
			mMyApp.mTradeData.mTradePassword = mPassword;
			mMyApp.mTradeData.mHQType = mMyApp.getCurrentHQType();
			mMyApp.mTradeNet.setMainHandler(mHandler);
			mMyApp.mTradeData.mTradeLockTimeout = PreferenceEngine
					.getInstance().getTradeOnlineTime();
			mMyApp.mTradeNet.initSocketThread();

			mMyApp.mTradeAccount = mMyApp.mTradeData.mTradeAccount;
			mMyApp.mTradePassword = mMyApp.mTradeData.mTradePassword;

			mMyApp.mTradeVersion = mMyApp.mTradeData.mTradeVersion;
			mMyApp.mTradeLockTimeout = mMyApp.mTradeData.mTradeLockTimeout;
			mMyApp.mHQType = mMyApp.mTradeData.mHQType;
		}
			break;

		case R.id.zq_imgbtn_trade_empty_pwd: // clear password
		{
			editPwd.setText("");
			mPassword = "";
		}
			break;
		case R.id.iv_showCode: {
			iv_giveCode.setImageBitmap(Code.getInstance().createBitmap());
		}
			break;
		case R.id.textViewBaochitime: {
			Intent intent = new Intent();

			intent.setClass(mActivity, SetOnLineTimeActivity.class);
			// saveZhangDian();
			mActivity.startActivity(intent);
		}
			break;
		default:
			break;
		}
	}

	/**
	 * 用于站点换后的账号与密码清除。
	 */
	public void saveZhangDian() {
		mZDbefore = mMyApp.mTradeQSIndex_ZQ;
		mTypeBefore = mMyApp.mTradeQSZHType_ZQ;
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		switch (requestCode) {
		case REQUEST_CODE_QS:
			// if (resultCode == ZQTradeQSListActivity.RESULT_CODE)
			// {
			// initQSInfoView();
			// }
			// break;
		case REQUEST_CODE:
			// if (resultCode == ZQTradeAccountTypeActivity.RESULT_CODE)
			// {
			initQSList();
			initQSInfoView();
			// }
			break;

		default:
			break;
		}
		return false;
	}

	private void gotoChiCangView() {
		ZhengQuanFragment frag = (ZhengQuanFragment) ((FragmentActivity) mActivity)
				.getSupportFragmentManager().findFragmentByTag(
						ZhengQuanActivity.TAG);
		if (frag != null) {
			frag.updateViewPagerItem(new JiaoYiChiCangPager(mActivity), 2);
		}
	}

	// 保存券商登录信息，bClear-true:清空保存信息
	private void saveQSLoginInfo(boolean bClear) {
		if (!bClear) {
			TradeAccountInfo info = new TradeAccountInfo();
			info.mName = mTradeQSList.get(mQSIndex).mName;
			info.mID = mTradeQSList.get(mQSIndex).mID;
			info.mAccout = mAccount;
			info.mAccoutType = mQSAccountType;
			mMyApp.AddtoMyTradeAccount_ZQ(info, 0);
		} else {
			mMyApp.ClearMyTradeAccount_ZQ();
		}
	}

	private void bindViews() {

		tv_zhengquangongsi = (TextView) view
				.findViewById(R.id.zq_trade_login_zqgs);
		rl_xuanzhegongsi = (RelativeLayout) view
				.findViewById(R.id.trade_activity_linearlayout0);
		btn_xuanzhegongsi = (ImageView) view
				.findViewById(R.id.zq_trade_login_xuanzhegongsi);
		rl_xuanzhegongsi.setOnClickListener(this);

		tv_zhanghaoleixing = (TextView) view
				.findViewById(R.id.zq_trade_login_leixin);
		rl_xuanzheleixing = (RelativeLayout) view
				.findViewById(R.id.trade_activity_linearlayout1);
		rl_xuanzheleixing.setOnClickListener(this);

		mBtnDelete = (ImageButton) view
				.findViewById(R.id.zq_imgbtn_trade_empty_pwd);
		mBtnDelete.setOnClickListener(this);
		mBtnDelete.setVisibility(View.GONE);

		editAccount = (EditText) view.findViewById(R.id.zq_input_zhanghao);
		editPwd = (EditText) view.findViewById(R.id.zq_trade_login_psw);
		editCommPwd = (EditText) view
				.findViewById(R.id.zq_trade_login_comm_psw);
		editYZM = (EditText) view.findViewById(R.id.zq_et_EnsureCodes);

		rl_tongxunmima = (RelativeLayout) view
				.findViewById(R.id.trade_activity_linearlayout4);
		rl_tongxunmima.setVisibility(View.GONE);

		iv_giveCode = (ImageView) view.findViewById(R.id.iv_showCode);
		// 将验证码用图片的形式显示出来
		iv_giveCode.setImageBitmap(Code.getInstance().createBitmap());
		realCode = Code.getInstance().getCode();
		iv_giveCode.setOnClickListener(this);

		cb_save_zhanghao = (CheckBox) view.findViewById(R.id.cb_save_zhanghao);
		cb_save_zhanghao.setOnCheckedChangeListener(CheckBoxListener);

		cb_save_comm_psw = (CheckBox) view.findViewById(R.id.cb_save_comm_psw);
		cb_save_comm_psw.setOnCheckedChangeListener(CheckBoxListener);

		btn_timeout_set = (Button) view.findViewById(R.id.textViewBaochitime);
		btn_timeout_set.setOnClickListener(this);

		btn_login = (Button) view.findViewById(R.id.btn_login_trade);
		btn_login.setOnClickListener(this);

		btn_register = (Button) view.findViewById(R.id.btn_register_trade);
		btn_register.setOnClickListener(this);

	}

	CompoundButton.OnCheckedChangeListener CheckBoxListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.cb_save_zhanghao: {

				if (isChecked) {
					if (AppConstants.IS_NEED_WARNING_SAVE_ACCOUNT) {
						new AlertDialog(mActivity)
								.builder()
								.setTitle("提示")

								.setMsg("保存账号可能会带来一定的安全风险，是否确定保存？")
								.setCancelable(false)
								.setCanceledOnTouchOutside(false)
								.setPositiveButton("确认", new OnClickListener() {

									@Override
									public void onClick(View v) {
										// mImageAccountNum.setBackgroundResource(R.drawable.save02);
										mIsSaveAccount = true;
										PreferenceEngine.getInstance()
												.saveIsTradeAccountSaved_ZQ(
														mIsSaveAccount);
									}
								})
								.setNegativeButton("取消", new OnClickListener() {

									@Override
									public void onClick(View v) {
										mIsSaveAccount = false;
										cb_save_zhanghao.setChecked(false);
									}
								}).show();
						return;
					} else {
						mIsSaveAccount = true;
					}
				} else {
					mIsSaveAccount = false;
				}
				PreferenceEngine.getInstance().saveIsTradeAccountSaved_ZQ(
						mIsSaveAccount);

			}
				break;
			case R.id.cb_save_comm_psw: {
				if (isChecked) {

				} else {

				}
			}
				break;
			}

		}
	};

	TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable arg0) {
			String temp = arg0.toString();
			if (temp.isEmpty()) {
				mBtnDelete.setVisibility(View.GONE);
			} else {
				mBtnDelete.setVisibility(View.VISIBLE);
			}

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {

		}

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
		}

	};

	/**
	 * 初始化交易密码
	 */
	private void initTradePassword() {
		editPwd.addTextChangedListener(mTextWatcher);
		editPwd.setInputType(InputType.TYPE_NULL);
		editPwd.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_UP) {

					editPwd.setInputType(InputType.TYPE_NULL);
					hideSoftInputMethod(editPwd);
					if (mKeyboard == null) {
						mKeyboard = new MyDigitKeyboardWindow(mActivity,
								itemsOnClick, editPwd);
						mKeyboard.setOutsideTouchable(true);
						mKeyboard.setFocusable(false);
						mKeyboard.showAtLocation(
								view.findViewById(R.id.zq_trade_login),
								Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
								0);
					} else {
						mKeyboard.ResetKeyboard(editPwd);
						mKeyboard.setOutsideTouchable(true);
						mKeyboard.setFocusable(false);
						mKeyboard.showAtLocation(
								view.findViewById(R.id.zq_trade_login),
								Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
								0);

					}

				}

				return false;
			}
		});
	}

	/**
	 * 初始化交易帐号
	 */
	private void initTradeAccount() {

		editAccount.setText(mAccount);
		editAccount.setInputType(EditorInfo.TYPE_CLASS_PHONE);

		if (mIsSaveAccount) {
			cb_save_zhanghao.setChecked(true);
		} else {
			cb_save_zhanghao.setChecked(false);
		}
	}

	private void initQSInfoView() {
		if (mTradeQSList.isEmpty()) {
			tv_zhanghaoleixing.setText("");
			tv_zhengquangongsi.setText("无");
			mQSIndex = -1;
			mQSAccountType = "";
			mAccount = "";
			btn_xuanzhegongsi.setVisibility(View.GONE);
		} else {
			if (mMyApp.mTradeQSIndex_ZQ != -1) {
				mQSIndex = mMyApp.mTradeQSIndex_ZQ;
			}

			if (mQSIndex >= 0 && mQSIndex < mTradeQSList.size()) {
				tv_zhengquangongsi.setText(mTradeQSList.get(mQSIndex).mName);
			} else {
				tv_zhengquangongsi.setText("无");
			}

			if (mTradeQSList.size() <= 1) {
				btn_xuanzhegongsi.setVisibility(View.GONE);
			} else {
				btn_xuanzhegongsi.setVisibility(View.VISIBLE);
			}

			if (!mMyApp.mTradeQSZHType_ZQ.isEmpty()) {
				mQSAccountType = mMyApp.mTradeQSZHType_ZQ;
			}
			String type = STD.GetValue(mQSAccountType, 1, ',');
			if (type.isEmpty()) {
				type = mQSAccountType;
			}
			tv_zhanghaoleixing.setText(type);
		}
		mMyApp.mTradeQSZHType_ZQ = mQSAccountType;
	}

	private void initQSList() {
		mQSAccountType = "";
		mQSIndex = -1;
		String strQSId = "";
		// 说明不是第一次程序启动，用户有选择过券商
		if (mMyApp.mTradeQSIndex_ZQ >= 0) {
			mQSIndex = mMyApp.mTradeQSIndex_ZQ;
			mQSAccountType = mMyApp.mTradeQSZHType_ZQ;
		} else {
			if (mMyApp.getTradeAccountList_ZQ().size() > 0) {
				TradeAccountInfo info = mMyApp.getTradeAccountList_ZQ().get(0);
				strQSId = info.mID;
				mQSAccountType = info.mAccoutType;
				mMyApp.mTradeQSZHType_ZQ = mQSAccountType;
				if (mIsSaveAccount) {
					mAccount = info.mAccout;
				} else {
					mAccount = "";
				}
			}
		}

		MIniFile iniFile = new MIniFile();
		iniFile.setFilePath(mActivity.getApplicationContext(),
				MyApp.TRADE_ADDRPATH);
		mMyApp.resetTradeQSArray_ZQ();
		mTradeQSList.clear();
		int icount = 0;
		for (int i = 1; i <= 1000; i++) {
			String key = String.format("证券%d", i);

			String strName = iniFile.ReadString(key, "证券名称", "");
			String strId = iniFile.ReadString(key, "证券id", "");
			String strType = iniFile.ReadString(key, "帐号类型", "");
			String strIP = iniFile.ReadString(key, "ip", "");

			if (!strName.isEmpty() && !strId.isEmpty() && !strType.isEmpty()
					&& !strIP.isEmpty()) {
				TradeQSInfo qsInfo = new TradeQSInfo();
				qsInfo.mName = strName;
				qsInfo.mID = strId;
				if (strQSId.equals(strId)) {
					if (this.mQSIndex == -1)// 说明是第一次启动，没有选择过券商，从保存的券商信息里获取
					{
						this.mQSIndex = icount;
					}
				}
				// 账号类型以|分割
				String typeTemp = "";
				for (int j = 0; j < 10; j++) {
					typeTemp = STD.GetValue(strType, j + 1, '|');

					if (typeTemp.isEmpty()) {
						break;
					}
					qsInfo.mAccoutType.add(typeTemp);
				}

				String ipTemp = "";
				for (int k = 0; k < 50; k++) {
					ipTemp = STD.GetValue(strIP, k + 1, '|');

					if (ipTemp.isEmpty()) {
						break;
					}
					qsInfo.mIPAdd.add(ipTemp);
				}

				mTradeQSList.add(qsInfo);
				icount++;
			} else {
				break;
			}
		}

		if (this.mQSIndex == -1) {
			this.mQSIndex = 0;
		}
		if (mQSAccountType.equalsIgnoreCase("") && mTradeQSList.size() > 0) {
			mQSAccountType = mTradeQSList.get(mQSIndex).mAccoutType.get(0);
		}
		mMyApp.mTradeQSZHType_ZQ = mQSAccountType;
	}

	private void initTradeKeepLiveTime() {
		int time = PreferenceEngine.getInstance().getTradeOnlineTime();
		String times = time + "" + "分钟";
		btn_timeout_set.setText(times);
	}

	private void setTradeNetAddress() {
		if (mQSIndex >= 0 && mQSIndex < mTradeQSList.size()) {
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
				if (editPwd.getText().length() == 0) {
					mPassword = input;
					editPwd.setText("*");
				} else if (input != null) {
					String strTmp = editPwd.getText().toString();
					strTmp += "*";
					mPassword += input;
					editPwd.setText(strTmp);
				}
			}
				break;
			case R.id.btn_digit_wancheng: {
				mKeyboard.dismiss();
			}
				break;

			case R.id.btn_digit_delete: {
				if (editPwd.getText().length() > 0) {
					String strTmp = editPwd.getText().toString();
					strTmp = strTmp.substring(0, strTmp.length() - 1);
					mPassword = mPassword.substring(0, mPassword.length() - 1);
					editPwd.setText(strTmp);
				}
			}
				break;
			default:
				break;
			}
		}

	};
	private TextView tv_zhengquangongsi;
	private RelativeLayout rl_xuanzhegongsi;
	private ImageView btn_xuanzhegongsi;
	private int mZDbefore;
	private String mTypeBefore;
	private RelativeLayout rl_tongxunmima;

	private void proc_MSG_TIMEOUT(Message msg) {
		L.d(TAG, "proc_MSG_TIMEOUT");
		closeProgress();
		new AlertDialog(mActivity).builder().setTitle("提示").setMsg("网络请求超时！")
				.setCancelable(false).setCanceledOnTouchOutside(false)
				.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				}).show();
	}

	private void proc_MSG_DISCONNECT(Message msg) {
		L.d(TAG, "proc_MSG_DISCONNECT");
		closeProgress();
		new AlertDialog(mActivity).builder().setTitle("提示")
				.setMsg("网络连接断开，请重新登录！").setCancelable(false)
				.setCanceledOnTouchOutside(false)
				.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
						mMyApp.setHQPushNetHandler(null);
						GlobalNetProgress.HQRequest_MultiCodeInfoPush(
								mMyApp.mHQPushNet, null, 0, 0);
						mMyApp.mTradeNet.setMainHandler(null);
						mMyApp.mTradeData.quitTrade();

					}
				}).show();
	}

	@Override
	public void visibleOnScreen() {
		if (bPagerReady) {
			initQSList();
			initQSInfoView();
			initTradeKeepLiveTime();

			// 在saveZhangDian被放开后这里解开注释。
			// editAccount.setText("");
			// mIsSaveAccount = false;
			// cb_save_zhanghao.setChecked(false);
			//
			// editPwd.setText("");
			// mPassword = "";
			// saveZhangDian();
		}
	}

	@Override
	public void invisibleOnScreen() {
		ZhengQuanFragment frag = (ZhengQuanFragment) ((FragmentActivity) mActivity)
				.getSupportFragmentManager().findFragmentByTag(
						ZhengQuanActivity.TAG);
		if (frag != null) {
			frag.clearMarketAndCode();
		}
		if (bPagerReady && editPwd != null) {
			hideSoftInputMethod(editPwd);
		}
	}

}
