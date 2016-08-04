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
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 交易数量默认加量
 * @author pobo
 *
 */
public class DefaultAddNum extends BaseActivity implements OnItemClickListener,OnClickListener{
	public static final int RESULT_CODE_defaultAddNum=10006;
	private TextView addNum_back;
	private GridView gv;
	private int stock_GridID=0;
	private List<String> datas;
	private SetOnLineAdapter mAdapter;

	String[] prices = new String[] { "1(默认) ","5", "10", "50"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setonlinetime);
		initView();
	}

	private void initView() {

		gv=(GridView) this.findViewById(R.id.setonlinetime_gv);
		addNum_back=(TextView) this.findViewById(R.id.setonlinetime_backbtn);
		datas=new ArrayList<String>();		
		for (int i = 0; i <prices.length; i++) {
			datas.add(prices[i]);
		}
		mAdapter=new SetOnLineAdapter(datas, this);
		mAdapter.setSeclection(PreferenceEngine.getInstance().getTradeOrderIncreaseNum());
		gv.setAdapter(mAdapter);
		gv.setOnItemClickListener(this);
		addNum_back.setOnClickListener(this);
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mAdapter.setSeclection(position);
		mAdapter.notifyDataSetChanged();		
		stock_GridID=position;
		
		Intent intent = new Intent();
		intent.putExtra("back_addNum", "交易数量默认加量");
		setResult(RESULT_CODE_defaultAddNum, intent);	
		PreferenceEngine.getInstance().saveTradeOrderIncreaseNum(stock_GridID);
		this.finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.setonlinetime_backbtn:
			{
				this.finish();
			}
				break;

			default:
				break;
		}
	}
	
}
