/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f;

import s3f.core.plugin.ConfigurableObject;
import s3f.core.plugin.PluginBuilder;
import s3f.core.project.editormanager.PlainTextFile;
import s3f.core.script.Script;
import s3f.core.simulation.MultiThreadSimulator;

/**
 *
 * @author antunes
 */
public class Builder extends PluginBuilder {

    public Builder() {
        super("S3F");
    }

    @Override
    public void init() {
        pm.registerFactory(Script.JS_SCRIPTS);
        pm.registerFactory(PlainTextFile.PLAIN_TEXT_FILES);
        
        PlainTextFile plainTextFile = new PlainTextFile();
        plainTextFile.setName("hellow");
        plainTextFile.setText("Hello World");
        PlainTextFile.PLAIN_TEXT_FILES.addModel(plainTextFile);
        
        ConfigurableObject o = new ConfigurableObject("s3f.core.interpreter");
        o.getData().setProperty("interpreter", new MultiThreadSimulator());
        pm.registerFactory(o);
    }

}
