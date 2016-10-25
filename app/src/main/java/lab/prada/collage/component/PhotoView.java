package lab.prada.collage.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.thuytrinh.multitouchlistener.MultiTouchListener;

public class PhotoView extends ImageView implements BaseComponent {

	public interface OnPhotoListener {
		void onModifyPhoto(PhotoView view);
	}

	private OnPhotoListener listener;


	public PhotoView(Context context) {
		super(context);
		setOnTouchListener(new MultiTouchListener());
	}

	public PhotoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnTouchListener(new MultiTouchListener());
	}

	public PhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setOnTouchListener(new MultiTouchListener());
	}

	public void setImage(Bitmap bitmap){
		setImageBitmap(bitmap);
	}
	
	public void setListener(OnPhotoListener listener){
		this.listener = listener;
		this.setOnTouchListener(new MultiTouchListener(
				new GestureListener()));
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
				listener.onModifyPhoto(PhotoView.this);
			return true;
		}
	}

	@Override
	public View getView() {
		return this;
	}

	@Override
	public void setXY(int x, int y) {
		ViewCompat.setX(this, x);
		ViewCompat.setY(this, y);
	}
}
