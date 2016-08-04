/**
 * 
 */
package com.pengbo.mhdcx.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import com.pengbo.mhdcx.bean.News;
import com.pengbo.mhdzq.data.NewsOneClassty;
import com.pengbo.mhdzq.tools.STD;

import android.util.Xml;

/**
 * 
 * @author pobo
 * 
 */
public class HttpUtil {

	private static News news;
	private static NewsOneClassty mNewsOneClassty;

	// return news number
	public static int getNews(String code, short market, List<News> listData) {

		try {
			String path = String
					.format("http://124.74.201.22/HDNews2/Web/Hd_LatestNewsList.aspx?type=gg&market=%d&code=%s",
							market, code);

			HttpURLConnection hConnection = (HttpURLConnection) new URL(path)
					.openConnection();

			hConnection.setConnectTimeout(5000);
			hConnection.setRequestMethod("GET");
			if (hConnection.getResponseCode() == 200) {
				InputStream inputStream = hConnection.getInputStream();
				return paseXml(inputStream, listData);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * @param inputStream
	 * @return
	 */
	private static int paseXml(InputStream inputStream, List<News> listData) {

		try {
			XmlPullParser pullParser = Xml.newPullParser();
			pullParser.setInput(inputStream, "UTF-8");

			if (listData == null) {
				listData = new ArrayList<News>();
			}
			listData.clear();

			int event = pullParser.getEventType();

			while (event != XmlPullParser.END_DOCUMENT) {

				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					break;

				case XmlPullParser.START_TAG:
					if (news == null) {
						news = new News();
					}
					if ("News".equals(pullParser.getName())) {
						String SEQ = pullParser.getAttributeValue(0);
						news.setSEQ(SEQ);
					} else if ("NewsID".equals(pullParser.getName())) {
						news.setNewsID(pullParser.nextText());
					} else if ("PubTime".equals(pullParser.getName())) {
						news.setPubTime(pullParser.nextText());
					} else if ("Title".equals(pullParser.getName())) {
						news.setTitle(pullParser.nextText());
					} else if ("Type".equals(pullParser.getName())) {
						news.setType(STD.StringToInt(pullParser.nextText()));
					}
					break;

				case XmlPullParser.END_TAG:
					if ("News".equals(pullParser.getName())) {
						if (news != null) {
							listData.add(news);
							news = null;
						}
					}
					break;
				}
				event = pullParser.next();
			}
			return listData.size();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	// return news number
	public static int getNewss(String[] news_id, List<NewsOneClassty> mList,
			boolean isNewsCenter) {

		if (news_id == null || news_id.length <= 0) {
			if (mList == null) {
				mList = new ArrayList<NewsOneClassty>();
			}
			mList.clear();
			return 0;
		}
		try {
			String path = "";
			int nCount = news_id.length;
			// if (isNewsCenter)
			// {
			// path =
			// String.format("http://news.huidian.net/HDNews2/Web/Hd_LatestNewsList.aspx?secgroup=%s&newsid=1",
			// news_id[0]);
			// }else
			{
				path = String
						.format("http://news.huidian.net/HDNews2/Web/Hd_LatestNewsList.aspx?type=mu&gcount=%d",
								nCount);
				for (int i = 0; i < nCount; i++) {
					path = path
							+ String.format("&group%d=%s", i + 1, news_id[i]);
				}
			}

			HttpURLConnection hConnection = (HttpURLConnection) new URL(path)
					.openConnection();

			hConnection.setConnectTimeout(5000);
			hConnection.setRequestMethod("GET");
			if (hConnection.getResponseCode() == 200) {
				InputStream inputStream = hConnection.getInputStream();
				if (mList == null) {
					mList = new ArrayList<NewsOneClassty>();
				}
				return paseXmlNewss(inputStream, mList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * @param inputStream
	 * @return
	 */
	private static int paseXmlNewss(InputStream inputStream,
			List<NewsOneClassty> mList) {

		try {
			XmlPullParser pullParser = Xml.newPullParser();
			pullParser.setInput(inputStream, "UTF-8");

			if (mList == null) {
				mList = new ArrayList<NewsOneClassty>();
			}
			mList.clear();

			int event = pullParser.getEventType();

			while (event != XmlPullParser.END_DOCUMENT) {

				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					break;

				case XmlPullParser.START_TAG:
					if (mNewsOneClassty == null) {
						mNewsOneClassty = new NewsOneClassty();
					}
					if ("News".equals(pullParser.getName())) {
						String ID = pullParser.getAttributeValue(0);
						mNewsOneClassty.setID(ID);
					} else if ("PubTime".equals(pullParser.getName())) {
						mNewsOneClassty.setPubTime(pullParser.nextText());
					} else if ("Title".equals(pullParser.getName())) {
						mNewsOneClassty.setTitle(pullParser.nextText());
					}
					break;

				case XmlPullParser.END_TAG:
					if ("News".equals(pullParser.getName())) {
						if (mNewsOneClassty != null) {
							mList.add(mNewsOneClassty);
							mNewsOneClassty = null;
						}
					}
					break;
				}
				event = pullParser.next();
			}
			return mList.size();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
