/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.base.ui.tab.tabbedpaneview.dndtabbedpane;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;
import s3f.base.plugin.Data;
import s3f.base.ui.tab.Tab;
import s3f.base.ui.tab.TabProperty;

/**
 * Representa uma aba com icone, titulo e botão de fechar.
 * 
 */
public class TabComponent extends JPanel {

    private final JTabbedPane pane;
    private final JLabel label;
    private final JIcon jicon;
    private final JButton button;

    public TabComponent(final JTabbedPane pane) {
        super.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
//        super.setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
        
        if (pane == null) {
            throw new NullPointerException("TabbedPane is null");
        } else {
            this.pane = pane;
        }

        setOpaque(false);
        //add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

        label = new JLabel();
        //add more space between the label and the button
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        jicon = new JIcon(null);
        button = new TabButton();
        
        add(jicon);
        add(label);
        add(button);
//        button.setPreferredSize(new Dimension(15,15));

//        
//        //make JLabel read titles from JTabbedPane
//        JLabel label = new JLabel() {
//            public String getText() {
//                int i = pane.indexOfTabComponent(TabComponent.this);
//                if (i != -1) {
//                    return pane.getTitleAt(i);
//                }
//                return null;
//            }
//        };
    }

    public void update(Data data){
        jicon.setIcon((Icon) data.getProperty(TabProperty.ICON));
        label.setText((String) data.getProperty(TabProperty.TITLE));
    }

    private class TabButton extends JButton implements ActionListener {

        public TabButton() {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("close this tab");
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            int i = pane.indexOfTabComponent(TabComponent.this);
            if (i != -1) {
                pane.remove(i);
            }
        }

        //we don't want to update UI for this button
        public void updateUI() {
        }

        //paint the cross
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            //shift the image for pressed buttons
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLACK);
            if (getModel().isRollover()) {
                g2.setColor(Color.RED);
            }
            int delta = 6;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.dispose();
        }
    }

    private final static MouseListener buttonMouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };

    class JIcon extends JComponent {

        private Icon icon;

        public JIcon(Icon icon) {
            this.icon = icon;
        }

        @Override
        public void paintComponent(Graphics g) {
            if (icon != null) {
                icon.paintIcon(this, g, 0, 0);
            }
        }

        @Override
        public Dimension getPreferredSize() {
            if (icon != null) {
                return new Dimension(icon.getIconWidth() + 5, icon.getIconHeight());
            } else {
                return new Dimension(0, 0);
            }
        }

        public void setIcon(Icon icon) {
            this.icon = icon;
        }
    }
}
