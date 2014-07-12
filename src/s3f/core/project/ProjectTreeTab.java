/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.project;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import net.infonode.docking.View;
import s3f.core.plugin.Data;
import s3f.core.plugin.EntityManager;
import s3f.core.plugin.Extensible;
import s3f.core.plugin.PluginManager;
import s3f.core.ui.MainUI;
import s3f.core.ui.tab.Tab;
import s3f.core.ui.tab.TabProperty;

/**
 *
 * @author antunes2
 */
public class ProjectTreeTab implements Tab, Extensible {

    private static final ImageIcon PROJECT_ICON = new ImageIcon(ProjectTreeTab.class.getResource("/resources/icons/silk/folder.png"));

    private final JTree tree;
    private final JScrollPane treeView;
    private Project project;
    private Data data;
    private HashMap<Element, Editor> openEditors = new HashMap<>();

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

        data = new Data("projectTreeTab", "s3f.core.project", "ProjectTreeTab");
        TabProperty.put(data, "Projeto", null, "Informações sobre o projeto atual", treeView);

        createUI();
    }

    public void createElement(final Element.CategoryData category) {
        final Element element = (Element) category.getStaticInstance().createInstance();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String newName = (String) JOptionPane.showInputDialog(
                        null,
                        "insert name:",
                        "novo elemento",
                        JOptionPane.PLAIN_MESSAGE,
                        element.getIcon(),
                        null,
                        element.getName()
                );

                if (newName != null) {
                    project.addElement(element);
                    element.setName(newName);
                    createElement(element);
                }
            }
        });
    }

    public void createElement(Element element) {
        Editor editor = (Editor) element.getEditorManager().getDefaultEditor().createInstance();
        createElement(element, editor);
    }

    public void createElement(Element element, Editor editor) {
        Editor openEditor = openEditors.get(element);
        if (openEditor != null) {
            Component component = openEditor.getData().getProperty(TabProperty.COMPONENT);

            if (openEditor.getClass().isAssignableFrom(editor.getClass())) {
                if (component != null) {
                    if (component.isDisplayable()) {
                        System.out.println("is displ");
                        MainUI.getInstance().selectComponent(component);
                    } else {
                        System.out.println("added");
                        MainUI.getInstance().addView(1, openEditor);
                    }
                } else {
                    System.out.println("comp is null");
                }
            } else {
                View v = MainUI.componentSearch(component, View.class, false);
                openEditors.remove(element);
                createElement(element, editor);
                v.close();
                return;
            }
        } else {
            editor.setContent(element);
            editor.update();
            element.setCurrentEditor(editor);
            openEditors.put(element, editor);
            MainUI.getInstance().addView(1, editor);
        }

        update();
    }

    public void deleteElement(Element element) {
        Editor openEditor = openEditors.get(element);
        if (openEditor != null) {
            Component component = openEditor.getData().getProperty(TabProperty.COMPONENT);
            View v = MainUI.componentSearch(component, View.class, false);
            openEditors.remove(element);
            v.close();
        }
        project.deleteElement(element);
        update();
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
                    Rectangle pathBounds = tree.getUI().getPathBounds(tree, selPath);
                    if (pathBounds != null && pathBounds.contains(e.getX(), e.getY())) {
                        if (selPath != null) {
                            Object[] path = selPath.getPath();
                            DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) path[path.length - 1];

                            if (dmtn.getUserObject() instanceof Project) {
                                JPopupMenu menu = new JPopupMenu();
                                List<Element.CategoryData> entities = PluginManager.getInstance().createFactoryManager(null).getEntities("s3f.core.project.category.*", Element.CategoryData.class);
                                for (final Element.CategoryData c : entities) {
                                    JMenuItem item = new JMenuItem(c.getName());
                                    item.addActionListener(new ActionListener() {

                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            //gerenciador de editores: abrir com...
                                            //default editor...
                                            //set default editor...

                                            createElement(c);
                                        }

                                    }
                                    );
                                    menu.add(item);
                                }

                                menu.show(tree, pathBounds.x, pathBounds.y + pathBounds.height);
                            } else if (dmtn.getUserObject() instanceof Resource) {
                                final Resource resource = (Resource) dmtn.getUserObject();
                                JPopupMenu menu = new JPopupMenu();
                                
                                JMenuItem item = new JMenuItem("remove");
                                item.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        resource.getPrimary().removeResource(resource);
                                        update();
                                    }
                                });
                                menu.add(item);
                                menu.show(tree, pathBounds.x, pathBounds.y + pathBounds.height);
                            } else if (dmtn.getUserObject() instanceof Element) {
                                final Element element = (Element) dmtn.getUserObject();
                                JPopupMenu menu = new JPopupMenu();

                                JMenuItem item = new JMenuItem("open with def editor");
                                item.addActionListener(new ActionListener() {

                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        createElement(element);
                                    }

                                });
                                menu.add(item);

                                if (element.getEditorManager().getAvailableEditors().size() > 1) {
                                    item = new JMenu("open with...");
                                    for (final Editor editor : element.getEditorManager().getAvailableEditors()) {
                                        JMenuItem subItem = new JMenuItem(editor.getClass().getSimpleName());
                                        subItem.addActionListener(new ActionListener() {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                createElement(element, (Editor) editor.createInstance());
                                            }
                                        });
                                        item.add(subItem);
                                    }
                                    menu.add(item);
                                }

                                if (element instanceof ExtensibleElement) {
                                    final ExtensibleElement extensibleElement = (ExtensibleElement) element;
                                    item = new JMenu("add resource");
                                    boolean empty = true;
                                    if (extensibleElement.getCompatibleCategories().isEmpty()) {
                                        for (final Element subElement : project.getElements()) {
                                            if (subElement != extensibleElement) {
                                                JMenuItem subItem = new JMenuItem(subElement.toString());
                                                subItem.addActionListener(new ActionListener() {
                                                    @Override
                                                    public void actionPerformed(ActionEvent e) {
                                                        extensibleElement.addResource(new Resource(extensibleElement, subElement));
                                                        update();
                                                    }
                                                });
                                                item.add(subItem);
                                                empty = false;
                                            }
                                        }
                                    } else {
                                        for (String category : extensibleElement.getCompatibleCategories()) {
                                            for (final Element subElement : project.getElements(category)) {
                                                JMenuItem subItem = new JMenuItem(subElement.toString());
                                                subItem.addActionListener(new ActionListener() {
                                                    @Override
                                                    public void actionPerformed(ActionEvent e) {
                                                        extensibleElement.addResource(new Resource(extensibleElement, subElement));
                                                        update();
                                                    }
                                                });
                                                item.add(subItem);
                                                empty = false;
                                            }
                                        }
                                    }
                                    if (!empty) {
                                        menu.add(item);
                                    }
                                }

                                item = new JMenuItem("rename");
                                item.addActionListener(new ActionListener() {

                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        SwingUtilities.invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                String newName = (String) JOptionPane.showInputDialog(
                                                        null,
                                                        "new name:",
                                                        "renomear",
                                                        JOptionPane.PLAIN_MESSAGE,
                                                        element.getIcon(),
                                                        null,
                                                        element.getName()
                                                );

                                                if (newName != null) {
                                                    element.setName(newName);
                                                    update();
                                                }
                                            }
                                        });
                                    }

                                });
                                menu.add(item);

                                menu.addSeparator();

                                item = new JMenuItem("delete");
                                item.addActionListener(new ActionListener() {

                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        deleteElement(element);
                                    }

                                });
                                menu.add(item);

                                menu.show(tree, pathBounds.x, pathBounds.y + pathBounds.height);
                            } else {
                                System.out.println(path[path.length - 1] + " : " + dmtn.getUserObject().getClass());
                            }
                        }
                    }
                }
            }
        }
        );

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

                        if (userObject instanceof Element) {
                            Element element = (Element) userObject;
                            createElement(element);
                        } else {
                            System.out.println("not configurable : " + path[path.length - 1] + " : " + userObject.getClass());
                        }
                    }
                }
            }
        };

        tree.addMouseListener(ml);

//        final TreeCellEditor editor = new DefaultTreeCellEditor(tree, (DefaultTreeCellRenderer) tree.getCellRenderer()) {
////            @Override
////            protected boolean canEditImmediately(EventObject event) {
////                if ((event instanceof MouseEvent)
////                        && SwingUtilities.isLeftMouseButton((MouseEvent) event)) {
////                    MouseEvent e = (MouseEvent) event;
////
////                    int selRow = tree.getRowForLocation(e.getX(), e.getY());
////                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
////                    if (selRow != -1 && selPath != null) {
////                        Object[] path = selPath.getPath();
////                        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) path[path.length - 1];
////                        Object userObject = dmtn.getUserObject();
////
////                        if (userObject instanceof Element) {
////                            return ((e.getClickCount() == 3)
////                                    && inHitRegion(e.getX(), e.getY()));
////                        }
////                    }
////                }
////                return (event == null);
////            }
//        };
//
//        editor.addCellEditorListener(new CellEditorListener() {
//            @Override
//            public void editingStopped(ChangeEvent e) {
//                System.out.println(tree.getLastSelectedPathComponent().getClass());
//                
//                if (tree.getLastSelectedPathComponent() instanceof DefaultMutableTreeNode) {
//                    
//                    DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
//                    Object userObject = dmtn.getUserObject();
//                    System.out.println(userObject.getClass());
//
//                    if (userObject instanceof Element) {
//                        Element element = (Element) userObject;
//                        element.setName(editor.getCellEditorValue().toString());
//                        SwingUtilities.invokeLater(new Runnable() {
//                            @Override
//                            public void run() {
//                                update();
//                            }
//                        });
//                    } else {
//                        System.out.println("???" + userObject.getClass());
//                    }
//                }
//            }
//
//            @Override
//            public void editingCanceled(ChangeEvent e) {
//            }
//        });
//        tree.setEditable(true);
//        tree.setCellEditor(editor);
    }

    public final void setProject(Project project) {
        this.project = project;
        update();
    }

    @Override
    public final void update() {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(project);
        tree.setModel(new DefaultTreeModel(top));
        List<Element.CategoryData> categories = PluginManager.getInstance().createFactoryManager(null).getEntities("s3f.core.project.category.*", Element.CategoryData.class);
        for (Element.CategoryData category : categories) {
            DefaultMutableTreeNode elementCategory = new DefaultMutableTreeNode(category);
            boolean isEmpty = true;
            for (Element se : project.getElements(category.getName())) {
                DefaultMutableTreeNode element = new DefaultMutableTreeNode(se);
                elementCategory.add(element);
                if (se instanceof ExtensibleElement) {
                    ExtensibleElement ex = (ExtensibleElement) se;
                    for (Resource r : ex.getResources()) {
                        DefaultMutableTreeNode subElement = new DefaultMutableTreeNode(r);
                        element.add(subElement);
                    }
                }

                isEmpty = false;
            }
            if (!isEmpty) {
                top.add(elementCategory);
            }
        }
        for (int i = 0; i < tree.getRowCount(); i++) {
            TreePath pathForRow = tree.getPathForRow(i);
            Object lastPathComponent = pathForRow.getLastPathComponent();
            if (lastPathComponent instanceof DefaultMutableTreeNode){
                DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) lastPathComponent;
                Object userObject = defaultMutableTreeNode.getUserObject();
                if (userObject instanceof Element){
                    continue;
                }
            }
            tree.expandRow(i);
        }
    }

//    private void setExpandedState(Object id, boolean expand) {
//        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
//        Enumeration e = root.breadthFirstEnumeration();
//        while (e.hasMoreElements()) {
//            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
//            if (node.getUserObject().equals(id)) {
//                TreePath path = new TreePath(node.getPath());
//                expandNode(path, expand);
//                break;
//            }
//        }
//    }
//
//    private void expandNode(TreePath parent, boolean expand) {
//        TreeNode node = (TreeNode) parent.getLastPathComponent();
//        if (node.getChildCount() >= 0) {
//            Enumeration e = node.children();
//            while (e.hasMoreElements()) {
//                TreeNode n = (TreeNode) e.nextElement();
//                TreePath path = parent.pathByAddingChild(n);
//                expandNode(path, expand);
//            }
//        }
//        if (expand) {
//            tree.expandPath(parent);
//        } else {
//            tree.collapsePath(parent);
//        }
//    }

    @Override
    public void selected() {

    }

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public void loadModulesFrom(EntityManager em) {

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
                icon = PROJECT_ICON;
            } else if (obj instanceof Element.CategoryData) {
                icon = ((Element.CategoryData) obj).getIcon();
            } else if (obj instanceof ExtensibleElement) {
                icon = ((ExtensibleElement) obj).getIcon();
            } else if (obj instanceof Resource) {
                icon = ((Resource) obj).getSecondary().getIcon();
            }

            if (icon != null) {
                setIcon(icon);
                return this;
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
