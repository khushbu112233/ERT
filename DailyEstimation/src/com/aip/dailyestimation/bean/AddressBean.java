package com.aip.dailyestimation.bean;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "TblAddress")
public class AddressBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@DatabaseField(id = true, columnName = "addressid")
	private int addressID;
	@DatabaseField(columnName = "name")
	private String addressName;
	@DatabaseField(columnName = "createdat", dataType = DataType.DATE)
	private Date createdAt;
	@DatabaseField(columnName = "updatedat", dataType = DataType.DATE)
	private Date updatedAt;
	@DatabaseField(columnName = "issync", defaultValue = "0")
	private int isSync;

	// New field
	@DatabaseField(columnName = "serverid", defaultValue = "-1")
	private int serverId;

	// New field
	@DatabaseField(columnName = "userid", defaultValue = "0")
	private int userId;

	// New field
	@DatabaseField(columnName = "addressserverid", defaultValue = "-1")
	private int addressServerId;

	public int getAddressServerId() {
		return addressServerId;
	}

	public void setAddressServerId(int addressServerId) {
		this.addressServerId = addressServerId;
	}

	public int getAddressID() {
		return addressID;
	}

	public void setAddressID(int addressID) {
		this.addressID = addressID;
	}

	public String getAddressName() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public int getIsSync() {
		return isSync;
	}

	public void setIsSync(int isSync) {
		this.isSync = isSync;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
