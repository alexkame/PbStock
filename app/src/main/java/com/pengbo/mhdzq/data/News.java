package com.pengbo.mhdzq.data;
/**
 * 
 * @author pobo
 *
 */
public class News {

	public String SEQ;
	public String NewsID;
	public String PubTime;
	public String Title;
	public int Type;

	public String getSEQ() {
		return SEQ;
	}

	public void setSEQ(String sEQ) {
		SEQ = sEQ;
	}

	/**
	 * @param sEQ
	 * @param newsID
	 * @param pubTime
	 * @param title
	 * @param type
	 */
	public News(String sEQ, String newsID, String pubTime, String title, int type) {
		super();
		SEQ = sEQ;
		NewsID = newsID;
		PubTime = pubTime;
		Title = title;
		Type = type;
	}

	@Override
	public String toString() {
		return "News [SEQ=" + SEQ + ", NewsID=" + NewsID + ", PubTime=" + PubTime + ", Title=" + Title + ", Type=" + Type + "]";
	}

	public News() {

	}

	public String getNewsID() {
		return NewsID;
	}

	public void setNewsID(String newsID) {
		NewsID = newsID;
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

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

}
