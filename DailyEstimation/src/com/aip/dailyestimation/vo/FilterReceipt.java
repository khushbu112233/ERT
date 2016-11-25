package com.aip.dailyestimation.vo;

public class FilterReceipt {

	String categoryId = "";
	String userId = "";
	String fromDate = "";
	String toDate = "";
	String maxAmount = "";
	String minAmount = "";
	String comment = "";

	public FilterReceipt() {
		super();
	}

	public FilterReceipt(String categoryId, String userId, String fromDate,
			String toDate, String maxAmount, String minAmount, String comment) {
		super();
		this.categoryId = categoryId;
		this.userId = userId;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.maxAmount = maxAmount;
		this.minAmount = minAmount;
		this.comment = comment;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(String maxAmount) {
		this.maxAmount = maxAmount;
	}

	public String getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(String minAmount) {
		this.minAmount = minAmount;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "FilterReceipt [categoryId=" + categoryId + ", userId=" + userId
				+ ", fromDate=" + fromDate + ", toDate=" + toDate
				+ ", maxAmount=" + maxAmount + ", minAmount=" + minAmount
				+ ", comment=" + comment + "]";
	}

}
