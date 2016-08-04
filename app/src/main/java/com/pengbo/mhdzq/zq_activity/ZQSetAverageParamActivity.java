package com.pengbo.mhdzq.zq_activity;

import java.util.ArrayList;
import java.util.List;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.ZqParamAdapter;
import com.pengbo.mhdzq.base.Param;
import com.pengbo.mhdzq.tools.PreferenceEngine;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 均线 类 
 * 
 * @author pobo
 * @date   2015-11-17 下午1:17:09
 * @className SetAverageParamActivity.java
 * @verson 1.0.0
 */
public class ZQSetAverageParamActivity extends HdActivity implements OnClickListener{
	private ImageView mBack;
	private TextView mSave;
	private TextView mTitleName;
	private ListView mListView;
	private ZqParamAdapter mAdapter;
	private List<Param> mData;
	private String mMa;
	private String [] mMAs;
	private static final int SHOW_MA_NUM=5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zq_set_average_param_activity);
		initView();
	}

	private void initView() {
		mBack=(ImageView) this.findViewById(R.id.img_public_black_head_title_left_blue_back);
		mSave=(TextView) this.findViewById(R.id.tv_public_black_head_title_right_blue_save);
		mTitleName = (TextView) this.findViewById(R.id.tv_public_black_head_title_middle_name);
		
		mBack.setVisibility(View.VISIBLE);
		mSave.setVisibility(View.VISIBLE);
		mTitleName.setVisibility(View.VISIBLE);
		
		mTitleName.setText("均线参数设置");
		mBack.setOnClickListener(this);
		mSave.setOnClickListener(this);
		
	
		
		mListView=(ListView) this.findViewById(R.id.listview);
		
		
		
		mData=new ArrayList<Param>();
		mMa=PreferenceEngine.getInstance().getMA();
		mMAs=mMa.split(",");
		for (int i = 0; i < SHOW_MA_NUM; i++) {
			Param param=new Param();
			param.setmJXName("第"+(i+1)+"条 均线");
			param.setmJXEditDay(mMAs[i]);
			param.setmJXR("日");
			mData.add(param);
		}
		
		mAdapter=new ZqParamAdapter(this, mData);
		mListView.setAdapter(mAdapter);
	}
	
	private void saveSetting()
	{
		String ma = "";
		for(int i = 0; i < mAdapter.getCount(); i++)
		{
			ma += ((Param)mAdapter.getItem(i)).getmJXEditDay();
			if(i < mAdapter.getCount() - 1)
			{
				ma += ",";
			}
		}
		PreferenceEngine.getInstance().saveMA(ma);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.img_public_black_head_title_left_blue_back:
			ZQSetAverageParamActivity.this.finish();
			
			break;
		case R.id.tv_public_black_head_title_right_blue_save:
			saveSetting();
			ZQSetAverageParamActivity.this.finish();
			break;

		default:
			break;
		}
	}
}
