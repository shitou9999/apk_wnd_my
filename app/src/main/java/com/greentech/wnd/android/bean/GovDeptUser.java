package com.greentech.wnd.android.bean;

import java.io.Serializable;

public class GovDeptUser implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String govDeptName;
	private String name;
	private Integer userId;
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return the govDeptName
	 */
	public String getGovDeptName() {
		return govDeptName;
	}
	/**
	 * @param govDeptName the govDeptName to set
	 */
	public void setGovDeptName(String govDeptName) {
		this.govDeptName = govDeptName;
	}
	/**
	 * @return the userId
	 */
	public Integer getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
