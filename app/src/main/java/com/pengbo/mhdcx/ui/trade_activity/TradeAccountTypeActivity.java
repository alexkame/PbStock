package com.pengbo.mhdcx.ui.trade_activity;

import java.util.ArrayList;
import java.util.List;

import com.pengbo.mhdcx.adapter.SetOnLineAdapter;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.zq_activity.HdActivity;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.ui.main_activity.TradeLoginActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class TradeAccountTypeActivity extends HdActivity implements OnItemClickListener,OnClickListener{

	public static final int RESULT_CODE=1;
	
	private MyApp mMyApp;
	private TextView back;
	private GridView gv;

	private List<String> datas;
	private SetOnLineAdapter mAdapter;
	ArrayList<String> types;//new String[] { "客户帐号 ","资金账号"};
	private int mCurrentSel = 0;
	private int mQSIndex;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setonlinetime);
		mMyApp = (MyApp)this.getApplication();
		initView();
	}

	private void initView() {
		mQSIndex = this.getIntent().getIntExtra(TradeLoginActivity.INTENT_QS_INDEX, 0);

		gv=(GridView) this.findViewById(R.id.setonlinetime_gv);
		back=(TextView) this.findViewById(R.id.setonlinetime_backbtn);
		datas=new ArrayList<String>();	
		types = new ArrayList<String>();
		
		String aTypeStr = mMyApp.mTradeQSZHType;
		if (mMyApp.getTradeQSNum() > 0)
		{
			if (mQSIndex >=0 && mQSIndex < mMyApp.getTradeQSNum())
			{
				for (int i = 0; i <mMyApp.getTradeQSInfo(mQSIndex).mAccoutType.size(); i++) {
					String type = mMyApp.getTradeQSInfo(mQSIndex).mAccoutType.get(i);
					if (aTypeStr.equals(type))
					{
						mCurrentSel = i;
					}
					types.add(type);
					datas.add(type.substring(0, type.indexOf(",")));
				}
			}
		}
		

		mAdapter=new SetOnLineAdapter(datas, this);
		mAdapter.setSeclection(mCurrentSel);
		gv.setAdapter(mAdapter);
		gv.setOnItemClickListener(this);
		back.setOnClickListener(this);
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mAdapter.setSeclection(position);
		mAdapter.notifyDataSetChanged();		
		mCurrentSel = position;
		
		mMyApp.mTradeQSIndex = mQSIndex;
		mMyApp.mTradeQSZHType = types.get(mCurrentSel);
		TradeAccountTypeActivity.this.finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.setonlinetime_backbtn:
			{
				TradeAccountTypeActivity.this.finish();
			}
				break;

			default:
				break;
		}
	}

}
