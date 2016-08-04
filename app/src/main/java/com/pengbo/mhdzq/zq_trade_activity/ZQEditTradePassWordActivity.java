package com.pengbo.mhdzq.zq_trade_activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.fragment.ZhengQuanFragment;
import com.pengbo.mhdzq.net.CMessageObject;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.MIniFile;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.trade.data.TradeNetConnect;
import com.pengbo.mhdzq.trade.data.Trade_Define;
import com.pengbo.mhdzq.zq_activity.HdActivity;
import com.pengbo.mhdzq.zq_activity.ZhengQuanActivity;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 证券中更多里面的 修改交易密码 
 * 
 * @author pobo
 * @date   2015-11-27 上午9:56:20
 * @className ZQEditTradePassWordActivity.java
 * @verson 1.0.0
 */
public class ZQEditTradePassWordActivity extends HdActivity implements OnClickListener{

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
	public TextView mMiddle;//抬头中的字体 
	private ImageView mBack;//返回按钮
	private Button mBtnSave;//交易页面的确定按钮
	private TextView mTV_pwtype; //密码类型    可以选择  默认是客户号 
	
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
	public Dialog mProgress;
	private Context mContext;
	
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
						Toast.makeText(ZQEditTradePassWordActivity.this, "登录失败", 5000)
								.show();
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
						new com.pengbo.mhdzq.widget.AlertDialog(mContext).builder().setTitle(mContext.getString(R.string.IDS_Warning))
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
					
					new com.pengbo.mhdzq.widget.AlertDialog(mContext).builder().setTitle(mContext.getString(R.string.IDS_TiShi))
					.setMsg(mContext.getString(R.string.IDS_XiuGaiChengGone))
					.setCancelable(false)
					.setCanceledOnTouchOutside(false)
					.setPositiveButton(mContext.getString(R.string.IDS_QueDing), new OnClickListener() {
						@Override
						public void onClick(View v) {
							//修改成功跳转到登陆页面 
							processNetLose();
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
		setContentView(R.layout.zq_trade_edit_password);
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
	
	@Override
	protected void onPause() {
		mMyApp.mTradeNet.setMainHandler(null);
		closeProgress();
		super.onPause();
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
		
		mMiddle = (TextView) this.findViewById(R.id.tv_public_black_head_title_middle_name);
		mBack = (ImageView) this.findViewById(R.id.img_public_black_head_title_left_blue_back);
		
		mMiddle.setVisibility(View.VISIBLE);
		mBack.setVisibility(View.VISIBLE);		
		mMiddle.setText("修改密码");
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
	
	protected void closeProgress() {

		if (mProgress != null && mProgress.isShowing()) {
			mProgress.cancel();
			mProgress.dismiss();
			mProgress = null;
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
			if (resultCode == ZQTradeMoreChooseBankActivity.REQUEST_CODE_MMLX) {
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
			intent.setClass(this, ZQTradeMoreChooseBankActivity.class);

			intent.putExtra("mmlx", mStrPWTypes);
			intent.putExtra("pwtype", mSelectPWTypeIndex);

			startActivityForResult(intent, REQUEST_CODE_MMLX);

			break;

		case R.id.img_public_black_head_title_left_blue_back:
			this.finish();
			break;
		default:
			break;
		}
	}
	
	private void proc_MSG_LOCK(Message msg)
    {    	
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
	
	private void processNetLose() {
		mMyApp.setHQPushNetHandler(null);
		GlobalNetProgress.HQRequest_MultiCodeInfoPush(mMyApp.mHQPushNet, null, 0, 0);
		mMyApp.setTradeHandler(null);
		mMyApp.mTradeNet.closeConnect();

		mMyApp.mTradeData.clearStepData();
		mMyApp.mTradeData.mTradeLoginFlag = false;

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
}
