/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.project.editormanager;

import s3f.core.project.Element;

/**
 *
 * @author antunes
 */
public interface TextFile extends Element {

    public void setText(String text);

    public String getText();

}
