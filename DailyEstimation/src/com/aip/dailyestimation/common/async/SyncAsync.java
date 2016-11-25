package com.aip.dailyestimation.common.async;

import android.app.Activity;

import com.aip.dailyestimation.bean.ReceiptBean;
import com.aip.dailyestimation.common.util.IConstants;
import com.aip.dailyestimation.common.util.IResultListner;
import com.aip.dailyestimation.common.util.ImageUtil;
import com.aip.dailyestimation.service.DatabaseService;
import com.aip.dailyestimation.service.WebServiceReader;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class SyncAsync {

	private Activity activity;

	private IResultListner iResultListner;

	public SyncAsync(Activity activity, IResultListner iResultListner) {
		this.activity = activity;
		this.iResultListner = iResultListner;
	}

	public void sync() {
		DatabaseService mDatabaseService = DatabaseService
				.getInstance(activity);

		WebServiceReader mWebServiceReader = new WebServiceReader();

		List<ReceiptBean> receiptBeans = mDatabaseService.getAllReceipts();

		RequestParams dataJson = new RequestParams();
		dataJson.put(IConstants.KEY_USER_ID, mDatabaseService.getAccount()
				.getServerId());
		int i = 0;
		for (ReceiptBean receiptBean : receiptBeans) {
			if (receiptBean.getIsSync() == 0) {
				try {
					JSONObject param = new JSONObject();

					param.put(IConstants.KEY_LOCAL_RECEIPT_ID,
							receiptBean.getReceiptID());
					param.put(IConstants.KEY_RECEIPT_NAME,
							receiptBean.getName());
					param.put(IConstants.KEY_RECEIPT_CAT,
							receiptBean.getCategoryName());
					param.put(IConstants.KEY_AMOUNT, receiptBean.getAmount()
							+ "");
					param.put(IConstants.KEY_DATE, receiptBean.getDate()
							.getTime());
					param.put(IConstants.KEY_TIP, receiptBean.getTip() + "");
					param.put(IConstants.KEY_COMMENT, receiptBean.getComment());
					param.put(IConstants.KEY_USER_ID, receiptBean.getUserId());
					param.put(IConstants.KEY_IS_DELETE,
							receiptBean.getIsDelete());
					param.put(IConstants.KEY_CREATED_AT, receiptBean
							.getCreatedAt().getTime());
					param.put(IConstants.KEY_UPDATED_AT, receiptBean
							.getUpdateddAt().getTime());
					param.put(IConstants.KEY_DEVICE,
							IConstants.KEY_DEVICE_VALUE);

					if (receiptBean.getServerId() != -1) {
						param.put(IConstants.KEY_ID, "");
					} else
						param.put(IConstants.KEY_ID, receiptBean.getServerId());

					dataJson.put("data[" + i + "]", param);

					if (receiptBean.getImageBytes() == null)
						dataJson.put("receiptImage[" + i + "]", "");
					else {
						File file = ImageUtil.getFileFromByte(receiptBean
								.getImageBytes());
						if (file != null)
							dataJson.put("receiptImage[" + i + "]", file);
						else
							dataJson.put("receiptImage[" + i + "]", "");
					}
					++i;

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		mWebServiceReader.doUpload(activity, false,
				mWebServiceReader.getSync(), dataJson, iResultListner);
	}

}