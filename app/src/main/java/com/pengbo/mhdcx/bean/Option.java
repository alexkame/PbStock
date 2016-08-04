package com.pengbo.mhdcx.bean;

import java.io.Serializable;

/**
 * 自选筛选中用到的 期权的 属性  
 * @author pobo
 *
 */
public class Option implements Serializable {
	private static final long serialVersionUID = 1L;

	public  CharSequence mname; //期权名称
	public  CharSequence mDueTime; //到期日期/委托时间

	public  CharSequence mAveragePrice; //均价
	public  CharSequence mlatestprice; //现价
	public  CharSequence mzf; //涨跌幅
	public  CharSequence mFudongyk; //浮动盈亏
	public  CharSequence mFudongyklv;//浮动盈亏比例
	public  CharSequence  myijia; //溢价率
	public  CharSequence  mxingquan; //行权价
	public  CharSequence  mWTprice; //委托价
	public  CharSequence  mWTSL; //委托数量
	public  CharSequence  mCJSL; //成交数量
	public  CharSequence  mnownum; //现量
	public  CharSequence  mchicang; //持仓量
	public  CharSequence  mCJJG;//成交价

	
	public  CharSequence  mcangcha; //仓差
	public  CharSequence  mgangganlv; //杠杆率
	public  CharSequence  mtruegangganlv; //真实杠杆率
	public  CharSequence  minprice; //内在价值
	public  CharSequence  mtimeprice; //时间价值
	public  CharSequence  mshixuzhi; //实虚值
	public  CharSequence  moldtime; //到期天数
	public  CharSequence  mBaoZJ; //到期天数
	
	public  CharSequence  mMMLBMC;//买卖类别名称
	public  CharSequence  mKPCMC;//开平仓名称
	public  CharSequence  mWTZTMC;//委托状态名称
	public  CharSequence  mWTZT;//委托状态
	
	public int image_one; //
	public int image_two; //
	public int image_three; //
	public boolean mbMMBZ; //买卖标志，true-buy
	public boolean mbBDBZ = false; //备兑标志，true-备兑
	public int days;//持仓中剩余天数 
	
	
	public int getDays() {
		return days;
	}
	public void setDays(int days) {
		this.days = days;
	}
	public boolean getBDBZ() {
		return mbBDBZ;
	}
	public void setBDBZ(boolean bdbz) {
		this.mbBDBZ = bdbz;
	}
	public boolean getMMBZ() {
		return mbMMBZ;
	}
	public void setMMBZ(boolean mmbz) {
		this.mbMMBZ = mmbz;
	}
	public int getImage_one() {
		return image_one;
	}
	public void setImage_one(int image_one) {
		this.image_one = image_one;
	}
	public int getImage_two() {
		return image_two;
	}
	public void setImage_two(int image_two) {
		this.image_two = image_two;
	}
	public int getImage_three() {
		return image_three;
	}
	public void setImage_three(int image_three) {
		this.image_three = image_three;
	}

	public CharSequence getMname() {
		return mname;
	}
	public void setMname(CharSequence mname) {
		this.mname = mname;
	}
	
	public CharSequence getmDueTime() {
		return mDueTime;
	}
	public void setmDueTime(CharSequence mDueTime) {
		this.mDueTime = mDueTime;
	}
	public CharSequence getMlatestprice() {
		return mlatestprice;
	}
	public void setMlatestprice(CharSequence mlatestprice) {
		this.mlatestprice = mlatestprice;
	}
	public CharSequence getAverateprice() {
		return mAveragePrice;
	}
	public void setAverateprice(CharSequence averageprice) {
		this.mAveragePrice = averageprice;
	}
	public CharSequence getFudongyk() {
		return mFudongyk;
	}
	public void setFudongyk(CharSequence fudongyk) {
		this.mFudongyk = fudongyk;
	}
	public CharSequence getFudongyklv() {
		return mFudongyklv;
	}
	public void setmFudongyklv(CharSequence fudongyklv) {
		this.mFudongyklv = fudongyklv;
	}
	public CharSequence getMzf() {
		return mzf;
	}
	public void setMzf(CharSequence mzf) {
		this.mzf = mzf;
	}
	public CharSequence getMyijia() {
		return myijia;
	}
	public void setMyijia(CharSequence myijia) {
		this.myijia = myijia;
	}
	public CharSequence getMxingquan() {
		return mxingquan;
	}
	public void setMxingquan(CharSequence mxingquan) {
		this.mxingquan = mxingquan;
	}
	public CharSequence getWTprice() {
		return mWTprice;
	}
	public void setWTprice(CharSequence WTJG) {
		this.mWTprice = WTJG;
	}
	public CharSequence getWTSL() {
		return mWTSL;
	}
	public void setWTSL(CharSequence WTSL) {
		this.mWTSL = WTSL;
	}
	public CharSequence getCJSL() {
		return mCJSL;
	}
	public void setCJSL(CharSequence CJSL) {
		this.mCJSL = CJSL;
	}
	public CharSequence getMnownum() {
		return mnownum;
	}
	public void setMnownum(CharSequence mnownum) {
		this.mnownum = mnownum;
	}
	public CharSequence getMchicang() {
		return mchicang;
	}
	public void setMchicang(CharSequence mchicang) {
		this.mchicang = mchicang;
	}
	public CharSequence getMcangcha() {
		return mcangcha;
	}
	public void setMcangcha(CharSequence mcangcha) {
		this.mcangcha = mcangcha;
	}
	public CharSequence getMgangganlv() {
		return mgangganlv;
	}
	public void setMgangganlv(CharSequence mgangganlv) {
		this.mgangganlv = mgangganlv;
	}
	public CharSequence getMtruegangganlv() {
		return mtruegangganlv;
	}
	public void setMtruegangganlv(CharSequence mtruegangganlv) {
		this.mtruegangganlv = mtruegangganlv;
	}
	public CharSequence getMinprice() {
		return minprice;
	}
	public void setMinprice(CharSequence minprice) {
		this.minprice = minprice;
	}
	public CharSequence getMtimeprice() {
		return mtimeprice;
	}
	public void setMtimeprice(CharSequence mtimeprice) {
		this.mtimeprice = mtimeprice;
	}
	public CharSequence getMshixuzhi() {
		return mshixuzhi;
	}
	public void setMshixuzhi(CharSequence mshixuzhi) {
		this.mshixuzhi = mshixuzhi;
	}
	public CharSequence getMoldtime() {
		return moldtime;
	}
	public void setMoldtime(CharSequence moldtime) {
		this.moldtime = moldtime;
	}
	public CharSequence getBaoZJ() {
		return mBaoZJ;
	}
	public void setBaoZJ(CharSequence baoZJ) {
		this.mBaoZJ = baoZJ;
	}
	public CharSequence getMMLBMC() {
		return mMMLBMC;
	}
	public void setMMLBMC(CharSequence MMLBMC) {
		this.mMMLBMC = MMLBMC;
	}
	public CharSequence getKPCMC() {
		return mKPCMC;
	}
	public void setKPCMC(CharSequence KPCMC) {
		this.mKPCMC = KPCMC;
	}
	public CharSequence getWTZTMC() {
		return mWTZTMC;
	}
	public void setWTZTMC(CharSequence WTZTMC) {
		this.mWTZTMC = WTZTMC;
	}
	public CharSequence getWTZT() {
		return mWTZT;
	}
	public void setWTZT(CharSequence WTZT) {
		this.mWTZT = WTZT;
	}
	public CharSequence getCJJG() {
		return mCJJG;
	}
	public void setCJJG(CharSequence CJJG) {
		this.mCJJG = CJJG;
	}
	@Override
	public String toString() {
		return "Option [mname=" + mname + ", mlatestprice=" + mlatestprice
				+ ", mzf=" + mzf + ", myijia=" + myijia + ", mxingquan="
				+ mxingquan + ", mnownum=" + mnownum
				+ ", mchicang=" + mchicang + ", mcangcha=" + mcangcha
				+ ", mgangganlv=" + mgangganlv + ", mtruegangganlv="
				+ mtruegangganlv + ", minprice=" + minprice + ", mtimeprice="
				+ mtimeprice + ", mshixuzhi=" + mshixuzhi + ", moldtime="
				+ moldtime + "]";
	}
	
	
}
