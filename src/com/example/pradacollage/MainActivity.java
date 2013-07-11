package com.example.pradacollage;

import java.io.FileNotFoundException;
import java.util.UUID;

import com.androidquery.AQuery;
import com.example.pradacollage.PradaTextView.OnTextListener;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends Activity implements OnTextListener {

	private static final int SELECT_PHOTO = 0;
	private static final int ADD_NEW_TEXT = 1;
	private static final int MODIFY_TEXT = 2;

	private AQuery aq;
	private ProgressDialog progressDialog;
	private ViewGroup picture;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		aq = new AQuery(this);
		aq.find(R.id.btnSave).clicked(this, "clickSave");
		aq.find(R.id.btnAddPic).clicked(this, "clickAddPic");
		aq.find(R.id.btnAddText).clicked(this, "clickAddText");
		picture = (ViewGroup) aq.find(R.id.frame).getView();
	}

	public void clickSave(View button) {
		progressDialog = ProgressDialog.show(this, "saving images",
				"please wait a moment", true);
		AsyncTask<Void, Void, Void> saveImagesTask = new AsyncTask<Void, Void, Void>() {
			@SuppressLint("NewApi")
			@Override
			protected Void doInBackground(Void... params) {
				Bitmap largeBitmap = Bitmap.createBitmap(picture.getWidth(),
						picture.getHeight(), Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(largeBitmap);
				for (int i = 0; i < picture.getChildCount(); i++) {
					View v = picture.getChildAt(i);
					canvas.drawBitmap(loadBitmapFromView(v), v.getX(), v.getY(),
							null);
				}
				Bitmap mediumBitmap = Bitmap.createScaledBitmap(largeBitmap,
						(int) (largeBitmap.getWidth() * 0.6),
						(int) (largeBitmap.getHeight() * 0.6), false);
				Bitmap smallBitmap = Bitmap.createScaledBitmap(largeBitmap,
						(int) (largeBitmap.getWidth() * 0.4),
						(int) (largeBitmap.getHeight() * 0.4), false);
				ContentResolver cr = getContentResolver();
				String fileNamePrefix = "prada_" + UUID.randomUUID().toString();
				Log.d("TEST", MediaStore.Images.Media.insertImage(cr, largeBitmap,
						fileNamePrefix + "_large", ""));
				largeBitmap.recycle();
				Log.d("TEST", MediaStore.Images.Media.insertImage(cr, mediumBitmap,
						fileNamePrefix + "_medium", ""));
				mediumBitmap.recycle();
				Log.d("TEST", MediaStore.Images.Media.insertImage(cr, smallBitmap,
						fileNamePrefix + "_small", ""));
				smallBitmap.recycle();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				progressDialog.dismiss();
			}
		};
		saveImagesTask.execute();
	}

	public static Bitmap loadBitmapFromView(View v) {
		Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		v.draw(c);
		return b;
	}

	public void clickAddPic(View button) {
		// TODO replace the list view for gallery by myself
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, SELECT_PHOTO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case SELECT_PHOTO:
				Uri selectedImage = intent.getData();
				try {
					aq.find(R.id.frame_bg).image(
							BitmapFactory.decodeStream(getContentResolver()
									.openInputStream(selectedImage)));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				break;
			case ADD_NEW_TEXT:
				addTextView(
						intent.getStringExtra(TextEditorActivity.EXTRA_EDITOR_TEXT),
						intent.getIntExtra(
								TextEditorActivity.EXTRA_EDITOR_COLOR,
								Color.BLACK));
				break;
			case MODIFY_TEXT:
				if(currentSelectedText!=null){
					currentSelectedText.setText(intent.getStringExtra(TextEditorActivity.EXTRA_EDITOR_TEXT));
					currentSelectedText.setTextColor(intent.getIntExtra(TextEditorActivity.EXTRA_EDITOR_COLOR,Color.BLACK));
					currentSelectedText = null;
				}
				break;
			}
		}
	}

	@SuppressLint("NewApi")
	private void addTextView(String text, int color) {
		PradaTextView tv = new PradaTextView(this,this);
		tv.setText(text);
		tv.setTextColor(color);
		tv.setX(picture.getWidth() / 2);
		tv.setY(picture.getHeight() / 2);
		tv.setTextSize(30);
		picture.addView(tv);
	}

	public void clickAddText(View button) {
		Intent intent = new Intent(this, TextEditorActivity.class);
		startActivityForResult(intent, ADD_NEW_TEXT);
		//addTextView("ด๚ธี",Color.BLUE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private PradaTextView currentSelectedText = null;

	@Override
	public void onModifyText(PradaTextView view, String text, int color) {
		Intent intent = new Intent(this, TextEditorActivity.class);
		currentSelectedText = view;
		intent.putExtra(TextEditorActivity.EXTRA_EDITOR_TEXT, text);
		intent.putExtra(TextEditorActivity.EXTRA_EDITOR_COLOR, color);
		intent.putExtra(TextEditorActivity.EXTRA_EDITOR_TYPE, TextEditorActivity.TYPE_UPDATE);
		startActivityForResult(intent, MODIFY_TEXT);
	}
}
