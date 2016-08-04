package com.pengbo.mhdcx.view;

import java.util.ArrayList;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdcx.view.MyHScrollView.OnScrollChangedListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MyTHScrollView extends MyHScrollView {

	private MyTHScrollView mRelatedScrollView = null;
	private MyTHScrollView mRelatedHeadScrollView = null;
	private MyTHScrollView mRelatedHeadView = null;
	private MyTHScrollView mlistenerview = null;
	public boolean sleftouched = false;;
	private int mNumCurrentScreen = 2;
	private int mScrollWidth;
	boolean mbLeftToRight = true;

	public LinearLayout mListHead;
	public LinearLayout mRelatedListHead;

	private final int SCROLL_MIN_DISTANCE = 25;

	public MyTHScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyTHScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyTHScrollView(Context context) {
		super(context);
	}

	public void setRelatedScrollView(MyTHScrollView view) {
		mRelatedScrollView = view;
	}

	public void setRelatedHeadView(MyTHScrollView view) {
		mRelatedHeadView = view;
	}

	public void setRelatedHeadScrollView(MyTHScrollView view) {
		mRelatedHeadScrollView = view;
	}

	public MyTHScrollView getRelatedScrollView() {
		return mRelatedScrollView;
	}

	public MyTHScrollView getRelatedHeadScrollView() {
		return mRelatedHeadScrollView;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		sleftouched = true;
		if (mRelatedScrollView != null) {
			mRelatedScrollView.sleftouched = false;
		}
		// mGesture.onTouchEvent(ev);
		return super.onTouchEvent(ev);
	}

	public void AddOnScrollChangedListener(OnScrollChangedListener listener) {
		mScrollViewObserver.AddOnScrollChangedListener(listener);
	}

	public void setWidth(int width) {
		mScrollWidth = width;
	}

	public void setScreenItemNum(int num) {
		this.mNumCurrentScreen = num;
	}

	public void setLeftToRight(boolean bLeft) {
		this.mbLeftToRight = bLeft;
	}

	public void resetToDefaultPos() {
		if (this.mbLeftToRight) {
			this.postDelayed(new Runnable() {
				@Override
				public void run() {
					MyTHScrollView.this.scrollTo(0, 0);
				}
			}, 300);
		} else {
			// 避免在某些机器上因为layout未初始化完成造成的scroll无效。
			this.postDelayed(new Runnable() {
				@Override
				public void run() {

					MyTHScrollView.this
							.scrollTo(
									mScrollWidth
											* (AppConstants.TLIST_HEADER_ITEMS - mNumCurrentScreen)
											/ AppConstants.TLIST_HEADER_ITEMS,
									0);
				}
			}, 300);
		}
	}

}
