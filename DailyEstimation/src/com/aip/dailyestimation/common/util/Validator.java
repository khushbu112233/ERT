package com.aip.dailyestimation.common.util;

import android.content.Context;

import com.aip.dailyestimation.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

	private static String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})";

	/**
	 * Email Address validation
	 * 
	 * @param input
	 * @return
	 */
	public static String getEmailAddressValid(Context context,
			CharSequence input) throws IllegalAccessException {
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(input);
		if (matcher.matches()) {
			return input.toString().trim();
		}
		// Password and confirm password does not match
		throw new IllegalAccessException(context.getResources().getString(
				R.string.invalid_email));
	}

	/**
	 * Phone number length validation
	 * 
	 * @param input
	 * @return
	 */
	public static String getPasswordValid(Context context, CharSequence input)
			throws IllegalAccessException {
		if (input.toString().trim().length() < 6
				|| input.toString().trim().length() >12)
			// Password must be between 6 to 15 character
			throw new IllegalAccessException(context.getResources().getString(
					R.string.invalid_password_length));
		/*else {
			Pattern regex = Pattern.compile("((?=.*[0-9])(?=.*[a-z]).{6,8})"); // this
																				// is
																				// regex
																				// contain
																				// no
																				// any
																				// special
																				// character
																				// ((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,15})
			Matcher matcher = regex.matcher(input);
			if (!(matcher.find())) {
				// This is very common password which makes it insecure. Please
				// choose a different password. password must contain at least
				// one digit, one lower case, one upper case and one special
				// character.
				throw new IllegalAccessException(context.getResources()
						.getString(R.string.insecure_password));
			}
		}*/
		return input.toString().trim();
	}

	public static String getCurrentPasswordValid(Context context, CharSequence input)
			throws IllegalAccessException {
		if (input.toString().trim().length() <6
				|| input.toString().trim().length() > 12)
			// Password must be between 6 to 15 character
			throw new IllegalAccessException(context.getResources().getString(
					R.string.invalid_current_password_length));
		/*else {
			Pattern regex = Pattern.compile("((?=.*[0-9])(?=.*[a-z]).{6,8})"); // this
																				// is
																				// regex
																				// contain
																				// no
																				// any
																				// special
																				// character
																				// ((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,15})
			Matcher matcher = regex.matcher(input);
			if (!(matcher.find())) {
				// This is very common password which makes it insecure. Please
				// choose a different password. password must contain at least
				// one digit, one lower case, one upper case and one special
				// character.
				throw new IllegalAccessException(context.getResources()
						.getString(R.string.insecure_password));
			}
		}*/
		return input.toString().trim();
	}

	public static String getConfirmPasswordValid(Context context, CharSequence input)
			throws IllegalAccessException {
		if (input.toString().trim().length() <6
				|| input.toString().trim().length() > 12)
			// Password must be between 6 to 15 character
			throw new IllegalAccessException(context.getResources().getString(
					R.string.invalid_confirm_password_length));
		/*else {
			Pattern regex = Pattern.compile("((?=.*[0-9])(?=.*[a-z]).{6,8})"); // this
																				// is
																				// regex
																				// contain
																				// no
																				// any
																				// special
																				// character
																				// ((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,15})
			Matcher matcher = regex.matcher(input);
			if (!(matcher.find())) {
				// This is very common password which makes it insecure. Please
				// choose a different password. password must contain at least
				// one digit, one lower case, one upper case and one special
				// character.
				throw new IllegalAccessException(context.getResources()
						.getString(R.string.insecure_password));
			}
		}*/
		return input.toString().trim();
	}

	public static String getNewPasswordValid(Context context, CharSequence input)
			throws IllegalAccessException {
		if (input.toString().trim().length() <6
				|| input.toString().trim().length() > 12)
			// Password must be between 6 to 15 character
			throw new IllegalAccessException(context.getResources().getString(
					R.string.invalid_New_password_length));
		/*else {
			Pattern regex = Pattern.compile("((?=.*[0-9])(?=.*[a-z]).{6,8})"); // this
																				// is
																				// regex
																				// contain
																				// no
																				// any
																				// special
																				// character
																				// ((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,15})
			Matcher matcher = regex.matcher(input);
			if (!(matcher.find())) {
				// This is very common password which makes it insecure. Please
				// choose a different password. password must contain at least
				// one digit, one lower case, one upper case and one special
				// character.
				throw new IllegalAccessException(context.getResources()
						.getString(R.string.insecure_password));
			}
		}*/
		return input.toString().trim();
	}
	/**
	 * Password and Confirm password validation
	 * 
	 * @param input
	 * @param input
	 * @return
	 */
	public static String getConfPasswordMatch(Context context,
			CharSequence password, CharSequence confCassword)
			throws IllegalAccessException {
		if (password != null && confCassword != null) {
			if (password.toString().equals(confCassword.toString()))
				return password.toString().trim();
		}
		// Password and confirm password does not match
		throw new IllegalAccessException(context.getResources().getString(
				R.string.invalid_confirm_password));
	}

	public static String getConfPasswordMatch_(Context context,
											  CharSequence password, CharSequence confCassword)
			throws IllegalAccessException {
		if (password != null && confCassword != null) {
			if (password.toString().equals(confCassword.toString()))
				return password.toString().trim();
		}
		// Password and confirm password does not match
		throw new IllegalAccessException(context.getResources().getString(
				R.string.invalid_confirm_password_));
	}

	public static boolean isValidateName(String input) {
		if (input == null || input.trim().length() < 1) {
			return false;
		}
		/*
		 * input = input.trim(); Pattern regex =
		 * Pattern.compile("[^\\.A-Za-z0-9_-]"); // this is regex contain no any
		 * special character Matcher matcher = regex.matcher(input); return
		 * (!matcher.find()); // contain no any special character
		 */
		return true;
	}

	/**
	 * Phone number length validation
	 * 
	 * @param input
	 * @return
	 */
	public static String getPhoneNumberValid(Context context, CharSequence input)
			throws IllegalAccessException {
		if (input.toString().trim().length() <= 6
				|| input.toString().trim().length() > 15)
			// Password must be between 6 to 15 character
			throw new IllegalAccessException(context.getResources().getString(
					R.string.invalid_phone));
		else {
			Pattern regex = Pattern.compile("^[-+]?[ 0-9]*\\.?[0-9]+$");
			Matcher matcher = regex.matcher(input);
			if (!(matcher.find())) {
				// This is very common password which makes it insecure. Please
				// choose a different password. password must contain at least
				// one digit, one lower case, one upper case and one special
				// character.
				throw new IllegalAccessException(context.getResources()
						.getString(R.string.invalid_phone));
			}
		}
		return input.toString().trim();
	}

	public static double getValidAmount(Context context, String amount)
			throws IllegalAccessException {
		if (amount == null || amount.trim().length() < 1) {
			throw new IllegalAccessException(context.getResources().getString(
					R.string.empty_amount));
		}

		amount = amount.trim();

		double amountInDouble = 0d;

		try {
			amountInDouble = Double.parseDouble(amount);
			if (amountInDouble < -1) {
				throw new IllegalAccessException(context.getResources()
						.getString(R.string.invalid_amount));
			}

		} catch (Exception e) {
			throw new IllegalAccessException(context.getResources().getString(
					R.string.invalid_amount));
		}
		return amountInDouble;
	}
	
	public static double getValidTip(Context context, String amount)
			throws IllegalAccessException {
		if (amount == null || amount.trim().length() < 1) {
			throw new IllegalAccessException(context.getResources().getString(
					R.string.empty_amount));
		}

		amount = amount.trim();

		double amountInDouble = 0d;

		try {
			amountInDouble = Double.parseDouble(amount);
		} catch (Exception e) {
			throw new IllegalAccessException(context.getResources().getString(
					R.string.invalid_amount));
		}
		return amountInDouble;
	}
	
}
