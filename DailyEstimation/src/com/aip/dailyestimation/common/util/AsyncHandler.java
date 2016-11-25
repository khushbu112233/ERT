package com.aip.dailyestimation.common.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

import com.loopj.android.http.JsonHttpResponseHandler;

public abstract class AsyncHandler extends JsonHttpResponseHandler implements OnCancelListener{

	private Context mContext;
	private boolean showProgressHud;
	private ProgressHUD mProgressHUD;

	public AsyncHandler(Context context, boolean showProgressHud){
		this.mContext = context;
		this.showProgressHud = showProgressHud;
	}

	@Override
	public void onStart() {
		if(showProgressHud)
		{
			mProgressHUD = ProgressHUD.show(mContext, "Please wait...", true, false, AsyncHandler.this);
			mProgressHUD.setCancelable(true);
			mProgressHUD.setCanceledOnTouchOutside(false);
		}
	}

	@Override
	public void onFinish() {
		if(showProgressHud)
		{
			if(mProgressHUD != null && mProgressHUD.isShowing())
				mProgressHUD.dismiss();
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		RestClient.stopClient();
		if(mProgressHUD != null && mProgressHUD.isShowing())
			mProgressHUD.dismiss();
	}
}
