package com.greentech.wnd.android.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Disease implements Serializable{

	private int id;//id
	private String title;//病害名称标题
	private String imgs;//图片id拼接而成的字符串
	private byte diseaseType;//病害或者虫害，1表示虫害，其他为病害
	private int agriProductId;//农产品
	private String diseaseFeatures;//病害特征
	private String content;//病害详情
	private String source;//病害知识来源
	private String img;//TODO:数据库表中的img字段，先用，最后图片放进imgs字段后，这个字段是要删除的
	private float score;
	public Disease() {
		
	}

	public int getId() {
		return id;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImgs() {
		return imgs;
	}

	public void setImgs(String imgs) {
		this.imgs = imgs;
	}

	public byte getDiseaseType() {
		return diseaseType;
	}

	public void setDiseaseType(byte diseaseType) {
		this.diseaseType = diseaseType;
	}

	public int getAgriProductId() {
		return agriProductId;
	}

	public void setAgriProductId(int agriProductId) {
		this.agriProductId = agriProductId;
	}

	public String getDiseaseFeatures() {
		return diseaseFeatures;
	}

	public void setDiseaseFeatures(String diseaseFeatures) {
		this.diseaseFeatures = diseaseFeatures;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}
	
}
