package s3f.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.ImageObserver;
import java.net.URL;

/**
 * Present a simple graphic to the user upon launch of the application, to
 * provide a faster initial response than is possible with the main window.
 * 
* <P>
 * Adapted from an <a
 * href=http://developer.java.sun.com/developer/qow/archive/24/index.html>item</a>
 * on Sun's Java Developer Connection.
 * 
* <P>
 * This splash screen appears within about 2.5 seconds on a development machine.
 * The main screen takes about 6.0 seconds to load, so use of a splash screen
 * cuts down the initial display delay by about 55 percent.
 */
public final class SplashScreen extends Frame {

    private final boolean animated;
    private boolean asd = false;
    private int asd2 = 3;
    private boolean error;
    private int errorX = 0;
    private String errorMsg = "An error occurred while attempting to initialize the GUI. The stack trace is available in clipboard while this window is open. Click here to close.";

    /**
     * Construct using an image for the splash screen.
     *
     * @param aImageId must have content, and is used by
     * {@link Class#getResource(java.lang.String)} to retrieve the splash screen
     * image.
     */
    public SplashScreen(String aImageId) {
        this(aImageId, false);
    }

    public SplashScreen(String aImageId, boolean animated) {
        /* 
         * Implementation Note
         * Args.checkForContent is not called here, in an attempt to minimize 
         * class loading.
         */
        if (aImageId == null || aImageId.trim().length() == 0) {
            throw new IllegalArgumentException("Image Id does not have content.");
        }
        fImageId = aImageId;
        this.animated = animated;
    }

    /**
     * Show the splash screen to the end user.
     *
     * <P>
     * Once this method returns, the splash screen is realized, which means that
     * almost all work on the splash screen should proceed through the event
     * dispatch thread. In particular, any call to <tt>dispose</tt> for the
     * splash screen must be performed in the event dispatch thread.
     */
    public void splash() {
        initImageAndTracker();
        setSize(fImage.getWidth(NO_OBSERVER), fImage.getHeight(NO_OBSERVER));
        center();

        fMediaTracker.addImage(fImage, IMAGE_ID);
        try {
            fMediaTracker.waitForID(IMAGE_ID);
        } catch (InterruptedException ex) {
            System.out.println("Cannot track image load.");
        }

        final SplashWindow splashWindow = new SplashWindow(this, fImage);

        if (animated) {
            new Thread() {
                @Override
                public void run() {
                    while (splashWindow.isVisible()) {
                        splashWindow.repaint();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {

                        }
                    }
                }
            }.start();

            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (error) {
                        System.exit(1);
                    } else {
                        asd = !asd;
                    }
                }

                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    asd2 -= e.getWheelRotation();
                    asd2 = (asd2 <= 0) ? 1 : (asd2 > 4) ? 4 : asd2;
                }
            };
            splashWindow.addMouseListener(mouseAdapter);
            splashWindow.addMouseWheelListener(mouseAdapter);
        }
    }
    // PRIVATE//
    private final String fImageId;
    private MediaTracker fMediaTracker;
    private Image fImage;
    private static final ImageObserver NO_OBSERVER = null;
    private static final int IMAGE_ID = 0;

    private void initImageAndTracker() {
        fMediaTracker = new MediaTracker(this);
        URL imageURL = SplashScreen.class.getResource(fImageId);
        fImage = Toolkit.getDefaultToolkit().getImage(imageURL);
    }

    /**
     * Centers the frame on the screen.
     *
     * <P>
     * This centering service is more or less in
     * {@link hirondelle.stocks.util.ui.UiUtil}; this duplication is justified
     * only because the use of {@link hirondelle.stocks.util.ui.UiUtil} would
     * entail more class loading, which is not desirable for a splash screen.
     */
    private void center() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle frame = getBounds();
        setLocation((screen.width - frame.width) / 2, (screen.height - frame.height) / 2);
    }

    public void showError(String message) {
        error = true;
        StringSelection stringSelection = new StringSelection(message);
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
    }

    private class SplashWindow extends Window {

        SplashWindow(Frame aParent, Image aImage) {
            super(aParent);
            fImage = aImage;
            setSize(fImage.getWidth(NO_OBSERVER), fImage.getHeight(NO_OBSERVER));
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle window = getBounds();
            setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);
            setBackground(new Color(0, 0, 0, 0));
            setVisible(true);
        }

        int b = 0;

        @Override
        public void paint(Graphics g2) {
            if (fImage != null) {
                g2.drawImage(fImage, 0, 0, this);
                if (animated) {
                    int w;
                    switch (asd2) {
                        case 1:
                            w = 10;
                            break;
                        case 2:
                            w = 25;
                            break;
                        case 3:
                            w = 50;
                            break;
                        case 4:
                            w = 125;
                            break;
                        default:
                            w = 10;
                    }

                    int n = 250 / w - 1;
                    if (error) {
                        g2.setColor(Color.red);
                        g2.fillRect(5, getHeight() - 21, 250, 12);
                        g2.setColor(Color.white);
                        g2.drawString(errorMsg, 20 + errorX, getHeight() - 11);
                        g2.setColor(Color.black);
                        g2.fillRect(0, 0, 5, getHeight());
                        g2.fillRect(getWidth() - 5, 0, 5, getHeight());
                        g2.fillRect(0, 0, getWidth(), 5);
                        g2.fillRect(0, getHeight() - 5, getWidth(), 5);
                        if (errorX < -g2.getFontMetrics().stringWidth(errorMsg)) {
                            errorX = getWidth();
                        }
                        errorX -= 5;
                    } else {
                        if (!asd) {
                            for (int i = n; i >= 0; i--) {
                                g2.setColor(RandomColor.generate(.5f, .9f));
                                //g2.fillRect(i * w + 5, getHeight() - 15, w, 3);
                                g2.fillRect(i * w + 5, getHeight() - 17, w, 5);
                                //g2.fillRect(i * w + 5, getHeight() - 9, w, 4);
                                //g2.fillRect(i * w + 5, getHeight() - 8, w, 3);
                            }
                        } else {
                            b += 3;
                            for (int i = n; i >= 0; i--) {
                                g2.setColor(RandomColor.generate(.6f, .99f));
                                g2.fillRect(i * w + 5, (int) (50 * Math.sin((i - b) / Math.PI)) + 100, w, 110);
                            }
                            g2.setColor(Color.BLACK);
                            g2.fillRect(0, getHeight() - 5, getWidth(), 5);
                        }
                    }
//                    for (int i = 15; i >= 0; i--) {
//                        for (int j = 10; j >= 0; j--) {
//                            g2.setColor(RandomColor.generate(.6f, .9f));
//                            g2.fillRect(i * 30, j * 30, 30, 30);
//                        }
//                    }
                    Toolkit.getDefaultToolkit().sync();
                }
            }
        }
        private Image fImage;
    }
}
