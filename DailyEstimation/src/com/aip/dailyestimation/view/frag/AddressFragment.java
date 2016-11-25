package com.aip.dailyestimation.view.frag;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.bean.AddressBean;
import com.aip.dailyestimation.common.async.DeleteAddressAsync;
import com.aip.dailyestimation.common.async.ReadAllAddressAsync;
import com.aip.dailyestimation.common.util.IConstants;
import com.aip.dailyestimation.common.util.IResultListner;
import com.aip.dailyestimation.common.util.L;
import com.aip.dailyestimation.common.util.SharedPrefrenceUtil;
import com.aip.dailyestimation.common.util.Util;
import com.aip.dailyestimation.core.CoreFragment;
import com.aip.dailyestimation.customdialog.CustomAlertDialog;
import com.aip.dailyestimation.customdialog.CustomAlertDialog.IOnOptionSelcted;
import com.aip.dailyestimation.service.DatabaseService;
import com.aip.dailyestimation.view.activity.MainActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddressFragment extends CoreFragment {

	View rootView;

	TextView txtNoAddress;
	ListView listAddress;

	DatabaseService mDatabaseService;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_address, container,
					false);

		} else {
			((ViewGroup) rootView.getParent()).removeView(rootView);
		}

		init();
		((MainActivity) getActivity()).setHeaderTitle(R.id.address_fragment);

		SharedPreferences sharedPref = getActivity().getPreferences(
				Context.MODE_PRIVATE);
		AdView mAdView = (AdView) rootView.findViewById(R.id.adView);

		mDatabaseService = DatabaseService.getInstance(getActivity());

		if (mDatabaseService.getAccount().getUserType().equals("free")) {
			AdRequest adRequest = new AdRequest.Builder().build();
			mAdView.loadAd(adRequest);
			mAdView.setVisibility(View.VISIBLE);
			mAdView.bringToFront();
		} else {
			mAdView.setVisibility(View.INVISIBLE);
		}

		Log.d("AddressCount", "Total: " + mDatabaseService.getAddressCount());
		if (Util.isNetAvailable(getActivity())) {

			ReadAllAddressAsync addressAsync = new ReadAllAddressAsync(
					getActivity(), new IResultListner() {

						@Override
						public void result(Object result, boolean isSuccess) {

							JSONObject object = (JSONObject) result;
							try {
								Log.d("ReadAllAddressAsync",
										"" + object.toString(4));
							} catch (Exception e1) {
								e1.printStackTrace();
							}

							try {
								try {
									if (object.optString("code").equals("1000")) {
										L.alertLogout(getActivity(), getResources().getString(R.string.msg_user_login_another_device));
										return;
									}
								} catch (Exception e) {
									e.printStackTrace();
								}

								if (object.getString("code").equals("200")) {

									JSONArray array = object
											.getJSONArray("data");

									mDatabaseService
											.deleteAllAddress(mDatabaseService
													.getCurrentUserId());

									for (int Counter = 0; Counter < array
											.length(); Counter++) {
										JSONObject obj = array
												.getJSONObject(Counter);

										AddressBean addressBean = new AddressBean();
										addressBean.setAddressServerId(obj
												.getInt("id"));
										addressBean.setServerId(obj
												.getInt("userId"));
										addressBean.setAddressName(obj
												.getString("addressBookName"));

										mDatabaseService.insertUpdateAddress(
												addressBean, true);
									}

									// refrehList();

									listAddress
											.setAdapter(new ArrayAdapter<String>(
													getActivity(),
													R.layout.item_address,
													R.id.item_txtName,
													mDatabaseService
															.getAllLocalAddressName()));

								} else {

									if (object.getString("code").equals("201")) {

										mDatabaseService
												.deleteAllAddress(mDatabaseService
														.getCurrentUserId());

										listAddress.setEmptyView(txtNoAddress);

										/*if (!(mDatabaseService
												.getAddressCount() > 0)) {

											L.confirmDialog(
													getActivity(),
													getResources()
															.getString(
																	R.string.no_address_added),
													new IL() {

														@Override
														public void onSuccess() {
															if (Util.isNetAvailable(getActivity())) {
																AddAddressFragment addAddressFragment = new AddAddressFragment();
																switchFragment(
																		R.id.main_content,
																		addAddressFragment);
															} else {
																L.alert(getActivity(),
																		getResources()
																				.getString(
																						R.string.msg_internet_error));

															}

														}

														@Override
														public void onCancel() {

														}
													});
										}*/
									} else {
										L.alert(getActivity(),
												object.getString("message"));
									}

								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
			addressAsync.execute();

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

	@Override
	public void onResume() {
		super.onResume();		
	}

	private void init() {

		txtNoAddress = (TextView) rootView.findViewById(R.id.txtNoAddressFound);
		listAddress = (ListView) rootView.findViewById(R.id.list_addresses);

		mDatabaseService = DatabaseService.getInstance(getActivity());

		getActivity().findViewById(R.id.actBar_rightText).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						if (Util.isNetAvailable(getActivity())) {
							addAddress(-1);
						} else {
							L.alert(getActivity(),
									getResources().getString(
											R.string.msg_internet_error));
						}
					}
				});

		// listAddress.setEmptyView(txtNoAddress);

		if (Util.isNetAvailable(getActivity())) {

			// listAddress.setAdapter(new ArrayAdapter<String>(getActivity(),
			// R.layout.item_address, R.id.item_txtName, mDatabaseService
			// .getAllLocalAddressName()));
		} else {
			listAddress.setEmptyView(txtNoAddress);
			L.alert(getActivity(),
					getResources().getString(R.string.msg_internet_error));
		}

		// this code filter receipt according to name

		// listAddress.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View v,
		// int position, long id) {
		// try {
		// String addressName = ((TextView) v
		// .findViewById(R.id.item_txtName)).getText()
		// .toString();
		// /*
		// * if(categoryName != null ) { CategoryBean categoryBean =
		// * mDatabaseService.getCategory(categoryName);
		// * if(categoryBean != null) {
		// * next(categoryBean.getCategoryID()); } }
		// */
		//
		// showAddress(addressName);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// });

		listAddress.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View v,
					int position, long id) {

				final String addressName = ((TextView) v
						.findViewById(R.id.item_txtName)).getText().toString();
				CustomAlertDialog.openOption(getActivity(), addressName,
						new IOnOptionSelcted() {

							@Override
							public void onOptionSelected(int id) {
								switch (id) {
								case R.id.update:
									onEdit(addressName);
									break;
								case R.id.delete:
									onDelete(addressName);
									break;
								default:
									break;
								}
							}
						});
				return true;
			}
		});
	}

	private void addAddress(int id) {
		AddAddressFragment addaddressFragment = new AddAddressFragment();
		if (id != -1) {
			Bundle bundle = new Bundle();
			bundle.putInt(IConstants.ARG_ID, id);
			addaddressFragment.setArguments(bundle);
		}
		switchFragment(R.id.main_content, addaddressFragment);
	}

	private void showAddress(String addressName) {
		ReceiptFragment receiptFragment = new ReceiptFragment();
		if (addressName != null) {
			Bundle bundle = new Bundle();
			bundle.putString(IConstants.ARG_ID, addressName);
			receiptFragment.setArguments(bundle);
			SharedPrefrenceUtil.setPrefrence(getActivity(), "SearchByAddress",
					"true");
		}
		switchFragment(R.id.main_content, receiptFragment);
	}

	private void refrehList() {
		listAddress.setAdapter(new ArrayAdapter<String>(getActivity(),
				R.layout.item_address, R.id.item_txtName, mDatabaseService
						.getAllLocalAddressName()));
	}

	private void onDelete(final String addressName) {
		L.confirmDialog(
				getActivity(),
				getResources().getString(R.string.msg_address_delete,
						addressName), new L.IL() {

					@Override
					public void onSuccess() {
						try {
							if (addressName != null) {
								final AddressBean addressBean = mDatabaseService
										.getAddress(addressName);
								if (addressBean != null) {

									DeleteAddressAsync deleteAddressAsync = new DeleteAddressAsync(
											getActivity(), addressBean,
											new IResultListner() {

												@Override
												public void result(
														Object result,
														boolean isSuccess) {

													JSONObject object = (JSONObject) result;

													try {

														try {
															if (object.optString("code").equals("1000")) {
																L.alertLogout(getActivity(), getResources().getString(R.string.msg_user_login_another_device));
																return;
															}
														} catch (Exception e) {
															e.printStackTrace();
														}

														if (object.getString(
																"code").equals(
																"200")) {

															mDatabaseService
																	.deleteAddress(addressBean
																			.getAddressID());
															refrehList();

														} else {
															String message = object
																	.getString("message");
															L.alert(getActivity(),
																	message,
																	new L.IL() {

																		@Override
																		public void onSuccess() {
																			// getActivity()
																			// .onBackPressed();
																		}

																		@Override
																		public void onCancel() {

																		}
																	});
														}
													} catch (JSONException e) {
														e.printStackTrace();
														L.alert(getActivity(),
																e.getMessage());
													}
												}
											});
									deleteAddressAsync.execute();

								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onCancel() {

					}
				});
	}

	public void onEdit(String addressName) {
		if (addressName != null) {
			AddressBean addressBean = mDatabaseService.getAddress(addressName);
			if (addressBean != null) {
				addAddress(addressBean.getAddressID());
				Log.d("" + addressBean.getAddressID(), "" + addressName);
			}
		}
	}
}
