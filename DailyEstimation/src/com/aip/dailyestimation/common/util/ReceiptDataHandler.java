package com.aip.dailyestimation.common.util;

import com.aip.dailyestimation.bean.ReceiptBean;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReceiptDataHandler {
	
	private int code;
	
	@SerializedName("data")
	private List<ReceiptBean> receiptBeans;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public List<ReceiptBean> getReceiptBeans() {
		return receiptBeans;
	}
	public void setReceiptBeans(List<ReceiptBean> receiptBeans) {
		this.receiptBeans = receiptBeans;
	}
	
	
}
