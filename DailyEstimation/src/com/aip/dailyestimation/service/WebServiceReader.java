package com.aip.dailyestimation.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aip.dailyestimation.bean.AccountBean;
import com.aip.dailyestimation.bean.AddressBean;
import com.aip.dailyestimation.bean.CategoryBean;
import com.aip.dailyestimation.common.util.AsyncHandler;
import com.aip.dailyestimation.common.util.IConstants;
import com.aip.dailyestimation.common.util.IResultListner;
import com.aip.dailyestimation.common.util.RestClient;
import com.aip.dailyestimation.common.util.SharedPrefrenceUtil;
import com.aip.dailyestimation.common.util.Util;
import com.aip.dailyestimation.common.util.WebServiceURL;
import com.aip.dailyestimation.vo.FilterReceipt;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WebServiceReader {

    static WebServiceURL mServiceURL;

    {
        mServiceURL = WebServiceURL.getInstance();
    }

    public JSONObject doSignup(AccountBean accountBean, Activity activity) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String reg_token = sharedPreferences.getString("sp_reg_token", null);

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_COMP_NAME, accountBean.getCompanyName());
        param.put(IConstants.KEY_EMAIL, accountBean.getEmail());
        param.put(IConstants.KEY_FIRST_NAME, accountBean.getFirstName());
        param.put(IConstants.KEY_LAST_NAME, accountBean.getLastName());
        param.put(IConstants.KEY_PASS, accountBean.getPassword());
        param.put(IConstants.KEY_PHONE, accountBean.getPhoneno());
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);
        param.put(IConstants.KEY_DEVICETOKEN, reg_token);

        if (accountBean.getCreatedAt() != null) {
            param.put(IConstants.KEY_CREATED_AT,
                    (accountBean.getUpdatedAt().getTime()) + "");
            param.put(IConstants.KEY_UPDATED_AT,
                    (accountBean.getUpdatedAt().getTime()) + "");
        } else {
            param.put(IConstants.KEY_CREATED_AT, (new Date().getTime()) + "");
            param.put(IConstants.KEY_UPDATED_AT, (new Date().getTime()) + "");
        }

        return sendRequest(mServiceURL.SIGNUP_URL, param);
    }

    public JSONObject doSignup(AccountBean accountBean, Service activity) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String reg_token = sharedPreferences.getString("sp_reg_token", null);

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_COMP_NAME, accountBean.getCompanyName());
        param.put(IConstants.KEY_EMAIL, accountBean.getEmail());
        param.put(IConstants.KEY_FIRST_NAME, accountBean.getFirstName());
        param.put(IConstants.KEY_LAST_NAME, accountBean.getLastName());
        param.put(IConstants.KEY_PASS, accountBean.getPassword());
        param.put(IConstants.KEY_PHONE, accountBean.getPhoneno());
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);
        param.put(IConstants.KEY_DEVICETOKEN, reg_token);

        if (accountBean.getCreatedAt() != null) {
            param.put(IConstants.KEY_CREATED_AT,
                    (accountBean.getUpdatedAt().getTime()) + "");
            param.put(IConstants.KEY_UPDATED_AT,
                    (accountBean.getUpdatedAt().getTime()) + "");
        } else {
            param.put(IConstants.KEY_CREATED_AT, (new Date().getTime()) + "");
            param.put(IConstants.KEY_UPDATED_AT, (new Date().getTime()) + "");
        }

        return sendRequest(mServiceURL.SIGNUP_URL, param);
    }

    public JSONObject doLogin(String username, String password, Activity activity) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String reg_token = sharedPreferences.getString("sp_reg_token", null);

        Log.v("reg_token", reg_token + "-----");

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_EMAIL, username);
        param.put(IConstants.KEY_PASS, password);
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);
        param.put(IConstants.KEY_DEVICETOKEN, reg_token);

        return sendRequest(mServiceURL.LOGIN_URL, param);
    }

    public JSONObject doLogout(Activity activity) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String reg_token = sharedPreferences.getString("sp_reg_token", null);

        String authKey = sharedPreferences.getString("authKey", "");

        Log.v("reg_token", reg_token + "-----");

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_AUTH, authKey);
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);
        param.put(IConstants.KEY_DEVICETOKEN, reg_token);

        return sendRequest(mServiceURL.LOGOUT_URL, param);
    }

    public JSONObject doStickyLogin(String auth, Activity activity) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String reg_token = sharedPreferences.getString("sp_reg_token", null);

        Log.v("reg_token", reg_token + "-----");

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_AUTH, auth);
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);
        param.put(IConstants.KEY_DEVICETOKEN, reg_token);

        return sendRequest(mServiceURL.STICKY_LOGIN_URL, param);
    }


    public JSONObject PurchaseOCR(String... params) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_USER_ID, params[0]);
        param.put("orderId", params[1]);
        param.put("amount", params[2]);
        param.put("receipt", params[3]);
        param.put("userType", params[4]);
        param.put("purchaseTime", params[5]);

        param.put(IConstants.KEY_AUTH, params[6]);
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);

        return sendRequest(mServiceURL.updateSubscription, param);
    }

    public JSONObject ReadOCR(String userID, String auth) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_USER_ID, userID);
        param.put(IConstants.KEY_AUTH, auth);
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);

        return sendRequest(mServiceURL.UserPurchaseReceiptCount, param);
    }

    public JSONObject DecreaseOCR(String userID, String auth) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_USER_ID, userID);
        param.put("quantity", "1");
        param.put(IConstants.KEY_AUTH, auth);
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);

        return sendRequest(mServiceURL.UserPurchaseReceiptUpdateCount, param);
    }

    public JSONObject ReadUserDetails(String userID, String auth) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_USER_ID, userID);
        param.put(IConstants.KEY_AUTH, auth);
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);

        return sendRequest(mServiceURL.readUserDetails, param);
    }

    public JSONObject ReadOCRPackages(String userID, String auth) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_USER_ID, userID);
        param.put(IConstants.KEY_AUTH, auth);
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);
        return sendRequest(mServiceURL.ReadOcrPackage, param);
    }

    public JSONObject forgotPassword(String usernameOrEmail) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_EMAIL, usernameOrEmail);
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);

        return sendRequest(mServiceURL.FORGOT_PASSWORD_URL, param);
    }


    public JSONObject changePassword(String id, String oldPassword,
                                     String newPassword, String auth) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_OLD_PASS, Util.convertPassMd5(oldPassword));
        param.put(IConstants.KEY_NEW_PASS, Util.convertPassMd5(newPassword));
        param.put(IConstants.KEY_ID, id);
        param.put(IConstants.KEY_AUTH, auth);
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);

        return sendRequest(mServiceURL.CHANGE_PASSWORD_URL, param);
    }

    public JSONObject deleteReceipt(int receiptId, String auth, String userID) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_ID, String.valueOf(receiptId));
        param.put(IConstants.KEY_AUTH, auth);
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);
        param.put(IConstants.KEY_USER_ID, userID);

        return sendRequest(mServiceURL.deleteReceipt, param);
    }

    public JSONObject readAllReceipt(String userID, String auth) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_USER_ID, userID);
        param.put(IConstants.KEY_AUTH, auth);
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);

        return sendRequest(mServiceURL.readAllReceipt, param);
    }

    public JSONObject readFilterReceipt(String userID,
                                        FilterReceipt filterReceipt, String auth) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_USER_ID, userID);
        param.put(IConstants.KEY_AUTH, auth);
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);

        if (!filterReceipt.getCategoryId().equals("")) {
            param.put("categoryId", filterReceipt.getCategoryId());
        }

        if (!filterReceipt.getFromDate().equals("")) {
            param.put("fromDate", filterReceipt.getFromDate());
        }

        if (!filterReceipt.getToDate().equals("")) {
            param.put("toDate", filterReceipt.getToDate());
        }
        if (!filterReceipt.getMaxAmount().equals("")) {
            param.put("maxAmount", filterReceipt.getMaxAmount());
        }
        if (!filterReceipt.getMinAmount().equals("")) {
            param.put("minAmount", filterReceipt.getMinAmount());
        }
        if (!filterReceipt.getComment().equals("")) {
            param.put("comment", filterReceipt.getComment());
        }

        Log.e("filterReceipt", "filterReceipt \n" + filterReceipt.toString());

        return sendRequest(mServiceURL.readFilterReceipt, param);
    }

    public JSONObject addCategory(CategoryBean categoryBean, String userID, String auth) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_USER_ID, userID);
        param.put(IConstants.KEY_AUTH, auth);
        param.put(IConstants.KEY_CAT_NAME, categoryBean.getCategoryName());
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);

        return sendRequest(mServiceURL.addCategory, param);
    }

    public JSONObject updateCategory(CategoryBean categoryBean, String userID, String auth) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_USER_ID, userID);
        param.put(IConstants.KEY_ID,
                String.valueOf(categoryBean.getCategoryServerId()));
        param.put(IConstants.KEY_AUTH, auth);
        param.put(IConstants.KEY_CAT_NAME, categoryBean.getCategoryName());
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);

        return sendRequest(mServiceURL.updateCategory, param);
    }

    public JSONObject deleteCategory(CategoryBean categoryBean, String userID, String auth) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_USER_ID, userID);
        param.put(IConstants.KEY_AUTH, auth);
        param.put(IConstants.KEY_ID,
                String.valueOf(categoryBean.getCategoryServerId()));
        param.put(IConstants.KEY_CAT_NAME, categoryBean.getCategoryName());
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);

        return sendRequest(mServiceURL.deleteCategory, param);
    }

    public JSONObject readAllCategory(String userID, String auth) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_USER_ID, userID);
        param.put(IConstants.KEY_AUTH, auth);
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);

        return sendRequest(mServiceURL.readAllCategory, param);
    }

    public JSONObject addAddress(AddressBean addressBean, String userID, String auth) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_USER_ID, userID);
        param.put(IConstants.KEY_AUTH, auth);
        param.put(IConstants.KEY_ADD_NAME, addressBean.getAddressName());
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);

        return sendRequest(mServiceURL.addAddress, param);
    }

    public JSONObject updateAddress(AddressBean addressBean, String userID, String auth) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_USER_ID, userID);
        param.put(IConstants.KEY_AUTH, auth);
        param.put(IConstants.KEY_ID,
                String.valueOf(addressBean.getAddressServerId()));
        param.put(IConstants.KEY_ADD_NAME, addressBean.getAddressName());
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);

        return sendRequest(mServiceURL.updateAddress, param);
    }

    public JSONObject deleteAddress(AddressBean addressBean, String userID, String auth) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_USER_ID, userID);
        param.put(IConstants.KEY_AUTH, auth);
        param.put(IConstants.KEY_ID,
                String.valueOf(addressBean.getAddressServerId()));
        param.put(IConstants.KEY_ADD_NAME, addressBean.getAddressName());
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);

        return sendRequest(mServiceURL.deleteAddress, param);
    }

    public JSONObject readAllAddress(String userID, String auth) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_USER_ID, userID);
        param.put(IConstants.KEY_AUTH, auth);
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);

        return sendRequest(mServiceURL.readAllAddress, param);
    }

    public JSONObject updateProfile(AccountBean accountBean, String auth) {

        Map<String, String> param = new HashMap<String, String>();
        param.put(IConstants.KEY_COMP_NAME, accountBean.getCompanyName());
        param.put(IConstants.KEY_EMAIL, accountBean.getEmail());
        param.put(IConstants.KEY_FIRST_NAME, accountBean.getFirstName());
        param.put(IConstants.KEY_AUTH, auth);
        param.put(IConstants.KEY_LAST_NAME, accountBean.getLastName());
        param.put(IConstants.KEY_PHONE, accountBean.getPhoneno());
        param.put(IConstants.KEY_DEVICE, IConstants.KEY_DEVICE_VALUE);

        param.put(IConstants.KEY_ID, accountBean.getServerId() + "");

        return sendRequest(mServiceURL.updateProfile, param);
    }

    public String getSync() {

        return mServiceURL.sync;
    }

    public String getAddReceiptUrl() {

        return mServiceURL.addReceipt;
    }

    public String getUpdateReceiptUrl() {

        return mServiceURL.updateReceipt;
    }

    public JSONObject sendRequest(String url, Map<String, String> map) {
        JSONObject resObj = null;
        try {
            Log.e("URL", url + ">> Params >>" + map.toString());
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(url);
            MultipartEntity reqEntity = new MultipartEntity(
                    HttpMultipartMode.BROWSER_COMPATIBLE);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                reqEntity.addPart(entry.getKey(),
                        new StringBody(entry.getValue()));
            }
            postRequest.setEntity(reqEntity);
            HttpResponse response = httpClient.execute(postRequest);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), "UTF-8"));
            String sResponse;
            StringBuilder s = new StringBuilder();
            while ((sResponse = reader.readLine()) != null) {
                s = s.append(sResponse);
            }
            resObj = new JSONObject(s.toString());
            Log.d("RESPONSE", "" + s.toString());
        } catch (Exception e) {
            Log.d("RESPONSE ERROR", e.toString());
        }
        return resObj;
    }

    public JSONObject sendRequest(String url, MultipartEntity reqEntity) {
        JSONObject resObj = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(url);
            postRequest.setEntity(reqEntity);
            HttpResponse response = httpClient.execute(postRequest);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), "UTF-8"));
            String sResponse;
            StringBuilder s = new StringBuilder();
            while ((sResponse = reader.readLine()) != null) {
                s = s.append(sResponse);
            }
            resObj = new JSONObject(s.toString());
        } catch (Exception e) {
            Log.d("RESPONSE ERROR", e.toString());
        }
        return resObj;
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            // FlurryAgent.onError(Definitions.FLURRY_ERROR_NETWORK_OPERATION,
            // e.getMessage(), e.getClass().getName());
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // FlurryAgent.onError(Definitions.FLURRY_ERROR_NETWORK_OPERATION,
                // e.getMessage(), e.getClass().getName());
                throw new RuntimeException(e.getMessage());
            }
        }
        return sb.toString();
    }

    public void doUpload(Context context, final boolean isShowProgress,
                         final String url, RequestParams params,
                         final IResultListner iResultListner) {
        RestClient.post(url, params, new AsyncHandler(context, isShowProgress) {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONArray response) {
                iResultListner.result(response, true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                iResultListner.result(response, true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  String responseString) {
                iResultListner.result(responseString, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                if (responseString != null) {
                    Log.e(url + ":ERROR", responseString + "");
                } else if (throwable != null) {
                    Log.e(url + ":ERROR", throwable.getLocalizedMessage() + "");
                }
                iResultListner.result(null, false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) {
                    Log.e(url + ":ERROR", errorResponse.toString() + "");
                } else if (throwable != null) {
                    Log.e(url + ":ERROR", throwable.getLocalizedMessage() + "");
                }
                iResultListner.result(null, false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONArray errorResponse) {
                if (errorResponse != null) {
                    Log.e(url + ":ERROR", errorResponse.toString() + "");
                } else if (throwable != null) {
                    Log.e(url + ":ERROR", throwable.getLocalizedMessage() + "");
                }
                iResultListner.result(null, false);
            }
        });
    }

}