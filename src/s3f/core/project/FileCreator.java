/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author antunes2
 */
public class FileCreator {

    private static final String tmpdir = System.getProperty("java.io.tmpdir");

    private File file;

    private FileCreator() {

    }

    public static FileCreator getInstance() {
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

    public void makeTextFile(String name, String extension, StringBuilder sb) {
        FileWriter fw = null;
        try {
            if (!extension.startsWith(".")) {
                extension = "." + extension;
            }
            createFile(name, extension);
            fw = new FileWriter(file.getAbsoluteFile());
            fw.write(sb.toString());
            fw.close();
        } catch (IOException ex) {
            //TODO
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException ex) {
                //TODO
            }
        }
    }

    public static String convertInputStreamToString(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
