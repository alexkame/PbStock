package com.pengbo.mhdzq.zq_trade_activity;

import java.util.ArrayList;
import java.util.List;

import com.pengbo.mhdcx.adapter.SetOnLineAdapter;
import com.pengbo.mhdcx.ui.main_activity.TradeLoginActivity;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.zq_activity.HdActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ZQTradeTimeOutSetActivity extends HdActivity implements
		OnItemClickListener, OnClickListener {

	private MyApp mMyApp;
	private TextView back;
	private GridView gv;

	private List<String> datas;
	private SetOnLineAdapter mAdapter;
	ArrayList<Integer> keepLiveTime;// new String[] { "客户帐号 ","资金账号"};
	ArrayList<String> timeout;
	private int mCurrentSel = 0;
	private int mQSIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logintimeset);
		mMyApp = (MyApp) this.getApplication();
		initView();
	}

	private void initView() {

		gv = (GridView) this.findViewById(R.id.setonlinetime_gv);
		back = (TextView) this.findViewById(R.id.setonlinetime_backbtn);
		datas = new ArrayList<String>();
		keepLiveTime = new ArrayList<Integer>();

		timeout = new ArrayList();

		timeout.add("一分钟");
		timeout.add("三分钟");
		timeout.add("五分钟(默认)");
		timeout.add("十分钟");
		timeout.add("十五分钟");

		keepLiveTime.add(1);
		keepLiveTime.add(3);
		keepLiveTime.add(5);
		keepLiveTime.add(10);
		keepLiveTime.add(15);

		for (int i = 0; i < timeout.size(); i++) {
			String type = timeout.get(i);
			datas.add(type);
		}

		mAdapter = new SetOnLineAdapter(datas, this);
		int time = PreferenceEngine.getInstance().getSaveKeepLiveTime();
		for(int i = 0;i< keepLiveTime.size();i++)
		{
			if(time == keepLiveTime.get(i))
			{
				mCurrentSel = i;
				break;
			}
		}
		
		
		mAdapter.setSeclection(mCurrentSel);
		gv.setAdapter(mAdapter);
		gv.setOnItemClickListener(this);
		back.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.setonlinetime_backbtn: {
			ZQTradeTimeOutSetActivity.this.finish();
		}
			break;

		default:
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		mAdapter.setSeclection(position);
		mAdapter.notifyDataSetChanged();
		int keeplive = keepLiveTime.get(position);
		mCurrentSel = position;

		PreferenceEngine.getInstance().saveKeepLiveTime(keeplive);
	}
}
