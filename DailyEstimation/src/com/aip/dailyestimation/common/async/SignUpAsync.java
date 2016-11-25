package com.aip.dailyestimation.common.async;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;

import com.aip.dailyestimation.bean.AccountBean;
import com.aip.dailyestimation.common.util.IResultListner;
import com.aip.dailyestimation.common.util.ProgressHUD;
import com.aip.dailyestimation.service.WebServiceReader;

import org.json.JSONObject;

public class SignUpAsync extends AsyncTask<String, Void, JSONObject> implements OnCancelListener{

	private Activity activity;
	
	private IResultListner iResultListner;
	
	private boolean isSuccess;
	
	private ProgressHUD mProgressHUD;
	
	AccountBean mAccountBean;
	
	public SignUpAsync(Activity activity, AccountBean accountBean, IResultListner iResultListner) {
		this.activity = activity;
		this.iResultListner = iResultListner;
		this.mAccountBean = accountBean;
	}

	@Override
	protected void onPreExecute() {
		mProgressHUD = ProgressHUD.show(activity, "Signing up...", true, false, this);
		mProgressHUD.setCancelable(false);
		mProgressHUD.setCanceledOnTouchOutside(false);
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		try {
			Log.v("signup",params.toString()+"");
			WebServiceReader reader = new WebServiceReader();
			return reader.doSignup(mAccountBean,activity);
		} catch (Exception e) {
			e.printStackTrace();
			isSuccess = false;
		}
		return null;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		if(mProgressHUD != null && mProgressHUD.isShowing())
			mProgressHUD.dismiss();
		// if(Util.filterResponse(activity, result))
			iResultListner.result(result, isSuccess);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		if(mProgressHUD != null && mProgressHUD.isShowing())
			mProgressHUD.dismiss();
	}
}