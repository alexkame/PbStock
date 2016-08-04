package com.pengbo.mhdzq.zq_activity;

import java.util.ArrayList;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.KLinePopWindowAdapter;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.CCodeTableItem;
import com.pengbo.mhdzq.data.CDataCodeTable;
import com.pengbo.mhdzq.data.CPbDataDecode;
import com.pengbo.mhdzq.data.CPbDataField;
import com.pengbo.mhdzq.data.CPbDataItem;
import com.pengbo.mhdzq.data.CPbDataPackage;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalDealData;
import com.pengbo.mhdzq.data.TagLocalKLineData;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.TagLocalTrendData;
import com.pengbo.mhdzq.data.CMarketInfoItem.mktGroupInfo;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.net.CMessageObject;
import com.pengbo.mhdzq.net.GlobalNetConnect;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.net.MyByteBuffer;
import com.pengbo.mhdzq.tools.DataTools;
import com.pengbo.mhdzq.tools.KdateTools;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.view.AutoScaleTextView;
import com.pengbo.mhdzq.view.MoreKLinePopWindow;
import com.pengbo.mhdzq.view.ZQLandScapeKLineView;
import com.pengbo.mhdzq.view.ZQLandScapeTrendLineView;
import com.pengbo.mhdzq.view.MoreKLinePopWindow.PopWindowCallBack;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.RadioGroup.OnCheckedChangeListener;

// OnGestureListener,
public class ZQLandscapeViewActivity extends HdActivity implements OnClickListener,OnCheckedChangeListener, OnTouchListener{
	private static final String TAG = "ZQLandscapeViewActivity";
	
	public static final String FLAG="FlagViewType";
	
	public static TagCodeInfo mOptionCodeInfo;// 合约
	private TagLocalStockData mOptionData;// 合约
	private MyApp mMyApp;

	public int  mRequestCode[];//0:走势    1:推送     2：Kline 3:detail
	private MoreKLinePopWindow mPopWindow;//点击更多查看其他分钟下的K线 行情 
	private RadioButton mMoreRadioButton ;
	private KLinePopWindowAdapter mKLinePopAdapter;
	String[] strss=new String[]{
			"1分钟","3分钟","5分钟","15分钟","30分钟","60分钟","120分钟","240分钟"
	};
	
	int [] klineTypes=new int[]{ ViewTools.VIEW_KLINE_1M,
			                     ViewTools.VIEW_KLINE_3M, 
			                     ViewTools.VIEW_KLINE_5M, 
			                     ViewTools.VIEW_KLINE_15M, 
			                     ViewTools.VIEW_KLINE_30M, 
			                     ViewTools.VIEW_KLINE_60M, 
			                     ViewTools.VIEW_KLINE_120M, 
			                     ViewTools.VIEW_KLINE_240M};
	

	private static final int NONE = 0;  
	private static final int DRAG = 1;  
	private static final int ZOOM = 2;
	private int mode = NONE;
	
	private GestureDetector mDoubleDetector;
	private ZQLandScapeTrendLineView mTrendLineView;
	private ZQLandScapeKLineView mKLineView;
	private int mViewType = ViewTools.VIEW_TRENDLINE;
	
	private RadioGroup mRgTrendKline;//包括分时 K线的 RadioGroup 
	private ViewFlipper mFlipper;
	private FrameLayout mFramelayout;
	

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
	
	private View mLayoutTitle, mLayoutChangeTitle_kline, mLayoutChangeTitle_trend;//没有长按时的抬头  及 有长按时的抬头  
	private AutoScaleTextView mTVName,mTVZDZDF,mTVCJL,mGXSJ;//没有长按时的  内容  名称   涨跌   成交量  更新时间   
	private ImageView mCloseBigView; //关闭大图 
	private TextView mTime,mGao,mKai,mDi,mShou,mZD;//长按时不弹pop 改变抬头的字体内容 分别为  时间     高   开     低   收  涨跌 
	private TextView mTime_trend,mJiage,mZD_trend,mCJL,mJunjia;//长按时不弹pop 改变抬头的字体内容 分别为  时间     高   开     低   收  涨跌 
	
	public final static int REQUEST_CODE = 999;
	private Handler mHandler=new Handler(){


		public void handleMessage(android.os.Message msg) {

			int nFrameType = msg.arg1;
			int nRequestCode = msg.arg2;
			switch (msg.what) {
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
					}else if(nFrameType ==  Global_Define.MFT_MOBILE_PUSH_DATA){
						TagLocalStockData optionStockData = new TagLocalStockData();
						if (mMyApp.mHQData_ZQ.getData(optionStockData, mOptionCodeInfo.market, mOptionCodeInfo.code, false)) {
							mOptionData = optionStockData;
							setTitleView();
							
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
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		this.getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
		
		setContentView(R.layout.zq_big_trend_kline);
		mMyApp = (MyApp) this.getApplication();	
		
		initData();
		initView();
	}


	private void initData() {
		mViewType = this.getIntent().getIntExtra(FLAG, ViewTools.VIEW_TRENDLINE);
		L.e("Landscapt==initData-------mViewType", "mViewType"+"===="+mViewType);
		//mViewType = ViewTools.VIEW_TRENDLINE;
		//手势识别类 
		mDoubleDetector = new GestureDetector(this, new MyGestureListener());
		mRequestCode = new int[4];
		//走势的数据 
		mTrendDataArray = mMyApp.getLandTrendDataArray();
		mKLineDataArray = mMyApp.getLandKLineDataArray();
		mKLineWeekArray = mMyApp.getLandKLineWeekArray();
		mKLineMonthArray = mMyApp.getLandKLineMonthArray();
		mKLineMinArray = mMyApp.getLandKLineMinArray();
		mDealDataArray = mMyApp.getDealDataArray();
		mKLineByTrendArray = new ArrayList<TagLocalKLineData> ();
		
		setStockData();	
	}


	/**
	 * 初始化控件  
	 */
	private void initView() {	
		initTitleView();
		setTitleView();
		initDrawTrendAndKline();
	}



	/**
	 * 初始化大图上的抬头中的代码名称等控件 
	 */
	private void initTitleView() {
		mLayoutTitle = this.findViewById(R.id.llayout_big_trend_kline_title);//没有长按时的抬头
		mLayoutChangeTitle_trend = this.findViewById(R.id.llayout_big_trend_title_pop);// 有长按时trend的抬头  
		mLayoutChangeTitle_kline = this.findViewById(R.id.llayout_big_kline_title_pop);// 有长按时kline的抬头 
		
		mTVName = (AutoScaleTextView) this.findViewById(R.id.tv_zq_big_view_name);// //没有长按时的  内容  名称   涨跌   成交量  更新时间 
		mTVZDZDF = (AutoScaleTextView) this.findViewById(R.id.tv_zq_big_view_zdf);
		mTVCJL = (AutoScaleTextView) this.findViewById(R.id.tv_zq_big_view_cjl);
		mGXSJ = (AutoScaleTextView) this.findViewById(R.id.tv_zq_big_view_gxsj);//更新时间 
		
		mCloseBigView =  (ImageView) this.findViewById(R.id.img_zq_big_view_close);//关闭大图 
		mCloseBigView.setOnClickListener(this);
		
		mTime = (TextView) this.findViewById(R.id.tv_zq_kline_shijianzhi);//长按时不弹pop 改变抬头的字体内容 分别为  时间     高   开     低   收  涨跌 
		mGao = (TextView) this.findViewById(R.id.tv_zq_kline_gaozhi);
		mKai = (TextView) this.findViewById(R.id.tv_zq_kline_kaizhi);
		mDi = (TextView) this.findViewById(R.id.tv_zq_kline_dizhi);
		mShou = (TextView) this.findViewById(R.id.tv_zq_kline_shouzhi);
		mZD = (TextView) this.findViewById(R.id.tv_zq_kline_zhangdiezhi);
		
		mTime_trend = (TextView) this.findViewById(R.id.tv_zq_big_view_shijianzhi);//长按时不弹pop 改变抬头的字体内容 分别为  时间     高   开     低   收  涨跌 
		mJiage = (TextView) this.findViewById(R.id.tv_zq_big_view_jiage);
		mZD_trend = (TextView) this.findViewById(R.id.tv_zq_big_view_zhangdiezhi);
		mJunjia = (TextView) this.findViewById(R.id.tv_zq_big_view_junjia);
		mCJL = (TextView) this.findViewById(R.id.tv_zq_big_view_chengjiao);
		
		mLayoutTitle.setVisibility(View.VISIBLE);
		mLayoutChangeTitle_trend.setVisibility(View.GONE);
		mLayoutChangeTitle_kline.setVisibility(View.GONE);
		
	}
	
	

	/**
	 * 设置抬头里面的控件值 
	 */
	private void setTitleView() {	
		mTVName.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_NAME_ANSI)+ "("
					  + ViewTools.getStringByFieldID(mOptionData,Global_Define.FIELD_HQ_CODE) + ")");
		mTVZDZDF.setText(ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_NOW)+"("+ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_ZDF_SIGN)+")");
		mTVZDZDF.setTextColor(ViewTools.getColorByFieldID(mOptionData, Global_Define.FIELD_HQ_NOW));
		
		mTVCJL.setText("成交量 "+ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_VOLUME));
		//更新时间 新需求中又  把 更新时间  几个字 拿掉了  
		//ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_HQTIME);
		
		String time=String.format("%s:%02d", mOptionData.HQData.nUpdateTime/100000/100,mOptionData.HQData.nUpdateTime/100000%100);//
		mGXSJ.setText("更新时间:"+time);
		
	}

	/**
	 * 初始化画图上的东西
	 */
	private void initDrawTrendAndKline() {
		mRgTrendKline = (RadioGroup) findViewById(R.id.rg_big_trend_kline);//分时 K线 中的各个分钟线    切换 包裹的 RadioGroup
		mMoreRadioButton = (RadioButton) findViewById(R.id.rg_big_one_minute);//点击能探出更多K线的 RadioButton 
		mMoreRadioButton.setOnClickListener(this);
		mRgTrendKline.setOnCheckedChangeListener(this);
		
		mFlipper  =  (ViewFlipper) this.findViewById(R.id.zq_big_trend_kline_flipper);
		mFramelayout = (FrameLayout) this.findViewById(R.id.zq_big_trend_kline_framelayout);
		mFramelayout.setOnTouchListener(this);
		mFramelayout.setLongClickable(true);
		mFramelayout.setClickable(true);
		
		mFlipper.removeAllViews();
		if(mViewType == ViewTools.VIEW_TRENDLINE){
			if(mTrendLineView == null){
				mTrendLineView = new ZQLandScapeTrendLineView(this, true);
				mTrendLineView.updateData(mOptionData, null);
			}
			onChangeView(ViewTools.VIEW_TRENDLINE, true);
			//((RadioButton) mRgTrendKline.findViewById(R.id.rg_big_trend)).setChecked(true);
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
				mKLineView = new ZQLandScapeKLineView(this, true,true);
				mKLineView.updateData(mOptionData);
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
		
		queryHQPushInfo();
	}
	
	/**
	 * 点击更多时弹出 PopWindow 
	 */
	private void showPop(View mRadioButton) {
		mPopWindow = new MoreKLinePopWindow(ZQLandscapeViewActivity.this, mRadioButton,true);
		mKLinePopAdapter = new KLinePopWindowAdapter(ZQLandscapeViewActivity.this, strss);
		mPopWindow.setContent(mKLinePopAdapter);
		mPopWindow.setPopWindowCallback(popwindowcallback);

	}
	
	PopWindowCallBack popwindowcallback = new PopWindowCallBack() {

		@Override
		public void popwindowdo(int index) {
			mMoreRadioButton.setText(strss[index]);
			onChangeView(klineTypes[index], true);
		}
	};

	
	/**
	 * 页面中  点击 一分钟线时 弹出 事件 
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rg_big_one_minute:
			showPop(mMoreRadioButton);
			break;
		case R.id.img_zq_big_view_close:

//			Intent intent = new Intent();
//			intent.putExtra(FLAG, mViewType);
//			setResult(REQUEST_CODE, intent);
			ZQLandscapeViewActivity.this.finish();
			break;
		default:
			break;
		}
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
		case R.id.rg_big_trend:
			mMoreRadioButton.setText("分钟");
			onChangeView(ViewTools.VIEW_TRENDLINE, true);
			
			break;
		case R.id.rg_big_rixian:
			mMoreRadioButton.setText("分钟");
			onChangeView(ViewTools.VIEW_KLINE_DAY, true);
			
			break;
		case R.id.rg_big_zhouxian:
			mMoreRadioButton.setText("分钟");
			onChangeView(ViewTools.VIEW_KLINE_WEEK, true);
			
			break;
		case R.id.rg_big_yuexian:
			mMoreRadioButton.setText("分钟");
			onChangeView(ViewTools.VIEW_KLINE_MONTH, true);
			
			break;
		default:
			break;
		}
	}
	
	
	
	
	private void onChangeView(int index, boolean bNeedChangeView) {
		//mViewType = index;
		switch (index) {
		
		case ViewTools.VIEW_TRENDLINE:
			
				if (mTrendLineView == null) {
					mTrendLineView = new ZQLandScapeTrendLineView(this, true);
				}
				mTrendLineView.updateData(mOptionData, null);
				if (bNeedChangeView && mViewType != ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mTrendLineView);
				}
				mViewType = ViewTools.VIEW_TRENDLINE;
				((RadioButton) mRgTrendKline.findViewById(R.id.rg_big_trend)).setChecked(true);
				requestTrendLine();
				requestDetail();
				mGXSJ.setVisibility(View.GONE);
			break;

			case ViewTools.VIEW_KLINE_DAY:
				
				if (mKLineView == null) {
					mKLineView = new ZQLandScapeKLineView(this, true,true);
				}
				clearDetailScreen();
				mKLineView.updateData(mOptionData);
				if (bNeedChangeView && mViewType == ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mKLineView);
				}
				mKLineView.SetCycle(Global_Define.HISTORY_TYPE_DAY);
				mViewType = ViewTools.VIEW_KLINE_DAY;
				((RadioButton) mRgTrendKline.findViewById(R.id.rg_big_rixian)).setChecked(true);
				requestKLine(0);
				mGXSJ.setVisibility(View.VISIBLE);
				break;
				
			case ViewTools.VIEW_KLINE_WEEK:
				
				if (mKLineView == null) {
					mKLineView = new ZQLandScapeKLineView(this, true,true);
				}
				clearDetailScreen();
				mKLineView.updateData(mOptionData);
				if (bNeedChangeView && mViewType == ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mKLineView);
				}
				mKLineView.SetCycle(Global_Define.HISTORY_TYPE_WEEK);
				mViewType = ViewTools.VIEW_KLINE_WEEK;
				((RadioButton) mRgTrendKline.findViewById(R.id.rg_big_zhouxian)).setChecked(true);
				requestKLine(0);
				mGXSJ.setVisibility(View.VISIBLE);
				break;

			case ViewTools.VIEW_KLINE_MONTH:
				
				if (mKLineView == null) {
					mKLineView = new ZQLandScapeKLineView(this, true,true);
				}
				clearDetailScreen();
				mKLineView.updateData(mOptionData);
				if (bNeedChangeView && mViewType == ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mKLineView);
				}
				mKLineView.SetCycle(Global_Define.HISTORY_TYPE_MONTH);
				mViewType = ViewTools.VIEW_KLINE_MONTH;
				((RadioButton) mRgTrendKline.findViewById(R.id.rg_big_yuexian)).setChecked(true);
				requestKLine(0);
				mGXSJ.setVisibility(View.VISIBLE);
				break;
			case ViewTools.VIEW_KLINE_1M:
				
				if (mKLineView == null) {
					mKLineView = new ZQLandScapeKLineView(this, true,true);
				}
				clearDetailScreen();
				mKLineView.updateData(mOptionData);
				if (bNeedChangeView && mViewType == ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mKLineView);
				}
				mKLineView.SetCycle(Global_Define.HISTORY_TYPE_1MIN);
				mViewType = ViewTools.VIEW_KLINE_1M;
				((RadioButton) mRgTrendKline.findViewById(R.id.rg_big_one_minute)).setChecked(true);
				mMoreRadioButton.setText(strss[0]);
				requestKLine(1);
				mGXSJ.setVisibility(View.VISIBLE);
				break;
			case ViewTools.VIEW_KLINE_3M:
				
				if (mKLineView == null) {
					mKLineView = new ZQLandScapeKLineView(this, true,true);
				}
				clearDetailScreen();
				mKLineView.updateData(mOptionData);
				if (bNeedChangeView && mViewType == ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mKLineView);
				}
				mKLineView.SetCycle(Global_Define.HISTORY_TYPE_3MIN);
				mViewType = ViewTools.VIEW_KLINE_3M;
				((RadioButton) mRgTrendKline.findViewById(R.id.rg_big_one_minute)).setChecked(true);
				mMoreRadioButton.setText(strss[1]);
				requestKLine(1);
				mGXSJ.setVisibility(View.VISIBLE);
				break;
			case ViewTools.VIEW_KLINE_5M:
				
				if (mKLineView == null) {
					mKLineView = new ZQLandScapeKLineView(this, true,true);
				}
				clearDetailScreen();
				mKLineView.updateData(mOptionData);
				if (bNeedChangeView && mViewType == ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mKLineView);
				}
				clearDetailScreen();
				mKLineView.SetCycle(Global_Define.HISTORY_TYPE_5MIN);
				mViewType = ViewTools.VIEW_KLINE_5M;
				((RadioButton) mRgTrendKline.findViewById(R.id.rg_big_one_minute)).setChecked(true);
				mMoreRadioButton.setText(strss[2]);
				requestKLine(2);
				mGXSJ.setVisibility(View.VISIBLE);
				break;
			case ViewTools.VIEW_KLINE_15M:
				
				if (mKLineView == null) {
					mKLineView = new ZQLandScapeKLineView(this, true,true);
				}
				clearDetailScreen();
				mKLineView.updateData(mOptionData);
				if (bNeedChangeView && mViewType == ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mKLineView);
				}
				mKLineView.SetCycle(Global_Define.HISTORY_TYPE_15MIN);
				mViewType = ViewTools.VIEW_KLINE_15M;
				((RadioButton) mRgTrendKline.findViewById(R.id.rg_big_one_minute)).setChecked(true);
				mMoreRadioButton.setText(strss[3]);
				requestKLine(2);
				mGXSJ.setVisibility(View.VISIBLE);
				break;
				
				
			case ViewTools.VIEW_KLINE_30M:
				
				if (mKLineView == null) {
					mKLineView = new ZQLandScapeKLineView(this, true,true);
				}
				clearDetailScreen();
				mKLineView.updateData(mOptionData);
				if (bNeedChangeView && mViewType == ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mKLineView);
				}
				mKLineView.SetCycle(Global_Define.HISTORY_TYPE_30MIN);
				mViewType = ViewTools.VIEW_KLINE_30M;
				((RadioButton) mRgTrendKline.findViewById(R.id.rg_big_one_minute)).setChecked(true);
				mMoreRadioButton.setText(strss[4]);
				requestKLine(2);
				mGXSJ.setVisibility(View.VISIBLE);
				break;
			case ViewTools.VIEW_KLINE_60M:
				
				if (mKLineView == null) {
					mKLineView = new ZQLandScapeKLineView(this, true,true);
				}
				clearDetailScreen();
				mKLineView.updateData(mOptionData);
				if (bNeedChangeView && mViewType == ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mKLineView);
				}
				mKLineView.SetCycle(Global_Define.HISTORY_TYPE_60MIN);
				mViewType = ViewTools.VIEW_KLINE_60M;
				((RadioButton) mRgTrendKline.findViewById(R.id.rg_big_one_minute)).setChecked(true);
				mMoreRadioButton.setText(strss[5]);
				requestKLine(3);
				mGXSJ.setVisibility(View.VISIBLE);
				break;
			case ViewTools.VIEW_KLINE_120M:
				
				if (mKLineView == null) {
					mKLineView = new ZQLandScapeKLineView(this, true,true);
				}
				clearDetailScreen();
				mKLineView.updateData(mOptionData);
				if (bNeedChangeView && mViewType == ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mKLineView);
				}
				mKLineView.SetCycle(Global_Define.HISTORY_TYPE_120MIN);
				mViewType = ViewTools.VIEW_KLINE_120M;
				((RadioButton) mRgTrendKline.findViewById(R.id.rg_big_one_minute)).setChecked(true);
				mMoreRadioButton.setText(strss[6]);
				requestKLine(3);
				mGXSJ.setVisibility(View.VISIBLE);
				break;
		
			case ViewTools.VIEW_KLINE_240M:
				
				if (mKLineView == null) {
					mKLineView = new ZQLandScapeKLineView(this, true,true);
				}
				clearDetailScreen();
				mKLineView.updateData(mOptionData);
				if (bNeedChangeView && mViewType == ViewTools.VIEW_TRENDLINE) {
					ChangeView(index, mKLineView);
				}
				mKLineView.SetCycle(Global_Define.HISTORY_TYPE_240MIN);
				mViewType = ViewTools.VIEW_KLINE_240M;
				((RadioButton) mRgTrendKline.findViewById(R.id.rg_big_one_minute)).setChecked(true);
				mMoreRadioButton.setText(strss[7]);
				requestKLine(3);
				mGXSJ.setVisibility(View.VISIBLE);
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
	public boolean onTouchEvent(MotionEvent event) {
		return mDoubleDetector.onTouchEvent(event);
	}
	
	
	
	

	







	
	
	float x_down = 0f;
	float y_down = 0f;
	PointF start = new PointF();  
    PointF mid = new PointF(); 
    float oldDist = 1f;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(mViewType == ViewTools.VIEW_KLINE_DAY || mViewType == ViewTools.VIEW_KLINE_WEEK
				|| mViewType == ViewTools.VIEW_KLINE_MONTH || mViewType == ViewTools.VIEW_KLINE_1M
				|| mViewType == ViewTools.VIEW_KLINE_3M || mViewType == ViewTools.VIEW_KLINE_5M
				|| mViewType == ViewTools.VIEW_KLINE_15M || mViewType == ViewTools.VIEW_KLINE_30M
				|| mViewType == ViewTools.VIEW_KLINE_60M || mViewType == ViewTools.VIEW_KLINE_120M
				|| mViewType == ViewTools.VIEW_KLINE_240M) {


			mKLineView.setKLineTop(mFramelayout.getTop());
			switch (event.getAction() & MotionEvent.ACTION_MASK) {  
	        case MotionEvent.ACTION_DOWN:  
	            mode = DRAG;  
	            x_down = event.getX();  
	            y_down = event.getY();   
	            break;  
	        case MotionEvent.ACTION_POINTER_DOWN:  
	            mode = ZOOM;  
	            oldDist = spacing(event);   
	            break;  
	        case MotionEvent.ACTION_MOVE:  
	            if (mode == ZOOM) {
	                float newDist = spacing(event);  
	                mKLineView.onScaleLine((newDist - oldDist));
	                  
	            }else
	            {
	            	mKLineView.setKLineTop(mFramelayout.getTop());
	    			// 弹出框时，只在当前屏幕的K线中，移动标尺
	            	if (mKLineView.mPopinfoFlag == true) {
	            		mKLineView.onMoveLine(event);
	            		showPopInfo(true);
	            	}else
	            	{
	        			showPopInfo(false);
	            	}
	            }
	            break;  
	        case MotionEvent.ACTION_UP:  
	        case MotionEvent.ACTION_POINTER_UP:  
	            mode = NONE; 
	            mKLineView.onZoomStop();
	            if (mKLineView.mPopinfoFlag == true)
	            {
	            	mKLineView.DismissTitle(true);
	            	showPopInfo(false);
	            }
	            break; 
	        }  
		
		}else if(mViewType == ViewTools.VIEW_TRENDLINE){

			mTrendLineView.setTrendLineTop(mFramelayout.getTop());
			switch (event.getAction() & MotionEvent.ACTION_MASK) {  
	        case MotionEvent.ACTION_DOWN:  
	            mode = DRAG; 
	            break;  
	        case MotionEvent.ACTION_POINTER_DOWN:  
	            mode = ZOOM;  
	            break;  
	        case MotionEvent.ACTION_MOVE:  
	            if (mode != ZOOM) {
	            	mTrendLineView.setTrendLineTop(mFramelayout.getTop());
	    			// 弹出框时，只在当前屏幕的K线中，移动标尺
	            	if (mTrendLineView.mPopinfoFlag == true) {
	            		mTrendLineView.onMoveLine(event);
	            		showPopInfo(true);
	            	}else
	            	{
	        			showPopInfo(false);
	            	}
	            }
	            break;  
	        case MotionEvent.ACTION_UP:  
	        case MotionEvent.ACTION_POINTER_UP: 
	            mode = NONE;
	            if (mTrendLineView.mPopinfoFlag == true)
	            {
	            	mTrendLineView.DismissInfo(true);
	            	showPopInfo(false);
	            }
	            break; 
	        }  
		
		}
		switch (event.getAction() & MotionEvent.ACTION_MASK) {  
        case MotionEvent.ACTION_DOWN:  
            mode = DRAG;    
            break;  
        case MotionEvent.ACTION_POINTER_DOWN:  
            mode = ZOOM;    
            break;  
        case MotionEvent.ACTION_MOVE:  
            break;  
        case MotionEvent.ACTION_UP:  
        case MotionEvent.ACTION_POINTER_UP:  
            mode = NONE; 
            break; 
        }
		if (mode == ZOOM)
		{
			return false;
		}
		return mDoubleDetector.onTouchEvent(event);
	}
	
	


	
	// 触碰两点间距离  
		private float spacing(MotionEvent event) {
			float ret = 0f;
			try {
				float x = event.getX(0) - event.getX(1);
				float y = event.getY(0) - event.getY(1);
				ret = FloatMath.sqrt(x * x + y * y);
			} catch (IllegalArgumentException e) {

				e.printStackTrace();
			}
			return ret;
		}
	
	private void showPopInfo(boolean bPop)
	{
		if (bPop)
		{
			mLayoutTitle.setVisibility(View.GONE);
			if (mViewType == ViewTools.VIEW_TRENDLINE)
			{
				int index = mTrendLineView.getCurrentSelectIndex();
				if (index < 0 || index >= mTrendDataArray.size())
				{
					return;
				}
				TagLocalTrendData TData = mTrendDataArray.get(index);

				// 价格
				String value = ViewTools.getStringByPrice(TData.now,
						mOptionData.HQData.nLastPrice,
						mOptionData.PriceDecimal, mOptionData.PriceRate);

				mJiage.setTextColor(ViewTools.getColor(TData.now,
						mOptionData.HQData.nLastClear));
				mJiage.setText(value);

				if (mTrendDataArray.get(0).average <= 0 && index == 0) {
					TData.average = TData.now;
				}

				if (TData.average <= 0 && index > 0) {
					for (int i = index - 1; i >= 0; i--) {
						if (mTrendDataArray.get(i).average > 0) {
							TData.average = mTrendDataArray.get(i).average;
							break;
						}
						if (i == 0) {
							if (mTrendDataArray.get(0).average <= 0) {
								TData.average = TData.now;
							}
						}
					}
				}

				// 均价
				value = ViewTools.getStringByPrice(TData.average,
						mOptionData.HQData.nLastPrice,
						mOptionData.PriceDecimal, mOptionData.PriceRate);
				mJunjia.setTextColor(ViewTools.getColor(TData.average,
						mOptionData.HQData.nLastClear));
				mJunjia.setText(value);

				// 涨跌幅
				value = ViewTools.getZDF(
						TData.now - mOptionData.HQData.getnLastClear(),
						mOptionData.HQData.getnLastClear(), 1, true, true);
				mZD_trend.setTextColor(ViewTools.getColor(TData.now,
						mOptionData.HQData.nLastClear));
				mZD_trend.setText(value);

				// 成交量
				value = ViewTools.getStringByVolume((long) TData.volume,
						mOptionData.market, mOptionData.VolUnit, 6, false);
				mCJL.setTextColor(value.startsWith("--") ? Color.BLACK
						: Color.BLACK);//ColorConstant.COLOR_YELLOW 之前后面显示的是黄色      新需求要求颜色 改为 统一黑色 
				mCJL.setText(value);

				// 时间

				String date = STD.getDateSringyyyymmdd(TData.date);
				String sub_date = date;
				if (date.length() >= 4) {
					sub_date = date.substring(date.length() - 4, date.length());
				}
				String time = STD.getTimeSringhhmm(TData.time);// / 100
				//mTime_trend.setTextColor(Color.BLACK);
				mTime_trend.setText(sub_date + " " + time);

				mLayoutChangeTitle_kline.setVisibility(View.GONE);
				mLayoutChangeTitle_trend.setVisibility(View.VISIBLE);
			}else
			{
				ArrayList<TagLocalKLineData> klineArray = mKLineView.getKLineData();
				int kIndex = mKLineView.getCurrentSelectIndex();
				if (klineArray == null || kIndex < 0 || kIndex >= klineArray.size())
				{
					return;
				}
				
				TagLocalKLineData KData = klineArray.get(kIndex);
				if (KData == null) return;
		
				TagLocalKLineData prevData;
				if (kIndex > 0) {
					prevData = klineArray.get(kIndex - 1);
				} else {
					prevData = KData;
				}
				// 开盘
				String value = ViewTools.getStringByPrice(KData.open, mOptionData.HQData.nLastPrice, mOptionData.PriceDecimal, mOptionData.PriceRate);
				mKai.setTextColor(ViewTools.getColor(KData.open, prevData.close));
				mKai.setText(value);
				// 最高--------
				value = ViewTools.getStringByPrice(KData.high, mOptionData.HQData.nLastPrice, mOptionData.PriceDecimal, mOptionData.PriceRate);
				mGao.setTextColor(ViewTools.getColor(KData.high, prevData.close));
				mGao.setText(value);
				// 最低
				value = ViewTools.getStringByPrice(KData.low, mOptionData.HQData.nLastPrice, mOptionData.PriceDecimal, mOptionData.PriceRate);
				mDi.setTextColor(ViewTools.getColor(KData.low, prevData.close));
				mDi.setText(value);
				// 收盘
				value = ViewTools.getStringByPrice(KData.close, mOptionData.HQData.nLastPrice, mOptionData.PriceDecimal, mOptionData.PriceRate);
				mShou.setTextColor(ViewTools.getColor(KData.close, prevData.close));
				mShou.setText(value);
				
				
				if (mKLineView.GetCycle() == Global_Define.HISTORY_TYPE_DAY && kIndex == klineArray.size()-1) {
					value=ViewTools.getStringByFieldID(mOptionData, Global_Define.FIELD_HQ_ZDF_SIGN);
				}else{
					// 幅度
					value = ViewTools.getZDF(KData.close - prevData.close, prevData.close, 1, true, true);
				}
				
				mZD.setTextColor(ViewTools.getColor(KData.close, prevData.close));
				mZD.setText(value);
				
				// 时间
				String  time = "";
				if (mKLineView.GetCycle() == Global_Define.HISTORY_TYPE_DAY
						|| mKLineView.GetCycle() == Global_Define.HISTORY_TYPE_WEEK
						|| mKLineView.GetCycle() == Global_Define.HISTORY_TYPE_MONTH) {
					time = STD.getDateSringyyyymmdd(klineArray
							.get(kIndex).date);
				} else {
					String date=STD.getDateSringyyyymmdd(klineArray.get(kIndex).date);
					String sub_date=date;
					if(date.length()>=4){
						sub_date=date.substring(date.length()-4, date.length());
					}
					time =sub_date+" "+STD.getTimeSringhhmm(klineArray.get(kIndex).time / 100);
				}
				
				mTime.setText(time);
				
				mLayoutChangeTitle_kline.setVisibility(View.VISIBLE);
				mLayoutChangeTitle_trend.setVisibility(View.GONE);
			}
			
		}else
		{
			mLayoutTitle.setVisibility(View.VISIBLE);
			mLayoutChangeTitle_kline.setVisibility(View.GONE);
			mLayoutChangeTitle_trend.setVisibility(View.GONE);
		}
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
	public void resetKLineParam() {
		if (mKLineView != null) {
			mKLineView.resetKLineParam();
		}
	}
	
	private void clearDetailScreen()
	{
		if (mViewType != ViewTools.VIEW_TRENDLINE)
		{
			//mMyApp.resetLandTrendDataArray();
			mMyApp.resetLandKLineDataArray();
			mMyApp.resetLandKLineMinArray();
			mMyApp.resetLandKLineMonthArray();
			mMyApp.resetLandKLineWeekArray();
			resetKLineParam();
			if (mKLineView != null)
			{
				mKLineView.updateAllData();
			}
		}
	}
	
	
	/**
	 * 双击消失
	 * 
	 * OnGestureListener的接口有这几个：
		// 单击，触摸屏按下时立刻触发  
		abstract boolean onDown(MotionEvent e);  
		// 抬起，手指离开触摸屏时触发(长按、滚动、滑动时，不会触发这个手势)  
		abstract boolean onSingleTapUp(MotionEvent e);  
		// 短按，触摸屏按下后片刻后抬起，会触发这个手势，如果迅速抬起则不会  
		abstract void onShowPress(MotionEvent e);  
		// 长按，触摸屏按下后既不抬起也不移动，过一段时间后触发  
		abstract void onLongPress(MotionEvent e);  
		// 滚动，触摸屏按下后移动  
		abstract boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);  
		// 滑动，触摸屏按下后快速移动并抬起，会先触发滚动手势，跟着触发一个滑动手势  
		abstract boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);  
		OnDoubleTapListener的接口有这几个：
		// 双击，手指在触摸屏上迅速点击第二下时触发  
		abstract boolean onDoubleTap(MotionEvent e);  
		// 双击的按下跟抬起各触发一次  
		abstract boolean onDoubleTapEvent(MotionEvent e);  
		// 单击确认，即很快的按下并抬起，但并不连续点击第二下  
		abstract boolean onSingleTapConfirmed(MotionEvent e);  

	 * @author pobo
	 * @date   2015-11-25 下午2:51:41
	 * @className ZQLandscapeViewActivity.java
	 * @verson 1.0.0
	 */
	class MyGestureListener extends SimpleOnGestureListener{
		
		// 双击，手指在触摸屏上迅速点击第二下时触发  
		@Override
		public boolean onDoubleTap(MotionEvent e) {
//			Intent intent = new Intent();
//			intent.putExtra(FLAG, mViewType);
//			setResult(REQUEST_CODE, intent);
			ZQLandscapeViewActivity.this.finish();
			return super.onDoubleTap(e);
		}              
		
		// 双击的按下跟抬起各触发一次  
//		@Override
//		public boolean onDoubleTapEvent(MotionEvent e) {
//			Intent intent = new Intent();
//			intent.putExtra(FLAG, mViewType);
//			setResult(REQUEST_CODE, intent);
//			ZQLandscapeViewActivity.this.finish();
//			return super.onDoubleTapEvent(e);
//		}
		
		/**
		 * --------------------onDown
		 */
		@Override
		public boolean onDown(MotionEvent arg0) {
			return false;
		}


		/**
		 * --------------------onFling
		 */
		@Override
		public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
				float arg3) {
			return false;
		}
		
		
		@Override
		public void onLongPress(MotionEvent event) {
			if(mViewType == ViewTools.VIEW_TRENDLINE)
			{
				mTrendLineView.setTrendLineTop(mFramelayout.getTop());
				mTrendLineView.onLongPressLine(event);
				if (mTrendLineView.mPopinfoFlag)
				{
					showPopInfo(true);
				}else
				{
					showPopInfo(false);
				}
				
			}else if(
					 mViewType == ViewTools.VIEW_KLINE_DAY ||
					 mViewType == ViewTools.VIEW_KLINE_WEEK||
					 mViewType == ViewTools.VIEW_KLINE_MONTH ||
					 mViewType == ViewTools.VIEW_KLINE_1M  ||
					 mViewType == ViewTools.VIEW_KLINE_3M  || 
					 mViewType == ViewTools.VIEW_KLINE_5M  ||
					 mViewType == ViewTools.VIEW_KLINE_15M || 
					 mViewType == ViewTools.VIEW_KLINE_30M ||
					 mViewType == ViewTools.VIEW_KLINE_60M ||
					 mViewType == ViewTools.VIEW_KLINE_120M ||
					 mViewType == ViewTools.VIEW_KLINE_240M
					){
				mKLineView.setKLineTop(mFramelayout.getTop());
				mKLineView.onLongPressLine(event);
				if (mKLineView.mPopinfoFlag)
				{
					showPopInfo(true);
				}else
				{
					showPopInfo(false);
				}
			}
		}
		
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY) {
			if (mViewType == ViewTools.VIEW_KLINE_DAY || mViewType == ViewTools.VIEW_KLINE_WEEK
					|| mViewType == ViewTools.VIEW_KLINE_MONTH || mViewType == ViewTools.VIEW_KLINE_1M
					|| mViewType == ViewTools.VIEW_KLINE_3M || mViewType == ViewTools.VIEW_KLINE_5M
					|| mViewType == ViewTools.VIEW_KLINE_15M || mViewType == ViewTools.VIEW_KLINE_30M
					|| mViewType == ViewTools.VIEW_KLINE_60M || mViewType == ViewTools.VIEW_KLINE_120M
					|| mViewType == ViewTools.VIEW_KLINE_240M) {
				mKLineView.setKLineTop(mFramelayout.getTop());
				// 弹出框时，只在当前屏幕的K线中，移动标尺
				if (mKLineView.mPopinfoFlag == true) {
					if (distanceX > 0.0 || distanceX < 0.0)
						mKLineView.onMoveLine(e2);
					return true;
				}
			}
			return false;
		}
		


		@Override
		public void onShowPress(MotionEvent arg0) {
			
		}
		
		
		@Override
		public boolean onSingleTapUp(MotionEvent event) {
			if(mViewType == ViewTools.VIEW_KLINE_DAY || mViewType == ViewTools.VIEW_KLINE_WEEK
					|| mViewType == ViewTools.VIEW_KLINE_MONTH || mViewType == ViewTools.VIEW_KLINE_1M
					|| mViewType == ViewTools.VIEW_KLINE_3M || mViewType == ViewTools.VIEW_KLINE_5M
					|| mViewType == ViewTools.VIEW_KLINE_15M || mViewType == ViewTools.VIEW_KLINE_30M
					|| mViewType == ViewTools.VIEW_KLINE_60M || mViewType == ViewTools.VIEW_KLINE_120M
					|| mViewType == ViewTools.VIEW_KLINE_240M) {
				
				mKLineView.setKLineTop(mFramelayout.getTop());
				mKLineView.onTouchLine(event);
				if (mKLineView.mPopinfoFlag)
				{
					showPopInfo(true);
				}else
				{
					showPopInfo(false);
				}
				
			}else if(mViewType == ViewTools.VIEW_TRENDLINE){
				mTrendLineView.setTrendLineTop(mFramelayout.getTop());
				mTrendLineView.onTouchLine(event);
				if (mTrendLineView.mPopinfoFlag)
				{
					showPopInfo(true);
				}else
				{
					showPopInfo(false);
				}
				
			}
			return false;
		}
		
		
	}
	

	
}
