package lab.prada.collage.util;

import java.io.IOException;

import lab.prada.collage.Constants;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class CameraImageHelper {
	
	public static Bitmap checkAndRotatePhoto(String path) throws IOException {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = Constants.BITMAP_SAMPLE_SIZE;

		ExifInterface exif = new ExifInterface(path);
		int exifOrientation = exif
				.getAttributeInt(ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);

		int rotate = 0;

		switch (exifOrientation) {
		case ExifInterface.ORIENTATION_ROTATE_90:
			rotate = 90;
			break;

		case ExifInterface.ORIENTATION_ROTATE_180:
			rotate = 180;
			break;

		case ExifInterface.ORIENTATION_ROTATE_270:
			rotate = 270;
			break;
		}

		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		if (rotate != 0) {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();

			// Setting pre rotate
			Matrix mtx = new Matrix();
			mtx.preRotate(rotate);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		}
		return bitmap;

	}
}
