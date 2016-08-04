package com.pengbo.mhdzq.main_activity;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.zq_activity.HdActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ObserverActivity extends HdActivity implements OnClickListener {

	private TextView mMiddleTitle;
	private ImageView mImgBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zq_pic_activity);
		mMiddleTitle = (TextView) findViewById(R.id.tv_public_black_head_title_middle_name);
		mImgBack = (ImageView) findViewById(R.id.img_public_black_head_title_left_blue_back);
		mMiddleTitle.setVisibility(View.VISIBLE);
		mImgBack.setVisibility(View.VISIBLE);
		mMiddleTitle.setText(R.string.ZQ_CaiYGC);
		mImgBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_public_black_head_title_left_blue_back:
			ObserverActivity.this.finish();
			break;

		default:
			break;
		}
	}

	
	
}
