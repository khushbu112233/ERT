package com.myinapptest.inappdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.myinapptest.android.util.IabHelper;
import com.myinapptest.android.util.IabResult;
import com.myinapptest.android.util.Inventory;
import com.myinapptest.android.util.Purchase;

public class MainActivity extends Activity {

	// http://coderzheaven.com
	static final String TAG = "CODERZHEAVEN IN APP DEMO";

	// static final String SKU_INAPPITEM = "android.test.purchased"; //"change to your in app item"; // "android.test.cancelled";
	// String base64EncodedPublicKey = "REPLACE WITH YOUR PUBLIC KEY";

	static final String SKU_INAPPITEM = "android.test.purchased";
//	 String base64EncodedPublicKey = "REPLACE WITH YOUR PUBLIC KEY";
	
	// static final String SKU_INAPPITEM = "com.aip.receipttracker.hundred";
	 String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhtpEwfetLawqqAFU46RlE53c68WU235+kWvAMeFAuWXw37lLaePG+wXk+yLTU3B0buntN2pYKBL64Jdsp39T35CaUL7sqI5P3PNzwR6wji9ghEtgUXCWtlmUBbwWKNU+7MMv+sLPzkiDCD1ypbX9Grjw8kxGYr5noS9BXoDNKgHL4BU1t22hA4dbT1tdFJIX4qWzKzxWrB2IMAc+zSH4Or+JiyGepTt0Gw3TxWOv7dCG527cimEvwm130b7ocqMdDRAnTTcXHB35yISrcbh2uzHbcPNbZQGQKhTn61zoEO3LlIPZUNYeL5NMQHLROZ0LtfVOHTkZEvW4EpJiBcPZtQIDAQAB";

	// The helper object
	IabHelper mHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mHelper = new IabHelper(this, base64EncodedPublicKey);

		// enable debug logging (for a production application, you should set
		// this to false).
		mHelper.enableDebugLogging(true);

		// Start setup. This is asynchronous and the specified listener
		// will be called once setup completes.
		Log.d(TAG, "Starting setup.");
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				Log.d(TAG, "Setup finished.");

				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					complain("Problem setting up in-app billing: " + result);
					return;
				}

				// Hooray, IAB is fully set up. Now, let's get an inventory of
				// stuff we own.
				Log.d(TAG, "Setup successful. Querying inventory.");
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});

	}

	// Listener that's called when we finish querying the items and
	// subscriptions we own
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result,
				Inventory inventory) {
			Log.d(TAG, "Query inventory finished.");
			if (result.isFailure()) {
				complain("Failed to query inventory: " + result);
				return;
			}

			Log.d(TAG, "Query inventory was successful.");

			/*
			 * Check for items we own. Notice that for each purchase, we check
			 * the developer payload to see if it's correct! See
			 * verifyDeveloperPayload().
			 */

			// Check for gas delivery -- if we own gas, we should fill up the
			// tank immediately
			Purchase removeAdsPurchase = inventory.getPurchase(SKU_INAPPITEM);
			if (removeAdsPurchase != null
					&& verifyDeveloperPayload(removeAdsPurchase)) {
				Log.d(TAG, "User has already purchased this item for removing ads. Write the Logic for removign Ads.");
				mHelper.consumeAsync(inventory.getPurchase(SKU_INAPPITEM),
						mConsumeFinishedListener);
				return;
			}

			Log.d(TAG, "Initial inventory query finished; enabling main UI.");
		}

	};

	// User clicked the "Buy Gas" button
	public void onBuyGasButtonClicked(View arg0) {
		Log.d(TAG, "Buy gas button clicked.");

		/*
		 * TODO: for security, generate your payload here for verification. See
		 * the comments on verifyDeveloperPayload() for more info. Since this is
		 * a SAMPLE, we just use an empty string, but on a production app you
		 * should carefully generate this.
		 */
		String payload = "";

		mHelper.launchPurchaseFlow(this, SKU_INAPPITEM, 10000,
				mPurchaseFinishedListener, payload);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + ","
				+ data);

		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			Log.d(TAG, "onActivityResult handled by IABUtil.");
		}
	}

	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			Log.d(TAG, "Purchase finished: " + result + ", purchase: "
					+ purchase);
			if (result.isFailure()) {
				complain("Error purchasing: " + result);
				return;
			}
			if (!verifyDeveloperPayload(purchase)) {
				complain("Error purchasing. Authenticity verification failed.");
				return;
			}

			Log.d(TAG, "Purchase successful.");

			if (purchase.getSku().equals(SKU_INAPPITEM)) {
				// bought 1/4 tank of gas. So consume it.
				Log.d(TAG,
						"removeAdsPurchase was succesful.. starting consumption.");
				mHelper.consumeAsync(purchase, mConsumeFinishedListener);
			}
		}
	};

	// Called when consumption is complete
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			Log.d(TAG, "Consumption finished. Purchase: " + purchase
					+ ", result: " + result);

			// We know this is the "gas" sku because it's the only one we
			// consume,
			// so we don't check which sku was consumed. If you have more than
			// one
			// sku, you probably should check...
			if (result.isSuccess()) {
				// successfully consumed, so we apply the effects of the item in
				// our
				// game world's logic, which in our case means filling the gas
				// tank a bit
				Log.d(TAG, "Consumption successful. Provisioning.");
				alert("You have purchased for removing ads from your app.");
			} else {
				complain("Error while consuming: " + result);
			}
			Log.d(TAG, "End consumption flow.");
		}
	};

	/** Verifies the developer payload of a purchase. */
	boolean verifyDeveloperPayload(Purchase p) {
		String payload = p.getDeveloperPayload();

		/*
		 * TODO: verify that the developer payload of the purchase is correct.
		 * It will be the same one that you sent when initiating the purchase.
		 * 
		 * WARNING: Locally generating a random string when starting a purchase
		 * and verifying it here might seem like a good approach, but this will
		 * fail in the case where the user purchases an item on one device and
		 * then uses your app on a different device, because on the other device
		 * you will not have access to the random string you originally
		 * generated.
		 * 
		 * So a good developer payload has these characteristics:
		 * 
		 * 1. If two different users purchase an item, the payload is different
		 * between them, so that one user's purchase can't be replayed to
		 * another user.
		 * 
		 * 2. The payload must be such that you can verify it even when the app
		 * wasn't the one who initiated the purchase flow (so that items
		 * purchased by the user on one device work on other devices owned by
		 * the user).
		 * 
		 * Using your own server to store and verify developer payloads across
		 * app installations is recommended.
		 */

		return true;
	}

	void complain(String message) {
		Log.e(TAG, "**** IN APP Purchase Error: " + message);
		alert(message);
	}

	void alert(String message) {
		Log.d(TAG, "Showing alert dialog: " + message);
		TextView resultTv = (TextView) findViewById(R.id.textView_result);
		resultTv.setText("Result : " + message);
	}

}
