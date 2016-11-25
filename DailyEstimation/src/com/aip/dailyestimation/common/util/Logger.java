package com.aip.dailyestimation.common.util;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class includes Log methods to be used in application.
 * 
 * @author aipxperts
 */
public class Logger {

	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");

	public static void info(String tag, String msg) {
		Log.i(tag, getLog(msg));
	}

	public static void debug(String tag, String msg) {
		Log.d(tag, getLog(msg));
	}

	public static void error(String tag, String msg) {
		Log.e(tag, getLog(msg));
	}

	public static void verbose(String tag, String msg) {
		Log.v(tag, getLog(msg));
	}

	public static void warning(String tag, String msg) {
		Log.w(tag, getLog(msg));
	}

	protected static String dateToString(Date date) {
		return sdf.format(date);
	}
	
	public static String getLog(String msg)
	{
		return  " [ " + dateToString(new Date()) + " ] " + msg;
	}
	
}