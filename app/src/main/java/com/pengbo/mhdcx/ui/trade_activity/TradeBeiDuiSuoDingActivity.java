package com.pengbo.mhdcx.ui.trade_activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.adapter.TradeBDListViewAdapter;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.HQ_Define;
import com.pengbo.mhdzq.data.PublicData.Stock;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.net.CMessageObject;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.trade.data.PBSTEP;
import com.pengbo.mhdzq.trade.data.PTK_Define;
import com.pengbo.mhdzq.trade.data.STEP_Define;
import com.pengbo.mhdzq.trade.data.TradeNetConnect;
import com.pengbo.mhdzq.trade.data.Trade_Define;
import com.pengbo.mhdzq.zq_activity.HdActivity;
import com.pengbo.mhdcx.ui.main_activity.MainTabActivity;
import com.pengbo.mhdcx.widget.MyTargetListDialog;
import com.pengbo.mhdcx.widget.MyTargetListDialog.DialogcallbackTarget;

public class TradeBeiDuiSuoDingActivity extends HdActivity implements OnClickListener, OnCheckedChangeListener {

	public final static int VIEW_BDCC = 0;
	public final static int VIEW_BDWT = 1;

	private TextView mTV_Back;
	private Button mBtn_zhengquanmc;
	private TextView mTV_DQKSD, mTV_DQKJS;

	private Button mBtn_Suoding, mBtn_Jiesuo;
	private TextView mBtn_ChaXun;
	private EditText mEdit_SDSL;
	private ImageView mImg_Refresh, mImg_XiaLa;

	private ListView mListView_BDCC;
	private ListView mListView_BDWT;

	private LinearLayout mlLayout_BDCCHeader;
	private LinearLayout mlLayout_BDWTHeader;

	private View mAmountMinus, mAmountAdd;

	// private RelativeLayout mrLayout_ZhengQuan;
	private MyTargetListDialog mZhengQuanTargetDialog;// 弹出的标的对话框 存储的是对象
	private ZhengQuanTargetDialogAdapter mZhengQuanListAdapter;
	private ArrayList<Stock> mZhengQuanListData;// 可以锁定和解锁的stock列表
	// private Stock mCurrentStock;//当前要解锁或锁定的合约
	private int mCurrentStockIndex = -1;// 当前选中要解锁的合约索引

	public RadioButton mRBBDCC, mRBBDWT;//
	public RadioGroup mRG;

	private TradeBDListViewAdapter mListAdapterBDCC;
	private TradeBDListViewAdapter mListAdapterBDWT;

	private int mViewSwitcherIndex = VIEW_BDCC; // 0:CC view;1:WT

	private MyApp mMyApp;
	private Context mContext;

	private PBSTEP mListData_BDCC;
	private PBSTEP mListData_BDWT;

	public Dialog mProgress;
	public com.pengbo.mhdcx.widget.AlertDialog mWTConfirmDialog;

	private int mRequestCode[]; // 0-可锁定数量，1-可解锁数量
	public String mKDJJDSL[]; // num 2 - 可锁定，可解锁
	
	private Timer mTimerRequest = null;

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			int nFrameType = msg.arg1;
			int nRequestCode = msg.arg2;

			switch (msg.what) {
				case 1: {
					requestBDSDWT();
					requestBDCCCX();
				}
					break;
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
							new com.pengbo.mhdcx.widget.AlertDialog(TradeBeiDuiSuoDingActivity.this).builder().setTitle("登录")
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
					else if (nFrameType == Trade_Define.Func_BDZQCCCX)// 备兑持仓
					{
						closeProgress();
						if (aMsgObject.nErrorCode < 0) {
							// 失败，弹出messagebox提醒用户
							new com.pengbo.mhdcx.widget.AlertDialog(mContext).builder().setTitle(mContext.getString(R.string.IDS_Warning)).setMsg(aMsgObject.errorMsg)
									.setCancelable(false).setCanceledOnTouchOutside(false).setPositiveButton(mContext.getString(R.string.IDS_QueDing), new OnClickListener() {

										@Override
										public void onClick(View v) {

										}
									}).show();
							return;
						}
						updateBDCCList();

					} else if (nFrameType == Trade_Define.Func_FJYWTCX)// 备兑委托查询
					{
						closeProgress();
						if (aMsgObject.nErrorCode < 0) {
							// 失败，弹出messagebox提醒用户
							new com.pengbo.mhdcx.widget.AlertDialog(mContext).builder().setTitle(mContext.getString(R.string.IDS_Warning)).setMsg(aMsgObject.errorMsg)
									.setCancelable(false).setCanceledOnTouchOutside(false).setPositiveButton(mContext.getString(R.string.IDS_QueDing), new OnClickListener() {

										@Override
										public void onClick(View v) {

										}
									}).show();
							return;
						}
						updateBDWTList();

					} else if (nFrameType == Trade_Define.Func_BDDJJD)// 备兑冻结解冻
					{
						closeProgress();
						if (aMsgObject.nErrorCode < 0) {
							// 修改失败，弹出messagebox提醒用户
							new com.pengbo.mhdcx.widget.AlertDialog(mContext).builder().setTitle(mContext.getString(R.string.IDS_Warning)).setMsg(aMsgObject.errorMsg)
									.setCancelable(false).setCanceledOnTouchOutside(false).setPositiveButton(mContext.getString(R.string.IDS_QueDing), new OnClickListener() {

										@Override
										public void onClick(View v) {

										}
									}).show();
						} else {

							PBSTEP aStep = new PBSTEP();
							int num = mMyApp.mTradeData.GetWT(aStep);
							String wtbh = aStep.GetFieldValueString(STEP_Define.STEP_WTBH);
							if (num > 0) {
								String strMsg = String.format("委托编号：%s", wtbh);
								Toast.makeText(MyApp.getInstance().mTradeDetailActivity.getParent(), strMsg, Toast.LENGTH_SHORT).show();
							}

							requestBDSDWT();
							requestBDCCCX();
							startRequestTimer(3000);
						}

					} else if (nFrameType == Trade_Define.Func_BDKDJJDSL)// 备兑冻结解冻数量查询
					{
						closeProgress();
						if (aMsgObject.nErrorCode < 0) {
							// 修改失败，弹出messagebox提醒用户
							new com.pengbo.mhdcx.widget.AlertDialog(mContext).builder().setTitle(mContext.getString(R.string.IDS_Warning)).setMsg(aMsgObject.errorMsg)
									.setCancelable(false).setCanceledOnTouchOutside(false).setPositiveButton(mContext.getString(R.string.IDS_QueDing), new OnClickListener() {

										@Override
										public void onClick(View v) {

										}
									}).show();
						} else {
							if (nRequestCode == mRequestCode[0])//可锁定数量
							{
								PBSTEP aStep = new PBSTEP();
								mMyApp.mTradeData.GetBDKDJSL(aStep);
								mKDJJDSL[0] = aStep.GetFieldValueString(STEP_Define.STEP_KMSL);
							} else if (nRequestCode == mRequestCode[1] )//可解锁数量
							{
								PBSTEP aStep = new PBSTEP();
								mMyApp.mTradeData.GetBDKJDSL(aStep);
								mKDJJDSL[1] = aStep.GetFieldValueString(STEP_Define.STEP_KMSL);
							}
							updateBDKDJJDSL();
						}
					}
					break;
				}

				case TradeNetConnect.MSG_LOCK: {
					proc_MSG_LOCK(msg);
				}
					break;

				case TradeNetConnect.MSG_DISCONNECT: {
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
		setContentView(R.layout.trade_suodingjiesuo_activity);

		mContext = this;
		mMyApp = (MyApp) this.getApplication();
		mWTConfirmDialog = new com.pengbo.mhdcx.widget.AlertDialog(this).builder();
		mRequestCode = new int[2];
		mKDJJDSL = new String[2];
		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();

		switch (mViewSwitcherIndex) {
			case VIEW_BDCC:
				this.requestBDCCCX();
				break;
			case VIEW_BDWT:
				this.requestBDSDWT();
				break;
		}
	}

	@Override
	protected void onDestroy() {
		mMyApp.mTradeNet.setMainHandler(null);
		closeProgress();
		if (mWTConfirmDialog != null) {
			mWTConfirmDialog.dismiss();
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mMyApp.mTradeNet.setMainHandler(null);
		closeProgress();
		if (mWTConfirmDialog != null) {
			mWTConfirmDialog.dismiss();
		}
		super.onPause();
	}

	private void initView() {
		mZhengQuanListData = this.getTargetStockInfo();
		if (mZhengQuanListData != null && mZhengQuanListData.size() > 0) {
			mCurrentStockIndex = 0;
		}
		mTV_Back = (TextView) findViewById(R.id.tv_bd_header_left_back);
		mTV_Back.setOnClickListener(this);

		mTV_DQKSD = (TextView) findViewById(R.id.tv_dqksd);
		mTV_DQKJS = (TextView) findViewById(R.id.tv_dqkjs);
		mBtn_ChaXun = (TextView) findViewById(R.id.btn_refresh);
		mBtn_ChaXun.setOnClickListener(this);

		mBtn_Suoding = (Button) findViewById(R.id.btn_suoding);
		mBtn_Suoding.setOnClickListener(this);
		mBtn_Jiesuo = (Button) findViewById(R.id.btn_jiesuo);
		mBtn_Jiesuo.setOnClickListener(this);

		mImg_Refresh = (ImageView) findViewById(R.id.img_right_refresh);
		mImg_Refresh.setOnClickListener(this);
		
		mImg_XiaLa = (ImageView) findViewById(R.id.beidui_xiala);
		mImg_XiaLa.setOnClickListener(this);

		mAmountMinus = findViewById(R.id.btn_delete_two);
		mAmountAdd = findViewById(R.id.btn_add_two);
		mAmountMinus.setOnClickListener(this);
		mAmountAdd.setOnClickListener(this);

		mEdit_SDSL = (EditText) findViewById(R.id.edit_sdsl);
		mEdit_SDSL.setInputType(InputType.TYPE_CLASS_PHONE);

		mRBBDCC = (RadioButton) findViewById(R.id.rb_bdcc);
		mRBBDWT = (RadioButton) findViewById(R.id.rb_bdwt);

		mRG = (RadioGroup) findViewById(R.id.beidui_radiogroup);
		mRG.setOnCheckedChangeListener(this);
		mRG.setFocusable(true);

		mlLayout_BDCCHeader = (LinearLayout) findViewById(R.id.lLayout_bdccheader);
		mlLayout_BDCCHeader.setVisibility(View.VISIBLE);
		mlLayout_BDWTHeader = (LinearLayout) findViewById(R.id.lLayout_bdwtheader);
		mlLayout_BDWTHeader.setVisibility(View.GONE);
		// mrLayout_ZhengQuan = (RelativeLayout) findViewById(R.id.rlayout_zhengquan);
		// mrLayout_ZhengQuan.setOnClickListener(this);
		mBtn_zhengquanmc = (Button) findViewById(R.id.btn_zhengquanmc);
		mBtn_zhengquanmc.setOnClickListener(this);
		if (mCurrentStockIndex >= 0 && mCurrentStockIndex < mZhengQuanListData.size()) {
			mBtn_zhengquanmc.setText(mZhengQuanListData.get(mCurrentStockIndex).code + " " + mZhengQuanListData.get(mCurrentStockIndex).name);
		} else {
			mBtn_zhengquanmc.setText(Global_Define.STRING_VALUE_EMPTY);
		}

		if (mListView_BDCC == null) {
			mListView_BDCC = (ListView) findViewById(R.id.lv_bdcc);

			mListData_BDCC = new PBSTEP();
			mListAdapterBDCC = new TradeBDListViewAdapter(this, mListData_BDCC, true);
			mListView_BDCC.setAdapter(mListAdapterBDCC);
			mListView_BDCC.setVisibility(View.VISIBLE);
			mListView_BDCC.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
						long id) {
					if (mViewSwitcherIndex == VIEW_BDCC) {
						if (mListData_BDCC != null) {
							int num = mListData_BDCC.GetRecNum();
							if (position < num) {

								mCurrentStockIndex = position;
								updateBDSLCXView();
							}
						}
					}
				}
				
			});
		}

		if (mListView_BDWT == null) {
			mListView_BDWT = (ListView) findViewById(R.id.lv_bdwt);

			mListData_BDWT = new PBSTEP();
			mListAdapterBDWT = new TradeBDListViewAdapter(this, mListData_BDWT, false);
			mListView_BDWT.setAdapter(mListAdapterBDWT);
			mListView_BDWT.setVisibility(View.GONE);
		}
	}

	// 备兑持仓查询
	private void requestBDCCCX() {
		mMyApp.mTradeNet.setMainHandler(mHandler);
		mMyApp.mTradeNet.Request_ListQuery(Trade_Define.Func_BDZQCCCX, null);
	}

	// 备兑锁定委托查询
	private void requestBDSDWT() {
		mMyApp.mTradeNet.setMainHandler(mHandler);
		mMyApp.mTradeNet.Request_ListQuery(Trade_Define.Func_FJYWTCX, null);
	}

	// 备兑可冻结解冻数量
	private void requestBDKDJJDSL() {
		showProgress();
		if (mCurrentStockIndex >= 0 && mCurrentStockIndex < mZhengQuanListData.size()) {
			Stock info = mZhengQuanListData.get(mCurrentStockIndex);
			String marketCode = info.tradeMarket;
			String gdzh = mMyApp.mTradeData.GetGDZHFromMarket(marketCode);
			String xwh = mMyApp.mTradeData.GetXWHFromMarket(marketCode);

			mMyApp.mTradeNet.setMainHandler(mHandler);
			mRequestCode[0] = mMyApp.mTradeNet.Request_BDKDJ_JDSL(info.code, marketCode, gdzh, xwh, PTK_Define.PTK_FJYLB_SD, "");
			mRequestCode[1] = mMyApp.mTradeNet.Request_BDKDJ_JDSL(info.code, marketCode, gdzh, xwh, PTK_Define.PTK_FJYLB_JS, "");

		} else {
			closeProgress();
		}
	}

	// 备兑锁定解锁
	private void requestBDDJJD(char sdfx, String wtsl) {
		showProgress();
		if (mCurrentStockIndex >= 0 && mCurrentStockIndex < mZhengQuanListData.size()) {
			Stock info = mZhengQuanListData.get(mCurrentStockIndex);

			String marketCode = info.tradeMarket;
			String gdzh = mMyApp.mTradeData.GetGDZHFromMarket(marketCode);
			String xwh = mMyApp.mTradeData.GetXWHFromMarket(marketCode);

			mMyApp.mTradeNet.setMainHandler(mHandler);
			mMyApp.mTradeNet.Request_BDDJJD(info.code, marketCode, wtsl, gdzh, xwh, sdfx, "");
		}
	}

	public void updateBDSLCXView() {
		if (mCurrentStockIndex >= 0 && mCurrentStockIndex < mZhengQuanListData.size()) {
			mBtn_zhengquanmc.setText(mZhengQuanListData.get(mCurrentStockIndex).code + " " + mZhengQuanListData.get(mCurrentStockIndex).name);
			mTV_DQKSD.setText(mZhengQuanListData.get(mCurrentStockIndex).ksdsl);
			mTV_DQKJS.setText(mZhengQuanListData.get(mCurrentStockIndex).kjssl);
		} else {
			mBtn_zhengquanmc.setText(Global_Define.STRING_VALUE_EMPTY);
			mTV_DQKSD.setText(Global_Define.STRING_VALUE_EMPTY);
			mTV_DQKJS.setText(Global_Define.STRING_VALUE_EMPTY);
		}
	}

	public void updateBDKDJJDSL() {
		if (this.mKDJJDSL[0] != null) {
			mKDJJDSL[0] = STD.IntToString((int)STD.StringToValue(mKDJJDSL[0]));
			mTV_DQKSD.setText(mKDJJDSL[0]);

		}
		if (this.mKDJJDSL[1] != null) {
			mKDJJDSL[1] = STD.IntToString((int)STD.StringToValue(mKDJJDSL[1]));
			mTV_DQKJS.setText(mKDJJDSL[1]);
		}
	}

	public void updateBDCCList() {
		mMyApp.mTradeData.GetBDCC(mListData_BDCC);
		if (this.mViewSwitcherIndex == VIEW_BDCC) {
			mListAdapterBDCC.notifyDataSetChanged();
		}

		mZhengQuanListData = this.getTargetStockInfo();
		if (mCurrentStockIndex < 0 || mCurrentStockIndex >= mZhengQuanListData.size()) {
			if (mZhengQuanListData.size() > 0) {
				mCurrentStockIndex = 0;
			} else {
				mCurrentStockIndex = -1;
			}
		}
		updateBDSLCXView();
	}

	public void updateBDWTList() {
		mMyApp.mTradeData.GetFJYWT(mListData_BDWT, true);
		if (this.mViewSwitcherIndex == VIEW_BDWT) {
			mListAdapterBDWT.notifyDataSetChanged();
		}
	}

	private ArrayList<Stock> getTargetStockInfo() {
		ArrayList<Stock> codeInfoArray = new ArrayList<Stock>();
		if (mListData_BDCC != null) {
			for (int i = 0; i < mListData_BDCC.GetRecNum(); i++) {
				mListData_BDCC.GotoRecNo(i);

				Stock info = new Stock();
				info.code = mListData_BDCC.GetFieldValueString(STEP_Define.STEP_HYDM);
				info.name = mListData_BDCC.GetFieldValueString(STEP_Define.STEP_HYDMMC);
				info.tradeMarket = mListData_BDCC.GetFieldValueString(STEP_Define.STEP_SCDM);
				info.kjssl = mListData_BDCC.GetFieldValueString(STEP_Define.STEP_KJSSL);
				info.ksdsl = mListData_BDCC.GetFieldValueString(STEP_Define.STEP_KSDSL);
				codeInfoArray.add(info);
			}
		}
		if (codeInfoArray.size() <= 0) {
			// 添加标的
			List<TagCodeInfo> tagCodeInfos = mMyApp.mStockConfigData.getStockList();
			if (tagCodeInfos != null) {
				for (int i = 0; i < tagCodeInfos.size(); i++) {
					if (tagCodeInfos.get(i).name == null || "".equals(tagCodeInfos.get(i).name.trim().toString())) {
						continue;
					} else {
						Stock info = new Stock();
						info.code = tagCodeInfos.get(i).code;
						info.name = tagCodeInfos.get(i).name;
						if (tagCodeInfos.get(i).market == HQ_Define.HQ_MARKET_SH_INT) {
							info.tradeMarket = Trade_Define.ENum_MARKET_SHQQA;
						} else if (tagCodeInfos.get(i).market == HQ_Define.HQ_MARKET_SZ_INT) {
							info.tradeMarket = Trade_Define.ENum_MARKET_SZQQA;
						}
						info.kjssl = Global_Define.STRING_VALUE_EMPTY;
						info.ksdsl = Global_Define.STRING_VALUE_EMPTY;
						codeInfoArray.add(info);
					}
				}
			}
		}

		return codeInfoArray;
	}

	/**
	 * 标的对象的回掉函数
	 */
	DialogcallbackTarget dialogcallback_Zhengquan = new DialogcallbackTarget() {

		@Override
		public void dialogdo(final int index) {
			if (mZhengQuanListData == null) {
				mCurrentStockIndex = -1;
				return;
			}

			if (index >= 0 && index < mZhengQuanListData.size()) {
				mCurrentStockIndex = index;
				updateBDSLCXView();
			}
		}

	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_bd_header_left_back:
				this.finish();
				break;
			case R.id.btn_zhengquanmc:
			case R.id.beidui_xiala:
			{
				if (mZhengQuanTargetDialog == null) {
					mZhengQuanTargetDialog = new MyTargetListDialog(mContext);
				}
				if (mZhengQuanListAdapter == null) {
					mZhengQuanListAdapter = new ZhengQuanTargetDialogAdapter(mContext, getTargetStockInfo());
				}else
				{
					mZhengQuanListAdapter.setDatas(getTargetStockInfo());
					mZhengQuanListAdapter.notifyDataSetChanged();
				}

				mZhengQuanTargetDialog.setContent(mZhengQuanListAdapter);
				mZhengQuanTargetDialog.setDialogCallback(dialogcallback_Zhengquan);
				mZhengQuanTargetDialog.setCancelable(true);
				mZhengQuanTargetDialog.setCanceledOnTouchOutside(true);
				mZhengQuanTargetDialog.show();

			}
				break;
			case R.id.btn_suoding: {
				if (mWTConfirmDialog != null) {
					mWTConfirmDialog.dismiss();
				} else {
					mWTConfirmDialog = new com.pengbo.mhdcx.widget.AlertDialog(this).builder();
				}

				mWTConfirmDialog.clear();
				
				if(mEdit_SDSL.getText().toString().isEmpty())
				{
					mWTConfirmDialog.setTitle("提示").setMsg("请输入委托数量").setCancelable(false).setCanceledOnTouchOutside(false).setPositiveButton("确认", new OnClickListener() {

						@Override
						public void onClick(View v) {
							
						}
					}).show();
					return;
				}

				Stock info = null;
				if (mCurrentStockIndex >= 0 && mCurrentStockIndex < mZhengQuanListData.size()) {
					info = mZhengQuanListData.get(mCurrentStockIndex);
				}
				String msg = "";
				if (info != null) {
					msg = String.format("证券代码：%s\n证券名称：%s\n委托数量：%s", info.code, info.name, mEdit_SDSL.getText().toString());
				}

				mWTConfirmDialog.setTitle("锁定确认").setMsg(msg).setCancelable(false).setCanceledOnTouchOutside(false).setPositiveButton("确认锁定", new OnClickListener() {

					@Override
					public void onClick(View v) {
						requestBDDJJD(PTK_Define.PTK_FJYLB_SD, mEdit_SDSL.getText().toString());
					}
				}).setNegativeButton("取消", new OnClickListener() {

					@Override
					public void onClick(View v) {

					}
				}).show();

			}
				break;
			case R.id.btn_jiesuo: {
				if (mWTConfirmDialog != null) {
					mWTConfirmDialog.dismiss();
				} else {
					mWTConfirmDialog = new com.pengbo.mhdcx.widget.AlertDialog(this).builder();
				}
				mWTConfirmDialog.clear();
				if(mEdit_SDSL.getText().toString().isEmpty())
				{
					mWTConfirmDialog.setTitle("提示").setMsg("请输入委托数量").setCancelable(false).setCanceledOnTouchOutside(false).setPositiveButton("确认", new OnClickListener() {

						@Override
						public void onClick(View v) {
							
						}
					}).show();
					return;
				}

				Stock info = null;
				if (mCurrentStockIndex >= 0 && mCurrentStockIndex < mZhengQuanListData.size()) {
					info = mZhengQuanListData.get(mCurrentStockIndex);
				}
				String msg = "";
				if (info != null) {
					msg = String.format("证券代码：%s\n证券名称：%s\n委托数量：%s", info.code, info.name, mEdit_SDSL.getText().toString());
				}

				mWTConfirmDialog.setTitle("解锁确认").setMsg(msg).setCancelable(false).setCanceledOnTouchOutside(false).setPositiveButton("确认解锁", new OnClickListener() {

					@Override
					public void onClick(View v) {
						requestBDDJJD(PTK_Define.PTK_FJYLB_JS, mEdit_SDSL.getText().toString());
					}
				}).setNegativeButton("取消", new OnClickListener() {

					@Override
					public void onClick(View v) {

					}
				}).show();
			}
				break;
			case R.id.btn_refresh: {
				requestBDKDJJDSL();
			}
				break;
			case R.id.btn_delete_two: // amount minus
			{
				if (mCurrentStockIndex == -1) {
					return;
				}

				String str = mEdit_SDSL.getText().toString();
				if (str.length() > 0) {
					int value = 0;
					try {
						value = Integer.parseInt(str);
					} catch (Exception e) {
						value = (int) Double.parseDouble(str);
					}

					if (value < 0) {
						mEdit_SDSL.setText("0");
						return;
					}
					value -= 100;
					if (value < 0) value = 0;

					mEdit_SDSL.setText(String.valueOf(value));
					mEdit_SDSL.setSelection(String.valueOf(value).length());
				}
			}
				break;
			case R.id.btn_add_two: // amount add
			{
				if (mCurrentStockIndex == -1) {
					return;
				}

				String str = mEdit_SDSL.getText().toString();
				int value = 0;
				if (str.length() > 0) {
					try {
						value = Integer.parseInt(str);
					} catch (Exception e) {
						value = (int) Double.parseDouble(str);
					}
				}
				value += 100;
				mEdit_SDSL.setText(String.valueOf(value));
				mEdit_SDSL.setSelection(String.valueOf(value).length());
			}
				break;
			case R.id.img_right_refresh: {
				if (this.mViewSwitcherIndex == VIEW_BDCC) {
					requestBDCCCX();

				} else if (this.mViewSwitcherIndex == VIEW_BDWT) {
					requestBDSDWT();
					requestBDCCCX();
				} else
				{
					requestBDSDWT();
					requestBDCCCX();
				}
			}
				break;
			default:
				break;
		}
	}
	
	//锁定解锁委托后3秒后重新请求持仓委托一次
	private void startRequestTimer(long timer) {

		stopRequestTimer();

		{
			mTimerRequest = new Timer();
			mTimerRequest.schedule(new TimerTask() {
				//
				public void run() {
					Message message = new Message();
					message.what = 1;
					mHandler.sendMessage(message);

				}
			}, timer);
		}
	}

	private void stopRequestTimer() {
		if (mTimerRequest != null) {
			mTimerRequest.cancel();
		}
		mTimerRequest = null;
	}

	private void proc_MSG_LOCK(Message msg) {
		new com.pengbo.mhdcx.widget.AlertDialog(this).builder().setTitle("交易登录超时").setMsg("交易登录在线时间过长，需要重新登录！").setCancelable(false).setCanceledOnTouchOutside(false)
				.setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(View v) {
						mMyApp.setHQPushNetHandler(null);
						mMyApp.mTradeNet.setMainHandler(null);
						mMyApp.mTradeNet.closeConnect();
						mMyApp.mTradeData.mTradeLoginFlag = false;
						mMyApp.setCurrentOption(null);
						mMyApp.mTradeData.clearStepData();

						if (mWTConfirmDialog != null) {
							mWTConfirmDialog.dismiss();
						}

						Intent intent = new Intent();
						MainTabActivity act = AppActivityManager.getAppManager().getMainTabActivity();

						if (act != null) {
							act.setChangePage(MainTabActivity.PAGE_TRADE);
						}
						intent.setClass(TradeBeiDuiSuoDingActivity.this, MainTabActivity.class);
						startActivity(intent);
						TradeBeiDuiSuoDingActivity.this.finish();
					}
				}).show();
	}
	
	protected void proc_MSG_DISCONNECT(Message msg) {
		closeProgress();
		if(mMyApp.mTradeData.mReconnected)
		{
		    new com.pengbo.mhdzq.widget.AlertDialog(this).builder().setTitle("提示")
		    .setMsg("网络连接断开").setCancelable(false)
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
		    .setMsg("网络连接断开").setCancelable(false)
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
		mMyApp.mTradeNet.setMainHandler(null);
		mMyApp.mTradeNet.closeConnect();
		mMyApp.mTradeData.mTradeLoginFlag = false;
		mMyApp.setCurrentOption(null);
		mMyApp.mTradeData.clearStepData();

		if (mWTConfirmDialog != null) {
			mWTConfirmDialog.dismiss();
		}

		Intent intent = new Intent();
		MainTabActivity act = AppActivityManager.getAppManager().getMainTabActivity();

		if (act != null) {
			act.setChangePage(MainTabActivity.PAGE_TRADE);
		}
		intent.setClass(TradeBeiDuiSuoDingActivity.this, MainTabActivity.class);
		startActivity(intent);
		TradeBeiDuiSuoDingActivity.this.finish();

	}

	protected void showProgress() {
		closeProgress();

		if (mProgress == null) {
			mProgress = new Dialog(this, R.style.AlertDialogStyle);
			mProgress.setContentView(R.layout.list_loading);
			TextView tv = (TextView) mProgress.findViewById(R.id.loading_text);
			tv.setText("请求中，请稍后......");
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

	private void changeView(int index) {
		switch (index) {
			case VIEW_BDCC:
				if (mViewSwitcherIndex != index) {
					requestBDCCCX();
					mViewSwitcherIndex = index;
					mlLayout_BDCCHeader.setVisibility(View.VISIBLE);
					mlLayout_BDWTHeader.setVisibility(View.GONE);

					mListView_BDCC.setVisibility(View.VISIBLE);
					mListView_BDWT.setVisibility(View.GONE);
				}
				break;
			case VIEW_BDWT:
				if (mViewSwitcherIndex != index) {
					requestBDSDWT();
					mViewSwitcherIndex = index;
					mlLayout_BDCCHeader.setVisibility(View.GONE);
					mlLayout_BDWTHeader.setVisibility(View.VISIBLE);

					mListView_BDCC.setVisibility(View.GONE);
					mListView_BDWT.setVisibility(View.VISIBLE);
				}
				break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
			case R.id.rb_bdcc:
				changeView(VIEW_BDCC);
				break;
			case R.id.rb_bdwt:
				changeView(VIEW_BDWT);
				break;

			default:
				break;
		}
	}

	public class ZhengQuanTargetDialogAdapter extends BaseAdapter {

		private List<Stock> datas;
		private Context context;

		public ZhengQuanTargetDialogAdapter(Context context, List<Stock> data) {
			this.context = context;
			this.datas = data;
		}

		public List<Stock> getDatas() {
			return datas;
		}

		public void setDatas(List<Stock> datas) {
			this.datas = datas;
		}

		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public Object getItem(int position) {
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.view_mydialog_item, null);
			TextView tv_mydialog = (TextView) view.findViewById(R.id.view_mydialog_item_text);
			Stock info = datas.get(position);
			tv_mydialog.setText(info.code + " " + info.name);
			return view;
		}
	}
}
