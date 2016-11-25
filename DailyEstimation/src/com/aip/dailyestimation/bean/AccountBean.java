package com.aip.dailyestimation.bean;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "TblAccount")
public class AccountBean implements Serializable {

	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;

	@DatabaseField(id = true, columnName = "id")
	private int ID;
	@SerializedName("email")
	@DatabaseField(columnName = "email")
	private String email;

	@SerializedName("password")
	@DatabaseField(columnName = "password")
	private String password;

	@SerializedName("firstName")
	@DatabaseField(columnName = "firstname")
	private String firstName;

	@SerializedName("lastName")
	@DatabaseField(columnName = "lastname")
	private String lastName;

	@SerializedName("companyName")
	@DatabaseField(columnName = "companyname")
	private String companyName;

	@SerializedName("phone")
	@DatabaseField(columnName = "phoneno")
	private String phoneno;

	@DatabaseField(columnName = "createdat", dataType = DataType.DATE)
	private Date createdAt;

	@DatabaseField(columnName = "updatedat", dataType = DataType.DATE)
	private Date updatedAt;

	@DatabaseField(columnName = "issync", defaultValue = "0")
	private int isSync;

	// New field
	@SerializedName("id")
	@DatabaseField(columnName = "serverid", defaultValue = "-1")
	private int serverId;

	// New field

	@SerializedName("usertype")
	@DatabaseField(columnName = "usertype")
	private String userType;

	// New field
	@SerializedName("receipt")
	@DatabaseField(columnName = "receipt")
	private String receipt;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getPhoneno() {
		return phoneno;
	}

	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
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

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getReceipt() {
		return receipt;
	}

	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}

	@Override
	public String toString() {
		return "AccountBean [ID=" + ID + ", email=" + email + ", password="
				+ password + ", firstName=" + firstName + ", lastName="
				+ lastName + ", companyName=" + companyName + ", phoneno="
				+ phoneno + ", createdAt=" + createdAt + ", updatedAt="
				+ updatedAt + ", isSync=" + isSync + ", serverId=" + serverId
				+ ", userType=" + userType + ", receipt=" + receipt + "]";
	}

}