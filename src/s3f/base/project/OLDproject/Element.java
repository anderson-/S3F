/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s3f.base.project.OLDproject;

import java.io.InputStream;
import javax.swing.Icon;

/**
 *
 * @author antunes2
 */
public interface Element {

    public static class CategoryData {
        private String name;
        private String extension;
        private Icon icon;
        private Element staticInstance;

        public CategoryData(String name, String extension, Icon icon, Element staticInstance) {
            this.name = name;
            this.extension = extension;
            this.icon = icon;
            this.staticInstance = staticInstance;
        }

        public String getName() {
            return name;
        }

        public String getExtension() {
            return extension;
        }

        public Icon getIcon() {
            return icon;
        }

        public Element getStaticInstance() {
            return staticInstance;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    public String getName();
    
    public Icon getIcon();
    
    public CategoryData getCategoryData();

    public void save(FileCreator fileCreator);
    
    public Element load (InputStream stream);
    
}
