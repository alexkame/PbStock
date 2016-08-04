package com.pengbo.mhdcx.ui.trade_activity;

import android.app.Activity;
import android.os.Bundle;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.zq_activity.HdActivity;

public class OrderTrendActivity extends HdActivity {

	public final static int result_code_trend = 1011;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_order_trend_dialog);
	}
}
