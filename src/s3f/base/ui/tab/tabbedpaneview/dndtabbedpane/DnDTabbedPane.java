package s3f.base.ui.tab.tabbedpaneview.dndtabbedpane;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import static javax.swing.SwingConstants.BOTTOM;
import static javax.swing.SwingConstants.LEFT;
import static javax.swing.SwingConstants.RIGHT;
import static javax.swing.SwingConstants.TOP;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.TabbedPaneUI;
import s3f.base.ui.tab.tabbedpaneview.JSplitPanel;
import s3f.base.ui.tab.tabbedpaneview.TabbedPaneView;

/**
 * Copyright (C) 2004 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 *
 * @author Robert Futrell
 * @version 0.5
 * @see DrawDnDIndicatorTabbedPane
 */
public class DnDTabbedPane extends JTabbedPane {

    private final DnDTabbedPaneTransferHandler transferHandler = new DnDTabbedPaneTransferHandler();
    private int rightClickIndex;
    private int x, y, width, height;
    private Stroke wideStroke;
    private JPopupMenu popup;
    private boolean inCloseCurrentDocument;
    private TabbedPaneCloseAction closeAction;

    private Rectangle rBackward = new Rectangle();
    private Rectangle rForward = new Rectangle();
    private Rectangle rScroll = new Rectangle();

    private Rectangle rSplitTop = new Rectangle();
    private Rectangle rSplitBottom = new Rectangle();
    private Rectangle rSplitLeft = new Rectangle();
    private Rectangle rSplitRight = new Rectangle();

    private int rwh = 20;
    private int buttonsize = 30; //XXX 30 is magic number of scroll button size
    private Point point = new Point();
    private final AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
    private long lastUpdate = 0;
    private boolean forceUpdate = false;
    private boolean splitPanel = false;
    private TabbedPaneView tabbedPaneView = null;
    private int splitOrientation = 0;
    private static final int stroke = 4;

    public boolean splitPanel() {
        return splitPanel;
    }

    public int getSplitOrientation() {
        return splitOrientation;
    }

    private Rectangle getTabAreaBounds() {
        Rectangle tabbedRect = getBounds();
        //pointed out by daryl. NullPointerException: i.e. addTab("Tab",null)
        //Rectangle compRect   = getSelectedComponent().getBounds();
        Component comp = getSelectedComponent();
        int idx = 0;
        while (comp == null && idx < getTabCount()) {
            comp = getComponentAt(idx++);
        }
        Rectangle compRect = (comp == null) ? new Rectangle() : comp.getBounds();
        int tabPlacement = getTabPlacement();
        if (tabPlacement == TOP) {
            tabbedRect.height = tabbedRect.height - compRect.height;
        } else if (tabPlacement == BOTTOM) {
            tabbedRect.y = tabbedRect.y + compRect.y + compRect.height;
            tabbedRect.height = tabbedRect.height - compRect.height;
        } else if (tabPlacement == LEFT) {
            tabbedRect.width = tabbedRect.width - compRect.width;
        } else if (tabPlacement == RIGHT) {
            tabbedRect.x = tabbedRect.x + compRect.x + compRect.width;
            tabbedRect.width = tabbedRect.width - compRect.width;
        }
        tabbedRect.grow(2, 2);
        return tabbedRect;
    }

    private void clickArrowButton(String actionKey) {
        ActionMap map = getActionMap();
        if (map != null) {
            Action action = map.get(actionKey);
            if (action != null && action.isEnabled()) {
                action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null, 0, 0));
            }
        }
    }

    public DnDTabbedPane() {
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        x = y = -1;
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        ToolTipManager.sharedInstance().registerComponent(this);

        //addChangeListener(this);
        setTransferHandler(transferHandler);

        try {
            getDropTarget().addDropTargetListener(transferHandler);
        } catch (java.util.TooManyListenersException tmle) {
            System.out.println(":/");
        }
        TabDragListener tdl = new TabDragListener();
        addMouseListener(tdl);
        addMouseMotionListener(tdl);
    }

    public void clearDnDIndicatorRect() {
        x = y = -1;
        repaint();

    }

    protected JPopupMenu getTabPopupMenu() {
        if (popup == null) {
            popup = new JPopupMenu();
            String title = "Close";
            closeAction = new TabbedPaneCloseAction(title);
            JMenuItem item = new JMenuItem(closeAction);
            popup.add(item);
            title = "CloseOthers";
            item = new JMenuItem(
                    new TabbedPaneCloseOthersAction(title));
            popup.add(item);
//            item = new JMenuItem(owner.getAction(RText.CLOSE_ALL_ACTION));
            item.setToolTipText(null);
            popup.add(item);
            popup.add(new JPopupMenu.Separator());
            title = "CopyPathToClipboard";
            item = new JMenuItem(
                    new TabbedPaneCopyPathAction(title));
            popup.add(item);
        }
        return popup;
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        TabbedPaneUI ui = getUI();
        if (ui != null) {
            int index = ui.tabForCoordinate(this, e.getX(), e.getY());
            if (index != -1) {
                return "whut1";//getRTextEditorPaneAt(index).getFileFullPath();
            }
        }
        return super.getToolTipText(e);
    }

    @Override
    public void paint(Graphics g) {
        try {
            super.paint(g);
        } catch (Exception e) {

        }
        if (x != -1 || forceUpdate) {
            forceUpdate = false;
            Graphics2D g2d = (Graphics2D) g;

            if (wideStroke == null) {
                wideStroke = new BasicStroke(stroke);
            }

            Stroke temp = g2d.getStroke();
            g2d.setStroke(wideStroke);
            g2d.setColor(SystemColor.controlDkShadow);

            Rectangle r = getTabAreaBounds();
            if (point.y < r.y + r.height) {
                g2d.drawRect(x, y, width, height);
            }

            int ctabPlacement = getTabPlacement();
            if (ctabPlacement == TOP || ctabPlacement == BOTTOM) {
                rBackward.setBounds(r.x, r.y, rwh, r.height);
                rForward.setBounds(r.x + r.width - rwh - buttonsize, r.y, rwh + buttonsize, r.height);
            } else if (ctabPlacement == LEFT || ctabPlacement == RIGHT) {
                rBackward.setBounds(r.x, r.y, r.width, rwh);
                rForward.setBounds(r.x, r.y + r.height - rwh - buttonsize, r.width, rwh + buttonsize);
            }

            int splitHeight = this.getHeight() / 5;
            int splitWidth = this.getWidth() / 5;

            rScroll.setBounds(r.x, r.y, this.getWidth(), r.height);

            rSplitTop.setBounds(stroke + r.x, stroke + r.y, this.getWidth() - stroke, splitHeight + r.height - stroke);
            rSplitBottom.setBounds(r.x + stroke, r.y + this.getHeight() - splitHeight + stroke, this.getWidth() - stroke, splitHeight - stroke);
            rSplitLeft.setBounds(r.x + stroke, r.y + stroke, splitWidth - stroke, this.getHeight() - stroke);
            rSplitRight.setBounds(r.x + this.getWidth() - splitWidth + stroke, r.y + stroke, splitWidth - stroke, this.getHeight() - stroke);

            rBackward = SwingUtilities.convertRectangle(getParent(), rBackward, this);
            rForward = SwingUtilities.convertRectangle(getParent(), rForward, this);

            rScroll = SwingUtilities.convertRectangle(getParent(), rScroll, this);
            rSplitTop = SwingUtilities.convertRectangle(getParent(), rSplitTop, this);
            rSplitBottom = SwingUtilities.convertRectangle(getParent(), rSplitBottom, this);
            rSplitLeft = SwingUtilities.convertRectangle(getParent(), rSplitLeft, this);
            rSplitRight = SwingUtilities.convertRectangle(getParent(), rSplitRight, this);

//            g2d.setPaint(Color.BLUE);
//            g2d.fillRect(point.x - 5, point.y - 5, 10, 10);
//
//            g2d.setComposite(composite);
//            g2d.setPaint(Color.RED);
//            g2d.fill(rBackward);
//            g2d.fill(rForward);
//
//            g2d.setPaint(Color.LIGHT_GRAY);
//            g2d.fill(rScroll);
//
//            g2d.setPaint(Color.GREEN);
//            g2d.fill(rSplitTop);
//            g2d.fill(rSplitBottom);
//            g2d.fill(rSplitLeft);
//            g2d.fill(rSplitRight);
//            g2d.setPaint(Color.BLUE);
            splitPanel = !rScroll.contains(point);

            if (splitPanel) {
                if (rSplitTop.contains(point)) {
                    g2d.draw(rSplitTop);
                    splitOrientation = 1;
                } else if (rSplitBottom.contains(point)) {
                    g2d.draw(rSplitBottom);
                    splitOrientation = 2;
                } else if (rSplitLeft.contains(point)) {
                    g2d.draw(rSplitLeft);
                    splitOrientation = 3;
                } else if (rSplitRight.contains(point)) {
                    g2d.draw(rSplitRight);
                    splitOrientation = 4;
                } else {
                    splitOrientation = 0;
                }
            } else {
                splitOrientation = 0;
            }

            g2d.setStroke(temp);
        }
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        // NOTE: We don't allow RMB clicks that aren't popup triggers
        // to go into super.processMouseEvent() in the off-chance that
        // a popup trigger is mouse release.  By default, tabbed panes
        // would then use the mouse "press" of the RMB to select the
        // armed tab, which we don't want.
        if (SwingUtilities.isRightMouseButton(e)) {
            if (e.isPopupTrigger()) {
                int x = e.getX();
                int y = e.getY();
                int index = indexAtLocation(x, y);
                if (index != -1) {
                    rightClickIndex = index;
                    JPopupMenu popup = getTabPopupMenu();
                    String name = "whut2";//RTextTabbedPaneView.this.getDocumentDisplayNameAt(index);
                    closeAction.setDocumentName(name);
                    popup.show(this, x, y);
                }
            }
        } else if (SwingUtilities.isMiddleMouseButton(e)) {
            if (e.getID() == MouseEvent.MOUSE_CLICKED
                    && e.getClickCount() == 1 && !e.isPopupTrigger()) {
                int x = e.getX();
                int y = e.getY();
                int index = indexAtLocation(x, y);
                if (index != -1) {
                    setSelectedIndex(index);
                    closeCurrentDocument();
                }
            }
        } else {
            super.processMouseEvent(e);
        }
    }

    public void setDnDIndicatorRect(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        repaint();
    }

    public void dragOver(Point mouseLocation) {
        point.x = mouseLocation.x;
        point.y = mouseLocation.y;

        forceUpdate = true;

        repaint();
        try {
            if (System.currentTimeMillis() - lastUpdate > 400) {
                if (rBackward.contains(point)) {
                    clickArrowButton("scrollTabsBackwardAction");
                    lastUpdate = System.currentTimeMillis();
                } else if (rForward.contains(point)) {
                    clickArrowButton("scrollTabsForwardAction");
                    lastUpdate = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (popup != null) {
            SwingUtilities.updateComponentTreeUI(popup);
        }
    }

    /**
     * Action that closes the tab last right-clicked (e.g. the popup menu was
     * displayed for it) in the tabbed pane.
     */
    private class TabbedPaneCloseAction extends AbstractAction {

        private String template;

        public TabbedPaneCloseAction(String template) {
            this.template = template;
        }

        public void actionPerformed(ActionEvent e) {
            if (rightClickIndex > -1) {
                setSelectedIndex(rightClickIndex);
                closeCurrentDocument();
            }
        }

        public void setDocumentName(String name) {
            String text = MessageFormat.format(template,
                    new Object[]{name});
            putValue(AbstractAction.NAME, text);
        }

    }

    /**
     * Action that closes all tabs except the one last right-clicked (e.g. the
     * popup menu was displayed for it) in the tabbed pane.
     */
    private class TabbedPaneCloseOthersAction extends AbstractAction {

        public TabbedPaneCloseOthersAction(String text) {
            putValue(AbstractAction.NAME, text);
        }

        public void actionPerformed(ActionEvent e) {
            if (rightClickIndex > -1) {
                closeAllDocumentsExcept(rightClickIndex);
            }
        }

    }

    /**
     * Action that copies the full path to the right-clicked tab to the
     * clipboard.
     */
    private class TabbedPaneCopyPathAction extends AbstractAction {

        public TabbedPaneCopyPathAction(String text) {
            putValue(AbstractAction.NAME, text);
        }

        public void actionPerformed(ActionEvent e) {
            if (rightClickIndex > -1) {
//                RTextEditorPane textArea = RTextTabbedPaneView.this.
//                        getRTextEditorPaneAt(rightClickIndex);
//                String path = textArea.getFileFullPath();
//                Clipboard c = Toolkit.getDefaultToolkit().
//                        getSystemClipboard();
//                c.setContents(new StringSelection(path), null);
            }
        }

    }

    /**
     * Listens for the user drag-and-dropping tabs in this tabbed pane.
     */
    private class TabDragListener extends MouseInputAdapter {

        private int tab;
        private JComponent draggedTab;
        MouseEvent firstMouseEvent;

        @Override
        public void mouseDragged(MouseEvent e) {
            if (draggedTab == null) {
                return;
            }
            if (firstMouseEvent != null) {
                e.consume();
                int action = javax.swing.TransferHandler.MOVE;
                int dx = Math.abs(e.getX() - firstMouseEvent.getX());
                int dy = Math.abs(e.getY() - firstMouseEvent.getY());
                //Arbitrarily define a 5-pixel shift as the
                //official beginning of a drag.
                if (dx > 5 || dy > 5) {
                    //This is a drag, not a click.
                    //Tell the transfer handler to initiate the drag.
                    transferHandler.exportAsDrag(DnDTabbedPane.this,
                            firstMouseEvent, action);
                    firstMouseEvent = null;
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            tab = indexAtLocation(e.getX(), e.getY());
            if (tab > -1) {
                draggedTab = (JComponent) getComponentAt(tab);
                firstMouseEvent = e;
                e.consume();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            draggedTab = null;
            firstMouseEvent = null;
        }

    }
//
//    /**
//     * Adds a tab to the tabbed pane, and places a number beside documents
//     * opened multiple times.
//     *
//     * @param title The "display name" to use on the tab of the document.
//     * @param component The scroll pane containing the text editor to add.
//     * @param fileFullPath The path to the file this editor contains.
//     */
//    @Override
//    protected void addTextAreaImpl(String title, Component component,
//            String fileFullPath) {
//
//        // "Physically" add the tab.
//        JPanel temp = new JPanel(new BorderLayout());
//        temp.add(component);
//        RTextScrollPane sp = (RTextScrollPane) component;
//        RTextEditorPane textArea = (RTextEditorPane) sp.getTextArea();
//        temp.add(createErrorStrip(textArea), BorderLayout.LINE_END);
//        tabbedPane.addTab(title, getIconFor(sp), temp);
//
//        // Loop through all tabs (documents) except the last (the one just added).
//        int tabCount = getNumDocuments();
//        for (int i = 0; i < tabCount - 1; i++) {
//
//            // If any of them is the same physical file as the just added one, do the numbering.
//            if (getRTextEditorPaneAt(i).getFileFullPath().equals(fileFullPath)) {
//                int count = 0;
//                for (int j = i; j < tabCount; j++) {
//                    RTextEditorPane pane = getRTextEditorPaneAt(j);
//                    if (pane.getFileFullPath().equals(fileFullPath)) {
//                        String newTitle = title + " (" + (++count) + ")";
//                        if (pane.isDirty()) {
//                            newTitle = newTitle + "*";
//                        }
//                        try {
//                            setDocumentDisplayNameAt(j, newTitle);
//                        } catch (Exception e) {
//                            owner.displayException(e);
//                        }
//                    }
//                }
//                break;
//            }
//
//        }
//
//		// Do any extra stuff.
//        // This updates currentTextArea and shifts focus too.
//        setSelectedIndex(tabCount - 1);
//        if (getCurrentTextArea().isDirty()) {
//            owner.setMessages(fileFullPath + "*", "Opened document '" + fileFullPath + "'");
//        } else {
//            owner.setMessages(fileFullPath, "Opened document '" + fileFullPath + "'");
//        }
//
//		// RText's listeners will be updated by stateChanged() for all
//        // addTextAreaImpl() calls.
//    }

    /**
     * {@inheritDoc}
     */
    protected synchronized boolean closeCurrentDocumentImpl() {

//        ResourceBundle msg = owner.getResourceBundle();
        // Return code for if the user is prompted to save; returns yes for
        // closeAllDocuments().
//        int rc = promptToSaveBeforeClosingIfDirty();
//        if (rc == JOptionPane.CANCEL_OPTION) {
//            return false;
//        }
        inCloseCurrentDocument = true;
//        RTextEditorPane oldTextArea = getCurrentTextArea();

        // Remove listeners from current text area IFF stateChanged() won't do it
        // (i.e., we're removing any document except the "rightmost" document).
//		boolean removingLastDocument = (getSelectedIndex()==getNumDocuments()-1);
//		if (removingLastDocument==false) {
//        oldTextArea.removeCaretListener(owner);
//		}
        // Remove the document from this tabbed pane.
        removeComponentAt(getSelectedIndex());

        // If there are open documents, make sure any duplicates are numbered
        // correctly.  If there are no open documents, add a new empty one.
        if (getNumDocuments() > 0) {
//            renumberDisplayNames();
        } else {
//            addNewEmptyUntitledFile();
        }

        // Request focus in the window for the new currentTextArea.
        // Note that this may also be done by stateChanged() but we need to
        // do it here too because code below relies on currentTextArea being
        // up-to-date.
//        oldTextArea = getCurrentTextArea();
//        setCurrentTextArea(getRTextEditorPaneAt(getSelectedIndex()));
//        final RTextEditorPane currentTextArea = getCurrentTextArea();
        // MUST be done by SwingUtilities.invokeLater(), I think because
        // currentTextArea is not yet visible on this line of code, so
        // calling requestFocusInWindow() now would do nothing.
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//				// NOTE: This null check here is silly, but we have to do
//                // it because: Before this runnable runs, we are sure that
//                // at least 1 document is open in RText.  However, when this
//                // Runnable runs, there is a case where there are no
//                // documents open: If one empty untitled document is open,
//                // and the user tries to open a file from the file history
//                // that no longer exists.  In that case the current document
//                // will have been closed to be replaced with the new one,
//                // but the "does not exist, create it?" dialog pops up
//                // while no documents are in the tabbed pane, which causes
//                // currentTextArea to be null here.
//                if (currentTextArea != null) {
//                    currentTextArea.requestFocusInWindow();
//                }
//            }
//        });
        // Update RText's listeners IFF the active document number doesn't
        // change (i.e., they closed any document except the "rightmost" one).
        // Closing the "rightmost" document means stateChanged() will handle
        // the listeners.
//		if (removingLastDocument==false) {
//        currentTextArea.addCaretListener(owner);
//		}
        inCloseCurrentDocument = false;

        // Let any listeners know that the current document changed.
//        firePropertyChange(CURRENT_DOCUMENT_PROPERTY, -1, getSelectedIndex());
//        fireCurrentTextAreaEvent(CurrentTextAreaEvent.TEXT_AREA_CHANGED,
//                oldTextArea, currentTextArea);
        // Update the RText's status bar.
//        updateStatusBar();
        // Update RText's title and the status bar message.
//        if (currentTextArea.isDirty()) {
//            owner.setMessages(currentTextArea.getFileFullPath() + "*", msg.getString("Ready"));
//        } else {
//            owner.setMessages(currentTextArea.getFileFullPath(), msg.getString("Ready"));
//        }
        return true;

    }

    /**
     * Returns the name being displayed for the specified document.
     *
     * @param index The index at which to find the name. If the index is
     * invalid, <code>null</code> is returned.
     * @return The name being displayed for this document.
     */
    public String getDocumentDisplayNameAt(int index) {
        if (index >= 0 && index < getTabCount()) {
            return getTitleAt(index);
        }
        return null;
    }

    /**
     * Returns the location of the document selection area of this component.
     *
     * @return The location of the document selection area.
     */
    public int getDocumentSelectionPlacement() {
        return getTabPlacement();
    }

    /**
     * Returns the number of documents open in this container.
     *
     * @return The number of open documents.
     */
    public int getNumDocuments() {
        return getTabCount();
    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public RTextScrollPane getRTextScrollPaneAt(int index) {
//        if (index < 0 || index >= getNumDocuments()) //throw new IndexOutOfBoundsException();
//        {
//            return null;
//        }
//        JPanel temp = (JPanel) tabbedPane.getComponentAt(index);
//        return (RTextScrollPane) temp.getComponent(0);
//    }
    /**
     * Returns the currently active component.
     *
     * @return The component.
     */
    @Override
    public Component getSelectedComponent() {
        return getComponentAt(getSelectedIndex());
    }

//    /**
//     * Repaints the display names for open documents.
//     */
//    @Override
//    public void refreshDisplayNames() {
//
//        Color defaultForeground = UIManager.getColor("tabbedpane.foreground");
//        Color modifiedColor = getModifiedDocumentDisplayNamesColor();
//        int numDocuments = getNumDocuments();
//
//        if (highlightModifiedDocumentDisplayNames() == true) {
//            for (int i = 0; i < numDocuments; i++) {
//                if (getRTextEditorPaneAt(i).isDirty() == true) {
//                    tabbedPane.setForegroundAt(i, modifiedColor);
//                } else {
//                    tabbedPane.setForegroundAt(i, defaultForeground);
//                }
//            }
//        } else {
//            for (int i = 0; i < numDocuments; i++) {
//                tabbedPane.setForegroundAt(i, defaultForeground);
//            }
//        }
//
//    }
    /**
     * Removes a component from this container. Note that this method does not
     * update currentTextArea, you must do that yourself.
     */
    protected void removeComponentAt(int index) {
        if (index >= 0 && index < getNumDocuments()) {
            removeTabAt(index);
            //currentTextArea = getRTextEditorPaneAt(getSelectedIndex());
        }
    }

//    /**
//     * Sets the name of the document displayed on the document's tab.
//     *
//     * @param index The index of the document whose name you want to change. If
//     * this value is invalid, this method does nothing.
//     * @param displayName The name to display.
//     * @see #getDocumentDisplayNameAt
//     */
//    @Override
//    public void setDocumentDisplayNameAt(int index, String displayName) {
//        if (index >= 0 && index < getNumDocuments()) {
//            setTitleAt(index, displayName);
//            // Hack-of-a-way to tell if this document is modified.
//            if (displayName.charAt(displayName.length() - 1) == '*') {
//                if (highlightModifiedDocumentDisplayNames() == true) {
//                    setForegroundAt(index,getModifiedDocumentDisplayNamesColor());
//                } else {
//                    setForegroundAt(index,getForeground());
//                }
//            } // Just set it to regular color (this may/may not be unnecessary...).
//            else {
//                setForegroundAt(index, getForeground());
//            }
//        }
//        // May need to reset icon if extension has changed.
//        setIconAt(index, getIconFor(getRTextScrollPaneAt(index)));
//    }
//    /**
//     * Sets the currently active document. This updates currentTextArea.
//     *
//     * @param index The index of the document to make the active document. If
//     * this value is invalid, nothing happens.
//     */
//    @Override
//    public void setSelectedIndex(int index) {
//        if (index >= 0 && index < getNumDocuments()) {
//            setSelectedIndex(index);
//            setCurrentTextArea(getRTextEditorPaneAt(index));
//            updateStatusBar();
//            getCurrentTextArea().requestFocusInWindow();
//        }
//    }
//    public void stateChanged(ChangeEvent e) {
//
//		// Skip this stuff if we're called from closeCurrentDocument(), as
//        // that method does this stuff to; let's not do it twice (listeners
//        // would get two CURRENT_DOCUMENT_PROPERTY events).
//        if (inCloseCurrentDocument) {
//            return;
//        }
//
//		// TODO: Factor the code below and the similar code in
//        // closeCurrentDocument() into a common method.
//		// Remove the RText listeners associated with the current
//        // currentTextArea.
//        RTextEditorPane oldTextArea = getCurrentTextArea();
//        if (oldTextArea != null) {
//            oldTextArea.removeCaretListener(owner);
//        }
//
//		// The new currentTextArea will only be null when we're closing the
//        // only open document.  Even then, after this a new document will be
//        // opened and this method will be re-called.
//        setCurrentTextArea(getRTextEditorPaneAt(getSelectedIndex()));
//        final RTextEditorPane currentTextArea = getCurrentTextArea();
//        if (currentTextArea != null) {
//
//            if (currentTextArea.isDirty()) {
//                owner.setMessages(currentTextArea.getFileFullPath() + "*", null);
//            } else {
//                owner.setMessages(currentTextArea.getFileFullPath(), null);
//            }
//            updateStatusBar(); // Update read-only and line/col. indicators.
//
//			// Give the current text area focus.  We have to do this in
//            // a Runnable as during this stateChanged() call, the text area's
//            // panel hasn't actually been made visible yet, and that must
//            // have happened for requestFocusInWindow to work.
//            SwingUtilities.invokeLater(new Runnable() {
//                public void run() {
//                    currentTextArea.requestFocusInWindow();
//                }
//            });
//
//            // Update RText actions associated with the current currentTextArea.
//            currentTextArea.addCaretListener(owner);
//
//			// Trick the parent RText into updating the row/column indicator.
//            // We have to check mainView for null because this is called in
//            // RText's constructor, before RText has a mainView.
//            if (owner.getMainView() != null) // Null because caretUpdate doesn't actually use the caret event.
//            {
//                owner.caretUpdate(null);
//            }
//
//            // Let any listeners know that the current document changed.
//            firePropertyChange(CURRENT_DOCUMENT_PROPERTY, -1, getSelectedIndex());
//            fireCurrentTextAreaEvent(CurrentTextAreaEvent.TEXT_AREA_CHANGED,
//                    null, currentTextArea);
//
//        }
//
//    }
    /**
     * Attempts to close all currently active documents.
     *
     * @return <code>true</code> if all active documents were closed, and
     * <code>false</code> if they weren't (i.e., the user hit cancel).
     */
    public boolean closeAllDocuments() {
        return closeAllDocumentsExcept(-1);
    }

    /**
     * Attempts to close all currently active documents except the one
     * specified.
     *
     * @return <code>true</code> if the documents were all closed, and
     * <code>false</code> if they weren't (i.e., the user hit cancel).
     */
    public boolean closeAllDocumentsExcept(int except) {

        int numDocuments = getNumDocuments();
        setSelectedIndex(numDocuments - 1); // Start at the back.

        // Cycle through each document, one by one.
        for (int i = numDocuments - 1; i >= 0; i--) {

            if (i == except) {
                // Instead of removing this document, set focus to the
                // "first" document, and continue closing documents with the
                // next iteration.  Since we're only keeping around 1
                // document, this keeps it open.
                if (i > 0) {
                    setSelectedIndex(0);
                }
            } else {

                // Try to close the document.
                boolean closed = closeCurrentDocument();

                // If the user cancels out of it, quit the whole schibang.
                if (!closed) {
                    // If the newly-active file is read-only, say so in the status bar.
//					owner.setStatusBarReadOnlyIndicatorEnabled(
//						currentTextArea==null ? false
//										: currentTextArea.isReadOnly());
                    return false;
                }

            }

        } // End of for (int i=tabCount-1; i>=0; i--).

        // If we got this far, then all documents were closed.
        // We'll just have an empty default-named file out there.
        return true;

    }

    /**
     * Attempts to close the current document.
     *
     * @return Whether the file was closed (e.g. the user didn't cancel the
     * operation). This will also return <code>false</code> if an IO error
     * occurs saving the file, if the user chooses to do so.
     */
    public final boolean closeCurrentDocument() {

//		RTextEditorPane old = currentTextArea;
        boolean closed = closeCurrentDocumentImpl();

        if (closed) {
//			old.clearParsers();
//			firePropertyChange(TEXT_AREA_REMOVED_PROPERTY, null, old);
        }

        return closed;

    }
}
