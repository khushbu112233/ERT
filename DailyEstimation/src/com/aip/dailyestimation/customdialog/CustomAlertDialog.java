package com.aip.dailyestimation.customdialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aip.dailyestimation.R;

import java.util.List;

public class CustomAlertDialog {

	public static void openOption(final Activity mActivity, String title, final IOnOptionSelcted onOptionSelcted) {

		final Dialog dialog=new Dialog(mActivity, R.style.DialogSlideAnim);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.option_select);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		final TextView optionEdit =(TextView)dialog.findViewById(R.id.optionEdit);
		final TextView optionDelete =(TextView)dialog.findViewById(R.id.optionDelete);

		final TextView optionHead =(TextView)dialog.findViewById(R.id.optionHead);
		optionHead.setText(title);

		optionEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(onOptionSelcted != null)
					onOptionSelcted.onOptionSelected(R.id.update);
			}
		});

		optionDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if(onOptionSelcted != null)
					onOptionSelcted.onOptionSelected(R.id.delete);
			}
		});

		dialog.show();
	}

	public interface IOnOptionSelcted{
		public void onOptionSelected(int id);
	}
	
	public static void openCategoryOption(final Activity mActivity, final List<String> categories, final IOnCategorySelcted OnCategorySelcted) {

		final Dialog dialog=new Dialog(mActivity, R.style.DialogSlideAnim);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_list);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		
		TextView txtHeader = (TextView)dialog.findViewById(R.id.categoryHead);
		
		ListView listCategory = (ListView)dialog.findViewById(R.id.dialogList);
		
		listCategory.setAdapter(new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_1, categories));
		
		listCategory.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				OnCategorySelcted.onCategorySelected(categories.get(position));
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public interface IOnCategorySelcted{
		public void onCategorySelected(String cateogryName);
	}
	
	public static void openListOption(final Activity mActivity, String title, final List<String> options, final IOnListOptionSelcted listOptionSelcted) {

		final Dialog dialog=new Dialog(mActivity, R.style.DialogSlideAnim);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_list);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		
		TextView txtHeader = (TextView)dialog.findViewById(R.id.categoryHead);
		txtHeader.setText(title);
		
		ListView listCategory = (ListView)dialog.findViewById(R.id.dialogList);
		
		listCategory.setAdapter(new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_1, options));
		
		listCategory.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				listOptionSelcted.onOptionSelected(options.get(position));
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public interface IOnListOptionSelcted{
		public void onOptionSelected(String option);
	}
}
