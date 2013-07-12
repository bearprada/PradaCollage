package com.example.pradacollage.comp;

import com.thuytrinh.multitouchlistener.MultiTouchListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * TODO replace the TextView implementation into the View with canvas solution
 * */
public class PradaTextCanvas extends ImageView implements PradaText {

	private OnTextListener listener;
	private String text;
	private int color;

	private Paint paint;

	private Context ctx;

	public final static float DEFAULT_FONT_SIZE = 30.0f;

	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	private GestureDetector gestureDetector;

	PradaTextCanvas(Context context, OnTextListener listener) {
		super(context);
		this.ctx = context;

		this.listener = listener;
		paint = new Paint();
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTextSize(DEFAULT_FONT_SIZE);
		setOnTouchListener(new MultiTouchListener());
		gestureDetector = new GestureDetector(context, new GestureListener());
	}

	// TODO support stroke and other font type
	public void setText(String text, int color, boolean hasStroke) {
		this.text = text;
		this.color = color;
		paint.setColor(color);

		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		Bitmap bitmap = Bitmap.createBitmap((int) paint.measureText(text),
				bounds.height(), Bitmap.Config.ARGB_8888);
		// Bitmap bitmap = Bitmap.createBitmap(200,50, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bitmap);
		c.drawText(text, 0, 0, paint);
		// TODO write the text by canvas
		setImageDrawable(new BitmapDrawable(this.getContext().getResources(),
				bitmap));
	}

	@Override
	public View getView() {
		return this;
	}

	@Override
	public void setXY(int x, int y) {
		setX(x);
		setY(y);
	}

	private class GestureListener extends
			GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
		
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (listener != null)
				listener.onModifyText(PradaTextCanvas.this, text, color , false);
			return true;
		}
	}
}
