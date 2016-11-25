package com.aip.dailyestimation.service;

import android.app.Activity;
import android.content.Intent;

import com.aip.dailyestimation.common.util.Util;

public class ServiceHandler {

	public static void startExportService(Activity activity){
		if(Util.isNetAvailable(activity))
			activity.startService(new Intent(activity, ExportDataService.class));
	}
}
