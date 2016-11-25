package com.aip.dailyestimation.common.async;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;

import com.aip.dailyestimation.bean.AccountBean;
import com.aip.dailyestimation.common.util.IConstants;
import com.aip.dailyestimation.common.util.IResultListner;
import com.aip.dailyestimation.common.util.ProgressHUD;
import com.aip.dailyestimation.common.util.Response;
import com.aip.dailyestimation.common.util.SharedPrefrenceUtil;
import com.aip.dailyestimation.service.DatabaseService;
import com.aip.dailyestimation.service.WebServiceReader;
import com.google.gson.Gson;

import org.json.JSONObject;

public class ChangePasswordAsync extends AsyncTask<String, Void, JSONObject> implements OnCancelListener {

    private Activity activity;

    private IResultListner iResultListner;

    private boolean isSuccess;

    private ProgressHUD mProgressHUD;

    private WebServiceReader mWebServiceReader;

    private DatabaseService mDatabaseService;


    public ChangePasswordAsync(Activity activity, IResultListner iResultListner) {
        this.activity = activity;
        this.iResultListner = iResultListner;
    }

    @Override
    protected void onPreExecute() {
        mProgressHUD = ProgressHUD.show(activity, "Please wait...", true, false, this);
        mProgressHUD.setCancelable(false);
        mProgressHUD.setCanceledOnTouchOutside(false);
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            mWebServiceReader = new WebServiceReader();

            mDatabaseService = DatabaseService.getInstance(activity);

            String id = params[0];
            String oldPassword = params[1];
            String newPassword = params[2];

            int serverId = Integer.parseInt(id);

            if (serverId < 1) {
                serverId = getAccountId(mDatabaseService.getAccount());
            }

            isSuccess = true;

            String auth = SharedPrefrenceUtil.getPrefrence(activity, IConstants.KEY_AUTH, "");

            return mWebServiceReader.changePassword(id, oldPassword, newPassword,auth);
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        if (mProgressHUD != null && mProgressHUD.isShowing())
            mProgressHUD.dismiss();

        //if(Util.filterResponse(activity, result))
        iResultListner.result(result, isSuccess);

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mProgressHUD != null && mProgressHUD.isShowing())
            mProgressHUD.dismiss();
    }

    public int getAccountId(AccountBean accountBean) {
        try {
            JSONObject jsonObject = mWebServiceReader.doSignup(accountBean,activity);

            Log.e("ChangePassword : IsUpdateAccount", "" + jsonObject.toString());

            try {
                SharedPrefrenceUtil.setPrefrence(activity, IConstants.KEY_AUTH, jsonObject.getJSONObject("data").getString("authKey"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            Gson gson = new Gson();
            Response response = gson.fromJson(jsonObject.toString(), Response.class);

            if (response.getCode() == 200) {
                accountBean.setServerId(response.getInnerData().getId());
                accountBean.setIsSync(1);

                mDatabaseService.insertUpdateContact(accountBean);
                return response.getInnerData().getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}