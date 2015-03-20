/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f;

import javax.swing.ImageIcon;
import s3f.core.code.CodeEditorTab;
import s3f.core.plugin.ConfigurableObject;
import s3f.core.plugin.PluginBuilder;
import s3f.core.project.EditableProperty;
import s3f.core.project.Project;
import s3f.core.project.ProjectTemplateCategory;
import s3f.core.project.editormanager.PlainTextFile;
import s3f.core.script.Script;
import s3f.core.simulation.MultiThreadSimulator;

/**
 *
 * @author antunes
 */
public class Builder extends PluginBuilder {

    public static final ProjectTemplateCategory S3F_TEMPLATES = new ProjectTemplateCategory("S3F", new ImageIcon(Builder.class.getResource("/resources/icons/fugue/scripts-text.png")));

    
    public Builder() {
        super("S3F");
    }

    @Override
    public void init() {
        
        pm.registerFactory(S3F_TEMPLATES);
        S3F_TEMPLATES.addTemplate(new Project("Empty Project"));
        
        //
        pm.registerFactory(Script.JS_SCRIPTS);
        pm.registerFactory(PlainTextFile.PLAIN_TEXT_FILES);
        
        EditableProperty.put(Script.JS_SCRIPTS.getData(), CodeEditorTab.class);
        EditableProperty.put(PlainTextFile.PLAIN_TEXT_FILES.getData(), CodeEditorTab.class);
        
        PlainTextFile plainTextFile = new PlainTextFile();
        plainTextFile.setName("hellow");
        plainTextFile.setText("Hello World");
        //plainTextFile.setIcon(new ImageIcon(Builder.class.getResource("/resources/icons/fugue/sushi.png")));
        PlainTextFile.PLAIN_TEXT_FILES.addModel(plainTextFile);
        
        ConfigurableObject o = new ConfigurableObject("s3f.core.interpreter");
        o.getData().setProperty("interpreter", new MultiThreadSimulator());
        pm.registerFactory(o);
        
    }

}
