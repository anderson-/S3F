/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.project;

import javax.swing.Icon;
import s3f.core.plugin.Data;
import s3f.core.project.editormanager.EditorManager;

/**
 *
 * @author antunes
 */
public abstract class SimpleElement implements Element {

    private String name;
    private final Data data;
    private Icon icon;
    private final CategoryData category;
    private final EditorManager editorManager;

    public SimpleElement(String name, Icon icon, Element.CategoryData category, EditorManager editorManager) {
        this.name = name;
        this.icon = icon;
        this.category = category;
        this.editorManager = editorManager;
        data = new Data(name, "s3f.base.project.element", "sei lá");
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
    public EditorManager getEditorManager() {
        return editorManager;
    }
}
