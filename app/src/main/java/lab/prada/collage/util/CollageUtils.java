package lab.prada.collage.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by prada on 15/9/3.
 */
public class CollageUtils {
    private static Random sRandom = new Random();
    public static List<ScrapTransform> generateScrapsTransform(int screenWidth, int screenHeight, int scrapNum) {
        List<ScrapTransform> trans = new ArrayList<>(scrapNum);
        for (int i = 0; i < scrapNum; i++) {
            trans.add(new ScrapTransform(sRandom.nextInt(screenWidth / 2), sRandom.nextInt(screenHeight / 2), 0, 1));
        }
        return trans;
    }

    public static class ScrapTransform {
        public final int centerX;
        public final int centerY;
        public final float rotation;
        public final float scale;
        public ScrapTransform(int x, int y, float r, float scale) {
            centerX = x;
            centerY = y;
            rotation = r;
            this.scale = scale;
        }
    }
}
