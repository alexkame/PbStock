package com.pengbo.mhdcx.ui.trade_activity;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.zq_activity.HdActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class EditIdentifyPassword extends HdActivity implements OnClickListener{

	public TextView mBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trade_more_choose_bank);
		initView();
	}
	
	private void initView() {
	
		mBack=(TextView) this.findViewById(R.id.back);
		mBack.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			this.finish();
			break;
		default:
			break;
		}
	}
}
