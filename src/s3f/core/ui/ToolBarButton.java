/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.core.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import s3f.util.ColorUtils;

/**
 *
 * @author antunes2
 */
public abstract class ToolBarButton {

    private static final double ratio = .7;
    private ImageIcon icon = new ImageIcon(JDDButton.class.getResource("/resources/icons/fugue-24/disk.png"));
//    private JPopupMenu jPopupMenu = null;
    private final JDDButton button = new JDDButton();
    private ActionListener actionListener = null;
    private boolean mouseOver;

    public ToolBarButton() {
    }

//    public final void setJPopupMenu(JPopupMenu jPopupMenu) {
//        this.jPopupMenu = jPopupMenu;
//        this.jPopupMenu.setFocusable(true);
//    }
    public JButton getJComponent() {
        return button;
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    class JDDButton extends JButton {

        public JDDButton() {
            super(icon);
            setBorderPainted(false);
            setFocusable(false);
            setMinimumSize(new Dimension(45, 35));
            setPreferredSize(new Dimension(45, 35));
            setOpaque(false);
            setIconTextGap(16);
            setText(" ");
            setFont(new Font("", Font.BOLD, 0));
            setHorizontalTextPosition(RIGHT);
            Listener listener = new Listener();
            addMouseListener(listener);
            addMouseMotionListener(listener);
            setRolloverEnabled(true);
            setRolloverIcon(new ImageIcon(ColorUtils.imageHSBAchange(icon.getImage(), 0, 0, .1f, 0)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
//            g.clearRect(29, 0, 55, 35);

            g.setColor((mouseOver || button.isSelected())
                    //                        ? SystemColor.controlDkShadow
                    //                        : SystemColor.activeCaptionBorder);
                    ? SystemColor.activeCaptionBorder
                    : SystemColor.control);

            g.drawLine(29, 7, 29, 27);

            if (ToolBarButton.this.actionListener != null) {
//                    g.setColor(SystemColor.activeCaptionBorder);
                g.setColor(SystemColor.controlDkShadow);
            }

            int x = 24 + 13;
            int y = this.getHeight() / 2 + 1;
            int aw = 4;
            int ah = 2;

            g.fillPolygon(new int[]{x - aw, x + aw, x}, new int[]{y - ah, y - ah, y + ah}, 3);
        }
    }

    public abstract JPopupMenu getJPopupMenu();

    private class Listener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            JPopupMenu jPopupMenu = getJPopupMenu();
            if (jPopupMenu != null && e.getX() >= button.getWidth() * ratio) {
                if (jPopupMenu.isVisible()) {
                    jPopupMenu.setVisible(false);
                } else {
                    jPopupMenu.setFocusable(true);
                    jPopupMenu.show(button, 0, button.getHeight());
                }
            } else {
                if (actionListener != null) {
                    actionListener.actionPerformed(new ActionEvent(button, 0, ""));
                }
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            mouseOver = false;
            button.repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            mouseOver = !(e.getX() >= button.getWidth() * ratio);
            button.repaint();
        }
    }
}
