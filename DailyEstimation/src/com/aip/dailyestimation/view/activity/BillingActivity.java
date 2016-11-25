package com.aip.dailyestimation.view.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.bean.AccountBean;
import com.aip.dailyestimation.common.util.L;
import com.aip.dailyestimation.common.util.SharedPrefrenceUtil;
import com.aip.dailyestimation.common.util.Util;
import com.aip.dailyestimation.common.util.WebServiceURL;
import com.aip.dailyestimation.service.DatabaseService;
import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

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

public class BillingActivity extends Activity {

    private static final String LOG_TAG = "BillingActivity";

    private DatabaseService mDatabaseService;

    private IInAppBillingService mService;
    private JSONObject PurchaseReceipt;

    static final String ITEM_SKU_1 = "com.aip.receipt.monthly2years";
    static final String ITEM_SKU_2 = "com.aip.receipt.monthly";

    Context mContext;

    View txtcurrentsubscriptionLine;
    View txtavailablesubLine;

    com.customeview.NormalTextView txtCurrentSubscription;
    com.customeview.NormalTextView txtSelectedSubscription;

    com.customeview.NormalTextView txtAvailableSubscription;
    com.customeview.NormalTextView txtFree;
    com.customeview.NormalTextView txtPackage1;
    com.customeview.NormalTextView txtPackage2;
    com.customeview.NormalTextView txtPackage1Price;
    com.customeview.NormalTextView txtPackage2Price;

    com.customeview.NormalTextView txtCancelSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.billing_activity);

        mContext = this;
        mDatabaseService = DatabaseService.getInstance(mContext);

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        txtcurrentsubscriptionLine = findViewById(R.id.txtcurrentsubscriptionLine);
        txtavailablesubLine = findViewById(R.id.txtavailablesubLine);

        txtCurrentSubscription = (com.customeview.NormalTextView) findViewById(R.id.txtcurrentsubscription);
        txtSelectedSubscription = (com.customeview.NormalTextView) findViewById(R.id.current_sub);

        txtAvailableSubscription = (com.customeview.NormalTextView) findViewById(R.id.txtavailablesub);
        txtFree = (com.customeview.NormalTextView) findViewById(R.id.txtFree);
        txtPackage1 = (com.customeview.NormalTextView) findViewById(R.id.txtPackage1);
        txtPackage2 = (com.customeview.NormalTextView) findViewById(R.id.txtPackage2);
        txtPackage1Price = (com.customeview.NormalTextView) findViewById(R.id.txtPackage1Price);
        txtPackage2Price = (com.customeview.NormalTextView) findViewById(R.id.txtPackage2Price);

        txtCancelSubscription = (com.customeview.NormalTextView) findViewById(R.id.txtCancelSubscription);

        txtPackage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubscribeForProduct(ITEM_SKU_1);
            }
        });

        txtPackage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubscribeForProduct(ITEM_SKU_2);
            }
        });

        AdView mAdView = (AdView) findViewById(R.id.adView);

        if (mDatabaseService.getAccount().getUserType().equals("free")) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            mAdView.setVisibility(View.VISIBLE);
            mAdView.bringToFront();
        } else {
            mAdView.setVisibility(View.INVISIBLE);
        }

        if (Util.isNetAvailable(mContext)) {
            new CheckSubscriptionAsync().execute();
        } else {
            L.alert(mContext,
                    getResources().getString(R.string.msg_internet_error));
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

    public void SubscribeForProduct(String ITEM_SKU_PRO) {
        try {
            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
                    ITEM_SKU_PRO, "subs", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            startIntentSenderForResult(pendingIntent.getIntentSender(),
                    1000, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                    Integer.valueOf(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void QueryingforPurchasedItems() {

        ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Querying For Purchased Items...");
        dialog.show();

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
                            dialog.hide();

                            new ProcessPaymentAsync().execute();
                            return;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.hide();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if (requestCode == 1000) {
                int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

                if (resultCode == RESULT_OK) {
                    try {
                        PurchaseReceipt = new JSONObject(purchaseData);
//                        Log.e(LOG_TAG, "JSONObject: " + jo.toString());
                        String sku = PurchaseReceipt.getString("productId");
                        Log.e(LOG_TAG, "You have bought the " + sku + ". Excellent choice,adventurer!");
                        new ProcessPaymentAsync().execute();
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "Failed to parse purchase data.");
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
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
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Process Payment...");
            dialog.setCancelable(false);
            dialog.show();
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
                nameValuePair.add(new BasicNameValuePair("deviceType", "android"));
                nameValuePair.add(new BasicNameValuePair("authKey", SharedPrefrenceUtil.getPrefrence(BillingActivity.this, "authKey", "")));

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
            dialog.hide();

            try {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.optString("code").equals("1000")) {
                    L.alertLogout(BillingActivity.this, getResources().getString(R.string.msg_user_login_another_device));
                    return;
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }

            SharedPrefrenceUtil.setPrefrence(mContext,
                    "userType", "paid");

            AccountBean accountBean = mDatabaseService.getAccount();
            accountBean.setReceipt(PurchaseReceipt.toString());
            accountBean.setUserType("paid");

            mDatabaseService.insertUpdateContact(accountBean);

            try {
                recreate();
            } catch (Exception e) {
                e.printStackTrace();

                Intent mIntent = new Intent(mContext, BillingActivity.class);
                startActivity(mIntent);
                finish();

            }


            return;
        }

    }

    class CheckSubscriptionAsync extends AsyncTask<Void, Void, Void> {

        private Map<String, String> responseMap;
        String responseString = "";
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Check Subscription...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
                nameValuePair.add(new BasicNameValuePair("userId", mDatabaseService.getCurrentUserId() + ""));
                nameValuePair.add(new BasicNameValuePair("authKey", SharedPrefrenceUtil.getPrefrence(BillingActivity.this, "authKey", "")));

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
            dialog.hide();

            try {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.optString("code").equals("1000")) {
                    L.alertLogout(BillingActivity.this, getResources().getString(R.string.msg_user_login_another_device));
                    return;
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }

            try {

                // if user not subscribe any time then run this code

                JSONObject object = new JSONObject(responseString);

                if (object.getInt("code") == 400) {

                    txtcurrentsubscriptionLine.setVisibility(View.VISIBLE);
                    txtavailablesubLine.setVisibility(View.VISIBLE);

                    txtCurrentSubscription.setVisibility(View.VISIBLE);
                    txtSelectedSubscription.setVisibility(View.VISIBLE);
                    txtSelectedSubscription.setText(R.string.msg_free_user);

                    txtAvailableSubscription.setVisibility(View.VISIBLE);
//                    txtFree.setVisibility(View.VISIBLE);
                    txtPackage1.setVisibility(View.VISIBLE);
                    txtPackage2.setVisibility(View.VISIBLE);

                    txtPackage1Price.setVisibility(View.VISIBLE);
                    txtPackage2Price.setVisibility(View.VISIBLE);

                    QueryingforPurchasedItems();

                    return;
                }

                if (object.getJSONObject("data").getString("subscripId").equals("0")) {

                    txtcurrentsubscriptionLine.setVisibility(View.VISIBLE);
                    txtavailablesubLine.setVisibility(View.VISIBLE);

                    txtCurrentSubscription.setVisibility(View.VISIBLE);
                    txtSelectedSubscription.setVisibility(View.VISIBLE);
                    txtSelectedSubscription.setText(R.string.msg_free_user);

                    txtAvailableSubscription.setVisibility(View.VISIBLE);
//                    txtFree.setVisibility(View.VISIBLE);
                    txtPackage1.setVisibility(View.VISIBLE);
                    txtPackage2.setVisibility(View.VISIBLE);

                    txtPackage1Price.setVisibility(View.VISIBLE);
                    txtPackage2Price.setVisibility(View.VISIBLE);

                } else if (object.getJSONObject("data").getString("subscripId").equals("4")) {

                    txtcurrentsubscriptionLine.setVisibility(View.VISIBLE);
                    txtavailablesubLine.setVisibility(View.VISIBLE);

                    txtCurrentSubscription.setVisibility(View.VISIBLE);
                    txtSelectedSubscription.setVisibility(View.VISIBLE);
                    txtSelectedSubscription.setText(R.string.msg_free_user);

                    txtAvailableSubscription.setVisibility(View.VISIBLE);
//                    txtFree.setVisibility(View.VISIBLE);
                    txtPackage1.setVisibility(View.VISIBLE);
                    txtPackage2.setVisibility(View.VISIBLE);

                    txtPackage1Price.setVisibility(View.VISIBLE);
                    txtPackage2Price.setVisibility(View.VISIBLE);

                } else if (object.getJSONObject("data").getString("subscripId").equals("5")) {

                    txtcurrentsubscriptionLine.setVisibility(View.VISIBLE);
                    txtavailablesubLine.setVisibility(View.VISIBLE);

                    txtCurrentSubscription.setVisibility(View.VISIBLE);
                    txtSelectedSubscription.setVisibility(View.VISIBLE);
                    txtSelectedSubscription.setText(R.string.msg_paid_user);

                    txtAvailableSubscription.setVisibility(View.VISIBLE);
//                    txtPackage1.setVisibility(View.VISIBLE);
                    txtPackage2.setVisibility(View.VISIBLE);
                    txtPackage2Price.setVisibility(View.VISIBLE);
//                    txtPackage1.setEnabled(false);

                } else if (object.getJSONObject("data").getString("subscripId").equals("6")) {

                    txtcurrentsubscriptionLine.setVisibility(View.VISIBLE);
                    txtavailablesubLine.setVisibility(View.VISIBLE);

                    txtCurrentSubscription.setVisibility(View.VISIBLE);
                    txtSelectedSubscription.setVisibility(View.VISIBLE);
                    txtSelectedSubscription.setText(R.string.msg_paid_user2);

                    txtAvailableSubscription.setVisibility(View.VISIBLE);
                    txtPackage1.setVisibility(View.VISIBLE);
                    txtPackage1Price.setVisibility(View.VISIBLE);
//                    txtPackage2.setVisibility(View.VISIBLE);
//                    txtPackage2.setEnabled(false);

                }

                if (object.getJSONObject("data").getInt("remainingDays") > 0 && object.getJSONObject("data").getString("deviceType").equals("android")) {
                    txtCancelSubscription.setVisibility(View.VISIBLE);

                    txtCancelSubscription.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse("market://details?id=" + BillingActivity.this.getPackageName());
                            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                            // To count with Play market backstack, After pressing back button,
                            // to taken back to our application, we need to add following flags to intent.
                            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                            try {
                                startActivityForResult(goToMarket, 2000);
                            } catch (ActivityNotFoundException e) {
//                    startActivity(new Intent(Intent.ACTION_VIEW,
//                            Uri.parse("http://play.google.com/store/apps/details?id=" + SubscriptionActivity.this.getPackageName())));
                            }
                        }
                    });

                } else if (object.getJSONObject("data").getInt("remainingDays") > 0 && object.getJSONObject("data").getString("deviceType").equals("ios")) {
                    txtCancelSubscription.setVisibility(View.VISIBLE);
                    txtCancelSubscription.setText("To cancel your subscription, please login to your iPhone with boss account and cancel your subscription.");
                }

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

            } catch (JSONException e) {
                e.printStackTrace();
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
