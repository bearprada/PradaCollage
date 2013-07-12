package com.example.pradacollage;

import java.util.Vector;

import android.graphics.Rect;

public class Constants {
	public static final int SUPPORTED_FRAME_NUMBER = 10;
	public static Frame findFrame(int numOfImages) throws IllegalArgumentException {
		if(numOfImages<=0||numOfImages>SUPPORTED_FRAME_NUMBER)
			throw new IllegalArgumentException();
		else{
			return frames.elementAt(numOfImages);
		}
	}
	
	public static Vector<Frame> frames = new Vector<Frame>(SUPPORTED_FRAME_NUMBER);
	static {
		Frame f1 = new Frame(1);
		//FIXME
		frames.add(f1);
	}
	public static class Frame{
		public Vector<Rect> layout;
		public Frame(int num){
			layout = new Vector<Rect>(num);
		}
	}
}
