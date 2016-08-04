package com.pengbo.mhdzq.data;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;

import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.CMarketInfoItem.mktGroupInfo;
import com.pengbo.mhdzq.net.MyByteBuffer;
import com.pengbo.mhdzq.tools.ByteUtils;
import com.pengbo.mhdzq.tools.FileService;
import com.pengbo.mhdzq.tools.L;

/**
 * 根据用户的权限返回对应的市场信息
 * 
 * @author Administrator
 * 
 */
public class CMarketInfo {

	public final static int MAX_LOCAL_MARKETINFO_COUNT = 200;
	public final static String MarketInfo_FileName = "pb_marketinfo_xh.cfg";
	public final static int MarketInfo_Version = 1;

	public boolean mDownSuccess;
	/**
	 * 文件内容的最后修改日期YYYYMMDD
	 */
	public int mDate;
	/**
	 * 文件内容的最后修改时间HHMMSS
	 */
	public int mTime;
	private ArrayList<CMarketInfoItem> mMarketList;

	public CMarketInfo() {
		mDownSuccess = false;
		mMarketList = new ArrayList<CMarketInfoItem>();
	}

	public int getNum() {
		return mMarketList.size();
	}

	public void addItem(CMarketInfoItem aItem) {
		mMarketList.add(aItem);
	}

	public void setDateTime(int date, int time) {
		mDate = date;
		mTime = time;
		mDownSuccess = true;
	}

	public String getGroupCode(short marketId) {
		for (int i = 0; i < mMarketList.size(); i++) {
			CMarketInfoItem aItem = mMarketList.get(i);
			if (aItem.MarketId == marketId) {
				return aItem.Code;
			}
		}
		return "";
	}

	public short getMarketId(String marketCode) {
		short sMarket = 0;
		for (int i = 0; i < mMarketList.size(); i++) {
			CMarketInfoItem aItem = mMarketList.get(i);
			if (marketCode.equals(aItem.Code)) {
				sMarket = aItem.MarketId;
				break;
			}
		}
		return sMarket;
	}

	public mktGroupInfo searchMarketGroupInfo(short market, String marketCode,
			short groupOffset) {
		for (int i = 0; i < mMarketList.size(); i++) {
			CMarketInfoItem aItem = mMarketList.get(i);
			if (aItem.MarketId == market
					|| (marketCode != null && marketCode.equals(aItem.Code))) {
				if (groupOffset < aItem.mGroupList.size()) {
					return aItem.mGroupList.get(groupOffset);
				}
				break;
			}
		}
		return null;
	}

	/**
	 *  与市场有关的接口
	 * @return
	 */
	public long readFromFile() {
		mMarketList.clear();

		FileService file = new FileService(MyApp.getInstance()
				.getApplicationContext());
		int size = file.getFileSize(MarketInfo_FileName);
		byte[] data = new byte[size + 1];

		int ret = file.readFile(MarketInfo_FileName, data);
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

		if (nVersion < MarketInfo_Version) {
			return 0;
		}

		mDate = MyByteBuffer.getInt(data, offset);
		offset += 4;

		mTime = MyByteBuffer.getInt(data, offset);
		offset += 4;

		long lCount = 0;
		lCount = MyByteBuffer.getLong(data, offset);
		offset += 8;

		CMarketInfoItem aItem = new CMarketInfoItem();

		int bodyItemLen = 0;
		byte[] byteItem = null;
		for (int i = 0; i < lCount && i < MAX_LOCAL_MARKETINFO_COUNT; i++) {
			bodyItemLen = MyByteBuffer.getInt(data, offset);
			offset += 4;

			byteItem = new byte[bodyItemLen];
			MyByteBuffer.getBytes(data, offset, byteItem, 0, bodyItemLen);
			try {
				aItem = (CMarketInfoItem) ByteUtils.toObject(byteItem);
				mMarketList.add(aItem);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			offset += bodyItemLen;
		}

		return mMarketList.size();
	}

	/**
	 *  写码表文件
	 * @return
	 */
	public long writeToFile() {
		int iCount = mMarketList.size();
		if (iCount <= 0) {
			return iCount;
		}
		char[] utf8header = { 0xef, 0xbb, 0xbf };

		byte[] data = new byte[24];

		int offset = 0;
		// utf8 header
		MyByteBuffer.putChar(data, offset, utf8header[0]);
		offset += 1;
		MyByteBuffer.putChar(data, offset, utf8header[1]);
		offset += 1;
		MyByteBuffer.putChar(data, offset, utf8header[2]);
		offset += 1;

		// nVersion
		int nVersion = MarketInfo_Version;
		MyByteBuffer.putInt(data, offset, nVersion);
		offset += 4;
		// date
		MyByteBuffer.putInt(data, offset, mDate);
		offset += 4;
		// time
		MyByteBuffer.putInt(data, offset, mTime);
		offset += 4;
		// count
		MyByteBuffer.putLong(data, offset, iCount);
		offset += 8;

		FileService file = new FileService(MyApp.getInstance()
				.getApplicationContext());
		try {
			//
			file.saveToFile(MarketInfo_FileName, data, offset);
			// 保存成功
			L.i("MarketInfo", "writeToFile Success!");
			data = null;
		} catch (Exception e) {
			// 保存失败
			L.e("MarketInfo", "writeToFile Error!");
		}

		DataOutputStream dos = file.openFile(MarketInfo_FileName,
				Context.MODE_PRIVATE + Context.MODE_APPEND);

		for (int i = 0; i < iCount; i++) {
			byte[] item = null;
			try {
				item = ByteUtils.toByteArray(mMarketList.get(i));
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (item != null) {
				int len = item.length;
				data = new byte[4 + item.length + 1];
				offset = 0;

				MyByteBuffer.putInt(data, offset, len);// 记录该条数据的长度
				offset += 4;

				MyByteBuffer.putBytes(data, offset, item, 0, len);
				offset += len;

				file.writeFile(dos, data, offset);
				// try {
				// //
				// file.saveToFileAppend(MarketInfo_FileName, data, offset);
				// // 保存成功
				// L.i("MarketInfo", "writeToFile Success!");
				// } catch (Exception e) {
				// // 保存失败
				// L.e("MarketInfo", "writeToFile Error!");
				// }
				data = null;
			}
		}
		file.closeFile(dos);
		return iCount;
	}

	public boolean isFileExist() {
		FileService file = new FileService(MyApp.getInstance()
				.getApplicationContext());
		int size = file.getFileSize(MarketInfo_FileName);

		if (size < 0)// 文件不存在
		{
			return false;
		}

		return true;
	}

	public void clear() {
		mMarketList.clear();
	}

	public long getLocalDateTime() {
		return ((long) mDate << 32) + mTime;
	}

	public void setTodayDownloadFlag() {
		mDownSuccess = true;
	}
}
