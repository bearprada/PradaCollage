package lab.prada.collage;

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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import lab.prada.collage.util.CollageUtils;

import java.io.IOException;
import java.util.List;

import lab.prada.collage.component.BaseLabelView;
import lab.prada.collage.component.BaseLabelView.OnLabelListener;
import lab.prada.collage.component.ComponentFactory;
import lab.prada.collage.component.PhotoView;
import lab.prada.collage.component.PhotoView.OnPhotoListener;
import lab.prada.collage.util.CameraImageHelper;
import lab.prada.collage.util.GlassesDetector;
import lab.prada.collage.util.StoreImageHelper;
import lab.prada.collage.util.StoreImageHelper.onSaveListener;

public class MainActivity extends Activity implements OnLabelListener, OnPhotoListener {

	private static final int SELECT_PHOTO = 0;
	private static final int ADD_NEW_TEXT = 1;
	private static final int MODIFY_TEXT = 2;
	private static final int MODIFY_PHOTO = 3;

	private AQuery aq;
	private ProgressDialog progressDialog;
	private ViewGroup allViews;
	private ViewGroup textPanel;
	private ViewGroup photoPanel;

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
		textPanel = (ViewGroup) aq.find(R.id.frame_texts).getView();
		photoPanel = (ViewGroup) aq.find(R.id.frame_images).getView();
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
		StoreImageHelper.save(getContentResolver(), allViews,
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
		GlassesDetector detector = new GlassesDetector(MainActivity.this,
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

				if (paths.length <= 0) {
					return;
				}
				List<CollageUtils.ScrapTransform> trans = CollageUtils.generateScrapsTransform(photoPanel.getWidth(), photoPanel.getHeight(), paths.length);
				clearImages();
				int i = 0;
				for (CollageUtils.ScrapTransform t : trans) {
					PhotoView iv = ComponentFactory.create(ComponentFactory.COMPONENT_IMAGE, this);
					iv.setListener(this);

					try {
						// angle
						iv.setRotation(t.rotation);
						iv.setScaleX(t.scaleX);
						iv.setScaleY(t.scaleY);

						iv.setImageBitmap(CameraImageHelper
								.checkAndRotatePhoto(paths[i++]));
						iv.setXY(t.centerX, t.centerY);
						photoPanel.addView(iv);
					} catch (IOException e) {}
				}
				photoPanel.invalidate();
				break;
			case MODIFY_PHOTO:
				if (currentSelectedImage != null) {
					try {
						Bitmap bitmap = CameraImageHelper.checkAndRotatePhoto(getRealPathFromURI(intent.getData()));
						if(bitmap!=null){
							currentSelectedImage.setImage(bitmap);
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
		photoPanel.removeAllViews();
	}

	@SuppressLint("NewApi")
	private void addTextView(String text, int color, boolean hasStroke) {
		//BaseLabelView tv = ComponentFactory.createText(this, this);
		BaseLabelView tv = ComponentFactory.create(ComponentFactory.COMPONENT_LABEL, this);
		tv.setListener(this);
		tv.setXY(textPanel.getWidth() / 2, textPanel.getHeight() / 2);
		tv.setText(text, color, hasStroke);
		textPanel.addView(tv.getView());
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

	private BaseLabelView currentSelectedText = null;
	private PhotoView currentSelectedImage = null;

	@Override
	public void onModifyLabel(BaseLabelView view, String text, int color,
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
	public void onModifyPhoto(PhotoView view) {
		currentSelectedImage = view;
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, MODIFY_PHOTO);
	}
}
