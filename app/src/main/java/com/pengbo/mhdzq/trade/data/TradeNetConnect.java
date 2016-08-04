package com.pengbo.mhdzq.trade.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.data.CDataEncrypt;
import com.pengbo.mhdzq.data.SSLEncrypt;
import com.pengbo.mhdzq.data.ZLibTools;
import com.pengbo.mhdzq.net.CMessageObject;
import com.pengbo.mhdzq.net.MyByteBuffer;
import com.pengbo.mhdzq.net.NetConnect;
import com.pengbo.mhdzq.net.tagConnectInfo;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.STD;

public class TradeNetConnect {
	public MyApp		mMyApp;
	
	public int		mSessionID;		//会话ID
	public int		mRequestCode;	//请求号
	String			mIP;
	int				mPort; 
	
	NetSendThread	mSendThread;
	
	Handler			mMainHandle;
	
	public	static final int	Trade_FrameHead_LEN	= 8;		//交易通讯头长度
	public	static final int	MAX_MOBILE_PACKAGE_SIZE	= 8000;	//通讯包大小
	public	static final int    MAX_MOBILE_PACKAGE_BUFFER_SIZE_STEP = 9900;
	public	static final int    MAX_MOBILE_PACKAGE_SIZE_STEP = 9000;
	
	public	static final int  	MSG_UPDATE_DATA		= 200;		//更新界面消息
	public	static final int  	MSG_RET_ERROR		= 201;		//
	public	static final int  	MSG_LOCK			= 202;		//超时锁屏
	public	static final int  	MSG_TIMEOUT			= 203;		//请求超时
	public	static final int  	MSG_DISCONNECT		= 204;		//断线
	
	public	static final int  	MSG_ADAPTER_CD_BUTTON_CLICK	= 210;
	public	static final int  	MSG_STOCKBOARD_CLICK		= 211;
	public	static final int  	MSG_DELAY_CLOSEACTIVITY		= 212;
	public	static final int  	MSG_ADAPTER_KJFS_BUTTON_CLICK	= 213;
	public  static final int    MSG_ADAPTER_CC_SELECT_CLICK = 214;
	

	public  static final int    MSG_ADAPTER_CC_BUY_BUTTON_CLICK = 310;
	public  static final int    MSG_ADAPTER_CC_SELL_BUTTON_CLICK = 311;
	public  static final int    MSG_ADAPTER_CC_HANGQING_BUTTON_CLICK = 312;
	//心跳包定时
	private static final long	HEART_TIMER			= 60000;
	private Timer 	mTimer;
	
	//请求超时
	private static final long	REQUEST_TIMEOUT		= 30;	//秒
	private Timer 				mTimeoutTimer;
	private int					mRequestChildID		= 0;
	private boolean				mIsProceeded		= false;
	
	//Socket连接超时
	private static final long	REQUEST_CONNECT_TIMEOUT		= 10;	//秒
	private Timer 				mConnectTimeoutTimer;
	private boolean				mIsProceeded_Connect		= false;
	
	
	//超时锁屏
//	private static final long	LOCK_TIMEOUT_UNIT	= 60;	//秒
	private Timer 				mLockTimer;
		
	private int		mFunc;
	private int		mMsgId;
	//private Object	mMsgObject;
	private CMessageObject mMsgObject;
	
	private boolean	isUpdate;
	
	//
	private String				mAddr[];			//存储服务器地址,地址格式为"192.168.3.72:50"
	private int					mAddrNum;
	private String				mAddrConnect[];		//当前正在连的地址列表
	private int					mAddrConnectNum;
	
	ByteBuffer					mSendByteBuffer;
	private String				mAddrSuccess;		//连接成功的地址
	private Object mLock;
	

	private class ConnectThread extends Thread
	{
		public ConnectThread()
		{

		}
		
		@Override
		public void run()
		{
			L.i("Trade", "ConnectThread run");
			if (mSendThread==null || mSendThread.mRun == false)
			{
				L.e("Trade", "ConnectThread--->NetSendThread==null");
				mSendThread	= new NetSendThread();
				mSendThread.mRun	= true;
				mSendThread.start();
				StartHeartTimer();
			}	
			else
			{
				L.e("Trade", "ConnectThread--->NetSendThread existed");
				mSendThread.closeNetThread(); 
				mSendThread.mRun	= true;
				mSendThread.resetSendSize();
			}
			
			if (!IsConnected()) 
			{
				L.i("Trade", "run--->IsConnected() = " + IsConnected());

				NetConnect	net = new NetConnect();
				net.setAddr(mMyApp.mTrade_Address, mMyApp.mTrade_AddrNum);

				tagConnectInfo	info = new tagConnectInfo();
				
				boolean ret = net.getSocketChannel(info, 20*1000);
				
				net.clear();
				net	= null;
				if (!ret || info.socket == null)
				{
					L.e("Trade", "Connect Failed!");
					L.w("Trade", "send MSG_DISCONNECT");
				    synchronized(mLock)
				    {
					   if (mMainHandle!=null)
					   {
						   Message Msg = mMainHandle.obtainMessage(MSG_DISCONNECT, 0, 0, 0);
						   mMainHandle.sendMessage(Msg);
					   }
				    }
	    			if (mMyApp.mTradeData.mTradeLoginFlag)
        			{
        				mMyApp.mTradeData.mTradeLoginFlag = false;
        			}
	    			
	    			mMyApp.resetAddrNum_Trade();
	    			closeConnect();
	    			
       				return;
        		}
				
				if (info.index >= 0 && info.index < mMyApp.mTrade_AddrNum)
				{
					if (mMyApp.mTrade_Address[info.index] != null)
					{
						mAddrSuccess = new String(mMyApp.mTrade_Address[info.index]);
					}else
					{
						mAddrSuccess = "";
					}
				}else
				{
					mAddrSuccess = "";
				}
				L.i("TradeNetConnect", "Connect Success address:" + mAddrSuccess);
				deleteAddrWithIndex(info.index);
				synchronized (this)
				{
					mSendThread.mSocketChannel	= info.socket;
				}
        	}
			else
			{
				L.i("Trade", "run--->IsConnected() = " + IsConnected());
			}
			

			
			//交易连接请求
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			mMyApp.mTradeNet.Request_Connect(mMyApp.mTradeData.mTradeVersion);
			
			L.i("Trade", "ConnectThread end");
		}
	}
	
	public void setAddr(String addr[], int num)
	{
		mAddrNum	= 0;
		for (int i = 0; i < num; i++)
		{
			String t = addr[i];
			if (t == null || t.length() < 1)
			{
				continue;
			}
			char c = t.charAt(0);
			if (c >= '0' && c <= '9')
			{
				if (mAddrNum < mAddr.length)
				{
					mAddr[mAddrNum]= t;
					mAddrNum++;
				}
			}
		}
		for (int i = 0; i < num; i++)
		{
			String t = addr[i];
			if (t == null || t.length() < 1)
			{
				continue;
			}
			char c = t.charAt(0);
			if (c < '0' || c > '9')
			{
				if (mAddrNum < mAddr.length)
				{
					mAddr[mAddrNum]= t;
					mAddrNum++;
				}
			}
		}
		for (int i = 0; i < mAddrNum; i++)
		{
			mAddrConnect[i]	= mAddr[i];
		}
		mAddrConnectNum	= mAddrNum;
	}
	
	public String getSuccessAddr()
	{
		return mAddrSuccess;
	}
	
	private void deleteAddrWithIndex(int index)
	{
		if (index < 0 || index >= mAddrConnectNum)
		{
			return;
		}
		for (int i = index+1; i < mAddrConnectNum; i++)
		{
			mAddrConnect[i-1]	= mAddrConnect[i];
		}
		mAddrConnectNum--;
	}
	private void resetConnectAddr()
	{
		for (int i = 0; i < mAddrNum; i++)
		{
			mAddrConnect[i]	= mAddr[i];
		}
		mAddrConnectNum	= mAddrNum;
	}
	
	public TradeNetConnect(MyApp app) 
	{
		mMyApp	= app;
				
//		mIP		= "125.215.195.211";//"125.215.195.211";//"222.68.185.166";//"192.168.3.165";	//"114.80.215.170";//"192.168.3.154";//"222.68.185.166";
//		mPort	= 20001;//20001;//21000;		
//		initSocketThread();
		mMsgObject = new CMessageObject();
		mSendByteBuffer	= ByteBuffer.allocateDirect(TradeNetConnect.MAX_MOBILE_PACKAGE_SIZE+100);
		
		mAddr			= new String[10];
		mAddrNum		= 0;
		
		mAddrConnect	= new String[10];
		mAddrConnectNum	= 0;
		mAddrSuccess = new String();
		mLock = new Object();
	}
	
	public void initSocketThread()
	{
		// start the connect thread
//		if (connectThread==null)
		{
			ConnectThread connectThread = new ConnectThread();
			connectThread.start();
		}
	}
	
	public void setMainHandler(Handler handler)
	{
		synchronized(mLock)
		{
		     this.mMainHandle	= handler;
		}
	}
	public Handler getHandler() {
		return this.mMainHandle;
	}
	
	//
	public void setAddress(String addr) 
	{
		mIP = STD.GetValue(addr, 1, ':');
		String tmp = STD.GetValue(addr, 2, ':');
		String port=STD.GetValue(tmp, 1, '|');
		mPort = Integer.parseInt(port);
	}
	
	//
	synchronized public void closeConnect() 
	{
		L.i("Trade", "closeConnect!");
		if (mSendThread != null)
		{
			mSendThread.closeNetThread();
			mSendThread	= null;
		}
		
		// kill timer
		if(mTimer != null) 
			mTimer.cancel();
		
		if(mLockTimer != null) 
			mLockTimer.cancel();
		
		if(mTimeoutTimer != null)
			mTimeoutTimer.cancel();
		
		if(mConnectTimeoutTimer != null)
			mConnectTimeoutTimer.cancel();
		
	}
	
	//是否已连接
	public boolean IsConnected() {
		if(mSendThread != null) {
			if(mSendThread.isConnected())
				return true;
		}
		return false;
	}
	
	/**
	 * 解析协议数据
	 * @param data
	 * @param size
	 * @return：返回0表示不满一个包，返回负数表示有错误，返回正数表示处理的字节数
	 */
	protected	int		decode(byte[] data, int size,CMessageObject msgObject)
	{
		msgObject.nErrorCode = 0;
		if (size < Trade_FrameHead_LEN)
		{
			L.e("Trade", "size < Trade_FrameHead_LEN!");
			return 0;
		}
		TradeFrameHead head	= new TradeFrameHead();
		int ret = CheckData(data, size, head);
		if (ret <= 0)
		{
			if(ret<0)
			L.e("Trade", "CheckData Error!!! " + ret);
			return ret;
		}
		
		int packagesize	= head.PackageSize;
		//解密
		int datasize	= packagesize;
        byte []szSrc = new byte[datasize+50];
		//拷贝数据。包头数据不压缩不加密
        System.arraycopy(data, Trade_FrameHead_LEN, szSrc, 0, datasize);

		if (head.crypt == 1)
		{
			byte []szDst = new byte[datasize+50];
			datasize = (int)SSLEncrypt.DesNcbcEncrypt(SSLEncrypt.SSLKEY_INDEX_TRADE,szSrc, szDst, datasize, 0);

			if (datasize < 0)
			{
				//解密错误
				L.e("Trade", "Decrypt Error!!! "+datasize);
				return datasize;
			}
			System.arraycopy(szDst, 0, szSrc, 0, szSrc.length);
			szDst = null;
		}
		
		byte cFlag = MyByteBuffer.getByte(szSrc, 0);
		head.zip = (byte)(cFlag&0x03);
		head.zipdatasize = (int)MyByteBuffer.getUnsignedShort(szSrc, 1);
		System.arraycopy(szSrc, 3, szSrc, 0, szSrc.length-3);
		datasize -= 3;
		//解压
		byte[]	expanddata	= new byte[1024*64];
		if (head.zip == 1)
		{
			datasize = ZLibTools.decompress(szSrc,datasize,expanddata);
			if (datasize <= 0)
			{
				L.e("Trade", "ExpandBuf Error!!!");
				expanddata = null;
				return -12;
			}
		}
		else
		{
			System.arraycopy(szSrc, 0, expanddata, 0, datasize);
		}
		szSrc = null;
		
		short sCheckCode = MyByteBuffer.getShort(expanddata, 0);
		short sCheckCode2 = GetCheckCode(expanddata,2,datasize-2);
		//返回错误
		if(sCheckCode != sCheckCode2) {
			L.e("Trade", "GetCheckCode Error,sCheckCode="+sCheckCode+",sCheckCode2="+sCheckCode2);
			return -2;
		}
		
		isUpdate	= false;
		
		if(head.PackageNo >=0 && head.PackageNo <= head.PackageNum - 1)
		{
			if(head.PackageNo == 0)
			{
				msgObject.clearData();
			}

			msgObject.setData(expanddata,2,datasize-2);
			if(head.PackageNo < head.PackageNum - 1)//未接受完完整包
			{
				expanddata = null;
				return packagesize+Trade_FrameHead_LEN;
			}
			isUpdate = true;
		}
		
		PBSTEP aStep = new PBSTEP();
		String content = new String(msgObject.getData());
		aStep.SetPackage(content.toCharArray(), msgObject.getDataLength());
		
		aStep.logContent();
		
		mFunc = aStep.GetBaseRecFieldValueINT(STEP_Define.STEP_FUNC);
		mRequestCode = aStep.GetBaseRecFieldValueINT(STEP_Define.STEP_REQUESTNO);
		int nErrorCode = aStep.GetBaseRecFieldValueINT(STEP_Define.STEP_CODE);
		msgObject.nErrorCode = nErrorCode;
		if(nErrorCode < 0)
		{
			msgObject.errorMsg = aStep.GetBaseRecFieldValueString(STEP_Define.STEP_MSG) ;
			L.e("Trade", "ERROR:[" + mFunc + "], aErrorCode == " + msgObject.nErrorCode + ",errormsg:"+ msgObject.errorMsg);
		}
		mMsgId = MSG_UPDATE_DATA;
	
		L.w("Trade", "receive -- Func: "+ mFunc);
		
		if (mFunc == Trade_Define.Func_HeartBeat)	//心跳包
        {
			L.e("Trade", "Receive heartbeat from trade server");
			isUpdate	= false;
        }
        else if (mFunc == Trade_Define.Func_Connect)	//交换密钥
        {
            String szKey = aStep.GetFieldValueString(STEP_Define.STEP_TXMY);
            setRequestDksKey(szKey);
            isUpdate	= true;
        }
        else if (mFunc == Trade_Define.Func_Login)	//登录
        {
			if (nErrorCode >= 0) {

				mMyApp.mTradeData.mReconnected = false;
				mMyApp.mTradeData.zjzh = aStep
						.GetBaseRecFieldValueString(STEP_Define.STEP_ZJZH);
				mMyApp.mTradeData.session = aStep
						.GetBaseRecFieldValueString(STEP_Define.STEP_SESSION);
				setPasswordDksKey(mMyApp.mTradeData.zjzh);

				// 查询股东帐号
				Request_ListQuery(Trade_Define.Func_GDZH, null);
				
				// 确认结账单
				String param = "";
				Calendar cal = Calendar.getInstance(); 
				int EndYear = cal.get(Calendar.YEAR);    
				int EndMonth = cal.get(Calendar.MONTH) + 1;  
				int EndDay = cal.get(Calendar.DATE);
				String strM = (EndMonth < 10) ? ("0"+EndMonth) : (""+EndMonth);
				String strD = (EndDay < 10) ? ("0"+EndDay) : (""+EndDay);
				String EndDate = String.format("%d%s%s", EndYear, strM, strD);
				
				param = String.format("%d:%s", STEP_Define.STEP_JYRQ, EndDate);
				Request_ListQuery(Trade_Define.Func_QRJZD, param);
				
				if (mMyApp.mTradeData.m_stepOptionList.GetRecNum() == 0) {
					Request_ListQuery(Trade_Define.Func_HYLB, null);// 查合约列表
				}
            }
        }
        else if (mFunc == Trade_Define.Func_Money)//资金查询
        {
            if (nErrorCode >= 0)
            {
                mMyApp.mTradeData.SetMoney(aStep);
            }
        }
        else if (mFunc == Trade_Define.Func_DRCJ)//当日成交
        {
            if (nErrorCode >= 0)
            {
                mMyApp.mTradeData.SetDRCJ(aStep);
            }
        }
        else if (mFunc == Trade_Define.Func_HOLDSTOCK)//持仓
        {
            if (nErrorCode >= 0)
            {
                mMyApp.mTradeData.SetHoldStock(aStep);
            }
        }
        else if (mFunc == Trade_Define.Func_DRWT)//当日委托
        {
            if (nErrorCode >= 0)
            {
                mMyApp.mTradeData.SetDRWT(aStep);
            }
        }
        else if (mFunc == Trade_Define.Func_LSWT)//历史委托
        {
            if (nErrorCode >= 0)
            {
                mMyApp.mTradeData.SetLSWT(aStep);
            }
        }
        else if (mFunc == Trade_Define.Func_LSCJ)//历史成交
        {
            if (nErrorCode >= 0)
            {
                mMyApp.mTradeData.SetLSCJ(aStep);
            }
        }
        else if (mFunc == Trade_Define.Func_Push_DRCJ)//推送当日成交
        {
//            name = NOTIFICATIONNANE_TRADE_PUSH_UPDATE;
//            if (nErrorCode >= 0)
//            {
//            }
        }
        else if (mFunc == Trade_Define.Func_Push_HOLDSTOCK)//推送持仓
        {
//            name = NOTIFICATIONNANE_TRADE_PUSH_UPDATE;
//            if (nErrorCode >= 0)
//            {
//            }
        }
        else if (mFunc == Trade_Define.Func_Push_DRWT)//推送当日委托
        {
//            name = NOTIFICATIONNANE_TRADE_PUSH_UPDATE;
//            if (nErrorCode >= 0)
//            {
//            }
        }else if(mFunc == Trade_Define.Func_CJHB){
        	 if (nErrorCode >= 0)
             {
                 mMyApp.mTradeData.SetCJHB(aStep);
             }
        }
        else if (mFunc == Trade_Define.Func_HYLB)//查合约列表
        {
        	mMyApp.mTradeData.SetOptionList(aStep);
            isUpdate	= false;
        }
        else if (mFunc == 6200 || mFunc == 6201 || mFunc == 6202 || mFunc == 6203 || mFunc == 6205)//银证转账
        {
        	if (nErrorCode >= 0)
        	{
        		mMyApp.mTradeData.SetYZZZ(aStep);
        	}
        }
        else if (mFunc == Trade_Define.Func_WT)//委托下单
        {
        	if (nErrorCode >= 0)
            {
                mMyApp.mTradeData.SetWT(aStep);
            }
        }
        else if (mFunc == Trade_Define.Func_WTCD)//委托撤单
        {
            
        }
        else if (mFunc == Trade_Define.Func_XGMM)//修改密码
        {
            
        }
        else if (mFunc == Trade_Define.Func_GDZH)//股东帐号
        {
        	mMyApp.mTradeData.SetGDZH(aStep);
        }
        else if (mFunc == Trade_Define.Func_KMSL)//可买数量
        {
        	if (nErrorCode >= 0)
            {
                mMyApp.mTradeData.SetKMSL(aStep);
            }
        }
        else if (mFunc == Trade_Define.Func_XQ)//行权下单
        {
        	if (nErrorCode >= 0)
            {
                mMyApp.mTradeData.SetXQWT(aStep);
            }
        }
        else if (mFunc == Trade_Define.Func_KXQSL)//查询可行权数量
        {
        	if (nErrorCode >= 0)
            {
                mMyApp.mTradeData.SetKXQSL(aStep);
            }
        }
        else if (mFunc == Trade_Define.Func_XQCD)//行权撤销
        {
        }
        else if (mFunc == Trade_Define.Func_FJYWTCX)//查询非交易委托
        {
        	if (nErrorCode >= 0)
            {
                mMyApp.mTradeData.SetFJYWT(aStep);
            }
        }
        else if (mFunc == Trade_Define.Func_XQZP)//行权指派查询
        {
        	if (nErrorCode >= 0)
            {
                mMyApp.mTradeData.SetXQZP(aStep);
            }
        }
        else if (mFunc == Trade_Define.Func_LSXQZP)//历史行权指派查询
        {
        	if (nErrorCode >= 0)
            {
                mMyApp.mTradeData.SetXQLSZP(aStep);
            }
        }
        else if (mFunc == Trade_Define.Func_BDZQCCCX)//备兑持仓查询
        {
        	if (nErrorCode >= 0)
            {
                mMyApp.mTradeData.SetBDCC(aStep);
            }
        }
        else if (mFunc == Trade_Define.Func_BDKDJJDSL)//备兑可冻结解冻数量查询
        {
        	if (nErrorCode >= 0)
            {
				String sdfx = aStep.GetFieldValueString(STEP_Define.STEP_SDFX);
				
        		if (sdfx.equals(String.format("%c", PTK_Define.PTK_FJYLB_SD))) {
        			mMyApp.mTradeData.SetBDKDJSL(aStep);
				} else if (sdfx.equals(String.format("%c", PTK_Define.PTK_FJYLB_JS))) {
					mMyApp.mTradeData.SetBDKJDSL(aStep);
				}else
				{
					mMyApp.mTradeData.SetBDKDJSL(aStep);
				}
            }
        }
        else if (mFunc == Trade_Define.Func_BDDJJD)//备兑冻结解冻
        {
        	if (nErrorCode >= 0)
            {
                mMyApp.mTradeData.SetWT(aStep);
            }
        }
        else if (mFunc == Trade_Define.Func_ZCSJHM)//注册手机号码
        {
        }
        else if (mFunc == Trade_Define.Func_YZSJZCM)//验证手机注册码
        {
        }
        else if (mFunc == Trade_Define.Func_Push_HBBZ)//推送委托回报，成交回报，撤单回报
        {

        }
        else if (mFunc == Trade_Define.Func_LSZJLS)//查询历史资金流水
        {
        	if (nErrorCode >= 0)
            {
                mMyApp.mTradeData.SetLSZJLS(aStep);
            }
        }
		
		return packagesize+Trade_FrameHead_LEN;
	}
	/**
	 * 检测数据是否正确
	 * 
	 * @param data
	 * @param size
	 * @param head
	 *            ，返回的头
	 * @return：大于0时表示是个完整包，小于0时数据有错误，0时还不是完整包
	 */
	public static int CheckData(byte[] data, int size, TradeFrameHead head) {
		if (size <= 0 || head == null) {
			return 0;
		}
//typedef struct
//{
//			BYTE							sign;			//协议标识，目前定义为66
//			BYTE							crypt:2;		//0表示未加密，1表示数据加密了
//			BYTE							unused:6;		//未使用
//			BYTE							PackageNum;		//通讯包总数
//			BYTE							PackageNo;		//当前通讯包序号，从0开始，当为PackageNum-1时表示是最后一个通讯包
//			UINT16							CheckCode;		//校验码（对通讯包体进行CRC16校验，在加密压缩后）
//			UINT16							PackageSize;	//包体的长度，加密压缩后
//}PACKED PB_FrameHead;	//8byte
		head.sign = (short)MyByteBuffer.getUnsignedByte(data, 0);

		if (size < Trade_FrameHead_LEN) // 还不是一个完整包
		{
			return -2;
		}

		{
			byte cFlag = MyByteBuffer.getByte(data, 1);
			head.crypt = (byte)(cFlag&0x03);
			
			head.PackageNum = (short)MyByteBuffer.getUnsignedByte(data, 2);
			head.PackageNo = (short)MyByteBuffer.getUnsignedByte(data, 3);
			head.CheckCode = (int)MyByteBuffer.getUnsignedShort(data, 4);
			head.PackageSize = (int)MyByteBuffer.getUnsignedShort(data, 6);
		}

		int packsize = head.PackageSize + Trade_FrameHead_LEN;

		if (size < packsize) // 未满一个包
		{
			return 0;
		}

		return 1;
	}
	 public short GetCheckCode(byte[] data,int offset, int size)
	{
		short retvalue = 0;

		int flag = size & 1;
		int len = size - flag; // 最后一位清零，相当于变成偶数，为奇数时减一
		short value = 0;

		while (len > 0) {
			value = MyByteBuffer.getShort(data, offset);
			offset += 2;
			retvalue += value;
			len -= 2;
		}

		if (flag != 0) {
			value = (short) (((int) data[offset]) & 0xff);
			retvalue += value;
		}

		return retvalue;
	}
	public void saveConfigFile(String fileName, byte[] content)
	{
		L.d("Trade", "saveConfigFile--->fileName = " + fileName);
		FileOutputStream fos = null;
		try {
			//其他包程序无法访问
			fos = mMyApp.getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			L.e("Trade", "saveConfigFile--->openFileOutput");
			return;
		}  
		
		String res = EncodingUtils.getString(content, "UNICODE");
		try {
			fos.write(0xFEFF);
			fos.write(res.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			L.e("Trade", "saveConfigFile--->write");
			return;
		}                                
		try {
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			L.e("Trade", "saveConfigFile--->close");
			return;
		}
		
		
		boolean bTest = false;
		if (bTest)
		{
			// 写文件
	        File file = new File(Environment.getExternalStorageDirectory() + File.separator + fileName);
	        if (!file.exists()) {
	            // 文件不存在、 Just创建
	            try {
	                file.createNewFile();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }

	        OutputStreamWriter osw = null;
	        try {
	            osw = new OutputStreamWriter(new FileOutputStream(
	                    file));
	        } catch (FileNotFoundException e1) {
	            e1.printStackTrace();
	        }
	        
	        try {
	        	osw.write(0xFEFF);
	            osw.write(res);
	            osw.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}
	
	//
	public int addSendData(byte[] data, int offset, int size)
	{	
//		initSocketThread();
		if(mSendThread != null)
		{
			return mSendThread.addSendData(data, offset, size);
		}
		return -1;
	}
	
	private void StartHeartTimer() 
	{
		if(mTimer != null) {
			mTimer.cancel();
		}
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			//
		    public void run() {
		    	   //发送心跳包
		    		Request_HeartBeat();
		    	   
		    	   L.w("Trade", "Send Heart");
		    }
		}, HEART_TIMER, HEART_TIMER);
	}
	
	
	public int procLock()
	{
		L.w("Trade", "procLock");
		if(mLockTimer != null) 
		{
			mLockTimer.cancel();
		}
		L.w("Trade", "mTradeLockTimeout========" + mMyApp.mTradeData.mTradeLockTimeout);
		mLockTimer = new Timer();
		mLockTimer.schedule(new TimerTask() 
		{
			//
		    public void run() 
		    {
		    	this.cancel();

				//发送超时锁屏消息
				if (mMainHandle!=null)
				{	
					L.w("Trade", "send MSG_LOCK, timeval = " + mMyApp.mTradeData.mTradeLockTimeout);
					Message Msg = mMainHandle.obtainMessage(MSG_LOCK, 0, 0, 0);
	                mMainHandle.sendMessage(Msg);
				}
				if (mMyApp.mTradeData.mTradeLoginFlag)
				{
					mMyApp.mTradeData.quitTrade();
				}
		    }
		}, mMyApp.mTradeData.mTradeLockTimeout*60*1000, mMyApp.mTradeData.mTradeLockTimeout*60*1000);	    
		
		return 0;
	}
	
	private int procTimeout()
	{
		L.w("Trade", "procTimeout");
		if(mTimeoutTimer != null)
		{
			mTimeoutTimer.cancel();
		}

		mTimeoutTimer = new Timer();
		mTimeoutTimer.schedule(new TimerTask() 
		{
			//
		    public void run() 
		    {
		    	this.cancel();

		    	if (mIsProceeded)
		    	{
		    		L.w("Trade", "procTimeout->mIsProceeded");
		    		return;
		    	}

				//发送超时消息
				if (mMainHandle!=null)
				{	
					L.w("Trade", "MSG_TIMEOUT");
					String ErrMsg = new String("请求超时,请重新登录！");
					Object MsgObject = ErrMsg;
					Message Msg = mMainHandle.obtainMessage(MSG_TIMEOUT, mRequestChildID, 0, MsgObject);
	                mMainHandle.sendMessage(Msg);
				}
				mMyApp.mTradeData.quitTrade();
		    }
		}, REQUEST_TIMEOUT*1000, REQUEST_TIMEOUT*1000);
		
		return 0;
	}
	
	private int procConnectTimeout()
	{
		L.w("Trade", "procConnectTimeout");
		if(mConnectTimeoutTimer != null)
		{
			mConnectTimeoutTimer.cancel();
		}
		
		mConnectTimeoutTimer = new Timer();
		mConnectTimeoutTimer.schedule(new TimerTask() 
		{
			//
		    public void run() 
		    {
		    	this.cancel();
		    	
		    	if (mIsProceeded_Connect)
		    	{
		    		L.w("Trade", "procConnectTimeout->mIsProceeded_Connect");
		    		return;
		    	}
		    }
		}, REQUEST_CONNECT_TIMEOUT*1000, REQUEST_CONNECT_TIMEOUT*1000);
		
		return 0;
	}

	/**
	 * 网络发送线程
	 * @author Administrator
	 */
	public class NetSendThread extends Thread {
		private	boolean				mRun;
		private byte[]				mSendData;
		private int					mSendSize;
		
		private byte[]				mReadData;
		private int					mReadSize;
		
		private	Message 			mMsg;
		
		SocketChannel				mSocketChannel;
		private	long				mLastNetDataTime;	//最后处理网络数据的时间
		private	long				mLastSessionTime;	//会话最后时间，即客户最后操作时间。
		private long				mLastRequestTime;	//最后请求时间，-1表示已有应答
		
		
		private SocketChannel		mSocketChannels[];
		
		synchronized	public	boolean isConnected()
		{
			if (mSocketChannel != null && mSocketChannel.isConnected())
			{
				return true;
			}
			return false;
		}
		public NetSendThread() 
		{
			mRun	= true;
			
			mSendData		= new byte[TradeNetConnect.MAX_MOBILE_PACKAGE_SIZE+100];
			mSendSize		= 0;
			
			mReadData		= new byte[TradeNetConnect.MAX_MOBILE_PACKAGE_SIZE*5+200];
			mReadSize		= 0;
			
			mLastNetDataTime	= System.currentTimeMillis();
			mLastSessionTime	= System.currentTimeMillis();
			mLastRequestTime	= -1;
			
			mSocketChannels	= new SocketChannel[10];
		}
		
		synchronized	private void closeNetThread() 
		{
			L.i("Trade", "NetSendThread--->closeNetThread 1");
			
			//结束心跳包timer
			if(mTimer != null) {
				mTimer.cancel();
			}
			
			mRun			= false;
			mSessionID		= 0;
			mRequestCode	= 0;

			if(mSocketChannel == null)
				return;
			L.i("Trade", "NetSendThread--->closeNetThread 2");
			{
				mLastNetDataTime	= System.currentTimeMillis();
				mLastSessionTime	= System.currentTimeMillis();
				mLastRequestTime	= -1;
				
				mRun		= false;
				mSendSize	= 0;
				mReadSize	= 0;
				mSendByteBuffer.clear();
			}
			{
				try {
					mSocketChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mSocketChannel	= null;
			}
		}
		
		public void resetSendSize()
		{
			mSendSize = 0;
		}
		
		public int addSendData(byte[] data, int offset, int size)
		{
			if (size + mSendSize > mSendData.length)	//缓冲不够
			{
				L.e("Trade", "addSendData->buffer is not enough!");
				return -1;
			}
			synchronized (this){
				System.arraycopy(data, offset, mSendData, mSendSize, size);
				mSendSize	+= size;
			}
			return mSendSize;
		}
        @Override
        public void run() {
        	boolean		sleepflag = false;
            while (mRun)
            {
            	sleepflag	= true;
            	
            	if (mSocketChannel != null) {
            		//发送数据
	            	if (mSendSize > 0) {
		            	{
		            		L.w("Trade", "send: sendsize = " + mSendSize);

		            		synchronized (this) 
		            		{
		            			mSendByteBuffer.clear();
		            			mSendByteBuffer.put(mSendData, 0, mSendSize);
		            			mSendByteBuffer.flip();
		            		}
		            		
		            		synchronized (this) 
		            		{
		            			int sendResult = 0;
			            		try 
			            		{
			            			sendResult = mSocketChannel.write(mSendByteBuffer);
								}
			            		catch (Exception e) 
								{
									e.printStackTrace();

									L.e("Trade", "send data IOException...");
									if (mMainHandle!=null)
									{
										Message Msg = mMainHandle.obtainMessage(MSG_DISCONNECT, 0, 0, 0);
										mMainHandle.sendMessage(Msg);
									}
		        	    			if (mMyApp.mTradeData.mTradeLoginFlag)
		                			{
		                				mMyApp.mTradeData.mTradeLoginFlag = false;
		                			}
		        					mMyApp.mTradeData.quitTrade();
									break;
								}
								mSendSize	= 0;
		            		}
		            	} 

		            	sleepflag	= false;
	            	}
            	
            	//接收数据
            	try 
            	{
            		int size = 0;
            		synchronized (this)
            		{
	            		mSendByteBuffer.clear();
	            		try 
	            		{
		            		size = mSocketChannel.read(mSendByteBuffer);
						} 
	            		catch (Exception e) 
						{
							e.printStackTrace();
							L.e("Trade", "receive data IOException...");

							L.w("Trade", "send MSG_DISCONNECT");
							if (mMainHandle!=null)
							{
								Message Msg = mMainHandle.obtainMessage(MSG_DISCONNECT, 0, 0, 0);
								mMainHandle.sendMessage(Msg);
							}
        	    			if (mMyApp.mTradeData.mTradeLoginFlag)
                			{
                				mMyApp.mTradeData.mTradeLoginFlag = false;
                			}
        	    			mMyApp.mTradeData.quitTrade();
							break;
						}
	            		mSendByteBuffer.flip();
            		}
            		
            		if (size < 0)
            		{
						L.e("Trade", "readsize:"+size);
						if (mMainHandle!=null)
						{
							Message Msg = mMainHandle.obtainMessage(MSG_DISCONNECT, 0, 0, 0);
							mMainHandle.sendMessage(Msg);
						}
    	    			if (mMyApp.mTradeData.mTradeLoginFlag)
            			{
            				mMyApp.mTradeData.mTradeLoginFlag = false;
            			}
    	    			mMyApp.mTradeData.quitTrade();
            			break;
            		}
            		else if (size > 0)
            		{
            			mLastRequestTime	= -1;

            			if(mMainHandle == null) {

            			}
            			sleepflag	= false;
						L.w("Trade", "readsize:"+size);

						if (size + mReadSize > mReadData.length)	//数据有误
						{
							L.e("Trade", "数据有误..." + "size = " + size
									+ ", mReadSize = " + mReadSize
									+ ", mReadData.length = " + mReadData.length);
		            		
		            		if (mMainHandle!=null)
		    				{	
		            			Message Msg = mMainHandle.obtainMessage(MSG_DISCONNECT, 0, 0, 0);
		            			mMainHandle.sendMessage(Msg);
		            			if (mMyApp.mTradeData.mTradeLoginFlag)
		            			{
		            				mMyApp.mTradeData.mTradeLoginFlag = false;
		            			}
		    				}
		            		mMyApp.mTradeData.quitTrade();
							break;
						}
						mSendByteBuffer.get(mReadData, mReadSize, size);
						mReadSize	+= size;

						boolean quitflag = false;
               			while (mRun) 
               			{
            				isUpdate = false;
                			size	= decode(mReadData, mReadSize,mMsgObject);
                			mIsProceeded = true;
                			L.w("Trade", "decode size:"+mReadSize + "  ret:" + size);
                			if (size < 0) 	//解析数据错误，需关闭会话
                			{
                				L.e("Trade", "解析数据错误");

                				quitflag = true;
                				break;
							} 
                			else if (size > 0) 
							{
            					//发送更新消息
            					if (isUpdate && mMainHandle!=null) 
            					{	
            						CMessageObject msgObj = mMsgObject.clone();
            						mMsg = mMainHandle.obtainMessage(mMsgId, mFunc, mRequestCode, msgObj);
                	                mMainHandle.sendMessage(mMsg);
                				}
                				int left = mReadSize-size;
                       			if (left > 0) 
                       			{
									System.arraycopy(mReadData, size,
											mReadData, 0, left);
									mReadSize	= left;
								} 
                       			else 
                       			{
									mReadSize = 0;
									break;
								}
							} 
                			else 
							{ // 不是完整包
								break;
							}
						}
               			
               			//
               			sleepflag	= false;
               			
           				if (quitflag)
           				{		            		
		            		if (mMainHandle!=null)
		    				{	
		            			Message Msg = mMainHandle.obtainMessage(MSG_DISCONNECT, 0, 0, 0);
		            			mMainHandle.sendMessage(Msg);
		            			if (mMyApp.mTradeData.mTradeLoginFlag)
		            			{
		            				mMyApp.mTradeData.mTradeLoginFlag = false;
		            			}
		    				}
		            		mMyApp.mTradeData.quitTrade();
           					break;
           				}
					}
            		
           		}
            	catch(Exception e){
            		e.printStackTrace();
            		L.e("Trade", "recieve: " + e.getMessage());
            		
            		if (mMainHandle!=null)
    				{	
            			Message Msg = mMainHandle.obtainMessage(MSG_DISCONNECT, 0, 0, 0);
            			mMainHandle.sendMessage(Msg);
            			if (mMyApp.mTradeData.mTradeLoginFlag)
            			{
            				mMyApp.mTradeData.mTradeLoginFlag = false;
            			}
    				}
            		mMyApp.mTradeData.quitTrade();
            		}
            	}
            	if (sleepflag) {
            		try {
            			Thread.sleep(50);
            		} catch (InterruptedException e) {}
            	}

            }//while (mRun)
        }
	}
	
	//
	protected int	AddRequest(int maintype, int subtype)
	{
		L.w("Trade", "send -- M: " + maintype + " C: " + subtype);
		
		if (subtype != Trade_Define.Func_Connect && subtype != Trade_Define.Func_Login)
		{
			// 超时锁屏处理
			procLock();
		}
		
		// 超时处理
		mRequestChildID = subtype;
		mIsProceeded = false;
		procTimeout();

		
		byte[]	senddata	= new byte[2048];
		
		int headsize = Trade_FrameHead_LEN;
		int size = 0;//= dbf.MakePackage(0, 1, senddata, headsize, senddata.length - headsize);

		mRequestCode ++;
		
//		L.w("Trade", "AddRequest -> MakePackage: size = " + size);
		
		return addSendData(senddata, 0, size);
	}
	
	private static  int	mLastRequestCode = 0;	//请求号
	public static int	MakeRequestCode()
	{
		mLastRequestCode++;
		mLastRequestCode	&= 0x3fff;
		if (mLastRequestCode == 0)
		{
			mLastRequestCode	= 1;
		}
		return mLastRequestCode;
	}

	/**
	 * 设置用于除密钥交换意外的其他所有请求的 DES密钥
	 * key: 密钥
	 */
    public	void	setRequestDksKey(String key)
    {
    	if(key == null || key.length() == 0)return;
    	SSLEncrypt.DesSetKey(key, SSLEncrypt.SSLKEY_INDEX_TRADE);
    }
	/**
	 * 设置用于除登录以外密码加密的密钥
	 * key: 密钥
	 */
    public	void	setPasswordDksKey(String key)
    {
    	if(key == null || key.length() == 0)return;
    	SSLEncrypt.DesSetKey(key, SSLEncrypt.SSLKEY_INDEX_TRADE_PWD);
    }
	/**
	 * 请求交互密钥
	 * version: 交易版本号
	 */
	public	int	Request_Connect(String version)
	{
	    mLastRequestCode	= 0;
		int keylen = 16;
		char []keydata = new char[keylen+1];
		CDataEncrypt.CreateRandData(keydata, keylen);
		keydata[keylen] = 0;
//		String testKey = "pwwuc9md8m530nge";
//		setRequestDksKey(testKey);
		setRequestDksKey(new String(keydata,0,keylen));
		setPasswordDksKey(mMyApp.mTradeData.mTradeAccount);

		PBSTEP aStep = new PBSTEP();
	    int nRequestCode = CreateStepBaseRecord(aStep, 1);
	    
	    aStep.AppendRecord();
		aStep.AddFieldValueString(STEP_Define.STEP_TXMY, keydata);				//密钥

	    MakePackageAndSend(aStep, 1, SSLEncrypt.SSLKEY_INDEX_TRADE,512);
		return nRequestCode;
	}
	
	/**
	 * 请求登录 (交易密码加密问题，登陆的时候的交易密码使用登陆账号进行加密。登陆完成以后使用资金账号对所有密码进行加密)
	 * @param account		账号
	 * @param password		密码
	 * @param YYBDM 		营业部代码
	 * @param QSDM 		           券商代码
	 * @param version       客户端版本号
	 * @param accountType 	账号类型 
	 * @param safeType		动态密码类别
	 * @param safeCode		动态密码
	 * @param loginType     登录类别-0 普通沪深交易；5 融资融券交易；6 个股期权交易7：黄金交易8：期货交易9：商品期货交易
	 * @param sjhm
	 * 硬盘编号	70	STEP_YPBH
	 */
	public	int	Request_Login(String account, String password,int YYBDM, int QSDM, String version,String accountType, int safeType, String safeCode, int loginType, String sjhm)
	{
		PBSTEP aStep = new PBSTEP();
	    int nRequestCode = CreateStepBaseRecord(aStep, 6011);
	    aStep.SetBaseRecFieldValue_Encrypt(STEP_Define.STEP_JYMM, password, SSLEncrypt.SSLKEY_INDEX_TRADE_PWD);	//交易密码
	    
	    aStep.AppendRecord();
	    if (QSDM != 0) {
	        aStep.AddFieldValueINT(STEP_Define.STEP_QSDM, QSDM);		//券商代码
	    }
	    if (YYBDM != 0) {
	        aStep.AddFieldValueINT(STEP_Define.STEP_YYBDM, YYBDM);		//营业部代码
	    }
		aStep.AddFieldValueString(STEP_Define.STEP_ZHLB, accountType);	//帐号类别
		aStep.AddFieldValueINT(STEP_Define.STEP_DLLB, loginType);	//登录类别	0 普通沪深交易；5 融资融券交易；6 个股期权交易7：黄金交易8：期货交易9：商品期货交易

		aStep.AddFieldValueString(STEP_Define.STEP_DLZH,account);	//帐号
	    aStep.AddFieldValueString_Encrypt(STEP_Define.STEP_JYMM, password,SSLEncrypt.SSLKEY_INDEX_TRADE_PWD);//交易密码
	    aStep.AddFieldValueINT(STEP_Define.STEP_DTMMLB, safeType);  // 动态密码类别
	    aStep.AddFieldValueString(STEP_Define.STEP_DTMM, safeCode); // 动态密码
	    aStep.AddFieldValueString(STEP_Define.STEP_SJHM, sjhm); // 手机号码

		aStep.AddFieldValueString(STEP_Define.STEP_VERSION, version);// 客户端版本号
	    aStep.AddFieldValueString(STEP_Define.STEP_CP, "8"); //商品现货
	    aStep.AddFieldValueString(STEP_Define.STEP_PT, "3");//安卓平台
		
	    
		String ipAddr = mMyApp.getLocalIpAddress();
		if(ipAddr!=null) {
			aStep.AddFieldValueString(STEP_Define.STEP_LOCAL_IP, ipAddr);		//客户端IP地址
		}
		String macAddr = mMyApp.getLocalMacAddress();
		if(macAddr!=null) {
			aStep.AddFieldValueString(STEP_Define.STEP_LOCAL_MAC, macAddr);	//客户端的mac地址(网卡地址)
		}
		
		String IMEI = mMyApp.getIMEI();
		aStep.AddFieldValueString(STEP_Define.STEP_YPBH, IMEI);
		
	    MakePackageAndSend(aStep, 6011, SSLEncrypt.SSLKEY_INDEX_TRADE,512);
		return nRequestCode;
	}
	
	/**
	 * 交易登录
	 * @param account		账号
	 * @param password		密码
	 * @param YYBDM			营业部代码
	 * @param QSDM			券商代码
	 * @param version       客户端版本号
	 * @param accountType 	账号类型 
	 * @param sjhm
	 * @param dllb          登录类别 （0：普通沪深交易5：融资融券交易6：个股期权交易7：黄金交易8：期货交易9：商品期货交易）
	 */
	public	int	Request_Login(String account, String password,int YYBDM, int QSDM, String version,String accountType, String sjhm, int dllb)
	{
		return Request_Login(account, password, YYBDM, QSDM, version, accountType, 0, "", dllb, sjhm);
	}
	
	//委托下单
	//marketCode:交易市场
	//stockCode:证券代码(期权代码)
	//mmFlag:买卖类别
	//kpcFlag:开平仓标志
	//wtsl:委托数量
	//price:委托价格
	//gdzh:股东帐号
	//bdFlag:期权备兑标志 0:非备兑 1:备兑
	//sjType:市价委托类型 '0'或'\0'表示限价
	public int	Request_WT(String marketCode,String stockCode,char mmFlag, char kpcFlag,String wtsl,String price,String gdzh,String xwh,int bdFlag,char sjType)
	{  
		PBSTEP aStep = new PBSTEP();
	    int nRequestCode = CreateStepBaseRecord(aStep, 6021);
	    
	    aStep.AppendRecord();
	    aStep.AddFieldValueString(STEP_Define.STEP_GDH, gdzh);//股东帐号
	    aStep.AddFieldValueString(STEP_Define.STEP_XWH, xwh);//席位号
	    aStep.AddFieldValueString(STEP_Define.STEP_SCDM, marketCode);//市场代码
	    aStep.AddFieldValueString(STEP_Define.STEP_HYDM, stockCode);//证券代码(期权代码)
	    aStep.AddFieldValueString(STEP_Define.STEP_MMLB, mmFlag);//买卖类别
	    aStep.AddFieldValueString(STEP_Define.STEP_KPBZ, kpcFlag);//开平仓标志
	    aStep.AddFieldValueString(STEP_Define.STEP_WTSL, wtsl);//委托数量
	    aStep.AddFieldValueString(STEP_Define.STEP_WTJG, price);//委托价格
	    aStep.AddFieldValueINT(STEP_Define.STEP_BDBZ, bdFlag);//期权备兑标志
	    if (sjType != '0') {
	        aStep.AddFieldValueString(STEP_Define.STEP_SJWTLB, sjType);//市价委托类型
	    }
		
	    MakePackageAndSend(aStep, 6021, SSLEncrypt.SSLKEY_INDEX_TRADE,512);
		return nRequestCode;
	}

	//委托撤单
	//wtbh:委托编号
	//wtrq:委托日期
	//gdzh:股东帐号
	//market:交易市场
	//xwh:席位号
	//xdxw:下单席位
	public  int	Request_WTCD(String wtbh,String wtrq,String gdzh,String market,String xwh,String xdxw)
	{
		PBSTEP aStep = new PBSTEP();
	    int nRequestCode = CreateStepBaseRecord(aStep, 6022);
	    
	    aStep.AppendRecord();
	    aStep.AddFieldValueString(STEP_Define.STEP_WTBH, wtbh);//委托编号
	    aStep.AddFieldValueString(STEP_Define.STEP_WTRQ, wtrq);//委托日期
	    aStep.AddFieldValueString(STEP_Define.STEP_GDH, gdzh);//股东帐号
	    aStep.AddFieldValueString(STEP_Define.STEP_SCDM, market);//市场代码
	    aStep.AddFieldValueString(STEP_Define.STEP_XWH, xwh);//席位号
	    aStep.AddFieldValueString(STEP_Define.STEP_XDXW, xdxw);//下单席位
		
	    MakePackageAndSend(aStep, 6022, SSLEncrypt.SSLKEY_INDEX_TRADE,512);
		return nRequestCode;
	}

	//查询可买数量
	//marketCode:交易市场
	//stockCode:证券代码(期权代码)
	//mmFlag:买卖类别
	//kpcFlag:开平仓标志
	//price:委托价格
	//gdzh:股东帐号
	//bdFlag:期权备兑标志 0:非备兑 1:备兑
	//sjType:市价委托类型 '0'或'\0'表示限价
	public int	Request_KMSL(String marketCode,String stockCode,char mmFlag, char kpcFlag,String price,String gdzh,String xwh,int bdFlag, char sjType)
	{
		PBSTEP aStep = new PBSTEP();
	    int nRequestCode = CreateStepBaseRecord(aStep, 6044);
	    
	    aStep.AppendRecord();
	    aStep.AddFieldValueString(STEP_Define.STEP_GDH, gdzh);//股东帐号
	    aStep.AddFieldValueString(STEP_Define.STEP_XWH, xwh);//席位号
	    aStep.AddFieldValueString(STEP_Define.STEP_SCDM, marketCode);//市场代码
	    aStep.AddFieldValueString(STEP_Define.STEP_HYDM, stockCode);//证券代码(期权代码)
	    aStep.AddFieldValueString(STEP_Define.STEP_MMLB, mmFlag);//买卖类别
	    aStep.AddFieldValueString(STEP_Define.STEP_KPBZ, kpcFlag);//开平仓标志
	    aStep.AddFieldValueString(STEP_Define.STEP_WTJG, price);//委托价格
	    aStep.AddFieldValueINT(STEP_Define.STEP_BDBZ, bdFlag);//期权备兑标志
	    if (sjType != '0') {
	        aStep.AddFieldValueString(STEP_Define.STEP_SJWTLB, sjType);//市价委托类型
	    }
		
	    MakePackageAndSend(aStep, 6044,SSLEncrypt.SSLKEY_INDEX_TRADE,512);
		return nRequestCode;
	}
	//查询持仓
	public int Request_HoldStock(String marketCode,String stockCode)
	{
		PBSTEP aStep = new PBSTEP();
	    int nRequestCode = CreateStepBaseRecord(aStep, 6014);
	    
	    aStep.AppendRecord();
	    aStep.AddFieldValueString(STEP_Define.STEP_SCDM, marketCode);//市场代码
	    if (stockCode != null && stockCode.length() > 0) {
	        aStep.AddFieldValueString(STEP_Define.STEP_HYDM, stockCode);//证券代码
	    }
		
	    MakePackageAndSend(aStep, 6014,SSLEncrypt.SSLKEY_INDEX_TRADE,512);
		return nRequestCode;

	}

	//查询各类列表,比如当日委托，当日成交等
	public int	Request_ListQuery(int nFunc,String para)
	{
		PBSTEP aStep = new PBSTEP();
	    int nRequestCode = CreateStepBaseRecord(aStep, nFunc);
	    
	    aStep.AppendRecord();
	    if (para != null && para.length() > 0)
		{
			for (int i = 0; i < 10; i++)	//最多10个参数
			{
				String temp = STD.GetValue(para, i+1, '|');
				if (temp == null || temp.length() == 0)
				{
					break;
				}
	            
				int nId = STD.GetValueInt(temp, 1, ':');
				String szValue =  STD.GetValue(temp, 2, ':');
				aStep.AddFieldValueString(nId, szValue);
			}
		}
		
	    MakePackageAndSend(aStep, nFunc,SSLEncrypt.SSLKEY_INDEX_TRADE,512);
		return nRequestCode;
	}
	//心跳包
	public void Request_HeartBeat()
	{
	    PBSTEP aStep = new PBSTEP();
	    aStep.Init();
	    aStep.SetBaseRecFieldValueINT(STEP_Define.STEP_FUNC, 0);
	    
	    MakePackageAndSend(aStep, 0,SSLEncrypt.SSLKEY_INDEX_TRADE,512);
	}
	//请求修改密码
	//返回请求号
	public int	Request_XGMM(String OldPwd, String NewPwd, int mmlb)
	{
		PBSTEP aStep = new PBSTEP();
	    int nRequestCode = CreateStepBaseRecord(aStep, 6023);
	    
	    aStep.AppendRecord();
	    aStep.AddFieldValueString_Encrypt(STEP_Define.STEP_OLDMM, OldPwd, SSLEncrypt.SSLKEY_INDEX_TRADE_PWD);
	    aStep.AddFieldValueString_Encrypt(STEP_Define.STEP_NEWMM, NewPwd, SSLEncrypt.SSLKEY_INDEX_TRADE_PWD);
	    aStep.AddFieldValueINT(STEP_Define.STEP_PWDTYPE, mmlb);
	    

		
	    MakePackageAndSend(aStep, 6023,SSLEncrypt.SSLKEY_INDEX_TRADE,512);
		return nRequestCode;
	}


	////////////////////////////////////银证转帐///////////////////////////////////////////////

	//请求银证转帐互转
	//返回请求号
	//银行划到券商
	public int	Request_YZZZ_YZQ(String zjzh, String yhbm, String yhzh, String yhmm, String zjmm, String hbdm, String amount)
	{
		PBSTEP aStep = new PBSTEP();
	    int nRequestCode = CreateStepBaseRecord(aStep, 6202);
		
	    aStep.AppendRecord();
	    //资金帐号
	    if (zjzh != null && zjzh.length() > 0) {
	        aStep.AddFieldValueString(STEP_Define.STEP_ZJZH, zjzh);
	    }
	    else
	    {
	        aStep.AddFieldValueString(STEP_Define.STEP_ZJZH, mMyApp.mTradeData.zjzh);//资金帐号
	    }
		aStep.AddFieldValueString(STEP_Define.STEP_YHBM, yhbm);				//银行编码
		aStep.AddFieldValueString(STEP_Define.STEP_YHZH, yhzh);				//银行帐号
	    aStep.AddFieldValueString_Encrypt(STEP_Define.STEP_YHMM, yhmm, SSLEncrypt.SSLKEY_INDEX_TRADE_PWD);//银行密码
	    aStep.AddFieldValueString_Encrypt(STEP_Define.STEP_ZJMM, zjmm, SSLEncrypt.SSLKEY_INDEX_TRADE_PWD);//资金密码
		aStep.AddFieldValueString(STEP_Define.STEP_HBDM, hbdm);					//币种
	    aStep.AddFieldValueString(STEP_Define.STEP_ZZJE, amount);					//转账金额
		
	    MakePackageAndSend(aStep, 6202,SSLEncrypt.SSLKEY_INDEX_TRADE,512);
		return nRequestCode;
	}

	//请求银证转帐互转
	//返回请求号
	//券商划到银行
	public int	Request_YZZZ_QZY(String zjzh, String yhbm, String yhzh, String yhmm, String zjmm, String hbdm, String amount)
	{
		PBSTEP aStep = new PBSTEP();
	    int nRequestCode = CreateStepBaseRecord(aStep, 6201);
		
	    aStep.AppendRecord();
	    //资金帐号
	    if (zjzh != null && zjzh.length() > 0) {
	        aStep.AddFieldValueString(STEP_Define.STEP_ZJZH, zjzh);
	    }
	    else
	    {
	        aStep.AddFieldValueString(STEP_Define.STEP_ZJZH, mMyApp.mTradeData.zjzh);//资金帐号
	    }
		aStep.AddFieldValueString(STEP_Define.STEP_YHBM, yhbm);				//银行编码
		aStep.AddFieldValueString(STEP_Define.STEP_YHZH, yhzh);				//银行帐号
	    aStep.AddFieldValueString_Encrypt(STEP_Define.STEP_YHMM, yhmm, SSLEncrypt.SSLKEY_INDEX_TRADE_PWD);//银行密码
	    aStep.AddFieldValueString_Encrypt(STEP_Define.STEP_ZJMM, zjmm, SSLEncrypt.SSLKEY_INDEX_TRADE_PWD);//资金密码
		aStep.AddFieldValueString(STEP_Define.STEP_HBDM, hbdm);					//币种
	    aStep.AddFieldValueString(STEP_Define.STEP_ZZJE, amount);					//转账金额
		
	    MakePackageAndSend(aStep, 6201,SSLEncrypt.SSLKEY_INDEX_TRADE,512);
		return nRequestCode;
	}
	//查询银行余额
	//返回请求号
	public int	Request_YHYE(String zjzh, String yhbm, String yhzh,String yhmm,String zjmm,String hbdm)
	{
		PBSTEP aStep = new PBSTEP();
	    int nRequestCode = CreateStepBaseRecord(aStep, 6203);
		
	    aStep.AppendRecord();
	    //资金帐号
	    if (zjzh != null && zjzh.length() > 0) {
	        aStep.AddFieldValueString(STEP_Define.STEP_ZJZH, zjzh);
	    }
	    else
	    {
	        aStep.AddFieldValueString(STEP_Define.STEP_ZJZH, mMyApp.mTradeData.zjzh);//资金帐号
	    }
		aStep.AddFieldValueString(STEP_Define.STEP_YHBM, yhbm);				//银行编码
		aStep.AddFieldValueString(STEP_Define.STEP_YHZH, yhzh);				//银行帐号
	    aStep.AddFieldValueString_Encrypt(STEP_Define.STEP_YHMM, yhmm, SSLEncrypt.SSLKEY_INDEX_TRADE_PWD);//银行密码
	    aStep.AddFieldValueString_Encrypt(STEP_Define.STEP_ZJMM, zjmm, SSLEncrypt.SSLKEY_INDEX_TRADE_PWD);//资金密码
		aStep.AddFieldValueString(STEP_Define.STEP_HBDM, hbdm);					//币种
		
	    MakePackageAndSend(aStep, 6203,SSLEncrypt.SSLKEY_INDEX_TRADE,512);
		return nRequestCode;
	}
	/**
	 * 历史委托查询
	 * @param start		起始记录
	 * @param num		请求记录个数
	 * @param qsrq		起始日期
	 * @param zzrq		终止日期
	 */
	public	int	Request_QueryHistoryEntrust(int start, int num, String qsrq, String zzrq)
	{
		return 0;
	}
	
	// 行权
	// marketCode:交易市场
	// stockCode:证券代码(期权代码)
	// xqsl:行权数量
	// gdzh:股东帐号
	// xwh:席位号
	// sqType:非交易申请类型 1-执行2-放弃
	public int Request_XQ(String marketCode, String stockCode, String xqsl,
			String gdzh, String xwh, String sqType) {
		PBSTEP aStep = new PBSTEP();
		int nRequestCode = CreateStepBaseRecord(aStep, 6100);

		aStep.AppendRecord();
		aStep.AddFieldValueString(STEP_Define.STEP_GDH, gdzh);// 股东帐号
		aStep.AddFieldValueString(STEP_Define.STEP_XWH, xwh);// 席位号
		aStep.AddFieldValueString(STEP_Define.STEP_SCDM, marketCode);// 市场代码
		aStep.AddFieldValueString(STEP_Define.STEP_HYDM, stockCode);// 证券代码(期权代码)
		aStep.AddFieldValueString(STEP_Define.STEP_XQSL, xqsl);// 行权数量
		aStep.AddFieldValueString(STEP_Define.STEP_FJYSQLB, sqType);// 非交易申请类型 

		MakePackageAndSend(aStep, 6100, SSLEncrypt.SSLKEY_INDEX_TRADE, 512);
		return nRequestCode;
	}
	
	// 行权
	// wtbh:委托编号
	// marketCode:交易市场
	// xdxw:下单席位
	// gdzh:股东帐号
	// xwh:席位号
	public int Request_XQCD(String marketCode, String wtbh,
			String gdzh, String xwh, String xdxw) {
		PBSTEP aStep = new PBSTEP();
		int nRequestCode = CreateStepBaseRecord(aStep, 6101);

		aStep.AppendRecord();
	    aStep.AddFieldValueString(STEP_Define.STEP_WTBH, wtbh);//委托编号
	    aStep.AddFieldValueString(STEP_Define.STEP_GDH, gdzh);//股东帐号
	    aStep.AddFieldValueString(STEP_Define.STEP_SCDM, marketCode);//市场代码
	    aStep.AddFieldValueString(STEP_Define.STEP_XWH, xwh);//席位号
	    aStep.AddFieldValueString(STEP_Define.STEP_XDXW, xdxw);//下单席位
		
	    MakePackageAndSend(aStep, 6101, SSLEncrypt.SSLKEY_INDEX_TRADE,512);
		return nRequestCode;
	}
	
	// 行权指派查询
	// marketCode:交易市场
	// stockCode:证券代码(期权代码)
	// gdzh:股东帐号
	public int Request_XQZP(String marketCode, String stockCode, String gdzh) {
		PBSTEP aStep = new PBSTEP();
		int nRequestCode = CreateStepBaseRecord(aStep, 6107);

		aStep.AppendRecord();
		aStep.AddFieldValueString(STEP_Define.STEP_GDH, gdzh);// 股东帐号
		aStep.AddFieldValueString(STEP_Define.STEP_SCDM, marketCode);// 市场代码
		aStep.AddFieldValueString(STEP_Define.STEP_HYDM, stockCode);// 证券代码(期权代码)

		MakePackageAndSend(aStep, 6107, SSLEncrypt.SSLKEY_INDEX_TRADE, 512);
		return nRequestCode;
	}
	
	// 查询可买数量
	// stockCode:证券代码(期权代码)
	// marketCode:交易市场
	// gdzh:股东帐号
	// xwh:席位号
	public int Request_KXQSL(String stockCode, String marketCode, String gdzh, String xwh) {
		PBSTEP aStep = new PBSTEP();
		int nRequestCode = CreateStepBaseRecord(aStep, 6102);

		aStep.AppendRecord();
		aStep.AddFieldValueString(STEP_Define.STEP_GDH, gdzh);// 股东帐号
		aStep.AddFieldValueString(STEP_Define.STEP_XWH, xwh);// 席位号
		aStep.AddFieldValueString(STEP_Define.STEP_HYDM, stockCode);// 证券代码(期权代码)
		aStep.AddFieldValueString(STEP_Define.STEP_SCDM, marketCode);// 市场代码
		
		MakePackageAndSend(aStep, 6102, SSLEncrypt.SSLKEY_INDEX_TRADE, 512);
		return nRequestCode;
	}
	
	// 6104：查询备兑证券可冻结与解冻数量
	// stockCode:证券代码(期权代码)
	// marketCode:交易市场
	// gdzh:股东帐号
	// xwh:席位号
	// sdfx：锁定方向STEP_SDFX(3:冻结 4:解冻)
	// jylb:校验类别(保留，不使用）STEP_JYLB
	public int Request_BDKDJ_JDSL(String stockCode, String marketCode, String gdzh, String xwh,
			char sdfx, String jylb) {
		PBSTEP aStep = new PBSTEP();
		int nRequestCode = CreateStepBaseRecord(aStep, 6104);

		aStep.AppendRecord();
		aStep.AddFieldValueString(STEP_Define.STEP_GDH, gdzh);// 股东帐号
		aStep.AddFieldValueString(STEP_Define.STEP_XWH, xwh);// 席位号
		aStep.AddFieldValueString(STEP_Define.STEP_HYDM, stockCode);// 证券代码(期权代码)
		aStep.AddFieldValueString(STEP_Define.STEP_SCDM, marketCode);// 市场代码
		aStep.AddFieldValueString(STEP_Define.STEP_SDFX, sdfx);// 锁定方向
		
		MakePackageAndSend(aStep, 6104, SSLEncrypt.SSLKEY_INDEX_TRADE, 512);
		return nRequestCode;
	}
	
	// 6105：备兑证券冻结与解冻
	// stockCode:证券代码(期权代码)
	// marketCode:交易市场
	// wtsl:委托数量STEP_WTSL
	// gdzh:股东帐号
	// xwh:席位号
	// sdfx：锁定方向STEP_SDFX(3 锁定 4 解锁)
	// jylb:校验类别(保留，不使用）STEP_JYLB
	public int Request_BDDJJD(String stockCode, String marketCode, String wtsl, String gdzh, String xwh,
			char sdfx, String jylb) {
		PBSTEP aStep = new PBSTEP();
		int nRequestCode = CreateStepBaseRecord(aStep, 6105);

		aStep.AppendRecord();
		aStep.AddFieldValueString(STEP_Define.STEP_GDH, gdzh);// 股东帐号
		aStep.AddFieldValueString(STEP_Define.STEP_XWH, xwh);// 席位号
		aStep.AddFieldValueString(STEP_Define.STEP_HYDM, stockCode);// 证券代码(期权代码)
		aStep.AddFieldValueString(STEP_Define.STEP_SCDM, marketCode);// 市场代码
		aStep.AddFieldValueString(STEP_Define.STEP_SDFX, sdfx);// 锁定方向
		aStep.AddFieldValueString(STEP_Define.STEP_WTSL, wtsl);//委托数量
		
		MakePackageAndSend(aStep, 6105, SSLEncrypt.SSLKEY_INDEX_TRADE, 512);
		return nRequestCode;
	}
	
	//获取手机短信验证码
	public int Request_GetSMSVerifyCode(String strPhone, int loginType)
	{
	    PBSTEP aStep = new PBSTEP();
	    int nRequestCode = CreateStepBaseRecord(aStep, 6403);
	    
	    aStep.AppendRecord();
	    aStep.AddFieldValueString(STEP_Define.STEP_SJHM, strPhone);//手机号码
	    aStep.AddFieldValueINT(STEP_Define.STEP_DLLB, loginType);//登录类别	0 普通沪深交易；5 融资融券交易；6 个股期权交易
	    
	    MakePackageAndSend(aStep, 6403, SSLEncrypt.SSLKEY_INDEX_TRADE, 512);
	    return nRequestCode;
	}

	//验证手机验证码
	public int Request_CheckSMSVerifyCode(String strPhone, String strCode)
	{
	    PBSTEP aStep = new PBSTEP();
	    int nRequestCode = CreateStepBaseRecord(aStep, 6404);
	    
	    aStep.AppendRecord();
	    aStep.AddFieldValueString(STEP_Define.STEP_SJHM, strPhone);//手机号码
	    aStep.AddFieldValueString(STEP_Define.STEP_DXYZM, strCode);//短信验证码

	    MakePackageAndSend(aStep, 6404, SSLEncrypt.SSLKEY_INDEX_TRADE, 512);
	    return nRequestCode;
	}

	//通过PBSTEP类对象生成一个加密请求包并发送
	//datasize为包体的长度，数据为:package
	public int	MakePackageAndSend(PBSTEP pStep,int func, int nKeyIndex, int MinCompressSize)
	{
	    TListBuffer aListBuffer = new TListBuffer();
	    pStep.MakeData(aListBuffer, 0, pStep.GetRecNum());
	    
	    String szSrc = new String(aListBuffer.GetPtr(), 0,(int)aListBuffer.GetSize());
	    int nLen = szSrc.length();
	    L.e("Trade", "MakePackageAndSend Buffer:" + szSrc);
	    
	    int sendLen = MAX_MOBILE_PACKAGE_BUFFER_SIZE_STEP+11;
	    if(sendLen < nLen-100)
	    {
	    	sendLen = nLen*2;
	    }

	    byte[] sendData = new byte[sendLen];  
	    if (func == 1)
	    	sendLen = AddPackageAnswerWithRSA(szSrc.getBytes(),szSrc.getBytes().length,sendData,sendLen,8192);	//不加密
		else
			sendLen = AddPackageAnswer_Step(szSrc.getBytes(),szSrc.getBytes().length,sendData,sendLen, nKeyIndex, MinCompressSize, (byte)((func == 1)?66:67));
	    
	    if (func != Trade_Define.Func_Connect && func != Trade_Define.Func_Login && func != Trade_Define.Func_KMSL && func != Trade_Define.Func_HeartBeat
	    		&& func != Trade_Define.Func_ZCSJHM && func != Trade_Define.Func_YZSJZCM)
		{
			// 超时锁屏处理
			procLock();
		}
	    if (func != Trade_Define.Func_HeartBeat)
		{
			// 请求超时
	    	procTimeout();
	    	mIsProceeded = false;
		}
	    
		SendRequest(sendData,sendLen);
	    return 0;
	}

	//创建Step的首条记录，返回请求编号
	public int CreateStepBaseRecord(PBSTEP aStep,int func)
	{
	    int nRequestCode = MakeRequestCode();
	    aStep.Init();
	    aStep.SetBaseRecFieldValueINT(STEP_Define.STEP_FUNC, func);
	    aStep.SetBaseRecFieldValueINT(STEP_Define.STEP_REQUESTNO, nRequestCode);
	    aStep.SetBaseRecFieldValueINT(STEP_Define.STEP_RETURNNUM, 1);
	    
	    //aStep.SetBaseRecFieldValue_Encrypt(STEP_Define.STEP_JYMM, "123123", SSLEncrypt.SSLKEY_INDEX_TRADE_PWD);	//交易密码
	    
	    if (func != 1 && func != 6011) {
	        //密钥交换(1)和登录(6011)不需要传下面几个字段
	        aStep.SetBaseRecFieldValueString(STEP_Define.STEP_SESSION, mMyApp.mTradeData.session);
	        aStep.SetBaseRecFieldValueString(STEP_Define.STEP_ZJZH, mMyApp.mTradeData.zjzh);
	        aStep.SetBaseRecFieldValue_Encrypt(STEP_Define.STEP_JYMM, mMyApp.mTradeData.mTradePassword, SSLEncrypt.SSLKEY_INDEX_TRADE_PWD);	//交易密码
	    }
	    return nRequestCode;
	}
	//添加一个包
	public int	AddPackageAnswer_Step(byte[] indata, int size,byte[] outdata, int outsize,int nDesKeyIndex, int MinCompressSize, byte sign)
	{
		int nOutOffset = 0;
		byte[] NewData = new byte[MAX_MOBILE_PACKAGE_BUFFER_SIZE_STEP+2];
		byte[] zipdata = new byte[MAX_MOBILE_PACKAGE_BUFFER_SIZE_STEP+2+3];
		STD.memset(outdata, 0, outdata.length);
		STD.memset(zipdata, 0, zipdata.length);
		STD.memset(NewData, 0, NewData.length);

		int PackageNum = (size + MAX_MOBILE_PACKAGE_SIZE_STEP - 1) / MAX_MOBILE_PACKAGE_SIZE_STEP;
		int PackageNo  = 0;
		int nOffset = 0;
		while (size > 0)
		{
			int packagesize = size;
			if (packagesize > MAX_MOBILE_PACKAGE_SIZE_STEP)
			{
				packagesize = MAX_MOBILE_PACKAGE_SIZE_STEP;
			}
			short m_check_code = (short)GetCheckCode(indata,nOffset, packagesize);
			MyByteBuffer.putShort(NewData, 0, m_check_code);
			System.arraycopy(indata, nOffset, NewData, 2, packagesize);
			nOffset += packagesize;
			size -= packagesize;
			packagesize += 2;
	        
			//第一步，压缩
			int datasize		= packagesize;
			boolean flag = false;
			byte zipFlag = 0;
			if (datasize > MinCompressSize)
			{
				datasize = ZLibTools.compress(NewData,datasize,zipdata);
				zipFlag	= 1;
				flag = true;
			}
			if (flag == false)
			{
				System.arraycopy(NewData, 0, zipdata, 0, datasize);
				zipFlag = 0;
			}
			
			STD.memset(NewData, 0, NewData.length);
			MyByteBuffer.putByte(NewData, 0, zipFlag);//0表示未压缩，1表示zlib压缩
			MyByteBuffer.putShort(NewData, 1, (short)datasize);//数据压缩后的长度
			System.arraycopy(zipdata, 0, NewData, 3, datasize);
			
			datasize += 3;
			//第二步，加密
			STD.memset(zipdata, 0, zipdata.length);
			int iResult = (int)SSLEncrypt.DesNcbcEncrypt(nDesKeyIndex,NewData, zipdata, datasize, 1);
	
			if(outsize - nOutOffset < 8+iResult)
			{
				L.e("Trade", "ERROR:AddPackageAnswer_Step outsize <  iResult" );
				break;
			}
			System.arraycopy(zipdata, 0, outdata, nOutOffset+8, iResult);
			nOutOffset += (8+iResult);
			
			outdata[0] = sign; //协议标识，目前定义为66
			outdata[1] = 1; //0表示未加密，1表示数据加密了
			outdata[2] = (byte)PackageNum;//通讯包总数
			outdata[3] = (byte)PackageNo;//当前通讯包序号，从0开始，当为PackageNum-1时表示是最后一个通讯包
			m_check_code = (short)GetCheckCode(outdata,8, iResult);
			MyByteBuffer.putShort(outdata, 4, (short)m_check_code);//校验码（对通讯包体进行CRC16校验，在加密压缩后）
			MyByteBuffer.putShort(outdata, 6, (short)iResult);//包体的长度，加密压缩后
			PackageNo++;
		}
		return nOutOffset;
	}

	public int		AddPackageAnswerWithRSA(byte[] indata, int size,byte[] outdata, int outsize, int MinCompressSize)
	{
		int nOutOffset = 0;
		byte[] NewData = new byte[MAX_MOBILE_PACKAGE_BUFFER_SIZE_STEP+2];
		byte[] zipdata = new byte[MAX_MOBILE_PACKAGE_BUFFER_SIZE_STEP+2+3];
		STD.memset(outdata, 0, outdata.length);
		STD.memset(zipdata, 0, zipdata.length);
		STD.memset(NewData, 0, NewData.length);

		int PackageNum = (size + MAX_MOBILE_PACKAGE_SIZE_STEP - 1) / MAX_MOBILE_PACKAGE_SIZE_STEP;
		int PackageNo  = 0;
		int nOffset = 0;
		while (size > 0)
		{
			int packagesize = size;
			if (packagesize > MAX_MOBILE_PACKAGE_SIZE_STEP)
			{
				packagesize = MAX_MOBILE_PACKAGE_SIZE_STEP;
			}
			short m_check_code = (short)GetCheckCode(indata,nOffset, packagesize);
			MyByteBuffer.putShort(NewData, 0, m_check_code);
			System.arraycopy(indata, nOffset, NewData, 2, packagesize);
			nOffset += packagesize;
			size -= packagesize;
			packagesize += 2;
	        
			//第一步，压缩
			int datasize		= packagesize;
			boolean flag = false;
			byte zipFlag = 0;
			if (datasize > MinCompressSize)
			{
				datasize = ZLibTools.compress(NewData,datasize,zipdata);
				zipFlag	= 1;
				flag = true;
			}
			if (flag == false)
			{
				System.arraycopy(NewData, 0, zipdata, 0, datasize);
				zipFlag = 0;
			}
			
			STD.memset(NewData, 0, NewData.length);
			MyByteBuffer.putByte(NewData, 0, zipFlag);//0表示未压缩，1表示zlib压缩
			MyByteBuffer.putShort(NewData, 1, (short)datasize);//数据压缩后的长度
			System.arraycopy(zipdata, 0, NewData, 3, datasize);
			
			datasize += 3;
			//第二步，加密
	
			STD.memset(zipdata, 0, zipdata.length);
			int iResult = (int)SSLEncrypt.RSAPublicEncrypt(NewData, zipdata, datasize, 1);
		
			if(outsize - nOutOffset < 8+iResult)
			{
				L.e("Trade", "AddPackageAnswerWithRSA outsize <  iResult" );
				break;
			}
			System.arraycopy(zipdata, 0, outdata, nOutOffset+8, iResult);
			nOutOffset += (8+iResult);
			
			outdata[0] = 66; //协议标识，目前定义为66
			outdata[1] = 1; //0表示未加密，1表示数据加密了
			outdata[2] = (byte)PackageNum;//通讯包总数
			outdata[3] = (byte)PackageNo;//当前通讯包序号，从0开始，当为PackageNum-1时表示是最后一个通讯包
			m_check_code = (short)GetCheckCode(outdata,8, iResult);
			MyByteBuffer.putShort(outdata, 4, (short)m_check_code);//校验码（对通讯包体进行CRC16校验，在加密压缩后）
			MyByteBuffer.putShort(outdata, 6, (short)iResult);//包体的长度，加密压缩后
			PackageNo++;
		}
	    
		return nOutOffset;
	}
	public int SendRequest(byte[] data, int size)
	{			
		return addSendData(data, 0, size);
	}
}
