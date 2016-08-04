package com.pengbo.mhdzq.data;

import java.util.Arrays;

/**
 * 
 * @author pobo
 * 
 */
public class NewsHomepager {
	public String mTitle;
	public String[] mIDs;

	/**
	 * @param title
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * @param title
	 * @param iD
	 */
	public NewsHomepager(String title, String[] ids) {
		this.mTitle = title;
		this.mIDs = ids;
	}

	public NewsHomepager() {
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public String[] getID() {
		return mIDs;
	}

	public void setID(String[] ids) {
		mIDs = ids;
	}

	@Override
	public String toString() {
		return "NewsHomepager [mTitle=" + mTitle + ", mIDs="
				+ Arrays.toString(mIDs) + "]";
	}
}
