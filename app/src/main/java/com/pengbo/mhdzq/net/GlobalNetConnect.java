package com.pengbo.mhdzq.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.Global_Define;
import com.pengbo.mhdzq.data.SSLEncrypt;
import com.pengbo.mhdzq.data.ZLibTools;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.MIniFile;
import com.pengbo.mhdzq.tools.STD;
import com.pengbo.mhdzq.tools.PreferenceEngine;

/**
 * 网络连接 类
 * 
 * @author pobo
 * 
 */
public class GlobalNetConnect {

	public final static String TAG = GlobalNetConnect.class.getSimpleName();
	//
	public MyApp mMyApp;

	/**
	 * http下载config文件
	 */
	private ArrayList<DownloadFileInfo> mDownloadList;
	/**
	 * downloadList.ini
	 */
	private String mCurrentDownloadFile;
	/**
	 * 存储服务器地址,地址格式为"192.168.3.72:50"
	 */
	private String mAddr[];
	private int mAddrNum;
	/**
	 * 当前正在连的地址列表
	 */
	private String mAddrConnect[];
	private int mAddrConnectNum;
	/**
	 * 连接成功，当前正连接的地址，用于推送连接
	 */
	private String mAddrSuccess;
	/**
	 * 会话ID
	 */
	public int mSessionID;
	/**
	 * 0:行情 1:当日走势 2:K线 3:明细 4:雷达 5:期货分笔明细 6.7.8:历史走势 9:香港经纪商名称 请求标记
	 */
	public int mRequestFlagCount[];
	/**
	 * 请求号
	 */
	public int mRequestCode;
	/**
	 * 功能页面ID
	 */
	public int mPageId;
	/**
	 * 设备标识保存用的文件名
	 */
	public static final String DEVICETOKEN_SAVE_FILE = "deviceToken";
	/**
	 * 设备标识保存用的KEY
	 */
	public static final String DEVICETOKEN_SAVE_KEY = "deviceToken";

	private NetSendThread mSendThread;

	/**
	 * 主线程上的Handler
	 */
	private Handler mMainHandle;
	/**
	 * 最大支持的字段数
	 */
	public static final int MAX_HQ_FIELD_NUM = 256;
	/**
	 * 通讯头长度
	 */
	public static final int MC_FrameHead_LEN = 12;
	/**
	 * 通讯包大小
	 */
	public static final int MAX_MOBILE_PACKAGE_SIZE = 8000;
	/**
	 * 连接失败
	 */
	public static final int MSG_CONNECT_FAIL = -1;
	/**
	 * 接受数据错误
	 */
	public static final int MSG_READDATA_ERROR = -2;
	/**
	 * 连接失败
	 */
	public static final int MSG_CONNECT_ERROR = -100;
	/**
	 * 更新界面消息
	 */
	public static final int MSG_UPDATE_DATA = 100;
	public static final int MSG_PUSH_DATA = 101;
	public static final int MSG_RET_ERROR = 102;
	public static final int MSG_SEARCH = 103;
	/**
	 * 踢人消息
	 */
	public static final int MSG_RELOGIN = 104;
	/**
	 * 更新版本
	 */
	public static final int MSG_UPDATE_VERSION = 105;
	/**
	 * 请求超时
	 */
	public static final int MSG_TIMEOUT = 106;
	/**
	 * 盘中实时点评消息
	 */
	public static final int MSG_RT_COMMENT = 108;
	/**
	 * 指数推送
	 */
	public static final int MSG_PUSH_INDEX = 109;
	/**
	 * 添加删除自选（搜索界面）
	 */
	public static final int MSG_ADAPTER_BUTTON_CLICK = 110;
	/**
	 * （搜索界面）返回刷新界面
	 */
	public static final int MSG_REFRESH_BACKFROMSEARCH = 111;
	/**
	 * （搜索界面）返回刷新个股界面添加删除按钮
	 */
	public static final int MSG_UPDATE_BUTTON_BACKFROMSEARCH = 112;
	/**
	 * 开启推送服务
	 */
	public static final int MSG_START_PUSHMAIL_SERVICE = 1900;

	/**
	 * 心跳包定时，心跳包间隔时间
	 */
	private static final long HEART_TIMER = 60 * 1000;
	/**
	 * 15分钟，会话超时时间
	 */
	private static final long SESSION_TIMEOUT = 15 * 60 * 1000;
	/**
	 * 连接超时时间
	 */
	private static final long CONNECT_TIMEOUT = 10 * 1000;
	/**
	 * 网络数据超时时间
	 */
	private static final long NETDATA_TIMEOUT = 20 * 1000;
	private int mMsgId = MSG_UPDATE_DATA;
	/**
	 * 控制Handle是否发送消息
	 */
	private boolean isUpdate;
	/**
	 * 附加数据
	 */
	private int mArg2;
	private CMessageObject mMsgObject;
	/**
	 * 用于坚持多包的请求号
	 */
	private int mDecodeRequestCode;
	/**
	 * 请求邮件的时间标识
	 */
	private long requestMailTime = 0;
	/**
	 * 是否正在请求邮件
	 */
	private boolean isRequestMailNow;
	public boolean bReq_144_7 = false;
	private Timer mTimerCheckNetWork = null;

	private byte[] mSaveData;
	private int mSaveDataSize;

	public void setUsePort2(boolean bUsePort2) {

	}

	public void setAddr(String addr[], int num) {
		mAddrNum = 0;
		for (int i = 0; i < num; i++) {
			String t = addr[i];
			if (t == null || t.length() < 1) {
				continue;
			}
			char c = t.charAt(0);
			if (c >= '0' && c <= '9') {
				if (mAddrNum < mAddr.length) {
					mAddr[mAddrNum] = t;
					mAddrNum++;
				}
			}
		}
		for (int i = 0; i < num; i++) {
			String t = addr[i];
			if (t == null || t.length() < 1) {
				continue;
			}
			char c = t.charAt(0);
			if (c < '0' || c > '9') {
				if (mAddrNum < mAddr.length) {
					mAddr[mAddrNum] = t;
					mAddrNum++;
				}
			}
		}
		for (int i = 0; i < mAddrNum; i++) {
			mAddrConnect[i] = mAddr[i];
		}
		mAddrConnectNum = mAddrNum;
	}

	private void deleteAddrWithIndex(int index) {
		if (index < 0 || index >= mAddrConnectNum) {
			return;
		}
		for (int i = index + 1; i < mAddrConnectNum; i++) {
			mAddrConnect[i - 1] = mAddrConnect[i];
		}
		mAddrConnectNum--;
	}

	private void resetConnectAddr() {
		for (int i = 0; i < mAddrNum; i++) {
			mAddrConnect[i] = mAddr[i];
		}
		mAddrConnectNum = mAddrNum;
	}

	public int getAddrNum() {
		return mAddrConnectNum;
	}

	public String getSuccessAddr() {
		return mAddrSuccess;
	}

	public boolean isRequestMailNow() {
		return isRequestMailNow;
	}

	public void setRequestMailNow(boolean isRequestMailNow) {
		this.isRequestMailNow = isRequestMailNow;
	}

	public long getRequestMailTime() {
		return requestMailTime;
	}

	public void setRequestMailTime(long requestMailTime) {
		this.requestMailTime = requestMailTime;
	}

	public GlobalNetConnect(MyApp app) {
		L.e(TAG, "GlobalNetConnect--->this = " + this.toString());
		mMyApp = app;
		mMsgObject = new CMessageObject();

		mRequestFlagCount = new int[10];

		mAddr = new String[10];
		mAddrNum = 0;

		mAddrConnect = new String[10];
		mAddrConnectNum = 0;

		mAddrSuccess = new String();

		mDownloadList = new ArrayList<DownloadFileInfo>();
		mCurrentDownloadFile = "";

	}

	synchronized public void closeConnect() {
		if (mSendThread != null) {
			if (GlobalNetConnect.this == mMyApp.mCertifyNet)
				L.e(TAG, "closeConnect--->Certify");
			else if (GlobalNetConnect.this == mMyApp.mHQPushNet)
				L.e(TAG, "closeConnect--->HQ");

			mSendThread.closeNetThread();
			mSendThread = null;
		}

		//
		if (GlobalNetConnect.this == mMyApp.mCertifyNet
				|| GlobalNetConnect.this == mMyApp.mHQPushNet)
			mMyApp.mLoginFlag = false;
	}

	// 是否已连接
	public boolean IsConnected() {
		if (mSendThread != null) {
			if (mSendThread.isConnected())
				return true;
		}
		return false;
	}

	public void setMainHandler(Handler handler) {
		this.mMainHandle = handler;
		if (handler == null) {
			L.i(TAG, "handler is null!");
		}
	}

	public Handler getHandler() {
		return this.mMainHandle;
	}

	//

	int mMainType;

	/**
	 * 解析协议数据
	 * 
	 * @param data
	 * @param size
	 * @return：返回0表示不满一个包，返回负数表示有错误，返回正数表示处理的字节数
	 */
	protected int decode(byte[] data, int size, CMessageObject msgObject) {
		msgObject.nErrorCode = 0;
		if (size < MC_FrameHead_LEN) {
			L.e(TAG, "size < MC_FrameHead_LEN!");
			return 0;
		}
		HQFrameHead head = new HQFrameHead();
		int ret = GlobalNetProgress.CheckData(data, size, head);
		if (ret <= 0) {
			if (ret < 0)
				L.e(TAG, "CheckData Error!!! " + ret);
			return ret;
		}

		int packagesize = head.wDataSize;

		int offset = 0;
		offset += MC_FrameHead_LEN;
		// 解密
		int datasize = packagesize;
		byte[] szSrc = new byte[datasize + 50];
		// 拷贝数据。包头数据不压缩不加密
		if (head.cFlagDesEncode == 1) {
			System.arraycopy(data, GlobalNetConnect.MC_FrameHead_LEN + 2,
					szSrc, 0, datasize - 2);
		} else {
			System.arraycopy(data, GlobalNetConnect.MC_FrameHead_LEN, szSrc, 0,
					datasize);
		}

		if (head.cFlagDesEncode == 1) {
			byte[] szDst = new byte[datasize + 50];
			datasize = MyByteBuffer.getUnsignedShort(data, offset);
			datasize = (int) SSLEncrypt.DesNcbcEncrypt(
					SSLEncrypt.SSLKEY_INDEX_HQ, szSrc, szDst, datasize, 0);

			if (datasize < 0) {
				// 解密错误
				L.e(TAG, "Decrypt Error!!! " + datasize);
				return datasize;
			}
			System.arraycopy(szDst, 0, szSrc, 0, szSrc.length);
			szDst = null;
		}

		// 解压
		byte[] expanddata = new byte[MAX_MOBILE_PACKAGE_SIZE + 100];
		if (head.cFlagZlibCompress == 1) {
			datasize = ZLibTools.decompress(szSrc, datasize, expanddata);

			if (datasize <= 0) {
				L.e(TAG, "ExpandBuf Error!!!");
				expanddata = null;
				return -12;
			}
		} else {
			System.arraycopy(szSrc, 0, expanddata, 0, datasize);
		}

		szSrc = null;
		// 返回错误
		if (head.cErrorCode == 1) {
			L.e(TAG, "[" + head.cFrameType + "], head.cErrorCode == 1!!!");
		}

		isUpdate = false;
		mMainType = head.cFrameType;
		mArg2 = head.RequestCode;

		if (head.wFrameNo >= 0 && head.wFrameNo <= head.wFrameCount - 1) {
			if (head.wFrameNo == 0) {
				msgObject.clearData();
				mDecodeRequestCode = head.RequestCode;
			}

			if (mDecodeRequestCode != head.RequestCode) {
				L.e(TAG, "ERROR:Package sequence is incorrect");
				expanddata = null;
				return packagesize + GlobalNetConnect.MC_FrameHead_LEN;
			}

			msgObject.setData(expanddata, datasize);
			if (head.wFrameNo < head.wFrameCount - 1)// 未接受完完整包
			{
				expanddata = null;
				return packagesize + GlobalNetConnect.MC_FrameHead_LEN;
			}
			isUpdate = true;
		}

		if (mMainType == Global_Define.MFT_ERROR_DESCRIBE
				|| mMainType == Global_Define.MFT_MOBILE_ERROR_DESCRIBE) {
			short aFrameType = (short) MyByteBuffer.getUnsignedByte(expanddata,
					0);
			short aChildType = (short) MyByteBuffer.getUnsignedByte(expanddata,
					1);
			int aErrorCode = MyByteBuffer.getUnsignedShort(expanddata, 2);
			if (aErrorCode != 0) {
				msgObject.nErrorCode = aErrorCode;
			} else {
				msgObject.nErrorCode = -1;
			}

			msgObject.errorMsg = STD.getStringFromUnicodeBytes(expanddata, 4,
					120);
			L.e(TAG, "ERROR:[" + head.cFrameType + "], aErrorCode == "
					+ aErrorCode + "errormsg:" + msgObject.errorMsg);
			mMainType = aFrameType;
		}
		switch (mMainType) {
		case Global_Define.MFT_KEEP_ALIVE: // 心跳包
		{
			isUpdate = false;
			break;
		}
		case Global_Define.MFT_MOBILE_LOGIN_APPLY: // 登录
		{
			byte cLoginType = MyByteBuffer.getByte(expanddata, 0);
			if (cLoginType == 0)// 查询连接
			{
				mMyApp.mLoginIdentID = MyByteBuffer.getInt(expanddata, 1);
				mMyApp.setHQLoginStatus(MyApp.HQLoginStatus_Query);
				mMyApp.UpdateHQPushNetAddress();
				isUpdate = true;
				doInitAfterHQLogin();

				// 登录成功，需要登录推送
				GlobalNetProgress.HQRequest_Login(mMyApp.mHQPushNet,
						mMyApp.mVersionCode, mMyApp.mUser, mMyApp.mPassWord,
						mMyApp.mLoginIdentID, mMyApp.mJGId, true);
			} else if (cLoginType == 1)// 推送连接
			{
				mMyApp.setHQLoginStatus(MyApp.HQLoginStatus_All);
				SendLastHQPushData();
			}
			resetConnectAddr();
			break;
		}
		case Global_Define.MFT_MOBILE_DATA_APPLY: // 手机端数据请求
		{
			break;
		}
		case Global_Define.MFT_MOBILE_SYSCONFIG_APPLY: // 手机端配置文件请求
		{
			break;
		}
		case Global_Define.MFT_MOBILE_PUSH_DATA: // 手机端行情订阅和推送
		{
			// int nTempLength = msgObject.getDataLength();
			// byte[] aTempData = new byte[nTempLength];
			// System.arraycopy(msgObject.getData(), 0, aTempData, 0,
			// nTempLength);
			mMyApp.parseAllOptionHQInfoData(msgObject.getData(),
					msgObject.getDataLength());
			break;
		}
		}

		expanddata = null;
		return packagesize + GlobalNetConnect.MC_FrameHead_LEN;
	}

	synchronized public int addSendData(byte[] data, int offset, int size,
			boolean bSaveData) {
		if (mSendThread == null || mSendThread.mRun == false) {
			mSendThread = new NetSendThread();
			if (GlobalNetConnect.this == mMyApp.mHQPushNet) {
				mSendThread.setUsePort2(true);
			}
			mSendThread.mRun = true;
			mSendThread.start();
		}

		if (GlobalNetConnect.this == mMyApp.mHQPushNet && bSaveData) {
			SaveLastHQPushData(data, size);
		}

		return mSendThread.addSendData(data, offset, size);
	}

	synchronized public void SaveLastHQPushData(byte[] data, int len)// 保存最后一次行情推送的请求数据
	{
		if (GlobalNetConnect.this != mMyApp.mHQPushNet)
			return;

		if (len > 0) {
			mSaveData = new byte[len + 1];
			mSaveDataSize = len;
			System.arraycopy(data, 0, mSaveData, 0, len);
		} else {
			mSaveData = null;
			mSaveDataSize = 0;
		}

	}

	synchronized public void ClearLastHQPushData()// 清空保存的最后一次行情推送的请求数据
	{
		if (GlobalNetConnect.this != mMyApp.mHQPushNet)
			return;

		mSaveData = null;
		mSaveDataSize = 0;
	}

	synchronized public void SendLastHQPushData()// 发送保存的最后一次行情推送的请求数据
	{
		if (GlobalNetConnect.this != mMyApp.mHQPushNet)
			return;

		if (mSaveDataSize > 0 && mSaveData != null) {
			addSendData(mSaveData, 0, mSaveDataSize, false);
		}
	}

	public class AlarmRecevier extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			//
			L.d(TAG, "onReceive Timer");
		}
	}

	// 发送心跳包
	public int SendHeartRequest() {
		byte[] data = new byte[MAX_MOBILE_PACKAGE_SIZE];
		int size = MAX_MOBILE_PACKAGE_SIZE;
		size = GlobalNetProgress.RequestMake_HeartBeat(data, size);

		L.e(TAG,
				"-----------SendHeartRequest!------------, this = "
						+ this.toString());
		if (mSendThread == null || mSendThread.mRun == false) {
			mSendThread = new NetSendThread();
			if (GlobalNetConnect.this == mMyApp.mHQPushNet) {
				mSendThread.setUsePort2(true);
			}
			mSendThread.mRun = true;
			mSendThread.start();
		}
		return mSendThread.addSendDataWithHeartFlag(data, 0, size, true);
	}

	private void startCheckNetWorkTimer() {
		stopCheckNetWorkTimer();

		mTimerCheckNetWork = new Timer();
		mTimerCheckNetWork.schedule(new TimerTask() {
			//
			public void run() {
				if (!mMyApp.mCertifyNet.IsConnected()
						|| !mMyApp.mHQPushNet.IsConnected()) {
					mMyApp.mCertifyNet.SendHeartRequest();
				}

			}
		}, 5000, 5000);
	}

	private void stopCheckNetWorkTimer() {
		if (mTimerCheckNetWork != null) {
			mTimerCheckNetWork.cancel();
		}
		mTimerCheckNetWork = null;
	}

	public void doInitAfterHQLogin() {
		startCheckNetWorkTimer();
	}

	public void RequestAPPVersionFile() {
		mCurrentDownloadFile = MyApp.APP_VERSIONFILE;
		DownLoaderTask task = new DownLoaderTask(
				AppConstants.SERVER_PATH_UPDATEAPP, MyApp.APP_VERSIONFILE);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public void RequestDownloadList() {
		mCurrentDownloadFile = MyApp.DOWNLOAD_CONFIGPATH;
		DownLoaderTask task = new DownLoaderTask(
				AppConstants.SERVER_PATH_UPDATEFILE, MyApp.DOWNLOAD_CONFIGPATH);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void DownloadConfigFile(String fileName) {
		if (fileName.isEmpty()) {
			return;
		}

		mCurrentDownloadFile = fileName;
		DownLoaderTask task = new DownLoaderTask(
				AppConstants.SERVER_PATH_UPDATEFILE, fileName);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	/**
	 * 网络发送线程
	 * 
	 * @author Administrator
	 * 
	 */
	public class NetSendThread extends Thread {
		SocketChannel mSocketChannel;
		public boolean mRun;
		public boolean mbUsePort2 = false;

		private byte[] mSendData;
		private int mSendSize;
		private Message mMsg;

		private long mLastNetDataTime; // 最后处理网络数据的时间
		private long mLastSessionTime; // 会话最后时间，即客户最后操作时间。
		private long mLastRequestTime; // 最后请求时间，-1表示已有应答

		private SocketChannel mSocketChannels[];

		byte[] mReadData;
		int mReadSize;

		ByteBuffer mSendByteBuffer;

		synchronized public boolean isConnected() {
			if (mSocketChannel != null && mSocketChannel.isConnected()) {
				return true;
			}
			return false;
		}

		public NetSendThread() {
			mRun = true;

			mSendData = new byte[GlobalNetConnect.MAX_MOBILE_PACKAGE_SIZE + 100];
			mSendSize = 0;

			mReadData = new byte[GlobalNetConnect.MAX_MOBILE_PACKAGE_SIZE * 5 + 200];
			mReadSize = 0;

			mSendByteBuffer = ByteBuffer
					.allocateDirect(GlobalNetConnect.MAX_MOBILE_PACKAGE_SIZE + 100);

			mLastNetDataTime = System.currentTimeMillis();
			mLastSessionTime = System.currentTimeMillis();
			mLastRequestTime = -1;

			mSocketChannels = new SocketChannel[10];
		}

		public void setUsePort2(boolean usePort2) {
			this.mbUsePort2 = usePort2;
		}

		synchronized private void closeNetThread() {
			L.i("NetSendThread", "NetSendThread--->closeNetThread 1");

			if (GlobalNetConnect.this == mMyApp.mCertifyNet) {
				L.i("NetSendThread",
						"mMyApp.mCertifyNet NetSendThread--->closeNetThread 1");
			} else if (GlobalNetConnect.this == mMyApp.mHQPushNet) {
				L.i("NetSendThread",
						"mMyApp.mHQPushNet NetSendThread--->closeNetThread 1");
			}

			//
			if (GlobalNetConnect.this == mMyApp.mCertifyNet
					|| GlobalNetConnect.this == mMyApp.mHQPushNet)
				mMyApp.mLoginFlag = false;

			mRun = false;
			mSessionID = 0;
			mRequestCode = 0;

			if (mSocketChannel == null) {
				return;
			}

			L.i(TAG, "NetSendThread--->closeNetThread 2");

			mLastNetDataTime = System.currentTimeMillis();
			mLastSessionTime = System.currentTimeMillis();
			mLastRequestTime = -1;

			mSendSize = 0;
			mReadSize = 0;
			mSendByteBuffer.clear();

			try {
				mSocketChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mSocketChannel = null;

		}

		/**
		 * 连接服务器方法，成功时返回0，否则返回负数
		 * 
		 * @return
		 */
		protected int connect() {
			// closeConnect();
			if (mAddrConnectNum == 0) {
				mAddrConnectNum = mAddrNum;
				for (int i = 0; i < mAddrNum; i++) {
					mAddrConnect[i] = mAddr[i];
				}
			}
			if (mAddrConnectNum == 0) {
				L.d(TAG, "没有连接地址!");
				return -2;
			}

			// L.d(TAG, "connect..." + mIP + ":" + mPort);
			int connectIndex = -1;
			int startindex = 0;// (int)(System.currentTimeMillis()&0xff);

			int addnum = 0;
			long lasttimes = System.currentTimeMillis();

			// L.d(TAG, " connect startindex: " + startindex + " lastip:" +
			// mLastConnectedAddr);
			while (mRun) {
				if (addnum < mAddrConnectNum)// 添加连接
				{
					SocketChannel tSocketChannel = null;
					try {
						tSocketChannel = SocketChannel.open();
						tSocketChannel.configureBlocking(false);

						int index = (startindex + addnum) % mAddrNum;
						String addr = mAddrConnect[index];
						L.d(TAG, "start addr:  " + addr);

						String ip = STD.GetValue(addr, 1, ':');
						int port = STD.GetValueInt(addr, 2, ':');
						if (mbUsePort2) {
							int port2 = STD.GetValueInt(addr, 3, ':');
							if (port2 > 0) {
								port = port2;
							}
						}
						InetAddress inetAddr = InetAddress.getByName(ip);
						SocketAddress address = new InetSocketAddress(
								inetAddr.getHostAddress(), port);

						L.d(TAG, "begin connect... ");
						boolean ret = tSocketChannel.connect(address);
						L.d(TAG, "connect end.  ret:" + ret);

						mSocketChannels[addnum] = tSocketChannel;
						lasttimes = System.currentTimeMillis();
					} catch (Exception e) {
						e.printStackTrace();
						L.e(TAG, "connect Exception");
						if (tSocketChannel != null) {
							try {
								tSocketChannel.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
					addnum++;

				}
				for (int i = 0; i < addnum; i++) {
					SocketChannel socket = mSocketChannels[i];
					if (socket != null) {
						boolean result = false;
						try {
							result = socket.finishConnect();
						} catch (IOException e) {
							e.printStackTrace();
						} // 已返回

						if (result) {
							connectIndex = i;
							break;
						}
					}
				}

				if (connectIndex != -1) {
					break;
				}
				if (System.currentTimeMillis() - lasttimes >= CONNECT_TIMEOUT) // 连接超时
				{
					return -1;
				}
				if (addnum < mAddrConnectNum) {
					continue;
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			for (int i = 0; i < mAddrConnectNum; i++) {
				if (i != connectIndex) {
					SocketChannel t = mSocketChannels[i];
					if (t != null) {
						try {
							t.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						mSocketChannels[i] = null;
					}
				}
			}
			if (connectIndex != -1) {
				if (mSocketChannels[connectIndex].isConnected()) {
					synchronized (this) {
						mSocketChannel = mSocketChannels[connectIndex];
					}
					mAddrSuccess = new String(mAddrConnect[connectIndex]);
					deleteAddrWithIndex(connectIndex);
					L.d(TAG, "connect successed ip:"
							+ mAddrConnect[connectIndex]);
					return 0;
				}
			}
			L.e(TAG, "connect failed");
			closeNetThread();
			return -3;
		}

		public int addSendDataWithHeartFlag(byte[] data, int offset, int size,
				boolean flag) {
			if (!mMyApp.isSchedule) {// 防止重复踢人
				return 0;
			}
			synchronized (this) {
				if (size + mSendSize > mSendData.length) // 缓冲不够
				{
					return -1;
				}
				System.arraycopy(data, offset, mSendData, mSendSize, size);
				mSendSize += size;
				data = null;

				mLastNetDataTime = System.currentTimeMillis();
				if (!flag) // 非心跳包时，重设会话时间
				{
					mLastSessionTime = mLastNetDataTime;
					// L.e(TAG, "mLastSessionTime:" + mLastSessionTime);
				}
				return mSendSize;
			}
		}

		public int addSendData(byte[] data, int offset, int size) {
			return addSendDataWithHeartFlag(data, offset, size, false);
		}

		@Override
		public void run() {
			L.w("NetSendThread", "NetSendThread start--->mRun = " + mRun);
			boolean sleepflag = false;
			String errMsg = "";
			int errFlag = 0; // 0--弹出Dialog，1--Toast

			while (mRun) {
				errFlag = 0;
				sleepflag = true;

				// L.i("NetSendThread", "run--->mSendSize = " + mSendSize);
				if (mSendSize > 0) // 有数据需要发送
				{
					if (!IsConnected()) {
						L.i("NetSendThread", "run--->IsConnected() = "
								+ IsConnected());

						NetConnect net = new NetConnect();
						net.setUsePort2(mbUsePort2);
						net.setAddr(mAddrConnect, mAddrConnectNum);
						tagConnectInfo info = new tagConnectInfo();

						boolean ret = net.getSocketChannel(info, 30 * 1000);

						net.clear();
						net = null;
						if (!ret || info.socket == null) {
							L.e("NetSendThread", "Connect Failed!");

							errMsg = "连接服务器失败！";
							errFlag = 1;
							resetConnectAddr();
							break;
						}

						mAddrSuccess = new String(mAddrConnect[info.index]);
						L.i("NetSendThread", "Connect Success address:"
								+ mAddrSuccess);
						deleteAddrWithIndex(info.index);
						mSocketChannel = info.socket;

						//
						// 获取包数据头，检查是否为心跳包
						HQFrameHead head = new HQFrameHead();
						int r = GlobalNetProgress.CheckData(mSendData,
								mSendSize, head);
						if (r < 0) {
							L.e("NetSendThread", "CheckData Error!!! " + r);
							break;
						}

						L.i("NetSendThread", "new net connect loginstatus = "
								+ mMyApp.getHQLoginStatus());
						if (GlobalNetConnect.this == mMyApp.mCertifyNet
								&& (mMyApp.getHQLoginStatus() & MyApp.HQLoginStatus_Query) != MyApp.HQLoginStatus_Query
								&& head.cFrameType != Global_Define.MFT_MOBILE_LOGIN_APPLY) {
							L.i("NetSendThread", "Need Query login");
							mSendSize = 0;// 丢弃掉之前无效的request
							// 查询需要重新登录
							GlobalNetProgress.HQRequest_Login(
									mMyApp.mCertifyNet, mMyApp.mVersionCode,
									mMyApp.mUser, mMyApp.mPassWord, 0,
									mMyApp.mJGId, false);

							mMyApp.setHQLoginStatus(MyApp.HQLoginStatus_Loging);
							continue;
						} else if (GlobalNetConnect.this == mMyApp.mHQPushNet
								&& head.cFrameType != Global_Define.MFT_MOBILE_LOGIN_APPLY) {
							if ((mMyApp.getHQLoginStatus() & MyApp.HQLoginStatus_Query) != MyApp.HQLoginStatus_Query) {
								// 行情未登录，需要登录
								L.i("NetSendThread", "Need Query login");
								mSendSize = 0;// 丢弃掉之前无效的request
								// 查询需要重新登录
								GlobalNetProgress.HQRequest_Login(
										mMyApp.mCertifyNet,
										mMyApp.mVersionCode, mMyApp.mUser,
										mMyApp.mPassWord, 0, mMyApp.mJGId,
										false);

								mMyApp.setHQLoginStatus(MyApp.HQLoginStatus_Loging);
								continue;
							} else if ((mMyApp.getHQLoginStatus() & MyApp.HQLoginStatus_Push) != MyApp.HQLoginStatus_Push) {
								L.i("NetSendThread", "Need push login");
								mSendSize = 0;// 丢弃掉之前无效的request
								// 推送需要重新登录
								GlobalNetProgress.HQRequest_Login(
										mMyApp.mHQPushNet, mMyApp.mVersionCode,
										mMyApp.mUser, mMyApp.mPassWord,
										mMyApp.mLoginIdentID, mMyApp.mJGId,
										true);
								continue;
							}
						}
					} // if (!IsConnected())
					else {
						L.i(TAG, "run--->IsConnected() = " + IsConnected());
					}
				} // if (mSendSize > 0)

				if (mSocketChannel != null) {
					// 发送数据
					if (mSendSize > 0) {
						synchronized (this) {
							mSendByteBuffer.clear();
							mSendByteBuffer.put(mSendData, 0, mSendSize);
							mSendByteBuffer.flip();
						}

						mLastNetDataTime = System.currentTimeMillis();

						if (mLastRequestTime == -1) {
							mLastRequestTime = mLastNetDataTime;
						}

						// 获取包数据头，检查是否为心跳包
						HQFrameHead head = new HQFrameHead();
						int ret = GlobalNetProgress.CheckData(mSendData,
								mSendSize, head);
						if (ret < 0) {
							L.e("NetSendThread", "CheckData Error!!! " + ret);
							break;
						}
						if (head.cFrameType == Global_Define.MFT_KEEP_ALIVE) {
							mLastRequestTime = -1;// 如果是心跳包，不要超时判断
						}

						L.d("NetSendThread",
								"--------------------------------------");
						L.d("NetSendThread", "sendsize:" + mSendSize);
						synchronized (this) {
							try {
								int size = mSocketChannel
										.write(mSendByteBuffer);
								L.i("NetSendThread", "mSocketChannel = "
										+ mSocketChannel + "write size = "
										+ size);
							} catch (Exception e) {
								e.printStackTrace();
								L.e("NetSendThread", "send data IOException...");
								errFlag = 1;
								errMsg = "连接服务器失败！";
								break;
							}
							mSendSize = 0;
						}
						sleepflag = false;
					} // if (mSendSize > 0)

					// 接收数据
					int size = 0;
					synchronized (this) {
						mSendByteBuffer.clear();
						try {
							size = mSocketChannel.read(mSendByteBuffer);
						} catch (Exception e) {
							e.printStackTrace();
							L.e("NetSendThread", "receive data IOException...");
							errFlag = 1;
							errMsg = "连接服务器失败！";
							break;
						}
						mSendByteBuffer.flip();
					}

					synchronized (this) {
						if (size < 0) {
							L.e("NetSendThread", "readsize:" + size);
							errMsg = "连接服务器失败！";
							break;
						} else if (size > 0) {
							mLastRequestTime = -1; // 网络有数据返回，请求有应答

							sleepflag = false;
							L.d("NetSendThread", "readsize:" + size);

							if (size + mReadSize > mReadData.length) // 数据有误
							{
								L.e("NetSendThread", "数据有误...");
								errFlag = 1;
								errMsg = "数据有误！";
								break;
							}
							mSendByteBuffer.get(mReadData, mReadSize, size);
							mReadSize += size;

							boolean quitflag = false;
							while (mRun) {
								isUpdate = false;
								size = decode(mReadData, mReadSize, mMsgObject);
								L.w("NetSendThread", "decode size:" + mReadSize
										+ "  ret:" + size);
								if (size < 0) // 解析数据错误，需关闭会话
								{
									L.e("NetSendThread", "解析数据错误");
									errMsg = "解析数据错误";
									errFlag = 1;
									quitflag = true;
									break;
								} else if (size > 0) {
									// 发送更新消息
									if (isUpdate && mMainHandle != null) {
										CMessageObject msgObj = mMsgObject
												.clone();
										mMsg = mMainHandle.obtainMessage(
												mMsgId, mMainType, mArg2,
												msgObj);
										mMainHandle.sendMessage(mMsg);
									} else if (mMainHandle == null) {
										L.e("NetSendThread",
												"data decode sucess but mMainHandle == null");
									}
									int left = mReadSize - size;
									if (left > 0) {
										System.arraycopy(mReadData, size,
												mReadData, 0, left);
										mReadSize = left;
									} else {
										mReadSize = 0;
										break;
									}
								} else { // 不是完整包
									break;
								}
							} // while (mRun)
							if (quitflag) {
								break;
							}
						} // else if (size > 0)
					} // synchronized (this)
				} // if (mSocketChannel != null)
				else {
					L.e("NetSendThread", "mSocketChannel == null");
				}

				long now = System.currentTimeMillis();
				if (now - mLastNetDataTime >= HEART_TIMER) // 一段时间没有网络数据，需要发心跳包了
				{
					SendHeartRequest();
				}
				// 临时加入超时处理，解决对方网络关闭，客户端无法收到关闭状态
				else if (now - mLastSessionTime >= SESSION_TIMEOUT) // 会话超时，需要关闭会话
				{
					L.e("NetSendThread", "SESSION TIMEOUT!");
					errMsg = "请求超时，请确认网络通讯是否正常！";
					closeNetThread();

					break;
				}

				// 临时加入超时处理，解决对方网络关闭，客户端无法收到关闭状态
				if (mLastRequestTime > 0
						&& now - mLastRequestTime >= NETDATA_TIMEOUT) // 请求数据超时
				{
					L.e("NetSendThread", "Recv Data time out!");
					errMsg = "请求超时，请确认网络通讯是否正常！";

					break;
				}
				if (sleepflag) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						L.e("NetSendThread", "Thread InterruptedException...");

					}
				}
			}// while(mRun)

			closeNetThread();

			if (mMainHandle != null && errMsg.length() > 0) {//
				L.e("NetSendThread", "out of while-errmsg=" + errMsg);
				Message mMsg = mMainHandle.obtainMessage(MSG_CONNECT_ERROR,
						errFlag, 0, errMsg);
				mMainHandle.sendMessage(mMsg);
			}
			if (GlobalNetConnect.this == mMyApp.mCertifyNet
					|| GlobalNetConnect.this == mMyApp.mHQPushNet) {
				mMyApp.setHQLoginStatus(MyApp.HQLoginStatus_NO);
			}
			L.w("NetSendThread", "NetSendThread end");
		}
	}

	public class DownLoaderTask extends AsyncTask<Void, Integer, Long> {
		private final String TAG = "DownLoaderTask";
		private URL mUrl;
		private File mFile;
		private String mOut;
		private boolean mSuccess;

		private ProgressReportingOutputStream mOutputStream;
		private Context mContext;

		public DownLoaderTask(String urlPath, String fileName) {
			super();

			File file = mMyApp.getApplicationContext().getFilesDir();
			String path = file.getPath();
			if (mCurrentDownloadFile
					.equalsIgnoreCase(MyApp.DOWNLOAD_CONFIGPATH)) {
				// mOut = MyApp.DOWNLOAD_CONFIGPATH_TEMP;
				mOut = String.format("%s/%s", path,
						MyApp.DOWNLOAD_CONFIGPATH_TEMP);
			} else {
				// mOut = fileName;
				mOut = String.format("%s/%s", path, fileName);
			}

			String url = String.format("%s%s", urlPath, fileName);
			try {
				mUrl = new URL(url);

				mFile = new File(mOut);
				// L.d(TAG, "out=" + out + ", name=" + fileName
				// + ",mUrl.getFile()=" + mUrl.getFile());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			mSuccess = true;
		}

		@Override
		protected void onPreExecute() {
			// if (mDialog != null) {
			// mDialog.setTitle("Downloading...");
			// mDialog.setMessage(mFile.getName());
			// mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			// mDialog.setButton(AlertDialog.BUTTON_POSITIVE,
			// mContext.getString(R.string.cancel),
			// new DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface arg0, int arg1) {
			// // TODO Auto-generated method stub
			// cancel(true);
			// mDownloading = false;
			// if (mFile.exists()) {
			// mFile.delete();
			// }
			// }
			// });
			//
			// mDialog.setCanceledOnTouchOutside(false);
			// mDialog.setOnCancelListener(new OnCancelListener() {
			//
			// @Override
			// public void onCancel(DialogInterface dialog) {
			// if (!mCanCancel) {
			// return;
			// }
			// cancel(true);
			// mDownloading = false;
			// if (mFile.exists()) {
			// mFile.delete();
			// }
			// }
			// });
			// mDialog.show();
			// }
			// mDownloading = true;
		}

		@Override
		protected Long doInBackground(Void... params) {
			long ret = 0;
			try {
				ret = download();
			} catch (Exception e) {
				mSuccess = false;
				ret = 0;
			}
			return ret;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// if (mDialog == null)
			// return;
			// if (values.length > 1) {
			// int contentLength = values[1];
			// if (contentLength == -1) {
			// mDialog.setIndeterminate(true);
			// } else {
			// mDialog.setMax(contentLength);
			// }
			// } else {
			// mDialog.setProgress(values[0].intValue());
			// }
		}

		@Override
		protected void onPostExecute(Long result) {
			// mDownloading = false;
			// if (mDialog != null && mDialog.isShowing()) {
			// mDialog.dismiss();
			// }
			if (isCancelled())
				return;

			if (mSuccess) {
				if (mCurrentDownloadFile
						.equalsIgnoreCase(MyApp.APP_VERSIONFILE)) {
					// 下载app version文件成功
					MIniFile iniFile = new MIniFile();
					iniFile.setFilePath(mMyApp.getApplicationContext(),
							MyApp.APP_VERSIONFILE);
					String name = "ver";
					mMyApp.mNewestVersion = iniFile
							.ReadString("base", name, "");
					name = "path";
					mMyApp.mUpdateURL = iniFile.ReadString("base", name, "");
				} else if (mCurrentDownloadFile
						.equalsIgnoreCase(MyApp.DOWNLOAD_CONFIGPATH)) {
					parseDownloadListFile();
				} else {
					for (int i = 0; i < mDownloadList.size(); i++) {
						String strName = mDownloadList.get(i).mName;
						if (mCurrentDownloadFile.equals(strName)) {
							int id = mDownloadList.get(i).mId;
							String strVersion = mDownloadList.get(i).mVersion;
							String strKey = String.format("file%d", id);

							MIniFile iniFileLocal = new MIniFile();
							iniFileLocal.setFilePath(
									mMyApp.getApplicationContext(),
									MyApp.DOWNLOAD_CONFIGPATH);
							iniFileLocal.WriteString("files", strKey, strName);

							strKey = String.format("version%d", id);
							iniFileLocal.WriteString("files", strKey,
									strVersion);
							iniFileLocal.Write();

							// 从下载列表中移除已下载完成的文件
							mDownloadList.remove(i);
							break;
						}

					}
				}

				if (mDownloadList.size() > 0) {
					String downloadName = mDownloadList.get(0).mName;
					DownloadConfigFile(downloadName);
				}
				// ZipExtractorTask task = new ZipExtractorTask(mFile.getPath(),
				// String.format("%s", mOut), mContext, true);
				// task.execute();
			} else {
				// Builder b = new AlertDialog.Builder(this.mContext);
				// b.setMessage(R.string.IDS_DOWNLOAD_FAIL);
				// b.setPositiveButton(
				// this.mContext.getString(android.R.string.ok),
				// new DialogInterface.OnClickListener() {
				// @Override
				// public void onClick(DialogInterface dialog,
				// int which) {
				// }
				// });
				// b.create().show();

				if (mCurrentDownloadFile
						.equalsIgnoreCase(MyApp.APP_VERSIONFILE)) {
					mMyApp.mNewestVersion = "0";
				}

				if (mFile.exists()) {
					mFile.delete();
				}
			}

			if (mCurrentDownloadFile.equalsIgnoreCase(MyApp.APP_VERSIONFILE)) {
				Message msg = mMainHandle.obtainMessage(mMsgId,
						Global_Define.MFT_MOBILE_APP_UPGRADE, 0, mMsgObject);
				mMainHandle.sendMessage(msg);
			}
		}

		private long download() {
			URLConnection connection = null;
			int bytesCopied = 0;
			try {
				connection = mUrl.openConnection();
				connection.setUseCaches(false);
				int length = connection.getContentLength();
				if (length <= 0) {
					return 0;
				}
				if (mFile.exists() && length == mFile.length()) {
					mFile.delete();
					// L.d(TAG, "file " + mFile.getName() + " already exits!!");
					// return 0;
				}
				mOutputStream = new ProgressReportingOutputStream(mFile);
				publishProgress(0, length);
				bytesCopied = copy(connection.getInputStream(), mOutputStream);
				if (bytesCopied != length && length != -1) {
					mSuccess = false;
					L.e(TAG, "Download incomplete bytesCopied=" + bytesCopied
							+ ", length" + length);
				}
				mOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return bytesCopied;
		}

		private int copy(InputStream input, OutputStream output) {
			byte[] buffer = new byte[1024 * 8];
			BufferedInputStream in = new BufferedInputStream(input, 1024 * 8);
			BufferedOutputStream out = new BufferedOutputStream(output,
					1024 * 8);
			int count = 0, n = 0;
			try {
				while ((n = in.read(buffer, 0, 1024 * 8)) != -1) {
					out.write(buffer, 0, n);
					count += n;

					// while (mPaused) {
					// try {
					// Thread.sleep(100);
					// } catch (InterruptedException e) {
					// e.printStackTrace();
					// }
					// }
				}

				out.flush();
			} catch (IOException e) {
				// mDownloading = false;
				mSuccess = false;
				e.printStackTrace();
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return count;
		}

		private final class ProgressReportingOutputStream extends
				FileOutputStream {

			public ProgressReportingOutputStream(File file)
					throws FileNotFoundException {
				super(file);
			}

			@Override
			public void write(byte[] buffer, int byteOffset, int byteCount)
					throws IOException {
				super.write(buffer, byteOffset, byteCount);
				// mProgress += byteCount;
				// publishProgress(mProgress);
			}

		}
	}

	protected void parseDownloadListFile() {
		MIniFile iniFileNew = new MIniFile();
		iniFileNew.setFilePath(mMyApp.getApplicationContext(),
				MyApp.DOWNLOAD_CONFIGPATH_TEMP);

		// 读取apk版本信息，判断是否有新版本更新提示
		String latestVersion = iniFileNew.ReadString("APP_Android",
				"lastverion", "");
		String updateMessage = iniFileNew.ReadString("APP_Android", "message",
				"");
		String downloadUrl = iniFileNew.ReadString("APP_Android", "url", "");
		String versionName = iniFileNew.ReadString("APP_Android",
				"versionname", "");
		mMyApp.mUpdateURL = downloadUrl;
		mMyApp.mNewestVersion = latestVersion;
		mMyApp.mUpdateMSG = updateMessage;
		mMyApp.mNewestVersionName = versionName;

		// if (!latestVersion.isEmpty())
		// {
		// String nowVersion = PreferenceEngine.getInstance().getVersionCode();
		// if(nowVersion.isEmpty())
		// {
		// nowVersion = mMyApp.mVersionCode;
		// }
		// int nVer_Now = STD.StringToInt(nowVersion);
		// int nVer_Last = STD.StringToInt(latestVersion);
		// if (nVer_Now < nVer_Last)
		// {//有最新版本，需要更新
		// //暂时取消自动提醒功能，在关于里手动点击更新
		// // if (mMainHandle != null)
		// // {
		// // CMessageObject object = new CMessageObject();
		// // object.errorMsg = updateMessage;
		// // Message msg = mMainHandle.obtainMessage(
		// // mMsgId, Global_Define.MFT_MOBILE_APP_UPGRADE, 0,
		// // object);
		// // mMainHandle.sendMessage(msg);
		// // }
		// }
		// }
		//

		MIniFile iniFileLocal = new MIniFile();
		iniFileLocal.setFilePath(mMyApp.getApplicationContext(),
				MyApp.DOWNLOAD_CONFIGPATH);

		mDownloadList.clear();
		for (int i = 1; i < 10; i++) // downloadlist 文件里从file1开始
		{
			String fileKey = String.format("file%d", i);
			String strFileName = iniFileNew.ReadString("files", fileKey, "");
			if (strFileName.isEmpty()) {
				break;
			}

			String versionKey = String.format("version%d", i);
			String strVersionNew = iniFileNew.ReadString("files", versionKey,
					"");
			if (strVersionNew.isEmpty()) {
				break;
			}

			String strVersionLocal = iniFileLocal.ReadString("files",
					versionKey, "");
			if (!strVersionLocal.equalsIgnoreCase(strVersionNew)) // 版本号不相同需要下载
			{
				DownloadFileInfo fileInfo = new DownloadFileInfo();
				fileInfo.mName = strFileName;
				fileInfo.mVersion = strVersionNew;
				fileInfo.mId = i;
				mDownloadList.add(fileInfo);
			}
		}
	}

	private void print_MDBF() {

	}
}
