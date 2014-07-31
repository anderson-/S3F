/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.project;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import s3f.core.plugin.Configurable;
import s3f.core.plugin.Data;
import s3f.core.plugin.Plugabble;

/**
 *
 * @author antunes2
 */
public interface Element extends Plugabble {

    public static class CategoryData implements Configurable {

        protected String name;
        protected String extension;
        protected Icon icon;
        protected ArrayList<Element> staticInstances;
        private Data data;

        public CategoryData(String name, String extension, Icon icon, Element staticInstance) {
            this(name, extension, icon);
            staticInstances.add(staticInstance);
        }

        public CategoryData(String name, String extension, Icon icon) {
            this.name = name;
            this.extension = extension;
            this.icon = icon;
            staticInstances = new ArrayList<>();
            data = new Data(name + "Category", "s3f.core.project.category", name + " category", this);
        }

        public void addModel(Element element) {
            staticInstances.add(element);
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
            return staticInstances.get(0);
        }

        public List<Element> getModels() {
            return staticInstances;
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

    public void setCurrentEditor(Editor editor);

}
