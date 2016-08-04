package com.pengbo.mhdzq.zq_trade_activity;

import java.util.ArrayList;

import com.pengbo.mhdcx.adapter.SetOnLineAdapter;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.zq_activity.HdActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ZQTradeMoreChooseBankActivity extends HdActivity implements OnItemClickListener,OnClickListener{


	public final static int REQUEST_CODE_MMLX = 10001;
	
	private TextView mMiddle;
	private ImageView mBack;
	
	private GridView mGridView;
	private int mCurrentSel = 0;
	private ArrayList<String> datas;
	private SetOnLineAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zq_trade_public_choose_activity);
		initView();
	}
	
	private void initView() {
		mGridView = (GridView) this.findViewById(R.id.zq_trade_public_choose_gv);
		
		mMiddle = (TextView) this.findViewById(R.id.tv_public_black_head_title_middle_name);
		mBack  = (ImageView) this.findViewById(R.id.img_public_black_head_title_left_blue_back);
		mMiddle.setVisibility(View.VISIBLE);
		mBack.setVisibility(View.VISIBLE);
		mMiddle.setText("请选择");
		mBack.setOnClickListener(this);
		
		
		datas = new ArrayList<String>();
		datas = (ArrayList<String>) getIntent().getSerializableExtra("mmlx");
		mCurrentSel = getIntent().getIntExtra("pwtype", 0);
		
		mAdapter=new SetOnLineAdapter(datas, this);
		mAdapter.setSeclection(mCurrentSel);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);
		
		
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
		ZQTradeMoreChooseBankActivity.this.finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.img_public_black_head_title_left_blue_back:
			{			
				ZQTradeMoreChooseBankActivity.this.finish();
			}
				break;

			default:
				break;
		}
	}


}
