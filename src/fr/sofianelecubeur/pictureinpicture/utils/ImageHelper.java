package fr.sofianelecubeur.pictureinpicture.utils;

import java.awt.*;
import java.awt.image.*;
import java.util.Arrays;

/**
 * Created by Sofiane on 05/01/2018.
 *
 * @author Sofiane
 */
public class ImageHelper {

    private static final Color BLACK = new Color(0, 0, 0, 255);

    public static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {
        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        if (original_width > bound_width) {
            new_width = bound_width;
            new_height = (new_width * original_height) / original_width;
        }

        if (new_height > bound_height) {
            new_height = bound_height;
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }

    public static BufferedImage[] getImageArray(BufferedImage image, int width, int height, int totalImages) {
        BufferedImage[] array = new BufferedImage[totalImages];
        for (int i = 0; i < totalImages; i++) array[i] = image.getSubimage(i * width, 0, width, height);
        return array;
    }

    public static BufferedImage replace(BufferedImage image, Color to){
        return replace(image, BLACK, to);
    }

    public static BufferedImage replace(BufferedImage image, Color from, Color to){
        BufferedImageOp lookup = new LookupOp(new ColorMapper(from, to), null);
        return lookup.filter(image, null);
    }

    private static class ColorMapper extends LookupTable {

        private final int[] from;
        private final int[] to;

        public ColorMapper(Color from,
                           Color to) {
            super(0, 4);

            this.from = new int[] {
                    from.getRed(),
                    from.getGreen(),
                    from.getBlue(),
                    from.getAlpha(),
            };
            this.to = new int[] {
                    to.getRed(),
                    to.getGreen(),
                    to.getBlue(),
                    from.getAlpha(),
            };
        }

        @Override
        public int[] lookupPixel(int[] src, int[] dest) {
            if (dest == null) {
                dest = new int[src.length];
            }

            if(src[3] != 0) {
                int[] src2 = new int[3];
                System.arraycopy(src, 0, src2, 0, src2.length);
                int[] from2 = new int[3];
                System.arraycopy(from, 0, from2, 0, from2.length);

                to[3] = src[3];
                int[] newColor = (Arrays.equals(src2, from2) ? to : src);
                System.arraycopy(newColor, 0, dest, 0, newColor.length);
            } else {
                int[] newColor = (Arrays.equals(src, from) ? to : src);
                System.arraycopy(newColor, 0, dest, 0, newColor.length);
            }

            return dest;
        }
    }

}