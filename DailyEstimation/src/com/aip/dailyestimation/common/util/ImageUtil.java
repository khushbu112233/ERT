package com.aip.dailyestimation.common.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ImageUtil {
	@SuppressLint("NewApi")
	public static byte[] getByteArrayFromBitmap(Bitmap bitmap) {
		try {			
			ByteArrayOutputStream blob = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 100 /*ignored for PNG*/, blob);
			byte[] bitmapdata = blob.toByteArray();

			return bitmapdata;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Bitmap getBitamp(byte[] image) throws Exception{
		try {
			if(image == null)
				throw new IllegalArgumentException("Bitmap can not be get from null byte array");
//			BitmapFactory.Options options = new BitmapFactory.Options();
            return BitmapFactory.decodeByteArray(image, 0, image.length); //Convert bytearray to bitmap
		} catch (Exception e) {
			throw e;
		}
		
	}

	public static int getPixel(int dp, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}

	public String getDirectory(){
		return Environment.getExternalStorageDirectory().toString()+"/tabu/image/";
	}
	
	public static File compressFile(File fileUri){
		try {

			int MAX_IMAGE_SIZE = 200 * 1024; // max final file size
			Bitmap bmpPic = BitmapFactory.decodeFile(fileUri.getAbsolutePath());
			/*if ((bmpPic.getWidth() >= 1024) && (bmpPic.getHeight() >= 1024)) {
				BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
				bmpOptions.inSampleSize = 1;
				while ((bmpPic.getWidth() >= 1024) && (bmpPic.getHeight() >= 1024)) {
					bmpOptions.inSampleSize++;
					bmpPic = BitmapFactory.decodeFile(fileUri.getAbsolutePath(), bmpOptions);
				}
				Log.d("#compressFile#", "Resize: " + bmpOptions.inSampleSize);
			}*/
			int compressQuality = 104; // quality decreasing by 5 every loop. (start from 99)
			int streamLength = MAX_IMAGE_SIZE;
			while (streamLength >= MAX_IMAGE_SIZE) {
				ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
				compressQuality -= 5;
				Log.d("#compressFile#", "Quality: " + compressQuality);
				bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
				byte[] bmpPicByteArray = bmpStream.toByteArray();
				streamLength = bmpPicByteArray.length;
				Log.d("#compressFile#", "Size: " + streamLength);
			}
			String filePath = fileUri.getAbsolutePath();
			try {
				fileUri.delete();
				FileOutputStream bmpFile = new FileOutputStream(filePath);
				bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile);
				bmpFile.flush();
				bmpFile.close();
			} catch (Exception e) {
				Log.e("#compressFile#", "Error on saving file");
			}

			return new File(filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
	/**
	 * Bitamp resize of widht & height
	 * @param image
	 * @param bitmapWidth
	 * @param bitmapHeight
	 * @return
	 */
	public Bitmap getCropedBitmap(Bitmap image, int bitmapWidth, int bitmapHeight) {

		int w = image.getWidth();
		int h = image.getHeight();

		if(w < bitmapWidth)
			image = Bitmap.createScaledBitmap(image, bitmapWidth, h, false);

		if(h < bitmapHeight)
			image = Bitmap.createScaledBitmap(image, image.getWidth(), bitmapHeight, false);

		return Bitmap.createBitmap(image, 0, 0, image.getWidth(), bitmapHeight);
	}
	
	public String sendFile(String url, File yourFile){
		try {

			FileBody filebody = new FileBody(yourFile, "image/jpeg");
			System.out.println("File body >>"+filebody.getContentLength());

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(url);
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			/*StringBody stringBody = new StringBody(userDataVO.getAuthKey());
			reqEntity.addPart(JsonKey.AUTH_KEY, stringBody);
			reqEntity.addPart(JsonKey.PROFILE_PICTURE, filebody);*/
			postRequest.setEntity(reqEntity);

			HttpResponse httpResponse = null;
			httpResponse = httpClient.execute(postRequest);

			InputStream inputStream = null;
			inputStream = httpResponse.getEntity().getContent();

			String result = null;
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * This method converts dp unit to equivalent pixels, depending on device density. 
	 * 
	 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
	 * @param context �Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on device density
	 */
	public static float convertDpToPixel(float dp, Context context){
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	public String convertInputStreamToString(InputStream inputStream) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;
		inputStream.close();
		return result;
	}
	
	public static File getFileFromBitmap(Bitmap imageData) {

		Bitmap bmp = imageData;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
		byte[] data = bos.toByteArray();

		File imageFile = new File(Environment.getExternalStorageDirectory(), "temp.png");

		if (imageFile.exists()) {
			imageFile.delete();
		}

		try {
			FileOutputStream fos=new FileOutputStream(imageFile.getPath());

			fos.write(data);
			fos.close();
			imageFile = compressFile(imageFile);
			return imageFile;
		}
		catch (java.io.IOException e) {
			Log.e("PictureDemo", "Exception in photoCallback", e);
		}
		return null;
	}
	
	public static File getFileFromByte(byte[] data) {


		File imageFile = new File(Environment.getExternalStorageDirectory(), "temp.png");

		if (imageFile.exists()) {
			imageFile.delete();
		}

		try {
			FileOutputStream fos=new FileOutputStream(imageFile.getPath());

			fos.write(data);
			fos.close();
			imageFile = compressFile(imageFile);
			return imageFile;
		}
		catch (java.io.IOException e) {
			Log.e("PictureDemo", "Exception in photoCallback", e);
		}
		return null;
	}
}
