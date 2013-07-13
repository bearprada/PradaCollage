package com.example.pradacollage.comp;

import com.thuytrinh.multitouchlistener.MultiTouchListener;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

public class PradaImage extends ImageView {

	
	public interface OnImageListener {
		public void onModifyImage(PradaImage view);
	}

	private OnImageListener listener;
	
	public PradaImage(Context context, OnImageListener listener) {
		super(context);
		this.listener = listener;
		if(listener!=null)
			this.setOnTouchListener(new MultiTouchListener(new GestureListener()));
		else
			this.setOnTouchListener(new MultiTouchListener());
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
				listener.onModifyImage(PradaImage.this);
			return true;
		}
	}
}
