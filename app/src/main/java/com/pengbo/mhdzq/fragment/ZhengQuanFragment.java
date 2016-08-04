package com.pengbo.mhdzq.fragment;

import java.util.ArrayList;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.adapter.ContentAdapter;
import com.pengbo.mhdzq.app.MyApp;
import com.pengbo.mhdzq.base.BaseFragment;
import com.pengbo.mhdzq.base.BasePager;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.data.KeyDefine;
import com.pengbo.mhdzq.tools.MIniFile;
import com.pengbo.mhdzq.tools.PreferenceEngine;
import com.pengbo.mhdzq.zq_activity.ZQMarketDetailActivity;
import com.pengbo.mhdzq.zq_trade_activity.ZqTradeDetailActivity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * 证券的fragment to init subpagers
 * 
 * @author pobo
 */
public class ZhengQuanFragment extends BaseFragment {

	public static final int ZQ_VIEW_ZIXUAN = 0, ZQ_VIEW_HANGQING = 1,
			ZQ_VIEW_JIAOYI = 2, ZQ_VIEW_Set = 3;
	private ArrayList<BasePager> mPagers;

	private ViewPager mViewPager;

	private RadioGroup rb_radioGroup;
	private RadioButton rb_home;
	private RadioButton rb_zixuan;
	private RadioButton rb_hangqing;
	private RadioButton rb_jiaoyi;
	private RadioButton rb_set;
	public int mCurrentView = ZQ_VIEW_HANGQING;
	private int mStockMarket = -1;
	private String mStockCode = "";
	private int mIndex_Trade = 0;// 交易详情页面索引，买入-卖出-委托-等
	private HangQingPager mHQPger;
	private int mId = 0;
	private boolean bFirstInFragment = true;

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.zq_fragment_zhengquan,
				null);

		mViewPager = (ViewPager) view.findViewById(R.id.vp_content);
		rb_radioGroup = (RadioGroup) view.findViewById(R.id.rb_radioGroup);
		rb_home = (RadioButton) view.findViewById(R.id.rb_home);
		rb_zixuan = (RadioButton) view.findViewById(R.id.rb_zixuan);
		rb_hangqing = (RadioButton) view.findViewById(R.id.rb_hangqing);
		rb_jiaoyi = (RadioButton) view.findViewById(R.id.rb_jiaoyi);
		rb_set = (RadioButton) view.findViewById(R.id.rb_zq_set);

		return view;
	}

	public void initData() {

		bFirstInFragment = true;
		// 初始化4个标签页面
		mPagers = new ArrayList<BasePager>();
		mPagers.add(new ZiXuanPager(mActivity));

		Bundle data = getArguments();
		int mId = data.getInt("mId");
		mHQPger = new HangQingPager(mActivity, mId);
		mPagers.add(mHQPger);

		if (mMyApp.mTradeData.mHQType == AppConstants.HQTYPE_ZQ
				&& mMyApp.mTradeData.mTradeLoginFlag) {
			mPagers.add(new JiaoYiChiCangPager(mActivity));
		} else {
			// smsverify=false
			MIniFile iniFile = new MIniFile();
			iniFile.setFilePath(mActivity.getApplicationContext(),
					MyApp.TRADE_ADDRPATH);
			String smsverify = iniFile.ReadString("base", "smsverify", "false");
			if (smsverify.equalsIgnoreCase("true")) {
				mMyApp.isSMSVerify = true;
			} else {
				mMyApp.isSMSVerify = false;
			}
			String strPhone = PreferenceEngine.getInstance()
					.getPhoneForVerify();
			if (mMyApp.isSMSVerify && strPhone.isEmpty()) {
				mPagers.add(new JiaoYiSMSVerifyPager(mActivity));
			} else {
				mPagers.add(new JiaoYiPager(mActivity));
			}

		}
		mPagers.add(new ZQSetPager(mActivity));

		mViewPager.setAdapter(new ContentAdapter(mPagers, mActivity));
		rb_radioGroup.setOnCheckedChangeListener(new CheckedChangeListener());
		mViewPager.setOnPageChangeListener(new PagerChangeListener());

		mStockMarket = mActivity.getIntent().getIntExtra("STOCK_MARKET", -1);
		mStockCode = mActivity.getIntent().getStringExtra("STOCK_CODE");
		mCurrentView = mActivity.getIntent().getIntExtra("ZQ_VIEW_INDEX",
				ZQ_VIEW_HANGQING);
		mIndex_Trade = mActivity.getIntent().getIntExtra(
				JiaoYiChiCangPager.INTENT_TRADE_PADE_INDEX, 0);

		if (mId == KeyDefine.KEY_MARKET_ZQJY) {
			mCurrentView = ZQ_VIEW_JIAOYI;
			mViewPager.setCurrentItem(ZQ_VIEW_JIAOYI);
		} else if (mId == KeyDefine.KEY_MARKET_SELF) {
			mCurrentView = ZQ_VIEW_ZIXUAN;
			mViewPager.setCurrentItem(ZQ_VIEW_ZIXUAN);
			rb_zixuan.setChecked(true);
		} else {
			mViewPager.setCurrentItem(mCurrentView);
		}
	}

	public void updateViewPagerItem(BasePager pager, int index) {
		((ContentAdapter) mViewPager.getAdapter()).curUpdatePager = index;
		mPagers.remove(index);
		mPagers.add(index, pager);

		if (mMyApp.mTradeData.mHQType == mMyApp.getCurrentHQType()
				&& mMyApp.mTradeData.mTradeLoginFlag && mStockMarket != -1
				&& mStockCode != null && !mStockCode.isEmpty()
				&& mIndex_Trade >= 0) {
			Intent intent = new Intent(mActivity, ZqTradeDetailActivity.class);
			intent.putExtra(JiaoYiChiCangPager.INTENT_TRADE_PADE_INDEX,
					mIndex_Trade);
			intent.putExtra("STOCK_MARKET", mStockMarket);
			intent.putExtra("STOCK_CODE", mStockCode);
			startActivity(intent);

			mStockMarket = -1;
			mStockCode = "";
			mIndex_Trade = -1;
		} else {
			mViewPager.getAdapter().notifyDataSetChanged();
			pager.visibleOnScreen();
		}
	}

	public void clearMarketAndCode() {
		mStockMarket = -1;
		mStockCode = "";
		mIndex_Trade = -1;
	}

	class PagerChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {

			if (!mMyApp.mTradeData.mTradeLoginFlag
					&& mCurrentView == ZQ_VIEW_JIAOYI && !bFirstInFragment) {
				String strPhone = PreferenceEngine.getInstance()
						.getPhoneForVerify();
				if (mMyApp.isSMSVerify && strPhone.isEmpty()) {
					updateViewPagerItem(new JiaoYiSMSVerifyPager(mActivity), 2);
				} else {
					updateViewPagerItem(new JiaoYiPager(mActivity), 2);
				}
			}
			bFirstInFragment = false;

			for (int i = 0; i < mPagers.size(); i++) {
				if (i != position) {
					mPagers.get(i).invisibleOnScreen();
				}
			}
			mPagers.get(position).visibleOnScreen();

			switch (position) {
			case 0:
				mCurrentView = ZQ_VIEW_ZIXUAN;
				rb_zixuan.setChecked(true);
				break;
			case 1:
				mCurrentView = ZQ_VIEW_HANGQING;
				rb_hangqing.setChecked(true);
				break;
			case 2:
				mCurrentView = ZQ_VIEW_JIAOYI;
				rb_jiaoyi.setChecked(true);
				break;
			case 3:
				mCurrentView = ZQ_VIEW_Set;
				rb_set.setChecked(true);
				break;
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}
	}

	class CheckedChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.rb_home:
				mActivity.finish();
				break;
			case R.id.rb_zixuan:
				mViewPager.setCurrentItem(0);
				mCurrentView = ZQ_VIEW_ZIXUAN;
				break;
			case R.id.rb_hangqing:
				mViewPager.setCurrentItem(1);
				mCurrentView = ZQ_VIEW_HANGQING;
				break;
			case R.id.rb_jiaoyi:
				mViewPager.setCurrentItem(2);
				mCurrentView = ZQ_VIEW_JIAOYI;
				break;
			case R.id.rb_zq_set:
				mViewPager.setCurrentItem(3);
				mCurrentView = ZQ_VIEW_Set;
				break;
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		int pagerIndex = mViewPager.getCurrentItem();
		mPagers.get(pagerIndex).invisibleOnScreen();
	}

	@Override
	public void onResume() {
		super.onResume();
		int pagerIndex = mViewPager.getCurrentItem();
		mViewPager.getAdapter().notifyDataSetChanged();
		mPagers.get(pagerIndex).visibleOnScreen();
	}

}
