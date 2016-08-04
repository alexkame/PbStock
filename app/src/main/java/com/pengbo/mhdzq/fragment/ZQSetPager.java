package com.pengbo.mhdzq.fragment;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.pengbo.mhdcx.widget.SwitchButton;
import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.base.BasePager;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.widget.AlertDialog;
import com.pengbo.mhdzq.zq_activity.ZQSetAverageParamActivity;
/**
 * 证券里面的设置 
 * 
 * @author pobo
 * @date   2015-11-17 上午10:00:41
 * @className ZQSetPager.java
 * @verson 1.0.0
 */
public class ZQSetPager extends BasePager implements OnClickListener{
	private View view;//设置页面布局
	private View mJXCSSZ;// 均线参数设置        
	private SwitchButton mSB_QYZNJP,mSB_XDWXQR;//启用智能键盘 ,下单无需确认
	
	
	private boolean mQYZNJP= true;//启用智能键盘
	private boolean mXDWXQR = true;// 启用智能键盘 
	
	
	public ZQSetPager(Activity activity) {
		super(activity);
	}

	/**
	 * 继承方法     里面初始化控件 
	 */
	@Override
	public void initDetailView() {
		tvTitle.setVisibility(View.VISIBLE);
		tvTitle.setText("设置");
		
		view = View.inflate(mActivity, R.layout.activity_mainhomesetting, null);
		flContent.addView(view);
		bPagerReady = true;
		
		initData();
		bindView();
		
	}

	/**
	 * 初始化数据 
	 */
	private void initData() {
		mQYZNJP = PreferenceEngine.getInstance().getQDZNJP();//启用智能键盘
		mXDWXQR = PreferenceEngine.getInstance().getZQOrderWithoutConfirm();//下单无需确认
		
	}

	/**
	 * 初始化控件 
	 */
	private void bindView() {
		//均线参数设置 
		mJXCSSZ = view.findViewById(R.id.llayout_set_jxcssz);
		
		//启用智能键盘 
		mSB_QYZNJP = (SwitchButton) view.findViewById(R.id.switchbutton_qyzdjp);
		//下单无需确认 
		mSB_XDWXQR = (SwitchButton) view.findViewById(R.id.switchbutton_xdwxqr);
		mJXCSSZ.setOnClickListener(this);
		
		//智能键盘默认开
		mSB_QYZNJP.setChecked(mQYZNJP);
		mSB_QYZNJP.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					mQYZNJP = true;
				} else {
					mQYZNJP = false;
				}
				PreferenceEngine.getInstance().saveQDZNJP(mQYZNJP);
			}
			
		});
		
		
		//下单无需确认   默认 关 
		mSB_XDWXQR.setChecked(mXDWXQR);
		mSB_XDWXQR.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					new AlertDialog(mActivity).builder().setTitle("警告")
					.setMsg("打开此功能后，下单时将没有确认提示，请谨慎使用").setCancelable(false)
					.setCanceledOnTouchOutside(false).setPositiveButton("确认", new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							mXDWXQR = true;
							PreferenceEngine.getInstance().saveZQOrderWithoutConfirm(true);
						}
					}).setNegativeButton("取消", new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							mSB_XDWXQR.setChecked(false);							
						}
					}).show();
					
				} else {
					mXDWXQR = false;
					PreferenceEngine.getInstance().saveZQOrderWithoutConfirm(false);
				}
				
			}
			
		});
		
	}

	@Override
	public void visibleOnScreen() {}

	@Override
	public void invisibleOnScreen() {}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.llayout_set_jxcssz:
			Intent intent=new Intent(mActivity, ZQSetAverageParamActivity.class);
			mActivity.startActivity(intent);
	
			break;

		default:
			break;
		}
	}

}
