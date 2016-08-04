package com.pengbo.mhdzq.data;

import java.util.Arrays;

/**
 * 
 * @author pobo
 *
 */
public class NewsTitle {

	public String mTitle;
	public String[] mIDs;

	/**
	 * @param sEQ
	 * @param newsID
	 * @param pubTime
	 * @param title
	 * @param type
	 */

	public String getTitle() {
		return mTitle;
	}

	/**
	 * @param title
	 * @param iD
	 */
	public NewsTitle(String title, String[] ids) {
		this.mTitle = title;
		this.mIDs = ids;
	}

	public NewsTitle() {

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
		return "NewsTitle [mTitle=" + mTitle + ", mIDs="
				+ Arrays.toString(mIDs) + "]";
	}
	
	

}
