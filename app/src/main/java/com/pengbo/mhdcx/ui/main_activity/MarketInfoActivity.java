package com.pengbo.mhdcx.ui.main_activity;

import java.util.ArrayList;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.CPbDataDecode;
import com.pengbo.mhdzq.data.CPbDataPackage;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.data.CMarketInfoItem.mktGroupInfo;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.net.CMessageObject;
import com.pengbo.mhdzq.net.GlobalNetConnect;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.net.MyByteBuffer;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.MIniFile;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.zq_activity.HdActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MarketInfoActivity extends HdActivity implements OnClickListener,
		OnItemClickListener {
	private static final String TAG = "MarketInfoActivity";
	/**
	 * 目前显示3个指数
	 */
	public static final int ZHISHU_NUM = 3;
	private MyApp mMyApp;
	/**
	 * zhishu list
	 */
	private ArrayList<TagCodeInfo> mZhiShuCodeInfos;
	/**
	 * 上部标题栏中间的文本;
	 */
	private TextView head_tv_option;
	/**
	 * 右边的搜索期权列表
	 */
	private TextView head_btn_searchlist;
	/**
	 * 存放了三条文本：“上证指数”，“深圳成批”，“上证50”
	 */
	private TextView mTV_Names[] = new TextView[3];
	/**
	 * 存放了三种指数的价格
	 */
	private TextView mTV_Prices[] = new TextView[3];
	/**
	 * 存放了三种指数的其它值
	 */
	private TextView mTV_ZFs[] = new TextView[3];
	// mTV_Name2, mTV_Price2, mTV_ZF2,
	// mTV_Name3, mTV_Price3, mTV_ZF3; //指数信息
	/**
	 * 上部标题栏右部的刷新图片
	 */
	private ImageView mRefesh;
	/**
	 * T型报价
	 */
	private View mTBaoJia;
	/**
	 * 热炒合约
	 */
	private View mReChaoHeYue;
	/**
	 * 合约筛选器
	 */
	private View mHeYueShaiXuan;
	/**
	 * 请求标记0-指数信息请求，1-指数信息推送
	 */
	public int mRequestCode[];

	private Handler mHandler = new Handler() {

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

				if (nFrameType == Global_Define.MFT_MOBILE_DATA_APPLY) {
					if (aMsgObject.nErrorCode != 0) {
						L.e(TAG, "RequestCode:" + nRequestCode);
						return;
					}

					if (mRequestCode[0] == nRequestCode)// 指数
					{
						parseStockInfoData(aMsgObject.getData(),
								aMsgObject.getDataLength());
						updateZhiShuView();
					}
				} else if (nFrameType == Global_Define.MFT_MOBILE_PUSH_DATA) {
					L.i(TAG, "Received push data");
					updateZhiShuView();
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
		setContentView(R.layout.activity_market_info);
		mMyApp = (MyApp) this.getApplication();
		initData();
		initView();
	}

	private void initData() {
		mRequestCode = new int[2];
		mZhiShuCodeInfos = mMyApp.mZhiShuCodeInfos;
	}

	/**
	 * 初始化控件
	 */
	private void initView() {

		head_btn_searchlist = (TextView) this
				.findViewById(R.id.header_right_search);
		head_tv_option = (TextView) this
				.findViewById(R.id.header_middle_textview);// the middle of the
															// text

		mTV_Names[0] = (TextView) this.findViewById(R.id.tv_name1);
		mTV_Prices[0] = (TextView) this.findViewById(R.id.tv_price1);
		mTV_ZFs[0] = (TextView) this.findViewById(R.id.tv_zf1);

		mTV_Names[1] = (TextView) this.findViewById(R.id.tv_name2);
		mTV_Prices[1] = (TextView) this.findViewById(R.id.tv_price2);
		mTV_ZFs[1] = (TextView) this.findViewById(R.id.tv_zf2);

		mTV_Names[2] = (TextView) this.findViewById(R.id.tv_name3);
		mTV_Prices[2] = (TextView) this.findViewById(R.id.tv_price3);
		mTV_ZFs[2] = (TextView) this.findViewById(R.id.tv_zf3);

		mRefesh = (ImageView) this.findViewById(R.id.header_right_refresh);

		mTBaoJia = this.findViewById(R.id.marketinfo_layout1);
		mReChaoHeYue = this.findViewById(R.id.marketinfo_layout2);
		mHeYueShaiXuan = this.findViewById(R.id.marketinfo_layout3);
		mTBaoJia.setOnClickListener(this);
		mReChaoHeYue.setOnClickListener(this);
		mHeYueShaiXuan.setOnClickListener(this);

		head_tv_option.setText("行情 ");
		head_btn_searchlist.setVisibility(View.GONE);
		mRefesh.setVisibility(View.VISIBLE);
		mRefesh.setOnClickListener(this);
	}

	private void updateZhiShuView() {
		for (int i = 0; i < ZHISHU_NUM && i < mZhiShuCodeInfos.size(); i++) {
			mTV_Names[i].setText(mZhiShuCodeInfos.get(i).name);
			TagLocalStockData aStockInfo = new TagLocalStockData();
			if (mMyApp.mZhiShuData.search(aStockInfo,
					mZhiShuCodeInfos.get(i).market,
					mZhiShuCodeInfos.get(i).code)) {
				// 现价幅度等
				String nowPrice = ViewTools.getStringByFieldID(aStockInfo,
						Global_Define.FIELD_HQ_NOW);
				String zd = ViewTools.getStringByFieldID(aStockInfo,
						Global_Define.FIELD_HQ_ZD);
				String fd = ViewTools.getStringByFieldID(aStockInfo,
						Global_Define.FIELD_HQ_ZDF_SIGN);

				mTV_Prices[i].setText(nowPrice);
				mTV_Prices[i].setTextColor(ViewTools.getColorByFieldID(
						aStockInfo, Global_Define.FIELD_HQ_NOW));

				mTV_ZFs[i].setText(String.format("%s %s", zd, fd));
				mTV_ZFs[i].setTextColor(ViewTools.getColorByFieldID(aStockInfo,
						Global_Define.FIELD_HQ_NOW));
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_right_refresh:
			updateZhiShuView();
			break;
		case R.id.marketinfo_layout1:
			Intent intent1 = new Intent();
			intent1.setClass(this, TOfferActivity.class);
			startActivity(intent1);

			break;
		case R.id.marketinfo_layout2:
			Intent intent2 = new Intent();
			intent2.setClass(this, HotOptionActivity.class);
			startActivity(intent2);
			break;
		case R.id.marketinfo_layout3:
			Intent intent = new Intent();
			intent.setClass(this, ScreenActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}

	@Override
	protected void onPause() {
		mMyApp.setHQPushNetHandler(null);
		GlobalNetProgress.HQRequest_MultiCodeInfoPush(mMyApp.mHQPushNet, null,
				0, 0);
		super.onPause();
	}

	@Override
	protected void onResume() {
		updateZhiShuView();
		queryHQPushInfo();
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	// 获取标的信息
	private void queryZhishuInfo() {

		if (mZhiShuCodeInfos == null)
			return;

		mMyApp.setCertifyNetHandler(mHandler);
		mRequestCode[0] = GlobalNetProgress.HQRequest_StockInfo(
				mMyApp.mCertifyNet, mZhiShuCodeInfos, 0,
				mZhiShuCodeInfos.size());
	}

	// 获取指数行情推送信息
	private void queryHQPushInfo() {
		mMyApp.setHQPushNetHandler(mHandler);
		mRequestCode[1] = GlobalNetProgress
				.HQRequest_MultiCodeInfoPush(mMyApp.mHQPushNet,
						mZhiShuCodeInfos, 0, mZhiShuCodeInfos.size());
	}

	private void parseStockInfoData(byte[] szData, int nSize) {
		L.e("MyApp", "Start parseStockInfoData");
		int nCount = 0;
		int offset = 0;
		while (offset >= 0 && nSize - offset >= 4) {
			int wPackSize = MyByteBuffer.getInt(szData, offset);
			if (wPackSize < 0) {
				L.e("MyApp", "ERROR: parseStockInfoData wPackSize = "
						+ wPackSize);
				break;
			}
			offset += 4;
			if (wPackSize > nSize - offset)
				break;

			CPbDataPackage pack = new CPbDataPackage();
			wPackSize = CPbDataDecode.DecodeOnePackage(szData, offset,
					wPackSize, pack);

			offset += wPackSize;
			if (pack.m_wPackageID == 1) {

				TagLocalStockData stockData = new TagLocalStockData();
				stockData.HQData.code = pack.GetNormalFieldByID(10).GetString();
				stockData.HQData.market = (short) pack.GetNormalFieldByID(11)
						.GetInt16();
				stockData.name = pack.GetNormalFieldByID(3).GetString();

				stockData.GroupOffset = (short) pack.GetNormalFieldByID(17)
						.GetInt8();
				stockData.group = stockData.GroupOffset;
				stockData.PriceDecimal = (short) pack.GetNormalFieldByID(14)
						.GetInt8();
				stockData.VolUnit = (short) pack.GetNormalFieldByID(15)
						.GetInt16();
				stockData.PriceRate = pack.GetNormalFieldByID(16).GetInt32();
				// DebugLog(@"  StockData[%d] marketId=%d,Code=%s,name=%@",nCount,stockData.HQData.market,stockData.HQData.code,[NSString
				// stringWithCString:stockData.name
				// encoding:NSUTF8StringEncoding]);
				nCount++;
				if (mMyApp.mStockConfigData.updateData(stockData, true) == false) {
					L.e("MyApp",
							"ERROR: mMyApp.mStockConfigData. updateData failed");
				}
			}
		}
		L.e("MyApp", "End parseStockInfoData,nRecordCount=" + nCount);

		// 更新市场信息
		TagLocalStockData aStockData;
		mktGroupInfo groupRecord;
		for (int m = 0; m < mMyApp.mStockConfigData.getNum(); m++) {
			aStockData = mMyApp.mStockConfigData.getItem(m);
			groupRecord = mMyApp.mMarketInfo.searchMarketGroupInfo(
					aStockData.HQData.market, null,
					(short) aStockData.GroupOffset);
			if (groupRecord != null) {
				aStockData.TradeFields = groupRecord.TradeFields;
				STD.memcpy(aStockData.Start, groupRecord.Start, 4);
				STD.memcpy(aStockData.End, groupRecord.End, 4);
				// L.e("MyApp"," GroupInfoData marketId="+
				// aStockData.HQData.market + ",TradeFields="+
				// aStockData.TradeFields + ",Start[0]=" + aStockData.Start[0] +
				// ",End[0]=" + aStockData.End[0]);
			} else {
				L.e("MyApp", "ERROR: MarketInfo.search failed ,marketId="
						+ aStockData.HQData.market + ",code="
						+ aStockData.HQData.code);
			}
		}
		aStockData = null;
		groupRecord = null;
	}
}
