package com.aip.dailyestimation.common.camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.common.util.Global;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Custom_CameraActivity extends Activity {

	private Camera mCamera;
	private CameraPreview mCameraPreview;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		mCamera = getCameraInstance();
		mCameraPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mCameraPreview);

		Button captureButton = (Button) findViewById(R.id.button_capture);
		captureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCamera.takePicture(null, null, mPicture);
			}
		});
	}

	/**
	 * Helper method to access the camera returns null if it cannot get the
	 * camera or does not exist
	 * 
	 * @return
	 */
	private Camera getCameraInstance() {
		Camera camera = null;
		try {
			camera = Camera.open();
			Parameters params = camera.getParameters();
			List<Camera.Size> sizes = params.getSupportedPictureSizes();
			// params.setPictureSize(1920, 1080);

//			Log.e("Support Screen", "" + sizes.toString());

//			for (int i = 0; i < sizes.size(); i++) {
//				Log.e("Support Screen", "" + sizes.get(i));
//			}

		} catch (Exception e) {
			// cannot get camera or does not exist
			Log.e("Error Support Screen", "Error Support Screen");
			e.printStackTrace();
		}

		return camera;
	}

	PictureCallback mPicture = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			Global.setImageBitmap(bitmap);

			Log.e("BitMap Image Size: ", "Width: " + bitmap.getWidth()
					+ "  Height: " + bitmap.getHeight());

			ImageView i = (ImageView) findViewById(R.id.imageView1);
			i.setImageBitmap(bitmap);
			i.setVisibility(View.VISIBLE);
			i.bringToFront();

			File pictureFile = getOutputMediaFile();
			if (pictureFile == null) {
				return;
			}
			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
				Log.d("Image Store", "Image Store");
			} catch (FileNotFoundException e) {

			} catch (IOException e) {
			}

			// Bundle extras = new Bundle();
			// extras.putString("OFF_OCR", "OFF_OCR");
			// AddReceiptFragment addReceiptFragment = new AddReceiptFragment();
			// addReceiptFragment.setArguments(extras);
			// BaseActivity.switchFragment(R.id.main_content,
			// addReceiptFragment);
			// finish();

		}
	};

	private static File getOutputMediaFile() {
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraApp");
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".jpg");

		return mediaFile;
	}
}