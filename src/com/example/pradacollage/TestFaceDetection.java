package com.example.pradacollage;

import com.example.pradacollage.comp.PradaText;
import com.example.pradacollage.comp.PradaText.OnTextListener;
import com.example.pradacollage.comp.PradaTextFactory;
import com.example.pradacollage.util.GlassDetector;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class TestFaceDetection extends Activity implements OnClickListener, OnTextListener {

	private ViewGroup vg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_face_detection);
		this.findViewById(R.id.btnDetection).setOnClickListener(this);
		this.findViewById(R.id.btnTestText).setOnClickListener(this);
		this.vg = (ViewGroup) findViewById(R.id.root);
	}

	@Override
	public void onClick(View v) {
		if(v.equals(findViewById(R.id.btnTestText))){
			addTextView("QQQQQ",Color.BLUE);
		}else{
			GlassDetector detector = new GlassDetector(this,vg);
			detector.detectFaces((ViewGroup)findViewById(R.id.images));
		}
	}
	
	private void addTextView(String text, int color) {
		PradaText tv = PradaTextFactory.create(this, this);
		tv.setXY(vg.getWidth() / 2, vg.getHeight() / 2);
		tv.setText(text, color, true);
		vg.addView(tv.getView());
	}

	@Override
	public void onModifyText(PradaText view, String text, int color, boolean hasStroke) {
	}

}
