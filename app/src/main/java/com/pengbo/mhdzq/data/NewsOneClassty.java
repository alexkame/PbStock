package com.pengbo.mhdzq.data;

/**
 * 新闻信息，包含了新闻标题，新闻ID和新闻发布的时间
 * @author pobo
 *
 */
public class NewsOneClassty {

	public String ID;
	public String PubTime;
	public String Title;

	public NewsOneClassty() {
		ID = "";
		PubTime = "";
		Title = "";
	}

	/**
	 * @param iD
	 * @param pubTime
	 * @param title
	 */
	public NewsOneClassty(String iD, String pubTime, String title) {
		super();
		ID = iD;
		PubTime = pubTime;
		Title = title;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getPubTime() {
		return PubTime;
	}

	public void setPubTime(String pubTime) {
		PubTime = pubTime;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	@Override
	public String toString() {
		return "NewsOneClassty [ID=" + ID + ", PubTime=" + PubTime + ", Title="
				+ Title + "]";
	}
	
	

}
