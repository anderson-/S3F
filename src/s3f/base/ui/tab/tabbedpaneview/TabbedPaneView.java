/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.base.ui.tab.tabbedpaneview;

import java.awt.Component;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import s3f.base.ui.tab.Tab;
import s3f.base.ui.tab.tabbedpaneview.dndtabbedpane.ButtonTabComponent;
import s3f.base.ui.tab.tabbedpaneview.dndtabbedpane.DnDTabbedPane;
import s3f.base.ui.tab.tabbedpaneview.dndtabbedpane.TabComponent;

/**
 *
 * @author Anderson
 */
public class TabbedPaneView {

    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 2;
    public static final int FIRST = 3;
    public static final int SECOND = 4;
    public static final int LEFT = FIRST;
    public static final int RIGHT = SECOND;
    public static final int TOP = FIRST;
    public static final int BOTTOM = SECOND;

    private final JSplitPanel jSplitPanel = new JSplitPanel();
    private final DnDTabbedPane tabbedPane = new DnDTabbedPane();
    private final ArrayList<Tab> views = new ArrayList<>();
    private TabbedPaneView firstTabbedPaneView = null;
    private TabbedPaneView secondTabbedPaneView = null;
    private boolean first = true;
    private boolean splited = false;
    private TabbedPaneView parent = null;

    public TabbedPaneView() {
        jSplitPanel.getDefaultPanel().add(tabbedPane);
        jSplitPanel.setTabbedPaneView(this);

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabbedPane.getTabCount() > 0) {
                    Tab view = getView(tabbedPane.getSelectedComponent());
                    if (view != null) {
                        view.selected();
                    }
                }
            }
        });

        tabbedPane.addContainerListener(new ContainerListener() {
            @Override
            public void componentAdded(ContainerEvent e) {

            }

            @Override
            public void componentRemoved(ContainerEvent e) {
                if (parent != null) {
                    if (tabbedPane.getTabCount() == 0 && secondTabbedPaneView == null) {
                        if (parent.splited) {
                            parent.unsplit();
                        }
                    }
                }

            }
        });
    }

    public Tab getView(Component component) {
        for (Tab view : views) {
            if (component.equals(view.getData().getProperty(Tab.COMPONENT))) {
                return view;
            }
        }
        return null;
    }

    public Tab getView(int index) {
        Component component = tabbedPane.getComponentAt(index);
        return getView(component);
    }

    public void setParent(TabbedPaneView parent) {
        this.parent = parent;
    }

    public TabbedPaneView getParent() {
        return parent;
    }

    public DnDTabbedPane getDnDTabbedPane() {
        return tabbedPane;
    }

    public void split(int orientation, boolean first) {
        if (firstTabbedPaneView == null) {
            firstTabbedPaneView = new TabbedPaneView();
            firstTabbedPaneView.setParent(this);
        }

        if (secondTabbedPaneView == null) {
            secondTabbedPaneView = new TabbedPaneView();
            secondTabbedPaneView.setParent(this);
        }

        transferTabs(tabbedPane, firstTabbedPaneView.getDnDTabbedPane());
        jSplitPanel.getDefaultPanel().removeAll();
        jSplitPanel.getDefaultPanel().add(firstTabbedPaneView.getJPanel());
        jSplitPanel.split((orientation == VERTICAL), !first, secondTabbedPaneView.getJPanel());
        this.first = first;
        splited = true;
    }

    private void transferTabs(JTabbedPane source, JTabbedPane dest) {
        int n = source.getTabCount();
        for (int i = 0; i < n; i++) {
            dest.addTab(source.getTitleAt(0), source.getIconAt(0), source.getComponentAt(0), source.getToolTipTextAt(0));
        }
    }

    public void unsplit() {
        if (splited) {
            transferTabs(tabbedPane, firstTabbedPaneView.getDnDTabbedPane());
            transferTabs(secondTabbedPaneView.getDnDTabbedPane(), firstTabbedPaneView.getDnDTabbedPane());

            jSplitPanel.unsplit();
            splited = false;
        }
    }

    public TabbedPaneView get(int position) {
        if (position == FIRST && first) {
            if (jSplitPanel.isSplitted()) {
                return firstTabbedPaneView;
            } else {
                return this;
            }
        } else {
            return secondTabbedPaneView;
        }
    }

    public void add(Tab view) {
        if (jSplitPanel.isSplitted()) {
            firstTabbedPaneView.add(view);
        } else {
            this.getDnDTabbedPane().addTab("", null, (Component) view.getData().getProperty(Tab.COMPONENT), (String) view.getData().getProperty(Tab.TOOL_TIP));
            views.add(view);
            TabComponent tabComponent = new TabComponent(tabbedPane);
            tabComponent.update(view.getData());
            tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tabComponent);
        }
    }

    public JPanel getJPanel() {
        return jSplitPanel;
    }

}
