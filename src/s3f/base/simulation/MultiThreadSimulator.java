/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.base.simulation;

import java.util.ArrayList;
import static s3f.base.simulation.Simulator.PAUSED;
import static s3f.base.simulation.Simulator.RUNNING;

/**
 *
 * @author antunes
 */
public class MultiThreadSimulator implements Simulator {

    static final class StepDispatchThread extends Thread {

        private System system;
        private boolean step = false;
        private boolean stepStatus = false;

        public StepDispatchThread(System system) {
            setSystem(system);
        }

        public System getSystem() {
            return system;
        }

        public void setSystem(System system) {
            this.system = system;
        }

        public void reset() {
            step = false;
            stepStatus = false;
        }

        public void step() {
            step = true;
        }

        public boolean getStepStatus() {
            return stepStatus;
        }

        @Override
        public void run() {
            while (true) {
                if (step) {
                    step = false;
                    stepStatus = system.performStep();
                } else {
                    //dormindo
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private int state = PAUSED;
    private ArrayList<System> systems = new ArrayList<>();
    private ArrayList<StepDispatchThread> pool = new ArrayList<>();

    public MultiThreadSimulator() {

    }

    @Override
    public void setSystemState(int state) {
        this.state = state;
    }

    @Override
    public int getSystemState() {
        return state;
    }

    @Override
    public void add(System s) {//134691220
        systems.add(s);
        pool.add(new StepDispatchThread(s));
    }

    public void step() {
        boolean next = false;
        long time = java.lang.System.currentTimeMillis();
        beginStep();
        for (System s : systems) {
            s.beginStep();
        }
        for (StepDispatchThread t : pool) {
            t.reset();
        }
        int k = 0;
        while (!next) {
            next = true;
            for (StepDispatchThread t : pool) {
                if (!t.isAlive()) {
                    t.start();
                }
                t.step();
                next &= t.getStepStatus();
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            k++;
        }
        for (System s : systems) {
            if (s instanceof ISystem) {
                ISystem iSystem = (ISystem) s;
                iSystem.printStatistics();
            }
        }
        performStep();

        java.lang.System.out.println("k:" + k);
        java.lang.System.out.println("TEMPO DO PASSO: " + (java.lang.System.currentTimeMillis() - time) + "ms.");
    }

    @Override
    public void run() {
        while (true) {
            //for (int i = 0; i < 4; i++) {
            if (state == RUNNING) {
                step();
            } else {
                //dormindo
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void beginStep() {

    }

    @Override
    public boolean performStep() {
        return true;
    }

}
