package com.example.pradacollage;

import java.util.Vector;

import android.graphics.Rect;

public class Constants {
	
	public static final int SUPPORTED_FRAME_WIDTH = 3;
	public static final int SUPPORTED_FRAME_HEIGHT = 4;
	public static final int SUPPORTED_FRAME_NUMBER = SUPPORTED_FRAME_WIDTH*SUPPORTED_FRAME_HEIGHT;
	
	/** it's not use now. */
	public static Frame findFrame(int numOfImages) throws IllegalArgumentException {
		if(numOfImages<=0||numOfImages>SUPPORTED_FRAME_NUMBER)
			throw new IllegalArgumentException();
		else{
			return frames.elementAt(numOfImages);
		}
	}
	/** it's not use now. */
	public static Vector<Frame> frames = new Vector<Frame>(SUPPORTED_FRAME_NUMBER);
	static {
		/**
		 * FIXME replace the Masonry style
		 * @reference https://github.com/expilu/AntipodalWall 
		 */
		Frame f1 = new Frame(1);
		frames.add(f1);
	}
	public static class Frame{
		public Vector<Rect> layout;
		public Frame(int num){
			layout = new Vector<Rect>(num);
		}
	}
	public final static float SMALL_SCALE_FACTOR = 0.4F;
	public final static float MEDIUM_SCALE_FACTOR = 0.6F;
	public final static int BITMAP_SAMPLE_SIZE= 4;
}
