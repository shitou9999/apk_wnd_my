package com.greentech.wnd.android.bean;

import java.util.Date;
import java.util.List;

public class TopicReply  implements java.io.Serializable {

	private Integer id;
	private String content;
	private Date releaseTime;
	private Integer userId;
	private Integer topicId;
	private String imgs;
	private Integer floor;
	private Integer status;
	private Integer parentId;
	private int agreed;
	private int disagreed;
	private int istaked;

	private List<TopicReply> replyList;
	private User user;
	
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
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @return the releaseTime
	 */
	public Date getReleaseTime() {
		return releaseTime;
	}
	/**
	 * @param releaseTime the releaseTime to set
	 */
	public void setReleaseTime(Date releaseTime) {
		this.releaseTime = releaseTime;
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
	/**
	 * @return the topicId
	 */
	public Integer getTopicId() {
		return topicId;
	}
	/**
	 * @param topicId the topicId to set
	 */
	public void setTopicId(Integer topicId) {
		this.topicId = topicId;
	}
	public String getImgs() {
		return imgs;
	}
	public void setImgs(String imgs) {
		this.imgs = imgs;
	}
	public Integer getFloor() {
		return floor;
	}
	public void setFloor(Integer floor) {
		this.floor = floor;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public int getAgreed() {
		return agreed;
	}
	public void setAgreed(int agreed) {
		this.agreed = agreed;
	}
	public int getDisagreed() {
		return disagreed;
	}
	public void setDisagreed(int disagreed) {
		this.disagreed = disagreed;
	}
	public int getIstaked() {
		return istaked;
	}
	public void setIstaked(int istaked) {
		this.istaked = istaked;
	}
	public List<TopicReply> getReplyList() {
		return replyList;
	}
	public void setReplyList(List<TopicReply> replyList) {
		this.replyList = replyList;
	}
}
