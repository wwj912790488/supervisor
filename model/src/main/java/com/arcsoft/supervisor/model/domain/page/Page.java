package com.arcsoft.supervisor.model.domain.page;

import java.util.ArrayList;
import java.util.List;

public class Page {
	private int pageNo;

	private long totalCount;

	private int pageSize;

	public int totalPage;

	public int prePage;

	public int nextPage;
	
	public List<Integer> prePages;
	public List<Integer> nextPages;

	// get total page
	public long getTotalPage() {
		if (totalCount % pageSize == 0) {
			return totalCount / pageSize;
		} else {
			return totalCount / pageSize + 1;
		}
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	// get previous page
	public int getPrePage() {

		if (pageNo > 1)
			return pageNo - 1;
		else
			return pageNo;

	}

	public void setPrePage(int prePage) {
		this.prePage = prePage;
	}

	// get next page
	public int getNextPage() {

		if (pageNo < this.getTotalPage())
			return pageNo + 1;
		else
			return pageNo;

	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}
	
	public List<Integer> getPrePages() {
		int begin = pageNo - 5 < 1 ? 1 : pageNo - 5;
		List<Integer> list = new ArrayList<Integer>();
		for(int i = begin; i < pageNo; i++){
			list.add(i);
		}
		
		return list;
	}
	
	public List<Integer> getNextPages() {
		long total = getTotalPage();
		int count = pageNo <= 5 ? 10 - pageNo : 4 ;
		long end = total < pageNo + count + 1 ? total + 1 : pageNo + count + 1;
		List<Integer> list = new ArrayList<Integer>();
		for(int i = pageNo+1; i < end; i++){
			list.add(i);
		}
		
		return list;
	}

	// get current page
	public int getPageNo() {
		return pageNo;
	}

	// set page num
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	// get total count
	public long getTotalCount() {
		return totalCount;
	}

	// set total count
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	// set page size
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}
