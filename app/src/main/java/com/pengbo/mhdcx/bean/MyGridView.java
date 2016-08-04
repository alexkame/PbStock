package com.pengbo.mhdcx.bean;

/**
 * 修改筛选条件中 把 中间的  gridview 中上下字号不一样的值 看作两个属性  
 * @author pobo
 *
 */
public class MyGridView {
	public  int   id; 
	
	public String uptext;
	public String downtext;
	public String getUptext() {
		return uptext;
	}
	public void setUptext(String uptext) {
		this.uptext = uptext;
	}
	public String getDowntext() {
		return downtext;
	}
	public void setDowntext(String downtext) {
		this.downtext = downtext;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
}
