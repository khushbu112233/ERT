package com.aip.dailyestimation.vo;

import java.io.Serializable;
import java.util.Calendar;

public class FilterVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Calendar fromCalendar;
	private Calendar toCalendar;
	private String categoryName;
	private Double amount;
	private Double Maxamount;
	private Double Minamount;
	private String comment;

	public Calendar getFromCalendar() {
		return fromCalendar;
	}

	public void setFromCalendar(Calendar fromCalendar) {
		this.fromCalendar = fromCalendar;
	}

	public Calendar getToCalendar() {
		return toCalendar;
	}

	public void setToCalendar(Calendar toCalendar) {
		this.toCalendar = toCalendar;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Double getAmount() {
		return amount;
	}

	public Double getMaxAmount() {
		return Maxamount;
	}

	public Double getMinAmount() {
		return Minamount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public void setMaxAmount(Double amount) {
		this.Maxamount = amount;
	}

	public void setMinAmount(Double amount) {
		this.Minamount = amount;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "FilterVO [fromCalendar=" + fromCalendar + ", toCalendar="
				+ toCalendar + ", categoryName=" + categoryName + ", amount="
				+ amount + ", comment=" + comment + ", getFromCalendar()="
				+ getFromCalendar() + ", getToCalendar()=" + getToCalendar()
				+ ", getCategoryName()=" + getCategoryName() + ", getAmount()="
				+ getAmount() + ", getComment()=" + getComment()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}

	public void clear() {
		fromCalendar = toCalendar = null;
		categoryName = comment = null;
		amount = null;
		Maxamount = null;
		Minamount = null;
	}

	public boolean isEmpty() {
		if (fromCalendar == null && toCalendar == null
				&& (categoryName == null || categoryName.trim().length() < 1)
				&& (comment == null || comment.trim().length() < 1)
				&& amount == null && Maxamount == null && Minamount == null) {
			return true;
		} else
			return false;
	}
}
