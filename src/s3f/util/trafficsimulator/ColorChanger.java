/**
 * ColorChanger.java
 *
 * Copyright (C) 2012 Anderson de Oliveira Antunes <anderson.utf@gmail.com>
 ****
 *
 * This file is part of TrafficSimulator.
 *
 * TrafficSimulator is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * TrafficSimulator is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * TrafficSimulator. If not, see http://www.gnu.org/licenses/.
 */
package s3f.util.trafficsimulator;

import java.awt.Color;

/**
 *
 */
public class ColorChanger {

    private float goalR;
    private float goalG;
    private float goalB;
    private float minAlpha = 0.1f;
    private float maxAlpha = 0.7f;
    private float alpha = 0.0f;
    private float velocity = .6f;
    private long dt = 0;
    private long ldt = 0;
    private boolean incr = true;
    private boolean singleFade = false;

    public ColorChanger() {
        goalR = 1.0f;
        goalG = 1.0f;
        goalB = 1.0f;
    }

    public ColorChanger(Color goal) {
        goalR = goal.getRed() / 255f;
        goalG = goal.getGreen() / 255f;
        goalB = goal.getBlue() / 255f;
    }

    public ColorChanger(Color goal, float velocity) {
        this(goal);
        this.velocity = velocity;
    }

    public ColorChanger(Color goal, float velocity, float minAlpha, float maxAlpha) {
        this(goal, velocity);

        if (minAlpha < 0f || minAlpha > 1f) {
            throw new IllegalArgumentException("minAlpha != [0,1]");
        }

        if (maxAlpha < 0f || maxAlpha > 1f) {
            throw new IllegalArgumentException("maxAlpha != [0,1]");
        }

        if (maxAlpha < minAlpha) {
            throw new IllegalArgumentException("maxAlpha < minAlpha");
        }

        this.minAlpha = minAlpha;
        this.maxAlpha = maxAlpha;
    }

    public synchronized void setSingleFade(boolean sf) {
        singleFade = sf;
    }

    public synchronized void setAlpha(float alpha) {
        this.alpha = alpha;
        ldt = System.currentTimeMillis();
    }

    public synchronized void setColor(Color c) {
        goalR = c.getRed() / 255f;
        goalG = c.getGreen() / 255f;
        goalB = c.getBlue() / 255f;
    }

    public synchronized float getAlpha() {
        dt = System.currentTimeMillis() - ldt;
        ldt = System.currentTimeMillis();

        alpha += velocity * (dt / 1000.0f) * ((incr) ? 1 : -1);

        incr = (((alpha >= minAlpha) ^ !incr) && (alpha <= maxAlpha) || singleFade);

        alpha = (alpha >= 1.0f || alpha < minAlpha) ? minAlpha : alpha;

        return alpha;
    }

    public void setMinAlpha(float minAlpha) {
        this.minAlpha = minAlpha;
    }

    public void setMaxAlpha(float maxAlpha) {
        this.maxAlpha = maxAlpha;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public synchronized Color getColor() {
        return new Color(goalR, goalG, goalB, getAlpha());
    }

    public static Color getColorBetween(Color c1, Color c2, float prop) {
        float h1, h2, s1, s2, v1, v2;
        float r, g, b, max, min, h, s, v;
        Color c;

        c = c1;

        r = c.getRed() / 255f;
        g = c.getGreen() / 255f;
        b = c.getBlue() / 255f;

        max = (r > g) ? ((r > b) ? r : b) : ((g > b) ? g : b);
        min = (r < g) ? ((r < b) ? r : b) : ((g < b) ? g : b);

        if (max == r && g >= b) {
            h = 60 * (g - b) / (max - min);
        } else if (max == r && g < b) {
            h = 60 * (g - b) / (max - min) + 360 / 360f;
        } else if (max == g) {
            h = 60 * (b - r) / (max - min) + 120 / 360f;
        } else if (max == b) {
            h = 60 * (r - g) / (max - min) + 240 / 360f;
        } else {
            h = 0; // TODO: 
        }

        s = (max - min) / max;

        v = max;

        h1 = h;
        s1 = s;
        v1 = v;

        c = c2;

        r = c.getRed() / 255f;
        g = c.getGreen() / 255f;
        b = c.getBlue() / 255f;

        max = (r > g) ? ((r > b) ? r : b) : ((g > b) ? g : b);
        min = (r < g) ? ((r < b) ? r : b) : ((g < b) ? g : b);

        if (max == r && g >= b) {
            h = 60 * (g - b) / (max - min);
        } else if (max == r && g < b) {
            h = 60 * (g - b) / (max - min) + 360 / 360f;
        } else if (max == g) {
            h = 60 * (b - r) / (max - min) + 120 / 360f;
        } else if (max == b) {
            h = 60 * (r - g) / (max - min) + 240 / 360f;
        } else {
            h = 0; // TODO: 
        }

        s = (max - min) / max;

        v = max;

        h2 = h;
        s2 = s;
        v2 = v;

        if (prop > 1f) {
            prop = 1f;
        }

        if (h1 < h2) {
            h = (h1 + (h2 - h1) * prop);
        } else {
            h = (h1 - (h1 - h2) * prop);
        }

        if (s1 < s2) {
            s = (s1 + (s2 - s1) * prop);
        } else {
            s = (s2 + (s1 - s2) * prop);
        }

        if (v1 < v2) {
            v = (v1 + (v2 - v1) * prop);
        } else {
            v = (v2 + (v1 - v2) * prop);
        }

        return Color.getHSBColor(h, s, v);
    }
}
