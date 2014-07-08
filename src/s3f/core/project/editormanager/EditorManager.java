/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.project.editormanager;

import java.util.List;
import s3f.core.project.Editor;

/**
 *
 * @author antunes
 */
public interface EditorManager {

    public Editor getDefaultEditor();

    public void setDefaultEditor(Editor defaultEditor);

    public List<Editor> getAvailableEditors();
    
}
