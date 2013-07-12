package com.example.pradacollage.comp;

import android.view.View;

public interface PradaText {
	public interface OnTextListener {
		public void onModifyText(PradaText view, String text, int color);
	}
	
	public void setText(String text , int color);
	public View getView();
	public void setXY(int x, int y);
}
