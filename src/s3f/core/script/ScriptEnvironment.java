/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.script;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import s3f.core.plugin.Data;
import s3f.core.plugin.PluginManager;

/**
 *
 * @author antunes
 */
public class ScriptEnvironment {

    public static Map<String, Object> getVariables() {
        HashMap<String, Object> var = new HashMap<>();
        var.put("pluginManager", PluginManager.getInstance());

        return var;
    }

    public static Map<String[], Class> getFunctions() {
        HashMap<String[], Class> func = new HashMap<>();
        //vetor com os nomes das funções publicas e estaticas, classe
        func.put(new String[]{"printExample"}, ScriptEnvironment.class);
        func.put(new String[]{"tree"}, ScriptEnvironment.class);
        func.put(new String[]{"printHistory"}, ScriptEnvironment.class);
        return func;
    }

    public static Object tree(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        if (args.length == 0) {
            PluginManager.getInstance().printTree(System.out);
        } else if (args.length > 0) {
            Data data = PluginManager.getInstance().createFactoryManager(null).getData((String) args[0]);
            if (data != null) {//tree("s3f.core.project.tmp")
                if (args.length > 1) {
                    Object o = data.getProperty((String) args[1]);
                    return o;
                } else {
                    return data;
                }
            }
        }
        return null;
    }

    public static void printHistory(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        for (String s : ScriptManager.getExecutionHistory()) {
            System.out.println(s);
        }
    }

    public static void printExample(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        PrintStream out = System.out;
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                out.print(" ");
            }

            // Convert the arbitrary JavaScript value into a string form.
            String s = Context.toString(args[i]);

            out.print(s);
        }
        out.println();
        //return Context.getUndefinedValue();
    }

}
