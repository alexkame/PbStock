package com.pengbo.mhdzq.adapter;

import java.util.ArrayList;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
/**
 * adapter to display listview for each view
 * @author pobo
 */
public class HomePagerListViewAdapter extends PagerAdapter {
		 
		private ArrayList list;
		public HomePagerListViewAdapter(ArrayList list) {
			super();
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object)  {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			View iv = (View) (list.get(position));
			container.addView(iv);
			return list.get(position);
		}
	}


