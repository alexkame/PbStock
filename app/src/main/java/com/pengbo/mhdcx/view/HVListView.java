package com.pengbo.mhdcx.view;

import java.util.ArrayList;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.constant.AppConstants;
import com.pengbo.mhdzq.tools.L;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector.OnGestureListener;
import android.view.animation.AnimationUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * 支持横向滑动的ListView
 * 
 */
public class HVListView extends ListView {

	// 手势
	private GestureDetector mGesture;
	private OnGestureListener mOnGesture;
	// list列头
	public LinearLayout mListHead;
	// 偏移坐标
	private int mOffset;
	// 屏幕宽度
	private int mScreenWidth;
	// listview宽度
	private int mListWidth;

	private int mScrollWidth;

	public int mode = MODE_DOWN;
	public final static int MODE_DOWN = 1;
	public final static int MODE_HDRAG = 2;

	// Scroll
	private final int SCROLL_MIN_DISTANCE = 25; // Unit:pixel/s

	// Fling
	private final int FLING_MIN_DISTANCE = 50; // Unit:pixel/s
	private final int FLING_MIN_VELOCITY = 50; // Unit:pixel/s

	private final float FLING_VELOCITY_UP_CRITICAL = -3000.0f; // Unit:pixel/s
	private final float FLING_VELOCITY_DOWN_CRITICAL = 3000.0f; // Unit:pixel/s

	private final float VELOCITY_FACTOR = 1.05f;
	private float mVx = 0.0f, mVx_last = 0.0f;
	private MotionEvent me1, me2; // record the motion event in the fling
									// function
	private FlingThread flingThread;
	private boolean mInterrupted = false;

	private long mStartTime;
	private final float mDeceleration;

	private ArrayList<HVListView> mListViewArray;// 需要联动的listview
	// private HVListView mListView2;
	private boolean mbLeftToRight = true;//
	private boolean mbHDragEnabled = true;
	public boolean mbNegation = false;// 是否反向滑动
	private int mNumCurrentScreen = 2;// 当前屏幕范围内显示item个数
	private int mItemId = R.id.hv_item;
	private int mHeadId = R.id.hv_head;

	Handler mHandler;

	public HVListView(Context context, AttributeSet attrs) {
		super(context, attrs);

		//
		float ppi = context.getResources().getDisplayMetrics().density * 160.0f;
		mDeceleration = SensorManager.GRAVITY_EARTH // g (m/s^2)
				* 39.37f // inch/meter
				* ppi // pixels per inch
				* ViewConfiguration.getScrollFriction();

		mListViewArray = new ArrayList<HVListView>();
		mListWidth = this.getWidth();
		//
		initHandler();
		initGesture();
		mGesture = new GestureDetector(context, mOnGesture);

	}

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// process incoming messages here
				switch (msg.what) {
				case 200: {
					if (mVx_last == msg.arg1)
						break;

					int distanceX = -msg.arg1;

					procGestureMove(distanceX);

					mVx_last = msg.arg1;
				}
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	public boolean onDispatch(MotionEvent ev) {
		mGesture.onTouchEvent(ev);

		try {
			return super.dispatchTouchEvent(ev);
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		this.mbNegation = false;
		if (this.mNumCurrentScreen > 1) {
			this.mbHDragEnabled = true;
		}
		{
			for (int i = 0; i < mListViewArray.size(); i++) {
				if (null != mListViewArray.get(i)) {
					mListViewArray.get(i).mbNegation = true;
					if (mNumCurrentScreen <= 1) // 如果滑动事件触发的是只有一个item的list，这样其他联动list不需要左右滑动
					{
						mListViewArray.get(i).mbHDragEnabled = false;
					} else if (mListViewArray.get(i).mNumCurrentScreen > 1) {
						mListViewArray.get(i).mbHDragEnabled = true;
					}
					mListViewArray.get(i).onDispatch(ev);
				}
			}
		}

		mGesture.onTouchEvent(ev);

		try {
			return super.dispatchTouchEvent(ev);
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}
	
	public void setItemId(int itemid) {
		this.mItemId = itemid;
	}
	
	public void setHeadId(int headid) {
		this.mHeadId = headid;
	}

	public void setLeftToRight(boolean bLeft) {
		this.mbLeftToRight = bLeft;
	}

	public void setHDragEnable(boolean bEnable) {
		this.mbHDragEnabled = bEnable;
	}

	public void setScreenItemNum(int num) {
		this.mNumCurrentScreen = num;
	}

	public void AddRelatedListView(HVListView listView) {
		if (mListViewArray == null) {
			mListViewArray = new ArrayList<HVListView>();
		}
		mListViewArray.add(listView);
	}

	private void initGesture() {
		mOnGesture = new GestureDetector.SimpleOnGestureListener() {

			@Override
			public boolean onDown(MotionEvent e) {
				mode = MODE_DOWN;
				//
				mInterrupted = true;
				return false;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {

				if(e1 == null || e2 == null)
				{
					return true;
				}
				
				me1 = e1;
				me2 = e2;

				mStartTime = AnimationUtils.currentAnimationTimeMillis();

				// 计算滑动的距离
				int dx = (int) (e2.getX() - e1.getX());

				// 降噪处理，必须有较大的动作才处理
				if (Math.abs(dx) > FLING_MIN_DISTANCE
						&& Math.abs(velocityX) > Math.abs(velocityY)
						&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
					//
					mInterrupted = false;

					mVx = velocityX;
					if (mVx > FLING_VELOCITY_DOWN_CRITICAL)
						mVx = FLING_VELOCITY_DOWN_CRITICAL;
					else if (mVx < FLING_VELOCITY_UP_CRITICAL)
						mVx = FLING_VELOCITY_UP_CRITICAL;

					if (flingThread == null) {
						mVx_last = 0;
						flingThread = new FlingThread();
						flingThread.start();
					}
				}
				return true;
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				// 降噪处理，必须有较大的动作才处理
				if (e1 == null || e2 == null) {
					return true;
				}

				if (Math.abs(e2.getX() - e1.getX()) > SCROLL_MIN_DISTANCE) {
					procGestureMove((int) distanceX);
				}
				return true;
			}
		};
	}

	/**
	 * 获取屏幕可见范围内最大宽度
	 * 
	 * @return
	 */
	public int getScreenWidth() {
		if (mScreenWidth == 0) {
			mScreenWidth = this.getWidth();
		}
		return mScreenWidth;
	}

	// 获取列头偏移量
	public int getHeadScrollX() {
		return mListHead.getScrollX();
	}

	public void setWidth(int width) {
		mScrollWidth = width;
	}

	public void resetToDefaultPos() {
		for (int i = 0, j = getChildCount(); i < j; i++) {
			View child = ((ViewGroup) getChildAt(i)).findViewById(mItemId);
			if (child != null) {
				if (this.mbLeftToRight) {
					child.scrollTo(0, 0);
				} else {
					child.scrollTo(
							mScrollWidth
									* (AppConstants.TLIST_HEADER_ITEMS - mNumCurrentScreen)
									/ AppConstants.TLIST_HEADER_ITEMS, 0);
				}
			}
		}
		if (this.mbLeftToRight) {
			mListHead.scrollTo(0, 0);
			mOffset = 0;
		} else {
			mListHead.scrollTo(mScrollWidth
					* (AppConstants.TLIST_HEADER_ITEMS - mNumCurrentScreen)
					/ AppConstants.TLIST_HEADER_ITEMS, 0);
			mOffset = mScrollWidth
					* (AppConstants.TLIST_HEADER_ITEMS - mNumCurrentScreen)
					/ AppConstants.TLIST_HEADER_ITEMS;
		}

	}

	private void procGestureMove(int distanceX) {
		L.d("HVListView",
				"procGestureMove--->this.getChildCount() = "
						+ this.getChildCount());

		try {
			if (getFooterViewsCount() > 0 && getChildCount() == 1) {
				L.e("HVListView", "procGestureMove--->getFooterViewsCount() = "
						+ getFooterViewsCount());
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			L.e("HVListView", "procGestureMove--->Exception 1");
			return;
		}

		try {
			if (((ViewGroup) getChildAt(0)) == null
					|| ((ViewGroup) getChildAt(0)).findViewById(mItemId) == null) {
				L.e("HVListView", "procGestureMove--->item1 = null");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			L.e("HVListView", "procGestureMove--->Exception 2");
			return;
		}

		synchronized (HVListView.this) {

			View temp_view = ((ViewGroup) getChildAt(0));
			if (temp_view != null) {
				ViewGroup temp_group = (ViewGroup) (temp_view
						.findViewById(mItemId));
				int temp_child_count = temp_group.getChildCount();
				if (temp_child_count <= mNumCurrentScreen || !mbHDragEnabled) {
					L.e("HVListView",
							"procGestureMove--->data column count <= 3!");
					return;
				}
			}

			int moveX = (int) distanceX;
			if (mbNegation) {
				moveX = -moveX;
			}
			int curX = mListHead.getScrollX();
			int scrollWidth = mScrollWidth;// getWidth();
			int dx = moveX;
			// 控制越界问题
			if (!mbLeftToRight) {
				if (curX + moveX < 0) {
					dx = -curX;
				}

				if (curX + moveX + getScreenWidth() > scrollWidth) {
					dx = scrollWidth - getScreenWidth() - curX;
				}
			} else {
				if (curX + moveX < 0) {
					dx = -curX;
				}

				if (curX + moveX + getScreenWidth() > scrollWidth) {
					dx = scrollWidth - getScreenWidth() - curX;
				}
			}

			mode = MODE_HDRAG;

			{
				mOffset += dx;
			}
			// 根据手势滚动item
			// L.d("onScroll", "getChildCount() = " + getChildCount());
			for (int i = 0, j = getChildCount(); i < j; i++) {
				// View child = ((ViewGroup) getChildAt(i)).getChildAt(1);
				View child = ((ViewGroup) getChildAt(i))
						.findViewById(mItemId);
				// L.d("HVList", "1 = " + getChildAt(i) + ", 2 = " +
				// ((ViewGroup) getChildAt(i)).getChildAt(1));
				if (child != null) {
					if (child.getScrollX() != mOffset)
						child.scrollTo(mOffset, 0);
				}
			}
			mListHead.scrollBy(dx, 0);
		}
		requestLayout();
	}

	// Fling animation thread
	private class FlingThread extends Thread {
		public FlingThread() {
		}

		public void resetValue() {
			mVx = 0;
			flingThread = null;
		}

		public void run() {
			while (true) {
				if (mInterrupted) {
					resetValue();
					break;
				}

				try {
					sleep(50);
				} catch (Exception e) {
					e.printStackTrace();
				}

				int timePassed = (int) (AnimationUtils
						.currentAnimationTimeMillis() - mStartTime);

				float timePassedSeconds = timePassed / 1000.0f;
				float distance = (mVx * timePassedSeconds)
						- (mDeceleration * timePassedSeconds
								* timePassedSeconds / 2.0f);

				if (mVx > 0) {
					mVx = mVx - mDeceleration * timePassedSeconds;
					if (mVx < -0.1f) {
						resetValue();
						break;
					}
				} else {
					mVx = mVx + mDeceleration * timePassedSeconds;
					if (mVx > 0.1f) {
						resetValue();
						break;
					}
				}

				if (mHandler != null) {
					mHandler.obtainMessage(200, (int) (distance), 0)
							.sendToTarget();
				}
			}
		}
	}

	@Override
	protected void onFinishInflate() {
		mListWidth = this.getWidth();
		super.onFinishInflate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mListWidth = this.getWidth();
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mListWidth = this.getWidth();
		super.onDraw(canvas);
	}
}
