package com.pengbo.mhdcx.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.adapter.AdvancedTradeDefaultAddNumAdapter;
import com.pengbo.mhdcx.ui.main_activity.SettingActivity;
import com.pengbo.mhdzq.tools.PreferenceEngine;

/**
 * 高级交易按钮顺序 
 * @author pobo
 *
 */

public class AdvancedModelBtnAry extends BaseActivity implements OnItemClickListener,OnClickListener{
	public static final int RESULT_CODE_ADVANCEDBTNary=10007;
	private TextView fasttrade_back;
	private GridView gv;
	private int mTradeBtnMode;
	private List<Integer> datas;
	private AdvancedTradeDefaultAddNumAdapter mAdapter;
	int [] btnmode = new int[] {R.drawable.shunxu1,R.drawable.shunxu2};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setonlinetime);
		mTradeBtnMode = this.getIntent().getIntExtra(SettingActivity.INTENT_JYBTNMODE, 0);
		
		initView();
	}

	private void initView() {

		gv=(GridView) this.findViewById(R.id.setonlinetime_gv);
		fasttrade_back=(TextView) this.findViewById(R.id.setonlinetime_backbtn);
		datas=new ArrayList<Integer>();		
		for (int i = 0; i <btnmode.length; i++) {
			datas.add(btnmode[i]);
		}
		mAdapter=new AdvancedTradeDefaultAddNumAdapter(datas, this);
		mAdapter.setSeclection(mTradeBtnMode);
		gv.setAdapter(mAdapter);
		gv.setOnItemClickListener(this);
		fasttrade_back.setOnClickListener(this);
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mAdapter.setSeclection(position);
		mAdapter.notifyDataSetChanged();		
		mTradeBtnMode = position;
		
		PreferenceEngine.getInstance().saveTradeSeniorModeWTBtn(mTradeBtnMode);
		
		Intent intent = new Intent();
		intent.putExtra(SettingActivity.INTENT_JYBTNMODE, mTradeBtnMode);
		setResult(RESULT_CODE_ADVANCEDBTNary, intent);			
		this.finish();	
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.setonlinetime_backbtn:		
				this.finish();	
				
				break;

			default:
				break;
		}
	}
	
}
