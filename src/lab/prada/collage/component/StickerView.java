package lab.prada.collage.component;

import com.thuytrinh.multitouchlistener.MultiTouchListener;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

public class StickerView extends ImageView implements BaseComponent{
	StickerView(Context context) {
		super(context);
		setOnTouchListener(new MultiTouchListener());
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
}
