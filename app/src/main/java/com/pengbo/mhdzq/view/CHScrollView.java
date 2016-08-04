package com.pengbo.mhdzq.view;

import com.pengbo.mhdzq.zq_activity.HdActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class CHScrollView extends HorizontalScrollView {
	/**
	 * 包括该HorizontalScrollView的Activity
	 */
	HdActivity activity;

	public CHScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		activity = (HdActivity) context;
	}

	public CHScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		activity = (HdActivity) context;
	}

	public CHScrollView(Context context) {
		super(context);
		activity = (HdActivity) context;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		activity.mTouchView = this;
		return super.onTouchEvent(ev);
		// return false;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if (activity.mTouchView == this) {
			activity.onScrollChanged(l, t, oldl, oldt);
		} else {
			super.onScrollChanged(l, t, oldl, oldt);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev);
	}
}
