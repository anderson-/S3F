/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import s3f.core.plugin.Data;
import s3f.core.plugin.Plugabble;
import s3f.core.plugin.PluginManager;
import s3f.core.ui.tab.Tab;

/**
 *
 * @author antunes
 */
public abstract class GUIBuilder implements Plugabble {

    public final Data data;
    private ResourceBundle bundle;

    public static class Element<T> implements Comparable<Element> {

        private final int priority;
        private final T t;

        public Element(T t, int priority) {
            this.priority = priority;
            this.t = t;
        }

        @Override
        public int compareTo(Element x) {
            if (this.priority < x.priority) {
                return -1;
            }
            if (this.priority > x.priority) {
                return 1;
            }
            return 0;
        }

        public int getPriority() {
            return priority;
        }

        public T getT() {
            return t;
        }
    }

    private final ArrayList<Element<JMenu>> menus = new ArrayList<>();
    private final ArrayList<Element<Component>> toolbarComponents = new ArrayList<>();
    private final ArrayList<Element<Tab>> tabs = new ArrayList<>();

    public GUIBuilder(String pluginName) {
        data = new Data(pluginName, "s3f.guibuilder", pluginName);
        bundle = PluginManager.getbundle(pluginName);
    }

    /**
     * Adiciona os componentes da gui.
     */
    @Override
    public abstract void init();

    @Override
    public Data getData() {
        data.setReference(this);
        return data;
    }

    @Override
    public Plugabble createInstance() {
        return null;
    }
    
    protected String getString(String key){
        return bundle.getString(key);
    }

    public void addMenubar(JMenu menu, int priority) {
        menus.add(new Element<>(menu, priority));
    }

    public void addToolbarComponent(Component toolbarComponent, int priority) {
        toolbarComponents.add(new Element<>(toolbarComponent, priority));
    }

    public void addTab(Tab tab, int priority) {
        tabs.add(new Element<>(tab, priority));
        data.addChild(tab.getData());
    }

    public ArrayList<JMenu> getMenus() {
        Collections.sort(menus);
        ArrayList<JMenu> a = new ArrayList<>();
        for (Element<JMenu> o : menus) {
            a.add(o.t);
        }
        return a;
    }

    public ArrayList<Component> getToolbarComponents(boolean left, int threshold) {
        Collections.sort(toolbarComponents);
        ArrayList<Component> a = new ArrayList<>();
        for (Element<Component> o : toolbarComponents) {
            if (o.priority < threshold) {
                if (left) {
                    a.add(o.t);
                }
            } else {
                if (!left) {
                    a.add(o.t);
                }
            }
        }
        return a;
    }

    public ArrayList<Tab> getTabs() {
        Collections.sort(tabs);
        ArrayList<Tab> a = new ArrayList<>();
        for (Element<Tab> o : tabs) {
            a.add(o.t);
        }
        return a;
    }

    public void append(GUIBuilder guibuilder) {
        menus.addAll(guibuilder.menus);
        toolbarComponents.addAll(guibuilder.toolbarComponents);
        tabs.addAll(guibuilder.tabs);
    }
    
    public JComponent separator() {
        JPanel p = new JPanel();
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        p.add(sep);
        p.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        sep.setPreferredSize(new Dimension(sep.getPreferredSize().width, 24));
        return p;
    }
}
