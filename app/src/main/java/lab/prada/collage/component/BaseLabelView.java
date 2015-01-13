package lab.prada.collage.component;


import com.thuytrinh.multitouchlistener.MultiTouchListener;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseLabelView extends View implements BaseComponent {

	protected OnLabelListener listener;
	
	public final static int DEFAULT_FONT_SIZE = 100;
	protected String mText;
	protected int mAscent;
	protected boolean hasStroke = false;

	public BaseLabelView(Context context) {
		super(context);
	}

	public interface OnLabelListener {
		public void onModifyLabel(BaseLabelView view, String text, int color,
				boolean hasStroke);
	}

	public abstract void setText(String text, int color, boolean hasStroke);
	public abstract int getTextColor();

	public void setListener(OnLabelListener listener) {
		this.listener = listener;
		this.setOnTouchListener(new MultiTouchListener(new GestureListener()));
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
				listener.onModifyLabel(BaseLabelView.this, mText, getTextColor(), hasStroke);
			return true;
		}
	}
}
