package lab.prada.collage.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import lab.prada.collage.R;

public class ComponentFactory {
	
	public final static int COMPONENT_IMAGE = 0;
	public final static int COMPONENT_STICKER = 1;
	public final static int COMPONENT_LABEL = 2;
	
	@SuppressWarnings("unchecked")
	public static <T extends BaseComponent> T create(int type, Context ctx, ViewGroup parent){
		switch(type){
		case COMPONENT_IMAGE:	return (T) LayoutInflater.from(ctx).inflate(R.layout.scrap_image, parent, false);
		case COMPONENT_STICKER: return (T) new StickerView(ctx);
		case COMPONENT_LABEL: 	return (T) new LabelViewImpl(ctx);
		default : return null;
		}
		
	}
/*
	public static BaseLabelView createText(Context ctx, OnTextListener listener){
		return new LabelViewImpl(ctx,listener);
	}
	
	public static PradaImage createImage(Context ctx, OnImageListener listener){
		return new PradaImage(ctx, listener);
	}
	
	public static StickerView createSticker(Context ctx){
		return new StickerView(ctx);
	}*/
}
