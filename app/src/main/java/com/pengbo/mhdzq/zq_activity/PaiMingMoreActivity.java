package com.pengbo.mhdzq.zq_activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.PaiMingMoreAdapter;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.CPbDataDecode;
import com.pengbo.mhdzq.data.CPbDataPackage;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.data.TopRankData;
import com.pengbo.mhdzq.main_activity.ZiXuanFastSearchActivity;
import com.pengbo.mhdzq.net.CMessageObject;
import com.pengbo.mhdzq.net.GlobalNetConnect;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.net.MyByteBuffer;
import com.pengbo.mhdzq.tools.L;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PaiMingMoreActivity extends HdActivity implements OnItemClickListener, OnClickListener {
	public static final String TAG = "PaiMingMoreActivity";
	private static final int TIMER_REQUEST = 1;
	
	private Context mContext;
	private MyApp mMyApp;
	private ListView lv_paiming;
	private PaiMingMoreAdapter mListAdapter;
	
	private TextView tv_title, tv_field;
	private ImageView imgRightSearch, imgLeftBack;
	
	private ArrayList<TopRankData> mListData;
	private int mTopField;

	private int mRequestCode[];// 请求标记0-排名请求
	
	private Timer mTimerRequestPaiMing = null;
	public Dialog mProgress;

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			int nFrameType = msg.arg1;
			int nRequestCode = msg.arg2;

			switch (msg.what) {
			case TIMER_REQUEST:
			{
				requestPaiMing();
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
						parseTopRankData(aMsgObject.getData(),
								aMsgObject.getDataLength());
						
						closeProgress();
						if (mListAdapter != null)
						{
							mListAdapter.notifyDataSetChanged();
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

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.zq_paiming_more_activity);
		
		mContext = this;
		
		mTopField = this.getIntent().getIntExtra("TOP_FIELD", -1);
		String strTitle = this.getIntent().getStringExtra("TOP_FIELD_NAME");
		if (strTitle == null || strTitle.isEmpty())
		{
			strTitle = "排行榜";
		}
		
		mMyApp = (MyApp) this.getApplication();
		mRequestCode = new int[1];
		
		tv_title = (TextView) findViewById(R.id.tv_public_black_head_title_middle_name);
		tv_title.setVisibility(View.VISIBLE);
		tv_title.setText(strTitle);
		
		tv_field = (TextView) findViewById(R.id.tv_field);
		tv_field.setText(strTitle);
		
		imgRightSearch = (ImageView) findViewById(R.id.img_public_black_head_title_right_blue_search);
		imgRightSearch.setVisibility(View.VISIBLE);
		imgRightSearch.setOnClickListener(this);
		
		imgLeftBack = (ImageView) findViewById(R.id.img_public_black_head_title_left_blue_back);
		imgLeftBack.setVisibility(View.VISIBLE);
		imgLeftBack.setOnClickListener(this);
		
		lv_paiming = (ListView) findViewById(R.id.lv_paiming);
		if (mListData == null)
		{
			mListData = new ArrayList<TopRankData>();// 初始化集合
		}
		if (mListAdapter == null)
		{
			mListAdapter = new PaiMingMoreAdapter(this, mListData, mTopField);
		}
		lv_paiming.setAdapter(mListAdapter);
		lv_paiming.setOnItemClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		requestPaiMing();
		startRequestPMTimer(30);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		stopRequestPMTimer();
		closeProgress();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void startRequestPMTimer(int time)// "快捷反手"开仓撤单
	{
		stopRequestPMTimer();

		mTimerRequestPaiMing = new Timer();
		mTimerRequestPaiMing.schedule(new TimerTask() {
			//
			public void run() {
				if (mHandler != null)
				{
					Message msg = mHandler.obtainMessage();
					msg.what = TIMER_REQUEST;
					mHandler.sendMessage(msg);
				}
				//requestPaiMing();
			}
		}, time * 1000, time * 1000);
	}

	private void stopRequestPMTimer() {
		if (mTimerRequestPaiMing != null) {
			mTimerRequestPaiMing.cancel();
		}
		mTimerRequestPaiMing = null;
	}
	
	//fileds:排名字段，可以多个
	//reqCount:请求个数
	private void requestPaiMing()
	{
		if (mTopField != -1)
		{
			short[] fields = new short[1];
			fields[0] = (short) mTopField;
			
			showProgress("加载中，请稍候...");
			//range  排名范围  （ 80 - 沪深A股 81 - 沪A股82 - 沪B股83 - 深A股84 - 深B股85 - 沪债86 - 深债87 - 中小板88 - 沪深B股89 - 沪深权证90 - 创业板91 - 个股期权缺省 - 沪深A）
			short range = 80;
			mMyApp.setCertifyNetHandler(mHandler);
			mRequestCode[0] = GlobalNetProgress.HQRequest_PaiMing(mMyApp.mCertifyNet, range, (short) 1, fields, 0);
		}
	}

	// parse top rank data and store it into ArrayList<TagLocalTrendData>
	private void parseTopRankData(byte[] szData, int nSize) {
		L.i(TAG, "******************Start parseTopRankData************");
		int nCount = 0;
		int offset = 0;
		while (nSize - offset >= 4) {
			int wPackSize = MyByteBuffer.getInt(szData, offset);
			if (wPackSize < 0) {
				L.e(TAG, "ERROR: parseTopRankData wPackSize = " + wPackSize);
				break;
			}
			offset += 4;
			if (wPackSize > nSize - offset)
				break;

			CPbDataPackage pack = new CPbDataPackage();
			wPackSize = CPbDataDecode.DecodeOnePackage(szData, offset,
					wPackSize, pack);

			offset += wPackSize;
			if (offset < 0) {
				L.e(TAG, "ERROR: CPbDataDecode.DecodeOnePackage failed");
			}

			if (pack.m_wPackageID == 18) {

				byte RankRegion = (byte) pack.GetNormalFieldByID(800).GetInt8();
				short RankField = (short) pack.GetNormalFieldByID(801)
						.GetInt8();

				mListData.clear();

				int nArraySize = pack.GetArraySize(0);
				for (int i = 0; i < nArraySize; i++) {
					TopRankData record = new TopRankData();
					record.sortField = RankField;
					record.code = pack.GetArrayFieldByID(10, i).GetString();
					record.market = (short) pack.GetArrayFieldByID(11, i)
							.GetInt16();
					record.fSortValue = pack.GetArrayFieldByID(802, i)
							.GetFloat();
					record.nLastClear = pack.GetArrayFieldByID(24, i)
							.GetInt32();
					record.nLastClose = pack.GetArrayFieldByID(23, i)
							.GetInt32();
					record.nLastPrice = pack.GetArrayFieldByID(29, i)
							.GetInt32();

					mListData.add(record);
				}

			}
		}

		L.i(TAG, "*******************End parseTopRankData,nRecordCount="
				+ nCount);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		TopRankData item = mListData.get(position);
		Intent intent = new Intent();
		intent.setClass(PaiMingMoreActivity.this, ZQMarketDetailActivity.class);

		TagCodeInfo optionCodeInfo = new TagCodeInfo(item.market, item.code, (short) 0, item.name);
		ZQMarketDetailActivity.mOptionCodeInfo = optionCodeInfo;
		startActivity(intent);
	}
	
	protected void showProgress(String msg) {
		closeProgress();

		if (mProgress == null) {
			mProgress = new Dialog(mContext, R.style.ProgressDialogStyle);
			mProgress.setContentView(R.layout.list_loading);
			TextView tv = (TextView) mProgress.findViewById(R.id.loading_text);
			tv.setText(msg);
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

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.img_public_black_head_title_left_blue_back:
			finish();
			break;
		case R.id.img_public_black_head_title_right_blue_search:
			Intent intent = new Intent(PaiMingMoreActivity.this, ZiXuanFastSearchActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
		
	}
}
