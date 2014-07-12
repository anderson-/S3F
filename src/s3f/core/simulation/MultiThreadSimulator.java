/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.simulation;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import static s3f.core.simulation.Simulator.PAUSED;
import static s3f.core.simulation.Simulator.RUNNING;

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
                if (step && system != null) {
                    stepStatus = system.performStep();
                    step = false;
                } else {
                    //dormindo
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static final int STEP = 4;
    public static final String PROPERTY_STATE = "state";
    public static final String PROPERTY_STATUS = "status";
    public static final String PROPERTY_STATUS_RUNNING = "running";
    public static final String PROPERTY_STATUS_PAUSED = "paused";
    public static final String PROPERTY_STATUS_RESETED = "reseted";
    public static final String PROPERTY_STEP = "step";
    public static final String PROPERTY_STEP_BEGIN = "begin";
    public static final String PROPERTY_STEP_END = "end";

    protected int state;
    protected final ArrayList<System> systems;
    protected final PropertyChangeSupport support;
    private final ArrayList<StepDispatchThread> pool;

    public MultiThreadSimulator() {
        state = PAUSED;
        systems = new ArrayList<>();
        pool = new ArrayList<>();
        support = new PropertyChangeSupport(this);

        new Thread(this).start();
    }

    @Override
    public void reset() {
        setSystemState(Simulator.PAUSED);
        support.firePropertyChange(PROPERTY_STATUS, null, PROPERTY_STATUS_RESETED);
        for (System s : systems) {
            s.reset();
        }
    }

    @Override
    public void setSystemState(int state) {
        if (this.state == DONE && state != PAUSED) {
            reset();
        }
        support.firePropertyChange(PROPERTY_STATE, this.state, state);
        this.state = state;
    }

    @Override
    public int getSystemState() {
        return state;
    }
    
    @Override
    public boolean contains(System s){
        return systems.contains(s);
    }

    @Override
    public void add(System s) {//134691220
        if (!systems.contains(s)) {
            systems.add(s);
            for (StepDispatchThread t : pool) {
                if (t.getSystem() == null) {
                    t.setSystem(s);
                    return;
                }
            }
            pool.add(new StepDispatchThread(s));
        }
    }

    @Override
    public void remove(System s) {
        if (systems.contains(s)) {
            s.reset();
            for (StepDispatchThread t : pool) {
                if (t.getSystem() == s) {
                    t.reset();
                    t.setSystem(null);
                }
            }
            systems.remove(s);
        }
    }

    @Override
    public void clear() {
        for (System s : systems) {
            s.reset();
        }
        for (StepDispatchThread t : pool) {
            t.reset();
            t.setSystem(null);
        }
    }

    private synchronized void step() {
        support.firePropertyChange(PROPERTY_STEP, null, PROPERTY_STEP_BEGIN);
        boolean next = false;
        long time = java.lang.System.currentTimeMillis();
        int systemsState = DONE;
        for (System s : systems) {
            systemsState &= s.getSystemState();
        }
        if (systemsState == DONE) {
            state = DONE;
            support.firePropertyChange(PROPERTY_STATUS, PROPERTY_STATUS_RUNNING, PROPERTY_STATUS_PAUSED);
            support.firePropertyChange(PROPERTY_STATUS, null, PROPERTY_STATUS_RESETED);
            return;
        }

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
            if (s instanceof DummySystem) {
                DummySystem sistem = (DummySystem) s;
                sistem.printStatistics();
            }
        }
        performStep();

//        java.lang.System.out.println("step");
//        java.lang.System.out.println("k:" + k);
//        java.lang.System.out.println("TEMPO DO PASSO: " + (java.lang.System.currentTimeMillis() - time) + "ms.");
        support.firePropertyChange(PROPERTY_STEP, null, PROPERTY_STEP_END);
        if (state != RUNNING) {
            support.firePropertyChange(PROPERTY_STATUS, PROPERTY_STATUS_RUNNING, PROPERTY_STATUS_PAUSED);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                //for (int i = 0; i < 4; i++) {
                if (state == RUNNING) {
                    step();
                } else if (state == RESET) {
                    reset();
                    state = PAUSED;
                } else if (state == STEP) {
                    step();
                    state = PAUSED;
                } else {
                    //dormindo
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (ThreadDeath e) {
            java.lang.System.out.println("thread quebrada, favor arrumar ;D");
        }
    }

    @Override
    public void beginStep() {

    }

    @Override
    public boolean performStep() {
        return true;
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        support.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        support.removePropertyChangeListener(l);
    }

}
