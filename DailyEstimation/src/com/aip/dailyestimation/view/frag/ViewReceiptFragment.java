package com.aip.dailyestimation.view.frag;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.bean.ReceiptBean;
import com.aip.dailyestimation.common.async.DeleteReceiptAsync;
import com.aip.dailyestimation.common.util.IConstants;
import com.aip.dailyestimation.common.util.IResultListner;
import com.aip.dailyestimation.common.util.ImageUtil;
import com.aip.dailyestimation.common.util.L;
import com.aip.dailyestimation.common.util.L.IL;
import com.aip.dailyestimation.common.util.Util;
import com.aip.dailyestimation.core.CoreFragment;
import com.aip.dailyestimation.service.DatabaseService;
import com.aip.dailyestimation.view.activity.MainActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

public class ViewReceiptFragment extends CoreFragment {

	View rootView;

	TextView txtAddReceipt, txtDeleteReceipt;

	TextView txtSelectDate;

	TextView txtSelectCategory;

	TextView editReceiptName;

	TextView editAmount;

	TextView editTip;

	TextView editComment;

	ImageView imgBig;

	DatabaseService mDatabaseService;

	ReceiptBean mReceiptBean;

	Calendar mCalendar;

	byte[] image;

	List<String> amounts;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_view_receipt,
					container, false);
		} else {
			((ViewGroup) rootView.getParent()).removeView(rootView);
		}
		((MainActivity) getActivity())
				.setHeaderTitle(R.id.view_receipt_fragment);
		init();

		SharedPreferences sharedPref = getActivity().getPreferences(
				Context.MODE_PRIVATE);
		AdView mAdView = (AdView) rootView.findViewById(R.id.adView);

		if (mDatabaseService.getAccount().getUserType().equals("free")) {
			AdRequest adRequest = new AdRequest.Builder().build();
			mAdView.loadAd(adRequest);
			mAdView.setVisibility(View.VISIBLE);
			mAdView.bringToFront();			
		} else {
			mAdView.setVisibility(View.INVISIBLE);			
		}

		// Fource Fully Give Recept In Update Mode
		// Start Update
		// if (Util.isNetAvailable(getActivity())) {
		// if (mReceiptBean != null) {
		// AddReceiptFragment addReceiptFragment = new AddReceiptFragment();
		// Bundle bundle = new Bundle();
		// bundle.putInt(IConstants.ARG_ID,
		// mReceiptBean.getReceiptID());
		// addReceiptFragment.setArguments(bundle);
		// switchFragment(R.id.main_content, addReceiptFragment);
		// }
		// } else {
		// L.alert(getActivity(),
		// getResources().getString(
		// R.string.msg_internet_error));
		// }
		// End Update
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

		txtAddReceipt = (TextView) rootView.findViewById(R.id.txtEditReceipt);
		txtDeleteReceipt = (TextView) rootView
				.findViewById(R.id.txtDeleteReceipt);
		txtSelectDate = (TextView) rootView.findViewById(R.id.txtDate);
		txtSelectCategory = (TextView) rootView.findViewById(R.id.txtCategory);

		editReceiptName = (TextView) rootView
				.findViewById(R.id.editReceiptName);
		editAmount = (TextView) rootView.findViewById(R.id.txtAmount);
		editTip = (TextView) rootView.findViewById(R.id.editTip);
		editComment = (TextView) rootView.findViewById(R.id.editComment);

		imgBig = (ImageView) rootView.findViewById(R.id.imgImage);

		mDatabaseService = DatabaseService.getInstance(getActivity());

		mCalendar = Calendar.getInstance();

		mReceiptBean = mDatabaseService.getReceipt(getReceiptId());
		if (mReceiptBean != null) {

			try {
				if (mReceiptBean.getImageBytes() != null)
					imgBig.setImageBitmap(ImageUtil.getBitamp(mReceiptBean
							.getImageBytes()));
				else if (mReceiptBean.getServerImgPath() != null
						&& !mReceiptBean.getServerImgPath().equals(""))
					Picasso.with(getActivity())
							.load(mReceiptBean.getServerImgPath()).into(imgBig);
			} catch (Exception e) {
				e.printStackTrace();
			}
			editReceiptName.setText(mReceiptBean.getName());
			editAmount.setText(Util.getAmount(mReceiptBean.getAmount()));
			editTip.setText(Util.getAmount(mReceiptBean.getTip()));
			editComment.setText(mReceiptBean.getComment());

			mCalendar.setTime(mReceiptBean.getDate());
			txtSelectDate.setText(Util.getLongToDate(
					mCalendar.getTimeInMillis(), IConstants.DATE_FORMAT));
			txtSelectCategory.setText(mReceiptBean.getCategoryName());

		} else {
			L.alert(getActivity(),
					getResources().getString(R.string.no_receipt_found_small),
					new IL() {

						@Override
						public void onSuccess() {
							getActivity().onBackPressed();
						}

						@Override
						public void onCancel() {
							getActivity().onBackPressed();
						}
					});
		}

		txtAddReceipt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (Util.isNetAvailable(getActivity())) {
					if (mReceiptBean != null) {
						AddReceiptFragment addReceiptFragment = new AddReceiptFragment();
						Bundle bundle = new Bundle();
						bundle.putInt(IConstants.ARG_ID,
								mReceiptBean.getReceiptID());
						addReceiptFragment.setArguments(bundle);
						switchFragment(R.id.main_content, addReceiptFragment);
					}
				} else {
					L.alert(getActivity(),
							getResources().getString(
									R.string.msg_internet_error));
				}

			}
		});

		txtDeleteReceipt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onDelete(mReceiptBean);
			}
		});
	}

	private boolean isEditMode() {
		if (getArguments() != null
				&& getArguments().containsKey(IConstants.ARG_ID)) {
			return true;
		}
		return false;
	}

	private int getReceiptId() {
		if (getArguments() != null
				&& getArguments().containsKey(IConstants.ARG_ID)) {
			return getArguments().getInt(IConstants.ARG_ID);
		}
		return -1;
	}

	private void onDelete(final ReceiptBean receiptBean) {
		if (receiptBean == null)
			return;

		L.confirmDialog(
				getActivity(),
				getResources().getString(R.string.msg_receipt_delete,
						receiptBean.getName()), new L.IL() {

					@Override
					public void onSuccess() {
						try {
							if (receiptBean.getServerId() > 0) {
								if (Util.isNetAvailable(getActivity()))
									onlineDelete(receiptBean);
								else {
									receiptBean.setIsDelete(1);
									mDatabaseService
											.insertUpdateReceipt(receiptBean);
									getActivity().onBackPressed();
								}
							} else {
								mDatabaseService.deleteReceipt(receiptBean
										.getReceiptID());
								getActivity().onBackPressed();
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

	private void onlineDelete(final ReceiptBean receiptBean) {
		DeleteReceiptAsync deleteReceiptAsync = new DeleteReceiptAsync(
				getActivity(), new IResultListner() {

					@Override
					public void result(Object result, boolean isSuccess) {
						if (isSuccess) {
							mDatabaseService.deleteReceipt(receiptBean
									.getReceiptID());
						} else {
							receiptBean.setIsDelete(1);
							mDatabaseService.insertUpdateReceipt(receiptBean);
						}

						getActivity().onBackPressed();
					}
				});

		deleteReceiptAsync.execute(receiptBean.getServerId());
	}

}