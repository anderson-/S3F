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
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.mozilla.javascript.tools.shell.JSConsole;
import s3f.base.plugin.PluginManager;

public class ScriptManager {

    private static ScriptManager SCRIPT_MANAGER = null;

    public static ScriptManager getScriptManager() {
        if (SCRIPT_MANAGER == null) {
            SCRIPT_MANAGER = new ScriptManager();
        }
        return SCRIPT_MANAGER;
    }

    private static Invocable runScript(String script, String extension, Map<String, Object> variables) throws ScriptException {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine;

        if (extension == null) {
            engine = mgr.getEngineByExtension("js");
        } else {
            engine = mgr.getEngineByExtension(extension);
        }

        if (variables != null) {
            for (Map.Entry<String, Object> e : variables.entrySet()) {
                engine.put(e.getKey(), e.getValue());
            }
        }

        engine.eval(script);

        return (Invocable) engine;
    }

    private Thread createThread(final Invocable inv, final long interval) {
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

    private void buildComponents(final Invocable inv) {

    }

    private void createDrawingFrame(final Invocable inv) {
        JFrame window = new JFrame();
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                try {
                    inv.invokeFunction("paint", g);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        window.add(panel);
        window.getContentPane().setPreferredSize(new Dimension(600, 400));
        window.pack();
        window.setVisible(true);
    }

    public static void main2(String[] args) throws FileNotFoundException, NoSuchMethodException {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");

//        try {
            engine.put("name", "Anderson");
//            engine.eval("print('Hello ' + name + '!')");
//            String s = PluginManager.convertInputStreamToString(RhinoEngine.class.getResourceAsStream("test.js"));
//            engine.eval("for (var i = 0; i < 20; i++) { ");
//            engine.eval("print(i)");
//            engine.eval("}");
//            engine.eval("");

            System.out.println("reading");
//            SimpleScriptContext s = new SimpleScriptContext();
//            engine.eval(new InputStreamReader(System.in), s);
            System.out.println("done");

            final Invocable inv = (Invocable) engine;

//            JFrame j = new JFrame();
//            JPanel p = new JPanel() {
//
//                @Override
//                protected void paintComponent(Graphics g) {
//                    try {
//                        inv.invokeFunction("paint", g);
//                    } catch (ScriptException ex) {
//                        Logger.getLogger(ScriptManager.class.getName()).log(Level.SEVERE, null, ex);
//                    } catch (NoSuchMethodException ex) {
//                        Logger.getLogger(ScriptManager.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//
//            };
//            j.add(p);
//            j.getContentPane().setPreferredSize(new Dimension(600, 400));
//            j.pack();
//            j.setVisible(true);
//        } catch (ScriptException ex) {
//            ex.printStackTrace();
//        }
    }

    public static void asd(String s) throws FileNotFoundException, NoSuchMethodException {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");

        try {
            engine.put("name", "Anderson");
//            engine.eval("print('Hello ' + name + '!')");
//            String s = PluginManager.convertInputStreamToString(RhinoEngine.class.getResourceAsStream("test.js"));
            engine.eval(s);

            final Invocable inv = (Invocable) engine;

            JFrame j = new JFrame();
            JPanel p = new JPanel() {

                @Override
                protected void paintComponent(Graphics g) {
                    try {
                        inv.invokeFunction("paint", g);
                    } catch (ScriptException ex) {
                        Logger.getLogger(ScriptManager.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NoSuchMethodException ex) {
                        Logger.getLogger(ScriptManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            };
            j.add(p);
            j.getContentPane().setPreferredSize(new Dimension(600, 400));
            j.pack();
            j.setVisible(true);

        } catch (ScriptException ex) {
            ex.printStackTrace();
        }
    }

    public void quit() {
        java.lang.System.exit(0);
    }

    public void main() throws java.lang.Throwable {
        final javax.script.ScriptEngine scriptEngine
                = new javax.script.ScriptEngineManager().getEngineByName("JavaScript");
        scriptEngine.put("main", this);
        final java.lang.StringBuilder text = new java.lang.StringBuilder();
        scriptEngine.put("text", text);
        scriptEngine.eval("print('');"); // loads engine for faster reaction 
        java.lang.System.out.printf("*** Texteditor 2000, V1.0 ***%n%n  %d Bytes Free%n%n  Enter%n%ntext."
                + "append('example');%n%n  or other StringBuilder calls in JavaScript "
                + "syntax to edit,%n%n  or enter%n%nmain.quit();%n%n  to quit.%n%n",
                java.lang.Runtime.getRuntime().freeMemory());
        Scanner s = new Scanner(System.in);
        while (true) {
            try {
                java.lang.System.out.printf("%n> ", text);
                java.lang.System.out.println(scriptEngine.eval(s.nextLine()));
                java.lang.System.out.printf("%s%n", text);
            } catch (final java.lang.Throwable throwable) {
                java.lang.System.err.println(throwable);
            }
        }
    }

    public static void main(final java.lang.String[] args) {
        
    }

}
