package com.pengbo.mhdzq.base;

import com.pengbo.mhdzq.app.MyApp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * base Fragment
 * 
 * @author pobo
 */

public abstract class BaseFragment extends Fragment {

	public Activity mActivity;
	public MyApp mMyApp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
		mMyApp = (MyApp) mActivity.getApplication();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = initView();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
	}

	public abstract View initView();

	public void initData() {}
}
