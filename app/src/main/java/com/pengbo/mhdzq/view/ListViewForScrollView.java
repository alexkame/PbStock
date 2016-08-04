package com.pengbo.mhdzq.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
import android.widget.ScrollView;

public class ListViewForScrollView extends ListView {
	public ScrollView parentScrollView;

	public ListViewForScrollView(Context context) {
		super(context);
	}

	public ListViewForScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ListViewForScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 重写 该方法 达到使 ListView适应 ScrollView 的效果
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int listViewSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);

		// NullPointerException
		if ((Integer) widthMeasureSpec == null) {
			widthMeasureSpec = MeasureSpec.AT_MOST;
		}
		super.onMeasure(widthMeasureSpec, listViewSpec);
	}

	private void setParentScrollAble(boolean flag) {
		// parentScrollView.requestDisallowInterceptTouchEvent(!flag);
		parentScrollView.requestDisallowInterceptTouchEvent(!flag);
	}
}
