package com.aip.dailyestimation.view.frag;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.TextView;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.core.CoreFragment;
import com.aip.dailyestimation.service.DatabaseService;
import com.aip.dailyestimation.view.activity.MainActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class CameraTipFragment extends CoreFragment {

	View rootView;
	TextView CameraTip;
	WebView wv;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_taking_camera_tip,
					container, false);
			init();
			((MainActivity) getActivity())
					.setHeaderTitle(R.id.camera_tip_fragment);
		} else {
			((ViewGroup) rootView.getParent()).removeView(rootView);
		}

		SharedPreferences sharedPref = getActivity().getPreferences(
				Context.MODE_PRIVATE);
		AdView mAdView = (AdView) rootView.findViewById(R.id.adView);

		if (DatabaseService.getInstance(getActivity()).getAccount()
				.getUserType().equals("free")) {
			AdRequest adRequest = new AdRequest.Builder().build();
			mAdView.loadAd(adRequest);
			mAdView.setVisibility(View.VISIBLE);
			mAdView.bringToFront();
		} else {
			mAdView.setVisibility(View.INVISIBLE);
		}

		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		final InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
		super.onActivityCreated(savedInstanceState);
	}

	public void init() {
		CameraTip = (TextView) rootView.findViewById(R.id.cameraTip);
		wv = (WebView) rootView.findViewById(R.id.web);
		wv.loadUrl("file:///android_asset/taking_tips.html");
		wv.setBackgroundColor(0x00FFFFFF);

		String msgOld = "These tips will help you take better pictures of your receipts and allow the built in Optical Character Recognition engine in our app to attempt to read values on your receipts to save you some typing. \n\n"
				+ "You do not need to follow these tips to use the app if you are not interested in using the built in OCR engine within the app. \n\n"
				+ "If you want to take advantage of OCR, using OCR is simple, when you scan a receipt, the OCR engine automatically will try to read your receipt. You will find an OCR button on the following screen offering you values it read to chose from. OCR is not an exact science, there may be multiple choices, some or all may be incorrect or there may be no choices at all, all depending on how well you follow these tips. \n\n"
				+ "1. Sunlight is better than indoor light. \n\n"
				+ "2. If you use indoor light, make sure you have strong, bright light above the receipt, with no shadows. \n\n"
				+ "3. Do not use your flash. \n\n"
				+ "4. Remove as many wrinkles as possible from the receipt. \n\n"
				+ "5. Take the picture as close as possible, capturing only the part of the receipt that has the info you need. \n\n"
				+ "6. Fill the camera with the receipt fully when taking the picture. \n\n"
				+ "7. Take the picture vertically, not horizontally. \n\n"
				+ "8. To save lots of time make sure you have names and categories setup so you don't have to type them every time.";

		String msgNew = "These tips will help you take better pictures of your receipts and allow the built in Optical Character Recognition engine in our app to attempt to read values on your receipts to save you some typing.\n\n" +
				"You do not need to follow these tips if you are not interested in using the built in OCR engine within the app.,\n\n" +
				"If you want to take advantage of OCR, using OCR is simple, when you scan a receipt, the OCR engine will automatically try to read your receipt. You will find an OCR button on the following screen offering you values it reads to choose from. OCR is not an exact science, there may be multiple choices, some or all may be incorrect, or there may be no choices at all, depending on how well you follow these tips.\n\n" +
				"1. Sunlight is better than indoor light.\n\n" +
				"2. If you use indoor light, make sure you have strong, bright light above the receipt, with no shadows.\n\n" +
				"3. Do not use your flash.\n\n" +
				"4. Remove as many wrinkles as possible from the receipt.\n\n" +
				"5. Take the picture as close as possible, capturing only the part of the receipt which has the info you need.\n\n" +
				"6. Fill the camera fully with the receipt when taking the picture.\n\n" +
				"7. Take the picture vertically, not horizontally.\n\n" +
				"8. To save time make sure you have names and categories setup so you don't have to type them every time.\n\n";

		CameraTip.setText(msgNew);
	}
}