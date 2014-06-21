/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.base.project.OLDproject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import s3f.base.project.OLDproject.Element.CategoryData;
import s3f.base.ui.tab.Tab;
//import robotinterface.algorithm.parser.Parser;

/**
 *
 * @author antunes2
 */
public class Project {

    public static final String FILE_EXTENSION = "proj";
    private final ArrayList<Element> elements = new ArrayList<>();
    private final ArrayList<CategoryData> categories = new ArrayList<>();
    private final String name;
//    private final ArrayList<Program> programs = new ArrayList<>();
    //private final Interpreter interpreter;

    public Project(String name) {
        this.name = name;
        //this.interpreter = new Interpreter();
    }

    public String getName() {
        return name;
    }

    public void addElement(Element element) {
        elements.add(element);
        CategoryData cd = element.getCategoryData();
        if (!categories.contains(cd)) {
            categories.add(cd);
        }
    }

    public Collection<Element> getElements(String category) {
        ArrayList<Element> newElementList = new ArrayList<>();
        for (Element element : elements) {
            if (element.getCategoryData().getName().equals(category)) {
                newElementList.add(element);
            }
        }
        return newElementList;
    }

    public Collection<CategoryData> getElementsCategories() {
        return categories;
    }

    public Collection<Tab> getDefaultViews() {
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean save(String path) {
        boolean result = false;
        ZipOutputStream zip;
        FileOutputStream fileWriter;

        try {
//            System.out.println("Program Start zipping");

            /*
             * create the output stream to zip file result
             */
            fileWriter = new FileOutputStream(path);
            zip = new ZipOutputStream(fileWriter);
            /*
             * add the folder to the zip
             */

            File file;
            FileCreator fileCreator = FileCreator.getInstance();
            for (CategoryData category : categories) {
                addFolderToZip("", category.getName(), zip);

                for (Element element : getElements(category.getName())) {
                    try {
                        fileCreator.setFile(null);
                        element.save(fileCreator);
                        file = fileCreator.getFile();
                        if (file != null) {
                            addFileToZip(category.getName(), file, zip, false);
                        }
                        file.delete();

                    } catch (Exception e) {
                        //do stuff with exception
                        e.printStackTrace();
                    }
                }

            }

            /*
             * close the zip objects
             */
            zip.flush();
            zip.close();

            result = true;
//            System.out.println("Given files are successfully zipped");
        } catch (Exception e) {
            System.out.println("Some Errors happned during the zip process");
            e.printStackTrace();
        }

        return result;
    }

    /*
     * recursively add files to the zip files
     */
    private void addFileToZip(String path, File file, ZipOutputStream zip, boolean flag) throws Exception {

        /*
         * if the folder is empty add empty folder to the Zip file
         */
        if (flag == true) {
            zip.putNextEntry(new ZipEntry(path + "/" + file.getName() + "/"));
        } else { /*
             * if the current name is directory, recursively traverse it
             * to get the files
             */

            if (file.isDirectory()) {
                /*
                 * if folder is not empty
                 */
                addFolderToZip(path, file.getPath(), zip);
            } else {
                /*
                 * write the file to the output
                 */
                byte[] buf = new byte[1024];
                int len;
                FileInputStream in = new FileInputStream(file.getPath());
                zip.putNextEntry(new ZipEntry(path + "/" + file.getName()));
                while ((len = in.read(buf)) > 0) {
                    /*
                     * Write the Result
                     */
                    zip.write(buf, 0, len);
                }

                file.delete();
//                if (file.delete()) {
////                    System.out.println(file.getName() + " is deleted!");
//                } else {
//                    System.out.println("Delete operation is failed: " + file);
//                }
            }
        }
    }

    /*
     * add folder to the zip file
     */
    private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception {
        File folder = new File(srcFolder);

        /*
         * check the empty folder
         */
        if (folder.list() == null || folder.list().length == 0) {
//            System.out.println(folder.getName());
            addFileToZip(path, new File(srcFolder), zip, true);
        } else {
            /*
             * list the files in the folder
             */
            for (String fileName : folder.list()) {
                if (path.equals("")) {
                    addFileToZip(folder.getName(), new File(srcFolder + "/" + fileName), zip, false);
                } else {
                    addFileToZip(path + "/" + folder.getName(), new File(srcFolder + "/" + fileName), zip, false);
                }
            }
        }
    }

    public void importFile(String path) {
//        importZip(path, functions);
    }

    private void importZip(String path) {
        try {
            ZipFile zipFile = new ZipFile(path);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                InputStream stream = zipFile.getInputStream(entry);

                for (CategoryData category : categories) {
                    if (entry.getName().startsWith(category.getName() + "/")
                            && entry.getName().endsWith(category.getExtension())) {
                        Element element = category.getStaticInstance().load(stream);
                        if (element != null){
                            addElement(element);
                        }
                        break;
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
//    public static robotinterface.project.Project load(String path) {
//        robotinterface.project.Project p = new robotinterface.project.Project();
//        importZip(path, p.functions);
//
//        return p;
//    }
}
