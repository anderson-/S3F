/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.project.editormanager;

import java.io.InputStream;
import javax.swing.ImageIcon;
import s3f.core.code.CodeEditorTab;
import s3f.core.plugin.Plugabble;
import s3f.core.project.Element;
import s3f.core.project.FileCreator;
import s3f.core.project.SimpleElement;

/**
 *
 * @author antunes
 */
public class PlainTextFile extends SimpleElement implements TextFile {

    public static final Element.CategoryData PLAIN_TEXT_FILES = new Element.CategoryData("Plain Text Files", "txt", new ImageIcon(PlainTextFile.class.getResource("/resources/icons/fugue/scripts-text.png")), new PlainTextFile());
    
    private String text;

    public PlainTextFile() {
        super("Empty Text File", "/resources/icons/fugue/document-text.png", PLAIN_TEXT_FILES, new Class[]{CodeEditorTab.class});
    }

    @Override
    public Plugabble createInstance() {
        return new PlainTextFile();
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
