package com.example.pradacollage.comp;

import com.example.pradacollage.comp.PradaText.OnTextListener;

import android.content.Context;

public class PradaTextFactory {
	public static PradaText create(Context ctx, OnTextListener listener){
		return new LabelView(ctx,listener);
	}
}
