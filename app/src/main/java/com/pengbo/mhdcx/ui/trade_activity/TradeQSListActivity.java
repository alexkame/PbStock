package com.pengbo.mhdcx.ui.trade_activity;

import java.util.ArrayList;
import java.util.List;

import com.pengbo.mhdcx.adapter.SetOnLineAdapter;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.zq_activity.HdActivity;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.ui.main_activity.TradeLoginActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class TradeQSListActivity extends HdActivity implements OnItemClickListener,OnClickListener{

	public static final int RESULT_CODE=1;
	private MyApp mMyApp;
	private TextView back;
	private GridView gv;

	private List<String> datas;
	private SetOnLineAdapter mAdapter;
	private int mCurrentPos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setonlinetime);
		mMyApp = (MyApp)this.getApplication();
		initView();
	}

	private void initView() {
		mCurrentPos = this.getIntent().getIntExtra(TradeLoginActivity.INTENT_QS_INDEX, 0);
		
		gv = (GridView) this.findViewById(R.id.setonlinetime_gv);
		back = (TextView) this.findViewById(R.id.setonlinetime_backbtn);
		datas = new ArrayList<String>();
		for (int i = 0; i < mMyApp.getTradeQSNum(); i++) {
			datas.add(mMyApp.getTradeQSInfo(i).mName);
		}
		mAdapter = new SetOnLineAdapter(datas, this);
		mAdapter.setSeclection(mCurrentPos);
		gv.setAdapter(mAdapter);
		gv.setOnItemClickListener(this);
		back.setOnClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mAdapter.setSeclection(position);
		mAdapter.notifyDataSetChanged();		
		mCurrentPos = position;
		
		mMyApp.mTradeQSIndex = mCurrentPos;
		if (mCurrentPos >= 0 && mCurrentPos < mMyApp.getTradeQSNum())
		{
			if (mMyApp.getTradeQSInfo(mCurrentPos) != null && mMyApp.getTradeQSInfo(mCurrentPos).mAccoutType.size() > 0)
			{
				mMyApp.mTradeQSZHType = mMyApp.getTradeQSInfo(mCurrentPos).mAccoutType.get(0);
			}else
			{
				mMyApp.mTradeQSZHType = "";
			}
		}else
		{
			mMyApp.mTradeQSZHType = "";
		}
		
		TradeQSListActivity.this.finish();	
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.setonlinetime_backbtn:
			{
				TradeQSListActivity.this.finish();				
			}
				break;

			default:
				break;
		}
	}
}
