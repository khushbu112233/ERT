package com.aip.dailyestimation.bean;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "TblCategory")
public class CategoryBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@DatabaseField(id = true, columnName = "categoryid")
	private int categoryID;
	@DatabaseField(columnName = "name")
	private String categoryName;
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
	@DatabaseField(columnName = "categoryserverid", defaultValue = "-1")
	private int categoryServerId;

	public int getCategoryServerId() {
		return categoryServerId;
	}

	public void setCategoryServerId(int categoryServerId) {
		this.categoryServerId = categoryServerId;
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

	public int getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public int getIsSync() {
		return isSync;
	}

	public void setIsSync(int isSync) {
		this.isSync = isSync;
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

}
