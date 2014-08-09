package s3f.util.splashscreen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
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
public class SimpleSplashScreen extends Frame implements SplashScreen {

    protected final SplashWindow splashWindow;
    protected boolean quickClose = false;
    protected boolean error = false;

    /**
     * Construct using an image for the splash screen.
     *
     * @param aImageId must have content, and is used by
     * {@link Class#getResource(java.lang.String)} to retrieve the splash screen
     * image.
     */
    public SimpleSplashScreen(String aImageId) {
        this();
        /* 
         * Implementation Note
         * Args.checkForContent is not called here, in an attempt to minimize 
         * class loading.
         */
        if (aImageId == null || aImageId.trim().length() == 0) {
            throw new IllegalArgumentException("Image Id does not have content.");
        }
        fImageId = aImageId;
    }

    public SimpleSplashScreen() {
        splashWindow = new SplashWindow(this);
        createSplashScreen("No Splash Screen", false, new Font(Font.MONOSPACED, Font.PLAIN, 40), new Color(1, 0, 0, .8f), Color.black, Color.white);
    }

    public SimpleSplashScreen(Image i) {
        this();
        fImage = i;
    }

    public SimpleSplashScreen(String text, boolean asciiArt) {
        this();
        Font font;
        if (asciiArt) {
            font = new Font(Font.MONOSPACED, Font.PLAIN, 15);
        } else {
            font = new Font(Font.MONOSPACED, Font.PLAIN, 70);
        }
        createSplashScreen(text, asciiArt, font, new Color(1, 1, 1, .8f), Color.black, Color.LIGHT_GRAY);
    }

    public final void createSplashScreen(String text, boolean asciiArt, Font font, Color bg, Color border, Color fill) {
        int w = 0, h = 0, x = 0, y = 0;
        h += this.getFontMetrics(font).getHeight() / 3;
        for (String s : text.split("\n")) {
            h += this.getFontMetrics(font).getHeight();
            int tw = this.getFontMetrics(font).stringWidth(s);
            if (tw > w) {
                w = tw;
            }
        }
        if (!asciiArt) {
            w += this.getFontMetrics(font).getHeight() / 2;
            x += this.getFontMetrics(font).getHeight() / 4 + 2;
        }
        BufferedImage bi = new BufferedImage(w + 1, h + 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(bg);
        g.fillRoundRect(0, 0, w, h, 10, 10);
        g.setFont(font);
        g.setColor(border);
        g.drawRoundRect(0, 0, w, h, 10, 10);
        g.translate(x, y);
        for (String s : text.split("\n")) {
            g.translate(0, g.getFontMetrics().getHeight());

            g.setColor(border);
            g.drawString(s, .8f, .8f);
            g.drawString(s, .8f, -.8f);
            g.drawString(s, -.8f, .8f);
            g.drawString(s, -.8f, -.8f);
            g.setColor(fill);
            g.drawString(s, 0, 0);
        }
        fImage = bi;
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
    @Override
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

        splashWindow.setImage(fImage);
    }
    // PRIVATE//
    private String fImageId = null;
    private MediaTracker fMediaTracker;
    protected Image fImage;
    private static final ImageObserver NO_OBSERVER = null;
    private static final int IMAGE_ID = 0;

    private void initImageAndTracker() {
        fMediaTracker = new MediaTracker(this);
        if (fImageId != null) {
            URL imageURL = SimpleSplashScreen.class.getResource(fImageId);
            fImage = Toolkit.getDefaultToolkit().getImage(imageURL);
        }
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

    @Override
    public void showError(String message) {
        error = true;
        StringSelection stringSelection = new StringSelection(message);
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
    }

    @Override
    public void done() {
        if (quickClose) {
            dispose();
        } else {
            new Thread(){
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                    }
                    dispose();
                }
            }.start();
        }
    }

    public boolean paintSplashScreen(Graphics g2) {
        return false;
    }

    protected class SplashWindow extends Window {

        private Image fImage;

        SplashWindow(Frame aParent) {
            super(aParent);
            setBackground(new Color(0, 0, 0, 0));
//            setAlwaysOnTop(true);
        }

        public void setImage(Image aImage) {
            fImage = aImage;
            setSize(fImage.getWidth(NO_OBSERVER), fImage.getHeight(NO_OBSERVER));
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle window = getBounds();
            setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);
            setVisible(true);
        }

        @Override
        public void paint(Graphics g2) {
            if (!paintSplashScreen(g2)) {
                if (fImage != null) {
                    g2.drawImage(fImage, 0, 0, this);
                }
            }
        }
    }
}
