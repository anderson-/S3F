/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.base.ui.tab.tabbedpaneview;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 *
 * @author antunes2
 */
public class JSplitPanel extends JPanel {

    public static final String UPDATE_UI = "Physicist";

    protected final JPanel defaultPanel = new JPanel();
    protected final JSplitPane splitpane = new JSplitPane();
    protected PropertyChangeListener propertyChangeListener;
    protected Component component = null;
    private boolean split = false;
    private TabbedPaneView tabbedPaneView = null;

    public JSplitPanel() {
        super();
        super.setLayout(new BorderLayout());//BorderLayout
        super.add(defaultPanel);
        defaultPanel.setLayout(new BorderLayout());

        //remove borda do jSplitPane
        SplitPaneUI spui = splitpane.getUI();
        splitpane.setBorder(null);
//        if (spui instanceof BasicSplitPaneUI) {
//            ((BasicSplitPaneUI) spui).getDivider().setBorder(null);
//        }

        
        
//        JButton jButton = new JButton("vertical");
//        jButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                splitVertical(new JSplitPanel());
//            }
//        });
//        defaultPanel.add(jButton,BorderLayout.CENTER);
//        jButton = new JButton("horizontal");
//        jButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                splitHorizontal(new JButton("" + Math.random()));
//            }
//        });
//        defaultPanel.add(jButton,BorderLayout.EAST);
//        jButton = new JButton("unsplit");
//        jButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                unsplit();
//            }
//        });
//        defaultPanel.add(jButton,BorderLayout.WEST);
        propertyChangeListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals(UPDATE_UI)) {
                    JSplitPanel.this.updateUI();
                    JSplitPanel.this.firePropertyChange(UPDATE_UI, null, null);
                }
            }
        };
        super.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals(UPDATE_UI)) {
                    JSplitPanel.this.updateUI();
                }
            }
        });

        defaultPanel.addPropertyChangeListener(propertyChangeListener);
    }

    public JPanel getDefaultPanel() {
        return defaultPanel;
    }

    public boolean isSplitted() {
        return split;
    }

    public void split(final boolean vertical, boolean first, Component comp) {
        if (comp != null) {
            super.remove(defaultPanel);
            component = comp;

            Dimension minimumSize = new Dimension(0, 0);
            defaultPanel.setMinimumSize(minimumSize);
            component.setMinimumSize(minimumSize);

            component.removePropertyChangeListener(propertyChangeListener);
            component.addPropertyChangeListener(propertyChangeListener);

            if (vertical) {
                splitpane.setOrientation(JSplitPane.VERTICAL_SPLIT);
            } else {
                splitpane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
            }

            if (first) {
                splitpane.setLeftComponent(component);
                splitpane.setRightComponent(defaultPanel);
            } else {
                splitpane.setLeftComponent(defaultPanel);
                splitpane.setRightComponent(component);
            }

            splitpane.setOneTouchExpandable(true);

            if (splitpane.getDividerLocation() < 0) {

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int dividerLocation;
                        if (!vertical) {
                            dividerLocation = JSplitPanel.this.getWidth() / 2;
                        } else {
                            dividerLocation = JSplitPanel.this.getHeight() / 2;
                        }
                        dividerLocation -= splitpane.getDividerSize() / 2;

                        splitpane.setDividerLocation(dividerLocation);
                    }
                });
            } else {
                splitpane.setDividerLocation(splitpane.getDividerLocation());
            }

            splitpane.setContinuousLayout(true);
            /*
             Setting the continuousLayout property to true makes the split pane's 
             contents be painted continuously while the user is moving the divider. 
             Continuous layout is not on, by default, because it can have a negative 
             performance impact. However, it makes sense to use it in this demo, when 
             having the split pane's components as up-to-date as possible can improve 
             the user experience.
             */
            super.add(splitpane);
            firePropertyChange(UPDATE_UI, null, null);
            split = true;
        } else {
            System.out.println("null?");
        }
    }

    public Component unsplit() {
        if (split) {
            super.remove(splitpane);
            super.add(defaultPanel);

            component.removePropertyChangeListener(propertyChangeListener);

            Component c = component;
            component = null;
            firePropertyChange(UPDATE_UI, null, null);
            split = false;
            return c;
        } else {
            return null;
        }
    }

    public void splitVertical(boolean first, Component comp) {
        split(true, first, comp);
    }

    public void splitHorizontal(boolean first, Component comp) {
        split(false, first, comp);
    }

    public void splitVertical(Component comp) {
        split(true, true, comp);
    }

    public void splitHorizontal(Component comp) {
        split(false, true, comp);
    }

    public void setTabbedPaneView(TabbedPaneView tpv) {
        tabbedPaneView = tpv;
    }

    public TabbedPaneView getTabbedPaneView() {
        return tabbedPaneView;
    }
}
