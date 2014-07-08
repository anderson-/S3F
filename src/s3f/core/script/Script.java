/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.script;

import java.io.InputStream;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.text.JTextComponent;
import s3f.core.code.CodeEditorTab;
import s3f.core.plugin.Data;
import s3f.core.plugin.Plugabble;
import s3f.core.project.Editor;
import s3f.core.project.Element;
import s3f.core.project.FileCreator;
import s3f.core.project.editormanager.DefaultEditorManager;
import s3f.core.project.editormanager.EditorManager;
import s3f.core.project.editormanager.TextFile;

/**
 *
 * @author antunes
 */
public class Script implements Element, TextFile {

    public static final Element JS_SCRIPT = new Script();
    public static final Element.CategoryData JS_SCRIPTS = new Element.CategoryData("Scripts", "js", new ImageIcon(Script.class.getResource("/resources/icons/fugue/scripts-text.png")), JS_SCRIPT);
    private static final EditorManager EDITOR_MANAGER = new DefaultEditorManager(new CodeEditorTab());

    private String name = "asd" + Math.random();
    private String script;
    private Data data;

    ImageIcon a = new ImageIcon(Script.class.getResource("/resources/icons/fugue/script-text.png"));
    ImageIcon b = new ImageIcon(Script.class.getResource("/resources/icons/fugue/script-smiley.png"));

    public Script() {
        data = new Data("script", "s3f.base.project.element", "sei lá");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Icon getIcon() {
        if (Math.random() < 0.5) {
            return a;
        } else {
            return b;
        }
    }

    @Override
    public CategoryData getCategoryData() {
        return JS_SCRIPTS;
    }

    @Override
    public void save(FileCreator fileCreator) {
        StringBuilder sb = new StringBuilder();
        sb.append(getText());
        fileCreator.makeTextFile(name, JS_SCRIPTS.getExtension(), sb);
    }

    @Override
    public Element load(InputStream stream) {
        Script newScript = new Script();
        newScript.setText(FileCreator.convertInputStreamToString(stream));
        return newScript;
    }

    @Override
    public void init() {

    }

    @Override
    public Plugabble createInstance() {
        return new Script();
    }

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public EditorManager getEditorManager() {
        return EDITOR_MANAGER;
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
        
    }

}
