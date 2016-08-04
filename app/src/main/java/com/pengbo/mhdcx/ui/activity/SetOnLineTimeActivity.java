package com.pengbo.mhdcx.ui.activity;
import java.util.ArrayList;
import java.util.List;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.adapter.SetOnLineAdapter;
import com.pengbo.mhdzq.tools.PreferenceEngine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class SetOnLineTimeActivity extends BaseActivity implements OnItemClickListener,OnClickListener{
	public static final int RESULT_CODE_ONLINETIME=10002;
	private TextView setonlinetime_back;
	private GridView gv;
	private int mCurrentSel = 0;
	private List<String> datas;
	private SetOnLineAdapter mAdapter;

	int[] mOnLineTimes = new int[] {5, 10, 15, 30, 60, 120, 180};
	String[] tv_times = new String[] { "5分钟","10分钟", "15分钟（默认）", "30分钟", "60分钟", "120分钟","180分钟"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setonlinetime);
		initView();
	}

	private void initView() {
		gv=(GridView) this.findViewById(R.id.setonlinetime_gv);
		setonlinetime_back=(TextView) this.findViewById(R.id.setonlinetime_backbtn);
		datas=new ArrayList<String>();
		int locktime = PreferenceEngine.getInstance().getTradeOnlineTime();
		for (int i = 0; i <tv_times.length; i++) {
			datas.add(tv_times[i]);
			if (locktime == mOnLineTimes[i])
			{
				mCurrentSel = i;
			}
		}
		mAdapter=new SetOnLineAdapter(datas, this);
		mAdapter.setSeclection(mCurrentSel);
		gv.setAdapter(mAdapter);
		gv.setOnItemClickListener(this);
		setonlinetime_back.setOnClickListener(this);
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mAdapter.setSeclection(position);
		mAdapter.notifyDataSetChanged();		
		mCurrentSel = position;
		
		PreferenceEngine.getInstance().saveTradeOnlineTime(mOnLineTimes[mCurrentSel]);
		mMyApp.mTradeData.mTradeLockTimeout = mOnLineTimes[mCurrentSel];
		mMyApp.mTradeNet.procLock();
		Intent intent = new Intent();
		intent.putExtra("back_onlinetime", "Back Data");
		setResult(RESULT_CODE_ONLINETIME, intent);				
		SetOnLineTimeActivity.this.finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.setonlinetime_backbtn:
			{			
				SetOnLineTimeActivity.this.finish();
			}
				break;

			default:
				break;
		}
	}
	
}
