package com.example.pradacollage.comp;

import com.thuytrinh.multitouchlistener.MultiTouchListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class LabelView extends View implements PradaText {
	private Paint mTextPaint, strokePaint;

	private String mText;
	private int mAscent;
	private boolean hasStroke = false;
	private OnTextListener listener;

	public LabelView(Context context, OnTextListener listener) {
		super(context);
		this.listener = listener;
		initLabelView();
		this.setOnTouchListener(new MultiTouchListener(new GestureListener()));
	}

	private void initLabelView() {
		//Typeface tf = Typeface.create("Helvetica",Typeface.BOLD);
		mTextPaint = new Paint();
		mTextPaint.setTextSize(30);
		//mTextPaint.setTypeface(tf);
		mTextPaint.setColor(0xFF000000);
		strokePaint = new Paint();
		strokePaint.setTextSize(30);
		strokePaint.setStyle(Style.STROKE);
		strokePaint.setColor(Color.BLACK);
		strokePaint.setStrokeWidth(2);
		//strokePaint.setTypeface(tf);
	}

	private void setText(String str) {
		mText = str;
	}

	/**
	 * Sets the text size for this label
	 * 
	 * @param size
	 *            Font size
	 */
	public void setTextSize(int size) {
		mTextPaint.setTextSize(size);
		requestLayout();
	}

	/**
	 * Sets the text color for this label.
	 * 
	 * @param color
	 *            ARGB value for the text
	 */
	public void setTextColor(int color) {
		mTextPaint.setColor(color);
	}

	/**
	 * @see android.view.View#measure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	/**
	 * Determines the width of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
					+ getPaddingRight();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	/**
	 * Determines the height of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The height of the view, honoring constraints from measureSpec
	 */
	private int measureHeight(int measureSpec) {

		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		mAscent = (int) mTextPaint.ascent();
		Log.i("tag", "Height Ascent: " + mAscent);
		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			result = (int) (-mAscent + mTextPaint.descent()) + getPaddingTop()
					+ getPaddingBottom();
			Log.i("tag", "Height mTextPaint.descent(): " + mTextPaint.descent());
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.TRANSPARENT);
		if (hasStroke) {
			canvas.drawText(mText, getPaddingLeft(), getPaddingTop() - mAscent,
					this.strokePaint);
		}
		Log.d("TEST","------- "+mTextPaint.getColor());
		canvas.drawText(mText, getPaddingLeft(), getPaddingTop() - mAscent,
				mTextPaint);
	}

	@Override
	public void setText(String text, int color, boolean hasStroke) {
		this.hasStroke = hasStroke;
		Log.d("TEST","---------- set color " + color);
		setTextColor(color);
		setText(text);

		requestLayout();
		invalidate();
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
				listener.onModifyText(LabelView.this, mText,
						mTextPaint.getColor(), hasStroke);
			return true;
		}
	}

}
