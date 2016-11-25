package com.aip.dailyestimation.customdialog;


public class CustomTimePicker {
//	private Context context;
//	private boolean wheelScrolled=false;
//	Dialog dialog;
//	private String hoursarr[]=new String[24];
//	private final String minutesarr[] = new String[60];
//	
//	private Calendar mCalendar;
//	private IOnDateSelected iOnDateSelected;
//	
//	public CustomTimePicker(Context context, Calendar calendar, IOnDateSelected iOnDateSelected){
//		this.context=context;
//		this.mCalendar = calendar;
//		this.iOnDateSelected = iOnDateSelected;
//		
//	}
//	/**
//	 * Dialog for pick the image from Avatar,Camera,Gallery
//	 */
//	public Dialog dialogBox( int hours, int mins)
//	{
//		mCalendar.set(Calendar.HOUR_OF_DAY, hours);
//		mCalendar.set(Calendar.MINUTE, mins);
//		mCalendar.set(Calendar.SECOND, 0);
//		mCalendar.set(Calendar.MILLISECOND, 0);
//		
//		dialog=new Dialog(context,R.style.DialogSlideAnim);//getParent();
//		dialog.setContentView(R.layout.pick_time_dialog);
//		for(int i=0;i<=23;i++){
//			hoursarr[i]=""+i;
//		}
//		for(int i=0;i<60;i++){
//			minutesarr[i]=""+i;
//		}
//		
//		initWheel1_hours(R.id.p1);
//		initWheel2_minutes(R.id.p2);
//		getWheel(R.id.p1).setCurrentItem(hours);
//		getWheel(R.id.p2).setCurrentItem(mins);
////		((Button)dialog.findViewById(R.id.set)).setTypeface(AppConstant.nexablack);
////		((Button)dialog.findViewById(R.id.cancel)).setTypeface(AppConstant.nexablack);
//		
//		dialog.findViewById(R.id.parentDialog).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();
//			}
//		});
//		dialog.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();
//			}
//		});
//
//		dialog.findViewById(R.id.set).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				int h = Integer.parseInt(hoursarr[getWheel(R.id.p1).getCurrentItem()]);
//				int m = Integer.parseInt(minutesarr[getWheel(R.id.p2).getCurrentItem()]);
//				
//				if(h == 0){
//					if(m == 0){
//						Toast.makeText(context, "Please select the estimate time", Toast.LENGTH_SHORT).show();
//					}else{
//						mCalendar.set(Calendar.HOUR_OF_DAY, h);
//						mCalendar.set(Calendar.MINUTE, m);
//						
//						iOnDateSelected.onDateSelected(mCalendar);
//						
//						dialog.dismiss();
//					}
//				}else{
//					
//				}
//				
//			}
//		});
//		return dialog;
//
//	}
//
//	private void initWheel1_hours(int id){
//		WheelView wheel = (WheelView)dialog.findViewById(id);
//		wheel.setViewAdapter(new ArrayWheelAdapter(context,hoursarr));
//		//  wheel.setVisibleItems(2);
//		wheel.setCurrentItem(0);
//		wheel.addChangingListener(changedListener);
//		wheel.addScrollingListener(scrolledListener);
//	}
//
//	private void initWheel2_minutes(int id){
//		WheelView wheel = (WheelView)dialog. findViewById(id);
//		wheel.setViewAdapter(new ArrayWheelAdapter(context,minutesarr));
//		//  wheel.setVisibleItems(2);
//		wheel.setCurrentItem(0);
//		wheel.addChangingListener(changedListener);
//		wheel.addScrollingListener(scrolledListener);
//	}
//
//	private final OnWheelChangedListener changedListener = new OnWheelChangedListener(){
//		public void onChanged(WheelView wheel, int oldValue, int newValue){
//			if (!wheelScrolled){
//
//			}
//		}
//	};
//
//	OnWheelScrollListener scrolledListener = new OnWheelScrollListener(){
//		public void onScrollStarts(WheelView wheel){
//			wheelScrolled = true;
//		}
//		public void onScrollEnds(WheelView wheel){
//			wheelScrolled = false;
//		}
//		@Override
//		public void onScrollingStarted(WheelView wheel) {
//		}
//		@Override
//		public void onScrollingFinished(WheelView wheel) {
//			updateStatus();
//		}
//	};
//
//	private void updateStatus(){
//		//Logger.e(""+Integer.parseInt(hoursarr[getWheel(R.id.p1).getCurrentItem()]));
//		//Logger.e(""+Integer.parseInt(minutesarr[getWheel(R.id.p2).getCurrentItem()]));
//
//	}
//
//
//
//	/**
//	 * Returns wheel by Id
//	 * 
//	 * @param id
//	 *          the wheel Id
//	 * @return the wheel with passed Id
//	 */
//	private WheelView getWheel(int id){
//		return (WheelView)dialog.findViewById(id);
//	}
}
