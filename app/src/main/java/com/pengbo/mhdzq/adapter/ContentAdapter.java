package com.pengbo.mhdzq.adapter;

import java.util.ArrayList;

import com.pengbo.mhdzq.base.BasePager;


import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * adapter to init pager for each view
 * @author pobo
 */
public class ContentAdapter extends PagerAdapter {

	private ArrayList<BasePager> mPagers;
	private Activity mActivity;
	public int curUpdatePager;
	
	public ContentAdapter(ArrayList<BasePager> mPagers, Activity mActivity) {
		super();
		this.mPagers = mPagers;
		this.mActivity = mActivity;
	}

	@Override
	public int getCount() {
		return mPagers.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		BasePager pager = mPagers.get(position);
		pager.mRootView.setTag(position);
		container.addView(pager.mRootView);// 将页面布局添加到容器中
		pager.initDetailView();// 初始化数据
		return pager.mRootView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getItemPosition(Object object) {
		View view = (View)object;  
        if(curUpdatePager == (Integer)view.getTag()){  
            return POSITION_NONE;    
        }else{  
            return POSITION_UNCHANGED;  
        }  
	}

}

