package com.aip.dailyestimation.common.async;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.common.util.IConstants;
import com.aip.dailyestimation.common.util.IResultListner;
import com.aip.dailyestimation.common.util.L;
import com.aip.dailyestimation.common.util.ProgressHUD;
import com.aip.dailyestimation.common.util.Response;
import com.aip.dailyestimation.common.util.SharedPrefrenceUtil;
import com.aip.dailyestimation.service.DatabaseService;
import com.aip.dailyestimation.service.WebServiceReader;
import com.google.gson.Gson;

import org.json.JSONObject;

public class DeleteReceiptAsync extends AsyncTask<Integer, Void, JSONObject> implements OnCancelListener {

    private Activity activity;

    private IResultListner iResultListner;

    private ProgressHUD mProgressHUD;


    public DeleteReceiptAsync(Activity activity, IResultListner iResultListner) {
        this.activity = activity;
        this.iResultListner = iResultListner;
    }

    @Override
    protected void onPreExecute() {
        mProgressHUD = ProgressHUD.show(activity, "Deleting...", true, false, this);
        mProgressHUD.setCancelable(false);
        mProgressHUD.setCanceledOnTouchOutside(false);
    }

    @Override
    protected JSONObject doInBackground(Integer... params) {
        try {
            int receiptId = params[0];
            String auth = SharedPrefrenceUtil.getPrefrence(activity, IConstants.KEY_AUTH, "");

            WebServiceReader reader = new WebServiceReader();
            return reader.deleteReceipt(receiptId, auth, String.valueOf(DatabaseService
                    .getInstance(activity).getCurrentUserId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        if (mProgressHUD != null && mProgressHUD.isShowing())
            mProgressHUD.dismiss();
        if (result != null) {
            try {
                if (result.optString("code").equals("1000")) {
                    L.alertLogout(activity, activity.getResources().getString(R.string.msg_user_login_another_device));
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.e("Delete Receipt response", ">>" + result.toString());
            Gson gson = new Gson();
            Response response = gson.fromJson(result.toString(), Response.class);
            if (response != null && response.getCode() == 200) {
                Log.e("Delete", "Delete " + result.toString());
                iResultListner.result(result, true);
            } else
                iResultListner.result(result, false);
        } else
            iResultListner.result(result, false);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mProgressHUD != null && mProgressHUD.isShowing())
            mProgressHUD.dismiss();
    }
}