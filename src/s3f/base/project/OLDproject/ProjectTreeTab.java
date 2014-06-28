/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.base.project.OLDproject;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.RootWindow;
import s3f.base.plugin.Configurable;
import s3f.base.plugin.Data;
import s3f.base.plugin.Extensible;
import s3f.base.plugin.Plugabble;
import s3f.base.plugin.PluginManager;
import s3f.base.ui.MainUI;
import s3f.base.ui.tab.Tab;
import s3f.base.ui.tab.TabProperty;

/**
 *
 * @author antunes2
 */
public class ProjectTreeTab implements Tab, Extensible {

    private final JTree tree;
    private final JScrollPane treeView;
    private Project project;
    private Data data;

    @Deprecated
    public ProjectTreeTab() {
        this(new Project("nil"));
    }

    public ProjectTreeTab(String name) {
        this(new Project(name));
    }

    public ProjectTreeTab(Project project) {
        //create a tree that allows one selection at a time
        tree = new JTree();
        tree.setCellRenderer(new FeeRenderer());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        //create the scroll pane and add the tree to it
        treeView = new JScrollPane(tree);
        //update content
        setProject(project);

        data = new Data("projectTreeTab", "s3f.base.project", "ProjectTreeTab");
        TabProperty.put(data, "Projeto", null, "Informações sobre o projeto atual", treeView);

        createUI();
    }

    private void createUI() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem jMenuItem = new JMenu("Novo");
        popupMenu.add(jMenuItem);
        jMenuItem.add(new JMenuItem("aasdasd"));
//        treeView.setComponentPopupMenu(popupMenu);
//        tree.setInheritsPopupMenu(true);

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                    if (selPath != null) {
                        Object[] path = selPath.getPath();
                        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) path[path.length - 1];

                        System.out.println(path[path.length - 1] + " : " + dmtn.getUserObject().getClass());
                    }
                    Rectangle pathBounds = tree.getUI().getPathBounds(tree, selPath);
                    if (pathBounds != null && pathBounds.contains(e.getX(), e.getY())) {
                        JPopupMenu menu = new JPopupMenu();
                        menu.add(new JMenuItem("Test"));
                        menu.show(tree, pathBounds.x, pathBounds.y + pathBounds.height);
                    } else {

                    }
                }
            }
        });

        MouseListener ml = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selRow = tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if (selRow != -1 && selPath != null) {
                    Object[] path = selPath.getPath();
                    DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) path[path.length - 1];

                    if (e.getClickCount() == 1) {
                        //mySingleClick(selRow, selPath);

                    } else if (e.getClickCount() == 2) {
                        //myDoubleClick(selRow, selPath);
                        Object userObject = dmtn.getUserObject();

                        if (userObject instanceof Configurable) {
                            Component component = ((Configurable) userObject).getData().getProperty(TabProperty.COMPONENT);

//                            DockingWindow dw = null;
//                            Component p = component;
//                            while (p != null) {
//                                System.out.println(p.getClass());
//                                p = p.getParent();
//
//                                if (p instanceof DockingWindow) {
//                                    dw = (DockingWindow) p;
//                                }
//                            }
//
//                            if (dw != null) {
//                                System.out.println(dw.isDisplayable());
//                            }

                            if (component != null) {
                                if (component.isDisplayable()) {
                                    System.out.println("is displ");
                                } else {
                                    System.out.println("added");
                                    MainUI.getInstance().addView(1, (Configurable) userObject);
                                }
                            }

                        }

                        System.out.println("ope" + path[path.length - 1] + " : ");
                    }
                }
            }
        };
        tree.addMouseListener(ml);
    }

    public final void setProject(Project project) {
        this.project = project;
        update();
    }

    @Override
    public final void update() {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(project);
        tree.setModel(new DefaultTreeModel(top));
        for (Element.CategoryData category : project.getElementsCategories()) {
            DefaultMutableTreeNode elementCategory = new DefaultMutableTreeNode(category);

            top.add(elementCategory);
            for (Element se : project.getElements(category.getName())) {
                DefaultMutableTreeNode element = new DefaultMutableTreeNode(se);
                elementCategory.add(element);
            }
        }
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    @Override
    public void selected() {

    }

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public void loadModulesFrom(PluginManager pm) {

    }

    class FeeRenderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean bSelected, boolean bExpanded, boolean bLeaf, int nRow, boolean bFocus) {
            super.getTreeCellRendererComponent(tree, value, bSelected, bExpanded, bLeaf, nRow, bFocus);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object obj = node.getUserObject();
            Icon icon = null;
            if (obj instanceof Element) {
                icon = ((Element) obj).getIcon();
            } else if (obj instanceof Project) {
                setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
                return this;
            } else if (obj instanceof Element.CategoryData) {
                icon = ((Element.CategoryData) obj).getIcon();
                if (icon != null) {
                    setIcon(icon);
                    return this;
                }
            }

            if (bLeaf) {
                if (icon != null) {
                    setIcon(icon);
                } else {
                    setIcon(UIManager.getIcon("Tree.leafIcon"));
                }
            } else {
                if (bExpanded) {
                    setIcon(UIManager.getIcon("Tree.openIcon"));
                } else {
                    setIcon(UIManager.getIcon("Tree.closedIcon"));
                }
            }
            return this;
        }
    }

}
