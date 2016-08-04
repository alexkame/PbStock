package com.pengbo.mhdzq.fragment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pengbo.mhdcx.view.MyKeyboardWindow;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.FastSearchAdapter;
import com.pengbo.mhdzq.adapter.ZqCCAdapter;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.CCodeTableItem;
import com.pengbo.mhdzq.data.CDataCodeTable;
import com.pengbo.mhdzq.data.CMarketInfoItem.mktGroupInfo;
import com.pengbo.mhdzq.data.CPbGDZHData;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.data.SearchDataItem;
import com.pengbo.mhdzq.data.TagLocalStockData;
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
import com.pengbo.mhdzq.trade.data.Trade_Define;
import com.pengbo.mhdzq.view.ZQ_Trade_Ctrl_Trend_FivePrice;
import com.pengbo.mhdzq.view.ZqShijiaWindow;
import com.pengbo.mhdzq.view.ZqXdCodeCCKeyBoard;
import com.pengbo.mhdzq.view.ZqXdCodeDigitKeyBoard;
import com.pengbo.mhdzq.view.ZqXdCodePriceKeyBoard;
import com.pengbo.mhdzq.view.ZqXdCodeZmKeyBoard;
import com.pengbo.mhdzq.widget.ZqGdPopWindow;
import com.pengbo.mhdzq.zq_trade_activity.OnTradeFragmentListener;

public class TradeZqBuyFragment extends Fragment implements OnClickListener {

	protected MyApp mMyApp = null;
	private Activity mActivity = null;
	private String TAG = TradeZqBuyFragment.class.getSimpleName();

	public View mView;

	private AutoCompleteTextView mZqCodeEdit;
	private TextView mZqCodeText;
	protected EditText mZQPrice;
	protected TextView mZqGudongText;
	private TextView mUpLimit;
	private TextView mDownLimit;
	protected EditText mZQBuyAmount;
	protected TextView mZQKMAmount;
	private TextView mUpMinUnit;
	private TextView mDownMinUnit;
	private LinearLayout mPriceAdd, mPriceReduce, mHqcodelayout;
	private RelativeLayout mXdLayout;
	protected Button mZqBuyBtn;

	protected com.pengbo.mhdzq.widget.AlertDialog mWTConfirmDialog;

	private ZqXdCodeDigitKeyBoard mKeyboard;
	private ZqXdCodeZmKeyBoard mKeyZmBorad;
	private ZqXdCodeCCKeyBoard mKeyCCBoard;
	private ZqXdCodePriceKeyBoard mKeyPriceBoard;
	private ZqShijiaWindow mKeyShijia;

	private ScrollView mScrollView;

	private RelativeLayout mZqGudongOption;

	private ZQ_Trade_Ctrl_Trend_FivePrice mFivePriceView;

	private boolean mIsViewReady = false;
	private boolean mIsKMSLneedUpdated = true;

	protected OnTradeFragmentListener mListener;

	private ArrayList<SearchDataItem> mHistorySearchList; // 历史搜索结果
	private ArrayList<SearchDataItem> mSearchResultList; // 搜索结果
	private ArrayList<SearchDataItem> mSearchStockList;// 被搜索的所有合约

	private ArrayList<CPbGDZHData> mGdList;

	private ListView mCCListView;
	private ZqCCAdapter mChiCangAdapterCC;

	protected TagLocalStockData mOptionData = null;// 合约
	private TagCodeInfo mOptionCodeInfo = null;// 合约

	protected TradeLocalRecord mOrderRecord;
	private int mCurrentKJBJType = MyKeyboardWindow.KJBJ_TYPES_SH;

	private float mMinPriceStep = 0.01f; // 最小变动价位
	private int mPriceDotLen = 2;

	protected char mMMLB = PTK_Define.PTK_D_Buy;
	private char mKPBZ;

	public boolean mbFok = false;

	protected String mWTPrice;
	protected String mCurrentMaket = "";

	private int mSellWTPriceMode = -1;

	private ZqGdPopWindow mPpview;

	protected PBSTEP mListData_CC;// 持仓

	private Filter mHYFilter;

	private int mZqCodeThreshold = 3;

	private static final int MLOCALMSG1 = 10001;

	private boolean mIsScrolled = false;

	protected int mKMSL = 0;

	public Handler mActivitHander;
	public boolean mIsNeedUpdate = false;

	class TradeTextWatcher implements TextWatcher {

		private EditText mEditText;
		private TextView mTextView;

		public TradeTextWatcher(EditText edit, TextView text) {
			mEditText = edit;
			mTextView = text;
		}

		@Override
		public void afterTextChanged(Editable arg0) {

			int len = mEditText.getText().length();

			mEditText.setSelection(len);
			if (len == 0) {
				if (mTextView != null) {
					mTextView.setText("");
				}
			}

			if (mEditText == mZqCodeEdit) {
				if (len == 0) {

				}
			}

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {

		}

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

			// Editable editable = mEditText.getText();
			// int len = editable.length();
			//
			// int selEndIndex = Selection.getSelectionEnd(editable);
			// Selection.setSelection(editable, selEndIndex);

		}

	};

	public Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// process incoming messages here
			switch (msg.what) {
			case TradeNetConnect.MSG_ADAPTER_KJFS_BUTTON_CLICK: {
				// proc_MSG_ADAPTER_KJFS_BUTTON_CLICK(msg);
			}
				break;
			case TradeNetConnect.MSG_ADAPTER_CC_SELECT_CLICK: {
				// proc_MSG_ADAPTER_CC_SELECT_CLICK(msg);
			}
				break;
			case TradeNetConnect.MSG_ADAPTER_CD_BUTTON_CLICK: {
				// proc_MSG_ADAPTER_CD_BUTTON_CLICK(msg);
			}
				break;

			case TradeNetConnect.MSG_UPDATE_DATA: {

				int pos = msg.arg1;

				mListData_CC.GotoRecNo(pos);

				String jycode = mListData_CC
						.GetFieldValueString(STEP_Define.STEP_HYDM);

				String hqcode = mMyApp.mTradeData
						.GetHQCodeFromTradeCode(jycode);

				String market = mListData_CC
						.GetFieldValueString(STEP_Define.STEP_SCDM);
				int nMarket = TradeData.GetHQMarketFromTradeMarket(market);

				boolean bNeedUpdate = false;
				if (mOptionData == null) {
					bNeedUpdate = true;
				} else {
					// if (!mOptionData.HQData.code.equals(hqcode)
					// || mOptionData.HQData.market != nMarket) {
					// bNeedUpdate = true;
					// }
					bNeedUpdate = true;
				}
				if (bNeedUpdate) {

					String optionName = "";
					SearchDataItem item;

					for (int i = 0; i < mMyApp.mSearchCodeArray.size(); i++) {
						item = mMyApp.mSearchCodeArray.get(i);
						if (item.market == nMarket
								&& item.code.endsWith(hqcode)) {
							optionName = item.name;

							mOptionCodeInfo = new TagCodeInfo();
							mOptionCodeInfo.code = item.code;
							mOptionCodeInfo.market = item.market;
							mOptionCodeInfo.group = item.group;
							mOptionCodeInfo.name = item.name;

							break;
						}
					}

					setHYCode(hqcode, optionName);

					// mMyApp.setCurrentOption(codeInfo);
					setStockData(mOptionCodeInfo);

					TradeZqBuyFragment.this.updateHQView("", true);
				}

				break;
			}
			case TradeZqBuyFragment.MLOCALMSG1:
				Handler handler = new Handler();

				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						showSoftInputMethod(mZqCodeEdit);
					}
				}, 500);

			}

			super.handleMessage(msg);
		}

	};

	public static TradeZqBuyFragment newInstance() {
		TradeZqBuyFragment f = new TradeZqBuyFragment();
		return f;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (mListener != null) {
			mListener.requestHoldStock();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.d(TAG, "onCreate");
		initData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.zq_trade_buy_frame, null);

		initView();
		initWuDangView();

		initCodeKeyBoard();

		mIsViewReady = true;

		return mView;
	}

	private void setHYCode(String code, String name) {
		mZqCodeEdit.setThreshold(100);
		mZqCodeEdit.setText(code);
		mZqCodeText.setText(name);

		mZqCodeEdit.setThreshold(mZqCodeThreshold);

		mZQPrice.setText("");
		mZQBuyAmount.setText("");

		updateKMSLView("0", true);
	}

	public void updateHQView(String keyongNum, boolean isFromSelect) {
		if (!mIsViewReady)
			return;

		if (mOptionData != null && isFromSelect) {

			setHYCode(mOptionData.code, mOptionData.name);

			for (int i = 0; i < mGdList.size(); i++) {
				if (mGdList.get(i).mHqMarket == mOptionData.market) {
					mZqGudongText.setText(mGdList.get(i).mGdzh);
					break;
				}
			}
		}

		if (mOptionData != null) {
			float price = 0;
			updateWuDangView();
			updateUpDownLimit();
			updateMinUnitView(mOptionData.PriceDecimal);
			updatePriceView();
			price = ViewTools.getPriceByFieldNo(Global_Define.FIELD_HQ_NOW,
					mOptionData);

			if (mIsKMSLneedUpdated) {
				requestKMSL(String.valueOf(price), mOptionData);
			}
		}

	}

	protected void updatePriceView() {
		if (mZQPrice.length() == 0) {
			if (mOptionData.HQData.sellPrice.length > 0) {
				String price = ViewTools.getStringByPrice(
						mOptionData.HQData.sellPrice[0], 0,
						mOptionData.PriceDecimal, mOptionData.PriceRate);
				mZQPrice.setText(price);
			}
		}
	}

	private void updateWuDangView() {
		if (mFivePriceView != null) {
			mFivePriceView.updateData(mOptionData);
		} else {
		}
	}

	public void updateKMSLView(String kmsl, boolean init) {
		Float km = Float.valueOf(kmsl);

		mKMSL = km.intValue();

		double mount = mKMSL / 100;
		mount = Math.floor(mount / 1);
		mount = mount * 100;

		String kmtmp = ViewTools.getStringByVolume(
				new Double(mount).longValue(), 0, 1, 6, false);

		if (init == true) {
			mZQKMAmount.setText("可买" + "--" + "股");
			mIsKMSLneedUpdated = true;
		} else {
			mZQKMAmount.setText("可买" + kmtmp + "股");
			mIsKMSLneedUpdated = false;
		}
	}

	private void updateUpDownLimit() {

		if (mOptionData == null) {
			return;
		}
		float uplimit = ViewTools.getPriceByFieldNo(
				Global_Define.FIELD_HQ_UPPRICE, mOptionData);
		float downlimit = ViewTools.getPriceByFieldNo(
				Global_Define.FIELD_HQ_DOWNPRICE, mOptionData);

		mUpLimit.setText("涨停价 " + uplimit);
		mDownLimit.setText("跌停价" + downlimit);
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
	public void onResume() {

		L.d(TAG, "onResume");
		if (!this.isHidden()) {

			updateAllData();
			updateAllView();
			updateTradeData(false);
			requestHQPushData();

		}

		super.onResume();
		L.d(TAG, "onResume");
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		updateAllView();
		requestHQPushData();
	}

	public void updateAllView() {
		updateHQView("", true);
		// updateKMSLView(mKMSL);
	}

	private void initData() {

		if (mOrderRecord == null) {
			mOrderRecord = new TradeLocalRecord();
		}

		mSearchStockList = new ArrayList<SearchDataItem>();
		mSearchStockList = mMyApp.getSearchCodeArray();
		mSearchResultList = new ArrayList<SearchDataItem>();

		Bundle bundle;
		if ((bundle = getArguments()) != null) {
			mOptionCodeInfo = new TagCodeInfo();
			mOptionCodeInfo.market = bundle.getShort("STOCK_MARKET");
			mOptionCodeInfo.code = bundle.getString("STOCK_CODE");
		}

		initCCData();
		// initGDZH();
		getGDZH();
	}

	private void initCCData() {
		mListData_CC = new PBSTEP();
	}

	public void initGDZH() {

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mGdList = new ArrayList<CPbGDZHData>();
				int wait = 0;
				while (mGdList.size() <= 0) {
					ArrayList<String> gdzhshList = MyApp.getInstance().mTradeData
							.GetStockGDZHFromMarket(Trade_Define.ENum_MARKET_SHA);

					ArrayList<String> gdzhszList = MyApp.getInstance().mTradeData
							.GetStockGDZHFromMarket(Trade_Define.ENum_MARKET_SZA);
					final ArrayList<String> gdList = new ArrayList<String>();

					L.d(TAG, "" + gdzhshList.size());
					L.d(TAG, "" + gdzhszList.size());

					if (mGdList.size() <= 0) {
						if (gdzhshList.size() > 0) {

							for (int i = 0; i < gdzhshList.size(); i++) {
								mGdList.add(new CPbGDZHData(
										Trade_Define.ENum_MARKET_SHA,
										gdzhshList.get(i)));
							}
						}

						if (gdzhszList.size() > 0) {

							for (int i = 0; i < gdzhszList.size(); i++) {
								mGdList.add(new CPbGDZHData(
										Trade_Define.ENum_MARKET_SZA,
										gdzhszList.get(i)));
							}

						}
					}

					try {
						wait++;
						if (wait < 10) {
							Thread.sleep(1000);
						} else {
							break;
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

		thread.start();

	}

	private void getGDZH() {
		mGdList = new ArrayList<CPbGDZHData>();
		int wait = 0;

		ArrayList<String> gdzhshList = MyApp.getInstance().mTradeData
				.GetStockGDZHFromMarket(Trade_Define.ENum_MARKET_SHA);

		ArrayList<String> gdzhszList = MyApp.getInstance().mTradeData
				.GetStockGDZHFromMarket(Trade_Define.ENum_MARKET_SZA);
		final ArrayList<String> gdList = new ArrayList<String>();

		L.d(TAG, "" + gdzhshList.size());
		L.d(TAG, "" + gdzhszList.size());

		if (mGdList.size() <= 0) {
			if (gdzhshList.size() > 0) {

				for (int i = 0; i < gdzhshList.size(); i++) {
					mGdList.add(new CPbGDZHData(Trade_Define.ENum_MARKET_SHA,
							gdzhshList.get(i)));
				}
			}

			if (gdzhszList.size() > 0) {

				for (int i = 0; i < gdzhszList.size(); i++) {
					mGdList.add(new CPbGDZHData(Trade_Define.ENum_MARKET_SZA,
							gdzhszList.get(i)));
				}

			}
		}
	}

	private void initView() {

		mZqCodeEdit = (AutoCompleteTextView) mView
				.findViewById(R.id.zq_daima_edit);
		mZqCodeText = (TextView) mView.findViewById(R.id.zq_daima_text);

		mHqcodelayout = (LinearLayout) mView
				.findViewById(R.id.zq_xd_codetext_layout);

		mZqCodeEdit.setThreshold(mZqCodeThreshold);

		mZqGudongText = (TextView) mView.findViewById(R.id.zq_chose_gudong);

		mZqGudongOption = (RelativeLayout) mView
				.findViewById(R.id.zq_zh_choose_lay);

		mPriceAdd = (LinearLayout) mView.findViewById(R.id.zq_addprice);
		mPriceReduce = (LinearLayout) mView.findViewById(R.id.zq_reduceprice);

		mPriceAdd.setOnClickListener(this);
		mPriceReduce.setOnClickListener(this);

		mUpLimit = (TextView) mView.findViewById(R.id.zquplimit);
		mDownLimit = (TextView) mView.findViewById(R.id.zqdownlimit);

		mUpMinUnit = (TextView) mView.findViewById(R.id.zqupmin);
		mDownMinUnit = (TextView) mView.findViewById(R.id.zqdownmin);

		mZQPrice = (EditText) mView.findViewById(R.id.zq_price);

		mZQBuyAmount = (EditText) mView.findViewById(R.id.zq_mairu_amount);
		mZQKMAmount = (TextView) mView.findViewById(R.id.zq_km_amount);

		mZqBuyBtn = (Button) mView.findViewById(R.id.zq_mairu_btn);
		initBuyBtnView();

		mZqBuyBtn.setOnClickListener(this);

		FastSearchAdapter mAdapter = new FastSearchAdapter(mMyApp, mActivity,
				mSearchResultList, mSearchStockList);
		mZqCodeEdit.setAdapter(mAdapter);
		mZqCodeEdit.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				mZqCodeEdit.setDropDownWidth(mHqcodelayout.getWidth());
			}
		});

		mHYFilter = mAdapter.getFilter();

		mZqCodeEdit.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				SearchDataItem item = mSearchResultList.get(position);

				setHYCode(item.code, item.name);

				mOptionCodeInfo = new TagCodeInfo();
				mOptionCodeInfo.code = item.code;
				mOptionCodeInfo.market = item.market;
				mOptionCodeInfo.group = item.group;
				mOptionCodeInfo.name = item.name;

				updateOptionData(false);

			}
		});

		mZqGudongOption.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (mPpview == null) {
					mPpview = new ZqGdPopWindow(mMyApp, mActivity, mActivity
							.getLayoutInflater(), mGdList, mZqGudongOption,
							new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									// TODO Auto-generated method stub
									mZqGudongText.setText(mGdList.get(position).mGdzh);

									if (position == 0) {
										mCurrentMaket = Trade_Define.ENum_MARKET_SHA;
									} else if (position == 1) {
										mCurrentMaket = Trade_Define.ENum_MARKET_SZA;
									}
									if (mPpview != null) {
										mPpview.dismiss();
									}
								}
							});

				}
				// 制定自定义PopupWindow显示的位置
				mPpview.showAsDropDown(mZqGudongText, 0, -5);
			}
		});

		mCCListView = (ListView) mView.findViewById(R.id.zq_trade_cc_listview);

		mCCListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_UP) {
					mIsScrolled = false;
					if (mIsNeedUpdate) {
						Handler handler = new Handler();
						handler.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								mIsNeedUpdate = false;
								updateOptionData(true);
								updateHQView("", false);
								updateTradeData(false);
							}
						});
					}

				} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mIsScrolled = true;
				}
				return false;
			}
		});

		mCCListView.setOnScrollListener(new OnScrollListener() {

			private Handler mHandle;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {//
					// list停止滚动时加载图片
					// 加载数据
					if (mHandle != null) {
						mMyApp.setHQPushNetHandler(mHandle);
					}
				} else if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					mHandle = mMyApp.getHQPushNetHandler();
					mMyApp.setHQPushNetHandler(null);
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});

		mChiCangAdapterCC = new ZqCCAdapter(mListData_CC, getActivity(),
				mHandler);
		mCCListView.setAdapter(mChiCangAdapterCC);

		/*
		 * mScrollView = (ScrollView) mView.findViewById(R.id.order_scrollview);
		 * 
		 * mScrollView
		 * .setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
		 * mScrollView.setFocusable(true);
		 * mScrollView.setFocusableInTouchMode(true);
		 * 
		 * mScrollView.setOnTouchListener(new OnTouchListener() { private int
		 * lastY = 0; private int touchEventId = -9983761; private Handler
		 * mHandle;
		 * 
		 * Handler handler = new Handler() {
		 * 
		 * @Override public void handleMessage(Message msg) {
		 * super.handleMessage(msg); View scroller = (View) msg.obj; if
		 * (msg.what == touchEventId) { if (lastY == scroller.getScrollY()) {
		 * handleStop(scroller); } else {
		 * handler.sendMessageDelayed(handler.obtainMessage( touchEventId,
		 * scroller), 50); lastY = scroller.getScrollY(); } } } };
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) { if
		 * (event.getAction() == MotionEvent.ACTION_UP) {
		 * 
		 * if (mOptionData != null) { String tmpCode =
		 * mZqCodeEdit.getText().toString(); if
		 * (!tmpCode.equals(mOptionData.code)) { setHYCode(mOptionData.code,
		 * mOptionData.name); } }
		 * 
		 * handler.sendMessageDelayed( handler.obtainMessage(touchEventId, v),
		 * 50); } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
		 * mHandle = mMyApp.getHQPushNetHandler();
		 * mMyApp.setHQPushNetHandler(null); mIsScrolled = true; } //
		 * v.requestFocusFromTouch(); return false; }
		 * 
		 * private void handleStop(Object view) {
		 * 
		 * if (mHandle != null) { mMyApp.setHQPushNetHandler(mHandle); }
		 * mIsScrolled = false; // updateTradeData(false);
		 * 
		 * // Do Something } });
		 */

	}

	// 隐藏系统键盘
	public void hideSoftInputMethod(EditText editText) {

		mActivity.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// InputMethodManager imm = ((InputMethodManager) mActivity
		// .getSystemService(Context.INPUT_METHOD_SERVICE));
		//
		// if (imm.isActive()) {
		// imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		// }

		int currentVersion = android.os.Build.VERSION.SDK_INT;
		String methodName = null;
		if (currentVersion >= 16) {
			// 4.2
			methodName = "setShowSoftInputOnFocus";
		} else if (currentVersion >= 14) {
			// 4.0
			methodName = "setSoftInputShownOnFocus";
		}

		if (methodName == null) {
			editText.setInputType(InputType.TYPE_NULL);
		} else {
			Class<EditText> cls = EditText.class;
			Method setShowSoftInputOnFocus;
			try {
				setShowSoftInputOnFocus = cls.getMethod(methodName,
						boolean.class);
				setShowSoftInputOnFocus.setAccessible(true);
				setShowSoftInputOnFocus.invoke(editText, false);
			} catch (NoSuchMethodException e) {
				editText.setInputType(InputType.TYPE_NULL);
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void showSoftInputMethod(EditText editText) {

		Editable editable = editText.getText();
		int len = editable.length();

		editText.setSelection(len);

		editText.setInputType(InputType.TYPE_CLASS_TEXT);
		InputMethodManager imm = ((InputMethodManager) mActivity
				.getSystemService(Context.INPUT_METHOD_SERVICE));

		editText.requestFocus();

		imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED,
				new ResultReceiver(mHandler));
		// imm.showSoftInputFromInputMethod(editText.getWindowToken(),
		// InputMethodManager.SHOW_FORCED);

	}

	private void initCodeKeyBoard() {
		mZqCodeEdit.addTextChangedListener(new TradeTextWatcher(mZqCodeEdit,
				mZqCodeText));
		mZqCodeEdit.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_UP) {

					if (PreferenceEngine.getInstance().getQDZNJP()) {
						hideSoftInputMethod(mZqCodeEdit);
						if (mKeyboard == null) {
							mKeyboard = new ZqXdCodeDigitKeyBoard(mActivity,
									itemsOnClick, mZqCodeEdit);
							mKeyboard.setOutsideTouchable(true);
							mKeyboard.setFocusable(false);
							mKeyboard.showAtLocation(
									mView.findViewById(R.id.trade_xh_buy_frame),
									Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,
									0, 0);
						} else {
							mKeyboard.ResetKeyboard(mZqCodeEdit);
							mKeyboard.setOutsideTouchable(true);
							mKeyboard.setFocusable(false);
							mKeyboard.showAtLocation(
									mView.findViewById(R.id.trade_xh_buy_frame),
									Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,
									0, 0);

						}
					}
				}

				return false;
			}
		});

		mZqCodeText.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				mZqCodeEdit.dispatchTouchEvent(event);
				return true;
			}
		});

		mZQBuyAmount.addTextChangedListener(new TradeTextWatcher(mZQBuyAmount,
				null));
		mZQBuyAmount.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (PreferenceEngine.getInstance().getQDZNJP()) {
					if (event.getAction() == MotionEvent.ACTION_UP) {

						// mZQBuyAmount.setInputType(InputType.TYPE_NULL);
						hideSoftInputMethod(mZQBuyAmount);
						if (mKeyCCBoard == null) {
							mKeyCCBoard = new ZqXdCodeCCKeyBoard(mActivity,
									itemsOnClick, mZQBuyAmount);
							mKeyCCBoard.setOutsideTouchable(true);
							mKeyCCBoard.setFocusable(false);
							mKeyCCBoard.showAtLocation(
									mView.findViewById(R.id.trade_xh_buy_frame),
									Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,
									0, 0);
						} else {
							mKeyCCBoard.setOutsideTouchable(true);
							mKeyCCBoard.setFocusable(false);
							mKeyCCBoard.ResetKeyboard(mZQBuyAmount);
							mKeyCCBoard.showAtLocation(
									mView.findViewById(R.id.trade_xh_buy_frame),
									Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,
									0, 0);

						}
					}
				}

				return false;
			}
		});

		mZQPrice.addTextChangedListener(new TradeTextWatcher(mZQPrice, null));
		mZQPrice.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (PreferenceEngine.getInstance().getQDZNJP()) {
					if (event.getAction() == MotionEvent.ACTION_UP) {

						// mZQPrice.setInputType(InputType.TYPE_NULL);
						hideSoftInputMethod(mZQPrice);
						if (mKeyPriceBoard == null) {
							mKeyPriceBoard = new ZqXdCodePriceKeyBoard(
									mActivity, itemsOnClick, mZQPrice);
							mKeyPriceBoard.setOutsideTouchable(true);
							mKeyPriceBoard.setFocusable(false);

							mKeyPriceBoard.showAtLocation(
									mView.findViewById(R.id.trade_xh_buy_frame),
									Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,
									0, 0);
						} else {
							mKeyPriceBoard.ResetKeyboard(mZQPrice);
							mKeyPriceBoard.setOutsideTouchable(true);
							mKeyPriceBoard.setFocusable(false);
							mKeyPriceBoard.showAtLocation(
									mView.findViewById(R.id.trade_xh_buy_frame),
									Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,
									0, 0);

						}
					}
				}

				return false;
			}
		});

	}

	public void dissMissKeyboard() {
		if (mKeyboard != null) {
			mKeyboard.dismiss();
		}
		if (mKeyZmBorad != null) {
			mKeyZmBorad.dismiss();
		}

		if (mKeyCCBoard != null) {
			mKeyCCBoard.dismiss();
		}

		if (mKeyPriceBoard != null) {
			mKeyPriceBoard.dismiss();
		}
	}

	/**
	 * 下单界面 初始化 五档界面的控件 及设置值
	 */
	private void initWuDangView() {
		if (mFivePriceView == null) {
			mFivePriceView = (ZQ_Trade_Ctrl_Trend_FivePrice) mView
					.findViewById(R.id.zq_fiveprice_panel).findViewById(
							R.id.layout_fiveprice);
		}
	}

	protected void initBuyBtnView() {
		if (mZqBuyBtn != null)
			mZqBuyBtn.setBackgroundDrawable(this.getResources().getDrawable(
					R.drawable.zq_xd_buy_btn_selector));
		mZqBuyBtn.setText("买入");

		mZQBuyAmount.setHint("买入数量");
	}

	private void updateMinUnitView(short priceDec) {

		double div = Math.pow(10, priceDec);

		BigDecimal b1 = new BigDecimal("1");

		BigDecimal b2 = new BigDecimal(div);

		BigDecimal v = b1.divide(b2, priceDec, BigDecimal.ROUND_HALF_UP);

		mPriceDotLen = priceDec;

		mUpMinUnit.setText(String.valueOf(v.floatValue()));
		mDownMinUnit.setText(String.valueOf(v.floatValue()));

		mMinPriceStep = v.floatValue();
	}

	private void setStockData(TagCodeInfo codeInfo) {
		if (codeInfo == null)
			return;

		mOptionData = new TagLocalStockData();
		if (!mMyApp.mHQData_ZQ.getData(mOptionData, codeInfo.market,
				codeInfo.code, false)) {
			ArrayList<CCodeTableItem> codeTableList = null;
			for (int j = 0; j < mMyApp.mCodeTableMarketNum
					&& j < CDataCodeTable.MAX_SAVE_MARKET_COUNT; j++) {
				if (mOptionData.HQData.market == mMyApp.mCodeTable[j].mMarketId) {
					codeTableList = mMyApp.mCodeTable[j].mCodeTableList;
					break;
				}
			}

			if (codeTableList != null) {
				for (int m = 0; m < codeTableList.size(); m++) {
					CCodeTableItem item = codeTableList.get(m);
					if (mOptionData.HQData.market == item.market
							&& mOptionData.HQData.code
									.equalsIgnoreCase(item.code)) {
						mOptionData.PriceDecimal = item.PriceDecimal;
						mOptionData.PriceRate = item.PriceRate;
						mOptionData.VolUnit = item.VolUnit;
						mOptionData.Multiplier = item.Multiplier;
						mOptionData.name = item.name;
						mOptionData.code = item.code;
						mOptionData.market = item.market;
						mOptionData.groupCode = item.groupCode;
						mOptionData.group = item.group;
						mOptionData.extCode = item.extCode;
						mOptionData.GroupOffset = item.GroupOffset;
						mOptionData.GroupFlag = item.GroupFlag;

						break;
					}
				}
			}

			mktGroupInfo groupRecord;
			groupRecord = mMyApp.mMarketInfo.searchMarketGroupInfo(
					mOptionData.HQData.market, null,
					(short) mOptionData.GroupOffset);
			if (groupRecord != null) {
				mOptionData.TradeFields = groupRecord.TradeFields;
				STD.memcpy(mOptionData.Start, groupRecord.Start, 4);
				STD.memcpy(mOptionData.End, groupRecord.End, 4);
				mOptionData.GroupFlag = groupRecord.Flag;
				// L.e("MyApp"," GroupInfoData marketId="+
				// aStockData.HQData.market + ",TradeFields="+
				// aStockData.TradeFields + ",Start[0]=" + aStockData.Start[0] +
				// ",End[0]=" + aStockData.End[0]);
			} else {
				L.e("MyApp", "ERROR: MarketInfo.search failed" + ",marketId="
						+ mOptionData.HQData.market + ",code="
						+ mOptionData.HQData.code);
			}

			mMyApp.mHQData_ZQ.addRecord(mOptionData, true);
		}

		// ArrayList<TagCodeInfo> codeList = new ArrayList<TagCodeInfo>();
		// codeList.add(codeInfo);
		if (this.mListener != null) {
			ArrayList<TagCodeInfo> codelist = new ArrayList<TagCodeInfo>();
			// mOptionCodeInfo = MyApp.getInstance().getCurrentOption();
			codelist.add(codeInfo);
			mListener.requestHQPushData(codelist);
		}

	}

	// update chicang info, zijin info, weituo info and so on.
	public void updateAllData() {

		updateOptionData(false);
		// getOptionList();
		// updateZJData();
		// updateChiCang(false);
		// updateDRWT_CD(false);
	}

	public void updateView() {
		if (!mIsScrolled) {
			mIsNeedUpdate = false;
			updateOptionData(true);
			updateHQView("", false);
			updateTradeData(false);
		} else {
			mIsNeedUpdate = true;
		}

	}

	public void updateOptionData(boolean isPush) {
		// if (MyApp.getInstance().getCurrentOption() != null) {
		// mOptionCodeInfo = MyApp.getInstance().getCurrentOption();
		// }
		if (mOptionCodeInfo != null) {
			TagLocalStockData optionStockData = new TagLocalStockData();
			if (MyApp.getInstance().mHQData_ZQ.getData(optionStockData,
					mOptionCodeInfo.market, mOptionCodeInfo.code, false)) {
				mOptionData = optionStockData;
			} else {
				setStockData(mOptionCodeInfo);
			}
			updateMinUnitView(mOptionData.PriceDecimal);
		}

		if (!isPush) {

			if (mOptionData != null) {

				setHYCode(mOptionData.code, mOptionData.name);

				for (int i = 0; i < mGdList.size(); i++) {
					if (mGdList.get(i).mHqMarket == mOptionData.market) {
						mZqGudongText.setText(mGdList.get(i).mGdzh);
						break;
					}
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.zq_addprice: // price add
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
				price = mZQPrice.getText().toString();
			}
			mZQPrice.setText(ViewTools.getPriceByStep(price, mMinPriceStep,
					true, mPriceDotLen));
			mSellWTPriceMode = Trade_Define.WTPRICEMODE_INPUT;
			// this.updateOrderPriceBtn();
			// startRequestKMSLTimer(2000);
		}
			break;
		case R.id.zq_reduceprice: {
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
				price = mZQPrice.getText().toString();
			}
			mZQPrice.setText(ViewTools.getPriceByStep(price, mMinPriceStep,
					false, mPriceDotLen));
			mSellWTPriceMode = Trade_Define.WTPRICEMODE_INPUT;
		}
			break;

		case R.id.zq_mairu_btn:

			if (mOptionData == null) {
				return;
			}
			requestWTClick(R.id.zq_mairu_btn);

			break;
		}
	}

	
	// 获取price edit的内容
	private String getPriceEditContent(char mmlb) {
		String strPrice = "";

		if (mSellWTPriceMode != Trade_Define.WTPRICEMODE_INPUT) {
			switch (mSellWTPriceMode) {
			case Trade_Define.WTPIRCEMODE_ZYWDJSCJSYCX:// 最优五档即时成交剩余撤销
				break;

			case Trade_Define.WTPIRCEMODE_ZYWDJSCJSYXJ:// 最优五档即时成交剩余转限价
				break;

			case Trade_Define.WTPIRCEMODE_JSCJSYCXWT://即时成交剩余撤销委托
				break;

			case Trade_Define.WTPIRCEMODE_QECJHCXWT://全额成交或撤消委托
				break;
			case Trade_Define.WTPIRCEMODE_DSZYJWT:// 对手方最优价格委托
				break;
			case Trade_Define.WTPIRCEMODE_BFZYJWT:// 本方最优价格委托
				break;
			case Trade_Define.WTPIRCEMODE_SJRYJ:// 市价 任意价
				break;
			default:
				break;
			}
		} else {
			strPrice = mZQPrice.getText().toString();
		}

		return strPrice;
	}

	private void requestHQPushData() {
		if (mOptionCodeInfo != null && !mOptionCodeInfo.code.isEmpty()) {
			ArrayList<TagCodeInfo> codelist = new ArrayList<TagCodeInfo>();
			if (mOptionCodeInfo != null)
				codelist.add(mOptionCodeInfo);

			mListener.requestHQPushData(codelist);
		} else {
			mListener.requestHQPushData(null);
		}
	}

	protected void requestWTClick(int btnIndex) {
		requestWTClick(btnIndex, "买入");
	}

	protected void requestWTClick(int btnIndex, String buysell) {
		if (mZqCodeEdit.getText().length() != 6) {
			new com.pengbo.mhdzq.widget.AlertDialog(getActivity()).builder()
					.setTitle("提示").setMsg("请输入完整股票代码").setCancelable(false)
					.setCanceledOnTouchOutside(false)
					.setPositiveButton("确认", new OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					}).show();
			return;
		}

		if (mZqGudongText.getText().length() == 0) {
			new com.pengbo.mhdzq.widget.AlertDialog(getActivity()).builder()
					.setTitle("提示").setMsg("请选择股东帐户").setCancelable(false)
					.setCanceledOnTouchOutside(false)
					.setPositiveButton("确认", new OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					}).show();
			return;
		}

		String price = mZQPrice.getText().toString();
		if (price.isEmpty()) {
			new com.pengbo.mhdzq.widget.AlertDialog(getActivity()).builder()
					.setTitle("提示").setMsg("请输入委托价格").setCancelable(false)
					.setCanceledOnTouchOutside(false)
					.setPositiveButton("确认", new OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					}).show();
			return;
		}

		String wtsl = mZQBuyAmount.getText().toString();
		if (wtsl.isEmpty()) {

			new com.pengbo.mhdzq.widget.AlertDialog(getActivity()).builder()
					.setTitle("提示").setMsg("请输入委托数量").setCancelable(false)
					.setCanceledOnTouchOutside(false)
					.setPositiveButton("确认", new OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					}).show();
			return;

		}

		if (mOptionData == null)
			return;

		mWTPrice = this.getPriceEditContent(mMMLB);

		String msg = String.format("%s, %s, %s, %s股", mZqCodeEdit.getText()
				.toString(), mZQPrice.getText().toString(), buysell, wtsl);

		if (mWTConfirmDialog != null) {
			mWTConfirmDialog.dismiss();
		} else {
			mWTConfirmDialog = new com.pengbo.mhdzq.widget.AlertDialog(
					getActivity()).builder();
		}

		mWTConfirmDialog.clear();

		mWTConfirmDialog.setTitle("确认下单吗？").setMsg(msg).setCancelable(false)
				.setCanceledOnTouchOutside(false)
				.setPositiveButton(buysell, new OnClickListener() {

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

	protected void requestWT() {
		String marketCode = TradeData.GetTradeMarketFromHQMarket(
				mOptionData.HQData.market, mOptionData.group);
		String gdzh = mZqGudongText.getText().toString();
		String xwh = MyApp.getInstance().mTradeData
				.GetXWHFromMarket(mCurrentMaket);

		mOrderRecord.mMMLB = PTK_Define.PTK_D_Buy;
		;
		mOrderRecord.mKPBZ = PTK_Define.PTK_OF_Open;
		mOrderRecord.mWTPrice = mWTPrice;
		mOrderRecord.mWTSL = mZQBuyAmount.getText().toString();
		mOrderRecord.mMarketCode = marketCode;
		mOrderRecord.mGDZH = gdzh;
		mOrderRecord.mXWH = xwh;
		mOrderRecord.mStockCode = mZqCodeEdit.getText().toString();
		mOrderRecord.mSJType = getSJType();

		if (mListener != null) {
			mListener.requestWT(mOrderRecord);
		}
		// setInitPriceAndVolume();// 下单后重置委托价格和数量

	}

	protected void requestKMSL(String price, TagLocalStockData optionData) {
		if (mListener != null) {
			mListener.requestKMSL(price, optionData, getSJType(), true);
		}
	}

	protected char getSJType() {
		char sjType = '0';
		if (mSellWTPriceMode == Trade_Define.WTPIRCEMODE_ZYWDJSCJSYCX) // 最优五档即时成交剩余撤销
		{
			sjType = PTK_Define.PTK_OPT_5FAK;
		} else if (mSellWTPriceMode == Trade_Define.WTPIRCEMODE_ZYWDJSCJSYXJ) // 最优五档即时成交剩余转限价
		{
			sjType = PTK_Define.PTK_OPT_5FAL;
		} else if (mSellWTPriceMode == Trade_Define.WTPIRCEMODE_JSCJSYCXWT) // 即时成交剩余撤销委托
		{
			sjType = PTK_Define.PTK_OPT_FAK;
		} else if (mSellWTPriceMode == Trade_Define.WTPIRCEMODE_QECJHCXWT) // 全额成交或撤消委托
		{
			sjType = PTK_Define.PTK_OPT_FOK;
		} else if (mSellWTPriceMode == Trade_Define.WTPIRCEMODE_DSZYJWT) // 对手方最优价格委托
		{
			sjType = PTK_Define.PTK_OPT_DBestPrice;
		} else if (mSellWTPriceMode == Trade_Define.WTPIRCEMODE_BFZYJWT) // 本方最优价格委托
		{
			sjType = PTK_Define.PTK_OPT_WBestPrice;
		} else if (mSellWTPriceMode == Trade_Define.WTPIRCEMODE_SJRYJ) // 市价 任意价
		{
			sjType = PTK_Define.PTK_OPT_AnyPrice;
		}

		return sjType;
	}

	public void updateTradeData(boolean isPushRefresh) {
		if (!mIsViewReady)
			return;

		mMyApp.mTradeData.GetHoldStock(mListData_CC);

		if (!mIsScrolled) {
			mChiCangAdapterCC.notifyDataSetChanged();
			// mIsScrolled = true;
		}

	}
	
    public void backgroundAlpha(float bgAlpha)  
    {  
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();  
        lp.alpha = bgAlpha; //0.0-1.0  
        mActivity.getWindow().setAttributes(lp);  
    }
    
    
	// 设置价格edit的字符
	public void setPriceEditContent(String content) {
		if (mCurrentKJBJType == ZqShijiaWindow.KJBJ_TYPES_SZ) {
			int kjbjType = STD.IsHave(ZqShijiaWindow.sKjbjTypesSZ, content);
			if (!content.isEmpty() && kjbjType >= 0) {

				mSellWTPriceMode = ZqShijiaWindow.sKjbjModeSZ[kjbjType];
			} else {
				mSellWTPriceMode = Trade_Define.WTPRICEMODE_INPUT;
			}
		} else {
			int kjbjType = STD.IsHave(ZqShijiaWindow.sKjbjTypesSH, content);
			if (!content.isEmpty() && kjbjType >= 0) {

				mSellWTPriceMode = ZqShijiaWindow.sKjbjModeSH[kjbjType];
			} else {
				mSellWTPriceMode = Trade_Define.WTPRICEMODE_INPUT;
			}
		}

		mZQPrice.setText(content);
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
				String input = ((TextView) v).getText().toString();
				if (mZqCodeEdit.getText().length() == 0) {
					mZqCodeEdit.setText(input);
				} else if (input != null) {
					String strTmp = mZqCodeEdit.getText().toString();
					strTmp += input;
					mZqCodeEdit.setText(strTmp);
					mZqCodeEdit.showDropDown();
				}
			}
				break;
			case R.id.btn_digit_600:
			case R.id.btn_digit_601:
			case R.id.btn_digit_000:
			case R.id.btn_digit_002:
			case R.id.btn_digit_300: {
				String input = ((Button) v).getText().toString();
				if (mZqCodeEdit.getText().length() == 0) {
					mZqCodeEdit.setText(input);
					mZqCodeEdit.showDropDown();
				} else if (input != null) {
					String strTmp = mZqCodeEdit.getText().toString();
					strTmp += input;
					mZqCodeEdit.setText(strTmp);
					mZqCodeEdit.showDropDown();
				}
			}

				break;
			case R.id.btn_digit_confirm: {
				mKeyboard.dismiss();
			}
				break;

			case R.id.btn_digit_back: // {
				// if (mZqCodeEdit.getText().length() > 0) {
				// String strTmp = mZqCodeEdit.getText().toString();
				// strTmp = strTmp.substring(0, strTmp.length() - 1);
				// mZqCodeEdit.setText(strTmp);
				// }
				// }
				break;

			case R.id.btn_digit_ABC:
				mKeyboard.dismiss();

				if (mKeyZmBorad == null) {
					mKeyZmBorad = new ZqXdCodeZmKeyBoard(mActivity,
							itemsOnClick, mZqCodeEdit);
					mKeyZmBorad.showAtLocation(
							mView.findViewById(R.id.trade_xh_buy_frame),
							Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				} else {
					mKeyZmBorad.ResetKeyboard(mZqCodeEdit);
					mKeyZmBorad.showAtLocation(
							mView.findViewById(R.id.trade_xh_buy_frame),
							Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

				}

				break;
			case R.id.btn_digit_hide:
				// mKeyboard.dismiss();
				break;
			case R.id.btn_zm_a:
			case R.id.btn_zm_b:
			case R.id.btn_zm_c:
			case R.id.btn_zm_d:
			case R.id.btn_zm_e:
			case R.id.btn_zm_f:
			case R.id.btn_zm_g:
			case R.id.btn_zm_h:
			case R.id.btn_zm_i:
			case R.id.btn_zm_j:
			case R.id.btn_zm_k:
			case R.id.btn_zm_l:
			case R.id.btn_zm_m:
			case R.id.btn_zm_n:
			case R.id.btn_zm_o:
			case R.id.btn_zm_p:
			case R.id.btn_zm_q:
			case R.id.btn_zm_r:
			case R.id.btn_zm_s:
			case R.id.btn_zm_t:
			case R.id.btn_zm_u:
			case R.id.btn_zm_v:
			case R.id.btn_zm_w:
			case R.id.btn_zm_x:
			case R.id.btn_zm_y:
			case R.id.btn_zm_z: {
				String input = ((Button) v).getText().toString();
				if (mZqCodeEdit.getText().length() == 0) {
					mZqCodeEdit.setText(input);
				} else if (input != null) {
					String strTmp = mZqCodeEdit.getText().toString();
					strTmp += input;
					mZqCodeEdit.setText(strTmp);
					mZqCodeEdit.showDropDown();
				}
			}
				break;

			case R.id.btn_zm_123:
				mKeyZmBorad.dismiss();

				if (mKeyboard == null) {
					mKeyboard = new ZqXdCodeDigitKeyBoard(mActivity,
							itemsOnClick, mZqCodeEdit);
					mKeyboard.showAtLocation(
							mView.findViewById(R.id.trade_xh_buy_frame),
							Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				} else {
					mKeyboard.ResetKeyboard(mZqCodeEdit);
					mKeyboard.showAtLocation(
							mView.findViewById(R.id.trade_xh_buy_frame),
							Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

				}

				break;
			case R.id.btn_zm_confirm: {
				mKeyZmBorad.dismiss();
			}
				break;
			case R.id.btn_zm_sys:
				mKeyZmBorad.dismiss();
				Message msg = TradeZqBuyFragment.this.mHandler.obtainMessage();
				msg.what = TradeZqBuyFragment.MLOCALMSG1;
				mHandler.sendMessage(msg);
				break;
			case R.id.btn_digit_xt:
				mKeyboard.dismiss();
				Message msg2 = TradeZqBuyFragment.this.mHandler.obtainMessage();
				msg2.what = TradeZqBuyFragment.MLOCALMSG1;
				mHandler.sendMessage(msg2);
				break;
			case R.id.btn_cc_1_4: {
				double mount = mKMSL / 100;
				mount = Math.floor(mount / 4);
				mount = mount * 100;
				mZQBuyAmount.setText(String.valueOf(new Double(mount)
						.intValue()));
			}
				break;
			case R.id.btn_cc_1_3: {
				double mount = mKMSL / 100;

				mount = Math.floor(mount / 3);
				mount = mount * 100;
				mZQBuyAmount.setText(String.valueOf(new Double(mount)
						.intValue()));
			}
				break;
			case R.id.btn_cc_1_2: {
				double mount = mKMSL / 100;
				mount = Math.floor(mount / 2);
				mount = mount * 100;
				mZQBuyAmount.setText(String.valueOf(new Double(mount)
						.intValue()));
			}
				break;
			case R.id.btn_cc_1_1: {
				double mount = mKMSL / 100;
				mount = Math.floor(mount / 1);
				mount = mount * 100;
				mZQBuyAmount.setText(String.valueOf(new Double(mount)
						.intValue()));
			}
				break;
			case R.id.btn_price_shijia:{
				mKeyPriceBoard.dismiss();

				SearchDataItem item;
                String hqcode = mZqCodeEdit.getEditableText().toString();
				int market = 1000;

				for (int i = 0; i < mMyApp.getSearchTradeCodeArray().size(); i++) {
					item = mMyApp.getSearchTradeCodeArray().get(i);
					if (item.code.endsWith(hqcode)) {
						market = item.market;
						break;
					}
				}
				
				if(market == 1000)
				{
					mCurrentKJBJType = ZqShijiaWindow.KJBJ_TYPES_SH;
				}
				else
				{
					mCurrentKJBJType = ZqShijiaWindow.KJBJ_TYPES_SZ;	
				}

				if (mKeyShijia == null) {
					mKeyShijia = new ZqShijiaWindow(mActivity, this,market,TradeZqBuyFragment.this);
				}
				else
				{
					mKeyShijia.resetMarket(market);
				}

				Handler handler = new Handler();

				handler.postDelayed(new Runnable() {
					   @Override
					   public void run() {
						// TODO Auto-generated method stub
						//mKeyShijia.showAtLocation(mView.findViewById(R.id.trade_xh_buy_frame), Gravity.CENTER, 0, 0);
						mKeyShijia.showAtLocation(
								mView.findViewById(R.id.trade_xh_buy_frame),
								Gravity.CENTER, 0, 0);
						
						//backgroundAlpha(1f);
					  }
				}, 500);	

			}
			    break;
			case R.id.btn_price_wc:
			{
				mKeyPriceBoard.dismiss();
				mSellWTPriceMode = Trade_Define.WTPRICEMODE_INPUT;
			}
			    break;
			default:
				break;
			}
		}

	};
}
