package lab.prada.collage.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import lab.prada.collage.Constants;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;

public class StoreImageHelper {
	
	public interface onSaveListener { 
		public void onSaveSuccess();
		public void onSaveFail();
	}

	protected static final int BACKGROUND_COLOR = Color.WHITE;
	protected static final String IMAGE_FORMAT_TYPE = ".jpg";
	
	private static Bitmap loadBitmapFromView(View v) {
		Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(),
				v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		v.draw(c);
		return b;
	}
	
	private static File saveFile(Bitmap bmp, String filename){
		File dcim = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/PradaCollage/");
		if(dcim.exists()==false)
			dcim.mkdirs();
		File f = new File(dcim,filename);
		try {
		       FileOutputStream out = new FileOutputStream(f);
		       bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
		       out.close();
		} catch (Exception e) {
		       e.printStackTrace();
		       return null;
		}
		return f;
	}
	
	private static Uri store(final ContentResolver cr, File file, String name, String description){
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, name);
		values.put(MediaStore.Images.Media.DATE_TAKEN, file.lastModified());
		values.put(MediaStore.Images.Media.DATE_ADDED, file.lastModified());
		values.put(MediaStore.Images.Media.DATE_MODIFIED, file.lastModified());
		values.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, "PradaCollage");
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		values.put(MediaStore.Images.Media.DESCRIPTION, description);
		values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
		return cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
	}
	
	public static void save(final ContentResolver cr, final ViewGroup allViews, final onSaveListener listener){
		AsyncTask<Void, Void, Void> saveImagesTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {

				Bitmap largeBitmap = Bitmap.createBitmap(allViews.getWidth(),
						allViews.getHeight(), Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(largeBitmap);
				canvas.drawColor(BACKGROUND_COLOR);
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
				String fileNamePrefix = UUID.randomUUID().toString();
				store(cr,saveFile(largeBitmap,fileNamePrefix + "_large"+IMAGE_FORMAT_TYPE),fileNamePrefix + "_large"+IMAGE_FORMAT_TYPE,"");
				largeBitmap.recycle();
				store(cr,saveFile(mediumBitmap,fileNamePrefix + "_medium"+IMAGE_FORMAT_TYPE),fileNamePrefix + "_large"+IMAGE_FORMAT_TYPE,"");
				mediumBitmap.recycle();
				store(cr,saveFile(smallBitmap,fileNamePrefix + "_small"+IMAGE_FORMAT_TYPE),fileNamePrefix + "_large"+IMAGE_FORMAT_TYPE,"");
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
