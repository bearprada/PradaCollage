package lab.prada.collage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatDelegate;
import android.widget.ImageView;

public class SplashActivity extends BaseActivity {

	static {
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		ImageView splashImage = (ImageView) findViewById(R.id.img_splash);
		ViewCompat.setAlpha(splashImage, 0f);
		ViewCompat.animate(splashImage)
			.setDuration(1500)
			.alpha(1f)
			.withEndAction(new Runnable() {
				@Override
				public void run() {
					startActivity(new Intent(SplashActivity.this, MainActivity.class));
					finish();
				}
			}).start();
	}
}
