/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.script;

import javax.swing.ImageIcon;
import s3f.core.code.CodeEditorTab;
import s3f.core.plugin.Plugabble;
import s3f.core.plugin.SimulableElement;
import s3f.core.project.Element;
import s3f.core.project.SimpleElement;
import s3f.core.project.editormanager.TextFile;
import s3f.core.simulation.System;

/**
 *
 * @author antunes
 */
public class Script extends SimpleElement implements TextFile, SimulableElement {

    public static final Element.CategoryData JS_SCRIPTS = new Element.CategoryData("Scripts", "js", new ImageIcon(Script.class.getResource("/resources/icons/fugue/scripts-text.png")), new Script());
    
    private String script;
    private final JSInterpreter interpreter = new JSInterpreter(this);

    public Script() {
        super("scry", "/resources/icons/fugue/script-text.png", JS_SCRIPTS, new Class[]{CodeEditorTab.class});
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
    public System getSystem() {
        return interpreter;
    }
}
