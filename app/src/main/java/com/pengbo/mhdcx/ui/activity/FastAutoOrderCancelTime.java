package com.pengbo.mhdcx.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdcx.adapter.SetOnLineAdapter;
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
 * 快捷反手自动撤单时间
 * @author pobo
 *
 */
public class FastAutoOrderCancelTime extends BaseActivity implements OnItemClickListener,OnClickListener{
	public static final int RESULT_CODE_FastAutoCancelOrderTime=10005;
	private TextView fastAutoCancelOrder_back;
	private GridView gv;
	private int mCancelTime;
	private int mSelectIndex;
	private List<String> datas;
	private SetOnLineAdapter mAdapter;

	String[] times_name = new String[] { "10秒（默认） ","20秒", "30秒"};
	int[] times = new int[] {10, 20, 30};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setonlinetime);
		mCancelTime = this.getIntent().getIntExtra(SettingActivity.INTENT_KJFSCDTIME, 10);

		for (int i = 0; i < times.length; i++)
		{
			if (mCancelTime == times[i])
			{
				mSelectIndex = i;
				break;
			}
		}
		
		initView();
	}

	private void initView() {

		gv=(GridView) this.findViewById(R.id.setonlinetime_gv);
		fastAutoCancelOrder_back=(TextView) this.findViewById(R.id.setonlinetime_backbtn);
		datas=new ArrayList<String>();		
		for (int i = 0; i <times_name.length; i++) {
			datas.add(times_name[i]);
		}
		mAdapter=new SetOnLineAdapter(datas, this);
		mAdapter.setSeclection(mSelectIndex);
		gv.setAdapter(mAdapter);
		gv.setOnItemClickListener(this);
		fastAutoCancelOrder_back.setOnClickListener(this);
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mAdapter.setSeclection(position);
		mAdapter.notifyDataSetChanged();		
		mSelectIndex = position;
		mCancelTime = times[mSelectIndex];
		
		PreferenceEngine.getInstance().saveTradeKJFSAutoCDTime(mCancelTime);
		Intent intent = new Intent();
		intent.putExtra(SettingActivity.INTENT_KJFSCDTIME, mCancelTime);
		setResult(RESULT_CODE_FastAutoCancelOrderTime, intent);
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
