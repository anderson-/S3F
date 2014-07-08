/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 *
 * @author antunes2
 */
public class ToolBarButton {

    private Icon icon = new ImageIcon(JDDButton.class.getResource("/resources/tango/32x32/mimetypes/text-x-generic.png"));
    private JPopupMenu jPopupMenu = null;
    private final JDDButton button = new JDDButton();
    private static double ratio = .75;
    private ActionListener actionListener = null;
    private boolean mouseOver;
    private boolean isShowingPopup;

    public ToolBarButton() {
        JPopupMenu popup = new JPopupMenu();
        //Create the popup menu.
        popup.add(new JMenuItem(new AbstractAction("Option 1") {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        }));
        popup.add(new JMenuItem(new AbstractAction("Option 2") {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        }));
        JPanel p = new JPanel();
        p.setBackground(UIManager.getColor("Menu.background"));

        p.add(new JLabel("Insira Tempo:"));
        p.add(new JTextField("123"));
        popup.insert(p, 0);
        setJPopupMenu(popup);
        button.addMouseListener(new Listener());
    }

    public final void setJPopupMenu(JPopupMenu jPopupMenu) {
        if (jPopupMenu == null) {
            button.setIcon(icon);
        } else {
            button.setIcon(null);
        }
        this.jPopupMenu = jPopupMenu;
        this.jPopupMenu.setFocusable(true);

        this.jPopupMenu.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                button.setSelected(false);
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });

//        this.jPopupMenu.addFocusListener(new FocusListener() {
//            @Override
//            public void focusLost(FocusEvent e) {
//                isShowingPopup = false;
//            }
//
//            @Override
//            public void focusGained(FocusEvent e) {
//            }
//        });
    }

    public JComponent getJComponent() {
        return button;
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    class JDDButton extends JToggleButton {

        public JDDButton() {
            super("", icon);
            setBorderPainted(false);
            setFocusable(false);
            setMinimumSize(new Dimension(45, 55));
            setPreferredSize(new Dimension(45, 55));
            //sem background
//            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (jPopupMenu != null) {
                int h = this.getHeight();
                int w = this.getWidth();
                int h1 = (int) (h * ratio);
                int h2 = (int) (h * (1 - ratio));

                icon.paintIcon(this, g, (w - icon.getIconWidth()) / 2, (h1 - icon.getIconHeight()) / 2);

                g.setColor(SystemColor.controlDkShadow);
//                g.setColor((mouseOver || button.isSelected())
//                        ? SystemColor.controlDkShadow
//                        : SystemColor.activeCaptionBorder);
                if (ToolBarButton.this.actionListener != null) {
                    g.drawLine(7, h1 - 1, this.getWidth() - 7, h1 - 1);
                }

                int x = w / 2;
                int y = h1 + h2 / 2 - 1;
                int aw = 4;
                int ah = 2;

                g.fillPolygon(new int[]{x - aw, x + aw, x}, new int[]{y - ah, y - ah, y + ah}, 3);
            }
        }
    }

    class Listener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (jPopupMenu != null && e.getY() >= button.getHeight() * ratio) {
//                if (isShowingPopup) {
//                    jPopupMenu.requestFocus();
//                    isShowingPopup = false;
//                } else {
                    jPopupMenu.show(button, 0, button.getHeight() + 5);
//                    isShowingPopup = true;
//                }
            } else {
                if (actionListener != null) {
                    actionListener.actionPerformed(new ActionEvent(button, 0, ""));
                } else {
                    jPopupMenu.show(button, 0, button.getHeight() + 5);
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        button.setSelected(true);
                    }
                });

            }

        }

        @Override
        public void mouseExited(MouseEvent e) {
            mouseOver = false;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            mouseOver = true;
        }
    }
}
