package com.aip.dailyestimation.common.util;

public class ReadOcrPackage {

	String Id;
	String Title;
	String NoOfReceipt;
	String IosPrice;
	String AndroidPrice;
	String status;

	public ReadOcrPackage() {
		super();
	}

	public ReadOcrPackage(String id, String title, String noOfReceipt,
			String iosPrice, String androidPrice, String status) {
		super();
		Id = id;
		Title = title;
		NoOfReceipt = noOfReceipt;
		IosPrice = iosPrice;
		AndroidPrice = androidPrice;
		this.status = status;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getNoOfReceipt() {
		return NoOfReceipt;
	}

	public void setNoOfReceipt(String noOfReceipt) {
		NoOfReceipt = noOfReceipt;
	}

	public String getIosPrice() {
		return IosPrice;
	}

	public void setIosPrice(String iosPrice) {
		IosPrice = iosPrice;
	}

	public String getAndroidPrice() {
		return AndroidPrice;
	}

	public void setAndroidPrice(String androidPrice) {
		AndroidPrice = androidPrice;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
