package com.pengbo.mhdcx.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.adapter.HeadListDataService;
import com.pengbo.mhdcx.adapter.NewsReportAdapter;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdcx.bean.News;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.CPbDataDecode;
import com.pengbo.mhdzq.data.CPbDataField;
import com.pengbo.mhdzq.data.CPbDataItem;
import com.pengbo.mhdzq.data.CPbDataPackage;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.data.TagLocalDealData;
import com.pengbo.mhdzq.data.TagLocalKLineData;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.TagLocalTrendData;
import com.pengbo.mhdzq.net.CMessageObject;
import com.pengbo.mhdzq.net.GlobalNetConnect;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.net.MyByteBuffer;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.view.AutoScaleTextView;
import com.pengbo.mhdzq.zq_activity.HdActivity;
import com.pengbo.mhdcx.ui.main_activity.MainTabActivity;
import com.pengbo.mhdcx.utils.HttpUtil;
import com.pengbo.mhdcx.view.GainLossView;
import com.pengbo.mhdcx.view.KLineView;
import com.pengbo.mhdcx.view.TrendLineView;

public class ScreenDetailActivity extends HdActivity implements OnGestureListener, OnTouchListener, OnCheckedChangeListener {

	public static final String TAG = ScreenDetailActivity.class.getSimpleName();

	protected static final int REFRESH_UI = -1;
	
	protected static final int SCREEN_DETAIL_REFRESH_UI = 0;
	protected static final int SCREEN_DETAIL_REFRESH_UI2 = 1;

	public final static int VIEW_TLINE = 2;
	public final static int VIEW_DETAIL = 3;
	public final static int VIEW_KLINE = 4;
	public final static int VIEW_GAINLOSS = 5;
	public final static int VIEW_STOCKKLINE = 6;
	
	GestureDetector	mGestureDetector;

	private LinearLayout mImgBack;// 顶部的 返回按钮
	private ImageView mRefresh;// 顶部的刷新 按钮

	private TextView mTextViewUp1, mTextViewUp2, mTextViewDown;// 顶部 中间 的 标的 的名称 和到期时间
	private TextView mTVbiaodiwu, mTV_Biaodi_ZDF;
	private Button mBottomAdd, mBottomTrade;
	private boolean mIsMyStock;

	public static TagCodeInfo mOptionCodeInfo;// 合约
	public static TagCodeInfo mStockCodeInfo;// 标的
	private TagLocalStockData mOptionData;// 合约
	private TagLocalStockData mStockData;// 标的
	private ArrayList<TagLocalTrendData> mTrendDataArray;// 合约走势数据
	private ArrayList<TagLocalTrendData> mTrendStockDataArray;// 标的走势数据
	private ArrayList<TagLocalDealData> mDealDataArray;
	private ArrayList<TagLocalKLineData> mKLineDataArray;
	private ArrayList<TagLocalKLineData> mStockKLineDataArray;

	private HeadListDataService mHService;

	/************ 新闻公告 和 研究报告 两个 控件 **************/
	private ViewFlipper mFlipper;
	private RadioGroup mRgNewsAndReports;
	
	public List<News> mNewReportList;
	private List<News> mNewListDatas;
	private int mNewReportPageIndex = 0; //0-news;1-report;

	private RelativeLayout mRLayout_News;
	private ListView mNewsListView;
	private NewsReportAdapter mNewsAdapter;

	/*********** 分时图 按钮 和 日线图 按钮 以及 包裹他们的 radiogroup ********************/
	private RadioGroup mRgChart;
	private TrendLineView mTrendLineView;
	private KLineView mKLineView;
	private KLineView mStockKLineView;
	private int mViewType = VIEW_TLINE;
	public GainLossView mGainLossView;
	private LinearLayout mLLayout_PJSY;

	public int mRequestCode[];// 请求标记0:trendline,1:detail;2:kline;3:datapush ;4:trendline(stock);5:stock kline

	/****  传递过来的  code  market  **/
	public static String sCode;
	public static short sMarket;
	public MyApp mMyApp;
	/*********    标的的数据  ************************/

	/**************  中间 总手 等  的数据  ********************************/
	private AutoScaleTextView field_hq_now, field_hq_zd, field_hq_zdf;
	private TextView field_hq_volume, field_hq_ccl, field_hq_high, field_hq_curvol, field_hq_low,
			field_hq_dzcb;
	
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			int nFrameType = msg.arg1;
			int nRequestCode = msg.arg2;

			switch (msg.what) {
				case REFRESH_UI:
					if (mNewsAdapter != null) 
					{
						mNewsAdapter.notifyDataSetChanged();
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

						if (mRequestCode[0] == nRequestCode)// 走势
						{
							parseTrendData(aMsgObject.getData(), aMsgObject.getDataLength(), true);
							requestStockTrendLine();
							
							if (mViewType == VIEW_TLINE) {
								mTrendLineView.updateAllView();// update view
							}
						} else if (mRequestCode[4] == nRequestCode) //标的走势
						{
							parseTrendData(aMsgObject.getData(), aMsgObject.getDataLength(), false);
							
							requestDetail();
							if (mViewType == VIEW_TLINE) {
								mTrendLineView.updateAllView();// update view
							}
							
						} else if (mRequestCode[1] == nRequestCode)// detail
						{
							parseDealData(aMsgObject.getData(), aMsgObject.getDataLength());
							if (mViewType == VIEW_TLINE) {
								mTrendLineView.updateAllView();// update view
							}
						} else if (mRequestCode[2] == nRequestCode)// kline
						{
							parseKLineData(aMsgObject.getData(), aMsgObject.getDataLength());
							setOptionDataForKLine(mOptionData, true);
							if (mViewType == VIEW_KLINE) {
								mKLineView.resetKLineParam();
								mKLineView.updateAllData();// update view
							}
						} else if (mRequestCode[5] == nRequestCode)// stock kline
						{
							parseStockKLineData(aMsgObject.getData(), aMsgObject.getDataLength());
							setStockDataForStockKLine(mStockData, true);
							if (mViewType == VIEW_STOCKKLINE && mStockKLineView != null) {
								mStockKLineView.resetKLineParam();
								mStockKLineView.updateAllData();// update view
							}
						}
					} else if (nFrameType == Global_Define.MFT_MOBILE_PUSH_DATA) {
						L.i(TAG, "Received push data");
						TagLocalStockData optionStockData = new TagLocalStockData();
						if (mMyApp.mHQData.getData(optionStockData, mOptionCodeInfo.market, mOptionCodeInfo.code, false)) {
							L.i(TAG, "Received push data1");
							// synchronized (this) {
							mOptionData = optionStockData;
							updataOptionView();
							setOptionDataForTLine(optionStockData, true);
							setOptionDataForTDetail(optionStockData, true);
							setOptionDataForKLine(optionStockData, true);

							if (mViewType == VIEW_GAINLOSS)
							{
								if (mGainLossView != null)
								{
									mGainLossView.updateData(mOptionData, mStockData);
								}
							} else if (mViewType == VIEW_TLINE) {
								L.i(TAG, "Received push data1mTrendLineView");
								if (mTrendLineView != null)
								{
									mTrendLineView.updateData(mOptionData, mStockData);
									mTrendLineView.updateAllView();// update trend
																// line view
								}
							} else if (mViewType == VIEW_KLINE) {
								L.i(TAG, "Received push data1mKLineView");
								mKLineView.updateAllData();// update kline view
							}
							// }
						}
						TagLocalStockData stockStockData = new TagLocalStockData();
						if (mMyApp.mStockConfigData.getData(stockStockData, mStockCodeInfo.market, mStockCodeInfo.code, false)) {
							L.i(TAG, "Received push data1");
							// synchronized (this) {
							mStockData = stockStockData;
							updateStockView();
							setStockDataForTLine(stockStockData, true);
							setStockDataForStockKLine(stockStockData, true);

							if (mViewType == VIEW_GAINLOSS)
							{
								if (mGainLossView != null)
								{
									mGainLossView.updateData(mOptionData, mStockData);
								}
							} else if (mViewType == VIEW_TLINE) {
								L.i(TAG, "Received push data1mTrendLineView");
								mTrendLineView.updateData(mOptionData, mStockData);
								mTrendLineView.updateAllView();// update trend
																// line view
							} else if (mViewType == VIEW_STOCKKLINE  && mStockKLineView != null) {
								L.i(TAG, "Received push data1mSTOCKKLineView");
								mStockKLineView.updateAllData();// update kline view
							}
							// }
						}
					}
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
		setContentView(R.layout.screen_detail);

		initData();
		initView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mMyApp.setCertifyNetHandler(null);
		mMyApp.setHQPushNetHandler(null);
		super.onPause();
	}

	@Override
	protected void onResume() {

		mMyApp.resetTrendDataArray();
		mMyApp.resetTrendStockDataArray();
		mMyApp.resetKLineDataArray();
		mMyApp.resetStockKLineDataArray();
		mMyApp.resetDealDataArray();
		updateStockView();
		updataOptionView();
		
		if (mViewType == VIEW_TLINE) {
			requestTrendLine();
		} else if (mViewType == VIEW_KLINE) {
			requestKLine();
		} else if (mViewType == VIEW_STOCKKLINE) {
			requestStockKLine();
		}
		RequestHttpUtil();
		
		queryHQPushInfo();

		super.onResume();
	}
	
	
	
	private void RequestHttpUtil() {

		new Thread(new Runnable() {

			@Override
			public void run() {		
				{
					{
						mNewReportList.clear();
						int num = HttpUtil.getNews(mStockData.HQData.code, mStockData.HQData.market, mNewReportList);
						
						mNewListDatas.clear();
						for (int i = 0; i < mNewReportList.size(); i++) {
							L.e("-----------------------sss", mNewReportList.get(i).Title+",,"+mNewReportList.get(i).Type);
							
							switch(mNewReportPageIndex)
							{
							case 0:
								if(mNewReportList.get(i).Type==1)
								{
									mNewListDatas.add(mNewReportList.get(i));
								}
								break;
								
							case 1:
								if(mNewReportList.get(i).Type==2){
									mNewListDatas.add(mNewReportList.get(i));
								}
								break;
							default:
								break;
							}
						}

						mHandler.sendEmptyMessage(REFRESH_UI);
					}
				}
														
			}
		}).start();
	}

	private void initData() {
		mViewType = VIEW_GAINLOSS;
		
		mNewReportList = new ArrayList<News>();

		mRequestCode = new int[6]; // [0]:trendline;[1]:detail;[2]:kline
		mIsMyStock = false;

		mMyApp = (MyApp) getApplication();

		sCode = mOptionCodeInfo.code;
		sMarket = mOptionCodeInfo.market;

		L.e("-------sdsdsdsd---------", sCode + "," + sMarket);
		mHService = new HeadListDataService(ScreenDetailActivity.this);

		mOptionData = mHService.getTagLocalStockData(sMarket, sCode);
		mStockData = mHService.getTagLocalStockDataAll(mOptionData.optionData.StockMarket, mOptionData.optionData.StockCode);

		// array for store trend&detail&kline data
		mTrendDataArray = mMyApp.getTrendDataArray();
		mTrendStockDataArray = mMyApp.getTrendStockDataArray();

		mDealDataArray = mMyApp.getDealDataArray();
		mKLineDataArray = mMyApp.getKLineDataArray();
		mStockKLineDataArray = mMyApp.getStockKLineDataArray();
		
		//手势识别类
		mGestureDetector = new GestureDetector(this);
	}

	/*************** 初始化控件 ***********************/
	private void initView() {
		initHeaderView();
		initTextViewData();
		initBottomView();
		initNewsAndReportView();
		initDrawTrendAndDayLine();
	}
	
	//biaodi zdf
	private void updateStockView() {
		// 现价幅度等
		String nowPrice = ViewTools.getStringByFieldID(mStockData,
				Global_Define.FIELD_HQ_NOW);
		String fd = ViewTools.getStringByFieldID(mStockData,
				Global_Define.FIELD_HQ_ZDF_SIGN);

		mTV_Biaodi_ZDF.setText(String.format("%s %s", nowPrice, fd));
		mTV_Biaodi_ZDF.setTextColor(ViewTools.getColorByFieldID(mStockData,
				Global_Define.FIELD_HQ_NOW));
	}

	private void updataOptionView() {
		
		field_hq_now.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_NOW, mStockData));
		field_hq_now.setTextColor(ViewTools.getColorByFieldID(mOptionData, Global_Define.FIELD_HQ_NOW));

		field_hq_zd.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_ZD, mStockData));
		field_hq_zd.setTextColor(ViewTools.getColorByFieldID(mOptionData, Global_Define.FIELD_HQ_NOW));

		field_hq_zdf.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_ZDF_SIGN, mStockData));
		field_hq_zdf.setTextColor(ViewTools.getColorByFieldID(mOptionData, Global_Define.FIELD_HQ_NOW));

		field_hq_volume.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_VOLUME, mStockData));
		field_hq_ccl.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_CCL, mStockData));

		field_hq_high.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_HIGH, mStockData));
		field_hq_high.setTextColor(ViewTools.getColorByFieldID(mOptionData, Global_Define.FIELD_HQ_HIGH));

		field_hq_curvol.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_CURVOL, mStockData));

		field_hq_low.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_LOW, mStockData));
		field_hq_low.setTextColor(ViewTools.getColorByFieldID(mOptionData, Global_Define.FIELD_HQ_LOW));

		field_hq_dzcb.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_DZCB, mStockData));
	}

	private void initTextViewData() {
		field_hq_volume = (TextView) this.findViewById(R.id.zongshou);
		field_hq_ccl = (TextView) this.findViewById(R.id.cicang);
		field_hq_high = (TextView) this.findViewById(R.id.highprice);
		field_hq_curvol = (TextView) this.findViewById(R.id.xianshou);
		field_hq_low = (TextView) this.findViewById(R.id.lowprice);
		field_hq_dzcb = (TextView) this.findViewById(R.id.danzhangchengben);

		field_hq_now = (AutoScaleTextView) this.findViewById(R.id.now_price);
		field_hq_zd = (AutoScaleTextView) this.findViewById(R.id.zd);
		field_hq_zdf = (AutoScaleTextView) this.findViewById(R.id.zdf);
	}

	/**
	 * 分时图 控件 和 日线 走势 控件
	 */

	private void initDrawTrendAndDayLine() {
		//mTrendLineView = new TrendLineView(this, true);
		//mKLineView = new KLineView(this);
		mGainLossView = new GainLossView(this, mOptionData, mStockData);

		mFlipper = (ViewFlipper) findViewById(R.id.flipper);
		mFlipper.addView(mGainLossView);
		
		mFlipper.setOnTouchListener(this);
        mFlipper.setLongClickable(true);

		//mTrendLineView.requestFocus();
		//mTrendLineView.updateData(mOptionData, mStockData);
		//mKLineView.updateData(mOptionData);

		mRgChart = (RadioGroup) this.findViewById(R.id.detail_radiogroup);
		mRgChart.setOnCheckedChangeListener(this);
	}

	/**
	 * 初始化 新闻 和 研究 报告 控件
	 */
	private void initNewsAndReportView() {
		// 新闻公告 和 研究报告点击切换两个 按钮时 更新 List数据
		mRgNewsAndReports = (RadioGroup) this.findViewById(R.id.screen_detail_radiogroup2);
		mRgNewsAndReports.setOnCheckedChangeListener(this);
		
		if(mNewsListView == null)
		{
			mNewsListView = (ListView) this.findViewById(R.id.news_listview);
			if (mNewListDatas == null)
			{
				mNewListDatas = new ArrayList<News>();
			}
			mNewsAdapter = new NewsReportAdapter(ScreenDetailActivity.this, mNewListDatas);
			mNewsListView.setAdapter(mNewsAdapter);
			mNewsListView.setOnItemClickListener(mOnItemClickListener);
		}
		
		mRLayout_News = (RelativeLayout) this.findViewById(R.id.rlayout_news_notice);
		if (mViewType == VIEW_GAINLOSS)
		{
			mRLayout_News.setVisibility(View.GONE);
		}else
		{
			mRLayout_News.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 顶部控件
	 */
	private void initHeaderView() {
		// 返回按钮
		mImgBack =(LinearLayout) this.findViewById(R.id.linearlayout_out_of_back);
		mImgBack.setOnClickListener(mClickListener);

		// 刷新按钮
		mRefresh = (ImageView) this.findViewById(R.id.screen_detail_header_right_refresh);
		mRefresh.setOnClickListener(mClickListener);
		mRefresh.setVisibility(View.GONE);
		
		// 顶部的标的的名称和到期时间
		mTextViewUp1 = (TextView) this.findViewById(R.id.header_middle_tv_name1);
		mTextViewUp2 = (TextView) this.findViewById(R.id.header_middle_tv_name2);
		mTextViewDown = (TextView) this.findViewById(R.id.header_middle_tv_time);
		mTVbiaodiwu = (TextView) this.findViewById(R.id.screen_detail_tvname);
		mTVbiaodiwu.setText(ViewTools.getStringByFieldID(mStockData, Global_Define.FIELD_HQ_NAME_ANSI));
		mTV_Biaodi_ZDF = (TextView) this.findViewById(R.id.screen_detail_tv_biaodi_zdf);
		/***************** 数据 显示 *********************/
		String name = ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_NAME_ANSI, mStockData);
		String name1 = "";
		String name2 = "";
		if (!name.isEmpty())
		{
			int index = name.indexOf("购");
			if (index >= 0)
			{
				name1 = name.substring(0, index);
				name2 = name.substring(index);
			}else
			{
				index = name.indexOf("沽");
				if(index >=0 )
				{
					name1 = name.substring(0, index);
					name2 = name.substring(index);
				}
			}
		}
		
		if(!name1.isEmpty())
		{
			mTextViewUp1.setText(name1);
			mTextViewUp2.setText(name2
					+ "("
					+ ViewTools.getStringByFieldID(mOptionData,
							Global_Define.FIELD_HQ_CODE, mStockData) + ")");
		}else
		{
			mTextViewUp2.setText(name
					+ "("
					+ ViewTools.getStringByFieldID(mOptionData,
							Global_Define.FIELD_HQ_CODE, mStockData) + ")");
			mTextViewUp1.setText("");
		}

		int days = ViewTools.getDaysDruationFromToday(mOptionData.optionData.StrikeDate);
		String temp = String.format("%s到期(剩余%d天)", ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_EXPIRE_DATE, mStockData), days);
		mTextViewDown.setText(temp);
	}

	/**
	 * 底部三个按钮 控件
	 */
	private void initBottomView() {
		// 底部的 加入 自选 交易 盈亏 分析
		mBottomAdd = (Button) this.findViewById(R.id.sbutton02_add);
		mBottomTrade = (Button) this.findViewById(R.id.sbutton01_trade);
		mBottomAdd.setOnClickListener(mClickListener);
		mBottomTrade.setOnClickListener(mClickListener);

		if (mMyApp.IsStockExist(mOptionData.code, mOptionData.market, AppConstants.HQTYPE_QQ)) {
			mIsMyStock = true;
			mBottomAdd.setText("删除自选");
		} else {
			mIsMyStock = false;
			mBottomAdd.setText("加自选");
		}
		
		mLLayout_PJSY = (LinearLayout) this.findViewById(R.id.llayout_gain_loss_bottom);
		if (mViewType != VIEW_GAINLOSS)
		{
			mLLayout_PJSY.setVisibility(View.GONE);
		}else
		{
			mLLayout_PJSY.setVisibility(View.VISIBLE);
		}
	}

	private void onChangeView(int index, boolean bNeedChangeView) {
		L.d(TAG, "onChangeView--->index = " + index + ", bNeedChangeView = " + bNeedChangeView);

		switch (index) {

		case VIEW_GAINLOSS:
			if (mGainLossView == null)
			{
				mGainLossView = new GainLossView(this, mOptionData, mStockData);
			}
			if (bNeedChangeView) {
				ChangeView(index, mGainLossView);
			}
			break;

		case VIEW_TLINE:
			
			if (mTrendLineView == null)
			{
				mTrendLineView = new TrendLineView(this, true);
			}
			mTrendLineView.updateData(mOptionData, mStockData);
			if (bNeedChangeView) {
				ChangeView(index, mTrendLineView);
			}
			//mTrendLineView.requestFocus();
			requestTrendLine();
			break;

		case VIEW_KLINE:
			if (mKLineView == null)
			{
				mKLineView = new KLineView(this, true);
			}
			mKLineView.updateData(mOptionData);
			if (bNeedChangeView) {
				ChangeView(index, mKLineView);
			}
			//mKLineView.requestFocus();
			requestKLine();
			break;
		case VIEW_STOCKKLINE:
			if (mStockKLineView == null)
			{
				mStockKLineView = new KLineView(this, false);
			}
			mStockKLineView.updateData(mStockData);
			if (bNeedChangeView) {
				ChangeView(index, mStockKLineView);
			}
			requestStockKLine();
			break;
		default:
			break;
		}
	}

	// change view between trendlineview and klineview
	private void ChangeView(int viewType, View view) {
		if (viewType == mViewType) {
			return;
		}

		mFlipper.addView(view);
		mViewType = viewType;

		mFlipper.showNext();
		mFlipper.removeViewAt(0);

		if (mViewType == VIEW_GAINLOSS)
		{
			mRLayout_News.setVisibility(View.GONE);
			mLLayout_PJSY.setVisibility(View.VISIBLE);
		}else
		{
			mRLayout_News.setVisibility(View.VISIBLE);
			mLLayout_PJSY.setVisibility(View.GONE);
			//RequestHttpUtil();
		}
	}

	// 获取期权行情信息
	private void queryHQPushInfo() {

		ArrayList<TagCodeInfo> codelist = new ArrayList<TagCodeInfo>();
		codelist.add(mOptionCodeInfo);
		codelist.add(mStockCodeInfo);

		mMyApp.setHQPushNetHandler(mHandler);
		mRequestCode[3] = GlobalNetProgress.HQRequest_MultiCodeInfoPush(mMyApp.mHQPushNet, codelist, 0, codelist.size());
	}

	// send request to retrieve option trend line
	private void requestTrendLine() {
		mMyApp.setCertifyNetHandler(mHandler);
		mRequestCode[0] = GlobalNetProgress.HQRequest_TrendLine(mMyApp.mCertifyNet, mOptionData.HQData.code, mOptionData.HQData.market, 0, (short) 0, mOptionData.Start[0]);
		L.e("requestTrendLine()", mRequestCode[0] + "");
	}

	// send request to retrieve Stock trend line
	private void requestStockTrendLine() {
		mMyApp.setCertifyNetHandler(mHandler);
		mRequestCode[4] = GlobalNetProgress.HQRequest_TrendLine(mMyApp.mCertifyNet, mStockData.HQData.code, mStockData.HQData.market, 0, (short) 0,
				mStockData.Start[0]);
		L.e("requestStockTrendLine()", mRequestCode[4] + "");
	}

	private void requestDetail() {
		mMyApp.setCertifyNetHandler(mHandler);
		mRequestCode[1] = GlobalNetProgress.HQRequest_Detail(mMyApp.mCertifyNet, mOptionData.HQData.code, mOptionData.HQData.market, -1,
				TagLocalDealData.MAX_NUM_DETAILDATA_ARRAY + 1);
	}

	private void requestKLine() {
		mMyApp.setCertifyNetHandler(mHandler);
		if (mKLineView != null) {
			mKLineView.resetKLineParam();
		}
		mRequestCode[2] = GlobalNetProgress.HQRequest_KLine(mMyApp.mCertifyNet, (short) 0, mOptionData.HQData.code, mOptionData.HQData.market, 0, 0, 0, 0,
				TagLocalKLineData.MAX_KLINE_NUM - 1, 0);

	}
	
	private void requestStockKLine()
	{
		mMyApp.setCertifyNetHandler(mHandler);
		if (mStockKLineView != null) {
			mStockKLineView.resetKLineParam();
		}
		mRequestCode[5] = GlobalNetProgress.HQRequest_KLine(mMyApp.mCertifyNet, (short) 0, mOptionData.optionData.StockCode, mOptionData.optionData.StockMarket, 0, 0, 0, 0,
				TagLocalKLineData.MAX_KLINE_NUM - 1, 0);
	}

	// parse trend data and store it into ArrayList<TagLocalTrendData>
	private void parseTrendData(byte[] szData, int nSize, boolean bOption) {
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

		if (bOption) {
			mTrendDataArray.clear();
		} else {
			mTrendStockDataArray.clear();
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
							L.e("MyApp", strSection + "=" + item.m_NormalField.GetInt16());
							break;
						case 11:
							// int nMarketId = item.m_NormalField.GetInt16();
							L.e("MyApp", strSection + "=" + item.m_NormalField.GetInt16());
							break;
						case 23:
							// int nLastClose = item.m_NormalField.GetInt32();
							L.e("MyApp", strSection + "=" + item.m_NormalField.GetInt32());
							break;
						case 24:
							// int nLastClear = item.m_NormalField.GetInt32();
							L.e("MyApp", strSection + "=" + item.m_NormalField.GetInt32());
							break;
						default:
							L.e("MyApp", "App unused field:" + item.m_NormalField.m_FieldID + "->" + strSection);
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

					if (bOption) {
						mTrendDataArray.add(aRecord);
						L.e("mTrendDataArray Add a record[" + nTrendNum + "]:" + aRecord.time + ",Price=" + aRecord.now + ", volume=" + aRecord.volume);
					} else {
						mTrendStockDataArray.add(aRecord);
						L.e("mTrendStockDataArray Add a record[" + nTrendNum + "]:" + aRecord.time + ",Price=" + aRecord.now + ", volume=" + aRecord.volume);
					}

					aRecord = null;
				}
			}
		}

		L.e("MyApp", "End parseTrendData,nRecordCount=" + nTrendNum);
	}

	private void parseDealData(byte[] szData, int nSize) {
		L.e("MyApp", "Start parseDealData");

		int offset = 0;
		int wPackSize = MyByteBuffer.getInt(szData, offset);
		offset += 4;
		if (wPackSize > nSize - offset || nSize == 0) {
			L.e("MyApp", "ERROR parseDealData data error");
			return;
		}

		CPbDataPackage pack = new CPbDataPackage();
		CPbDataDecode.DecodeOnePackage(szData, offset, wPackSize, pack);

		mDealDataArray.clear();
		double lastVolume = 0;// 存储上一笔成交量
		for (int i = 0; i < pack.m_nItemSize; ++i) {
			CPbDataItem item = pack.m_DataItems.get(i);
			if (item.m_ItemType == CPbDataItem.DIT_NORMAL) {
				if (item.m_NormalField.IsValid()) {
					String strSection = item.m_NormalField.m_szFieldName;
					switch (item.m_NormalField.m_FieldID) {
						case 10:
							// code
							L.e("MyApp", strSection + "=" + item.m_NormalField.GetString());
							break;
						case 11:
							// int nMarketId = item.m_NormalField.GetInt16();
							L.e("MyApp", strSection + "=" + item.m_NormalField.GetInt16());
							break;
						default:
							L.e("MyApp", "App unused field:" + item.m_NormalField.m_FieldID + "->" + strSection);
							break;
					}
				}
			} else {
				for (int m = 0; m < item.nArraySize; ++m) {
					TagLocalDealData aRecord = new TagLocalDealData();
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
								case 45:
									aRecord.inoutflag = field.GetInt8();
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
					int nDetailNum = mDealDataArray.size();
					if (nDetailNum > TagLocalDealData.MAX_NUM_DETAILDATA_ARRAY) {
						mDealDataArray.remove(0);
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

					mDealDataArray.add(aRecord);
					L.e("mDealDataArray Add a record[" + nDetailNum + "]:" + aRecord.time + ",Price=" + aRecord.now + ", volume=" + aRecord.volume);
					aRecord = null;
				}
			}
		}

		L.e("MyApp", "End parseDealData,nRecordCount=" + mDealDataArray.size());
	}

	private void parseKLineData(byte[] szData, int nSize) {
		L.e("MyApp", "Start parseKLineData");

		int offset = 0;
		int wPackSize = MyByteBuffer.getInt(szData, offset);
		offset += 4;
		if (wPackSize > nSize - offset || nSize == 0) {
			L.e("MyApp", "ERROR parseKLineData data error");
			return;
		}

		CPbDataPackage pack = new CPbDataPackage();
		CPbDataDecode.DecodeOnePackage(szData, offset, wPackSize, pack);

		mKLineDataArray.clear();
		int nKLineNum = 0;
		for (int i = 0; i < pack.m_nItemSize; ++i) {
			CPbDataItem item = pack.m_DataItems.get(i);
			if (item.m_ItemType == CPbDataItem.DIT_NORMAL) {
				if (item.m_NormalField.IsValid()) {
					String strSection = item.m_NormalField.m_szFieldName;
					switch (item.m_NormalField.m_FieldID) {
						case 10:
							// code
							L.e("MyApp", strSection + "=" + item.m_NormalField.GetString());
							break;
						case 11:
							// int nMarketId = item.m_NormalField.GetInt16();
							L.e("MyApp", strSection + "=" + item.m_NormalField.GetInt16());
							break;
						case 46:
							// int nKlineType = item.m_NormalField.GetInt8();
							L.e("MyApp", strSection + "=" + item.m_NormalField.GetInt32());
							break;
						case 1018:
							// int nKLineWeight = item.m_NormalField.GetInt8();
							L.e("MyApp", strSection + "=" + item.m_NormalField.GetInt32());
							break;
						default:
							L.e("MyApp", "App unused field:" + item.m_NormalField.m_FieldID + "->" + strSection);
							break;
					}
				}
			} else {
				for (int m = 0; m < item.nArraySize; ++m) {
					TagLocalKLineData aRecord = new TagLocalKLineData();
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
								case 26:
									aRecord.open = field.GetInt32();
									// L.e("MyApp", strSection + "=" + aRecord.now);
									break;
								case 27:
									aRecord.high = field.GetInt32();
									// L.e("MyApp", strSection + "=" + aRecord.now);
									break;
								case 28:
									aRecord.low = field.GetInt32();
									// L.e("MyApp", strSection + "=" + aRecord.now);
									break;
								case 33:
									aRecord.clearPrice = field.GetInt32();
									// L.e("MyApp", strSection + "=" + aRecord.now);
									break;
								case 34:
									aRecord.close = field.GetInt32();
									// L.e("MyApp", strSection + "=" + aRecord.now);
									break;
								case 35:
									aRecord.volume = (long) field.GetDouble();
									// L.e("MyApp", strSection + "=" +
									// field.GetDouble());
									break;
								case 36:
									aRecord.amount = (long) field.GetDouble();
									// L.e("MyApp", strSection + "=" +
									// field.GetDouble());
									break;
								case 39:
									aRecord.ccl = field.GetDouble();
									// L.e("MyApp", strSection + "=" +
									// field.GetDouble());
									break;
								case 37:
									aRecord.volSell = field.GetDouble();
									// L.e("MyApp", strSection + "=" +
									// field.GetDouble());
									break;
								case 6001:
									aRecord.raiseNum = field.GetInt16();
									// L.e("MyApp", strSection + "=" + aRecord.now);
									break;
								case 6002:
									aRecord.fallNum = field.GetInt16();
									// L.e("MyApp", strSection + "=" + aRecord.now);
									break;
								default:
									// L.e("MyApp", "App unused field:" +
									// field.m_FieldID + "->" + strSection);
									break;
							}
						}
					}
					nKLineNum++;
					if (nKLineNum > TagLocalKLineData.MAX_KLINE_NUM) {
						L.e("nKLineNum > MAX_KLINE_NUM");
						break;
					}

					mKLineDataArray.add(aRecord);
					L.e("mKLineDataArray Add a record[" + nKLineNum + "]:" + aRecord.date + ", volume=" + aRecord.volume);
					aRecord = null;
				}
			}
		}

		L.e("MyApp", "End parseKLineData,nRecordCount=" + nKLineNum);
	}
	
	private void parseStockKLineData(byte[] szData, int nSize) {
		L.e("MyApp", "Start parseStockKLineData");

		int offset = 0;
		int wPackSize = MyByteBuffer.getInt(szData, offset);
		offset += 4;
		if (wPackSize > nSize - offset || nSize == 0) {
			L.e("MyApp", "ERROR parseKLineData data error");
			return;
		}

		CPbDataPackage pack = new CPbDataPackage();
		CPbDataDecode.DecodeOnePackage(szData, offset, wPackSize, pack);

		mStockKLineDataArray.clear();
		int nKLineNum = 0;
		for (int i = 0; i < pack.m_nItemSize; ++i) {
			CPbDataItem item = pack.m_DataItems.get(i);
			if (item.m_ItemType == CPbDataItem.DIT_NORMAL) {
				if (item.m_NormalField.IsValid()) {
					String strSection = item.m_NormalField.m_szFieldName;
					switch (item.m_NormalField.m_FieldID) {
						case 10:
							// code
							L.e("MyApp", strSection + "=" + item.m_NormalField.GetString());
							break;
						case 11:
							// int nMarketId = item.m_NormalField.GetInt16();
							L.e("MyApp", strSection + "=" + item.m_NormalField.GetInt16());
							break;
						case 46:
							// int nKlineType = item.m_NormalField.GetInt8();
							L.e("MyApp", strSection + "=" + item.m_NormalField.GetInt32());
							break;
						case 1018:
							// int nKLineWeight = item.m_NormalField.GetInt8();
							L.e("MyApp", strSection + "=" + item.m_NormalField.GetInt32());
							break;
						default:
							L.e("MyApp", "App unused field:" + item.m_NormalField.m_FieldID + "->" + strSection);
							break;
					}
				}
			} else {
				for (int m = 0; m < item.nArraySize; ++m) {
					TagLocalKLineData aRecord = new TagLocalKLineData();
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
								case 26:
									aRecord.open = field.GetInt32();
									// L.e("MyApp", strSection + "=" + aRecord.now);
									break;
								case 27:
									aRecord.high = field.GetInt32();
									// L.e("MyApp", strSection + "=" + aRecord.now);
									break;
								case 28:
									aRecord.low = field.GetInt32();
									// L.e("MyApp", strSection + "=" + aRecord.now);
									break;
								case 33:
									aRecord.clearPrice = field.GetInt32();
									// L.e("MyApp", strSection + "=" + aRecord.now);
									break;
								case 34:
									aRecord.close = field.GetInt32();
									// L.e("MyApp", strSection + "=" + aRecord.now);
									break;
								case 35:
									aRecord.volume = (long) field.GetDouble();
									// L.e("MyApp", strSection + "=" +
									// field.GetDouble());
									break;
								case 36:
									aRecord.amount = (long) field.GetDouble();
									// L.e("MyApp", strSection + "=" +
									// field.GetDouble());
									break;
								case 39:
									aRecord.ccl = field.GetDouble();
									// L.e("MyApp", strSection + "=" +
									// field.GetDouble());
									break;
								case 37:
									aRecord.volSell = field.GetDouble();
									// L.e("MyApp", strSection + "=" +
									// field.GetDouble());
									break;
								case 6001:
									aRecord.raiseNum = field.GetInt16();
									// L.e("MyApp", strSection + "=" + aRecord.now);
									break;
								case 6002:
									aRecord.fallNum = field.GetInt16();
									// L.e("MyApp", strSection + "=" + aRecord.now);
									break;
								default:
									// L.e("MyApp", "App unused field:" +
									// field.m_FieldID + "->" + strSection);
									break;
							}
						}
					}
					nKLineNum++;
					if (nKLineNum > TagLocalKLineData.MAX_KLINE_NUM) {
						L.e("nKLineNum > MAX_KLINE_NUM");
						break;
					}

					mStockKLineDataArray.add(aRecord);
					L.e("mStockKLineDataArray Add a record[" + nKLineNum + "]:" + aRecord.date + ", volume=" + aRecord.volume);
					aRecord = null;
				}
			}
		}

		L.e("MyApp", "End parseStockKLineData,nRecordCount=" + nKLineNum);
	}
	
	//标的推送记录
	public void setStockDataForTLine(TagLocalStockData stockData, boolean bPush) {
		if (bPush) {
			TagLocalTrendData aRecord = new TagLocalTrendData();
			aRecord.time = stockData.HQData.nUpdateTime / 100000;
			aRecord.volume = stockData.HQData.currentCJ;
			aRecord.now = stockData.HQData.nLastPrice;
			int nTrendNum = mTrendStockDataArray.size();
			if (nTrendNum == 0 || aRecord.time >= mTrendStockDataArray.get(mTrendStockDataArray.size() - 1).time) {
				// 如果是新一分钟，添加新走势记录，如果是同一分钟，更新最新一笔的数据
				if (nTrendNum == 0 || aRecord.time > mTrendStockDataArray.get(mTrendStockDataArray.size() - 1).time) {
					if (nTrendNum > TagLocalTrendData.MAX_TREND_NUM) {
						L.e("nTrendNum > MAX_TREND_NUM");
						return;
					}
					mTrendStockDataArray.add(aRecord);
				} else {
					TagLocalTrendData tempRecord = mTrendStockDataArray.get(mTrendStockDataArray.size() - 1);
					tempRecord.volume += aRecord.volume;
					tempRecord.time = aRecord.time;
					tempRecord.now = aRecord.now;
				}
			}
		}
	}

	//合约推送记录
	public void setOptionDataForTLine(TagLocalStockData stockData, boolean bPush) {
		if (bPush) {
			TagLocalTrendData aRecord = new TagLocalTrendData();
			aRecord.time = stockData.HQData.nUpdateTime / 100000;
			aRecord.volume = stockData.HQData.currentCJ;
			aRecord.now = stockData.HQData.nLastPrice;
			int nTrendNum = mTrendDataArray.size();
			if (nTrendNum == 0 || aRecord.time >= mTrendDataArray.get(mTrendDataArray.size() - 1).time) {
				// 如果是新一分钟，添加新走势记录，如果是同一分钟，更新最新一笔的数据
				if (nTrendNum == 0 || aRecord.time > mTrendDataArray.get(mTrendDataArray.size() - 1).time) {
					if (nTrendNum > TagLocalTrendData.MAX_TREND_NUM) {
						L.e("nTrendNum > MAX_TREND_NUM");
						return;
					}
					mTrendDataArray.add(aRecord);
				} else {
					TagLocalTrendData tempRecord = mTrendDataArray.get(mTrendDataArray.size() - 1);
					tempRecord.volume += aRecord.volume;
					tempRecord.time = aRecord.time;
					tempRecord.now = aRecord.now;
				}
			}
		}
	}

	public void setOptionDataForTDetail(TagLocalStockData stockData, boolean bPush) {
		if (bPush) {
			TagLocalDealData aRecord = new TagLocalDealData();
			aRecord.time = mOptionData.HQData.nUpdateTime / 1000;
			aRecord.volume = mOptionData.HQData.currentCJ;

			int nDetailNum = mDealDataArray.size();
			if ((nDetailNum == 0 || aRecord.time > mDealDataArray.get(nDetailNum - 1).time) && aRecord.volume > 0) {
				aRecord.now = mOptionData.HQData.nLastPrice;
				aRecord.inoutflag = (byte) (stockData.HQData.nTradeDirect);

				if (nDetailNum >= TagLocalDealData.MAX_NUM_DETAILDATA_ARRAY) {
					mDealDataArray.remove(0);
				}
				mDealDataArray.add(aRecord);
			}
			String log = String.format("aRecord time=%d,now=%d,volume=%f", aRecord.time, aRecord.now, aRecord.volume);
			L.i(TAG, log);
		}
	}

	public void setOptionDataForKLine(TagLocalStockData stockData, boolean bPush) {
		if (bPush) {
			int nKLineNum = mKLineDataArray.size();
			if (nKLineNum == 0 || stockData.HQData.nUpdateDate > mKLineDataArray.get(nKLineNum - 1).date) {
				if (nKLineNum > TagLocalKLineData.MAX_KLINE_NUM) {
					L.e("nKLineNum > MAX_KLINE_NUM");
					return;
				}
				TagLocalKLineData aRecord = new TagLocalKLineData();
				aRecord.date = stockData.HQData.nUpdateDate;
				aRecord.volume = (long) stockData.HQData.volume;
				aRecord.amount = (long) stockData.HQData.amount;
				aRecord.high = stockData.HQData.nHighPrice;
				aRecord.low = stockData.HQData.nLowPrice;
				aRecord.open = stockData.HQData.nOpenPrice;
				aRecord.close = stockData.HQData.nLastPrice;

				mKLineDataArray.add(aRecord);
			} else if (stockData.HQData.nUpdateDate == mKLineDataArray.get(nKLineNum - 1).date) {
				// 如果是最新一天的数据，最新数据
				TagLocalKLineData tempRecord = mKLineDataArray.get(nKLineNum - 1);

				tempRecord.volume = (long) stockData.HQData.volume;
				tempRecord.amount = (long) stockData.HQData.amount;
				if (stockData.HQData.nHighPrice != 0) {
					tempRecord.high = stockData.HQData.nHighPrice;
				}
				if (stockData.HQData.nLowPrice != 0) {
					tempRecord.low = mOptionData.HQData.nLowPrice;
				}
				tempRecord.close = mOptionData.HQData.nLastPrice;
			}
		}
	}
	
	public void setStockDataForStockKLine(TagLocalStockData stockData, boolean bPush) {
		if (bPush) {
			int nKLineNum = mStockKLineDataArray.size();
			if (nKLineNum == 0 || stockData.HQData.nUpdateDate > mStockKLineDataArray.get(nKLineNum - 1).date) {
				if (nKLineNum > TagLocalKLineData.MAX_KLINE_NUM) {
					L.e("nKLineNum > MAX_KLINE_NUM");
					return;
				}
				TagLocalKLineData aRecord = new TagLocalKLineData();
				aRecord.date = stockData.HQData.nUpdateDate;
				aRecord.volume = (long) stockData.HQData.volume;
				aRecord.amount = (long) stockData.HQData.amount;
				aRecord.high = stockData.HQData.nHighPrice;
				aRecord.low = stockData.HQData.nLowPrice;
				aRecord.open = stockData.HQData.nOpenPrice;
				aRecord.close = stockData.HQData.nLastPrice;

				mStockKLineDataArray.add(aRecord);
			} else if (stockData.HQData.nUpdateDate == mStockKLineDataArray.get(nKLineNum - 1).date) {
				// 如果是最新一天的数据，最新数据
				TagLocalKLineData tempRecord = mStockKLineDataArray.get(nKLineNum - 1);

				tempRecord.volume = (long) stockData.HQData.volume;
				tempRecord.amount = (long) stockData.HQData.amount;
				if (stockData.HQData.nHighPrice != 0) {
					tempRecord.high = stockData.HQData.nHighPrice;
				}
				if (stockData.HQData.nLowPrice != 0) {
					tempRecord.low = mStockData.HQData.nLowPrice;
				}
				tempRecord.close = mStockData.HQData.nLastPrice;
			}
		}
	}
	
	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			Intent intent = new Intent();
			String newid = mNewListDatas.get(position).NewsID;
			intent.putExtra("news_id", newid);
			int typeid=mNewListDatas.get(position).Type;
			intent.putExtra("typeid", typeid);
			
			String code = mStockData.HQData.code;
			short market = mStockData.HQData.market;
			intent.putExtra("news_code", code);
			intent.putExtra("news_market", market);
			intent.setClass(ScreenDetailActivity.this, NewsDetailActivity.class);
			startActivity(intent);
		}
		
	};

	/**
	 * 所有的
	 */
	OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.linearlayout_out_of_back:
					ScreenDetailActivity.this.finish();
					break;
				case R.id.screen_detail_header_right_refresh:
					/*********** 这里 用来 请求 数据 从新 刷新 界面 ***************/
					break;

				case R.id.sbutton02_add: {
					if (mIsMyStock) {// delete from my stock

						int position = -1;
						int size = mMyApp.getMyStockList(AppConstants.HQTYPE_QQ).size();
						for (int i = 0; i < size; i++) {
							if (mOptionData.code.equals(mMyApp.getMyStockList(AppConstants.HQTYPE_QQ).get(i).code) && mOptionData.market == mMyApp.getMyStockList(AppConstants.HQTYPE_QQ).get(i).market) {
								position = i;
								break;
							}
						}
						int ret = mMyApp.RemoveFromMyStock(position, AppConstants.HQTYPE_QQ);
						if (ret == 0) {
							mIsMyStock = false;
							mBottomAdd.setText("加自选");
							Toast.makeText(ScreenDetailActivity.this, "该自选股已删除！", Toast.LENGTH_LONG).show();
						}

					} else {// add to my stock
						int ret = mMyApp.AddtoMyStock(mOptionData, AppConstants.HQTYPE_QQ);
						if (ret == 0) {
							mIsMyStock = true;
							mBottomAdd.setText("删除自选");

							Toast.makeText(ScreenDetailActivity.this, "已添加到自选股！", Toast.LENGTH_LONG).show();
						} else if (ret == -1) {
							Toast.makeText(ScreenDetailActivity.this, "自选股已存在！", Toast.LENGTH_LONG).show();
						} else if (ret == -2) {
							Toast.makeText(ScreenDetailActivity.this, "自选股超过最大限制！", Toast.LENGTH_LONG).show();
						}
					}
				}
					break;
				case R.id.sbutton01_trade: {
					mMyApp.setCurrentOption(mOptionCodeInfo);
					mMyApp.mbDirectInOrderPage = true; // enable this flag for enter order page.

					Intent intent = new Intent();
					MainTabActivity act = AppActivityManager.getAppManager().getMainTabActivity();

					if (act != null) {
						act.setChangePage(MainTabActivity.PAGE_TRADE);
					}
					intent.setClass(ScreenDetailActivity.this, MainTabActivity.class);
					startActivity(intent);
					finish();
				}
					break;
				default:
					break;
			}

		}
	};
	
	private void changeNewAndReportView(int index)
	{
		if (mNewReportPageIndex != index)
		{
			mNewReportPageIndex = index;
			mNewListDatas.clear();
			for (int i = 0; i < mNewReportList.size(); i++) {
				L.e("-----------------------sss", mNewReportList.get(i).Title+",,"+mNewReportList.get(i).Type);
				
				switch(mNewReportPageIndex)
				{
				case 0:
					if(mNewReportList.get(i).Type==1)
					{
						mNewListDatas.add(mNewReportList.get(i));
					}
					break;
					
				case 1:
					if(mNewReportList.get(i).Type==2){
						mNewListDatas.add(mNewReportList.get(i));
					}
					break;
				default:
					break;
				}
			}
			mHandler.sendEmptyMessage(REFRESH_UI);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {

		switch (checkedId) {
		case R.id.detail_rb_gainloss:
			onChangeView(VIEW_GAINLOSS, true);
			break;
		case R.id.detail_rb_trendline:
			onChangeView(VIEW_TLINE, true);
			break;
		case R.id.detail_rb_kline:
			onChangeView(VIEW_KLINE, true);
			break;

		case R.id.detail_rb_stockkline:
			onChangeView(VIEW_STOCKKLINE, true);
			break;

		case R.id.radiobutton_news:
			changeNewAndReportView(0);
			break;

		case R.id.radiobutton_research_report:
			changeNewAndReportView(1);
			break;

		default:
			break;
		}
	}

	double nLenStart = 0; 
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (mViewType == VIEW_TLINE) {
			mTrendLineView.onTouchLine(event);
		} else if (mViewType == VIEW_KLINE) {
			mKLineView.onTouchLine(event);
		} else if (mViewType == VIEW_STOCKKLINE) {
			mStockKLineView.onTouchLine(event);
		}

		int nCnt = event.getPointerCount();
		if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN
				&& 2 == nCnt)// 2表示两个手指
		{

			int xlen = Math.abs((int) event.getX(0) - (int) event.getX(1));
			int ylen = Math.abs((int) event.getY(0) - (int) event.getY(1));

			nLenStart = Math.sqrt((double) xlen * xlen + (double) ylen * ylen);

		} else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP
				&& 2 == nCnt) 
		{

			int xlen = Math.abs((int) event.getX(0) - (int) event.getX(1));
			int ylen = Math.abs((int) event.getY(0) - (int) event.getY(1));

			double nLenEnd = Math.sqrt((double) xlen * xlen + (double) ylen
					* ylen);

			if (mViewType == VIEW_KLINE) {
				L.d(TAG, "onFling--->mViewType = VIEW_KLINE");
				mKLineView.onScaleLine((float) (nLenEnd - nLenStart));
			} else if (mViewType == VIEW_STOCKKLINE) {
				mStockKLineView.onScaleLine((float) (nLenEnd - nLenStart));
			}
		}

		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		L.d(TAG, "onFling");
		float dy = e1.getY()-e2.getY();
		if(mViewType == VIEW_KLINE) {
			L.d(TAG, "onFling--->mViewType = VIEW_KLINE");
			mKLineView.onScaleLine(dy);
		} else if(mViewType == VIEW_STOCKKLINE)
		{
			mStockKLineView.onScaleLine(dy);
		}
		
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if(mViewType == VIEW_KLINE) {
			
			//弹出框时，只在当前屏幕的K线中，移动标尺
			if(mKLineView.mPopinfoFlag == true)
			{
//				L.d("123", "--- onScroll --- " + e1.getX() + ", " + e2.getX() + " -- "+distanceX);
				if (distanceX>0.0 || distanceX<0.0)
					mKLineView.onMoveLine(e2);
				return true;
			}

			mKLineView.onScrollLine(e1.getY(), distanceX, distanceY);
		} else if (mViewType == VIEW_STOCKKLINE)
		{
			//弹出框时，只在当前屏幕的K线中，移动标尺
			if(mStockKLineView.mPopinfoFlag == true)
			{
//				L.d("123", "--- onScroll --- " + e1.getX() + ", " + e2.getX() + " -- "+distanceX);
				if (distanceX>0.0 || distanceX<0.0)
					mStockKLineView.onMoveLine(e2);
				return true;
			}

			mStockKLineView.onScrollLine(e1.getY(), distanceX, distanceY);
		}
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}
}
