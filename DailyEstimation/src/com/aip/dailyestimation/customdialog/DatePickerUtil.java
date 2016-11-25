package com.aip.dailyestimation.customdialog;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.widget.DatePicker;

import java.util.Calendar;


public class DatePickerUtil {

	private Context mContext;
	//private TextView mTextView;
	private int dd;
	private int mm;
	private int yy;
	private IOnDateSetListner dateSetListner;
	private DatePickerDialog datePickerDialog;

	public DatePickerUtil(Context context, int d, int m, int y, IOnDateSetListner dateSetListner){
		this.mContext = context;
		//this.mTextView = textViewDate;
		this.dd = d;
		this.mm = m;
		this.yy = y;
		this.dateSetListner = dateSetListner;
		init();
	}

	public void init(){
		try {
			datePickerDialog = new DatePickerDialog(mContext,  getConstructorListener(), yy, mm, dd);

			if (hasJellyBeanAndAbove()) {
				datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, 
						mContext.getString(android.R.string.ok),
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DatePicker dp = datePickerDialog.getDatePicker();
						onDateSetListener.onDateSet(dp, dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
					}
				});
				datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
						mContext.getString(android.R.string.cancel),
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void show(){
		if(datePickerDialog != null)
		{
			datePickerDialog.show();
		}
	}


	public void setMinDateLong(long minDateLong) {
		datePickerDialog.getDatePicker().setMinDate(minDateLong);
	}


	public void setMaxDateLong(long maxDateLong) {
		datePickerDialog.getDatePicker().setMaxDate(maxDateLong);
	}

	public void setDate(long currentDateLong){
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(currentDateLong);
			this.yy = calendar.get(Calendar.YEAR);
			this.mm = calendar.get(Calendar.MONTH);
			this.dd = calendar.get(Calendar.DAY_OF_MONTH);
			//mTextView.setText(WorkTrackApplication.getLongToDate(calendar.getTimeInMillis(), Constants.LIST_DATE_FORMAT));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public interface IOnDateSetListner{
		public void onDateSet(int yy, int mm, int dd);
	}

	private static boolean hasJellyBeanAndAbove() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	private OnDateSetListener getConstructorListener() {
		return hasJellyBeanAndAbove() ? null : onDateSetListener;
	}

	private OnDateSetListener onDateSetListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {


			String strMonthOfYear = String.valueOf(monthOfYear + 1);

			if (strMonthOfYear.length() == 1)
				strMonthOfYear = "0" + strMonthOfYear;
			else if (strMonthOfYear.length() == 2)
				strMonthOfYear = "" + strMonthOfYear;

			String strDay = String .valueOf(dayOfMonth);

			if (strDay.length() == 1)
				strDay = "0" + strDay;
			else if (strDay.length() == 2)
				strDay = "" + strDay;

			yy = year;
			mm = monthOfYear;
			dd = dayOfMonth;

			dateSetListner.onDateSet(yy, mm, dd);
		}
	};

}
