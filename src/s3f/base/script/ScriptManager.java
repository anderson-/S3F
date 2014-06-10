/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.base.script;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ScriptManager {

    private static List<String> history = new ArrayList<>();
    private static ScriptEngineManager SEE = new ScriptEngineManager();

    private ScriptManager() {
    }

    public static List<String> getSuportedExtensions() {
        ArrayList<String> exts = new ArrayList<>();
        for (ScriptEngineFactory factory : SEE.getEngineFactories()) {
            exts.addAll(factory.getExtensions());
        }
        return exts;
    }

    public static List<String> getExecutionHistory() {
        return new ArrayList(history);
    }

    public static Invocable runScript(String script, String extension, Map<String, Object> env) throws ScriptException {
        history.add(Arrays.toString(new Throwable().getStackTrace()));

        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine;

        engine = mgr.getEngineByExtension(extension);

        if (env != null) {
            for (Map.Entry<String, Object> e : env.entrySet()) {
                engine.put(e.getKey(), e.getValue());
            }
        }

        engine.eval(script);

        return (Invocable) engine;
    }

    public static void performActions(Invocable inv) {

    }

    public static Thread createThread(final Invocable inv, final long interval) {
        return new Thread() {
            @Override
            public void run() {
                boolean b = true;
                while (b) {
                    try {
                        b = (Boolean) inv.invokeFunction("run");
                    } catch (ScriptException ex) {
                        ex.printStackTrace();
                        return;
                    } catch (NoSuchMethodException ex) {
                        return;
                    }
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        };
    }

    public static void buildComponents(final Invocable inv) {

    }

    public static void createDrawingFrame(final Invocable inv, final long interval) {
        JFrame window = new JFrame();
        JPanel panel = new JPanel() {
            boolean ok = true;

            {
                if (interval > 0) {
                    new Thread() {
                        @Override
                        public void run() {
                            while (ok) {
                                repaint();
                                try {
                                    Thread.sleep(interval);
                                } catch (InterruptedException ex) {
                                }
                            }
                        }
                    }.start();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                try {
                    inv.invokeFunction("paint", g);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    ok = false;
                }
            }
        };
        window.add(panel);
        window.getContentPane().setPreferredSize(new Dimension(600, 400));
        window.pack();
        window.setVisible(true);
    }

//    public void main() throws java.lang.Throwable {
//        final javax.script.ScriptEngine scriptEngine
//                = new javax.script.ScriptEngineManager().getEngineByName("JavaScript");
//        scriptEngine.put("main", this);
//        final java.lang.StringBuilder text = new java.lang.StringBuilder();
//        scriptEngine.put("text", text);
//        scriptEngine.eval("print('');"); // loads engine for faster reaction 
//        java.lang.System.out.printf("*** Texteditor 2000, V1.0 ***%n%n  %d Bytes Free%n%n  Enter%n%ntext."
//                + "append('example');%n%n  or other StringBuilder calls in JavaScript "
//                + "syntax to edit,%n%n  or enter%n%nmain.quit();%n%n  to quit.%n%n",
//                java.lang.Runtime.getRuntime().freeMemory());
//        Scanner s = new Scanner(System.in);
//        while (true) {
//            try {
//                java.lang.System.out.printf("%n> ", text);
//                java.lang.System.out.println(scriptEngine.eval(s.nextLine()));
//                java.lang.System.out.printf("%s%n", text);
//            } catch (final java.lang.Throwable throwable) {
//                java.lang.System.err.println(throwable);
//            }
//        }
//    }
}
