package s3f.core.simulation;

///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package s3f.base.simulation;
//
//import java.util.*;
//import static s3f.base.simulation.Simulator.PAUSED;
//import static s3f.base.simulation.Simulator.RUNNING;
//
//public class InterpretadorTeste {
//
//    public static void main(String[] args) {
//        //Simulator sim = new ISimulatorMultiThread(); //perfeito
//        Simulator sim = new MultiThreadSimulator();
//        System[] s = new ISystem[10];
//        java.lang.System.out.println("INICIO");
//        for (int i = 0; i < 10; i++) {
//            s[i] = new ISystem("Sistema " + i);
//            sim.add(s[i]);
//        }
//        Thread t = new Thread(sim);
//        t.start();
//        sim.setSystemState(Simulator.RUNNING);
//        try {
//            t.join();
//        } catch (Exception ex) {
//        }
//        java.lang.System.out.println("FIM");
//        java.lang.System.exit(0);
//    }
//}
//
//class ISystem implements System {
//
//    public static final int PAUSED = 0;
//    public static final int RUNNING = 1;
//
//    private String name;
//    private int state = PAUSED;
//    private long time;
//    private long stepTime;
//    private boolean printOnEnd;
//    private long lastStepRunningTime;
//    private long lastStepDoneRunningTime;
//    private static final long defaultPerformStepDelay;
//    private static Random generator;
//
//    static {
//        generator = new Random(2);//java.lang.System.currentTimeMillis());
//        defaultPerformStepDelay = (long) (generator.nextDouble() * 5) + 1;
//    }
//
//    public ISystem(String name) {
//        this.name = name;
//        stepTime = (long) (generator.nextDouble() * 500);
//    }
//
//    public String getSystemName() {
//        return name;
//    }
//
//    @Override
//    public void setSystemState(int state) {
//        this.state = state;
//    }
//
//    @Override
//    public int getSystemState() {
//        return state;
//    }
//
//    @Override
//    public final void beginStep() {
//        time = java.lang.System.currentTimeMillis();
//        printOnEnd = true;
//        lastStepRunningTime = 0;
//        lastStepDoneRunningTime = 0;
//    }
//
//    /* retorna false para continuar e true para sair */
//    @Override
//    public boolean performStep() {
//        try {
//            Thread.sleep(defaultPerformStepDelay);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        //tempo do loop
//        boolean exit = (java.lang.System.currentTimeMillis() - time > stepTime);
//        if (exit) {
//            if (printOnEnd) {
//                java.lang.System.out.println(name + " : saindo");
//                printOnEnd = false;
//            }
//            lastStepDoneRunningTime++; //conta a chamada dessa função quando terminado o passo desse sistema
//        } else {
//            lastStepRunningTime++; //conta a chamada dessa função quando *não* terminado o passo desse sistema
//        }
//        return exit;
//    }
//
//    public void printStatistics() {
//        java.lang.System.out.println(name + "(" + stepTime + "/" + defaultPerformStepDelay + " ms) : ex " + lastStepRunningTime + " : waiting " + lastStepDoneRunningTime + " ; " + (stepTime/(float)lastStepRunningTime));
//    }
//}
//
///**
// * Todo sistema é executadoao menos uma vez por passo global, dependendo da ordem do sistema 
// * @author antunes
// */
//
//class ISimulatorSingleThread implements Simulator {
//
//    private int state = PAUSED;
//    private ArrayList<System> systems = new ArrayList<>();
//
//    public ISimulatorSingleThread() {
//
//    }
//
//    @Override
//    public void setSystemState(int state) {
//        this.state = state;
//    }
//
//    @Override
//    public int getSystemState() {
//        return state;
//    }
//
//    @Override
//    public void add(System s) {//134691220
//        systems.add(s);
//    }
//
//    @Override
//    public void run() {
//        boolean next;
//        long time;
//        //while (true){
//        for (int i = 0; i < 4; i++) {
//            next = false;
//            if (state == RUNNING) {
//                time = java.lang.System.currentTimeMillis();
//                for (System s : systems) {
//                    //s.beginStep();
//                }
//                boolean init = false;
//                while (!next) {
//                    next = true;
//                    for (System s : systems) {
//                        if (!init){
//                            s.beginStep();
//                        }
//                        next &= s.performStep();
//                    }
//                    init = true;
//                }
//                for (System s : systems) {
//                    if (s instanceof ISystem) {
//                        ISystem iSystem = (ISystem) s;
//                        iSystem.printStatistics();
//                    }
//                }
//                java.lang.System.out.println("TEMPO DO PASSO: " + (java.lang.System.currentTimeMillis() - time) + "ms.");
//            } else {
//                //dormindo
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    @Override
//    public void beginStep() {
//        
//    }
//
//    @Override
//    public boolean performStep() {
//        return true;
//    }
//}
//
//class ISimulatorMultiThread implements Simulator {
//
//    @Override
//    public void beginStep() {
//        
//    }
//
//    @Override
//    public boolean performStep() {
//        return true;
//    }
//
//    //dispatch thread
//    static class MyThread extends Thread {
//
//        private System system;
//        private boolean step = false;
//        private boolean stepStatus = false;
//
//        public MyThread(System system) {
//            this.system = system;
//        }
//
//        public void reset() {
//            step = false;
//            stepStatus = false;
//        }
//
//        public void step() {
//            step = true;
//        }
//
//        public boolean getStepStatus() {
//            return stepStatus;
//        }
//
//        @Override
//        public void run() {
//            while (true) {
//                if (step) {
//                    step = false;
//                    stepStatus = system.performStep();
//                } else {
//                    //dormindo
//                    try {
//                        Thread.sleep(1);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }
//
//    private int state = PAUSED;
//    private ArrayList<System> systems = new ArrayList<>();
//    private ArrayList<MyThread> pool = new ArrayList<>();
//
//    public ISimulatorMultiThread() {
//
//    }
//
//    @Override
//    public void setSystemState(int state) {
//        this.state = state;
//    }
//
//    @Override
//    public int getSystemState() {
//        return state;
//    }
//
//    @Override
//    public void add(System s) {//134691220
//        systems.add(s);
//        pool.add(new MyThread(s));
//    }
//
//    @Override
//    public void run() {
//        boolean next;
//        long time;
//        //while (true){
//        for (int i = 0; i < 4; i++) {
//            next = false;
//            if (state == RUNNING) {
//                time = java.lang.System.currentTimeMillis();
//                for (System s : systems) {
//                    s.beginStep();
//                }
//                for (MyThread t : pool) {
//                    t.reset();
//                }
//                int k = 0;
//                while (!next) {
//                    next = true;
//                    for (MyThread t : pool) {
//                        if (!t.isAlive()) {
//                            t.start();
//                        }
//                        t.step();
//                        next &= t.getStepStatus();
//                    }
//                    try {
//                        Thread.sleep(1);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    k++;
//                }
//                for (System s : systems) {
//                    if (s instanceof ISystem) {
//                        ISystem iSystem = (ISystem) s;
//                        iSystem.printStatistics();
//                    }
//                }
//                java.lang.System.out.println("k:" + k);
//                java.lang.System.out.println("TEMPO DO PASSO: " + (java.lang.System.currentTimeMillis() - time) + "ms.");
//            } else {
//                //dormindo
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//}
