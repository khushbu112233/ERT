package com.aip.dailyestimation.common.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Window;

public class FunctionHelper {

	private Activity activity;

	private ProgressDialog mDialog;

	private static FunctionHelper mFunctionHelper;

	private FunctionHelper(){}


	public static FunctionHelper getFunctionHelper(Activity activity){
		if(null == mFunctionHelper)
			mFunctionHelper = new FunctionHelper();

		mFunctionHelper.activity = activity;

		return mFunctionHelper;
	}

	public void disableUserInteration()
	{
		mDialog = new ProgressDialog(activity);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialog.setMessage("Loading...");
		
		mDialog.show();
		Log.e("DIALOG", "SHOW");
	}

	public void enableUserInteration()
	{
		if(mDialog != null)
			mDialog.dismiss();
		
		Log.e("DIALOG", "DISMISS");
	}
}
