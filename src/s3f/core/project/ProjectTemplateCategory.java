/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.project;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import s3f.core.plugin.Configurable;
import s3f.core.plugin.Data;

/**
 *
 * @author antunes2
 */
public class ProjectTemplateCategory implements Configurable {

    protected String name;
    protected Icon icon;
    protected ArrayList<Project> staticInstances;
    private Data data;

    public ProjectTemplateCategory(String name, Icon icon) {
        this.name = name;
        this.icon = icon;
        staticInstances = new ArrayList<>();
        data = new Data(name + "TemplateCategory", "s3f.core.project.template", name + " template category", this);
    }

    public String getName() {
        return name;
    }

    public Icon getIcon() {
        return icon;
    }

    public void addTemplate(Project project) {
        staticInstances.add(project);
    }

    public List<Project> getTemplates() {
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
