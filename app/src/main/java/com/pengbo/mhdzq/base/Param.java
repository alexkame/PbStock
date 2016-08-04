package com.pengbo.mhdzq.base;

public class Param {
	private String mJXName;//均线名称 
	private String mJXEditDay;//均线天数
	private String mJXR;//均线后面的字体
	

	public Param() {
	}
	
	public Param(String mJXName,String mJXDay,String mJXR) {
		this.mJXEditDay=mJXDay;
		this.mJXName=mJXName;
		this.mJXR=mJXR;
	}

	public String getmJXName() {
		return mJXName;
	}

	public void setmJXName(String mJXName) {
		this.mJXName = mJXName;
	}

	public String getmJXEditDay() {
		return mJXEditDay;
	}

	public void setmJXEditDay(String mJXEditDay) {
		this.mJXEditDay = mJXEditDay;
	}

	public String getmJXR() {
		return mJXR;
	}

	public void setmJXR(String mJXR) {
		this.mJXR = mJXR;
	}

	

}
