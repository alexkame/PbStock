package com.pengbo.mhdzq.zq_trade_activity;

import java.util.ArrayList;
import java.util.List;

import com.pengbo.mhdzq.adapter.ZQSetOnLineAdapter;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.fragment.JiaoYiPager;
import com.pengbo.mhdzq.zq_activity.HdActivity;
import com.pengbo.mhdzq.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ZQTradeQSListActivity extends HdActivity implements OnItemClickListener,OnClickListener{

	public static final int RESULT_CODE=0;
	private MyApp mMyApp;
	private TextView back;
	private GridView gv;

	private List<String> datas;
	private ZQSetOnLineAdapter mAdapter;
	private int mCurrentPos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zqsetonlinetime);
		mMyApp = (MyApp)this.getApplication();
		initView();
	}

	private void initView() {
		mCurrentPos = this.getIntent().getIntExtra(JiaoYiPager.INTENT_QS_INDEX, 0);
		
		gv = (GridView) this.findViewById(R.id.setonlinetime_gv);
		back = (TextView) this.findViewById(R.id.setonlinetime_backbtn);
		datas = new ArrayList<String>();
		for (int i = 0; i < mMyApp.getTradeQSNum_ZQ(); i++) {
			datas.add(mMyApp.getTradeQSInfo_ZQ(i).mName);
		}
		mAdapter = new ZQSetOnLineAdapter(datas, this);
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
		
		mMyApp.mTradeQSIndex_ZQ = mCurrentPos;
		if (mCurrentPos >= 0 && mCurrentPos < mMyApp.getTradeQSNum_ZQ())
		{
			if (mMyApp.getTradeQSInfo_ZQ(mCurrentPos) != null && mMyApp.getTradeQSInfo_ZQ(mCurrentPos).mAccoutType.size() > 0)
			{
				mMyApp.mTradeQSZHType_ZQ = mMyApp.getTradeQSInfo_ZQ(mCurrentPos).mAccoutType.get(0);
			}else
			{
				mMyApp.mTradeQSZHType_ZQ = "";
			}
		}else
		{
			mMyApp.mTradeQSZHType_ZQ = "";
		}
		ZQTradeQSListActivity.this.finish();	
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.setonlinetime_backbtn:
			{
				ZQTradeQSListActivity.this.finish();				
			}
				break;

			default:
				break;
		}
	}
}
