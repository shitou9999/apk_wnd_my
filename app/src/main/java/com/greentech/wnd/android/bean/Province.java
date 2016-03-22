package com.greentech.wnd.android.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Province implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	
	public Province(){}
	public Province(Integer id,String name){
		this.id = id;
		this.name = name;
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
	public static List<String> PToStr(List<Province> list) {
		List<String> slist = new ArrayList<String>();
		for (Province p : list) {
			slist.add(p.getName());
		}
		return slist;
	}
}
