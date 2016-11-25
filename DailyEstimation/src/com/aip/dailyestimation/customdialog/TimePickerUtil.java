package com.aip.dailyestimation.customdialog;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.os.Build;
import android.widget.TimePicker;

import java.util.Calendar;


public class TimePickerUtil {

	private Context mContext;
	private int hh;
	private int mm;
	private IOnTimeSetListner timeSetListner;
	private TimePickerDialog timePickerDialog;
	private TimePicker timePicker;
	private int minHH, minMM;
	private boolean is24HourFormat;

	public TimePickerUtil(Context context, int hh, int mm, boolean is24HourFormat, IOnTimeSetListner iOnTimeSetListner){
		this.mContext = context;
		this.hh = hh;
		this.mm = mm;
		this.timeSetListner = iOnTimeSetListner;
		this.is24HourFormat = is24HourFormat;
		init();
	}

	public void init(){
		try {

			timePickerDialog = new TimePickerDialog(mContext, getConstructorListener(), hh, mm, is24HourFormat);
			/*if (hasJellyBeanAndAbove()) {
				timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, 
						mContext.getString(android.R.string.ok),
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							try {
						        Class<?> superclass = timePickerDialog.getClass();
						        Field mTimePickerField = superclass.getDeclaredField("mTimePicker");
						        mTimePickerField.setAccessible(true);
						        timePicker = (TimePicker) mTimePickerField.get(this);
						    } catch (NoSuchFieldException e) {
						    } catch (IllegalArgumentException e) {
						    } catch (IllegalAccessException e) {
						    }
							onTimeSetListener.onTimeSet(timePicker, timePicker.getCurrentHour(), timePicker.getCurrentMinute());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
						mContext.getString(android.R.string.cancel),
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				});
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void show(){
		if(timePickerDialog != null)
		{
			timePickerDialog.show();
		}
	}


	public void setMinTime(int minHH, int minMM) {
		this.minHH = minHH;
		this.minMM = minMM;
	}

	public void setDate(long currentDateLong){
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(currentDateLong);
			this.hh = calendar.get(Calendar.HOUR_OF_DAY);
			this.mm = calendar.get(Calendar.MINUTE);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public interface IOnTimeSetListner{
		public void onTimeSet(int hourOfDay, int minuteOfHour);
	}
	
	private static boolean hasJellyBeanAndAbove() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	private OnTimeSetListener getConstructorListener() {
		return hasJellyBeanAndAbove() ? onTimeSetListener : onTimeSetListener;
	}
	
	OnTimeSetListener onTimeSetListener = new OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

			TimePickerUtil.this.hh = hourOfDay;
			TimePickerUtil.this.mm = minute;
			timeSetListner.onTimeSet(hourOfDay, minute);
	
		}
	};

}
