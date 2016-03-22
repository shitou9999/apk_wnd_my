package com.greentech.wnd.android.bean;

import java.io.Serializable;
import java.util.List;



public class PageInfoImpl implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int pageCount;//总页数
	private int pageSize; //页大小
	private long totalRecords; //总记录数
	private int start; //起始位置
	private int currentNo;//当前页数
	private List pageData;//页内容
	private int currentSize;
	
	
	public int getCurrentSize() {
		currentSize = pageData.size();
		return currentSize;
	}

	public void setCurrentSize(int currentSize) {
		this.currentSize = currentSize;
	}

	public PageInfoImpl(List pageData, long totalRecords, int pageSize, int pageNo) {
		this.pageData = pageData;
		this.totalRecords = totalRecords;
		this.pageSize = pageSize;
		this.currentNo = pageNo;
		
		
		pageCount = (int)((totalRecords + pageSize - 1) / pageSize);
		if(totalRecords <= 0) {
			pageCount = 0;
		}
		if(currentNo == pageCount) {//如果 当前页等于 总页数
			this.pageSize = (int)(totalRecords - ((pageCount - 1) * pageSize));
		}
		start = (pageNo - 1) * pageSize + 1;
		
	}

	public int getCurrentPageNo() {
		return currentNo;
	}

	public long getEndIndex() {
		return start + pageSize - 1;
	}

	public int getPageCount() {
		return pageCount;
	}

	

	public long getTotalRecords() {
		return totalRecords;
	}

	public long getStartIndex() {
		return start;
	}

	public int getStartOfNextPage() {
		int pageNo = currentNo + 1;
		if(pageNo > pageCount) {
			pageNo = pageCount;
		}
		return this.getStartOfPage(pageNo);
	}

	public int getStartOfPage(int pageNo) {
		int startIndex = (pageNo - 1) * pageSize + 1;
		if(startIndex < 1) {
			startIndex = 1;
		}
		return startIndex;
	}

	public int getStartOfPreviousPage() {
		int pageNo = currentNo - 1;
		if(pageNo < 1) {
			pageNo = 1;
		}
		return this.getStartOfPage(pageNo);
	}

	public boolean isNextPageEnable() {
		return currentNo < pageCount;
	}

	public boolean isPreviousPageEnable() {
		return currentNo > 1;
	}
	public List getPageData() {
		return pageData;
	}

	public void setPageData(List pageData) {
		this.pageData = pageData;
	}

	public int getPageSize() {
		return this.pageSize;
	}
}
