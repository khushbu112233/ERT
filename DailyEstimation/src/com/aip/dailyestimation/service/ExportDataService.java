package com.aip.dailyestimation.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.aip.dailyestimation.bean.AccountBean;
import com.aip.dailyestimation.common.util.Response;
import com.google.gson.Gson;

import org.json.JSONObject;

public class ExportDataService extends IntentService {

	public static final String NOTIFICATION_EVENT_SERVICE = "com.aip.dailyestimation.service.exportdata";

	DatabaseService mDatabaseService;

	WebServiceReader mWebServiceReader;

	// Map<Integer, Integer> userIdMapping = new HashMap<Integer, Integer>();

	final String TAG = "#ExportDataService#";

	public ExportDataService() {
		super("ExportDataService");
	}

	// will be called asynchronously by Android
	@Override
	protected void onHandleIntent(Intent intent) {
		try {

			mDatabaseService = DatabaseService
					.getInstance(getApplicationContext());

			mWebServiceReader = new WebServiceReader();

			/*
			
			// Sync Account
			List<AccountBean> accountBeans = mDatabaseService.getAllAccounts();

			if (accountBeans != null) {

				for (AccountBean accountBean : accountBeans) {
					if (accountBean.getIsSync() == 0) {
						if (isUpdateAccount(accountBean)) {

						}
					}
					// userIdMapping.put(accountBean.getID(),
					// accountBean.getServerId());
				}
			}

			List<ReceiptBean> receiptBeans = mDatabaseService.getAllReceipts();

			// JSONObject dataJson = new JSONObject();
			// dataJson.put(IConstants.KEY_USER_ID,
			// mDatabaseService.getCurrentUserId()+"");

			if (mDatabaseService.getCurrentUserId() != -1) {
				MultipartEntity localMultipartEntity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);
				localMultipartEntity
						.addPart(IConstants.KEY_USER_ID, new StringBody(
								mDatabaseService.getCurrentUserId() + ""));
				int i = 0;
				for (ReceiptBean receiptBean : receiptBeans) {
					if (receiptBean.getIsSync() == 0) {
						JSONObject param = new JSONObject();

						param.put(IConstants.KEY_LOCAL_RECEIPT_ID,
								receiptBean.getReceiptID());
						param.put(IConstants.KEY_RECEIPT_NAME,
								receiptBean.getName());
						param.put(IConstants.KEY_RECEIPT_CAT,
								receiptBean.getCategoryName());
						param.put(IConstants.KEY_AMOUNT,
								receiptBean.getAmount() + "");
						param.put(IConstants.KEY_DATE, receiptBean.getDate()
								.getTime());
						param.put(IConstants.KEY_TIP, receiptBean.getTip() + "");
						param.put(IConstants.KEY_COMMENT,
								receiptBean.getComment() + "");
						param.put(IConstants.KEY_USER_ID,
								receiptBean.getUserId() + "");
						param.put(IConstants.KEY_IS_DELETE,
								receiptBean.getIsDelete());
						param.put(IConstants.KEY_CREATED_AT, receiptBean
								.getCreatedAt().getTime());
						param.put(IConstants.KEY_UPDATED_AT, receiptBean
								.getUpdateddAt().getTime());
						param.put(IConstants.KEY_CATEGORY_SERVER_ID,
								receiptBean.getCategoryServerId());
						param.put(IConstants.KEY_ADDRESS_SERVER_ID,
								receiptBean.getAddressServerId());
						param.put(IConstants.KEY_DEVICE,
								IConstants.KEY_DEVICE_VALUE);

						if (receiptBean.getServerId() == -1) {
							param.put(IConstants.KEY_ID, "");
						} else
							param.put(IConstants.KEY_ID,
									receiptBean.getServerId());

						// dataJson.put("data["+i+"]", param);

						localMultipartEntity.addPart("data[" + i + "]",
								new StringBody(param.toString()));

						if (receiptBean.getImageBytes() == null) {
							localMultipartEntity.addPart("receiptImage[" + i
									+ "]", new StringBody(""));
						} else {
							try {
								File file = ImageUtil
										.getFileFromByte(receiptBean
												.getImageBytes());

								if (file != null) {
									// dataJson.put("file["+i+"]",file);
									FileBody localFileBody = new FileBody(file,
											"image/jpeg");
									localMultipartEntity.addPart(
											"receiptImage[" + i + "]",
											localFileBody);
								} else {
									// dataJson.put("file["+i+"]", "");
									localMultipartEntity.addPart(
											"receiptImage[" + i + "]",
											new StringBody(""));
								}

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						++i;
					}
				}

				// Log.e("ExportDataService:",
				// "Receipt Sync::Paramter >> "+dataJson.toString());
				syncReceipt(localMultipartEntity);
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			publishResults();
			stopSelf();
		}
	}

	private void publishResults() {
		try {
			Intent intent = new Intent(NOTIFICATION_EVENT_SERVICE);
			sendBroadcast(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isUpdateAccount(AccountBean accountBean) {
		try {
			JSONObject jsonObject = mWebServiceReader.doSignup(accountBean,ExportDataService.this);
			Log.i(TAG, "isUpdateAccount>>" + jsonObject.toString());
			Gson gson = new Gson();
			Response response = gson.fromJson(jsonObject.toString(),
					Response.class);

			if (response.getCode() == 200) {
				Log.e(TAG, "Get server Id >> "
						+ response.getInnerData().getId());
				accountBean.setServerId(response.getInnerData().getId());
				accountBean.setIsSync(1);

				mDatabaseService.insertUpdateContact(accountBean);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/*public void syncReceipt(MultipartEntity localMultipartEntity) {

		try {
			JSONObject jsonObject = mWebServiceReader.sendRequest(
					mWebServiceReader.getSync(), localMultipartEntity);
			Log.e("ExportDataService:",
					"Receipt Sync::Result >> " + jsonObject.toString());

			ReceiptHelper receiptHelper = Util.getReceiptBeans(jsonObject);

			List<ReceiptBean> receiptBeans = null;

			if (receiptHelper != null)
				receiptBeans = receiptHelper.getReceiptBeans();

			// TODO Insert and/or update receipt here
			if (receiptBeans != null) {
				for (ReceiptBean receiptBean : receiptBeans) {

					receiptBean.setIsSync(1);
					ReceiptBean localReceiptBean = mDatabaseService
							.getReceipt(receiptBean.getReceiptID());

					if (localReceiptBean == null)
						localReceiptBean = mDatabaseService
								.getReceiptByServerId(receiptBean.getServerId());

					if (localReceiptBean != null) {
						// TODO update receipt
						localReceiptBean = Util.getToReceiptBean(receiptBean,
								localReceiptBean);
						mDatabaseService.insertUpdateReceipt(localReceiptBean);
						Log.i("Export Data Service", "Updated receipt bean >> "
								+ localReceiptBean.toString());
					} else {
						// TODO insert receipt
						mDatabaseService.insertUpdateReceipt(receiptBean);

						Log.i("Export Data Service",
								"New Inserted receipt bean >> "
										+ receiptBean.toString());
					}

					localReceiptBean = mDatabaseService.getReceipt(receiptBean
							.getReceiptID());

					// // TODO Insert Category
					// try {
					// Log.d("Category: ",
					// "" + localReceiptBean.getCategoryName());
					// CategoryBean bean = new CategoryBean();
					// bean.setCategoryName(localReceiptBean
					// .getCategoryName().toString());
					// Log.d("IsAvailable In Database: ", ""
					// + mDatabaseService.isCategoryExist(bean));
					// mDatabaseService.insertUpdateCategory(bean, true);
					// Log.d("Insert Category In Database: ", "True");
					// } catch (Exception e) {
					// e.printStackTrace();
					// }

				}
			}

			// TODO Delete receipt here
			List<DeletedId> deletedIds = null;

			if (receiptHelper != null)
				deletedIds = receiptHelper.getDeletedIds();

			if (deletedIds != null) {
				for (DeletedId deletedId : deletedIds) {

					mDatabaseService.deleteServerReceipt(deletedId.getId());

					// Log.e("Deleted Receipt", ">>" + deletedId.getId());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}