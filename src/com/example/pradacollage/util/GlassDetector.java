package com.example.pradacollage.util;

import java.util.Random;

import com.example.pradacollage.R;
import com.example.pradacollage.comp.PradaImage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class GlassDetector {
	private Context ctx;
	private ViewGroup root;

	public final int[] GLASSES_RES_LIST = new int[] { R.drawable.glasses01,
			R.drawable.glasses02, R.drawable.glasses03 };
	
	private Random random = new Random();

	public static final int MAX_FACES = 10;

	public GlassDetector(Context ctx, ViewGroup root) {
		this.root = root;
		this.ctx = ctx;
	}

	public void detectFaces(ViewGroup vg) {
		int len = vg.getChildCount();
		View view;
		for (int i = 0; i < len; i++) {
			view = vg.getChildAt(i);
			if (view instanceof ImageView) {
				detectFaces((ImageView) view);
			}
		}
	}

	private void detectFaces(ImageView iv) {
		Bitmap b = Bitmap.createBitmap(iv.getMeasuredWidth(),
				iv.getMeasuredHeight(), Bitmap.Config.RGB_565);
		Canvas c = new Canvas(b);
		iv.draw(c);
		detectFaces(b, iv);
	}

	private void detectFaces(Bitmap b, View view) {
		FaceDetector fd = new FaceDetector(view.getWidth(), view.getHeight(), MAX_FACES);
		FaceDetector.Face[] faces = new FaceDetector.Face[MAX_FACES];
		fd.findFaces(b, faces);
		for (FaceDetector.Face f : faces) {
			if (f != null) {
				PointF midPoint = new PointF();
				f.getMidPoint(midPoint);
				float eyesdist = f.eyesDistance();
				addGlasses((int) (midPoint.x - eyesdist / 2), (int) midPoint.y,
						(int) (midPoint.x + eyesdist / 2), (int) midPoint.y,
						view);
			}
		}
	}

	@SuppressLint("NewApi")
	private void addGlasses(int x1, int y1, int x2, int y2, View view) {
		PradaImage iv = new PradaImage(ctx,null);
		iv.setImageResource(GLASSES_RES_LIST[random.nextInt(GLASSES_RES_LIST.length)]);
		int w = Math.abs(x2 - x1);
		int h = w / 2;// FIXME
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
		params.leftMargin = x1 + view.getLeft();
		params.topMargin = y1 + view.getTop();
		iv.setLayoutParams(params);
		iv.setRotation(view.getRotation());
		iv.setTranslationX(view.getTranslationX());
		iv.setTranslationY(view.getTranslationY());
		iv.setScaleX(view.getScaleX());
		iv.setScaleY(view.getScaleY());
		root.addView(iv);
	}
}
