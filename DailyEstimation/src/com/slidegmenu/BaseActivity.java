package com.slidegmenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.core.CoreFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActionbarActivity;

public class BaseActivity extends SlidingActionbarActivity {

	protected LeftMenuFragment mFrag;
	protected SlidingMenu mSlidingMenu;

	private View mToggleMenu;

	public BaseActivity(int titleRes) {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().hide();
		// set the Behind View
		setBehindContentView(R.layout.slide_menu_frame);
		if (savedInstanceState == null) {
			FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
			mFrag = new LeftMenuFragment();
			t.replace(R.id.menu_frame, mFrag);
			t.commit();
		} else {
			mFrag = (LeftMenuFragment) this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
		}

		// customize the SlidingMenu
		try {
			mSlidingMenu = getSlidingMenu();
			mSlidingMenu.setSlidingEnabled(false);
			mSlidingMenu.setFadeDegree(0.35f);
			mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			mSlidingMenu.setSlidingEnabled(true);
			mSlidingMenu.setShadowDrawable(R.drawable.shadow);
			mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
			mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		} catch (Exception e) {
			e.printStackTrace();
			mSlidingMenu.setShadowWidth(15);
			mSlidingMenu.setBehindOffset(60);
		}
	}

	/**
	 * This method switch fragment.
	 * 
	 * @param fragment
	 *            - from fragment
	 * @param viewReplace
	 *            - Destination view/container
	 * 
	 */
	public void switchFragmentWithoutBackStack(Fragment fragment, int viewReplace) {
		try {
			FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
			mTransaction.replace(viewReplace, fragment);
			mTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			mTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
			mTransaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * If you want to start this activity from another
	 */
	public static void startUrself(Activity context, Class<?> clzz) {
		Intent newActivity = new Intent(context, clzz);
		newActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(newActivity);
	}

	public void toggleLeftMenu() {
		mSlidingMenu.toggle();
	}

	protected void setToggleMenu(View view) {
		if (view != null) {
			mToggleMenu = view;
			mToggleMenu.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					toggleLeftMenu();
				}
			});
		}
	}

	public void switchFragment(int id, CoreFragment mFragment) {
		try {
			FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
			mTransaction.replace(id, mFragment);
			mTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			mTransaction.addToBackStack(null);
			mTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
			mTransaction.commitAllowingStateLoss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}