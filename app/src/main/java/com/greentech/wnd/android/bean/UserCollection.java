package com.greentech.wnd.android.bean;

import java.io.Serializable;
import java.util.Date;

public class UserCollection implements Serializable{
	private int userId;
	private int refId;
	private byte type;
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getRefId() {
		return refId;
	}
	public void setRefId(int refId) {
		this.refId = refId;
	}
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	
}
