package com.pengbo.mhdzq.zq_activity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.view.Window;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import com.pengbo.mhdcx.bean.News;
import com.pengbo.mhdcx.utils.HttpUtil;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.KLinePopWindowAdapter;
import com.pengbo.mhdzq.adapter.ZQNewsReportAdapter;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.CCodeTableItem;
import com.pengbo.mhdzq.data.CDataCodeTable;
import com.pengbo.mhdzq.data.CMarketInfoItem.mktGroupInfo;
import com.pengbo.mhdzq.data.CPbDataDecode;
import com.pengbo.mhdzq.data.CPbDataField;
import com.pengbo.mhdzq.data.CPbDataItem;
import com.pengbo.mhdzq.data.CPbDataPackage;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalDealData;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.data.TagLocalKLineData;
import com.pengbo.mhdzq.data.TagLocalStockBaseInfoRecord;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.TagLocalTrendData;
import com.pengbo.mhdzq.fragment.JiaoYiChiCangPager;
import com.pengbo.mhdzq.fragment.ZhengQuanFragment;
import com.pengbo.mhdzq.net.CMessageObject;
import com.pengbo.mhdzq.net.GlobalNetConnect;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.net.MyByteBuffer;
import com.pengbo.mhdzq.systembartint.SystemBarTintManager;
import com.pengbo.mhdzq.tools.ColorConstant;
import com.pengbo.mhdzq.tools.DataTools;
import com.pengbo.mhdzq.tools.KdateTools;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.view.AutoScaleTextView;
import com.pengbo.mhdzq.view.MoreKLinePopWindow;
import com.pengbo.mhdzq.view.MoreKLinePopWindow.PopWindowCallBack;
import com.pengbo.mhdzq.view.ZQKLineView;
import com.pengbo.mhdzq.view.ZQTrendLineView;
import com.pengbo.mhdzq.zq_trade_activity.ZqTradeDetailActivity;

/**
 * 证券详情页 
 * 
 * @author pobo
 * @date   2015-10-30 下午5:45:54
 * @className ZQMarketDetailActivity.java
 * @verson 1.0.0
 */
public class ZQMarketDetailActivity extends HdActivity implements OnClickListener,OnCheckedChangeListener, OnGestureListener{
	private static final String TAG   =   "ZQMarketDetailActivity";
	SystemBarTintManager tintManager;
	public static TagCodeInfo mOptionCodeInfo;// 合约
	private TagLocalStockData mOptionData;// 合约
	private TagLocalStockBaseInfoRecord mStockBaseInfoRecord;
	private MyApp mMyApp;
	
	private ImageView mBack;//返回按钮 
	private ImageView mRefersh;//刷新按钮
	private TextView mTVMidHeadUp,mTVMidHeadDown;
	private LinearLayout mLlayUpDown;//抬头中包含 名称  代码    和是否在交易时间内的外布局
	private RelativeLayout mRlayBcg;//抬头  为了设置 涨跌背景用 
	private View mRlayBcgZDBJ;
	private AutoScaleTextView mNow,mZD,mZDF,mJK,mZS,mCJL,mHSL;//现价      涨跌    涨跌幅   今开   昨收  成交量  换手率 
	//最高   最低      成交额      内盘  外盘   流通市值   市盈率    振幅    总市值  
	private AutoScaleTextView mZuiGao,mZuiDi,mChengJiaoE,mNeiPan,mWaiPan,mLiuTongShiZhi,mShiYingLv,mZhenFu,mZongShiZhi;

	public int  mRequestCode[];//0:走势    1:推送     2：Kline 3:detail	4:股票基础数据查询
	private MoreKLinePopWindow mPopWindow;//点击更多查看其他分钟下的K线 行情 
	private RadioButton mMoreRadioButton ;
	private KLinePopWindowAdapter mKLinePopAdapter;
	String[] strklineType = new String[]{
			"1分钟","3分钟","5分钟","15分钟","30分钟","60分钟","120分钟","240分钟"
	};
	
	int [] klineTypes = new int[]{ ViewTools.VIEW_KLINE_1M,
			                      ViewTools.VIEW_KLINE_3M, 
			                      ViewTools.VIEW_KLINE_5M, 
			                      ViewTools.VIEW_KLINE_15M, 
			                      ViewTools.VIEW_KLINE_30M, 
			                      ViewTools.VIEW_KLINE_60M, 
			                      ViewTools.VIEW_KLINE_120M, 
			                      ViewTools.VIEW_KLINE_240M };
	
	GestureDetector mGestureDetector;
	
	private ZQTrendLineView mTrendLineView;
	private ZQKLineView mKLineView;
	public int mViewType = ViewTools.VIEW_TRENDLINE;
	
	public RadioGroup mRgTrendKline;//包括分时 K线的 RadioGroup 
	private FrameLayout mDrawFrameLayout;
	private ViewFlipper mFlipper;
	

	private ArrayList<TagLocalTrendData> mTrendDataArray;// 走势数据
	private ArrayList<TagLocalKLineData> mKLineDataArray;
	private ArrayList<TagLocalKLineData> mKLineWeekArray;
	private ArrayList<TagLocalKLineData> mKLineMonthArray;
	private ArrayList<TagLocalKLineData> mKLineMinArray;//3,15,30,240min
	private ArrayList<TagLocalKLineData> mKLineByTrendArray;//走势线解析成分钟k线
	private ArrayList<TagLocalDealData> mDealDataArray;
	
	private int mKLineOrTrendSuccess = 0; //kline 请求或者走势请求是否成功标志
	private int mCurrentDayKNum = 0;//交易日当天已有的k线个数（分钟线用）
	public static final int KLINE_SUCCESS = 1;
	public static final int TREND_SUCCESS = 2;
	public static final int KLINE_TREND_ALL = 3;
	
	private boolean mIsMyStock;//是否是自选股中的证券 
	private Button mBtnZiXuan,mBtnBuy,mBtnSell;//初始化底部的   自选     买入  卖出  按钮 
	
	private RadioGroup mRadioGroupNews;
	private ZQNewsReportAdapter mNewsAdapter;
	private ListView mListViewNews;
	
	public List<News> mAllNewsList; //读取  新闻  和研报 
	private List<News> mListNewsData; //存储是  新闻 还是研报的集合 
	private int mNewReportPageIndex = 0; //0-news;1-report;
	protected static final int REFRESH_UI = -1;
	private final static int REQUEST_CODE = 999;
	
	private Handler mHandler=new Handler(){


		public void handleMessage(android.os.Message msg) {

			int nFrameType = msg.arg1;
			int nRequestCode = msg.arg2;
			switch (msg.what) {
			//&& mListNewsData.size() != 0
				case REFRESH_UI:
					if (mNewsAdapter != null) {
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
					if(nFrameType == Global_Define.MFT_MOBILE_DATA_APPLY){
						if (aMsgObject.nErrorCode != 0 && mRequestCode[2] != nRequestCode) {
							return;
						}
						if(mRequestCode[0] == nRequestCode){//走势 
							parseTrendData(aMsgObject.getData(), aMsgObject.getDataLength(), true);

							if(mViewType == ViewTools.VIEW_TRENDLINE){
								mTrendLineView.updateData(mOptionData, null);
								mTrendLineView.updateAllView();
							} else {
								if (mViewType == ViewTools.VIEW_KLINE_1M) {
									reParse1MinKLineDataWithTrend();
									mKLineOrTrendSuccess |= TREND_SUCCESS;
								} else if (mViewType == ViewTools.VIEW_KLINE_5M) {
									reParse5MinKLineDataWithTrend();
									mKLineOrTrendSuccess |= TREND_SUCCESS;
								} else if (mViewType == ViewTools.VIEW_KLINE_60M) {
									reParse60MinKLineDataWithTrend();
									mKLineOrTrendSuccess |= TREND_SUCCESS;
								} else if (mViewType == ViewTools.VIEW_KLINE_3M) {
									Parse3MinKLineDataWithTrend(3);
									reParseMinKLineData(Global_Define.HISTORY_TYPE_3MIN);
									mKLineOrTrendSuccess |= TREND_SUCCESS;
								} else if (mViewType == ViewTools.VIEW_KLINE_15M) {
									Parse3MinKLineDataWithTrend(15);
									reParseMinKLineData(Global_Define.HISTORY_TYPE_15MIN);
									mKLineOrTrendSuccess |= TREND_SUCCESS;
								} else if (mViewType == ViewTools.VIEW_KLINE_30M) {
									Parse3MinKLineDataWithTrend(30);
									reParseMinKLineData(Global_Define.HISTORY_TYPE_30MIN);
									mKLineOrTrendSuccess |= TREND_SUCCESS;
								} else if (mViewType == ViewTools.VIEW_KLINE_120M) {
									Parse3MinKLineDataWithTrend(120);
									reParseMinKLineData(Global_Define.HISTORY_TYPE_120MIN);
									mKLineOrTrendSuccess |= TREND_SUCCESS;
								} else if (mViewType == ViewTools.VIEW_KLINE_240M) {
									Parse3MinKLineDataWithTrend(240);
									reParseMinKLineData(Global_Define.HISTORY_TYPE_240MIN);
									mKLineOrTrendSuccess |= TREND_SUCCESS;
								}
								if (mKLineView != null) {
									mKLineView.resetKLineParam();
									mKLineView.updateData(mOptionData);
									mKLineView.updateAllData();
								}
							}
							
						}else if(mRequestCode[2] == nRequestCode){   // K 线数据请求 
							if(mViewType == ViewTools.VIEW_TRENDLINE)
							{
								return;
							}
							L.e("TEST", "mViewType"+mViewType);
							parseKLineData(aMsgObject.getData(), aMsgObject.getDataLength());
							mKLineOrTrendSuccess |= KLINE_SUCCESS;
							if (mViewType == ViewTools.VIEW_KLINE_1M) {
								requestTrendLine();
							}
							else if (mViewType == ViewTools.VIEW_KLINE_5M)
							{
								requestTrendLine();
							}
							else if(mViewType ==ViewTools. VIEW_KLINE_60M)
							{
								requestTrendLine();
							}
							else if (mViewType == ViewTools.VIEW_KLINE_DAY) {
								setOptionDataForKLine(mOptionData, true,
										mKLineDataArray);
								
							}else if (mViewType == ViewTools.VIEW_KLINE_WEEK) {
								reParseWeekKLineData();
								setOptionDataForWeekKLine(mOptionData, true,
										mKLineWeekArray);
							} else if (mViewType == ViewTools.VIEW_KLINE_MONTH) {
								reParseMonthKLineData();
								setOptionDataForMonthKLine(mOptionData, true,
										mKLineMonthArray);
							} else if (mViewType == ViewTools.VIEW_KLINE_3M
									|| mViewType == ViewTools.VIEW_KLINE_15M
									|| mViewType == ViewTools.VIEW_KLINE_30M
									|| mViewType == ViewTools.VIEW_KLINE_120M
									|| mViewType == ViewTools.VIEW_KLINE_240M) {
								requestTrendLine();
							}
							
							if( ((mKLineOrTrendSuccess & KLINE_TREND_ALL) == KLINE_TREND_ALL )
									||mViewType == ViewTools.VIEW_KLINE_DAY 
									||  mViewType == ViewTools.VIEW_KLINE_WEEK 
									|| mViewType ==  ViewTools.VIEW_KLINE_MONTH ){							
								if(mKLineView != null){
									mKLineView.resetKLineParam();
									mKLineView.updateData(mOptionData);
									mKLineView.updateAllData();
								}
								
							}
						}
						else if (mRequestCode[3] == nRequestCode)// detail
						{
							parseDealData(aMsgObject.getData(), aMsgObject.getDataLength());
							if (mViewType == ViewTools.VIEW_TRENDLINE) {
								mTrendLineView.updateAllView();// update view
							}
						}
						else if(mRequestCode[4] == nRequestCode)//股票基础数据
						{
							parseStockInfoData(aMsgObject.getData(), aMsgObject.getDataLength());
							updataMiddleTextView();
						}
					}else if(nFrameType ==  Global_Define.MFT_MOBILE_PUSH_DATA){
						TagLocalStockData optionStockData = new TagLocalStockData();
						if (mMyApp.mHQData_ZQ.getData(optionStockData, mOptionCodeInfo.market, mOptionCodeInfo.code, false)) {
							mOptionData=optionStockData;
							updataMiddleTextView();
							
							boolean bNeedUpdateKLine = true;
							if (mViewType == ViewTools.VIEW_TRENDLINE){
								if (mTrendLineView != null){
									setOptionDataForTLine(optionStockData, true);
									setOptionDataForTDetail(optionStockData, true);
									mTrendLineView.updateData(mOptionData, null);
									mTrendLineView.updateAllView();// update trend
								}
							} else 
							{
								if (mViewType == ViewTools.VIEW_KLINE_DAY) {
									setOptionDataForKLine(mOptionData, true,mKLineDataArray);
								} else if (mViewType == ViewTools.VIEW_KLINE_WEEK) {
									setOptionDataForWeekKLine(mOptionData, true,mKLineWeekArray);
								} else if (mViewType == ViewTools.VIEW_KLINE_MONTH){
									setOptionDataForMonthKLine(mOptionData, true,mKLineMonthArray);
								} else if(mViewType == ViewTools.VIEW_KLINE_3M ||
										  mViewType == ViewTools.VIEW_KLINE_15M||
										  mViewType == ViewTools.VIEW_KLINE_30M||
										  mViewType == ViewTools.VIEW_KLINE_120M||
										  mViewType == ViewTools.VIEW_KLINE_240M){
									if (mKLineOrTrendSuccess == KLINE_TREND_ALL){
										setOptionDataForMinKLine(mOptionData, true,
										mKLineMinArray);
										bNeedUpdateKLine = true;
									}else{
										bNeedUpdateKLine = false;
									}
									
								} else if(mViewType == ViewTools.VIEW_KLINE_1M  || 
										  mViewType == ViewTools.VIEW_KLINE_5M  || 
										  mViewType == ViewTools.VIEW_KLINE_60M ){
									
									if (mKLineOrTrendSuccess == KLINE_TREND_ALL){
										setOptionDataFor1MinKLine(mOptionData, true,mKLineDataArray);
										bNeedUpdateKLine = true;
									}else{
										bNeedUpdateKLine = false;
									}
									
								}
								
								if(mKLineView != null)
								{
									mKLineView.updateData(mOptionData);
									if(bNeedUpdateKLine)
									{
										mKLineView.updateAllData();
									}
								}
							}
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
		
		setContentView(R.layout.zq_market_detail_activity);
		//判断此页面是否有状态栏 
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
		}
		
		tintManager = new SystemBarTintManager(this);
		//激活状态栏 
		tintManager.setStatusBarTintEnabled(true);
		//激活导航栏 
		tintManager.setNavigationBarTintEnabled(true);
		
		 //设置系统栏设置颜色
      //  tintManager.setTintColor(R.color.blue);
        //给状态栏设置颜色
        tintManager.setStatusBarTintResource(R.color.transparent);
        // 设置导航栏设置资源
       // tintManager.setNavigationBarTintResource(R.color.blue);
		
		mMyApp = (MyApp) this.getApplication();	
		
		
		initData();
		initView();
		initDrawTrendAndKline();
		upDateDrawTrendAndKline();
		
		
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_CODE){
			if(resultCode == ZQLandscapeViewActivity.REQUEST_CODE){
				//this.mViewType = data.getIntExtra(ZQLandscapeViewActivity.FLAG, ViewTools.VIEW_TRENDLINE);
				L.e("onActivityResult++", "===="+mViewType);
			}
		}
	}

    
    
    private void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}


	private void initData() {
		
		mViewType = ViewTools.VIEW_TRENDLINE;//初始化进入的是分时图
		mIsMyStock = false;
		mAllNewsList = new ArrayList<News>();
		//手势识别类 
		mGestureDetector = new GestureDetector(this);
		mRequestCode = new int[5];
		//走势的数据 
		mTrendDataArray = mMyApp.getTrendDataArray();
		mKLineDataArray = mMyApp.getKLineDataArray();
		mKLineWeekArray = mMyApp.getKLineWeekArray();
		mKLineMonthArray = mMyApp.getKLineMonthArray();
		mKLineMinArray = mMyApp.getKLineMinArray();
		mDealDataArray = mMyApp.getDealDataArray();
		
		mKLineByTrendArray = new ArrayList<TagLocalKLineData> ();
		
		mDealDataArray.clear();
		mTrendDataArray.clear();
		mKLineDataArray.clear();
		mKLineWeekArray.clear();
		mKLineMonthArray.clear();
		mKLineMinArray.clear();
		
		setStockData();
	}


	/**
	 * 初始化控件  
	 */
	private void initView() {
		initMiddleTextView();
		updataMiddleTextView();	
		initBottomView();		
		initNewsAndReportView();
		
	}
	
	/**
	 * 初始化 新闻 和 研究 报告 控件
	 */
	private void initNewsAndReportView() {
		// 新闻公告 和 研究报告点击切换两个 按钮时 更新 List数据
		mRadioGroupNews = (RadioGroup) this.findViewById(R.id.rg_news_report);
		mRadioGroupNews.setOnCheckedChangeListener(this);
		if(mListViewNews == null){
			mListViewNews = (ListView) this.findViewById(R.id.zq_news_listview);
			if(mListNewsData == null){
				mListNewsData = new ArrayList<News>();
			}
			
			mNewsAdapter = new ZQNewsReportAdapter(this, mListNewsData);
			mListViewNews.setAdapter(mNewsAdapter);
			mListViewNews.setOnItemClickListener(mOnItemClickListener);
		}
		
	}
	
	
	
	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			Intent intent = new Intent();
			String newid = mListNewsData.get(position).NewsID;
			intent.putExtra("news_id", newid);
			int typeid=mListNewsData.get(position).Type;
			intent.putExtra("typeid", typeid);
			
			String code = mOptionData.HQData.code;
			short market = mOptionData.HQData.market;
			intent.putExtra("news_code", code);
			intent.putExtra("news_market", market);
			intent.setClass(ZQMarketDetailActivity.this, ZQDetailNewsDetailActivity.class);
			startActivity(intent);
		}
		
	};
	/**
	 * 初始化底部的   自选     买入  卖出  按钮 
	 */
	private void initBottomView() {
		mBtnZiXuan = (Button) findViewById(R.id.btn_zq_market_deail_zixuan);
		mBtnBuy  =  (Button) findViewById(R.id.btn_zq_market_deail_buy);
		mBtnSell =  (Button) findViewById(R.id.btn_zq_market_deail_sell);
		
		if(mOptionCodeInfo != null){
			if (mMyApp.IsStockExist(mOptionCodeInfo.code, mOptionCodeInfo.market,AppConstants.HQTYPE_ZQ)) {
				mIsMyStock = true;
				mBtnZiXuan.setText("删除自选");
			} else {
				mIsMyStock = false;
				mBtnZiXuan.setText("加自选");
			}
		}else{
			mIsMyStock = false;
			mBtnZiXuan.setText("加自选");
		}
		
		
		mBtnZiXuan.setOnClickListener(this);
		mBtnBuy.setOnClickListener(this);
		mBtnSell.setOnClickListener(this);
	}


	/**
	 * 初始化画图上的东西
	 */
	private void initDrawTrendAndKline() {
		mRgTrendKline = (RadioGroup) findViewById(R.id.rg_trend_kline);//分时 K线 中的各个分钟线    切换 包裹的 RadioGroup
		mMoreRadioButton = (RadioButton) findViewById(R.id.rb_one_minute);//点击能探出更多K线的 RadioButton 		
		mDrawFrameLayout  =  (FrameLayout) this.findViewById(R.id.zq_trend_kline_framelayout);			
		mFlipper  =  (ViewFlipper) this.findViewById(R.id.zq_trend_kline_flipper);
	}
	
	
	private void upDateDrawTrendAndKline() {
		mDrawFrameLayout.setOnClickListener(this);
		mMoreRadioButton.setOnClickListener(this);
		mRgTrendKline.setOnCheckedChangeListener(this);
		
		if( mFlipper != null){
			mFlipper.removeAllViews();
		}		
		if(mViewType == ViewTools.VIEW_TRENDLINE){
			if(mTrendLineView == null){
				mTrendLineView = new ZQTrendLineView(this, true);
				mTrendLineView.updateData(mOptionData, null);
				L.e("upDateDrawTrendAndKline-------mTrendLineView", "分时");
			}
			onChangeView(ViewTools.VIEW_TRENDLINE, true);		
			mFlipper.addView(mTrendLineView);
		}else if(mViewType == ViewTools.VIEW_KLINE_DAY || 
				mViewType == ViewTools.VIEW_KLINE_WEEK ||
				mViewType == ViewTools.VIEW_KLINE_MONTH ||
				mViewType == ViewTools.VIEW_KLINE_1M ||
				mViewType == ViewTools.VIEW_KLINE_3M  ||
				mViewType == ViewTools.VIEW_KLINE_5M  ||
				mViewType == ViewTools.VIEW_KLINE_15M ||
				mViewType == ViewTools.VIEW_KLINE_30M ||
				mViewType == ViewTools.VIEW_KLINE_60M ||
				mViewType == ViewTools.VIEW_KLINE_120M ||
				mViewType == ViewTools.VIEW_KLINE_240M
				){
			
			if(mKLineView == null){
				mKLineView = new ZQKLineView(this, true);
				mKLineView.updateData(mOptionData);
				L.e("upDateDrawTrendAndKline-------mKLineView", "K线 ");
			}
			onChangeView(mViewType, true);
			mFlipper.addView(mKLineView);
		}
	}


	/**
	 * 取行情的详细数据 先判断是否为空 若为空从码表拿对应的市场 code 由推送过来数据
	 */
	private void setStockData(){
		if (mOptionCodeInfo == null){
			return;
		}
		mOptionData = new TagLocalStockData();
		if (!mMyApp.mHQData_ZQ.getData(mOptionData, mOptionCodeInfo.market, mOptionCodeInfo.code, false))
		{
			ArrayList<CCodeTableItem> codeTableList = null;
			for (int j = 0; j < mMyApp.mCodeTableMarketNum && j < CDataCodeTable.MAX_SAVE_MARKET_COUNT; j++)
            {
                if (mOptionData.HQData.market == mMyApp.mCodeTable[j].mMarketId) {
                	codeTableList = mMyApp.mCodeTable[j].mCodeTableList;
                    break;
                }
            }
            
			if (codeTableList != null)
			{
				for(int m = 0; m < codeTableList.size(); m++)
				{
					CCodeTableItem item = codeTableList.get(m);
					if (mOptionData.HQData.market == item.market && mOptionData.HQData.code.equalsIgnoreCase(item.code))
                    {
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
		    groupRecord = mMyApp.mMarketInfo.searchMarketGroupInfo(mOptionData.HQData.market, null, (short)mOptionData.GroupOffset);
		    if (groupRecord != null)
		    {
		    	mOptionData.TradeFields = groupRecord.TradeFields;
		    	STD.memcpy(mOptionData.Start, groupRecord.Start, 4);
		    	STD.memcpy(mOptionData.End, groupRecord.End, 4);
		    	mOptionData.GroupFlag = groupRecord.Flag;
		    }
		    else
		    {
		    	L.e(TAG,"ERROR: MarketInfo.search failed" + ",marketId="+ mOptionData.HQData.market + ",code=" + mOptionData.HQData.code);
		    }
		    mMyApp.mHQData_ZQ.addRecord(mOptionData,false);
		}
	}
	

	/**
	 * 初始化 现价  涨跌幅   及 所有详细的字段的 TextView
	 */
	private void initMiddleTextView() {
		mLlayUpDown  =  (LinearLayout) this.findViewById(R.id.llayout_middle_tv);
		mRlayBcg  =  (RelativeLayout) this.findViewById(R.id.include_zq_mystock_head);
		mRlayBcgZDBJ  =  this.findViewById(R.id.include_zq_mystock_zdbj);
		
		mBack  =  (ImageView) this.findViewById(R.id.img_public_black_head_title_left_white_back);
		mRefersh  =  (ImageView) this.findViewById(R.id.img_public_black_head_title_right_white_refresh);
		
		mTVMidHeadUp  =  (TextView) this.findViewById(R.id.llayout_middle_tv_up);
		mTVMidHeadDown  =  (TextView) this.findViewById(R.id.llayout_middle_tv_down);
		mLlayUpDown.setVisibility(View.VISIBLE);
		mBack.setVisibility(View.VISIBLE);
		mRefersh.setVisibility(View.VISIBLE);

		mBack.setOnClickListener(this);
		mRefersh.setOnClickListener(this);
		mNow = (AutoScaleTextView) this.findViewById(R.id.tv_detail_now_price);
		mZD = (AutoScaleTextView) this.findViewById(R.id.tv_detail_zd);
		mZDF  =  (AutoScaleTextView) this.findViewById(R.id.tv_detail_zdf);
		mJK  =  (AutoScaleTextView) this.findViewById(R.id.tv_detail_jinkai);
		mZS  =  (AutoScaleTextView) this.findViewById(R.id.tv_detail_zuoshou);
		mCJL  =  (AutoScaleTextView) this.findViewById(R.id.tv_detail_chengjiaoliang);
		mHSL  =  (AutoScaleTextView) this.findViewById(R.id.tv_detail_huanshoulv);
		

		
		//最高   最低      成交额      内盘  外盘   流通市值   市盈率    振幅    总市值  
		mZuiGao  =  (AutoScaleTextView) this.findViewById(R.id.tv_detail_zuigao_zhi);
		mZuiDi  =  (AutoScaleTextView) this.findViewById(R.id.tv_detail_zuidi_zhi);
		mChengJiaoE  =  (AutoScaleTextView) this.findViewById(R.id.tv_detail_chengjiaoe_zhi);
		mNeiPan  =  (AutoScaleTextView) this.findViewById(R.id.tv_detail_neipan_zhi);
		mWaiPan  =  (AutoScaleTextView) this.findViewById(R.id.tv_detail_waipan_zhi);
		mLiuTongShiZhi  =  (AutoScaleTextView) this.findViewById(R.id.tv_detail_liutongshizhi_zhi);
		mShiYingLv  =  (AutoScaleTextView) this.findViewById(R.id.tv_detail_shiyinglv_zhi);
		mZhenFu  =  (AutoScaleTextView) this.findViewById(R.id.tv_detail_zhenfu_zhi);
		mZongShiZhi  =  (AutoScaleTextView) this.findViewById(R.id.tv_detail_zongshizhi_zhi);
		
	}
	
	
	public static int getColorByFieldBcg(TagLocalStockData stockData, int nField) {
		int nColor = Color.rgb(90,90,90);  //当不涨不跌时的背景 5a5a5a
		switch (nField) {
		case Global_Define.FIELD_HQ_NOW:
			nColor = getColorBcg(stockData.HQData.nLastPriceForCalc
					- stockData.HQData.nLastClear);
			break;
		default:
			break;
		}

		return nColor;
	}
	
	
	public static int getColorByFieldBcgTinner(TagLocalStockData stockData, int nField) {
		int nColor = R.color.zq_5a5a5a;  //当不涨不跌时的背景 5a5a5a
		switch (nField) {
		case Global_Define.FIELD_HQ_NOW:
			nColor = getColorBcgTinner(stockData.HQData.nLastPriceForCalc
					- stockData.HQData.nLastClear);
			break;
		default:
			break;
		}

		return nColor;
	}
	
	public static int getColorBcg(int data) {
		return getColor(data, 0);
	}
	
	public static int getColorBcgTinner(int data) {
		return getColorTiner(data, 0);
	}
	
	
	public static int getColor(int data, int base) {
			int color=Color.rgb(90,90,90); //不涨不跌 //当不涨不跌时的背景 5a5a5a
			if (data == 0 && base != 0) {
				color = ColorConstant.ZQ_DETAIL_NULL;
			} else if (data > base) {
				color = ColorConstant.ZQ_DETAIL_PRICE_UP_ALL_RED;
			} else if (data < base) {
				color = ColorConstant.ZQ_DETAIL_PRICE_DOWN_ALL_GREEN;
			}
			return color;
		}
	
	
	public static int getColorTiner(int data, int base) {
		int color = R.color.zq_5a5a5a; //不涨不跌 //当不涨不跌时的背景 5a5a5a
		if (data == 0 && base != 0) {
			color = R.color.zq_5a5a5a;
		} else if (data > base) {
			color = R.color.zq_d42626;
		} else if (data < base) {
			color = R.color.zq_29b462;
		}
		return color;
	}

	
	
	
	private void updataMiddleTextView() {
		if (mOptionData == null)
		{
			return;
		}
		//判断背景的红绿 
		mRlayBcg.setBackgroundColor(getColorByFieldBcg(mOptionData, Global_Define.FIELD_HQ_NOW));
		mRlayBcgZDBJ.setBackgroundColor(getColorByFieldBcg(mOptionData, Global_Define.FIELD_HQ_NOW));
		tintManager.setStatusBarTintResource(getColorByFieldBcgTinner(mOptionData, Global_Define.FIELD_HQ_NOW));
		
		boolean isIn=KdateTools.IsInTradeTime(mOptionData.HQData.nUpdateTime/100000, mOptionData);
		
		Date date = new Date();
		int mHour = KdateTools.getHour(date);
		int mMinute = KdateTools.getMinute(date);
		int currenSecond = mHour*100 + mMinute;
		int startTime = 0;
		int endTime = 0 ;

		if (isIn) {
			mTVMidHeadDown.setText("交易中");
			//防止行情推送过来的最后一笔时间是在交易内     而系统的时间现在已经不再交易时间内了
			for (int i = 0; i < mOptionData.TradeFields; i++) {
				startTime = mOptionData.Start[i];
				endTime = mOptionData.End[i];
				if (currenSecond < startTime && currenSecond > endTime) {
					mTVMidHeadDown.setText("闭市");
				}
			}
		} else {
			mTVMidHeadDown.setText("闭市");			
		}

	    mTVMidHeadUp.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_NAME_ANSI)+"("+ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_CODE)+")");
	   
	   
		mNow.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_NOW));
		//mNow.setTextColor(ViewTools.getColorByFieldIDOther(mOptionData, Global_Define.FIELD_HQ_NOW));

		mZD.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_ZD));
		//mZD.setTextColor(ViewTools.getColorByFieldIDOther(mOptionData, Global_Define.FIELD_HQ_NOW));

		mZDF.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_ZDF_SIGN));
		//mZDF.setTextColor(ViewTools.getColorByFieldIDOther(mOptionData, Global_Define.FIELD_HQ_NOW));
		
		mJK.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_OPEN));
		//mJK.setTextColor(ViewTools.getColorByFieldIDOther(mOptionData, Global_Define.FIELD_HQ_OPEN));

		mZS.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_YESTERDAY));
		//mZS.setTextColor(ViewTools.getColorByFieldIDOther(mOptionData, Global_Define.FIELD_HQ_YESTERDAY));

		mCJL.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_VOLUME));
		
		mHSL.setText(ViewTools.getStringByFieldID(mOptionData, mStockBaseInfoRecord, Global_Define.FIELD_HQ_HSL));
		

		
		//最高   最低      成交额      内盘  外盘   流通市值   市盈率    振幅    总市值  
		
		mZuiGao.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_HIGH));
		//新需求中  最高  最低 的颜色 值  全部和 其他  一样    不要  红绿 
	//	mZuiGao.setTextColor(ViewTools.getColorByFieldID(mOptionData, Global_Define.FIELD_HQ_HIGH));
		
		mZuiDi.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_LOW));
	//	mZuiDi.setTextColor(ViewTools.getColorByFieldID(mOptionData, Global_Define.FIELD_HQ_LOW));
	
		mChengJiaoE.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_AMOUNT));

		mNeiPan.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_INVOLUME));
		
		
		mWaiPan.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_OUTVOLUME));
		mLiuTongShiZhi.setText(ViewTools.getStringByFieldID(mOptionData, mStockBaseInfoRecord, Global_Define.FIELD_HQ_LTSZ));			
		mShiYingLv.setText(ViewTools.getStringByFieldID(mOptionData, mStockBaseInfoRecord, Global_Define.FIELD_HQ_SYL));
		mZhenFu.setText(ViewTools.getStringByFieldID(mOptionData, mStockBaseInfoRecord, Global_Define.FIELD_HQ_ZHENFU));
		mZongShiZhi.setText(ViewTools.getStringByFieldID(mOptionData, mStockBaseInfoRecord, Global_Define.FIELD_HQ_ZSZ));
	
		
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
		super.onResume();
		switch (mViewType) {
		case ViewTools.VIEW_TRENDLINE:
			requestTrendLine();
			requestDetail();
			break;
		case ViewTools.VIEW_KLINE_DAY://日线 
		case ViewTools.VIEW_KLINE_WEEK://周线 
		case ViewTools.VIEW_KLINE_MONTH://月线 
			 requestKLine(0);
			break;
		case ViewTools.VIEW_KLINE_1M:
		case ViewTools.VIEW_KLINE_3M:
			requestKLine(1);
			break;
		case ViewTools.VIEW_KLINE_5M:
		case ViewTools.VIEW_KLINE_15M:
		case ViewTools.VIEW_KLINE_30M:
			requestKLine(2);
			break;
		case ViewTools.VIEW_KLINE_60M:
		case ViewTools.VIEW_KLINE_120M:
		case ViewTools.VIEW_KLINE_240M:
			requestKLine(3);
			break;
		default:
			break;
		}
		
		
		RequestHttpUtil();
		queryZQStockInfo();
		queryHQPushInfo();
		
	}
	
	
	
	
	private void RequestHttpUtil() {

		new Thread(new Runnable() {

			@Override
			public void run() {		
				{
					{
						mAllNewsList.clear();
						int num = HttpUtil.getNews(mOptionData.HQData.code, mOptionData.HQData.market, mAllNewsList);
						mListNewsData.clear();
						for (int i = 0; i < mAllNewsList.size(); i++) {
							
							switch(mNewReportPageIndex)
							{
							case 0:
								if(mAllNewsList.get(i).Type==1)
								{
									mListNewsData.add(mAllNewsList.get(i));
								}
								break;
								
							case 1:
								if(mAllNewsList.get(i).Type==2){
									mListNewsData.add(mAllNewsList.get(i));
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
	
	/**
	 * 点击更多时弹出 PopWindow 
	 */
	private void showPop(View mRadioButton) {
		mPopWindow = new MoreKLinePopWindow(ZQMarketDetailActivity.this, mRadioButton,false);
		mKLinePopAdapter = new KLinePopWindowAdapter(ZQMarketDetailActivity.this, strklineType);
		mPopWindow.setContent(mKLinePopAdapter);
		mPopWindow.setPopWindowCallback(popwindowcallback);
	}
	
	PopWindowCallBack popwindowcallback = new PopWindowCallBack() {

		@Override
		public void popwindowdo(int index) {
			mMoreRadioButton.setText(strklineType[index]);
			onChangeView(klineTypes[index], true);
		}
	};

	
	/**
	 * 页面中  点击 一分钟线时 弹出 事件 
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rb_one_minute:
			showPop(mMoreRadioButton);
			break;
		case R.id.zq_trend_kline_framelayout:
		{
			Intent intent = new Intent(ZQMarketDetailActivity.this, ZQLandscapeViewActivity.class);
			intent.putExtra(ZQLandscapeViewActivity.FLAG, mViewType);
			ZQLandscapeViewActivity.mOptionCodeInfo = mOptionCodeInfo;
			startActivity(intent);
			//startActivityForResult(intent, REQUEST_CODE);
		}
			break;
		case R.id.img_public_black_head_title_left_white_back:
			this.finish();
			break;
		case R.id.btn_zq_market_deail_zixuan:
		{

			if (mIsMyStock) {// delete from my stock

				int position = -1;
				int size = mMyApp.getMyStockList(AppConstants.HQTYPE_ZQ).size();
				for (int i = 0; i < size; i++) {
					if (mOptionData.code.equals(mMyApp.getMyStockList(AppConstants.HQTYPE_ZQ).get(i).code) && mOptionData.market == mMyApp.getMyStockList(AppConstants.HQTYPE_ZQ).get(i).market) {
						position = i;
						break;
					}
				}
				int ret = mMyApp.RemoveFromMyStock(position, AppConstants.HQTYPE_ZQ);
				if (ret == 0) {
					mIsMyStock = false;
					mBtnZiXuan.setText("加自选");
					Toast.makeText(ZQMarketDetailActivity.this, "该自选股已删除！", Toast.LENGTH_LONG).show();
				}

			} else {// add to my stock
				int ret = mMyApp.AddtoMyStock(mOptionData, AppConstants.HQTYPE_ZQ);
				if (ret == 0) {
					mIsMyStock = true;
					mBtnZiXuan.setText("删除自选");

					Toast.makeText(ZQMarketDetailActivity.this, "已添加到自选股！", Toast.LENGTH_LONG).show();
				} else if (ret == -1) {
					Toast.makeText(ZQMarketDetailActivity.this, "自选股已存在！", Toast.LENGTH_LONG).show();
				} else if (ret == -2) {
					Toast.makeText(ZQMarketDetailActivity.this, "自选股超过最大限制！", Toast.LENGTH_LONG).show();
				}
			}
		
		}
			break;	
			
		case R.id.btn_zq_market_deail_buy://买入 
		{
			if(mMyApp.mTradeData.mHQType == mMyApp.getCurrentHQType() 
					&& mMyApp.getCurrentHQType() == AppConstants.HQTYPE_ZQ
					&& mMyApp.mTradeData.mTradeLoginFlag)
			{
				Intent intent = new Intent(ZQMarketDetailActivity.this, ZqTradeDetailActivity.class);
				intent.putExtra(JiaoYiChiCangPager.INTENT_TRADE_PADE_INDEX, 0);
				intent.putExtra("STOCK_MARKET", (int)mOptionCodeInfo.market);
				intent.putExtra("STOCK_CODE", mOptionCodeInfo.code);
				startActivity(intent);
			}else
			{
				Intent intent = new Intent();
				intent.setClass(ZQMarketDetailActivity.this, ZhengQuanActivity.class);
				
				if(AppActivityManager.getAppManager().getZQActivity() != null)
				{
					AppActivityManager.getAppManager().finishActivity(AppActivityManager.getAppManager().getZQActivity());
				}
				intent.putExtra(JiaoYiChiCangPager.INTENT_TRADE_PADE_INDEX, 0);
				intent.putExtra("ZQ_VIEW_INDEX", ZhengQuanFragment.ZQ_VIEW_JIAOYI);
				intent.putExtra("STOCK_MARKET", (int)mOptionCodeInfo.market);
				intent.putExtra("STOCK_CODE", mOptionCodeInfo.code);
				
				startActivity(intent);
				
			}
			finish();
		}
			break;
		case R.id.btn_zq_market_deail_sell://卖出 
		{
			if(mMyApp.mTradeData.mHQType == mMyApp.getCurrentHQType() 
					&& mMyApp.getCurrentHQType() == AppConstants.HQTYPE_ZQ
					&& mMyApp.mTradeData.mTradeLoginFlag)
			{
				Intent intent = new Intent(ZQMarketDetailActivity.this, ZqTradeDetailActivity.class);
				intent.putExtra(JiaoYiChiCangPager.INTENT_TRADE_PADE_INDEX, 1);
				intent.putExtra("STOCK_MARKET", (int)mOptionCodeInfo.market);
				intent.putExtra("STOCK_CODE", mOptionCodeInfo.code);
				startActivity(intent);
			}else
			{
				Intent intent = new Intent();
				intent.setClass(ZQMarketDetailActivity.this, ZhengQuanActivity.class);
				
				if(AppActivityManager.getAppManager().getZQActivity() != null)
				{
					AppActivityManager.getAppManager().finishActivity(AppActivityManager.getAppManager().getZQActivity());
				}
				intent.putExtra(JiaoYiChiCangPager.INTENT_TRADE_PADE_INDEX, 1);
				intent.putExtra("ZQ_VIEW_INDEX", ZhengQuanFragment.ZQ_VIEW_JIAOYI);
				intent.putExtra("STOCK_MARKET", (int)mOptionCodeInfo.market);
				intent.putExtra("STOCK_CODE", mOptionCodeInfo.code);
				
				startActivity(intent);
			}
			finish();
		}
			break;

		case R.id.img_public_black_head_title_right_white_refresh:
			queryHQPushInfo();
			break;
		default:
			break;
		}
	}

	//获取股票基础数据
	private void queryZQStockInfo() {
		
		if(mOptionCodeInfo == null)
			return;
		
		ArrayList<TagCodeInfo> codelist = new ArrayList<TagCodeInfo>();
		codelist.add(mOptionCodeInfo);
	    
		mMyApp.setCertifyNetHandler(mHandler);
		mRequestCode[4] = GlobalNetProgress.HQRequest_ZQStockInfo(mMyApp.mCertifyNet,codelist,0,codelist.size());
	}
	
	// 获取期权行情信息
	private void queryHQPushInfo() {

		if(mOptionCodeInfo == null){
			return;
		}
		ArrayList<TagCodeInfo> codelist = new ArrayList<TagCodeInfo>();
		codelist.add(mOptionCodeInfo);

		mMyApp.setHQPushNetHandler(mHandler);
		mRequestCode[1] = GlobalNetProgress.HQRequest_MultiCodeInfoPush(mMyApp.mHQPushNet, codelist, 0, codelist.size());
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rb_fenshi:
			mMoreRadioButton.setText("分钟");
			onChangeView(ViewTools.VIEW_TRENDLINE, true);
			break;
		case R.id.rb_rixian:
			mMoreRadioButton.setText("分钟");
			onChangeView(ViewTools.VIEW_KLINE_DAY, true);
			break;
		case R.id.rb_zhouxian:
			mMoreRadioButton.setText("分钟");
			onChangeView(ViewTools.VIEW_KLINE_WEEK, true);
			break;
		case R.id.rb_yuexian:
			mMoreRadioButton.setText("分钟");
			onChangeView(ViewTools.VIEW_KLINE_MONTH, true);
			break;
			
		case R.id.rb_xinwengonggao:
			changeNewAndReportView(0);
			break;
		case R.id.rb_yanjiubaogao:
			changeNewAndReportView(1);
			break;


		default:
			break;
		}
	}
	

	
	private void changeNewAndReportView(int index)
	{
		if (mNewReportPageIndex != index)
		{
			mNewReportPageIndex = index;
			mListNewsData.clear();
			for (int i = 0; i < mAllNewsList.size(); i++) {
				switch(mNewReportPageIndex)
				{
				case 0:
					if(mAllNewsList.get(i).Type==1)
					{
						mListNewsData.add(mAllNewsList.get(i));
					}
					break;
					
				case 1:
					if(mAllNewsList.get(i).Type==2){
						mListNewsData.add(mAllNewsList.get(i));
					}
					break;
				default:
					break;
				}
			}
			mHandler.sendEmptyMessage(REFRESH_UI);
		}
	}

	
	
	private void onChangeView(int index, boolean bNeedChangeView) {
		//mViewType = index;
		switch (index) {
		
		case ViewTools.VIEW_TRENDLINE:
				if (mTrendLineView == null) {
					mTrendLineView = new ZQTrendLineView(this, true);
				}
				mTrendLineView.updateData(mOptionData, null);
				if (bNeedChangeView && mViewType != ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mTrendLineView);
				}
				mViewType = ViewTools.VIEW_TRENDLINE;
				
				requestTrendLine();
				requestDetail();
				L.e("onChangeView========", "VIEW_TRENDLINE");
			break;

			case ViewTools.VIEW_KLINE_DAY:
				if (mKLineView == null) {
					mKLineView = new ZQKLineView(this, true);
				}
				clearDetailScreen();
				mKLineView.updateData(mOptionData);
				if (bNeedChangeView && mViewType == ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mKLineView);
				}
				mKLineView.SetCycle(Global_Define.HISTORY_TYPE_DAY);
				mViewType = ViewTools.VIEW_KLINE_DAY;
				//((RadioButton) mRgTrendKline.findViewById(R.id.rb_rixian)).setChecked(true);
				requestKLine(0);
				L.e("onChangeView========", "VIEW_KLINE_DAY");
				break;
				
			case ViewTools.VIEW_KLINE_WEEK:
				if (mKLineView == null) {
					mKLineView = new ZQKLineView(this, true);
				}
				clearDetailScreen();
				mKLineView.updateData(mOptionData);
				if (bNeedChangeView && mViewType == ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mKLineView);
				}
				mKLineView.SetCycle(Global_Define.HISTORY_TYPE_WEEK);
				mViewType = ViewTools.VIEW_KLINE_WEEK;
				//((RadioButton) mRgTrendKline.findViewById(R.id.rb_zhouxian)).setChecked(true);
				requestKLine(0);
				L.e("onChangeView========", "VIEW_KLINE_WEEK");
				break;

			case ViewTools.VIEW_KLINE_MONTH:
				if (mKLineView == null) {
					mKLineView = new ZQKLineView(this, true);
				}
				clearDetailScreen();
				mKLineView.updateData(mOptionData);
				if (bNeedChangeView && mViewType == ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mKLineView);
				}
				mKLineView.SetCycle(Global_Define.HISTORY_TYPE_MONTH);
				mViewType = ViewTools.VIEW_KLINE_MONTH;
				//((RadioButton) mRgTrendKline.findViewById(R.id.rb_yuexian)).setChecked(true);
				requestKLine(0);
				L.e("onChangeView========", "VIEW_KLINE_MONTH");
				break;
			case ViewTools.VIEW_KLINE_1M:
				if (mKLineView == null) {
					mKLineView = new ZQKLineView(this, true);
				}
				clearDetailScreen();
				mKLineView.updateData(mOptionData);
				if (bNeedChangeView && mViewType == ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mKLineView);
				}
				mKLineView.SetCycle(Global_Define.HISTORY_TYPE_1MIN);
				mViewType = ViewTools.VIEW_KLINE_1M;
				//((RadioButton) mRgTrendKline.findViewById(R.id.rb_one_minute)).setChecked(true);
				//mMoreRadioButton.setText(strklineType[0]);
				requestKLine(1);
				L.e("onChangeView========", "VIEW_KLINE_1M");
				break;
			case ViewTools.VIEW_KLINE_3M:
				if (mKLineView == null) {
					mKLineView = new ZQKLineView(this, true);
				}
				clearDetailScreen();
				mKLineView.updateData(mOptionData);
				if (bNeedChangeView && mViewType == ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mKLineView);
				}
				mKLineView.SetCycle(Global_Define.HISTORY_TYPE_3MIN);
				mViewType = ViewTools.VIEW_KLINE_3M;
				//((RadioButton) mRgTrendKline.findViewById(R.id.rb_one_minute)).setChecked(true);
				//mMoreRadioButton.setText(strklineType[1]);
				requestKLine(1);
				L.e("onChangeView========", "VIEW_KLINE_3M");
				break;	
			case ViewTools.VIEW_KLINE_5M:
				if (mKLineView == null) {
					mKLineView = new ZQKLineView(this, true);
				}
				clearDetailScreen();
				mKLineView.updateData(mOptionData);
				if (bNeedChangeView && mViewType == ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mKLineView);
				}
				clearDetailScreen();
				mKLineView.SetCycle(Global_Define.HISTORY_TYPE_5MIN);
				mViewType = ViewTools.VIEW_KLINE_5M;
				//((RadioButton) mRgTrendKline.findViewById(R.id.rb_one_minute)).setChecked(true);
				//mMoreRadioButton.setText(strklineType[2]);
				requestKLine(2);
				L.e("onChangeView========", "VIEW_KLINE_5M");
				break;
			case ViewTools.VIEW_KLINE_15M:
				if (mKLineView == null) {
					mKLineView = new ZQKLineView(this, true);
				}
				clearDetailScreen();
				mKLineView.updateData(mOptionData);
				if (bNeedChangeView && mViewType == ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mKLineView);
				}
				mKLineView.SetCycle(Global_Define.HISTORY_TYPE_15MIN);
				mViewType = ViewTools.VIEW_KLINE_15M;
				//((RadioButton) mRgTrendKline.findViewById(R.id.rb_one_minute)).setChecked(true);
				//mMoreRadioButton.setText(strklineType[3]);
				requestKLine(2);
				L.e("onChangeView========", "VIEW_KLINE_15M");
				break;
	
			case ViewTools.VIEW_KLINE_30M:
				if (mKLineView == null) {
					mKLineView = new ZQKLineView(this, true);
				}
				clearDetailScreen();
				mKLineView.updateData(mOptionData);
				if (bNeedChangeView && mViewType == ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mKLineView);
				}
				mKLineView.SetCycle(Global_Define.HISTORY_TYPE_30MIN);
				mViewType = ViewTools.VIEW_KLINE_30M;
				//((RadioButton) mRgTrendKline.findViewById(R.id.rb_one_minute)).setChecked(true);
				//mMoreRadioButton.setText(strklineType[4]);
				requestKLine(2);
				L.e("onChangeView========", "VIEW_KLINE_30M");
				break;
			case ViewTools.VIEW_KLINE_60M:
				if (mKLineView == null) {
					mKLineView = new ZQKLineView(this, true);
				}
				clearDetailScreen();
				mKLineView.updateData(mOptionData);
				if (bNeedChangeView && mViewType == ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mKLineView);
				}
				mKLineView.SetCycle(Global_Define.HISTORY_TYPE_60MIN);
				mViewType = ViewTools.VIEW_KLINE_60M;
				//((RadioButton) mRgTrendKline.findViewById(R.id.rb_one_minute)).setChecked(true);
				//mMoreRadioButton.setText(strklineType[5]);
				requestKLine(3);
				L.e("onChangeView========", "VIEW_KLINE_60M");
				break;
			case ViewTools.VIEW_KLINE_120M:
				if (mKLineView == null) {
					mKLineView = new ZQKLineView(this, true);
				}
				clearDetailScreen();
				mKLineView.updateData(mOptionData);
				if (bNeedChangeView && mViewType == ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mKLineView);
				}
				mKLineView.SetCycle(Global_Define.HISTORY_TYPE_120MIN);
				mViewType = ViewTools.VIEW_KLINE_120M;
				//((RadioButton) mRgTrendKline.findViewById(R.id.rb_one_minute)).setChecked(true);
				//mMoreRadioButton.setText(strklineType[6]);
				requestKLine(3);
				L.e("onChangeView========", "VIEW_KLINE_120M");
				break;
	
			case ViewTools.VIEW_KLINE_240M:
				if (mKLineView == null) {
					mKLineView = new ZQKLineView(this, true);
				}
				clearDetailScreen();
				mKLineView.updateData(mOptionData);
				if (bNeedChangeView && mViewType == ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mKLineView);
				}
				mKLineView.SetCycle(Global_Define.HISTORY_TYPE_240MIN);
				mViewType = ViewTools.VIEW_KLINE_240M;
				//((RadioButton) mRgTrendKline.findViewById(R.id.rb_one_minute)).setChecked(true);
				//mMoreRadioButton.setText(strklineType[7]);
				requestKLine(3);
				L.e("onChangeView========", "VIEW_KLINE_240M");
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

	}


	

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	
	// parse trend data and store it into ArrayList<TagLocalTrendData>
	private void parseTrendData(byte[] szData, int nSize, boolean bOption) {
		L.d("XHTrendFragment", "Start parseTrendData");

		if (bOption) {
			mTrendDataArray.clear();
		}
		int offset = 0;
		int wPackSize = MyByteBuffer.getInt(szData, offset);
		offset += 4;
		if (wPackSize > nSize - offset || nSize == 0) {
			L.e("MyApp", "ERROR parseTrendData data error");
			return;
		}

		CPbDataPackage pack = new CPbDataPackage();
		CPbDataDecode.DecodeOnePackage(szData, offset, wPackSize, pack);

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
							case 26:
								aRecord.open = field.GetInt32();
								break;
							case 27:
								aRecord.high = field.GetInt32();
								break;
							case 28:
								aRecord.low = field.GetInt32();
								break;
							case 32:
								aRecord.average = field.GetInt32();
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

					if (Math.abs(aRecord.average - aRecord.now) >= 0.3 * aRecord.now) {
						aRecord.average = 0;
					}

					if (bOption) {
						mTrendDataArray.add(aRecord);
						L.e("mTrendDataArray Add a record[" + nTrendNum + "]:"
								+ aRecord.time + ",Price=" + aRecord.now
								+ ", volume=" + aRecord.volume);
					}

					aRecord = null;
				}
			}
		}

		formatTrendDataArray();
		L.e("MyApp", "End parseTrendData,nRecordCount=" + nTrendNum);
	}
	
	
	
	
	private void formatTrendDataArray() {
		if (mTrendDataArray == null)
			return;

		int nTrendNum = mTrendDataArray.size();
		for (int i = 0; i < nTrendNum; i++) {
			TagLocalTrendData tempData = mTrendDataArray.get(i);
			if (tempData.now == 0) {
				if (i == 0) {
					if (mOptionData == null)
						return;
					tempData.now = this.mOptionData.HQData.getnLastClear();
					tempData.high = tempData.now;
					tempData.low = tempData.now;
					tempData.open = tempData.now;
				}// if(i == 0)
				else {
					// 取前一根数据
					tempData.now = mTrendDataArray.get(i - 1).now;
					tempData.high = tempData.now;
					tempData.low = tempData.now;
					tempData.open = tempData.now;
				}
			}// if (tempData.now <= 0)
			else {
				if (tempData.high == 0)
					tempData.high = tempData.now;
				if (tempData.low == 0)
					tempData.low = tempData.now;
				if (tempData.open == 0)
					tempData.open = tempData.now;
			}
		}// end for(int i = 0; i < nTrendNum; i++)
		
		//处理第一根走势线是否是开盘点
		if (mOptionData != null)
		{
			if (mTrendDataArray.size() > 1)
			{
				TagLocalTrendData tempData = mTrendDataArray.get(0);
				if(mOptionData.Start[0] >= 2400)
			    {
			    	mOptionData.Start[0] -= 2400;
			    }
			    if (tempData.time == mOptionData.Start[0]) {
			    	mTrendDataArray.remove(0);
			    }
			}
			
		}
	}
	
	
	// send request to retrieve option trend line
	private void requestTrendLine() {
		mMyApp.setCertifyNetHandler(mHandler);
		mRequestCode[0] = GlobalNetProgress.HQRequest_TrendLine(
				mMyApp.mCertifyNet, mOptionData.HQData.code,
				mOptionData.HQData.market, 0, (short) 0, mOptionData.Start[0]);
		L.e("requestTrendLine()", mRequestCode[0] + "");
	}


	private void requestDetail() {
		mMyApp.setCertifyNetHandler(mHandler);
		mRequestCode[3] = GlobalNetProgress.HQRequest_Detail(mMyApp.mCertifyNet, mOptionData.HQData.code, mOptionData.HQData.market, -1,
				TagLocalDealData.MAX_NUM_DETAILDATA_ARRAY + 1);
	}
	
	public void requestKLine(int klinetype) {
		mMyApp.setCertifyNetHandler(mHandler);

		mKLineOrTrendSuccess = 0;
		mRequestCode[2] = GlobalNetProgress.HQRequest_KLine(mMyApp.mCertifyNet,
				(short) klinetype, mOptionData.HQData.code,
				mOptionData.HQData.market, 0, 0, 0, 0,
				TagLocalKLineData.MAX_KLINE_NUM, 0);
	}

	
		
	// 重新解析kLine
	// 用当日走势数据来拼1分钟K线
	private void reParse1MinKLineDataWithTrend() {
		int trendNum = mTrendDataArray.size();
		if (trendNum > 0) {
			int kNum = mKLineDataArray.size();
			if (kNum > 0) {
				TagLocalTrendData trendFirst = mTrendDataArray.get(0);
				TagLocalKLineData kLineLast = mKLineDataArray.get(kNum - 1);
				if (kLineLast.date > trendFirst.date) {
					return;
				} else if (kLineLast.date == trendFirst.date) {
					if (kLineLast.time >= trendFirst.time * 100) {
						return;
					}
				}
			}

			mCurrentDayKNum = 0;
			for (int i = 0; i < trendNum; i++) {
				TagLocalTrendData aRecord = mTrendDataArray.get(i);
				TagLocalKLineData kdata = new TagLocalKLineData();
				kdata.open = aRecord.open;
				kdata.date = aRecord.date;
				kdata.time = aRecord.time * 100;
				kdata.high = aRecord.high;
				kdata.low = aRecord.low;
				kdata.close = aRecord.now;
				kdata.ccl = aRecord.ccl;
				kdata.volume = (long) aRecord.volume;
				kdata.amount = (long) aRecord.amount;
				if (kdata.low > 0) {
					if (mKLineDataArray.size() >= TagLocalKLineData.MAX_KLINE_NUM) {
						mKLineDataArray.remove(0);
					}
					mKLineDataArray.add(kdata);
					mCurrentDayKNum++;
				}
			}

		}
	}
				
				
				
				
	// 重新解析kLine
	// 用当日走势数据来拼5分钟K线
	private void reParse5MinKLineDataWithTrend() {
		int trendNum = mTrendDataArray.size();

		if (trendNum > 0) {
			int kNum = mKLineDataArray.size();
			if (kNum > 0) {
				TagLocalTrendData trendFirst = mTrendDataArray.get(0);
				TagLocalKLineData kLineLast = mKLineDataArray.get(kNum - 1);
				if (kLineLast.date > trendFirst.date) {
					return;
				} else if (kLineLast.date == trendFirst.date) {
					if (kLineLast.time >= trendFirst.time * 100) {
						return;
					}
				}
			}

			mCurrentDayKNum = 0;
			int nCount = (trendNum) / 5;
			int nMod = (trendNum) % 5;
			// /////////////////////////
			for (int i = 0; i < nCount; i++) {
				TagLocalKLineData kdata = new TagLocalKLineData();
				kdata.open = mTrendDataArray.get(i * 5).open;
				kdata.date = mTrendDataArray.get(i * 5 + 4).date;
				kdata.time = mTrendDataArray.get(i * 5 + 4).time * 100;
				kdata.high = mTrendDataArray.get(i * 5).high;
				kdata.close = mTrendDataArray.get(i * 5).now;
				kdata.ccl = mTrendDataArray.get(i * 5).ccl;
				kdata.volume = (long) mTrendDataArray.get(i * 5).volume;
				kdata.amount = (long) mTrendDataArray.get(i * 5).amount;
				kdata.low = mTrendDataArray.get(i * 5).low;

				for (int j = 1; j < 5; j++) {
					if (mTrendDataArray.get(i * 5 + j).now > 0) {
						kdata.close = mTrendDataArray.get(i * 5 + j).now;
					}
					if (mTrendDataArray.get(i * 5 + j).ccl > 0) {
						kdata.ccl = mTrendDataArray.get(i * 5 + j).ccl;
					}

					kdata.volume += (long) mTrendDataArray.get(i * 5 + j).volume;
					kdata.amount += (long) mTrendDataArray.get(i * 5 + j).amount;
					kdata.high = Math.max(kdata.high,
							mTrendDataArray.get(i * 5 + j).high);
					if (mTrendDataArray.get(i * 5 + j).low > 0) {
						if (kdata.low == 0) {
							kdata.low = mTrendDataArray.get(i * 5 + j).low;
						} else
							kdata.low = Math.min(kdata.low,
									mTrendDataArray.get(i * 5 + j).low);
					}
				}

				if (kdata.low > 0) {
					if (mKLineDataArray.size() >= TagLocalKLineData.MAX_KLINE_NUM) {
						mKLineDataArray.remove(0);
					}
					mKLineDataArray.add(kdata);
					mCurrentDayKNum++;
				}
			}

			if (nMod > 0) {
				TagLocalKLineData kdata = new TagLocalKLineData();
				kdata.open = mTrendDataArray.get(nCount * 5).open;
				kdata.date = mTrendDataArray.get(trendNum - 1).date;
				// kdata.time = mTrendDataArray.get(trendNum-1).time*100;
				if (mOptionData != null) {
					kdata.time = KdateTools.PointToTime((nCount + 1) * 5,
							mOptionData) * 100;
				} else {
					kdata.time = mTrendDataArray.get(trendNum - 1).time * 100;
				}
				kdata.close = mTrendDataArray.get(nCount * 5).now;
				kdata.ccl = mTrendDataArray.get(nCount * 5).ccl;
				kdata.high = mTrendDataArray.get(nCount * 5).high;

				if (mTrendDataArray.get(nCount * 5).low > 0) {
					kdata.low = mTrendDataArray.get(nCount * 5).low;
				}
				kdata.volume = (long) mTrendDataArray.get(nCount * 5).volume;
				kdata.amount = (long) mTrendDataArray.get(nCount * 5).amount;
				for (int k = 1; k < nMod; k++) {
					if (mTrendDataArray.get(nCount * 5 + k).now > 0) {
						kdata.close = mTrendDataArray.get(nCount * 5 + k).now;
					}
					if (mTrendDataArray.get(nCount * 5 + k).ccl > 0) {
						kdata.ccl = mTrendDataArray.get(nCount * 5 + k).ccl;
					}

					kdata.high = Math.max(kdata.high,
							mTrendDataArray.get(nCount * 5 + k).high);
					if (kdata.low == 0) {
						kdata.low = mTrendDataArray.get(nCount * 5 + k).low;
					} else {
						kdata.low = Math.min(kdata.low,
								mTrendDataArray.get(nCount * 5 + k).low);
					}
					kdata.volume += mTrendDataArray.get(nCount * 5 + k).volume;
					kdata.amount += mTrendDataArray.get(nCount * 5 + k).amount;
				}
				if (kdata.low > 0) {
					if (mKLineDataArray.size() >= TagLocalKLineData.MAX_KLINE_NUM) {
						mKLineDataArray.remove(0);
					}
					mKLineDataArray.add(kdata);
					mCurrentDayKNum++;
				}
			}
		}
	}
				
				
	// 重新解析kLine
	// 用当日走势数据来拼60分钟K线
	private void reParse60MinKLineDataWithTrend() {
		int trendNum = mTrendDataArray.size();

		if (trendNum > 0) {
			int kNum = mKLineDataArray.size();
			if (kNum > 0) {
				TagLocalTrendData trendFirst = mTrendDataArray.get(0);
				TagLocalKLineData kLineLast = mKLineDataArray.get(kNum - 1);
				if (kLineLast.date > trendFirst.date) {
					return;
				} else if (kLineLast.date == trendFirst.date) {
					if (kLineLast.time >= trendFirst.time * 100) {
						return;
					}
				}
			}

			int nCount = (trendNum) / 60;
			int nMod = (trendNum) % 60;
			mCurrentDayKNum = 0;
			for (int i = 0; i < nCount; i++) {
				TagLocalKLineData kdata = new TagLocalKLineData();
				kdata.open = mTrendDataArray.get(i * 60).open;
				kdata.date = mTrendDataArray.get(i * 60 + 59).date;
				kdata.time = mTrendDataArray.get(i * 60 + 59).time * 100;
				kdata.high = mTrendDataArray.get(i * 60).high;
				kdata.close = mTrendDataArray.get(i * 60).now;
				kdata.ccl = mTrendDataArray.get(i * 60).ccl;
				kdata.volume = (long) mTrendDataArray.get(i * 60).volume;
				kdata.amount = (long) mTrendDataArray.get(i * 60).amount;
				kdata.low = mTrendDataArray.get(i * 60).low;

				for (int j = 1; j < 60; j++) {
					if (mTrendDataArray.get(i * 60 + j).now > 0) {
						kdata.close = mTrendDataArray.get(i * 60 + j).now;
					}
					if (mTrendDataArray.get(i * 60 + j).ccl > 0) {
						kdata.ccl = mTrendDataArray.get(i * 60 + j).ccl;
					}

					kdata.volume += (long) mTrendDataArray.get(i * 60 + j).volume;
					kdata.amount += (long) mTrendDataArray.get(i * 60 + j).amount;
					kdata.high = Math.max(kdata.high,
							mTrendDataArray.get(i * 60 + j).high);
					if (mTrendDataArray.get(i * 60 + j).low > 0) {
						if (kdata.low == 0) {
							kdata.low = mTrendDataArray.get(i * 60 + j).low;
						} else
							kdata.low = Math.min(kdata.low,
									mTrendDataArray.get(i * 60 + j).low);
					}
				}

				if (kdata.low > 0) {
					if (mKLineDataArray.size() >= TagLocalKLineData.MAX_KLINE_NUM) {
						mKLineDataArray.remove(0);
					}
					mKLineDataArray.add(kdata);
					mCurrentDayKNum++;
				}
			}

			if (nMod > 0) {
				TagLocalKLineData kdata = new TagLocalKLineData();
				kdata.open = mTrendDataArray.get(nCount * 60).open;
				kdata.date = mTrendDataArray.get(trendNum - 1).date;
				if (mOptionData != null) {
					kdata.time = KdateTools.PointToTime((nCount + 1) * 60,
							mOptionData) * 100;
				} else {
					kdata.time = mTrendDataArray.get(trendNum - 1).time * 100;
				}
				kdata.close = mTrendDataArray.get(nCount * 60).now;
				kdata.ccl = mTrendDataArray.get(nCount * 60).ccl;
				kdata.high = mTrendDataArray.get(nCount * 60).high;

				if (mTrendDataArray.get(nCount * 60).low > 0) {
					kdata.low = mTrendDataArray.get(nCount * 60).low;
				}
				kdata.volume = (long) mTrendDataArray.get(nCount * 60).volume;
				kdata.amount = (long) mTrendDataArray.get(nCount * 60).amount;
				for (int k = 1; k < nMod; k++) {
					if (mTrendDataArray.get(nCount * 60 + k).now > 0) {
						kdata.close = mTrendDataArray.get(nCount * 60 + k).now;
					}
					if (mTrendDataArray.get(nCount * 60 + k).ccl > 0) {
						kdata.ccl = mTrendDataArray.get(nCount * 60 + k).ccl;
					}

					kdata.high = Math.max(kdata.high,
							mTrendDataArray.get(nCount * 60 + k).high);
					if (kdata.low == 0) {
						kdata.low = mTrendDataArray.get(nCount * 60 + k).low;
					} else {
						kdata.low = Math.min(kdata.low,
								mTrendDataArray.get(nCount * 60 + k).low);
					}
					kdata.volume += mTrendDataArray.get(nCount * 60 + k).volume;
					kdata.amount += mTrendDataArray.get(nCount * 60 + k).amount;
				}
				if (kdata.low > 0) {
					if (mKLineDataArray.size() >= TagLocalKLineData.MAX_KLINE_NUM) {
						mKLineDataArray.remove(0);
					}
					mKLineDataArray.add(kdata);
					mCurrentDayKNum++;
				}
			}

		}
	}
				
	// 用当日走势数据来拼今日3，15,30,120,240分钟K线
	private void Parse3MinKLineDataWithTrend(int minType) {
		int trendNum = mTrendDataArray.size();
		mKLineByTrendArray.clear();
		if (trendNum > 0) {
			int nCount = (trendNum) / minType;
			int nMod = (trendNum) % minType;
			mCurrentDayKNum = 0;
			for (int i = 0; i < nCount; i++) {
				TagLocalKLineData kdata = new TagLocalKLineData();
				kdata.open = mTrendDataArray.get(i * minType).open;
				kdata.date = mTrendDataArray.get(i * minType + minType - 1).date;
				kdata.time = mTrendDataArray.get(i * minType + minType - 1).time * 100;
				kdata.high = mTrendDataArray.get(i * minType).high;
				kdata.close = mTrendDataArray.get(i * minType).now;
				kdata.ccl = mTrendDataArray.get(i * minType).ccl;
				kdata.volume = (long) mTrendDataArray.get(i * minType).volume;
				kdata.amount = (long) mTrendDataArray.get(i * minType).amount;
				kdata.low = mTrendDataArray.get(i * minType).low;

				for (int j = 1; j < minType; j++) {
					if (mTrendDataArray.get(i * minType + j).now > 0) {
						kdata.close = mTrendDataArray.get(i * minType + j).now;
					}
					if (mTrendDataArray.get(i * minType + j).ccl > 0) {
						kdata.ccl = mTrendDataArray.get(i * minType + j).ccl;
					}

					kdata.volume += (long) mTrendDataArray.get(i * minType
							+ j).volume;
					kdata.amount += (long) mTrendDataArray.get(i * minType
							+ j).amount;
					kdata.high = Math.max(kdata.high,
							mTrendDataArray.get(i * minType + j).high);
					if (mTrendDataArray.get(i * minType + j).low > 0) {
						if (kdata.low == 0) {
							kdata.low = mTrendDataArray
									.get(i * minType + j).low;
						} else
							kdata.low = Math
									.min(kdata.low,
											mTrendDataArray.get(i * minType
													+ j).low);
					}
				}

				if (kdata.low > 0) {
					if (mKLineByTrendArray.size() >= TagLocalKLineData.MAX_KLINE_NUM) {
						mKLineByTrendArray.remove(0);
					}
					mKLineByTrendArray.add(kdata);
					mCurrentDayKNum++;
				}
			}

			if (nMod > 0) {
				TagLocalKLineData kdata = new TagLocalKLineData();
				kdata.open = mTrendDataArray.get(nCount * minType).open;
				kdata.date = mTrendDataArray.get(trendNum - 1).date;
				if (mOptionData != null) {
					kdata.time = KdateTools.PointToTime((nCount + 1) * minType,
							mOptionData) * 100;
				} else {
					kdata.time = mTrendDataArray.get(trendNum - 1).time * 100;
				}
				kdata.close = mTrendDataArray.get(nCount * minType).now;
				kdata.ccl = mTrendDataArray.get(nCount * minType).ccl;
				kdata.high = mTrendDataArray.get(nCount * minType).high;

				if (mTrendDataArray.get(nCount * minType).low > 0) {
					kdata.low = mTrendDataArray.get(nCount * minType).low;
				}
				kdata.volume = (long) mTrendDataArray.get(nCount * minType).volume;
				kdata.amount = (long) mTrendDataArray.get(nCount * minType).amount;
				for (int k = 1; k < nMod; k++) {
					if (mTrendDataArray.get(nCount * minType + k).now > 0) {
						kdata.close = mTrendDataArray.get(nCount * minType
								+ k).now;
					}
					if (mTrendDataArray.get(nCount * minType + k).ccl > 0) {
						kdata.ccl = mTrendDataArray.get(nCount * minType
								+ k).ccl;
					}

					kdata.high = Math.max(kdata.high,
							mTrendDataArray.get(nCount * minType + k).high);
					if (kdata.low == 0) {
						kdata.low = mTrendDataArray.get(nCount * minType
								+ k).low;
					} else {
						kdata.low = Math
								.min(kdata.low,
										mTrendDataArray.get(nCount * minType
												 + k).low);
					}
					kdata.volume += mTrendDataArray.get(nCount * minType
							+ k).volume;
					kdata.amount += mTrendDataArray.get(nCount * minType
							+ k).amount;
				}
				if (kdata.low > 0) {
					if (mKLineByTrendArray.size() >= TagLocalKLineData.MAX_KLINE_NUM) {
						mKLineByTrendArray.remove(0);
					}
					mKLineByTrendArray.add(kdata);
					mCurrentDayKNum++;
				}
			}

		}
	}

	// 重新解析kLine
	// cycle-周期（3min,15min,30min,120min,240min）
	// 3min-用1min kline 组合
	// 15min-用5min kline组合
	// 30min-用5min kline组合
	// 120min-用60min kline组合
	// 240min-用60min kline组合
	private void reParseMinKLineData(int cycle) {
		int num = mKLineDataArray.size();

		TagLocalKLineData tmpData = null;

		mKLineMinArray.clear();
		if (num > 0)
		{
			int step = 1;
			int i, cstep;
			int mod = 1;
			if (cycle == Global_Define.HISTORY_TYPE_240MIN) {
				step = 4;
				mod = 240;
			} else if (cycle == Global_Define.HISTORY_TYPE_3MIN) {
				step = 3;
				mod = 3;
			} else if (cycle == Global_Define.HISTORY_TYPE_15MIN) {
				step = 3;
				mod = 15;
			} else if (cycle == Global_Define.HISTORY_TYPE_120MIN) {
				step = 2;
				mod = 120;
			} else {
				step = 6;// 30min
				mod = 30;
			}

			tmpData = mKLineDataArray.get(0);
			tmpData.volume = 0;
			tmpData.amount = 0;
			tmpData.volSell = 0;
			for (i = 0, cstep = 0; i < num; i++) {
				if (KdateTools.same_day(tmpData.date,
						mKLineDataArray.get(i).date) && cstep < step) {
					cstep++;
					tmpData.date = mKLineDataArray.get(i).date;
					tmpData.time = mKLineDataArray.get(i).time;
					tmpData.close = mKLineDataArray.get(i).close;
					tmpData.high = Math.max(tmpData.high,
							mKLineDataArray.get(i).high);
					tmpData.low = Math.min(tmpData.low,
							mKLineDataArray.get(i).low);
					tmpData.volume += mKLineDataArray.get(i).volume; //
					tmpData.volSell += mKLineDataArray.get(i).volSell;
					tmpData.amount += mKLineDataArray.get(i).amount;
					tmpData.raiseNum = mKLineDataArray.get(i).raiseNum;
					tmpData.fallNum = mKLineDataArray.get(i).fallNum;
				} else {
					mKLineMinArray.add(tmpData);
					tmpData = mKLineDataArray.get(i);
					cstep = 1;
				}
				if (i >= (num - 1))// last one
				{
					// mCurrentDayKNum++;
					// if (mOptionData != null)
					// {
					// tmpData.time =
					// KdateTools.PointToTime((mCurrentDayKNum)*mod,
					// mOptionData)*100;
					// }
					mKLineMinArray.add(tmpData);
				}
			}
		}

			int kNum = mKLineMinArray.size();
			if (kNum > 0) {
				if (mTrendDataArray == null || mTrendDataArray.size() <= 0)
				{
					return;
				}
				TagLocalTrendData trendFirst = mTrendDataArray.get(0);
				TagLocalKLineData kLineLast = mKLineMinArray.get(kNum - 1);
				if (kLineLast.date > trendFirst.date) {
					return;
				} else if (kLineLast.date == trendFirst.date) {
					if (kLineLast.time >= trendFirst.time * 100) {
						return;
					}
				}
			}
			for (int m = 0; m < this.mKLineByTrendArray.size(); m++) {
				if (mKLineMinArray.size() >= TagLocalKLineData.MAX_KLINE_NUM) {
					mKLineMinArray.remove(0);
				}
				mKLineMinArray.add(mKLineByTrendArray.get(m));
			}

	}
				
				
				
	private void parseKLineData(byte[] szData, int nSize) {
		L.e("MyApp", "Start parseKLineData");

		mKLineDataArray.clear();
		int offset = 0;
		int wPackSize = MyByteBuffer.getInt(szData, offset);
		offset += 4;
		if (wPackSize > nSize - offset || nSize == 0) {
			L.e("MyApp", "ERROR parseKLineData data error");
			return;
		}

		CPbDataPackage pack = new CPbDataPackage();
		CPbDataDecode.DecodeOnePackage(szData, offset, wPackSize, pack);

		int nKLineNum = 0;
		for (int i = 0; i < pack.m_nItemSize; ++i) {
			CPbDataItem item = pack.m_DataItems.get(i);
			if (item.m_ItemType == CPbDataItem.DIT_NORMAL) {
				if (item.m_NormalField.IsValid()) {
					String strSection = item.m_NormalField.m_szFieldName;
					switch (item.m_NormalField.m_FieldID) {
					case 10:
						// code
						L.e("MyApp",
								strSection + "="
										+ item.m_NormalField.GetString());
						break;
					case 11:
						// int nMarketId = item.m_NormalField.GetInt16();
						L.e("MyApp",
								strSection + "="
										+ item.m_NormalField.GetInt16());
						break;
					case 46:
						// int nKlineType = item.m_NormalField.GetInt8();
						L.e("MyApp",
								strSection + "="
										+ item.m_NormalField.GetInt32());
						break;
					case 1018:
						// int nKLineWeight = item.m_NormalField.GetInt8();
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
					L.e("mKLineDataArray Add a record[" + nKLineNum + "]:"
							+ aRecord.date + ", volume=" + aRecord.volume);
					aRecord = null;
				}
			}
		}

		L.e("MyApp", "End parseKLineData,nRecordCount=" + nKLineNum);
	}
				
				
				
	public void setOptionDataForKLine(TagLocalStockData stockData,
			boolean bPush, ArrayList<TagLocalKLineData> klineArray) {
		if (bPush) {
			if (stockData == null || (stockData.HQData.nLastPrice == 0 && stockData.HQData.nOpenPrice == 0))
			{
				return;
			}
			int nKLineNum = klineArray.size();
			if (nKLineNum == 0
					|| stockData.HQData.nTradeDate > klineArray
							.get(nKLineNum - 1).date) {
				if (nKLineNum >= TagLocalKLineData.MAX_KLINE_NUM) {
					L.e("nKLineNum > MAX_KLINE_NUM");
					klineArray.remove(0);
				}
				TagLocalKLineData aRecord = new TagLocalKLineData();
				aRecord.date = stockData.HQData.nTradeDate;
				aRecord.volume = (long) stockData.HQData.volume;
				aRecord.amount = (long) stockData.HQData.amount;
				aRecord.high = stockData.HQData.nHighPrice;
				aRecord.low = stockData.HQData.nLowPrice;
				aRecord.open = stockData.HQData.nOpenPrice;
				aRecord.close = stockData.HQData.nLastPrice;
				aRecord.ccl = stockData.HQData.dOpenInterest;

				klineArray.add(aRecord);
			} else if (stockData.HQData.nTradeDate == klineArray
					.get(nKLineNum - 1).date) {
				// 如果是最新一天的数据，最新数据
				TagLocalKLineData tempRecord = klineArray.get(nKLineNum - 1);

				tempRecord.volume = (long) stockData.HQData.volume;
				tempRecord.amount = (long) stockData.HQData.amount;
				tempRecord.ccl = stockData.HQData.dOpenInterest;
				if (stockData.HQData.nHighPrice != 0) {
					tempRecord.high = stockData.HQData.nHighPrice;
				}
				if (stockData.HQData.nLowPrice != 0) {
					tempRecord.low = stockData.HQData.nLowPrice;
				}
				tempRecord.close = stockData.HQData.nLastPrice;
			}
		}
	}
				
				
				
	private void reParseWeekKLineData() {
		int Num = mKLineDataArray.size();

		TagLocalKLineData kWData = null;

		mKLineWeekArray.clear();

		for (int i = 0; i < Num; i++) {

			TagLocalKLineData kDData = mKLineDataArray.get(i);

			if (i == 0 && Num == TagLocalKLineData.MAX_KLINE_NUM) {
				TagLocalKLineData kNextData = mKLineDataArray.get(i + 1);

				while (KdateTools.same_week(kDData.date, kNextData.date)
						&& i < Num - 1) {
					kDData = mKLineDataArray.get(i++);// 每次请求会比周开盘日请求几天,计算的时候抛掉多余的获取周开盘日
					kNextData = mKLineDataArray.get(i);
				}
				kDData = kNextData;
			}

			if (kWData == null
					|| !KdateTools.same_week(kDData.date, kWData.date))// 新的一周
			{
				kWData = new TagLocalKLineData();
				kWData.open = kDData.open;
				kWData.date = kDData.date;
				kWData.high = kDData.high;
				kWData.low = kDData.low;
				kWData.close = kDData.close;

				kWData.volume = kDData.volume;
				kWData.amount = kDData.amount;
			}

			int date = kDData.date;

			int weekday = KdateTools.lon_weekday(date);

			if (weekday == 5)// 周五收盘价
			{
				if (KdateTools.lon_weekday(kWData.date) == 5)// 本周只有周五的
				{
					kWData.open = kDData.open;
					kWData.date = kDData.date;
					kWData.high = kDData.high;
					kWData.low = kDData.low;

					kWData.volume = kDData.volume;
					kWData.amount = kDData.amount;
					kWData.close = kDData.close;
					mKLineWeekArray.add(kWData);
					continue;
				}

				kWData.volume += kDData.volume;
				kWData.amount += kDData.amount;

				kWData.date = kDData.date;
				kWData.close = kDData.close;

				if (kWData.high < kDData.high) {
					kWData.high = kDData.high;
				}
				if (kWData.low > kDData.low) {
					kWData.low = kDData.low;
				}
				mKLineWeekArray.add(kWData);
			} else {

				if (i != 0) {
					TagLocalKLineData kDDataPre = mKLineDataArray.get(i - 1);
					if (KdateTools.same_week(kDData.date, kDDataPre.date))// 非开盘日,累加
					{
						kWData.volume += kDData.volume;
						kWData.amount += kDData.amount;
					}
				}

				if (kWData.high < kDData.high) {
					kWData.high = kDData.high;
				}
				if (kWData.low > kDData.low) {
					kWData.low = kDData.low;
				}

				if (i < Num - 1) {
					TagLocalKLineData kDDataNext = mKLineDataArray.get(i + 1);
					if (!KdateTools.same_week(kDData.date, kDDataNext.date)) {
						kWData.date = kDData.date;
						kWData.close = kDData.close;// 非周末的收盘价
						mKLineWeekArray.add(kWData);
					}
				} else {
					kWData.date = kDData.date;
					kWData.close = kDData.close;// 当前最后一天的作为收盘价
					mKLineWeekArray.add(kWData);
				}
			}

		}
	}
				
				
				
	public void setOptionDataForWeekKLine(TagLocalStockData stockData,
			boolean bPush, ArrayList<TagLocalKLineData> klineArray) {
		if (bPush) {
			if (stockData == null || (stockData.HQData.nLastPrice == 0 && stockData.HQData.nOpenPrice == 0))
			{
				return;
			}
			int nKLineNum = klineArray.size();
			if (nKLineNum == 0
					|| stockData.HQData.nTradeDate > klineArray
							.get(nKLineNum - 1).date
					&& !KdateTools.same_week(stockData.HQData.nTradeDate,
							klineArray.get(nKLineNum - 1).date)) {// 不是最后周最新数据，新建
				if (nKLineNum >= TagLocalKLineData.MAX_KLINE_NUM) {
					L.e("nKLineNum > MAX_KLINE_NUM");
					klineArray.remove(0);
				}
				TagLocalKLineData aRecord = new TagLocalKLineData();
				aRecord.date = stockData.HQData.nTradeDate;
				aRecord.volume = (long) stockData.HQData.volume;
				aRecord.amount = (long) stockData.HQData.amount;
				aRecord.high = stockData.HQData.nHighPrice;
				aRecord.low = stockData.HQData.nLowPrice;
				aRecord.open = stockData.HQData.nOpenPrice;
				aRecord.close = stockData.HQData.nLastPrice;
				aRecord.ccl = stockData.HQData.dOpenInterest;

				klineArray.add(aRecord);
			} else if (stockData.HQData.nTradeDate >= klineArray
					.get(nKLineNum - 1).date
					&& KdateTools.same_week(stockData.HQData.nTradeDate,
							klineArray.get(nKLineNum - 1).date)) {
				// 如果是最后周最新一天的数据，最新数据
				TagLocalKLineData tempRecord = klineArray.get(nKLineNum - 1);

				tempRecord.volume += (long) stockData.HQData.currentCJ;
				tempRecord.amount = (long) stockData.HQData.amount;
				tempRecord.ccl = stockData.HQData.dOpenInterest;
				if (tempRecord.high < stockData.HQData.nHighPrice) {
					tempRecord.high = stockData.HQData.nHighPrice;
				}
				if (tempRecord.low > stockData.HQData.nLowPrice
						&& stockData.HQData.nLowPrice > 0) {
					tempRecord.low = stockData.HQData.nLowPrice;
				}
				tempRecord.close = stockData.HQData.nLastPrice;
				tempRecord.date = stockData.HQData.nTradeDate;
			}
		}
	}
				
				
				
	public void setOptionDataForMonthKLine(TagLocalStockData stockData,
			boolean bPush, ArrayList<TagLocalKLineData> klineArray) {
		if (bPush) {
			if (stockData == null || (stockData.HQData.nLastPrice == 0 && stockData.HQData.nOpenPrice == 0))
			{
				return;
			}
			int nKLineNum = klineArray.size();
			if (nKLineNum == 0
					|| stockData.HQData.nTradeDate > klineArray
							.get(nKLineNum - 1).date
					&& !KdateTools.same_month(stockData.HQData.nTradeDate,
							klineArray.get(nKLineNum - 1).date)) {// 非最后一个月
				if (nKLineNum >= TagLocalKLineData.MAX_KLINE_NUM) {
					L.e("nKLineNum > MAX_KLINE_NUM");
					klineArray.remove(0);
				}
				TagLocalKLineData aRecord = new TagLocalKLineData();
				aRecord.date = stockData.HQData.nTradeDate;
				aRecord.volume = (long) stockData.HQData.volume;
				aRecord.amount = (long) stockData.HQData.amount;
				aRecord.high = stockData.HQData.nHighPrice;
				aRecord.low = stockData.HQData.nLowPrice;
				aRecord.open = stockData.HQData.nOpenPrice;
				aRecord.close = stockData.HQData.nLastPrice;
				aRecord.ccl = stockData.HQData.dOpenInterest;

				klineArray.add(aRecord);
			} else if (stockData.HQData.nTradeDate >= klineArray
					.get(nKLineNum - 1).date
					&& KdateTools.same_month(stockData.HQData.nTradeDate,
							klineArray.get(nKLineNum - 1).date)) {
				// 如果是最后月最新一天的数据，最新数据
				TagLocalKLineData tempRecord = klineArray.get(nKLineNum - 1);

				tempRecord.volume += (long) stockData.HQData.currentCJ;
				tempRecord.amount = (long) stockData.HQData.amount;
				tempRecord.ccl = stockData.HQData.dOpenInterest;
				if (tempRecord.high < stockData.HQData.nHighPrice) {
					tempRecord.high = stockData.HQData.nHighPrice;
				}
				if (tempRecord.low > stockData.HQData.nLowPrice
						&& stockData.HQData.nLowPrice > 0) {
					tempRecord.low = stockData.HQData.nLowPrice;
				}

				tempRecord.close = stockData.HQData.nLastPrice;
				tempRecord.date = stockData.HQData.nTradeDate;
			}
		}
	}
				
				
				
				
	private void reParseMonthKLineData() {
		int Num = mKLineDataArray.size();

		TagLocalKLineData kMData = null;

		mKLineMonthArray.clear();

		for (int i = 0; i < Num; i++) {

			TagLocalKLineData kDData = mKLineDataArray.get(i);

			if (i == 0 && Num == TagLocalKLineData.MAX_KLINE_NUM) {
				TagLocalKLineData kNextData = mKLineDataArray.get(i + 1);

				while (KdateTools.same_month(kDData.date, kNextData.date)
						&& i < Num - 1) {
					kDData = mKLineDataArray.get(i++);// 每次请求会比月开盘日多请求几天,计算的时候抛掉多余的获取月开盘日
					kNextData = mKLineDataArray.get(i);
				}
				kDData = kNextData;
			}

			if (kMData == null
					|| !KdateTools.same_month(kDData.date, kMData.date))// 新的一月
			{
				kMData = new TagLocalKLineData();
				kMData.open = kDData.open;
				kMData.date = kDData.date;
				kMData.high = kDData.high;
				kMData.low = kDData.low;
				kMData.close = kDData.close;

				kMData.volume = kDData.volume;
				kMData.amount = kDData.amount;
			}

			int date = kDData.date;

			int MM = KdateTools.getMM(date);

			int DD = KdateTools.getDD(date);

			if (DD == KdateTools.lastday_of_month(date))// 月末收盘
			{
				if (kMData.date == kDData.date)// 本月只有这一天
				{
					mKLineMonthArray.add(kMData);
					continue;
				}

				kMData.volume += kDData.volume;
				kMData.amount += kDData.amount;

				kMData.date = kDData.date;
				kMData.close = kDData.close;

				if (kMData.high < kDData.high) {
					kMData.high = kDData.high;
				}
				if (kMData.low > kDData.low) {
					kMData.low = kDData.low;
				}
				mKLineMonthArray.add(kMData);
			} else {
				if (i != 0) {
					TagLocalKLineData kDDataPre = mKLineDataArray.get(i - 1);

					if (KdateTools.getMM(kDDataPre.date) == MM)// 非开盘日,累加
					{
						kMData.volume += kDData.volume;
						kMData.amount += kDData.amount;
					}
				}

				if (kMData.high < kDData.high) {
					kMData.high = kDData.high;
				}
				if (kMData.low > kDData.low) {
					kMData.low = kDData.low;
				}

				if (i < Num - 1) {
					TagLocalKLineData kDDataNext = mKLineDataArray.get(i + 1);
					if (KdateTools.getMM(kDDataNext.date) != MM)// 非月底的收盘
					{
						kMData.date = kDData.date;
						kMData.close = kDData.close;// 非月末的收盘价
						mKLineMonthArray.add(kMData);
					} else {
						kMData.volume += kDData.volume;
						kMData.amount += kDData.amount;
					}
				} else {
					kMData.date = kDData.date;
					kMData.close = kDData.close;// 当前最后一天的作为收盘价
					mKLineMonthArray.add(kMData);
				}
			}

		}

	}
	
	private void parseDealData(byte[] szData, int nSize) {
		L.e(TAG, "Start parseDealData");

		int offset = 0;
		int wPackSize = MyByteBuffer.getInt(szData, offset);
		offset += 4;
		if (wPackSize > nSize - offset || nSize == 0) {
			L.e(TAG, "ERROR parseDealData data error");
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
							L.e(TAG, strSection + "=" + item.m_NormalField.GetString());
							break;
						case 11:
							// int nMarketId = item.m_NormalField.GetInt16();
							L.e(TAG, strSection + "=" + item.m_NormalField.GetInt16());
							break;
						default:
							L.e(TAG, "App unused field:" + item.m_NormalField.m_FieldID + "->" + strSection);
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
									// L.e(TAG, strSection + "=" +
									// aRecord.date);
									break;
								case 22:
									aRecord.time = field.GetInt32();
									// L.e(TAG, strSection + "=" +
									// aRecord.time);
									break;
								case 29:
									aRecord.now = field.GetInt32();
									// L.e(TAG, strSection + "=" + aRecord.now);
									break;
								case 39:
									aRecord.ccl = field.GetDouble();
									// L.e(TAG, strSection + "=" +
									// field.GetDouble());
									break;
								case 35:
									aRecord.volume = field.GetDouble();
									// L.e(TAG, strSection + "=" +
									// field.GetDouble());
									break;
								case 45:
									aRecord.inoutflag = field.GetInt8();
									// L.e(TAG, strSection + "=" +
									// field.GetDouble());
									break;
								default:
									// L.e(TAG, "App unused field:" +
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

		L.e(TAG, "End parseDealData,nRecordCount=" + mDealDataArray.size());
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
	
	// 合约推送记录
	public void setOptionDataForTLine(TagLocalStockData stockData, boolean bPush) {
		if (bPush) {
			TagLocalTrendData aRecord = new TagLocalTrendData();
			aRecord.time = stockData.HQData.nUpdateTime / 100000;
			aRecord.volume = stockData.HQData.currentCJ;
			aRecord.now = stockData.HQData.nLastPrice;
			aRecord.date = stockData.HQData.nTradeDate;
			// 1) 如果当前是指数，直接使用均价字段
			// 2) 如果非指数，客户端采用金额/量的方式计算
			if (DataTools.isStockIndex(stockData))// 是否是指数
			{
				aRecord.average = stockData.HQData.nAveragePrice;
			} else {
				int average = 0;
				if (stockData.HQData.volume != 0 && mOptionData.Multiplier != 0) {
					average = (int) (stockData.HQData.amount * mOptionData.PriceRate
							/ stockData.HQData.volume / mOptionData.Multiplier);
				}
				if (average == 0) {
					if (mTrendDataArray.size() <= 1) {
						aRecord.average = aRecord.now;
					} else {
						aRecord.average = mTrendDataArray.get(mTrendDataArray
								.size() - 1).average;
					}
				} else {
					aRecord.average = average;
				}
			}

			int nTrendNum = mTrendDataArray.size();
			if (nTrendNum == 0
					|| aRecord.time >= mTrendDataArray.get(mTrendDataArray
							.size() - 1).time) {
				// 如果是新一分钟，添加新走势记录，如果是同一分钟，更新最新一笔的数据
				if (nTrendNum == 0
						|| aRecord.time > mTrendDataArray.get(mTrendDataArray
								.size() - 1).time) {
					if (nTrendNum > TagLocalTrendData.MAX_TREND_NUM) {
						L.e("nTrendNum > MAX_TREND_NUM");
						return;
					}
					mTrendDataArray.add(aRecord);
				} else {
					TagLocalTrendData tempRecord = mTrendDataArray
							.get(mTrendDataArray.size() - 1);
					tempRecord.volume += aRecord.volume;
					tempRecord.time = aRecord.time;
					tempRecord.now = aRecord.now;
				}
			}
		}
	}
	
	
	
	public void setOptionDataForMinKLine(TagLocalStockData stockData,
			boolean bPush, ArrayList<TagLocalKLineData> klineArray) {
		if (bPush) {
			if (stockData == null || (stockData.HQData.nLastPrice == 0 && stockData.HQData.nOpenPrice == 0))
			{
				return;
			}
			if(!KdateTools.IsInTradeTime(stockData.HQData.nUpdateTime/100000, stockData))
			{
				return;
			}
			int point = KdateTools.TimeToPoint(stockData.HQData.nUpdateTime/1000, stockData);
			int kIndex = 0;//索引从0开始
			int mod = 3;
			int nKLineNum = klineArray.size();
			if (getKLineViewCycle() == Global_Define.HISTORY_TYPE_3MIN)
			{
				kIndex = point/3 + ((point%3 == 0)?0:1) - 1;
				mod = 3;

			}else if(getKLineViewCycle() == Global_Define.HISTORY_TYPE_15MIN)
			{
				kIndex = point/15 + ((point%15 == 0)?0:1) - 1;
				mod = 15;
			}else if(getKLineViewCycle() == Global_Define.HISTORY_TYPE_30MIN)
			{
				kIndex = point/30 + ((point%30 == 0)?0:1) - 1;
				mod = 30;
			}else if(getKLineViewCycle() == Global_Define.HISTORY_TYPE_240MIN)
			{
				kIndex = point/240 + ((point%240 == 0)?0:1) - 1;
				mod = 240;
			}
			
			if (kIndex > this.mCurrentDayKNum-1 || nKLineNum <= 0)
			{
				for (int i = mCurrentDayKNum; i <= kIndex; i++)
				{
					if (i == kIndex)//当前推送的数据
					{
						TagLocalKLineData aRecord = new TagLocalKLineData();
						aRecord.date = stockData.HQData.nTradeDate;
						aRecord.volume = (long) stockData.HQData.currentCJ;
						aRecord.amount = (long) (stockData.HQData.currentCJAveragePrice * stockData.HQData.currentCJ);
						aRecord.high = stockData.HQData.nLastPrice;
						aRecord.low = stockData.HQData.nLastPrice;
						aRecord.open = stockData.HQData.nLastPrice;
						aRecord.close = stockData.HQData.nLastPrice;
						aRecord.time = KdateTools.PointToTime((kIndex+1)*mod, stockData)*100;
						aRecord.ccl= stockData.HQData.dOpenInterest;
						if(klineArray.size() >= TagLocalKLineData.MAX_KLINE_NUM)
		                {
							klineArray.remove(0);
		                }
						klineArray.add(aRecord);
						mCurrentDayKNum++;
					}else
					{
						TagLocalKLineData aRecord = new TagLocalKLineData();
						aRecord.date = klineArray.get(klineArray.size()-1).date;
						aRecord.volume = 0;
						aRecord.amount = 0;
						aRecord.high = klineArray.get(klineArray.size()-1).open;
						aRecord.low = klineArray.get(klineArray.size()-1).open;
						aRecord.open = klineArray.get(klineArray.size()-1).open;
						aRecord.close = klineArray.get(klineArray.size()-1).open;
						aRecord.time = KdateTools.PointToTime((i+1)*mod, stockData)*100;
						aRecord.ccl= klineArray.get(klineArray.size()-1).ccl;

						if(klineArray.size() >= TagLocalKLineData.MAX_KLINE_NUM)
		                {
							klineArray.remove(0);
		                }
						klineArray.add(aRecord);
						mCurrentDayKNum++;
					}
				}
			}else if(kIndex == this.mCurrentDayKNum-1 && nKLineNum > 0)
			{
				// 如果是最后一分钟的数据，最新数据
				TagLocalKLineData tempRecord = klineArray.get(nKLineNum - 1);

				tempRecord.volume += (long) stockData.HQData.currentCJ;
				tempRecord.amount += (long) (stockData.HQData.currentCJAveragePrice * stockData.HQData.currentCJ);
				if (tempRecord.high < stockData.HQData.nLastPrice) {
					tempRecord.high = stockData.HQData.nLastPrice;
				}
				if (tempRecord.low > stockData.HQData.nLastPrice) {
					tempRecord.low = stockData.HQData.nLastPrice;
				}
				tempRecord.close = stockData.HQData.nLastPrice;
				tempRecord.ccl= stockData.HQData.dOpenInterest;
//				if (stockData.HQData.nUpdateTime/1000 > tempRecord.time)
//				{
//					tempRecord.time = stockData.HQData.nUpdateTime/1000;
//				}
			}
		}
	}
	
	
	
	
	
	public void setOptionDataFor1MinKLine(TagLocalStockData stockData,
			boolean bPush, ArrayList<TagLocalKLineData> klineArray) {
		if (bPush) {
			if (stockData == null || (stockData.HQData.nLastPrice == 0 && stockData.HQData.nOpenPrice == 0))
			{
				return;
			}
			if(!KdateTools.IsInTradeTime(stockData.HQData.nUpdateTime/100000, stockData))
			{
				return;
			}
			int point = KdateTools.TimeToPoint(stockData.HQData.nUpdateTime/1000, stockData);
			int kIndex = 0;//索引从0开始
			int mod = 1;
			int nKLineNum = klineArray.size();
			if (getKLineViewCycle() == Global_Define.HISTORY_TYPE_1MIN)
			{
				kIndex = point/1 - 1;
				mod = 1;

			}else if(getKLineViewCycle() == Global_Define.HISTORY_TYPE_5MIN)
			{
				kIndex = point/5 + ((point%5 == 0)?0:1) - 1;
				mod = 5;
			}else if(getKLineViewCycle() == Global_Define.HISTORY_TYPE_60MIN)
			{
				kIndex = point/60 + ((point%60 == 0)?0:1) - 1;
				mod = 60;
			}
			
			if (kIndex > this.mCurrentDayKNum-1 || nKLineNum <= 0)
			{
				for (int i = mCurrentDayKNum; i <= kIndex; i++)
				{
					if (i == kIndex)//当前推送的数据
					{
						TagLocalKLineData aRecord = new TagLocalKLineData();
						aRecord.date = stockData.HQData.nTradeDate;
						aRecord.volume = (long) stockData.HQData.currentCJ;
						aRecord.amount = (long) (stockData.HQData.currentCJAveragePrice * stockData.HQData.currentCJ);
						aRecord.high = stockData.HQData.nLastPrice;
						aRecord.low = stockData.HQData.nLastPrice;
						aRecord.open = stockData.HQData.nLastPrice;
						aRecord.close = stockData.HQData.nLastPrice;
						aRecord.time = KdateTools.PointToTime((kIndex+1)*mod, stockData)*100;
						aRecord.ccl=stockData.HQData.dOpenInterest;
						if(klineArray.size() >= TagLocalKLineData.MAX_KLINE_NUM)
		                {
							klineArray.remove(0);
		                }
						klineArray.add(aRecord);
						mCurrentDayKNum++;
					}else
					{
						TagLocalKLineData aRecord = new TagLocalKLineData();
						aRecord.date = klineArray.get(klineArray.size()-1).date;
						aRecord.volume = 0;
						aRecord.amount = 0;
						aRecord.high = klineArray.get(klineArray.size()-1).open;
						aRecord.low = klineArray.get(klineArray.size()-1).open;
						aRecord.open = klineArray.get(klineArray.size()-1).open;
						aRecord.close = klineArray.get(klineArray.size()-1).open;
						aRecord.time = KdateTools.PointToTime((i+1)*mod, stockData)*100;
						aRecord.ccl=klineArray.get(klineArray.size()-1).ccl;

						if(klineArray.size() >= TagLocalKLineData.MAX_KLINE_NUM)
		                {
							klineArray.remove(0);
		                }
						klineArray.add(aRecord);
						mCurrentDayKNum++;
					}
				}
			}else if(kIndex == this.mCurrentDayKNum-1 && nKLineNum > 0)
			{
				// 如果是最后一根K线的数据，更新数据
				TagLocalKLineData tempRecord = klineArray.get(nKLineNum - 1);

				tempRecord.volume += (long) stockData.HQData.currentCJ;
				tempRecord.amount += (long) (stockData.HQData.currentCJAveragePrice * stockData.HQData.currentCJ);
				if (tempRecord.high < stockData.HQData.nLastPrice) {
					tempRecord.high = stockData.HQData.nLastPrice;
				}
				if (tempRecord.low > stockData.HQData.nLastPrice) {
					tempRecord.low = stockData.HQData.nLastPrice;
				}
				tempRecord.close = stockData.HQData.nLastPrice;
				tempRecord.ccl=stockData.HQData.dOpenInterest;
//				if (stockData.HQData.nUpdateTime/1000 > tempRecord.time)
//				{
//					tempRecord.time = stockData.HQData.nUpdateTime/1000;
//				}
			}
		}
	}

				
	public int getKLineViewCycle() {
		if (mKLineView != null) {
			return mKLineView.GetCycle();
		}

		return Global_Define.HISTORY_TYPE_DAY;
	}
				
	public boolean getKLineViewPopFlag() {
		if (mKLineView != null) {
			return mKLineView.mPopinfoFlag;
		}
		return false;
	}

	public void resetKLineParam() {
		if (mKLineView != null) {
			mKLineView.resetKLineParam();
		}
	}
	
	private void clearDetailScreen()
	{
		if (mViewType != ViewTools.VIEW_TRENDLINE)
		{
			//mMyApp.resetTrendDataArray();
			mMyApp.resetKLineDataArray();
			mMyApp.resetKLineMinArray();
			mMyApp.resetKLineMonthArray();
			mMyApp.resetKLineWeekArray();
			resetKLineParam();
			if (mKLineView != null)
			{
				mKLineView.updateAllData();
			}
		}
	}
	
	private void parseStockInfoData(byte[] szData, int nSize)
	{
		L.e(TAG, "Start parseStockInfoData");
	    int nCount = 0;
	    int offset = 0;
		while(offset >= 0 && nSize-offset >= 4)
		{
			int wPackSize = MyByteBuffer.getInt(szData,offset);
	        if(wPackSize < 0)
	        {
	        	L.e(TAG, "ERROR: parseStockInfoData wPackSize = " + wPackSize);
	        	break;
	        }
			 offset += 4;
			if(wPackSize > nSize-offset)
				break;
	        
			CPbDataPackage pack = new CPbDataPackage();
			wPackSize = CPbDataDecode.DecodeOnePackage(szData,offset, wPackSize, pack);
	        
	        offset += wPackSize;
	        if (pack.m_wPackageID == 2) {
	            
	        	if (mStockBaseInfoRecord == null)
	        	{
	        		mStockBaseInfoRecord = new TagLocalStockBaseInfoRecord();
	        	}
	        	mStockBaseInfoRecord.code = pack.GetNormalFieldByID(10).GetString();
	        	mStockBaseInfoRecord.market = (short)pack.GetNormalFieldByID(11).GetInt16();
	        	mStockBaseInfoRecord.StopFlag = pack.GetNormalFieldByID(1002).GetInt8();
	        	mStockBaseInfoRecord.Avg5Volume = pack.GetNormalFieldByID(1003).GetDouble();
	        	mStockBaseInfoRecord.TotalCapital = pack.GetNormalFieldByID(1010).GetDouble();
	        	mStockBaseInfoRecord.FlowCapital = pack.GetNormalFieldByID(1011).GetDouble();
	        	mStockBaseInfoRecord.AvgNetAssets = pack.GetNormalFieldByID(1012).GetDouble();
	        	mStockBaseInfoRecord.LastAvgProfit = pack.GetNormalFieldByID(1013).GetDouble();
	        	mStockBaseInfoRecord.ForecastAvgProfit = pack.GetNormalFieldByID(1014).GetDouble();
	        	mStockBaseInfoRecord.NetProfit = pack.GetNormalFieldByID(1015).GetDouble();
	        	mStockBaseInfoRecord.TotalAssets = pack.GetNormalFieldByID(1016).GetDouble();
	        	mStockBaseInfoRecord.TotalDebt = pack.GetNormalFieldByID(1017).GetDouble();
	            
	            nCount++;
	            
	        }
		}
	    L.e(TAG, "End parseStockInfoData,nRecordCount="+nCount);
	}

}
