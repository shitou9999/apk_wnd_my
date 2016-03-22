package com.greentech.wnd.android.bean;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class AgriProduct implements Serializable{

	private int id;//id
	private String name;//农产品名称
	private String alias;//农产品别名
	private byte type;//农产品类别
	private String remark;//说明
	private String img;//产品图片
	private Date releaseTime;
	private String category;
	public Date getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(Date releaseTime) {
		this.releaseTime = releaseTime;
	}

	public AgriProduct() {
		
	}
    
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	/**
	 * 返回name字符串，是为了给ArrayAdapter使用
	 * @return
	 */
//	public String toString() {
//		return name == null ? "" : name; 
//	}
}
