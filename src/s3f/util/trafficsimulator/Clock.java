/**
 * Clock.java
 *
 * Copyright (C) 2012 Anderson de Oliveira Antunes <anderson.utf@gmail.com> ***
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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Classe responsavel por gerenciar o tempo da simulação e eventos.
 */
public class Clock extends Thread { 

    private int d;
    private int h;
    private int m;
    private int s;
    private int ms;
    private long lastMs;
    private long dt;
    private boolean paused = true;
    private double ratio;
    private long ems;
    private final ArrayList<Timer> timers;
    private final ArrayList<ClockListener> listeners;
    private int sleep;

    public Clock(int d, int h, int m, int s, int ms, double ratio) {
        this.d = d;
        this.h = h;
        this.m = m;
        this.s = s;
        this.ms = ms;
        this.ratio = ratio;
        ems = 0;
        timers = new ArrayList<>();
        listeners = new ArrayList<>();
        sleep = 100;
    }

    public Clock() {
        this(0, 0, 0, 0, 0, 1.0);
    }

    public void increase() {
        if (!paused) {
            dt = System.currentTimeMillis() - lastMs;
            lastMs = System.currentTimeMillis();
            ms += (long) (dt * ratio);
            ems += (long) (dt * ratio);

            synchronized (timers) {
                for (Iterator<Timer> it = timers.iterator(); it.hasNext();) {
                    if (it.next().increase(dt)) {
                        it.remove();
                    }
                }
            }

            if (ms >= 1000) {
                s += ms / 1000;
                ms = ms % 1000;
                if (s >= 60) {
                    m += s / 60;
                    s = s % 60;
                    if (m >= 60) {
                        h += m / 60;
                        m = m % 60;
                        if (h >= 24) {
                            d += h / 24;
                            h = h % 24;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            if (!paused) {
                synchronized (listeners) {
                    for (ClockListener cl : listeners) {
                        cl.clockIncrease(this);
                    }
                }
            }
            try {
                Thread.sleep(sleep);
            } catch (Exception ex) {
            }
        }
    }

    public void reset() {
        paused = false;
        lastMs = System.currentTimeMillis();
        d = h = m = s = ms = 0;
        ems = 0;
    }

    public void pause(boolean p) {
        paused = p;
        lastMs = System.currentTimeMillis();
    }

    public double getDt() {
        return (paused) ? 0 : dt * ratio / 1000.0;
    }

    public long getElapsedMilis() {
        return ems;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        if (paused) {
            dt = 0;
            lastMs = 0;
        } else {
            dt = 1;
            lastMs = System.currentTimeMillis();
        }
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public void addListener(ClockListener c) {
        synchronized (listeners) {
            listeners.add(c);
        }
    }

    public void removeListener(ClockListener c) {
        synchronized (listeners) {
            listeners.remove(c);
            //System.out.println("clock:" + listeners.size());
        }
    }

    public void addTimer(Timer t) {
        synchronized (timers) {
            if (!timers.contains(t)){
                timers.add(t);
            }
        }
    }

    public void removeTimer(Timer t) {
        synchronized (timers) {
            timers.remove(t);
        }
    }

    @Override
    public String toString() {
        String str = "Clock{" + "d=" + d + ", h=" + h + ", m=" + m + ", s=" + s + ", ms=" + ms + ", dt=" + dt + " frames: " + (int) (1000.0f / dt) + ", paused=" + paused + ", ratio=" + ratio + '}';
        return str;
    }

    public void setSleep(int i) {
        sleep = i;
    }

    public interface ClockListener {

        public void clockIncrease(Clock c);
    }
}
