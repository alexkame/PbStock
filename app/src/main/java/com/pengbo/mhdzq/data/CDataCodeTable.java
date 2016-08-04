package com.pengbo.mhdzq.data;

import java.io.DataOutputStream;
import java.util.ArrayList;

import android.content.Context;

import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.net.MyByteBuffer;
import com.pengbo.mhdzq.tools.FileService;
import com.pengbo.mhdzq.tools.L;

/**
 * 根据用户的权限返回对应的市场信息
 * 
 * @author Administrator
 * 
 */
public class CDataCodeTable {
	private final static String TAG = "CDataCodeTable";
	public final static int MAX_SAVE_MARKET_COUNT = 100;
	public final static int MAX_CODETABLE_NUM = 10000;
	public final static String CodeTable_FileName = "pb_codetable_zq";
	public final static String CodeTable_Folder = "codetable";
	/**
	 * 如果码表数据结构有变化，此版本须更新
	 */
	public final static int CodeTable_Version = 3;

	public MyApp mMyApp;
	public boolean mDownSuccess;
	public int mMarketId;
	public int mSaveDate;
	public int mSaveTime;
	/**
	 * 码表校验和
	 */
	public int mTableCRC;

	public ArrayList<CCodeTableItem> mCodeTableList;

	public CDataCodeTable() {
		mDownSuccess = false;
		mSaveDate = 0;
		mSaveTime = 0;

		mMyApp = MyApp.getInstance();
		mCodeTableList = new ArrayList<CCodeTableItem>();
	}

	/**
	 * 与码表有关的接口
	 * 
	 * @param bNeedAddToHqList
	 * @return
	 */
	public long readFromFile(boolean bNeedAddToHqList) {
		L.d(TAG, "Start readFromFile" + this.mMarketId);
		mCodeTableList.clear();

		String fileName = String.format("%s%d.cfg", CodeTable_FileName,
				mMarketId);

		FileService file = new FileService(mMyApp.getApplicationContext());
		int size = file.getFileSize(fileName);
		byte[] data = new byte[size + 1];

		int ret = file.readFile(fileName, data);
		if (ret == -1) {
			// 如果读到的为 -1 说明文件不存在
			return 0;
		}

		int offset = 0;
		// utf8 head
		offset += 3;
		// version
		int nVersion = 0;
		nVersion = MyByteBuffer.getInt(data, offset);
		offset += 4;

		if (nVersion < CodeTable_Version) {
			return 0;
		}

		mMarketId = MyByteBuffer.getInt(data, offset);
		offset += 4;

		mSaveDate = MyByteBuffer.getInt(data, offset);
		offset += 4;

		mSaveTime = MyByteBuffer.getInt(data, offset);
		offset += 4;

		mTableCRC = MyByteBuffer.getInt(data, offset);
		offset += 4;

		long lCount = 0;
		lCount = MyByteBuffer.getLong(data, offset);
		offset += 8;

		CCodeTableItem item = new CCodeTableItem();

		L.d(TAG, "Start readFromFile Anlyse item" + lCount);
		for (int i = 0; i < lCount && i < MAX_CODETABLE_NUM; i++) {
			// public short market; // 市场，为本地类型
			// public String code; // 代码
			// public short group; // 类别，为本地类型
			// public String name; // 名称

			// 对应LOCAL_CODETABLE_RECORD数据
			// public short GroupOffset; //分类号
			// public short PriceDecimal; //显示价格小数位
			// public int PriceRate; //价格放大倍率
			// public short VolUnit; //每手股数
			// public int Multiplier; //合约乘数，计划均价是用到
			// public String groupCode;

			// 对应ST_MKT_GROUP_INFO_UNICODE数据
			// public String extCode; //交易所代码
			// public short ContractCRC; //商品信息校验和
			// public short ContractUpdate; //商品代码状态 0-更新，1-新增，2-删除
			// public short GroupFlag; //指数或一般品种
			item = new CCodeTableItem();
			int len;

			item.market = MyByteBuffer.getShort(data, offset);
			offset += 2;

			len = MyByteBuffer.getInt(data, offset);
			offset += 4;
			byte[] byte1 = new byte[len];
			MyByteBuffer.getBytes(data, offset, byte1, 0, len);
			item.code = new String(byte1);
			offset += len;

			item.group = MyByteBuffer.getShort(data, offset);
			offset += 2;

			len = MyByteBuffer.getInt(data, offset);
			offset += 4;
			byte[] byte2 = new byte[len];
			MyByteBuffer.getBytes(data, offset, byte2, 0, len);
			item.name = new String(byte2);
			// item.name = EncodingUtils.getString(byte2, "UTF-8");
			offset += len;

			item.GroupOffset = MyByteBuffer.getShort(data, offset);
			offset += 2;

			item.PriceDecimal = MyByteBuffer.getShort(data, offset);
			offset += 2;

			item.PriceRate = MyByteBuffer.getInt(data, offset);
			offset += 4;

			item.VolUnit = MyByteBuffer.getShort(data, offset);
			offset += 2;

			item.Multiplier = MyByteBuffer.getInt(data, offset);
			offset += 4;

			len = MyByteBuffer.getInt(data, offset);
			offset += 4;
			byte[] byte3 = new byte[len];
			MyByteBuffer.getBytes(data, offset, byte3, 0, len);
			item.groupCode = new String(byte3);
			offset += len;

			len = MyByteBuffer.getInt(data, offset);
			offset += 4;
			byte[] byte4 = new byte[len];
			MyByteBuffer.getBytes(data, offset, byte4, 0, len);
			item.extCode = new String(byte4);
			offset += len;

			item.ContractCRC = MyByteBuffer.getShort(data, offset);
			offset += 2;

			item.ContractUpdate = MyByteBuffer.getShort(data, offset);
			offset += 2;
			item.GroupFlag = MyByteBuffer.getShort(data, offset);
			offset += 2;

			mCodeTableList.add(item);

			if (bNeedAddToHqList) {
				TagLocalStockData hqItem = new TagLocalStockData();
				hqItem.code = item.code;
				hqItem.HQData.code = item.code;
				hqItem.group = item.group;
				hqItem.GroupOffset = item.GroupOffset;
				hqItem.market = item.market;
				hqItem.HQData.market = item.market;
				hqItem.extCode = item.extCode;
				hqItem.name = item.name;
				hqItem.PriceDecimal = item.PriceDecimal;
				hqItem.PriceRate = item.PriceRate;
				hqItem.VolUnit = item.VolUnit;
				hqItem.Multiplier = item.Multiplier;
				mMyApp.mHQData.updateData(hqItem, true);
			}
		}
		L.d(TAG, "End readFromFile Anlyse item" + lCount);
		L.d(TAG, "End readFromFile" + this.mMarketId);
		return mCodeTableList.size();
	}

	/**
	 * 写码表文件
	 * 
	 * @return
	 */
	public long writeToFile() {
		L.d(TAG, "Start writeToFile" + this.mMarketId);
		long lCount = mCodeTableList.size();
		if (lCount <= 0) {
			return 0;
		}
		char[] utf8header = { 0xef, 0xbb, 0xbf };

		byte[] data = new byte[32];

		int offset = 0;
		// utf8 header
		MyByteBuffer.putChar(data, offset, utf8header[0]);
		offset += 1;
		MyByteBuffer.putChar(data, offset, utf8header[1]);
		offset += 1;
		MyByteBuffer.putChar(data, offset, utf8header[2]);
		offset += 1;

		// nVersion
		int nVersion = CodeTable_Version;
		MyByteBuffer.putInt(data, offset, nVersion);
		offset += 4;
		// marketid
		MyByteBuffer.putInt(data, offset, mMarketId);
		offset += 4;
		// date
		MyByteBuffer.putInt(data, offset, mSaveDate);
		offset += 4;
		// time
		MyByteBuffer.putInt(data, offset, mSaveTime);
		offset += 4;
		// CRC
		MyByteBuffer.putInt(data, offset, mTableCRC);
		offset += 4;
		// count
		MyByteBuffer.putLong(data, offset, lCount);
		offset += 8;

		String fileName = String.format("%s%d.cfg", CodeTable_FileName,
				mMarketId);

		FileService file = new FileService(mMyApp.getApplicationContext());
		try {
			//
			file.saveToFile(fileName, data, offset);
			// 保存成功
			L.i(TAG, "writeToFile Success!");
			data = null;
		} catch (Exception e) {
			// 保存失败
			L.e("MarketInfo", "writeToFile Error!");
		}

		DataOutputStream dos = file.openFile(fileName, Context.MODE_PRIVATE
				+ Context.MODE_APPEND);

		L.d(TAG, "Start writeToFile anlyse item" + lCount);
		for (int i = 0; i < lCount; i++) {

			data = new byte[1000];
			offset = 0;

			// MyByteBuffer.putInt(data, offset, len);// 记录该条数据的长度
			// offset += 4;

			// MyByteBuffer.putBytes(data, offset, item, 0, len);
			// offset += len;

			// public short market; // 市场，为本地类型
			// public String code; // 代码
			// public short group; // 类别，为本地类型
			// public String name; // 名称

			// 对应LOCAL_CODETABLE_RECORD数据
			// public short GroupOffset; //分类号
			// public short PriceDecimal; //显示价格小数位
			// public int PriceRate; //价格放大倍率
			// public short VolUnit; //每手股数
			// public int Multiplier; //合约乘数，计划均价是用到
			// public String groupCode;

			// 对应ST_MKT_GROUP_INFO_UNICODE数据
			// public String extCode; //交易所代码
			// public short ContractCRC; //商品信息校验和
			// public short ContractUpdate; //商品代码状态 0-更新，1-新增，2-删除
			// public short GroupFlag; //指数或一般品种

			MyByteBuffer.putShort(data, offset, mCodeTableList.get(i).market);
			offset += 2;

			MyByteBuffer.putInt(data, offset,
					mCodeTableList.get(i).code.length());
			offset += 4;
			MyByteBuffer.putBytes(data, offset,
					mCodeTableList.get(i).code.getBytes(), 0,
					mCodeTableList.get(i).code.length());
			offset += mCodeTableList.get(i).code.length();

			MyByteBuffer.putShort(data, offset, mCodeTableList.get(i).group);
			offset += 2;

			MyByteBuffer.putInt(data, offset,
					mCodeTableList.get(i).name.getBytes().length);
			offset += 4;
			MyByteBuffer.putBytes(data, offset,
					mCodeTableList.get(i).name.getBytes(), 0,
					mCodeTableList.get(i).name.getBytes().length);
			offset += mCodeTableList.get(i).name.getBytes().length;

			MyByteBuffer.putShort(data, offset,
					mCodeTableList.get(i).GroupOffset);
			offset += 2;

			MyByteBuffer.putShort(data, offset,
					mCodeTableList.get(i).PriceDecimal);
			offset += 2;

			MyByteBuffer.putInt(data, offset, mCodeTableList.get(i).PriceRate);
			offset += 4;

			MyByteBuffer.putShort(data, offset, mCodeTableList.get(i).VolUnit);
			offset += 2;

			MyByteBuffer.putInt(data, offset, mCodeTableList.get(i).Multiplier);
			offset += 4;

			MyByteBuffer.putInt(data, offset,
					mCodeTableList.get(i).groupCode.length());
			offset += 4;
			MyByteBuffer.putBytes(data, offset,
					mCodeTableList.get(i).groupCode.getBytes(), 0,
					mCodeTableList.get(i).groupCode.length());
			offset += mCodeTableList.get(i).groupCode.length();

			MyByteBuffer.putInt(data, offset,
					mCodeTableList.get(i).extCode.length());
			offset += 4;
			MyByteBuffer.putBytes(data, offset,
					mCodeTableList.get(i).extCode.getBytes(), 0,
					mCodeTableList.get(i).extCode.length());
			offset += mCodeTableList.get(i).extCode.length();

			MyByteBuffer.putShort(data, offset,
					mCodeTableList.get(i).ContractCRC);
			offset += 2;

			MyByteBuffer.putShort(data, offset,
					mCodeTableList.get(i).ContractUpdate);
			offset += 2;
			MyByteBuffer
					.putShort(data, offset, mCodeTableList.get(i).GroupFlag);
			offset += 2;
			file.writeFile(dos, data, offset);

			data = null;

		}
		L.d(TAG, "End writeToFile anlyse item" + lCount);
		file.closeFile(dos);
		L.d(TAG, "End writeToFile" + this.mMarketId);
		return lCount;
	}

	public boolean isFileExist() {
		String fileName = String.format("%s%d.cfg", CodeTable_FileName,
				mMarketId);
		FileService file = new FileService(mMyApp.getApplicationContext());
		int size = file.getFileSize(fileName);

		if (size < 0)// 文件不存在
		{
			return false;
		}

		return true;
	}

	/**
	 * 获取码表数量
	 * 
	 * @param nMarket
	 * @return
	 */
	public long getNum(int nMarket) {
		long lRet = 0;
		if (mMarketId == nMarket) {
			lRet = mCodeTableList.size();
		}
		return lRet;
	}

	/**
	 * 获取码表校验和
	 * 
	 * @param nMarket
	 * @return
	 */
	public int getCRC(int nMarket) {
		int nRet = 0;
		if (mMarketId == nMarket) {
			nRet = mTableCRC;
		}
		return nRet;
	}

	public boolean setCRC(int nMarket, int nCRC) {
		boolean bRet = false;
		if (mMarketId == nMarket) {
			mTableCRC = nCRC;
			bRet = true;
		}
		return bRet;
	}

	public int getCRC_Calc(int nMarket) {
		int nRet = 0;
		if (mMarketId == nMarket) {
			for (int m = 0; m < mCodeTableList.size(); m++) {
				nRet += mCodeTableList.get(m).ContractCRC;
			}
			nRet = nRet % 65536;
		}
		return nRet;
	}

	/**
	 * 获取码表，从第start(从0开始计数)只开始取num只股票，返回数量
	 * 
	 * @param nMarket
	 * @param code
	 * @return
	 */
	public ArrayList<CCodeTableItem> getDataByCode(int nMarket, String code) {
		if (mMarketId == nMarket) {

			ArrayList<CCodeTableItem> list = new ArrayList<CCodeTableItem>();
			String inCode = code;
			/**
			 * 通配符
			 */
			boolean bWildcards = false;
			if (inCode.endsWith("*")) {
				inCode = inCode.substring(0, inCode.length() - 1);
				bWildcards = true;
			}

			for (int i = 0; i < mCodeTableList.size(); i++) {
				CCodeTableItem item = mCodeTableList.get(i);
				if (bWildcards) {
					if (item.market == nMarket && item.code.startsWith(inCode)) {
						list.add(item);
					}
				} else {
					if (item.market == nMarket
							&& item.code.equalsIgnoreCase(inCode)) {
						list.add(item);
						break;
					}
				}
			}
			return list;
		}
		return null;
	}

	/**
	 * 获取码表，从第start(从0开始计数)只开始取num只股票，返回数量
	 * 
	 * @param nMarket
	 * @param group
	 * @return
	 */
	public ArrayList<CCodeTableItem> getDataByGroup(int nMarket, String group) {
		if (mMarketId == nMarket) {
			ArrayList<CCodeTableItem> list = new ArrayList<CCodeTableItem>();

			for (int i = 0; i < mCodeTableList.size(); i++) {
				CCodeTableItem item = mCodeTableList.get(i);
				if (item.market == nMarket && item.groupCode.equals(group)) {
					list.add(item);
				}
			}
			return list;
		}
		return null;
	}

	/**
	 * 获取码表，从第start(从0开始计数)只开始取num只股票，返回数量
	 * 
	 * @param nMarket
	 * @return
	 */
	public ArrayList<CCodeTableItem> getData(int nMarket) {
		if (mMarketId == nMarket) {
			return mCodeTableList;
		}
		return null;
	}

	/**
	 * 设置码表市场代码，值类型为int
	 * 
	 * @param nMarket
	 * @return
	 */
	public boolean setCodeTableMarket(int nMarket) {
		mMarketId = nMarket;
		return true;
	}

	/*
	 * 将CDataCodeTable成员变量的值设为0值，将List的值设为空
	 */
	public boolean clearCodeTable() {
		mSaveDate = 0;
		mSaveTime = 0;
		mCodeTableList.clear();
		return true;
	}

	public boolean add(CCodeTableItem aRecord) {
		if (mMarketId == aRecord.market) {
			if (mCodeTableList.size() >= MAX_CODETABLE_NUM) {
				L.e("ERROR: mCodeTableList.size() >= MAX_CODETABLE_NUM");
				return false;
			}

			mCodeTableList.add(aRecord);
		}
		return true;
	}

	public boolean remove(CCodeTableItem aRecord) {
		boolean bRet = false;
		if (mMarketId == aRecord.market) {
			for (int m = 0; m < mCodeTableList.size(); m++) {
				CCodeTableItem item = mCodeTableList.get(m);
				if (aRecord.market == item.market
						&& aRecord.code.equalsIgnoreCase(item.code)) {
					mCodeTableList.remove(m);
					bRet = true;
					break;
				}
			}
		}
		return bRet;
	}

	public boolean modify(CCodeTableItem aRecord) {
		boolean bRet = false;
		if (mMarketId == aRecord.market) {
			for (int m = 0; m < mCodeTableList.size(); m++) {
				CCodeTableItem item = mCodeTableList.get(m);
				if (aRecord.market == item.market
						&& aRecord.code.equalsIgnoreCase(item.code)) {
					mCodeTableList.get(m).copyData(aRecord);
					bRet = true;
					break;
				}
			}
		}
		if (bRet == false) {
			add(aRecord);
		}
		return bRet;
	}

	public void setLocalDateTime(int date, int time, int nMarket) {
		if (mMarketId == nMarket) {
			mSaveDate = date;
			mSaveTime = time;
			mDownSuccess = true;
		}
	}

	public boolean getTodayDownloadFlag(int nMarket) {
		boolean bRet = false;
		if (mMarketId == nMarket) {
			bRet = mDownSuccess;
		}
		return bRet;
	}

	public void setTodayDownloadFlag(int nMarket) {
		if (mMarketId == nMarket) {
			mDownSuccess = true;
		}
	}
}
