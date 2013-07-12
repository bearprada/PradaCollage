package com.example.pradacollage.comp;

import com.thuytrinh.multitouchlistener.MultiTouchListener;

import android.content.Context;
import android.widget.ImageView;

public class PradaImage2 extends ImageView {

	public PradaImage2(Context context) {
		super(context);
		this.setOnTouchListener(new MultiTouchListener());
	}
}
