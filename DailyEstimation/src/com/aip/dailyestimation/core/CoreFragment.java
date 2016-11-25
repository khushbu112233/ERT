package com.aip.dailyestimation.core;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.aip.dailyestimation.R;

public class CoreFragment extends Fragment{

	protected void switchFragment(CoreFragment mFragment) {
		try {
			FragmentTransaction mTransaction =  getActivity().getSupportFragmentManager().beginTransaction();
			//mTransaction.replace(R.id.main_content, mFragment);
			mTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			mTransaction.addToBackStack(null);
			mTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
			mTransaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void switchFragment(int id, CoreFragment mFragment) {
		try {
			FragmentTransaction mTransaction =  getActivity().getSupportFragmentManager().beginTransaction();
			mTransaction.replace(id, mFragment);
			mTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			mTransaction.addToBackStack(null);
			mTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
			mTransaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void switchFragmentWithoutBackStack(int id, CoreFragment mFragment) {
		try {
			FragmentTransaction mTransaction =  getActivity().getSupportFragmentManager().beginTransaction();
			mTransaction.replace(id, mFragment);
			mTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
			mTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
			mTransaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	public void pushFragments(Fragment fragment, boolean shouldAnimate, boolean shouldAdd, String tag, int tabContent) {
		FragmentManager manager = getActivity().getSupportFragmentManager();
		int count = manager.getBackStackEntryCount();
		if(count > 0)
		{
			manager.popBackStack();
		}
		FragmentTransaction ft = manager.beginTransaction();
		if (shouldAnimate) {
			/*ft.setCustomAnimations(R.animator.fragment_slide_left_enter,
					R.animator.fragment_slide_left_exit,
					R.animator.fragment_slide_right_enter,
					R.animator.fragment_slide_right_exit);*/
		}
		ft.replace(tabContent, fragment, tag);

		if (shouldAdd) {

			//  here you can create named backstack for realize another logic.
			ft.addToBackStack("TopFragmentContainer");

			ft.addToBackStack(null);
		} else {

			/*and remove named backstack:
	            manager.popBackStack("name of your backstack", FragmentManager.POP_BACK_STACK_INCLUSIVE);
	            or remove whole:*/
			manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
		ft.commit();
	}

	/*
    If you want to start this activity from another
	 */
	public static void startUrself(Activity context, Class<?> clzz) {
		Intent newActivity = new Intent(context, clzz);
		newActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(newActivity);
		context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
}