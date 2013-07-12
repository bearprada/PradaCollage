package com.example.pradacollage;

import java.io.IOException;
import java.util.UUID;

import com.androidquery.AQuery;
import com.example.pradacollage.comp.PradaText;
import com.example.pradacollage.comp.PradaText.OnTextListener;
import com.example.pradacollage.comp.PradaTextFactory;
import com.example.pradacollage.util.GlassDetector;

import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity implements OnTextListener {

	private static final int SELECT_PHOTO = 0;
	private static final int ADD_NEW_TEXT = 1;
	private static final int MODIFY_TEXT = 2;

	private AQuery aq;
	private ProgressDialog progressDialog;
	private ViewGroup allViews;
	private ViewGroup textViews;
	private ViewGroup imageViews;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		aq = new AQuery(this);
		aq.find(R.id.btnSave).clicked(this, "clickSave");
		aq.find(R.id.btnAddPic).clicked(this, "clickAddPic");
		aq.find(R.id.btnAddText).clicked(this, "clickAddText");
		aq.find(R.id.btnAddGlasses).clicked(this, "clickAddGlasses");
		allViews = (ViewGroup) aq.find(R.id.frame).getView();
		textViews = (ViewGroup) aq.find(R.id.frame_texts).getView();
		imageViews = (ViewGroup) aq.find(R.id.frame_images).getView();
	}

	private void showProgressDialog(boolean enable) {
		if (enable) {
			progressDialog = ProgressDialog.show(this, getResources()
					.getString(R.string.progress_title), getResources()
					.getString(R.string.progress_message), true);
		} else {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	}

	public void clickSave(View button) {
		showProgressDialog(true);
		AsyncTask<Void, Void, Void> saveImagesTask = new AsyncTask<Void, Void, Void>() {
			@SuppressLint("NewApi")
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
						(int) (largeBitmap.getWidth() * 0.6),
						(int) (largeBitmap.getHeight() * 0.6), false);
				Bitmap smallBitmap = Bitmap.createScaledBitmap(largeBitmap,
						(int) (largeBitmap.getWidth() * 0.4),
						(int) (largeBitmap.getHeight() * 0.4), false);
				ContentResolver cr = getContentResolver();
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
				showProgressDialog(false);
			}
		};
		saveImagesTask.execute();
	}

	public static Bitmap loadBitmapFromView(View v) {
		Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(),
				v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		v.draw(c);
		return b;
	}

	public void clickAddPic(View button) {
		Intent intent = new Intent(this, MultipleImagePickerActivity.class);
		startActivityForResult(intent, SELECT_PHOTO);
	}

	public void clickAddGlasses(View button) {
		/*
		 * showProgressDialog(true); new Thread(new Runnable() {
		 * 
		 * @Override public void run() {
		 */
		GlassDetector detector = new GlassDetector(MainActivity.this,
				(ViewGroup) aq.find(R.id.frame_texts).getView());
		detector.detectFaces((ViewGroup) aq.find(R.id.frame_images).getView());
		/*
		 * showProgressDialog(false); } }).start();
		 */
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case SELECT_PHOTO:
				String[] paths = intent
						.getStringArrayExtra(MultipleImagePickerActivity.EXTRA_IMAGE_PICKER_IMAGE_PATH);
				if (paths.length > 0) {
					clearImages();
					int gapX = imageViews.getWidth()
							/ Constants.SUPPORTED_FRAME_WIDTH;
					int gapY = imageViews.getHeight()
							/ Constants.SUPPORTED_FRAME_HEIGHT;
					int x, y;
					ImageView iv;

					for (int i = 0; i < paths.length; i++) {
						x = (i % Constants.SUPPORTED_FRAME_WIDTH) * gapX;
						y = (i / Constants.SUPPORTED_FRAME_WIDTH) * gapY;

						/**
						 * TODO replace the customized ImageView to support
						 * pinch zoom, and replace image by double tap
						 */

						iv = new ImageView(this);
						try {
							iv.setImageBitmap(checkPhotoRotation(paths[i]));
							RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
									gapX, gapY);
							params.setMargins(x, y, 0, 0);
							iv.setLayoutParams(params);
							imageViews.addView(iv);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					imageViews.invalidate();
				}
				break;
			case ADD_NEW_TEXT:
				String text = intent.getStringExtra(TextEditorActivity.EXTRA_EDITOR_TEXT);
				if(text==null||text.trim()==null)
					return;
				addTextView(
						text,
						intent.getIntExtra(
								TextEditorActivity.EXTRA_EDITOR_COLOR,
								Color.BLACK), intent.getBooleanExtra(
								TextEditorActivity.EXTRA_EDITOR_BORDER, false));
				break;
			case MODIFY_TEXT:
				if (currentSelectedText != null) {
					String txt = intent.getStringExtra(TextEditorActivity.EXTRA_EDITOR_TEXT);
					if(txt==null||txt.trim()==null)
						return;
					currentSelectedText
							.setText(
									txt,
									intent.getIntExtra(
											TextEditorActivity.EXTRA_EDITOR_COLOR,
											Color.BLACK),
									intent.getBooleanExtra(
											TextEditorActivity.EXTRA_EDITOR_BORDER,
											false));
					currentSelectedText = null;
				}
				break;
			}
		}
	}

	private Bitmap checkPhotoRotation(String path) throws IOException {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;

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
			bitmap = bitmap.copy(Bitmap.Config.RGB_565, true);

		}
		return bitmap;

	}

	private void clearImages() {
		imageViews.removeAllViews();
	}

	@SuppressLint("NewApi")
	private void addTextView(String text, int color, boolean hasStroke) {
		PradaText tv = PradaTextFactory.create(this, this);
		tv.setXY(textViews.getWidth() / 2, textViews.getHeight() / 2);
		tv.setText(text, color, hasStroke);
		textViews.addView(tv.getView());
	}

	public void clickAddText(View button) {
		Intent intent = new Intent(this, TextEditorActivity.class);
		startActivityForResult(intent, ADD_NEW_TEXT);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private PradaText currentSelectedText = null;

	@Override
	public void onModifyText(PradaText view, String text, int color, boolean hasStroke) {
		Intent intent = new Intent(this, TextEditorActivity.class);
		currentSelectedText = view;
		intent.putExtra(TextEditorActivity.EXTRA_EDITOR_TEXT, text);
		intent.putExtra(TextEditorActivity.EXTRA_EDITOR_COLOR, color);
		intent.putExtra(TextEditorActivity.EXTRA_EDITOR_BORDER, hasStroke);
		intent.putExtra(TextEditorActivity.EXTRA_EDITOR_TYPE,
				TextEditorActivity.TYPE_UPDATE);
		startActivityForResult(intent, MODIFY_TEXT);
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.dialog_exit_title)
				.setMessage(R.string.dialog_exit_message)
				.setPositiveButton(R.string.dialog_exit_ok,
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();
								finish();
							}

						})
				.setNegativeButton(R.string.dialog_exit_cancel,
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).show();
	}
}
