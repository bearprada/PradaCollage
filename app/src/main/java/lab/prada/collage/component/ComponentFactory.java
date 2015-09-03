package lab.prada.collage.component;

import android.content.Context;

public class ComponentFactory {
	
	public final static int COMPONENT_IMAGE = 0;
	public final static int COMPONENT_STICKER = 1;
	public final static int COMPONENT_LABEL = 2;
	
	@SuppressWarnings("unchecked")
	public static <T extends BaseComponent> T create(int type, Context ctx){
		switch(type){
		case COMPONENT_IMAGE:	return (T) new PhotoView(ctx);
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
