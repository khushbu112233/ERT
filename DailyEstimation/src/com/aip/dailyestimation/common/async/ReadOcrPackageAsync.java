package com.aip.dailyestimation.common.async;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

import com.aip.dailyestimation.common.util.IConstants;
import com.aip.dailyestimation.common.util.IResultListner;
import com.aip.dailyestimation.common.util.ProgressHUD;
import com.aip.dailyestimation.common.util.SharedPrefrenceUtil;
import com.aip.dailyestimation.service.DatabaseService;
import com.aip.dailyestimation.service.WebServiceReader;

import org.json.JSONObject;

public class ReadOcrPackageAsync extends AsyncTask<String, Void, JSONObject>
		implements OnCancelListener {

	private Activity activity;

	private IResultListner iResultListner;

	private boolean isSuccess;

	private DatabaseService mDatabaseService; 
	
	private ProgressHUD mProgressHUD;

	public ReadOcrPackageAsync(Activity activity, IResultListner iResultListner) {
		this.activity = activity;
		this.iResultListner = iResultListner;
		mDatabaseService = DatabaseService.getInstance(activity);
	}

	@Override
	protected void onPreExecute() {
		mProgressHUD = ProgressHUD.show(activity, "", true, false,
				this);
		mProgressHUD.setCancelable(false);
		mProgressHUD.setCanceledOnTouchOutside(false);
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		try {
			String userID = mDatabaseService.getCurrentUserType();
			String auth = SharedPrefrenceUtil.getPrefrence(activity, IConstants.KEY_AUTH, "");

			WebServiceReader reader = new WebServiceReader();
			return reader.ReadOCRPackages(userID,auth);
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
		// if (Util.filterResponse(activity, result))
			iResultListner.result(result, isSuccess);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		if (mProgressHUD != null && mProgressHUD.isShowing())
			mProgressHUD.dismiss();
	}
}