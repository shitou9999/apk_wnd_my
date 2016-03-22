package com.greentech.wnd.android.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DiseaseFeature implements Serializable{

	private int id;//id
	private String name;//病害特征描述
	private int agriProductId;//农产品
	private int diseasePositonId;//病害部位
	
	public DiseaseFeature() {
		
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

	public int getAgriProductId() {
		return agriProductId;
	}

	public void setAgriProductId(int agriProductId) {
		this.agriProductId = agriProductId;
	}

	public int getDiseasePositonId() {
		return diseasePositonId;
	}

	public void setDiseasePositonId(int diseasePositonId) {
		this.diseasePositonId = diseasePositonId;
	}
	
}
