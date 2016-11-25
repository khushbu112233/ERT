package com.slidegmenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.bean.AccountBean;
import com.aip.dailyestimation.common.util.L;
import com.aip.dailyestimation.common.util.L.IL;
import com.aip.dailyestimation.core.CoreFragment;
import com.aip.dailyestimation.service.DatabaseService;
import com.aip.dailyestimation.view.activity.ChangePasswordActivity;
import com.aip.dailyestimation.view.activity.LoginActivity;
import com.aip.dailyestimation.view.activity.MainActivity;

public class LeftMenuFragment extends CoreFragment implements OnItemClickListener{

	private View rootView;
	private MainActivity activity;

	//	private ListView listView;
	//	private ArrayList <NavDrawerItem> navDrawerItems;
	//	private NavDrawerListAdapter listAdapter; 
	private TextView txtCompName, txtClientEmail;

	DatabaseService mDatabaseService;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if(rootView == null)
		{
			rootView = inflater.inflate(R.layout.layout_left_menu, container, false);
			init();
		}else
		{
			((ViewGroup)rootView.getParent()).removeView(rootView);
		}
		return rootView;
	}

	public void init()
	{
		try {
			activity = (MainActivity)getActivity();
			mDatabaseService = DatabaseService.getInstance(activity);
			//			listView = (ListView)mFilterContainer.findViewById(R.id.leftMenu_listView);
			//			addSlideMenuDrawerItem();
			//			listAdapter = new NavDrawerListAdapter(activity, navDrawerItems);
			//			listView.setAdapter(listAdapter);
			//			listView.setOnItemClickListener(this);

			txtClientEmail = (TextView)rootView.findViewById(R.id.drawer_txtEmail);
			txtCompName = (TextView)rootView.findViewById(R.id.drawer_txtCompanyName);

			AccountBean accountBean = mDatabaseService.getAccount();
			txtCompName.setText(accountBean.getCompanyName());
			txtClientEmail.setText(accountBean.getEmail());


			(rootView.findViewById(R.id.drawer_txtChangePassword)).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startUrself(activity, ChangePasswordActivity.class);
				}
			});

			(rootView.findViewById(R.id.drawer_txtLogout)).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					activity.toggle();
					
					Handler handler = new Handler(Looper.getMainLooper());
					
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							L.confirmDialog(activity, getResources().getString(R.string.msg_logout), new IL() {
								
								@Override
								public void onSuccess() {
									mDatabaseService.clearPreference();
									startUrself(activity, LoginActivity.class);
									activity.finish();
								}
								
								@Override
								public void onCancel() {
									
								}
							});
						}
					});
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addSlideMenuDrawerItem() {
		//		navDrawerItems = new ArrayList<NavDrawerItem>();
		//		NavDrawerItem item1 = new NavDrawerItem();
		//		item1.setIcon(R.drawable.icon_small_white_client);
		//		item1.setTitle(Constants.DRAWER_ITEM_CLIENTS);
		//		navDrawerItems.add(item1);
		//		
		//		NavDrawerItem item2 = new NavDrawerItem();
		//		item2.setIcon(R.drawable.icon_small_white_project);
		//		item2.setTitle(Constants.DRAWER_ITEM_PROJECTS);
		//		navDrawerItems.add(item2);
		//		
		//		NavDrawerItem item3 = new NavDrawerItem();
		//		item3.setIcon(R.drawable.icon_small_white_invoice);
		//		item3.setTitle(Constants.DRAWER_ITEM_INVOICE);
		//		navDrawerItems.add(item3);
		//		
		//		NavDrawerItem item4 = new NavDrawerItem();
		//		item4.setIcon(R.drawable.icon_small_white_news);
		//		item4.setTitle(Constants.DRAWER_ITEM_NEWS);
		//		navDrawerItems.add(item4);
		//		
		//		NavDrawerItem item5 = new NavDrawerItem();
		//		item5.setIcon(R.drawable.icon_small_white_logout);
		//		item5.setTitle(Constants.DRAWER_ITEM_LOG_OUT);
		//		navDrawerItems.add(item5);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		view.setSelected(true);
	}

	/*
    If you want to start this activity from another
	 */
	public static void startUrself(Activity context, Class<?> clzz) {
		Intent newActivity = new Intent(context, clzz);
		newActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(newActivity);
	}
}
