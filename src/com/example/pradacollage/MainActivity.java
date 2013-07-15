package com.example.pradacollage;

import java.io.IOException;
import com.androidquery.AQuery;
import com.example.pradacollage.comp.PradaImage;
import com.example.pradacollage.comp.PradaImage.OnImageListener;
import com.example.pradacollage.comp.PradaText;
import com.example.pradacollage.comp.PradaText.OnTextListener;
import com.example.pradacollage.comp.PradaTextFactory;
import com.example.pradacollage.util.CameraImageTranslator;
import com.example.pradacollage.util.GlassDetector;
import com.example.pradacollage.util.ImageStorageHelper;
import com.example.pradacollage.util.ImageStorageHelper.onSaveListener;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class MainActivity extends Activity implements OnTextListener,
		OnImageListener {

	private static final int SELECT_PHOTO = 0;
	private static final int ADD_NEW_TEXT = 1;
	private static final int MODIFY_TEXT = 2;
	private static final int MODIFY_PHOTO = 3;

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
		ImageStorageHelper.save(getContentResolver(), allViews,
				new onSaveListener() {
					@Override
					public void onSaveSuccess() {
						showProgressDialog(false);
					}

					@Override
					public void onSaveFail() {
						showProgressDialog(false);
					}
				});
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
		cleanSticks();
		GlassDetector detector = new GlassDetector(MainActivity.this,
				(ViewGroup) aq.find(R.id.frame_sticks).getView());
		detector.detectFaces((ViewGroup) aq.find(R.id.frame_images).getView());
		/*
		 * showProgressDialog(false); } }).start();
		 */
	}

	private void cleanSticks() {
		((ViewGroup) aq.find(R.id.frame_sticks).getView()).removeAllViews();
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
					// ImageView iv;
					PradaImage iv;

					for (int i = 0; i < paths.length; i++) {
						x = (i % Constants.SUPPORTED_FRAME_WIDTH) * gapX;
						y = (i / Constants.SUPPORTED_FRAME_WIDTH) * gapY;

						// iv = new ImageView(this);
						iv = new PradaImage(this, this);
						try {
							iv.setImageBitmap(CameraImageTranslator
									.checkPhotoRotation(paths[i]));
							RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
									gapX, gapY);
							params.setMargins(x, y, 0, 0);
							iv.setLayoutParams(params);
							imageViews.addView(iv);
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
					imageViews.invalidate();
				}
				break;
			case MODIFY_PHOTO:
				if (currentSelectedImage != null) {
					try {
						Bitmap bitmap = CameraImageTranslator.checkPhotoRotation(getRealPathFromURI(intent.getData()));
						if(bitmap!=null){
							currentSelectedImage.setImageBitmap(bitmap);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					currentSelectedImage = null;
				}
				break;
			case ADD_NEW_TEXT:
				String text = intent
						.getStringExtra(TextEditorActivity.EXTRA_EDITOR_TEXT);
				if (text == null || text.trim() == null)
					return;
				int color = intent.getIntExtra(
						TextEditorActivity.EXTRA_EDITOR_COLOR, Color.BLACK);
				boolean hasStroke = intent.getBooleanExtra(
						TextEditorActivity.EXTRA_EDITOR_BORDER, false);
				addTextView(text, color, hasStroke);
				break;
			case MODIFY_TEXT:
				if (currentSelectedText != null) {
					String txt = intent
							.getStringExtra(TextEditorActivity.EXTRA_EDITOR_TEXT);
					if (txt == null || txt.trim() == null)
						return;
					currentSelectedText.setText(txt,
							intent.getIntExtra(
									TextEditorActivity.EXTRA_EDITOR_COLOR,
									Color.BLACK), intent.getBooleanExtra(
									TextEditorActivity.EXTRA_EDITOR_BORDER,
									false));
					currentSelectedText = null;
				}
				break;
			}
		}
	}
	
	private String getRealPathFromURI(Uri contentURI) {
	    Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
	    if (cursor == null) { // Source is Dropbox or other similar local file path
	        return contentURI.getPath();
	    } else { 
	        cursor.moveToFirst(); 
	        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
	        return cursor.getString(idx); 
	    }
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
	private PradaImage currentSelectedImage = null;

	@Override
	public void onModifyText(PradaText view, String text, int color,
			boolean hasStroke) {
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

	@Override
	public void onModifyImage(PradaImage view) {
		currentSelectedImage = view;
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, MODIFY_PHOTO);
	}
}
