package com.aip.dailyestimation.view.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;

import com.aip.dailyestimation.bean.AccountBean;
import com.aip.dailyestimation.common.util.SharedPrefrenceUtil;
import com.aip.dailyestimation.common.util.Util;
import com.aip.dailyestimation.common.util.WebServiceURL;
import com.aip.dailyestimation.service.DatabaseService;
import com.android.vending.billing.IInAppBillingService;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsumeBillingActivity extends Activity {

    private static final String LOG_TAG = "BillingActivity";

    private DatabaseService mDatabaseService;

    private IInAppBillingService mService;
    private JSONObject PurchaseReceipt;

    static final String ITEM_SKU_1 = "com.aip.receipt.monthly2years";
    static final String ITEM_SKU_2 = "com.aip.receipt.monthly";

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = this;
        mDatabaseService = DatabaseService.getInstance(mContext);

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        if (Util.isNetAvailable(mContext)) {
            new CheckSubscriptionAsync().execute();
        } else {
            finish();
        }

    }

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    private void QueryingforPurchasedItems() {

        try {
            Bundle ownedItems = mService.getPurchases(3, getPackageName(), "subs", null);

            int response = ownedItems.getInt("RESPONSE_CODE");

            if (response == 0) {
                ArrayList<String> ownedSkus =
                        ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList<String> purchaseDataList =
                        ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList<String> signatureList =
                        ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                String continuationToken =
                        ownedItems.getString("INAPP_CONTINUATION_TOKEN");

                for (int i = 0; i < purchaseDataList.size(); ++i) {
                    String purchaseData = purchaseDataList.get(i);
                    String signature = signatureList.get(i);
                    String sku = ownedSkus.get(i);

                    try {

                        PurchaseReceipt = new JSONObject(purchaseData);

                        if (PurchaseReceipt.has("autoRenewing") && PurchaseReceipt.getBoolean("autoRenewing")) {
                            new ProcessPaymentAsync().execute();
                            return;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

            finish();

        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

    class ProcessPaymentAsync extends AsyncTask<Void, Void, Void> {

        private Map<String, String> responseMap;
        String responseString = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                String subscripID = "0";

                if (PurchaseReceipt.getString("productId").equals(ITEM_SKU_1)) {
                    subscripID = "5";
                } else if (PurchaseReceipt.getString("productId").equals(ITEM_SKU_2)) {
                    subscripID = "6";
                }

                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(PurchaseReceipt.getLong("purchaseTime"));
                calendar.add(Calendar.MONTH, 1);

                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
                nameValuePair.add(new BasicNameValuePair("userId", mDatabaseService.getCurrentUserId() + ""));
                nameValuePair.add(new BasicNameValuePair("endDate", (calendar.getTimeInMillis() / 1000) + ""));
                nameValuePair.add(new BasicNameValuePair("startDate", (System.currentTimeMillis() / 1000) + ""));
                nameValuePair.add(new BasicNameValuePair("subscripId", subscripID));
                nameValuePair.add(new BasicNameValuePair("receipts", PurchaseReceipt.toString()));
                nameValuePair.add(new BasicNameValuePair("authKey", SharedPrefrenceUtil.getPrefrence(ConsumeBillingActivity.this, "authKey", "")));
                nameValuePair.add(new BasicNameValuePair("deviceType", "android"));

                if (PurchaseReceipt.has("orderId")) {
                    nameValuePair.add(new BasicNameValuePair("transactionid", PurchaseReceipt.getString("orderId")));
                } else {
                    nameValuePair.add(new BasicNameValuePair("transactionid", "transactionId." + PurchaseReceipt.getString("productId")));
                }

                responseMap = getResponseString(WebServiceURL.getInstance().updateSubscribeUser, nameValuePair, true);
                responseString = responseMap.get("ResponseString");

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                SharedPrefrenceUtil.setPrefrence(mContext,
                        "userType", "paid");
                AccountBean accountBean = mDatabaseService.getAccount();
                accountBean.setReceipt(PurchaseReceipt.toString());
                accountBean.setUserType("paid");
                mDatabaseService.insertUpdateContact(accountBean);
            } catch (Exception e) {
                e.printStackTrace();
            }

            finish();
            return;
        }

    }

    class CheckSubscriptionAsync extends AsyncTask<Void, Void, Void> {

        private Map<String, String> responseMap;
        String responseString = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
                nameValuePair.add(new BasicNameValuePair("userId", mDatabaseService.getCurrentUserId() + ""));
                nameValuePair.add(new BasicNameValuePair("authKey", SharedPrefrenceUtil.getPrefrence(ConsumeBillingActivity.this, "authKey", "")));

                responseMap = getResponseString(WebServiceURL.getInstance().getSubscribeUserInfo, nameValuePair, true);
                responseString = responseMap.get("ResponseString");

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                JSONObject object = new JSONObject(responseString);

                if (object.getJSONObject("data").getInt("remainingDays") <= 0) {

                    try {
                        SharedPrefrenceUtil.setPrefrence(mContext,
                                "userType", "free");
                        AccountBean accountBean = mDatabaseService.getAccount();
                        accountBean.setUserType("free");
                        mDatabaseService.insertUpdateContact(accountBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    QueryingforPurchasedItems();

                } else {

                    try {
                        SharedPrefrenceUtil.setPrefrence(mContext,
                                "userType", "paid");
                        AccountBean accountBean = mDatabaseService.getAccount();
                        accountBean.setUserType("paid");
                        mDatabaseService.insertUpdateContact(accountBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                finish();

            } catch (JSONException e) {
                e.printStackTrace();
                finish();
            }

        }

    }

    public synchronized static Map<String, String> getResponseString(
            String url, List<NameValuePair> nameValuePair, boolean flag)
            throws Exception {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        HttpParams httpParams;
        HttpClient client;

        httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 100000); // 100 seconds
        HttpConnectionParams.setSoTimeout(httpParams, 150000);         // 150 seconds
        client = new DefaultHttpClient(httpParams);


        Map<String, String> responseMap = new HashMap<String, String>();
        String responseString = null;

        HttpPost request = new HttpPost(url);

        if (flag) {
            try {

                Log.d("URL", url);
                for (int i = 0; i < nameValuePair.size(); i++) {
                    NameValuePair _sentParams = nameValuePair.get(i);
                    Log.d("PARAM " + i, "KEY : " + _sentParams.getName()
                            + "----- VALUE : " + _sentParams.getValue());
                }
                request.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        HttpResponse response = client.execute(request);
        int responseCode = response.getStatusLine().getStatusCode();
        responseString = EntityUtils.toString(response.getEntity());

        Log.v("Response Code : ", "" + responseCode);
        Log.v("JSON Response : ", responseString);

        // Storing Response in Map
        responseMap.put("ResponseCode", String.valueOf(responseCode));
        responseMap.put("ResponseString", responseString);

        return responseMap;
    }

}
