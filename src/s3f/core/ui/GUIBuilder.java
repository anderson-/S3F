/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import s3f.core.plugin.Data;
import s3f.core.plugin.Plugabble;
import s3f.core.plugin.PluginManager;
import s3f.core.ui.tab.Tab;
import s3f.util.splashscreen.SplashScreen;

/**
 *
 * @author antunes
 */
public abstract class GUIBuilder implements Plugabble {

    private static SplashScreen splashScreen;
    private static LookAndFeel lookAndFeel;
    private static Image icon;
    private static String htmlWelcomePage;
    private static String css;

    public static void setSplashScreen(SplashScreen splashScreen) {
        GUIBuilder.splashScreen = splashScreen;
    }

    public static SplashScreen getSplashScreen() {
        return splashScreen;
    }

    public static void setLookAndFeel(LookAndFeel lookAndFeel) {
        GUIBuilder.lookAndFeel = lookAndFeel;
    }

    public static LookAndFeel getLookAndFeel() {
        return lookAndFeel;
    }

    public static void setIcon(Image icon) {
        GUIBuilder.icon = icon;
    }

    public static Image getIcon() {
        return icon;
    }

    public static void setWelcomePage(String htmlWelcomePage, String css) {
        GUIBuilder.htmlWelcomePage = htmlWelcomePage;
        GUIBuilder.css = css;
    }

    public static String getWelcomePage() {
        return htmlWelcomePage;
    }

    public static String getWelcomePageStyle() {
        return css;
    }

    public final Data data;
    private ResourceBundle bundle;

    private static class Element<T> implements Comparable<Element> {

        private final float priority;
        private final T t;
        private final String[] path;

        public Element(T t, float priority) {
            this(t, priority, null);
        }

        public Element(T t, float priority, String[] path) {
            this.priority = priority;
            this.t = t;
            this.path = path;
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

        public float getPriority() {
            return priority;
        }

        public T getT() {
            return t;
        }

        public String[] getPath() {
            return path;
        }
    }

    private final ArrayList<Element<JMenuItem>> menus = new ArrayList<>();
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

    protected String getString(String key) {
        return bundle.getString(key);
    }

    public void removeMenuItem(String path) {
        addMenuItem(path, null, null, null, null, 0, null);
    }

    public JMenuItem addMenuItem(String path, String mnemonic, String acceleratorKey, String iconPath, String toolTipText, float priority, AbstractAction action) {
        String[] fullpath = path.split(">");
        if (!path.endsWith("---")) {
            for (Iterator<Element<JMenuItem>> it = menus.iterator(); it.hasNext();) {
                Element e = it.next();
                if (Arrays.equals(fullpath, e.getPath())) {
                    it.remove();
                    if (mnemonic == null || mnemonic.isEmpty()) {
                        return null;
                    } else {
                        break;
                    }
                }
            }
        }

        JMenuItem item;
        if (path.endsWith(">")) {
            item = new JMenu(fullpath[fullpath.length - 1]);
        } else if (path.endsWith("*")) {
            item = new JRadioButtonMenuItem(fullpath[fullpath.length - 1].replace("*", ""));
        } else if (path.endsWith("+")) {
            item = new JCheckBoxMenuItem(fullpath[fullpath.length - 1].replace("+", ""));
        } else {
            item = new JMenuItem(fullpath[fullpath.length - 1]);
        }

        if (mnemonic != null && !mnemonic.isEmpty()) {
            item.setMnemonic(mnemonic.charAt(0));
        }

        if (action != null) {
            action.putValue(AbstractAction.NAME, fullpath[fullpath.length - 1]);

            if (iconPath != null && !iconPath.isEmpty()) {
                action.putValue(AbstractAction.SMALL_ICON, new ImageIcon(GUIBuilder.class.getResource(iconPath)));
            }

            if (toolTipText != null && !toolTipText.isEmpty()) {
                action.putValue(AbstractAction.SHORT_DESCRIPTION, toolTipText);
            }

            if (mnemonic != null && !mnemonic.isEmpty()) {
                action.putValue(AbstractAction.MNEMONIC_KEY, KeyStroke.getKeyStroke(mnemonic).getKeyCode());
            }

            if (acceleratorKey != null && !acceleratorKey.isEmpty()) {
                //item.setAccelerator(KeyStroke.getKeyStroke("control alt P"));
                action.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(acceleratorKey));
            }

            item.setAction(action);
        }

        menus.add(new Element<>(item, priority, fullpath));
        return item;
    }

    public void addToolbarComponent(Component toolbarComponent, float priority) {
        toolbarComponents.add(new Element<>(toolbarComponent, priority));
    }

    public void addTab(Tab tab, int priority) {
        tabs.add(new Element<>(tab, priority));
        data.addChild(tab.getData());
    }

    private ArrayList<JMenuItem> buildMenus(JMenuItem menu, int depth, String[] path, ArrayList<JMenuItem> list) {
        for (Element<JMenuItem> o : menus) {
            if (o.path.length == depth + 1) {
                buildMenus(o.t, depth + 1, o.path, list);
                if (o.path.length == 1) {
                    list.add(o.t);
                } else if (Arrays.equals(Arrays.copyOfRange(path, 0, depth), Arrays.copyOfRange(o.path, 0, depth))) {
                    if (menu != null) {
                        if (menu instanceof JMenu && o.t.getText().equals("---")) {
                            ((JMenu) menu).addSeparator();
                        } else {
                            menu.add(o.t);
                        }
                    } else {
                        throw new Error();
                    }
                }
            }
        }
        return list;
    }

    public ArrayList<JMenuItem> getMenus() {
        Collections.sort(menus);
        ArrayList<JMenuItem> mainMenus = buildMenus(null, 0, null, new ArrayList<JMenuItem>());
        for (Element<JMenuItem> o : menus) {
            if (o.t instanceof JMenu) {
                ((JMenu) o.t).setEnabled(((JMenu) o.t).getItemCount() > 0);
            }
        }
        return mainMenus;
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
        for (Element e : menus) {
            for (Iterator<Element<JMenuItem>> j = guibuilder.menus.iterator(); j.hasNext();) {
                Element f = j.next();
                if (Arrays.equals(f.getPath(), e.getPath())) {
                    j.remove();
                }
            }
        }
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
