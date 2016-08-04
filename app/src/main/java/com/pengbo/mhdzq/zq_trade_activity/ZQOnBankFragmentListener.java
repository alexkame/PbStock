package com.pengbo.mhdzq.zq_trade_activity;

public interface ZQOnBankFragmentListener {
	public void requestYZZZ_YZQ(int bankIndex, String zzje, String yhmm, String zjmm);
	public void requestYZZZ_QZY(int bankIndex, String zzje, String yhmm, String zjmm);
	public void request_YHYE(int bankIndex, String yhmm, String zjmm);
}
