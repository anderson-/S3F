/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 *
 * @author antunes
 */
public class ColorUtils {

    public static Color setAlpha(Color color, float a) {
        int rgb = color.getRGB();
        return RGBtoARGB(rgb, a);
    }

    public static Color HSBAtoARGB(float h, float s, float b, float a) {
        int rgb = Color.HSBtoRGB(h, s, b);
        return RGBtoARGB(rgb, a);
    }

    public static Color HSBAtoARGB(float... components) {
        int rgb = Color.HSBtoRGB(components[0], components[1], components[2]);
        if (components.length == 4) {
            return RGBtoARGB(rgb, components[3]);
        } else {
            return new Color(rgb);
        }
    }

    public static Color RGBtoARGB(int rgb, float a) {
        int alpha = (int) (a * 255) << 24;
        rgb &= 0xFFFFFF;
        return new Color(rgb | alpha, true);
    }

    public static float[] getHSBA(Color color) {
        float[] hsba = new float[4];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsba);
        hsba[3] = color.getAlpha() / 255f;
        return hsba;
    }

    public static Color changeHSBA(Color color, float h, float s, float b, float a) {
        float[] hsba = getHSBA(color);
        hsba[0] += h;
        hsba[0] %= 1f;
        hsba[1] += s;
        hsba[1] = (hsba[1] > 1f) ? 1f : (hsba[1] < 0f) ? 0 : hsba[1];
        hsba[2] += b;
        hsba[2] = (hsba[2] > 1f) ? 1f : (hsba[2] < 0f) ? 0 : hsba[2];
        hsba[3] += a;
        hsba[3] = (hsba[3] > 1f) ? 1f : (hsba[3] < 0f) ? 0 : hsba[3];
        return HSBAtoARGB(hsba);
    }

    public static BufferedImage imageHSBAchange(Image img, float h, float s, float b, float a) {
        BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = bi.getGraphics();
        graphics.drawImage(img, 0, 0, null);
        graphics.dispose();
        for (int x = 0; x < bi.getWidth(); x++) {
            for (int y = 0; y < bi.getHeight(); y++) {
                Color color = ColorUtils.changeHSBA(new Color(bi.getRGB(x, y), true), h, s, b, a);
                bi.setRGB(x, y, color.getRGB());
            }
        }
        return bi;
    }

}
