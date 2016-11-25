package com.aip.dailyestimation.bean;

import com.aip.dailyestimation.common.util.IConstants;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "TblReceipt")
public class ReceiptBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SerializedName(IConstants.KEY_LOCAL_RECEIPT_ID)
	@DatabaseField(id = true, columnName = "receiptid")
	private int receiptID;

	@SerializedName("receiptCategory")
	@DatabaseField(columnName = "categoryname")
	private String categoryName;

	@SerializedName("receiptName")
	@DatabaseField(columnName = "name")
	private String name;

	@SerializedName("amount")
	@DatabaseField(columnName = "amount", dataType = DataType.DOUBLE)
	private double amount;

	@SerializedName("tip")
	@DatabaseField(columnName = "tip", dataType = DataType.DOUBLE)
	private double tip;

	@SerializedName("comment")
	@DatabaseField(columnName = "comment")
	private String comment;

	@SerializedName("createdDate")
	@DatabaseField(columnName = "createdat", dataType = DataType.DATE)
	private Date createdAt;

	@SerializedName("modifiedDate")
	@DatabaseField(columnName = "updatedat", dataType = DataType.DATE)
	private Date updateddAt;

	@DatabaseField(columnName = "issync", defaultValue = "0")
	private int isSync;

	@DatabaseField(columnName = "image", dataType = DataType.BYTE_ARRAY)
	private byte[] imageBytes;

	@SerializedName("receiptImage")
	@DatabaseField(columnName = "serverimagepath")
	private String serverImgPath;

	@SerializedName("date")
	@DatabaseField(columnName = "date", dataType = DataType.DATE)
	private Date date;

	// New field
	@SerializedName("id")
	@DatabaseField(columnName = "serverid", defaultValue = "-1")
	private int serverId;

	// New field
	@SerializedName(IConstants.KEY_USER_ID)
	@DatabaseField(columnName = "userid", defaultValue = "0")
	private int userId;

	// New field
	@SerializedName(IConstants.KEY_IS_DELETE)
	@DatabaseField(columnName = "isdelete", defaultValue = "0")
	private int isDelete;

	// New field
	@DatabaseField(columnName = "categoryserverid", defaultValue = "-1")
	private int categoryServerId;

	// New field
	@DatabaseField(columnName = "addressserverid", defaultValue = "-1")
	private int addressServerId;

	public int getIsDelete() {
		return isDelete;
	}

	public int getCategoryServerId() {
		return categoryServerId;
	}

	public void setCategoryServerId(int categoryServerId) {
		this.categoryServerId = categoryServerId;
	}

	public int getAddressServerId() {
		return addressServerId;
	}

	public void setAddressServerId(int addressServerId) {
		this.addressServerId = addressServerId;
	}

	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public int getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(int receiptID) {
		this.receiptID = receiptID;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getTip() {
		return tip;
	}

	public void setTip(double tip) {
		this.tip = tip;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdateddAt() {
		return updateddAt;
	}

	public void setUpdateddAt(Date updateddAt) {
		this.updateddAt = updateddAt;
	}

	public int getIsSync() {
		return isSync;
	}

	public void setIsSync(int isSync) {
		this.isSync = isSync;
	}

	public byte[] getImageBytes() {
		return imageBytes;
	}

	public void setImageBytes(byte[] imageBytes) {
		this.imageBytes = imageBytes;
	}

	public String getServerImgPath() {
		return serverImgPath;
	}

	public void setServerImgPath(String serverImgPath) {
		this.serverImgPath = serverImgPath;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "ReceiptBean [receiptID=" + receiptID + ", categoryName="
				+ categoryName + ", name=" + name + ", amount=" + amount
				+ ", tip=" + tip + ", comment=" + comment + ", createdAt="
				+ createdAt + ", updateddAt=" + updateddAt + ", isSync="
				+ isSync + ", serverImgPath=" + serverImgPath + ", date="
				+ date + ", serverId=" + serverId + ", userId=" + userId + "]";
	}

}
