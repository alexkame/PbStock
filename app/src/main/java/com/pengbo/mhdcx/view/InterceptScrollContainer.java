package com.pengbo.mhdcx.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 一个视图容器控件
 * 阻止 拦截 ontouch事件传递给其子控件
 * @author pobo
 *
 */
public class InterceptScrollContainer extends LinearLayout {

	public InterceptScrollContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InterceptScrollContainer(Context context) {
		super(context);
	}
	
	/**
	 *ViewGroup里的onInterceptTouchEvent默认值是false这样才能把事件传给View里的onTouchEvent.
	 *ViewGroup里的onTouchEvent默认值是false。
	 *View里的onTouchEvent返回默认值是true.这样才能执行多次touch事件。
	 */	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}
}
