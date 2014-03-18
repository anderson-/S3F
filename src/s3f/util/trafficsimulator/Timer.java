/**
 * Timer.java
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

/**
 * Cronometro de uso geral.
 *
 *
 */
public class Timer {

    private long timeElapsed;
    private long tick;
    private long count;
    private long lastCount;
    private boolean paused = false;
    private boolean consumed = false;
    private boolean disposable = true;

    public Timer(long milis) {
        if (milis < 0) {
            throw new IllegalArgumentException("milis < 0");
        }
        tick = milis;
    }

    public Timer(long milis, boolean paused) {
        this(milis);
        this.paused = paused;
    }

    public long getTick() {
        return tick;
    }

    public void setTick(long tick) {
        this.tick = tick;
    }

    public synchronized void setDisposable(boolean disposable) {
        this.disposable = disposable;
    }

    public synchronized void reset() {
        consumed = false;
        paused = false;
        timeElapsed = 0;
        lastCount = 0;
        count = 0;
    }

    public void start() {
        paused = false;
    }

    public void pause(boolean state) {
        paused = state;
    }

    public boolean isPaused() {
        return paused;
    }

    public void consume() {
        consumed = true;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public synchronized boolean increase(long milis) {
        if (!paused) {
            timeElapsed += milis;
            lastCount = count;
            if (tick != 0) {
                count = timeElapsed / tick;
            } else {
                count++;
            }
            if (lastCount != count) {
                if (disposable) {
                    consumed = true;
                }
//                System.out.println(timeElapsed);
                run();
            }
        }
        return consumed;
    }

    public synchronized long getTimeElapsed() {
        return timeElapsed;
    }

    public synchronized long getCount() {
        return count;
    }

    public void run() {

    }
}
