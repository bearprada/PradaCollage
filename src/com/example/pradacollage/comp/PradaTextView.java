package com.example.pradacollage.comp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.text.TextPaint;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * TODO replace the TextView implementation into the View with canvas solution
 * */
public class PradaTextView extends TextView implements PradaText {
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

	PradaTextView(Context context, OnTextListener listener) {
		super(context);
		gestureDetector = new GestureDetector(context, new GestureListener());
		this.listener = listener;
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
            _xDelta = (int)event.getRawX() - mParams.leftMargin;
            _yDelta = (int)event.getRawY()- mParams.topMargin;
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			if (oldDist > 10f) {
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				mParams.leftMargin = (int)event.getRawX() - _xDelta;
				mParams.topMargin = (int)event.getRawY() - _yDelta;
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f) {
					scale = (newDist / oldDist) * getTextSize() ;
					if(scale>100)
						scale = 100;
					else if(scale<10)
						scale = 10;
					setTextSize(scale);
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
			if(listener!=null)
				listener.onModifyText(PradaTextView.this, getText().toString(),getCurrentTextColor());
			return true;
		}
	}

	@Override
	public void setText(String text, int color) {
		setText(text);
		setTextColor(color);
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
