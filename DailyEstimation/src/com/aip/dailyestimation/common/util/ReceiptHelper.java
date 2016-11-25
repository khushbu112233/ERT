package com.aip.dailyestimation.common.util;

import com.aip.dailyestimation.bean.ReceiptBean;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReceiptHelper {

	@SerializedName("data")
	private List<ReceiptBean> receiptBeans;
	
	@SerializedName("deletedId")
	private List<DeletedId> deletedIds;

	public List<ReceiptBean> getReceiptBeans() {
		return receiptBeans;
	}

	public void setReceiptBeans(List<ReceiptBean> receiptBeans) {
		this.receiptBeans = receiptBeans;
	}
	
	public List<DeletedId> getDeletedIds() {
		return deletedIds;
	}

	public void setDeletedIds(List<DeletedId> deletedIds) {
		this.deletedIds = deletedIds;
	}


	public class DeletedId{
		private int id;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
	}
	
	
}
