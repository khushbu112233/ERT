package com.aip.dailyestimation.common.util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

public class RestClient {

	//private static final String BASE_URL = "http://pitsupport.handyortungen-kostenlos.de/api/";

	private static AsyncHttpClient client = new AsyncHttpClient();
	private static RequestHandle requestHandle;
	//Initialise block
	{
//		client.setMaxRetriesAndTimeout(5, 120000);
		client.setMaxRetriesAndTimeout(10, 150000);
	}
	
	public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		//client.setBasicAuth("client", "password");
		requestHandle = client.get(getAbsoluteUrl(url), params, responseHandler);
	}
	
	public static void get(String url, AsyncHttpResponseHandler responseHandler) {
		//client.setBasicAuth("client", "password");
		requestHandle = client.get(getAbsoluteUrl(url), responseHandler);
	}

	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		requestHandle = client.post(getAbsoluteUrl(url), params, responseHandler);
	}
	
	public static void post(String url, AsyncHttpResponseHandler responseHandler) {
		requestHandle = client.post(getAbsoluteUrl(url), responseHandler);
	}

	private static String getAbsoluteUrl(String relativeUrl) {
		return relativeUrl;
	}
	
	public static void stopClient(){
		try {
			if(requestHandle != null)
			requestHandle.cancel(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
