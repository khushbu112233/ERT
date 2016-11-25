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
import com.aip.dailyestimation.bean.CategoryBean;
import com.aip.dailyestimation.common.async.DeleteCategoryAsync;
import com.aip.dailyestimation.common.async.ReadAllCategoryAsync;
import com.aip.dailyestimation.common.util.IConstants;
import com.aip.dailyestimation.common.util.IResultListner;
import com.aip.dailyestimation.common.util.L;
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

public class CategoryFragment extends CoreFragment {

	View rootView;

	TextView txtNoCategory;
	ListView listCategory;

	DatabaseService mDatabaseService;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_category, container,
					false);

		} else {
			((ViewGroup) rootView.getParent()).removeView(rootView);
		}

		init();
		((MainActivity) getActivity()).setHeaderTitle(R.id.category_fragment);
		
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

		Log.d("CategoryCount", "Total: " + mDatabaseService.getCategoryCount());

		if (Util.isNetAvailable(getActivity())) {

			ReadAllCategoryAsync categoryAsync = new ReadAllCategoryAsync(
					getActivity(), new IResultListner() {

						@Override
						public void result(Object result, boolean isSuccess) {

							JSONObject object = (JSONObject) result;
							try {
								Log.d("ReadAllCategoryAsync",
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
											.deleteAllCategory(mDatabaseService
													.getCurrentUserId());

									for (int Counter = 0; Counter < array
											.length(); Counter++) {
										JSONObject obj = array
												.getJSONObject(Counter);

										CategoryBean categoryBean = new CategoryBean();

										categoryBean.setCategoryServerId(obj
												.getInt("id"));
										categoryBean.setServerId(obj
												.getInt("userId"));
										categoryBean.setCategoryName(obj
												.getString("categoryName"));

										Log.d("CategoryBean: "
												+ categoryBean
														.getCategoryName(),
												"ServerId: "
														+ categoryBean
																.getServerId());

										mDatabaseService.insertUpdateCategory(
												categoryBean, true);
									}

									// refrehList();

									listCategory
											.setAdapter(new ArrayAdapter<String>(
													getActivity(),
													R.layout.item_category,
													R.id.item_txtName,
													mDatabaseService
															.getAllLocalCategoriesName()));

								} else {

									if (object.getString("code").equals("201")) {

										mDatabaseService
												.deleteAllCategory(mDatabaseService
														.getCurrentUserId());

										listCategory
												.setEmptyView(txtNoCategory);

										/*if (!(mDatabaseService
												.getCategoryCount() > 0)) {

											L.confirmDialog(
													getActivity(),
													getResources()
															.getString(
																	R.string.no_category_added),
													new IL() {

														@Override
														public void onSuccess() {
															if (Util.isNetAvailable(getActivity())) {
																AddCategoryFragment addCategoryFragment = new AddCategoryFragment();
																switchFragment(
																		R.id.main_content,
																		addCategoryFragment);
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
			categoryAsync.execute();

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

		txtNoCategory = (TextView) rootView
				.findViewById(R.id.txtNoCategoryFound);
		listCategory = (ListView) rootView.findViewById(R.id.list_categories);

		mDatabaseService = DatabaseService.getInstance(getActivity());

		getActivity().findViewById(R.id.actBar_rightText).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						if (Util.isNetAvailable(getActivity())) {
							addCategory(-1);
						} else {
							L.alert(getActivity(),
									getResources().getString(
											R.string.msg_internet_error));
						}
					}
				});

		// listCategory.setEmptyView(txtNoCategory);

		if (Util.isNetAvailable(getActivity())) {
			// listCategory.setAdapter(new ArrayAdapter<String>(getActivity(),
			// R.layout.item_category, R.id.item_txtName, mDatabaseService
			// .getAllLocalCategoriesName()));
		} else {
			listCategory.setEmptyView(txtNoCategory);
			L.alert(getActivity(),
					getResources().getString(R.string.msg_internet_error));
		}

		// this code filter receipt according to name

		// listCategory.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View v,
		// int position, long id) {
		// try {
		// String categoryName = ((TextView) v
		// .findViewById(R.id.item_txtName)).getText()
		// .toString();
		// /*
		// * if(categoryName != null ) { CategoryBean categoryBean =
		// * mDatabaseService.getCategory(categoryName);
		// * if(categoryBean != null) {
		// * next(categoryBean.getCategoryID()); } }
		// */
		//
		// showReceipts(categoryName);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// });

		listCategory.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View v,
					int position, long id) {

				final String categoryName = ((TextView) v
						.findViewById(R.id.item_txtName)).getText().toString();
				CustomAlertDialog.openOption(getActivity(), categoryName,
						new IOnOptionSelcted() {

							@Override
							public void onOptionSelected(int id) {
								switch (id) {
								case R.id.update:
									onEdit(categoryName);
									break;
								case R.id.delete:
									onDelete(categoryName);
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

	private void addCategory(int id) {
		AddCategoryFragment addCategoryFragment = new AddCategoryFragment();
		if (id != -1) {
			Bundle bundle = new Bundle();
			bundle.putInt(IConstants.ARG_ID, id);
			addCategoryFragment.setArguments(bundle);
		}
		switchFragment(R.id.main_content, addCategoryFragment);
	}

	private void showReceipts(String categoryName) {
		ReceiptFragment receiptFragment = new ReceiptFragment();
		if (categoryName != null) {
			Bundle bundle = new Bundle();
			bundle.putString(IConstants.ARG_ID, categoryName);
			receiptFragment.setArguments(bundle);
		}
		switchFragment(R.id.main_content, receiptFragment);
	}

	private void refrehList() {
		listCategory.setAdapter(new ArrayAdapter<String>(getActivity(),
				R.layout.item_category, R.id.item_txtName, mDatabaseService
						.getAllLocalCategoriesName()));
	}

	private void onDelete(final String categoryName) {
		L.confirmDialog(
				getActivity(),
				getResources().getString(R.string.msg_category_delete,
						categoryName), new L.IL() {

					@Override
					public void onSuccess() {
						try {
							if (categoryName != null) {
								final CategoryBean categoryBean = mDatabaseService
										.getCategory(categoryName);

								if (categoryBean != null) {

									DeleteCategoryAsync deleteCategoryAsync = new DeleteCategoryAsync(
											getActivity(), categoryBean,
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
																	.deleteCategory(categoryBean
																			.getCategoryID());
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
									deleteCategoryAsync.execute();
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

	public void onEdit(String categoryName) {
		if (categoryName != null) {
			CategoryBean categoryBean = mDatabaseService
					.getCategory(categoryName);
			if (categoryBean != null) {
				addCategory(categoryBean.getCategoryID());
			}
		}
	}
}
