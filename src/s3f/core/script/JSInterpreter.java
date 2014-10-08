/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.script;

import javax.script.Invocable;
import javax.script.ScriptException;
import static s3f.core.simulation.System.DONE;
import static s3f.core.simulation.System.PAUSED;

/**
 *
 * @author antunes
 */
public class JSInterpreter implements s3f.core.simulation.System {

    private int state = PAUSED;
    private boolean step = false;
    private Script script;

    public JSInterpreter(Script script) {
        this.script = script;
    }

    @Override
    public void reset() {

        state = PAUSED;
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
    public void beginStep() {
        step = true;
    }

    @Override
    public boolean performStep() {
        if (step) {
            step = false;
            step();
        }
        return true;
    }

    private void step() {
        try {
            Invocable runScript = ScriptManager.runScript(script.getText(), "js", null);

//            ScriptManager.createDrawingFrame(runScript, 10);
            ScriptManager.runFunction(runScript, "main");
        } catch (ScriptException ex) {
            ex.printStackTrace();
        }
        state = DONE;
    }

}
