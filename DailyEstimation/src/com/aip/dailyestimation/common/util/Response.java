package com.aip.dailyestimation.common.util;

import com.google.gson.annotations.SerializedName;

public class Response {
	private int code;
	private String message;
	
	@SerializedName("data")
	private InnerData innerData;

	public class InnerData{
		private int id;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

	}

	public InnerData getInnerData() {
		return innerData;
	}
	public void setInnerData(InnerData innerData) {
		this.innerData = innerData;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "Response [code=" + code + ", message=" + message + "]";
	}


}
