package com.pengbo.mhdcx.ui.trade_activity;

import java.util.ArrayList;

import com.pengbo.mhdcx.adapter.SetOnLineAdapter;
import com.pengbo.mhdzq.R;
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

public class TradeMoreChooseBankActivity extends HdActivity implements OnItemClickListener,OnClickListener{

	public final static int REQUEST_CODE_MMLX = 10001;
	
	private TextView mTV_back;
	private GridView mGridView;
	private int mCurrentSel = 0;
	private ArrayList<String> datas;
	private SetOnLineAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setonlinetime);
		initView();
	}
	
	private void initView() {
		mGridView = (GridView) this.findViewById(R.id.setonlinetime_gv);
		mTV_back = (TextView) this.findViewById(R.id.setonlinetime_backbtn);
		datas = new ArrayList<String>();
		
		datas = (ArrayList<String>) getIntent().getSerializableExtra("mmlx");
		mCurrentSel = getIntent().getIntExtra("pwtype", 0);
		
		mAdapter=new SetOnLineAdapter(datas, this);
		mAdapter.setSeclection(mCurrentSel);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);
		mTV_back.setOnClickListener(this);
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mAdapter.setSeclection(position);
		mAdapter.notifyDataSetChanged();		
		mCurrentSel = position;

		Intent intent = new Intent();
		intent.putExtra("PWType", mCurrentSel);
		setResult(REQUEST_CODE_MMLX, intent);				
		TradeMoreChooseBankActivity.this.finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.setonlinetime_backbtn:
			{			
				TradeMoreChooseBankActivity.this.finish();
			}
				break;

			default:
				break;
		}
	}
}
