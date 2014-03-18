/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.base.project.OLDproject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author antunes2
 */
public class FileCreator {

    private static final String tmpdir = System.getProperty("java.io.tmpdir");

    private File file;
    
    private FileCreator(){
        
    }
    
    public static FileCreator getInstance(){
        return new FileCreator();
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File createFile(String name, String extension) {
        file = new File(tmpdir, name + extension);
        return file;
    }

    public void makeTextFile(String name, String extension, StringBuilder sb) throws IOException {
        createFile(name, extension);
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        fw.write(sb.toString());
        fw.close();
    }

}
