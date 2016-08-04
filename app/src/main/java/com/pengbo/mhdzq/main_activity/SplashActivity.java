package com.pengbo.mhdzq.main_activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.AppActivityManager;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.CCodeTableItem;
import com.pengbo.mhdzq.data.CDataCodeTable;
import com.pengbo.mhdzq.data.CMarketInfo;
import com.pengbo.mhdzq.data.CMarketInfoItem;
import com.pengbo.mhdzq.data.CMarketInfoItem.mktGroupInfo;
import com.pengbo.mhdzq.data.CPbDataDecode;
import com.pengbo.mhdzq.data.CPbDataField;
import com.pengbo.mhdzq.data.CPbDataItem;
import com.pengbo.mhdzq.data.CPbDataPackage;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.PublicData.TagCodeInfo;
import com.pengbo.mhdzq.data.Rule;
import com.pengbo.mhdzq.data.TagLocalOptionRecord;
import com.pengbo.mhdzq.data.TagLocalStockData;
import com.pengbo.mhdzq.net.CMessageObject;
import com.pengbo.mhdzq.net.GlobalNetConnect;
import com.pengbo.mhdzq.net.GlobalNetProgress;
import com.pengbo.mhdzq.net.MyByteBuffer;
import com.pengbo.mhdzq.service.UnderService;
import com.pengbo.mhdzq.tools.FileService;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.MIniFile;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.ViewTools;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.zq_activity.HdActivity;

public class SplashActivity extends HdActivity {
	private static final String TAG = "SplashActivity";

	private Context mContext;
	private MyApp mMyApp;

	private static final int STOPSPLASH = 10;
	/**
	 * 程序启动时闪屏时间，ms
	 */
	private int mTime = 1000;
	/**
	 * 请求标记
	 * 0-login;1-marketinfo;3-GlobalParamInfo;4-1000.ini;5-CustomColumn.ini;
	 * 6-期权查询;7-标的查询;8-期权行情查询
	 */
	private int mRequestCode[];
	private ArrayList<Integer> mCodeTableRequest;
	private int mCurrentMarketIndex = 0;

	/**
	 * 当自动停留三秒后 读取是否是第一次安装 还是已经不是 第一次 进入 界面什么都不做停留三秒以后执行到handlerMessage方法
	 */
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Log.d("msg", msg.toString());
			int nFrameType = msg.arg1;
			int nRequestCode = msg.arg2;

			CMessageObject aMsgOject = null;
			if (msg.obj != null && (msg.obj instanceof CMessageObject)) {
				aMsgOject = (CMessageObject) msg.obj;
			} else {
				if (msg.obj != null && (msg.obj instanceof String)) {
					String errMsg = (String) msg.obj;
					L.e("MyApp", "handleMessage return" + errMsg);
					showErrorDialog(errMsg);
					super.handleMessage(msg);
					return;
				}
			}

			switch (msg.what) {
			case STOPSPLASH: {
				initCodeTable();
				doLogin();
				downloadListFile();
			}
				break;

			case GlobalNetConnect.MSG_CONNECT_ERROR: {
				showErrorDialog((String) msg.obj);
			}
				break;
			case GlobalNetConnect.MSG_UPDATE_DATA:
				if (aMsgOject.nErrorCode != 0) {
					showErrorDialog(aMsgOject.errorMsg);
					break;
				}

				if (nFrameType == Global_Define.MFT_MOBILE_LOGIN_APPLY)// 登录成功
				{
					if (mMyApp.mMarketInfo.mDownSuccess == false) {
						// 如果市场配置文件已经下载过，就不再下载
						queryMarketInfo();
					}

					doPushLogin();
				} else if (nFrameType == Global_Define.MFT_MOBILE_DATA_APPLY) {
					if (mRequestCode[2] == nRequestCode)// 上海市场码表查询
					{
						mRequestCode[2] = -1;
						boolean bSucceed = parseCodeTableData(
								aMsgOject.getData(), aMsgOject.getDataLength(),
								mMyApp.mCodeTableMarket[mCurrentMarketIndex]);
						if (bSucceed == false) {// 需要全量下载码表
							mRequestCode[2] = GlobalNetProgress
									.HQRequest_CodeTable(
											mMyApp.mCertifyNet,
											0,
											0,
											(short) mMyApp.mCodeTableMarket[mCurrentMarketIndex],
											(short) 0, (short) 0, (short) 0,
											(short) 0, (short) 0/* 0-全量，缺省1-增量 */);
						} else {
							mCurrentMarketIndex++;
							if (mCurrentMarketIndex < mMyApp.mCodeTableMarketNum) {
								queryCodeTable(
										mMyApp.mCodeTableMarket[mCurrentMarketIndex],
										mCurrentMarketIndex);
							} else {
								querySysConfigFile();
							}
						}
					} else if (mRequestCode[3] == nRequestCode)// 全局信息查询
					{
						mRequestCode[3] = -1;
						parseGlobalParamInfoData(aMsgOject.getData(),
								aMsgOject.getDataLength());
					} else if (mRequestCode[4] == nRequestCode)// 配置文件查询
					{
						mRequestCode[4] = -1;
						mMyApp.m_bZLHYDownload = true;
						parseSysConfigData(aMsgOject.getData(),
								aMsgOject.getDataLength(),
								MyApp.FILE_CONFIG_ZLHY);
						// queryGlobalParamInfo();
					} else if (mRequestCode[5] == nRequestCode)// 配置文件查询
					{
						mRequestCode[5] = -1;
						parseSysConfigData(aMsgOject.getData(),
								aMsgOject.getDataLength(),
								MyApp.SHUILV_CONFIGFILE);
					} else if (mRequestCode[6] == nRequestCode)// 期权查询
					{
						parseOptionInfoData(aMsgOject.getData(),
								aMsgOject.getDataLength());
						updateHQMarketInfo();
						queryStockInfo();
					} else if (mRequestCode[7] == nRequestCode)// 标的信息查询
					{
						parseStockInfoData(aMsgOject.getData(),
								aMsgOject.getDataLength());
						queryAllOptionHQInfo();
					} else if (mRequestCode[8] == nRequestCode)// 期权行情信息查询
					{
						mMyApp.parseAllOptionHQInfoData(aMsgOject.getData(),
								aMsgOject.getDataLength());
						mMyApp.mHQData.sort();
						showMainPage();
					} else { // 码表查询
						{
							for (int i = 0; i < mMyApp.mCodeTableMarketNum
									&& i < CDataCodeTable.MAX_SAVE_MARKET_COUNT; i++) {
								if (nRequestCode == mCodeTableRequest.get(i)
										.intValue()) {
									boolean bSucceed = parseCodeTableData(
											aMsgOject.getData(),
											aMsgOject.getDataLength(),
											mMyApp.mCodeTableMarket[i]);
									if (bSucceed == false) {// 需要全量下载码表
										int reqeustcode = GlobalNetProgress
												.HQRequest_CodeTable(
														mMyApp.mCertifyNet,
														0,
														0,
														(short) mMyApp.mCodeTableMarket[i],
														(short) 0, (short) 0,
														(short) 0, (short) 0,
														(short) 0/* 0-全量，缺省1-增量 */);
										mCodeTableRequest.set(i,
												Integer.valueOf(reqeustcode));
									}
									break;
								}
							}
							if (nRequestCode == mCodeTableRequest
									.get(mCodeTableRequest.size() - 1)) {
								queryOptionInfo();
							}
						}
					}
				} else if (nFrameType == Global_Define.MFT_MOBILE_MARKET_INFO_APPLY) {
					if (mRequestCode[1] == nRequestCode) {
						parseMarketInfoData(aMsgOject.getData(),
								aMsgOject.getDataLength());

						querySysConfigFile();
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						queryGlobalParamInfo();
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						queryCodeTable();
					}
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		mContext = this;
		mRequestCode = new int[9];
		mCodeTableRequest = new ArrayList<Integer>(
				CDataCodeTable.MAX_SAVE_MARKET_COUNT);
		mMyApp = (MyApp) getApplication();

		Message msg = new Message();
		msg.what = STOPSPLASH;
		mHandler.sendMessageDelayed(msg, mTime);

		ServiceConnection conn = new ServiceConnection() {
			public void onServiceDisconnected(ComponentName name) {
				L.v(TAG, "onServiceDisconnected");
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				L.v(TAG, "onServiceConnected");

			}
		};

		if (mMyApp.isCheckServerStart) {
			Intent intent = new Intent(UnderService.ACTION);
			intent.setPackage(getPackageName());
			try {
				mMyApp.bindService(intent, conn, BIND_AUTO_CREATE);
			} catch (Exception ex) {
				L.e(TAG, "Exception in bindService");
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 退出确认对话框
			AppActivityManager.getAppManager().AppExit(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void initLocalCodeTableFile(String folder, String fileName) {
		L.i(TAG, "start initLocalCodeTableFile");
		boolean bNeedUpdate = false;
		FileService file = new FileService(this.getApplicationContext());
		int size = file.getFileSize(fileName);

		try {
			InputStream input = this.getApplicationContext().getAssets()
					.open(folder + File.separator + fileName);

			if (size < 0)// 文件不存在
			{
				bNeedUpdate = true;
			} else {
				String destFileName = fileName;

				FileService fileDest = new FileService(
						mMyApp.getApplicationContext());
				int nSizeDest = fileDest.getFileSize(destFileName);
				byte[] dataDest = new byte[nSizeDest + 1];

				int ret = fileDest.readFile(destFileName, dataDest);
				if (ret == -1) {
					// 如果读到的为 -1 说明文件不存在
					bNeedUpdate = true;
				}

				int offset = 0;
				// utf8 head
				offset += 3;
				// version
				int nVersion = 0;
				int nDestMarket = 0;
				int nDestDate = 0;
				int nDestTime = 0;
				nVersion = MyByteBuffer.getInt(dataDest, offset);
				offset += 4;
				if (nVersion == CDataCodeTable.CodeTable_Version) {

					nDestMarket = MyByteBuffer.getInt(dataDest, offset);
					offset += 4;

					nDestDate = MyByteBuffer.getInt(dataDest, offset);
					offset += 4;

					nDestTime = MyByteBuffer.getInt(dataDest, offset);
					offset += 4;
				}

				int nSizeDefault = input.available();
				byte[] dataDefault = new byte[nSizeDefault + 1];
				input.read(dataDefault);
				offset = 0;
				// utf8 head
				offset += 3;
				// version
				nVersion = 0;
				int nDefaultMarket = 0;
				int nDefaultDate = 0;
				int nDefaultTime = 0;
				nVersion = MyByteBuffer.getInt(dataDefault, offset);
				offset += 4;
				if (nVersion == CDataCodeTable.CodeTable_Version) {

					nDefaultMarket = MyByteBuffer.getInt(dataDefault, offset);
					offset += 4;

					nDefaultDate = MyByteBuffer.getInt(dataDefault, offset);
					offset += 4;

					nDefaultTime = MyByteBuffer.getInt(dataDefault, offset);
					offset += 4;
				}

				// 若original=1表示本文件是本地最初的配置文件，本地可能有新版本，需要覆盖
				if (nDestMarket == nDefaultMarket && nDefaultDate > nDestDate) {
					L.d("Caution:Replace CodeTable %s by default resource",
							folder + File.separator + fileName);
					bNeedUpdate = true;
				}
			}
			if (bNeedUpdate) {
				file.copyFile(input, fileName);
			}
			input.close();

		} catch (IOException e) {
			e.printStackTrace();
			L.e(TAG, "initLocalCodeTableFile IOException");
			L.i(TAG, "end initLocalCodeTableFile");
			return;
		}
		L.i(TAG, "end initLocalCodeTableFile");
	}

	/**
	 * 初始化码表，读取码表文件
	 */
	public void initCodeTable() {
		L.i(TAG, "start initCodeTable");
		mMyApp.mCodeTableMarketNum = 0;

		// 0和1个市场是期权市场
		String qqMarket = "";
		qqMarket = mMyApp.mMainIniFile.ReadString("hq", "codeTableMarket", "");
		String temp = "";
		for (int i = 0; i < CDataCodeTable.MAX_SAVE_MARKET_COUNT; i++) {
			temp = STD.GetValue(qqMarket, i + 1, '|');
			if (temp.isEmpty()) {
				break;
			}
			int nMarket = STD.StringToInt(temp);
			if (nMarket > 0) {
				mMyApp.mCodeTableMarket[mMyApp.mCodeTableMarketNum] = nMarket;
				if (mMyApp.mCodeTable[mMyApp.mCodeTableMarketNum] == null) {
					mMyApp.mCodeTable[mMyApp.mCodeTableMarketNum] = new CDataCodeTable();
				}
				{
					mMyApp.mCodeTable[mMyApp.mCodeTableMarketNum]
							.clearCodeTable();
					mMyApp.mCodeTable[mMyApp.mCodeTableMarketNum]
							.setCodeTableMarket(nMarket);
				}
				mMyApp.mCodeTable[mMyApp.mCodeTableMarketNum]
						.readFromFile(true);
				mMyApp.mCodeTableMarketNum++;
			}
		}
		/**
		 * 市场ID，见MbUserMarket.xml文件中pbmarket/Rules/Rule中的MarketID字段
		 */
		String strMarket = "";
		int nExistNum = mMyApp.mCodeTableMarketNum;
		for (int index = 0; index < mMyApp.mPBMarketArray.size(); index++) {
			ArrayList<Rule> ruleArray = mMyApp.mPBMarketArray.get(index).mRules;
			if (ruleArray == null) {
				continue;
			}

			for (int j = 0; j < ruleArray.size()
					&& j < (CDataCodeTable.MAX_SAVE_MARKET_COUNT - nExistNum); j++) {
				strMarket = ruleArray.get(j).mMarketId;
				if (strMarket.isEmpty()) {
					continue;
				}

				int nMarket = STD.StringToInt(strMarket);
				if (nMarket <= 0) {
					continue;
				}

				for (int k = 0; k < mMyApp.mCodeTableMarketNum; k++) {
					if (mMyApp.mCodeTableMarket[k] == nMarket) {
						nMarket = 0;
						break;
					}
				}
				if (nMarket > 0) {
					// 初始化本地已有的码表
					String strFileName = String.format("%s%d.cfg",
							CDataCodeTable.CodeTable_FileName, nMarket);
					// initLocalCodeTableFile(CDataCodeTable.CodeTable_Folder,
					// strFileName);

					mMyApp.mCodeTableMarket[mMyApp.mCodeTableMarketNum] = nMarket;
					if (mMyApp.mCodeTable[mMyApp.mCodeTableMarketNum] == null) {
						mMyApp.mCodeTable[mMyApp.mCodeTableMarketNum] = new CDataCodeTable();
					}
					{
						mMyApp.mCodeTable[mMyApp.mCodeTableMarketNum]
								.clearCodeTable();
						mMyApp.mCodeTable[mMyApp.mCodeTableMarketNum]
								.setCodeTableMarket(nMarket);
					}
					mMyApp.mCodeTable[mMyApp.mCodeTableMarketNum]
							.readFromFile(false);
					mMyApp.mCodeTableMarketNum++;
				}
			}
		}
		L.i(TAG, "end initCodeTable");
	}

	private void downloadListFile() {
		GlobalNetProgress.Request_DownloadListFile(mMyApp.mCertifyNet);
	}

	/**
	 * 登录
	 */
	private void doLogin() {

		// 读取账号密码
		if (mMyApp.mUser.isEmpty() || mMyApp.mPassWord.isEmpty()) {
			mMyApp.mUser = "hdcf-pb";
			mMyApp.mPassWord = "123456";
		}

		mMyApp.setCertifyNetHandler(mHandler);
		mRequestCode[0] = GlobalNetProgress.HQRequest_Login(mMyApp.mCertifyNet,
				mMyApp.mVersionCode, mMyApp.mUser, mMyApp.mPassWord, 0,
				mMyApp.mJGId, false);

	}

	/**
	 * 推送登录
	 */
	private void doPushLogin() {

		mMyApp.setHQPushNetHandler(null);
		GlobalNetProgress.HQRequest_Login(mMyApp.mHQPushNet,
				mMyApp.mVersionCode, mMyApp.mUser, mMyApp.mPassWord,
				mMyApp.mLoginIdentID, mMyApp.mJGId, true);
	}

	/**
	 * 登录成功直接进入主界面
	 * 
	 * @param errMsg
	 */
	private void showErrorDialog(String errMsg) {

		AlertDialog alertDialog = new AlertDialog.Builder(mContext)
				.setTitle("提示")
				.setMessage(errMsg)
				.setCancelable(true)
				.setPositiveButton("重新登录",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								doLogin();
							}
						})
				.setNegativeButton("退出", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 退出
						AppActivityManager.getAppManager().AppExit(false);
					}
				}).create();
		alertDialog.show();
	}

	/**
	 * 登录成功直接进入主界面
	 */
	private void showMainPage() {
		mMyApp.getSearchCodeArray();
		SplashActivity.this.startActivity(new Intent(SplashActivity.this,
				MainActivity.class));
		mMyApp.setCertifyNetHandler(null);
		SplashActivity.this.finish();
	}

	private void queryCodeTable() {
		L.i(TAG, "*******Start queryCodeTable num= "
				+ mMyApp.mCodeTableMarketNum);

		mCodeTableRequest.clear();
		// 获取码表
		for (int i = 0; i < mMyApp.mCodeTableMarketNum
				&& i < CDataCodeTable.MAX_SAVE_MARKET_COUNT; i++) {

			int nMarket = mMyApp.mCodeTableMarket[i];
			L.i(TAG, "Start queryCodeTable market" + nMarket + "index:" + i);

			int nCount = 0;
			int date = 0;
			int time = 0;
			int nTableCRC = 0;

			for (int j = 0; j < mMyApp.mCodeTableMarketNum
					&& j < CDataCodeTable.MAX_SAVE_MARKET_COUNT; j++) {
				if (nMarket == mMyApp.mCodeTable[j].mMarketId) {
					nCount = (int) mMyApp.mCodeTable[j].getNum(nMarket);
					nTableCRC = mMyApp.mCodeTable[j].getCRC(nMarket);
					date = mMyApp.mCodeTable[j].mSaveDate;
					time = mMyApp.mCodeTable[j].mSaveTime;
					break;
				}
			}

			int requestCode = GlobalNetProgress
					.HQRequest_CodeTable(mMyApp.mCertifyNet, date, time,
							(short) nMarket, (short) 0, (short) 0,
							(short) nCount, (short) nTableCRC, (short) 1/*
																		 * 0-全量，缺省1
																		 * -增量
																		 */);
			mCodeTableRequest.add(Integer.valueOf(requestCode));
			L.i(TAG, "End queryCodeTable market" + nMarket + "index:" + i);
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		L.i(TAG, "*******End queryCodeTable*******");
	}

	/**
	 * 获取码表-上海
	 * 
	 * @param market
	 * @param index
	 */
	private void queryCodeTable(int market, int index) {
		L.i(TAG, "Start queryCodeTable market" + market + "index:" + index);
		int nCount = 0;

		int date = 0;
		int time = 0;
		int nTableCRC = 0;
		for (int i = 0; i < mMyApp.mCodeTableMarketNum
				&& i < CDataCodeTable.MAX_SAVE_MARKET_COUNT; i++) {
			if (market == mMyApp.mCodeTable[i].mMarketId) {
				nCount = (int) mMyApp.mCodeTable[i].getNum(market);
				nTableCRC = mMyApp.mCodeTable[i].getCRC(market);
				date = mMyApp.mCodeTable[i].mSaveDate;
				time = mMyApp.mCodeTable[i].mSaveTime;
				break;
			}
		}

		mRequestCode[2] = GlobalNetProgress.HQRequest_CodeTable(
				mMyApp.mCertifyNet, date, time, (short) market, (short) 0,
				(short) 0, (short) nCount, (short) nTableCRC, (short) 1/*
																		 * 0-全量，缺省1
																		 * -增量
																		 */);
		L.i(TAG, "End queryCodeTable market" + market + "index:" + index);
	}

	private boolean parseCodeTableData(byte[] szData, int nSize, int nMarket) {
		L.i(TAG, "Start parseCodeTableData" + nMarket);
		boolean bSuccess = true;
		if (nSize <= 0) {
			L.e(TAG, "Start parseCodeTableData no data recieved");
			return bSuccess;
		}

		int offset = 0;
		int wPackSize = MyByteBuffer.getInt(szData, offset);
		offset += 4;
		if (wPackSize > nSize - offset || nSize == 0) {
			L.e(TAG, "ERROR parseCodeTableData data error");
			return bSuccess;
		}

		CPbDataPackage pack = new CPbDataPackage();
		CPbDataDecode.DecodeOnePackage(szData, offset, wPackSize, pack);
		L.i("parseCodeTableData", "Finish DecodeOnePackage size=" + wPackSize);

		int nDate = pack.GetNormalFieldByID(21).GetInt32();
		int nTime = pack.GetNormalFieldByID(22).GetInt32();
		int nMarketId = pack.GetNormalFieldByID(11).GetInt16();
		int nTableCRC = pack.GetNormalFieldByID(100).GetInt16();
		int nUpdateFlag = pack.GetNormalFieldByID(101).GetInt8();

		if (nMarketId != nMarket) {
			L.e(TAG, "ERROR: return market=" + nMarketId + ",request market="
					+ mMyApp.mCodeTableMarket);
			return bSuccess;
		}

		// 获取对应market的codetable索引
		int codeTable_Index = -1;// codetable 索引
		for (int index = 0; index < mMyApp.mCodeTableMarketNum
				&& index < CDataCodeTable.MAX_SAVE_MARKET_COUNT; index++) {
			if (nMarketId == mMyApp.mCodeTable[index].mMarketId) {
				codeTable_Index = index;
				break;
			}
		}
		if (codeTable_Index < 0
				&& codeTable_Index >= mMyApp.mCodeTableMarketNum) {
			L.e(TAG, "ERROR: return codeTable_Index=" + codeTable_Index);
			return bSuccess;
		}

		if (nUpdateFlag == 0) {// 全量下载
			mMyApp.mCodeTable[codeTable_Index].clearCodeTable();
		}

		L.i("parseCodeTableData", "Finish get codetable index="
				+ codeTable_Index);

		int nRecordCount = 0;
		for (int i = 0; i < pack.m_nItemSize; ++i) {
			CPbDataItem item = pack.m_DataItems.get(i);
			if (item.m_ItemType == CPbDataItem.DIT_NORMAL) {
				if (item.m_NormalField.IsValid()) {
					// String strSection = item.m_NormalField.m_szFieldName;
					switch (item.m_NormalField.m_FieldID) {
					case 11:
						// nMarketId = item.m_NormalField.GetInt16();
						// L.e(TAG, strSection + "=" +
						// item.m_NormalField.GetInt16());
						break;
					case 21:
						nDate = item.m_NormalField.GetInt32();
						// L.e(TAG, strSection + "=" +
						// item.m_NormalField.GetInt32());
						break;
					case 22:
						nTime = item.m_NormalField.GetInt32();
						// L.e(TAG, strSection + "=" +
						// item.m_NormalField.GetInt32());
						break;
					default:
						// L.e(TAG, "App unused field:" +
						// item.m_NormalField.m_FieldID + "->" + strSection);
						break;
					}
				}
			} else {
				for (int m = 0; m < item.nArraySize; ++m) {
					TagLocalStockData aRecord = new TagLocalStockData();
					CCodeTableItem codeTableRecord = new CCodeTableItem();

					aRecord.market = (short) nMarketId;
					aRecord.HQData.market = (short) nMarketId;
					codeTableRecord.market = (short) nMarketId;

					for (int n = 0; n < item.nSubFields; ++n) {
						int nIndex = n + m * item.nSubFields;
						CPbDataField field = item.m_ArrayValue.get(nIndex);

						if (field.IsValid()) {
							// String strSection = field.m_szFieldName;
							switch (field.m_FieldID) {
							case 10:
								aRecord.code = field.GetString();
								aRecord.HQData.code = aRecord.code;
								codeTableRecord.code = aRecord.code;
								// L.e(TAG, strSection + "=" + aRecord.code);
								break;
							case 2:
								aRecord.extCode = field.GetString();
								codeTableRecord.extCode = aRecord.extCode;
								// L.e(TAG, strSection + "=" +
								// aRecord.MarketCode);
								break;
							case 3:
								aRecord.name = field.GetString();
								codeTableRecord.name = aRecord.name;
								// L.e(TAG, strSection + "=" + aRecord.name);
								break;
							case 17:
								aRecord.GroupOffset = (short) field.GetInt8();
								aRecord.group = aRecord.GroupOffset;
								codeTableRecord.GroupOffset = aRecord.GroupOffset;
								codeTableRecord.group = aRecord.GroupOffset;
								// L.e(TAG, strSection + "=" + field.GetInt8());
								break;
							case 14:
								aRecord.PriceDecimal = (short) field.GetInt8();
								codeTableRecord.PriceDecimal = aRecord.PriceDecimal;
								// L.e(TAG, strSection + "=" + field.GetInt8());
								break;
							case 15:
								aRecord.VolUnit = (short) field.GetInt16();
								codeTableRecord.VolUnit = aRecord.VolUnit;
								// L.e(TAG, strSection + "=" +
								// field.GetInt16());
								break;
							case 16:
								aRecord.PriceRate = field.GetInt32();
								codeTableRecord.PriceRate = aRecord.PriceRate;
								// L.e(TAG, strSection + "=" +
								// field.GetInt32());
								break;
							case 18:
								codeTableRecord.ContractCRC = field.GetInt8();
								// L.e(TAG, strSection + "=" + field.GetInt8());
								break;
							case 19:
								aRecord.Multiplier = field.GetInt32();
								codeTableRecord.Multiplier = aRecord.Multiplier;
								// L.e(TAG, strSection + "=" +
								// field.GetInt16());
								break;
							case 102:
								codeTableRecord.ContractUpdate = field
										.GetInt8();
								// L.e(TAG, strSection + "=" + field.GetInt8());
								break;
							default:
								// L.e(TAG, "App unused field:" +
								// field.m_FieldID + "->" + strSection);
								break;
							}
						}
					}
					// 商品代码状态 0-更新，1-新增，2-删除
					if (codeTableRecord.ContractUpdate == 1) {
						// L.i("parseCodeTableData",
						// "Start searchMarketGroupInfo");
						mktGroupInfo groupInfo = mMyApp.mMarketInfo
								.searchMarketGroupInfo(codeTableRecord.market,
										codeTableRecord.code,
										codeTableRecord.GroupOffset);
						if (groupInfo != null) {
							aRecord.groupCode = groupInfo.Code;
							codeTableRecord.groupCode = groupInfo.Code;
							aRecord.GroupFlag = groupInfo.Flag;
							codeTableRecord.GroupFlag = groupInfo.Flag;
						}
						// L.i("parseCodeTableData",
						// "End searchMarketGroupInfo");

						// L.i("parseCodeTableData",
						// "Start add item to codetable");
						if (codeTable_Index >= 0
								&& codeTable_Index < mMyApp.mCodeTableMarketNum
								&& codeTable_Index < CDataCodeTable.MAX_SAVE_MARKET_COUNT) {
							mMyApp.mCodeTable[codeTable_Index]
									.add(codeTableRecord);
						}
						// L.i("parseCodeTableData",
						// "End add item to codetable");
					} else if (codeTableRecord.ContractUpdate == 2) {
						// L.i("parseCodeTableData",
						// "remove item(%d-%d-%s) from codetable"+nRecordCount+nMarketId+codeTableRecord.code);
						if (codeTable_Index >= 0
								&& codeTable_Index < mMyApp.mCodeTableMarketNum
								&& codeTable_Index < CDataCodeTable.MAX_SAVE_MARKET_COUNT) {
							mMyApp.mCodeTable[codeTable_Index]
									.remove(codeTableRecord);
						}
					} else if (codeTableRecord.ContractUpdate == 0) {
						// L.i("parseCodeTableData",
						// "Start searchMarketGroupInfo");
						mktGroupInfo groupInfo = mMyApp.mMarketInfo
								.searchMarketGroupInfo(codeTableRecord.market,
										codeTableRecord.code,
										codeTableRecord.GroupOffset);
						if (groupInfo != null) {
							aRecord.groupCode = groupInfo.Code;
							codeTableRecord.groupCode = groupInfo.Code;
							aRecord.GroupFlag = groupInfo.Flag;
							codeTableRecord.GroupFlag = groupInfo.Flag;
						}
						// L.i("parseCodeTableData",
						// "End searchMarketGroupInfo");

						// L.i("parseCodeTableData",
						// "modify item(%d-%d-%s) from codetable"+nRecordCount+nMarketId+codeTableRecord.code);
						if (codeTable_Index >= 0
								&& codeTable_Index < mMyApp.mCodeTableMarketNum
								&& codeTable_Index < CDataCodeTable.MAX_SAVE_MARKET_COUNT) {
							mMyApp.mCodeTable[codeTable_Index]
									.modify(codeTableRecord);
						}
					}

					nRecordCount++;
					// 期权市场，需要加入合约列表
					if (nMarket == mMyApp.mCodeTableMarket[0]
							|| nMarket == mMyApp.mCodeTableMarket[1]) {
						mMyApp.mHQData.updateData(aRecord, true);
					}
					aRecord = null;
				}
			}
		}// pack.m_nItemSize
		if (nUpdateFlag == 1) {// 增量下载
			int nLocalCRC = mMyApp.mCodeTable[codeTable_Index]
					.getCRC_Calc(nMarket);
			if (nTableCRC != nLocalCRC) {
				bSuccess = false;
				L.e(TAG, "ERROR: parseCodeTableData[%d] nTableCRC != nLocalCRC"
						+ nMarketId);
			}
		}

		L.i("parseCodeTableData", "Start write to file");
		if (bSuccess == true && codeTable_Index >= 0
				&& codeTable_Index < mMyApp.mCodeTableMarketNum
				&& codeTable_Index < CDataCodeTable.MAX_SAVE_MARKET_COUNT) {
			mMyApp.mCodeTable[codeTable_Index].setLocalDateTime(nDate, nTime,
					nMarketId);
			mMyApp.mCodeTable[codeTable_Index].setCRC(nMarketId, nTableCRC);
			mMyApp.mCodeTable[codeTable_Index].writeToFile();
		}

		L.i(TAG, "End parseCodeTableData,nRecordCount=" + nRecordCount);
		return bSuccess;
	}

	/**
	 * 获取市场信息
	 */
	private void queryMarketInfo() {

		mRequestCode[1] = GlobalNetProgress.HQRequest_MarketInfo(
				mMyApp.mCertifyNet, mMyApp.mMarketInfo.mDate,
				mMyApp.mMarketInfo.mTime, CMarketInfo.MarketInfo_Version);
	}

	private void parseMarketInfoData(byte[] szData, int nSize) {

		// //------------协议版本为1时的数据格式：
		// /*---Reply-------------------(第1个包)-----------------------------------------------------------------------
		// |MOB_MAIN_FRMHEAD|ST_SYSFILE_HEAD|MBST_MARKET_LIST1|MBST_MKT_BASIC_INFO1|N1个MBST_MKT_GROUP_INFO1|
		// ...|MBST_MKT_BASIC_INFO1|N2个MBST_MKT_GROUP_INFO1|
		// N1、N2 为前一个MBST_MKT_BASIC_INFO1 中的GroupNumber的值
		// --Reply-------------------(第2个包
		// 及以后的包)---------------------------------------------
		// |MOB_MAIN_FRMHEAD|MBST_MKT_BASIC_INFO1| N个MBST_MKT_GROUP_INFO1|---*/
		//
		// typedef struct
		// {
		// WORD MarketCount; //可见市场数量
		// } MBST_MARKET_LIST1;
		//
		// typedef struct
		// {
		// unsigned char Flag; //指数或一般品种
		// unsigned short Name[IM_MAX_GROUPNAME2]; //类别名称
		// unsigned char Code[IM_MAX_GROUPCODE];
		// unsigned char TradeFields; //交易区段
		// unsigned short Start[IM_MAX_TRADE_FIELDS]; //区段起始 hhss
		// unsigned short End[IM_MAX_TRADE_FIELDS]; //区段结束 hhss
		// unsigned char FlagAskBid; //买卖盘的档数 0x01 1档，0x03 3档, 0x05 5档 ,0x10 档
		// }MBST_MKT_GROUP_INFO1; //70
		//
		// //为方便处理，定义时特意使MBST_MKT_GROUP_INFO1的字节数和MBST_MKT_BASIC_INFO1
		// 字节数一致,都为70个字节
		// typedef struct
		// {
		// unsigned short MarketId; //交易所ID
		// unsigned char MarketAttr; //交易所属性
		// unsigned short Name[IM_MAX_MKNAME]; //交易所简称
		// unsigned char Code[IM_MAX_MKCODE]; //交易所代码
		// short TimeZone; //时区
		// unsigned short MaxNumber; //最大允许商品数
		// unsigned short GroupNumber; //分类数
		// unsigned short StartTime, EndTime; //开收盘时间 0930
		// BYTE Resv[6]; //保留
		// }MBST_MKT_BASIC_INFO1; //64+6字节

		L.i(TAG, "******Start parseMarketInfoData*******");
		if (nSize <= 0) {
			return;
		}
		int offset = 0;
		offset = 8;
		int nDate = MyByteBuffer.getInt(szData, offset);
		offset += 4;
		int nTime = MyByteBuffer.getInt(szData, offset);
		offset += 4;
		short sMarketCount = MyByteBuffer.getShort(szData, offset);
		offset += 2;

		mMyApp.mMarketInfo.clear();
		for (int i = 0; i < sMarketCount; i++) {
			CMarketInfoItem aMarketInfoItem = new CMarketInfoItem();

			aMarketInfoItem.MarketId = MyByteBuffer.getShort(szData, offset);
			offset += 2;
			aMarketInfoItem.MarketAttr = MyByteBuffer.getByte(szData, offset);
			offset += 1;
			aMarketInfoItem.Name = STD.getStringFromUnicodeBytes(szData,
					offset, 42);
			offset += 42;
			aMarketInfoItem.Code = STD.getStringFromBytes(szData, offset, 9);
			offset += 9;
			aMarketInfoItem.TimeZone = MyByteBuffer.getShort(szData, offset);
			offset += 2;
			aMarketInfoItem.MaxNumber = MyByteBuffer.getShort(szData, offset);
			offset += 2;
			aMarketInfoItem.GroupNumber = MyByteBuffer.getShort(szData, offset);
			offset += 2;
			aMarketInfoItem.StartTime = MyByteBuffer.getShort(szData, offset);
			offset += 2;
			aMarketInfoItem.EndTime = MyByteBuffer.getShort(szData, offset);
			offset += 2;

			offset += 6;

			for (int m = 0; m < aMarketInfoItem.GroupNumber; m++) {
				if (m < aMarketInfoItem.GroupNumber) {
					mktGroupInfo aGroupItem = new mktGroupInfo();

					aGroupItem.Flag = MyByteBuffer.getByte(szData, offset);
					offset += 1;
					aGroupItem.Name = STD.getStringFromUnicodeBytes(szData,
							offset, 42);
					offset += 42;
					aGroupItem.Code = STD.getStringFromBytes(szData, offset, 9);
					offset += 9;
					aGroupItem.TradeFields = MyByteBuffer.getByte(szData,
							offset);
					offset += 1;
					aGroupItem.Start[0] = MyByteBuffer.getShort(szData, offset);
					offset += 2;
					aGroupItem.Start[1] = MyByteBuffer.getShort(szData, offset);
					offset += 2;
					aGroupItem.Start[2] = MyByteBuffer.getShort(szData, offset);
					offset += 2;
					aGroupItem.Start[3] = MyByteBuffer.getShort(szData, offset);
					offset += 2;
					aGroupItem.End[0] = MyByteBuffer.getShort(szData, offset);
					offset += 2;
					aGroupItem.End[1] = MyByteBuffer.getShort(szData, offset);
					offset += 2;
					aGroupItem.End[2] = MyByteBuffer.getShort(szData, offset);
					offset += 2;
					aGroupItem.End[3] = MyByteBuffer.getShort(szData, offset);
					offset += 2;
					aGroupItem.FlagAskBid = MyByteBuffer
							.getByte(szData, offset);
					offset += 1;

					aMarketInfoItem.addGroup(aGroupItem);
				} else {
					// offset += 61;
				}
			}
			mMyApp.mMarketInfo.addItem(aMarketInfoItem);
		}
		mMyApp.mMarketInfo.setDateTime(nDate, nTime);
		mMyApp.mMarketInfo.writeToFile();
		L.i(TAG, "******End parseMarketInfoData******");
	}

	/**
	 * 获取全局参数信息
	 */
	private void queryGlobalParamInfo() {

		mRequestCode[3] = GlobalNetProgress
				.HQRequest_GlobalParamInfo(mMyApp.mCertifyNet);
	}

	private void parseGlobalParamInfoData(byte[] szData, int nSize) {
		L.e(TAG, "Start parseGlobalParamInfoData");
		int offset = 0;
		while (offset >= 0 && nSize - offset >= 4) {
			int wPackSize = MyByteBuffer.getInt(szData, offset);
			offset += 4;
			if (wPackSize > nSize - offset)
				break;

			CPbDataPackage pack = new CPbDataPackage();
			wPackSize = CPbDataDecode.DecodeOnePackage(szData, offset,
					wPackSize, pack);

			offset += wPackSize;
			if (pack.m_wPackageID == 999) {
				mMyApp.mGlobalParam.m_dRateWithoutRish = pack
						.GetNormalFieldByID(1).GetFloat() / 100;
				ViewTools.m_dRateWithoutRisk = mMyApp.mGlobalParam.m_dRateWithoutRish;
				L.e(TAG, "parseGlobalParamInfoData m_dRateWithoutRish="
						+ mMyApp.mGlobalParam.m_dRateWithoutRish);
			}
		}
		L.e(TAG, "End parseGlobalParamInfoData");
	}

	private void updateHQMarketInfo() {
		// 更新市场信息
		if (mMyApp.mHQData.getNum() <= 0) {
			return;
		}
		if (mMyApp.mMarketInfo.getNum() <= 0) {
			return;
		}
		L.e(TAG, "Start updateHQMarketInfo");
		TagLocalStockData aStockData;
		mktGroupInfo groupRecord;
		for (int m = 0; m < mMyApp.mHQData.getNum(); m++) {
			aStockData = mMyApp.mHQData.getItem(m);
			groupRecord = mMyApp.mMarketInfo.searchMarketGroupInfo(
					aStockData.HQData.market, null,
					(short) aStockData.GroupOffset);
			if (groupRecord != null) {
				aStockData.TradeFields = groupRecord.TradeFields;
				STD.memcpy(aStockData.Start, groupRecord.Start, 4);
				STD.memcpy(aStockData.End, groupRecord.End, 4);
				aStockData.GroupFlag = groupRecord.Flag;
			} else {
				L.e(TAG, "ERROR: MarketInfo.search failed [" + m
						+ "],marketId=" + aStockData.HQData.market + ",code="
						+ aStockData.HQData.code);
			}
		}
		aStockData = null;
		groupRecord = null;
		L.e(TAG, "End updateHQMarketInfo");
	}

	private void querySysConfigFile() {
		// 配置文件更新
		// if (g_globalData.m_bZLHYDownload == false)
		{
			int nSize = PreferenceEngine.getInstance().getLengthZLHY();
			int nDate = PreferenceEngine.getInstance().getDateZLHY();
			int nTime = PreferenceEngine.getInstance().getTimeZLHY();
			mRequestCode[4] = GlobalNetProgress.HQRequest_SYSCONFIG(
					mMyApp.mCertifyNet, MyApp.FILE_CONFIG_ZLHY_REQ, nSize,
					nDate, nTime, -1);
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			nSize = PreferenceEngine.getInstance().getLengthSHUILV();
			nDate = PreferenceEngine.getInstance().getDateSHUILV();
			nTime = PreferenceEngine.getInstance().getTimeSHUILV();
			mRequestCode[5] = GlobalNetProgress.HQRequest_SYSCONFIG(
					mMyApp.mCertifyNet, MyApp.SHUILV_CONFIGFILE, nSize, nDate,
					nTime, mMyApp.mJGId);
		}
	}

	private void parseSysConfigData(byte[] szData, int nSize, String fileName) {
		// 现货返回(请求系统配置文件)
		// ----Reply-----------------------------
		// |MOB_MAIN_FRMHEAD|CFH_MBSYSFILE_APPLY|filebody1|
		// |MOB_MAIN_FRMHEAD|filebody2|

		L.d(TAG, "/*Start parseSysConfigData*/");

		if (nSize <= 0) {
			if (fileName != null && fileName.equals(MyApp.SHUILV_CONFIGFILE)) {
				if (mMyApp.mSHUILVIni == null) {
					mMyApp.mSHUILVIni = new MIniFile();
					mMyApp.mSHUILVIni.setFilePath(getApplicationContext(),
							MyApp.SHUILV_CONFIGFILE);
				}
			}
			return;
		}
		// typedef struct
		// {
		// char FileName[32]; //hldy.ini, hldyex.ini....
		// DWORD FileLen;
		// DWORD FileDate; //yyyymmdd
		// DWORD FileTime; //hhmmss
		// DWORD FilePos; //客户端待更新内容的文件起始位置(即客户端已有新文件的长度)
		// DWORD MaxBodyLen; //一次应答最多发送的文件体长度，缺省值为32K
		// DWORD uCompanyID; //机构编号
		// BYTE Resv[4]; //保留
		// }CFH_MBSYSFILE_APPLY; 32+4*6+4

		int offset = 0;
		String name = STD.getStringFromUnicodeBytes(szData, offset, 32);
		offset += 32;
		int nFileLen = MyByteBuffer.getInt(szData, offset);
		offset += 4;
		int nDate = MyByteBuffer.getInt(szData, offset);
		offset += 4;
		int nTime = MyByteBuffer.getInt(szData, offset);
		offset += 4;

		offset = 60;
		nSize -= offset;
		if (nSize > 0)// 后面是文件内容，直接保存
		{
			// 保存下载文件的日期、时间、内容长度
			if (fileName != null && fileName.equals(MyApp.SHUILV_CONFIGFILE)) {
				PreferenceEngine.getInstance().saveLengthSHUILV(nFileLen);
				PreferenceEngine.getInstance().saveDateSHUILV(nDate);
				PreferenceEngine.getInstance().saveTimeSHUILV(nTime);
			} else if (fileName != null
					&& fileName.equals(MyApp.FILE_CONFIG_ZLHY)) {
				PreferenceEngine.getInstance().saveLengthZLHY(nFileLen);
				PreferenceEngine.getInstance().saveDateZLHY(nDate);
				PreferenceEngine.getInstance().saveTimeZLHY(nTime);
			}
			// unicode to utf8
			byte[] szData_utf8 = new byte[nSize];
			System.arraycopy(szData, offset, szData_utf8, 0, nSize);

			try {
				String utf8 = STD.unicodeToUtf8(szData_utf8, nSize);
				szData_utf8 = utf8.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			FileService file = new FileService(this.getApplicationContext());
			try {
				//
				if (fileName == null || fileName.isEmpty()) {
					fileName = MyApp.FILE_CONFIG_ZLHY;
				}
				file.saveToFile(fileName, szData_utf8, 0, nSize);
				// 保存成功
				L.d(TAG, "Save SysConfigData Success!");
				szData = null;
			} catch (Exception e) {
				// 保存失败
				L.e(TAG, "Save SysConfigData Error!");
			}
		}
		if (fileName != null && fileName.equals(MyApp.SHUILV_CONFIGFILE)) {
			if (mMyApp.mSHUILVIni == null) {
				mMyApp.mSHUILVIni = new MIniFile();
				mMyApp.mSHUILVIni.setFilePath(getApplicationContext(),
						MyApp.SHUILV_CONFIGFILE);
			}
		}
		L.d(TAG, "/*End parseSysConfigData*/");
	}

	/**
	 * 获取期权列表信息
	 */
	private void queryOptionInfo() {

		mRequestCode[6] = GlobalNetProgress.HQRequest_OptionInfo(
				mMyApp.mCertifyNet, mMyApp.mCodeTableMarket[0],
				mMyApp.mCodeTableMarket[1]);
	}

	private void parseOptionInfoData(byte[] szData, int nSize) {
		L.e(TAG, "Start parseOptionInfoData");
		int nCount = 0;
		int offset = 0;
		while (nSize - offset >= 4) {
			int wPackSize = MyByteBuffer.getInt(szData, offset);
			if (wPackSize < 0) {
				L.e(TAG, "ERROR: parseOptionInfoData wPackSize = " + wPackSize);
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
			if (pack.m_wPackageID == 5) {

				TagLocalOptionRecord aRecord = new TagLocalOptionRecord();
				aRecord.code = pack.GetNormalFieldByID(10).GetString();
				aRecord.market = (short) pack.GetNormalFieldByID(11).GetInt16();

				// 获取期货标的代码和市场
				aRecord.TargetSymbol = pack.GetNormalFieldByID(3001)
						.GetString();

				aRecord.StockCode = STD.GetValue(aRecord.TargetSymbol, 1, '.');
				aRecord.StockMarketCode = STD.GetValue(aRecord.TargetSymbol, 2,
						'.');
				if (aRecord.StockMarketCode.length() > 0) {
					aRecord.StockMarket = mMyApp.mMarketInfo
							.getMarketId(aRecord.StockMarketCode);
				} else {
					aRecord.StockMarket = aRecord.market;// 如果aRecord.StockMarketCode不存在，则标的和期权默认是同一市场
				}

				aRecord.OptionAttr = (byte) pack.GetNormalFieldByID(3002)
						.GetInt8();
				aRecord.OptionType = (byte) pack.GetNormalFieldByID(3003)
						.GetInt8();
				aRecord.StrikePrice = pack.GetNormalFieldByID(3004).GetFloat();
				aRecord.StrikeDate = pack.GetNormalFieldByID(3005).GetInt32();
				aRecord.StrikeDateNoDay = aRecord.StrikeDate / 100; // 行权到期日,只有年月
				aRecord.StrikeUnit = pack.GetNormalFieldByID(3006).GetInt32();
				aRecord.OptionCP = (byte) pack.GetNormalFieldByID(3007)
						.GetInt8();
				aRecord.OptionAdjust = (byte) pack.GetNormalFieldByID(3014)
						.GetInt8();
				aRecord.OptionLife = (byte) pack.GetNormalFieldByID(3015)
						.GetInt8();
				aRecord.OpenLimit = (byte) pack.GetNormalFieldByID(3016)
						.GetInt8();
				aRecord.dHistoryVolatility = pack.GetNormalFieldByID(6)
						.GetFloat();
				aRecord.Delta = pack.GetNormalFieldByID(3009).GetFloat();
				aRecord.Gamma = pack.GetNormalFieldByID(3010).GetFloat();
				aRecord.Rho = pack.GetNormalFieldByID(3011).GetFloat();
				aRecord.Theta = pack.GetNormalFieldByID(3012).GetFloat();
				aRecord.Vega = pack.GetNormalFieldByID(3013).GetFloat();

				L.e(TAG, "OptionData[" + nCount + "] marketId="
						+ aRecord.market + " Code=" + aRecord.code
						+ " TargetSymbol=" + aRecord.TargetSymbol);
				nCount++;
				if (mMyApp.mHQData.updateOptionData(aRecord) == false) {
					L.e(TAG, "ERROR: CDataHQ updateOptionData failed");
				}
				// 获取期货标的代码表
				if (false == mMyApp.mStockConfigData.search(null,
						(short) aRecord.StockMarket, aRecord.StockCode)) {
					mMyApp.mStockConfigData.add((short) aRecord.StockMarket,
							aRecord.StockCode, true);
					L.d(TAG, "mStockConfigData add marketId="
							+ aRecord.StockMarket + ",Code="
							+ aRecord.StockCode + ",TargetSymbol="
							+ aRecord.TargetSymbol);
				}

			}
		}
		L.e(TAG, "End parseOptionInfoData,nRecordCount=" + nCount);
	}

	/**
	 * 获取期权行情信息
	 */
	private void queryAllOptionHQInfo() {

		ArrayList<TagCodeInfo> codelist = new ArrayList<TagCodeInfo>();

		TagLocalStockData aStockData;
		for (int m = 0; m < mMyApp.mStockConfigData.getNum(); m++) {
			aStockData = mMyApp.mStockConfigData.getItem(m);
			codelist.add(new TagCodeInfo(aStockData.HQData.market,
					aStockData.HQData.code, (short) aStockData.GroupOffset));
		}
		for (int m = 0; m < mMyApp.mZhiShuData.getNum(); m++) {
			aStockData = mMyApp.mZhiShuData.getItem(m);
			codelist.add(new TagCodeInfo(aStockData.HQData.market,
					aStockData.HQData.code, (short) aStockData.GroupOffset));
		}

		mRequestCode[8] = GlobalNetProgress.HQRequest_MultiCodeInfo(
				mMyApp.mCertifyNet, codelist, 0, codelist.size(),
				mMyApp.mCodeTableMarket[0], mMyApp.mCodeTableMarket[1]);
	}

	/**
	 * 获取标的信息+添加指数
	 */
	private void queryStockInfo() {
		// 添加指数到标的列表
		for (int i = 0; i < mMyApp.mZhiShuCodeInfos.size(); i++) {
			if (false == mMyApp.mZhiShuData.search(null,
					mMyApp.mZhiShuCodeInfos.get(i).market,
					mMyApp.mZhiShuCodeInfos.get(i).code)) {
				mMyApp.mZhiShuData.add(mMyApp.mZhiShuCodeInfos.get(i).market,
						mMyApp.mZhiShuCodeInfos.get(i).code, true);
			}
		}

		ArrayList<TagCodeInfo> codelist = new ArrayList<TagCodeInfo>();

		TagLocalStockData aStockData;
		for (int m = 0; m < mMyApp.mStockConfigData.getNum(); m++) {
			aStockData = mMyApp.mStockConfigData.getItem(m);
			codelist.add(new TagCodeInfo(aStockData.HQData.market,
					aStockData.HQData.code, (short) aStockData.GroupOffset));
		}
		for (int n = 0; n < mMyApp.mZhiShuData.getNum(); n++) {
			aStockData = mMyApp.mZhiShuData.getItem(n);
			codelist.add(new TagCodeInfo(aStockData.HQData.market,
					aStockData.HQData.code, (short) aStockData.GroupOffset));
		}

		mRequestCode[7] = GlobalNetProgress.HQRequest_StockInfo(
				mMyApp.mCertifyNet, codelist, 0, codelist.size());
	}

	private void parseStockInfoData(byte[] szData, int nSize) {
		L.e(TAG, "Start parseStockInfoData");
		int nCount = 0;
		int offset = 0;
		while (offset >= 0 && nSize - offset >= 4) {
			int wPackSize = MyByteBuffer.getInt(szData, offset);
			if (wPackSize < 0) {
				L.e(TAG, "ERROR: parseStockInfoData wPackSize = " + wPackSize);
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

				nCount++;
				if (mMyApp.mZhiShuData.search(null, stockData.HQData.market,
						stockData.HQData.code) == true) {
					if (mMyApp.mZhiShuData.updateData(stockData, true) == false) {
						L.e(TAG,
								"ERROR: mMyApp.mStockConfigData. updateData failed");
					}
				} else {
					if (mMyApp.mStockConfigData.updateData(stockData, true) == false) {
						L.e(TAG,
								"ERROR: mMyApp.mStockConfigData. updateData failed");
					}
				}
			}
		}
		L.e(TAG, "End parseStockInfoData,nRecordCount=" + nCount);

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
				// L.e(TAG," GroupInfoData marketId="+ aStockData.HQData.market
				// + ",TradeFields="+ aStockData.TradeFields + ",Start[0]=" +
				// aStockData.Start[0] + ",End[0]=" + aStockData.End[0]);
			} else {
				L.e(TAG, "ERROR: MarketInfo.search failed ,marketId="
						+ aStockData.HQData.market + ",code="
						+ aStockData.HQData.code);
			}
		}
		aStockData = null;
		groupRecord = null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
