package com.pengbo.mhdzq.adapter;

import java.util.ArrayList;

import com.pengbo.mhdzq.constant.AppConstants;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * the adapter for the viewpager which is displaying the imageview in the
 * homepager
 * 
 * @author pobo
 * 
 */
public class HomePagerAdapter extends PagerAdapter {

	private ArrayList list;
	private Context context;

	public HomePagerAdapter(ArrayList list, Context context) {
		super();
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	/**
	 * 而isViewFromObject方法是用来判断pager的一个view是否和instantiateItem方法返回的object有关联，
	 * 如果有关联做什么呢？去看代码吧
	 */
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	/**
	 * 一般来说，destroyitem在viewpager移除一个item时调用。
	 * viewpage一般都会缓冲3个item，即一开始就会调用3次instantiateItem,
	 * 当向右滑动，到第3页时，第1页的item会被调用到destroyitem。
	 * 
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	/**
	 * 该方法声明了返回值不一定是view，可以是任意对象。要知道view的添加是在该方法内部，通过container来添加的，
	 * 所以这个方法不一定要返回。 view Create the page for the given position. The adapter is
	 * responsible for adding the view to the container given here, although it
	 * only must ensure this is done by the time it returns from
	 * finishUpdate(ViewGroup).
	 * 
	 * @param container
	 *            The containing View in which the page will be shown.
	 * @param position
	 *            The page position to be instantiated
	 */
	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		ImageView iv = (ImageView) (list.get(position));
		container.addView(iv);

		iv.setOnClickListener(new OnClickListener() {

			/**
			 * 单击图片则跳转至相应的网站
			 */
			@Override
			public void onClick(View v) {
				if (AppConstants.HOMAPAGE_ADS.isEmpty()) {
					return;
				}
				Uri uri = Uri.parse(AppConstants.HOMAPAGE_ADS);
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(it);
			}
		});
		return list.get(position);
	}
}
