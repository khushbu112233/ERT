package com.aip.dailyestimation.common.util;

public interface IConstants {

	String APP_NAME = "Receipt Tracker";
	String LOG_TAG = "#RECEIPT TRACKER#";

	String FONT_NAME = "Economica_bold.otf"; // This font must be exist on
												// assets/fonts folder
	String FONT_GEN_NAME = "Economic_Regular.otf"; // This font must be exist on
													// assets/fonts folder

	String ORDER_BY_UPDATED_AT = "updatedat";
	String CATEGORY_NAME = "categoryname";
	String ADDRESS_NAME = "name";
	String AMOUNT = "amount";
	String MAXAMOUNT = "minamount";
	String MINAMOUNT = "maxamount";
	String COMMENT = "comment";

	String ARG_ID = "argid";
	String ARG_FILTER = "filter";

	String PREF_EMAIL = "loingEmail";
	String PREF_PASSWORD = "loginPassword";

	// String DATE_FORMAT = "dd/MM/yyyy";
	String DATE_FORMAT = "MM/dd/yyyy";

	int MILLI_SECOND_OF_DAY = 86400000;

	int SPLASH_TIME = 1000;

	// keys

	String KEY_USER_ID = "userId";
	String KEY_ID = "id";
	String KEY_EMAIL = "email";
	String KEY_PASS = "password";
	String KEY_FIRST_NAME = "firstName";
	String KEY_LAST_NAME = "lastName";
	String KEY_COMP_NAME = "companyName";
	String KEY_NEW_PASS = "newPassword";
	String KEY_OLD_PASS = "oldPassword";
	String KEY_PHONE = "phone";
	String KEY_CREATED_AT = "createdDate";
	String KEY_UPDATED_AT = "modifiedDate";
	String KEY_DEVICE = "deviceType";
	String KEY_DEVICE_VALUE = "android";
	String KEY_CATEGORY_SERVER_ID = "categoryId";
	String KEY_ADDRESS_SERVER_ID = "addressBookId";
	String KEY_AUTH = "authKey";

	// receipt

	String KEY_RECEIPT_NAME = "receiptName";
	String KEY_RECEIPT_CAT = "receiptCategory";
	String KEY_AMOUNT = "amount";
	String KEY_DATE = "date";
	String KEY_TIP = "tip";
	String KEY_COMMENT = "comment";
	String KEY_IS_DELETE = "isDelete";
	String CLOUD_OCR_VALUE = "off";
	String CLOUD_OCR_COUNTER = "OcrCounter";

	// category

	String KEY_CAT_NAME = "categoryName";

	// address

	String KEY_ADD_NAME = "addressBookName";

	// extra
	
	String PREF_CURR_USER_ID = "currentAccId";

	String PREF_CURR_USER_TYPE = "currentUserType";

	String PREF_FIRST_TIME_INSTALLED = "isFirstTimeInstalled";

	String KEY_LOCAL_RECEIPT_ID = "localReceiptId";

	String KEY_DEVICETOKEN="deviceToken";

}
