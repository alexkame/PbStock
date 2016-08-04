package com.pengbo.mhdcx.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.adapter.SetOnLineAdapter;
import com.pengbo.mhdzq.trade.data.Trade_Define;
import com.pengbo.mhdcx.ui.main_activity.SettingActivity;
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
 * 全部平仓默认委托价格
 * @author pobo
 *
 */
public class AllTradePrice extends BaseActivity implements OnItemClickListener,OnClickListener{
	public static final int RESULT_CODE_AllTradePrice=10003;
	private TextView alltrade_back;
	private GridView gv;
	private int mPriceType;
	private int mSelectIndex;
	private List<String> datas;
	private SetOnLineAdapter mAdapter;

	String[] prices = new String[] { "最新价 ","对手价（默认）", "挂单价", "涨停价", "跌停价", "全额成交或撤销"};
	int[] priceTypes = new int[] {  Trade_Define.WTPRICEMODE_NOW, 
									Trade_Define.WTPRICEMODE_DSJ, 
									Trade_Define.WTPRICEMODE_GDJ, 
									Trade_Define.WTPRICEMODE_UP, 
									Trade_Define.WTPRICEMODE_DOWN
									,
									Trade_Define.WTPRICEMODE_QBCJHCX//全额成交或撤销,分深圳和上海
									};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setonlinetime);
		mPriceType = this.getIntent().getIntExtra(SettingActivity.INTENT_QBPCTYPE, Trade_Define.WTPRICEMODE_DSJ);

		for (int i = 0; i < priceTypes.length; i++)
		{
			if (mPriceType == priceTypes[i])
			{
				mSelectIndex = i;
				break;
			}
		}
		
		initView();
	}

	private void initView() {
		gv=(GridView) this.findViewById(R.id.setonlinetime_gv);
		alltrade_back=(TextView) this.findViewById(R.id.setonlinetime_backbtn);
		datas=new ArrayList<String>();		
		for (int i = 0; i <prices.length; i++) {
			datas.add(prices[i]);
		}
		mAdapter=new SetOnLineAdapter(datas, this);
		mAdapter.setSeclection(mSelectIndex);
		gv.setAdapter(mAdapter);
		gv.setOnItemClickListener(this);
		alltrade_back.setOnClickListener(this);
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mAdapter.setSeclection(position);
		mAdapter.notifyDataSetChanged();		
		mSelectIndex = position;
		mPriceType = priceTypes[mSelectIndex];
		
		PreferenceEngine.getInstance().saveTradeAllPingCangPrice(priceTypes[mSelectIndex]);
		Intent intent = new Intent();
		intent.putExtra(SettingActivity.INTENT_QBPCTYPE, mPriceType);
		setResult(RESULT_CODE_AllTradePrice, intent);				
		AllTradePrice.this.finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.setonlinetime_backbtn:
				
				AllTradePrice.this.finish();	

				break;

			default:
				break;
		}
	}
	
}