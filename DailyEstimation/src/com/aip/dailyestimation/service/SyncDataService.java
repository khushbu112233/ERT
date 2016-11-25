package com.aip.dailyestimation.service;

import android.app.IntentService;
import android.content.Intent;

public class SyncDataService extends IntentService {

	public static final String NOTIFICATION_EVENT_SERVICE = "com.aip.dailyestimation.service.syncdata";

	public SyncDataService() {
		super("SyncDataService");
	}

	// will be called asynchronously by Android
	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
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
} 
