/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.project;

import java.io.InputStream;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import s3f.core.code.CodeEditorTab;
import s3f.core.plugin.Data;
import s3f.core.plugin.Plugabble;
import s3f.core.project.editormanager.TextFile;

/**
 *
 * @author antunes
 */
public abstract class SimpleElement implements Element {

    private String name;
    private final Data data;
    private Icon icon;
    private final CategoryData category;
    private Editor editor;

    public SimpleElement(String name, String iconpath, Element.CategoryData category, Class<? extends Editor>[] editors) {
        this.name = name;
        this.icon = new ImageIcon(getClass().getResource(iconpath));
        this.category = category;
        data = new Data(name, "s3f.core.project.element", "sei lá");
        for (Class<? extends Editor> e : editors) {
            EditableProperty.put(data, e);
        }
        if (this instanceof TextFile) {
            EditableProperty.put(data, CodeEditorTab.class);
        }
    }

    @Override
    public void save(FileCreator fileCreator) {
        if (this instanceof TextFile) {
            TextFile textFile = (TextFile) this;
            StringBuilder sb = new StringBuilder();
            sb.append(textFile.getText());
            fileCreator.makeTextFile(getName(), category.getExtension(), sb);
        }
    }

    @Override
    public Element load(InputStream stream) {
        Plugabble instance = createInstance();
        if (instance instanceof TextFile) {
            TextFile textFile = (TextFile) instance;
            textFile.setText(FileCreator.convertInputStreamToString(stream));
            return textFile;
        }
        return null;
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
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    @Override
    public Element.CategoryData getCategoryData() {
        return category;
    }

    @Override
    public void init() {

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
    public void setCurrentEditor(Editor editor) {
        this.editor = editor;
    }

    public Editor getCurrentEditor() {
        return editor;
    }

}
