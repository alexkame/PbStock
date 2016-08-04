package com.pengbo.mhdcx.ui.activity;
import java.util.ArrayList;
import java.util.List;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.adapter.GainLossListViewAdapter;
import com.pengbo.mhdcx.adapter.HeadListDataService;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.TagProfitRecord;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.net.GlobalNetConnect;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdcx.view.GainLossView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ViewFlipper;

/**
 * 盈亏分析页
 * 
 * @author pobo
 * 
 */
public class GainLossAnalysisActivity extends BaseActivity implements
		OnClickListener, OnCheckedChangeListener {
	public static final int UI_CODE=1;
	private View mImgBack;// 顶部的 返回按钮
	private ImageView mRefresh;// 顶部的刷新 按钮
	
	private RadioGroup mRgThree;//平缓 正常 剧烈
	private TextView mTextViewUp1, mTextViewUp2, mTextViewDown;// 顶部 中间 的 标的 的名称 和到期时间
	private ListView mBottom_lv;
	GainLossListViewAdapter mAdapter;
	
	
	public static TagLocalStockData mOptionData;// 合约信息
	public static TagLocalStockData mStockData;// 合约对应的标的

	private HeadListDataService hService;

	/**** 传递过来的 code market **/
	private String code;
	private short market;
	private MyApp mMyApp;
	List<TagProfitRecord> mTagProfitRecords = null;// 不用初始化 业务类中有初始化
	private TagProfitRecord mPjsyRecord;

	private float bdyqbl = 1.0f;//默认是正常

	/************** 中间 总手 等 的数据 ********************************/
	private TextView field_hq_xqj, field_hq_nzjz, field_hq_sjjz, field_hq_ggl,
			field_hq_zsggl, field_hq_delta, field_hq_gamma, field_hq_theta,
			field_hq_rho, field_hq_vega, field_hq_yjl, field_hq_ylgl,
			field_hq_dzcb, field_hq_hyd, field_hq_gssyb;
	private TextView field_pjsy, field_pjsyl, field_pjrate;

	public FrameLayout mDraw;// 画盈亏分析的控件 设置高度为屏幕的1/3
	public int mWidth;
	public int mHeight;
	
	public ViewFlipper mFlipper;
	public GainLossView mGainLossView;
	public int mRequestCode[];// 请求标记 0:query push

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int nFrameType = msg.arg1;
			int nRequestCode = msg.arg2;
			
			switch (msg.what) {
			case GlobalNetConnect.MSG_UPDATE_DATA: 
			{
				if (nFrameType == Global_Define.MFT_MOBILE_PUSH_DATA)
				{
					// 更新数据（最新数据已经保存在mMyApp.mHQData里）
					TagLocalStockData optionStockData = new TagLocalStockData();
					if (mMyApp.mHQData.getData(optionStockData, market, code, false)) {
						mOptionData = optionStockData;
					}
					TagLocalStockData stockStockData = new TagLocalStockData();
					if (mMyApp.mStockConfigData.getData(stockStockData, mStockData.HQData.market,  mStockData.HQData.code, false)) {
						mStockData = stockStockData;
					}
					updateAllView();
				}
			}
				break;
			case UI_CODE:
				closeProgress();
				if (mAdapter == null) {
					mAdapter = new GainLossListViewAdapter(
							GainLossAnalysisActivity.this, mTagProfitRecords);
					mBottom_lv.setAdapter(mAdapter);
				} else {
					mAdapter.notifyDataSetChanged();
				}
				updatePJSYView();
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gain_loss_analysis);
		
		initView();
		setData();
		initLoad();
		initDraw();
	}
	
	@Override
	protected void onResume() {
		queryHQPushInfo();
		super.onResume();
	}
	
	
	private void initDraw() {
//		mGainLossView = new GainLossView(this);
//		mFlipper = (ViewFlipper)findViewById(R.id.flipper);
//        mFlipper.addView(mGainLossView);
//        mGainLossView.updateData(mOptionData);
	}

	

	private void initLoad() {
		new Thread() {
			public void run() {

				mTagProfitRecords = hService.getProfitRecordList(bdyqbl,
						mOptionData, mStockData, mPjsyRecord);
				mHandler.sendEmptyMessage(UI_CODE);
			};
		}.start();
		
	}

	/*************** 初始化控件 ***********************/
	private void initView() {
		mMyApp = (MyApp) getApplication();
		mRequestCode = new int[2];

		mPjsyRecord = new TagProfitRecord();
		hService = new HeadListDataService(this);

		// 获取屏幕的宽和高

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mWidth = dm.widthPixels;
		mHeight = dm.heightPixels;

		mDraw = (FrameLayout) this.findViewById(R.id.gain_loss_analysis_draw);
		mDraw.setLayoutParams(new LinearLayout.LayoutParams(mWidth, mHeight*1/3));
	//	mDraw.addView(mTrendView);
		

		// 返回按钮
		mImgBack =this
				.findViewById(R.id.linearlayout_out_of_back);
		mImgBack.setOnClickListener(this);

		// 刷新按钮
		mRefresh = (ImageView) this
				.findViewById(R.id.screen_detail_header_right_refresh);
		mRefresh.setOnClickListener(this);
		
		mRgThree=(RadioGroup) this.findViewById(R.id.history_radiogroup);
		mRgThree.setOnCheckedChangeListener(this);

		// 底部的listview
		mBottom_lv = (ListView) this.findViewById(R.id.gain_loss_listview);

		// 顶部的标的的名称和到期时间
		mTextViewUp1 = (TextView) this.findViewById(R.id.header_middle_tv_name1);
		mTextViewUp2 = (TextView) this.findViewById(R.id.header_middle_tv_name2);
		mTextViewDown = (TextView) this.findViewById(R.id.header_middle_tv_time);

		field_hq_xqj = (TextView) this.findViewById(R.id.field_hq_xqj);
		field_hq_nzjz = (TextView) this.findViewById(R.id.field_hq_nzjz);
		field_hq_sjjz = (TextView) this.findViewById(R.id.field_hq_sjjz);
		field_hq_ggl = (TextView) this.findViewById(R.id.field_hq_ggl);
		field_hq_zsggl = (TextView) this.findViewById(R.id.field_hq_zsggl);
		field_hq_delta = (TextView) this.findViewById(R.id.field_hq_delta);
		field_hq_gamma = (TextView) this.findViewById(R.id.field_hq_gamma);
		field_hq_theta = (TextView) this.findViewById(R.id.field_hq_theta);
		field_hq_rho = (TextView) this.findViewById(R.id.field_hq_rho);
		field_hq_vega = (TextView) this.findViewById(R.id.field_hq_vega);
		field_hq_yjl = (TextView) this.findViewById(R.id.field_hq_yjl);
		field_hq_ylgl = (TextView) this.findViewById(R.id.field_hq_ylgl);

		field_hq_dzcb = (TextView) this.findViewById(R.id.field_hq_dzcb);
		field_hq_hyd = (TextView) this.findViewById(R.id.field_hq_hyd);
		field_hq_gssyb = (TextView) this.findViewById(R.id.field_hq_gssyb);
		
		field_pjsy = (TextView) this.findViewById(R.id.gain_loss_anilysis_pjsy);
		field_pjsyl = (TextView) this.findViewById(R.id.gain_loss_anilysis_pjsyl);
		field_pjrate = (TextView) this.findViewById(R.id.gain_loss_anilysis_pjrate);
		field_pjrate.setVisibility(View.INVISIBLE);

		// 拿到传过来的对象
		Intent intent = GainLossAnalysisActivity.this.getIntent();
		Bundle bunde = intent.getExtras();

		code = bunde.getString(HeadOptionListActivity.INTENT_SERIALIZABLE_CODE);
		market = bunde
				.getShort(HeadOptionListActivity.INTENT_SERIALIZABLE_Market);

		hService = new HeadListDataService(GainLossAnalysisActivity.this);
		mOptionData = hService.getTagLocalStockData(market, code);

		mStockData = hService.getTagLocalStockDataAll(
				mOptionData.optionData.StockMarket,
				mOptionData.optionData.StockCode);
	}
	
	private void updateAllView()
	{
		setData();
		initLoad();
		//mGainLossView.updateData(mOptionData);
	}
	
	private void updatePJSYView()
	{
		if (mPjsyRecord != null)
		{
			field_pjsy.setText(String.format("%.2f", mPjsyRecord.sy));
			field_pjsyl.setText(String.format("%.2f%s", mPjsyRecord.syl*100, "%"));
			field_pjrate.setText(String.format("%.2f%s", mPjsyRecord.rate*100, "%"));
		}else
		{
			field_pjsy.setText(Global_Define.STRING_VALUE_EMPTY);
			field_pjsyl.setText(Global_Define.STRING_VALUE_EMPTY);
			field_pjrate.setText(Global_Define.STRING_VALUE_EMPTY);
		}
	}

	private void setData() {
		field_hq_xqj.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_XQJ, mStockData));
		field_hq_nzjz.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_NZJZ, mStockData));

		field_hq_sjjz.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_SJJZ, mStockData));

		field_hq_ggl.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_GGL, mStockData));

		field_hq_zsggl.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_ZSGGL, mStockData));

		field_hq_delta.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_Delta, mStockData));

		field_hq_gamma.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_Gamma, mStockData));

		field_hq_theta.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_Theta, mStockData));

		field_hq_rho.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_Rho, mStockData));

		field_hq_vega.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_Vega, mStockData));

		field_hq_yjl.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_YJL, mStockData));
		field_hq_ylgl.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_YLGL, mStockData));

		field_hq_dzcb.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_DZCB, mStockData));

		field_hq_hyd.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_HYD, mStockData));

		field_hq_gssyb.setText(ViewTools.getStringByFieldID(mOptionData,
				Global_Define.FIELD_HQ_GSSYB, mStockData));

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
				index = name.indexOf("估");
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.linearlayout_out_of_back:
			GainLossAnalysisActivity.this.finish();
			break;
		case R.id.screen_detail_header_right_refresh:

		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.history_1:// 平缓
			bdyqbl = 0.7f;
			showProgress();

			new Thread() {
				public void run() {
					L.e("bdyqbl__0.7f", mOptionData.code + "..." + mStockData.market+"****"+bdyqbl+"****");
					mTagProfitRecords = hService.getProfitRecordList(bdyqbl,
							mOptionData, mStockData, mPjsyRecord);
					for (int i = 0; i < mTagProfitRecords.size(); i++) {
						L.e("222222222", mTagProfitRecords.get(i).price+"---");
					}
					mAdapter.setLists(mTagProfitRecords);
					mHandler.sendEmptyMessage(UI_CODE);
				};
			}.start();
			
		
			break;
		case R.id.history_2:// 正常
			bdyqbl = 1.0f;
			// ViewTools.getProfitRecordList(tempProfitRecord, bdyqbl,
			// mOptionData, mStockData);
			showProgress();
			new Thread() {
				public void run() {
					L.e("bdyqbl__1.0f", mOptionData.code + "..." + mStockData.market+"****"+bdyqbl+"****");
					mTagProfitRecords = hService.getProfitRecordList(bdyqbl,
							mOptionData, mStockData, mPjsyRecord);
					
					//for (int i = 0; i < mTagProfitRecords.size(); i++) {
					//	L.e("222222222b dyqbl__1.0f", mTagProfitRecords.get(i).price+"---");
					//}
					mAdapter.setLists(mTagProfitRecords);
					mHandler.sendEmptyMessage(UI_CODE);
				};
			}.start();
			
			break;
		case R.id.history_3:// 剧烈
			bdyqbl = 1.5f;
			// ViewTools.getProfitRecordList(tempProfitRecord, bdyqbl,
			// mOptionData, mStockData);
			
			
			showProgress();
			new Thread() {
				public void run() {
					L.e("bdyqbl__1.5f", mOptionData.code + "..." + mStockData.market+"****"+bdyqbl+"****");
					mTagProfitRecords = hService.getProfitRecordList(bdyqbl,
							mOptionData, mStockData, mPjsyRecord);
					
					//for (int i = 0; i < mTagProfitRecords.size(); i++) {
					//	L.e("222222222b dyqbl__1.5f", mTagProfitRecords.get(i).price+"---");
					//}
					mAdapter.setLists(mTagProfitRecords);
					mHandler.sendEmptyMessage(UI_CODE);
				};
			}.start();
			break;

		default:
			break;
		}
	}
	
	// 获取期权行情信息
	private void queryHQPushInfo() {

		ArrayList<TagCodeInfo> codelist = new ArrayList<TagCodeInfo>();
		TagCodeInfo aCodeRecord = new TagCodeInfo(mOptionData.HQData.market, mOptionData.HQData.code, mOptionData.group, mOptionData.name);
		codelist.add(aCodeRecord);//option
		
		TagCodeInfo stockCodeRecord = new TagCodeInfo(mStockData.HQData.market, mStockData.HQData.code, mStockData.group, mStockData.name);
		codelist.add(stockCodeRecord);

		mMyApp.setHQPushNetHandler(mHandler);
		mRequestCode[0] = GlobalNetProgress.HQRequest_MultiCodeInfoPush(
				mMyApp.mHQPushNet, codelist, 0, codelist.size());
	}
}
