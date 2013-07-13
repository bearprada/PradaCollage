package com.example.pradacollage.util;

import java.util.UUID;

import com.example.pradacollage.Constants;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class ImageStorageHelper {
	
	public interface onSaveListener { 
		public void onSaveSuccess();
		public void onSaveFail();
	}
	
	private static Bitmap loadBitmapFromView(View v) {
		Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(),
				v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		v.draw(c);
		return b;
	}
	
	public static void save(final ContentResolver cr, final ViewGroup allViews, final onSaveListener listener){
		AsyncTask<Void, Void, Void> saveImagesTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {

				Bitmap largeBitmap = Bitmap.createBitmap(allViews.getWidth(),
						allViews.getHeight(), Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(largeBitmap);
				for (int i = 0; i < allViews.getChildCount(); i++) {
					View v = allViews.getChildAt(i);
					canvas.drawBitmap(loadBitmapFromView(v), v.getX(),
							v.getY(), null);
				}
				Bitmap mediumBitmap = Bitmap.createScaledBitmap(largeBitmap,
						(int) (largeBitmap.getWidth() * Constants.MEDIUM_SCALE_FACTOR),
						(int) (largeBitmap.getHeight() * Constants.MEDIUM_SCALE_FACTOR), false);
				Bitmap smallBitmap = Bitmap.createScaledBitmap(largeBitmap,
						(int) (largeBitmap.getWidth() * Constants.SMALL_SCALE_FACTOR),
						(int) (largeBitmap.getHeight() * Constants.SMALL_SCALE_FACTOR), false);
				String fileNamePrefix = "prada_" + UUID.randomUUID().toString();
				Log.d("TEST", MediaStore.Images.Media.insertImage(cr,
						largeBitmap, fileNamePrefix + "_large", ""));
				largeBitmap.recycle();
				Log.d("TEST", MediaStore.Images.Media.insertImage(cr,
						mediumBitmap, fileNamePrefix + "_medium", ""));
				mediumBitmap.recycle();
				Log.d("TEST", MediaStore.Images.Media.insertImage(cr,
						smallBitmap, fileNamePrefix + "_small", ""));
				smallBitmap.recycle();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if(listener!=null)
					listener.onSaveSuccess();
			}
		};
		saveImagesTask.execute();
	}
}
