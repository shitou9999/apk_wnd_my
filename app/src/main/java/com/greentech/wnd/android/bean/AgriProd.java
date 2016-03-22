package com.greentech.wnd.android.bean;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class AgriProd implements Serializable {

	private int id;// id
	private String type;// 农产品名称
	private Integer parentId;
	private String userId;
	private String py;
	private Double minPriceLimit;
	private Double maxPriceLimit;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPy() {
		return py;
	}
	public void setPy(String py) {
		this.py = py;
	}
	public Double getMinPriceLimit() {
		return minPriceLimit;
	}
	public void setMinPriceLimit(Double minPriceLimit) {
		this.minPriceLimit = minPriceLimit;
	}
	public Double getMaxPriceLimit() {
		return maxPriceLimit;
	}
	public void setMaxPriceLimit(Double maxPriceLimit) {
		this.maxPriceLimit = maxPriceLimit;
	}
	
	
}
