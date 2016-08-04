package com.pengbo.mhdcx.fragment;

import com.pengbo.mhdcx.adapter.OrderListViewCCAdapter;
import com.pengbo.mhdcx.adapter.OrderListViewCDAdapter;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.HQ_Define;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.tools.ColorConstant;
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
import com.pengbo.mhdcx.ui.activity.TradeHeadOptionListActivity;
import com.pengbo.mhdcx.ui.trade_activity.OnTradeFragmentListener;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdcx.view.Ctrl_Trend_FivePrice;
import com.pengbo.mhdcx.view.MyKeyboardWindow;
import com.pengbo.mhdcx.widget.ActionSheetDialog;
import com.pengbo.mhdcx.widget.AlertDialog;
import com.pengbo.mhdcx.widget.OrderTrendDialog;
import com.pengbo.mhdcx.widget.ActionSheetDialog.OnSheetItemClickListener;
import com.pengbo.mhdcx.widget.ActionSheetDialog.SheetItemColor;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * 交易中下单页
 * 
 * @author pobo
 * 
 */
public class TradeOrderFragment extends Fragment implements OnClickListener,
		OnCheckedChangeListener, OnItemClickListener {
	public static final String TAG = "TradeOrderFragment";

	public final static int TRADE_STATUS_FOK = 2;
	public final static int request_code_trend = 1011;
	public final static int request_code_selectoption = 1012;
	public final static int VIEW_CHICANG = 0;
	public final static int VIEW_WT = 1;

	public final static int Btn_WT_BuyOpen = 100;
	public final static int Btn_WT_SellClose = 101;
	public final static int Btn_WT_SellOpen = 102;
	public final static int Btn_WT_BuyClose = 103;
	public final static int Btn_WT_All = 104;

	private MyApp mMyApp;
	public View mView;
	public Activity mActivity;

	private ListView mListView_CC;
	private ListView mListView_CD;
	private OrderListViewCCAdapter mListAdapterCC;
	private OrderListViewCDAdapter mListAdapterCD;// 修改显示所有当日委托
	private LinearLayout mlLayout_listHeader;

	private Ctrl_Trend_FivePrice mFivePriceView;
	public OrderTrendDialog mTrendDialog;
	public com.pengbo.mhdcx.widget.AlertDialog mWTConfirmDialog;
	public ActionSheetDialog mActionSheetDlg;

	private RadioGroup mRgChiCangWT;
	private EditText mEditTradePrice; // 交易价格
	private EditText mEditTradeAmount; // 交易数量
	private LinearLayout mPriceMinus, mPriceAdd, mAmountMinus, mAmountAdd;
	private LinearLayout mBuyOpen,// 买入开仓
			mSellPing,// 卖出平仓
			mSellOpen,// 卖出开仓
			mBuyPing;// 买入平仓
	private LinearLayout mlLayout_buysell;// 买卖按钮布局
	private RelativeLayout mRLayout_EditPrice;// 价格显示edit布局
	private TextView mTV_Account_Del, mTV_Account_Add;
	private PopupWindow mMoneyWindow;
	private CheckBox mCB_BeiDui;

	// private TextView mStockName;// 标的名称
	// private TextView mZDFD; // 标的名称后面跟的 涨跌幅度
	// private TextView mZT, mDT;// 涨停 和 跌停 值

	// private TextView mTV_KMSL;// 可买可卖数量显示
	// private TextView mTV_buy_open_num, mTV_sell_ping_num, mTV_sell_open_num,
	// mTV_buy_ping_num;
	private TextView mTV_buy_open_btn_price, mTV_sell_close_btn_price,
			mTV_sell_open_btn_price, mTV_buy_close_btn_price;
	private TextView mTV_bottom_kyzj, mTV_bottom_bzjye;// 换成风险度
	private TextView mTV_delete_one_price, mTV_add_one_price;

	private ImageView mBtnTrend;// 点击从上单只弹出走势
	private ImageView mImg_PreOption, mImg_NextOption;// 点击上一个下一个期权button上的箭头

	private ImageView mShuaXin;// 持仓上面的透明刷新按钮
	private Button mChooseOption;// 点击选择期权跳转到 期权列表 包含全部和 自选
	private RelativeLayout mBtnPreOption, mBtnNextOption;// 点击上一个或下一个期权

	private ImageView mImg_fok, mImg_fuwei;

	public int mViewSwitcherIndex = VIEW_CHICANG; // 0:chicang view;1:chedan
													// view

	private ArrayList<TagCodeInfo> mOptionCodeInfoList; // 根据当前合约筛选出对应的月份标的购/估的合约列表
	private int mCurrentOptionIndex = 0; // 当前合约在合约列表里的索引
	private TagLocalStockData mPreOptionData = null;
	private TagLocalStockData mNextOptionData = null;
	private TagCodeInfo mOptionCodeInfo = null;// 合约
	private TagLocalStockData mOptionData = null;// 合约
	private TagLocalStockData mStockData = null;// 标的
	private TradeLocalRecord mOrderRecord;
	public TradeLocalRecord mTradeRecordQBPC;
	public TradeLocalRecord mTradeRecordKJFS;
	public ArrayList<String> mTradeWTBHArray;//快捷反手先平仓拆单后委托编号队列
	private char mMMLB;
	private char mKPBZ;
	private float mDWBZJ; // 单位保证金
	private boolean mBDFlag = false;//备兑标志false-非备兑(0)；true备兑(1)（只有认购期权，高级模式下才能勾选备兑，选中备兑只能卖开）
	private String mWTPrice;
	private float mMinPriceStep = 0.0001f; // 最小变动价位
	private int mAmountChangeNum = 1;// 1,5,10,50

	private PBSTEP mHoldList;
	private PBSTEP mCheDanList;
	private PBSTEP mListData_CC;
	private PBSTEP mListData_DRWT;//
	private PBSTEP mMoney;
	private int mSellWTPriceMode;
	public boolean mbUpdateFromSelect = false;
	private int mTradeMode;
	// private int[] mKMSL;

	private int mPriceDotLen = 4;

	private OnTradeFragmentListener mListener;
	private boolean mIsViewReady = false;

	public MyKeyboardWindow mKeyboard;
	private int mCurrentKJBJType = MyKeyboardWindow.KJBJ_TYPES_SH;

	public boolean mbFok = false;

	private Timer mTimerMoney = null;
	private Timer mTimerKJFS = null;

	// public View mLeftFront, mRightNext; // mLeftFront 前面一个按钮 mRightNext
	// 后面一个按钮
	// public ImageView mImgFrontBack, mImagBehindNext;
	public TextView mLeftOptionName, mLeftOptionPrice, mRightOptionName,
			mRightOptionPrice;

	protected Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// process incoming messages here
			switch (msg.what) {
			case TradeNetConnect.MSG_ADAPTER_KJFS_BUTTON_CLICK: {
				proc_MSG_ADAPTER_KJFS_BUTTON_CLICK(msg);
			}
				break;
			case 1: {
				if (mMoneyWindow != null) {
					mMoneyWindow.dismiss();
				}
			}
				break;
			case TRADE_STATUS_FOK: {
				int fok = msg.arg1;
				mbFok = (fok == 1)?true:false;
			}
				break;
			}

			super.handleMessage(msg);
		}

	};

	public TradeOrderFragment(MyApp myApp) {
		mMyApp = myApp;
		initData();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWTConfirmDialog = new com.pengbo.mhdcx.widget.AlertDialog(
				getActivity().getParent()).builder();
		
		mActionSheetDlg = new ActionSheetDialog(getActivity().getParent()).builder();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
		mMyApp = (MyApp) this.mActivity.getApplication();

		try {
			mListener = (OnTradeFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		L.d(TAG,"TradeDetailActivity.OnCreateView");
		mView = inflater.inflate(R.layout.trade_order_fragment, null);

		initStockView();
		initWuDangView();
		initPositionWeiTuoView();
		mIsViewReady = true;

		if (mListener != null) {
			mListener.requestDRWT();
		}
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		L.d(TAG, "onActivityCreated");
	}

	// get option and stock data, maybe null
	private void initData() {
		if (mOrderRecord == null) {
			mOrderRecord = new TradeLocalRecord();
		}

		if (mTradeRecordQBPC == null) {
			mTradeRecordQBPC = new TradeLocalRecord();
		}
		if (mTradeRecordKJFS == null) {
			mTradeRecordKJFS = new TradeLocalRecord();
		}

		if (mHoldList == null) {
			mHoldList = new PBSTEP();
		}

		if (mCheDanList == null) {
			mCheDanList = new PBSTEP();
		}

		if (mMoney == null) {
			mMoney = new PBSTEP();
		}

		if(mListData_CC == null)
		{
		   mListData_CC = new PBSTEP();
		}

		if(mListData_DRWT == null)
		{
		   mListData_DRWT = new PBSTEP();
		}
		// mKMSL = new int[4];
		mOptionCodeInfoList = new ArrayList<TagCodeInfo>();
		mTradeWTBHArray = new ArrayList<String>();
	}

	// 根据当前合约获得筛选后的合约列表
	// 并给当前合约前一个和后一个合约赋值
	private void getOptionList() {
		mOptionCodeInfoList.clear();
		if (mOptionCodeInfo != null && mOptionData != null) {
			mOptionCodeInfoList = mMyApp.mHQData.getOptionList(
					mOptionData.optionData.StockCode,
					mOptionData.optionData.StockMarket,
					mOptionData.optionData.StrikeDateNoDay,
					mOptionData.optionData.OptionCP);
			for (int i = 0; i < mOptionCodeInfoList.size(); i++) {
				if (mOptionCodeInfo.code.equalsIgnoreCase(mOptionCodeInfoList
						.get(i).code)
						&& mOptionCodeInfo.market == mOptionCodeInfoList.get(i).market) {
					mCurrentOptionIndex = i;
				}
			}
		} else {
			mCurrentOptionIndex = 0;
		}
		setPreNextOption();
	}

	private void setPreNextOption() {
		int iCount = mOptionCodeInfoList.size();
		if (iCount > 0) {
			if (mCurrentOptionIndex < iCount && mCurrentOptionIndex >= 0) {
				if (mCurrentOptionIndex > 0) {
					if (mPreOptionData == null) {
						mPreOptionData = new TagLocalStockData();
					}
					if (!mMyApp.mHQData
							.getData(mPreOptionData, mOptionCodeInfoList
									.get(mCurrentOptionIndex - 1).market,
									mOptionCodeInfoList
											.get(mCurrentOptionIndex - 1).code,
									false)) {
						mPreOptionData = null;
					}
				}

				if (mCurrentOptionIndex < iCount - 1) {
					if (mNextOptionData == null) {
						mNextOptionData = new TagLocalStockData();
					}
					if (!mMyApp.mHQData
							.getData(mNextOptionData, mOptionCodeInfoList
									.get(mCurrentOptionIndex + 1).market,
									mOptionCodeInfoList
											.get(mCurrentOptionIndex + 1).code,
									false)) {
						mNextOptionData = null;
					}
				}

			}
		} else {
			mPreOptionData = null;
			mNextOptionData = null;
		}
	}

	// update chicang info, zijin info, weituo info and so on.
	public void updateAllData() {

		updateOptionData(false);
		// getOptionList();
		updateZJData();
		updateChiCang(false);
		updateDRWT_CD(false);
	}

	public void updateTrendLineData() {
		if (mTrendDialog != null) {
			mTrendDialog.updateOptionData(mOptionData, mStockData);
		}
	}

	public void updateOptionData(boolean isPush) {
		if (MyApp.getInstance().getCurrentOption() != null) {
			mOptionCodeInfo = MyApp.getInstance().getCurrentOption();
		}
		if (mOptionCodeInfo != null) {
			TagLocalStockData optionStockData = new TagLocalStockData();
			if (MyApp.getInstance().mHQData.getData(optionStockData,
					mOptionCodeInfo.market, mOptionCodeInfo.code, false)) {
				mOptionData = optionStockData;
				TagLocalStockData sStockInfo = new TagLocalStockData();
				if (MyApp.getInstance().mStockConfigData.getData(sStockInfo,
						mOptionData.optionData.StockMarket,
						mOptionData.optionData.StockCode, true)) // 标的的信息
				{
					mStockData = sStockInfo;
				}
			}
		}

		if (!isPush)
		{
			getOptionList();
			refreshDWBZJ();
			this.mBDFlag = false;
			this.mCB_BeiDui.setChecked(false);
			mMyApp.mIndexOfOrderBtn = Btn_WT_All;
		}
	}

	public void updateZJData() {
		if (mMoney == null) {
			mMoney = new PBSTEP();
		}
		mMyApp.mTradeData.GetMoney(mMoney);
	}

	public void updateChiCang(boolean isViewChange) {
		mMyApp.mTradeData.GetHoldStock(mListData_CC);
		if (this.mViewSwitcherIndex == VIEW_CHICANG) {
			mListAdapterCC.notifyDataSetChanged();
		}
	}

	public void updateDRWT_CD(boolean isViewChange) {
		mMyApp.mTradeData.GetDRWT(mListData_DRWT);
		if (this.mViewSwitcherIndex == VIEW_WT) {
			mListAdapterCD.notifyDataSetChanged();
			requestHQPushData();
		}
	}

	public void updateAllView() {
		updateHQView("", true);
		// updateKMSLView(mKMSL);
		updateZJView();
	}

	// 隐藏系统键盘
	public void hideSoftInputMethod(EditText editText) {
		InputMethodManager imm = ((InputMethodManager) mActivity
				.getSystemService(Context.INPUT_METHOD_SERVICE));

		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(mEditTradePrice.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}

	}

	/**
	 * 初始化控件
	 */
	private void initStockView() {
		mBtnTrend = (ImageView) mView.findViewById(R.id.btn_trendview);
		mChooseOption = (Button) mView.findViewById(R.id.choose_option);

		mImg_PreOption = (ImageView) mView.findViewById(R.id.left_back_front);
		mImg_NextOption = (ImageView) mView.findViewById(R.id.right_go_next);
		mBtnPreOption = (RelativeLayout) mView
				.findViewById(R.id.left_back_relativelayout);
		mBtnPreOption.setOnClickListener(this);
		mBtnNextOption = (RelativeLayout) mView
				.findViewById(R.id.right_next_relativelayout);
		mBtnNextOption.setOnClickListener(this);
		mLeftOptionName = (TextView) mView.findViewById(R.id.front_option_name);
		mLeftOptionPrice = (TextView) mView
				.findViewById(R.id.front_option_price);
		mRightOptionName = (TextView) mView
				.findViewById(R.id.behind_option_name);
		mRightOptionPrice = (TextView) mView
				.findViewById(R.id.behind_option_price);
		
		mCB_BeiDui = (CheckBox) mView.findViewById(R.id.cb_beidui);
		mCB_BeiDui.setEnabled(false);
		mCB_BeiDui.setChecked(false);
		mCB_BeiDui.setOnCheckedChangeListener(BDCheckListener); 

		mRLayout_EditPrice = (RelativeLayout) mView
				.findViewById(R.id.rlayout_order_editprice);
		mImg_fok = (ImageView) mView.findViewById(R.id.order_fok);
		mImg_fuwei = (ImageView) mView.findViewById(R.id.order_fuwei);
		mImg_fuwei.setOnClickListener(itemsOnClick);

		mShuaXin = (ImageView) mView.findViewById(R.id.order_shuaxin);
		mShuaXin.setOnClickListener(this);

		mEditTradePrice = (EditText) mView.findViewById(R.id.edit_add_cut);
		mEditTradePrice.setInputType(InputType.TYPE_NULL);
		mEditTradePrice.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (mOptionData == null) {
						return false;
					}

					if (mOptionData.HQData.market == HQ_Define.MARKET_SZQQ
							|| mOptionData.HQData.market == HQ_Define.MARKET_SZQQFZ) {
						mCurrentKJBJType = MyKeyboardWindow.KJBJ_TYPES_SZ;
					} else {
						mCurrentKJBJType = MyKeyboardWindow.KJBJ_TYPES_SH;
					}
					mEditTradePrice.setInputType(InputType.TYPE_NULL);
					hideSoftInputMethod(mEditTradePrice);
					if (mKeyboard == null) {
						mKeyboard = new MyKeyboardWindow(mHandler,
								mMyApp.mTradeDetailActivity, itemsOnClick,
								mEditTradePrice, mImg_fok, mImg_fuwei, false,
								mSellWTPriceMode, mCurrentKJBJType, mbFok);
						mKeyboard.showAtLocation(
								mView.findViewById(R.id.main_order),
								Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
								0);
					} else {
						mKeyboard.ResetKeyboard(mEditTradePrice, mImg_fok,
								mImg_fuwei, false, mSellWTPriceMode,
								mCurrentKJBJType, mbFok);
						mKeyboard.showAtLocation(
								mView.findViewById(R.id.main_order),
								Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
								0);

					}
				}

				return false;
			}
		});

		mEditTradeAmount = (EditText) mView.findViewById(R.id.edit_add_cut2);
		mEditTradeAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

		setPriceEditContent("");

		mPriceMinus = (LinearLayout) mView.findViewById(R.id.btn_delete_one);
		mPriceAdd = (LinearLayout) mView.findViewById(R.id.btn_add_one);
		mTV_delete_one_price = (TextView) mView
				.findViewById(R.id.delete_one_price);
		mTV_add_one_price = (TextView) mView.findViewById(R.id.add_one_price);

		mAmountMinus = (LinearLayout) mView.findViewById(R.id.btn_delete_two);
		mAmountAdd = (LinearLayout) mView.findViewById(R.id.btn_add_two);
		mTV_Account_Del = (TextView) mView.findViewById(R.id.tv_account_del);
		mTV_Account_Add = (TextView) mView.findViewById(R.id.tv_account_add);

		// mBuyOpen = (LinearLayout) mView.findViewById(R.id.order_buy_in_open);
		// mSellPing = (LinearLayout)
		// mView.findViewById(R.id.order_sell_out_ping);
		// mSellOpen = (LinearLayout)
		// mView.findViewById(R.id.order_sell_out_open);
		// mBuyPing = (LinearLayout) mView.findViewById(R.id.order_buy_in_ping);

		mTV_buy_open_btn_price = (TextView) mView
				.findViewById(R.id.order_buy_open);
		mTV_sell_close_btn_price = (TextView) mView
				.findViewById(R.id.order_sell_close);
		mTV_sell_open_btn_price = (TextView) mView
				.findViewById(R.id.order_sell_open);
		mTV_buy_close_btn_price = (TextView) mView
				.findViewById(R.id.order_buy_close);

		// mStockName = (TextView) mView.findViewById(R.id.BiaoDiName);
		// mZDFD = (TextView) mView.findViewById(R.id.BiaoDiFuDong);
		// mZT = (TextView) mView.findViewById(R.id.tv_zhangting_content);
		// mDT = (TextView) mView.findViewById(R.id.tv_dieting_content);
		//
		// mTV_KMSL = (TextView) mView.findViewById(R.id.tv_kmsl);

		mTV_bottom_kyzj = (TextView) mView.findViewById(R.id.tv_kyzj_bottom);// 可用资金
		mTV_bottom_bzjye = (TextView) mView.findViewById(R.id.tv_bzjye_bottom);// 保证金余额

		mBtnTrend.setOnClickListener(new ClickEvent());
		mChooseOption.setOnClickListener(this);

		// price amount add and minus
		mPriceMinus.setOnClickListener(this);
		mPriceAdd.setOnClickListener(this);
		mAmountMinus.setOnClickListener(this);
		mAmountAdd.setOnClickListener(this);

		mlLayout_buysell = (LinearLayout) mView
				.findViewById(R.id.llayout_buy_sell);
		mBuyOpen = (LinearLayout) mView.findViewById(R.id.order_buy_in_open);
		mSellPing = (LinearLayout) mView.findViewById(R.id.order_sell_out_ping);
		mSellOpen = (LinearLayout) mView.findViewById(R.id.order_sell_out_open);
		mBuyPing = (LinearLayout) mView.findViewById(R.id.order_buy_in_ping);

		mBuyOpen.setOnClickListener(this);
		mSellPing.setOnClickListener(this);
		mSellOpen.setOnClickListener(this);
		mBuyPing.setOnClickListener(this);
	}

	private void updateSellBuyBtns() {
		mlLayout_buysell.removeAllViews();

		int mode = PreferenceEngine.getInstance().getTradeSeniorModeWTBtn();
		if (mode == 0)// 买开，卖平，卖开，买平
		{
			mlLayout_buysell.addView(mBuyOpen);
			mlLayout_buysell.addView(mSellPing);
			mlLayout_buysell.addView(mSellOpen);
			mlLayout_buysell.addView(mBuyPing);
		} else if (mode == 1) // 买开，卖开，卖平，买平
		{
			mlLayout_buysell.addView(mBuyOpen);
			mlLayout_buysell.addView(mSellOpen);
			mlLayout_buysell.addView(mSellPing);
			mlLayout_buysell.addView(mBuyPing);
		} else {
			mlLayout_buysell.addView(mBuyOpen);
			mlLayout_buysell.addView(mSellPing);
			mlLayout_buysell.addView(mSellOpen);
			mlLayout_buysell.addView(mBuyPing);
		}
	}

	/**
	 * 下单界面 初始化 五档界面的控件 及设置值
	 */
	private void initWuDangView() {
		if (mFivePriceView == null) {
			mFivePriceView = (Ctrl_Trend_FivePrice) mView.findViewById(
					R.id.fiveprice_panel).findViewById(R.id.layout_fiveprice);
		}
	}

	private void initPositionWeiTuoView() {

		mRgChiCangWT = (RadioGroup) mView
				.findViewById(R.id.radiogroup_chicang_weituo);
		mRgChiCangWT.setOnCheckedChangeListener(this);

		mlLayout_listHeader = (LinearLayout) mView
				.findViewById(R.id.lLayout_order_list_header);
		mlLayout_listHeader.setVisibility(View.VISIBLE);


		if (mListView_CC == null) {
			mListView_CC = (ListView) mView
					.findViewById(R.id.trade_order_listview_cc);
			mListView_CC.setOnItemClickListener(this);

			mListAdapterCC = new OrderListViewCCAdapter(getActivity(),
					mListData_CC, mHandler);
			mListView_CC.setAdapter(mListAdapterCC);

		}
		if (mListView_CD == null) {
			mListView_CD = (ListView) mView
					.findViewById(R.id.trade_order_listview_cd);
			mListView_CD.setOnItemClickListener(this);

			mListAdapterCD = new OrderListViewCDAdapter(getActivity(),
					mListData_DRWT, mHandler);
			mListView_CD.setAdapter(mListAdapterCD);
			mListView_CD.setVisibility(View.GONE);
		}
	}

	private void updateWuDangView() {
		if (mFivePriceView != null) {
			mFivePriceView.updateData(mOptionData);
		}
	}

	public void updateZJView() {
		if (mMoney == null || !mIsViewReady)
			return;

		int retNum = mMoney.GetRecNum();
		if (retNum > 0) {
			String temp = mMoney.GetFieldValueString(STEP_Define.STEP_KYZJ);
			mTV_bottom_kyzj.setText(temp);

			// 风险度
			temp = mMoney.GetFieldValueString(STEP_Define.STEP_FXD);
			if (temp.isEmpty()) {
				temp = mMoney.GetFieldValueString(STEP_Define.STEP_JYSFXD);
			}
			if (temp.isEmpty()) {
				temp = Global_Define.STRING_VALUE_EMPTY;
			}
			mTV_bottom_bzjye.setText(temp);
		} else {
			mTV_bottom_kyzj.setText(Global_Define.STRING_VALUE_EMPTY);
			mTV_bottom_bzjye.setText(Global_Define.STRING_VALUE_EMPTY);
		}
	}

	// 切换到前一个或后一个期权
	// bPreOrNext - true-前一个，false-后一个
	private void gotoPreOrNextOption(boolean bPreOrNext) {
		boolean bUpdate = false;
		if (bPreOrNext) {
			if (mCurrentOptionIndex > 0) {
				mCurrentOptionIndex -= 1;
				bUpdate = true;
			}
		} else {
			if (mCurrentOptionIndex >= 0
					&& mCurrentOptionIndex < mOptionCodeInfoList.size() - 1) {
				mCurrentOptionIndex += 1;
				bUpdate = true;
			}
		}
		if (bUpdate) {
			mMyApp.setCurrentOption(mOptionCodeInfoList
					.get(mCurrentOptionIndex));

			mMyApp.mIndexOfOrderBtn = Btn_WT_All;
			mBuyOpen.setEnabled(true);
			mBuyPing.setEnabled(true);
			mSellOpen.setEnabled(true);
			mSellPing.setEnabled(true);

			updateOptionData(false);
			updateHQView("", true);
		}
	}

	private void updateHQAddView() {
		if (mPreOptionData != null && mCurrentOptionIndex - 1 >= 0
				&& mCurrentOptionIndex - 1 < mOptionCodeInfoList.size()) {
			String name1 = mPreOptionData.name;
			if (mPreOptionData.optionData.OptionCP == 0)// 购
			{
				name1 = name1.substring(name1.indexOf("购") + 1);
			} else if (mPreOptionData.optionData.OptionCP == 1)// 估
			{
				name1 = name1.substring(name1.indexOf("沽") + 1);
			}
			mLeftOptionName.setText(name1);
			mLeftOptionPrice.setText(ViewTools.getStringByFieldID(
					mPreOptionData, Global_Define.FIELD_HQ_NOW));
			// mLeftOptionPrice.setTextColor(ViewTools.getColorByFieldID(mPreOptionData,
			// Global_Define.FIELD_HQ_NOW));
		} else {
			mLeftOptionName.setText("--");
			mLeftOptionPrice.setText("--");
		}

		if (mNextOptionData != null && mCurrentOptionIndex + 1 > 0
				&& mCurrentOptionIndex + 1 < mOptionCodeInfoList.size()) {
			String name2 = mNextOptionData.name;
			if (mNextOptionData.optionData.OptionCP == 0)// 购
			{
				name2 = name2.substring(name2.indexOf("购") + 1);
			} else if (mNextOptionData.optionData.OptionCP == 1)// 估
			{
				name2 = name2.substring(name2.indexOf("沽") + 1);
			}
			mRightOptionName.setText(name2);
			mRightOptionPrice.setText(ViewTools.getStringByFieldID(
					mNextOptionData, Global_Define.FIELD_HQ_NOW));
			// mRightOptionPrice.setTextColor(ViewTools.getColorByFieldID(mNextOptionData,
			// Global_Define.FIELD_HQ_NOW));
		} else {
			mRightOptionName.setText("--");
			mRightOptionPrice.setText("--");
		}

		if (mOptionCodeInfo == null) {
			mBtnPreOption.setEnabled(false);
			mBtnPreOption.setClickable(false);
			mBtnNextOption.setEnabled(false);
			mBtnNextOption.setClickable(false);

			mImg_PreOption.setEnabled(false);
			mImg_NextOption.setEnabled(false);
		} else if (mCurrentOptionIndex <= 0) {
			mBtnPreOption.setEnabled(false);
			mBtnPreOption.setClickable(false);
			mBtnNextOption.setEnabled(true);
			mBtnNextOption.setClickable(true);

			mImg_PreOption.setEnabled(false);
			mImg_NextOption.setEnabled(true);
		} else if (mCurrentOptionIndex < mOptionCodeInfoList.size() - 1) {
			mBtnPreOption.setEnabled(true);
			mBtnPreOption.setClickable(true);
			mBtnNextOption.setEnabled(true);
			mBtnNextOption.setClickable(true);

			mImg_PreOption.setEnabled(true);
			mImg_NextOption.setEnabled(true);
		} else {
			mBtnPreOption.setEnabled(true);
			mBtnPreOption.setClickable(true);
			mBtnNextOption.setEnabled(false);
			mBtnNextOption.setClickable(false);

			mImg_PreOption.setEnabled(true);
			mImg_NextOption.setEnabled(false);
		}
	}

	public void updateHQView(String keyongNum, boolean isFromSelect) {
		if (!mIsViewReady)
			return;

		// 合约选择，合约涨停跌停，合约现价，
		if (mOptionData != null) {
			mChooseOption.setText(mOptionData.name);

			// 最新设计不显示涨停跌停价
			// String zt = ViewTools.getStringByFieldID(mOptionData,
			// Global_Define.FIELD_HQ_UPPRICE);
			// mZT.setText(zt);
			//
			// String dt = ViewTools.getStringByFieldID(mOptionData,
			// Global_Define.FIELD_HQ_DOWNPRICE);
			// mDT.setText(dt);
			mPriceDotLen = mOptionData.PriceDecimal;
			
			//认购期权以及高级模式，备兑enable
			if (mTradeMode == Trade_Define.TRADE_MODE_GAOJI && mOptionData.optionData.OptionCP == 0)
			{
				mCB_BeiDui.setEnabled(true);
				mCB_BeiDui.setChecked(mBDFlag);
			}else
			{
				mCB_BeiDui.setEnabled(false);
				mCB_BeiDui.setChecked(false);
				mBDFlag = false;
			}

		} else {
			mChooseOption.setText("选择期权");
			// mZT.setText(Global_Define.STRING_VALUE_EMPTY);
			// mDT.setText(Global_Define.STRING_VALUE_EMPTY);
			mCB_BeiDui.setEnabled(false);
			mCB_BeiDui.setChecked(false);
			mBDFlag = false;
		}

		// 最细设计不显示标的信息
		// 标的名称，涨跌幅
		// if (mStockData != null) {
		// mStockName.setText(ViewTools.getStringByFieldID(mStockData,
		// Global_Define.FIELD_HQ_NAME_ANSI));
		//
		// // 现价幅度等
		// String nowPrice = ViewTools.getStringByFieldID(mStockData,
		// Global_Define.FIELD_HQ_NOW);
		// String fd = ViewTools.getStringByFieldID(mStockData,
		// Global_Define.FIELD_HQ_ZDF_SIGN);
		// mZDFD.setText(String.format("%s %s", nowPrice, fd));
		// mZDFD.setTextColor(ViewTools.getColorByFieldID(mStockData,
		// Global_Define.FIELD_HQ_NOW));
		//
		// } else {
		// mStockName.setText(Global_Define.STRING_VALUE_EMPTY);
		// mZDFD.setText(Global_Define.STRING_VALUE_EMPTY);
		// }

		if (mOptionData != null && isFromSelect) {
			setInitPriceAndVolume();
			if (!keyongNum.isEmpty()) {
				mEditTradeAmount.setText(keyongNum);
			}
		} else {
			updateOrderPriceBtn();
		}

		mTV_Account_Add.setText(STD.IntToString(mAmountChangeNum));
		mTV_Account_Del.setText(STD.IntToString(mAmountChangeNum));

		updateHQAddView();

		updateWuDangView();
		//refreshDWBZJ();
		// startRequestKMSLTimer(2000);
	}

	public void updateKMSLView(int[] nkmsl) {
		if (!mIsViewReady)
			return;

		// mKMSL = nkmsl;

		String lableStr = "";

		if (nkmsl[0] > 0) {
			lableStr += String.format("买开：%d", nkmsl[0]);
		}

		if (nkmsl[1] > 0) {
			lableStr += String.format(" 卖平：%d", nkmsl[1]);
		}

		if (nkmsl[2] > 0) {
			lableStr += String.format(" 卖开：%d", nkmsl[2]);
		}

		if (nkmsl[3] > 0) {
			lableStr += String.format(" 买平：%d", nkmsl[3]);
		}
		// mTV_KMSL.setText(lableStr);
	}

	// 更新买卖按钮上的价格
	public void updateOrderPriceBtn() {
		String price = getPriceEditContent(mMMLB);

		if (mSellWTPriceMode == Trade_Define.WTPRICEMODE_DSJ) {
			// 复位btn不显示
			mImg_fuwei.setVisibility(View.GONE);
			String buyPrice = ViewTools.getStringByFieldID(mOptionData,
					Global_Define.FIELD_HQ_BUYPRICE);
			String sellPrice = ViewTools.getStringByFieldID(mOptionData,
					Global_Define.FIELD_HQ_SELLPRICE);
			mTV_buy_open_btn_price.setText(sellPrice);
			mTV_sell_close_btn_price.setText(buyPrice);
			mTV_sell_open_btn_price.setText(buyPrice);
			mTV_buy_close_btn_price.setText(sellPrice);
		} else if (mSellWTPriceMode == Trade_Define.WTPRICEMODE_GDJ) {
			// 复位btn显示
			mImg_fuwei.setVisibility(View.VISIBLE);
			String buyPrice = ViewTools.getStringByFieldID(mOptionData,
					Global_Define.FIELD_HQ_BUYPRICE);
			String sellPrice = ViewTools.getStringByFieldID(mOptionData,
					Global_Define.FIELD_HQ_SELLPRICE);

			mTV_buy_open_btn_price.setText(buyPrice);
			mTV_sell_close_btn_price.setText(sellPrice);
			mTV_sell_open_btn_price.setText(sellPrice);
			mTV_buy_close_btn_price.setText(buyPrice);
		} else {
			// 复位btn显示
			mImg_fuwei.setVisibility(View.VISIBLE);
			if (price.isEmpty()) {
				price = Global_Define.STRING_VALUE_EMPTY;
				if (mOptionData == null) {
					mImg_fuwei.setVisibility(View.GONE);
				}
			}

			mTV_buy_open_btn_price.setText(price);
			mTV_sell_close_btn_price.setText(price);
			mTV_sell_open_btn_price.setText(price);
			mTV_buy_close_btn_price.setText(price);
		}

		if (mTradeMode == Trade_Define.TRADE_MODE_GAOJI) {
			this.mBuyOpen.setVisibility(View.VISIBLE);
			this.mSellPing.setVisibility(View.VISIBLE);
			this.mSellOpen.setVisibility(View.VISIBLE);
			this.mBuyPing.setVisibility(View.VISIBLE);
		} else if (mTradeMode == Trade_Define.TRADE_MODE_PUTONG) {
			this.mBuyOpen.setVisibility(View.VISIBLE);
			this.mSellPing.setVisibility(View.VISIBLE);
			this.mSellOpen.setVisibility(View.GONE);
			this.mBuyPing.setVisibility(View.GONE);
		} else {
			this.mBuyOpen.setVisibility(View.VISIBLE);
			this.mSellPing.setVisibility(View.VISIBLE);
			this.mSellOpen.setVisibility(View.VISIBLE);
			this.mBuyPing.setVisibility(View.VISIBLE);
		}
	}
	
	//设置备兑选中框复位，以及下单按钮复位
	public void setInitBDStatus()
	{
		if(this.mBDFlag)
		{
			mMyApp.mIndexOfOrderBtn = Btn_WT_All;
			mBuyOpen.setEnabled(true);
			mBuyPing.setEnabled(true);
			mSellOpen.setEnabled(true);
			mSellPing.setEnabled(true);
			mCB_BeiDui.setChecked(false);
			this.mBDFlag = false;
		}
	}

	// 选择合约后设置初始价格和数量
	public void setInitPriceAndVolume() {
		// 设成“对手价”
		setPriceEditContent(MyKeyboardWindow.sKjbjTypes[0]);
		updateOrderPriceBtn();

		mEditTradeAmount.setText("1");
		mbUpdateFromSelect = false;
	}

	// 设置价格edit的字符
	public void setPriceEditContent(String content) {
		if (mCurrentKJBJType == MyKeyboardWindow.KJBJ_TYPES_SZ) {
			int kjbjType = STD.IsHave(MyKeyboardWindow.sKjbjTypesSZ, content);
			if (!content.isEmpty() && kjbjType >= 0) {

				mSellWTPriceMode = MyKeyboardWindow.sKjbjModeSZ[kjbjType];
			} else {
				mSellWTPriceMode = Trade_Define.WTPRICEMODE_INPUT;
			}
		} else {
			int kjbjType = STD.IsHave(MyKeyboardWindow.sKjbjTypes, content);
			if (!content.isEmpty() && kjbjType >= 0) {

				mSellWTPriceMode = MyKeyboardWindow.sKjbjModeSH[kjbjType];
			} else {
				mSellWTPriceMode = Trade_Define.WTPRICEMODE_INPUT;
			}
		}

		mEditTradePrice.setText(content);
	}

	// 获取price edit的内容
	private String getPriceEditContent(char mmlb) {
		String strPrice = "";

		if (mSellWTPriceMode != Trade_Define.WTPRICEMODE_INPUT) {
			switch (mSellWTPriceMode) {
			case Trade_Define.WTPRICEMODE_DSJ:// 对手价
				if (mmlb == PTK_Define.PTK_D_Sell) {
					strPrice = ViewTools.getStringByFieldID(mOptionData,
							Global_Define.FIELD_HQ_BUYPRICE);
				} else {
					strPrice = ViewTools.getStringByFieldID(mOptionData,
							Global_Define.FIELD_HQ_SELLPRICE);
				}
				break;

			case Trade_Define.WTPRICEMODE_NOW:// 最新价
				strPrice = ViewTools.getStringByFieldID(mOptionData,
						Global_Define.FIELD_HQ_NOW);
				break;

			case Trade_Define.WTPRICEMODE_GDJ:// 挂单价
				if (mmlb == PTK_Define.PTK_D_Sell) {
					strPrice = ViewTools.getStringByFieldID(mOptionData,
							Global_Define.FIELD_HQ_SELLPRICE);
				} else {
					strPrice = ViewTools.getStringByFieldID(mOptionData,
							Global_Define.FIELD_HQ_BUYPRICE);
				}
				break;

			case Trade_Define.WTPRICEMODE_UP:// 涨停价
				strPrice = ViewTools.getStringByFieldID(mOptionData,
						Global_Define.FIELD_HQ_UPPRICE);
				break;

			case Trade_Define.WTPRICEMODE_DOWN:// 跌停价
				strPrice = ViewTools.getStringByFieldID(mOptionData,
						Global_Define.FIELD_HQ_DOWNPRICE);
				break;

			case Trade_Define.WTPRICEMODE_SJSYZXJ:// 市价剩余转限价
				break;

			case Trade_Define.WTPRICEMODE_SJSYCX:// 市价剩余撤销
				break;

			case Trade_Define.WTPRICEMODE_QBCJHCX:// 全额成交或撤销
				break;

			case Trade_Define.WTPIRCEMODE_DSFZY_SZ:// 对手方最优
				break;
			case Trade_Define.WTPIRCEMODE_BFZY_SZ:// 本方最优
				break;
			case Trade_Define.WTPIRCEMODE_JSCJSYCX_SZ:// 即时成交剩余撤销
				break;
			case Trade_Define.WTPIRCEMODE_ZYWDJSCJ_SZ:// 最优五档即时成交
				break;
			case Trade_Define.WTPIRCEMODE_QBCJHCX_SZ:// 全额成交或撤销
				break;
			default:
				break;
			}
		} else {
			strPrice = mEditTradePrice.getText().toString();
		}

		return strPrice;
	}

	private void showFOK(boolean bShow) {
		if (bShow) {
			mImg_fok.setVisibility(View.VISIBLE);
		} else {
			mImg_fok.setVisibility(View.GONE);
		}
	}

	private void refreshDWBZJ()// 单位保证金
	{
		mDWBZJ = 0;
		mMinPriceStep = 0.0001f;

		if (mOptionData == null)
			return;

		String marketCode = TradeData.GetTradeMarketFromHQMarket(
				mOptionData.HQData.market, mOptionData.group);

		int nCount = MyApp.getInstance().mTradeData.m_stepOptionList
				.GetRecNum();
		if (nCount > 0) {
			for (int i = 0; i < nCount; i++) {
				MyApp.getInstance().mTradeData.m_stepOptionList.GotoRecNo(i);

				String strMarket = MyApp.getInstance().mTradeData.m_stepOptionList
						.GetFieldValueString(STEP_Define.STEP_SCDM);
				String strCode = MyApp.getInstance().mTradeData.m_stepOptionList
						.GetFieldValueString(STEP_Define.STEP_HYDM);

				if (strCode.equalsIgnoreCase(mOptionData.HQData.code)
						&& strMarket.equalsIgnoreCase(marketCode)) {

					String strDWBZJ = MyApp.getInstance().mTradeData.m_stepOptionList
							.GetFieldValueString(STEP_Define.STEP_DWBZJ);
					mDWBZJ = STD.StringToValue(strDWBZJ);

					String strMinPrice = MyApp.getInstance().mTradeData.m_stepOptionList
							.GetFieldValueString(STEP_Define.STEP_ZXBDJW);
					mMinPriceStep = STD.StringToValue(strMinPrice);
					String format = String.format("%%.%df", mPriceDotLen);
					mTV_delete_one_price.setText(String.format(format,
							mMinPriceStep));
					mTV_add_one_price.setText(String.format(format,
							mMinPriceStep));
					break;
				}
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
				if (mEditTradePrice.getText().length() == 0
						|| mSellWTPriceMode != Trade_Define.WTPRICEMODE_INPUT) {
					mEditTradePrice.setText(input);
				} else if (input != null) {
					String strTmp = mEditTradePrice.getText().toString();
					strTmp += input;
					mEditTradePrice.setText(strTmp);
				}
				mSellWTPriceMode = Trade_Define.WTPRICEMODE_INPUT;
				updateOrderPriceBtn();
				PopupInfo();
				// startRequestKMSLTimer(2000);
			}
				break;
			case R.id.btn_digit_dian: {
				String input = ".";
				if (mEditTradePrice.getText().length() == 0
						|| mSellWTPriceMode != Trade_Define.WTPRICEMODE_INPUT) {
					mEditTradePrice.setText(input);
				} else if (input != null) {
					String strTmp = mEditTradePrice.getText().toString();
					strTmp += input;
					mEditTradePrice.setText(strTmp);
				}
				mSellWTPriceMode = Trade_Define.WTPRICEMODE_INPUT;
				updateOrderPriceBtn();
				PopupInfo();
				// startRequestKMSLTimer(2000);
			}
				break;

			case R.id.btn_digit_delete: {
				if (mSellWTPriceMode != Trade_Define.WTPRICEMODE_INPUT) {
					mEditTradePrice.setText("");
				} else if (mEditTradePrice.getText().length() > 0) {
					String strTmp = mEditTradePrice.getText().toString();
					strTmp = strTmp.substring(0, strTmp.length() - 1);
					mEditTradePrice.setText(strTmp);
				}
				updateOrderPriceBtn();
				PopupInfo();
				// startRequestKMSLTimer(2000);
			}
				break;
			case R.id.order_fuwei: {
				if (mImg_fuwei.getVisibility() == View.VISIBLE) {
					mImg_fuwei.setVisibility(View.GONE);
					// 设成“对手价”
					setPriceEditContent(MyKeyboardWindow.sKjbjTypes[0]);
					updateOrderPriceBtn();
					PopupInfo();
				}
			}
				break;

			case R.id.key_fok: {
				if (mbFok) {
					mbFok = false;
					ImageView img = (ImageView) v;
					img.setImageResource(R.drawable.order_fox02);
				} else {
					mbFok = true;
					ImageView img = (ImageView) v;
					img.setImageResource(R.drawable.order_fox03);
				}
				showFOK(mbFok);
				mKeyboard.setKeyFOK(mbFok);
			}
				break;

			case R.id.key_btn_confirm: {
				int index = mKeyboard.getKeyboardLayerIndex();
				if (index == MyKeyboardWindow.KEYBOARD_LAYER_KJBJ) {
					mSellWTPriceMode = mKeyboard.mSellWTPriceMode;
					if (mSellWTPriceMode != Trade_Define.WTPRICEMODE_INPUT) {
						String input = mKeyboard.mCurrentKJBJContent;
						setPriceEditContent(input);
					}
				}

				updateOrderPriceBtn();
				mKeyboard.dismiss();
				PopupInfo();
			}
				break;

			default:
				break;
			}
		}

	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case request_code_trend:

			break;
		default:
			break;
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_trendview:// 点击从上单只弹出走势
			//
			break;
		case R.id.choose_option:// 点击选择期权跳转到 期权列表 包含全部和 自选
			Intent intent = new Intent();
			intent.setClass(mActivity, TradeHeadOptionListActivity.class);

			startActivity(intent);
			break;

		case R.id.left_back_relativelayout:// 点击切换到上一个期权
		{
			if (mCurrentOptionIndex <= 0) {
				mBtnPreOption.setEnabled(false);
				mBtnPreOption.setClickable(false);

				mImg_PreOption.setEnabled(false);
			} else {
				mBtnPreOption.setEnabled(true);
				mBtnPreOption.setClickable(true);
				mBtnNextOption.setEnabled(true);
				mBtnNextOption.setClickable(true);

				mImg_PreOption.setEnabled(true);
				mImg_NextOption.setEnabled(true);
				gotoPreOrNextOption(true);
			}
		}
			break;

		case R.id.right_next_relativelayout:// 点击切换到下一个期权
		{
			if (mCurrentOptionIndex >= mOptionCodeInfoList.size() - 1) {
				mBtnNextOption.setEnabled(false);
				mBtnNextOption.setClickable(false);

				mImg_NextOption.setEnabled(false);
			} else {
				mBtnPreOption.setEnabled(true);
				mBtnPreOption.setClickable(true);
				mBtnNextOption.setEnabled(true);
				mBtnNextOption.setClickable(true);

				mImg_PreOption.setEnabled(true);
				mImg_NextOption.setEnabled(true);
				gotoPreOrNextOption(false);
			}
		}
			break;
		case R.id.btn_delete_one: // price minus
		{
			if (mOptionData == null) {
				return;
			}
			String price = "";
			if (mSellWTPriceMode != Trade_Define.WTPRICEMODE_INPUT) {
				price = getPriceEditContent(PTK_Define.PTK_D_Buy);
				if (price.isEmpty()) {
					return;
				}
			} else {
				price = mEditTradePrice.getText().toString();
			}
			mEditTradePrice.setText(ViewTools.getPriceByStep(price,
					mMinPriceStep, false, mPriceDotLen));
			mSellWTPriceMode = Trade_Define.WTPRICEMODE_INPUT;
			this.updateOrderPriceBtn();
			PopupInfo();
			// startRequestKMSLTimer(2000);
		}
			break;
		case R.id.btn_add_one: // price add
		{
			if (mOptionData == null) {
				return;
			}
			String price = "";
			if (mSellWTPriceMode != Trade_Define.WTPRICEMODE_INPUT) {
				price = getPriceEditContent(PTK_Define.PTK_D_Buy);
				if (price.isEmpty()) {
					return;
				}
			} else {
				price = mEditTradePrice.getText().toString();
			}
			mEditTradePrice.setText(ViewTools.getPriceByStep(price,
					mMinPriceStep, true, mPriceDotLen));
			mSellWTPriceMode = Trade_Define.WTPRICEMODE_INPUT;
			this.updateOrderPriceBtn();
			PopupInfo();
			// startRequestKMSLTimer(2000);
		}
			break;

		case R.id.btn_delete_two: // amount minus
		{
			if (mOptionData == null) {
				return;
			}
			String str = mEditTradeAmount.getText().toString();
			if (str.length() > 0) {
				int value = 0;
				try {
					value = Integer.parseInt(str);
				} catch (Exception e) {
					value = (int) Double.parseDouble(str);
				}

				if (value < 0) {
					mEditTradeAmount.setText("0");
					return;
				}
				value -= mAmountChangeNum;
				if (value < 0)
					value = 0;

				mEditTradeAmount.setText(String.valueOf(value));
			}
			this.PopupInfo();
		}
			break;
		case R.id.btn_add_two: // amount add
		{
			if (mOptionData == null) {
				return;
			}

			String str = mEditTradeAmount.getText().toString();
			if (str.length() > 0) {
				int value = 0;
				try {
					value = Integer.parseInt(str);
				} catch (Exception e) {
					value = (int) Double.parseDouble(str);
				}

				value += mAmountChangeNum;
				mEditTradeAmount.setText(String.valueOf(value));
			}
			this.PopupInfo();
		}
			break;

		case R.id.order_buy_in_open:// 买入开仓
		{
			requestWTClick(Btn_WT_BuyOpen);
		}
			break;
		case R.id.order_sell_out_ping:// 卖出平仓
		{
			requestWTClick(Btn_WT_SellClose);
		}
			break;
		case R.id.order_sell_out_open:// 卖出开仓
		{
			requestWTClick(Btn_WT_SellOpen);
		}
			break;
		case R.id.order_buy_in_ping:// 买入平仓
		{
			requestWTClick(Btn_WT_BuyClose);
		}
			break;

		case R.id.order_shuaxin:
			if (mListener != null) {
				mListener.requestDRWT();
			}
			break;

		default:
			break;

		}
	}

	private void requestWTClick(int btnIndex) {
		if (mOptionData == null)
			return;

		if(!this.mBDFlag || mMyApp.mIndexOfOrderBtn != Btn_WT_SellOpen)
		{
			mMyApp.mIndexOfOrderBtn = Btn_WT_All;
			mBuyOpen.setEnabled(true);
			mBuyPing.setEnabled(true);
			mSellOpen.setEnabled(true);
			mSellPing.setEnabled(true);
		}
		
		String price = mEditTradePrice.getText().toString();
		if (price.isEmpty()) {
			new com.pengbo.mhdcx.widget.AlertDialog(getActivity().getParent())
					.builder().setTitle("提示").setMsg("请输入委托价格")
					.setCancelable(false).setCanceledOnTouchOutside(false)
					.setPositiveButton("确认", new OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					}).show();
			return;
		}

		String wtsl = mEditTradeAmount.getText().toString();
		if (wtsl.isEmpty()) {
			new com.pengbo.mhdcx.widget.AlertDialog(getActivity().getParent())
					.builder().setTitle("提示").setMsg("请输入委托数量")
					.setCancelable(false).setCanceledOnTouchOutside(false)
					.setPositiveButton("确认", new OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					}).show();
			return;
		}

		String mmlbStr = "";
		if (btnIndex == Btn_WT_BuyOpen) {
			mmlbStr = "确认买入开仓";
			mMMLB = PTK_Define.PTK_D_Buy;
			mKPBZ = PTK_Define.PTK_OF_Open;

		} else if (btnIndex == Btn_WT_SellClose) {
			mmlbStr = "确认卖出平仓";
			mMMLB = PTK_Define.PTK_D_Sell;
			mKPBZ = PTK_Define.PTK_OF_Close;
		} else if (btnIndex == Btn_WT_SellOpen) {
			mmlbStr = "确认卖出开仓";
			mMMLB = PTK_Define.PTK_D_Sell;
			mKPBZ = PTK_Define.PTK_OF_Open;
		} else if (btnIndex == Btn_WT_BuyClose) {
			mmlbStr = "确认买入平仓";
			mMMLB = PTK_Define.PTK_D_Buy;
			mKPBZ = PTK_Define.PTK_OF_Close;
		}

		price = this.getPriceEditContent(mMMLB);
		mWTPrice = price;

		if (mSellWTPriceMode == Trade_Define.WTPRICEMODE_SJSYZXJ) {// 市价剩余转限价
			price = "市价剩余转限价";
		} else if (mSellWTPriceMode == Trade_Define.WTPRICEMODE_SJSYCX) // 市价剩余撤销
		{
			price = "市价剩余撤销";
		} else if (mSellWTPriceMode == Trade_Define.WTPRICEMODE_QBCJHCX) // 全额成交或撤销
		{
			price = "全额成交或撤销";
		} else if (mSellWTPriceMode == Trade_Define.WTPIRCEMODE_DSFZY_SZ) // 对手方最优
		{
			price = "对手方最优";
		} else if (mSellWTPriceMode == Trade_Define.WTPIRCEMODE_BFZY_SZ) // 本方最优
		{
			price = "本方最优";
		} else if (mSellWTPriceMode == Trade_Define.WTPIRCEMODE_JSCJSYCX_SZ) // 即时成交剩余撤销
		{
			price = "即时成交剩余撤销";
		} else if (mSellWTPriceMode == Trade_Define.WTPIRCEMODE_ZYWDJSCJ_SZ) // 最优五档即时成交
		{
			price = "最优五档即时成交";
		} else if (mSellWTPriceMode == Trade_Define.WTPIRCEMODE_QBCJHCX_SZ) // 全额成交或撤销
		{
			price = "全额成交或撤销";// 市价FOK,即市价全部成交否则全部撤单
		} else {
			if (mbFok) {
				price = String.format("限价%s FOK", price);
			} else {
				price = String.format("限价%s", price);
			}
		}
		
		if (this.mBDFlag)
		{
			price += " 备兑";
		}

		String strBzj = null;
		if (btnIndex == Btn_WT_SellOpen && !this.mBDFlag) {
			this.refreshDWBZJ();
			float bzj = mDWBZJ * STD.StringToValue(wtsl);
			strBzj = String.format("%.2f", bzj);
		} else {
			strBzj = null;
		}

		if (PreferenceEngine.getInstance().getOrderWithoutConfirm()) {
			requestWT();
		} else {
			int nDays = ViewTools
					.getDaysDruationFromToday(mOptionData.optionData.StrikeDate);

			if (mWTConfirmDialog != null) {
				mWTConfirmDialog.dismiss();
			} else {
				mWTConfirmDialog = new com.pengbo.mhdcx.widget.AlertDialog(
						getActivity().getParent()).builder();
			}

			mWTConfirmDialog.clear();

			if (nDays >= 0
					&& nDays <= 7
					&& (btnIndex == Btn_WT_BuyOpen || btnIndex == Btn_WT_SellOpen)) {
				mWTConfirmDialog
						.setTitle("委托确认")
						.setMsg1(String.format("该合约剩余%d天到期！", nDays),
								ColorConstant.COLOR_ALL_RED)
						.setOptionInfo(mOptionData.name,
								mOptionData.optionData.code, price, wtsl)
						.setOptionBZJ(strBzj).setCancelable(false)
						.setCanceledOnTouchOutside(false)
						.setPositiveButton(mmlbStr, new OnClickListener() {
							@Override
							public void onClick(View v) {
								requestWT();
							}
						}).setNegativeButton("取消", new OnClickListener() {
							@Override
							public void onClick(View v) {

							}
						}).show();
			} else {
				mWTConfirmDialog
						.setTitle("委托确认")
						.setOptionInfo(mOptionData.name,
								mOptionData.optionData.code, price, wtsl)
						.setOptionBZJ(strBzj).setCancelable(false)
						.setCanceledOnTouchOutside(false)
						.setPositiveButton(mmlbStr, new OnClickListener() {
							@Override
							public void onClick(View v) {
								requestWT();
							}
						}).setNegativeButton("取消", new OnClickListener() {
							@Override
							public void onClick(View v) {

							}
						}).show();
			}
		}
	}

	private void requestWT() {
		String marketCode = TradeData.GetTradeMarketFromHQMarket(
				mOptionData.HQData.market, mOptionData.group);
		String gdzh = MyApp.getInstance().mTradeData
				.GetGDZHFromMarket(marketCode);
		String xwh = MyApp.getInstance().mTradeData
				.GetXWHFromMarket(marketCode);

		mOrderRecord.mMMLB = mMMLB;
		mOrderRecord.mKPBZ = mKPBZ;
		mOrderRecord.mWTPrice = mWTPrice;
		mOrderRecord.mWTSL = mEditTradeAmount.getText().toString();
		mOrderRecord.mMarketCode = marketCode;
		mOrderRecord.mGDZH = gdzh;
		mOrderRecord.mXWH = xwh;
		mOrderRecord.mStockCode = mOptionData.HQData.code;
		mOrderRecord.mSJType = getSJType();
		if(this.mBDFlag)
		{
			mOrderRecord.mBDFlag = 1;
		}else
		{
			mOrderRecord.mBDFlag = 0;
		}

		if (mListener != null) {
			mListener.requestWT(mOrderRecord);
		}
		setInitPriceAndVolume();// 下单后重置委托价格和数量
		setInitBDStatus();
		
	}

	private char getSJType() {
		char sjType = '0';
		if (mSellWTPriceMode == Trade_Define.WTPRICEMODE_SJSYZXJ) // 市价剩余转限价
		{
			sjType = PTK_Define.PTK_QQ_OPT_FAL;
		} else if (mSellWTPriceMode == Trade_Define.WTPRICEMODE_SJSYCX) // 市价剩余撤销
		{
			sjType = PTK_Define.PTK_QQ_OPT_FAK;
		} else if (mSellWTPriceMode == Trade_Define.WTPRICEMODE_QBCJHCX) // 全额成交或撤销
		{
			sjType = PTK_Define.PTK_QQ_OPT_FOK;// 市价FOK,即市价全部成交否则全部撤单
		} else if (mSellWTPriceMode == Trade_Define.WTPIRCEMODE_DSFZY_SZ) // 对手方最优
		{
			sjType = PTK_Define.PTK_QQ_OPT_DBestPrice;
		} else if (mSellWTPriceMode == Trade_Define.WTPIRCEMODE_BFZY_SZ) // 本方最优
		{
			sjType = PTK_Define.PTK_QQ_OPT_WBestPrice;
		} else if (mSellWTPriceMode == Trade_Define.WTPIRCEMODE_JSCJSYCX_SZ) // 即时成交剩余撤销
		{
			sjType = PTK_Define.PTK_QQ_OPT_FAK_SZ;
		} else if (mSellWTPriceMode == Trade_Define.WTPIRCEMODE_ZYWDJSCJ_SZ) // 最优五档即时成交
		{
			sjType = PTK_Define.PTK_QQ_OPT_5FAK_SZ;
		} else if (mSellWTPriceMode == Trade_Define.WTPIRCEMODE_QBCJHCX_SZ) // 全额成交或撤销
		{
			sjType = PTK_Define.PTK_QQ_OPT_FOK_SZ;// 市价FOK,即市价全部成交否则全部撤单
		} else if (mbFok == true) {
			sjType = PTK_Define.PTK_QQ_OPT_FOK_XJ;// 限价FOK,即全部成交否则全部撤单
		}
		return sjType;
	}

	// 根据设置获取全部开仓和快捷反手的委托价格
	// (int)priceType->委托价格设置 对应(对手价0，最新价1，挂单价2，涨停价3，跌停价4)
	private String getPriceBySettings(int priceType,
			TagLocalStockData optionData, char mmlb) {
		String strPrice = "";
		switch (priceType) {
		case 0:// 对手价
			if (mmlb == PTK_Define.PTK_D_Sell) {
				strPrice = ViewTools.getStringByFieldID(optionData,
						Global_Define.FIELD_HQ_BUYPRICE);
			} else {
				strPrice = ViewTools.getStringByFieldID(optionData,
						Global_Define.FIELD_HQ_SELLPRICE);
			}
			break;
		case 1:// 最新价
			strPrice = ViewTools.getStringByFieldID(optionData,
					Global_Define.FIELD_HQ_NOW);
			break;

		case 2:// 挂单价
			if (mmlb == PTK_Define.PTK_D_Sell) {
				strPrice = ViewTools.getStringByFieldID(optionData,
						Global_Define.FIELD_HQ_SELLPRICE);
			} else {
				strPrice = ViewTools.getStringByFieldID(optionData,
						Global_Define.FIELD_HQ_BUYPRICE);
			}
			break;
		case 3:// 涨停价
			strPrice = ViewTools.getStringByFieldID(optionData,
					Global_Define.FIELD_HQ_UPPRICE);
			break;
		case 4:// 跌停价
			strPrice = ViewTools.getStringByFieldID(optionData,
					Global_Define.FIELD_HQ_DOWNPRICE);
			break;
		case Trade_Define.WTPRICEMODE_QBCJHCX:// 全额成交或撤销-上海
		case Trade_Define.WTPIRCEMODE_QBCJHCX_SZ:// 全额成交或撤销-深圳
			strPrice = "全额成交或撤销";
			break;
		default:
			break;
		}

		return strPrice;
	}

	class ClickEvent implements OnClickListener {

		@Override
		public void onClick(View v) {

			if (v == mBtnTrend) {
				if (mOptionData == null) {
					return;
				}
				mListener.requestHQTrendLine(mOptionData, mStockData);
				showDialog(getActivity().getParent());
			}
		}

	}

	private void showDialog(Context context) {

		if (mTrendDialog == null) {
			int h = mActivity.findViewById(R.id.head_navigation_tab)
					.getHeight();
			mTrendDialog = new com.pengbo.mhdcx.widget.OrderTrendDialog(
					context, 0, h).builder();
		}

		mTrendDialog.updateOptionData(mOptionData, mStockData);
		mTrendDialog.setCancelable(false).setCanceledOnTouchOutside(false);
		mTrendDialog.show();
	}

	public void changeToChedanView() {
		((RadioButton) mRgChiCangWT.findViewById(R.id.radiobutton_weituo))
				.setChecked(true);
	}

	private void changeView(int index) {
		switch (index) {
		case 0:
			if (mViewSwitcherIndex != index) {
				mViewSwitcherIndex = index;
				mlLayout_listHeader.setVisibility(View.VISIBLE);
				mListView_CC.setVisibility(View.VISIBLE);
				mListView_CD.setVisibility(View.GONE);
				this.updateChiCang(true);
				// this.updateChiCangView();
			}
			break;
		case 1:
			if (mViewSwitcherIndex != index) {
				mViewSwitcherIndex = index;
				mlLayout_listHeader.setVisibility(View.GONE);
				mListView_CC.setVisibility(View.GONE);
				mListView_CD.setVisibility(View.VISIBLE);
				if (mListener != null) {
					mListener.requestDRWT();
				}
				this.updateDRWT_CD(true);
			}
			break;
		}
	}
	
	CompoundButton.OnCheckedChangeListener BDCheckListener = new CompoundButton.OnCheckedChangeListener(){ 
        @Override 
        public void onCheckedChanged(CompoundButton buttonView, 
                boolean isChecked) {
            if(isChecked){
            	mBDFlag = true;
            	mMyApp.mIndexOfOrderBtn = Btn_WT_SellOpen;
            	if(isOptionHaveBDInHold())
            	{
            		mBuyPing.setEnabled(true);
        			mSellOpen.setEnabled(true);
            	}else
            	{
            		mBuyPing.setEnabled(false);
        			mSellOpen.setEnabled(true);
            	}
    			mBuyOpen.setEnabled(false);
    			mSellPing.setEnabled(false); 
            }else{
            	mBDFlag = false;
            	if(mMyApp.mIndexOfOrderBtn == Btn_WT_All || mMyApp.mIndexOfOrderBtn == Btn_WT_SellOpen)
            	{
            		mMyApp.mIndexOfOrderBtn = Btn_WT_All;
            		mBuyOpen.setEnabled(true);
        			mBuyPing.setEnabled(true);
        			mSellOpen.setEnabled(true);
        			mSellPing.setEnabled(true);
            	}
            } 
        } 
    };

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radiobutton_cicang:
			changeView(VIEW_CHICANG);
			break;
		case R.id.radiobutton_weituo:
			changeView(VIEW_WT);
			break;

		default:
			break;
		}
	}

	@Override
	public void onResume() {
		if (!this.isHidden()) {
			int num = PreferenceEngine.getInstance().getTradeOrderIncreaseNum();
			mAmountChangeNum = num;

			if (mMyApp.mIndexOfOrderBtn == Btn_WT_All) {
				mBuyOpen.setEnabled(true);
				mBuyPing.setEnabled(true);
				mSellOpen.setEnabled(true);
				mSellPing.setEnabled(true);
			} else if (mMyApp.mIndexOfOrderBtn == Btn_WT_BuyOpen) {
				mBuyOpen.setEnabled(true);
				mBuyPing.setEnabled(false);
				mSellOpen.setEnabled(false);
				mSellPing.setEnabled(false);
				this.mBDFlag = false;
			} else if (mMyApp.mIndexOfOrderBtn == Btn_WT_SellOpen) {
				mBuyOpen.setEnabled(false);
				mBuyPing.setEnabled(false);
				mSellOpen.setEnabled(true);
				mSellPing.setEnabled(false);
			} else if (mMyApp.mIndexOfOrderBtn == Btn_WT_BuyClose) {
				mBuyOpen.setEnabled(false);
				mBuyPing.setEnabled(true);
				mSellOpen.setEnabled(false);
				mSellPing.setEnabled(false);
				this.mBDFlag = false;
			} else if (mMyApp.mIndexOfOrderBtn == Btn_WT_SellClose) {
				mBuyOpen.setEnabled(false);
				mBuyPing.setEnabled(false);
				mSellOpen.setEnabled(false);
				mSellPing.setEnabled(true);
				this.mBDFlag = false;
			} else {
				mBuyOpen.setEnabled(true);
				mBuyPing.setEnabled(true);
				mSellOpen.setEnabled(true);
				mSellPing.setEnabled(true);
				this.mBDFlag = false;
			}
			
			mTradeMode = PreferenceEngine.getInstance().getTradeMode();

			this.updateSellBuyBtns();
			updateAllData();
			refreshDWBZJ();
			updateAllView();
			requestHQPushData();
		} else {
			if (mMoneyWindow != null) {
				mMoneyWindow.dismiss();
			}
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mMoneyWindow != null) {
			mMoneyWindow.dismiss();
		}
		mMyApp.mIndexOfOrderBtn = Btn_WT_All;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mMyApp.mIndexOfOrderBtn = Btn_WT_All;
		if (mMoneyWindow != null) {
			mMoneyWindow.dismiss();
		}
		if (mKeyboard != null)
		{
			mKeyboard.dismiss();
		}
		if (mWTConfirmDialog != null)
		{
			mWTConfirmDialog.dismiss();
		}
		if (mTrendDialog != null)
		{
			mTrendDialog.dismiss();
		}
		hideSoftInputMethod(mEditTradeAmount);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		if (!hidden) {
			int num = PreferenceEngine.getInstance().getTradeOrderIncreaseNum();
			mAmountChangeNum = num;
			if (mMyApp.mIndexOfOrderBtn == Btn_WT_All) {
				mBuyOpen.setEnabled(true);
				mBuyPing.setEnabled(true);
				mSellOpen.setEnabled(true);
				mSellPing.setEnabled(true);
			} else if (mMyApp.mIndexOfOrderBtn == Btn_WT_BuyOpen) {
				mBuyOpen.setEnabled(true);
				mBuyPing.setEnabled(false);
				mSellOpen.setEnabled(false);
				mSellPing.setEnabled(false);
				this.mBDFlag = false;
			} else if (mMyApp.mIndexOfOrderBtn == Btn_WT_SellOpen) {
				mBuyOpen.setEnabled(false);
				mBuyPing.setEnabled(false);
				mSellOpen.setEnabled(true);
				mSellPing.setEnabled(false);
			} else if (mMyApp.mIndexOfOrderBtn == Btn_WT_BuyClose) {
				mBuyOpen.setEnabled(false);
				mBuyPing.setEnabled(true);
				mSellOpen.setEnabled(false);
				mSellPing.setEnabled(false);
				this.mBDFlag = false;
			} else if (mMyApp.mIndexOfOrderBtn == Btn_WT_SellClose) {
				mBuyOpen.setEnabled(false);
				mBuyPing.setEnabled(false);
				mSellOpen.setEnabled(false);
				mSellPing.setEnabled(true);
				this.mBDFlag = false;
			} else {
				mBuyOpen.setEnabled(true);
				mBuyPing.setEnabled(true);
				mSellOpen.setEnabled(true);
				mSellPing.setEnabled(true);
				this.mBDFlag = false;
			}

			mTradeMode = PreferenceEngine.getInstance().getTradeMode();
			this.updateSellBuyBtns();

			updateAllData();
			refreshDWBZJ();
			updateAllView();
			requestHQPushData();

		} else {
			if (mMoneyWindow != null) {
				mMoneyWindow.dismiss();
			}
			if (mKeyboard != null)
			{
				mKeyboard.dismiss();
			}
			if (mWTConfirmDialog != null)
			{
				mWTConfirmDialog.dismiss();
			}
			if (mTrendDialog != null)
			{
				mTrendDialog.dismiss();
			}
			hideSoftInputMethod(mEditTradeAmount);
			if(!this.mBDFlag)
			{
				mMyApp.mIndexOfOrderBtn = Btn_WT_All;
			}
		}
		super.onHiddenChanged(hidden);
	}

	private void startHideMoneyTimer(long timer) {

		stopHideMoneyTimer();

		{
			mTimerMoney = new Timer();
			mTimerMoney.schedule(new TimerTask() {
				//
				public void run() {
					Message message = new Message();
					message.what = 1;
					mHandler.sendMessage(message);

				}
			}, timer);
		}
	}

	private void stopHideMoneyTimer() {
		if (mTimerMoney != null) {
			mTimerMoney.cancel();
		}
		mTimerMoney = null;
	}

	private void startKJFSCDTimer()// "快捷反手"开仓撤单
	{
		stopKJFSCDTimer();

		long time = PreferenceEngine.getInstance().getTradeKJFSAutoCDTime();

		if (time <= 10) {
			time = 10;
		}
		mTimerKJFS = new Timer();
		mTimerKJFS.schedule(new TimerTask() {
			//
			public void run() {
				mTimerKJFS.cancel();
				
				mListener.setKJFSRuning(false);
				if (mTradeWTBHArray.size() <= 0) {
					return;
				}
				if (mMyApp.mTradeData.mTradeLoginFlag) {

					if (mTradeRecordQBPC.mWTZT.equals("1")) {
						for(int m = 0; m < mTradeWTBHArray.size(); m++)
			            {
			                String tempWtbh = mTradeWTBHArray.get(m);
			                mListener.requestWTCD(mTradeRecordQBPC, tempWtbh);
			                try {
								Thread.sleep(mMyApp.mTradeData.m_wtIntervalTime);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
			            }
						mListener.setKJFSRuning(false);
					}
				}
			}
		}, time * 1000, time * 1000);
	}

	private void stopKJFSCDTimer() {
		if (mTimerKJFS != null) {
			mTimerKJFS.cancel();
		}
		mTimerKJFS = null;
	}

	private void requestHQPushData() {
		if (mOptionCodeInfo != null && !mOptionCodeInfo.code.isEmpty()) {
			ArrayList<TagCodeInfo> codelist = new ArrayList<TagCodeInfo>();
			codelist.add(mOptionCodeInfo);
			// 最新设计部显示标的信息，所以标的推送也取消
			// TagCodeInfo aStockCodeInfo = new TagCodeInfo(
			// mOptionData.optionData.StockMarket,
			// mOptionData.optionData.StockCode, mOptionData.group,
			// mOptionCodeInfo.name);
			// codelist.add(aStockCodeInfo);
			mListener.requestHQPushData(codelist);
		} else {
			mListener.requestHQPushData(null);
		}
	}

	private void proc_MSG_ADAPTER_KJFS_BUTTON_CLICK(Message msg) {

		if(!this.mBDFlag || mMyApp.mIndexOfOrderBtn != Btn_WT_SellOpen)
		{
			mMyApp.mIndexOfOrderBtn = Btn_WT_All;
			mBuyOpen.setEnabled(true);
			mBuyPing.setEnabled(true);
			mSellOpen.setEnabled(true);
			mSellPing.setEnabled(true);
		}

		final int pos = msg.arg1;
		L.i(TAG, "pos: " + pos);
		if (mListData_CC != null) {
			int num = mListData_CC.GetRecNum();
			if (pos < num) {
				String strTitle = "";
				mListData_CC.GotoRecNo(pos);

				char chBDBZ = mListData_CC.GetFieldValueCHAR(STEP_Define.STEP_BDBZ);
				boolean bBD = (chBDBZ == PTK_Define.PTK_D_BD ? true : false);
				
				float fMMBZ = STD.StringToValue(mListData_CC
						.GetFieldValueString(STEP_Define.STEP_MMLB));
				boolean bBuy = (fMMBZ == 0f ? true : false);
				boolean isKJFSEnabled = true;
				
				if(bBD)
				{
					strTitle = "备兑仓";
					isKJFSEnabled = false;
				}else
				{
					if (bBuy) {
						strTitle = "权利仓";
					} else {
						strTitle = "义务仓";
					}
					isKJFSEnabled = true;
				}

				final String optionName = mListData_CC
						.GetFieldValueString(STEP_Define.STEP_HYDMMC);
				String junPrice = mListData_CC
						.GetFieldValueString(STEP_Define.STEP_MRJJ);
				
				String code = mListData_CC.GetFieldValueString(STEP_Define.STEP_HYDM);
				String market = mListData_CC.GetFieldValueString(STEP_Define.STEP_SCDM);
				String keyongNumTemp = mListData_CC.GetFieldValueString(STEP_Define.STEP_KYSL);
				if (keyongNumTemp.equalsIgnoreCase("-99999999"))
				{
					int nBDBZ = mListData_CC.GetFieldValueInt(STEP_Define.STEP_BDBZ);
	                int nDJSL = MyApp.getInstance().mTradeData.GetDJSL(code, market, bBuy,nBDBZ);
	                int nCCSL = mListData_CC.GetFieldValueInt(STEP_Define.STEP_DQSL);
	                int nKYSL = nCCSL-nDJSL;
	                if (nKYSL < 0) {
	                    nKYSL = 0;
	                }
	                keyongNumTemp = STD.IntToString(nKYSL);
				}
				final String keyongNum = keyongNumTemp;

				String subTitle = String.format("%s        均价：%s    %s张",
						optionName, junPrice, keyongNum);
				
				if(mActionSheetDlg != null)
				{
					mActionSheetDlg.dismiss();
				}else
				{
					mActionSheetDlg = new ActionSheetDialog(getActivity().getParent()).builder();
				}

				mActionSheetDlg.clear();
				mActionSheetDlg.setTitle(strTitle)
						.setSubTitle(subTitle)
						.setCancelable(false)
						.setCanceledOnTouchOutside(false)
						.addSheetItem("全部平仓", SheetItemColor.Blue,
								new OnSheetItemClickListener() {
									@Override
									public void onClick(int which) {
										// 全部平仓
										mTradeRecordQBPC.clear();

										mListData_CC.GotoRecNo(pos);
										String code = mListData_CC
												.GetFieldValueString(STEP_Define.STEP_HYDM);
										String market = mListData_CC
												.GetFieldValueString(STEP_Define.STEP_SCDM);
										mTradeRecordQBPC.mMarketCode = market;
										mTradeRecordQBPC.mStockCode = code;

										char szmmlb = mListData_CC
												.GetFieldValueCHAR(STEP_Define.STEP_MMLB);
										String mmlbStr = "";
										if (szmmlb == PTK_Define.PTK_D_Buy) {
											// 该持仓是买入，所以平仓即是卖出平仓
											mmlbStr = "卖出平仓";
											mTradeRecordQBPC.mMMLB = PTK_Define.PTK_D_Sell;
										} else {
											// 该持仓是卖出，所以平仓即是买入平仓
											mmlbStr = "买入平仓";
											mTradeRecordQBPC.mMMLB = PTK_Define.PTK_D_Buy;
											
											char chBDBZ = mListData_CC.GetFieldValueCHAR(STEP_Define.STEP_BDBZ);
											boolean bBD = (chBDBZ == PTK_Define.PTK_D_BD ? true : false);
											if (bBD)
											{
												mTradeRecordQBPC.mBDFlag =  1;
											}else
											{
												mTradeRecordQBPC.mBDFlag =  0;
											}
										}

										int nMarket = TradeData
												.GetHQMarketFromTradeMarket(market);

										TagLocalStockData optionData = new TagLocalStockData();
										mMyApp.mHQData.getData(optionData,
												(short) nMarket, code, false);

										int priceType = PreferenceEngine
												.getInstance()
												.getTradeAllPingCangPrice();
										String price = getPriceBySettings(
												priceType, optionData,
												mTradeRecordQBPC.mMMLB);
										
										if (price.equalsIgnoreCase("全额成交或撤销"))
										{
											mTradeRecordQBPC.mWTPrice = "";
							                if (nMarket == HQ_Define.MARKET_SHQQ || nMarket == HQ_Define.MARKET_SHQQFZ) {
							                	mTradeRecordQBPC.mSJType = PTK_Define.PTK_QQ_OPT_FOK;;
							                }
							                else if (nMarket == HQ_Define.MARKET_SZQQ || nMarket == HQ_Define.MARKET_SZQQFZ) {
							                	mTradeRecordQBPC.mSJType = PTK_Define.PTK_QQ_OPT_FOK_SZ;
							                }
							                else
							                {
							                	mTradeRecordQBPC.mSJType = PTK_Define.PTK_QQ_OPT_FOK;
							                }
										}else
										{
											mTradeRecordQBPC.mWTPrice = price;
											mTradeRecordQBPC.mSJType = PTK_Define.PTK_OPT_LimitPrice;
										}

										
										mTradeRecordQBPC.mWTSL = keyongNum;
										mTradeRecordQBPC.mKPBZ = PTK_Define.PTK_OF_Close;

										if(mTradeRecordQBPC.mBDFlag == 1)//备兑
										{
											price += " 备兑";
										}
										//refreshDWBZJ();

										String gdzh = mMyApp.mTradeData
												.GetGDZHFromMarket(market);
										String xwh = mMyApp.mTradeData
												.GetXWHFromMarket(market);
										mTradeRecordQBPC.mGDZH = gdzh;
										mTradeRecordQBPC.mXWH = xwh;

										if (PreferenceEngine.getInstance()
												.getOrderWithoutConfirm()) {
											//
											mListener.requestWTWithFlag(
													mTradeRecordQBPC, true);
										} else {

											if (mWTConfirmDialog != null) {
												mWTConfirmDialog.dismiss();
											} else {
												mWTConfirmDialog = new com.pengbo.mhdcx.widget.AlertDialog(
														getActivity()
																.getParent())
														.builder();
											}

											mWTConfirmDialog.clear();
											mWTConfirmDialog
													.setTitle("委托确认")
													.setOptionInfo(
															optionName,
															mTradeRecordQBPC.mStockCode,
															price,
															mTradeRecordQBPC.mWTSL)
													.setOptionJYLX(mmlbStr)
													.setCancelable(false)
													.setCanceledOnTouchOutside(
															false)
													.setPositiveButton(
															"确认全部平仓",
															new OnClickListener() {
																@Override
																public void onClick(
																		View v) {
																	mListener
																			.requestWTWithFlag(
																					mTradeRecordQBPC,
																					true);
																	setInitBDStatus();
																}
															})
													.setNegativeButton(
															"取消",
															new OnClickListener() {
																@Override
																public void onClick(
																		View v) {

																}
															}).show();
										}
									}// end of onClick QBPC

								}, true)
						.addSheetItem("快捷反手", (isKJFSEnabled ? SheetItemColor.Blue:SheetItemColor.Gray),
								new OnSheetItemClickListener() {

									@Override
									public void onClick(int which) {
										// 快捷反手
										mTradeRecordQBPC.clear();// 平仓
										mTradeRecordKJFS.clear();// 再开仓

										mListData_CC.GotoRecNo(pos);
										String code = mListData_CC
												.GetFieldValueString(STEP_Define.STEP_HYDM);
										String market = mListData_CC
												.GetFieldValueString(STEP_Define.STEP_SCDM);
										mTradeRecordQBPC.mMarketCode = mTradeRecordKJFS.mMarketCode = market;
										mTradeRecordQBPC.mStockCode = mTradeRecordKJFS.mStockCode = code;

										char szmmlb = mListData_CC
												.GetFieldValueCHAR(STEP_Define.STEP_MMLB);
										String mmlbStr1 = ""; // 平仓类别（买平或卖平）
										if (szmmlb == PTK_Define.PTK_D_Buy) {
											// 该持仓是买入，所以平仓即是卖出平仓
											mmlbStr1 = "卖出平仓";
											mTradeRecordQBPC.mMMLB = PTK_Define.PTK_D_Sell;
										} else {
											// 该持仓是卖出，所以平仓即是买入平仓
											mmlbStr1 = "买入平仓";
											mTradeRecordQBPC.mMMLB = PTK_Define.PTK_D_Buy;
										}
										String mmlbStr2 = "";
										if (mTradeRecordQBPC.mMMLB == PTK_Define.PTK_D_Buy)// 先平仓是买平
										{
											mmlbStr2 = "买入开仓";
											mTradeRecordKJFS.mMMLB = PTK_Define.PTK_D_Buy;
										} else if (mTradeRecordQBPC.mMMLB == PTK_Define.PTK_D_Sell)// 先平仓是卖平
										{
											mmlbStr2 = "卖出开仓";
											mTradeRecordKJFS.mMMLB = PTK_Define.PTK_D_Sell;
										}

										int nMarket = TradeData
												.GetHQMarketFromTradeMarket(market);

										TagLocalStockData optionData = new TagLocalStockData();
										mMyApp.mHQData.getData(optionData,
												(short) nMarket, code, false);

										int priceType = PreferenceEngine
												.getInstance()
												.getTradeKJFSWTPrice();
										String pricePC = getPriceBySettings(
												priceType, optionData,
												mTradeRecordQBPC.mMMLB);

										String priceKC = getPriceBySettings(
												priceType, optionData,
												mTradeRecordKJFS.mMMLB);
										
										if (pricePC.equalsIgnoreCase("全额成交或撤销"))
										{
											mTradeRecordQBPC.mWTPrice = "";
											mTradeRecordKJFS.mWTPrice = "";
							                if (nMarket == HQ_Define.MARKET_SHQQ || nMarket == HQ_Define.MARKET_SHQQFZ) {
							                	mTradeRecordQBPC.mSJType = PTK_Define.PTK_QQ_OPT_FOK;
												mTradeRecordKJFS.mSJType = PTK_Define.PTK_QQ_OPT_FOK;
							                }
							                else if (nMarket == HQ_Define.MARKET_SZQQ || nMarket == HQ_Define.MARKET_SZQQFZ) {
							                	mTradeRecordQBPC.mSJType = PTK_Define.PTK_QQ_OPT_FOK_SZ;
												mTradeRecordKJFS.mSJType = PTK_Define.PTK_QQ_OPT_FOK_SZ;
							                }
							                else
							                {
							                	mTradeRecordQBPC.mSJType = PTK_Define.PTK_QQ_OPT_FOK;
												mTradeRecordKJFS.mSJType = PTK_Define.PTK_QQ_OPT_FOK;
							                }
										}else
										{
											mTradeRecordQBPC.mWTPrice = pricePC;
											mTradeRecordKJFS.mWTPrice = priceKC;
											mTradeRecordQBPC.mSJType = PTK_Define.PTK_OPT_LimitPrice;
											mTradeRecordKJFS.mSJType = PTK_Define.PTK_OPT_LimitPrice;
										}

										mTradeRecordQBPC.mWTSL = keyongNum;
										mTradeRecordKJFS.mWTSL = keyongNum;

										mTradeRecordQBPC.mKPBZ = PTK_Define.PTK_OF_Close;
										mTradeRecordKJFS.mKPBZ = PTK_Define.PTK_OF_Open;

										//refreshDWBZJ();

										String gdzh = mMyApp.mTradeData
												.GetGDZHFromMarket(market);
										String xwh = mMyApp.mTradeData
												.GetXWHFromMarket(market);
										mTradeRecordQBPC.mGDZH = gdzh;
										mTradeRecordQBPC.mXWH = xwh;
										
										mTradeRecordKJFS.mGDZH = gdzh;
										mTradeRecordKJFS.mXWH = xwh;
										
										if (mWTConfirmDialog != null) {
											mWTConfirmDialog.dismiss();
										} else {
											mWTConfirmDialog = new com.pengbo.mhdcx.widget.AlertDialog(
													getActivity().getParent())
													.builder();
										}

										mWTConfirmDialog.clear();

										mWTConfirmDialog
												.setTitle("委托确认")
												.setOptionInfo(
														optionName,
														mTradeRecordQBPC.mStockCode,
														pricePC,
														mTradeRecordQBPC.mWTSL)
												.setOptionJYLX(mmlbStr1)
												.setKJFS()
												.setOptionInfo_fs(
														optionName,
														mTradeRecordKJFS.mStockCode,
														priceKC,
														mTradeRecordKJFS.mWTSL)
												.setOptionJYLX_fs(mmlbStr2)
												.setCancelable(false)
												.setCanceledOnTouchOutside(
														false)
												.setPositiveButton("确认快捷反手",
														new OnClickListener() {
															@Override
															public void onClick(
																	View v) {
																mTradeWTBHArray.clear();
																mListener
																		.requestWTWithFlag(
																				mTradeRecordQBPC,
																				false);
															}
														})
												.setNegativeButton("取消",
														new OnClickListener() {
															@Override
															public void onClick(
																	View v) {

															}
														}).show();
									}// end of onClick KJFS

								}, isKJFSEnabled).show();
			}
		}

	}

	// 开始快捷反手,等待recordQBPC平仓成功后进行recordKC开仓的动作，如果N秒(默认十秒)后平仓没有成功，则撤单，不再继续
	public void startKJFS(TradeLocalRecord recordKC, TradeLocalRecord recordQBPC) {
		if (mTradeRecordQBPC == null || mTradeRecordKJFS == null) {
			L.e(TAG, "ERROR:startKJFS Record=null");
			return;
		}
		mTradeRecordQBPC.mWTZT = "1";

		startKJFSCDTimer();
	}

	public boolean refreshPCRecordStatus() {
//		if (mTradeRecordQBPC.mWTBH.isEmpty()) {
//			return false;
//		}
		if(this.mTradeWTBHArray.size() == 0)
		{
			return false;
		}
		boolean bRet = false;
		if (mTradeRecordQBPC.mWTZT.equals("2")) {
			return true;
		} else if (!mTradeRecordQBPC.mWTZT.equals("1")) {
			return false;
		}
		PBSTEP drwtList = new PBSTEP();
		mMyApp.mTradeData.GetDRWT(drwtList);
		
		for (int i = 0; i < drwtList.GetRecNum(); i++) {

			drwtList.GotoRecNo(i);
			String wtbh = drwtList.GetFieldValueString(STEP_Define.STEP_WTBH);
			String code = drwtList.GetFieldValueString(STEP_Define.STEP_HYDM);
			for(int j = 0; j < mTradeWTBHArray.size(); j++)
			{
				String tempWTBH = mTradeWTBHArray.get(j);
				if (wtbh.equals(tempWTBH) && code.equals(mTradeRecordQBPC.mStockCode))
				{
					String wtzt = drwtList.GetFieldValueString(STEP_Define.STEP_WTZT);
					if (DataTools.isTradeSucceed(wtzt) == true) {
						mTradeWTBHArray.remove(j);
	                    if (mTradeWTBHArray.size() == 0) {
	                    	mTradeRecordQBPC.mWTZT = "2";
	                    	stopKJFSCDTimer();
	                        bRet = true;
	                        L.i(TAG, "refreshPCRecordStatus: KJFS PC succeed");
	                        return bRet;
	                    }
	                    break;
						
					} else if (DataTools.isCDStatusEnabled(wtzt) == false) {
						mTradeRecordQBPC.mWTZT = "3";
						stopKJFSCDTimer();
						if(mListener != null)
						{
							mListener.setKJFSRuning(false);
						}
						bRet = false;
						L.i(TAG, "refreshPCRecordStatus: KJFS PC Cancel or Error");
					} else {
						mTradeRecordQBPC.mWTZT = "1";
					}
					break;
				}
			}//for(int j = 0; j < mTradeWTBHArray.size(); j++)
		}//for (int i = 0; i < drwtList.GetRecNum(); i++)
		
		return bRet;
	}
	
	//判断当前选中合约是否在持仓里是备兑仓 true-持仓中有备兑仓false-无备兑仓
	private boolean isOptionHaveBDInHold()
	{
		if(mListData_CC == null || mOptionData == null)
			return false;
		
		for(int i = 0; i < mListData_CC.GetRecNum(); i++)
		{
			mListData_CC.GotoRecNo(i);

			String code = mListData_CC.GetFieldValueString(STEP_Define.STEP_HYDM);
			String market = mListData_CC.GetFieldValueString(STEP_Define.STEP_SCDM);
			int nMarket = TradeData.GetHQMarketFromTradeMarket(market);
			if (mOptionData.code.equalsIgnoreCase(code) && mOptionData.HQData.market == nMarket)
			{
				char chBDBZ = mListData_CC.GetFieldValueCHAR(STEP_Define.STEP_BDBZ);
				boolean bBD = (chBDBZ == PTK_Define.PTK_D_BD ? true : false);
				if (bBD)
				{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (mViewSwitcherIndex == VIEW_CHICANG) {
			if (this.mListData_CC != null) {
				int num = this.mListData_CC.GetRecNum();
				if (position < num) {

					TagLocalStockData optionData = new TagLocalStockData();
					mListData_CC.GotoRecNo(position);

					String code = mListData_CC
							.GetFieldValueString(STEP_Define.STEP_HYDM);
					String market = mListData_CC
							.GetFieldValueString(STEP_Define.STEP_SCDM);
					int nMarket = TradeData.GetHQMarketFromTradeMarket(market);
					mMyApp.mHQData.getData(optionData, (short) nMarket, code,
							false);

					float fMMBZ = STD.StringToValue(mListData_CC.GetFieldValueString(STEP_Define.STEP_MMLB));
					boolean bBuy = (fMMBZ == 0f ? true : false);
					String keyongNumTemp = mListData_CC.GetFieldValueString(STEP_Define.STEP_KYSL);
					if (keyongNumTemp.equalsIgnoreCase("-99999999"))
					{
						int nBDBZ = mListData_CC.GetFieldValueInt(STEP_Define.STEP_BDBZ);
		                int nDJSL = MyApp.getInstance().mTradeData.GetDJSL(code, market, bBuy,nBDBZ);
		                int nCCSL = mListData_CC.GetFieldValueInt(STEP_Define.STEP_DQSL);
		                int nKYSL = nCCSL-nDJSL;
		                if (nKYSL < 0) {
		                    nKYSL = 0;
		                }
		                keyongNumTemp = STD.IntToString(nKYSL);
					}
					final String keyongNum = keyongNumTemp;
					
					TagCodeInfo optionCodeInfo = new TagCodeInfo();
					optionCodeInfo.code = optionData.code;
					optionCodeInfo.market = optionData.market;
					optionCodeInfo.group = optionData.group;
					optionCodeInfo.name = optionData.name;
					mMyApp.setCurrentOption(optionCodeInfo);
					mMyApp.mIndexOfOrderBtn = Btn_WT_All;
					mBuyOpen.setEnabled(true);
					mBuyPing.setEnabled(true);
					mSellOpen.setEnabled(true);
					mSellPing.setEnabled(true);

					if (PreferenceEngine.getInstance().getTradeMode() == 0) {
						if (!bBuy)// 义务仓并且是普通交易模式，弹提示
						{
							new AlertDialog(getActivity().getParent())
									.builder()
									.setTitle(
											getActivity().getString(
													R.string.IDS_TiShi))
									.setMsg(getActivity().getString(
											R.string.IDS_YiWuCangTiShi))
									.setCancelable(false)
									.setCanceledOnTouchOutside(false)
									.setPositiveButton(
											getActivity().getString(
													R.string.IDS_ZhiDaoLe),
											new OnClickListener() {
												@Override
												public void onClick(View v) {
													updateOptionData(false);
													updateHQView(keyongNum,
															true);
													requestHQPushData();
													PopupInfo();
												}
											}).show();
							return;
						}
					}

					updateOptionData(false);
					updateHQView(keyongNum, true);
					requestHQPushData();
					PopupInfo();
				}
			}
		}
	}

	public void PopupInfo() {
		if (mOptionData == null) {
			return;
		}
		double money = 0.0;
		String wtsl = mEditTradeAmount.getText().toString();
		int nNum = STD.StringToInt(wtsl);
		String price = this.getPriceEditContent(PTK_Define.PTK_D_Buy);
		money = STD.StringToDouble(price) * nNum * mOptionData.optionData.StrikeUnit;

		if (money <= 0) {
			if (mMoneyWindow != null) {
				mMoneyWindow.dismiss();
			}
			return;
		}
		showPopupWindow(mRLayout_EditPrice, mEditTradeAmount, money);
		this.startHideMoneyTimer(3000);
	}

	private void showPopupWindow(View mLayout, View mLayout1, double money) {

		if (mMoneyWindow != null && mMoneyWindow.isShowing()) {
			TextView mCancel = (TextView) mMoneyWindow.getContentView()
					.findViewById(R.id.ordermoney_popinfo_field0);
			mCancel.setText(String.format("%.2f", money));

		} else {
			LayoutInflater inflater = (LayoutInflater) mActivity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view_window = inflater.inflate(R.layout.pop_ordermoney, null);

			TextView mCancel = (TextView) view_window
					.findViewById(R.id.ordermoney_popinfo_field0);
			mCancel.setText(String.format("%.2f", money));

			mMoneyWindow = new PopupWindow(view_window);
			// 设置 window 的layout 以及其宽高
			int width = mLayout.getWidth();
			int height = mLayout.getHeight();// 条目的高
			mMoneyWindow.setWidth(width);

			mMoneyWindow.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.moneybg));
			mMoneyWindow.setFocusable(false);// 设置焦距 让点其他条目或后退键时条目消失

			// 设置window弹出来的位置
			int[] location = new int[2];
			// 获取一个空间在手机屏幕上的位置
			mLayout.getLocationOnScreen(location);
			int x = location[0];
			int y = location[1] + height;

			mLayout1.getLocationOnScreen(location);
			mMoneyWindow.setHeight(location[1] - y + 10);

			mMoneyWindow.showAtLocation(mLayout, Gravity.LEFT | Gravity.TOP, x,
					y);
		}

	}

}
