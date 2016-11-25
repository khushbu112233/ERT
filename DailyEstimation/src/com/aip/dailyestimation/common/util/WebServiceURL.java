package com.aip.dailyestimation.common.util;

public class WebServiceURL {

    // public static final String MAIN_URL =
    // "http://apptechnolabs.com/projects/estimation/web/app_dev.php/api/";
    // public static final String MAIN_URL =
    // "http://aipxperts.com/projects/estimation/web/app_dev.php/api/";
    // public static final String MAIN_URL =
    // "http://apptechnolabs.com/projects/estimation/web/api/";

   // public static final String MAIN_URL = "http://expense.solutions/api/v1/";
   public static final String MAIN_URL = "https://expense.solutions/api/v2/";

    // public static final String MAIN_URL =
    // "http://aipxperts.com/projects/estimation/web/app_dev.php/api/";

    public String SIGNUP_URL = "register/";
    public String LOGIN_URL = "login/";
    public String LOGOUT_URL = "logout/";
    public String STICKY_LOGIN_URL = "stickyLogin/";
    public String FORGOT_PASSWORD_URL = "forgotPassword/";
    public String CHANGE_PASSWORD_URL = "changePassword/";

    public String addReceipt = "addReceipt/";
    public String updateReceipt = "updateReceipt/";
    public String readAllReceipt = "getAllUserReceipts/";
    public String readFilterReceipt = "filter/";

    public String addCategory = "addCategory/";
    public String updateCategory = "updateCategory/";
    public String deleteCategory = "deleteCategory/";
    public String readAllCategory = "getUserCategory/";

    public String addAddress = "addAddressBook/";
    public String updateAddress = "updateAddressBook/";
    public String deleteAddress = "deleteAddressBook/";
    public String readAllAddress = "getUserAddressBook/";

    public String readUserDetails = "getUser/";
    public String updateProfile = "updateProfile/";

    public String UserPurchaseReceiptCount = "getUserPurchaseReceiptCount/";
    public String PurchaseOCR = "purchase/";
    public String updateSubscription = "updateSubscription/";
    public String UserPurchaseReceiptUpdateCount = "updatePurchaseReceipt/";
    public String ReadOcrPackage = "getAllPackages/";

    public String sync = "sync/";

    public String deleteReceipt = "deleteReceipt/";

    public String getSubscribeUserInfo = "getSubscribeUserInfo/";

    public String updateSubscribeUser = "updateSubscribeUser/";

    static WebServiceURL mServiceURL;

    private WebServiceURL() {

        SIGNUP_URL = MAIN_URL + SIGNUP_URL;
        LOGIN_URL = MAIN_URL + LOGIN_URL;
        STICKY_LOGIN_URL = MAIN_URL + STICKY_LOGIN_URL;
        LOGOUT_URL = MAIN_URL + LOGOUT_URL;
        FORGOT_PASSWORD_URL = MAIN_URL + FORGOT_PASSWORD_URL;
        CHANGE_PASSWORD_URL = MAIN_URL + CHANGE_PASSWORD_URL;

        UserPurchaseReceiptCount = MAIN_URL + UserPurchaseReceiptCount;
        PurchaseOCR = MAIN_URL + PurchaseOCR;
        UserPurchaseReceiptUpdateCount = MAIN_URL
                + UserPurchaseReceiptUpdateCount;

        addReceipt = MAIN_URL + addReceipt;
        updateReceipt = MAIN_URL + updateReceipt;
        readAllReceipt = MAIN_URL + readAllReceipt;
        readFilterReceipt = MAIN_URL + readFilterReceipt;
        ReadOcrPackage = MAIN_URL + ReadOcrPackage;

        addCategory = MAIN_URL + addCategory;
        updateCategory = MAIN_URL + updateCategory;
        deleteCategory = MAIN_URL + deleteCategory;
        readAllCategory = MAIN_URL + readAllCategory;

        addAddress = MAIN_URL + addAddress;
        updateAddress = MAIN_URL + updateAddress;
        deleteAddress = MAIN_URL + deleteAddress;
        readAllAddress = MAIN_URL + readAllAddress;

        readUserDetails = MAIN_URL + readUserDetails;
        updateProfile = MAIN_URL + updateProfile;
        sync = MAIN_URL + sync;

        updateSubscription = MAIN_URL + updateSubscription;

        deleteReceipt = MAIN_URL + deleteReceipt;

        getSubscribeUserInfo = MAIN_URL + getSubscribeUserInfo;
        updateSubscribeUser = MAIN_URL + updateSubscribeUser;
    }

    public static WebServiceURL getInstance() {
        if (mServiceURL == null)
            mServiceURL = new WebServiceURL();
        return mServiceURL;
    }
}