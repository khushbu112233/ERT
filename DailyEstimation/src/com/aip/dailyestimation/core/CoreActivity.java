package com.aip.dailyestimation.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.aip.dailyestimation.R;

/**
 * This is main core activity. All activity defined in this project that extends this activity.
 * 
 * @author aipxperts
 *
 */
public class CoreActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	/**
	 * This method switch activity to other activity.
	 * If it goes to Login activity then only splash activity finish from the stack.
	 * 
	 * @param activity - from activity 
	 * @param clazz - Destination activity class
	 * 
	 */
	public void switchActivity(Activity activity, Class<?> clazz){
		try {
			Intent intent = new Intent(activity, clazz);
			activity.startActivity(intent);
			
			activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * This method switch activity to other activity.
	 * If it goes to Login activity then only splash activity finish from the stack.
	 * 
	 * @param activity - from activity 
	 * @param clazz - Destination activity class
	 * 
	 */
	public void switchLeftActivity(Activity activity, Class<?> clazz){
		try {
			Intent intent = new Intent(activity, clazz);
			activity.startActivity(intent);
//			if(activity.getClass() == SplashActivity.class)
//				activity.finish();
			
			activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method switch activity to other activity.
	 * If it goes to Login activity then only splash activity finish from the stack.
	 * 
	 * @param activity - from activity 
	 * @param clazz - Destination activity class
	 * 
	 */
	public void switchActivity(Activity activity, Class<?> clazz, Bundle bundle){
		try {
			Intent intent = new Intent(activity, clazz);
			intent.putExtras(bundle);
			activity.startActivity(intent);
			
			activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method start new activity on other activity.
	 * 
	 * @param activity - from activity
	 * @param clazz - Destination activity class
	 */
	public void newActivity(Activity activity, Class<?> clazz){
		try {
			Intent intent = new Intent(activity, clazz);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
}