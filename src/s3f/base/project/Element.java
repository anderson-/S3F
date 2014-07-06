/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.base.project;

import java.io.InputStream;
import javax.swing.Icon;
import s3f.base.plugin.Configurable;
import s3f.base.plugin.Data;
import s3f.base.plugin.Plugabble;
import s3f.base.project.editormanager.EditorManager;

/**
 *
 * @author antunes2
 */
public interface Element extends Plugabble {

    public static class CategoryData implements Configurable {

        protected String name;
        protected String extension;
        protected Icon icon;
        protected Element staticInstance;
        private Data data;

        public CategoryData(String name, String extension, Icon icon, Element staticInstance) {
            this.name = name;
            this.extension = extension;
            this.icon = icon;
            this.staticInstance = staticInstance;
            data = new Data(name + "Category", "s3f.base.project.category", name + " category", this);
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

        @Override
        public Data getData() {
            return data;
        }
    }

    public String getName();

    public void setName(String name);

    public Icon getIcon();

    public CategoryData getCategoryData();

    public void save(FileCreator fileCreator);

    public Element load(InputStream stream);

    public EditorManager getEditorManager();

}
