package lab.prada.collage.util;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by prada on 15/9/3.
 */
public class CollageUtils {
    public static List<ScrapTransform> generateScrapsTransform(int screenWidth, int screenHeight, int scrapNum) {
        List<ScrapTransform> trans = new ArrayList<ScrapTransform>(scrapNum);
        for (int i = 0; i < scrapNum; i++) {
            trans.add(new ScrapTransform(screenWidth / 2, screenHeight / 2, 0, 1, 1));
        }
        return trans;
    }
    public static class ScrapTransform {
        public final int centerX;
        public final int centerY;
        public final float rotation;
        public final float scaleX;
        public final float scaleY;
        public ScrapTransform(int x, int y, float r, float sx, float sy) {
            centerX = x;
            centerY = y;
            rotation = r;
            scaleX = sx;
            scaleY = sy;
        }
    }
}
