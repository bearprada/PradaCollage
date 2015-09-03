package com.nostra13.universalimageloader.utils;


import java.util.ArrayList;
import java.util.List;

import lab.prada.collage.Constants;

/**
 * Created by prada on 15/9/3.
 */
public class CollageUtils {
    public static List<ScrapTransform> generateScrapsTransform(int screenWidth, int screenHeight, int scrapNum) {
        int gapX = screenWidth / Constants.SUPPORTED_FRAME_WIDTH;
        int gapY = screenHeight / Constants.SUPPORTED_FRAME_HEIGHT;
        int[] x_pos = {1, 0, 2};
        int[] y_pos = {2, 3, 1, 0};
        List<ScrapTransform> trans = new ArrayList<ScrapTransform>(scrapNum);

        for (int i = 0; i < scrapNum; i++) {
            int y = y_pos[i % 4] * gapY;
            int x = x_pos[i % Constants.SUPPORTED_FRAME_WIDTH] * gapX;
            float rotation = (i%5 <= 2) ? 30*(i%5) : 360 - 30*(i%5 - 2);
            //size
            float sx = 1 + (12 - (float)scrapNum)/12;
            float sy = 1 + (12 - (float)scrapNum)/12;
            trans.add(new ScrapTransform(x, y, rotation, sx, sy));

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
