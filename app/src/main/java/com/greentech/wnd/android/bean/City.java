package com.greentech.wnd.android.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class City implements Serializable{
	//test1
	private Integer id;
	private String name;
	private Integer provinceId;
	public City(){}
	public City(Integer id,String name,Integer provinceId){
		this.id= id;
		this.name= name;
		this.provinceId = provinceId;
	}
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
	 * @return the provinceId
	 */
	public Integer getProvinceId() {
		return provinceId;
	}
	/**
	 * @param provinceId the provinceId to set
	 */
	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
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
	
	public static List<String> PToStr(List<City> list) {
		List<String> slist = new ArrayList<String>();
		for (City p : list) {
			slist.add(p.getName());
		}
		return slist;
	}
}
