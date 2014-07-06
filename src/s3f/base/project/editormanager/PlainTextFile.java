/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.base.project.editormanager;

import java.io.InputStream;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.text.JTextComponent;
import s3f.base.code.CodeEditorTab;
import s3f.base.plugin.Data;
import s3f.base.plugin.Plugabble;
import s3f.base.project.Element;
import s3f.base.project.FileCreator;

/**
 *
 * @author antunes
 */
public class PlainTextFile implements Element, TextFile {

    public static final Element PLAIN_TEXT_FILE = new PlainTextFile();
    public static final Element.CategoryData PLAIN_TEXT_FILES = new Element.CategoryData("Plain Text", "txt", new ImageIcon(PlainTextFile.class.getResource("/resources/icons/fugue/scripts-text.png")), PLAIN_TEXT_FILE);
    private static final EditorManager EDITOR_MANAGER = new DefaultEditorManager(new CodeEditorTab());

    private String name = "sss" + Math.random();
    private String text;
    private Data data;

    ImageIcon a = new ImageIcon(PlainTextFile.class.getResource("/resources/icons/fugue/document-text.png"));

    public PlainTextFile() {
        data = new Data("plaintext", "s3f.base.project.element", "sei lá");
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
        return a;
    }

    @Override
    public Element.CategoryData getCategoryData() {
        return PLAIN_TEXT_FILES;
    }

    @Override
    public void save(FileCreator fileCreator) {
        StringBuilder sb = new StringBuilder();
        sb.append(getText());
        fileCreator.makeTextFile(name, PLAIN_TEXT_FILES.getExtension(), sb);
    }

    @Override
    public Element load(InputStream stream) {
        PlainTextFile txtFile = new PlainTextFile();
        txtFile.setText(FileCreator.convertInputStreamToString(stream));
        return txtFile;
    }

    @Override
    public void init() {

    }

    @Override
    public Plugabble createInstance() {
        return new PlainTextFile();
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
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

}
