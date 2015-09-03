package lab.prada.collage.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.thuytrinh.multitouchlistener.MultiTouchListener;

public class PhotoView extends ImageView implements BaseComponent {

	public interface OnPhotoListener {
		public void onModifyPhoto(PhotoView view);
	}

	private OnPhotoListener listener;


	PhotoView(Context context) {
		super(context);
		this.setOnTouchListener(new MultiTouchListener());
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
		setX(x);
		setY(y);
	}

	public void set_pos(int x, int y) {
		x_val = x;
		y_val = y;
	}

	public int get_x_val() {
		return x_val;
	}

	public int get_y_val() {
		return y_val;
	}

	private int x_val;
	private int y_val;
}