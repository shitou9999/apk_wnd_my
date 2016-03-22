package com.greentech.wnd.android.bean;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class DiseasePosition implements Serializable{

	private int id;//id
	private String name;//病害部位
	private int agriProductId;//农产品
	
	private List<DiseaseFeature> diseaseFeatureList;
	
	public DiseasePosition() {
		
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

	public List<DiseaseFeature> getDiseaseFeatureList() {
		return diseaseFeatureList;
	}

	public void setDiseaseFeatureList(List<DiseaseFeature> diseaseFeatureList) {
		this.diseaseFeatureList = diseaseFeatureList;
	}
	
}
