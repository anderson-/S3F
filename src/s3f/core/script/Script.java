/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.script;

import java.awt.event.ActionEvent;
import javax.script.Invocable;
import javax.script.ScriptException;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import s3f.core.code.CodeEditorTab;
import s3f.core.plugin.Plugabble;
import s3f.core.plugin.PluginManager;
import s3f.core.plugin.SimulableElement;
import s3f.core.project.Editor;
import s3f.core.project.Element;
import s3f.core.project.SimpleElement;
import s3f.core.project.editormanager.TextFile;
import s3f.core.simulation.System;
import s3f.core.ui.GUIBuilder;
import s3f.core.ui.MainUI;

/**
 *
 * @author antunes
 */
public class Script extends SimpleElement implements TextFile {

    public static final Element.CategoryData JS_SCRIPTS = new Element.CategoryData("Scripts", "js", new ImageIcon(Script.class.getResource("/resources/icons/fugue/scripts-text.png")), new Script());

    private String script;

    public Script() {
        super("Empty Script", "/resources/icons/fugue/script-text.png", JS_SCRIPTS, new Class[]{CodeEditorTab.class});
    }

    @Override
    public Plugabble createInstance() {
        return new Script();
    }

    @Override
    public void setText(String text) {
        this.script = text;
    }

    @Override
    public String getText() {
        return script;
    }

    @Override
    public void setCurrentEditor(Editor editor) {
        super.setCurrentEditor(editor);
        GUIBuilder gui = new GUIBuilder("Script Interpreter GUI Builder") {
            @Override
            public void init() {
                final AbstractAction runScript = new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        ScriptManager.runScript(Script.this, "js", null);
                    }
                };
                addMenuItem("Run>", "S", null, null, null, 4, null);
                addMenuItem("Run>Run script", "R", "F6", null, null, 0, runScript);

                addMenuItem("Run>Stop script", "S", "ESCAPE", null, null, 0, new AbstractAction() {

                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        new Thread("Process Killer") {
                            @Override
                            public void run() {
                                ScriptManager.kill();
                            }
                        }.start();
                    }
                });
            }

        };

        //force GUI rebuild
        PluginManager pm = PluginManager.getInstance();
        pm.registerFactory(gui);
        pm.createFactoryManager(MainUI.getInstance());
    }

}
