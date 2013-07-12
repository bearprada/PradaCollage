package com.example.pradacollage.comp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * TODO replace the TextView implementation into the View with canvas solution
 * */
public class PradaTextCanvas extends View implements PradaText {
	private GestureDetector gestureDetector;

	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;

	private int _xDelta;

	private int _yDelta;

	private OnTextListener listener;

	private String text;

	private int color;

	private Paint paint;

	private Context ctx;

	private int mAscent;

	public final static float DEFAULT_FONT_SIZE = 30.0f;

	private static final int MimWidth = 30;

	private static final int MaximunWidth = 300;
	
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	PradaTextCanvas(Context context, OnTextListener listener) {
		super(context);
		this.ctx = context;
		gestureDetector = new GestureDetector(context, new GestureListener());
		this.listener = listener;
		paint = new Paint();
		paint.setTextSize(DEFAULT_FONT_SIZE);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			result = (int) paint.measureText(text) + getPaddingLeft()
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

		mAscent = (int) paint.ascent();
		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			result = (int) (-mAscent + paint.descent()) + getPaddingTop()
					+ getPaddingBottom();
			Log.i("tag", "Height mTextPaint.descent(): " + paint.descent());
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	// TODO support stroke and other font type
	public void setText(String text, int color) {
		this.text = text;
		this.color = color;
		paint.setColor(color);

		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				bounds.width(), bounds.height());
		params.setMargins(bounds.left, bounds.top, bounds.right, bounds.bottom);
		setLayoutParams(params);
		requestLayout();
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// TODO calculate width and height
		//canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
		canvas.setMatrix(matrix);
		canvas.drawText(text, 0, 0, paint);
	}

	@SuppressLint("FloatMath")
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		RelativeLayout.LayoutParams mParams = (android.widget.RelativeLayout.LayoutParams) getLayoutParams();

		float scale = 1.0f;

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			start.set(event.getX(), event.getY());
			_xDelta = (int) event.getRawX() - mParams.leftMargin;
			_yDelta = (int) event.getRawY() - mParams.topMargin;
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			if (oldDist > 10f) {
				midPoint(mid, event);
				savedMatrix.set(matrix);
				mode = ZOOM;
				
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				mParams.leftMargin = (int) event.getRawX() - _xDelta;
				mParams.topMargin = (int) event.getRawY() - _yDelta;
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f) {
					scale = (newDist / oldDist);
					int scaledWidth = (int) (mParams.width*scale);
					int scaledHeight = (int) (mParams.height*scale);
					if(scaledWidth>MaximunWidth){
						scaledWidth = MaximunWidth;
						scaledHeight = MaximunWidth;
					}
					else if(scaledWidth<MimWidth){
						scaledWidth = MimWidth;
						scaledHeight = MimWidth;
					}
					mParams.width = scaledWidth;
					mParams.height = scaledHeight;
					matrix.set(savedMatrix);
	                matrix.postScale(scale, scale, mid.x, mid.y);
				}

			}
			break;
		}

		setLayoutParams(mParams);
		invalidate();
		return gestureDetector.onTouchEvent(event);
	}

	private class GestureListener extends
			GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@SuppressLint("NewApi")
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (listener != null)
				listener.onModifyText(PradaTextCanvas.this, text, color);
			return true;
		}
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
}
