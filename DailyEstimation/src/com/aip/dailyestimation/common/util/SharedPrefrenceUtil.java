package com.aip.dailyestimation.common.util;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * This class is used for storing and retrieving shared preference values.
 */

public class SharedPrefrenceUtil {

	/**
	 * Used to get a string value stored in SharedPreferences.
	 * 
	 * @param context ,application context
	 * @param pref a string name with which the preference would be stored.
	 * @param def a default string value , nothing is stored in preference 
	 * 
	 * @return String
	 */
	public static String getPrefrence(Context context,String pref,String def)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).getString(pref, def);
	}

	/**
	 * Used to get a string value stored in SharedPreferences.
	 * 
	 * @param context ,application context
	 * @param pref a string name with which the preference would be stored
	 * @param def a string value to be stored in preference 
	 */
	public static void setPrefrence(Context context,String pref,String def)
	{
		Editor e = PreferenceManager.getDefaultSharedPreferences(context).edit();
		e.putString(pref, def);
		e.commit();
	}

	/**
	 * Used to get a boolean value stored in SharedPreferences.
	 * 
	 * @param context ,application context
	 * @param pref a boolean name with which the preference would be stored.
	 * @param def a default boolean value , nothing is stored in preference
	 * 
	 * @return boolean
	 */
	public static boolean getPrefrence(Context context,String pref, boolean def)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(pref, def);
	}

	/**
	 * Used to get a boolean value stored in SharedPreferences.
	 * 
	 * @param context ,application context
	 * @param pref a boolean name with which the preference would be stored
	 * @param def a boolean value to be stored in preference 
	 */
	public static void setPrefrence(Context context,String pref,boolean def)
	{
		Editor e = PreferenceManager.getDefaultSharedPreferences(context)
				.edit();
		e.putBoolean(pref, def);
		e.commit();
	}

	/**
	 * Used to get a integer value stored in SharedPreferences.
	 * 
	 * @param context ,application context
	 * @param pref a integer name with which the preference would be stored
	 * @param def a integer value to be stored in preference
	 * 
	 * @return integer
	 */
	public static int getPrefrence(Context context,String pref,int def)
	{
		int c = PreferenceManager.getDefaultSharedPreferences(context).getInt(pref, def);
		return c;
	}

	/**
	 * Used to get a integer value stored in SharedPreferences.
	 * 
	 * @param context ,application context
	 * @param pref a integer name with which the preference would be stored
	 * @param def a integer value to be stored in preference
	 */
	public static void setPrefrence(Context context,String pref,int def)
	{
		Editor e = PreferenceManager.getDefaultSharedPreferences(context)
				.edit();
		e.putInt(pref, def);
		e.commit();
	}
	
	/**
	 * Used to get a long value stored in SharedPreferences.
	 * 
	 * @param context ,application context
	 * @param pref a long name with which the preference would be stored
	 * @param def a long value to be stored in preference
	 * 
	 * @return long
	 */
	@SuppressWarnings("unused")
	public static long getPrefrence(Context context,String pref,long def)
	{
		long c = PreferenceManager.getDefaultSharedPreferences(context).getLong(pref, def);
		return PreferenceManager.getDefaultSharedPreferences(context).getLong(pref, def);
	}

	/**
	 * Used to get a long value stored in SharedPreferences.
	 * 
	 * @param context ,application context
	 * @param pref a long name with which the preference would be stored
	 * @param def a long value to be stored in preference
	 */
	public static void setPrefrence(Context context,String pref,long def)
	{
		Editor e = PreferenceManager.getDefaultSharedPreferences(context)
				.edit();
		e.putLong(pref, def);
		e.commit();
	}
	
}