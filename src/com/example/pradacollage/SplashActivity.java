package com.example.pradacollage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

public class SplashActivity extends Activity implements AnimationListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash);
		animation.setAnimationListener(this);
		findViewById(R.id.imgSplash).startAnimation(animation);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
		SplashActivity.this.startActivity(intent);
		SplashActivity.this.finish();
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
		//do nothing
	}

	@Override
	public void onAnimationStart(Animation arg0) {
		//do nothing
	}
	
	@Override
	public void onBackPressed() {
		//do nothing
	}
}
