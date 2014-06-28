/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.util;

import java.awt.Color;

/**
 *
 * @author antunes
 */
public class RandomColor {

    private static final float golden_ratio_conjugate = 0.618033988749895f;
    private static float h = (float) Math.random();

    public static Color generate() {
        h += golden_ratio_conjugate;
        h %= 1f;
        return Color.getHSBColor(h, 0.35f, 0.95f);
    }
    
    public static Color generate(float saturation, float brightness) {
        h += golden_ratio_conjugate;
        h %= 1f;
        return Color.getHSBColor(h, saturation, brightness);
    }
    
}
