/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.simulation;

import java.util.Random;

/**
 *
 * @author antunes
 */
class DummySystem implements System {
    
    private String name;
    private int state = PAUSED;
    private long time;
    private long stepTime;
    private boolean printOnEnd;
    private long lastStepRunningTime;
    private long lastStepDoneRunningTime;
    private static final long defaultPerformStepDelay;
    private static Random generator;

    static {
        generator = new Random(2);//java.lang.System.currentTimeMillis());
        defaultPerformStepDelay = (long) (generator.nextDouble() * 5) + 1;
    }

    public DummySystem(String name) {
        this.name = name;
        stepTime = (long) (generator.nextDouble() * 1500);
    }

    public String getSystemName() {
        return name;
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
    public final void beginStep() {
        time = java.lang.System.currentTimeMillis();
        printOnEnd = true;
        lastStepRunningTime = 0;
        lastStepDoneRunningTime = 0;
    }

    /* retorna false para continuar e true para sair */
    @Override
    public boolean performStep() {
        try {
            Thread.sleep(defaultPerformStepDelay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //tempo do loop
        boolean exit = (java.lang.System.currentTimeMillis() - time > stepTime);
        if (exit) {
            if (printOnEnd) {
                java.lang.System.out.println(name + " : saindo");
                printOnEnd = false;
            }
            lastStepDoneRunningTime++; //conta a chamada dessa função quando terminado o passo desse sistema
        } else {
            lastStepRunningTime++; //conta a chamada dessa função quando *não* terminado o passo desse sistema
        }
        return exit;
    }

    public void printStatistics() {
        java.lang.System.out.println(name + "(" + stepTime + "/" + defaultPerformStepDelay + " ms) : ex " + lastStepRunningTime + " : waiting " + lastStepDoneRunningTime + " ; " + (stepTime / (float) lastStepRunningTime));
    }

    @Override
    public void reset() {
        
    }
}
