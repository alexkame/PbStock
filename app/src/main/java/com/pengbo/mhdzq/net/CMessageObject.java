package com.pengbo.mhdzq.net;

import com.pengbo.mhdzq.data.tagListBuffer;

public class CMessageObject {

	public int nErrorCode;
	public String errorMsg;
	private tagListBuffer mBuffer;

	public CMessageObject() {
		nErrorCode = 0;
		errorMsg = new String();
		mBuffer = new tagListBuffer();
	}

	public void setData(byte[] szData, int nSize) {
		mBuffer.Add(szData, nSize);
	}

	public void setData(byte[] szData, int offset, int nSize) {
		mBuffer.Add(szData, offset, nSize);
	}

	public void clearData() {
		mBuffer.Empty();
	}

	public byte[] getData() {
		return mBuffer.GetPtr();
	}

	public int getDataLength() {
		return mBuffer.GetSize();
	}

	public CMessageObject clone() {
		CMessageObject msgObj = new CMessageObject();
		msgObj.nErrorCode = this.nErrorCode;
		msgObj.errorMsg = this.errorMsg;
		msgObj.mBuffer = this.mBuffer.clone();
		return msgObj;
	}
}
