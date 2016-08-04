package com.pengbo.mhdcx.view;

import java.util.ArrayList;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.tools.ViewTools;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MyTListView extends ListView {

	private ArrayList<MyTListView> mListViewArray;
	boolean mbLeftToRight = true;
	private int mNumCurrentScreen = 2;
	private int mScrollWidth;
	public boolean sleftouched = false;

	public LinearLayout mListHead;
	public boolean mbNegation = false;
	private final int SCROLL_MIN_DISTANCE = 25;
	private boolean isMoveSelf = false;
	Handler mHandler;

	public MyTListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mListViewArray = new ArrayList<MyTListView>();
	}

	public void AddRelatedListView(MyTListView listView) {
		if (mListViewArray == null) {
			mListViewArray = new ArrayList<MyTListView>();
		}
		mListViewArray.add(listView);
	}
	
	
	public void RemoveAllRelatedListView()
	{
		if(mListViewArray != null)
		{
			mListViewArray.clear();
		}
	}

	public void onTouch(MotionEvent ev) {
		super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		mbNegation = false;
		for (int i = 0; i < mListViewArray.size(); i++) {
			{
				if (!ViewTools.getHandSetInfoSpecial()) {
					mListViewArray.get(i).mbNegation = true;
					mListViewArray.get(i).onTouch(ev);
				}
			}
		}
		return super.dispatchTouchEvent(ev);

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isMoveSelf = true;
			break;
		case MotionEvent.ACTION_UP:
			isMoveSelf = false;
			break;
		default:
			break;
		}

		for (int i = 0; i < mListViewArray.size(); i++) {
			{
				mListViewArray.get(i).isMoveSelf = false;
			}
		}

		return super.onTouchEvent(ev);

	}

	public ArrayList<MyTListView> getRelatedView() {
		return mListViewArray;
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

	public boolean isMoveSelf() {
		return isMoveSelf;
	}

}
