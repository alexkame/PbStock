package com.pengbo.mhdzq.view;

import com.pengbo.mhdzq.R;
import com.pengbo.mhdzq.tools.ScrollLayout;
import com.pengbo.mhdzq.tools.ScrollLayout.onScrollChangedListener;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PageControlView extends LinearLayout {
	private Context context;

	private int count;

	public void bindScrollViewGroup(ScrollLayout scrollViewGroup) {
		this.count = scrollViewGroup.getChildCount();
		System.out.println("count=" + count);
		generatePageControl(scrollViewGroup.getCurrentScreenIndex());

		scrollViewGroup.onChangeListener = new onScrollChangedListener() {
			@Override
			public void onChangeEvent(int currentIndex) {
				generatePageControl(currentIndex);
			}
		};
	}

	public PageControlView(Context context) {
		super(context);
		this.init(context);
	}

	public PageControlView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init(context);
	}

	private void init(Context context) {
		this.context = context;
	}

	private void generatePageControl(int currentIndex) {
		this.removeAllViews();

		int pageNum = 6;
		int pageNo = currentIndex + 1;
		int pageSum = this.count;

		if (pageSum > 1) {
			int currentNum = (pageNo % pageNum == 0 ? (pageNo / pageNum) - 1
					: (int) (pageNo / pageNum)) * pageNum;

			if (currentNum < 0)
				currentNum = 0;

			if (pageNo > pageNum) {
				ImageView imageView = new ImageView(context);
				imageView
						.setImageResource(R.drawable.img22_xiaoxi_point_unselect);
				this.addView(imageView);
			}

			for (int i = 0; i < pageNum; i++) {
				if ((currentNum + i + 1) > pageSum || pageSum < 2)
					break;

				ImageView imageView = new ImageView(context);
				imageView.setPadding(10, 10, 10, 10);
				if (currentNum + i + 1 == pageNo) {
					// 实心圆点
					imageView
							.setImageResource(R.drawable.img21_xiaoxi_point_select);
				} else {
					// 空心圆点
					imageView
							.setImageResource(R.drawable.img22_xiaoxi_point_unselect);
				}
				this.addView(imageView);
			}

			if (pageSum > (currentNum + pageNum)) {
				ImageView imageView = new ImageView(context);
				imageView
						.setImageResource(R.drawable.img22_xiaoxi_point_unselect);
				this.addView(imageView);
			}
		}
	}
}