package com.example.pradacollage.comp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class PradaImage extends ImageView {

	private GestureDetector gestureDetector;

	// these matrices will be used to move and zoom image
	//private Matrix matrix = new Matrix();
	//private Matrix savedMatrix = new Matrix();
	// we can be in one of these 3 states
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;

	private static final int MIN_WIDTH = 30;
	private int mode = NONE;
	// remember some things for zooming
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDist = 1f;
	private float d = 0f;
	private float newRot = 0f;

	public PradaImage(Context context) {
		super(context);
		gestureDetector = new GestureDetector(context, new GestureListener());
		
		//setScaleType(ImageView.ScaleType.MATRIX);
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

	/**
	 * Calculate the degree to be rotated by.
	 * 
	 * @param event
	 * @return Degrees
	 */
	private float rotation(MotionEvent event) {
		double delta_x = (event.getX(0) - event.getX(1));
		double delta_y = (event.getY(0) - event.getY(1));
		double radians = Math.atan2(delta_y, delta_x);
		return (float) Math.toDegrees(radians);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) getLayoutParams();

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			start.set(event.getRawX()-params.leftMargin, event.getRawY()- params.topMargin);
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			if (oldDist > 10f) {
				midPoint(mid, event);
				mode = ZOOM;
			}
			d = rotation(event);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				params.leftMargin = (int)( event.getRawX() - start.x);
				params.topMargin = (int)( event.getRawY() - start.y);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f) {
					float scale = (newDist / oldDist);
					int sx = (int) (params.width*scale);
					int sy = (int) (params.height*scale);
					//TODO boundary check ... maybe we should not add this.
					if(sx<MIN_WIDTH){
						sx = MIN_WIDTH;
						sy = MIN_WIDTH;
					}
					params.width = sx;
					params.height = sy;
					
				}
				if (event.getPointerCount() == 3) {
					newRot = rotation(event);
					float r = newRot - d;
					if (r > 10f)
						setRotation(r);
				}
			}
			break;
		}
		setLayoutParams(params);
		return gestureDetector.onTouchEvent(event);
	}

	private class GestureListener extends
			GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			// if (listener != null)
			// listener.onModifyText(PradaTextView.this, getText().toString(),
			// getCurrentTextColor());
			return true;
		}
	}

}
