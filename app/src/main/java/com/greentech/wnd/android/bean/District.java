package com.greentech.wnd.android.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class District implements Serializable{
	private Integer id;
	private String name;
	private Integer cityId;
	
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
	 * @return the cityId
	 */
	public Integer getCityId() {
		return cityId;
	}
	/**
	 * @param cityId the cityId to set
	 */
	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	public static List<String> PToStr(List<District> list) {
		List<String> slist = new ArrayList<String>();
		for (District p : list) {
			slist.add(p.getName());
		}
		return slist;
	}
	
}
