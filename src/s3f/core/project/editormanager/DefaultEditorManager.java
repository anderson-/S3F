/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.project.editormanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import s3f.core.project.Editor;

/**
 *
 * @author antunes
 */
public class DefaultEditorManager implements EditorManager {

    private final ArrayList<Editor> editors = new ArrayList<>();
    private Editor defaultEditor;

    public DefaultEditorManager(Editor... editors) {
        this.editors.addAll(Arrays.asList(editors));
        if (editors.length > 0){
            defaultEditor = editors[0];
        }
    }

    @Override
    public Editor getDefaultEditor() {
        return defaultEditor;
    }

    @Override
    public void setDefaultEditor(Editor defaultEditor) {
        this.defaultEditor = defaultEditor;
    }

    @Override
    public List<Editor> getAvailableEditors() {
        return editors;
    }

}
